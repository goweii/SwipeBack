package per.goweii.swipeback;

import androidx.annotation.IntDef;

@IntDef({
        SwipeBackDirection.NONE,
        SwipeBackDirection.RIGHT,
        SwipeBackDirection.BOTTOM,
        SwipeBackDirection.LEFT,
        SwipeBackDirection.TOP
})
public @interface SwipeBackDirection {
    int NONE = 0;
    int RIGHT = 1;
    int BOTTOM = 2;
    int LEFT = 3;
    int TOP = 4;
}