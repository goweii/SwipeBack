package per.goweii.swipeback;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class SwipeBackActivity extends AppCompatActivity {

    protected SwipeBackHelper mSwipeBackHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSwipeBackHelper = SwipeBackHelper.inject(this);
        mSwipeBackHelper.setTakeOverActivityEnterExitAnim(supportTakeOverActivityEnterExitAnim());
        mSwipeBackHelper.setSwipeBackEnable(supportSwipeBack());
        mSwipeBackHelper.setSwipeBackOnlyEdge(supportOnlyEdge());
        mSwipeBackHelper.setSwipeBackForceEdge(supportForceEdge());
        mSwipeBackHelper.setSwipeDirection(supportSwipeDirection());
        mSwipeBackHelper.getSwipeBackLayout().setShadowStartColor(0);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mSwipeBackHelper.onPostCreate();
    }

    @Override
    public void onEnterAnimationComplete() {
        super.onEnterAnimationComplete();
        mSwipeBackHelper.onEnterAnimationComplete();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSwipeBackHelper.onDestroy();
    }

    @Override
    public void finish() {
        if (mSwipeBackHelper.finish()) {
            super.finish();
        }
    }

    protected boolean supportSwipeBack() {
        return true;
    }

    protected boolean supportOnlyEdge() {
        return false;
    }

    protected boolean supportForceEdge() {
        return true;
    }

    protected boolean supportTakeOverActivityEnterExitAnim() {
        return false;
    }

    @SwipeBackDirection
    protected int supportSwipeDirection() {
        return SwipeBackDirection.FROM_LEFT;
    }
}
