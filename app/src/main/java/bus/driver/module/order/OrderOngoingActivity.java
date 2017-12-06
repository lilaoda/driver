package bus.driver.module.order;

import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.base.Constants;
import bus.driver.bean.OrderInfo;
import bus.driver.bean.event.DistanceEvent;
import bus.driver.bean.event.LocationEvent;
import bus.driver.data.AMapManager;
import bus.driver.data.HttpManager;
import bus.driver.service.DriverService;
import bus.driver.utils.EventBusUtls;
import bus.driver.utils.overlay.AMapUtil;
import bus.driver.widget.ConfirmExpensesDialog;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.ActivityUtils;
import lhy.lhylibrary.utils.DateUtils;
import lhy.lhylibrary.utils.StatusBarUtil;
import lhy.lhylibrary.utils.ToastUtils;

import static bus.driver.utils.RxUtils.wrapHttp;

/**
 * Created by Lilaoda on 2017/9/29.
 * Email:749948218@qq.com
 * <p>
 * 正在进行中的订单
 */

public class OrderOngoingActivity extends BaseActivity implements OrderOngoingrFragment.LocaitonChageListener {

    /**
     * 未到达乘客地点
     */
    public static final int STATUS_READY_ARRIVE_PASSENGER = 0;
    /**
     * 到达乘客地点，未上车（未开始行程）
     */
    public static final int STATUS_READY_ABOARD = 1;
    /**
     * 乘客已上车，行程进行中
     */
    public static final int STATUS_ON_GOING = 2;
    /**
     * 乘客已到达目的地，待确认费用
     */
    public static final int STATUS_READY_PAY = 3;

    @BindView(R.id.text_target)
    TextView textTarget;
    @BindView(R.id.text_passenger_info)
    TextView textPassengerInfo;
    @BindView(R.id.btn_arrive_passenger)
    Button btnArrivePassenger;
    @BindView(R.id.btn_aboard)
    Button btnAboard;
    @BindView(R.id.btn_arrive)
    Button btnArrive;
    @BindView(R.id.btn_expenses)
    Button btnExpenses;
    @BindView(R.id.text_money)
    TextView textMoney;
    @BindView(R.id.ll_over)
    LinearLayout llOver;
    @BindView(R.id.ll_status)
    LinearLayout llStatus;
    @BindView(R.id.text_distance)
    TextView textDistance;
    @BindView(R.id.text_time)
    TextView textTime;

    private int mCurrentStatus = STATUS_READY_ARRIVE_PASSENGER;
    private long mWaitTime;
    private long mGoingTime;

    private OrderInfo mOrderInfo;
    private OrderOngoingrFragment mOrderOngoingrFragment;
    private HttpManager mHttpManager;
    private AMapManager mAMapManager;
    private Disposable mWaitTimeDisposable;
    private Disposable mGoingTimeDisposable;
    private ConfirmExpensesDialog mConfirmExpensesDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initToolbar("订单");
        initData();
        initView();
    }

    private void initData() {
        mHttpManager = HttpManager.instance();
        mAMapManager = AMapManager.instance();
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(Constants.ORDER_INFO)) {
            mOrderInfo = intent.getParcelableExtra(Constants.ORDER_INFO);
        }
        if (mOrderInfo == null) {
            finish();
        }
        mOrderOngoingrFragment = OrderOngoingrFragment.newInstance(mOrderInfo);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mOrderOngoingrFragment, R.id.fl_map_content);
        mOrderOngoingrFragment.setLocationChangeListener(this);
    }

    private void initView() {
        textTarget.setText(mOrderInfo.getDestAddress());
        String name = TextUtils.isEmpty(mOrderInfo.getActualPasNam()) ? "乘客" : mOrderInfo.getActualPasNam();
        textPassengerInfo.setText(name + ": " + mOrderInfo.getActualPasMob());

        int subStatus = mOrderInfo.getSubStatus();
        if (subStatus < 220) {
            //未到达
            showView(STATUS_READY_ARRIVE_PASSENGER);
        } else if (subStatus == 220) {
            //到达未开始
            showView(STATUS_READY_ABOARD);
        } else if (subStatus == 300) {
            //开始未到达目的地
            showView(STATUS_ON_GOING);
        } else if (subStatus == 301) {
            showView(STATUS_READY_PAY);
            //到达目的地未确认费用
        } else if (subStatus == 400) {
            //确认费用了未支付
            ToastUtils.showString("跳到支付页面");
        } else if (subStatus == 500 || subStatus == 501) {
            //行程已结束已经付费
            llOver.setVisibility(View.VISIBLE);
            llStatus.setVisibility(View.GONE);
            setToolbarTitle("行程已结束付费");
        } else {
            //行程已取消
            ToastUtils.showString("行程已取消");
            finish();
        }

        if (mCurrentStatus == STATUS_READY_ABOARD || mCurrentStatus == STATUS_READY_ARRIVE_PASSENGER) {
            showWaitTime();
        } else if (mCurrentStatus == STATUS_ON_GOING) {
            showGoingTime();
        } else if (mCurrentStatus == STATUS_READY_PAY) {
            showEndText();
        }
    }

    private void showView(int status) {
        mCurrentStatus = status;
        btnArrivePassenger.setVisibility(mCurrentStatus == STATUS_READY_ARRIVE_PASSENGER ? View.VISIBLE : View.GONE);
        btnAboard.setVisibility(mCurrentStatus == STATUS_READY_ABOARD ? View.VISIBLE : View.GONE);
        btnArrive.setVisibility(mCurrentStatus == STATUS_ON_GOING ? View.VISIBLE : View.GONE);
        btnExpenses.setVisibility(mCurrentStatus == STATUS_READY_PAY ? View.VISIBLE : View.GONE);
        if (status == STATUS_READY_PAY) {
            showEndText();
        }
        if (mCurrentStatus == STATUS_READY_ARRIVE_PASSENGER) {
            setToolbarTitle("去接乘客");
        } else if (mCurrentStatus == STATUS_READY_ABOARD) {
            setToolbarTitle("等待乘客上车");
        } else if (mCurrentStatus == STATUS_ON_GOING) {
            //开始实时计算距离
            beginCaculateDistance();
            setToolbarTitle("正在行程中");
            textDistance.setText("加载中...");
        } else if (mCurrentStatus == STATUS_READY_PAY) {
            setToolbarTitle("费用详情");
        }
    }

    private void beginCaculateDistance() {
        LocationEvent caculateLocationEvent = LocationEvent.LOCATION_DISTANCE_START;
        caculateLocationEvent.setOrderUuid(mOrderInfo.getOrderUuid());
        caculateLocationEvent.setTripDistance(mOrderInfo.getTripDistance());
        if (mOrderInfo.getLastLat() > 0 && mOrderInfo.getLastLng() > 0) {
            LatLng lastLatLng = new LatLng(mOrderInfo.getOriginLat(), mOrderInfo.getOriginLng());
            caculateLocationEvent.setLastLatLng(lastLatLng);
        } else {
            caculateLocationEvent.setLastLatLng(null);
        }
        EventBusUtls.notifyLocation(caculateLocationEvent);
    }

    private void setToolbarTitle(String title) {
        TextView textTitle = (TextView) findViewById(R.id.toolbar_title);
        if (textTitle != null) {
            textTitle.setText(title);
        }
    }

    private void showEndText() {
        textDistance.setText("已到达");
        long l = DateUtils.getCurrenTimestemp(mOrderInfo.getArriveTime()) - DateUtils.getCurrenTimestemp(mOrderInfo.getPasArrTime());
        textTime.setText("共用时:" + getIntervalTime(l / 1000));
    }

    /**
     * 乘客上车后显示正式行程的行驶时间
     */
    private void showGoingTime() {
        //防止重复显示
        cancelGoingTime();
        if (TextUtils.isEmpty(mOrderInfo.getPasArrTime())) {
            return;
        }
        mGoingTime = (System.currentTimeMillis() - DateUtils.getCurrenTimestemp(mOrderInfo.getPasArrTime())) / 1000;
        mGoingTimeDisposable = Flowable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Long, String>() {
                    @Override
                    public String apply(@NonNull Long aLong) throws Exception {
                        mGoingTime += 1;
                        return getIntervalTime(mGoingTime);
                    }
                })
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String time) throws Exception {
                        textTime.setText("已行驶：" + time);
                    }
                });
    }

    private void cancelGoingTime() {
        if (mGoingTimeDisposable != null && !mGoingTimeDisposable.isDisposed()) {
            mGoingTimeDisposable.dispose();
        }
    }

    /**
     * 显示乘客等待时间
     */
    private void showWaitTime() {
        cancelWaitTime();
        //等待时间以国国说的为准，从司机到达乘客位置开始算
        if (TextUtils.isEmpty(mOrderInfo.getDriArrTime())) {
            //从抢单页进来，可能没有接单时间
            return;
        }
        mWaitTime = (System.currentTimeMillis() - DateUtils.getCurrenTimestemp(mOrderInfo.getDriArrTime())) / 1000;
        mWaitTimeDisposable = Flowable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .map(new Function<Long, String>() {
                    @Override
                    public String apply(@io.reactivex.annotations.NonNull Long aLong) throws Exception {
                        mWaitTime += 1;
                        return getIntervalTime(mWaitTime);
                    }
                })
                .compose(this.<String>bindToLifecycle())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(@NonNull String time) throws Exception {
                        textTime.setText("已等待：" + time);
                    }
                });
    }

    private void cancelWaitTime() {
        if (mWaitTimeDisposable != null && !mWaitTimeDisposable.isDisposed()) {
            mWaitTimeDisposable.dispose();
        }
    }

    /**
     * 根据毫秒获取时间
     *
     * @param intervalSecond 间隔秒
     * @return 小时分秒
     */
    @android.support.annotation.NonNull
    private String getIntervalTime(long intervalSecond) {
        if (intervalSecond / 3600 != 0) {
            return intervalSecond / 3600 + "小时" + (intervalSecond % 3600) / 60 + "分" + intervalSecond % 60 + "秒";
        } else {
            return intervalSecond / 60 + "分" + intervalSecond % 60 + "秒";
        }
    }

    @OnClick({R.id.btn_navi, R.id.btn_call, R.id.ib_reLocate, R.id.btn_arrive_passenger, R.id.btn_aboard, R.id.btn_arrive, R.id.btn_expenses})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_reLocate:
                break;
            case R.id.btn_navi:
                startNavi();
                break;
            case R.id.btn_call:
                doCall();
                break;
            case R.id.btn_aboard:
                confirmAboard();
                break;
            case R.id.btn_arrive:
                confirmArrive();
                break;
            case R.id.btn_expenses:
//                if (mConfirmExpensesDialog == null) {
//                    mConfirmExpensesDialog = ConfirmExpensesDialog.newInstance();
//                }
//                mConfirmExpensesDialog.show(getSupportFragmentManager(), "bottomDialog");
//                confirmExpenses();
                break;
            case R.id.btn_arrive_passenger:
                confirmArrivePassengerAddress();
                break;
        }
    }

    /**
     * 打电话
     */
    private void doCall() {
        RxPermissions rxPermissions = new RxPermissions(this);
        rxPermissions.request(android.Manifest.permission.CALL_PHONE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + mOrderInfo.getActualPasMob()));
                            startActivity(intent);
                        } else {
                            ToastUtils.showString("权限被拒绝");
                        }
                    }
                });
    }

    private void startNavi() {
        AmapNaviParams amapNaviParams;
        if (mCurrentStatus == STATUS_READY_ARRIVE_PASSENGER) {
            amapNaviParams = new AmapNaviParams(new Poi("我的位置", new LatLng(DriverService.latitude, DriverService.longitude), null),
                    null, new Poi("乘客位置", new LatLng(mOrderInfo.getOriginLat(), mOrderInfo.getOriginLng()), null), AmapNaviType.DRIVER);
        } else {
            amapNaviParams = new AmapNaviParams(new Poi("我的位置", new LatLng(DriverService.latitude, DriverService.longitude), null),
                    null, new Poi("目的地", new LatLng(mOrderInfo.getDestLat(), mOrderInfo.getDestLng()), null), AmapNaviType.DRIVER);
        }
        AmapNaviPage.getInstance().showRouteActivity(getApplication(), amapNaviParams, null);
//        AmapNaviPage.getInstance().showRouteActivity(getContext(), amapNaviParams, new INaviInfoCallback() {
//            NaviStatus event = new NaviStatus();
//
//            @Override
//            public void onInitNaviFailure() {
//
//            }
//
//            @Override
//            public void onGetNavigationText(String s) {
//
//            }
//
//            @Override
//            public void onLocationChange(AMapNaviLocation aMapNaviLocation) {
//
//            }
//
//            @Override
//            public void onArriveDestination(boolean b) {
//                event.setNaving(false);
//                EventBusUtls.notifyIsNaving(event);
//            }
//
//            @Override
//            public void onStartNavi(int i) {
//                event.setNaving(true);
//                EventBusUtls.notifyIsNaving(event);
//            }
//
//            @Override
//            public void onCalculateRouteSuccess(int[] ints) {
//
//            }
//
//            @Override
//            public void onCalculateRouteFailure(int i) {
//
//            }
//
//            @Override
//            public void onStopSpeaking() {
//
//            }
//        });
    }

    /**
     * 确认到达乘客上车地点
     */
    private void confirmArrivePassengerAddress() {
        wrapHttp(mHttpManager.getDriverService().waitPassenger(mOrderInfo.getOrderUuid()))
                .compose(this.<OrderInfo>bindToLifecycle())
                .subscribe(new ResultObserver<OrderInfo>(this, "确认到达乘客位置...", true) {
                    @Override
                    public void onSuccess(OrderInfo value) {
                        notifyOrderChanged(value);
                        showView(STATUS_READY_ABOARD);
                        cancelWaitTime();
                    }
                });
    }

    private void notifyOrderChanged(OrderInfo value) {
        mOrderInfo = value;
        EventBusUtls.notifyOrderChanged(value);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onDistanceEvent(DistanceEvent event) {
        Logger.d(event.getLoacationDistance());
        textDistance.setText(AMapUtil.getFriendlyLength((int) (event.getLoacationDistance())));
    }

    /**
     * 确认乘客上车
     */
    private void confirmAboard() {
        wrapHttp(mHttpManager.getDriverService().confirmPassenger(mOrderInfo.getOrderUuid()))
                .compose(this.<OrderInfo>bindToLifecycle())
                .subscribe(new ResultObserver<OrderInfo>(this, "正在开始行程...", true) {
                    @Override
                    public void onSuccess(OrderInfo value) {
                        notifyOrderChanged(value);
                        showView(STATUS_ON_GOING);
                        showGoingTime();
                    }
                });
    }

    /**
     * 确认到达目的地
     */
    private void confirmArrive() {
        wrapHttp(mHttpManager.getDriverService().confirmArrive(mOrderInfo.getOrderUuid()))
                .compose(this.<OrderInfo>bindToLifecycle())
                .subscribe(new ResultObserver<OrderInfo>(this, "正在结束行程...", true) {
                    @Override
                    public void onSuccess(OrderInfo value) {
//                        showView(STATUS_READY_PAY);
//                        cancelGoingTime();
                        //关闭实时计算距离
                        EventBusUtls.notifyLocation(LocationEvent.LOCATION_DISTANCE_STOP);
                        notifyOrderChanged(value);
                        gotoConfirmExpensesActivity();
                        finish();
                    }
                });
    }

    /**
     * 跳转到确认费用页面
     */
    private void gotoConfirmExpensesActivity() {
        Intent intent = new Intent(this, ConfirmExpensesActivity.class);
        intent.putExtra(Constants.ORDER_INFO, mOrderInfo);
        startActivity(intent);
    }

    @Override
    public void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.app_color), 0);
    }

    /**
     * 司机位置改变的回调
     *
     * @param location 位置参数
     */
    @Override
    public void locationChange(Location location) {
        showDistanceText(location);
    }

    private void showDistanceText(Location location) {
        float distance = mAMapManager.calculateLineDistance(new LatLng(location.getLatitude(), location.getLongitude()),
                new LatLng(mOrderInfo.getOriginLat(), mOrderInfo.getOriginLng()));
        if (mCurrentStatus == STATUS_READY_ARRIVE_PASSENGER || mCurrentStatus == STATUS_READY_ABOARD) {
            if (distance / 1000 != 0) {
                distance += 500;
                textDistance.setText(String.format(Locale.CHINA, "距离乘客： %d 公里", (int) distance / 1000));
            } else {
                textDistance.setText(String.format(Locale.CHINA, "距离乘客： %f 米", distance));
            }
        }
//        else if (mCurrentStatus == STATUS_ON_GOING) {
//            if (distance / 1000 != 0) {
//                distance += 500;
//                textDistance.setText(String.format(Locale.CHINA, "已行驶： %d 公里", (int) distance / 1000));
//            } else {
//                textDistance.setText(String.format(Locale.CHINA, "已行驶： %f 米", distance));
//            }
//        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        cancelWaitTime();
        cancelGoingTime();
    }
}
