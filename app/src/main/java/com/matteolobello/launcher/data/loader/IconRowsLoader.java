package com.matteolobello.launcher.data.loader;

import android.content.pm.ApplicationInfo;

import com.matteolobello.launcher.data.model.IconRow;
import com.matteolobello.launcher.ui.activity.LauncherActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class IconRowsLoader {

    public static List<IconRow> loadIconRows(List<ApplicationInfo> applicationInfoList) {
        final int numberOfRows = applicationInfoList.size() / LauncherActivity.APP_DRAWER_COLUMNS + 1;

        List<IconRow> iconRows = new ArrayList<>();

        int rowsDone = 0;
        for (int i = 0; true; i++) {
            if (rowsDone == numberOfRows) {
                break;
            }

            if (i % LauncherActivity.APP_DRAWER_COLUMNS == 0) {
                rowsDone++;

                ArrayList<ApplicationInfo> filteredApplicationInfoList = new ArrayList<>();

                ApplicationInfo[] applicationInfoArray = Arrays.copyOfRange(applicationInfoList.toArray(), i,
                        i + LauncherActivity.APP_DRAWER_COLUMNS, ApplicationInfo[].class);
                for (ApplicationInfo applicationInfo : applicationInfoArray) {
                    if (applicationInfo != null) {
                        filteredApplicationInfoList.add(applicationInfo);
                    }
                }

                IconRow iconRow = new IconRow(filteredApplicationInfoList);
                iconRows.add(iconRow);
            }
        }

        return iconRows;
    }
}
