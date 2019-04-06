package com.academy.fundamentals.Movies.Threads.AsyncTask;

import android.os.AsyncTask;

import com.academy.fundamentals.Movies.Movies.CommonData.Constants;
import com.academy.fundamentals.Movies.Threads.AsyncTaskEvents;

public class CounterAsyncTask extends AsyncTask<Integer, Integer, String> implements Constants {

    private AsyncTaskEvents asyncTaskEvents;

    public CounterAsyncTask(AsyncTaskEvents activityAsyncTaskEvents){
        this.asyncTaskEvents = activityAsyncTaskEvents;
    }

    @Override
    protected String doInBackground(Integer... numbers) {
        for (Integer number : numbers) {
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
        return FINISH_RUN;
    }

    @Override
    protected void onPreExecute() {
        asyncTaskEvents.onPreExecute();
    }

    @Override
    protected void onPostExecute(String result) {
        asyncTaskEvents.onPostExecute(result);
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        asyncTaskEvents.onProgressUpdate(values);
    }

    @Override
    protected void onCancelled() {
        asyncTaskEvents.onCancelled();
    }

}
