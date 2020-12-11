package per.goweii.android.swipeback;

import android.os.Bundle;

public class SwipeBackTranslucentActivity extends BaseSwipeBackActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setAlpha(0.8F);
    }
}

