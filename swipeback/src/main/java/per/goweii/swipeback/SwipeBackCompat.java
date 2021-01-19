package per.goweii.swipeback;

import android.graphics.Rect;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AbsListView;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

import androidx.core.view.ScrollingView;
import androidx.core.view.ViewCompat;
import androidx.viewpager.widget.ViewPager;

import java.util.ArrayList;
import java.util.List;

import per.goweii.swipeback.utils.ScrollCompat;

public class SwipeBackCompat {

    @Deprecated
    public static boolean canViewScrollUp(View view, float x, float y, boolean defaultValueForNull) {
        if (view == null || !contains(view, x, y)) {
            return defaultValueForNull;
        }
        return ViewCompat.canScrollVertically(view, -1);
    }

    public static boolean canViewScrollUp(List<View> views, float x, float y, boolean defaultValueForNull) {
        if (views == null) {
            return defaultValueForNull;
        }
        List<View> contains = contains(views, x, y);
        if (contains == null) {
            return defaultValueForNull;
        }
        boolean canViewScroll = false;
        for (int i = contains.size() - 1; i >= 0; i--) {
            canViewScroll = ScrollCompat.canScrollVertically(contains.get(i), -1);
            if (canViewScroll) {
                break;
            }
        }
        return canViewScroll;
    }

    @Deprecated
    public static boolean canViewScrollDown(View view, float x, float y, boolean defaultValueForNull) {
        if (view == null || !contains(view, x, y)) {
            return defaultValueForNull;
        }
        return ViewCompat.canScrollVertically(view, 1);
    }

    public static boolean canViewScrollDown(List<View> views, float x, float y, boolean defaultValueForNull) {
        if (views == null) {
            return defaultValueForNull;
        }
        List<View> contains = contains(views, x, y);
        if (contains == null) {
            return defaultValueForNull;
        }
        boolean canViewScroll = false;
        for (int i = contains.size() - 1; i >= 0; i--) {
            canViewScroll = ScrollCompat.canScrollVertically(contains.get(i), 1);
            if (canViewScroll) {
                break;
            }
        }
        return canViewScroll;
    }

    @Deprecated
    public static boolean canViewScrollRight(View view, float x, float y, boolean defaultValueForNull) {
        if (view == null || !contains(view, x, y)) {
            return defaultValueForNull;
        }
        return ViewCompat.canScrollHorizontally(view, -1);
    }

    public static boolean canViewScrollRight(List<View> views, float x, float y, boolean defaultValueForNull) {
        if (views == null) {
            return defaultValueForNull;
        }
        List<View> contains = contains(views, x, y);
        if (contains == null) {
            return defaultValueForNull;
        }
        boolean canViewScroll = false;
        for (int i = contains.size() - 1; i >= 0; i--) {
            canViewScroll = ScrollCompat.canScrollHorizontally(contains.get(i), 1);
            if (canViewScroll) {
                break;
            }
        }
        return canViewScroll;
    }

    @Deprecated
    public static boolean canViewScrollLeft(View view, float x, float y, boolean defaultValueForNull) {
        if (view == null || !contains(view, x, y)) {
            return defaultValueForNull;
        }
        return ViewCompat.canScrollHorizontally(view, 1);
    }

    public static boolean canViewScrollLeft(List<View> views, float x, float y, boolean defaultValueForNull) {
        if (views == null) {
            return defaultValueForNull;
        }
        List<View> contains = contains(views, x, y);
        if (contains == null) {
            return defaultValueForNull;
        }
        boolean canViewScroll = false;
        for (int i = contains.size() - 1; i >= 0; i--) {
            canViewScroll = ScrollCompat.canScrollHorizontally(contains.get(i), -1);
            if (canViewScroll) {
                break;
            }
        }
        return canViewScroll;
    }

    @Deprecated
    public static View findAllScrollViews(ViewGroup viewGroup) {
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view.getVisibility() != View.VISIBLE) {
                continue;
            }
            if (isScrollableView(view)) {
                return view;
            }
            if (view instanceof ViewGroup) {
                view = findAllScrollViews((ViewGroup) view);
                if (view != null) {
                    return view;
                }
            }
        }
        return null;
    }

    public static List<View> findAllScrollViews2(ViewGroup viewGroup) {
        List<View> views = new ArrayList<>();
        for (int i = 0; i < viewGroup.getChildCount(); i++) {
            View view = viewGroup.getChildAt(i);
            if (view.getVisibility() != View.VISIBLE) {
                continue;
            }
            if (isScrollableView(view)) {
                views.add(view);
            }
            if (view instanceof ViewGroup) {
                views.addAll(findAllScrollViews2((ViewGroup) view));
            }
        }
        return views;
    }

    public static boolean isScrollableView(View view) {
        return view instanceof ScrollView
                || view instanceof HorizontalScrollView
                || view instanceof AbsListView
                || view instanceof ViewPager
                || view instanceof WebView
                || view instanceof ScrollingView;
    }

    public static boolean contains(View view, float x, float y) {
        Rect localRect = new Rect();
        view.getGlobalVisibleRect(localRect);
        return localRect.contains((int) x, (int) y);
    }

    public static List<View> contains(List<View> views, float x, float y) {
        if (views == null) {
            return null;
        }
        List<View> contains = new ArrayList<>(views.size());
        for (int i = views.size() - 1; i >= 0; i--) {
            View v = views.get(i);
            Rect localRect = new Rect();
            int[] l = new int[2];
            v.getLocationOnScreen(l);
            localRect.set(l[0], l[1], l[0] + v.getWidth(), l[1] + v.getHeight());
            if (localRect.contains((int) x, (int) y)) {
                contains.add(v);
            }
        }
        return contains;
    }
}
