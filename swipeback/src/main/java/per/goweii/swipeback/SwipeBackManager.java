package per.goweii.swipeback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

class SwipeBackManager {
    private static SwipeBackManager INSTANCE = null;
    private final List<SwipeBackNode> mNodes = new ArrayList<>();

    private SwipeBackManager(Application application) {
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks());
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

    public boolean isRootNode(@NonNull SwipeBackNode node) {
        return mNodes.indexOf(node) == 0;
    }

    public boolean isRootNode(@NonNull Activity activity) {
        if (mNodes.isEmpty()) return false;
        SwipeBackNode node = mNodes.get(0);
        if (node == null) return false;
        return node.getActivity() == activity;
    }

    @Nullable
    public SwipeBackNode findPreviousNode(@NonNull SwipeBackNode node) {
        if (mNodes.isEmpty()) return null;
        int index = mNodes.indexOf(node);
        if (index < 1) return null;
        return mNodes.get(index - 1);
    }

    @Nullable
    public SwipeBackNode findFirstNode() {
        if (mNodes.isEmpty()) return null;
        return mNodes.get(mNodes.size() - 1);
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

    private void addNode(@NonNull Activity activity) {
        SwipeBackNode node = new SwipeBackNode(activity);
        mNodes.add(node);
    }

    private void removeNode(@NonNull Activity activity) {
        for (int i = mNodes.size() - 1; i >= 0; i--) {
            SwipeBackNode node = mNodes.get(i);
            if (node.getActivity() == activity) {
                mNodes.remove(i);
                break;
            }
        }
    }

    private class ActivityLifecycleCallbacks implements Application.ActivityLifecycleCallbacks {
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
}
