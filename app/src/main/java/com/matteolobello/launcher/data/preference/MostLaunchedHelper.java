package com.matteolobello.launcher.data.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.matteolobello.launcher.util.DictionaryUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MostLaunchedHelper {

    private static final String PREF_FILE_NAME = "most_launched_apps.xml";

    private static MostLaunchedHelper sInstance;

    private MostLaunchedHelper() {
    }

    public static MostLaunchedHelper get() {
        if (sInstance == null) {
            sInstance = new MostLaunchedHelper();
        }

        return sInstance;
    }

    public void setupIfNeeded(Context context, List<ApplicationInfo> applicationInfoList) {
        SharedPreferences preferences = getSharedPreferences(context);

        Map<String, ?> allPreferences = preferences.getAll();

        for (ApplicationInfo applicationInfo : applicationInfoList) {
            if (!allPreferences.containsKey(applicationInfo.packageName)) {
                preferences.edit().putInt(applicationInfo.packageName, 0).apply();
            }
        }
    }

    public ArrayList<String> getMostLaunchedApps(Context context) {
        ArrayList<String> returnValue = new ArrayList<>();

        List<Map.Entry<String, Integer>> entries = getAllEntries(context);
        for (Map.Entry<String, Integer> entry : entries) {
            String packageName = entry.getKey();
            try {
                context.getPackageManager().getPackageInfo(packageName, 0);
            } catch (PackageManager.NameNotFoundException e) {
                getSharedPreferences(context).edit().remove(packageName).apply();

                continue;
            }

            returnValue.add(packageName);
        }

        return returnValue;
    }

    public void incrementCounterForPackageName(Context context, String packageName) {
        SharedPreferences preferences = getSharedPreferences(context);

        int previousValue = preferences.getInt(packageName, -1);
        int newValue = previousValue + 1;

        preferences.edit().putInt(packageName, newValue).apply();
    }

    @SuppressWarnings("unchecked")
    private List<Map.Entry<String, Integer>> getAllEntries(Context context) {
        final SharedPreferences preferences = getSharedPreferences(context);
        final Map<String, ?> allPreferences = preferences.getAll();

        return DictionaryUtil.sortHashMapByValues((HashMap<String, Integer>) allPreferences);
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }
}


