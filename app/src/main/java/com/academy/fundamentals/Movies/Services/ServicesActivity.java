package com.academy.fundamentals.Movies.Services;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.academy.fundamentals.Movies.Movies.CommonData.Constants;
import com.academy.fundamentals.Movies.Services.BackgroundReciever.BackgroundProgressReceiver;
import com.academy.fundamentals.Movies.Services.BackgroundReciever.BroadcastReceiverListener;
import com.academy.fundamentals.R;

public class ServicesActivity extends AppCompatActivity implements View.OnClickListener, Constants, BroadcastReceiverListener {

    public TextView m_tv_progress;
    private Button m_btn_service, m_btn_intent_service;
    private BackgroundProgressReceiver m_backgroundProgressReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services);

        m_backgroundProgressReceiver = BackgroundProgressReceiver.getInstance();

        m_btn_service = findViewById(R.id.btn_service);
        m_btn_intent_service = findViewById(R.id.btn_intent_service);

        m_tv_progress = findViewById(R.id.tv_progress_percentage);
        m_btn_service.setOnClickListener(this);
        m_btn_intent_service.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int buttonId = v.getId();
        Intent service = null;

        switch (buttonId){
            case R.id.btn_service:
                service = new Intent(this,ServiceController.MyService.class);
                break;
            case R.id.btn_intent_service:
                service = new Intent(this,ServiceController.MyIntentService.class);
                break;
        }
        startService(service);
    }

    @Override
    public void onPause() {
        // Unregister to the Broadcast Receiver.
        m_backgroundProgressReceiver.unregisterListener(this, this);
        super.onPause();
    }

    @Override
    protected void onResume() {
        subscribeForProgressUpdates();
        super.onResume();
    }

    private void subscribeForProgressUpdates() {
        IntentFilter progressUpdateActionFilter = new IntentFilter(PROGRESS_UPDATE_ACTION);
        m_backgroundProgressReceiver.registerListener(this, this, progressUpdateActionFilter);
    }

    @Override
    public void onReceiveInfo(final String info) {
        runOnUiThread(new Runnable()
        {
            @Override
            public void run()
            {
                m_tv_progress.setText(info + "%");
            }
        });
    }
}
