package per.goweii.android.swipeback;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import per.goweii.swipeback.SwipeBackActivity;

public class MainActivity extends SwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected boolean supportSwipeBack() {
        return false;
    }

    public void startNormalActivity(View view) {
        startActivity(new Intent(this, SwipeBackNormalActivity.class));
    }

    public void startTranslucentActivity(View view) {
        startActivity(new Intent(this, SwipeBackTranslucentActivity.class));
    }

}
