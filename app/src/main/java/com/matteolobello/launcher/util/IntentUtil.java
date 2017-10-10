package com.matteolobello.launcher.util;

import android.app.ActivityOptions;
import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.view.View;

public class IntentUtil {

    public static void launchApp(View view, String packageName) {
        Intent intent = view.getContext().getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent == null) {
            return;
        }

        launchApp(view, intent);
    }

    public static void launchApp(View view, Intent intent) {
        if (intent == null) {
            return;
        }

        intent.setSourceBounds(IntentUtil.getViewBounds(view));
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        view.getContext().startActivity(intent, IntentUtil.getActivityLaunchOptions(view));
    }

    public static Rect getViewBounds(View v) {
        int[] pos = new int[2];
        v.getLocationOnScreen(pos);
        return new Rect(pos[0], pos[1], pos[0] + v.getWidth(), pos[1] + v.getHeight());
    }

    public static void startGoogleSearchActivity(View view) {
        final Context context = view.getContext();

        final SearchManager searchManager =
                (SearchManager) context.getSystemService(Context.SEARCH_SERVICE);
        if (searchManager == null) {
            return;
        }

        ComponentName globalSearchActivity = searchManager.getGlobalSearchActivity();
        if (globalSearchActivity == null) {
            return;
        }

        Intent intent = new Intent(SearchManager.INTENT_ACTION_GLOBAL_SEARCH);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setComponent(globalSearchActivity);

        Bundle appSearchData = new Bundle();
        appSearchData.putString("source", context.getPackageName());

        intent.putExtra(SearchManager.APP_DATA, appSearchData);
        intent.setSourceBounds(getViewBounds(view));
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException ex) {
            ex.printStackTrace();
        }
    }

    public static void startGoogleVoiceRecognitionActivity(View view) {
        Intent intent = new Intent("android.speech.action.VOICE_SEARCH_HANDS_FREE");
        intent.setSourceBounds(getViewBounds(view));
        view.getContext().startActivity(intent);
    }

    private static Bundle getActivityLaunchOptions(View v) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int left = 0, top = 0;
            int width = v.getMeasuredWidth(), height = v.getMeasuredHeight();
            return ActivityOptions.makeClipRevealAnimation(v, left, top, width, height).toBundle();
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            return ActivityOptions.makeCustomAnimation(
                    v.getContext(), android.R.anim.fade_in, android.R.anim.fade_out).toBundle();
        }
        return null;
    }
}
