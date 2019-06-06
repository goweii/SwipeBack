package per.goweii.android.swipeback;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
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
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.RecyclerViewHolder> {

    private List<String> mList = new ArrayList<>();

    public RecyclerViewAdapter(){
        for (int i = 0; i < 10; i++) {
            mList.add("" + 1);
        }
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        TextView textView = new TextView(viewGroup.getContext());
        textView.setGravity(Gravity.CENTER);
        textView.setLayoutParams(new RecyclerView.LayoutParams(300, ViewGroup.LayoutParams.MATCH_PARENT));
        return new RecyclerViewHolder(textView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewHolder recyclerViewHolder, int i) {
        TextView textView = (TextView) recyclerViewHolder.itemView;
        textView.setText("这是一个水平RecyclerView");
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder{

        public RecyclerViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
