package com.matteolobello.launcher.ui.fragment.base;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.matteolobello.launcher.R;
import com.matteolobello.launcher.data.loader.ShortcutsLoader;
import com.matteolobello.launcher.data.preference.MostLaunchedHelper;
import com.matteolobello.launcher.ui.activity.LauncherActivity;
import com.matteolobello.launcher.util.IconUtil;
import com.matteolobello.launcher.util.IntentUtil;

import java.util.ArrayList;

public abstract class DockFragment extends Fragment {

    private View mRootView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.item_dock, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRootView = view;
        init(mRootView);
    }

    public void dispatchMostLaunchedAppIconsUpdate() {
        if (!isAdded()) {
            return;
        }
        
        final ArrayList<String> packageNames = MostLaunchedHelper.get().getMostLaunchedApps(getContext());

        iterateOverDockIcons((dockItemView, dockIconColumn) -> {
            final String packageName = packageNames.get(dockIconColumn);
            if (packageName == null) {
                return;
            }

            dockItemView.setOnClickListener(view -> IntentUtil.launchApp(view, packageName));

            ImageView imageView = (ImageView) ((ViewGroup) dockItemView).getChildAt(0);
            try {
                IconUtil.setIconOnImageView(getLauncherActivity(), imageView,
                        getContext().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA));

                dockItemView.setOnLongClickListener(view -> {
                    getLauncherActivity().showShortcutsBottomSheet(
                            packageName, ShortcutsLoader.loadShortcuts(getContext(), packageName));

                    return !getLauncherActivity().isExpandingAppDrawer();
                });
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
        });
    }

    public void init(View rootView) {
        // Can be overridden
    }

    public final void iterateOverDockIcons(DockItemIteratorCallback callback) {
        for (int column = 0; column < LauncherActivity.APP_DRAWER_COLUMNS; column++) {
            callback.editDockIcon(getItemAtColumn(column), column);
        }
    }

    public final View getItemAtColumn(int column) {
        final int iconRes;
        switch (column + 1) {
            case 1:
                iconRes = R.id.first_icon_row;
                break;
            case 2:
                iconRes = R.id.second_icon_row;
                break;
            case 3:
                iconRes = R.id.third_icon_row;
                break;
            case 4:
                iconRes = R.id.fourth_icon_row;
                break;
            case 5:
                iconRes = R.id.fifth_icon_row;
                break;
            default:
                throw new UnsupportedOperationException("Column index unknown");
        }

        return mRootView.findViewById(iconRes);
    }

    public final LauncherActivity getLauncherActivity() {
        return ((LauncherActivity) getActivity());
    }

    public interface DockItemIteratorCallback {
        void editDockIcon(View dockItem, int dockIconColumn);
    }
}
