package com.qi.Common;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.firebase.firestore.FirebaseFirestore;
import com.qi.MainActivity;
import com.qi.Manager.AppPreferenceManager;
import com.qi.Manager.AppUtilityMethod;
import com.qi.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.regex.Pattern;

import ir.alirezabdn.wp7progress.WP7ProgressBar;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.qi.URL_Constants.JsonTagConstants.JTAG_RESPONSE;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_RESPONSECODE;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_STATUSCODE;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_USERCODE;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_USERNAME;
import static com.qi.URL_Constants.StausCodes.RESPONSE_ACCESSTOKEN_EXPIRED;
import static com.qi.URL_Constants.StausCodes.RESPONSE_ACCESSTOKEN_MISSING;
import static com.qi.URL_Constants.StausCodes.RESPONSE_INTERNALSERVER_ERROR;
import static com.qi.URL_Constants.StausCodes.RESPONSE_INVALID_ACCESSTOKEN;
import static com.qi.URL_Constants.StausCodes.RESPONSE_SUCCESS;
import static com.qi.URL_Constants.StausCodes.STATUSCODE_ALREADYREGISTERED;
import static com.qi.URL_Constants.StausCodes.STATUSCODE_GRNO_NOTVALID;
import static com.qi.URL_Constants.StausCodes.STATUSCODE_INVALIDUSERNAMEORPASWD;
import static com.qi.URL_Constants.StausCodes.STATUSCODE_MISSING_PARAMETER;
import static com.qi.URL_Constants.StausCodes.STATUSCODE_SUCCESS;
import static com.qi.URL_Constants.URLConstant.URL_PARENT_LOGIN;
import static com.qi.URL_Constants.URLConstant.URL_PARENT_REGISTRATION;


public class RegistrationActivity extends AppCompatActivity {

    EditText Name,Email,Password,Phone;
    Button Register;
    Context mContext;
    Activity activity;
    String UserId;
    WP7ProgressBar wp10ProgressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.bg));
        mContext = this;
        activity = this;

        IniUi();
    }

    private void IniUi() {
        Email = findViewById(R.id.Reg_Email);
        Name = findViewById(R.id.Reg_Name);
        Phone = findViewById(R.id.Reg_Mobile);
        Password = findViewById(R.id.Reg_Password);
        Register = findViewById(R.id.reg_btn);
        wp10ProgressBar = findViewById(R.id.login_loader);

        Register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                UploadData();
                if (Email.getText().toString().isEmpty() &&
                        Phone.getText().toString().isEmpty() &&
                Name.getText().toString().isEmpty() &&
                Password.getText().toString().isEmpty()){
                    AppUtilityMethod.showSingleButton(activity,"All Fields are mandatory");
                }else if (Email.getText().toString().isEmpty()){
                    Email.setError("Enter Email");
                }else if (!validEmail(Email.getText().toString())){
                    Email.setError("Email is not match");
                }else if (Name.getText().toString().isEmpty()){
                    Name.setError("Enter Name");
                }else if (Phone.getText().toString().isEmpty() || Phone.getText().toString().length() < 8 || Phone.getText().toString().length() > 16){
                    Phone.setError("Enter a valid Phone Number");
                }else if (Password.getText().toString().isEmpty()){
                    Password.setError("Enter Member Id");
                }else {
                    if (AppUtilityMethod.isNetworkConnected(mContext)) {
                        wp10ProgressBar.showProgressBar();
                        RegisterNewUser(Name.getText().toString(),
                                Email.getText().toString(),
                                Phone.getText().toString(),
                                Password.getText().toString());
                    }else {
                        AppUtilityMethod.showSingleButton((Activity)mContext,getString(R.string.please_check_internet));
                    }

                }

            }
        });


    }

    private void RegisterNewUser(String name, String email, String phone, String memberId) {
        OkHttpClient client = new OkHttpClient();
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        JSONObject postdata = new JSONObject();

        try {
            postdata.put("member_name",name);
            postdata.put("member_email",email);
            postdata.put("member_phone",phone);
            postdata.put("member_id",memberId);
            postdata.put("device_id",AppPreferenceManager.getFirebaseToken(mContext));
            postdata.put("device_type","2");
        } catch(JSONException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(postdata.toString(),MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(URL_PARENT_REGISTRATION)
                .post(body)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                System.out.println("ResponseValue: Registration: "+e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String Response = response.body().string();
                System.out.println("ResponseValue: Registration: "+Response);

                try {
                    JSONObject obj = new JSONObject(Response);
                    String response_code = obj.getString(JTAG_RESPONSECODE);
                    if (response_code.equalsIgnoreCase(RESPONSE_SUCCESS)) {
                        JSONObject secobj = obj.getJSONObject(JTAG_RESPONSE);
                        String status_code = secobj.getString(JTAG_STATUSCODE);

                        if (status_code.equalsIgnoreCase(STATUSCODE_SUCCESS)) {

                            Intent intent = new Intent(mContext,LoginActivity.class);
                            intent.putExtra("Number",phone);
                            startActivity(intent);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    wp10ProgressBar.hideProgressBar();
                                    Toast.makeText(mContext, "Password has been sent to your email address", Toast.LENGTH_SHORT).show();
                                }
                            });
//                            System.out.println("ResponseValue: Username "+ AppPreferenceManager.getQIUserName(mContext));

                        } else if (status_code.equalsIgnoreCase(STATUSCODE_INVALIDUSERNAMEORPASWD)) {
                            System.out.println("ResponseValue: "+"IVALUDE USERNAME OR PASS");
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.invalid_usr_pswd));
                                    wp10ProgressBar.hideProgressBar();
                                }
                            });

                        }else if (status_code.equalsIgnoreCase(STATUSCODE_ALREADYREGISTERED)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.already_registered));
                                    wp10ProgressBar.hideProgressBar();
                                }
                            });
                        } else if (status_code.equalsIgnoreCase(STATUSCODE_GRNO_NOTVALID)){
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext,getString(R.string.invalid_grno));
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


    private boolean validEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }
    public void ToLogin(View view) {
        startActivity(new Intent(this,LoginActivity.class));
    }


}
