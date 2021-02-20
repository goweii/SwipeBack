package per.goweii.android.swipeback;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import per.goweii.swipeback.SwipeBackDirection;
import per.goweii.swipeback.SwipeBackTransformer;
import per.goweii.swipeback.transformer.ParallaxSwipeBackTransformer;
import per.goweii.swipeback.transformer.ShrinkSwipeBackTransformer;

public class SwipeBackNormalActivity extends BaseSwipeBackActivity {
    public static void start(@NonNull Activity activity,
                             @NonNull SwipeBackDirection direction,
                             @Nullable SwipeBackTransformer transformer,
                             boolean onlyEdge,
                             boolean forceEdge) {
        Intent intent = new Intent(activity, SwipeBackNormalActivity.class);
        intent.putExtra("direction", direction.name());
        intent.putExtra("transformer", transformer == null ? "" : transformer.getClass().getName());
        intent.putExtra("onlyEdge", onlyEdge);
        intent.putExtra("forceEdge", forceEdge);
        final int openIn;
        final int openOut;
        switch (direction) {
            case NONE:
            case RIGHT:
                openIn = R.anim.swipeback_activity_open_right_in;
                break;
            case BOTTOM:
                openIn = R.anim.swipeback_activity_open_bottom_in;
                break;
            case LEFT:
                openIn = R.anim.swipeback_activity_open_left_in;
                break;
            case TOP:
                openIn = R.anim.swipeback_activity_open_top_in;
                break;
            default:
                throw new IllegalArgumentException();
        }
        if (transformer instanceof ShrinkSwipeBackTransformer) {
            openOut = R.anim.swipeback_activity_open_scale_out;
        } else if (transformer instanceof ParallaxSwipeBackTransformer) {
            switch (direction) {
                case NONE:
                    openOut = R.anim.swipeback_activity_open_alpha_out;
                    break;
                case RIGHT:
                    openOut = R.anim.swipeback_activity_open_left_out;
                    break;
                case BOTTOM:
                    openOut = R.anim.swipeback_activity_open_top_out;
                    break;
                case LEFT:
                    openOut = R.anim.swipeback_activity_open_right_out;
                    break;
                case TOP:
                    openOut = R.anim.swipeback_activity_open_bottom_out;
                    break;
                default:
                    throw new IllegalArgumentException();
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
        String directionName = intent.getStringExtra("direction");
        mSwipeBackDirection = SwipeBackDirection.valueOf(directionName);
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
