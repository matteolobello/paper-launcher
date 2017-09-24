package com.matteolobello.launcher.util;

import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.support.annotation.FloatRange;
import android.widget.ImageView;

public class SaturationUtil {

    public static void setSaturation(ImageView imageView, @FloatRange(from = 0, to = 1) float saturationLevel) {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(saturationLevel);
        ColorMatrixColorFilter filter = new ColorMatrixColorFilter(matrix);
        imageView.setColorFilter(filter);
    }
}
