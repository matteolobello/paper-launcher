package com.matteolobello.launcher.data.watcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.matteolobello.launcher.data.watcher.base.BaseEventWatcher;

public class HomeButtonPressWatcher implements BaseEventWatcher {

    private Context mContext;
    private IntentFilter mFilter;
    private OnHomePressedListener mListener;
    private HomeButtonPressReceiver mReceiver;

    public HomeButtonPressWatcher(Context context) {
        mContext = context;
        mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
    }

    public void setOnHomePressedListener(OnHomePressedListener listener) {
        mListener = listener;
        mReceiver = new HomeButtonPressReceiver();
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

    public interface OnHomePressedListener {
        void onHomePressed();

        void onHomeLongPressed();
    }

    private class HomeButtonPressReceiver extends BroadcastReceiver {

        private static final String SYSTEM_DIALOG_REASON_KEY = "reason";
        private static final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
        private static final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
        private static final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action == null) {
                return;
            }

            if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
                String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
                if (reason != null) {
                    if (mListener != null) {
                        if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
                            mListener.onHomePressed();
                        } else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
                            mListener.onHomeLongPressed();
                        }
                    }
                }
            }
        }
    }
}
