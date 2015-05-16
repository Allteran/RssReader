package ua.ck.geekhub.prozapas.ghprozapasrssreader.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import ua.ck.geekhub.prozapas.ghprozapasrssreader.R;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.activities.MainActivity;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.tasks.DownloadString;
import ua.ck.geekhub.prozapas.ghprozapasrssreader.utilities.Const;

/**
 * Created by Allteran on 11.12.2014.
 */
public class RssPullService extends Service {
    private static final String TAG = RssPullService.class.getName();
    public NotificationManager mNotificationManager;
    private String mDownloadedString;
    private String mLastArticle;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "Service has been started");

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        mLastArticle = sharedPreferences.getString(Const.SHARED_PREFERECES_KEY, null);

        mNotificationManager = (NotificationManager) getApplicationContext().getSystemService(Context
                .NOTIFICATION_SERVICE);

//        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
//        notificationBuilder
//                .setSmallIcon(R.drawable.ic_launcher)
//                .setContentTitle(getString(R.string.app_name))
//                .setContentText(getString(R.string.notification_message));
//
//        Intent serviceIntent = new Intent(getApplicationContext(), MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, serviceIntent,
//                PendingIntent.FLAG_UPDATE_CURRENT);
//        notificationBuilder.setContentIntent(pendingIntent);
//
//        Notification notification = notificationBuilder.build();
//        startForeground(Const.NOTIFICATION_ID, notification);

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    new DownloadRssTask().execute(Const.URL);
                    try {
                        TimeUnit.SECONDS.sleep(Const.SLEEP_UPDATE_TIME);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return new Binder();
    }

    class DownloadRssTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            String url = params[0];
            DownloadString downloadString = new DownloadString(url);
            mDownloadedString = downloadString.getDownloadedString();
            return null;

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.i(TAG, "Last article = Downloaded string?" + String.valueOf(mDownloadedString.equals(mLastArticle)));
            if (mLastArticle != null) {
                if (!mLastArticle.equals(mDownloadedString)) {

                    Intent updNotificationIntent = new Intent(getApplicationContext(), MainActivity
                            .class);
                    updNotificationIntent.putExtra(Const.UPD_NOTIFICATION_STRING, mDownloadedString);

                    PendingIntent updNotificationPendingIntent = PendingIntent.getActivity(getApplicationContext()
                            , 0, updNotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext());
                    notificationBuilder
                            .setSmallIcon(R.drawable.ic_update_article)
                            .setContentTitle(getString(R.string.notification_update_title))
                            .setContentText(getString(R.string.got_new_update_message))
                            .setTicker(getString(R.string.got_new_update_message));
                    notificationBuilder.setContentIntent(updNotificationPendingIntent);
                    Notification updNotification = notificationBuilder.build();

                    startForeground(Const.NOTIFICATION_ID, updNotification);

                }
            }
        }
    }
}
