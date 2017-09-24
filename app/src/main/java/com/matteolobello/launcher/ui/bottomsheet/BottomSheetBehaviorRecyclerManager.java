package com.matteolobello.launcher.ui.bottomsheet;

import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BottomSheetBehaviorRecyclerManager {

    private List<View> mViews;
    private View.OnTouchListener mTouchEventListener;

    private CoordinatorLayout mParent;
    private BottomSheetBehaviorV2 mBehavior;
    private View mBottomSheetView;

    public BottomSheetBehaviorRecyclerManager(CoordinatorLayout parent, BottomSheetBehaviorV2 behaviorV2, View bottomSheetView) {
        mViews = new ArrayList<>();

        this.mParent = parent;
        this.mBehavior = behaviorV2;
        this.mBottomSheetView = bottomSheetView;

        initTouchCallback();
    }


    public void addControl(View recyclerView) {
        if (mViews == null) {
            mViews = new ArrayList<>();
        }
        mViews.add(recyclerView);
    }

    public void create() {
        if (mViews == null) {
            return;
        }
        if (mParent == null) {
            return;
        }
        if (mBehavior == null) {
            return;
        }
        if (mBottomSheetView == null) {
            return;
        }
        for (int i = 0; i < mViews.size(); i++) {
            mViews.get(i).setOnTouchListener(mTouchEventListener);
        }
    }

    private void initTouchCallback() {
        mTouchEventListener = (view, motionEvent) -> {
            onTouchScroll(view, motionEvent);
            return false;
        };
    }

    public void onTouchScroll(View view, MotionEvent motionEvent) {
        mBehavior.onLayoutChild(mParent, mBottomSheetView, ViewCompat.LAYOUT_DIRECTION_LTR);
        mBehavior.updateScroller(view);
    }
}
