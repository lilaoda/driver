package bus.driver.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.HashMap;

import bus.driver.base.GlobeConstants;
import bus.driver.bean.GpsPoint;
import bus.driver.bean.event.DistanceEvent;
import bus.driver.bean.event.LocationEvent;
import bus.driver.bean.event.LocationResultEvent;
import bus.driver.data.HttpManager;
import bus.driver.data.remote.DriverService;
import bus.driver.data.remote.HttpResult;
import bus.driver.utils.EventBusUtls;
import lhy.lhylibrary.http.ResultObserver;

import static bus.driver.utils.RxUtils.wrapAsync;
import static lhy.lhylibrary.base.LhyApplication.getContext;

/**
 * Created by Lilaoda on 2017/9/27.
 * Email:749948218@qq.com
 * 定位服务
 * 第一次开启服务时自动定位一次就关闭，以后再通过事件开启定时，会一直定位，直到调用关闭定位才会停止定位
 */

public class LocationService extends Service implements AMapLocationListener {

    public static final String TAG = "LocationService";
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

    private AMapLocationClient mlocationClient;
    private DriverService mDriverService;
    private DistanceEvent mDistanceEvent;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        mDriverService = HttpManager.instance().getDriverService();
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
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
            if (aMapLocation.getErrorCode() == 0) {
                longitude = aMapLocation.getLongitude();
                latitude = aMapLocation.getLatitude();
                Log.d(TAG, "onLocationChanged: " + longitude + "_" + latitude + aMapLocation.getAoiName());

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
                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());

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
            if (lastGpsPoint.getSpeed() < SPEED_LOW_LIMIT) {
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

}
