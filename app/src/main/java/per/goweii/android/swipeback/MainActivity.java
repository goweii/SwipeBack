package per.goweii.android.swipeback;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends BaseSwipeBackActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void startNormalActivity(View view) {
        startActivity(new Intent(this, SwipeBackNormalActivity.class));
        overridePendingTransition(R.anim.swipeback_activity_open_right_in,
                R.anim.swipeback_activity_open_left_out);
    }

}
