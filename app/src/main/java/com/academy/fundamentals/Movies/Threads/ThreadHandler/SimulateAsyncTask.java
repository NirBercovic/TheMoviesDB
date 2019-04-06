package com.academy.fundamentals.Movies.Threads.ThreadHandler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Handler;
import com.academy.fundamentals.Movies.Movies.CommonData.Constants;
import com.academy.fundamentals.Movies.Threads.AsyncTaskEvents;

public class SimulateAsyncTask implements Constants {

    private String m_status;
    private Handler m_mainUIHandler, m_workerHandler;
    private Integer[] m_numbers;
    private AsyncTaskEvents asyncTaskEvents;

    public String getStatus()
    {
        return m_status;
    }

    public SimulateAsyncTask(AsyncTaskEvents activityAsyncTaskEvents){
        this.asyncTaskEvents = activityAsyncTaskEvents;
        m_mainUIHandler = new Handler(Looper.getMainLooper());
        HandlerThread handlerThread = new HandlerThread("worker");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        m_workerHandler = new Handler(looper);
        m_status = READY;
    }

    void execute(Integer ... numbers){
        m_status = RUNNING;
        m_numbers = numbers;
        onPreExecute.run();
        m_workerHandler.post(doInBackground);
    }

    Runnable onPreExecute = new Runnable() {
        @Override
        public void run() {
            asyncTaskEvents.onPreExecute();
        }
    };

    Runnable doInBackground = new Runnable() {
        @Override
        public void run() {
            for (Integer number : m_numbers) {
                if (!isCancelled()) {
                    publishProgress(number);
                    try {
                        Thread.sleep(SLEEP_INTERVAL);
                    } catch(InterruptedException e) {
                        System.out.println(INTERRUPT_RUN);
                    }
                } else {
                    break;
                }
            }
            if (!isCancelled()) {
                m_mainUIHandler.post(onPostExecute);
            }
        }
    };

    void publishProgress(int number)
    {
        OnProgressUpdate onProgressUpdate = new OnProgressUpdate(number);
        m_mainUIHandler.post(onProgressUpdate);
    }

    public class OnProgressUpdate implements Runnable {

        private int m_number;
        public OnProgressUpdate(int number)
        {
            this.m_number = number;
        }

        @Override
        public void run() {
            asyncTaskEvents.onProgressUpdate(m_number);
        }
    }

    Runnable onPostExecute = new Runnable() {
        @Override
        public void run() {
            m_status = FINISH;
            asyncTaskEvents.onPostExecute(FINISH_RUN);
        }
    };

    Runnable onCancelled = new Runnable() {
        @Override
        public void run() {
            m_status = CANCEL;
            asyncTaskEvents.onCancelled();
        }
    };

    boolean isCancelled()
    {
        if (m_status.equals(CANCEL))
            return true;
        return false;
    }

    void cancel(boolean mayInterruptIfRunning){
        if (!mayInterruptIfRunning && m_status.equals(RUNNING)) {
            m_mainUIHandler.post(onCancelled);
        }
    }

}
