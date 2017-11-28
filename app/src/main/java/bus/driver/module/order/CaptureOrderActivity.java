package bus.driver.module.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.base.Constants;
import bus.driver.bean.OrderInfo;
import butterknife.BindView;
import butterknife.ButterKnife;
import lhy.lhylibrary.utils.ActivityUtils;

/**
 * Created by Lilaoda on 2017/10/10.
 * Email:749948218@qq.com
 */

public class CaptureOrderActivity extends BaseActivity {

    @BindView(R.id.text_order_info)
    TextView textOrderInfo;
    @BindView(R.id.btn_aboard)
    Button btnAboard;
    @BindView(R.id.fl_map_content)
    FrameLayout flMapContent;
    private OrderInfo mOrderInfo;
    private CaptureMapFragment mCaptureOrderMapFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture_order);
        ButterKnife.bind(this);
        initToolbar("抢单");
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.ORDER_INFO)) {
            mOrderInfo = intent.getParcelableExtra(Constants.ORDER_INFO);
            if (mOrderInfo == null) {
                finish();
            }
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), CaptureInfoFragment.newInstance(mOrderInfo), R.id.fl_map_content);
//            mCaptureOrderMapFragment = CaptureMapFragment.newInstance(mOrderInfo);
//            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mCaptureOrderMapFragment, R.id.fl_map_content);
        }
    }


}
