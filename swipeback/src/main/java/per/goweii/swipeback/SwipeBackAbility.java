package per.goweii.swipeback;

import android.app.Activity;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;

/**
 * 用于自定义单个Activity的滑动返回效果
 * <p>
 * 如果需要效果全局生效请使用{@link SwipeBack}配置
 * <p>
 * 例如：
 * SwipeBackActivity extends AppCompatActivity implements SwipeBackAbility.Direction {
 *     public SwipeBackDirection swipeBackDirection() {
 *         return mSwipeBackDirection;
 *     }
 * }
 */
public class SwipeBackAbility {
    /**
     * 控制滑动返回的方向{@link SwipeBackDirection}
     * 其中返回{@link SwipeBackDirection#NONE}为禁用滑动返回
     */
    public interface Direction {
        @NonNull
        SwipeBackDirection swipeBackDirection();
    }

    /**
     * 自定义底部Activity联动效果
     */
    public interface Transformer {
        @Nullable
        SwipeBackTransformer swipeBackTransformer();
    }

    public interface OnlyEdge {
        boolean swipeBackOnlyEdge();
    }

    public interface ForceEdge {
        boolean swipeBackForceEdge();
    }

    public interface ShadowColor {
        @ColorInt
        int swipeBackShadowColor();
    }

    public interface ShadowSize {
        @Px
        int swipeBackShadowSize();
    }

    public interface MaskAlpha {
        @IntRange(from = 0, to = 255)
        int swipeBackMaskAlpha();
    }

    @NonNull
    static SwipeBackDirection getSwipeBackDirectionForActivity(@NonNull Activity activity) {
        if (activity instanceof Direction) {
            Direction ability = (Direction) activity;
            return ability.swipeBackDirection();
        } else {
            return SwipeBack.getInstance().getSwipeBackDirection();
        }
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

    static boolean isSwipeBackOnlyEdgeForActivity(@NonNull Activity activity) {
        if (activity instanceof OnlyEdge) {
            OnlyEdge ability = (OnlyEdge) activity;
            return ability.swipeBackOnlyEdge();
        } else {
            return SwipeBack.getInstance().isSwipeBackOnlyEdge();
        }
    }

    static boolean isSwipeBackForceEdgeForActivity(@NonNull Activity activity) {
        if (activity instanceof ForceEdge) {
            ForceEdge ability = (ForceEdge) activity;
            return ability.swipeBackForceEdge();
        } else {
            return SwipeBack.getInstance().isSwipeBackForceEdge();
        }
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

    @Px
    static int getSwipeBackShadowSizeForActivity(@NonNull Activity activity) {
        if (activity instanceof ShadowSize) {
            ShadowSize ability = (ShadowSize) activity;
            return ability.swipeBackShadowSize();
        } else {
            return SwipeBack.getInstance().getSwipeBackShadowSize();
        }
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
}
