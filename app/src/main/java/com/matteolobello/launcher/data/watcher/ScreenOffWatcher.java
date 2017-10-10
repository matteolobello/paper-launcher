package com.matteolobello.launcher.data.watcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.matteolobello.launcher.data.watcher.base.BaseEventWatcher;

public class ScreenOffWatcher implements BaseEventWatcher {

    private Context mContext;
    private IntentFilter mFilter;
    private OnScreenOffListener mListener;
    private ScreenOffReceiver mReceiver;

    public ScreenOffWatcher(Context context) {
        mContext = context;

        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_SCREEN_OFF);
    }

    public void setOnScreenOffListener(OnScreenOffListener listener) {
        mListener = listener;
        mReceiver = new ScreenOffReceiver();
    }

    @Override
    public void startWatching() {
        if (mReceiver != null) {
            mContext.registerReceiver(mReceiver, mFilter);
        }
    }

    @Override
    public void stopWatching() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }

    public interface OnScreenOffListener {
        void onScreenOff();
    }

    private class ScreenOffReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mListener.onScreenOff();
        }
    }
}
