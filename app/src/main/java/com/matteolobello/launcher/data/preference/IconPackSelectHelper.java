package com.matteolobello.launcher.data.preference;

import android.content.Context;
import android.content.SharedPreferences;

public class IconPackSelectHelper {

    private static final String PREF_FILE_NAME = "icon_pack.xml";

    private static IconPackSelectHelper sInstance;

    private IconPackSelectHelper() {
    }

    public static IconPackSelectHelper get() {
        if (sInstance == null) {
            sInstance = new IconPackSelectHelper();
        }

        return sInstance;
    }

    public void setIconPack(Context context, String packageName) {
        getSharedPreferences(context).edit().putString("packageName", packageName).apply();
    }

    public String getIconPack(Context context) {
        return getSharedPreferences(context).getString("packageName", null);
    }

    private SharedPreferences getSharedPreferences(Context context) {
        return context.getSharedPreferences(PREF_FILE_NAME, Context.MODE_PRIVATE);
    }
}
