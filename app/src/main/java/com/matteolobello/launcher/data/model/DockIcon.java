package com.matteolobello.launcher.data.model;

public class DockIcon {

    private final String mPackageName;
    private final int mColumn;

    public DockIcon(String packageName, int colum) {
        mPackageName = packageName;
        mColumn = colum;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public int getColumn() {
        return mColumn;
    }
}
