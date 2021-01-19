package per.goweii.swipeback;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public final class SwipeBack {
    private static final SwipeBack sInstance = new SwipeBack();

    @SwipeBackDirection
    private int mSwipeBackDirection = SwipeBackDirection.NONE;
    @Nullable
    private SwipeBackTransformer mSwipeBackTransformer = null;
    private boolean mSwipeBackOnlyEdge = false;
    private boolean mSwipeBackForceEdge = true;

    private SwipeBack() {
    }

    public static SwipeBack getInstance() {
        return sInstance;
    }

    public void init(@NonNull Application application) {
        SwipeBackManager.init(application);
    }

    public void setSwipeBackDirection(@SwipeBackDirection int swipeBackDirection) {
        this.mSwipeBackDirection = swipeBackDirection;
    }

    @SwipeBackDirection
    public int getSwipeBackDirection() {
        return mSwipeBackDirection;
    }

    public void setSwipeBackTransformer(@Nullable SwipeBackTransformer swipeBackTransformer) {
        this.mSwipeBackTransformer = swipeBackTransformer;
    }

    @Nullable
    public SwipeBackTransformer getSwipeBackTransformer() {
        return mSwipeBackTransformer;
    }

    public void setSwipeBackForceEdge(boolean swipeBackForceEdge) {
        this.mSwipeBackForceEdge = swipeBackForceEdge;
    }

    public void setSwipeBackOnlyEdge(boolean swipeBackOnlyEdge) {
        this.mSwipeBackOnlyEdge = swipeBackOnlyEdge;
    }

    public boolean isSwipeBackForceEdge() {
        return mSwipeBackForceEdge;
    }

    public boolean isSwipeBackOnlyEdge() {
        return mSwipeBackOnlyEdge;
    }
}
