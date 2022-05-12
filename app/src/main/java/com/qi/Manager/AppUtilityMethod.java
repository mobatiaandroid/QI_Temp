package com.qi.Manager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.qi.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.qi.URL_Constants.JsonTagConstants.JTAG_ACCESS_TOKEN;
import static com.qi.URL_Constants.URLConstant.URL_METHOD_GET_ACCESSTOKEN;


public class AppUtilityMethod {
    static Context mContext;    private static int count = 0;

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        if (ni == null) {
            // There are no active networks.
            return false;
        } else
            return true;
    }

    public static void showSingleButton(final Activity activity,String message) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_single_btn);
        ImageView icon = (ImageView) dialog.findViewById(R.id.single_ok_btn);
        TextView text = (TextView) dialog.findViewById(R.id.single_msg_txt);
        text.setText(message);

        icon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    public static void showDialogAlertDismiss(Activity mContext, String string) {
        final Dialog dialog = new Dialog(mContext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_single_btn);
        TextView Msg =dialog.findViewById(R.id.single_msg_txt);
        Msg.setText(string);
        ImageView OK = dialog.findViewById(R.id.single_ok_btn);
        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                // RestartApp();
            }
        });
        dialog.show();
    }

    public interface GetTokenSuccess {
        void tokenrenewed();
    }

    public static void getToken(Context context, GetTokenSuccess tokenobj) {
        mContext = context;
        AppUtilityMethod apputils = new AppUtilityMethod();
        Securitycode accesstoken = apputils.new Securitycode(context, tokenobj);
        accesstoken.getToken();
    }

    private class Securitycode {
        private Context mContext;
        private GetTokenSuccess getTokenObj;

        public Securitycode(Context context, GetTokenSuccess getTokenObj) {
            this.mContext = context;
            this.getTokenObj = getTokenObj;
        }

        public void getToken() {
            Log.d("ResponseValue: ",URL_METHOD_GET_ACCESSTOKEN);
            OkHttpClient client = new OkHttpClient();
            MediaType MEDIA_TYPE = MediaType.parse("application/json");
            JSONObject postdata = new JSONObject();

            Log.d("ResponseValue: ",AppPreferenceManager.getUserCode(mContext));

            try {
                postdata.put("authorization-user",AppPreferenceManager.getUserCode(mContext));
            } catch(JSONException e){
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            RequestBody body = RequestBody.create(postdata.toString(),MEDIA_TYPE);

            Request request = new Request.Builder()
                    .url(URL_METHOD_GET_ACCESSTOKEN)
                    .post(body)
                    .addHeader("authorization-user",AppPreferenceManager.getUserCode(mContext))
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
                            JSONObject rootObject = new JSONObject(Response);
                            JSONObject job1 = rootObject.getJSONObject("response");

                            if (job1 != null) {
                                String access_token=job1.optString(JTAG_ACCESS_TOKEN);
                                AppPreferenceManager.setAccessToken(mContext,access_token);
                            } else {
                                //CustomStatusDialog(RESPONSE_FAILURE);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

//                    Log.d("ResponseValue: Get",AppPreferenceManager.getAccessToken(mContext));

                }
            });

        }
    }

    public static void RenewToken(final Context mContext) throws IOException{
        Log.d("ResponseValue: ",URL_METHOD_GET_ACCESSTOKEN);
        OkHttpClient client = new OkHttpClient();
        MediaType MEDIA_TYPE = MediaType.parse("application/json");
        JSONObject postdata = new JSONObject();

        try {
            postdata.put("authorization-user",AppPreferenceManager.getUserCode(mContext));
        } catch(JSONException e){
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        RequestBody body = RequestBody.create(postdata.toString(),MEDIA_TYPE);

        Request request = new Request.Builder()
                .url(URL_METHOD_GET_ACCESSTOKEN)
                .post(body)
                .addHeader("authorization-user",AppPreferenceManager.getUserCode(mContext))
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
                    JSONObject rootObject = new JSONObject(Response);
                    JSONObject job1 = rootObject.getJSONObject("response");

                    if (job1 != null) {
                        String access_token=job1.optString(JTAG_ACCESS_TOKEN);
                        AppPreferenceManager.setAccessToken(mContext,access_token);
                    } else {
                        //CustomStatusDialog(RESPONSE_FAILURE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

//                    Log.d("ResponseValue: Get",AppPreferenceManager.getAccessToken(mContext));

            }
        });
    }


}
