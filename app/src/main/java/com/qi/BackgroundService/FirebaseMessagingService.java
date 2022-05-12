package com.qi.BackgroundService;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.RemoteMessage;
import com.qi.Common.LoginActivity;
import com.qi.MainActivity;
import com.qi.Manager.AppPreferenceManager;
import com.qi.R;

import org.json.JSONException;
import org.json.JSONObject;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {

    private static final String TAG = "MyFirebaseMsgService";
    Context mContext = this;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        Log.d(TAG, "From: " + remoteMessage.getFrom());

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload: " + remoteMessage.getData());

            try {
                JSONObject json = new JSONObject(remoteMessage.getData().toString().replaceAll("=", ":"));
//                handleDataMessage(json);
                try {
                    JSONObject data = json.getJSONObject("body");
                    String title = data.optString("message");
                    sendNotification(title,mContext.getResources().getString(R.string.app_name));


                } catch (JSONException e) {
                    Log.e(TAG, "Json Exception: " + e.getMessage());
                } catch (Exception e) {
                    Log.e(TAG, "Exception: " + e.getMessage());
                }

            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }

        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody(),mContext.getResources().getString(R.string.app_name));
        }
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);
        System.out.println("TOKE: "+token);
        AppPreferenceManager.setFirebaseToken(mContext,token);
        sendRegistrationToServer(token);
    }


    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
        FirebaseInstanceId.getInstance().getToken();
    }

    private void sendNotification(String messageBody,String Title) {
        Intent intent;

        if (AppPreferenceManager.getIsUserAlreadyLoggedIn(mContext)){
            intent = new Intent(this, MainActivity.class);
        }else {
            intent = new Intent(this, LoginActivity.class);
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(R.drawable.quiz_logo_n)
                        .setContentTitle(Title)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        // Since android Oreo notification channel is needed.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_DEFAULT);
            assert notificationManager != null;
            notificationManager.createNotificationChannel(channel);
        }

        assert notificationManager != null;
        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

}
