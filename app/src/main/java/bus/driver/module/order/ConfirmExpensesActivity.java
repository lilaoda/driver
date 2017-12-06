package bus.driver.module.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.orhanobut.logger.Logger;

import java.util.HashMap;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.base.Constants;
import bus.driver.bean.OrderInfo;
import bus.driver.bean.event.OrderEvent;
import bus.driver.data.HttpManager;
import bus.driver.utils.EventBusUtls;
import bus.driver.utils.overlay.AMapUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.DateUtils;
import lhy.lhylibrary.utils.ToastUtils;

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
        initView();
        mHttpManager = HttpManager.instance();
    }

    private void initData() {
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.ORDER_INFO)) {
            mOrderInfo = intent.getParcelableExtra(Constants.ORDER_INFO);
        }
        if (mOrderInfo == null) {
            finish();
        }
    }

    private void initView() {
        Logger.d(new Gson().toJson(mOrderInfo));
        totalDistance.setText(AMapUtil.getFriendlyLength((int) mOrderInfo.getTripDistance()));
//        if ((int) mOrderInfo.getTotalFare() == 0) {
//            //TODO 待后端无问题再去掉
//            textExpenses.setText("10");
//        } else {
        textExpenses.setText("" + mOrderInfo.getTotalFare());
//        }
        long l = (DateUtils.getCurrenTimestemp(mOrderInfo.getArriveTime()) - DateUtils.getCurrenTimestemp(mOrderInfo.getPasArrTime())) / 1000;
        totalTime.setText(AMapUtil.getFriendlyTime((int) l));
    }

    @OnClick(R.id.btn_expenses)
    public void onViewClicked() {
        confirmExpenses();
    }

    /**
     * 确认费用
     */
    private void confirmExpenses() {
        if (TextUtils.isEmpty(CommonUtils.getString(moneyHighSpeed))
                || TextUtils.isEmpty(CommonUtils.getString(moneyPark))
                || TextUtils.isEmpty(CommonUtils.getString(moneyOther))) {
            ToastUtils.showString("请填写相关费用");
            return;
        }
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("order_uuid", mOrderInfo.getOrderUuid());
        paramMap.put("high_peed_fare", CommonUtils.getString(moneyHighSpeed));
        paramMap.put("parking_fare", CommonUtils.getString(moneyPark));
        paramMap.put("other_fare", CommonUtils.getString(moneyOther));
        wrapHttp(mHttpManager.getDriverService().confirmExpenses(paramMap))
                .compose(this.<String>bindToLifecycle())
                .subscribe(new ResultObserver<String>(this, "确认费用...", true) {
                    @Override
                    public void onSuccess(String value) {
                        btnExpenses.setText(value + "元");
                        btnExpenses.setEnabled(false);
                        Constants.ORDER_STATSU = Constants.ORDER_STATSU_NO;
                        EventBusUtls.notifyOrderChanged(mOrderInfo);
                        //开启循环拉取订单
                        EventBusUtls.notifyPullOrder(OrderEvent.ORDER_PULL_ENABLE);
                        //关闭循环拉取订单是否被取消
                        EventBusUtls.notifyPullOrder(OrderEvent.ORDER_GET_CANCEL_UNABLE);
                    }
                });
    }
}
