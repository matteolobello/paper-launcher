package com.matteolobello.launcher.ui.activity;

import android.animation.ArgbEvaluator;
import android.annotation.SuppressLint;
import android.app.WallpaperManager;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ShortcutInfo;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.matteolobello.launcher.R;
import com.matteolobello.launcher.data.loader.ApplicationInfoLoader;
import com.matteolobello.launcher.data.loader.IconPackLoader;
import com.matteolobello.launcher.data.loader.IconRowsLoader;
import com.matteolobello.launcher.data.model.IconPack;
import com.matteolobello.launcher.data.preference.IconPackSelectHelper;
import com.matteolobello.launcher.data.preference.MostLaunchedHelper;
import com.matteolobello.launcher.data.watcher.AppListUpdateWatcher;
import com.matteolobello.launcher.data.watcher.HomeButtonPressWatcher;
import com.matteolobello.launcher.data.watcher.ScreenOffWatcher;
import com.matteolobello.launcher.ui.adapter.fragment.DockViewPagerAdapter;
import com.matteolobello.launcher.ui.adapter.recyclerview.AllAppsRecyclerViewAdapter;
import com.matteolobello.launcher.ui.adapter.recyclerview.ShortcutsRecyclerViewAdapter;
import com.matteolobello.launcher.ui.bottomsheet.BottomSheetBehaviorRecyclerManager;
import com.matteolobello.launcher.ui.bottomsheet.BottomSheetBehaviorV2;
import com.matteolobello.launcher.ui.dialog.CustomizationDialog;
import com.matteolobello.launcher.ui.fragment.HomeScreenDockFragment;
import com.matteolobello.launcher.ui.view.WorkspaceLayout;
import com.matteolobello.launcher.util.DpPxUtil;
import com.matteolobello.launcher.util.IconUtil;
import com.matteolobello.launcher.util.IntentUtil;
import com.matteolobello.launcher.util.SDKUtil;
import com.matteolobello.launcher.util.SystemBarUtil;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LauncherActivity extends AppCompatActivity implements
        SlidingUpPanelLayout.PanelSlideListener, HomeButtonPressWatcher.OnHomePressedListener,
        AppListUpdateWatcher.OnMustUpdateAppListListener, ScreenOffWatcher.OnScreenOffListener {

    /**
     * The number of columns of the dock and app drawer
     */
    public static final int APP_DRAWER_COLUMNS = 5;

    /**
     * The wallpaper select request code
     */
    public static final int WALLPAPER_SELECT_INTENT_CODE = 505;

    /**
     * The full List of apps
     */
    private List<ApplicationInfo> mApplicationInfoList;

    /**
     * The Views
     */
    private CoordinatorLayout mParentCoordinatorLayout;
    private SlidingUpPanelLayout mSlidingUpPanelLayout;
    private CardView mSearchBarCardView;
    private ImageView mWallpaperImageView;
    private WorkspaceLayout mWorkspaceLayout;
    private ViewPager mDockViewPager;
    private ImageView mGoogleIconImageView;
    private ImageView mGoogleArrowImageView;
    private ImageView mGoogleMicImageView;
    private View mSearchBarPillDividerView;
    private View mSearchAppsWrapperView;
    private TextView mSearchAppsTextView;
    private View mAppDrawerShadowView;
    private RecyclerView mAllAppsRecyclerView;
    private View mBottomSheetView;
    private ImageView mBottomSheetAppImageView;
    private TextView mBottomSheetAppTextView;
    private ImageView mBottomSheetAppInfoImageView;
    private RecyclerView mShortcutsRecyclerView;
    private FrameLayout mWallpaperOverlay;

    /**
     * The Adapters
     */
    private DockViewPagerAdapter mDockViewPagerAdapter;
    private AllAppsRecyclerViewAdapter mAllAppsRecyclerViewAdapter;

    /**
     * The Shortcuts BottomSheetBehavior with support to RecyclerView
     */
    private BottomSheetBehaviorV2 mBottomSheetBehavior;

    /**
     * Used to handle IconPacks
     */
    private IconPack mCurrentIconPack;

    /**
     * Used to handle the most launched apps
     */
    private MostLaunchedHelper mMostLaunchedHelper;

    /**
     * Handle shadow alpha on app drawer scroll
     */
    private RecyclerView.OnScrollListener mAllAppsRecyclerViewScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            boolean isFirstRowVisible = ((LinearLayoutManager) mAllAppsRecyclerView.getLayoutManager())
                    .findFirstCompletelyVisibleItemPosition() == 0;
            mAppDrawerShadowView.setAlpha(isFirstRowVisible ? 0.0f : 1.0f);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launcher);
        findViews();
        setupUi();
    }

    private void findViews() {
        mWallpaperOverlay = findViewById(R.id.wallpaper_overlay);
        mParentCoordinatorLayout = findViewById(R.id.parent_container);
        mSlidingUpPanelLayout = findViewById(R.id.sliding_layout);
        mSearchBarCardView = findViewById(R.id.search_bar_container);
        mWallpaperImageView = findViewById(R.id.wallpaper);
        mWorkspaceLayout = findViewById(R.id.workspace);
        mDockViewPager = findViewById(R.id.dock_view_pager);
        mGoogleIconImageView = findViewById(R.id.google_icon);
        mGoogleArrowImageView = findViewById(R.id.google_arrow);
        mGoogleMicImageView = findViewById(R.id.google_mic);
        mSearchBarPillDividerView = findViewById(R.id.pill_divider);
        mSearchAppsWrapperView = findViewById(R.id.search_apps_wrapper);
        mSearchAppsTextView = findViewById(R.id.search_apps_label);
        mAppDrawerShadowView = findViewById(R.id.app_drawer_top_shadow);
        mAllAppsRecyclerView = findViewById(R.id.apps_recycler_view);
        mBottomSheetView = findViewById(R.id.bottom_sheet);
        mBottomSheetAppImageView = findViewById(R.id.shortcuts_app_icon);
        mBottomSheetAppTextView = findViewById(R.id.shortcuts_app_title);
        mBottomSheetAppInfoImageView = findViewById(R.id.shortcuts_app_info);
        mShortcutsRecyclerView = findViewById(R.id.shortcuts_recycler_view);
    }

    private void setupUi() {
        mApplicationInfoList = ApplicationInfoLoader.loadAppList(this);

        mCurrentIconPack = fetchCurrentIconPack();

        mMostLaunchedHelper = MostLaunchedHelper.get();
        mMostLaunchedHelper.setupIfNeeded(this, mApplicationInfoList);

        mParentCoordinatorLayout.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);

        CoordinatorLayout.LayoutParams params =
                (CoordinatorLayout.LayoutParams) mBottomSheetView.getLayoutParams();
        params.setBehavior(new BottomSheetBehaviorV2());
        mBottomSheetView.requestLayout();

        mBottomSheetBehavior = BottomSheetBehaviorV2.from(mBottomSheetView);

        SystemBarUtil.setFullyTransparentStatusBar(this);
        if (SDKUtil.AT_LEAST_O) {
            // Avoid setting white color to NavigationBar on Lollipop
            // as SoftKeys wouldn't be visible (white on white)
            SystemBarUtil.setNavigationBarColor(this, getColor(R.color.colorPrimary));

            SystemBarUtil.enableDarkNavigationBarIcons(this);
        }

        mGoogleArrowImageView.setOnClickListener(view -> {
            if (isAppDrawerOpened()) {
                mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
            }
        });

        mGoogleMicImageView.setOnClickListener(IntentUtil::startGoogleVoiceRecognitionActivity);

        mSearchAppsWrapperView.setOnClickListener(view -> {
            if (!isAppDrawerOpened()) {
                IntentUtil.startGoogleSearchActivity(view);
            }
        });

        mDockViewPagerAdapter = new DockViewPagerAdapter(getSupportFragmentManager());
        mDockViewPager.setAdapter(mDockViewPagerAdapter);

        mSlidingUpPanelLayout.addPanelSlideListener(this);

        mShortcutsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mShortcutsRecyclerView.setItemViewCacheSize(30);
        mShortcutsRecyclerView.setDrawingCacheEnabled(true);
        mShortcutsRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mShortcutsRecyclerView.setHasFixedSize(true);

        mAllAppsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAllAppsRecyclerView.setItemViewCacheSize(30);
        mAllAppsRecyclerView.setDrawingCacheEnabled(true);
        mAllAppsRecyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        mAllAppsRecyclerView.setHasFixedSize(true);
        mAllAppsRecyclerView.setOnScrollListener(mAllAppsRecyclerViewScrollListener);

        setAppDrawerData();

        updateWallpaper();

        HomeButtonPressWatcher homeButtonPressWatcher = new HomeButtonPressWatcher(this);
        homeButtonPressWatcher.setOnHomePressedListener(this);
        homeButtonPressWatcher.startWatching();

        AppListUpdateWatcher appListUpdateWatcher = new AppListUpdateWatcher(this);
        appListUpdateWatcher.setOnMustUpdateAppListListener(this);
        appListUpdateWatcher.startWatching();

        ScreenOffWatcher screenOffWatcher = new ScreenOffWatcher(this);
        screenOffWatcher.setOnScreenOffListener(this);
        screenOffWatcher.startWatching();
    }

    @Override
    public void onPanelSlide(View panel, float slideOffset) {
        mDockViewPagerAdapter.getHomeScreenDockFragment().stopEditAnimation();

        SystemBarUtil.setStatusBarColor(this, (Integer) new ArgbEvaluator().evaluate(slideOffset,
                Color.TRANSPARENT, ContextCompat.getColor(this, R.color.colorStatusBar)));

        if (slideOffset >= 0.6) {
            SystemBarUtil.enableLightStatusBar(this);
        } else {
            SystemBarUtil.clearLightStatusBar(this);
        }

        boolean isShowingHomeScreenDock = mDockViewPager.getCurrentItem() == 0;
        if (isShowingHomeScreenDock) {
            mDockViewPager.scrollTo((int) (mDockViewPager.getWidth() * slideOffset), 0);
        }

        FrameLayout.LayoutParams searchBarCardViewLayoutParams = (FrameLayout.LayoutParams) mSearchBarCardView.getLayoutParams();

        searchBarCardViewLayoutParams.topMargin = (int) (DpPxUtil.dpToPx(this, 32)
                - DpPxUtil.dpToPx(this, 8) * slideOffset);
        searchBarCardViewLayoutParams.leftMargin = (int) (DpPxUtil.dpToPx(this, 8)
                - DpPxUtil.dpToPx(this, 8) * slideOffset);
        searchBarCardViewLayoutParams.rightMargin = (int) (DpPxUtil.dpToPx(this, 8)
                - DpPxUtil.dpToPx(this, 8) * slideOffset);
        searchBarCardViewLayoutParams.height = (int) (DpPxUtil.dpToPx(this, 48)
                + DpPxUtil.dpToPx(this, 8) * slideOffset);

        mSearchBarCardView.setLayoutParams(searchBarCardViewLayoutParams);

        float searchBarCardViewElevation = DpPxUtil.dpToPx(this, 4)
                - DpPxUtil.dpToPx(this, 4) * slideOffset;
        mSearchBarCardView.setCardElevation(searchBarCardViewElevation);

        float searchBarCardViewRadius = DpPxUtil.dpToPx(this, 2)
                - DpPxUtil.dpToPx(this, 2) * slideOffset;
        // CardView bug, if radius equals to 0.xxx, the alpha will be changed
        mSearchBarCardView.setRadius(
                String.valueOf(searchBarCardViewRadius).charAt(0) == '0'
                        ? 0
                        : searchBarCardViewRadius);

        mGoogleArrowImageView.animate()
                .setDuration(0)
                .rotation(-90 * slideOffset)
                .translationX(DpPxUtil.dpToPx(this, 12) * slideOffset)
                .start();

        mGoogleIconImageView.animate()
                .setDuration(0)
                .scaleX(1 - slideOffset)
                .scaleY(1 - slideOffset)
                .start();

        mSearchAppsTextView.animate()
                .setDuration(0)
                .alpha(slideOffset)
                .start();

        mWallpaperOverlay.animate()
                .setDuration(0)
                .alpha(slideOffset)
                .start();

        mSearchAppsWrapperView.animate()
                .setDuration(0)
                .translationX(-DpPxUtil.dpToPx(this, 26) * slideOffset)
                .start();

        mSearchBarPillDividerView.animate()
                .setDuration(0)
                .translationX(-DpPxUtil.dpToPx(this, 16) * slideOffset)
                .start();

        mGoogleMicImageView.setColorFilter((Integer) new ArgbEvaluator().evaluate(slideOffset, Color.TRANSPARENT,
                ContextCompat.getColor(this, R.color.colorMicOnAppDrawer)));
    }

    @Override
    public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState,
                                    SlidingUpPanelLayout.PanelState newState) {
    }

    @Override
    public void onHomePressed() {
        if (isAppDrawerOpened()) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }

        if (mBottomSheetBehavior.getState() == BottomSheetBehaviorV2.STATE_EXPANDED) {
            mBottomSheetBehavior.setState(BottomSheetBehaviorV2.STATE_COLLAPSED);
        }
    }

    @Override
    public void onHomeLongPressed() {
        // Do nothing
    }

    @Override
    public void onMustUpdateAppList() {
        setAppDrawerData();
    }

    @Override
    public void onScreenOff() {
        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
    }

    @Override
    public void onBackPressed() {
        if (mBottomSheetBehavior.getState() != BottomSheetBehaviorV2.STATE_COLLAPSED) {
            mBottomSheetBehavior.setState(BottomSheetBehaviorV2.STATE_COLLAPSED);
            return;
        }

        if (mDockViewPagerAdapter.getHomeScreenDockFragment().isEditing()) {
            mDockViewPagerAdapter.getHomeScreenDockFragment().stopEditAnimation();
            return;
        }

        if (isAppDrawerOpened()) {
            mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mBottomSheetBehavior.getState() == BottomSheetBehaviorV2.STATE_EXPANDED) {
                Rect outRect = new Rect();
                mBottomSheetView.getGlobalVisibleRect(outRect);

                if (!outRect.contains((int) event.getRawX(), (int) event.getRawY())) {
                    mBottomSheetBehavior.setState(BottomSheetBehaviorV2.STATE_COLLAPSED);
                }
            }
        }

        return super.dispatchTouchEvent(event);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == WALLPAPER_SELECT_INTENT_CODE && resultCode == RESULT_OK) {
            updateWallpaper();
        }
    }

    public void notifyAppLaunch(ApplicationInfo applicationInfo) {
        mMostLaunchedHelper.incrementCounterForPackageName(this, applicationInfo.packageName);
        mDockViewPagerAdapter.getMostLaunchedAppsDockFragment().dispatchMostLaunchedAppIconsUpdate();
    }

    public void startDockIconsEditing() {
        mDockViewPagerAdapter.getHomeScreenDockFragment().startIconsEditing();
    }

    public boolean isExpandingAppDrawer() {
        return mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.DRAGGING;
    }

    public RecyclerView getAppListRecyclerView() {
        return mAllAppsRecyclerView;
    }

    public DockViewPagerAdapter getDockViewPagerAdapter() {
        return mDockViewPagerAdapter;
    }

    public List<ApplicationInfo> getApplicationInfoList() {
        return mApplicationInfoList;
    }

    public void onDockAppSelected(ApplicationInfo applicationInfo, int column) {
        HomeScreenDockFragment homeScreenDockFragment = mDockViewPagerAdapter.getHomeScreenDockFragment();
        homeScreenDockFragment.saveApplicationItemForColumn(applicationInfo, column);

        homeScreenDockFragment.iterateOverDockIcons((dockItem, dockIconColumn) -> {
            if (dockIconColumn == column) {
                IconUtil.setIconOnImageView(this,
                        ((ImageView) ((ViewGroup) dockItem).getChildAt(0)), applicationInfo);
            }
        });
    }

    public void showShortcutsBottomSheet(String packageName, List<ShortcutInfo> shortcutsArrayList) {
        if (isExpandingAppDrawer()) {
            return;
        }

        mShortcutsRecyclerView.setAdapter(new ShortcutsRecyclerViewAdapter(shortcutsArrayList == null
                ? new ArrayList<>()
                : shortcutsArrayList));

        ApplicationInfo applicationInfo;
        try {
            applicationInfo = getPackageManager().getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();

            return;
        }

        IconUtil.setIconOnImageView(this, mBottomSheetAppImageView, applicationInfo);

        mBottomSheetAppTextView.setText(applicationInfo.loadLabel(getPackageManager()));

        mBottomSheetAppInfoImageView.setOnClickListener(view -> {
            Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.setData(Uri.parse("package:" + packageName));
            startActivity(intent);
        });

        BottomSheetBehaviorRecyclerManager manager =
                new BottomSheetBehaviorRecyclerManager(mParentCoordinatorLayout, mBottomSheetBehavior, mBottomSheetView);
        manager.addControl(mShortcutsRecyclerView);
        manager.create();

        mBottomSheetBehavior.setState(BottomSheetBehaviorV2.STATE_EXPANDED);
    }

    public IconPack getCurrentIconPack() {
        return mCurrentIconPack;
    }

    public void openAppDrawer() {
        mSlidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.EXPANDED);
    }

    public void showWorkspaceEditDialog() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null) {
            vibrator.vibrate(25);
        }

        CustomizationDialog.show(this);
    }

    @SuppressLint({"PrivateApi", "WrongConstant"})
    public void expandStatusBar() {
        try {
            Class.forName("android.app.StatusBarManager")
                    .getMethod("expandNotificationsPanel")
                    .invoke(getSystemService("statusbar"));
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void updateWallpaper() {
        mWallpaperImageView.setImageDrawable(WallpaperManager.getInstance(this).getDrawable());
    }

    private IconPack fetchCurrentIconPack() {
        HashMap<String, IconPack> iconPackHashMap = IconPackLoader.getAvailableIconPacks(this, false);
        String iconPackPackageName = IconPackSelectHelper.get().getIconPack(this);
        if (iconPackPackageName != null) {
            for (Map.Entry<String, IconPack> entry : iconPackHashMap.entrySet()) {
                if (entry.getKey().equals(iconPackPackageName)) {
                    return entry.getValue();
                }
            }
        }

        return null;
    }

    private void setAppDrawerData() {
        new UpdateAppDrawerAsyncTask().execute();
    }

    private boolean isAppDrawerOpened() {
        return mSlidingUpPanelLayout.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED;
    }

    private class UpdateAppDrawerAsyncTask extends AsyncTask<Void, Void, List<ApplicationInfo>> {

        @Override
        protected List<ApplicationInfo> doInBackground(Void... voids) {
            return ApplicationInfoLoader.loadAppList(getApplicationContext());
        }

        @Override
        protected void onPostExecute(List<ApplicationInfo> applicationInfoList) {
            super.onPostExecute(applicationInfoList);

            mApplicationInfoList = applicationInfoList;

            mAllAppsRecyclerViewAdapter = new AllAppsRecyclerViewAdapter(LauncherActivity.this,
                    IconRowsLoader.loadIconRows(mApplicationInfoList));
            mAllAppsRecyclerView.setAdapter(mAllAppsRecyclerViewAdapter);

            mDockViewPagerAdapter.getMostLaunchedAppsDockFragment().dispatchMostLaunchedAppIconsUpdate();
            mDockViewPagerAdapter.getHomeScreenDockFragment().setupDockIcons();
        }
    }
}
