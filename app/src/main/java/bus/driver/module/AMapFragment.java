package bus.driver.module;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.CameraUpdate;
import com.amap.api.maps.CameraUpdateFactory;
import com.amap.api.maps.TextureMapView;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.LatLngBounds;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.Polyline;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.maps.utils.SpatialRelationUtil;
import com.amap.api.maps.utils.overlay.SmoothMoveMarker;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RideRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkRouteResult;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import bus.driver.R;
import bus.driver.bean.event.LocationEvent;
import bus.driver.bean.event.LocationResultEvent;
import bus.driver.data.AMapManager;
import bus.driver.utils.overlay.AMapUtil;
import bus.driver.utils.overlay.DrivingRouteOverlay;
import bus.driver.service.DriverService;
import bus.driver.utils.EventBusUtls;
import lhy.lhylibrary.base.LhyFragment;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.ImageFactory;
import lhy.lhylibrary.utils.ToastUtils;

/**
 * Created by Liheyu on 2017/9/12.
 * Email:liheyu999@163.com
 */

public class AMapFragment extends LhyFragment implements AMap.OnPOIClickListener, AMap.OnMarkerClickListener, RouteSearch.OnRouteSearchListener, AMap.OnMapLoadedListener {

    /**
     * 定位时间间隔，mS
     */
    public static final int INTERVAL_LOCAITON = 3000;

    private TextureMapView mapView;
    private Marker mGrowMarker;
    protected AMap mAMap;
    private MyLocationStyle mMyLocationStyle;
    private LinearLayout llRoot;
    private boolean isResume;
    private Polyline mPolyline;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public static AMapFragment newInstance() {
        Bundle args = new Bundle();
        AMapFragment fragment = new AMapFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_amap, null);
        llRoot = (LinearLayout) view.findViewById(R.id.ll_root);
        addMapView(savedInstanceState);
        initMap();
        return view;
    }

    private void addMapView(@Nullable Bundle savedInstanceState) {
        LatLng centerMyPoint = new LatLng(DriverService.latitude, DriverService.longitude);
        AMapOptions mapOptions = new AMapOptions();
        mapOptions.camera(new CameraPosition(centerMyPoint, AMapManager.ZOOM_SCALE_VALUE, 0, 0));
        mapView = new TextureMapView(getContext(), mapOptions);
        llRoot.addView(mapView, 0, new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mapView.onCreate(savedInstanceState);
    }

    private void initMap() {
        if (mAMap == null) {
            mAMap = mapView.getMap();
            mAMap.setOnMapLoadedListener(this);
            onMapCreated();
        }
    }

    protected void onMapCreated() {
    }

    @Override
    public void onResume() {
        super.onResume();
        isResume = true;
        mapView.onResume();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        isResume = false;
        mapView.onPause();
        EventBus.getDefault().unregister(this);
    }

    public boolean isResume() {
        return isResume;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapView.onDestroy();
    }

    protected void initLocation(AMap.OnMyLocationChangeListener listener) {
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_map_location_direction);
        ImageFactory imageFactory = new ImageFactory();
        Bitmap ratio = imageFactory.ratio(bitmap, CommonUtils.dp2px(getContext(), 5), CommonUtils.dp2px(getContext(), 5));
        BitmapDescriptor pointIcon = BitmapDescriptorFactory.fromBitmap(bitmap);
        mMyLocationStyle = new MyLocationStyle();
        mMyLocationStyle.myLocationIcon(pointIcon);
        mMyLocationStyle.strokeWidth(0F);
        mMyLocationStyle.radiusFillColor(getResources().getColor(android.R.color.transparent));
        mMyLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);
//        mMyLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATE);
        mMyLocationStyle.interval(INTERVAL_LOCAITON);
        mMyLocationStyle.showMyLocation(true);
//        mAMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_SCALE_VALUE));
        moveMapToCurrentLocation();
        mAMap.setMyLocationStyle(mMyLocationStyle);
        mAMap.setMyLocationEnabled(true);// 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        mAMap.getUiSettings().setMyLocationButtonEnabled(false);//设置默认定位按钮是否显示，非必需设置。
        mAMap.getUiSettings().setZoomControlsEnabled(false);
        if (listener != null) {
            mAMap.setOnMyLocationChangeListener(listener);
        }
    }


    protected void addStartEndMark(LatLng startLatLng, String startTitle, LatLng endLatLng, String endTitle) {
        mAMap.clear();
        ArrayList<MarkerOptions> optionses = new ArrayList<MarkerOptions>();
        optionses.add(getTargetMarkerOptions(startLatLng, startTitle));
        optionses.add(getStartMarkerOptions(endLatLng, endTitle));
        mAMap.addMarkers(optionses, false);

        LatLngBounds lngBounds = new LatLngBounds.Builder()
                .include(endLatLng)
                .include(startLatLng)
                .build();
        int bottom = CommonUtils.dp2px(getContext(), 100);
        int padding = CommonUtils.dp2px(getContext(), 50);
        int top = CommonUtils.dp2px(getContext(), 100);
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBoundsRect(lngBounds, padding, padding, top, bottom));//左右上下的PADDING
//        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(lngBounds,bottom,bottom,top));//宽高和PADDING
    }

    @NonNull
    private MarkerOptions getStartMarkerOptions(LatLng startLatLng, String title) {
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(startLatLng);
        View mAddressView = LayoutInflater.from(getContext()).inflate(R.layout.map_location, null);
        TextView mAddressText = (TextView) mAddressView.findViewById(R.id.text_location);
        ImageView mAddressImgIndicator = (ImageView) mAddressView.findViewById(R.id.img_indicate);
        ImageView mAddressImgPoint = (ImageView) mAddressView.findViewById(R.id.img_point);
        mAddressText.setText(title);
        mAddressImgIndicator.setImageResource(R.mipmap.map_start);
        mAddressImgPoint.setImageResource(R.mipmap.map_start_point);
        markOptiopns.icon(BitmapDescriptorFactory.fromView(mAddressView));
        return markOptiopns;
    }

    @NonNull
    private MarkerOptions getTargetMarkerOptions(LatLng endLatLng, String title) {
        MarkerOptions markOptiopns = new MarkerOptions();
        markOptiopns.position(endLatLng);
        View mAddressView = LayoutInflater.from(getContext()).inflate(R.layout.map_location, null);
        TextView mAddressText = (TextView) mAddressView.findViewById(R.id.text_location);
        ImageView mAddressImgIndicator = (ImageView) mAddressView.findViewById(R.id.img_indicate);
        ImageView mAddressImgPoint = (ImageView) mAddressView.findViewById(R.id.img_point);
        mAddressText.setText(title);
        mAddressImgIndicator.setImageResource(R.mipmap.map_end);
        mAddressImgPoint.setImageResource(R.mipmap.map_end_point);
        markOptiopns.icon(BitmapDescriptorFactory.fromView(mAddressView));
        return markOptiopns;
    }


    /**
     * 移动地图到指定位置
     */
    public void moveMapToCurrentLocation() {
        CameraUpdate cameraUpate = CameraUpdateFactory.newLatLngZoom(new LatLng(DriverService.latitude, DriverService.longitude), AMapManager.ZOOM_SCALE_VALUE);
        mAMap.animateCamera(cameraUpate);
    }

    protected void routeCaculate(LatLng startLatLng, LatLng ednLatLng, DrawRouteListener listener) {
        mDrawRouteListener = listener;
        routeCaculate(startLatLng, ednLatLng, this);
    }

    /**
     * 驾车路线规划
     */
    private void routeCaculate(LatLng latLng, LatLng latLng1, RouteSearch.OnRouteSearchListener listener) {
        RouteSearch routeSearch = new RouteSearch(getContext());
        routeSearch.setRouteSearchListener(listener);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(latLng.latitude, latLng.longitude), new LatLonPoint(latLng1.latitude, latLng1.longitude));
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }


    //重新定位，移动到定位位置
    public void refreshLocation() {
        EventBusUtls.notifyLocation(LocationEvent.LOCATION_REFRESH);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LocationResultEvent event) {
        if (event.isLocationSuccess()) {
            moveMapToCurrentLocation();
        } else {
            ToastUtils.showString(event.getErrorInfo());
        }
    }

    @Override
    public void onMapLoaded() {

    }

    @Override
    public void onPOIClick(Poi poi) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        return false;
    }

    /**
     * 添加一个从地上生长的Marker
     */
    public void addGrowMarker(AMap aMap, LatLng latLng) {
        if (mGrowMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions().icon(BitmapDescriptorFactory
                    .defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                    .position(latLng);
            mGrowMarker = aMap.addMarker(markerOptions);
        }
        startGrowAnimation(mGrowMarker);
    }

    private void startGrowAnimation(Marker marker) {
        if (marker != null) {
            Animation animation = new ScaleAnimation(0, 1, 0, 1);
            animation.setInterpolator(new LinearInterpolator());
            animation.setDuration(1000);
            marker.setAnimation(animation);
            marker.startAnimation();
        }
    }

    public void startNavi(Poi start, Poi end, AmapNaviType naviType) {
        AmapNaviParams amapNaviParams = new AmapNaviParams(start, null, end, naviType);
        AmapNaviPage.getInstance().showRouteActivity(getContext(), amapNaviParams, null);
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

    //起始终点画线
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
                    setDrawRouteSuccessInfo(AMapUtil.getFriendlyLength(dis), AMapUtil.getFriendlyTime(dur), taxiCost);
                } else if (result.getPaths() == null) {
                    setDrawRouteErrorInfo("没有结果");
                }
            } else {
                setDrawRouteErrorInfo("没有结果");
            }
        } else {
            setDrawRouteErrorInfo("错误码：" + errorCode);
        }
    }

    private void setDrawRouteSuccessInfo(String friendlyLength, String friendlyTime, int taxiCost) {
        if (mDrawRouteListener != null) {
            mDrawRouteListener.drawRouteSuccess(friendlyLength, friendlyTime, taxiCost);
        }
    }

    private void setDrawRouteErrorInfo(String error) {
        if (mDrawRouteListener != null) {
            mDrawRouteListener.drawRouteFailue(error);
        }
    }

    private DrawRouteListener mDrawRouteListener;

    /**
     * 路线规划结果回调
     */
    public interface DrawRouteListener {

        void drawRouteSuccess(String friendlyLength, String friendlyTime, int taxiCost);

        void drawRouteFailue(String error);
    }

    /**
     * 添加轨迹线
     */
    protected void addPolylineInPlayGround(List<LatLng> list) {
//        List<LatLng> list = readLatLngs();
//        List<Integer> colorList = new ArrayList<Integer>();
//        List<BitmapDescriptor> bitmapDescriptors = new ArrayList<BitmapDescriptor>();
//
//        int[] colors = new int[]{Color.argb(255, 0, 255, 0),Color.argb(255, 255, 255, 0),Color.argb(255, 255, 0, 0)};

//        //用一个数组来存放纹理
//        List<BitmapDescriptor> textureList = new ArrayList<BitmapDescriptor>();
//        textureList.add(BitmapDescriptorFactory.fromResource(R.drawable.custtexture));
//
//        List<Integer> texIndexList = new ArrayList<Integer>();
//        texIndexList.add(0);//对应上面的第0个纹理
//        texIndexList.add(1);
//        texIndexList.add(2);
//
//        Random random = new Random();
//        for (int i = 0; i < list.size(); i++) {
//            colorList.add(colors[random.nextInt(3)]);
//            bitmapDescriptors.add(textureList.get(0));
//
//        }

        mPolyline = mAMap.addPolyline(new PolylineOptions().setCustomTexture(BitmapDescriptorFactory.fromResource(R.mipmap.custtexture)) //setCustomTextureList(bitmapDescriptors)
//				.setCustomTextureIndex(texIndexList)
                .addAll(list)
                .useGradient(true)
                .width(18));

        LatLngBounds bounds = new LatLngBounds(list.get(0), list.get(list.size() - 2));
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
    }

    /**
     * @param points 轨迹坐标点
     */
    protected void startMove(List<LatLng> points) {
        if (points.size() < 0) return;
        LatLngBounds bounds = new LatLngBounds(points.get(0), points.get(points.size() - 2));
        mAMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));

        SmoothMoveMarker smoothMarker = new SmoothMoveMarker(mAMap);
        // 设置滑动的图标
        smoothMarker.setDescriptor(BitmapDescriptorFactory.fromResource(R.mipmap.icon_car));

        LatLng drivePoint = points.get(0);
        Pair<Integer, LatLng> pair = SpatialRelationUtil.calShortestDistancePoint(points, drivePoint);
        points.set(pair.first, drivePoint);
        List<LatLng> subList = points.subList(pair.first, points.size());

        // 设置滑动的轨迹左边点
        smoothMarker.setPoints(subList);
        // 设置滑动的总时间
        smoothMarker.setTotalDuration(40);
        // 开始滑动
        smoothMarker.startSmoothMove();
    }
}
