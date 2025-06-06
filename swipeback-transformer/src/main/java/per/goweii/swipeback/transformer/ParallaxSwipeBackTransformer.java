package per.goweii.swipeback.transformer;

import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import per.goweii.swipeback.SwipeBackDirection;
import per.goweii.swipeback.SwipeBackTransformer;

/**
 * 视差联动效果，类似微信
 */
public class ParallaxSwipeBackTransformer implements SwipeBackTransformer {
    private final float mPercent;
    private final float mAlpha;

    public ParallaxSwipeBackTransformer() {
        this(0.12F, 1F);
    }

    public ParallaxSwipeBackTransformer(@FloatRange(from = 0, to = 1) float percent,
                                        @FloatRange(from = 0, to = 1) float alpha) {
        mPercent = percent;
        mAlpha = alpha;
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
            @SwipeBackDirection int direction
    ) {
        if (previousView == null) return;
        switch (direction) {
            case SwipeBackDirection.RIGHT: {
                float translation = previousView.getWidth() * mPercent * (1 - fraction);
                previousView.setTranslationX(-translation);
                previousView.setTranslationY(0);
                break;
            }
            case SwipeBackDirection.LEFT: {
                float translation = (previousView.getWidth() * mPercent) * (1 - fraction);
                previousView.setTranslationX(translation);
                previousView.setTranslationY(0);
                break;
            }
            case SwipeBackDirection.BOTTOM: {
                float translation = previousView.getWidth() * mPercent * (1 - fraction);
                previousView.setTranslationY(-translation);
                previousView.setTranslationX(0);
                break;
            }
            case SwipeBackDirection.TOP: {
                float translation = previousView.getWidth() * mPercent * (1 - fraction);
                previousView.setTranslationY(translation);
                previousView.setTranslationX(0);
                break;
            }
        }
        float alpha = mAlpha + (1 - mAlpha) * fraction;
        previousView.setAlpha(alpha);
    }

    @Override
    public void restore(
            @NonNull View currentView,
            @Nullable View previousView
    ) {
        if (previousView == null) return;
        if (previousView.getWidth() <= 0) return;
        if (previousView.getHeight() <= 0) return;
        previousView.setTranslationX(0);
        previousView.setTranslationY(0);
        previousView.setAlpha(1);
    }
}
