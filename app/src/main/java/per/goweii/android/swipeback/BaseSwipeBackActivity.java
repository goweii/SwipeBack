package per.goweii.android.swipeback;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

import android.os.Handler;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.RadioGroup;

import per.goweii.actionbarex.common.ActionBarCommon;
import per.goweii.actionbarex.common.OnActionBarChildClickListener;
import per.goweii.swipeback.SwipeBackDirection;
import per.goweii.swipeback.SwipeBackTransformer;
import per.goweii.swipeback.SwipeBackAbility;
import per.goweii.swipeback.transformer.ParallaxSwipeBackTransformer;
import per.goweii.swipeback.transformer.ShrinkSwipeBackTransformer;

public class BaseSwipeBackActivity extends AppCompatActivity implements
        SwipeBackAbility.Direction,
        SwipeBackAbility.Transformer,
        SwipeBackAbility.OnlyEdge,
        SwipeBackAbility.ForceEdge {

    private final Handler mHandler = new Handler();

    @NonNull
    protected SwipeBackDirection mSwipeBackDirection = SwipeBackDirection.RIGHT;
    protected SwipeBackTransformer mSwipeBackTransformer = null;
    protected boolean mSwipeBackOnlyEdge = false;
    protected boolean mSwipeBackForceEdge = true;

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
                SwipeBackNormalActivity.start(BaseSwipeBackActivity.this,
                        mSwipeBackDirection,
                        mSwipeBackTransformer,
                        mSwipeBackOnlyEdge,
                        mSwipeBackForceEdge
                );
                SwitchCompat sw_close_self_on_open_activity = findViewById(R.id.sw_close_self_on_open_activity);
                if (sw_close_self_on_open_activity.isChecked()) {
                    finish();
                }
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
                } else {
                    mSwipeBackTransformer = null;
                }
            }
        });
        if (mSwipeBackTransformer instanceof ShrinkSwipeBackTransformer) {
            rg_transformer.check(R.id.rb_shrink);
        } else if (mSwipeBackTransformer instanceof ParallaxSwipeBackTransformer) {
            rg_transformer.check(R.id.rb_parallax);
        } else {
            rg_transformer.check(R.id.rb_nothing);
        }

        RadioGroup rg_direction = findViewById(R.id.rg_direction);
        rg_direction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_left) {
                    mSwipeBackDirection = SwipeBackDirection.LEFT;
                } else if (checkedId == R.id.rb_right) {
                    mSwipeBackDirection = SwipeBackDirection.RIGHT;
                } else if (checkedId == R.id.rb_top) {
                    mSwipeBackDirection = SwipeBackDirection.TOP;
                } else if (checkedId == R.id.rb_bottom) {
                    mSwipeBackDirection = SwipeBackDirection.BOTTOM;
                } else {
                    mSwipeBackDirection = SwipeBackDirection.NONE;
                }
            }
        });
        switch (mSwipeBackDirection) {
            case NONE:
                rg_direction.check(R.id.rb_none);
                break;
            case RIGHT:
                rg_direction.check(R.id.rb_right);
                break;
            case BOTTOM:
                rg_direction.check(R.id.rb_bottom);
                break;
            case LEFT:
                rg_direction.check(R.id.rb_left);
                break;
            case TOP:
                rg_direction.check(R.id.rb_top);
                break;
        }

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

        WebView web_view = findViewById(R.id.web_view);
        web_view.getSettings().setJavaScriptEnabled(true);
        web_view.setWebChromeClient(new WebChromeClient());
        web_view.setWebChromeClient(new WebChromeClient());
        web_view.loadUrl("https://www.wanandroid.com/blog/show/3775");

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                bindData();
            }
        }, 2000);
    }

    @Override
    protected void onDestroy() {
        mHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    private void bindData() {
        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(BaseSwipeBackActivity.this, LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(new RecyclerViewAdapter());
        ViewPager vp = findViewById(R.id.vp);
        vp.setAdapter(new ViewPagerAdapter());
    }

    @NonNull
    @Override
    public SwipeBackDirection swipeBackDirection() {
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
