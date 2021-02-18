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
public class ActivityTranslucentConverter {
    private final Activity mActivity;
    private final ToConverter mToConverter;
    private final FromConverter mFromConverter;

    private boolean mIsTranslucent;

    public ActivityTranslucentConverter(@NonNull Activity activity) {
        mToConverter = new ToConverter();
        mFromConverter = new FromConverter();
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
        if (mIsTranslucent) {
            return;
        }
        mToConverter.convert(new ActivityTranslucentConverter.TranslucentCallback() {
            @Override
            public void onTranslucentCallback(boolean translucent) {
                mIsTranslucent = translucent;
            }
        });
    }

    public void fromTranslucent() {
        if (!mIsTranslucent) {
            return;
        }
        mFromConverter.convert();
        this.mIsTranslucent = false;
    }

    private class FromConverter {
        private boolean mInitialedConvertFromTranslucent = false;
        private Method mMethodConvertFromTranslucent = null;

        private void convert() {
            try {
                if (mMethodConvertFromTranslucent == null) {
                    if (mInitialedConvertFromTranslucent) {
                        return;
                    }
                    mInitialedConvertFromTranslucent = true;
                    Method method = Activity.class.getDeclaredMethod("convertFromTranslucent");
                    method.setAccessible(true);
                    mMethodConvertFromTranslucent = method;
                }
                mMethodConvertFromTranslucent.invoke(mActivity);
            } catch (Throwable ignored) {
            }
        }
    }

    private class ToConverter {
        private boolean mInitialedConvertToTranslucent = false;
        private Class mTranslucentConversionListenerClass = null;
        private Method mMethodConvertToTranslucent = null;
        private Method mMethodGetActivityOptions = null;

        private void convert(final TranslucentCallback callback) {
            if (mInitialedConvertToTranslucent && mMethodConvertToTranslucent == null) {
                if (callback != null) {
                    callback.onTranslucentCallback(false);
                }
                return;
            }
            try {
                Object translucentConversionListener = getTranslucentConversionListener(callback);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    convertActivityToTranslucentAboveL(translucentConversionListener);
                } else {
                    convertActivityToTranslucentBelowL(translucentConversionListener);
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

        private Object getTranslucentConversionListener(
                @Nullable final TranslucentCallback callback
        ) throws Throwable {
            if (mTranslucentConversionListenerClass == null) {
                Class[] clazzArray = Activity.class.getDeclaredClasses();
                for (Class clazz : clazzArray) {
                    if (clazz.getSimpleName().contains("TranslucentConversionListener")) {
                        mTranslucentConversionListenerClass = clazz;
                    }
                }
            }
            if (mTranslucentConversionListenerClass != null) {
                InvocationHandler invocationHandler = new InvocationHandler() {
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
                };
                return Proxy.newProxyInstance(mTranslucentConversionListenerClass.getClassLoader(), new Class[]{mTranslucentConversionListenerClass}, invocationHandler);
            }
            return null;
        }

        private void convertActivityToTranslucentBelowL(
                @Nullable Object translucentConversionListener
        ) throws Throwable {
            if (mMethodConvertToTranslucent == null) {
                mInitialedConvertToTranslucent = true;
                Method method = Activity.class.getDeclaredMethod("convertToTranslucent", mTranslucentConversionListenerClass);
                method.setAccessible(true);
                mMethodConvertToTranslucent = method;
            }
            mMethodConvertToTranslucent.invoke(mActivity, translucentConversionListener);
        }

        private void convertActivityToTranslucentAboveL(
                @Nullable Object translucentConversionListener
        ) throws Throwable {
            if (mMethodConvertToTranslucent == null) {
                mInitialedConvertToTranslucent = true;
                Method getActivityOptions = Activity.class.getDeclaredMethod("getActivityOptions");
                getActivityOptions.setAccessible(true);
                mMethodGetActivityOptions = getActivityOptions;
                Method method = Activity.class.getDeclaredMethod("convertToTranslucent", mTranslucentConversionListenerClass, ActivityOptions.class);
                method.setAccessible(true);
                mMethodConvertToTranslucent = method;
            }
            Object options = mMethodGetActivityOptions.invoke(mActivity);
            mMethodConvertToTranslucent.invoke(mActivity, translucentConversionListener, options);
        }
    }

    public interface TranslucentCallback {
        void onTranslucentCallback(boolean translucent);
    }
}
