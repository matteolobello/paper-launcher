package com.matteolobello.launcher.data.loader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.LauncherApps;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.os.UserHandle;

import com.matteolobello.launcher.util.SDKUtil;

import java.util.List;

public class ShortcutsLoader {

    private static final int QUERY_FLAGS = 11;

    @SuppressLint("WrongConstant")
    public static List<ShortcutInfo> loadShortcuts(Context context, String packageName) {
        if (!SDKUtil.AT_LEAST_N_MR1) {
            return null;
        }

        LauncherApps launcherApps = (LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE);
        if (launcherApps == null) {
            return null;
        }

        if (!launcherApps.hasShortcutHostPermission()) {
            return null;
        }

        PackageManager packageManager = context.getPackageManager();
        Intent mainIntent = new Intent("android.intent.action.MAIN", null);
        mainIntent.addCategory("android.intent.category.LAUNCHER");

        if (packageManager != null && packageManager.queryIntentActivities(mainIntent, 0) != null) {
            try {
                return launcherApps.getShortcuts((new LauncherApps.ShortcutQuery())
                                .setPackage(packageName).setQueryFlags(QUERY_FLAGS),
                        UserHandle.getUserHandleForUid(context.getPackageManager().getPackageUid(packageName, PackageManager.GET_META_DATA)));
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();

                return null;
            }
        }

        return null;
    }
}
