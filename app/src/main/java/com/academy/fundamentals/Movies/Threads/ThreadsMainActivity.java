package com.academy.fundamentals.Movies.Threads;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.academy.fundamentals.Movies.Movies.CommonData.Constants;
import com.academy.fundamentals.Movies.Threads.ThreadHandler.SimulateAsyncTask;
import com.academy.fundamentals.Movies.Threads.ThreadHandler.ThreadsActivity;
import com.academy.fundamentals.R;

public class ThreadsMainActivity extends AppCompatActivity {

    private TextView m_statusTextView;
    Button m_createThreadButton;
    Button m_startThreadButton;

    public Button getCreateThreadButton() {
        return m_createThreadButton;
    }

    public Button getStartThreadButton() {
        return m_startThreadButton;
    }

    public Button getCancelThreadButton() {
        return m_cancelThreadButton;
    }

    Button m_cancelThreadButton;

    public TextView getStatusTextView()
    {
        return m_statusTextView;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_thread);

        m_statusTextView = findViewById(R.id.tv_counter_status);

        m_createThreadButton = findViewById(R.id.btn_create);
        m_startThreadButton = findViewById(R.id.btn_start);
        m_cancelThreadButton = findViewById(R.id.btn_cancel);

    }

}