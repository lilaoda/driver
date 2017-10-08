package bus.driver.module.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Lilaoda on 2017/10/8.
 * Email:749948218@qq.com
 */

public class OrderDetailActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        initToolbar("订单详情");
    }

    @OnClick({R.id.btn_aboard, R.id.btn_arrive})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_aboard:
                break;
            case R.id.btn_arrive:
                break;
        }
    }
}
