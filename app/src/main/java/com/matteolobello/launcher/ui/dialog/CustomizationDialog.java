package com.matteolobello.launcher.ui.dialog;

import android.app.Dialog;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.view.View;

import com.matteolobello.launcher.R;
import com.matteolobello.launcher.ui.activity.IconPackChooserActivity;
import com.matteolobello.launcher.ui.activity.LauncherActivity;
import com.matteolobello.launcher.util.LauncherUtil;

import java.util.ArrayList;
import java.util.List;

public class CustomizationDialog extends Dialog implements View.OnClickListener {

    private LauncherActivity mLauncherActivity;

    private CustomizationDialog(@NonNull LauncherActivity launcherActivity) {
        super(launcherActivity);

        init(launcherActivity);
    }

    public static void show(LauncherActivity launcherActivity) {
        new CustomizationDialog(launcherActivity).show();
    }

    private void init(LauncherActivity launcherActivity) {
        mLauncherActivity = launcherActivity;

        setContentView(R.layout.dialog_customization);

        View defaultLauncher = findViewById(R.id.customization_default_launcher);
        View changeWallpaper = findViewById(R.id.customization_wallpaper);
        View editDockIcons = findViewById(R.id.customization_edit_dock_icons);
        View iconPack = findViewById(R.id.customization_icon_pack);

        defaultLauncher.setOnClickListener(this);
        changeWallpaper.setOnClickListener(this);
        editDockIcons.setOnClickListener(this);
        iconPack.setOnClickListener(this);

        if (isPaperLauncherDefault()) {
            defaultLauncher.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.customization_default_launcher:
                dismiss();

                mLauncherActivity.startActivity(new Intent("android.settings.HOME_SETTINGS"));
                break;
            case R.id.customization_wallpaper:
                dismiss();

                Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
                mLauncherActivity.startActivityForResult(intent, LauncherActivity.WALLPAPER_SELECT_INTENT_CODE);
                break;
            case R.id.customization_edit_dock_icons:
                dismiss();

                mLauncherActivity.startDockIconsEditing();
                break;
            case R.id.customization_icon_pack:
                dismiss();

                mLauncherActivity.finish();
                mLauncherActivity.startActivity(new Intent(mLauncherActivity, IconPackChooserActivity.class));
                break;
        }
    }

    private boolean isPaperLauncherDefault() {
        final IntentFilter filter = new IntentFilter(Intent.ACTION_MAIN);
        filter.addCategory(Intent.CATEGORY_HOME);

        List<IntentFilter> filters = new ArrayList<>();
        filters.add(filter);

        String paperLauncherPackageName = mLauncherActivity.getPackageName();
        List<ComponentName> activities = new ArrayList<>();

        PackageManager packageManager = mLauncherActivity.getPackageManager();
        packageManager.getPreferredActivities(filters, activities, null);

        for (ComponentName activity : activities) {
            if (paperLauncherPackageName.equals(activity.getPackageName())) {
                return true;
            }
        }

        return false;
    }
}
