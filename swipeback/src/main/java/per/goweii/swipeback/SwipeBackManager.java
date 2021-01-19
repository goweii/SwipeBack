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
    public SwipeBackNode getPreviousNode(@NonNull SwipeBackNode currNode) {
        if (mNodes.isEmpty()) return null;
        int index = mNodes.indexOf(currNode);
        if (index < 1) return null;
        return mNodes.get(index - 1);
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

    public void addNode(@NonNull Activity activity) {
        SwipeBackNode node = new SwipeBackNode(activity);
        mNodes.add(node);
    }

    public void removeNode(@NonNull Activity activity) {
        SwipeBackNode node = findNode(activity);
        if (node != null) {
            mNodes.remove(node);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {
        addNode(activity);
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
        removeNode(activity);
    }
}
