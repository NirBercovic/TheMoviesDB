package com.academy.fundamentals.Movies.Downloader;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;

import android.widget.Toast;

import com.academy.fundamentals.Movies.Movies.CommonData.Constants;
import com.academy.fundamentals.R;


public class DownloadServiceController implements Constants {

    public static class DownloadService extends Service {

        private NotificationCompat.Builder m_notificationBuilder;
        private NotificationManager m_notificationManager;

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }

        private void sendBroadcastMsgDownloadComplete(String filePath) {
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra(MOVIE_POSTER_PATH_EXTRA, filePath);
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
        }

        private void updateNotificationOnFinish()
        {
            String progressStr = getString(R.string.notification_message, NOTIFICATION_PROGRESS_MAX);
            m_notificationBuilder.setContentTitle(getString(R.string.notification_download_finished))
                    .setProgress(0,0,false)
                    .setContentText(progressStr)
                    .setOngoing(false);
            m_notificationManager.notify(ONGOING_NOTIFICATION_ID, m_notificationBuilder.build());


        }

        private void updateNotification(int progress) {

            //just to see the progress - the files download are small so we might miss the progress
            SystemClock.sleep(500);

            if (m_notificationManager != null) {

                String progressStr = getString(R.string.notification_message, progress);
                m_notificationBuilder.setProgress(NOTIFICATION_PROGRESS_MAX ,progress,false)
                .setContentText(progressStr);
                m_notificationManager.notify(ONGOING_NOTIFICATION_ID, m_notificationBuilder.build());
            }
        }

        private NotificationCompat.Builder createNotification(int progress) {
            String progressStr = getString(R.string.notification_message, progress);

            return new NotificationCompat.Builder(this, CHANNEL_ID)
                    .setSmallIcon(android.R.drawable.stat_sys_upload)
                    .setContentTitle(getText(R.string.notification_title))
                    .setContentText(progressStr)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .setOngoing(true)
                    .setOnlyAlertOnce(true)
                    .setProgress(NOTIFICATION_PROGRESS_MAX, progress, false);
        }

        private void createNotificationChannel() {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // SDK 26
                CharSequence name = NOTIFICATION_CHANNEL_NAME;
                String description = NOTIFICATION_CHANNEL_DESCRIPTION;
                int importance = NotificationManager.IMPORTANCE_HIGH;
                NotificationChannel channel = new NotificationChannel(
                        CHANNEL_ID,
                        name,
                        importance);
                channel.setDescription(description);
                channel.enableLights(true);
                channel.setLightColor(Color.RED);
                m_notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
                m_notificationManager.createNotificationChannel(channel);
            }

        }

        private void startForeground() {
            createNotificationChannel();
            m_notificationBuilder = createNotification(0);
            //startForeground(ONGOING_NOTIFICATION_ID, notification);
            m_notificationManager.notify(ONGOING_NOTIFICATION_ID, m_notificationBuilder.build());

        }

        @Override
        public int onStartCommand(Intent intent, int flags, final int startId) {
            startForeground();
            String url = intent.getStringExtra(DOWNLOAD_URL);

            new DownloadThread(url, new DownloadThread.DownloadCallBack() {
                @Override
                public void onProgressUpdate(int percent) {
                    updateNotification(percent);
                }
                @Override
                public void onDownloadFinished(String filePath) {
                    updateNotificationOnFinish();
                    sendBroadcastMsgDownloadComplete(filePath);
                    stopSelf();
                }
                @Override
                public void onError(String error) {
                    Toast.makeText(getApplicationContext(), "Error: " + error, Toast.LENGTH_SHORT).show();

                }
            }).start();

            return START_STICKY;
        }
    }
}