package bus.driver.service;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

import bus.driver.R;
import bus.driver.base.BaseApplication;
import bus.driver.base.GlobeConstants;
import bus.driver.bean.GpsPoint;
import bus.driver.bean.OrderInfo;
import bus.driver.bean.event.DistanceEvent;
import bus.driver.bean.event.LocationEvent;
import bus.driver.bean.event.LocationResultEvent;
import bus.driver.bean.event.OrderEvent;
import bus.driver.data.HttpManager;
import bus.driver.data.remote.DriverApi;
import bus.driver.data.remote.HttpResult;
import bus.driver.module.order.CaptureOrderActivity;
import bus.driver.utils.EventBusUtls;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import lhy.lhylibrary.base.LhyActivity;
import lhy.lhylibrary.base.LhyApplication;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.ToastUtils;

import static bus.driver.utils.RxUtils.wrapAsync;
import static bus.driver.utils.RxUtils.wrapHttp;
import static lhy.lhylibrary.base.LhyApplication.getContext;

/**
 * Created by Lilaoda on 2017/9/27.
 * Email:749948218@qq.com
 * 定位服务
 * 第一次开启服务时自动定位一次就关闭，以后再通过事件开启定时，会一直定位，直到调用关闭定位才会停止定位
 */

public class DriverService extends Service implements AMapLocationListener {

    public static final String TAG = "LocationService";
    /**
     * 获取推送订单的间隔
     */
    public static final int INTERVAL_PULL_ORDER = 10;
    /**
     * 定位时间间隔
     */
    private final static int LOCATION_TIME_INTERVAL = 5000;
    /**
     * 第一次启动定位的标记 第一次启动后获取定位结果会立即关闭
     */
    public static final String FIRST_LOCATE = "first_locate";

    //默认是北京的经纬度
    public static double latitude = 39.904989;
    public static double longitude = 116.405285;

    /**
     * 实时行驶距离 单位 米
     */
    private static double distanceLocation = 0;

    /**
     * 修复的里程 单位 米
     */
    private static double distanceRepair = 0;

    /**
     * 总行驶里程 单位 米
     */
    private static double distanceTotal = 0;

    /**
     * 实时定位次数
     */
    private static int locationNumber = 0;

    /**
     * 为了避免静止时产生的误差，设置一个低速过滤值，低于这个值不计算到里程里。
     * 此为经验值，可以根据测试结果，具体进行调节。
     */
    public static double SPEED_LOW_LIMIT = 1.0;

    /**
     * 只获取一次定位就关闭定位，为了地图在初始化时以定位点开始,或者不在连接定位状态下的刷新定位时使用
     */
    private boolean isOnceLocation = false;
    /**
     * 是否需要定位结果，其它页面在刷新定位时使用
     */
    private boolean isNeedResult = false;

    /**
     * 是否实时测算距离
     */
    private boolean isCaculateDistance;

    private AlertDialog mOrderDialog;
    private NotificationCompat.Builder mNotifyBuilder;
    private NotificationManager mNotificationManager;
    private HttpManager mHttpManager;
    private Disposable mOrderDisposable;
    private Disposable mCancelOrderDisposable;

    private AMapLocationClient mlocationClient;
    private DriverApi mDriverService;
    private DistanceEvent mDistanceEvent;


    public static void start(Context context) {
        context.startService(new Intent(context, DriverService.class));
    }


    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        mHttpManager = HttpManager.instance();
        mDriverService = mHttpManager.getDriverService();
        initNotification();
        initLocationClient();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mlocationClient != null) {
            mlocationClient.startLocation();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void initLocationClient() {
        mlocationClient = new AMapLocationClient(getContext());
        AMapLocationClientOption mLocationOption = new AMapLocationClientOption();
        mLocationOption.setNeedAddress(true);
        //设置定位模式为高精度模式，Battery_Saving为低功耗模式，Device_Sensors是仅设备模式
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Battery_Saving);
        mLocationOption.setInterval(LOCATION_TIME_INTERVAL); //设置定位间隔,单位毫秒,默认为2000ms
        mLocationOption.setSensorEnable(true);//定位不是GPS时返回速度和角度
        mlocationClient.setLocationOption(mLocationOption);   //设置定位参数
        mlocationClient.setLocationListener(this);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            Log.i(TAG, "onLocationChanged: "+aMapLocation.toStr());
            if (aMapLocation.getErrorCode() == 0) {
                longitude = aMapLocation.getLongitude();
                latitude = aMapLocation.getLatitude();

                if (isOnceLocation) {
                    isOnceLocation = false;
                    mlocationClient.stopLocation();
                }

                //如果处于工作状态，或者在测算实时距离，就上传定位
                if (GlobeConstants.DRIVER_STATSU == GlobeConstants.DRIVER_STATSU_WORK || isCaculateDistance) {
                    uploadLocation(latitude, longitude);
                }

                //在有地图的页面，点击刷新定位时，获取结果
                if (isNeedResult) {
                    EventBusUtls.notifyLocationResult(new LocationResultEvent(true));
                    isNeedResult = false;
                }

                //如果需要，测算距离
                if (isCaculateDistance) {
                    caculateDistance(aMapLocation);
                }
            } else {

                if (isNeedResult) {
                    String errorInfo = "定位失败:" + aMapLocation.getErrorCode() + "," + aMapLocation.getErrorInfo();
                    isNeedResult = false;
                    EventBusUtls.notifyLocationResult(new LocationResultEvent(false, errorInfo));
                }
            }
        }
    }

    /**
     * 开始计算距离时 要清楚数据
     */
    private void clearDistanceData() {
        startGpsPoint = null;
        lastGpsPoint = null;
        locationNumber = 0;
        distanceLocation = 0.0;
    }

    private GpsPoint startGpsPoint = null;
    private GpsPoint lastGpsPoint = null;

    /**
     * 计算实时距离，并发送事件
     *
     * @param aMapLocation 定位结果
     */
    private void caculateDistance(AMapLocation aMapLocation) {
        if (startGpsPoint == null) {
            startGpsPoint = new GpsPoint();
            lastGpsPoint = new GpsPoint();
            convertToGpsPoint(startGpsPoint, aMapLocation);
            convertToGpsPoint(lastGpsPoint, aMapLocation);
        } else {
            convertToGpsPoint(lastGpsPoint, aMapLocation);
            if (lastGpsPoint.getSpeed() > SPEED_LOW_LIMIT) {
                locationNumber++;
                distanceLocation += distance_guo(startGpsPoint.getLat(), startGpsPoint.getLng(), lastGpsPoint.getLat(), lastGpsPoint.getLng());
            }
            convertToGpsPoint(startGpsPoint, aMapLocation);
            Log.i(TAG, "caculateDistance: " + distanceLocation);
            if (mDistanceEvent == null) {
                mDistanceEvent = new DistanceEvent();
            }
            mDistanceEvent.setLoacationDistance(distanceLocation);
            mDistanceEvent.setLocationNumber(locationNumber);
            EventBusUtls.notifyDistanceResult(mDistanceEvent);
        }
    }

    //数据转换
    private void convertToGpsPoint(GpsPoint point, AMapLocation aMapLocation) {
        if (point == null) return;
        point.setAccuracy(aMapLocation.getAccuracy());
        point.setBearing(aMapLocation.getBearing());
        point.setLat(aMapLocation.getLatitude());
        point.setLng(aMapLocation.getLongitude());
        point.setSpeed(aMapLocation.getSpeed());
        point.setLocType(aMapLocation.getLocationType());
        point.setTimemap(aMapLocation.getTime());
    }

    /**
     * 上传定位
     *
     * @param latitude  经 度
     * @param longitude 纬 度
     */
    private void uploadLocation(double latitude, double longitude) {
        HashMap<String, String> argsMap = new HashMap<>(3);
        argsMap.put("lat", String.valueOf(latitude));
        argsMap.put("lng", String.valueOf(longitude));
        argsMap.put("mapType", "0");
        wrapAsync(mDriverService.uploadLocation(argsMap)).subscribe(new ResultObserver<HttpResult<String>>() {
            @Override
            public void onSuccess(HttpResult<String> value) {

            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LocationEvent event) {
        switch (event) {
            case LOCATION_ENABLE:
                mlocationClient.startLocation();
                break;
            case LOCATION_UNABLE:
                mlocationClient.stopLocation();
                break;
            case LOCATION_ONCE:
                isOnceLocation = true;
                mlocationClient.startLocation();
                break;
            case LOCATION_REFRESH:
                isNeedResult = true;
                mlocationClient.startLocation();
                break;
            case LOCATION_DISTANCE_START:
                Log.i(TAG, "onMessageEvent: LOCATION_DISTANCE_START");
                //测算司机实时距离
                clearDistanceData();
                isCaculateDistance = true;
                mlocationClient.startLocation();
                break;
            case LOCATION_DISTANCE_STOP:
                isCaculateDistance = false;
                break;
        }
    }

    /**
     * 补偿里程计算
     *
     * @param startP              最后一次定位信息点，需要从服务器获取
     * @param curDistanceTotal    当前行程总里程，需要从服务器获取
     * @param curDistanceGps      当前行程GPS累积里程，需要从服务器获取
     * @param curDistanceRepaired 当前行程累加里程，需要从服务器获取
     */
    public static void needRepaireDistance(GpsPoint startP, double curDistanceTotal, double curDistanceGps, double curDistanceRepaired) {

    }

    /**
     * 算距离 建成提供
     */
    public static double distance_guo(double lat1, double lon1, double lat2, double lon2) {
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        double miles = dist * 60 * 1.1515 * 1.609344 * 1000;
        return miles;
    }

    //将角度转换为弧度
    static double deg2rad(double degree) {
        return degree / 180 * Math.PI;
    }

    //将弧度转换为角度
    static double rad2deg(double radian) {
        return radian * 180 / Math.PI;
    }


    //定位成功回调信息，设置相关消息
//                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                aMapLocation.getAccuracy();//获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//                Date date = new Date(aMapLocation.getTime());
//                df.format(date);//定位时间
//                aMapLocation.getSatellites();	//获取当前可用卫星数量, 仅在GPS定位时有效,;
//                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                aMapLocation.getCountry();//国家信息
//                aMapLocation.getProvince();//省信息
//                aMapLocation.getCity();//城市信息
//                aMapLocation.getDistrict();//城区信息
//                aMapLocation.getStreet();//街道信息
//                aMapLocation.getStreetNum();//街道门牌号信息
//                aMapLocation.getCityCode();//城市编码
//                aMapLocation.getAdCode();//地区编码
//                aMapLocation.getAoiName();//获取当前定位点的AOI信息


    //-------------------------------------------------------------以下订单相关----------------------------------
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OrderEvent event) {
        Log.i(TAG, "onMessageEvent: OrderEvent" + event.getLocationValue());
        switch (event) {
            case ORDER_PULL_ENABLE:
                startPullOrder();
                break;
            case ORDER_PULL_UNABLE:
                stopPullOrder();
                break;
            case ORDER_GET_CANCEL_ENABLE:
                startPullCancelOrder();
                break;
            case ORDER_GET_CANCEL_UNABLE:
                stopPullCancelOrder();
                break;
        }
    }

    private void startPullCancelOrder() {
        Log.i(TAG, "startPullCancelOrdr: " + "开始循环获取乘客是否取消订单的通知");
        if (mCancelOrderDisposable != null && !mCancelOrderDisposable.isDisposed()) return;
        mCancelOrderDisposable = Flowable.interval(INTERVAL_PULL_ORDER, TimeUnit.SECONDS)
                .onBackpressureLatest()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        pullCancelOrder();
                    }
                });
    }

    private void stopPullCancelOrder() {
        Log.i(TAG, "stopPullCancelOrder: " + "停止循环获取乘客是否取消订单的通知");
        if (mCancelOrderDisposable != null && !mCancelOrderDisposable.isDisposed()) {
            mCancelOrderDisposable.dispose();
        }
    }

    private void pullCancelOrder() {
        wrapHttp(mHttpManager.getOrderApi().pushOrderCancel()).subscribe(new ResultObserver<List<OrderInfo>>() {
            @Override
            public void onSuccess(List<OrderInfo> value) {
                ToastUtils.showString("订单已被取消");
            }
        });
    }

    private void stopPullOrder() {
        Log.i(TAG, "stopPullOrder: " + "取消循环拉取订单");
        if (mOrderDisposable != null && !mOrderDisposable.isDisposed()) {
            mOrderDisposable.dispose();
        }
    }

    private void startPullOrder() {
        Log.i(TAG, "startPullOrder: " + "开始循环拉取订单");
        if (mOrderDisposable != null && !mOrderDisposable.isDisposed()) return;
        mOrderDisposable = Flowable.interval(INTERVAL_PULL_ORDER, TimeUnit.SECONDS)
                .onBackpressureLatest()
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        if (GlobeConstants.DRIVER_STATSU == GlobeConstants.DRIVER_STATSU_WORK) {
                            pullOrder();
                        }
                    }
                });
    }

    private void pullOrder() {
        wrapHttp(mHttpManager.getDriverService().pullOrder()).subscribe(new ResultObserver<List<OrderInfo>>() {
            @Override
            public void onSuccess(List<OrderInfo> value) {
                if (value.size() > 0) {
                    checkNotify(value);
                }
            }
        });
    }

    private void checkNotify(List<OrderInfo> value) {
        LhyActivity currentActivity = BaseApplication.getInstance().getCurrentActivity();
        if (GlobeConstants.ORDER_STATSU == GlobeConstants.ORDER_STATSU_ONDOING || GlobeConstants.DRIVER_STATSU == GlobeConstants.DRIVER_STATSU_REST) {
            return;
        }
        if (isBackground() || currentActivity == null || !currentActivity.isResume()) {
            notifyOrder(value.get(0));
        } else {
            if (GlobeConstants.ORDER_STATSU == GlobeConstants.ORDER_STATSU_NO) {
                if (mOrderDialog != null && mOrderDialog.isShowing() || currentActivity instanceof CaptureOrderActivity)
                    return;
                showOrderDialog(currentActivity, value.get(0));
            }
        }
    }

    private void showOrderDialog(final Activity currentActivity, final OrderInfo orderInfo) {
        if (mOrderDialog != null) {
            return;
        }
        mOrderDialog = new AlertDialog.Builder(currentActivity)
                .setTitle("您有新的订单")
                .setMessage("起始位置：" + orderInfo.getOriginAddress() + "\n" + "目的地：" + orderInfo.getDestAddress())
                .setPositiveButton("接单", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(currentActivity, CaptureOrderActivity.class);
                        intent.putExtra(GlobeConstants.ORDER_INFO, orderInfo);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).setNegativeButton("勇敢的拒绝", null)
                .setOnDismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        mOrderDialog = null;
                    }
                })
                .setCancelable(false)
                .show();
        // mOrderDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
    }


    //初始化通知
    private void initNotification() {
        mNotifyBuilder = new NotificationCompat.Builder(this);
        mNotifyBuilder.setContentTitle("订单通知")
                .setContentText("点击查看详情")
                .setSmallIcon(R.mipmap.loading)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.mis_asv))
                .setAutoCancel(true)
                .setOnlyAlertOnce(true)
                .setWhen(System.currentTimeMillis())
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true);
        mNotificationManager = (NotificationManager) LhyApplication.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
    }

    private void notifyOrder(OrderInfo orderInfo) {
        Intent intent = new Intent(this, CaptureOrderActivity.class);
        intent.putExtra(GlobeConstants.ORDER_INFO, orderInfo);
        Notification notification = mNotifyBuilder.setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT))
                .build();
        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;
        mNotificationManager.notify(1, notification);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        if (mOrderDialog != null) {
            mOrderDialog.dismiss();
            mOrderDialog = null;
        }
        if (mOrderDisposable != null) {
            if (!mOrderDisposable.isDisposed()) {
                mOrderDisposable.dispose();
            }
            mOrderDisposable = null;
        }
    }

    /**
     * 判断应用是否处于后台
     */
    public boolean isBackground() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(getPackageName())) {
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    Logger.i(TAG, "处于前台：" + appProcess.processName);
                    return false;
                } else {
                    Logger.i(TAG, "处于后台：" + appProcess.processName);
                    return true;
                }
            }
        }
        return true;
    }

}
