package bus.driver.module.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.base.GlobeConstants;
import bus.driver.bean.OrderInfo;
import bus.driver.module.navi.NaviActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.utils.ActivityUtils;

/**
 * Created by Lilaoda on 2017/9/29.
 * Email:749948218@qq.com
 */

public class OrderOngoingActivity extends BaseActivity {

    @BindView(R.id.text_target)
    TextView textTarget;
    @BindView(R.id.text_passenger_info)
    TextView textPassengerInfo;

    private OrderInfo mOrderInfo;
    private OrderOngoingrFragment mOrderOngoingrFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing);
        ButterKnife.bind(this);
        initToolbar("订单");
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(GlobeConstants.ORDER_INFO)) {
            mOrderInfo = intent.getParcelableExtra(GlobeConstants.ORDER_INFO);
        }
        if (mOrderInfo == null) {
            finish();
        }
        mOrderOngoingrFragment = OrderOngoingrFragment.newInstance(mOrderInfo);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mOrderOngoingrFragment, R.id.fl_map_content);
    }

    private void initView() {
        textTarget.setText(mOrderInfo.getDestAddress());
        textPassengerInfo.setText(mOrderInfo.getActualPasNam() + ": " + mOrderInfo.getActualPasMob());
    }

    @OnClick({R.id.btn_navi, R.id.btn_call, R.id.ib_reLocate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_reLocate:
                break;
            case R.id.btn_navi:
                Intent intent = new Intent(this, NaviActivity.class);
                intent.putExtra(GlobeConstants.ORDER_INFO, mOrderInfo);
                startActivity(intent);
//                if (mOrderOngoingrFragment.isResume()) {
//                    mOrderOngoingrFragment.startNavi(new Poi("我的位置", new LatLng(LocationService.latitude, LocationService.longitude), null), new Poi("终点", new LatLng(mOrderInfo.getDestLat(), mOrderInfo.getDestLng()), null), AmapNaviType.DRIVER);
//                }
                break;
            case R.id.btn_call:
                break;
        }
    }
}
