package per.goweii.swipeback;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import java.util.Objects;

public class SwipeBackNode {
    private final Activity mActivity;
    private SwipeBackLayout mLayout = null;
    private SwipeBackTransformer mTransformer = null;

    public SwipeBackNode(@NonNull Activity activity) {
        mActivity = activity;
    }

    public Activity getActivity() {
        return mActivity;
    }

    public SwipeBackLayout getLayout() {
        return mLayout;
    }

    public SwipeBackTransformer getTransformer() {
        return mTransformer;
    }

    public void inject() {
        if (mLayout != null) {
            return;
        }
        int swipeBackDirection = 0;
        SwipeBackTransformer swipeBackTransformer = null;
        if (mActivity instanceof SwipeBackable) {
            SwipeBackable swipeBackable = (SwipeBackable) mActivity;
            swipeBackDirection = swipeBackable.swipeBackDirection();
            swipeBackTransformer = swipeBackable.swipeBackTransformer();
        } else {
            swipeBackDirection = SwipeBack.getInstance().getSwipeBackDirection();
            swipeBackTransformer = SwipeBack.getInstance().getSwipeBackTransformer();
        }
        if (swipeBackDirection == 0) {
            return;
        }
        SwipeBackLayout swipeBackLayout = new SwipeBackLayout(mActivity);
        swipeBackLayout.setSwipeDirection(swipeBackDirection);
        swipeBackLayout.setOnSwipeListener(new SwipeBackListener());
        FrameLayout decorView = (FrameLayout) mActivity.getWindow().getDecorView();
        View activityContentView = decorView.findViewById(android.R.id.content);
        int activityContentViewIndex = decorView.indexOfChild(activityContentView);
        decorView.removeViewInLayout(activityContentView);
        FrameLayout.LayoutParams activityContentLayoutParams = (FrameLayout.LayoutParams) activityContentView.getLayoutParams();
        activityContentView.setLayoutParams(null);
        swipeBackLayout.addView(activityContentView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        decorView.addView(swipeBackLayout, activityContentViewIndex, activityContentLayoutParams);
        mLayout = swipeBackLayout;
        mTransformer = swipeBackTransformer;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SwipeBackNode that = (SwipeBackNode) o;
        return mActivity == that.mActivity;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mActivity);
    }

    private class SwipeBackListener implements SwipeBackLayout.OnSwipeListener {
        @Override
        public void onStart() {

        }

        @Override
        public void onSwiping(int direction, float fraction) {

        }

        @Override
        public void onEnd(int direction) {

        }
    }
}
