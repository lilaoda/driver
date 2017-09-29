package bus.driver.module.order;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import bus.driver.R;
import bus.driver.bean.event.LocationResultEvent;
import bus.driver.bean.event.StartLocationEvent;
import bus.driver.data.AMapManager;
import bus.driver.module.AMapFragment;
import bus.driver.overlay.AMapUtil;
import bus.driver.overlay.DrivingRouteOverlay;
import bus.driver.service.LocationService;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.ImageFactory;
import lhy.lhylibrary.utils.ToastUtils;

import static bus.driver.module.main.MainFragment.ZOOM_SCALE_VALUE;

/**
 * Created by Lilaoda on 2017/9/29.
 * Email:749948218@qq.com
 */

public class OrderOngoingFragment extends AMapFragment implements AMap.OnMapLoadedListener, AMap.OnMyLocationChangeListener, RouteSearch.OnRouteSearchListener {

    @BindView(R.id.ib_reLocate)
    ImageButton ibReLocate;
    @BindView(R.id.fl_root)
    FrameLayout flRoot;
    TextureMapView mapView;
    private Unbinder mUnbinder;
    private AMap mAMap;
    private MyLocationStyle mMyLocationStyle;
    AMapManager mAMapManager;
    LatLng mTargetLatLng = new LatLng(23.220226, 113.306295);

    public static OrderOngoingFragment newInstance() {

        Bundle args = new Bundle();

        OrderOngoingFragment fragment = new OrderOngoingFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAMapManager = AMapManager.instance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_map, null);
        mUnbinder = ButterKnife.bind(this, view);
        addMapView(savedInstanceState);
        initMap();
//        initLocation();
        mAMapManager.routeCaculate(new LatLng(LocationService.latitude, LocationService.longitude), mTargetLatLng, this);
        return view;
    }

    private void addMapView(@Nullable Bundle savedInstanceState) {
        LatLng centerMyPoint = new LatLng(LocationService.latitude, LocationService.longitude);
        AMapOptions mapOptions = new AMapOptions();
        mapOptions.camera(new CameraPosition(centerMyPoint, ZOOM_SCALE_VALUE, 0, 0));
        mapView = new TextureMapView(getContext(), mapOptions);
        flRoot.addView(mapView, 0, new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mapView.onCreate(savedInstanceState);
    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mapView.getMap();
            mAMap.setOnMapLoadedListener(this);
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
        mUnbinder.unbind();
    }

    private void initLocation() {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_map_location);
        ImageFactory imageFactory = new ImageFactory();
        Bitmap ratio = imageFactory.ratio(bitmap, CommonUtils.dp2px(getContext(), 5), CommonUtils.dp2px(getContext(), 5));
        BitmapDescriptor pointIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
        mMyLocationStyle = new MyLocationStyle();
        mMyLocationStyle.myLocationIcon(pointIcon);
        mMyLocationStyle.strokeWidth(0F);
        mMyLocationStyle.radiusFillColor(getResources().getColor(android.R.color.transparent));
        mMyLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        mMyLocationStyle.showMyLocation(true);
//        mAMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_SCALE_VALUE));
        moveMapToCurrentLocation();
        mAMap.setMyLocationStyle(mMyLocationStyle);
        mAMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示，非必需设置。
        mAMap.getUiSettings().setZoomControlsEnabled(false);
    }

    private void moveMapToCurrentLocation() {
        CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(new LatLng(LocationService.latitude, LocationService.longitude), ZOOM_SCALE_VALUE);
        mAMap.animateCamera(cameraUpate);
    }

    //重新定位，移动到定位位置
    private void refreshLocation() {
        StartLocationEvent event = new StartLocationEvent(true, true);
        EventBus.getDefault().post(event);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LocationResultEvent event) {
        EventBus.getDefault().post(new StartLocationEvent(false));
        if (event.isLocationSuccess()) {
            moveMapToCurrentLocation();
        } else {
            ToastUtils.showString(event.getErrorInfo());
        }
    }

    @OnClick({R.id.btn_navi, R.id.btn_call, R.id.ib_reLocate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_reLocate:
                refreshLocation();
                break;
            case R.id.btn_navi:
                startNavi(new Poi("我的位置", new LatLng(LocationService.latitude, LocationService.longitude), null), new Poi("终点", mTargetLatLng, null), AmapNaviType.DRIVER);
                break;
            case R.id.btn_call:
                break;
        }
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onMyLocationChange(Location location) {

    }

    @Override
    public void onBusRouteSearched(BusRouteResult busRouteResult, int i) {

    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult driveRouteResult, int i) {
        drawRoute(driveRouteResult, i);
    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult walkRouteResult, int i) {

    }

    @Override
    public void onRideRouteSearched(RideRouteResult rideRouteResult, int i) {

    }

    private void drawRoute(DriveRouteResult result, int errorCode) {
        mAMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    final DrivePath drivePath = result.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            getContext(), mAMap, drivePath,
                            result.getStartPos(),
                            result.getTargetPos(), null);
                    drivingRouteOverlay.setNodeIconVisibility(false);//设置节点marker是否显示
                    drivingRouteOverlay.setIsColorfulline(true);//是否用颜色展示交通拥堵情况，默认true
                    drivingRouteOverlay.setRouteWidth(12F);
                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();

                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    int taxiCost = (int) result.getTaxiCost();
                    String des = AMapUtil.getFriendlyTime(dur) + "(" + AMapUtil.getFriendlyLength(dis) + ")";
                } else if (result.getPaths() == null) {
                    ToastUtils.showString("没有结果");
                }
            } else {
                ToastUtils.showString("没有结果");
            }
        } else {
            ToastUtils.showInt(errorCode);
        }
    }

}
