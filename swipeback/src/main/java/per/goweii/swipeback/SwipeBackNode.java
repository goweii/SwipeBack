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

import per.goweii.swipeback.utils.ActivityTranslucentConverter;

class SwipeBackNode {
    private final Activity mActivity;
    private final ActivityTranslucentConverter mTranslucentConverter;
    private final boolean mThemeTranslucent;

    private SwipeBackLayout mLayout = null;

    private SwipeBackTransformer mTransformer = null;
    private SwipeBackNode mPreviousNode = null;

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
    private View getPreviousView() {
        if (mPreviousNode == null) return null;
        Window window = mPreviousNode.mActivity.getWindow();
        if (window == null) return null;
        FrameLayout decorView = (FrameLayout) window.getDecorView();
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
            if (mLayout != null && mTransformer != null && swipeFraction == 0 && mTranslucentConverter.isTranslucent()) {
                mTransformer.initialize(mLayout, getPreviousView());
            }
        }

        @Override
        public void onSwiping(@FloatRange(from = 0F, to = 1F) float swipeFraction, @NonNull SwipeBackDirection swipeDirection) {
            if (mLayout != null && mTransformer != null && mTranslucentConverter.isTranslucent()) {
                mTransformer.transform(mLayout, getPreviousView(), swipeFraction, swipeDirection);
            }
        }

        @Override
        public void onEndSwipe(@FloatRange(from = 0F, to = 1F) float swipeFraction, @NonNull SwipeBackDirection swipeDirection) {
            if (mLayout != null && mTransformer != null && mTranslucentConverter.isTranslucent()) {
                mTransformer.restore(mLayout, getPreviousView());
            }
            mPreviousNode = null;
            if (swipeFraction != 1) {
                if (!mThemeTranslucent) {
                    mTranslucentConverter.fromTranslucent();
                }
            } else {
                mActivity.finish();
                mActivity.overridePendingTransition(0, 0);
            }
        }
    }
}
