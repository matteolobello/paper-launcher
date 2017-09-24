package com.matteolobello.launcher.data.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.matteolobello.launcher.data.model.DockIcon;
import com.matteolobello.launcher.util.DictionaryUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DockAppsHelper {

    private static final String PREF_FILE_NAME = "dock_apps.xml";

    private static DockAppsHelper sInstance;

    private SharedPreferences mSharedPreferences;

    private DockAppsHelper() {
    }

    public static DockAppsHelper get() {
        if (sInstance == null) {
            sInstance = new DockAppsHelper();
        }

        return sInstance;
    }

    public ArrayList<DockIcon> getOrderedDockIcons(Context context) {
        ArrayList<DockIcon> returnValue = new ArrayList<>();

        List<Map.Entry<String, Integer>> entries = getAllEntries(context);
        for (Map.Entry<String, Integer> entry : entries) {
            returnValue.add(new DockIcon(entry.getKey(), entry.getValue()));
        }

        return returnValue;
    }

    public void saveItem(Context context, String packageName, int column) {
        SharedPreferences sharedPreferences = getSharedPreferences(context);
        List<Map.Entry<String, Integer>> entries = getAllEntries(context);

        for (Map.Entry<String, Integer> entry : entries) {
            String key = entry.getKey();
            int value = entry.getValue();
            if (value == column) {
                sharedPreferences.edit().remove(key).apply();
            }
        }

        getSharedPreferences(context).edit().putInt(packageName, column).apply();
    }

    private SharedPreferences getSharedPreferences(Context context) {
        if (mSharedPreferences == null) {
            mSharedPreferences = context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
        }

        return mSharedPreferences;
    }

    @SuppressWarnings("unchecked")
    private List<Map.Entry<String, Integer>> getAllEntries(Context context) {
        final SharedPreferences preferences = getSharedPreferences(context);
        final Map<String, ?> allPreferences = preferences.getAll();

        return DictionaryUtil.sortHashMapByValues((HashMap<String, Integer>) allPreferences);
    }
}


