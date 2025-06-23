package per.goweii.swipeback.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.res.TypedArray;
import android.os.Build;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;

@SuppressWarnings({"rawtypes", "JavaReflectionMemberAccess", "DiscouragedPrivateApi"})
public class TranslucentConverter {
    private final Activity mActivity;

    private boolean mIsTranslucent;

    public TranslucentConverter(@NonNull Activity activity) {
        this.mActivity = activity;
        this.mIsTranslucent = isThemeTranslucent();
    }

    public boolean isTranslucent() {
        return mIsTranslucent;
    }

    public boolean isThemeTranslucent() {
        try {
            //noinspection resource
            TypedArray typedArray = mActivity.getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowIsTranslucent});
            boolean windowIsTranslucent = typedArray.getBoolean(0, false);
            typedArray.recycle();
            return windowIsTranslucent;
        } catch (Throwable ignore) {
            return false;
        }
    }

    public void toTranslucent() {
        if (mIsTranslucent) return;
        ToConverter.convert(mActivity);
    }

    public void fromTranslucent() {
        if (!mIsTranslucent) return;
        FromConverter.convert(mActivity);
        this.mIsTranslucent = false;
    }

    private static class FromConverter {
        private static boolean mInitialedConvertFromTranslucent = false;
        private static Method mMethodConvertFromTranslucent = null;

        private static void convert(@NonNull Activity activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.setTranslucent(false);
                int[] animations = getActivityCloseAnimation(activity);
                activity.overridePendingTransition(animations[0], animations[1]);
                return;
            }
            if (mInitialedConvertFromTranslucent && mMethodConvertFromTranslucent == null) {
                return;
            }
            try {
                if (mMethodConvertFromTranslucent == null) {
                    mInitialedConvertFromTranslucent = true;
                    Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
                    method.setAccessible(true);
                    mMethodConvertFromTranslucent = method;
                }
                mMethodConvertFromTranslucent.invoke(activity);
            } catch (Throwable ignored) {
            }
        }

        private static int[] getActivityCloseAnimation(@NonNull Activity activity) {
            int enterAnim = 0;
            int exitAnim = 0;
            try {
                //noinspection resource
                TypedArray typedArray = activity.getTheme().obtainStyledAttributes(new int[]{
                        android.R.attr.activityCloseEnterAnimation,
                        android.R.attr.activityCloseExitAnimation
                });
                enterAnim = typedArray.getResourceId(0, 0);
                exitAnim = typedArray.getResourceId(1, 0);
                typedArray.recycle();
            } catch (Throwable ignore) {
            }
            return new int[]{enterAnim, exitAnim};
        }
    }

    private static class ToConverter {
        private static boolean mInitialedConvertToTranslucent = false;
        private static Class mTranslucentConversionListenerClass = null;
        private static Method mMethodConvertToTranslucent = null;
        private static Method mMethodGetActivityOptions = null;

        private static void convert(@NonNull Activity activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                activity.setTranslucent(true);
                activity.overridePendingTransition(0, 0);
                return;
            }
            if (mInitialedConvertToTranslucent && mMethodConvertToTranslucent == null) {
                return;
            }
            try {
                if (mTranslucentConversionListenerClass == null) {
                    Class[] clazzArray = Activity.class.getDeclaredClasses();
                    for (Class clazz : clazzArray) {
                        if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                            mTranslucentConversionListenerClass = clazz;
                        }
                    }
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    convertActivityToTranslucentAboveL(activity);
                } else {
                    convertActivityToTranslucentBelowL(activity);
                }
            } catch (Throwable ignored) {
            }
        }

        private static void convertActivityToTranslucentBelowL(@NonNull Activity activity) throws Throwable {
            if (mMethodConvertToTranslucent == null) {
                mInitialedConvertToTranslucent = true;
                Method method = Activity.class.getDeclaredMethod("convertToTranslucent", mTranslucentConversionListenerClass);
                method.setAccessible(true);
                mMethodConvertToTranslucent = method;
            }
            mMethodConvertToTranslucent.invoke(activity, (Object) null);
        }

        private static void convertActivityToTranslucentAboveL(@NonNull Activity activity) throws Throwable {
            if (mMethodConvertToTranslucent == null) {
                mInitialedConvertToTranslucent = true;
                Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
                getActivityOptions.setAccessible(true);
                mMethodGetActivityOptions = getActivityOptions;
                Method method = Activity.class.getDeclaredMethod("convertToTranslucent", mTranslucentConversionListenerClass, ActivityOptions.class);
                method.setAccessible(true);
                mMethodConvertToTranslucent = method;
            }
            Object options = mMethodGetActivityOptions.invoke(activity);
            mMethodConvertToTranslucent.invoke(activity, null, options);
        }
    }
}
