package per.goweii.swipeback;

import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author CuiZhen
 * @date 2019/5/21
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
@IntDef({SwipeBackDirection.FROM_LEFT, SwipeBackDirection.FROM_TOP, SwipeBackDirection.FROM_RIGHT, SwipeBackDirection.FROM_BOTTOM})
@Retention(RetentionPolicy.SOURCE)
public @interface SwipeBackDirection {
    int FROM_LEFT = 1 << 0;
    int FROM_RIGHT = 1 << 1;
    int FROM_TOP = 1 << 2;
    int FROM_BOTTOM = 1 << 3;
}
