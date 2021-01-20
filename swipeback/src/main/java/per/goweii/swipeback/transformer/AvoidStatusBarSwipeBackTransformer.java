package per.goweii.swipeback.transformer;

import android.graphics.Outline;
import android.os.Build;
import android.view.View;
import android.view.ViewOutlineProvider;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;

import per.goweii.swipeback.SwipeBackDirection;
import per.goweii.swipeback.SwipeBackTransformer;
import per.goweii.swipeback.utils.Utils;

public class AvoidStatusBarSwipeBackTransformer implements SwipeBackTransformer {
    private float mScale = 1F;
    private float mFraction = 0F;

    private boolean mOldClipToOutline = false;
    private ViewOutlineProvider mOldViewOutlineProvider = null;

    public AvoidStatusBarSwipeBackTransformer() {
    }

    @Override
    public void initialize(
            @NonNull View currentView,
            @Nullable final View previousView
    ) {
        mFraction = 0;
        if (previousView == null) return;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mOldViewOutlineProvider = previousView.getOutlineProvider();
            previousView.setOutlineProvider(new ViewOutlineProvider() {
                @Override
                public void getOutline(View view, Outline outline) {
                    outline.setRoundRect(0, 0, view.getWidth(), view.getHeight(), (1 - mFraction) * 50);
                }
            });
            mOldClipToOutline = previousView.getClipToOutline();
            previousView.setClipToOutline(true);
        }
    }

    @Override
    public void transform(
            @NonNull View currentView,
            @Nullable final View previousView,
            @FloatRange(from = 0.0, to = 1.0) final float fraction,
            @SwipeBackDirection int swipeDirection
    ) {
        mFraction = fraction;
        if (previousView == null) return;
        if (previousView.getWidth() <= 0) return;
        if (previousView.getHeight() <= 0) return;
        if (mScale == 1) {
            int statusBarHeight = Utils.getStatusBarHeight(previousView.getContext());
            int endHeight = previousView.getHeight() - statusBarHeight;
            mScale = 1F * endHeight / previousView.getHeight();
        }
        previousView.setPivotX(previousView.getWidth() * 0.5F);
        previousView.setPivotY(previousView.getHeight());
        float scale = mScale + (1 - mScale) * fraction;
        previousView.setScaleX(scale);
        previousView.setScaleY(scale);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            previousView.invalidateOutline();
        }
    }

    @Override
    public void restore(
            @NonNull View currentView,
            @Nullable View previousView
    ) {
        mFraction = 0;
        if (previousView == null) return;
        previousView.setScaleX(1F);
        previousView.setScaleY(1F);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            previousView.setClipToOutline(mOldClipToOutline);
            mOldClipToOutline = false;
            previousView.setOutlineProvider(mOldViewOutlineProvider);
            mOldViewOutlineProvider = null;
            previousView.invalidateOutline();
        }
    }
}
