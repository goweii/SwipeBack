package per.goweii.swipeback;

import android.app.Application;

import androidx.annotation.NonNull;

public final class SwipeBack {
    private static final SwipeBack sInstance = new SwipeBack();

    private int mSwipeBackDirection = 0;
    private SwipeBackTransformer mSwipeBackTransformer = null;

    private SwipeBack() {}

    public static SwipeBack getInstance() {
        return sInstance;
    }

    public void init(@NonNull Application application) {
        SwipeBackManager.init(application);
    }

    public void setSwipeBackDirection(int swipeBackDirection) {
        this.mSwipeBackDirection = swipeBackDirection;
    }

    public int getSwipeBackDirection() {
        return mSwipeBackDirection;
    }

    public void setSwipeBackTransformer(SwipeBackTransformer swipeBackTransformer) {
        this.mSwipeBackTransformer = swipeBackTransformer;
    }

    public SwipeBackTransformer getSwipeBackTransformer() {
        return mSwipeBackTransformer;
    }
}
