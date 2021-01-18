package per.goweii.swipeback;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
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

    public SwipeBackLayout getSwipeBackLayout() {
        return mLayout;
    }

    public void inject() {
        if (mLayout != null) {
            return;
        }
        final int swipeBackDirection = getActivitySwipeBackDirection();
        SwipeBackLayout swipeBackLayout = new SwipeBackLayout(mActivity);
        swipeBackLayout.setSwipeBackDirection(swipeBackDirection);
        swipeBackLayout.setSwipeBackListener(new SwipeBackListener());
        FrameLayout decorView = (FrameLayout) mActivity.getWindow().getDecorView();
        View userContentView = decorView.getChildAt(0);
        decorView.removeViewInLayout(userContentView);
        ViewGroup.LayoutParams userContentViewParams = userContentView.getLayoutParams();
        userContentView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        swipeBackLayout.addView(userContentView);
        swipeBackLayout.setLayoutParams(userContentViewParams);
        decorView.addView(swipeBackLayout);
        mLayout = swipeBackLayout;
        mTransformer = getActivitySwipeBackTransformer();
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

    private class SwipeBackListener implements SwipeBackLayout.SwipeBackListener {
        @Override
        public void onSwipeStart(float swipeFraction, int swipeDirection) {
            SwipeBackCompat.convertActivityToTranslucent(mActivity);
        }

        @Override
        public void onSwiping(float swipeFraction, int swipeDirection) {
            if (mTransformer != null) {
                SwipeBackNode previousNode = SwipeBackManager.getInstance().getPreviousNode(SwipeBackNode.this);
                if (previousNode != null) {
                    FrameLayout decorView = (FrameLayout) previousNode.getActivity().getWindow().getDecorView();
                    View previewView = decorView.getChildAt(0);
                    mTransformer.transform(mLayout, previewView, swipeFraction, swipeDirection);
                }
            }
        }

        @Override
        public void onSwipeEnd(float swipeFraction, int swipeDirection) {
            if (swipeFraction != 1) {
                SwipeBackCompat.convertActivityFromTranslucent(mActivity);
            } else {
                mActivity.finish();
                mActivity.overridePendingTransition(0, 0);
            }
        }
    }
}
