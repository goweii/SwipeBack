package per.goweii.android.swipeback;

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
import per.goweii.swipeback.SwipeBackable;
import per.goweii.swipeback.transformer.ParallaxSwipeBackTransformer;
import per.goweii.swipeback.transformer.ShrinkSwipeBackTransformer;

public class BaseSwipeBackActivity extends AppCompatActivity implements SwipeBackable {

    private int mSwipeBackDirection = SwipeBackDirection.NONE;
    private SwipeBackTransformer mSwipeBackTransformer = null;

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
                    mSwipeBackTransformer = new ParallaxSwipeBackTransformer();
                } else if (checkedId == R.id.rb_shrink) {
                    mSwipeBackTransformer = new ShrinkSwipeBackTransformer();
                } else {
                    mSwipeBackTransformer = null;
                }
            }
        });
        rb_parallax.setChecked(true);

        final CheckBox cb_left = findViewById(R.id.cb_left);
        final CheckBox cb_right = findViewById(R.id.cb_right);
        final CheckBox cb_top = findViewById(R.id.cb_top);
        final CheckBox cb_bottom = findViewById(R.id.cb_bottom);
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mSwipeBackDirection = 0;
                if (cb_left.isChecked()) {
                    mSwipeBackDirection = mSwipeBackDirection | SwipeBackDirection.LEFT;
                }
                if (cb_right.isChecked()) {
                    mSwipeBackDirection = mSwipeBackDirection | SwipeBackDirection.RIGHT;
                }
                if (cb_top.isChecked()) {
                    mSwipeBackDirection = mSwipeBackDirection | SwipeBackDirection.TOP;
                }
                if (cb_bottom.isChecked()) {
                    mSwipeBackDirection = mSwipeBackDirection | SwipeBackDirection.BOTTOM;
                }
            }
        };
        cb_left.setOnCheckedChangeListener(onCheckedChangeListener);
        cb_right.setOnCheckedChangeListener(onCheckedChangeListener);
        cb_top.setOnCheckedChangeListener(onCheckedChangeListener);
        cb_bottom.setOnCheckedChangeListener(onCheckedChangeListener);
        cb_left.setChecked(true);

        SwitchCompat sw_only_edge = findViewById(R.id.sw_only_edge);
        sw_only_edge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });

        SwitchCompat sw_force_edge = findViewById(R.id.sw_force_edge);
        sw_force_edge.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            }
        });

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

    @Override
    public int swipeBackDirection() {
        return mSwipeBackDirection;
    }

    @Override
    public SwipeBackTransformer swipeBackTransformer() {
        return mSwipeBackTransformer;
    }
}
