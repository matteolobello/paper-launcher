package com.matteolobello.launcher.util;

import android.app.Activity;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class SystemBarUtil {

    public static void setFullyTransparentStatusBar(Activity activity) {
        if (SDKUtil.AT_LEAST_KITKAT) {
            setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, true);

            activity.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

            if (SDKUtil.AT_LEAST_LOLLIPOP) {
                setWindowFlag(activity, WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS, false);
                activity.getWindow().setStatusBarColor(Color.TRANSPARENT);
            }
        }
    }

    public static void enableLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View view = activity.getWindow().getDecorView();

            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

    public static void clearLightStatusBar(Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View view = activity.getWindow().getDecorView();

            int flags = view.getSystemUiVisibility();
            flags &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
        }
    }

    public static void enableDarkNavigationBarIcons(Activity activity) {
        if (SDKUtil.AT_LEAST_O) {
            activity.getWindow().getDecorView().setSystemUiVisibility(
                    activity.getWindow().getDecorView().getSystemUiVisibility()
                            | View.SYSTEM_UI_FLAG_LIGHT_NAVIGATION_BAR);
        }
    }

    private static void setWindowFlag(Activity activity, final int bits, boolean on) {
        Window win = activity.getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        if (on) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
        }
        win.setAttributes(winParams);
    }

    public static void setStatusBarColor(Activity activity, int color) {
        if (SDKUtil.AT_LEAST_LOLLIPOP) {
            activity.getWindow().setStatusBarColor(color);
        }
    }

    public static void setNavigationBarColor(Activity activity, int color) {
        if (SDKUtil.AT_LEAST_LOLLIPOP) {
            activity.getWindow().setNavigationBarColor(color);
        }
    }
}
