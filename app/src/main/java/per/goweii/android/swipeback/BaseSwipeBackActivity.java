package per.goweii.android.swipeback;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import per.goweii.actionbarex.common.ActionBarCommon;
import per.goweii.actionbarex.common.OnActionBarChildClickListener;
import per.goweii.swipeback.SwipeBackDirection;
import per.goweii.swipeback.SwipeBackTransformer;
import per.goweii.swipeback.SwipeBackAble;
import per.goweii.swipeback.transformer.AvoidStatusBarSwipeBackTransformer;
import per.goweii.swipeback.transformer.ParallaxSwipeBackTransformer;
import per.goweii.swipeback.transformer.ShrinkSwipeBackTransformer;

public class BaseSwipeBackActivity extends AppCompatActivity implements SwipeBackAble {

    private int mSwipeBackDirection = SwipeBackDirection.LEFT;
    private SwipeBackTransformer mSwipeBackTransformer = null;
    private boolean mSwipeBackOnlyEdge = false;
    private boolean mSwipeBackForceEdge = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_swipe_back);

        ActionBarCommon abc = findViewById(R.id.abc);
        abc.setOnRightTextClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                App.recreate();
            }
        });
        abc.setOnLeftTextClickListener(new OnActionBarChildClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(BaseSwipeBackActivity.this, SwipeBackNormalActivity.class));
                overridePendingTransition(
                        R.anim.swipeback_activity_open_right_in,
                        R.anim.swipeback_activity_open_left_out
                );
            }
        });

        RadioGroup rg_transformer = findViewById(R.id.rg_transformer);
        rg_transformer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_parallax) {
                    mSwipeBackTransformer = new ParallaxSwipeBackTransformer();
                } else if (checkedId == R.id.rb_shrink) {
                    mSwipeBackTransformer = new ShrinkSwipeBackTransformer();
                } else if (checkedId == R.id.rb_avoid_statusbar) {
                    mSwipeBackTransformer = new AvoidStatusBarSwipeBackTransformer();
                } else {
                    mSwipeBackTransformer = null;
                }
            }
        });
        rg_transformer.check(R.id.rb_parallax);

        RadioGroup rg_direction = findViewById(R.id.rg_direction);
        rg_direction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.cb_left) {
                    mSwipeBackDirection = SwipeBackDirection.RIGHT;
                } else if (checkedId == R.id.cb_right) {
                    mSwipeBackDirection = SwipeBackDirection.LEFT;
                } else if (checkedId == R.id.cb_top) {
                    mSwipeBackDirection = SwipeBackDirection.BOTTOM;
                } else if (checkedId == R.id.cb_bottom) {
                    mSwipeBackDirection = SwipeBackDirection.TOP;
                } else {
                    mSwipeBackDirection = SwipeBackDirection.NONE;
                }
            }
        });
        rg_direction.check(R.id.cb_left);

        SwitchCompat sw_only_edge = findViewById(R.id.sw_only_edge);
        sw_only_edge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSwipeBackOnlyEdge = isChecked;
            }
        });
        sw_only_edge.setChecked(mSwipeBackOnlyEdge);

        SwitchCompat sw_force_edge = findViewById(R.id.sw_force_edge);
        sw_force_edge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSwipeBackForceEdge = isChecked;
            }
        });
        sw_force_edge.setChecked(mSwipeBackForceEdge);

        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(new RecyclerViewAdapter());

        ViewPager vp = findViewById(R.id.vp);
        vp.setAdapter(new ViewPagerAdapter());
    }

    @Override
    public int swipeBackDirection() {
        return mSwipeBackDirection;
    }

    @Override
    public SwipeBackTransformer swipeBackTransformer() {
        return mSwipeBackTransformer;
    }

    @Override
    public boolean swipeBackOnlyEdge() {
        return mSwipeBackOnlyEdge;
    }

    @Override
    public boolean swipeBackForceEdge() {
        return mSwipeBackForceEdge;
    }
}
