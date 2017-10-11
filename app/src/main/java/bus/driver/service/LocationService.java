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

import bus.driver.bean.event.LocationEvent;
import bus.driver.bean.event.LocationResultEvent;
import bus.driver.data.HttpManager;
import bus.driver.data.remote.DriverService;

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
     * 第一次启动定位的标记 第一次启动后获取定位结果会立即关闭
     */
    public static final String FIRST_LOCATE = "first_locate";
    //默认是北京的经纬度
    public static double latitude = 39.904989;
    public static double longitude = 116.405285;

    /**
     * 只获取一次定位就关闭定位，为了地图在初始化时以定位点开始,或者不在连接定位状态下的刷新定位时使用
     */
    private boolean isOnceLocation = false;
    /**
     * 是否需要定位结果，其它页面在刷新定位时使用
     */
    private boolean isNeedResult = false;
    private AMapLocationClient mlocationClient;
    private DriverService mDriverService;

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
        if (intent != null && intent.hasExtra(FIRST_LOCATE)) {
            isOnceLocation = intent.getBooleanExtra(FIRST_LOCATE, false);
            if (isOnceLocation && mlocationClient != null) {
                mlocationClient.startLocation();
            }
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
        mLocationOption.setInterval(60000); //设置定位间隔,单位毫秒,默认为2000ms
        mlocationClient.setLocationOption(mLocationOption);   //设置定位参数
        mlocationClient.setLocationListener(this);
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
//                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
//                aMapLocation.getAccuracy();//获取精度信息
//                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
//                Date date = new Date(aMapLocation.getTime());
//                df.format(date);//定位时间
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

                longitude = aMapLocation.getLongitude();
                latitude = aMapLocation.getLatitude();
                Log.d(TAG, "onLocationChanged: " + longitude + "_" + latitude + aMapLocation.getAoiName());

                if (isOnceLocation) {
                    isOnceLocation = false;
                    mlocationClient.stopLocation();
                } else {
                    uploadLocation(latitude, longitude);
                }

                if (isNeedResult) {
                    EventBus.getDefault().post(new LocationResultEvent(true));
                    isNeedResult = false;
                }
            } else {
                if (isNeedResult) {
                    String errorInfo = "定位失败:" + aMapLocation.getErrorCode() + "," + aMapLocation.getErrorInfo();
                    isNeedResult = false;
                    EventBus.getDefault().post(new LocationResultEvent(false, errorInfo));
                }

                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    private void uploadLocation(double latitude, double longitude) {
        HashMap<String, String> argsMap = new HashMap<>(3);
        argsMap.put("lat", String.valueOf(latitude));
        argsMap.put("lng", String.valueOf(longitude));
        argsMap.put("mapType", "0");
        wrapAsync(mDriverService.uploadLocation(argsMap)).subscribe();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LocationEvent event) {
        Log.i(TAG, "onMessageEvent: LocationEvent" + event.getLocationValue());
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
            case LOCATION_NEED_RESULT:
                isNeedResult = true;
                mlocationClient.startLocation();
                break;
        }
    }

}
