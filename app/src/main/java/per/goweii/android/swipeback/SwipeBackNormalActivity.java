package per.goweii.android.swipeback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import per.goweii.swipeback.transformer.R;
import per.goweii.swipeback.SwipeBackDirection;
import per.goweii.swipeback.SwipeBackTransformer;
import per.goweii.swipeback.transformer.ParallaxSwipeBackTransformer;
import per.goweii.swipeback.transformer.ShrinkSwipeBackTransformer;

public class SwipeBackNormalActivity extends BaseSwipeBackActivity {
    public static void start(@NonNull Activity activity,
                             @SwipeBackDirection int direction,
                             @Nullable SwipeBackTransformer transformer,
                             boolean onlyEdge,
                             boolean forceEdge) {
        Intent intent = new Intent(activity, SwipeBackNormalActivity.class);
        intent.putExtra("direction", direction);
        intent.putExtra("transformer", transformer == null ? "" : transformer.getClass().getName());
        intent.putExtra("onlyEdge", onlyEdge);
        intent.putExtra("forceEdge", forceEdge);
        final int openIn;
        final int openOut;
        if ((direction & SwipeBackDirection.RIGHT) != 0) {
            openIn = R.anim.swipeback_activity_open_right_in;
        } else if ((direction & SwipeBackDirection.BOTTOM) != 0) {
            openIn = R.anim.swipeback_activity_open_bottom_in;
        } else if ((direction & SwipeBackDirection.LEFT) != 0) {
            openIn = R.anim.swipeback_activity_open_left_in;
        } else if ((direction & SwipeBackDirection.TOP) != 0) {
            openIn = R.anim.swipeback_activity_open_top_in;
        } else {
            openIn = R.anim.swipeback_activity_open_right_in;
        }
        if (transformer instanceof ShrinkSwipeBackTransformer) {
            openOut = R.anim.swipeback_activity_open_scale_out;
        } else if (transformer instanceof ParallaxSwipeBackTransformer) {
            if ((direction & SwipeBackDirection.RIGHT) != 0) {
                openOut = R.anim.swipeback_activity_open_left_out;
            } else if ((direction & SwipeBackDirection.BOTTOM) != 0) {
                openOut = R.anim.swipeback_activity_open_top_out;
            } else if ((direction & SwipeBackDirection.LEFT) != 0) {
                openOut = R.anim.swipeback_activity_open_right_out;
            } else if ((direction & SwipeBackDirection.TOP) != 0) {
                openOut = R.anim.swipeback_activity_open_bottom_out;
            } else {
                openOut = R.anim.swipeback_activity_open_alpha_out;
            }
        } else {
            openOut = R.anim.swipeback_activity_open_alpha_out;
        }
        activity.startActivity(intent);
        activity.overridePendingTransition(openIn, openOut);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        mSwipeBackDirection = intent.getIntExtra("direction", 0);
        String transformerName = intent.getStringExtra("transformer");
        if (TextUtils.equals(transformerName, ShrinkSwipeBackTransformer.class.getName())) {
            mSwipeBackTransformer = new ShrinkSwipeBackTransformer();
        } else if (TextUtils.equals(transformerName, ParallaxSwipeBackTransformer.class.getName())) {
            mSwipeBackTransformer = new ParallaxSwipeBackTransformer();
        } else {
            mSwipeBackTransformer = null;
        }
        mSwipeBackOnlyEdge = intent.getBooleanExtra("onlyEdge", mSwipeBackOnlyEdge);
        mSwipeBackForceEdge = intent.getBooleanExtra("forceEdge", mSwipeBackForceEdge);
        super.onCreate(savedInstanceState);
    }
}
