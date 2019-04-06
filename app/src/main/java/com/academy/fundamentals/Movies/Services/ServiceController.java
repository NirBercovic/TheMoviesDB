package com.academy.fundamentals.Movies.Services;

import android.app.IntentService;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.SystemClock;
import android.os.Process;
import android.util.Log;

import com.academy.fundamentals.Movies.Movies.CommonData.Constants;

import static android.content.ContentValues.TAG;

public class ServiceController implements Constants {

    private static boolean isDestroyed;

    public static class MyService extends Service {

        private ServiceHandler m_serviceHandler;
        private Looper m_serviceLooper;

        public MyService() {}

        @Override
        public void onCreate() {
            Log.v("ServiceController","MyService onCreate called");
            // To avoid cpu-blocking, we create a background handler to run our service
            HandlerThread thread = new HandlerThread(TAG, Process.THREAD_PRIORITY_BACKGROUND);
            // start the new handler thread
            thread.start();
            m_serviceLooper = thread.getLooper();
            // start the service using the background handler
            m_serviceHandler = new ServiceHandler(m_serviceLooper);
        }

        @Override
        public int onStartCommand(Intent intent, int flags, int startId) {
            Log.v("ServiceController","MyService onStartCommand called");

            isDestroyed = false;

            // call a new service handler. The service ID can be used to identify the service
            Message message = m_serviceHandler.obtainMessage();
            message.arg1 = startId;
            m_serviceHandler.sendMessage(message);

            return START_STICKY;
        }

        @Override
        public IBinder onBind(Intent intent) {
                return null;
        }

        private final class ServiceHandler extends Handler {

            public ServiceHandler(Looper looper) {
                super(looper);
                Log.v("ServiceController","MyService ServiceHandler called");
            }

            @Override public void handleMessage(Message msg) {
                Log.v("ServiceController","MyService ServiceHandler handleMessage called");

                // Well calling m_serviceHandler.sendMessage(message);  from onStartCommand this method will be called.
                // Add your cpu-blocking activity here
                for (int i = 0; i <= 100 && !isDestroyed; i++) {
                    SystemClock.sleep(500);
                    Intent intent = new Intent(PROGRESS_UPDATE_ACTION);
                    intent.putExtra(PROGRESS_VALUE_KEY, i);
                    sendBroadcast(intent);
                }
                sendBroadcast(new Intent(Constants.ACTION_COUNT_FINISHED));
                // the msg.arg1 is the startId used in the onStartCommand,so we can track the running service here.
                stopSelf(msg.arg1);
            }
        }
    }

    public static class MyIntentService extends IntentService {

        public MyIntentService() {
            super("MyIntentService");
        }

        @Override
        protected void onHandleIntent(Intent intent) {
            isDestroyed = false;
            for (int i = 0; i <= 100 && !isDestroyed; i++) {
                SystemClock.sleep(100);
                Intent broadcastIntent = new Intent(PROGRESS_UPDATE_ACTION);
                broadcastIntent.putExtra(PROGRESS_VALUE_KEY, i);
                sendBroadcast(broadcastIntent);
            }
        }

    }
}


