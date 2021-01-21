package per.goweii.swipeback;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public interface SwipeBackAble {
    @NonNull
    SwipeBackDirection swipeBackDirection();

    @Nullable
    SwipeBackTransformer swipeBackTransformer();

    boolean swipeBackOnlyEdge();

    boolean swipeBackForceEdge();
}
