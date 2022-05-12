package com.qi.Manager;

import android.content.Context;
import android.content.SharedPreferences;

public class AppPreferenceManager {

    public static void setAccessToken(Context mContext, String accesstoken) {
        SharedPreferences prefs = mContext.getSharedPreferences("ALGUBRA_QI", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("access_token", accesstoken);
        editor.commit();
    }

    public static String getAccessToken(Context mContext) {
        String tokenValue = "";
        SharedPreferences prefs = mContext.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        tokenValue = prefs.getString("access_token", "0");
        return tokenValue;
    }

    public static void setHomePos(Context mContext, String accesstoken) {
        SharedPreferences prefs = mContext.getSharedPreferences("ALGUBRA_QI", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("HomePos", accesstoken);
        editor.commit();
    }

    public static String getHomePos(Context mContext) {
        String tokenValue = "";
        SharedPreferences prefs = mContext.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        tokenValue = prefs.getString("HomePos", "0");
        return tokenValue;
    }

    public static boolean getIsGuest(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getBoolean("is_guest", true);

    }

    public static void setIsGuest(Context context, boolean result) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_guest", result);
        editor.commit();

    }

    public static String getSchoolSelection(Context context) {
        String tokenValue = "";
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        tokenValue = prefs.getString("selection", "ISG");
        return tokenValue;

    }

    public static void setSchoolSelection(Context context, String result) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("selection", result);
        editor.commit();

    }

    public static String getMobileNo(Context context) {
        String mobile = "";
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        mobile = prefs.getString("mobile", "");
        return mobile;

    }

    public static void setMobileNo(Context context, String mobile) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("mobile", mobile);
        editor.commit();

    }

    public static boolean getIsUserAlreadyLoggedIn(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getBoolean("is_loggedin", false);

    }

    public static void setIsUserAlreadyLoggedIn(Context context, boolean result) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean("is_loggedin", result);
        editor.commit();

    }

    public static String getUserId(Context context) {
        String userId = "";
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        userId = prefs.getString("userId", "");
        return userId;

    }

    public static void setUserId(Context context, String userId) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userId", userId);
        editor.commit();

    }

    public static String getUserCode(Context context) {
        String userId = "";
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        userId = prefs.getString("userCode", "");
        return userId;

    }

    public static void setUserCode(Context context, String userId) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("userCode", userId);
        editor.commit();

    }

    public static String getStudentsResponseFromLoginAPI(Context context) {
        String student_list = "";
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        student_list = prefs.getString("student_list", "");
        return student_list;

    }

    public static void setStudentsResponseFromLoginAPI(Context context, String student_list) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("student_list", student_list);
        editor.commit();

    }

    public static String getStudentId(Context context) {
        String studentId = "";
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        studentId = prefs.getString("studentId", "");
        return studentId;

    }

    public static void setStudentId(Context context, String studentId) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("studentId", studentId);
        editor.commit();

    }

    public static String getStudentName(Context context) {
        String studentName = "";
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        studentName = prefs.getString("studentName", "");
        return studentName;

    }

    public static void setStudentName(Context context, String studentName) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("studentName", studentName);
        editor.commit();

    }

    public static String getPhno(Context context) {
        String Phno = "";
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        Phno = prefs.getString("Phno", "");
        return Phno;

    }

    public static void setPhno(Context context, String Phno) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Phno", Phno);
        editor.commit();

    }

    public static String getBadgecount(Context context) {
        String badgecount = "";
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        badgecount = prefs.getString("badgecount", "0");
        return badgecount;

    }

    public static void setBadgecount(Context context, String badgecount) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("badgecount", badgecount);
        editor.commit();

    }

    public static String getUserRespFromLoginAPI(Context context) {
        String user_resp = "";
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        user_resp = prefs.getString("user_resp", "");
        return user_resp;

    }

    public static void setUserRespFromLoginAPI(Context context, String user_resp) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_resp", user_resp);
        editor.commit();

    }

    /*********** Force Update **********/
    public static void setVersionFromApi(Context context, String result) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("android_version", result);
        editor.commit();
    }

    public static String getVersionFromApi(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("android_version", "");
    }

    /*********** Save Quiz Time **********/
    public static void setEndTime(Context context, String result) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("end_time", result);
        editor.commit();
    }

    public static String getEndTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("end_time", "");
    }

    public static void setClearEndTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    /*********** Save Total Question **********/
    public static void setTotalQuestion(Context context, String result) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("total_ques", result);
        editor.commit();
    }

    public static String getTotalQuestion(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("total_ques", "");
    }

    public static void setClearTotalQuestion(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.clear();
        editor.commit();
    }

    /*********** Save Total Time **********/

    public static void setTotalTime(Context context, String result) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("total_Time", result);
        editor.commit();
    }

    public static String getTotalTime(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("total_Time", "");
    }

    /*********** Save Quiz Json Response **********/
    public static void setJsonResponse(Context context, String result) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("quiz_json", result);
        editor.commit();
    }

    public static String getJsonResponse(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("quiz_json", "");
    }

    /*********** Save Phone **********/
    public static void setPhone(Context context, String result) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Save_Phone", result);
        editor.commit();
    }

    public static String getPhone(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("Save_Phone", "");
    }

    /*********** Save Pass **********/
    public static void setPass(Context context, String result) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Save_Pass", result);
        editor.commit();
    }

    public static String getPass(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("Save_Pass", "");
    }

    /*********** Save QuizId **********/
    public static void setQuizId(Context context, String result) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Save_QuizId", result);
        editor.commit();
    }

    public static String getQuizId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("Save_QuizId", "");
    }

    /*********** Save QuizId **********/
    public static void setLevelId(Context context, String result) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Save_LevelId", result);
        editor.commit();
    }

    public static String getLevelId(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("Save_LevelId", "");
    }

    /*********** Save QuizId **********/
    public static void setStudentStar(Context context, String result) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("Save_StudentStar", result);
        editor.commit();
    }

    public static String getStudentStar(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("Save_StudentStar", "");
    }

    /************* Save App test version **************/
    public static void setAppVersion(Context context, String version) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("App_version", version);
        editor.commit();
    }

    public static String getAppVersion(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("App_version", "");
    }

    /************* Save if it is Total Score **************/
    public static String getTotalScore(Context context) {
        String totalscore = "";
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA",
                Context.MODE_PRIVATE);
        totalscore = prefs.getString("totalscore", "");
        return totalscore;

    }

    public static void setTotalScore(Context context, String totalscore) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("totalscore", totalscore);
        editor.commit();

    }

    /************* Save if it is Single Student **************/
    public static void setSingleStudent(Context context, String version) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("SingleStudent", version);
        editor.commit();
    }

    public static String getSingleStudent(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("SingleStudent", "");
    }


    /************* Save Firebase UID Data **************/
    public static void setFirebaseUID(Context context, String version) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("FirebaseUID", version);
        editor.commit();
    }

    public static String getFirebaseUID(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("FirebaseUID", "");
    }

    /************* Save Firebase UID Data **************/
    public static void setQIUserName(Context context, String version) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("QIUserName", version);
        editor.commit();
    }

    public static String getQIUserName(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("QIUserName", "");
    }

    /************* Save Firebase Token **************/

    public static void setFirebaseToken(Context context, String version) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("FirebaseToken", version);
        editor.commit();
    }

    public static String getFirebaseToken(Context context) {
        SharedPreferences prefs = context.getSharedPreferences("ALGUBRA_QI",
                Context.MODE_PRIVATE);
        return prefs.getString("FirebaseToken", "");
    }
}
