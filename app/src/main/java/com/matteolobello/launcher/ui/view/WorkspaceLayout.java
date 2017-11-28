package com.matteolobello.launcher.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.matteolobello.launcher.ui.activity.LauncherActivity;

public class WorkspaceLayout extends View {

    private static final int SWIPE_UP = 1;
    private static final int SWIPE_DOWN = 2;
    private static final int SWIPE_RIGHT = 3;
    private static final int SWIPE_LEFT = 4;

    public WorkspaceLayout(Context context) {
        this(context, null);
    }

    public WorkspaceLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    @SuppressLint("ClickableViewAccessibility")
    public WorkspaceLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        GestureDetectorCompat gestureDetectorCompat
                = new GestureDetectorCompat(getContext(), new CustomGestureDetector(this));

        setOnTouchListener((view, motionEvent) -> {
            gestureDetectorCompat.onTouchEvent(motionEvent);

            return true;
        });
    }

    static class CustomGestureDetector extends GestureDetector.SimpleOnGestureListener {

        private final LauncherActivity mLauncherActivity;

        CustomGestureDetector(View view) {
            mLauncherActivity = (LauncherActivity) view.getContext();
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            switch (getSlope(e1.getX(), e1.getY(), e2.getX(), e2.getY())) {
                case SWIPE_UP:
                    mLauncherActivity.openAppDrawer();
                    return true;
                case SWIPE_DOWN:
                    mLauncherActivity.expandStatusBar();
                    return true;
            }

            return false;
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            mLauncherActivity.getDockViewPagerAdapter().getHomeScreenDockFragment().stopEditAnimation();

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            super.onLongPress(e);

            mLauncherActivity.showWorkspaceEditDialog();
        }

        private int getSlope(float x1, float y1, float x2, float y2) {
            Double angle = Math.toDegrees(Math.atan2(y1 - y2, x2 - x1));
            if (angle > 45 && angle <= 135) {
                return SWIPE_UP;
            }

            if (angle >= 135 && angle < 180 || angle < -135 && angle > -180) {
                return SWIPE_LEFT;
            }

            if (angle < -45 && angle >= -135) {
                return SWIPE_DOWN;
            }

            if (angle > -45 && angle <= 45) {
                return SWIPE_RIGHT;
            }

            return 0;
        }
    }
}
