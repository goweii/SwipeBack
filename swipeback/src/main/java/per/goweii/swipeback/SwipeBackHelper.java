package per.goweii.swipeback;

import android.app.Activity;
import android.view.View;

public class SwipeBackHelper {

    private Activity mActivity;
    private SwipeBackLayout mSwipeBackLayout;

    private SwipeBackHelper(Activity activity) {
        mActivity = activity;
        mSwipeBackLayout = new SwipeBackLayout(mActivity);
    }

    public static SwipeBackHelper inject(Activity activity) {
        return new SwipeBackHelper(activity);
    }

    public void onPostCreate() {
        mSwipeBackLayout.attachTo(mActivity);
    }

    public void onDestroy() {
        mActivity = null;
        mSwipeBackLayout = null;
    }

    public void onEnterAnimationComplete() {
        if (!mSwipeBackLayout.isTakeOverActivityEnterExitAnim()) {
            if (!mSwipeBackLayout.isActivitySwiping()) {
                mSwipeBackLayout.setActivityTranslucent(false);
            }
//            if (mSwipeBackLayout.getPreviousChildView() != null) {
//                mSwipeBackLayout.setActivityTranslucent(true);
//            }
        }
    }

    public boolean finish() {
        if (mSwipeBackLayout.isTakeOverActivityEnterExitAnim()) {
            mSwipeBackLayout.startExitAnim();
            return false;
        } else {
            if (mSwipeBackLayout.isActivitySwiping()) {
                return false;
            } else {
//                if (mSwipeBackLayout.isBackSuccess()) {
//                    return true;
//                } else {
//                    mSwipeBackLayout.setActivityTranslucent(false);
//                }
                return true;
            }
        }
    }

    public View findViewById(int id) {
        if (mSwipeBackLayout != null) {
            return mSwipeBackLayout.findViewById(id);
        }
        return null;
    }

    public SwipeBackLayout getSwipeBackLayout() {
        return mSwipeBackLayout;
    }

    public void setTakeOverActivityEnterExitAnim(boolean enable) {
        mSwipeBackLayout.setTakeOverActivityEnterExitAnim(enable);
    }

    public boolean isTakeOverActivityEnterExitAnim() {
        return mSwipeBackLayout.isTakeOverActivityEnterExitAnim();
    }

    public void setSwipeBackEnable(boolean enable) {
        mSwipeBackLayout.setSwipeBackEnable(enable);
    }

    public boolean isSwipeBackEnable() {
        return mSwipeBackLayout.isSwipeBackEnable();
    }

    public void setSwipeBackOnlyEdge(boolean enable) {
        mSwipeBackLayout.setSwipeBackOnlyEdge(enable);
    }

    public boolean isSwipeBackOnlyEdge() {
        return mSwipeBackLayout.isSwipeBackOnlyEdge();
    }

    public void setSwipeBackForceEdge(boolean enable) {
        mSwipeBackLayout.setSwipeBackForceEdge(enable);
    }

    public boolean isSwipeBackForceEdge() {
        return mSwipeBackLayout.isSwipeBackForceEdge();
    }

    public void setSwipeBackDirection(@SwipeBackDirection int direction) {
        mSwipeBackLayout.setSwipeBackDirection(direction);
    }

    @SwipeBackDirection
    public int getSwipeBackDirection() {
        return mSwipeBackLayout.getSwipeBackDirection();
    }
}
