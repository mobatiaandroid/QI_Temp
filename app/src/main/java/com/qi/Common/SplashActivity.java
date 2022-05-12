package com.qi.Common;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.qi.MainActivity;
import com.qi.Manager.AppPreferenceManager;
import com.qi.Manager.AppUtilityMethod;
import com.qi.R;


public class SplashActivity extends AppCompatActivity {

    Context mContext;
    Activity mActivity;
    VideoView videoView;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        mContext =this;
        mActivity= this;

        videoView = findViewById(R.id.videoView);

        if (AppUtilityMethod.isNetworkConnected(mContext)){
            StartFirabaseAuthentication();
            getAccessToken();
            LoadIntent();
        }else {
            ShowNoInternetAlert(mActivity,getString(R.string.please_check_internet));
        }

    }

    private void ShowNoInternetAlert(Activity mActivity, String please_check_internet) {
        final Dialog dialog = new Dialog(mActivity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_single_btn);
        TextView Msg =dialog.findViewById(R.id.single_msg_txt);
        Msg.setText(please_check_internet);
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

    private void LoadIntent() {
        Uri video = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.isg_v);
        videoView.setVideoURI(video);

        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {

                if (AppPreferenceManager.getIsUserAlreadyLoggedIn(mContext)){
                    Intent i = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                }else {
                    Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(i);
                    finish();
                }
            }
        });

        videoView.start();

    }

    private void getAccessToken() {
        AppUtilityMethod.getToken(mContext, new AppUtilityMethod.GetTokenSuccess() {
            @Override
            public void tokenrenewed() {

            }
        });

    }

    private void StartFirabaseAuthentication() {
        System.out.println("Fireabse: Called");
        mAuth.signInWithEmailAndPassword("om.ahmedrayees@gmail.com", "Firebase1234567890")
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            AppPreferenceManager.setFirebaseUID(mContext,user.getUid());
                            System.out.println("Fireabse: "+AppPreferenceManager.getFirebaseUID(mContext));
                            System.out.println("Fireabse: "+user.getUid());
//                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SplashActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                        }

                        // ...
                    }
                });
    }

}
