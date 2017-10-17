package bus.driver.module.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;

import org.greenrobot.eventbus.EventBus;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.base.GlobeConstants;
import bus.driver.bean.OrderInfo;
import bus.driver.bean.event.OrderEvent;
import bus.driver.data.HttpManager;
import bus.driver.service.LocationService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.ActivityUtils;

import static bus.driver.utils.RxUtils.wrapHttp;
import static lhy.lhylibrary.base.LhyApplication.getContext;

/**
 * Created by Lilaoda on 2017/9/29.
 * Email:749948218@qq.com
 */

public class OrderOngoingActivity extends BaseActivity {

    @BindView(R.id.text_target)
    TextView textTarget;
    @BindView(R.id.text_passenger_info)
    TextView textPassengerInfo;
    @BindView(R.id.btn_aboard)
    Button btnAboard;
    @BindView(R.id.btn_arrive)
    Button btnArrive;
    @BindView(R.id.btn_expenses)
    Button btnExpenses;

    private OrderInfo mOrderInfo;
    private OrderOngoingrFragment mOrderOngoingrFragment;
    private HttpManager mHttpManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing);
        ButterKnife.bind(this);
        initToolbar("订单");
        mHttpManager = HttpManager.instance();
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
//        if (mOrderInfo.getMainStatus() > 100 && mOrderInfo.getMainStatus() < 300) {
//            btnAboard.setEnabled(true);
//        } else {
//            btnAboard.setEnabled(false);
//        }
//        if (mOrderInfo.getMainStatus()==300) {
//            btnAboard.setEnabled(true);
//        } else {
//            btnAboard.setEnabled(false);
//        }
    }

    @OnClick({R.id.btn_navi, R.id.btn_call, R.id.ib_reLocate, R.id.btn_aboard, R.id.btn_arrive, R.id.btn_expenses})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_reLocate:
                break;
            case R.id.btn_navi:
                AmapNaviParams amapNaviParams = new AmapNaviParams(new Poi("我的位置", new LatLng(LocationService.latitude, LocationService.longitude), null),
                        null, new Poi("终点", new LatLng(mOrderInfo.getDestLat(), mOrderInfo.getDestLng()), null), AmapNaviType.DRIVER);
                AmapNaviPage.getInstance().showRouteActivity(getContext(), amapNaviParams, null);
//                Intent intent = new Intent(this, NaviActivity.class);
//                intent.putExtra(GlobeConstants.ORDER_INFO, mOrderInfo);
//                startActivity(intent);
//                if (mOrderOngoingrFragment.isResume()) {
//                    mOrderOngoingrFragment.startNavi(new Poi("我的位置", new LatLng(LocationService.latitude, LocationService.longitude), null), new Poi("终点", new LatLng(mOrderInfo.getDestLat(), mOrderInfo.getDestLng()), null), AmapNaviType.DRIVER);
//                }
                break;
            case R.id.btn_call:
                break;
            case R.id.btn_aboard:
                confirmAboard();
                break;
            case R.id.btn_arrive:
                confirmArrive();
                break;
            case R.id.btn_expenses:
                confirmExpenses();
                break;
        }
    }

    private void confirmExpenses() {
        wrapHttp(mHttpManager.getDriverService().confirmExpenses(mOrderInfo.getOrderUuid()))
                .compose(this.<String>bindToLifecycle())
                .subscribe(new ResultObserver<String>(this, "确认费用...", true) {
                    @Override
                    public void onSuccess(String value) {
                        btnExpenses.setText(value);
                        btnExpenses.setEnabled(false);
                        GlobeConstants.ORDER_STATSU = GlobeConstants.ORDER_STATSU_NO;
                        EventBus.getDefault().post(OrderEvent.ORDER_PULL_ENABLE);
                        EventBus.getDefault().post(OrderEvent.ORDER_GET_CANCEL_UNABLE);
                    }
                });
    }

    private void confirmArrive() {
        wrapHttp(mHttpManager.getDriverService().confirmArrive(mOrderInfo.getOrderUuid()))
                .compose(this.<String>bindToLifecycle())
                .subscribe(new ResultObserver<String>(this, "确认到达...", true) {
                    @Override
                    public void onSuccess(String value) {
                        btnArrive.setText("已到达目的地");
                        btnArrive.setEnabled(false);

//                        //TODO ,订单完成才改状态，现在在这里测试
//                        GlobeConstants.ORDER_STATSU = GlobeConstants.ORDER_STATSU_NO;
//                        EventBus.getDefault().post(OrderEvent.ORDER_PULL_ENABLE);
//                        EventBus.getDefault().post(OrderEvent.ORDER_GET_CANCEL_UNABLE);
                    }
                });
    }

    private void confirmAboard() {
        wrapHttp(mHttpManager.getDriverService().confirmPassenger(mOrderInfo.getOrderUuid()))
                .compose(this.<String>bindToLifecycle())
                .subscribe(new ResultObserver<String>(this, "确认上车...", true) {
                    @Override
                    public void onSuccess(String value) {
                        btnAboard.setText("乘客已上车");
                        btnAboard.setEnabled(false);
                    }
                });
    }
}
