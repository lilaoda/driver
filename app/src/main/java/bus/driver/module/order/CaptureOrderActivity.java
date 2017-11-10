package bus.driver.module.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.base.GlobeConstants;
import bus.driver.bean.OrderInfo;
import bus.driver.bean.event.LocationEvent;
import bus.driver.bean.event.OrderEvent;
import bus.driver.data.HttpManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.ActivityUtils;

import static bus.driver.utils.RxUtils.wrapHttp;

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
        if (intent != null && intent.hasExtra(GlobeConstants.ORDER_INFO)) {
            mOrderInfo = intent.getParcelableExtra(GlobeConstants.ORDER_INFO);
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), CaptureInfoFragment.newInstance(mOrderInfo), R.id.fl_map_content);
//            mCaptureOrderMapFragment = CaptureMapFragment.newInstance(mOrderInfo);
//            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mCaptureOrderMapFragment, R.id.fl_map_content);
        }
    }

    @OnClick(R.id.btn_aboard)
    public void onViewClicked() {
        captureOrder(mOrderInfo);
    }

    private void captureOrder(OrderInfo orderInfo) {
        wrapHttp(HttpManager.instance().getDriverService().getOrder(orderInfo.getOrderUuid()))
                .compose(this.<String>bindToLifecycle())
                .subscribe(new ResultObserver<String>(this, "正在抢单...", true) {
                    @Override
                    public void onSuccess(String value) {
                        GlobeConstants.ORDER_STATSU = GlobeConstants.ORDER_STATSU_ONDOING;
                        Intent intent = new Intent(CaptureOrderActivity.this, OrderOngoingActivity.class);
                        intent.putExtra(GlobeConstants.ORDER_INFO, mOrderInfo);
                        startActivity(intent);
                        EventBus.getDefault().post(LocationEvent.LOCATION_UNABLE);
                        EventBus.getDefault().post(OrderEvent.ORDER_GET_CANCEL_ENABLE);
                        finish();
                    }
                });
    }

}
