package com.matteolobello.launcher.data.watcher;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class AppListUpdateWatcher {

    private Context mContext;
    private IntentFilter mFilter;
    private OnMustUpdateAppListListener mListener;
    private MustUpdateAppListReceiver mReceiver;

    public AppListUpdateWatcher(Context context) {
        mContext = context;

        mFilter = new IntentFilter();
        mFilter.addAction(Intent.ACTION_PACKAGE_ADDED);
        mFilter.addAction(Intent.ACTION_PACKAGE_INSTALL);
        mFilter.addDataScheme("package");
    }

    public void setOnMustUpdateAppListListener(OnMustUpdateAppListListener listener) {
        mListener = listener;
        mReceiver = new MustUpdateAppListReceiver();
    }

    public void startWatch() {
        if (mReceiver != null) {
            mContext.registerReceiver(mReceiver, mFilter);
        }
    }

    public void stopWatch() {
        if (mReceiver != null) {
            mContext.unregisterReceiver(mReceiver);
        }
    }

    public interface OnMustUpdateAppListListener {
        void onMustUpdateAppList();
    }

    class MustUpdateAppListReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            mListener.onMustUpdateAppList();
        }
    }
}
