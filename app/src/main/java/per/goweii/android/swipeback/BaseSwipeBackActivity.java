package per.goweii.android.swipeback;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import per.goweii.actionbarex.common.ActionBarCommon;
import per.goweii.actionbarex.common.OnActionBarChildClickListener;
import per.goweii.swipeback.transformer.ParallaxSwipeBackTransformer;
import per.goweii.swipeback.transformer.ShrinkSwipeBackTransformer;

public abstract class BaseSwipeBackActivity extends AppCompatActivity {

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

        RadioGroup rg_transformer = findViewById(R.id.rg_transformer);
        RadioButton rb_parallax = findViewById(R.id.rb_parallax);
        rg_transformer.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_parallax) {
                    mSwipeBackHelper.getSwipeBackLayout().setSwipeBackTransformer(new ParallaxSwipeBackTransformer());
                } else if (checkedId == R.id.rb_shrink) {
                    mSwipeBackHelper.getSwipeBackLayout().setSwipeBackTransformer(new ShrinkSwipeBackTransformer());
                } else {
                    mSwipeBackHelper.getSwipeBackLayout().setSwipeBackTransformer(null);
                }
            }
        });
        rb_parallax.setChecked(true);

        RadioGroup rg_direction = findViewById(R.id.rg_direction);
        RadioButton rb_left = findViewById(R.id.rb_left);
        rg_direction.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_left) {
                    mSwipeBackHelper.getSwipeBackLayout().setSwipeBackDirection(SwipeBackDirection.FROM_LEFT);
                } else if (checkedId == R.id.rb_right) {
                    mSwipeBackHelper.getSwipeBackLayout().setSwipeBackDirection(SwipeBackDirection.FROM_RIGHT);
                } else if (checkedId == R.id.rb_top) {
                    mSwipeBackHelper.getSwipeBackLayout().setSwipeBackDirection(SwipeBackDirection.FROM_TOP);
                } else if (checkedId == R.id.rb_bottom) {
                    mSwipeBackHelper.getSwipeBackLayout().setSwipeBackDirection(SwipeBackDirection.FROM_BOTTOM);
                } else {
                    mSwipeBackHelper.getSwipeBackLayout().setSwipeBackDirection(SwipeBackDirection.FROM_LEFT);
                }
            }
        });
        rb_left.setChecked(true);

        Switch sw_enable = findViewById(R.id.sw_enable);
        sw_enable.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSwipeBackHelper.setSwipeBackEnable(isChecked);
            }
        });
        sw_enable.setChecked(mSwipeBackHelper.isSwipeBackEnable());

        Switch sw_only_edge = findViewById(R.id.sw_only_edge);
        sw_only_edge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSwipeBackHelper.setSwipeBackOnlyEdge(isChecked);
            }
        });
        sw_only_edge.setChecked(mSwipeBackHelper.isSwipeBackOnlyEdge());

        Switch sw_force_edge = findViewById(R.id.sw_force_edge);
        sw_force_edge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSwipeBackHelper.setSwipeBackForceEdge(isChecked);
            }
        });
        sw_force_edge.setChecked(mSwipeBackHelper.isSwipeBackForceEdge());

        RecyclerView rv = findViewById(R.id.rv);
        rv.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rv.setAdapter(new RecyclerViewAdapter());

        ViewPager vp = findViewById(R.id.vp);
        vp.setAdapter(new ViewPagerAdapter());

        WebView wv = findViewById(R.id.wv);
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                view.loadUrl(url);
                return true;
            }
        });
        wv.loadUrl("https://github.com/goweii");
    }
}
