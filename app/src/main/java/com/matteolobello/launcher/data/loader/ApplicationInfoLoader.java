package com.matteolobello.launcher.data.loader;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ApplicationInfoLoader {

    public static List<ApplicationInfo> loadAppList(Context context) {
        List<ApplicationInfo> applicationInfoList = context.getPackageManager().getInstalledApplications(PackageManager.GET_META_DATA);
        Collections.sort(applicationInfoList, new ApplicationInfo.DisplayNameComparator(context.getPackageManager()));

        final ArrayList<ApplicationInfo> filteredApplicationInfoList = new ArrayList<>();
        for (ApplicationInfo applicationInfo : applicationInfoList) {
            if (context.getPackageManager().getLaunchIntentForPackage(applicationInfo.packageName) == null
                    || applicationInfo.packageName.equals(context.getPackageName())) {
                continue;
            }

            filteredApplicationInfoList.add(applicationInfo);
        }

        return filteredApplicationInfoList;
    }
}
