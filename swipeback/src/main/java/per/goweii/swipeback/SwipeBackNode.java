package per.goweii.swipeback;

import android.app.Activity;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.FrameLayout;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import per.goweii.swipeback.utils.ActivityTranslucentConverter;

class SwipeBackNode {
    private final Activity mActivity;
    private final ActivityTranslucentConverter mTranslucentConverter;
    private final boolean mThemeTranslucent;

    private SwipeBackLayout mLayout = null;

    private SwipeBackTransformer mTransformer = null;

    SwipeBackNode(@NonNull Activity activity) {
        mActivity = activity;
        mTranslucentConverter = new ActivityTranslucentConverter(activity);
        mThemeTranslucent = mTranslucentConverter.isThemeTranslucent();
    }

    @NonNull
    Activity getActivity() {
        return mActivity;
    }

    void inject() {
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
        mTransformer = SwipeBackAbility.getSwipeBackTransformerForActivity(mActivity);
    }

    private void configLayout() {
        if (mLayout != null) {
            if (SwipeBackManager.getInstance().isRootNode(this) && !SwipeBack.getInstance().isRootSwipeBackEnable()) {
                mLayout.setSwipeBackDirection(SwipeBackDirection.NONE);
            } else {
                mLayout.setSwipeBackDirection(SwipeBackAbility.getSwipeBackDirectionForActivity(mActivity));
            }
            mLayout.setSwipeBackForceEdge(SwipeBackAbility.isSwipeBackForceEdgeForActivity(mActivity));
            mLayout.setSwipeBackOnlyEdge(SwipeBackAbility.isSwipeBackOnlyEdgeForActivity(mActivity));
            mLayout.setMaskAlpha(SwipeBackAbility.getSwipeBackMaskAlphaForActivity(mActivity));
            mLayout.setShadowColor(SwipeBackAbility.getSwipeBackShadowColorForActivity(mActivity));
            mLayout.setShadowSize(SwipeBackAbility.getSwipeBackShadowSizeForActivity(mActivity));
        }
    }

    @Nullable
    private SwipeBackNode findPreviousNode() {
        return SwipeBackManager.getInstance().findPreviousNode(this);
    }

    @Nullable
    private FrameLayout getDecorView() {
        Window window = mActivity.getWindow();
        if (window == null) return null;
        return (FrameLayout) window.getDecorView();
    }

    @Nullable
    private View getDecorChild0() {
        FrameLayout decorView = getDecorView();
        if (decorView == null) return null;
        if (decorView.getChildCount() == 0) return null;
        return decorView.getChildAt(0);
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
        private SwipeBackNode mPreviousNode = null;

        @Override
        public void onBeforeSwipe(@FloatRange(from = 0F, to = 1F) float swipeFraction, @NonNull SwipeBackDirection swipeDirection) {
            configLayout();
            mTransformer = SwipeBackAbility.getSwipeBackTransformerForActivity(mActivity);
        }

        @Override
        public void onStartSwipe(@FloatRange(from = 0F, to = 1F) float swipeFraction, @NonNull SwipeBackDirection swipeDirection) {
            mPreviousNode = findPreviousNode();
            if (!mThemeTranslucent) {
                mTranslucentConverter.toTranslucent();
            }
            if (mLayout != null && mTransformer != null) {
                View previousDecorView = null;
                View previousDecorChild0 = null;
                if (mPreviousNode != null) {
                    previousDecorView = mPreviousNode.getDecorView();
                    previousDecorChild0 = mPreviousNode.getDecorChild0();
                }
                if (previousDecorView != null) {
                    previousDecorView.setBackground(new ColorDrawable(Color.BLACK));
                }
                if (swipeFraction == 0) {
                    mTransformer.initialize(mLayout, previousDecorChild0);
                }
                mTransformer.transform(mLayout, previousDecorChild0, swipeFraction, swipeDirection);
            }
        }

        @Override
        public void onSwiping(@FloatRange(from = 0F, to = 1F) float swipeFraction, @NonNull SwipeBackDirection swipeDirection) {
            if (mLayout != null && mTransformer != null) {
                View previousDecorChild0 = null;
                if (mPreviousNode != null) {
                    previousDecorChild0 = mPreviousNode.getDecorChild0();
                }
                mTransformer.transform(mLayout, previousDecorChild0, swipeFraction, swipeDirection);
            }
        }

        @Override
        public void onEndSwipe(@FloatRange(from = 0F, to = 1F) float swipeFraction, @NonNull SwipeBackDirection swipeDirection) {
            if (mLayout != null && mTransformer != null) {
                View previousDecorView = null;
                View previousDecorChild0 = null;
                if (mPreviousNode != null) {
                    previousDecorView = mPreviousNode.getDecorView();
                    previousDecorChild0 = mPreviousNode.getDecorChild0();
                }
                mTransformer.restore(mLayout, previousDecorChild0);
                if (previousDecorView != null) {
                    previousDecorView.setBackground(new ColorDrawable(Color.TRANSPARENT));
                }
            }
            if (swipeFraction != 1) {
                if (!mThemeTranslucent) {
                    mTranslucentConverter.fromTranslucent();
                }
            } else {
                mActivity.finish();
                mActivity.overridePendingTransition(0, 0);
            }
            mPreviousNode = null;
        }
    }
}
