package per.goweii.swipeback.transformer;

import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import per.goweii.swipeback.SwipeBackDirection;
import per.goweii.swipeback.SwipeBackTransformer;

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
    public void transform(
            @NonNull View currentView,
            @Nullable View previousView,
            @FloatRange(from = 0.0, to = 1.0) float fraction,
            @SwipeBackDirection int swipeDirection
    ) {
        if (previousView == null) {
            return;
        }
        int maxTranslation = previousView.getWidth();
        if (swipeDirection == SwipeBackDirection.LEFT) {
            float translation = previousView.getWidth() * mPercent * (1 - fraction);
            previousView.setTranslationX(translation);
        } else if (swipeDirection == SwipeBackDirection.RIGHT) {
            float translation = (maxTranslation * mPercent) * (1 - fraction);
            previousView.setTranslationX(-translation);
        } else if (swipeDirection == SwipeBackDirection.TOP) {
            float translation = maxTranslation * mPercent * (1 - fraction);
            previousView.setTranslationY(translation);
        } else if (swipeDirection == SwipeBackDirection.BOTTOM) {
            float translation = maxTranslation * mPercent * (1 - fraction);
            previousView.setTranslationY(-translation);
        }
        float alpha = mAlpha + (1 - mAlpha) * fraction;
        previousView.setAlpha(alpha);
    }
}
