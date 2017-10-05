package com.matteolobello.launcher.util;

import android.os.Build;

public class SDKUtil {

    public static final boolean AT_LEAST_N_MR1 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1;
    public static final boolean AT_LEAST_O = Build.VERSION.SDK_INT >= Build.VERSION_CODES.O;
}
