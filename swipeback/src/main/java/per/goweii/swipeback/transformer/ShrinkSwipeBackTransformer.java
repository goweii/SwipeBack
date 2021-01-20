package per.goweii.swipeback.transformer;

import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import per.goweii.swipeback.SwipeBackDirection;
import per.goweii.swipeback.SwipeBackTransformer;

public class ShrinkSwipeBackTransformer implements SwipeBackTransformer {
    private final float mScale;
    private final float mAlpha;
    private final float mCenterX;
    private final float mCenterY;

    public ShrinkSwipeBackTransformer() {
        this(0.96F, 1F, 0.5F, 0.5F);
    }

    public ShrinkSwipeBackTransformer(@FloatRange(from = 0, to = 1) float scale,
                                      @FloatRange(from = 0, to = 1) float alpha,
                                      @FloatRange(from = 0, to = 1) float centerX,
                                      @FloatRange(from = 0, to = 1) float centerY) {
        mScale = scale;
        mAlpha = alpha;
        mCenterX = centerX;
        mCenterY = centerY;
    }

    @Override
    public void initialize(
            @NonNull View currentView,
            @Nullable final View previousView
    ) {
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
        previousView.setPivotX(previousView.getWidth() * mCenterX);
        previousView.setPivotY(previousView.getHeight() * mCenterY);
        float scale = mScale + (1 - mScale) * fraction;
        previousView.setScaleX(scale);
        previousView.setScaleY(scale);
        float alpha = mAlpha + (1 - mAlpha) * fraction;
        previousView.setAlpha(alpha);
    }

    @Override
    public void restore(
            @NonNull View currentView,
            @Nullable View previousView
    ) {
        if (previousView == null) return;
        previousView.setScaleX(1);
        previousView.setScaleY(1);
        previousView.setAlpha(1);
    }
}
