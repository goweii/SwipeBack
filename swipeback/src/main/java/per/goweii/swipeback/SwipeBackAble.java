package per.goweii.swipeback;

import androidx.annotation.Nullable;

public interface SwipeBackAble {
    @SwipeBackDirection
    int swipeBackDirection();

    @Nullable
    SwipeBackTransformer swipeBackTransformer();

    boolean swipeBackOnlyEdge();

    boolean swipeBackForceEdge();
}
