package per.goweii.swipeback;

import android.support.v4.view.ScrollingView;
import android.support.v4.view.ViewCompat;
import android.view.View;

/**
 * @author CuiZhen
 * @date 2019/6/6
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ScrollCompat {

    public static boolean canScrollHorizontally(View v, int direction) {
        if (v instanceof ScrollingView) {
            return canScrollingViewScrollHorizontally((ScrollingView) v, direction);
        } else {
            return ViewCompat.canScrollHorizontally(v, direction);
        }
    }

    public static boolean canScrollVertically(View v, int direction) {
        if (v instanceof ScrollingView) {
            return canScrollingViewScrollVertically((ScrollingView) v, direction);
        } else {
            return ViewCompat.canScrollVertically(v, direction);
        }
    }

    private static boolean canScrollingViewScrollHorizontally(ScrollingView view, int direction) {
        final int offset = view.computeHorizontalScrollOffset();
        final int range = view.computeHorizontalScrollRange() - view.computeHorizontalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }

    private static boolean canScrollingViewScrollVertically(ScrollingView view, int direction) {
        final int offset = view.computeVerticalScrollOffset();
        final int range = view.computeVerticalScrollRange() - view.computeVerticalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }
}
