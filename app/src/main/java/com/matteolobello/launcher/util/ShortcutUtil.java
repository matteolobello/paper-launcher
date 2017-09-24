package com.matteolobello.launcher.util;

import android.content.Context;
import android.content.pm.LauncherApps;
import android.content.pm.ShortcutInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;

public class ShortcutUtil {

    public static Drawable loadDrawable(Context context, ShortcutInfo shortcutInfo) {
        if (!SDKUtil.AT_LEAST_N_MR1) {
            return null;
        }

        return ((LauncherApps) context.getSystemService(Context.LAUNCHER_APPS_SERVICE))
                .getShortcutIconDrawable(shortcutInfo, context.getResources().getDisplayMetrics().densityDpi);
    }

    public static void launchShortcut(View view, ShortcutInfo shortcutInfo) {
        if (!SDKUtil.AT_LEAST_N_MR1) {
            return;
        }

        ((LauncherApps) view.getContext().getSystemService(Context.LAUNCHER_APPS_SERVICE))
                .startShortcut(shortcutInfo, IntentUtil.getViewBounds(view), new Bundle());
    }
}
