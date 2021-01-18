package per.goweii.swipeback;

import androidx.annotation.IntDef;

@IntDef({
        SwipeBackDirection.NONE,
        SwipeBackDirection.LEFT,
        SwipeBackDirection.TOP,
        SwipeBackDirection.RIGHT,
        SwipeBackDirection.BOTTOM
})
public @interface SwipeBackDirection {
    int NONE = 0;
    int LEFT = 1;
    int TOP = 2;
    int RIGHT = 3;
    int BOTTOM = 4;
}