package per.goweii.swipeback;

import android.app.Activity;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

public class SwipeBackAbility {

    @NonNull
    static SwipeBackDirection getSwipeBackDirectionForActivity(@NonNull Activity activity) {
        if (activity instanceof Direction) {
            Direction ability = (Direction) activity;
            return ability.swipeBackDirection();
        } else {
            return SwipeBack.getInstance().getSwipeBackDirection();
        }
    }

    public interface Direction {
        @NonNull
        SwipeBackDirection swipeBackDirection();
    }

    @Nullable
    static SwipeBackTransformer getSwipeBackTransformerForActivity(@NonNull Activity activity) {
        if (activity instanceof Transformer) {
            Transformer ability = (Transformer) activity;
            return ability.swipeBackTransformer();
        } else {
            return SwipeBack.getInstance().getSwipeBackTransformer();
        }
    }

    public interface Transformer {
        @Nullable
        SwipeBackTransformer swipeBackTransformer();
    }

    static boolean isSwipeBackOnlyEdgeForActivity(@NonNull Activity activity) {
        if (activity instanceof OnlyEdge) {
            OnlyEdge ability = (OnlyEdge) activity;
            return ability.swipeBackOnlyEdge();
        } else {
            return SwipeBack.getInstance().isSwipeBackOnlyEdge();
        }
    }

    public interface OnlyEdge {
        boolean swipeBackOnlyEdge();
    }

    static boolean isSwipeBackForceEdgeForActivity(@NonNull Activity activity) {
        if (activity instanceof ForceEdge) {
            ForceEdge ability = (ForceEdge) activity;
            return ability.swipeBackForceEdge();
        } else {
            return SwipeBack.getInstance().isSwipeBackForceEdge();
        }
    }

    public interface ForceEdge {
        boolean swipeBackForceEdge();
    }

    @ColorInt
    static int getSwipeBackShadowColorForActivity(@NonNull Activity activity) {
        if (activity instanceof ShadowColor) {
            ShadowColor ability = (ShadowColor) activity;
            return ability.swipeBackShadowColor();
        } else {
            return SwipeBack.getInstance().getSwipeBackShadowColor();
        }
    }

    public interface ShadowColor {
        @ColorInt
        int swipeBackShadowColor();
    }

    @Px
    static int getSwipeBackShadowSizeForActivity(@NonNull Activity activity) {
        if (activity instanceof ShadowSize) {
            ShadowSize ability = (ShadowSize) activity;
            return ability.swipeBackShadowSize();
        } else {
            return SwipeBack.getInstance().getSwipeBackShadowSize();
        }
    }

    public interface ShadowSize {
        @Px
        int swipeBackShadowSize();
    }

    @IntRange(from = 0, to = 255)
    static int getSwipeBackMaskAlphaForActivity(@NonNull Activity activity) {
        if (activity instanceof MaskAlpha) {
            MaskAlpha ability = (MaskAlpha) activity;
            return ability.swipeBackMaskAlpha();
        } else {
            return SwipeBack.getInstance().getSwipeBackMaskAlpha();
        }
    }

    public interface MaskAlpha {
        @IntRange(from = 0, to = 255)
        int swipeBackMaskAlpha();
    }
}
