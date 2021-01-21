package per.goweii.swipeback;

import android.app.Application;
import android.graphics.Color;
import android.util.TypedValue;

import androidx.annotation.ColorInt;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.graphics.ColorUtils;

public final class SwipeBack {
    private static final SwipeBack sInstance = new SwipeBack();

    @NonNull
    private SwipeBackDirection mSwipeBackDirection = SwipeBackDirection.NONE;
    @Nullable
    private SwipeBackTransformer mSwipeBackTransformer = null;
    private boolean mSwipeBackOnlyEdge = false;
    private boolean mSwipeBackForceEdge = true;
    private boolean mShadowEnable = true;
    @ColorInt
    private int mShadowColor = ColorUtils.setAlphaComponent(Color.BLACK, 50);
    @Px
    private int mShadowSize = 32;
    @IntRange(from = 0, to = 255)
    private int mMaskAlpha = 150;

    private SwipeBack() {
    }

    public static SwipeBack getInstance() {
        return sInstance;
    }

    public void init(@NonNull Application application) {
        SwipeBackManager.init(application);
        mShadowSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12F, application.getResources().getDisplayMetrics());
    }

    public void setSwipeBackDirection(@NonNull SwipeBackDirection swipeBackDirection) {
        this.mSwipeBackDirection = swipeBackDirection;
    }

    @NonNull
    public SwipeBackDirection getSwipeBackDirection() {
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

    public boolean ismShadowEnable() {
        return mShadowEnable;
    }

    public void setShadowEnable(boolean mShadowEnable) {
        this.mShadowEnable = mShadowEnable;
    }

    @ColorInt
    public int getShadowColor() {
        return mShadowColor;
    }

    public void setShadowStartColor(@ColorInt int shadowColor) {
        this.mShadowColor = shadowColor;
    }

    @Px
    public int getShadowSize() {
        return mShadowSize;
    }

    public void setShadowSize(@Px int shadowSize) {
        this.mShadowSize = shadowSize;
    }

    @IntRange(from = 0, to = 255)
    public int getMaskAlpha() {
        return mMaskAlpha;
    }

    public void setMaskAlpha(@IntRange(from = 0, to = 255) int maskAlpha) {
        this.mMaskAlpha = maskAlpha;
    }
}
