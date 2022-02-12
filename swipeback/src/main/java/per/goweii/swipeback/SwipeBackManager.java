package per.goweii.swipeback;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

class SwipeBackManager {
    private static SwipeBackManager sInstance = null;
    private final List<SwipeBackNode> mNodes = new ArrayList<>();

    private SwipeBackManager(@NonNull Application application) {
        application.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks());
    }

    @NonNull
    public static SwipeBackManager getInstance() {
        if (sInstance == null) {
            throw new RuntimeException("需要先在Application中调用SwipeBack.init()方法完成初始化");
        }
        return sInstance;
    }

    public static void init(@NonNull Application application) {
        if (sInstance == null) {
            sInstance = new SwipeBackManager(application);
        }
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

    @Nullable
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
        int index = -1;
        for (int i = mNodes.size() - 1; i >= 0; i--) {
            SwipeBackNode node = mNodes.get(i);
            if (node.getActivity() == activity) {
                return;
            }
            // Activity重建了，更新当前节点
            if (node.getActivity().getComponentName() == activity.getComponentName()) {
                index = i;
                break;
            }
        }
        SwipeBackNode node = new SwipeBackNode(activity);
        if (index >= 0 && index < mNodes.size()) {
            mNodes.remove(index);
            mNodes.add(index, node);
        } else {
            mNodes.add(node);
        }
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
                // DecorView创建完成，执行注入
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
            SwipeBackNode node = findNode(activity);
            if (node != null) {
                // 完全不可见，恢复状态
                node.restore();
            }
        }

        @Override
        public void onActivityDestroyed(Activity activity) {
            // 如果是配置变更导致的Activity重建，则不应该移除，而应该在重建后更新
            if (!activity.isChangingConfigurations()) {
                removeNode(activity);
            }
        }

        @Override
        public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {
        }
    }
}
