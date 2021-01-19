package per.goweii.swipeback;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityOptions;
import android.content.res.TypedArray;
import android.os.Build;

import androidx.annotation.NonNull;

import java.lang.reflect.Method;

public class TranslucentCompat {
    public static boolean isActivityThemeTranslucent(@NonNull Activity activity) {
        try {
            TypedArray typedArray = activity.getTheme().obtainStyledAttributes(new int[]{android.R.attr.windowIsTranslucent});
            boolean windowIsTranslucent = typedArray.getBoolean(0, false);
            typedArray.recycle();
            return windowIsTranslucent;
        } catch (Throwable ignore) {
            return false;
        }
    }

    public static void convertActivityFromTranslucent(@NonNull Activity activity) {
        ConvertActivityFromTranslucent.convert(activity);
    }

    public static void convertActivityToTranslucent(@NonNull Activity activity) {
        ConvertActivityToTranslucent.convert(activity);
    }

    private static class ConvertActivityFromTranslucent {
        private static Method sConvertFromTranslucentMethod = null;

        @SuppressWarnings("JavaReflectionMemberAccess")
        private static void convert(@NonNull Activity activity) {
            try {
                if (sConvertFromTranslucentMethod == null) {
                    sConvertFromTranslucentMethod = Activity.class.getDeclaredMethod("convertFromTranslucent");
                    sConvertFromTranslucentMethod.setAccessible(true);
                }
                sConvertFromTranslucentMethod.invoke(activity);
            } catch (Throwable ignore) {
            }
        }
    }

    private static class ConvertActivityToTranslucent {
        private static Method sConvertToTranslucentMethodBeforeL = null;
        private static Method sConvertToTranslucentMethodAfterL = null;
        private static Object sConvertToTranslucentOptionsAfterL = null;

        private static void convert(@NonNull Activity activity) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                convertActivityToTranslucentAfterL(activity);
            } else {
                convertActivityToTranslucentBeforeL(activity);
            }
        }

        @SuppressWarnings("JavaReflectionMemberAccess")
        private static void convertActivityToTranslucentBeforeL(@NonNull Activity activity) {
            try {
                if (sConvertToTranslucentMethodBeforeL == null) {
                    Class<?>[] classes = Activity.class.getDeclaredClasses();
                    Class<?> translucentConversionListenerClazz = null;
                    for (Class<?> clazz : classes) {
                        if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                            translucentConversionListenerClazz = clazz;
                        }
                    }
                    sConvertToTranslucentMethodBeforeL = Activity.class.getDeclaredMethod("convertToTranslucent", translucentConversionListenerClazz);
                    sConvertToTranslucentMethodBeforeL.setAccessible(true);
                }
                sConvertToTranslucentMethodBeforeL.invoke(activity, new Object[]{null});
            } catch (Throwable ignore) {
            }
        }

        @SuppressLint("DiscouragedPrivateApi")
        @SuppressWarnings({"JavaReflectionMemberAccess"})
        private static void convertActivityToTranslucentAfterL(@NonNull Activity activity) {
            try {
                if (sConvertToTranslucentMethodAfterL == null) {
                    Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
                    getActivityOptions.setAccessible(true);
                    sConvertToTranslucentOptionsAfterL = getActivityOptions.invoke(activity);
                    Class<?>[] classes = Activity.class.getDeclaredClasses();
                    Class<?> translucentConversionListenerClazz = null;
                    for (Class<?> clazz : classes) {
                        if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                            translucentConversionListenerClazz = clazz;
                        }
                    }
                    sConvertToTranslucentMethodAfterL = Activity.class.getDeclaredMethod("convertToTranslucent", translucentConversionListenerClazz, ActivityOptions.class);
                    sConvertToTranslucentMethodAfterL.setAccessible(true);
                }
                sConvertToTranslucentMethodAfterL.invoke(activity, null, sConvertToTranslucentOptionsAfterL);
            } catch (Throwable ignore) {
            }
        }
    }
}
