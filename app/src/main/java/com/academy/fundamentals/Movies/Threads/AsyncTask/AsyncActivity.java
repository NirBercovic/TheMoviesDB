package com.academy.fundamentals.Movies.Threads.AsyncTask;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.academy.fundamentals.Movies.Threads.AsyncTaskEvents;
import com.academy.fundamentals.Movies.Threads.ThreadsMainActivity;
import com.academy.fundamentals.R;

import static com.academy.fundamentals.Movies.Movies.CommonData.Constants.CANCEL_RUN;

public class AsyncActivity extends ThreadsMainActivity implements AsyncTaskEvents, View.OnClickListener {

    CounterAsyncTask m_asyncTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getCreateThreadButton().setOnClickListener(this);
        getStartThreadButton().setOnClickListener(this);
        getCancelThreadButton().setOnClickListener(this);
    }

    @Override
    public void onClick(View target) {
        switch(target.getId())
        {
            case R.id.btn_create:
                if (m_asyncTask == null)
                {
                    Toast.makeText(target.getContext(), "Task created", Toast.LENGTH_SHORT).show();
                    m_asyncTask = new CounterAsyncTask(AsyncActivity.this);
                }
                else
                {
                    Toast.makeText(target.getContext(), "Can't create another task - run it first", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_start:
                if (m_asyncTask == null)
                {
                    Toast.makeText(target.getContext(), "No task to start - create one", Toast.LENGTH_SHORT).show();
                }
                else if (m_asyncTask.getStatus() == AsyncTask.Status.RUNNING)
                {
                    Toast.makeText(target.getContext(), "Task already ran - wait to finish", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    m_asyncTask.execute(1,2,3,4,5,6,7,8,9,10);
                }
                break;
            case R.id.btn_cancel:
                if (m_asyncTask == null)
                {
                    Toast.makeText(target.getContext(), "No task to cancel - create one and run it", Toast.LENGTH_SHORT).show();
                }
                else if (m_asyncTask.getStatus() != AsyncTask.Status.RUNNING)
                {
                    Toast.makeText(target.getContext(), "Task not running - first run it", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    m_asyncTask.cancel(false);
                }
                break;
            default:
                throw new RuntimeException("Unknown button ID");
        }
    }

    @Override
    public void onCancelled() {
        getStatusTextView().setText(CANCEL_RUN);
        m_asyncTask = null;
    }

    @Override
    public void onPreExecute() {

    }

    @Override
    public void onPostExecute(String result) {
        getStatusTextView().setText(result);
        m_asyncTask = null;
    }

    @Override
    public void onProgressUpdate(Integer... numbers) {
        getStatusTextView().setText(String.valueOf(numbers[0]));
    }





}
