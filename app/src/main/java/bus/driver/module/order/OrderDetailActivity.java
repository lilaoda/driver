package bus.driver.module.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.base.Constants;
import bus.driver.bean.OrderInfo;
import bus.driver.bean.event.OrderEvent;
import bus.driver.data.HttpManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.http.ResultObserver;

import static bus.driver.utils.RxUtils.wrapHttp;

/**
 * Created by Lilaoda on 2017/10/8.
 * Email:749948218@qq.com
 */

public class OrderDetailActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.btn_aboard)
    Button btnAboard;
    @BindView(R.id.btn_arrive)
    Button btnArrive;
    @BindView(R.id.btn_expenses)
    Button btnExpenses;
    private OrderInfo mOrderInfo;
    private HttpManager mHttpManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        ButterKnife.bind(this);
        initToolbar("订单详情");
        initData();
    }

    private void initData() {
        mHttpManager = HttpManager.instance();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.ORDER_INFO)) {
            mOrderInfo = intent.getParcelableExtra(Constants.ORDER_INFO);
        }
    }

    @OnClick({R.id.btn_aboard, R.id.btn_arrive, R.id.btn_expenses})
    public void onViewClicked(View view) {
        switch (view.getId()) {
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
                        Constants.ORDER_STATSU = Constants.ORDER_STATSU_NO;
                        EventBus.getDefault().post(OrderEvent.ORDER_PULL_ENABLE);
                        EventBus.getDefault().post(OrderEvent.ORDER_GET_CANCEL_UNABLE);
                    }
                });
    }

    private void confirmArrive() {
        wrapHttp(mHttpManager.getDriverService().confirmArrive(mOrderInfo.getOrderUuid()))
                .compose(this.<OrderInfo>bindToLifecycle())
                .subscribe(new ResultObserver<OrderInfo>(this, "确认到达...", true) {
                    @Override
                    public void onSuccess(OrderInfo value) {
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
                .compose(this.<OrderInfo>bindToLifecycle())
                .subscribe(new ResultObserver<OrderInfo>(this, "确认上车...", true) {
                    @Override
                    public void onSuccess(OrderInfo value) {
                        btnAboard.setText("乘客已上车");
                        btnAboard.setEnabled(false);
                    }
                });
    }
}
