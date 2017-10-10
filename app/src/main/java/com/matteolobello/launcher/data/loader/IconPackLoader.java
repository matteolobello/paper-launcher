package com.matteolobello.launcher.data.loader;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;

import com.matteolobello.launcher.data.model.IconPack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IconPackLoader {

    private static HashMap<String, IconPack> sIconPacks = null;

    public static HashMap<String, IconPack> getAvailableIconPacks(Context context, boolean forceReload) {
        if (sIconPacks == null || forceReload) {
            sIconPacks = new HashMap<>();

            PackageManager packageManager = context.getPackageManager();

            List<ResolveInfo> adwLauncherThemes = packageManager.queryIntentActivities(new Intent("org.adw.launcher.THEMES"), PackageManager.GET_META_DATA);
            List<ResolveInfo> goLauncherThemes = packageManager.queryIntentActivities(new Intent("com.gau.go.launcherex.theme"), PackageManager.GET_META_DATA);

            List<ResolveInfo> resolveInfoArrayList = new ArrayList<>(adwLauncherThemes);
            resolveInfoArrayList.addAll(goLauncherThemes);

            for (ResolveInfo resolveInfo : resolveInfoArrayList) {
                String packageName = resolveInfo.activityInfo.packageName;

                IconPack iconPack = new IconPack(packageName);
                sIconPacks.put(packageName, iconPack);
            }
        }
        return sIconPacks;
    }
}