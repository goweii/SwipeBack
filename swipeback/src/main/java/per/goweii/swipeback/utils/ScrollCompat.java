package per.goweii.swipeback.utils;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.view.ScrollingView;

public class ScrollCompat {
    public static final int SCROLL_DIRECTION_UP = 1;
    public static final int SCROLL_DIRECTION_DOWN = 2;
    public static final int SCROLL_DIRECTION_LEFT = 3;
    public static final int SCROLL_DIRECTION_RIGHT = 4;

    public static boolean hasViewCanScrollUp(@NonNull View view, float x, float y) {
        return hasViewCanScrollDirection(view, x, y, ScrollCompat.SCROLL_DIRECTION_UP);
    }

    public static boolean hasViewCanScrollDown(@NonNull View view, float x, float y) {
        return hasViewCanScrollDirection(view, x, y, ScrollCompat.SCROLL_DIRECTION_DOWN);
    }

    public static boolean hasViewCanScrollLeft(@NonNull View view, float x, float y) {
        return hasViewCanScrollDirection(view, x, y, ScrollCompat.SCROLL_DIRECTION_LEFT);
    }

    public static boolean hasViewCanScrollRight(@NonNull View view, float x, float y) {
        return hasViewCanScrollDirection(view, x, y, ScrollCompat.SCROLL_DIRECTION_RIGHT);
    }

    public static boolean hasViewCanScrollDirection(@NonNull View view, float x, float y, int direction) {
        if (!isPointInView(view, x, y)) {
            return false;
        }
        if (ScrollCompat.canScrollDirection(view, direction)) {
            return true;
        }
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                if (hasViewCanScrollDirection(child, x, y, direction)) {
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean canScrollDirection(@NonNull View view, int direction) {
        switch (direction) {
            case SCROLL_DIRECTION_UP:
                return canScrollUp(view);
            case SCROLL_DIRECTION_DOWN:
                return canScrollDown(view);
            case SCROLL_DIRECTION_LEFT:
                return canScrollLeft(view);
            case SCROLL_DIRECTION_RIGHT:
                return canScrollRight(view);
        }
        return false;
    }

    public static boolean canScrollUp(@NonNull View view) {
        return ScrollCompat.canScrollVertically(view, -1);
    }

    public static boolean canScrollDown(@NonNull View view) {
        return ScrollCompat.canScrollVertically(view, 1);
    }

    public static boolean canScrollLeft(@NonNull View view) {
        return ScrollCompat.canScrollHorizontally(view, -1);
    }

    public static boolean canScrollRight(@NonNull View view) {
        return ScrollCompat.canScrollHorizontally(view, 1);
    }

    private static boolean canScrollHorizontally(@NonNull View v, int direction) {
        if (v instanceof ScrollingView) {
            return canScrollingViewScrollHorizontally((ScrollingView) v, direction);
        } else {
            return v.canScrollHorizontally(direction);
        }
    }

    private static boolean canScrollVertically(@NonNull View v, int direction) {
        if (v instanceof ScrollingView) {
            return canScrollingViewScrollVertically((ScrollingView) v, direction);
        } else {
            return v.canScrollVertically(direction);
        }
    }

    private static boolean canScrollingViewScrollHorizontally(@NonNull ScrollingView view, int direction) {
        final int offset = view.computeHorizontalScrollOffset();
        final int range = view.computeHorizontalScrollRange() - view.computeHorizontalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }

    private static boolean canScrollingViewScrollVertically(@NonNull ScrollingView view, int direction) {
        final int offset = view.computeVerticalScrollOffset();
        final int range = view.computeVerticalScrollRange() - view.computeVerticalScrollExtent();
        if (range == 0) return false;
        if (direction < 0) {
            return offset > 0;
        } else {
            return offset < range - 1;
        }
    }

    public static boolean isPointInView(View view, float x, float y) {
        Rect localRect = new Rect();
        view.getGlobalVisibleRect(localRect);
        return localRect.contains((int) x, (int) y);
    }
}
