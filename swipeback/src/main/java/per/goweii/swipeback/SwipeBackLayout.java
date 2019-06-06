package per.goweii.swipeback;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.ColorInt;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import java.util.List;

import per.goweii.swipeback.transformer.ParallaxSwipeBackTransformer;

public class SwipeBackLayout extends FrameLayout {

    private final ViewDragHelper mDragHelper;
    private int mTouchSlop;
    private Rect mShadowRect = new Rect();
    private Activity mCurrentActivity;
    private List<View> mInnerScrollViews;
    private View mPreviousChildView;
    private View mCurrentChildView;
    private boolean mActivityTranslucent = true;
    private boolean mActivitySwiping = false;
    private float mFraction;
    private float mDownX;
    private float mDownY;
    private int mWidth;
    private int mHeight;
    private int mLeftOffset = 0;
    private int mTopOffset = 0;
    private int mTouchedEdge = ViewDragHelper.INVALID_POINTER;
    private GradientDrawable mShadowDrawable = null;
    private boolean mTakeOverActivityExitAnimRunning = false;

    private boolean mActivityIsAlreadyTranslucent = false;
    private boolean mShadowEnable = true;
    private int mShadowStartColor = 0;
    private int mShadowSize = 0;
    @SwipeBackDirection
    private int mSwipeBackDirection = SwipeBackDirection.FROM_LEFT;
    private long mTakeOverActivityEnterExitAnimDuration = 300;
    private boolean mTakeOverActivityEnterExitAnim = false;
    private boolean mSwipeBackEnable = true;
    private boolean mSwipeBackForceEdge = true;
    private boolean mSwipeBackOnlyEdge = false;
    private float mSwipeBackFactor = 0.5f;
    private float mAutoFinishedVelocityLimit = 2000f;
    private int mMaskAlpha = 150;

    private SwipeBackListener mSwipeBackListener;
    private SwipeBackTransformer mSwipeBackTransformer;
    private ValueAnimator mTakeOverActivityEnterAnimator;
    private ValueAnimator mTakeOverActivityExitAnimator;

    public SwipeBackLayout(@NonNull Context context) {
        this(context, null);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SwipeBackLayout(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        setWillNotDraw(false);
        mDragHelper = ViewDragHelper.create(this, 1f, new DragHelperCallback());
        mDragHelper.setEdgeTrackingEnabled(mSwipeBackDirection);
        mTouchSlop = mDragHelper.getTouchSlop();
        init(context, attrs);
    }

    private void init(@NonNull Context context, @Nullable AttributeSet attrs) {
        final int shadowStartColor = Color.argb(30, 0, 0, 0);
        final int shadowWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 30, context.getResources().getDisplayMetrics());
        int takeOverActivityEnterExitAnimDuration = getContext().getResources().getInteger(R.integer.swipeback_activity_duration);
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.SwipeBackLayout);
        mSwipeBackEnable = typedArray.getBoolean(R.styleable.SwipeBackLayout_sbl_enable, true);
        mActivityIsAlreadyTranslucent = typedArray.getBoolean(R.styleable.SwipeBackLayout_sbl_activityIsAlreadyTranslucent, false);
        mTakeOverActivityEnterExitAnim = typedArray.getBoolean(R.styleable.SwipeBackLayout_sbl_takeOverActivityAnim, true);
        mTakeOverActivityEnterExitAnimDuration = typedArray.getInteger(R.styleable.SwipeBackLayout_sbl_takeOverActivityAnimDuration, takeOverActivityEnterExitAnimDuration);
        mSwipeBackForceEdge = typedArray.getBoolean(R.styleable.SwipeBackLayout_sbl_forceEdge, true);
        mSwipeBackOnlyEdge = typedArray.getBoolean(R.styleable.SwipeBackLayout_sbl_onlyEdge, true);
        mShadowEnable = typedArray.getBoolean(R.styleable.SwipeBackLayout_sbl_shadowEnable, true);
        mShadowStartColor = typedArray.getColor(R.styleable.SwipeBackLayout_sbl_shadowStartColor, shadowStartColor);
        mShadowSize = (int) typedArray.getDimension(R.styleable.SwipeBackLayout_sbl_shadowSize, shadowWidth);
        mMaskAlpha = typedArray.getInteger(R.styleable.SwipeBackLayout_sbl_maskAlpha, 150);
        mAutoFinishedVelocityLimit = typedArray.getInteger(R.styleable.SwipeBackLayout_sbl_autoFinishedVelocityLimit, 2000);
        mSwipeBackFactor = typedArray.getFloat(R.styleable.SwipeBackLayout_sbl_factor, 0.5F);
        mSwipeBackDirection = typedArray.getInt(R.styleable.SwipeBackLayout_sbl_direction, SwipeBackDirection.FROM_LEFT);
        typedArray.recycle();
    }

    public void attachTo(Activity activity) {
        setVisibility(View.INVISIBLE);
        mCurrentActivity = activity;
        activity.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        activity.getWindow().getDecorView().setBackgroundDrawable(null);
        TypedArray a = activity.getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowBackground});
        int background = a.getResourceId(0, 0);
        a.recycle();
        ViewGroup decorView = (ViewGroup) activity.getWindow().getDecorView();
        ViewGroup decorChild = (ViewGroup) decorView.getChildAt(0);
        decorChild.setBackgroundResource(background);
        decorView.removeViewInLayout(decorChild);
        addViewInLayout(decorChild, 0, new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mCurrentChildView = decorChild;
        decorView.addView(this);
        Activity previousActivity = SwipeBackManager.getInstance().getPreviousActivity();
        if (previousActivity != null) {
            ViewGroup previousDecorView = (ViewGroup) previousActivity.getWindow().getDecorView();
            ViewGroup previousDecorChild = (ViewGroup) previousDecorView.getChildAt(0);
            if (previousDecorChild instanceof SwipeBackLayout) {
                SwipeBackLayout previousSwipeBackLayout = (SwipeBackLayout) previousDecorChild;
                mPreviousChildView = previousSwipeBackLayout.getChildAt(0);
            }
        }
        if (previousActivity != null && mTakeOverActivityEnterExitAnim) {
            setActivityTranslucent(true);
            activity.overridePendingTransition(0, 0);
        }
        startEnterAnim();
    }

    public View getPreviousChildView() {
        return mPreviousChildView;
    }

    public void startEnterAnim() {
        if (mPreviousChildView != null) {
            if (mTakeOverActivityEnterExitAnim) {
                if (mTakeOverActivityExitAnimator != null) {
                    mTakeOverActivityExitAnimator.pause();
                    mTakeOverActivityExitAnimator = null;
                }
                mFraction = 1;
                mTakeOverActivityEnterAnimator = ValueAnimator.ofFloat(mFraction, 0);
                if (mTakeOverActivityEnterExitAnimDuration < 0L) {
                    mTakeOverActivityEnterExitAnimDuration = mPreviousChildView.getResources().getInteger(R.integer.swipeback_activity_duration);
                }
                mTakeOverActivityEnterAnimator.setDuration(mTakeOverActivityEnterExitAnimDuration);
                mTakeOverActivityEnterAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        if (getVisibility() != View.VISIBLE) {
                            setVisibility(View.VISIBLE);
                        }
                        mLeftOffset = 0;
                        mTopOffset = 0;
                        mFraction = (float) animation.getAnimatedValue();
                        if (mSwipeBackDirection == SwipeBackDirection.FROM_LEFT) {
                            mLeftOffset = (int) (mWidth - mWidth * (1 - mFraction));
                        } else if (mSwipeBackDirection == SwipeBackDirection.FROM_RIGHT) {
                            mLeftOffset = (int) -(mWidth - mWidth * (1 - mFraction));
                        } else if (mSwipeBackDirection == SwipeBackDirection.FROM_TOP) {
                            mTopOffset = (int) (mHeight - mHeight * (1 - mFraction));
                        } else if (mSwipeBackDirection == SwipeBackDirection.FROM_BOTTOM) {
                            mTopOffset = (int) -(mHeight - mHeight * (1 - mFraction));
                        }
                        getNonNullSwipeBackTransformer().transform(mCurrentChildView, mPreviousChildView, mFraction, mSwipeBackDirection);
                        requestLayout();
                    }
                });
                post(new Runnable() {
                    @Override
                    public void run() {
                        mTakeOverActivityEnterAnimator.start();
                    }
                });
            } else {
                setVisibility(View.VISIBLE);
                mFraction = 1;
                mLeftOffset = 0;
                mTopOffset = 0;
                getNonNullSwipeBackTransformer().transform(mCurrentChildView, mPreviousChildView, mFraction, mSwipeBackDirection);
                requestLayout();
            }
        } else {
            setVisibility(View.VISIBLE);
        }
    }

    public void startExitAnim() {
        if (mPreviousChildView != null) {
            if (mTakeOverActivityEnterExitAnim) {
                if (mTakeOverActivityEnterAnimator != null) {
                    mTakeOverActivityEnterAnimator.pause();
                    mTakeOverActivityEnterAnimator = null;
                }
                if (mTakeOverActivityExitAnimRunning) {
                    return;
                }
                mTakeOverActivityExitAnimRunning = true;
                mTakeOverActivityExitAnimator = ValueAnimator.ofFloat(mFraction, 1);
                if (mTakeOverActivityEnterExitAnimDuration < 0L) {
                    mTakeOverActivityEnterExitAnimDuration = mPreviousChildView.getResources().getInteger(R.integer.swipeback_activity_duration);
                }
                mTakeOverActivityExitAnimator.setDuration(mTakeOverActivityEnterExitAnimDuration);
                mTakeOverActivityExitAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        mLeftOffset = 0;
                        mTopOffset = 0;
                        mFraction = (float) animation.getAnimatedValue();
                        if (mSwipeBackDirection == SwipeBackDirection.FROM_LEFT) {
                            mLeftOffset = (int) (mWidth - mWidth * (1 - mFraction));
                        } else if (mSwipeBackDirection == SwipeBackDirection.FROM_RIGHT) {
                            mLeftOffset = (int) -(mWidth - mWidth * (1 - mFraction));
                        } else if (mSwipeBackDirection == SwipeBackDirection.FROM_TOP) {
                            mTopOffset = (int) (mHeight - mHeight * (1 - mFraction));
                        } else if (mSwipeBackDirection == SwipeBackDirection.FROM_BOTTOM) {
                            mTopOffset = (int) -(mHeight - mHeight * (1 - mFraction));
                        }
                        getNonNullSwipeBackTransformer().transform(mCurrentChildView, mPreviousChildView, mFraction, mSwipeBackDirection);
                        requestLayout();
                    }
                });
                mTakeOverActivityExitAnimator.addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mTakeOverActivityExitAnimRunning = false;
                        if (mCurrentActivity != null) {
                            mCurrentActivity.finish();
                            mCurrentActivity.overridePendingTransition(0, 0);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }
                });
                mTakeOverActivityExitAnimator.start();
            } else {
                mFraction = 1;
                mLeftOffset = 0;
                mTopOffset = 0;
                getNonNullSwipeBackTransformer().transform(mCurrentChildView, mPreviousChildView, 1, mSwipeBackDirection);
                requestLayout();
            }
        }
    }

    public boolean isTakeOverActivityExitAnimRunning() {
        return mTakeOverActivityExitAnimRunning;
    }

    public void setTakeOverActivityEnterExitAnim(boolean enable) {
        mTakeOverActivityEnterExitAnim = enable;
    }

    public boolean isTakeOverActivityEnterExitAnim() {
        return mTakeOverActivityEnterExitAnim;
    }

    public void setSwipeBackEnable(boolean swipeBackEnable) {
        mSwipeBackEnable = swipeBackEnable;
    }

    public boolean isSwipeBackEnable() {
        return mSwipeBackEnable;
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

    public float getAutoFinishedVelocityLimit() {
        return mAutoFinishedVelocityLimit;
    }

    public void setAutoFinishedVelocityLimit(@FloatRange(from = 0.0f, to = 1.0f) float autoFinishedVelocityLimit) {
        this.mAutoFinishedVelocityLimit = autoFinishedVelocityLimit;
    }

    public boolean isSwipeBackOnlyEdge() {
        return mSwipeBackOnlyEdge;
    }

    public void setSwipeBackOnlyEdge(boolean swipeBackOnlyEdge) {
        this.mSwipeBackOnlyEdge = swipeBackOnlyEdge;
    }

    public boolean isActivitySwiping() {
        return mActivitySwiping;
    }

    public boolean isActivityTranslucent() {
        if (mCurrentActivity == null) {
            return true;
        }
        return mActivityTranslucent;
    }

    public void setActivityTranslucent(boolean activityTranslucent) {
        if (mActivityIsAlreadyTranslucent) {
            mActivityTranslucent = true;
        } else {
            mActivityTranslucent = activityTranslucent;
        }
        if (null != mCurrentActivity) {
            if (mActivityTranslucent) {
                SwipeBackCompat.convertActivityToTranslucent(mCurrentActivity);
            } else {
                SwipeBackCompat.convertActivityFromTranslucent(mCurrentActivity);
            }
        }
    }

    public void setSwipeBackListener(SwipeBackListener swipeBackListener) {
        this.mSwipeBackListener = swipeBackListener;
    }

    public void setSwipeBackTransformer(SwipeBackTransformer swipeBackTransformer) {
        mSwipeBackTransformer = swipeBackTransformer;
    }

    @NonNull
    public SwipeBackTransformer getNonNullSwipeBackTransformer() {
        if (mSwipeBackTransformer == null) {
            mSwipeBackTransformer = new ParallaxSwipeBackTransformer();
        }
        return mSwipeBackTransformer;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        try {
            if (!isSwipeBackEnable()) {
                super.onLayout(changed, l, t, r, b);
                return;
            }
            int left = getPaddingLeft() + mLeftOffset;
            int top = getPaddingTop() + mTopOffset;
            int right = left + mCurrentChildView.getMeasuredWidth();
            int bottom = top + mCurrentChildView.getMeasuredHeight();
            mCurrentChildView.layout(left, top, right, bottom);
            if (changed) {
                mWidth = getWidth();
                mHeight = getHeight();
            }
        } catch (Exception e) {
            super.onLayout(changed, l, t, r, b);
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
        if (child == mCurrentChildView) {
            drawShadow(canvas, child);
        }
        return ret;
    }

    private void drawShadow(Canvas canvas, View child) {
        final Rect childRect = mShadowRect;
        child.getHitRect(childRect);
        if (mShadowEnable) {
            final Drawable shadow = getNonNullShadowDrawable();
            if (mSwipeBackDirection == SwipeBackDirection.FROM_LEFT) {
                shadow.setBounds(childRect.left - shadow.getIntrinsicWidth(), childRect.top, childRect.left, childRect.bottom);
            } else if (mSwipeBackDirection == SwipeBackDirection.FROM_RIGHT) {
                shadow.setBounds(childRect.left, childRect.top, childRect.left, childRect.bottom);
            } else if (mSwipeBackDirection == SwipeBackDirection.FROM_TOP) {
                shadow.setBounds(childRect.left, childRect.top - shadow.getIntrinsicHeight(), childRect.left, childRect.bottom);
            } else if (mSwipeBackDirection == SwipeBackDirection.FROM_BOTTOM) {
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
                mInnerScrollViews = SwipeBackCompat.findAllScrollViews2(this);
                mTouchInnerScrollView = SwipeBackCompat.contains(mInnerScrollViews, mDownX, mDownY) != null;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mInnerScrollViews != null && mTouchInnerScrollView) {
                    float distanceX = Math.abs(ev.getRawX() - mDownX);
                    float distanceY = Math.abs(ev.getRawY() - mDownY);
                    if (mSwipeBackDirection == SwipeBackDirection.FROM_LEFT || mSwipeBackDirection == SwipeBackDirection.FROM_RIGHT) {
                        if (distanceY > mTouchSlop && distanceY > distanceX) {
                            return super.onInterceptTouchEvent(ev);
                        }
                    } else if (mSwipeBackDirection == SwipeBackDirection.FROM_TOP || mSwipeBackDirection == SwipeBackDirection.FROM_BOTTOM) {
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
                case SwipeBackDirection.FROM_LEFT:
                    return mTouchedEdge == ViewDragHelper.EDGE_LEFT;
                case SwipeBackDirection.FROM_TOP:
                    return mTouchedEdge == ViewDragHelper.EDGE_TOP;
                case SwipeBackDirection.FROM_RIGHT:
                    return mTouchedEdge == ViewDragHelper.EDGE_RIGHT;
                case SwipeBackDirection.FROM_BOTTOM:
                    return mTouchedEdge == ViewDragHelper.EDGE_BOTTOM;
                default:
                    break;
            }
        }
        return true;
    }

    private boolean backJudgeBySpeed(float xvel, float yvel) {
        switch (mSwipeBackDirection) {
            case SwipeBackDirection.FROM_LEFT:
                return xvel > mAutoFinishedVelocityLimit;
            case SwipeBackDirection.FROM_TOP:
                return yvel > mAutoFinishedVelocityLimit;
            case SwipeBackDirection.FROM_RIGHT:
                return xvel < -mAutoFinishedVelocityLimit;
            case SwipeBackDirection.FROM_BOTTOM:
                return yvel < -mAutoFinishedVelocityLimit;
            default:
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
            if (mSwipeBackDirection == SwipeBackDirection.FROM_LEFT) {
                mShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.RIGHT_LEFT, colors);
                mShadowDrawable.setSize(mShadowSize, 0);
            } else if (mSwipeBackDirection == SwipeBackDirection.FROM_RIGHT) {
                mShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.LEFT_RIGHT, colors);
                mShadowDrawable.setSize(mShadowSize, 0);
            } else if (mSwipeBackDirection == SwipeBackDirection.FROM_TOP) {
                mShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.BOTTOM_TOP, colors);
                mShadowDrawable.setSize(0, mShadowSize);
            } else if (mSwipeBackDirection == SwipeBackDirection.FROM_BOTTOM) {
                mShadowDrawable = new GradientDrawable(GradientDrawable.Orientation.TOP_BOTTOM, colors);
                mShadowDrawable.setSize(0, mShadowSize);
            } else {
                mShadowDrawable = new GradientDrawable();
                mShadowDrawable.setSize(0, 0);
            }
        }
        return mShadowDrawable;
    }

    private void finish() {
        mTakeOverActivityExitAnimRunning = false;
        mCurrentActivity.finish();
        mCurrentActivity.overridePendingTransition(0, 0);
    }

    private class DragHelperCallback extends ViewDragHelper.Callback {

        @Override
        public boolean tryCaptureView(@NonNull View child, int pointerId) {
            if (isSwipeBackEnable()) {
                mActivitySwiping = true;
                setActivityTranslucent(true);
                return child == mCurrentChildView;
            }
            return false;
        }

        @Override
        public int clampViewPositionHorizontal(@NonNull View child, int left, int dx) {
            mLeftOffset = getPaddingLeft();
            if (isSwipeEnabled()) {
                if (mSwipeBackDirection == SwipeBackDirection.FROM_LEFT) {
                    if (!SwipeBackCompat.canViewScrollLeft(mInnerScrollViews, mDownX, mDownY, false)) {
                        mLeftOffset = Math.min(Math.max(left, getPaddingLeft()), mWidth);
                    } else {
                        if (mSwipeBackForceEdge && mTouchedEdge == ViewDragHelper.EDGE_LEFT) {
                            mLeftOffset = Math.min(Math.max(left, getPaddingLeft()), mWidth);
                        }
                    }
                } else if (mSwipeBackDirection == SwipeBackDirection.FROM_RIGHT) {
                    if (!SwipeBackCompat.canViewScrollRight(mInnerScrollViews, mDownX, mDownY, false)) {
                        mLeftOffset = Math.min(Math.max(left, -mWidth), getPaddingRight());
                    } else {
                        if (mSwipeBackForceEdge && mTouchedEdge == ViewDragHelper.EDGE_RIGHT) {
                            mLeftOffset = Math.min(Math.max(left, -mWidth), getPaddingRight());
                        }
                    }
                }
            }
            return mLeftOffset;
        }

        @Override
        public int clampViewPositionVertical(@NonNull View child, int top, int dy) {
            mTopOffset = getPaddingTop();
            if (isSwipeEnabled()) {
                if (mSwipeBackDirection == SwipeBackDirection.FROM_TOP) {
                    if (!SwipeBackCompat.canViewScrollUp(mInnerScrollViews, mDownX, mDownY, false)) {
                        mTopOffset = Math.min(Math.max(top, getPaddingTop()), mHeight);
                    } else {
                        if (mSwipeBackForceEdge && mTouchedEdge == ViewDragHelper.EDGE_TOP) {
                            mTopOffset = Math.min(Math.max(top, getPaddingTop()), mHeight);
                        }
                    }
                } else if (mSwipeBackDirection == SwipeBackDirection.FROM_BOTTOM) {
                    if (!SwipeBackCompat.canViewScrollDown(mInnerScrollViews, mDownX, mDownY, false)) {
                        mTopOffset = Math.min(Math.max(top, -mHeight), getPaddingBottom());
                    } else {
                        if (mSwipeBackForceEdge && mTouchedEdge == ViewDragHelper.EDGE_BOTTOM) {
                            mTopOffset = Math.min(Math.max(top, -mHeight), getPaddingBottom());
                        }
                    }
                }
            }
            return mTopOffset;
        }

        @Override
        public void onViewPositionChanged(@NonNull View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);
            left = Math.abs(left);
            top = Math.abs(top);
            switch (mSwipeBackDirection) {
                case SwipeBackDirection.FROM_LEFT:
                case SwipeBackDirection.FROM_RIGHT:
                    mFraction = 1.0f * left / mWidth;
                    break;
                case SwipeBackDirection.FROM_TOP:
                case SwipeBackDirection.FROM_BOTTOM:
                    mFraction = 1.0f * top / mHeight;
                    break;
                default:
                    break;
            }
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
                    case SwipeBackDirection.FROM_LEFT:
                        //滑动关闭
                        smoothScrollToX(mWidth);
                        break;
                    case SwipeBackDirection.FROM_TOP:
                        smoothScrollToY(mHeight);
                        break;
                    case SwipeBackDirection.FROM_RIGHT:
                        smoothScrollToX(-mWidth);
                        break;
                    case SwipeBackDirection.FROM_BOTTOM:
                        smoothScrollToY(-mHeight);
                        break;
                    default:
                        break;
                }
            } else {
                switch (mSwipeBackDirection) {
                    case SwipeBackDirection.FROM_LEFT:
                    case SwipeBackDirection.FROM_RIGHT:
                        smoothScrollToX(getPaddingLeft());
                        break;
                    case SwipeBackDirection.FROM_BOTTOM:
                    case SwipeBackDirection.FROM_TOP:
                        smoothScrollToY(getPaddingTop());
                        break;
                    default:
                        break;
                }
            }
        }

        @Override
        public void onViewDragStateChanged(int state) {
            super.onViewDragStateChanged(state);
            if (state == ViewDragHelper.STATE_IDLE) {
                if (mFraction == 0) {
                    onFinish(false);
                } else if (mFraction == 1) {
                    onFinish(true);
                }
            }
        }

        @Override
        public int getViewHorizontalDragRange(@NonNull View child) {
            return mWidth;
        }

        @Override
        public int getViewVerticalDragRange(@NonNull View child) {
            return mHeight;
        }

        @Override
        public void onEdgeTouched(int edgeFlags, int pointerId) {
            super.onEdgeTouched(edgeFlags, pointerId);
            //边缘Touch状态 开始滑动
            mTouchedEdge = edgeFlags;
        }
    }

    protected void onSwiping() {
        if (mTakeOverActivityEnterAnimator != null) {
            mTakeOverActivityEnterAnimator.pause();
        }
        if (mTakeOverActivityExitAnimator != null) {
            mTakeOverActivityExitAnimator.pause();
        }
        invalidate();
        if (mPreviousChildView != null) {
            if (mCurrentActivity.isFinishing() || mCurrentActivity.isDestroyed()) {
                getNonNullSwipeBackTransformer().transform(mCurrentChildView, mPreviousChildView, mFraction, mSwipeBackDirection);
            } else {
                getNonNullSwipeBackTransformer().transform(mCurrentChildView, mPreviousChildView, mFraction, mSwipeBackDirection);
            }
        }
        if (mSwipeBackListener != null) {
            mSwipeBackListener.onSwiping(mCurrentActivity, mCurrentChildView, mPreviousChildView, mFraction, mSwipeBackFactor, mSwipeBackDirection);
        }
    }

    private boolean mBackSuccess = false;

    public boolean isBackSuccess() {
        return mBackSuccess;
    }

    protected void onFinish(boolean backSuccess) {
        mActivitySwiping = false;
        if (backSuccess) {
            mBackSuccess = true;
            finish();
        } else {
            if (!mTakeOverActivityEnterExitAnim) {
                setActivityTranslucent(false);
            }
            mFraction = 1;
            mLeftOffset = 0;
            mTopOffset = 0;
            getNonNullSwipeBackTransformer().transform(mCurrentChildView, mPreviousChildView, mFraction, mSwipeBackDirection);
            requestLayout();
        }
        if (mSwipeBackListener != null) {
            mSwipeBackListener.onFinish(mCurrentActivity, mCurrentChildView, mPreviousChildView, backSuccess, mSwipeBackDirection);
        }
    }

    public interface SwipeBackListener {
        void onSwiping(Activity currentActivity, View currentView, View previousView, float swipeBackFraction, float swipeBackFactor, @SwipeBackDirection int swipeDirection);

        void onFinish(Activity currentActivity, View currentView, View previousView, boolean backSuccess, @SwipeBackDirection int swipeDirection);
    }

    public interface SwipeBackTransformer {
        void transform(View currentView, View previousView, float fraction, @SwipeBackDirection int swipeDirection);
    }
}
