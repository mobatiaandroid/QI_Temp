package com.qi.Common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.qi.Common.Login.Adapter.StudentAdapter;
import com.qi.Common.Login.Model.StudentModels;
import com.qi.MainActivity;
import com.qi.Manager.AppPreferenceManager;
import com.qi.Manager.AppUtilityMethod;
import com.qi.R;
import com.qi.recyclerviewmanager.RecyclerItemListener;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import ir.alirezabdn.wp7progress.WP7ProgressBar;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.qi.URL_Constants.JsonTagConstants.JTAG_BLOOD_GROUP;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_BOARD_ID;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_CLASS;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_DIV;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_DOB;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_ELECT1;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_ELECT2;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_FATHER;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_GENDER;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_HOUSE;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_ID;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_MENTOR;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_MOTHER;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_NAME;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_PARENT_ID;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_RESPONSE;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_RESPONSECODE;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_STATUSCODE;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_STUDENTS;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_STUDENT_GRNO;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_STUDENT_PHOTO;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_USERCODE;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_USERNAME;
import static com.qi.URL_Constants.StausCodes.RESPONSE_ACCESSTOKEN_EXPIRED;
import static com.qi.URL_Constants.StausCodes.RESPONSE_ACCESSTOKEN_MISSING;
import static com.qi.URL_Constants.StausCodes.RESPONSE_INTERNALSERVER_ERROR;
import static com.qi.URL_Constants.StausCodes.RESPONSE_INVALID_ACCESSTOKEN;
import static com.qi.URL_Constants.StausCodes.RESPONSE_SUCCESS;
import static com.qi.URL_Constants.StausCodes.STATUSCODE_INVALIDUSER;
import static com.qi.URL_Constants.StausCodes.STATUSCODE_INVALIDUSERNAMEORPASWD;
import static com.qi.URL_Constants.StausCodes.STATUSCODE_MISSING_PARAMETER;
import static com.qi.URL_Constants.StausCodes.STATUSCODE_SUCCESS;
import static com.qi.URL_Constants.URLConstant.URL_FORGOT_PASS;
import static com.qi.URL_Constants.URLConstant.URL_PARENT_LOGIN;

public class LoginActivity extends AppCompatActivity {
    
//    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Context mContext;
    Activity activity;
    EditText Email,Password;
    TextView ForgotPass,versionTxt,Registration;
    String PassedNumber,android_id;

    Button LoginBtn;
    public ArrayList<StudentModels> studentModelsArrayList=new ArrayList<>();
    WP7ProgressBar wp10ProgressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg));
        mContext = this;
        activity = this;
        
        IniUi();
    }

    private void IniUi() {
        Email = findViewById(R.id.login_email);
        Password = findViewById(R.id.login_pass);
        LoginBtn = findViewById(R.id.login_btn);
        ForgotPass = findViewById(R.id.forgot_pass_txt);
        Registration = findViewById(R.id.ToRegistration);
        wp10ProgressBar = findViewById(R.id.login_loader);
        versionTxt = findViewById(R.id.TestVersion);

        PassedNumber = getIntent().getStringExtra("Number");
        Email.setText(PassedNumber);
//        Email.setText("9496861587");
//        Password.setText("1234");

//        AppPreferenceManager.setAppVersion(mContext,"Test Build V 1.7");   // Test Build Version 1.7 --> Rules Scrolling, LeaderBoard Status & Result Page status --> Firebase Auth
        versionTxt.setVisibility(View.GONE);

        android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);


        LoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    InputMethodManager imm = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

                if (Email.getText().toString().equals("")){
                    Email.setError("Please Enter Mobile Number");

                }else if (Password.getText().toString().equals("")){
                    Password.setError("Please Enter Password");
                }else {
                   // CheckUser();
                    if (AppUtilityMethod.isNetworkConnected(mContext)) {
                        wp10ProgressBar.showProgressBar();
//                        StartFirabaseAuthentication();
                        AppPreferenceManager.setPhone(mContext,Email.getText().toString());
                        AppPreferenceManager.setPass(mContext,Password.getText().toString());
                        LoginUsers(Email.getText().toString(), Password.getText().toString());
                    }else {
                        AppUtilityMethod.showSingleButton((Activity)mContext,getString(R.string.please_check_internet));
                    }
                }
            }
        });

        ForgotPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Email.getText().toString().equals("")){
                    Email.setError("Please Enter Mobile Number");

                }else {
                    // CheckUser();
                    if (AppUtilityMethod.isNetworkConnected(mContext)) {
                        ShowForgotPassAlert(activity, "Do you want to Continue?");
                    }else {
                        AppUtilityMethod.showSingleButton((Activity)mContext,getString(R.string.please_check_internet));
                    }
                }
            }
        });

        Registration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext,RegistrationActivity.class));
            }
        });

    }

    private void ShowForgotPassAlert(Activity activity, String s) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_double_btn);

        TextView Msg = dialog.findViewById(R.id.double_msg_txt);
        Msg.setText(s);

        TextView logout = dialog.findViewById(R.id.double_ok_txt);
        logout.setText("Continue");

        TextView cancel = dialog.findViewById(R.id.double_cancel_txt);
        cancel.setText("Cancel");

        ImageView OK = dialog.findViewById(R.id.double_ok_btn);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // RestartApp();
                wp10ProgressBar.showProgressBar();
                ProceedForgotPass(Email.getText().toString());
            }
        });

        ImageView CancelBtn = dialog.findViewById(R.id.double_cancel_btn);
        CancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void ProceedForgotPass(String moblie) {
        OkHttpClient client = new OkHttpClient();
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        JSONObject postdata = new JSONObject();

        try {
            postdata.put("phone",moblie);
        } catch(JSONException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(postdata.toString(),MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(URL_FORGOT_PASS)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("ResponseValue: E",e.getMessage());
                wp10ProgressBar.hideProgressBar();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String Response = response.body().string();
                Log.d("ResponseValue: Forgot",Response);

                try {
                    JSONObject obj = new JSONObject(Response);
                    String response_code = obj.getString(JTAG_RESPONSECODE);
                    if (response_code.equalsIgnoreCase(RESPONSE_SUCCESS)) {
                        JSONObject secobj = obj.getJSONObject(JTAG_RESPONSE);
                        String status_code = secobj.getString(JTAG_STATUSCODE);

                        if (status_code.equalsIgnoreCase(STATUSCODE_SUCCESS)) {

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(mContext, "A new password has been sent to your registered Email id", Toast.LENGTH_SHORT).show();
                                    wp10ProgressBar.hideProgressBar();
                                }
                            });

                        }else if (status_code.equalsIgnoreCase(STATUSCODE_INVALIDUSER)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.invalid_user));
                                    wp10ProgressBar.hideProgressBar();
                                }
                            });
                        } else if (status_code.equalsIgnoreCase(STATUSCODE_INVALIDUSERNAMEORPASWD)) {
                            System.out.println("ResponseValue: "+"IVALUDE USERNAME OR PASS");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.invalid_usr_pswd));
                                    wp10ProgressBar.hideProgressBar();
                                }
                            });


                        } else if (status_code.equalsIgnoreCase(STATUSCODE_MISSING_PARAMETER)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.missing_parameter));
                                    wp10ProgressBar.hideProgressBar();
                                }
                            });



                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.common_error));
                                    wp10ProgressBar.hideProgressBar();
                                }
                            });

                        }
                         /*else {
                            AppUtilityMethods.showDialogAlertDismiss((Activity) mContext, getString(R.string.error_heading), getString(R.string.common_error), R.drawable.infoicon,  R.drawable.roundblue);

                        }*/
                    } else if (response_code.equalsIgnoreCase(RESPONSE_INTERNALSERVER_ERROR)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppUtilityMethod.showDialogAlertDismiss((Activity) mContext, getString(R.string.internal_server_error));
                                wp10ProgressBar.hideProgressBar();
                            }
                        });


                    } else if (response_code.equalsIgnoreCase(RESPONSE_INVALID_ACCESSTOKEN) || response_code.equalsIgnoreCase(RESPONSE_ACCESSTOKEN_MISSING) || response_code.equalsIgnoreCase(RESPONSE_ACCESSTOKEN_EXPIRED)) {
//                        AppUtilityMethod.getToken(mContext, new AppUtilityMethod.GetTokenSuccess() {
//                            @Override
//                            public void tokenrenewed() {
//                                sendLoginToServer(URL_PARENT_LOGIN);
//                            }
//                        });
                        wp10ProgressBar.hideProgressBar();
                    } else {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,mContext.getString(R.string.common_error));
                                wp10ProgressBar.hideProgressBar();
                            }
                        });


                    }
                } catch (Exception ex) {
                    System.out.println("The Exception in edit profile is" + ex.toString());
                }

            }
        });

    }

    private void LoginUsers(String Phone, String Pass) {
        OkHttpClient client = new OkHttpClient();
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        JSONObject postdata = new JSONObject();

        try {
            postdata.put("phone",Phone);
            postdata.put("passwd",Pass);
            postdata.put("device_id",AppPreferenceManager.getFirebaseToken(mContext));
            postdata.put("device_type","2");
        } catch(JSONException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(postdata.toString(),MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(URL_PARENT_LOGIN)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("ResponseValue: E",e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String Response = response.body().string();
                Log.d("ResponseValue: S",Response);

                try {
                    JSONObject obj = new JSONObject(Response);
                    String response_code = obj.getString(JTAG_RESPONSECODE);
                    if (response_code.equalsIgnoreCase(RESPONSE_SUCCESS)) {
                        JSONObject secobj = obj.getJSONObject(JTAG_RESPONSE);
                        String status_code = secobj.getString(JTAG_STATUSCODE);

                        if (status_code.equalsIgnoreCase(STATUSCODE_SUCCESS)) {
                            String UserCode = secobj.getString(JTAG_USERCODE);
                            String Username = secobj.getString(JTAG_USERNAME);
                            String UserId = secobj.getString("user_id");

                            System.out.println("Data: UserCode: "+UserCode);
                            System.out.println("Data: Username: "+Username);
                            System.out.println("Data: UserId: "+UserId);

                            AppPreferenceManager.setUserCode(mContext,UserCode);
                            AppPreferenceManager.setStudentId(mContext, UserId);
                            AppPreferenceManager.setQIUserName(mContext,Username);

                            AppPreferenceManager.setIsUserAlreadyLoggedIn(mContext,true);

                            startActivity(new Intent(mContext,MainActivity.class));

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    wp10ProgressBar.hideProgressBar();
                                }
                            });
                            System.out.println("ResponseValue: Username "+ AppPreferenceManager.getQIUserName(mContext));

                        } else if (status_code.equalsIgnoreCase(STATUSCODE_INVALIDUSERNAMEORPASWD)) {
                            System.out.println("ResponseValue: "+"IVALUDE USERNAME OR PASS");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.invalid_usr_pswd));
                                    wp10ProgressBar.hideProgressBar();
                                }
                            });


                        } else if (status_code.equalsIgnoreCase(STATUSCODE_MISSING_PARAMETER)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.missing_parameter));
                                    wp10ProgressBar.hideProgressBar();
                                }
                            });



                        } else {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.common_error));
                                    wp10ProgressBar.hideProgressBar();
                                }
                            });

                        }
                         /*else {
                            AppUtilityMethods.showDialogAlertDismiss((Activity) mContext, getString(R.string.error_heading), getString(R.string.common_error), R.drawable.infoicon,  R.drawable.roundblue);

                        }*/
                    } else if (response_code.equalsIgnoreCase(RESPONSE_INTERNALSERVER_ERROR)) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppUtilityMethod.showDialogAlertDismiss((Activity) mContext, getString(R.string.internal_server_error));
                                wp10ProgressBar.hideProgressBar();
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
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,mContext.getString(R.string.common_error));
                                wp10ProgressBar.hideProgressBar();
                            }
                        });


                    }
                } catch (Exception ex) {
                    System.out.println("The Exception in edit profile is" + ex.toString());
                }


            }
        });
    }

    private void showStudentList(final ArrayList<StudentModels> studentModelsArrayList) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_student_list);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        ImageView dialogDismiss = (ImageView) dialog.findViewById(R.id.closebtn);
        RecyclerView studentList = (RecyclerView) dialog.findViewById(R.id.recycler_view_social_media);
        Button SelectStu = (Button)dialog.findViewById(R.id.btn_dismiss);
        //if(mSocialMediaArray.get())
        final int sdk = Build.VERSION.SDK_INT;
       /* if (sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            dialogDismiss.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.l));

        } else {
            dialogDismiss.setBackground(mContext.getResources().getDrawable(R.drawable.button));

        }*/

//        studentList.addItemDecoration(new DividerItemDecoration(mContext.getResources().getDrawable(line)));
//        studentList.addItemDecoration(new DividerItemDecoration(mContext.getResources().getDrawable(R.drawable.line)));
//        studentList.addItemDecoration(new DividerItemDecoration(mContext, DividerItemDecoration.VERTICAL));

        studentList.setHasFixedSize(true);
        LinearLayoutManager llm = new LinearLayoutManager(mContext);
        llm.setOrientation(LinearLayoutManager.VERTICAL);
        studentList.setLayoutManager(llm);

        StudentAdapter studentAdapter = new StudentAdapter(mContext, studentModelsArrayList);
        studentList.setAdapter(studentAdapter);
        dialogDismiss.setOnClickListener(new View.OnClickListener() {

            @Override

            public void onClick(View v) {

                dialog.dismiss();

            }

        });


        studentList.addOnItemTouchListener(new RecyclerItemListener(mContext, studentList,
                new RecyclerItemListener.RecyclerTouchListener() {
                    public void onClickItem(View v, int position) {
//                        dialog.dismiss();
                        dialogDismiss.setVisibility(View.GONE);
                        for (int i = 0; i<studentModelsArrayList.size();i++){
                            if (studentModelsArrayList.get(i).isClicked()) {
                                studentModelsArrayList.get(i).setClicked(false);
                            }
                        }
                        studentModelsArrayList.get(position).setClicked(true);
                        studentAdapter.notifyDataSetChanged();

                        if(studentModelsArrayList.get(position).getBoardid().equals("1")){
                            String selectedFromList="ISG";
                            AppPreferenceManager.setSchoolSelection(mContext, selectedFromList);
                        }else {
                            String selectedFromList="ISG-INT";
                            AppPreferenceManager.setSchoolSelection(mContext, selectedFromList);
                        }

                        AppPreferenceManager.setIsGuest(mContext,false);
                        AppPreferenceManager.setStudentId(mContext, studentModelsArrayList.get(position).getId());
                        AppPreferenceManager.setStudentName(mContext, studentModelsArrayList.get(position).getName());
                        AppPreferenceManager.setStudentStar(mContext,studentModelsArrayList.get(position).getStars());
                        System.out.println("STARS: "+studentModelsArrayList.get(position).getStars());
                    }

                    public void onLongClickItem(View v, int position) {
                        System.out.println("On Long Click Item interface");
                    }
                }));
        SelectStu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AppPreferenceManager.setIsUserAlreadyLoggedIn(mContext, true);
                for (int i = 0; i<studentModelsArrayList.size();i++){
                    if (studentModelsArrayList.get(i).isClicked() == false) {
//                        Toast.makeText(mContext, "Please Select Student", Toast.LENGTH_SHORT).show();
                    }else {
                        Intent intent = new Intent(mContext, MainActivity.class);
                        intent.putExtra("confirmlogin",true);
                        dialog.dismiss();
                        startActivity(intent);
                        finish();
                    }
                }

            }
        });

        dialog.show();
    }


    public void ToRegister(View view) {
        startActivity(new Intent(this,RegistrationActivity.class));
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent a = new Intent(Intent.ACTION_MAIN);
        a.addCategory(Intent.CATEGORY_HOME);
        a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(a);
    }
}
