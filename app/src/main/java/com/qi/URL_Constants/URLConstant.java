package com.qi.URL_Constants;

public interface URLConstant {


    /*======================= Dev Url ======================================> */
//    public static String URL_HEAD="http://memberqz.mobatia.com/api/";

    /*  <==================== NOW LIVE =====================================> */
   /* public static String URL_HEAD="http://memberqz.mobatia.com:8081/api/";*/
    public static String URL_HEAD="http://memberqz.mobatia.com:8081/api/";
    public static String URL_METHOD_GET_ACCESSTOKEN = URL_HEAD+"user/token";
    public static String URL_PARENT_REGISTRATION = URL_HEAD+"memberRegistration";
    public static String URL_PARENT_LOGIN = URL_HEAD+"login";
    public static String URL_GET_QUIZ = URL_HEAD+"getQuiz";
    public static String URL_SUBMIT_QUIZ=URL_HEAD+"submitQuiz";
    public static String URL_GET_LEADERBOARD=URL_HEAD+"leaderBoard";
    public static String URL_FORGOT_PASS=URL_HEAD+"forgotPassword";
    public static String URL_CHANGEPASSWORD=URL_HEAD+"changePassword";
    public static String URL_GET_NOTIFICATIONS=URL_HEAD+"getAlerts";
    public static String URL_GET_BADGE=URL_HEAD+"getBadgeCount";
    public static String URL_RESET_BADGE=URL_HEAD+"resetBadge";


//    public static String URL_GET_ALL_STUDENTS=URL_HEAD+"api/getStudents";
//    public static String URL_FORGOTPASSWORD=URL_HEAD+"api/forgotPassword";



}
