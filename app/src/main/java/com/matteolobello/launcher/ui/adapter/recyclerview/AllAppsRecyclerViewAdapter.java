package com.matteolobello.launcher.ui.adapter.recyclerview;

import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.matteolobello.launcher.R;
import com.matteolobello.launcher.data.loader.ShortcutsLoader;
import com.matteolobello.launcher.ui.activity.LauncherActivity;
import com.matteolobello.launcher.util.IconUtil;
import com.matteolobello.launcher.util.IntentUtil;
import com.matteolobello.launcher.util.SDKUtil;

import java.util.HashMap;
import java.util.List;

public class AllAppsRecyclerViewAdapter extends RecyclerView.Adapter<AllAppsRecyclerViewAdapter.ViewHolder> {

    private final HashMap<String, Bitmap> iconHashMap = new HashMap<>();

    private final LauncherActivity mLauncherActivity;
    private final List<ApplicationInfo> mApplicationInfoList;

    public AllAppsRecyclerViewAdapter(LauncherActivity launcherActivity, List<ApplicationInfo> applicationInfoList) {
        mLauncherActivity = launcherActivity;
        mApplicationInfoList = applicationInfoList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final ApplicationInfo applicationInfo = mApplicationInfoList.get(position);

        if (applicationInfo == null) {
            mApplicationInfoList.remove(position);
            notifyDataSetChanged();
            return;
        }

        holder.itemView.setOnClickListener(view -> {
            mLauncherActivity.notifyAppLaunch(applicationInfo);

            IntentUtil.launchApp(view, applicationInfo.packageName);
        });

        holder.itemView.setOnLongClickListener(view -> {
            if (!SDKUtil.AT_LEAST_N_MR1) {
                return false;
            }

            try {
                mLauncherActivity.showShortcutsBottomSheet(applicationInfo.packageName,
                        ShortcutsLoader.loadShortcuts(mLauncherActivity, applicationInfo.packageName));
            } catch (Exception e) {
                Toast.makeText(mLauncherActivity, mLauncherActivity.getString(R.string.app_name)
                        + " is not the default Launcher", Toast.LENGTH_SHORT).show();

                e.printStackTrace();
            }

            return !mLauncherActivity.isExpandingAppDrawer();
        });

        holder.appNameTextView.setText(applicationInfo.loadLabel(holder.itemView.getContext().getPackageManager()));

        if (iconHashMap.containsKey(applicationInfo.packageName)) {
            holder.appIconImageView.setImageBitmap(iconHashMap.get(applicationInfo.packageName));
        } else {
            String packageName = applicationInfo.packageName;
            Bitmap icon = IconUtil.setIconOnImageView(mLauncherActivity, holder.appIconImageView, applicationInfo);

            iconHashMap.put(packageName, icon);
        }
    }

    @Override
    public int getItemCount() {
        return mApplicationInfoList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView appIconImageView;
        TextView appNameTextView;

        ViewHolder(View itemView) {
            super(itemView);

            appIconImageView = itemView.findViewById(R.id.app_icon);
            appNameTextView = itemView.findViewById(R.id.app_name);
        }
    }
}