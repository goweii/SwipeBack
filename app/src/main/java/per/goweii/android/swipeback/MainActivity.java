package per.goweii.android.swipeback;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void startNormalActivity(View view) {
        startActivity(new Intent(this, SwipeBackNormalActivity.class));
        overridePendingTransition(R.anim.swipeback_activity_open_right_in,
                R.anim.swipeback_activity_open_left_out);
    }

    public void startTranslucentActivity(View view) {
        startActivity(new Intent(this, SwipeBackTranslucentActivity.class));
    }

}
