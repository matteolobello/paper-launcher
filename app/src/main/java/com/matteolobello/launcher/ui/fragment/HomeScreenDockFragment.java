package com.matteolobello.launcher.ui.fragment;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;

import com.matteolobello.launcher.data.loader.ShortcutsLoader;
import com.matteolobello.launcher.data.model.DockIcon;
import com.matteolobello.launcher.data.preference.DockAppsHelper;
import com.matteolobello.launcher.ui.activity.LauncherActivity;
import com.matteolobello.launcher.ui.dialog.SelectDockIconDialog;
import com.matteolobello.launcher.ui.fragment.base.DockFragment;
import com.matteolobello.launcher.util.IconUtil;
import com.matteolobello.launcher.util.IntentUtil;

import java.util.ArrayList;

public class HomeScreenDockFragment extends DockFragment {

    private final ApplicationInfo[] DOCK_ITEMS_APPLICATION_INFO_ARRAY = new ApplicationInfo[LauncherActivity.APP_DRAWER_COLUMNS];

    private Animation mDockIconsEditAnimation;

    @Override
    public void init(View rootView) {
        super.init(rootView);

        setupDockIcons();
    }

    public void startIconsEditing() {
        mDockIconsEditAnimation = new AlphaAnimation(0.4f, 1.0f);
        mDockIconsEditAnimation.setDuration(260);
        mDockIconsEditAnimation.setRepeatMode(Animation.REVERSE);
        mDockIconsEditAnimation.setRepeatCount(Animation.INFINITE);

        iterateOverDockIcons((dockItem, dockIconColumn) -> {
            dockItem.setOnClickListener(view -> showSelectAppForIconAtColumn(dockIconColumn));

            dockItem.startAnimation(mDockIconsEditAnimation);
        });
    }

    public void stopEditAnimation() {
        if (mDockIconsEditAnimation != null) {
            mDockIconsEditAnimation.cancel();
        }

        iterateOverDockIcons((dockItem, dockIconColumn) -> {
            ApplicationInfo applicationInfo = DOCK_ITEMS_APPLICATION_INFO_ARRAY[dockIconColumn];
            if (applicationInfo == null) {
                return;
            }

            dockItem.setOnClickListener(view -> IntentUtil.launchApp(dockItem, applicationInfo.packageName));
        });
    }

    public boolean isEditing() {
        return mDockIconsEditAnimation != null && mDockIconsEditAnimation.isInitialized();
    }

    public final void saveApplicationItemForColumn(ApplicationInfo applicationInfo, int column) {
        DOCK_ITEMS_APPLICATION_INFO_ARRAY[column] = applicationInfo;

        DockAppsHelper.get().saveItem(getContext(), applicationInfo.packageName, column);
    }

    private void showSelectAppForIconAtColumn(int dockIconColumn) {
        SelectDockIconDialog.show(getLauncherActivity(), dockIconColumn);
    }

    private void setupDockIcons() {
        ArrayList<DockIcon> orderedDockItemPackagesArrayList = DockAppsHelper.get().getOrderedDockIcons(getContext());
        if (orderedDockItemPackagesArrayList == null || orderedDockItemPackagesArrayList.size() == 0) {
            return;
        }

        for (DockIcon dockIcon : orderedDockItemPackagesArrayList) {
            String packageName = dockIcon.getPackageName();
            if (packageName == null) {
                return;
            }

            try {
                ApplicationInfo applicationInfo = getContext().getPackageManager().getApplicationInfo(packageName, 0);

                DOCK_ITEMS_APPLICATION_INFO_ARRAY[dockIcon.getColumn()] = applicationInfo;

                View dockItemView = getItemAtColumn(dockIcon.getColumn());
                dockItemView.post(() -> {
                    IconUtil.setIconOnImageView(getLauncherActivity(),
                            ((ImageView) ((ViewGroup) dockItemView).getChildAt(0)), applicationInfo);

                    dockItemView.setOnClickListener(view -> IntentUtil.launchApp(view, packageName));

                    dockItemView.setOnLongClickListener(view -> {
                        getLauncherActivity().showShortcutsBottomSheet(applicationInfo.packageName,
                                ShortcutsLoader.loadShortcuts(getContext(), applicationInfo.packageName));

                        return !getLauncherActivity().isExpandingAppDrawer();
                    });
                });
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
