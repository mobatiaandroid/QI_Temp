package com.qi.QuizQuestion;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Dialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.sasank.roundedhorizontalprogress.RoundedHorizontalProgressBar;
import com.qi.BackgroundService.BackgroundSoundService;
import com.qi.Common.FinalResult;
import com.qi.MainActivity;
import com.qi.Manager.AppPreferenceManager;
import com.qi.Manager.AppUtilityMethod;
import com.qi.QuizQuestion.Model.QuestionsModel;
import com.qi.QuizQuestion.Model.ScoreModel;
import com.qi.R;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
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
import static com.qi.URL_Constants.JsonTagConstants.JTAG_QUESTIONS;
import static com.qi.URL_Constants.JsonTagConstants.JTAG_QUIZ;
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
import static com.qi.URL_Constants.URLConstant.URL_SUBMIT_QUIZ;

public class Questions extends AppCompatActivity {

    private String android_id, DeviceName;
    Button OP_1, OP_2, OP_3, OP_4;
    TextView Question, TimerTxt, Explanation, Explanationques, UsersCount, StarCount, textcurrent_time, duration_time, textcurrent_time_audio, duration_time_audio;
    ImageView Question_Image, explanation_ques_image, playbutton, playbutton_audio;
    RelativeLayout R1, R2, R3, R4, linearlayout, linearlayout_audio;
    String ExplanationText;
    Context mContext;
    Activity activity;
    TextView QuesCount;
    private SeekBar seebbar, seekbar_audio;
    private MediaPlayer mediaplayer;
    private MediaPlayer media_player;
    private Handler handler1 = new Handler();
    private Handler handler2 = new Handler();
    CountDownTimer cTimer = null;
    ArrayList<QuestionsModel> questionArray = new ArrayList<>();
    int QuestionCount = 0;
    int CorrectAnswers = 0;
    int QuestionSize;
    long timerSec;
    final Handler handler = new Handler();
    ArrayList<String> Q_ANS = new ArrayList<>();
    protected AlphaAnimation fadeIn = new AlphaAnimation(0.0f, 1.0f);
    String Selected = null;
    ArrayList<ScoreModel> ScoreArray = new ArrayList<>();
    MediaPlayer mediaPlayer;
    ArrayList<String> QuesList = new ArrayList<>();
    ArrayList<String> AnsList = new ArrayList<>();
    ArrayList<String> UserAnsList = new ArrayList<>();
    boolean flag = true;
    String JSONSTRING = null;
    Handler GetCounthandler;
    LinearLayout CounterLayout;
    ConstraintLayout LayoutwithButtons, LayoutwithProgress;
    RoundedHorizontalProgressBar Bar1, Bar2, Bar3, Bar4;
    TextView ProgressAns1, ProgressAns2, ProgressAns3, ProgressAns4;
    TextView ProgressScore1, ProgressScore2, ProgressScore3, ProgressScore4;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    ArrayList<String> Firbaseids = new ArrayList<>();
    int Barto1;
    int Barto2;
    int Barto3;
    int Barto4;
    int SIZE;
    int TotalUsersInProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questions);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mContext = this;
        activity = this;
        android_id = Settings.Secure.getString(mContext.getContentResolver(), Settings.Secure.ANDROID_ID);
        DeviceName = Build.BRAND;
        Intent svc = new Intent(mContext, BackgroundSoundService.class);
        startService(svc);
        IniUi();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Intent svc = new Intent(mContext, BackgroundSoundService.class);
        stopService(svc);

        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                Log.d("TAG------->", "player is running");
                mediaPlayer.stop();
                Log.d("Tag------->", "player is stopped");
                mediaPlayer.release();
                Log.d("TAG------->", "player is released");
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }

    }

    private void IniUi() {
        OP_1 = findViewById(R.id.OptBtn_1);
        OP_2 = findViewById(R.id.optBtn_2);
        OP_3 = findViewById(R.id.optBtn_3);
        OP_4 = findViewById(R.id.optBtn_4);
        Question = findViewById(R.id.Question_txt);
        TimerTxt = findViewById(R.id.timer_txt);
        QuesCount = findViewById(R.id.question_count);
        UsersCount = findViewById(R.id.UsersCount);
        R1 = findViewById(R.id.R1);
        R2 = findViewById(R.id.R2);
        R3 = findViewById(R.id.R3);
        R4 = findViewById(R.id.R4);
        playbutton = findViewById(R.id.playbutton);
        linearlayout = findViewById(R.id.linearlayout);
        linearlayout_audio = findViewById(R.id.linearlayout_audio);
        textcurrent_time = findViewById(R.id.textcurrent_time);
        duration_time = findViewById(R.id.duration_time);
        seebbar = findViewById(R.id.seebbar);
        textcurrent_time_audio = findViewById(R.id.textcurrent_time_audio);
        playbutton_audio = findViewById(R.id.playbutton_audio);
        duration_time_audio = findViewById(R.id.duration_time_audio);
        seekbar_audio = findViewById(R.id.seekbar_audio);
        mediaplayer = new MediaPlayer();
        media_player = new MediaPlayer();
        seebbar.setMax(100);
        seekbar_audio.setMax(100);
        Question_Image = findViewById(R.id.Question_Image);
        explanation_ques_image = findViewById(R.id.explanation_ques_image);
        StarCount = findViewById(R.id.starCount);
        StarCount.setText(AppPreferenceManager.getStudentStar(mContext));

        Explanation = findViewById(R.id.explantion_txt);
        LayoutwithButtons = findViewById(R.id.constraintLayout_buttons);
        LayoutwithProgress = findViewById(R.id.constraintLayout_progress);
        CounterLayout = findViewById(R.id.CounterLayout);
        Explanationques = findViewById(R.id.explanation_ques);
        Bar1 = findViewById(R.id.progress_bar_1);
        ProgressAns1 = findViewById(R.id.progress_ans_txt1);
        ProgressScore1 = findViewById(R.id.progress_score_txt1);

        Bar2 = findViewById(R.id.progress_bar_2);
        ProgressAns2 = findViewById(R.id.progress_ans_txt2);
        ProgressScore2 = findViewById(R.id.progress_score_txt2);

        Bar3 = findViewById(R.id.progress_bar_3);
        ProgressAns3 = findViewById(R.id.progress_ans_txt3);
        ProgressScore3 = findViewById(R.id.progress_score_txt3);

        Bar4 = findViewById(R.id.progress_bar_4);
        ProgressAns4 = findViewById(R.id.progress_ans_txt4);
        ProgressScore4 = findViewById(R.id.progress_score_txt4);

        fadeIn.setDuration(1200);
        fadeIn.setFillAfter(true);
        Explanation.startAnimation(fadeIn);

        GetUsersCount();
        getAccessToken();

        FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
        crashlytics.setUserId(AppPreferenceManager.getStudentId(mContext));

        GetCounthandler = handler;
        GetCounthandler.postDelayed(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(mContext, "Count Called", Toast.LENGTH_SHORT).show();
                GetUsersCount();
                GetCounthandler.postDelayed(this, 5000);
            }
        }, 5000);


//        GetQuiz(QuestionCount);
        GetData(QuestionCount);

        OP_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                OP_1.setEnabled(false);
//                OP_2.setEnabled(false);
//                OP_3.setEnabled(false);
//                OP_4.setEnabled(false);

//                QuestionCount++;

                OP_1.setBackgroundResource(R.drawable.login_btn_background);
                OP_2.setBackgroundResource(R.drawable.editext_background);
                OP_3.setBackgroundResource(R.drawable.editext_background);
                OP_4.setBackgroundResource(R.drawable.editext_background);

                Selected = "1";

//                InsertWhoClickedWhick();
//                GetQuestions(QuestionCount);

            }
        });
        OP_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                OP_1.setEnabled(false);
//                OP_2.setEnabled(false);
//                OP_3.setEnabled(false);
//                OP_4.setEnabled(false);
//
//                QuestionCount++;

//                GetQuestions(QuestionCount);

                OP_2.setBackgroundResource(R.drawable.login_btn_background);
                OP_3.setBackgroundResource(R.drawable.editext_background);
                OP_1.setBackgroundResource(R.drawable.editext_background);
                OP_4.setBackgroundResource(R.drawable.editext_background);

                Selected = "2";
//                InsertWhoClickedWhick();
            }
        });
        OP_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                OP_1.setEnabled(false);
//                OP_2.setEnabled(false);
//                OP_3.setEnabled(false);
//                OP_4.setEnabled(false);

//                QuestionCount++;
//                GetQuestions(QuestionCount);

                OP_3.setBackgroundResource(R.drawable.login_btn_background);
                OP_2.setBackgroundResource(R.drawable.editext_background);
                OP_1.setBackgroundResource(R.drawable.editext_background);
                OP_4.setBackgroundResource(R.drawable.editext_background);
                Selected = "3";

            }
        });
        OP_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                OP_1.setEnabled(false);
//                OP_2.setEnabled(false);
//                OP_3.setEnabled(false);
//                OP_4.setEnabled(false);
//
//                QuestionCount++;


                OP_4.setBackgroundResource(R.drawable.login_btn_background);
                OP_3.setBackgroundResource(R.drawable.editext_background);
                OP_1.setBackgroundResource(R.drawable.editext_background);
                OP_2.setBackgroundResource(R.drawable.editext_background);
                Selected = "4";
            }
        });

    }


    private void GetUsersCount() {
        Firbaseids.clear();
        db.collection("QZ").document("users").collection(AppPreferenceManager.getLevelId(mContext))
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("SSTAG", document.getId() + " => " + document.getData());
                                Log.d("SSTAG", " => Size: " + document.getData().size());
                                Firbaseids.add(document.getId());
                            }
                            String Usres = null;
                            for (int i = 0; i < Firbaseids.size(); i++) {
                                Usres = String.valueOf(Firbaseids.size());
                                TotalUsersInProgress = Firbaseids.size();
//                                Usres = "500";
                            }
                            System.out.println("UserCounts: " + TotalUsersInProgress);
                            try {
                                if (Integer.parseInt(Usres) >= 500) {
                                    CounterLayout.setVisibility(View.VISIBLE);
                                    UsersCount.setText(Usres);
                                } else {
                                    CounterLayout.setVisibility(View.GONE);
                                }
                            } catch (NumberFormatException e) {
                                e.fillInStackTrace();
                            }


                        } else {
                            Log.w("SSTAG", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void GetData(int questionCount) {
        ScoreArray.clear();
        questionArray.clear();
        System.out.println("Arshad: This is what is working.....");
        try {
            JSONObject obj = new JSONObject(AppPreferenceManager.getJsonResponse(mContext));
            String response_code = obj.getString(JTAG_RESPONSECODE);
            System.out.println("Question" + response_code);
            if (response_code.equalsIgnoreCase(RESPONSE_SUCCESS)) {
                JSONObject secobj = obj.getJSONObject(JTAG_RESPONSE);
                String status_code = secobj.getString(JTAG_STATUSCODE);
                if (status_code.equalsIgnoreCase(STATUSCODE_SUCCESS)) {
                    final JSONObject thirdobj = secobj.getJSONObject(JTAG_QUIZ);
                    Log.d("ResponseValue: INSIDE", thirdobj.optString("start_time"));
                    timerSec = Long.parseLong(thirdobj.optString("question_duration") + "000");
                    final JSONArray jsonArray = thirdobj.getJSONArray(JTAG_QUESTIONS);
                    for (int i = 0; i < jsonArray.length(); i++) {

                        final JSONObject job = jsonArray.getJSONObject(i);
                        ArrayList<String> OP = new ArrayList<>();
                        QuestionsModel model = new QuestionsModel();
                        model.setQuestion(job.optString("question"));
                        model.setAnswer(job.optString("answer_index"));
                        model.setExplanation(job.optString("explanation"));
                        model.setQuestionType(job.optInt("question_type"));
                        final JSONArray array = job.getJSONArray("options");
                        for (int K = 0; K < array.length(); K++) {
                            OP.add(String.valueOf(array.get(K)));
                        }
                        model.setOptions(OP);
                        questionArray.add(model);

                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            QuestionSize = questionArray.size();
                            if (QuestionSize == questionCount) {
                                CancelTimer();
                            } else {
                                System.out.println("Success");
                                for (int i = 0; i < questionArray.size(); i++) {
                                    Log.d("ResponseValue: SIZE", String.valueOf(questionArray.size()));

                                    Integer question_type = Integer.valueOf(questionArray.get(questionCount).getQuestionType());
                                    if (question_type.equals(2)) {
                                        Question_Image.setVisibility(View.VISIBLE);
                                        Question.setVisibility(View.GONE);
                                        linearlayout.setVisibility(View.GONE);
                                        Glide.with(mContext)
                                                .load(questionArray.get(questionCount).getQuestion())
                                                .into(Question_Image);
                                    } else if (question_type.equals(3)) {

                                        linearlayout.setVisibility(View.VISIBLE);
                                        Question.setVisibility(View.GONE);
                                        Question_Image.setVisibility(View.GONE);

                                        Intent svc = new Intent(mContext, BackgroundSoundService.class);
                                        if (media_player.isPlaying()) {
                                            startService(svc);
                                            handler1.removeCallbacks(updater);
                                            media_player.pause();
                                            playbutton.setImageResource(R.drawable.play);
                                        } else {
                                            stopService(svc);
                                            media_player.start();
                                            playbutton.setImageResource(R.drawable.pause_button);
                                            updateseekbar();
                                        }
                                        preparedmediaplayer(questionCount);

                                    } else {
                                        Question_Image.setVisibility(View.GONE);
                                        Question.setVisibility(View.VISIBLE);
                                        Question.setText(questionArray.get(questionCount).getQuestion());
                                    }

                                    System.out.println("Countt: " + questionArray.get(questionCount).getOptions().size());
                                    SIZE = questionArray.get(questionCount).getOptions().size();

//                                for (int a = 0;a<questionArray.size();a++){

                                    if (SIZE == 3) {
                                        System.out.println("Countt: this is what working");
                                        OP_1.setText(questionArray.get(questionCount).getOptions().get(0));
                                        OP_2.setText(questionArray.get(questionCount).getOptions().get(1));
                                        OP_3.setText(questionArray.get(questionCount).getOptions().get(2));
                                        OP_4.setVisibility(View.GONE);

                                        if (question_type.equals(2)) {

                                            explanation_ques_image.setVisibility(View.VISIBLE);
                                            Explanationques.setVisibility(View.GONE);
                                            linearlayout_audio.setVisibility(View.GONE);
                                            Glide.with(mContext)
                                                    .load(questionArray.get(questionCount).getQuestion())
                                                    .into(explanation_ques_image);
                                        } else if (question_type.equals(3)) {

                                            linearlayout_audio.setVisibility(View.VISIBLE);
                                            Explanationques.setVisibility(View.GONE);
                                            explanation_ques_image.setVisibility(View.GONE);
                                            if (mediaplayer.isPlaying()) {
                                                handler2.removeCallbacks(updater_audio);
                                                mediaplayer.pause();
                                                playbutton_audio.setImageResource(R.drawable.play);
                                            } else {
                                                mediaplayer.start();
                                                playbutton_audio.setImageResource(R.drawable.pause_button);
                                                updateseekbar_audio();
                                            }
                                            preparedmediaplayer_audio(questionCount);

                                        } else {
                                            explanation_ques_image.setVisibility(View.GONE);
                                            Explanationques.setVisibility(View.VISIBLE);
                                            Explanationques.setText(questionArray.get(questionCount).getQuestion());

                                        }
                                        ExplanationText = questionArray.get(questionCount).getExplanation();


                                        ProgressAns1.setText(questionArray.get(questionCount).getOptions().get(0));
                                        ProgressAns2.setText(questionArray.get(questionCount).getOptions().get(1));
                                        ProgressAns3.setText(questionArray.get(questionCount).getOptions().get(2));
                                        R4.setVisibility(View.GONE);
                                    } else if (SIZE == 2) {
                                        OP_1.setText(questionArray.get(questionCount).getOptions().get(0));
                                        OP_2.setText(questionArray.get(questionCount).getOptions().get(1));
                                        OP_3.setVisibility(View.GONE);
                                        OP_4.setVisibility(View.GONE);

                                        if (question_type.equals(2)) {
                                            explanation_ques_image.setVisibility(View.VISIBLE);
                                            Explanationques.setVisibility(View.GONE);
                                            linearlayout_audio.setVisibility(View.GONE);
                                            Glide.with(mContext)
                                                    .load(questionArray.get(questionCount).getQuestion())
                                                    .into(explanation_ques_image);
                                        } else if (question_type.equals(3)) {

                                            linearlayout_audio.setVisibility(View.VISIBLE);
                                            Explanationques.setVisibility(View.GONE);
                                            explanation_ques_image.setVisibility(View.GONE);
                                            if (mediaplayer.isPlaying()) {
                                                handler2.removeCallbacks(updater_audio);
                                                mediaplayer.pause();
                                                playbutton_audio.setImageResource(R.drawable.play);
                                            } else {
                                                mediaplayer.start();
                                                playbutton_audio.setImageResource(R.drawable.pause_button);
                                                updateseekbar_audio();
                                            }
                                            preparedmediaplayer_audio(questionCount);

                                        } else {
                                            explanation_ques_image.setVisibility(View.GONE);
                                            Explanationques.setVisibility(View.VISIBLE);
                                            Explanationques.setText(questionArray.get(questionCount).getQuestion());

                                        }

                                        ExplanationText = questionArray.get(questionCount).getExplanation();


                                        ProgressAns1.setText(questionArray.get(questionCount).getOptions().get(0));
                                        ProgressAns2.setText(questionArray.get(questionCount).getOptions().get(1));
                                        R3.setVisibility(View.GONE);
                                        R4.setVisibility(View.GONE);
                                    } else {
                                        System.out.println("Countt: this also is what working");
                                        OP_1.setText(questionArray.get(questionCount).getOptions().get(0));
                                        OP_2.setText(questionArray.get(questionCount).getOptions().get(1));
                                        OP_3.setText(questionArray.get(questionCount).getOptions().get(2));
                                        OP_4.setText(questionArray.get(questionCount).getOptions().get(3));

                                        if (question_type.equals(2)) {
                                            explanation_ques_image.setVisibility(View.VISIBLE);
                                            Explanationques.setVisibility(View.GONE);
                                            linearlayout_audio.setVisibility(View.GONE);
                                            Glide.with(mContext)
                                                    .load(questionArray.get(questionCount).getQuestion())
                                                    .into(explanation_ques_image);
                                        } else if (question_type.equals(3)) {

                                            linearlayout_audio.setVisibility(View.VISIBLE);
                                            Explanationques.setVisibility(View.GONE);
                                            explanation_ques_image.setVisibility(View.GONE);
                                            if (mediaplayer.isPlaying()) {
                                                handler2.removeCallbacks(updater_audio);
                                                mediaplayer.pause();
                                                playbutton_audio.setImageResource(R.drawable.play);
                                            } else {
                                                mediaplayer.start();
                                                playbutton_audio.setImageResource(R.drawable.pause_button);
                                                updateseekbar_audio();
                                            }
                                            preparedmediaplayer_audio(questionCount);

                                        } else {
                                            explanation_ques_image.setVisibility(View.GONE);
                                            Explanationques.setVisibility(View.VISIBLE);
                                            Explanationques.setText(questionArray.get(questionCount).getQuestion());

                                        }
                                        ExplanationText = questionArray.get(questionCount).getExplanation();

                                        ProgressAns1.setText(questionArray.get(questionCount).getOptions().get(0));
                                        ProgressAns2.setText(questionArray.get(questionCount).getOptions().get(1));
                                        ProgressAns3.setText(questionArray.get(questionCount).getOptions().get(2));
                                        ProgressAns4.setText(questionArray.get(questionCount).getOptions().get(3));
                                    }
//                                }
                                }
                                QuesList.add(Question.getText().toString());
                            }
                        }


                    });

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            QuesCount.setText("1/" + QuestionSize);
                        }
                    });
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            StartTimer(timerSec);
                        }
                    });


                } else if (status_code.equalsIgnoreCase(STATUS_CODE_NO_QUIZ)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppUtilityMethod.showDialogAlertDismiss((Activity) mContext, getString(R.string.no_quiz_available));
                        }
                    });
                } else if (status_code.equalsIgnoreCase(STATUSCODE_INVALIDUSERNAMEORPASWD)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppUtilityMethod.showDialogAlertDismiss((Activity) mContext, getString(R.string.invalid_usr_pswd));
                        }
                    });


                } else if (status_code.equalsIgnoreCase(STATUSCODE_MISSING_PARAMETER)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppUtilityMethod.showDialogAlertDismiss((Activity) mContext, getString(R.string.missing_parameter));
                        }
                    });


                } else {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            AppUtilityMethod.showDialogAlertDismiss((Activity) mContext, getString(R.string.common_error));
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
                        AppUtilityMethod.showDialogAlertDismiss((Activity) mContext, mContext.getString(R.string.common_error));
                    }
                });


            }
        } catch (Exception ex) {
            System.out.println("The Exception in edit profile is" + ex.toString());
        }

    }

    private void GetQuestions(int questionCount) {
        DocumentReference Doc_ref = db.collection("QZ").document("Questions");
        Doc_ref.update("0", FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));
        Doc_ref.update("1", FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));
        Doc_ref.update("2", FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));
        Doc_ref.update("3", FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));

        System.out.println("ARSHAD: GetQuestion Called QuestionSize" + QuestionSize + " Count " + questionCount);
//        GetUsersCount();
        if (QuestionSize == questionCount) {
            CancelTimer();
        } else {

            for (int i = 0; i < questionArray.size(); i++) {
                System.out.println("ARSHAD: " + questionArray.get(questionCount).getQuestion());
                Integer question_type = Integer.valueOf(questionArray.get(questionCount).getQuestionType());
                System.out.println("question_type" + question_type);
                if (question_type.equals(2)) {
                    Intent svc = new Intent(mContext, BackgroundSoundService.class);
                    startService(svc);
                    Question_Image.setVisibility(View.VISIBLE);
                    Question.setVisibility(View.GONE);
                    linearlayout.setVisibility(View.GONE);
                    Glide.with(mContext)
                            .load(questionArray.get(questionCount).getQuestion())
                            .into(Question_Image);
                } else if (question_type.equals(3)) {

                    linearlayout.setVisibility(View.VISIBLE);
                    Question.setVisibility(View.GONE);
                    Question_Image.setVisibility(View.GONE);

                    Intent svc = new Intent(mContext, BackgroundSoundService.class);

                    try {
                        if (media_player.isPlaying()) {
                            startService(svc);
                            handler1.removeCallbacks(updater);
                            media_player.pause();
                            playbutton.setImageResource(R.drawable.play);
                        } else {
                            stopService(svc);
                            media_player.start();
                            playbutton.setImageResource(R.drawable.pause_button);
                            updateseekbar();
                        }
                        preparedmediaplayer(questionCount);

                    } catch (Exception e) {

                    }


                } else {
                    Intent svc = new Intent(mContext, BackgroundSoundService.class);
                    startService(svc);
                    Question_Image.setVisibility(View.GONE);
                    Question.setVisibility(View.VISIBLE);
                    Question.setText(questionArray.get(questionCount).getQuestion());
                }
                System.out.println("Countt: " + questionArray.get(questionCount).getOptions().size());
                SIZE = questionArray.get(questionCount).getOptions().size();
//                for (int a = 0;a<questionArray.size();a++){

                if (SIZE == 3) {
                    System.out.println("Countt: this is what working");
                    OP_1.setText(questionArray.get(questionCount).getOptions().get(0));
                    OP_2.setText(questionArray.get(questionCount).getOptions().get(1));
                    OP_3.setText(questionArray.get(questionCount).getOptions().get(2));
                    OP_4.setVisibility(View.GONE);
                    if (question_type.equals(2)) {
                        explanation_ques_image.setVisibility(View.VISIBLE);
                        Explanationques.setVisibility(View.GONE);
                        linearlayout_audio.setVisibility(View.GONE);
                        Glide.with(mContext)
                                .load(questionArray.get(questionCount).getQuestion())
                                .into(explanation_ques_image);
                    } else if (question_type.equals(3)) {

                        linearlayout_audio.setVisibility(View.VISIBLE);
                        Explanationques.setVisibility(View.GONE);
                        explanation_ques_image.setVisibility(View.GONE);


                        try {
                           if (mediaplayer.isPlaying()) {
                            handler2.removeCallbacks(updater_audio);
                            mediaplayer.pause();
                            playbutton_audio.setImageResource(R.drawable.play);
                        } else {
                            mediaplayer.start();
                            playbutton_audio.setImageResource(R.drawable.pause_button);
                            updateseekbar_audio();
                        }
                        preparedmediaplayer_audio(questionCount);

                        }
                        catch (Exception e)
                        {

                        }



                    } else {
                        explanation_ques_image.setVisibility(View.GONE);
                        Explanationques.setVisibility(View.VISIBLE);
                        Explanationques.setText(questionArray.get(questionCount).getQuestion());

                    }
                    ExplanationText = questionArray.get(questionCount).getExplanation();


                    ProgressAns1.setText(questionArray.get(questionCount).getOptions().get(0));
                    ProgressAns2.setText(questionArray.get(questionCount).getOptions().get(1));
                    ProgressAns3.setText(questionArray.get(questionCount).getOptions().get(2));
                    R4.setVisibility(View.GONE);
                } else if (SIZE == 2) {
                    OP_1.setText(questionArray.get(questionCount).getOptions().get(0));
                    OP_2.setText(questionArray.get(questionCount).getOptions().get(1));
                    OP_3.setVisibility(View.GONE);
                    OP_4.setVisibility(View.GONE);
                    if (question_type.equals(2)) {
                        explanation_ques_image.setVisibility(View.VISIBLE);
                        Explanationques.setVisibility(View.GONE);
                        linearlayout_audio.setVisibility(View.GONE);
                        Glide.with(mContext)
                                .load(questionArray.get(questionCount).getQuestion())
                                .into(explanation_ques_image);
                    } else if (question_type.equals(3)) {
                        linearlayout_audio.setVisibility(View.VISIBLE);
                        Explanationques.setVisibility(View.GONE);
                        explanation_ques_image.setVisibility(View.GONE);

                        if (mediaplayer.isPlaying()) {
                            handler2.removeCallbacks(updater_audio);
                            mediaplayer.pause();
                            playbutton_audio.setImageResource(R.drawable.play);
                        } else {
                            mediaplayer.start();
                            playbutton_audio.setImageResource(R.drawable.pause_button);
                            updateseekbar_audio();
                        }
                        preparedmediaplayer_audio(questionCount);
                    } else {
                        explanation_ques_image.setVisibility(View.GONE);
                        Explanationques.setVisibility(View.VISIBLE);
                        Explanationques.setText(questionArray.get(questionCount).getQuestion());
                    }
                    ExplanationText = questionArray.get(questionCount).getExplanation();

                    ProgressAns1.setText(questionArray.get(questionCount).getOptions().get(0));
                    ProgressAns2.setText(questionArray.get(questionCount).getOptions().get(1));
                    R3.setVisibility(View.GONE);
                    R4.setVisibility(View.GONE);
                } else {
                    System.out.println("Countt: this also is what working");
                    OP_1.setText(questionArray.get(questionCount).getOptions().get(0));
                    OP_2.setText(questionArray.get(questionCount).getOptions().get(1));
                    OP_3.setText(questionArray.get(questionCount).getOptions().get(2));
                    OP_4.setText(questionArray.get(questionCount).getOptions().get(3));
                    if (question_type.equals(2)) {
                        explanation_ques_image.setVisibility(View.VISIBLE);
                        Explanationques.setVisibility(View.GONE);
                        linearlayout_audio.setVisibility(View.GONE);
                        Glide.with(mContext)
                                .load(questionArray.get(questionCount).getQuestion())
                                .into(explanation_ques_image);
                    } else if (question_type.equals(3)) {
                        linearlayout_audio.setVisibility(View.VISIBLE);
                        Explanationques.setVisibility(View.GONE);
                        explanation_ques_image.setVisibility(View.GONE);

                        if (mediaplayer.isPlaying()) {
                            handler2.removeCallbacks(updater_audio);
                            mediaplayer.pause();
                            playbutton_audio.setImageResource(R.drawable.play);
                        } else {
                            mediaplayer.start();
                            playbutton_audio.setImageResource(R.drawable.pause_button);
                            updateseekbar_audio();
                        }
                        preparedmediaplayer_audio(questionCount);
                    } else {
                        explanation_ques_image.setVisibility(View.GONE);
                        Explanationques.setVisibility(View.VISIBLE);
                        Explanationques.setText(questionArray.get(questionCount).getQuestion());
                    }
                    ExplanationText = questionArray.get(questionCount).getExplanation();


                    ProgressAns1.setText(questionArray.get(questionCount).getOptions().get(0));
                    ProgressAns2.setText(questionArray.get(questionCount).getOptions().get(1));
                    ProgressAns3.setText(questionArray.get(questionCount).getOptions().get(2));
                    ProgressAns4.setText(questionArray.get(questionCount).getOptions().get(3));
                }
//                }

//                OP_1.setText(questionArray.get(questionCount).getOptions().get(0));
//                OP_2.setText(questionArray.get(questionCount).getOptions().get(1));
//                OP_3.setText(questionArray.get(questionCount).getOptions().get(2));
//                OP_4.setText(questionArray.get(questionCount).getOptions().get(3));
//                ExplanationText = questionArray.get(questionCount).getExplanation();
//
//                Explanationques.setText(questionArray.get(questionCount).getQuestion());
//                ProgressAns1.setText(questionArray.get(questionCount).getOptions().get(0));
//                ProgressAns2.setText(questionArray.get(questionCount).getOptions().get(1));
//                ProgressAns3.setText(questionArray.get(questionCount).getOptions().get(2));
//                ProgressAns4.setText(questionArray.get(questionCount).getOptions().get(3));
            }
            QuesList.add(Question.getText().toString());
        }
        if (SIZE == 3) {
            OP_1.setVisibility(View.VISIBLE);
            OP_2.setVisibility(View.VISIBLE);
            OP_3.setVisibility(View.VISIBLE);
            OP_4.setVisibility(View.GONE);

            R1.setVisibility(View.VISIBLE);
            R2.setVisibility(View.VISIBLE);
            R3.setVisibility(View.VISIBLE);
            R4.setVisibility(View.GONE);
        } else if (SIZE == 2) {
            OP_1.setVisibility(View.VISIBLE);
            OP_2.setVisibility(View.VISIBLE);
            OP_3.setVisibility(View.GONE);
            OP_4.setVisibility(View.GONE);

            R1.setVisibility(View.VISIBLE);
            R2.setVisibility(View.VISIBLE);
            R3.setVisibility(View.GONE);
            R4.setVisibility(View.GONE);
        } else {
            OP_1.setVisibility(View.VISIBLE);
            OP_2.setVisibility(View.VISIBLE);
            OP_3.setVisibility(View.VISIBLE);
            OP_4.setVisibility(View.VISIBLE);

            R1.setVisibility(View.VISIBLE);
            R2.setVisibility(View.VISIBLE);
            R3.setVisibility(View.VISIBLE);
            R4.setVisibility(View.VISIBLE);
        }


        LayoutwithButtons.setVisibility(View.VISIBLE);
        LayoutwithProgress.setVisibility(View.GONE);

        Bar1.animateProgress(1000, 0, 0);
        Bar2.animateProgress(1000, 0, 0);
        Bar3.animateProgress(1000, 0, 0);
        Bar4.animateProgress(1000, 0, 0);

        Bar1.setProgressColors(getResources().getColor(R.color.progress_light_blue), getResources().getColor(R.color.progress_blue));
        Bar2.setProgressColors(getResources().getColor(R.color.progress_light_blue), getResources().getColor(R.color.progress_blue));
        Bar3.setProgressColors(getResources().getColor(R.color.progress_light_blue), getResources().getColor(R.color.progress_blue));
        Bar4.setProgressColors(getResources().getColor(R.color.progress_light_blue), getResources().getColor(R.color.progress_blue));

        Explanation.setText("");
        OP_1.setEnabled(true);
        OP_2.setEnabled(true);
        OP_3.setEnabled(true);
        OP_4.setEnabled(true);

        OP_1.setBackgroundResource(R.drawable.editext_background);
        OP_2.setBackgroundResource(R.drawable.editext_background);
        OP_3.setBackgroundResource(R.drawable.editext_background);
        OP_4.setBackgroundResource(R.drawable.editext_background);

    }


    private void StartTimer(final long timerSec) {
//        System.out.println("Times: Method" );
        cTimer = new CountDownTimer(timerSec, 1000) {
            public void onTick(long millisUntilFinished) {
                TimerTxt.setText("" + millisUntilFinished / 1000);
                System.out.println("Times: " + millisUntilFinished / 1000);
                if (TimerTxt.getText().equals("4")) {
                    System.out.println("Timer 15 Reached");
                    mediaPlayer = MediaPlayer.create(mContext, R.raw.countdown_isg);
                    mediaPlayer.start();
                }
            }

            public void onFinish() {

                ChangeFirstColor();

                if (Selected == null) {
                    System.out.println("IF_DATA: Selected NULL: " + Selected);
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
//                    getAccessToken();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            CancelTimer();
                            QuestionCount++;
                            GetQuestions(QuestionCount);

//                            if (QuestionSize <= QuestionCount) {
                            if (QuestionSize == QuestionCount) {
                                GetCounthandler.removeCallbacksAndMessages(null);
                                String TotalScore = null;
                                String QUIZID = null, STUDENTID = null, LEVELID = null;
                                int ScoreValue = 0;
                                String Got_star = null;
                                QuesCount.setVisibility(View.INVISIBLE);

                                Dialog dialog = new Dialog(mContext);
                                WP7ProgressBar alertloader;
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                dialog.setCancelable(false);
                                dialog.setContentView(R.layout.alert_gathering_data);
                                alertloader = dialog.findViewById(R.id.gathering_main_loader);
                                TextView Msg = dialog.findViewById(R.id.gathering_box_msg);
                                TextView okTxt = dialog.findViewById(R.id.gathering_box_ok_txt);
                                okTxt.setVisibility(View.GONE);
                                Msg.setText("Finalizing Score...");
                                ImageView OK = dialog.findViewById(R.id.gathering_box_ok_btn);
                                OK.setVisibility(View.GONE);
                                alertloader.showProgressBar();
                                dialog.show();

                                for (int i = 0; i < ScoreArray.size(); i++) {
                                    System.out.println("Score: Score: " + "Ques: " + ScoreArray.get(i).getQuestionList());
                                    System.out.println("Score: Score: " + " Ans: " + ScoreArray.get(i).getAnswersList());
                                    System.out.println("Score: Score: " + " UserAns: " + ScoreArray.get(i).getUsersAnsList());
                                    System.out.println("Score: Score: " + " Parent ID: " + ScoreArray.get(i).getParentID());
                                    System.out.println("Score: Score: " + " Student ID: " + ScoreArray.get(i).getStudentID());
                                    System.out.println("Score: Score: " + " Quiz ID: " + ScoreArray.get(i).getQuizID());
                                    System.out.println("Score: Score: " + ScoreArray.size());

                                    JSONArray QUES = new JSONArray(ScoreArray.get(i).getQuestionList());
                                    JSONArray ANS = new JSONArray(ScoreArray.get(i).getAnswersList());
                                    JSONArray UANS = new JSONArray(ScoreArray.get(i).getUsersAnsList());

                                    QUIZID = ScoreArray.get(i).getQuizID();
                                    STUDENTID = ScoreArray.get(i).getStudentID();
                                    LEVELID = ScoreArray.get(i).getParentID();
                                    TotalScore = String.valueOf(ScoreArray.size());

                                    System.out.println("AAAA: " + CorrectAnswers);
                                    if (CorrectAnswers == QuestionSize) {
                                        Got_star = "1";
                                    } else {
                                        Got_star = "0";
                                    }


                                    if (String.valueOf(TotalScore).equals("10")) {
                                        ScoreValue = 1;
                                    } else {
                                        ScoreValue = 0;
                                    }
                                    JSONSTRING = "{\"qz_id\": " + QUIZID + "," +
                                            "\"level_id\": " + LEVELID + "," +
                                            "\"studentId\": \"" + STUDENTID + "\"," +
                                            "\"questions\": " + QUES + "," +
                                            "\"answer\": " + ANS + "," +
                                            "\"user_answer\": " + UANS + "," +
                                            "\"no_of_correct_answer\": " + CorrectAnswers + "," +
                                            "\"got_star\": " + Got_star + "}";

                                    Log.d("JSONSTIRN", JSONSTRING);
                                }
                                PostJsonToServer(JSONSTRING);

                                String finalGot_star = Got_star;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertloader.hideProgressBar();
                                        dialog.dismiss();
                                        Intent intent = new Intent(mContext, FinalResult.class);
                                        intent.putExtra("SCORE", String.valueOf(finalGot_star));
                                        intent.putExtra("CorrectAns", String.valueOf(CorrectAnswers));
                                        intent.putExtra("QuesSize", String.valueOf(QuestionSize));
                                        startActivity(intent);
                                    }
                                }, 4000);

                            } else {
                                StartTimer(timerSec);
                            }

                            Selected = null;
                            QuesCount.setText((QuestionCount + 1) + "/" + QuestionSize);
                            System.out.println("ARSHAD: SKIPPED " + QuestionCount);

                        }
                    }, 8000);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OP_1.setVisibility(View.GONE);
                            OP_2.setVisibility(View.GONE);
                            OP_3.setVisibility(View.GONE);
                            OP_4.setVisibility(View.GONE);

                            LayoutwithButtons.setVisibility(View.GONE);
                            LayoutwithProgress.setVisibility(View.VISIBLE);
//                            Bar1.animateProgress(1000, 0 , 140);
//                            Bar2.animateProgress(1000, 0 , 320);
//                            Bar3.animateProgress(1000, 0 , 200);
//                            Bar4.animateProgress(1000, 0 , 500);
                            if (ExplanationText.equals("null")) {
                                Explanation.setText("");
                            } else {
                                fadeIn.setDuration(1200);
                                fadeIn.setFillAfter(true);
                                Explanation.setVisibility(View.VISIBLE);
                                Explanation.setAlpha(1.0f);
                                Explanation.startAnimation(fadeIn);
                                Explanation.setText(ExplanationText);
                            }

                        }
                    }, 3500);

                    OP_1.setEnabled(false);
                    OP_2.setEnabled(false);
                    OP_3.setEnabled(false);
                    OP_4.setEnabled(false);

//                    InsertWhoClickedWhick();

                    /**
                     * Color in Buttons when Skipped (GREEN)
                     */

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            db.collection("QZ").document("Questions")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot document = task.getResult();
                                            Log.d("Android: ", String.valueOf(document.getData()));

                                            List<String> OP1 = (List<String>) document.get("0");
                                            List<String> OP2 = (List<String>) document.get("1");
                                            List<String> OP3 = (List<String>) document.get("2");
                                            List<String> OP4 = (List<String>) document.get("3");

                                            Log.d("ARSAHD: ", String.valueOf(OP1.size()));

                                            Barto1 = OP1.size();
                                            Barto2 = OP2.size();
                                            Barto3 = OP3.size();
                                            Barto4 = OP4.size();

                                            System.out.println("ARSAHDBAR: Bar1: " + Barto1 + "OP: " + OP1.size() + " Bar2: " + Barto2 + " Bar3: " + Barto3 + " Bar4: " + Barto4);

                                            Bar1.animateProgress(100, 0, Barto1 - 1);
                                            Bar2.animateProgress(100, 0, Barto2 - 1);
                                            Bar3.animateProgress(100, 0, Barto3 - 1);
                                            Bar4.animateProgress(100, 0, Barto4 - 1);

                                            ProgressScore1.setText(String.valueOf(Barto1 - 1));
                                            ProgressScore2.setText(String.valueOf(Barto2 - 1));
                                            ProgressScore3.setText(String.valueOf(Barto3 - 1));
                                            ProgressScore4.setText(String.valueOf(Barto4 - 1));

                                        }
                                    });

                        }
                    }, 3000);

//                    ProgressScore1.setText(String.valueOf(Barto1));
//                    ProgressScore2.setText(String.valueOf(Barto2));
//                    ProgressScore3.setText(String.valueOf(Barto3));
//                    ProgressScore4.setText(String.valueOf(Barto4));

                    String value = null;
                    for (int i = 0; i < questionArray.size(); i++) {
                        value = questionArray.get((QuestionCount)).getAnswer();
                    }
                    if (value.equals("1")) {
//                        OP_1.setBackgroundResource(R.drawable.green_btn_background);
                        Bar1.setMax(TotalUsersInProgress);
                        Bar1.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));
                        AnsList.add(OP_1.getText().toString());
                        UserAnsList.add("SKIPPED");
                    } else if (value.equals("2")) {
//                        OP_2.setBackgroundResource(R.drawable.green_btn_background);
                        Bar2.setMax(TotalUsersInProgress);
                        Bar2.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));
                        AnsList.add(OP_2.getText().toString());
                        UserAnsList.add("SKIPPED");
                    } else if (value.equals("3")) {
//                        OP_3.setBackgroundResource(R.drawable.green_btn_background);
                        Bar3.setMax(TotalUsersInProgress);
                        Bar3.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));
                        AnsList.add(OP_3.getText().toString());
                        UserAnsList.add("SKIPPED");
                    } else if (value.equals("4")) {
//                        OP_4.setBackgroundResource(R.drawable.green_btn_background);
                        Bar4.setMax(TotalUsersInProgress);
                        Bar4.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));
                        AnsList.add(OP_4.getText().toString());
                        UserAnsList.add("SKIPPED");
                    }

                } else {
                    System.out.println("IF_DATA: Selected NOT NULL: " + Selected);
                    if (mediaPlayer != null) {
                        mediaPlayer.stop();
                        mediaPlayer.release();
                    }
//                    getAccessToken();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            CancelTimer();
                            QuestionCount++;
                            GetQuestions(QuestionCount);

//                            if (QuestionSize <= QuestionCount) {
                            if (QuestionSize == QuestionCount) {
                                GetCounthandler.removeCallbacksAndMessages(null);
                                String TotalScore = null;
                                String QUIZID = null, STUDENTID = null, LEVELID = null;
                                int ScoreValue = 0;
                                String Got_star = null;
                                QuesCount.setVisibility(View.INVISIBLE);

                                Dialog dialog = new Dialog(mContext);
                                WP7ProgressBar alertloader;
                                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                                dialog.setCancelable(false);
                                dialog.setContentView(R.layout.alert_gathering_data);
                                alertloader = dialog.findViewById(R.id.gathering_main_loader);
                                TextView Msg = dialog.findViewById(R.id.gathering_box_msg);
                                TextView okTxt = dialog.findViewById(R.id.gathering_box_ok_txt);
                                okTxt.setVisibility(View.GONE);
                                Msg.setText("Finalizing Score...");
                                ImageView OK = dialog.findViewById(R.id.gathering_box_ok_btn);
                                OK.setVisibility(View.GONE);
                                alertloader.showProgressBar();
                                dialog.show();

                                for (int i = 0; i < ScoreArray.size(); i++) {
//                                    System.out.println("Score: Score: "+ScoreArray.get(i).getScore()+" Ques: "+ScoreArray.get(i).getQuestion());
                                    System.out.println("Score: Score: " + "Ques: " + ScoreArray.get(i).getQuestionList());
                                    System.out.println("Score: Score: " + " Ans: " + ScoreArray.get(i).getAnswersList());
                                    System.out.println("Score: Score: " + " UserAns: " + ScoreArray.get(i).getUsersAnsList());
                                    System.out.println("Score: Score: " + " Parent ID: " + ScoreArray.get(i).getParentID());
                                    System.out.println("Score: Score: " + " Student ID: " + ScoreArray.get(i).getStudentID());
                                    System.out.println("Score: Score: " + " Quiz ID: " + ScoreArray.get(i).getQuizID());
                                    System.out.println("Score: Score: " + ScoreArray.size());

                                    JSONArray QUES = new JSONArray(ScoreArray.get(i).getQuestionList());
                                    JSONArray ANS = new JSONArray(ScoreArray.get(i).getAnswersList());
                                    JSONArray UANS = new JSONArray(ScoreArray.get(i).getUsersAnsList());

                                    TotalScore = String.valueOf(ScoreArray.size());
                                    QUIZID = ScoreArray.get(i).getQuizID();
                                    STUDENTID = ScoreArray.get(i).getStudentID();
                                    LEVELID = ScoreArray.get(i).getParentID();
                                    TotalScore = String.valueOf(ScoreArray.size());

//                                    String Q__ANS = "CORRECTANS";
//                                    if (ScoreArray.get(i).getAnswersList() == ScoreArray.get(i).getUsersAnsList()){
//                                        System.out.println("AAAA: Inside IF");
//                                        Q_ANS.add(Q__ANS);
//                                    }

                                    System.out.println("AAAA: " + CorrectAnswers);
                                    if (CorrectAnswers == QuestionSize) {
                                        Got_star = "1";
                                    } else {
                                        Got_star = "0";
                                    }

                                    if (String.valueOf(TotalScore).equals("10")) {
                                        ScoreValue = 1;
                                    } else {
                                        ScoreValue = 0;
                                    }
                                    JSONSTRING = "{\"qz_id\": " + QUIZID + "," +
                                            "\"level_id\": " + LEVELID + "," +
                                            "\"studentId\": \"" + STUDENTID + "\"," +
                                            "\"questions\": " + QUES + "," +
                                            "\"answer\": " + ANS + "," +
                                            "\"user_answer\": " + UANS + "," +
                                            "\"no_of_correct_answer\": " + CorrectAnswers + "," +
                                            "\"got_star\": " + Got_star + "}";

                                    Log.d("JSONSTIRN", JSONSTRING);
                                }
                                PostJsonToServer(JSONSTRING);

                                String finalGot_star = Got_star;
                                new Handler().postDelayed(new Runnable() {
                                    @Override
                                    public void run() {
                                        alertloader.hideProgressBar();
                                        dialog.dismiss();
                                        Intent intent = new Intent(mContext, FinalResult.class);
                                        intent.putExtra("SCORE", String.valueOf(finalGot_star));
                                        intent.putExtra("CorrectAns", String.valueOf(CorrectAnswers));
                                        intent.putExtra("QuesSize", String.valueOf(QuestionSize));
                                        startActivity(intent);
                                    }
                                }, 4000);

                            } else {
                                StartTimer(timerSec);
                            }

                            Selected = null;
                            QuesCount.setText((QuestionCount + 1) + "/" + QuestionSize);
                            System.out.println("ARSHAD: NON-SKIPPED " + QuestionCount);

                        }
                    }, 8000);

                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            OP_1.setVisibility(View.GONE);
                            OP_2.setVisibility(View.GONE);
                            OP_3.setVisibility(View.GONE);
                            OP_4.setVisibility(View.GONE);

                            if (ExplanationText.equals("null")) {
                                Explanation.setText("");
                            } else {
                                fadeIn.setDuration(1200);
                                fadeIn.setFillAfter(true);
                                Explanation.setVisibility(View.VISIBLE);
                                Explanation.setAlpha(1.0f);
                                Explanation.startAnimation(fadeIn);
                                Explanation.setText(ExplanationText);
                            }


                            LayoutwithButtons.setVisibility(View.GONE);
                            LayoutwithProgress.setVisibility(View.VISIBLE);


                        }
                    }, 3500);


                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            db.collection("QZ").document("Questions")
                                    .get()
                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                            DocumentSnapshot document = task.getResult();
                                            Log.d("Android: ", String.valueOf(document.getData()));

                                            List<String> OP1 = (List<String>) document.get("0");
                                            List<String> OP2 = (List<String>) document.get("1");
                                            List<String> OP3 = (List<String>) document.get("2");
                                            List<String> OP4 = (List<String>) document.get("3");

                                            Log.d("ARSAHD: ", String.valueOf(OP1.size()));

                                            Barto1 = OP1.size();
                                            Barto2 = OP2.size();
                                            Barto3 = OP3.size();
                                            Barto4 = OP4.size();

                                            Bar1.animateProgress(100, 0, Barto1 - 1);
                                            Bar2.animateProgress(100, 0, Barto2 - 1);
                                            Bar3.animateProgress(100, 0, Barto3 - 1);
                                            Bar4.animateProgress(100, 0, Barto4 - 1);

                                            ProgressScore1.setText(String.valueOf(Barto1 - 1));
                                            ProgressScore2.setText(String.valueOf(Barto2 - 1));
                                            ProgressScore3.setText(String.valueOf(Barto3 - 1));
                                            ProgressScore4.setText(String.valueOf(Barto4 - 1));


                                        }
                                    });

                        }
                    }, 3000);

                    OP_1.setEnabled(false);
                    OP_2.setEnabled(false);
                    OP_3.setEnabled(false);
                    OP_4.setEnabled(false);


                    InsertWhoClickedWhick();


                    System.out.println("ARSAHDBAR: Bar1: " + Barto1 + " Bar2: " + Barto2 + " Bar3: " + Barto3 + " Bar4: " + Barto4);


                    String value = null;
                    for (int i = 0; i < questionArray.size(); i++) {
                        value = questionArray.get((QuestionCount)).getAnswer();
                    }

                    /**
                     * Color in Buttons when Button Clicked (GREEN & RED)
                     */

                    if (value.equals("1")) {
                        CorrectAnswers++;
                        Bar1.setMax(TotalUsersInProgress);
                        Bar1.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));
                        AnsList.add(OP_1.getText().toString());
                    } else if (value.equals("2")) {
                        CorrectAnswers++;
                        Bar2.setMax(TotalUsersInProgress);
                        Bar2.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));
                        AnsList.add(OP_2.getText().toString());
                    } else if (value.equals("3")) {
                        CorrectAnswers++;
                        Bar3.setMax(TotalUsersInProgress);
                        Bar3.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));
                        AnsList.add(OP_3.getText().toString());
                    } else if (value.equals("4")) {
                        CorrectAnswers++;
                        Bar4.setMax(TotalUsersInProgress);
                        Bar4.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));
                        AnsList.add(OP_4.getText().toString());
                    }

                    System.out.println("WEASE: value " + value + " selected " + Selected);

                    if (value.equals(Selected)) {

                        if (Selected.equals("1")) {
//                            AnsList.add(OP_1.getText().toString());
                            UserAnsList.add(OP_1.getText().toString());
                        } else if (Selected.equals("2")) {
//                            AnsList.add(OP_2.getText().toString());
                            UserAnsList.add(OP_2.getText().toString());
                        } else if (Selected.equals("3")) {
//                            AnsList.add(OP_3.getText().toString());
                            UserAnsList.add(OP_3.getText().toString());
                        } else if (Selected.equals("4")) {
//                            AnsList.add(OP_4.getText().toString());
                            UserAnsList.add(OP_4.getText().toString());
                        }
//                        QuesList.add(Question.getText().toString());


//                        ScoreModel model = new ScoreModel();
//                        model.setScore("1");
//                        model.setQuestion(String.valueOf((QuestionCount)));
//                        model.setUsersAnsList(UserAnsList);
//                        model.setQuestionList(QuesList);
//                        model.setAnswersList(AnsList);
//                        model.setParentID(AppPreferenceManager.getLevelId(mContext));
//                        model.setStudentID(AppPreferenceManager.getStudentId(mContext));
//                        model.setQuizID(AppPreferenceManager.getQuizId(mContext));
//                        ScoreArray.add(model);
                    }

                    if (value.equalsIgnoreCase("1")) {
                        System.out.println("WEASE: if 1");
//                        OP_1.setBackgroundResource(R.drawable.red_btn_background);
//                        OP_1.setBackgroundColor(getResources().getColor(R.color.btn_light_green));


                        if (Selected.equals("2")) {
                            CorrectAnswers--;
//                            OP_2.setBackgroundColor(getResources().getColor(R.color.btn_light_red));
                            Bar2.setMax(TotalUsersInProgress);
                            Bar2.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                            UserAnsList.add(OP_2.getText().toString());
                        } else if (Selected.equals("3")) {
                            CorrectAnswers--;
//                            OP_3.setBackgroundColor(getResources().getColor(R.color.btn_light_red));
                            Bar3.setMax(TotalUsersInProgress);
                            Bar3.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                            UserAnsList.add(OP_3.getText().toString());
                        } else if (Selected.equals("4")) {
                            CorrectAnswers--;
//                            OP_4.setBackgroundColor(getResources().getColor(R.color.btn_light_red));
                            Bar4.setMax(TotalUsersInProgress);
                            Bar4.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                            UserAnsList.add(OP_4.getText().toString());
                        }

                    } else if (value.equalsIgnoreCase("2")) {
                        System.out.println("WEASE: if 2");
//                        OP_2.setBackgroundResource(R.drawable.red_btn_background);
//                        OP_2.setBackgroundResource(R.drawable.green_btn_background);


                        if (Selected.equals("1")) {
                            CorrectAnswers--;
//                            OP_1.setBackgroundResource(R.drawable.red_btn_background);
                            Bar1.setMax(TotalUsersInProgress);
                            Bar1.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                            UserAnsList.add(OP_1.getText().toString());
                        } else if (Selected.equals("3")) {
                            CorrectAnswers--;
//                            OP_3.setBackgroundResource(R.drawable.red_btn_background);
                            Bar3.setMax(TotalUsersInProgress);
                            Bar3.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                            UserAnsList.add(OP_3.getText().toString());
                        } else if (Selected.equals("4")) {
                            CorrectAnswers--;
//                            OP_4.setBackgroundResource(R.drawable.red_btn_background);
                            Bar4.setMax(TotalUsersInProgress);
                            Bar4.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                            UserAnsList.add(OP_4.getText().toString());
                        }
                    } else if (value.equalsIgnoreCase("3")) {
                        System.out.println("WEASE: if 3");
//                        OP_3.setBackgroundResource(R.drawable.red_btn_background);
//                        OP_3.setBackgroundResource(R.drawable.green_btn_background);


                        if (Selected.equals("2")) {
                            CorrectAnswers--;
//                            OP_2.setBackgroundResource(R.drawable.red_btn_background);
                            Bar2.setMax(TotalUsersInProgress);
                            Bar2.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                            UserAnsList.add(OP_2.getText().toString());
                        } else if (Selected.equals("1")) {
                            CorrectAnswers--;
//                            OP_1.setBackgroundResource(R.drawable.red_btn_background);
                            Bar1.setMax(TotalUsersInProgress);
                            Bar1.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                            UserAnsList.add(OP_1.getText().toString());
                        } else if (Selected.equals("4")) {
                            CorrectAnswers--;
//                            OP_4.setBackgroundResource(R.drawable.red_btn_background);
                            Bar4.setMax(TotalUsersInProgress);
                            Bar4.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                            UserAnsList.add(OP_4.getText().toString());
                        }

                    } else if (value.equalsIgnoreCase("4")) {
                        System.out.println("WEASE: if 4");
//                        OP_4.setBackgroundResource(R.drawable.red_btn_background);
//                        OP_4.setBackgroundResource(R.drawable.green_btn_background);


                        if (Selected.equals("2")) {
                            CorrectAnswers--;
//                            OP_2.setBackgroundResource(R.drawable.red_btn_background);
                            Bar2.setMax(TotalUsersInProgress);
                            Bar2.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                            UserAnsList.add(OP_2.getText().toString());
                        } else if (Selected.equals("3")) {
                            CorrectAnswers--;
//                            OP_3.setBackgroundResource(R.drawable.red_btn_background);
                            Bar3.setMax(TotalUsersInProgress);
                            Bar3.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                            UserAnsList.add(OP_3.getText().toString());
                        } else if (Selected.equals("1")) {
                            CorrectAnswers--;
//                            OP_1.setBackgroundResource(R.drawable.red_btn_background);
                            Bar1.setMax(TotalUsersInProgress);
                            Bar1.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                            UserAnsList.add(OP_1.getText().toString());
                        }
                    }

                    // Explanation.setText(ExplanationText);

                }
                ScoreModel model = new ScoreModel();
                model.setScore("1");
                model.setQuestion(String.valueOf((QuestionCount)));
                model.setUsersAnsList(UserAnsList);
                model.setQuestionList(QuesList);
                model.setAnswersList(AnsList);
                model.setParentID(AppPreferenceManager.getLevelId(mContext));
                model.setStudentID(AppPreferenceManager.getStudentId(mContext));
                model.setQuizID(AppPreferenceManager.getQuizId(mContext));
                ScoreArray.add(model);
            }
        };
        cTimer.start();
    }

    private void ChangeFirstColor() {
        LayoutwithButtons.setVisibility(View.GONE);
        LayoutwithProgress.setVisibility(View.VISIBLE);
//        Explanation.setVisibility(View.INVISIBLE);

        ProgressScore1.setText("0");
        ProgressScore2.setText("0");
        ProgressScore3.setText("0");
        ProgressScore4.setText("0");

        if (Explanation.getText().equals("Question Explanation")) {
            Explanation.setAlpha(0.0f);
        } else {
            Explanation.setVisibility(View.VISIBLE);
            Explanation.setAlpha(1.0f);
        }

        Bar1.animateProgress(1000, 0, 0);
        Bar2.animateProgress(1000, 0, 0);
        Bar3.animateProgress(1000, 0, 0);
        Bar4.animateProgress(1000, 0, 0);

        if (Selected == null) {

            String value = null;
            for (int i = 0; i < questionArray.size(); i++) {
                value = questionArray.get((QuestionCount)).getAnswer();
            }
            if (value.equals("1")) {
                Bar1.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));
            } else if (value.equals("2")) {
                Bar2.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));
            } else if (value.equals("3")) {
                Bar3.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));
            } else if (value.equals("4")) {
                Bar4.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));
            }

        } else {

            String value = null;
            for (int i = 0; i < questionArray.size(); i++) {
                value = questionArray.get((QuestionCount)).getAnswer();
            }

            if (value.equalsIgnoreCase("1")) {
                Bar1.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));

                if (Selected.equals("2")) {
                    Bar2.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                } else if (Selected.equals("3")) {
                    Bar3.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                } else if (Selected.equals("4")) {
                    Bar4.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                }
            } else if (value.equalsIgnoreCase("2")) {
                System.out.println("WEASE: if 2");
//                        OP_2.setBackgroundResource(R.drawable.red_btn_background);
                Bar2.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));


                if (Selected.equals("1")) {
                    Bar1.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                } else if (Selected.equals("3")) {
                    Bar3.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                } else if (Selected.equals("4")) {
                    Bar4.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                }
            } else if (value.equalsIgnoreCase("3")) {
                System.out.println("WEASE: if 3");
                Bar3.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));

                if (Selected.equals("2")) {
                    Bar2.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                } else if (Selected.equals("1")) {
                    Bar1.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                } else if (Selected.equals("4")) {
                    Bar4.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                }

            } else if (value.equalsIgnoreCase("4")) {
                System.out.println("WEASE: if 4");
                Bar4.setProgressColors(getResources().getColor(R.color.btn_light_green), getResources().getColor(R.color.btn_green));

                if (Selected.equals("2")) {
                    Bar2.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                } else if (Selected.equals("3")) {
                    Bar3.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                } else if (Selected.equals("1")) {
                    Bar1.setProgressColors(getResources().getColor(R.color.btn_light_red), getResources().getColor(R.color.btn_red));
                }
            }
        }

    }

    private void PostJsonToServer(String jsonStr) {
        OkHttpClient client = new OkHttpClient();

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(jsonStr, JSON);


        Request request = new Request.Builder()
                .url(URL_SUBMIT_QUIZ)
                .post(body)
                .header("Authorization", "Bearer " + AppPreferenceManager.getAccessToken(mContext))
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Log.d("ResponseValue: JSON", e.getMessage());
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                String Response = response.body().string();
                Log.d("ResponseValue: JSON", Response);

                try {
                    JSONObject obj = new JSONObject(Response);
                    String response_code = obj.getString(JTAG_RESPONSECODE);
                    if (response_code.equalsIgnoreCase(RESPONSE_SUCCESS)) {
                        JSONObject secobj = obj.getJSONObject(JTAG_RESPONSE);
                        String status_code = secobj.getString(JTAG_STATUSCODE);
                        if (status_code.equalsIgnoreCase(STATUSCODE_SUCCESS)) {
                            String STARS = secobj.getString(JTAGVALUE_TOTAL_STARS);
                            System.out.println("ResponseValue: stars INSIDE Success" + STARS);
                            AppPreferenceManager.setStudentStar(mContext, STARS);


                        } else if (status_code.equalsIgnoreCase(STATUS_CODE_NO_QUIZ)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext, getString(R.string.no_quiz_available));
                                }
                            });
                        } else if (status_code.equalsIgnoreCase("216")) {
                            System.out.println("ResponseValue: Called for Token");
                            AppUtilityMethod.RenewToken(mContext);
                            PostJsonToServer(JSONSTRING);
                        } else if (status_code.equalsIgnoreCase(STATUSCODE_INVALIDUSERNAMEORPASWD)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext, getString(R.string.invalid_usr_pswd));
                                }
                            });


                        } else if (status_code.equalsIgnoreCase(STATUSCODE_MISSING_PARAMETER)) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    AppUtilityMethod.showDialogAlertDismiss((Activity) mContext, getString(R.string.missing_parameter));
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
                            PostJsonToServer(JSONSTRING);

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
                        PostJsonToServer(JSONSTRING);

                    }
                } catch (Exception ex) {
                    System.out.println("The Exception in edit profile is" + ex.toString());
                }


            }
        });
    }

    private void InsertWhoClickedWhick() {

        DocumentReference Doc_ref = db.collection("QZ").document("Questions");

        if (Selected.equals("1")) {
            Doc_ref.update("0", FieldValue.arrayUnion(AppPreferenceManager.getStudentId(mContext)));
        } else if (Selected.equals("2")) {
            Doc_ref.update("1", FieldValue.arrayUnion(AppPreferenceManager.getStudentId(mContext)));
        } else if (Selected.equals("3")) {
            Doc_ref.update("2", FieldValue.arrayUnion(AppPreferenceManager.getStudentId(mContext)));
        } else if (Selected.equals("4")) {
            Doc_ref.update("3", FieldValue.arrayUnion(AppPreferenceManager.getStudentId(mContext)));
        }


    }

    private void CancelTimer() {
        System.out.println("ARSHAD: Cancel timer Called");
        if (cTimer != null)
            cTimer.cancel();
    }

    @Override
    public void onBackPressed() {
        if (!shouldAllowBack()) {
            showAlert(activity, getString(R.string.cannot_go_back));
        } else {
            super.onBackPressed();
        }
    }

    private void showAlert(Activity activity, String string) {
        final Dialog dialog = new Dialog(activity);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.alert_single_btn);
        TextView Msg = dialog.findViewById(R.id.single_msg_txt);
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

    private boolean shouldAllowBack() {
        return false;
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        if (flag) {
//            Toast.makeText(getApplicationContext(), "start", 1).show();
        } else {
//            Toast.makeText(getApplicationContext(), "Restart 2", 1).show();
            Intent i = new Intent(mContext, MainActivity.class);
            finish();
            startActivity(i);

        }
    }

    @Override
    protected void onRestart() {
        // TODO Auto-generated method stub
        super.onRestart();
//        Toast.makeText(getApplicationContext(), "Restart", 1).show();
        flag = false;
    }

    @Override
    protected void onUserLeaveHint() {
        // When user presses home page
//        Log.v(TAG, "Home Button Pressed");
        super.onUserLeaveHint();
//        finishAffinity();
//        System.exit(0);
        if (cTimer != null) {
            cTimer.cancel();
        }


        try {
            if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                Log.d("TAG------->", "player is running");
                mediaPlayer.stop();
                Log.d("Tag------->", "player is stopped");
                mediaPlayer.release();
                Log.d("TAG------->", "player is released");
            }
        } catch (Exception e) {
            e.fillInStackTrace();
        }

        handler.removeCallbacksAndMessages(null);
        finish();
//        Intent svc = new Intent(mContext, BackgroundSoundService.class);
//        stopService(svc);
    }

    @Override
    public void onPause() {
        if (isApplicationSentToBackground(this)) {
            // Do what you want to do on detecting Home Key being Pressed
            RemoveUser();
            if (cTimer != null) {
                cTimer.cancel();
            }

            try {
                if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                    Log.d("TAG------->", "player is running");
                    mediaPlayer.stop();
                    Log.d("Tag------->", "player is stopped");
                    mediaPlayer.release();
                    Log.d("TAG------->", "player is released");
                }
            } catch (Exception e) {
                e.fillInStackTrace();
            }

            handler.removeCallbacksAndMessages(null);
            finish();
            DocumentReference Doc_ref = db.collection("QZ").document("Questions");
            Doc_ref.update("0", FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));
            Doc_ref.update("1", FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));
            Doc_ref.update("2", FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));
            Doc_ref.update("3", FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));

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

            AddUserVerficationDetails("1");

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
        Log.d("CurrentData", currentDate);

        Map<String, Object> map = new HashMap<>();
        map.put("CurrentState", AppState);
        map.put("LoginTime", currentDate);
        map.put("DeviceID", android_id);
        map.put("DeviceName", DeviceName);
        map.put("StudentId", AppPreferenceManager.getStudentId(mContext));
        map.put("UserName", AppPreferenceManager.getQIUserName(mContext));
        map.put("Version", AppPreferenceManager.getAppVersion(mContext));
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
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            System.out.println("count: V Has focus");
            AddUserVerficationDetails("2");
        } else {
            System.out.println("count: V No focus");


//            Intent svc = new Intent(mContext, BackgroundSoundService.class);
//            stopService(svc);

            if (isApplicationSentToBackground(this)) {
                AddUserVerficationDetails("1");
                handler.removeCallbacksAndMessages(null);
                finish();
                DocumentReference Doc_ref = db.collection("QZ").document("Questions");
                Doc_ref.update("0", FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));
                Doc_ref.update("1", FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));
                Doc_ref.update("2", FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));
                Doc_ref.update("3", FieldValue.arrayRemove(AppPreferenceManager.getStudentId(mContext)));

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
                if (cTimer != null) {
                    cTimer.cancel();
                }

                try {
                    if (mediaPlayer != null && mediaPlayer.isPlaying()) {
                        Log.d("TAG------->", "player is running");
                        mediaPlayer.stop();
                        Log.d("Tag------->", "player is stopped");
                        mediaPlayer.release();
                        Log.d("TAG------->", "player is released");
                    }
                } catch (Exception e) {
                    e.fillInStackTrace();
                }

                Intent svc = new Intent(mContext, BackgroundSoundService.class);
                stopService(svc);
            }
        }
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

    private void getAccessToken() {
        AppUtilityMethod.getToken(mContext, new AppUtilityMethod.GetTokenSuccess() {
            @Override
            public void tokenrenewed() {

            }
        });
    }

    private void preparedmediaplayer(int questionCount) {
        /* Toast.makeText(this, "successs", Toast.LENGTH_SHORT).show();*/

        try {
            media_player.reset();
            media_player.setDataSource(questionArray.get(questionCount).getQuestion());
            Log.e("audioplay", questionArray.get(questionCount).getQuestion());

            media_player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }

            });
            media_player.prepare();
            duration_time.setText(milliseconds(media_player.getDuration()));
        } catch (Exception exception) {
            Toast.makeText(this, "failed for load" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println("failed for load" + exception.getMessage());
        }

    }

    private void preparedmediaplayer_audio(int questionCount) {
        /* Toast.makeText(this, "successs", Toast.LENGTH_SHORT).show();*/
        try {
            mediaplayer.reset();
            mediaplayer.setDataSource(questionArray.get(questionCount).getQuestion());
            Log.e("audioplay1", questionArray.get(questionCount).getQuestion());

            mediaplayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mp.start();
                }
            });
            mediaplayer.prepare();
            duration_time_audio.setText(milliseconds(mediaplayer.getDuration()));

        } catch (Exception exception) {
            Toast.makeText(this, "Exception" + exception.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println("Exception" + exception.getMessage());
        }

    }

    private Runnable updater = new Runnable() {
        @Override
        public void run() {
            updateseekbar();
            long currentduration = media_player.getCurrentPosition();
            textcurrent_time.setText(milliseconds(currentduration));

        }
    };

    private Runnable updater_audio = new Runnable() {
        @Override
        public void run() {
            updateseekbar_audio();
            long currentduration = mediaplayer.getCurrentPosition();
            textcurrent_time_audio.setText(milliseconds(currentduration));
        }
    };

    private void updateseekbar() {

        if (media_player.isPlaying()) {
            /* Toast.makeText(this, "successs", Toast.LENGTH_SHORT).show();*/

            seebbar.setProgress((int) (((float) media_player.getCurrentPosition() / media_player.getDuration() * 100)));
            handler1.postDelayed(updater, 1000);
        }
    }

    private void updateseekbar_audio() {

        if (mediaplayer.isPlaying()) {
            seekbar_audio.setProgress((int) (((float) mediaplayer.getCurrentPosition() / mediaplayer.getDuration() * 100)));
            handler1.postDelayed(updater_audio, 1000);
        }
    }

    private String milliseconds(long milliscnd) {
        String timer = "";
        String secondString;
        int hour = (int) (milliscnd / (1000 * 60 * 60));
        int min = (int) (milliscnd % (1000 * 60 * 60)) / (1000 * 60);
        int sec = (int) (milliscnd % (1000 * 60 * 60)) % (1000 * 60) / 1000;
        if (hour > 0) {
            timer = hour + ";";
        }
        if (sec < 10) {
            secondString = "0" + sec;
        } else {
            secondString = "" + sec;
        }
        timer = timer + min + ":" + secondString;
        return timer;
    }

}
