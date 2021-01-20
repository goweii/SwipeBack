package per.goweii.swipeback.transformer;

import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import per.goweii.swipeback.SwipeBackDirection;
import per.goweii.swipeback.SwipeBackTransformer;
import per.goweii.swipeback.utils.Utils;

public class AvoidStatusBarSwipeBackTransformer implements SwipeBackTransformer {
    private float mScale = 1F;

    public AvoidStatusBarSwipeBackTransformer() {
    }

    @Override
    public void transform(
            @NonNull View currentView,
            @Nullable View previousView,
            @FloatRange(from = 0.0, to = 1.0) float fraction,
            @SwipeBackDirection int swipeDirection
    ) {
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
    }

    @Override
    public void restore(
            @NonNull View currentView,
            @Nullable View previousView,
            @FloatRange(from = 0.0, to = 1.0) float fraction,
            @SwipeBackDirection int swipeDirection
    ) {
        if (previousView == null) return;
        previousView.setScaleX(1F);
        previousView.setScaleY(1F);
    }
}
