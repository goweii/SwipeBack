package per.goweii.swipeback;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Objects;

public class SwipeBackNode {
    private final Activity mActivity;
    private final boolean mTranslucent;
    private SwipeBackLayout mLayout = null;
    private SwipeBackTransformer mTransformer = null;
    private View mPreviewView = null;

    public SwipeBackNode(@NonNull Activity activity) {
        mActivity = activity;
        mTranslucent = TranslucentCompat.isActivityThemeTranslucent(activity);
    }

    @NonNull
    public Activity getActivity() {
        return mActivity;
    }

    public void inject() {
        if (mLayout != null) return;
        Window window = mActivity.getWindow();
        if (window == null) return;
        FrameLayout decorView = (FrameLayout) window.getDecorView();
        if (decorView.getChildCount() == 0) return;
        View decorChildView = decorView.getChildAt(0);
        if (decorChildView == null) return;
        TypedArray typedArray = mActivity.getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        int background = typedArray.getResourceId(0, 0);
        typedArray.recycle();
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        decorView.setBackground(new ColorDrawable(Color.TRANSPARENT));
        decorChildView.setBackgroundResource(background);
        SwipeBackLayout swipeBackLayout = new SwipeBackLayout(mActivity);
        swipeBackLayout.setSwipeBackListener(new SwipeBackListener());
        decorView.removeViewInLayout(decorChildView);
        ViewGroup.LayoutParams userContentViewParams = decorChildView.getLayoutParams();
        decorChildView.setLayoutParams(new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
        ));
        swipeBackLayout.addView(decorChildView);
        swipeBackLayout.setLayoutParams(userContentViewParams);
        decorView.addView(swipeBackLayout);
        swipeBackLayout.setSwipeBackDirection(getActivitySwipeBackDirection());
        swipeBackLayout.setSwipeBackForceEdge(isActivitySwipeBackForceEdge());
        swipeBackLayout.setSwipeBackOnlyEdge(isActivitySwipeBackOnlyEdge());
        mLayout = swipeBackLayout;
        mTransformer = getActivitySwipeBackTransformer();
    }

    @SwipeBackDirection
    private int getActivitySwipeBackDirection() {
        final int swipeBackDirection;
        if (mActivity instanceof SwipeBackAble) {
            SwipeBackAble swipeBackable = (SwipeBackAble) mActivity;
            swipeBackDirection = swipeBackable.swipeBackDirection();
        } else {
            swipeBackDirection = SwipeBack.getInstance().getSwipeBackDirection();
        }
        return swipeBackDirection;
    }

    @Nullable
    private SwipeBackTransformer getActivitySwipeBackTransformer() {
        final SwipeBackTransformer swipeBackTransformer;
        if (mActivity instanceof SwipeBackAble) {
            SwipeBackAble swipeBackable = (SwipeBackAble) mActivity;
            swipeBackTransformer = swipeBackable.swipeBackTransformer();
        } else {
            swipeBackTransformer = SwipeBack.getInstance().getSwipeBackTransformer();
        }
        return swipeBackTransformer;
    }

    private boolean isActivitySwipeBackOnlyEdge() {
        final boolean swipeBackOnlyEdge;
        if (mActivity instanceof SwipeBackAble) {
            SwipeBackAble swipeBackable = (SwipeBackAble) mActivity;
            swipeBackOnlyEdge = swipeBackable.swipeBackOnlyEdge();
        } else {
            swipeBackOnlyEdge = SwipeBack.getInstance().isSwipeBackOnlyEdge();
        }
        return swipeBackOnlyEdge;
    }

    private boolean isActivitySwipeBackForceEdge() {
        final boolean swipeBackForceEdge;
        if (mActivity instanceof SwipeBackAble) {
            SwipeBackAble swipeBackable = (SwipeBackAble) mActivity;
            swipeBackForceEdge = swipeBackable.swipeBackForceEdge();
        } else {
            swipeBackForceEdge = SwipeBack.getInstance().isSwipeBackForceEdge();
        }
        return swipeBackForceEdge;
    }

    @Nullable
    private View findPreviewView() {
        SwipeBackNode previousNode = SwipeBackManager.getInstance().getPreviousNode(this);
        if (previousNode != null) {
            FrameLayout decorView = (FrameLayout) previousNode.getActivity().getWindow().getDecorView();
            return decorView.getChildAt(0);
        }
        return null;
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
        public void onBeforeSwipe(float swipeFraction, int swipeDirection) {
            if (mLayout != null) {
                mLayout.setSwipeBackDirection(getActivitySwipeBackDirection());
                mLayout.setSwipeBackForceEdge(isActivitySwipeBackForceEdge());
                mLayout.setSwipeBackOnlyEdge(isActivitySwipeBackOnlyEdge());
            }
            mTransformer = getActivitySwipeBackTransformer();
        }

        @Override
        public void onStartSwipe(float swipeFraction, int swipeDirection) {
            mPreviewView = findPreviewView();
            if (!mTranslucent) {
                TranslucentCompat.convertActivityToTranslucent(mActivity);
            }
        }

        @Override
        public void onSwiping(float swipeFraction, int swipeDirection) {
            if (mLayout != null && mTransformer != null) {
                mTransformer.transform(mLayout, mPreviewView, swipeFraction, swipeDirection);
            }
        }

        @Override
        public void onEndSwipe(float swipeFraction, int swipeDirection) {
            if (swipeFraction != 1) {
                if (mLayout != null && mTransformer != null) {
                    mTransformer.restore(mLayout, mPreviewView, swipeFraction, swipeDirection);
                }
                if (!mTranslucent) {
                    TranslucentCompat.convertActivityFromTranslucent(mActivity);
                }
            } else {
                mActivity.finish();
                mActivity.overridePendingTransition(0, 0);
            }
        }
    }
}
