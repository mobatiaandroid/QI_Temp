package com.qi.LeaderBoard;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

public class LeaderBoardActivity extends AppCompatActivity {

    private List<LeaderModel> movieList = new ArrayList<>();
    private List<LeaderModel> moviee = new ArrayList<>();
    private RecyclerView recyclerView;
    private LeadersAdapter mAdapter;
    TextView Name,TotalStar,Version,TotalScore;
    Context mContext;
    WP7ProgressBar loader,alertloader;
    String totalStar,passedScore;
    ImageView star;
    boolean flag = true;
    Dialog dialog;
    private String android_id,DeviceName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String PassedData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leader_board);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContext = this;
        android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        DeviceName = Build.BRAND;
        passedScore = getIntent().getStringExtra("Star");
        System.out.println("PassedStar"+passedScore);
        Name = findViewById(R.id.LeaderName);
        TotalStar = findViewById(R.id.total_start);
        loader = findViewById(R.id.LeaderLoader);
        star = findViewById(R.id.imageView11);
        Version = findViewById(R.id.BuildVersion);
        Version.setText(AppPreferenceManager.getAppVersion(mContext));
        recyclerView = (RecyclerView) findViewById(R.id.LeaderBoard_Recycler);
        TotalScore=findViewById(R.id.total_score);

        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setUserId(AppPreferenceManager.getStudentId(mContext));

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        Intent svc1 = new Intent(mContext, BackgroundSoundService.class);
        startService(svc1);

        Name.setText(AppPreferenceManager.getQIUserName(mContext));

        PassedData = getIntent().getStringExtra("From");
        System.out.println("ResponseValue: Leader: "+PassedData);
        if (PassedData.equalsIgnoreCase("Result")){
            ShowAlert();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    GetData();
                }
            },3000);

        }else if (PassedData.equalsIgnoreCase("Main")){
            loader.showProgressBar();
            GetData();
        }
        AddUserVerficationDetails("0");
    }

    private void ShowAlert() {
        dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_gathering_data);
        alertloader = dialog.findViewById(R.id.gathering_main_loader);
        TextView Msg =dialog.findViewById(R.id.gathering_box_msg);
        TextView okTxt = dialog.findViewById(R.id.gathering_box_ok_txt);
        okTxt.setVisibility(View.GONE);
        Msg.setText("The names on the leaderboard are...");
        ImageView OK = dialog.findViewById(R.id.gathering_box_ok_btn);
        OK.setVisibility(View.GONE);
        alertloader.showProgressBar();
        dialog.show();
    }

    private void GetData() {
        System.out.println("ResponseValue: Leader: Called for API");
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
                .url(URL_GET_LEADERBOARD)
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
                Log.d("ResponseValue: Leader",Response);

                try {
                    JSONObject obj = new JSONObject(Response);
                    String response_code = obj.getString(JTAG_RESPONSECODE);
                    if (response_code.equalsIgnoreCase(RESPONSE_SUCCESS)) {
                        JSONObject secobj = obj.getJSONObject(JTAG_RESPONSE);
                        String status_code = secobj.getString(JTAG_STATUSCODE);
                        if (status_code.equalsIgnoreCase(STATUSCODE_SUCCESS)) {
                            totalStar = secobj.optString("total_stars");
                            final JSONArray jsonArray = secobj.getJSONArray(JTAG_LEADERBOARD);
                            if (jsonArray.length()>0){
                                for (int i =0;i<jsonArray.length();i++){
//                                Log.d("ResponseValue: INSIDE","INSIDE");
                                    System.out.println("DATATA"+jsonArray.get(i));
                                    JSONObject JOB = jsonArray.getJSONObject(i);
                                    String name  = JOB.optString("name");
                                    String total_qz_played  = JOB.optString("total_qz_played");
                                    String total_stars  = JOB.optString("total_stars");
                                    String total_mark  = JOB.optString("total_mark");
                                    LeaderModel model = new LeaderModel(name,total_qz_played,total_stars,total_mark);
                                    movieList.add(model);

                                    runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            TotalStar.setText(AppPreferenceManager.getStudentStar(mContext));
                                            TotalScore.setText(AppPreferenceManager.getTotalScore(mContext));
                                            star.setVisibility(View.VISIBLE);

                                            moviee.clear();
                                            if (movieList.size()>10)
                                            {
                                                for (int j=0;j<10;j++)
                                                {
                                                    moviee.add(movieList.get(j));
                                                }
                                                mAdapter = new LeadersAdapter(moviee);
                                                recyclerView.setAdapter(mAdapter);
                                            }
                                            else
                                            {
                                                mAdapter = new LeadersAdapter(movieList);
                                                recyclerView.setAdapter(mAdapter);
                                            }

                                        }
                                    });

                                }
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        recyclerView.setVisibility(View.VISIBLE);
                                        loader.hideProgressBar();

                                        if (PassedData.equalsIgnoreCase("Result")){
                                            alertloader.hideProgressBar();
                                            dialog.dismiss();
                                        }

                                    }
                                });

                            }else {
                                runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        TotalStar.setText(totalStar);
                                        star.setVisibility(View.VISIBLE);
                                        AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.no_datafound));
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
                            GetData();
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
                            GetData();
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
                        GetData();
                    } else {
//                        runOnUiThread(new Runnable() {
//                            @Override
//                            public void run() {
//                                AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,mContext.getString(R.string.common_error));
//                                loader.hideProgressBar();
//                            }
//                        });
                        AppUtilityMethod.RenewToken(mContext);
                        GetData();


                    }
                } catch (Exception ex) {
                    System.out.println("The Exception in edit profile is" + ex.toString());
                }


            }
        });
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
        map.put("StudentId",AppPreferenceManager.getStudentId(mContext));
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
