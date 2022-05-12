package com.qi.Common;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.qi.BackgroundService.BackgroundSoundService;
import com.qi.LeaderBoard.LeaderBoardActivity;
import com.qi.MainActivity;
import com.qi.Manager.AppPreferenceManager;
import com.qi.QuizQuestion.Model.ScoreModel;
import com.qi.R;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class FinalResult extends AppCompatActivity {

    Context mContext;
    Activity activity;
    String PassedScore,TotalScore,QuesSize;
    TextView ScoreTxt,OutOfScore,TotalStarCount,EmojiScore;
    ImageView pointImageReaction,fiveStars,CloseIcon,ToLeaderBoard,Emoji;
    ArrayList<ScoreModel> models = new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private String android_id,DeviceName;
    boolean flag = true;

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_final_result);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContext = this;
        activity = this;
        PassedScore = getIntent().getStringExtra("SCORE");
        TotalScore = getIntent().getStringExtra("CorrectAns");
        QuesSize = getIntent().getStringExtra("QuesSize");

        android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        DeviceName = Build.BRAND;

        System.out.println("PASSEDDATA: "+PassedScore);
        AppPreferenceManager.setStudentStar(mContext,PassedScore);
        if (PassedScore == null){
            PassedScore = "0";
        }

        IniUi();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void IniUi() {

//        Intent svc = new Intent(mContext, BackgroundSoundService.class);
//        startService(svc);

        ScoreTxt = findViewById(R.id.ScoreTxt);
        pointImageReaction = findViewById(R.id.point_image_reaction);
        fiveStars = findViewById(R.id.five_stars);
        CloseIcon = findViewById(R.id.final_close_icon);
        ToLeaderBoard = findViewById(R.id.final_leaderBoard);
        OutOfScore = findViewById(R.id.outpfScore);
        TotalStarCount = findViewById(R.id.starCount);
        Emoji = findViewById(R.id.final_emoji);
        EmojiScore = findViewById(R.id.emoji_score);
        TotalStarCount.setText(AppPreferenceManager.getStudentStar(mContext));

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AddUserVerficationDetails("0");
            }
        },2000);
        RemoveUserAnswers();
        RemoveUser();

        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setUserId(AppPreferenceManager.getStudentId(mContext));

        Intent svc = new Intent(this, BackgroundSoundService.class);
        startService(svc);

        OutOfScore.setText("Your Score is "+TotalScore+" out of "+QuesSize);
        EmojiScore.setText(TotalScore);
        AppPreferenceManager.setTotalScore(mContext,TotalScore);
        CloseIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddUserVerficationDetails("0");
                RemoveUser();
                startActivity(new Intent(mContext, MainActivity.class));
                finish();
            }
        });

        if (PassedScore.equals("1")){
            pointImageReaction.setBackgroundResource(R.drawable.trophy);
            ScoreTxt.setText("You have earned 1 star");
            EmojiScore.setVisibility(View.GONE);
            Emoji.setVisibility(View.GONE);
        }else {
            pointImageReaction.setBackgroundResource(R.drawable.try_again);
            ScoreTxt.setText("Keep trying to collect more stars");
            fiveStars.setVisibility(View.GONE);
        }

        for (int i = 0;i<models.size();i++){
            System.out.println("Arraysss: "+models.get(i).getScore());
        }

        ToLeaderBoard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddUserVerficationDetails("0");
                Intent i = new Intent(mContext, LeaderBoardActivity.class);
                i.putExtra("From","Result");
                i.putExtra("Star",PassedScore);
                startActivity(i);
                RemoveUser();
            }
        });
    }

    private void RemoveUser() {
        DocumentReference productIdRef = db.collection("QZ").document("users")
                .collection(AppPreferenceManager.getLevelId(mContext)).document(AppPreferenceManager.getStudentId(mContext));
        productIdRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
//                Toast.makeText(mContext, "Deleted", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(mContext, "Failed with: " + e.getMessage(), Toast.LENGTH_SHORT).show();
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


    @Override
    public void onBackPressed() {
        AddUserVerficationDetails("0");
        RemoveUser();
        startActivity(new Intent(this, MainActivity.class));
        finish();
    }


    @Override
    public void onPause() {
        AddUserVerficationDetails("1");
        if (isApplicationSentToBackground(this)){
            // Do what you want to do on detecting Home Key being Pressed
            RemoveUser();
            Intent svc = new Intent(mContext, BackgroundSoundService.class);
            stopService(svc);
        }
        super.onPause();
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

    private void RemoveUserAnswers() {
        DocumentReference Doc_ref = db.collection("QZ").document("Questions");
        Doc_ref.update("0", FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));
        Doc_ref.update("1",FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));
        Doc_ref.update("2",FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));
        Doc_ref.update("3",FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));
    }

}
