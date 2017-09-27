package bus.driver.module.route;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.ArrayList;
import java.util.List;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Liheyu on 2017/9/26.
 * 我的行程
 */

public class RouteActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);
        ButterKnife.bind(this);
        initToolbar(getString(R.string.my_route));
        initView();
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        List<String> data = new ArrayList<>();
        data.add("行程1");
        recyclerView.setAdapter(new BaseQuickAdapter<String, BaseViewHolder>(R.layout.view_text, data) {

            @Override
            protected void convert(BaseViewHolder helper, String item) {
                helper.setText(R.id.text, item);
            }
        });
    }
}
