package per.goweii.swipeback;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.Scroller;

import androidx.annotation.FloatRange;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.NestedScrollingParent3;
import androidx.core.view.NestedScrollingParentHelper;
import androidx.core.view.ScrollingView;
import androidx.core.view.ViewCompat;
import androidx.customview.widget.ViewDragHelper;

import java.util.ArrayList;
import java.util.List;

import per.goweii.swipeback.utils.DragCompat;
import per.goweii.swipeback.utils.ScrollCompat;
import per.goweii.swipeback.utils.Utils;

@SuppressWarnings({"unused"})
public class SwipeBackLayout extends FrameLayout implements NestedScrollingParent3 {
    public static final int DIRECTION_NONE = 0;
    public static final int DIRECTION_LEFT = 1;
    public static final int DIRECTION_TOP = 1 << 1;
    public static final int DIRECTION_RIGHT = 1 << 2;
    public static final int DIRECTION_BOTTOM = 1 << 3;

    private final ViewDragHelper mDragHelper;
    private final Scroller mScroller;
    private final NestedScrollingParentHelper mNestedHelper;
    private final float mMaxVelocity;
    private final float mMinVelocity;
    private final int mTouchSlop;
    private final List<View> mInnerScrollViews = new ArrayList<>(0);

    private int mSwipeDirection = DIRECTION_NONE;
    private OnSwipeListener mOnSwipeListener = null;

    private View mSwipeView = null;

    private boolean mUsingNested = false;
    private boolean mHandleTouchEvent = false;
    private float mDownX = 0F;
    private float mDownY = 0F;
    private int mLeft = 0;
    private int mTop = 0;
    private int mRight = 0;
    private int mBottom = 0;
    private int mCurrSwipeDirection = DIRECTION_NONE;
    @FloatRange(from = 0F, to = 1F)
    private float mSwipeFraction = 0F;
    private float mVelocity = 0F;

    public SwipeBackLayout(@NonNull Context context) {
        this(context, null);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDragHelper = ViewDragHelper.create(this, new DragCallback());
        mScroller = new Scroller(context, new DecelerateInterpolator());
        mNestedHelper = new NestedScrollingParentHelper(this);
        final ViewConfiguration vc = ViewConfiguration.get(context);
        mTouchSlop = vc.getScaledTouchSlop();
        mMaxVelocity = vc.getScaledMaximumFlingVelocity();
        mMinVelocity = vc.getScaledMinimumFlingVelocity();
    }

    public void setOnSwipeListener(OnSwipeListener onSwipeListener) {
        mOnSwipeListener = onSwipeListener;
    }

    public void setSwipeDirection(int swipeDirection) {
        mSwipeDirection = swipeDirection;
    }

    public boolean canSwipe() {
        return mSwipeDirection != DIRECTION_NONE;
    }

    public boolean canSwipeDirection(int swipeDirection) {
        return (mSwipeDirection & swipeDirection) != 0;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(@NonNull MotionEvent ev) {
        if (!canSwipe()) {
            mHandleTouchEvent = false;
            return super.onInterceptTouchEvent(ev);
        }
        switch (ev.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mDownX = ev.getRawX();
                mDownY = ev.getRawY();
                if (mSwipeFraction == 0) {
                    mDragHelper.abort();
                    mScroller.abortAnimation();
                    mUsingNested = false;
                    mCurrSwipeDirection = 0;
                }
                break;
            default:
                break;
        }
        if (mUsingNested) {
            mHandleTouchEvent = false;
            return super.onInterceptTouchEvent(ev);
        }
        boolean shouldIntercept = mDragHelper.shouldInterceptTouchEvent(ev);
        mHandleTouchEvent = shouldIntercept;
        return shouldIntercept;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(@NonNull MotionEvent ev) {
        if (!canSwipe()) {
            return super.onTouchEvent(ev);
        }
        if (mUsingNested) {
            return super.onTouchEvent(ev);
        }
        if (mHandleTouchEvent) {
            mDragHelper.processTouchEvent(ev);
        }
        return mHandleTouchEvent;
    }

    @Override
    public void computeScroll() {
        if (!canSwipe()) {
            return;
        }
        if (mUsingNested) {
            if (mScroller.computeScrollOffset()) {
                scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
                invalidate();
            }
        } else {
            if (mDragHelper.continueSettling(true)) {
                invalidate();
            }
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (getChildCount() != 1) {
            throw new IllegalStateException("只能设置一个子View");
        }
        mSwipeView = getChildAt(0);
        mLeft = mSwipeView.getLeft();
        mTop = mSwipeView.getTop();
        mRight = mSwipeView.getRight();
        mBottom = mSwipeView.getBottom();
        if (mInnerScrollViews.isEmpty()) {
            mInnerScrollViews.addAll(DragCompat.findAllScrollViews(this));
        }
    }

    private int getSwipeX() {
        return mSwipeView.getLeft() - mLeft;
    }

    private int getSwipeY() {
        return mSwipeView.getTop() - mTop;
    }

    @Override
    public void scrollBy(int x, int y) {
        swipeBy(-x, -y);
    }

    @Override
    public void scrollTo(int x, int y) {
        swipeTo(-x, -y);
    }

    private void swipeBy(int x, int y) {
        swipeTo(getSwipeX() + x, getSwipeY() + y);
    }

    private void swipeTo(int x, int y) {
        int realx = x;
        int realy = y;
        switch (mCurrSwipeDirection) {
            case DIRECTION_LEFT:
                realx = Utils.intRange(x, -calcViewLeftRange(mSwipeView), 0);
                break;
            case DIRECTION_RIGHT:
                realx = Utils.intRange(x, 0, calcViewRightRange(mSwipeView));
                break;
            case DIRECTION_TOP:
                realy = Utils.intRange(y, -calcViewTopRange(mSwipeView), 0);
                break;
            case DIRECTION_BOTTOM:
                realy = Utils.intRange(y, 0, calcViewBottomRange(mSwipeView));
                break;
            default:
                break;
        }
        updateSwipeViewLayout(realx, realy);
        invalidate();
        onSwipeChanged();
    }

    private void updateSwipeViewLayout(int offx, int offy) {
        mSwipeView.setLeft(mLeft + offx);
        mSwipeView.setRight(mRight + offx);
        mSwipeView.setTop(mTop + offy);
        mSwipeView.setBottom(mBottom + offy);
    }

    private void onSwipeChanged() {
        handleSwipeFractionChange();
    }

    @FloatRange(from = 0F, to = 1F)
    private float calcSwipeFraction() {
        float f = 0;
        switch (mCurrSwipeDirection) {
            case DIRECTION_LEFT:
                f = (float) Math.abs(getSwipeX()) / (float) calcViewLeftRange(mSwipeView);
                break;
            case DIRECTION_RIGHT:
                f = (float) Math.abs(getSwipeX()) / (float) calcViewRightRange(mSwipeView);
                break;
            case DIRECTION_TOP:
                f = (float) Math.abs(getSwipeY()) / (float) calcViewTopRange(mSwipeView);
                break;
            case DIRECTION_BOTTOM:
                f = (float) Math.abs(getSwipeY()) / (float) calcViewBottomRange(mSwipeView);
                break;
            default:
                break;
        }
        return Utils.floatRange01(f);
    }

    private void refreshFraction() {
        mSwipeFraction = calcSwipeFraction();
    }

    private void handleSwipeFractionChange() {
        refreshFraction();
        onSwiping();
        if (mSwipeFraction == 0) {
            mCurrSwipeDirection = 0;
        } else if (mSwipeFraction == 1) {
            onSwipeEnd();
            mCurrSwipeDirection = 0;
        }
    }

    private void onSwipeStart() {
        if (mSwipeFraction == 0) {
            if (mOnSwipeListener != null) {
                mOnSwipeListener.onStart();
            }
        }
    }

    private void onSwiping() {
        if (mOnSwipeListener != null) {
            mOnSwipeListener.onSwiping(mCurrSwipeDirection, mSwipeFraction);
        }
    }

    private void onSwipeEnd() {
        if (mOnSwipeListener != null) {
            mOnSwipeListener.onEnd(mCurrSwipeDirection);
        }
    }

    private int calcViewCurrRange(@NonNull View view) {
        int range = 0;
        switch (mCurrSwipeDirection) {
            default:
                break;
            case DIRECTION_LEFT:
                range = calcViewLeftRange(mSwipeView);
                break;
            case DIRECTION_RIGHT:
                range = calcViewRightRange(mSwipeView);
                break;
            case DIRECTION_TOP:
                range = calcViewTopRange(mSwipeView);
                break;
            case DIRECTION_BOTTOM:
                range = calcViewBottomRange(mSwipeView);
                break;
        }
        return range;
    }

    private int calcViewLeftRange(@NonNull View view) {
        return mLeft + view.getWidth();
    }

    private int calcViewRightRange(@NonNull View view) {
        return getWidth() - mLeft;
    }

    private int calcViewTopRange(@NonNull View view) {
        return mTop + view.getHeight();
    }

    private int calcViewBottomRange(@NonNull View view) {
        return getHeight() - mTop;
    }

    @Override
    public boolean onStartNestedScroll(@NonNull View child, @NonNull View target, int axes, int type) {
        if ((ViewCompat.SCROLL_AXIS_VERTICAL & axes) == ViewCompat.SCROLL_AXIS_VERTICAL) {
            if (canSwipeDirection(DIRECTION_TOP | DIRECTION_BOTTOM)) {
                mUsingNested = ScrollCompat.canScrollUp(target) || ScrollCompat.canScrollDown(target);
            }
        } else if ((ViewCompat.SCROLL_AXIS_HORIZONTAL & axes) == ViewCompat.SCROLL_AXIS_HORIZONTAL) {
            if (canSwipeDirection(DIRECTION_LEFT | DIRECTION_RIGHT)) {
                mUsingNested = ScrollCompat.canScrollLeft(target) || ScrollCompat.canScrollRight(target);
            }
        } else {
            mUsingNested = false;
        }
        return mUsingNested;
    }

    @Override
    public void onNestedScrollAccepted(@NonNull View child, @NonNull View target, int axes, int type) {
        mNestedHelper.onNestedScrollAccepted(child, target, axes, type);
        if (type == ViewCompat.TYPE_TOUCH) {
            mScroller.abortAnimation();
            mVelocity = 0F;
            onSwipeStart();
        }
    }

    @Override
    public void onNestedPreScroll(@NonNull View target, int dx, int dy, @NonNull int[] consumed, int type) {
        if (!(target instanceof ScrollingView)) {
            return;
        }
        int scrollX = -getSwipeX();
        int scrollY = -getSwipeY();
        if (mCurrSwipeDirection == 0) {
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 0) {
                    if (canSwipeDirection(DIRECTION_LEFT))
                        mCurrSwipeDirection = DIRECTION_LEFT;
                } else {
                    if (canSwipeDirection(DIRECTION_RIGHT))
                        mCurrSwipeDirection = DIRECTION_RIGHT;
                }
            } else {
                if (dy > 0) {
                    if (canSwipeDirection(DIRECTION_TOP))
                        mCurrSwipeDirection = DIRECTION_TOP;
                } else {
                    if (canSwipeDirection(DIRECTION_BOTTOM))
                        mCurrSwipeDirection = DIRECTION_BOTTOM;
                }
            }
        }
        switch (mCurrSwipeDirection) {
            case DIRECTION_LEFT:
                if (dx < 0) {
                    if (scrollX > 0) {
                        if (scrollX + dx < 0) {
                            consumed[0] = -scrollX;
                        } else {
                            consumed[0] = dx;
                        }
                    } else {
                        consumed[0] = 0;
                    }
                } else if (dx > 0) {
                    if (scrollX > 0) {
                        consumed[0] = dx;
                    } else {
                        if (ScrollCompat.canScrollRight(target)) {
                            consumed[0] = 0;
                        } else {
                            if (type == ViewCompat.TYPE_NON_TOUCH) {
                                if (scrollX + dx < 0) {
                                    consumed[0] = -scrollX;
                                } else {
                                    consumed[0] = 0;
                                }
                            } else {
                                consumed[0] = dx;
                            }
                        }
                    }
                } else {
                    consumed[0] = 0;
                }
                consumed[1] = 0;
                break;
            case DIRECTION_RIGHT:
                if (dx > 0) {
                    if (scrollX < 0) {
                        if (scrollX + dx > 0) {
                            consumed[0] = -scrollX;
                        } else {
                            consumed[0] = dx;
                        }
                    } else {
                        consumed[0] = 0;
                    }
                } else if (dx < 0) {
                    if (scrollX < 0) {
                        consumed[0] = dx;
                    } else {
                        if (ScrollCompat.canScrollLeft(target)) {
                            consumed[0] = 0;
                        } else {
                            if (type == ViewCompat.TYPE_NON_TOUCH) {
                                if (scrollX + dx > 0) {
                                    consumed[0] = -scrollX;
                                } else {
                                    consumed[0] = 0;
                                }
                            } else {
                                consumed[0] = dx;
                            }
                        }
                    }
                } else {
                    consumed[0] = 0;
                }
                consumed[1] = 0;
                break;
            case DIRECTION_TOP:
                consumed[0] = 0;
                if (dy < 0) {
                    if (scrollY > 0) {
                        if (scrollY + dy < 0) {
                            consumed[1] = -scrollY;
                        } else {
                            consumed[1] = dy;
                        }
                    } else {
                        consumed[1] = 0;
                    }
                } else if (dy > 0) {
                    if (scrollY > 0) {
                        consumed[1] = dy;
                    } else {
                        if (ScrollCompat.canScrollDown(target)) {
                            consumed[1] = 0;
                        } else {
                            if (type == ViewCompat.TYPE_NON_TOUCH) {
                                if (scrollY + dy < 0) {
                                    consumed[1] = -scrollY;
                                } else {
                                    consumed[1] = 0;
                                }
                            } else {
                                consumed[1] = dy;
                            }
                        }
                    }
                } else {
                    consumed[1] = 0;
                }
                break;
            case DIRECTION_BOTTOM:
                consumed[0] = 0;
                if (dy > 0) {
                    if (scrollY < 0) {
                        if (scrollY + dy > 0) {
                            consumed[1] = -scrollY;
                        } else {
                            consumed[1] = dy;
                        }
                    } else {
                        consumed[1] = 0;
                    }
                } else if (dy < 0) {
                    if (scrollY < 0) {
                        consumed[1] = dy;
                    } else {
                        if (ScrollCompat.canScrollUp(target)) {
                            consumed[1] = 0;
                        } else {
                            if (type == ViewCompat.TYPE_NON_TOUCH) {
                                if (scrollY + dy > 0) {
                                    consumed[1] = -scrollY;
                                } else {
                                    consumed[1] = 0;
                                }
                            } else {
                                consumed[1] = dy;
                            }
                        }
                    }
                } else {
                    consumed[1] = 0;
                }
                break;
            default:
                consumed[0] = 0;
                consumed[1] = 0;
                break;
        }
        if (consumed[0] != 0 || consumed[1] != 0) {
            scrollBy(consumed[0], consumed[1]);
        }
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type) {
    }

    @Override
    public void onNestedScroll(@NonNull View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed, int type, @NonNull int[] consumed) {
        int scrollX = -getSwipeX();
        int scrollY = -getSwipeY();
        switch (mCurrSwipeDirection) {
            case DIRECTION_LEFT:
                if (scrollY + dxUnconsumed < 0) {
                    consumed[0] = -scrollX;
                } else {
                    if (type == ViewCompat.TYPE_NON_TOUCH) {
                        consumed[0] = 0;
                    } else {
                        consumed[0] = dxUnconsumed;
                    }
                }
                consumed[1] = 0;
                break;
            case DIRECTION_RIGHT:
                if (scrollX + dxUnconsumed > 0) {
                    consumed[0] = -scrollX;
                } else {
                    if (type == ViewCompat.TYPE_NON_TOUCH) {
                        consumed[0] = 0;
                    } else {
                        consumed[0] = dxUnconsumed;
                    }
                }
                consumed[1] = 0;
                break;
            case DIRECTION_TOP:
                consumed[0] = 0;
                if (scrollY + dyUnconsumed < 0) {
                    consumed[1] = -scrollY;
                } else {
                    if (type == ViewCompat.TYPE_NON_TOUCH) {
                        consumed[1] = 0;
                    } else {
                        consumed[1] = dyUnconsumed;
                    }
                }
                break;
            case DIRECTION_BOTTOM:
                consumed[0] = 0;
                if (scrollY + dyUnconsumed > 0) {
                    consumed[1] = -scrollY;
                } else {
                    if (type == ViewCompat.TYPE_NON_TOUCH) {
                        consumed[1] = 0;
                    } else {
                        consumed[1] = dyUnconsumed;
                    }
                }
                break;
            default:
                consumed[0] = 0;
                consumed[1] = 0;
                break;
        }
        if (consumed[0] != 0 || consumed[1] != 0) {
            scrollBy(consumed[0], consumed[1]);
        }
    }

    @Override
    public boolean onNestedPreFling(@NonNull View target, float velocityX, float velocityY) {
        switch (mCurrSwipeDirection) {
            default:
                mVelocity = 0F;
                break;
            case DIRECTION_LEFT:
            case DIRECTION_RIGHT:
                mVelocity = velocityX;
                break;
            case DIRECTION_TOP:
            case DIRECTION_BOTTOM:
                mVelocity = velocityY;
                break;
        }
        return super.onNestedPreFling(target, velocityX, velocityY);
    }

    @Override
    public boolean onNestedFling(@NonNull View target, float velocityX, float velocityY, boolean consumed) {
        return super.onNestedFling(target, velocityX, velocityY, consumed);
    }

    @Override
    public void onStopNestedScroll(@NonNull View target, int type) {
        mNestedHelper.onStopNestedScroll(target, type);
        if (type == ViewCompat.TYPE_TOUCH) {
            int fromx = getSwipeX();
            int fromy = getSwipeY();
            int endx = 0;
            int endy = 0;
            if (judgeDismiss(-mVelocity)) {
                switch (mCurrSwipeDirection) {
                    default:
                        break;
                    case DIRECTION_LEFT:
                        endx = -calcViewLeftRange(mSwipeView);
                        break;
                    case DIRECTION_RIGHT:
                        endx = calcViewRightRange(mSwipeView);
                        break;
                    case DIRECTION_TOP:
                        endy = -calcViewTopRange(mSwipeView);
                        break;
                    case DIRECTION_BOTTOM:
                        endy = calcViewBottomRange(mSwipeView);
                        break;
                }
            }
            int dx = endx - fromx;
            int dy = endy - fromy;
            int d = Math.max(Math.abs(dx), Math.abs(dy));
            int duration = computeSettleDuration(d, (int) mVelocity);
            mScroller.abortAnimation();
            mScroller.startScroll(-fromx, -fromy, -dx, -dy, duration);
            invalidate();
        }
    }

    @Override
    public int getNestedScrollAxes() {
        return mNestedHelper.getNestedScrollAxes();
    }

    private int computeSettleDuration(int d, int v) {
        v = clampMag(v, (int) mMinVelocity, (int) mMaxVelocity);
        int range = calcViewCurrRange(mSwipeView);
        return computeAxisDuration(d, v, range);
    }

    private int clampMag(int value, int absMin, int absMax) {
        final int absValue = Math.abs(value);
        if (absValue < absMin) return 0;
        if (absValue > absMax) return value > 0 ? absMax : -absMax;
        return value;
    }

    private int computeAxisDuration(int delta, int velocity, int motionRange) {
        if (delta == 0) {
            return 0;
        }
        final int width = getWidth();
        final int halfWidth = width / 2;
        final float distanceRatio = Math.min(1f, (float) Math.abs(delta) / width);
        final float distance = halfWidth + halfWidth
                * distanceInfluenceForSnapDuration(distanceRatio);
        int duration;
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            final float range = (float) Math.abs(delta) / motionRange;
            duration = (int) ((range + 1) * 256);
        }
        return Math.min(duration, 350);
    }

    private float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f;
        f *= 0.3f * (float) Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    private boolean judgeDismiss(float vel) {
        boolean isDismiss = false;
        float velocityLimit = 2000F;
        float dismissFactor = 0.5F;
        switch (mCurrSwipeDirection) {
            case DIRECTION_LEFT:
                isDismiss = getSwipeX() < 0 && vel < -velocityLimit;
                break;
            case DIRECTION_RIGHT:
                isDismiss = getSwipeX() > 0 && vel > velocityLimit;
                break;
            case DIRECTION_TOP:
                isDismiss = getSwipeY() < 0 && vel < -velocityLimit;
                break;
            case DIRECTION_BOTTOM:
                isDismiss = getSwipeY() > 0 && vel > velocityLimit;
                break;
            default:
                break;
        }
        if (isDismiss) {
            return true;
        }
        isDismiss = mSwipeFraction >= dismissFactor;
        return isDismiss;
    }

    private class DragCallback extends ViewDragHelper.Callback {
        private boolean mBeanDragged = false;

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            return canSwipe();
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            if (canSwipeDirection(DIRECTION_LEFT)) {
                return calcViewLeftRange(child);
            } else if (canSwipeDirection(DIRECTION_RIGHT)) {
                return calcViewRightRange(child);
            } else {
                return 0;
            }
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            if (canSwipeDirection(DIRECTION_TOP)) {
                return calcViewTopRange(child);
            } else if (canSwipeDirection(DIRECTION_BOTTOM)) {
                return calcViewBottomRange(child);
            } else {
                return 0;
            }
        }

        @Override
        public void onViewCaptured(@NonNull View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            mBeanDragged = false;
            onSwipeStart();
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            if (mCurrSwipeDirection == 0) {
                boolean swipedLeft = mBeanDragged ? dx < 0 : dx < -mTouchSlop;
                boolean swipedRight = mBeanDragged ? dx > 0 : dx > mTouchSlop;
                if (canSwipeDirection(DIRECTION_LEFT) && swipedLeft)
                    mCurrSwipeDirection = DIRECTION_LEFT;
                if (canSwipeDirection(DIRECTION_RIGHT) && swipedRight)
                    mCurrSwipeDirection = DIRECTION_RIGHT;
            }
            if (mCurrSwipeDirection == 0) {
                mBeanDragged = true;
            }
            switch (mCurrSwipeDirection) {
                case DIRECTION_LEFT:
                    if (DragCompat.canViewScrollRight(mInnerScrollViews, mDownX, mDownY, false)) {
                        return mLeft;
                    }
                    if (left > mLeft) {
                        return mLeft;
                    }
                    final int l = mLeft + child.getWidth();
                    return Math.max(left, -l);
                case DIRECTION_RIGHT:
                    if (DragCompat.canViewScrollLeft(mInnerScrollViews, mDownX, mDownY, false)) {
                        return mLeft;
                    }
                    if (left > getWidth()) {
                        return getWidth();
                    }
                    return Math.max(left, mLeft);
                case DIRECTION_TOP:
                case DIRECTION_BOTTOM:
                default:
                    return mLeft;
            }
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            if (mCurrSwipeDirection == 0) {
                boolean swipedTop = mBeanDragged ? dy < 0 : dy < -mTouchSlop;
                boolean swipedBottom = mBeanDragged ? dy > 0 : dy > mTouchSlop;
                if (canSwipeDirection(DIRECTION_TOP) && swipedTop)
                    mCurrSwipeDirection = DIRECTION_TOP;
                if (canSwipeDirection(DIRECTION_BOTTOM) && swipedBottom)
                    mCurrSwipeDirection = DIRECTION_BOTTOM;
            }
            if (mCurrSwipeDirection == 0) {
                mBeanDragged = true;
            }
            switch (mCurrSwipeDirection) {
                case DIRECTION_TOP:
                    if (DragCompat.canViewScrollDown(mInnerScrollViews, mDownX, mDownY, false)) {
                        return mTop;
                    }
                    if (top > mTop) {
                        return mTop;
                    }
                    final int t = mTop + child.getHeight();
                    return Math.max(top, -t);
                case DIRECTION_BOTTOM:
                    if (DragCompat.canViewScrollUp(mInnerScrollViews, mDownX, mDownY, false)) {
                        return mTop;
                    }
                    if (top > getHeight()) {
                        return getHeight();
                    }
                    return Math.max(top, mTop);
                case DIRECTION_LEFT:
                case DIRECTION_RIGHT:
                default:
                    return mTop;
            }
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            handleSwipeFractionChange();
        }

        @Override
        public void onViewReleased(@NonNull View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);
            mBeanDragged = false;
            float vel = 0F;
            switch (mCurrSwipeDirection) {
                case DIRECTION_LEFT:
                case DIRECTION_RIGHT:
                    vel = xvel;
                    break;
                case DIRECTION_TOP:
                case DIRECTION_BOTTOM:
                    vel = yvel;
                    break;
                default:
                    break;
            }
            int l = mLeft;
            int t = mTop;
            if (judgeDismiss(vel)) {
                switch (mCurrSwipeDirection) {
                    case DIRECTION_LEFT:
                        l = -(mLeft + releasedChild.getWidth());
                        break;
                    case DIRECTION_RIGHT:
                        l = getWidth();
                        break;
                    case DIRECTION_TOP:
                        t = -(mTop + releasedChild.getHeight());
                        break;
                    case DIRECTION_BOTTOM:
                        t = getHeight();
                        break;
                    default:
                        break;
                }
            }
            mDragHelper.settleCapturedViewAt(l, t);
            invalidate();
        }
    }

    public interface OnSwipeListener {
        /**
         * 开始滑动
         */
        void onStart();

        /**
         * 滑动中
         *
         * @param direction 滑动关闭的方向
         * @param fraction  滑动比例 0为开始，1为结束
         */
        void onSwiping(int direction,
                       @FloatRange(from = 0F, to = 1F) float fraction);

        /**
         * 滑动结束
         *
         * @param direction 滑动关闭的方向，0表示复位
         */
        void onEnd(int direction);
    }
}