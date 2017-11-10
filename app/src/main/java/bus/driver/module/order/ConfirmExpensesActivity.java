package bus.driver.module.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.base.GlobeConstants;
import bus.driver.bean.OrderInfo;
import bus.driver.bean.event.OrderEvent;
import bus.driver.data.HttpManager;
import bus.driver.utils.EventBusUtls;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.http.ResultObserver;

import static bus.driver.utils.RxUtils.wrapHttp;

/**
 * Created by Lilaoda on 2017/11/7.
 * Email:749948218@qq.com
 * 确认订单费用
 */

public class ConfirmExpensesActivity extends BaseActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.expenses_des)
    TextView expensesDes;
    @BindView(R.id.text_expenses)
    TextView textExpenses;
    @BindView(R.id.total_distance)
    TextView totalDistance;
    @BindView(R.id.total_time)
    TextView totalTime;
    @BindView(R.id.money_start)
    TextView moneyStart;
    @BindView(R.id.money_beyond)
    TextView moneyBeyond;
    @BindView(R.id.money_long_distance)
    TextView moneyLongDistance;
    @BindView(R.id.money_night)
    TextView moneyNight;
    @BindView(R.id.money_high_speed)
    EditText moneyHighSpeed;
    @BindView(R.id.money_park)
    EditText moneyPark;
    @BindView(R.id.money_other)
    EditText moneyOther;
    @BindView(R.id.text_money)
    TextView textMoney;
    @BindView(R.id.btn_expenses)
    Button btnExpenses;
    @BindView(R.id.ll_status)
    LinearLayout llStatus;
    private HttpManager mHttpManager;
    private OrderInfo mOrderInfo;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm_expenses);
        ButterKnife.bind(this);
        initToolbar("费用详情");
        initData();
        mHttpManager = HttpManager.instance();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(GlobeConstants.ORDER_INFO)) {
            mOrderInfo = intent.getParcelableExtra(GlobeConstants.ORDER_INFO);
        }
        if (mOrderInfo == null) {
            finish();
        }
    }

    @OnClick(R.id.btn_expenses)
    public void onViewClicked() {
        confirmExpenses();
    }

    /**
     * 确认费用
     */
    private void confirmExpenses() {
        wrapHttp(mHttpManager.getDriverService().confirmExpenses(mOrderInfo.getOrderUuid()))
                .compose(this.<String>bindToLifecycle())
                .subscribe(new ResultObserver<String>(this, "确认费用...", true) {
                    @Override
                    public void onSuccess(String value) {
                        btnExpenses.setText(value);
                        btnExpenses.setEnabled(false);
                        GlobeConstants.ORDER_STATSU = GlobeConstants.ORDER_STATSU_NO;
                        EventBusUtls.notifyOrderChanged(mOrderInfo);
                        //开启循环拉取订单
                        EventBus.getDefault().post(OrderEvent.ORDER_PULL_ENABLE);
                        //关闭循环拉取订单是否被取消
                        EventBus.getDefault().post(OrderEvent.ORDER_GET_CANCEL_UNABLE);
                    }
                });
    }

}
