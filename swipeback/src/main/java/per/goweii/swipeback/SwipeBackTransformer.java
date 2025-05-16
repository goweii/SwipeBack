package per.goweii.swipeback;

import android.view.View;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * 使当前Activity与下层Activity产生联动效果
 */
public interface SwipeBackTransformer {
    /**
     * 刚开始滑动时，即fraction为0时
     *
     * @param currentView  当前Activity的View
     * @param previousView 下层需要联动的Activity的View
     */
    void initialize(
            @NonNull View currentView,
            @Nullable View previousView
    );

    /**
     * 当faction变化时会回调这个方法，在这里可以控制下层view联动
     *
     * @param currentView    当前Activity的View
     * @param previousView   下层需要联动的Activity的View
     * @param fraction       滑动百分比，为0-1的闭区间[0, 1]，0为未滑动，1为完全划出
     * @param swipeDirection 滑动方向
     */
    void transform(
            @NonNull View currentView,
            @Nullable View previousView,
            @FloatRange(from = 0.0, to = 1.0) float fraction,
            @SwipeBackDirection int swipeDirection
    );

    /**
     * 滑动结束时，即fraction为0或者1时
     * <p>
     * 应该在这里恢复状态
     *
     * @param currentView  当前Activity的View
     * @param previousView 下层需要联动的Activity的View
     */
    void restore(
            @NonNull View currentView,
            @Nullable View previousView
    );
}