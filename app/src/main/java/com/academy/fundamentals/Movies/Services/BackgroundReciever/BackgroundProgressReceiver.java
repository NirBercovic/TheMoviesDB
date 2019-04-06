package com.academy.fundamentals.Movies.Services.BackgroundReciever;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.academy.fundamentals.Movies.Movies.CommonData.Constants;

import java.util.HashMap;
import java.util.Map;

public class BackgroundProgressReceiver extends BroadcastReceiver implements Constants {

    private static BackgroundProgressReceiver m_instance;
    private Map<BroadcastReceiverListener, IntentFilter> m_listeners;

    private BackgroundProgressReceiver()
    {
        Log.v("BroadcastReceiver","BackgroundProgressReceiver called");
        m_listeners = new HashMap<>();
    }

    public static BackgroundProgressReceiver getInstance()
    {
        Log.v("BroadcastReceiver","getInstance called");
        if (m_instance == null) {
            m_instance = new BackgroundProgressReceiver();
        }
        return m_instance;
    }


    @Override
    public void onReceive(Context context, Intent intent) {
        Log.v("BroadcastReceiver","onReceive called");

        int progress = intent.getIntExtra(PROGRESS_VALUE_KEY, -1);
        String info = String.valueOf(progress);

        String action = intent.getAction();

        for (Map.Entry<BroadcastReceiverListener,IntentFilter> entry : m_listeners.entrySet()) {

            //assuming filter holds only one action

            if (entry.getValue().getAction(0).equals(action)) {
                entry.getKey().onReceiveInfo(info);
            }
        }
    }

    public void registerListener(Context context, BroadcastReceiverListener listener, IntentFilter filter)
    {
        Log.v("BroadcastReceiver","registerListener called");
        m_listeners.put(listener, filter);
        context.registerReceiver(m_instance, filter);
    }

    public void unregisterListener(Context context, BroadcastReceiverListener listener)
    {
        Log.v("BroadcastReceiver","unregisterListener called");
        m_listeners.remove(listener);
        context.unregisterReceiver(m_instance);
    }

}
