package com.qi.notification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.FirebaseFirestore;
import com.qi.BackgroundService.BackgroundSoundService;
import com.qi.MainActivity;
import com.qi.Manager.AppPreferenceManager;
import com.qi.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class NotificationDetailActivity extends AppCompatActivity {

    Context mContext;
    Activity mActivity;
    private String android_id,DeviceName;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    TextView NotifiMsg;
    boolean flag = true;
    String PassedMsg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_detail);
        mContext = this;
        mActivity = this;

        IniUI();
    }

    private void IniUI() {
        NotifiMsg = findViewById(R.id.NotificationMessage);

        PassedMsg = getIntent().getStringExtra("Message");
        NotifiMsg.setText(PassedMsg);

        android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        DeviceName = Build.BRAND;

        Intent svc = new Intent(this, BackgroundSoundService.class);
        startService(svc);

        AddUserVerficationDetails("0");

        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setUserId(AppPreferenceManager.getStudentId(mContext));
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
        startActivity(new Intent(mContext, NotificationActivity.class));
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