package per.goweii.android.swipeback;

import androidx.annotation.NonNull;

import per.goweii.swipeback.SwipeBackAble;
import per.goweii.swipeback.SwipeBackDirection;

public class MainActivity extends BaseSwipeBackActivity implements SwipeBackAble {
    @NonNull
    @Override
    public SwipeBackDirection swipeBackDirection() {
        return SwipeBackDirection.NONE;
    }
}
