package per.goweii.swipeback.utils;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.res.TypedArray;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

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
        ToConverter.convert(mActivity, new TranslucentConverter.TranslucentCallback() {
            @Override
            public void onTranslucentCallback(boolean translucent) {
                mIsTranslucent = translucent;
            }
        });
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
    }

    private static class ToConverter {
        private static boolean mInitialedConvertToTranslucent = false;
        private static Class mTranslucentConversionListenerClass = null;
        private static Method mMethodConvertToTranslucent = null;
        private static Method mMethodGetActivityOptions = null;

        private static void convert(@NonNull Activity activity, final TranslucentCallback callback) {
            if (mInitialedConvertToTranslucent && mMethodConvertToTranslucent == null) {
                if (callback != null) {
                    callback.onTranslucentCallback(false);
                }
                return;
            }
            try {
                Object translucentConversionListener = getTranslucentConversionListener(callback);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    convertActivityToTranslucentAboveL(activity, translucentConversionListener);
                } else {
                    convertActivityToTranslucentBelowL(activity, translucentConversionListener);
                }
                if (translucentConversionListener == null) {
                    if (callback != null) {
                        callback.onTranslucentCallback(false);
                    }
                }
            } catch (Throwable ignored) {
                if (callback != null) {
                    callback.onTranslucentCallback(false);
                }
            }
        }

        private static Object getTranslucentConversionListener(@Nullable final TranslucentCallback callback) throws Throwable {
            if (mTranslucentConversionListenerClass == null) {
                Class[] clazzArray = Activity.class.getDeclaredClasses();
                for (Class clazz : clazzArray) {
                    if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                        mTranslucentConversionListenerClass = clazz;
                    }
                }
            }
            if (mTranslucentConversionListenerClass == null) {
                return null;
            }
            return Proxy.newProxyInstance(
                    mTranslucentConversionListenerClass.getClassLoader(),
                    new Class[]{mTranslucentConversionListenerClass},
                    new InvocationHandler() {
                        @SuppressWarnings("SuspiciousInvocationHandlerImplementation")
                        @Override
                        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                            boolean translucent = false;
                            if (args != null && args.length == 1) {
                                translucent = (Boolean) args[0];
                            }
                            if (callback != null) {
                                callback.onTranslucentCallback(translucent);
                            }
                            return null;
                        }
                    });
        }

        private static void convertActivityToTranslucentBelowL(@NonNull Activity activity, @Nullable Object translucentConversionListener) throws Throwable {
            if (mMethodConvertToTranslucent == null) {
                mInitialedConvertToTranslucent = true;
                Method method = Activity.class.getDeclaredMethod("convertToTranslucent", mTranslucentConversionListenerClass);
                method.setAccessible(true);
                mMethodConvertToTranslucent = method;
            }
            mMethodConvertToTranslucent.invoke(activity, translucentConversionListener);
        }

        private static void convertActivityToTranslucentAboveL(@NonNull Activity activity, @Nullable Object translucentConversionListener) throws Throwable {
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
            mMethodConvertToTranslucent.invoke(activity, translucentConversionListener, options);
        }
    }

    public interface TranslucentCallback {
        void onTranslucentCallback(boolean translucent);
    }
}
