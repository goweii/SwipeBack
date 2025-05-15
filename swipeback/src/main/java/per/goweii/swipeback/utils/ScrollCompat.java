package per.goweii.swipeback.utils;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;

import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ScrollingView;

public class ScrollCompat {
    public static final int SCROLL_DIRECTION_UP = 1;
    public static final int SCROLL_DIRECTION_DOWN = 1 << 1;
    public static final int SCROLL_DIRECTION_LEFT = 1 << 2;
    public static final int SCROLL_DIRECTION_RIGHT = 1 << 3;

    private static final ScrollDirectionResult sResult = new ScrollDirectionResult();

    @NonNull
    public static ScrollDirectionResult calcScrollDirection(@NonNull View view, float x, float y) {
        sResult.reset();
        calcScrollDirection(view, x, y, sResult);
        return sResult;
    }

    /**
     * @noinspection deprecation
     */
    private static void calcScrollDirection(@NonNull final View view, final float x, final float y, @NonNull final ScrollDirectionResult result) {
        if (!isPointInView(view, x, y)) return;

        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                float cx = x + (view.getScrollX() - child.getLeft());
                float cy = y + (view.getScrollY() - child.getTop());
                calcScrollDirection(child, cx, cy, result);
            }
        }

        if (canScrollVertically(view, -1)) {
            result.direction |= SCROLL_DIRECTION_UP;
        }
        if (canScrollVertically(view, 1)) {
            result.direction |= SCROLL_DIRECTION_DOWN;
        }
        if (canScrollHorizontally(view, -1)) {
            result.direction |= SCROLL_DIRECTION_LEFT;
        }
        if (canScrollHorizontally(view, 1)) {
            result.direction |= SCROLL_DIRECTION_RIGHT;
        }
        if (view instanceof WebView) {
            WebView webView = (WebView) view;
            if (webView.getSettings().getJavaScriptEnabled() && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                result.asyncResult = true;
                //noinspection ExtractMethodRecommender
                final float wx = x / webView.getScale();
                final float wy = y / webView.getScale();
                final String script = "(function(x, y) {\n" +
                        "    console.log(x)\n" +
                        "    console.log(y)\n" +
                        "    const elements = document.elementsFromPoint(x, y);\n" +
                        "    console.log(elements);\n" +
                        "    if (!elements || elements.length === 0) return 0;\n" +
                        "    let scrollDirection = 0;\n" +
                        "    elements.forEach(e => {" +
                        "        const canScrollLeft = e.scrollWidth > e.clientWidth && e.scrollLeft > 0;\n" +
                        "        const canScrollRight = e.scrollWidth > e.clientWidth && e.scrollLeft < (e.scrollWidth - e.clientWidth);\n" +
                        "        const canScrollUp = e.scrollHeight > e.clientHeight && e.scrollTop > 0;\n" +
                        "        const canScrollDown = e.scrollHeight > e.clientHeight && e.scrollTop < (e.scrollHeight - e.clientHeight);\n" +
                        "        if (canScrollLeft) scrollDirection |= " + SCROLL_DIRECTION_LEFT + ";\n" +
                        "        if (canScrollRight) scrollDirection |= " + SCROLL_DIRECTION_RIGHT + ";\n" +
                        "        if (canScrollUp) scrollDirection |= " + SCROLL_DIRECTION_UP + ";\n" +
                        "        if (canScrollDown) scrollDirection |= " + SCROLL_DIRECTION_DOWN + ";\n" +
                        "    });\n" +
                        "    console.log(scrollDirection)\n" +
                        "    return scrollDirection;\n" +
                        "})(" + wx + "," + wy + ");";
                webView.evaluateJavascript(script, new ValueCallback<String>() {
                    @Override
                    public void onReceiveValue(String value) {
                        try {
                            sResult.direction |= Integer.parseInt(value);
                            if (result.onAsyncResult != null) {
                                result.onAsyncResult.run();
                            }
                        } catch (NumberFormatException ignored) {
                        }
                    }
                });
            }
        }
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

    public static boolean isPointInView(@NonNull View view, float x, float y) {
        return x >= 0 && x <= view.getWidth() && y >= 0 && y <= view.getHeight();
    }

    @IntDef(value = {
            SCROLL_DIRECTION_UP,
            SCROLL_DIRECTION_DOWN,
            SCROLL_DIRECTION_LEFT,
            SCROLL_DIRECTION_RIGHT
    }, flag = true)
    public @interface ScrollDirection {
    }

    public static class ScrollDirectionResult {
        @ScrollDirection
        private int direction;
        private boolean asyncResult;
        @Nullable
        private Runnable onAsyncResult;

        @ScrollDirection
        public int getDirection() {
            return direction;
        }

        public boolean hasAsyncResult() {
            return asyncResult;
        }

        public void setOnAsyncResult(@Nullable Runnable onAsyncResult) {
            this.onAsyncResult = onAsyncResult;
        }

        public boolean hasDirection(@ScrollDirection int direction) {
            return (this.direction & direction) != 0;
        }

        public void reset() {
            direction = 0;
            asyncResult = false;
            onAsyncResult = null;
        }
    }
}
