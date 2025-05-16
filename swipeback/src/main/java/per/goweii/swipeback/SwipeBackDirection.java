package per.goweii.swipeback;

import androidx.annotation.IntDef;

/**
 * 滑动返回的方向
 */
@IntDef(value = {
        SwipeBackDirection.TOP,
        SwipeBackDirection.BOTTOM,
        SwipeBackDirection.LEFT,
        SwipeBackDirection.RIGHT
}, flag = true)
public @interface SwipeBackDirection {
    /**
     * 向顶部滑动返回，即从屏幕顶部划出
     */
    int TOP = 1;
    /**
     * 向底部滑动返回，即从屏幕底部划出
     */
    int BOTTOM = 1 << 1;
    /**
     * 向左侧滑动返回，即从屏幕左侧划出
     */
    int LEFT = 1 << 2;
    /**
     * 向右侧滑动返回，即从屏幕右侧划出
     */
    int RIGHT = 1 << 3;
}