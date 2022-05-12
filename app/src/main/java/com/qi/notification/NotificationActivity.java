package com.qi.notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.qi.BackgroundService.BackgroundSoundService;
import com.qi.LeaderBoard.adapter.LeadersAdapter;
import com.qi.LeaderBoard.model.LeaderModel;
import com.qi.MainActivity;
import com.qi.Manager.AppPreferenceManager;
import com.qi.Manager.AppUtilityMethod;
import com.qi.R;
import com.qi.notification.adapter.NotificationAdapter;
import com.qi.notification.model.NotificationModel;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import ir.alirezabdn.wp7progress.WP7ProgressBar;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.qi.URL_Constants.JsonTagConstants.JTAGVALUE_TOTAL_STARS;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_ALERTS;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_LEADERBOARD;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_RESPONSE;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_RESPONSECODE;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_STATUSCODE;
import static com.qi.URL_Constants.StausCodes.RESPONSE_ACCESSTOKEN_EXPIRED;
import static com.qi.URL_Constants.StausCodes.RESPONSE_ACCESSTOKEN_MISSING;
import static com.qi.URL_Constants.StausCodes.RESPONSE_INTERNALSERVER_ERROR;
import static com.qi.URL_Constants.StausCodes.RESPONSE_INVALID_ACCESSTOKEN;
import static com.qi.URL_Constants.StausCodes.RESPONSE_SUCCESS;
import static com.qi.URL_Constants.StausCodes.STATUSCODE_INVALIDUSERNAMEORPASWD;
import static com.qi.URL_Constants.StausCodes.STATUSCODE_MISSING_PARAMETER;
import static com.qi.URL_Constants.StausCodes.STATUSCODE_SUCCESS;
import static com.qi.URL_Constants.StausCodes.STATUS_CODE_NO_QUIZ;
import static com.qi.URL_Constants.URLConstant.URL_GET_LEADERBOARD;
import static com.qi.URL_Constants.URLConstant.URL_GET_NOTIFICATIONS;
import static com.qi.URL_Constants.URLConstant.URL_RESET_BADGE;
import static com.qi.URL_Constants.URLConstant.URL_SUBMIT_QUIZ;

public class NotificationActivity extends AppCompatActivity {

    Context mContext;
    Activity mActivity;
    RecyclerView recyclerView;
    private String android_id,DeviceName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    WP7ProgressBar loader;
    ArrayList<NotificationModel> arrayList;
    boolean flag = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        mContext = this;
        mActivity = this;

        IniUI();
    }

    private void IniUI() {
        recyclerView = findViewById(R.id.Notification_Recycler);
        loader = findViewById(R.id.NotificationLoader);

        android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        DeviceName = Build.BRAND;

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Intent svc = new Intent(this, BackgroundSoundService.class);
        startService(svc);

        AddUserVerficationDetails("0");
        getNotifications();

        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setUserId(AppPreferenceManager.getStudentId(mContext));

    }

    private void getNotifications() {
        System.out.println("ResponseValue: NOTIFI: Called for API");
        loader.showProgressBar();
        arrayList = new ArrayList<>();

        OkHttpClient client = new OkHttpClient();
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        JSONObject postdata = new JSONObject();

        try {
            postdata.put("studentId", AppPreferenceManager.getStudentId(mContext));
        } catch(JSONException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(postdata.toString(),MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(URL_GET_NOTIFICATIONS)
                .post(body)
                .header("Authorization","Bearer "+AppPreferenceManager.getAccessToken(mContext))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("ResponseValue: E",e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String Response = response.body().string();
                Log.d("ResponseValue: Notifi",Response);

                try {
                    JSONObject obj = new JSONObject(Response);
                    String response_code = obj.getString(JTAG_RESPONSECODE);
                    if (response_code.equalsIgnoreCase(RESPONSE_SUCCESS)) {
                        JSONObject secobj = obj.getJSONObject(JTAG_RESPONSE);
                        String status_code = secobj.getString(JTAG_STATUSCODE);

                        if (status_code.equalsIgnoreCase(STATUSCODE_SUCCESS)) {
                            CallForResetBadge();
                            final JSONArray jsonArray = secobj.getJSONArray(JTAG_ALERTS);
                            if (jsonArray.length()>0){
                                for (int i =0;i<jsonArray.length();i++){
                                    System.out.println("DATATA"+jsonArray.get(i));
                                    JSONObject JOB = jsonArray.getJSONObject(i);

                                    NotificationModel model = new NotificationModel();
                                    model.setId(JOB.optString("id"));
                                    model.setMessage(JOB.optString("message"));
                                    model.setUrl(JOB.optString("url"));
                                    model.setAlert_type(JOB.optString("alert_type"));
                                    model.setSection(JOB.optString("section"));
                                    model.setLanguage_id(JOB.optString("language_id"));
                                    model.setCreated_time(JOB.optString("created_time"));

                                    arrayList.add(model);
                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
//                                            TotalStar.setText(totalStar);

//                                            star.setVisibility(View.VISIBLE);
//                                            mAdapter = new LeadersAdapter(movieList);
//                                            recyclerView.setAdapter(mAdapter);
                                            NotificationAdapter adapter = new NotificationAdapter(arrayList,mContext);
                                            recyclerView.setAdapter(adapter);


                                        }
                                    });

                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        loader.hideProgressBar();

                                    }
                                });

                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
//                                        TotalStar.setText(totalStar);
//
//                                        star.setVisibility(View.VISIBLE);
                                        AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.no_notification));
                                        loader.hideProgressBar();
                                    }
                                });
                            }


                        }else if (status_code.equalsIgnoreCase(STATUS_CODE_NO_QUIZ)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.no_quiz_available));
                                    loader.hideProgressBar();
                                }
                            });
                        }else if (status_code.equalsIgnoreCase("216")) {
                            System.out.println("ResponseValue: Called for Token");
                            AppUtilityMethod.RenewToken(mContext);
                            getNotifications();
                        }else if (status_code.equalsIgnoreCase(STATUSCODE_INVALIDUSERNAMEORPASWD)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.invalid_usr_pswd));
                                    loader.hideProgressBar();
                                }
                            });


                        } else if (status_code.equalsIgnoreCase(STATUSCODE_MISSING_PARAMETER)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.missing_parameter));
                                    loader.hideProgressBar();
                                }
                            });


                        } else {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.common_error));
//                                    loader.hideProgressBar();
//                                }
//                            });
                            AppUtilityMethod.RenewToken(mContext);
                            getNotifications();
                        }
                         /*else {
                            AppUtilityMethods.showDialogAlertDismiss((Activity) mContext, getString(R.string.error_heading), getString(R.string.common_error), R.drawable.infoicon,  R.drawable.roundblue);

                        }*/
                    } else if (response_code.equalsIgnoreCase(RESPONSE_INTERNALSERVER_ERROR)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppUtilityMethod.showDialogAlertDismiss((Activity) mContext, getString(R.string.internal_server_error));
                                loader.hideProgressBar();
                            }
                        });


                    } else if (response_code.equalsIgnoreCase(RESPONSE_INVALID_ACCESSTOKEN) || response_code.equalsIgnoreCase(RESPONSE_ACCESSTOKEN_MISSING) || response_code.equalsIgnoreCase(RESPONSE_ACCESSTOKEN_EXPIRED)) {
//                        AppUtilityMethod.getToken(mContext, new AppUtilityMethod.GetTokenSuccess() {
//                            @Override
//                            public void tokenrenewed() {
//                                sendLoginToServer(URL_PARENT_LOGIN);
//                            }
//                        });
                        AppUtilityMethod.RenewToken(mContext);
                        getNotifications();
                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,mContext.getString(R.string.common_error));
//                                loader.hideProgressBar();
//                            }
//                        });
                        AppUtilityMethod.RenewToken(mContext);
                        getNotifications();


                    }
                } catch (Exception ex) {
                    System.out.println("The Exception in edit profile is" + ex.toString());
                }


            }
        });
    }

    private void CallForResetBadge() {
        System.out.println("Called: for Reset");

        OkHttpClient client = new OkHttpClient();
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        JSONObject postdata = new JSONObject();

        try {
            postdata.put("studentId", AppPreferenceManager.getStudentId(mContext));
        } catch(JSONException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(postdata.toString(),MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(URL_RESET_BADGE)
                .post(body)
                .header("Authorization","Bearer "+AppPreferenceManager.getAccessToken(mContext))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("ResponseValue: RESET",e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String Response = response.body().string();
                Log.d("ResponseValue: RESET",Response);

                try {
                    JSONObject obj = new JSONObject(Response);
                    String response_code = obj.getString(JTAG_RESPONSECODE);
                    if (response_code.equalsIgnoreCase(RESPONSE_SUCCESS)) {
                        JSONObject secobj = obj.getJSONObject(JTAG_RESPONSE);
                        String status_code = secobj.getString(JTAG_STATUSCODE);
                        if (status_code.equalsIgnoreCase(STATUSCODE_SUCCESS)) {
//                            String STARS = secobj.getString(JTAGVALUE_TOTAL_STARS);
//                            System.out.println("ResponseValue: stars INSIDE Success"+STARS);
//                            AppPreferenceManager.setStudentStar(mContext,STARS);


                        }else if (status_code.equalsIgnoreCase(STATUS_CODE_NO_QUIZ)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.no_quiz_available));
                                }
                            });
                        }else if (status_code.equalsIgnoreCase("216")) {
                            System.out.println("ResponseValue: Called for Token");
                            AppUtilityMethod.RenewToken(mContext);
                            CallForResetBadge();
                        } else if (status_code.equalsIgnoreCase(STATUSCODE_INVALIDUSERNAMEORPASWD)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.invalid_usr_pswd));
                                }
                            });


                        } else if (status_code.equalsIgnoreCase(STATUSCODE_MISSING_PARAMETER)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.missing_parameter));
                                }
                            });


                        } else {
//                            runOnUiThread(new Runnable() {
//                                @Override
//                                public void run() {
//                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.common_error));
//                                }
//                            });
                            AppUtilityMethod.RenewToken(mContext);
                            CallForResetBadge();

                        }
                         /*else {
                            AppUtilityMethods.showDialogAlertDismiss((Activity) mContext, getString(R.string.error_heading), getString(R.string.common_error), R.drawable.infoicon,  R.drawable.roundblue);

                        }*/
                    } else if (response_code.equalsIgnoreCase(RESPONSE_INTERNALSERVER_ERROR)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppUtilityMethod.showDialogAlertDismiss((Activity) mContext, getString(R.string.internal_server_error));
                            }
                        });


                    } else if (response_code.equalsIgnoreCase(RESPONSE_INVALID_ACCESSTOKEN) || response_code.equalsIgnoreCase(RESPONSE_ACCESSTOKEN_MISSING) || response_code.equalsIgnoreCase(RESPONSE_ACCESSTOKEN_EXPIRED)) {
//                        AppUtilityMethod.getToken(mContext, new AppUtilityMethod.GetTokenSuccess() {
//                            @Override
//                            public void tokenrenewed() {
//                                sendLoginToServer(URL_PARENT_LOGIN);
//                            }
//                        });
                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,mContext.getString(R.string.common_error));
//                            }
//                        });

                        AppUtilityMethod.RenewToken(mContext);
                        CallForResetBadge();

                    }
                } catch (Exception ex) {
                    System.out.println("The Exception in edit profile is" + ex.toString());
                }


            }
        });

    }

    private void AddUserVerficationDetails(String AppState) {

        /*
         *      0 = Application Active
         *      1 = Application Background
         *      2 = Application in Quiz
         */

        String currentDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault()).format(new Date());
        Log.d("CurrentData",currentDate);

        Map<String,Object> map = new HashMap<>();
        map.put("CurrentState",AppState);
        map.put("LoginTime",currentDate);
        map.put("DeviceID",android_id);
        map.put("DeviceName",DeviceName);
        map.put("StudentId", AppPreferenceManager.getStudentId(mContext));
        map.put("UserName",AppPreferenceManager.getQIUserName(mContext));
        map.put("Version",AppPreferenceManager.getAppVersion(mContext));
        db.collection("USERVERIFICATION").document(AppPreferenceManager.getStudentId(mContext))
                .set(map)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        // Toast.makeText(mContext, "Inserted", Toast.LENGTH_SHORT).show();
//                        CheckUserExist();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
    }

    @Override
    public void onPause() {
        AddUserVerficationDetails("1");
        if (isApplicationSentToBackground(this)){
            // Do what you want to do on detecting Home Key being Pressed

            Intent svc = new Intent(mContext, BackgroundSoundService.class);
            stopService(svc);
        }
        super.onPause();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(mContext, MainActivity.class));
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if(flag) {
//            Toast.makeText(getApplicationContext(), "start", 1).show();
        } else
        {
//            Toast.makeText(getApplicationContext(), "Restart 2", 1).show();
            Intent i = new Intent(mContext, MainActivity.class);
            finish();
            startActivity(i);

        }
    }

    @Override
    protected void onRestart() {
        Intent svc = new Intent(mContext, BackgroundSoundService.class);
        startService(svc);
//        AddUserVerficationDetails("0");
        flag = false;
        super.onRestart();
    }

    public boolean isApplicationSentToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }

}