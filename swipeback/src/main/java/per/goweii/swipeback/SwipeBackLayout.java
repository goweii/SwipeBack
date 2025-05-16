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
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.core.graphics.ColorUtils;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import per.goweii.swipeback.utils.ScrollCompat;

public class SwipeBackLayout extends FrameLayout {
    private final ViewDragHelper mDragHelper;
    private final Rect mShadowRect = new Rect();

    @ColorInt
    private int mShadowColor = Color.TRANSPARENT;
    @Px
    private int mShadowSize = 0;
    @IntRange(from = 0, to = 255)
    private int mMaskAlpha = 150;
    @SwipeBackDirection
    private int mSwipeBackDirection = 0;
    private boolean mSwipeBackForceEdge = true;
    private boolean mSwipeBackOnlyEdge = false;
    private float mSwipeBackFactor = 0.5f;
    private float mSwipeBackVelocity = 2000f;

    private boolean mCheckedIntercept = false;
    private boolean mShouldIntercept = false;
    private boolean mSwiping = false;
    @SwipeBackDirection
    private int mDirection = 0;
    private float mFraction = 0;
    private float mDownX = 0;
    private float mDownY = 0;
    private Map<Integer, GradientDrawable> mShadowDrawables = null;

    private ScrollCompat.ScrollDirectionResult mScrollDirectionResult;

    private SwipeBackListener mSwipeBackListener;

    public SwipeBackLayout(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        mDragHelper.setMinVelocity(mSwipeBackVelocity);
        setEdgeTrackingEnabledByDirection();
    }

    public boolean isSwipeBackEnable() {
        return mSwipeBackDirection != 0;
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

    public void setSwipeBackDirection(@SwipeBackDirection int direction) {
        if (mSwipeBackDirection == direction) {
            return;
        }
        mSwipeBackDirection = direction;
        setEdgeTrackingEnabledByDirection();
        if (mShadowDrawables != null) {
            Iterator<Integer> iterator = mShadowDrawables.keySet().iterator();
            while (iterator.hasNext()) {
                int d = iterator.next();
                if ((d & direction) == 0) {
                    iterator.remove();
                }
            }
        }
    }

    private void setEdgeTrackingEnabledByDirection() {
        int edgeFlags = 0;
        if (hasDirection(SwipeBackDirection.BOTTOM)) {
            edgeFlags |= ViewDragHelper.EDGE_TOP;
        }
        if (hasDirection(SwipeBackDirection.LEFT)) {
            edgeFlags |= ViewDragHelper.EDGE_RIGHT;
        }
        if (hasDirection(SwipeBackDirection.RIGHT)) {
            edgeFlags |= ViewDragHelper.EDGE_LEFT;
        }
        if (hasDirection(SwipeBackDirection.TOP)) {
            edgeFlags |= ViewDragHelper.EDGE_BOTTOM;
        }
        mDragHelper.setEdgeTrackingEnabled(edgeFlags);
    }

    @SwipeBackDirection
    public int getSwipeBackDirection() {
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

    private boolean canSwipeBack() {
        if (mSwipeBackOnlyEdge) {
            return getDirectionByTouchedEdge() != 0;
        } else {
            return isSwipeBackEnable();
        }
    }

    @SwipeBackDirection
    private int getDirectionByTouchedEdge() {
        if (hasDirection(SwipeBackDirection.BOTTOM) && mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_TOP)) {
            return SwipeBackDirection.BOTTOM;
        }
        if (hasDirection(SwipeBackDirection.LEFT) && mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_RIGHT)) {
            return SwipeBackDirection.LEFT;
        }
        if (hasDirection(SwipeBackDirection.RIGHT) && mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_LEFT)) {
            return SwipeBackDirection.RIGHT;
        }
        if (hasDirection(SwipeBackDirection.TOP) && mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_BOTTOM)) {
            return SwipeBackDirection.TOP;
        }
        return 0;
    }

    @Override
    public boolean dispatchTouchEvent(@NonNull MotionEvent ev) {
        final int actionMasked = ev.getActionMasked();
        if (actionMasked == MotionEvent.ACTION_DOWN) {
            mCheckedIntercept = false;
            mShouldIntercept = false;
            mDirection = 0;
            beforeSwipe();
        }
        if (!isSwipeBackEnable()) {
            return super.dispatchTouchEvent(ev);
        }
        float x = ev.getRawX();
        float y = ev.getRawY();
        switch (actionMasked) {
            case MotionEvent.ACTION_DOWN:
                mDownX = x;
                mDownY = y;
                mScrollDirectionResult = ScrollCompat.calcScrollDirection(this, x, y);
                break;
            case MotionEvent.ACTION_MOVE:
                if (!mCheckedIntercept) {
                    int directionByTouchedEdge = getDirectionByTouchedEdge();
                    if (mSwipeBackForceEdge && directionByTouchedEdge != 0) {
                        mCheckedIntercept = true;
                        mShouldIntercept = true;
                        mDirection = directionByTouchedEdge;
                    } else {
                        float dx = x - mDownX;
                        float dy = y - mDownY;
                        if (Math.abs(dx) > mDragHelper.getTouchSlop() || Math.abs(dy) > mDragHelper.getTouchSlop()) {
                            mCheckedIntercept = true;
                            if (Math.abs(dx) > Math.abs(dy)) {
                                if (dx > 0) {
                                    if (hasDirection(SwipeBackDirection.RIGHT)) {
                                        if (mScrollDirectionResult != null && !mScrollDirectionResult.hasDirection(ScrollCompat.SCROLL_DIRECTION_LEFT)) {
                                            mDirection = SwipeBackDirection.RIGHT;
                                            mShouldIntercept = true;
                                        }
                                    }
                                } else {
                                    if (hasDirection(SwipeBackDirection.LEFT)) {
                                        if (mScrollDirectionResult != null && !mScrollDirectionResult.hasDirection(ScrollCompat.SCROLL_DIRECTION_RIGHT)) {
                                            mDirection = SwipeBackDirection.LEFT;
                                            mShouldIntercept = true;
                                        }
                                    }
                                }
                            } else {
                                if (dy > 0) {
                                    if (hasDirection(SwipeBackDirection.BOTTOM)) {
                                        if (mScrollDirectionResult != null && !mScrollDirectionResult.hasDirection(ScrollCompat.SCROLL_DIRECTION_UP)) {
                                            mDirection = SwipeBackDirection.BOTTOM;
                                            mShouldIntercept = true;
                                        }
                                    }
                                } else {
                                    if (hasDirection(SwipeBackDirection.TOP)) {
                                        if (mScrollDirectionResult != null && !mScrollDirectionResult.hasDirection(ScrollCompat.SCROLL_DIRECTION_DOWN)) {
                                            mDirection = SwipeBackDirection.TOP;
                                            mShouldIntercept = true;
                                        }
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
                mScrollDirectionResult = null;
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
            switch (mDirection) {
                case SwipeBackDirection.RIGHT:
                case SwipeBackDirection.LEFT:
                    offsetLeft = (int) (mFraction * (getWidth() + mShadowSize));
                    break;
                case SwipeBackDirection.BOTTOM:
                case SwipeBackDirection.TOP:
                    offsetTop = (int) (mFraction * (getHeight() + mShadowSize));
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
        if (mDirection == 0) return;
        final Rect childRect = mShadowRect;
        child.getHitRect(childRect);
        final Drawable shadow = getShadowDrawableDyDirection(mDirection);
        if (shadow == null) return;
        switch (mDirection) {
            case SwipeBackDirection.RIGHT:
                shadow.setBounds(childRect.left - shadow.getIntrinsicWidth(), childRect.top, childRect.left, childRect.bottom);
                break;
            case SwipeBackDirection.LEFT:
                shadow.setBounds(childRect.right, childRect.top, childRect.right + shadow.getIntrinsicWidth(), childRect.bottom);
                break;
            case SwipeBackDirection.BOTTOM:
                shadow.setBounds(childRect.left, childRect.top - shadow.getIntrinsicHeight(), childRect.right, childRect.top);
                break;
            case SwipeBackDirection.TOP:
                shadow.setBounds(childRect.left, childRect.bottom, childRect.right, childRect.bottom + shadow.getIntrinsicHeight());
                break;
        }
        //mShadowDrawable.setAlpha((int) ((1 - mFraction) * 255));
        shadow.draw(canvas);
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private boolean hasDirection(@SwipeBackDirection int direction) {
        return (direction & mSwipeBackDirection) == direction;
    }

    private boolean shouldBackBySpeed(float xvel, float yvel) {
        switch (mDirection) {
            case SwipeBackDirection.RIGHT:
                return xvel > mSwipeBackVelocity;
            case SwipeBackDirection.LEFT:
                return xvel < -mSwipeBackVelocity;
            case SwipeBackDirection.BOTTOM:
                return yvel > mSwipeBackVelocity;
            case SwipeBackDirection.TOP:
                return yvel < -mSwipeBackVelocity;
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
        switch (mDirection) {
            case SwipeBackDirection.RIGHT:
            case SwipeBackDirection.LEFT:
                mFraction = 1F * Math.abs(view.getLeft()) / (getWidth() + mShadowSize);
                break;
            case SwipeBackDirection.BOTTOM:
            case SwipeBackDirection.TOP:
                mFraction = 1F * Math.abs(view.getTop()) / (getHeight() + mShadowSize);
                break;
        }
        mFraction = Math.max(0F, mFraction);
        mFraction = Math.min(1F, mFraction);
    }

    @Nullable
    private GradientDrawable getShadowDrawableDyDirection(@SwipeBackDirection int direction) {
        if (direction == 0) return null;
        if (mShadowDrawables == null) {
            mShadowDrawables = new HashMap<>(1);
        }
        GradientDrawable drawable = mShadowDrawables.get(direction);
        if (drawable == null) {
            int[] colors = new int[]{
                    mShadowColor,
                    ColorUtils.setAlphaComponent(mShadowColor, (int) (Color.alpha(mShadowColor) * 0.3)),
                    Color.TRANSPARENT
            };
            switch (direction) {
                case SwipeBackDirection.RIGHT:
                    drawable = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors);
                    drawable.setSize(mShadowSize, 0);
                    break;
                case SwipeBackDirection.LEFT:
                    drawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
                    drawable.setSize(mShadowSize, 0);
                    break;
                case SwipeBackDirection.BOTTOM:
                    drawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors);
                    drawable.setSize(0, mShadowSize);
                    break;
                case SwipeBackDirection.TOP:
                    drawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
                    drawable.setSize(0, mShadowSize);
                    break;
            }
            if (drawable != null) {
                mShadowDrawables.put(direction, drawable);
            }
        }
        return drawable;
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
            if (mDirection == SwipeBackDirection.RIGHT) {
                if (mSwipeBackOnlyEdge) {
                    if (mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_LEFT)) {
                        newLeft = Math.min(Math.max(left, 0), getWidth() + mShadowSize);
                    }
                } else {
                    newLeft = Math.min(Math.max(left, 0), getWidth() + mShadowSize);
                }
            } else if (mDirection == SwipeBackDirection.LEFT) {
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
            if (mDirection == SwipeBackDirection.BOTTOM) {
                if (mSwipeBackOnlyEdge) {
                    if (mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_TOP)) {
                        newTop = Math.min(Math.max(top, 0), getHeight() + mShadowSize);
                    }
                } else {
                    newTop = Math.min(Math.max(top, 0), getHeight() + mShadowSize);
                }
            } else if (mDirection == SwipeBackDirection.TOP) {
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
                switch (mDirection) {
                    case SwipeBackDirection.BOTTOM:
                        smoothScrollToY(getHeight() + mShadowSize);
                        break;
                    case SwipeBackDirection.LEFT:
                        smoothScrollToX(-getWidth() - mShadowSize);
                        break;
                    case SwipeBackDirection.RIGHT:
                        smoothScrollToX(getWidth() + mShadowSize);
                        break;
                    case SwipeBackDirection.TOP:
                        smoothScrollToY(-getHeight() - mShadowSize);
                        break;
                }
            } else {
                switch (mDirection) {
                    case SwipeBackDirection.BOTTOM:
                    case SwipeBackDirection.TOP:
                        smoothScrollToY(0);
                        break;
                    case SwipeBackDirection.RIGHT:
                    case SwipeBackDirection.LEFT:
                        smoothScrollToX(0);
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
        void onBeforeSwipe(float swipeFraction, @SwipeBackDirection int swipeDirection);

        void onStartSwipe(float swipeFraction, @SwipeBackDirection int swipeDirection);

        void onSwiping(float swipeFraction, @SwipeBackDirection int swipeDirection);

        void onEndSwipe(float swipeFraction, @SwipeBackDirection int swipeDirection);
    }
}
