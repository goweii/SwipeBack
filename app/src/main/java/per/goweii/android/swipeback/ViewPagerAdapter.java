package per.goweii.android.swipeback;

import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;

import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * @author CuiZhen
 * @date 2019/6/6
 * QQ: 302833254
 * E-mail: goweii@163.com
 * GitHub: https://github.com/goweii
 */
public class ViewPagerAdapter extends PagerAdapter {

    private List<String> mList = new ArrayList<>();

    public ViewPagerAdapter(){
        for (int i = 0; i < 10; i++) {
            mList.add("" + 1);
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        TextView textView = new TextView(container.getContext());
        textView.setText("这是一个ViewPager");
        textView.setGravity(Gravity.CENTER);
        container.addView(textView);
        return textView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
        return view == o;
    }
}
