package per.goweii.swipeback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Px;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import per.goweii.swipeback.utils.ScrollCompat;

import static per.goweii.swipeback.SwipeBackDirection.BOTTOM;
import static per.goweii.swipeback.SwipeBackDirection.LEFT;
import static per.goweii.swipeback.SwipeBackDirection.NONE;
import static per.goweii.swipeback.SwipeBackDirection.RIGHT;
import static per.goweii.swipeback.SwipeBackDirection.TOP;

public class SwipeBackLayout extends FrameLayout {
    private final ViewDragHelper mDragHelper;
    private final Rect mShadowRect = new Rect();

    private boolean mSwiping = false;
    private float mFraction = 0;
    private float mDownX = 0;
    private float mDownY = 0;
    private GradientDrawable mShadowDrawable = null;

    @ColorInt
    private int mShadowColor = Color.TRANSPARENT;
    @Px
    private int mShadowSize = 0;
    @IntRange(from = 0, to = 255)
    private int mMaskAlpha = 150;
    @NonNull
    private SwipeBackDirection mSwipeBackDirection = NONE;
    private boolean mSwipeBackForceEdge = true;
    private boolean mSwipeBackOnlyEdge = false;
    private float mSwipeBackFactor = 0.5f;
    private float mSwipeBackVelocity = 2000f;

    private boolean mShouldIntercept = false;
    private boolean mCheckedIntercept = false;

    private SwipeBackListener mSwipeBackListener;

    public SwipeBackLayout(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        mDragHelper.setMinVelocity(mSwipeBackVelocity);
        setEdgeTrackingEnabledByDirection();
    }

    public boolean isSwipeBackEnable() {
        return mSwipeBackDirection != NONE;
    }

    public void setSwipeBackForceEdge(boolean enable) {
        mSwipeBackForceEdge = enable;
    }

    public boolean isSwipeBackForceEdge() {
        return mSwipeBackForceEdge;
    }

    public void setSwipeBackFactor(@FloatRange(from = 0.0f, to = 1.0f) float swipeBackFactor) {
        if (swipeBackFactor > 1) {
            mSwipeBackFactor = 1;
        } else if (swipeBackFactor < 0) {
            mSwipeBackFactor = 0;
        } else {
            mSwipeBackFactor = swipeBackFactor;
        }
    }

    public float getSwipeBackFactor() {
        return mSwipeBackFactor;
    }

    public boolean isShadowEnable() {
        return mShadowSize > 0 && mShadowColor != Color.TRANSPARENT;
    }

    public void setShadowColor(@ColorInt int colorInt) {
        mShadowColor = colorInt;
    }

    public void setShadowSize(@Px int px) {
        mShadowSize = px;
    }

    public void setMaskAlpha(@IntRange(from = 0, to = 255) int maskAlpha) {
        if (maskAlpha > 255) {
            maskAlpha = 255;
        } else if (maskAlpha < 0) {
            maskAlpha = 0;
        }
        this.mMaskAlpha = maskAlpha;
    }

    public void setMaskAlpha(@FloatRange(from = 0, to = 1) float maskAlpha) {
        setMaskAlpha((int) (255 * maskAlpha));
    }

    public int getMaskAlpha() {
        return mMaskAlpha;
    }

    public void setSwipeBackDirection(@NonNull SwipeBackDirection direction) {
        if (mSwipeBackDirection == direction) {
            return;
        }
        mSwipeBackDirection = direction;
        setEdgeTrackingEnabledByDirection();
        mShadowDrawable = null;
    }

    private void setEdgeTrackingEnabledByDirection() {
        switch (mSwipeBackDirection) {
            case BOTTOM:
                mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_TOP);
                break;
            case LEFT:
                mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_RIGHT);
                break;
            case RIGHT:
                mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_LEFT);
                break;
            case TOP:
                mDragHelper.setEdgeTrackingEnabled(ViewDragHelper.EDGE_BOTTOM);
                break;
            case NONE:
                mDragHelper.setEdgeTrackingEnabled(0);
                break;
        }
    }

    @NonNull
    public SwipeBackDirection getSwipeBackDirection() {
        return mSwipeBackDirection;
    }

    public float getSwipeBackVelocity() {
        return mSwipeBackVelocity;
    }

    public void setSwipeBackVelocity(@FloatRange(from = 0.0f) float swipeBackVelocity) {
        this.mSwipeBackVelocity = swipeBackVelocity;
        mDragHelper.setMinVelocity(mSwipeBackVelocity);
    }

    public boolean isSwipeBackOnlyEdge() {
        return mSwipeBackOnlyEdge;
    }

    public void setSwipeBackOnlyEdge(boolean swipeBackOnlyEdge) {
        this.mSwipeBackOnlyEdge = swipeBackOnlyEdge;
    }

    public boolean isSwiping() {
        return mSwiping;
    }

    public void setSwipeBackListener(SwipeBackListener swipeBackListener) {
        this.mSwipeBackListener = swipeBackListener;
    }

    public boolean canSwipeBack() {
        if (mSwipeBackOnlyEdge) {
            return isDirectionEdgeTouched();
        } else {
            return isSwipeBackEnable();
        }
    }

    public boolean isDirectionEdgeTouched() {
        switch (mSwipeBackDirection) {
            case BOTTOM:
                return mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_TOP);
            case LEFT:
                return mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_RIGHT);
            case RIGHT:
                return mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_LEFT);
            case TOP:
                return mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_BOTTOM);
            case NONE:
                return false;
        }
        return false;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getActionMasked() == MotionEvent.ACTION_DOWN) {
            mCheckedIntercept = false;
            mShouldIntercept = false;
            beforeSwipe();
        }
        if (!isSwipeBackEnable()) {
            return super.dispatchTouchEvent(ev);
        }
        float x = ev.getRawX();
        float y = ev.getRawY();
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mCheckedIntercept) {
                    if (mSwipeBackForceEdge && isDirectionEdgeTouched()) {
                        mCheckedIntercept = true;
                        mShouldIntercept = true;
                    } else {
                        float dx = x - mDownX;
                        float dy = y - mDownY;
                        if (Math.abs(dx) > mDragHelper.getTouchSlop() || Math.abs(dy) > mDragHelper.getTouchSlop()) {
                            mCheckedIntercept = true;
                            if (Math.abs(dx) > Math.abs(dy)) {
                                if (dx > 0) {
                                    if (mSwipeBackDirection == RIGHT) {
                                        mShouldIntercept = !ScrollCompat.hasViewCanScrollLeft(this, mDownX, mDownY);
                                    }
                                } else {
                                    if (mSwipeBackDirection == LEFT) {
                                        mShouldIntercept = !ScrollCompat.hasViewCanScrollRight(this, mDownX, mDownY);
                                    }
                                }
                            } else {
                                if (dy > 0) {
                                    if (mSwipeBackDirection == BOTTOM) {
                                        mShouldIntercept = !ScrollCompat.hasViewCanScrollUp(this, mDownX, mDownY);
                                    }
                                } else {
                                    if (mSwipeBackDirection == TOP) {
                                        mShouldIntercept = !ScrollCompat.hasViewCanScrollDown(this, mDownX, mDownY);
                                    }
                                }
                            }
                        }
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mCheckedIntercept = false;
                mShouldIntercept = false;
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isSwipeBackEnable()) return false;
        if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
            if (mCheckedIntercept) {
                if (mShouldIntercept) {
                    return mDragHelper.shouldInterceptTouchEvent(ev);
                }
            }
            return false;
        }
        return mDragHelper.shouldInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (!isSwipeBackEnable()) return false;
        if (ev.getActionMasked() == MotionEvent.ACTION_MOVE) {
            if (mCheckedIntercept) {
                if (mShouldIntercept) {
                    mDragHelper.processTouchEvent(ev);
                    return true;
                }
            }
            return false;
        }
        mDragHelper.processTouchEvent(ev);
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
        View capturedView = mDragHelper.getCapturedView();
        if (capturedView != null) {
            mFraction = Math.max(0F, mFraction);
            mFraction = Math.min(1F, mFraction);
            int offsetLeft = 0, offsetTop = 0;
            switch (mSwipeBackDirection) {
                case RIGHT:
                case LEFT:
                    offsetLeft = (int) (mFraction * (getWidth() + mShadowSize));
                    break;
                case BOTTOM:
                case TOP:
                    offsetTop = (int) (mFraction * (getHeight() + mShadowSize));
                    break;
                case NONE:
                    break;
            }
            if (offsetLeft != 0) {
                capturedView.offsetLeftAndRight(offsetLeft);
            }
            if (offsetTop != 0) {
                capturedView.offsetTopAndBottom(offsetTop);
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSwipeBackEnable()) {
            canvas.drawARGB(mMaskAlpha - (int) (mMaskAlpha * mFraction), 0, 0, 0);
        }
    }

    @Override
    protected boolean drawChild(Canvas canvas, View child, long drawingTime) {
        boolean ret = super.drawChild(canvas, child, drawingTime);
        if (isSwipeBackEnable() && isShadowEnable()) {
            if (child == getChildAt(0)) {
                drawShadow(canvas, child);
            }
        }
        return ret;
    }

    private void drawShadow(Canvas canvas, View child) {
        final Rect childRect = mShadowRect;
        child.getHitRect(childRect);
        final Drawable shadow = getNonNullShadowDrawable();
        if (mSwipeBackDirection == RIGHT) {
            shadow.setBounds(childRect.left - shadow.getIntrinsicWidth(), childRect.top, childRect.left, childRect.bottom);
        } else if (mSwipeBackDirection == LEFT) {
            shadow.setBounds(childRect.right, childRect.top, childRect.right + shadow.getIntrinsicWidth(), childRect.bottom);
        } else if (mSwipeBackDirection == BOTTOM) {
            shadow.setBounds(childRect.left, childRect.top - shadow.getIntrinsicHeight(), childRect.right, childRect.top);
        } else if (mSwipeBackDirection == TOP) {
            shadow.setBounds(childRect.left, childRect.bottom, childRect.right, childRect.bottom + shadow.getIntrinsicHeight());
        }
        //mShadowDrawable.setAlpha((int) ((1 - mFraction) * 255));
        mShadowDrawable.draw(canvas);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private boolean shouldBackBySpeed(float xvel, float yvel) {
        switch (mSwipeBackDirection) {
            case RIGHT:
                return xvel > mSwipeBackVelocity;
            case BOTTOM:
                return yvel > mSwipeBackVelocity;
            case LEFT:
                return xvel < -mSwipeBackVelocity;
            case TOP:
                return yvel < -mSwipeBackVelocity;
            case NONE:
                break;
        }
        return false;
    }

    private void smoothScrollToX(int finalLeft) {
        if (mDragHelper.settleCapturedViewAt(finalLeft, getPaddingTop())) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void smoothScrollToY(int finalTop) {
        if (mDragHelper.settleCapturedViewAt(getPaddingLeft(), finalTop)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private void refreshFraction(@NonNull View view) {
        switch (mSwipeBackDirection) {
            case RIGHT:
            case LEFT:
                mFraction = 1F * Math.abs(view.getLeft()) / (getWidth() + mShadowSize);
                break;
            case BOTTOM:
            case TOP:
                mFraction = 1F * Math.abs(view.getTop()) / (getHeight() + mShadowSize);
                break;
            case NONE:
                break;
        }
        mFraction = Math.max(0F, mFraction);
        mFraction = Math.min(1F, mFraction);
    }

    @NonNull
    private GradientDrawable getNonNullShadowDrawable() {
        if (mShadowDrawable == null) {
            int[] colors = new int[]{
                    mShadowColor,
                    ColorUtils.setAlphaComponent(mShadowColor, (int) (Color.alpha(mShadowColor) * 0.3)),
                    Color.TRANSPARENT
            };
            if (mSwipeBackDirection == RIGHT) {
                mShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors);
                mShadowDrawable.setSize(mShadowSize, 0);
            } else if (mSwipeBackDirection == LEFT) {
                mShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
                mShadowDrawable.setSize(mShadowSize, 0);
            } else if (mSwipeBackDirection == BOTTOM) {
                mShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors);
                mShadowDrawable.setSize(0, mShadowSize);
            } else if (mSwipeBackDirection == TOP) {
                mShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
                mShadowDrawable.setSize(0, mShadowSize);
            } else {
                mShadowDrawable = new GradientDrawable();
                mShadowDrawable.setSize(0, 0);
            }
        }
        return mShadowDrawable;
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {
        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return canSwipeBack();
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            refreshFraction(capturedChild);
            onSwipeStart();
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return isSwipeBackEnable() ? (getWidth() + mShadowSize) : 0;
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return isSwipeBackEnable() ? (getHeight() + mShadowSize) : 0;
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            int newLeft = 0;
            if (mSwipeBackDirection == RIGHT) {
                if (mSwipeBackOnlyEdge) {
                    if (mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_LEFT)) {
                        newLeft = Math.min(Math.max(left, 0), getWidth() + mShadowSize);
                    }
                } else {
                    newLeft = Math.min(Math.max(left, 0), getWidth() + mShadowSize);
                }
            } else if (mSwipeBackDirection == LEFT) {
                if (mSwipeBackOnlyEdge) {
                    if (mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_RIGHT)) {
                        newLeft = Math.min(Math.max(left, -getWidth() - mShadowSize), 0);
                    }
                } else {
                    newLeft = Math.min(Math.max(left, -getWidth() - mShadowSize), 0);
                }
            }
            return newLeft;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            int newTop = 0;
            if (mSwipeBackDirection == BOTTOM) {
                if (mSwipeBackOnlyEdge) {
                    if (mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_TOP)) {
                        newTop = Math.min(Math.max(top, 0), getHeight() + mShadowSize);
                    }
                } else {
                    newTop = Math.min(Math.max(top, 0), getHeight() + mShadowSize);
                }
            } else if (mSwipeBackDirection == TOP) {
                if (mSwipeBackOnlyEdge) {
                    if (mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_BOTTOM)) {
                        newTop = Math.min(Math.max(top, -getHeight() - mShadowSize), 0);
                    }
                } else {
                    newTop = Math.min(Math.max(top, -getHeight() - mShadowSize), 0);
                }
            }
            return newTop;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            refreshFraction(changedView);
            onSwiping();
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            if (!isSwipeBackEnable()) return;
            if (!canSwipeBack()) return;
            boolean isBackToEnd = shouldBackBySpeed(xvel, yvel) || mFraction >= mSwipeBackFactor;
            if (isBackToEnd) {
                switch (mSwipeBackDirection) {
                    case RIGHT:
                        smoothScrollToX(getWidth() + mShadowSize);
                        break;
                    case BOTTOM:
                        smoothScrollToY(getHeight() + mShadowSize);
                        break;
                    case LEFT:
                        smoothScrollToX(-getWidth() - mShadowSize);
                        break;
                    case TOP:
                        smoothScrollToY(-getHeight() - mShadowSize);
                        break;
                    case NONE:
                        break;
                }
            } else {
                switch (mSwipeBackDirection) {
                    case RIGHT:
                    case LEFT:
                        smoothScrollToX(0);
                        break;
                    case TOP:
                    case BOTTOM:
                        smoothScrollToY(0);
                        break;
                    case NONE:
                        break;
                }
            }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (state == ViewDragHelper.STATE_IDLE) {
                if (mFraction == 0) {
                    onSwipeEnd();
                } else if (mFraction == 1) {
                    onSwipeEnd();
                }
            }
        }
    }

    protected void beforeSwipe() {
        if (mSwipeBackListener != null) {
            mSwipeBackListener.onBeforeSwipe(mFraction, mSwipeBackDirection);
        }
    }

    protected void onSwipeStart() {
        mSwiping = true;
        if (mSwipeBackListener != null) {
            mSwipeBackListener.onStartSwipe(mFraction, mSwipeBackDirection);
        }
    }

    protected void onSwiping() {
        invalidate();
        if (mSwipeBackListener != null) {
            mSwipeBackListener.onSwiping(mFraction, mSwipeBackDirection);
        }
    }

    protected void onSwipeEnd() {
        mSwiping = false;
        if (mSwipeBackListener != null) {
            mSwipeBackListener.onEndSwipe(mFraction, mSwipeBackDirection);
        }
    }

    public interface SwipeBackListener {
        void onBeforeSwipe(float swipeFraction, @NonNull SwipeBackDirection swipeDirection);

        void onStartSwipe(float swipeFraction, @NonNull SwipeBackDirection swipeDirection);

        void onSwiping(float swipeFraction, @NonNull SwipeBackDirection swipeDirection);

        void onEndSwipe(float swipeFraction, @NonNull SwipeBackDirection swipeDirection);
    }
}
