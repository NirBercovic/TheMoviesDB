package com.academy.fundamentals.Movies.Threads;

public interface AsyncTaskEvents {

    void onPreExecute();
    void onPostExecute(String result);
    void onProgressUpdate(Integer... numbers);
    void onCancelled();

}
