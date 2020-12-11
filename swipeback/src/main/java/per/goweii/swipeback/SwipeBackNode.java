package per.goweii.swipeback;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
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

    public void inject() {
        if (mLayout != null) {
            return;
        }
        final int swipeBackDirection = getActivitySwipeBackDirection();
        SwipeBackLayout swipeBackLayout = new SwipeBackLayout(mActivity);
        swipeBackLayout.setSwipeDirection(swipeBackDirection);
        swipeBackLayout.setOnSwipeListener(new SwipeBackListener());
        FrameLayout decorView = (FrameLayout) mActivity.getWindow().getDecorView();
        ViewGroup activityContentView = decorView.findViewById(android.R.id.content);
        View userContentView = activityContentView.getChildAt(0);
        activityContentView.removeView(userContentView);
        ViewGroup.LayoutParams userContentViewParams = userContentView.getLayoutParams();
        userContentView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        swipeBackLayout.addView(userContentView);
        swipeBackLayout.setLayoutParams(userContentViewParams);
        activityContentView.addView(swipeBackLayout);
        mLayout = swipeBackLayout;
    }

    private int getActivitySwipeBackDirection() {
        final int swipeBackDirection;
        if (mActivity instanceof SwipeBackable) {
            SwipeBackable swipeBackable = (SwipeBackable) mActivity;
            swipeBackDirection = swipeBackable.swipeBackDirection();
        } else {
            swipeBackDirection = SwipeBack.getInstance().getSwipeBackDirection();
        }
        return swipeBackDirection;
    }

    private SwipeBackTransformer getActivitySwipeBackTransformer() {
        final SwipeBackTransformer swipeBackTransformer;
        if (mActivity instanceof SwipeBackable) {
            SwipeBackable swipeBackable = (SwipeBackable) mActivity;
            swipeBackTransformer = swipeBackable.swipeBackTransformer();
        } else {
            swipeBackTransformer = SwipeBack.getInstance().getSwipeBackTransformer();
        }
        return swipeBackTransformer;
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
            final int swipeBackDirection = getActivitySwipeBackDirection();
            mLayout.setSwipeDirection(swipeBackDirection);
            mTransformer = getActivitySwipeBackTransformer();
        }

        @Override
        public void onSwiping(int direction, float fraction) {
            if (mTransformer != null) {
                SwipeBackNode previousNode = SwipeBackManager.getInstance().getPreviousNode(SwipeBackNode.this);
                View previewView = null;
                if (previousNode != null) {
                    if (previousNode.getLayout() != null) {
                        previewView = previousNode.getLayout();
                    } else {
                        View decorView = mActivity.getWindow().getDecorView();
                        previewView = decorView.findViewById(android.R.id.content);
                    }
                }
                mTransformer.transform(mLayout, previewView, fraction, direction);
            }
        }

        @Override
        public void onEnd(int direction) {
            if (direction == 0) {
                return;
            }
            mActivity.finish();
        }
    }
}
