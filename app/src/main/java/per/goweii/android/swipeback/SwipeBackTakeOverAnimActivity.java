package per.goweii.android.swipeback;

import android.os.Bundle;

public class SwipeBackTakeOverAnimActivity extends BaseSwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mSwipeBackHelper.getSwipeBackLayout().setTakeOverActivityEnterExitAnim(true);
    }
}

