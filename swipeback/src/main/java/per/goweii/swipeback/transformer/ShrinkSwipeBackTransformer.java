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

    public ShrinkSwipeBackTransformer() {
        this(0.96F, 1F);
    }

    public ShrinkSwipeBackTransformer(@FloatRange(from = 0, to = 1) float scale,
                                      @FloatRange(from = 0, to = 1) float alpha) {
        mScale = scale;
        mAlpha = alpha;
    }

    @Override
    public void transform(
            @NonNull View currentView,
            @Nullable View previousView,
            @FloatRange(from = 0.0, to = 1.0) float fraction,
            @SwipeBackDirection int swipeDirection
    ) {
        if (previousView == null) {
            return;
        }
        float scale = mScale + (1 - mScale) * fraction;
        previousView.setScaleX(scale);
        previousView.setScaleY(scale);
        float alpha = mAlpha + (1 - mAlpha) * fraction;
        previousView.setAlpha(alpha);
    }
}
