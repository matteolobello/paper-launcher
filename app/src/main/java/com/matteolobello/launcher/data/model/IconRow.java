package com.matteolobello.launcher.data.model;

import android.content.pm.ApplicationInfo;

import java.util.List;

public class IconRow {

    private final List<ApplicationInfo> mApplicationInfoList;

    public IconRow(List<ApplicationInfo> applicationInfoList) {
        mApplicationInfoList = applicationInfoList;
    }

    public List<ApplicationInfo> getApplicationInfoList() {
        return mApplicationInfoList;
    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        for (ApplicationInfo applicationInfo : mApplicationInfoList) {
            output.append("\n" + "Package name: ").append(
                    applicationInfo == null ? "null" : applicationInfo.packageName);
        }

        return output.toString();
    }
}
