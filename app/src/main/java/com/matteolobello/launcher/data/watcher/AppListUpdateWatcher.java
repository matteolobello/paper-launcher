package com.matteolobello.launcher.data.watcher;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.matteolobello.launcher.data.watcher.base.BaseEventWatcher;

public class AppListUpdateWatcher implements BaseEventWatcher {

    private Context mContext;
    private IntentFilter mFilter;
    private OnMustUpdateAppListListener mListener;
    private MustUpdateAppListReceiver mReceiver;

    public AppListUpdateWatcher(Context context) {
        mContext = context;

        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        mFilter.addAction(Intent.ACTION_PACKAGE_REMOVED);
        mFilter.addAction(Intent.ACTION_PACKAGE_CHANGED);
        mFilter.addDataScheme("package");
    }

    public void setOnMustUpdateAppListListener(OnMustUpdateAppListListener listener) {
        mListener = listener;
        mReceiver = new MustUpdateAppListReceiver();
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

    public interface OnMustUpdateAppListListener {
        void onMustUpdateAppList();
    }

    private class MustUpdateAppListReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            mListener.onMustUpdateAppList();
        }
    }
}
