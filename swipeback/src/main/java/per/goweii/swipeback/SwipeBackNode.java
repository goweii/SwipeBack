package per.goweii.swipeback;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

public class SwipeBackNode {
    private final Activity mActivity;
    private final boolean mTranslucent;
    private SwipeBackLayout mLayout = null;
    private SwipeBackTransformer mTransformer = null;
    private SwipeBackNode mPreviousNode = null;

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
        mLayout = swipeBackLayout;
        configLayout();
        mTransformer = getActivitySwipeBackTransformer();
    }

    private SwipeBackDirection getActivitySwipeBackDirection() {
        final SwipeBackDirection swipeBackDirection;
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

    private boolean isRootActivitySwipeBackEnable() {
        return SwipeBack.getInstance().isRootSwipeBackEnable();
    }

    @ColorInt
    private int getActivitySwipeBackShadowColor() {
        return SwipeBack.getInstance().getShadowColor();
    }

    @Px
    private int getActivitySwipeBackShadowSize() {
        return SwipeBack.getInstance().getShadowSize();
    }

    @IntRange(from = 0, to = 255)
    private int getActivitySwipeBackMaskAlpha() {
        return SwipeBack.getInstance().getMaskAlpha();
    }

    private void configLayout() {
        if (mLayout != null) {
            if (isRootNode() && !isRootActivitySwipeBackEnable()) {
                mLayout.setSwipeBackDirection(SwipeBackDirection.NONE);
            } else {
                mLayout.setSwipeBackDirection(getActivitySwipeBackDirection());
            }
            mLayout.setSwipeBackForceEdge(isActivitySwipeBackForceEdge());
            mLayout.setSwipeBackOnlyEdge(isActivitySwipeBackOnlyEdge());
            mLayout.setMaskAlpha(getActivitySwipeBackMaskAlpha());
            mLayout.setShadowColor(getActivitySwipeBackShadowColor());
            mLayout.setShadowSize(getActivitySwipeBackShadowSize());
        }
    }

    private boolean isRootNode() {
        return SwipeBackManager.getInstance().isRootNode(this);
    }

    @Nullable
    private View getTransformerView() {
        Window window = mActivity.getWindow();
        if (window == null) return null;
        FrameLayout decorView = (FrameLayout) window.getDecorView();
        if (decorView.getChildCount() == 0) return null;
        return decorView.getChildAt(0);
    }

    @Nullable
    private View getPreviousView() {
        if (mPreviousNode == null) return null;
        return mPreviousNode.getTransformerView();
    }

    @Nullable
    private SwipeBackNode findPreviousNode() {
        return SwipeBackManager.getInstance().findPreviousNode(this);
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
        return mActivity.hashCode();
    }

    private class SwipeBackListener implements SwipeBackLayout.SwipeBackListener {
        @Override
        public void onBeforeSwipe(@FloatRange(from = 0F, to = 1F) float swipeFraction, @NonNull SwipeBackDirection swipeDirection) {
            configLayout();
            mTransformer = getActivitySwipeBackTransformer();
        }

        @Override
        public void onStartSwipe(@FloatRange(from = 0F, to = 1F) float swipeFraction, @NonNull SwipeBackDirection swipeDirection) {
            mPreviousNode = findPreviousNode();
            if (mLayout != null && mTransformer != null && swipeFraction == 0) {
                mTransformer.initialize(mLayout, getPreviousView());
            }
            if (!mTranslucent) {
                TranslucentCompat.convertActivityToTranslucent(mActivity);
            }
        }

        @Override
        public void onSwiping(@FloatRange(from = 0F, to = 1F) float swipeFraction, @NonNull SwipeBackDirection swipeDirection) {
            if (mLayout != null && mTransformer != null) {
                mTransformer.transform(mLayout, getPreviousView(), swipeFraction, swipeDirection);
            }
        }

        @Override
        public void onEndSwipe(@FloatRange(from = 0F, to = 1F) float swipeFraction, @NonNull SwipeBackDirection swipeDirection) {
            if (mLayout != null && mTransformer != null) {
                mTransformer.restore(mLayout, getPreviousView());
            }
            mPreviousNode = null;
            if (swipeFraction != 1) {
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
