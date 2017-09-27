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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import bus.driver.bean.event.LocationResultEvent;
import bus.driver.bean.event.StartLocationEvent;

import static lhy.lhylibrary.base.LhyApplication.getContext;

/**
 * Created by Lilaoda on 2017/9/27.
 * Email:749948218@qq.com
 * 定位服务
 * 第一次开启服务时自动定位一次就关闭，以后再通过事件开启定时，会一直定位，直到调用关闭定位才会停止定位
 */

public class LocationService extends Service implements AMapLocationListener {

    public static final String TAG = "LocationService";
    public static final String FIRST_LOCATE = "first_locate";
    //默认是北京的经纬度
    public static double latitude = 39.904989;
    public static double longitude = 116.405285;

    private AMapLocationClient mlocationClient;
    private boolean isFirstLocate = false;
    private boolean isNeedResult = false;

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
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
        isFirstLocate = intent.getBooleanExtra(FIRST_LOCATE, false);
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
        mLocationOption.setInterval(3000); //设置定位间隔,单位毫秒,默认为2000ms
        mlocationClient.setLocationOption(mLocationOption);   //设置定位参数
        mlocationClient.setLocationListener(this);
        // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
        // 注意设置合适的定位时间的间隔（最小间隔支持为1000ms），并且在合适时间调用stopLocation()方法来取消定位请求
        // 在定位结束后，在合适的生命周期调用onDestroy()方法
        // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
    }

    @Override
    public void onLocationChanged(AMapLocation aMapLocation) {
        if (aMapLocation != null) {
            if (aMapLocation.getErrorCode() == 0) {
                //定位成功回调信息，设置相关消息
                aMapLocation.getLocationType();//获取当前定位结果来源，如网络定位结果，详见定位类型表
                aMapLocation.getAccuracy();//获取精度信息
                SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
                Date date = new Date(aMapLocation.getTime());
                df.format(date);//定位时间
                aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                aMapLocation.getCountry();//国家信息
                aMapLocation.getProvince();//省信息
                aMapLocation.getCity();//城市信息
                aMapLocation.getDistrict();//城区信息
                aMapLocation.getStreet();//街道信息
                aMapLocation.getStreetNum();//街道门牌号信息
                aMapLocation.getCityCode();//城市编码
                aMapLocation.getAdCode();//地区编码
                aMapLocation.getAoiName();//获取当前定位点的AOI信息

                longitude = aMapLocation.getLongitude();
                latitude = aMapLocation.getLatitude();
                Log.d(TAG, "onLocationChanged: " + longitude + "_" + latitude + aMapLocation.getAoiName());

                if (isFirstLocate) {
                    isFirstLocate = false;
                    mlocationClient.stopLocation();
                }
                if (isNeedResult) {
                    EventBus.getDefault().post(new LocationResultEvent(true));
                    isNeedResult = false;
                }
            } else {
                if (isNeedResult) {
                    String errorInfo = "定位失败:" + aMapLocation.getErrorCode() + "," + aMapLocation.getErrorInfo();
                    EventBus.getDefault().post(new LocationResultEvent(false,errorInfo));
                    isNeedResult = false;
                }

                Log.e("AmapError", "location Error, ErrCode:"
                        + aMapLocation.getErrorCode() + ", errInfo:"
                        + aMapLocation.getErrorInfo());
            }
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(StartLocationEvent event) {
        isNeedResult = event.isNeedResult();
        if (event.isStartLocation()) {
            Log.d(TAG, "onMessageEvent: " + "开始定位");
            mlocationClient.startLocation();
        } else {
            mlocationClient.stopLocation();
        }
    }
}
