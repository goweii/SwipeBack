package per.goweii.swipeback;

import androidx.annotation.IntDef;

@IntDef({
        SwipeBackDirection.LEFT,
        SwipeBackDirection.TOP,
        SwipeBackDirection.RIGHT,
        SwipeBackDirection.BOTTOM
})
public @interface SwipeBackDirection {
    int LEFT = SwipeBackLayout.DIRECTION_LEFT;
    int TOP = SwipeBackLayout.DIRECTION_TOP;
    int RIGHT = SwipeBackLayout.DIRECTION_RIGHT;
    int BOTTOM = SwipeBackLayout.DIRECTION_BOTTOM;
}