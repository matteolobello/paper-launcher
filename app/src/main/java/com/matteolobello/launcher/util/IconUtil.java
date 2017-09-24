package com.matteolobello.launcher.util;

import android.content.pm.ApplicationInfo;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

public class IconUtil {

    public static Drawable setIconOnImageView(ImageView imageView, ApplicationInfo applicationInfo) {
        Drawable drawable = applicationInfo.loadIcon(imageView.getContext().getPackageManager());
        imageView.setImageDrawable(drawable);

        return drawable;
    }
}
