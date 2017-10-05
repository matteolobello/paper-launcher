package com.matteolobello.launcher.util;

import android.content.Context;
import android.content.Intent;

public class LauncherUtil {

    public static void askForDefaultLauncher(Context context, String message) {
        Intent launcherSelectorIntent = new Intent();
        launcherSelectorIntent.setAction(Intent.ACTION_MAIN);
        launcherSelectorIntent.addCategory(Intent.CATEGORY_HOME);
        launcherSelectorIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        context.startActivity(Intent.createChooser(launcherSelectorIntent, message));
    }
}
