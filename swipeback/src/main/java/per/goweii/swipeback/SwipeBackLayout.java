package per.goweii.swipeback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.ColorInt;
import androidx.annotation.FloatRange;
import androidx.annotation.IntRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.MotionEventCompat;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import java.util.ArrayList;
import java.util.List;

public class SwipeBackLayout extends FrameLayout {

    public static final String TAG = SwipeBackLayout.class.getSimpleName();

    private final ViewDragHelper mDragHelper;
    private final int mTouchSlop;
    private final Rect mShadowRect = new Rect();
    private final List<View> mInnerScrollViews = new ArrayList<>(0);

    private boolean mSwiping = false;
    private float mFraction = 0;
    private float mDownX = 0;
    private float mDownY = 0;
    private int mWidth = 0;
    private int mHeight = 0;
    private int mLeftOffset = 0;
    private int mTopOffset = 0;
    private int mTouchedEdge = ViewDragHelper.INVALID_POINTER;
    private GradientDrawable mShadowDrawable = null;

    private boolean mShadowEnable = true;
    private int mShadowStartColor = 0;
    private int mShadowSize = 0;
    @SwipeBackDirection
    private int mSwipeBackDirection = SwipeBackDirection.NONE;
    private boolean mSwipeBackForceEdge = true;
    private boolean mSwipeBackOnlyEdge = false;
    private float mSwipeBackFactor = 0.5f;
    private float mSwipeBackVelocity = 2000f;
    private int mMaskAlpha = 150;

    private SwipeBackListener mSwipeBackListener;

    public SwipeBackLayout(@NonNull Context context) {
        super(context);
        setWillNotDraw(false);
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        mDragHelper.setEdgeTrackingEnabled(mSwipeBackDirection);
        mTouchSlop = mDragHelper.getTouchSlop();
    }

    public boolean isSwipeBackEnable() {
        return mSwipeBackDirection != SwipeBackDirection.NONE;
    }

    public void setSwipeBackForceEdge(boolean enable) {
        mSwipeBackForceEdge = enable;
    }

    public boolean isSwipeBackForceEdge() {
        return mSwipeBackForceEdge;
    }

    public void setSwipeBackFactor(@FloatRange(from = 0.0f, to = 1.0f) float swipeBackFactor) {
        if (swipeBackFactor > 1) {
            swipeBackFactor = 1;
        } else if (swipeBackFactor < 0) {
            swipeBackFactor = 0;
        }
        this.mSwipeBackFactor = swipeBackFactor;
    }

    public float getSwipeBackFactor() {
        return mSwipeBackFactor;
    }

    public void setShadowEnable(boolean shadowEnable) {
        mShadowEnable = shadowEnable;
    }

    public boolean isShadowEnable() {
        return mShadowEnable;
    }

    public void setShadowStartColor(@ColorInt int colorInt) {
        mShadowStartColor = colorInt;
    }

    public void setShadowSize(int px) {
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

    public int getMaskAlpha() {
        return mMaskAlpha;
    }

    public void setSwipeBackDirection(@SwipeBackDirection int direction) {
        mSwipeBackDirection = direction;
        mDragHelper.setEdgeTrackingEnabled(direction);
    }

    public int getSwipeBackDirection() {
        return mSwipeBackDirection;
    }

    public float getSwipeBackVelocity() {
        return mSwipeBackVelocity;
    }

    public void setSwipeBackVelocity(@FloatRange(from = 0.0f) float swipeBackVelocity) {
        this.mSwipeBackVelocity = swipeBackVelocity;
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

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (!isSwipeBackEnable()) {
            super.onLayout(changed, l, t, r, b);
            return;
        }
        for (int i = 0; i < getChildCount(); i++) {
            final View child = getChildAt(i);
            int left = getPaddingLeft() + mLeftOffset;
            int top = getPaddingTop() + mTopOffset;
            int right = left + child.getMeasuredWidth();
            int bottom = top + child.getMeasuredHeight();
            child.layout(left, top, right, bottom);
            if (changed) {
                mWidth = getWidth();
                mHeight = getHeight();
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
        if (!isSwipeBackEnable()) {
            return super.drawChild(canvas, child, drawingTime);
        }
        boolean ret = super.drawChild(canvas, child, drawingTime);
        if (child == getChildAt(0)) {
            drawShadow(canvas, child);
        }
        return ret;
    }

    private void drawShadow(Canvas canvas, View child) {
        final Rect childRect = mShadowRect;
        child.getHitRect(childRect);
        if (mShadowEnable) {
            final Drawable shadow = getNonNullShadowDrawable();
            if (mSwipeBackDirection == SwipeBackDirection.LEFT) {
                shadow.setBounds(childRect.left - shadow.getIntrinsicWidth(), childRect.top, childRect.left, childRect.bottom);
            } else if (mSwipeBackDirection == SwipeBackDirection.RIGHT) {
                shadow.setBounds(childRect.left, childRect.top, childRect.left, childRect.bottom);
            } else if (mSwipeBackDirection == SwipeBackDirection.TOP) {
                shadow.setBounds(childRect.left, childRect.top - shadow.getIntrinsicHeight(), childRect.left, childRect.bottom);
            } else if (mSwipeBackDirection == SwipeBackDirection.BOTTOM) {
                shadow.setBounds(childRect.left, childRect.top, childRect.left, childRect.bottom);
            }
            mShadowDrawable.setAlpha((int) ((1 - mFraction) * 255));
            mShadowDrawable.draw(canvas);
        }
    }

    private boolean mTouchInnerScrollView = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (!isSwipeBackEnable()) {
            return false;
        }
        switch (MotionEventCompat.getActionMasked(ev)) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getRawX();
                mDownY = ev.getRawY();
                mInnerScrollViews.clear();
                mInnerScrollViews.addAll(SwipeBackCompat.findAllScrollViews2(this));
                mTouchInnerScrollView = SwipeBackCompat.contains(mInnerScrollViews, mDownX, mDownY) != null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mInnerScrollViews != null && mTouchInnerScrollView) {
                    float distanceX = Math.abs(ev.getRawX() - mDownX);
                    float distanceY = Math.abs(ev.getRawY() - mDownY);
                    if (mSwipeBackDirection == SwipeBackDirection.LEFT || mSwipeBackDirection == SwipeBackDirection.RIGHT) {
                        if (distanceY > mTouchSlop && distanceY > distanceX) {
                            return super.onInterceptTouchEvent(ev);
                        }
                    } else if (mSwipeBackDirection == SwipeBackDirection.TOP || mSwipeBackDirection == SwipeBackDirection.BOTTOM) {
                        if (distanceX > mTouchSlop && distanceX > distanceY) {
                            return super.onInterceptTouchEvent(ev);
                        }
                    }
                }
                break;
            default:
                break;
        }
        boolean handled = mDragHelper.shouldInterceptTouchEvent(ev);
        return handled ? handled : super.onInterceptTouchEvent(ev);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isSwipeBackEnable()) {
            return false;
        }
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            ViewCompat.postInvalidateOnAnimation(this);
        }
    }

    private boolean isSwipeEnabled() {
        if (mSwipeBackOnlyEdge) {
            switch (mSwipeBackDirection) {
                case SwipeBackDirection.LEFT:
                    return mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_LEFT);
                case SwipeBackDirection.TOP:
                    return mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_TOP);
                case SwipeBackDirection.RIGHT:
                    return mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_RIGHT);
                case SwipeBackDirection.BOTTOM:
                    return mDragHelper.isEdgeTouched(ViewDragHelper.EDGE_BOTTOM);
                case SwipeBackDirection.NONE:
                    break;
            }
        }
        return true;
    }

    private boolean backJudgeBySpeed(float xvel, float yvel) {
        switch (mSwipeBackDirection) {
            case SwipeBackDirection.LEFT:
                return xvel > mSwipeBackVelocity;
            case SwipeBackDirection.TOP:
                return yvel > mSwipeBackVelocity;
            case SwipeBackDirection.RIGHT:
                return xvel < -mSwipeBackVelocity;
            case SwipeBackDirection.BOTTOM:
                return yvel < -mSwipeBackVelocity;
            case SwipeBackDirection.NONE:
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

    @NonNull
    private GradientDrawable getNonNullShadowDrawable() {
        if (mShadowDrawable == null) {
            int[] colors = new int[]{mShadowStartColor, Color.TRANSPARENT};
            if (mSwipeBackDirection == SwipeBackDirection.LEFT) {
                mShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors);
                mShadowDrawable.setSize(mShadowSize, 0);
            } else if (mSwipeBackDirection == SwipeBackDirection.RIGHT) {
                mShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
                mShadowDrawable.setSize(mShadowSize, 0);
            } else if (mSwipeBackDirection == SwipeBackDirection.TOP) {
                mShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors);
                mShadowDrawable.setSize(0, mShadowSize);
            } else if (mSwipeBackDirection == SwipeBackDirection.BOTTOM) {
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
            return isSwipeBackEnable();
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            onSwipeStart();
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            mLeftOffset = getPaddingLeft();
            if (isSwipeEnabled()) {
                if (mSwipeBackDirection == SwipeBackDirection.LEFT) {
                    if (!SwipeBackCompat.canViewScrollLeft(mInnerScrollViews, mDownX, mDownY, false)) {
                        mLeftOffset = Math.min(Math.max(left, getPaddingLeft()), mWidth);
                    } else {
                        if (mSwipeBackForceEdge && mTouchedEdge == ViewDragHelper.EDGE_LEFT) {
                            mLeftOffset = Math.min(Math.max(left, getPaddingLeft()), mWidth);
                        }
                    }
                } else if (mSwipeBackDirection == SwipeBackDirection.RIGHT) {
                    if (!SwipeBackCompat.canViewScrollRight(mInnerScrollViews, mDownX, mDownY, false)) {
                        mLeftOffset = Math.min(Math.max(left, -mWidth), getPaddingRight());
                    } else {
                        if (mSwipeBackForceEdge && mTouchedEdge == ViewDragHelper.EDGE_RIGHT) {
                            mLeftOffset = Math.min(Math.max(left, -mWidth), getPaddingRight());
                        }
                    }
                }
            }
            Log.i(TAG, "clampViewPositionHorizontal -> " + "mLeftOffset=" + mLeftOffset);
            return mLeftOffset;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            mTopOffset = getPaddingTop();
            if (isSwipeEnabled()) {
                if (mSwipeBackDirection == SwipeBackDirection.TOP) {
                    if (!SwipeBackCompat.canViewScrollUp(mInnerScrollViews, mDownX, mDownY, false)) {
                        mTopOffset = Math.min(Math.max(top, getPaddingTop()), mHeight);
                    } else {
                        if (mSwipeBackForceEdge && mTouchedEdge == ViewDragHelper.EDGE_TOP) {
                            mTopOffset = Math.min(Math.max(top, getPaddingTop()), mHeight);
                        }
                    }
                } else if (mSwipeBackDirection == SwipeBackDirection.BOTTOM) {
                    if (!SwipeBackCompat.canViewScrollDown(mInnerScrollViews, mDownX, mDownY, false)) {
                        mTopOffset = Math.min(Math.max(top, -mHeight), getPaddingBottom());
                    } else {
                        if (mSwipeBackForceEdge && mTouchedEdge == ViewDragHelper.EDGE_BOTTOM) {
                            mTopOffset = Math.min(Math.max(top, -mHeight), getPaddingBottom());
                        }
                    }
                }
            }
            Log.i(TAG, "clampViewPositionVertical -> " + "mTopOffset=" + mTopOffset);
            return mTopOffset;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            left = Math.abs(left);
            top = Math.abs(top);
            switch (mSwipeBackDirection) {
                case SwipeBackDirection.LEFT:
                case SwipeBackDirection.RIGHT:
                    mFraction = 1.0f * left / mWidth;
                    break;
                case SwipeBackDirection.TOP:
                case SwipeBackDirection.BOTTOM:
                    mFraction = 1.0f * top / mHeight;
                    break;
                case SwipeBackDirection.NONE:
                    break;
            }
            Log.i(TAG, "onViewPositionChanged -> " + "mFraction=" + mFraction);
            onSwiping();
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            mLeftOffset = mTopOffset = 0;
            if (!isSwipeEnabled()) {
                mTouchedEdge = ViewDragHelper.INVALID_POINTER;
                return;
            }
            mTouchedEdge = ViewDragHelper.INVALID_POINTER;

            boolean isBackToEnd = backJudgeBySpeed(xvel, yvel) || mFraction >= mSwipeBackFactor;
            if (isBackToEnd) {
                switch (mSwipeBackDirection) {
                    case SwipeBackDirection.LEFT:
                        smoothScrollToX(mWidth);
                        break;
                    case SwipeBackDirection.TOP:
                        smoothScrollToY(mHeight);
                        break;
                    case SwipeBackDirection.RIGHT:
                        smoothScrollToX(-mWidth);
                        break;
                    case SwipeBackDirection.BOTTOM:
                        smoothScrollToY(-mHeight);
                        break;
                    case SwipeBackDirection.NONE:
                        break;
                }
            } else {
                switch (mSwipeBackDirection) {
                    case SwipeBackDirection.LEFT:
                    case SwipeBackDirection.RIGHT:
                        smoothScrollToX(getPaddingLeft());
                        break;
                    case SwipeBackDirection.BOTTOM:
                    case SwipeBackDirection.TOP:
                        smoothScrollToY(getPaddingTop());
                        break;
                    case SwipeBackDirection.NONE:
                        break;
                }
            }
            Log.i(TAG, "onViewReleased -> ");
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
            Log.i(TAG, "onViewDragStateChanged -> " + "state=" + state);
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            Log.i(TAG, "getViewHorizontalDragRange -> " + "mWidth" + mWidth);
            return mWidth;
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            Log.i(TAG, "getViewVerticalDragRange -> " + "mHeight" + mHeight);
            return mHeight;
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
            //边缘Touch状态 开始滑动
            mTouchedEdge = edgeFlags;
            Log.i(TAG, "onEdgeTouched -> " + "mTouchedEdge" + mTouchedEdge);
        }

        @Override
        public void onEdgeDragStarted(int edgeFlags, int pointerId) {
            super.onEdgeDragStarted(edgeFlags, pointerId);
            Log.i(TAG, "onEdgeDragStarted -> " + "mTouchedEdge" + mTouchedEdge);
        }
    }

    protected void onSwipeStart() {
        mSwiping = true;
        if (mSwipeBackListener != null) {
            mSwipeBackListener.onSwipeStart(mFraction, mSwipeBackDirection);
        }
    }

    protected void onSwiping() {
        if (mSwipeBackListener != null) {
            mSwipeBackListener.onSwiping(mFraction, mSwipeBackDirection);
        }
    }

    protected void onSwipeEnd() {
        mSwiping = false;
        if (mSwipeBackListener != null) {
            mSwipeBackListener.onSwipeEnd(mFraction, mSwipeBackDirection);
        }
    }

    public interface SwipeBackListener {
        void onSwipeStart(float swipeFraction, @SwipeBackDirection int swipeDirection);
        void onSwiping(float swipeFraction, @SwipeBackDirection int swipeDirection);
        void onSwipeEnd(float swipeFraction, @SwipeBackDirection int swipeDirection);
    }
}
