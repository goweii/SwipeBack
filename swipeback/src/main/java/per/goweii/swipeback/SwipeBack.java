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

/**
 * 你应该在{@link Application#onCreate()}方法中调用{@link SwipeBack#init(Application)}完成初始化，
 * 并自定义你的全局配置，而无需在每个Activity中单独配置。当然如果你需要一个Activity拥有不同于全局配置的
 * 效果，你可以使用实现{@link SwipeBackAbility}内部接口的方式实现。
 */
public final class SwipeBack {
    private static final SwipeBack sInstance = new SwipeBack();

    @NonNull
    private SwipeBackDirection mSwipeBackDirection = SwipeBackDirection.RIGHT;
    @Nullable
    private SwipeBackTransformer mSwipeBackTransformer = null;
    private boolean mSwipeBackOnlyEdge = false;
    private boolean mSwipeBackForceEdge = true;
    @ColorInt
    private int mShadowColor = ColorUtils.setAlphaComponent(Color.BLACK, 50);
    @Px
    private int mShadowSize = 32;
    @IntRange(from = 0, to = 255)
    private int mMaskAlpha = 150;
    private boolean mRootSwipeBackEnable = false;

    private SwipeBack() {
    }

    public static SwipeBack getInstance() {
        return sInstance;
    }

    /**
     * 你应该在{@link Application#onCreate()}方法中调用完成初始化
     *
     * @param application Application
     */
    public void init(@NonNull Application application) {
        SwipeBackManager.init(application);
        mShadowSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12F, application.getResources().getDisplayMetrics());
    }

    /**
     * 设置根Activity是否支持滑动返回，默认为false
     *
     * @param enable true/false
     */
    public void setRootSwipeBackEnable(boolean enable) {
        this.mRootSwipeBackEnable = enable;
    }

    /**
     * 获取根Activity是否支持滑动返回
     *
     * @return true/false
     */
    public boolean isRootSwipeBackEnable() {
        return mRootSwipeBackEnable;
    }

    /**
     * 设置滑动返回的方向，共支持4个方向和一个禁用状态
     * 默认只{@link SwipeBackDirection#RIGHT}
     *
     * @param swipeBackDirection 滑动方向或者禁用
     */
    public void setSwipeBackDirection(@NonNull SwipeBackDirection swipeBackDirection) {
        this.mSwipeBackDirection = swipeBackDirection;
    }

    /**
     * 获取滑动返回的方向
     *
     * @return SwipeBackDirection
     */
    @NonNull
    public SwipeBackDirection getSwipeBackDirection() {
        return mSwipeBackDirection;
    }

    /**
     * 设置下层联动效果
     *
     * @param swipeBackTransformer SwipeBackTransformer
     */
    public void setSwipeBackTransformer(@Nullable SwipeBackTransformer swipeBackTransformer) {
        this.mSwipeBackTransformer = swipeBackTransformer;
    }

    /**
     * 获取联动效果SwipeBackTransformer
     *
     * @return SwipeBackTransformer
     */
    @Nullable
    public SwipeBackTransformer getSwipeBackTransformer() {
        return mSwipeBackTransformer;
    }

    /**
     * 设置触摸边缘时是否强制触发滑动返回
     * 这个在下面这种场景时可能会非常有用：你的布局是一个拥有多个页面的ViewPager，并且现在页面为于中间位置，如果
     * 不支持边缘强制滑动返回，则需要将ViewPager滑动到第一页才能触发滑动返回。
     *
     * @param swipeBackForceEdge true/false
     */
    public void setSwipeBackForceEdge(boolean swipeBackForceEdge) {
        this.mSwipeBackForceEdge = swipeBackForceEdge;
    }

    /**
     * 触摸边缘时是否强制触发滑动返回
     */
    public boolean isSwipeBackForceEdge() {
        return mSwipeBackForceEdge;
    }

    /**
     * 这是是否仅边缘触摸时可以触发滑动返回
     * 这个在下面这种场景时可能会非常有用：你的页面是一个WebView，并且WebView中会包含一些与滑动方向冲突的div，为了
     * 避免这个冲突，你可以将这个页面设置成只有边缘可以触发滑动返回。
     * 可见这个其实是对滑动冲突的规避，并不一定是解决。
     * 最优的是通过自定义内部View的一组滑动方向方法来适配滑动：
     * {@link android.view.View#canScrollVertically(int)}
     * {@link android.view.View#canScrollHorizontally(int)}
     * {@link android.view.View#computeVerticalScrollOffset()}
     * {@link android.view.View#computeVerticalScrollRange()}
     * {@link android.view.View#computeHorizontalScrollExtent()}
     *
     * @param swipeBackOnlyEdge true/false
     */
    @SuppressWarnings("JavadocReference")
    public void setSwipeBackOnlyEdge(boolean swipeBackOnlyEdge) {
        this.mSwipeBackOnlyEdge = swipeBackOnlyEdge;
    }

    /**
     * 是否仅触摸边缘时可滑动返回
     *
     * @return true/false
     */
    public boolean isSwipeBackOnlyEdge() {
        return mSwipeBackOnlyEdge;
    }

    /**
     * 滑动时边缘的阴影颜色，这个颜色应该是一个半透明颜色
     *
     * @param shadowColor ColorInt
     */
    public void setSwipeBackShadowColor(@ColorInt int shadowColor) {
        this.mShadowColor = shadowColor;
    }

    /**
     * 滑动时边缘的阴影颜色
     *
     * @return px
     */
    @ColorInt
    public int getSwipeBackShadowColor() {
        return mShadowColor;
    }

    /**
     * 滑动时边缘的阴影宽度
     *
     * @param shadowSize px
     */
    public void setSwipeBackShadowSize(@Px int shadowSize) {
        this.mShadowSize = shadowSize;
    }

    /**
     * 滑动时边缘的阴影宽度
     *
     * @return px
     */
    @Px
    public int getSwipeBackShadowSize() {
        return mShadowSize;
    }

    /**
     * 滑动时下层Activity遮罩不透明度
     *
     * @param maskAlpha [0, 255]，0为全透明，255为纯黑
     */
    public void setSwipeBackMaskAlpha(@IntRange(from = 0, to = 255) int maskAlpha) {
        this.mMaskAlpha = maskAlpha;
    }

    /**
     * 获取滑动时下层Activity遮罩不透明度
     *
     * @return [0, 255]，0为全透明，255为纯黑
     */
    @IntRange(from = 0, to = 255)
    public int getSwipeBackMaskAlpha() {
        return mMaskAlpha;
    }
}
