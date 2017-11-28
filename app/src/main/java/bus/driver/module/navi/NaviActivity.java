package bus.driver.module.navi;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.navi.INaviInfoCallback;
import com.amap.api.navi.model.AMapNaviLocation;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.base.Constants;
import bus.driver.bean.OrderInfo;
import bus.driver.service.DriverService;
import bus.driver.utils.navi.AmapTTSController;

/**
 * Created by Lilaoda on 2017/10/12.
 * Email:749948218@qq.com
 * 导航页面
 */

public class NaviActivity extends BaseActivity implements INaviInfoCallback {


    private AmapTTSController amapTTSController;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navi);
        OrderInfo orderInfo = getIntent().getParcelableExtra(Constants.ORDER_INFO);

        amapTTSController = AmapTTSController.getInstance(getApplicationContext());
        //TODO 空指针
        amapTTSController.init();

        Poi start = new Poi("起点", new LatLng(DriverService.latitude, DriverService.longitude), "");//起点
        Poi end = new Poi("终点", new LatLng(orderInfo.getDestLat(), orderInfo.getDestLng()), "");//终点
        AmapNaviParams amapNaviParams = new AmapNaviParams(start, null, end, AmapNaviType.DRIVER);
        AmapNaviPage.getInstance().showRouteActivity(getApplicationContext(), amapNaviParams, NaviActivity.this);
    }

    @Override
    public void onInitNaviFailure() {

    }

    @Override
    public void onLocationChange(AMapNaviLocation aMapNaviLocation) {

    }

    @Override
    public void onArriveDestination(boolean b) {

    }

    @Override
    public void onStartNavi(int i) {

    }

    @Override
    public void onCalculateRouteSuccess(int[] ints) {

    }

    @Override
    public void onCalculateRouteFailure(int i) {

    }

    @Override
    public void onGetNavigationText(String s) {
        amapTTSController.onGetNavigationText(s);
    }

    @Override
    public void onStopSpeaking() {
        amapTTSController.stopSpeaking();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        amapTTSController.destroy();
    }
}
