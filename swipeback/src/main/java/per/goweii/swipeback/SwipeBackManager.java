package per.goweii.swipeback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

class SwipeBackManager implements Application.ActivityLifecycleCallbacks {
    private static SwipeBackManager INSTANCE = null;
    private final List<SwipeBackNode> mNodes = new ArrayList<>();

    private SwipeBackManager(Application application) {
        application.registerActivityLifecycleCallbacks(this);
    }

    public static SwipeBackManager getInstance() {
        if (INSTANCE == null) {
            throw new RuntimeException("需要先在Application中调用SwipeBack.init()方法完成初始化");
        }
        return INSTANCE;
    }

    public static void init(Application application) {
        if (INSTANCE == null) {
            INSTANCE = new SwipeBackManager(application);
        }
    }

    @Nullable
    public SwipeBackNode getPreviousNode(@Nullable SwipeBackNode currNode) {
        int size = mNodes.size();
        int currIndex = size - 1;
        if (currNode != null) {
            int index = mNodes.indexOf(currNode);
            if (index >= 0) {
                currIndex = index;
            }
        }
        if (currIndex < 1) {
            return null;
        }
        return mNodes.get(currIndex - 1);
    }

    public SwipeBackNode findNode(@NonNull Activity activity) {
        for (int i = mNodes.size() - 1; i >= 0; i--) {
            SwipeBackNode node = mNodes.get(i);
            if (node.getActivity() == activity) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        SwipeBackNode node = new SwipeBackNode(activity);
        mNodes.add(node);
    }

    @Override
    public void onActivityStarted(Activity activity) {
        SwipeBackNode node = findNode(activity);
        if (node != null) {
            node.inject();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {
    }

    @Override
    public void onActivityPaused(Activity activity) {
    }

    @Override
    public void onActivityStopped(Activity activity) {
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        mNodes.remove(activity);
    }
}
