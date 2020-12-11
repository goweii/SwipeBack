package per.goweii.swipeback;

import androidx.annotation.FloatRange;

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
    void onSwiping(@SwipeBackDirection int direction,
                   @FloatRange(from = 0F, to = 1F) float fraction);

    /**
     * 滑动结束
     *
     * @param direction 滑动关闭的方向，0表示复位
     */
    void onEnd(@SwipeBackDirection int direction);
}