package com.matteolobello.launcher.ui.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;

import com.matteolobello.launcher.R;
import com.matteolobello.launcher.ui.activity.LauncherActivity;

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

        View changeWallpaper = findViewById(R.id.customization_wallpaper);
        View editDockIcons = findViewById(R.id.customization_edit_dock_icons);

        changeWallpaper.setOnClickListener(this);
        editDockIcons.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.customization_wallpaper:
                dismiss();

                Intent intent = new Intent(Intent.ACTION_SET_WALLPAPER);
                mLauncherActivity.startActivityForResult(intent, LauncherActivity.WALLPAPER_SELECT_INTENT_CODE);
                break;
            case R.id.customization_edit_dock_icons:
                dismiss();

                mLauncherActivity.startDockIconsEditing();
                break;
        }
    }
}
