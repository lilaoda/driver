package bus.driver.data;

import android.content.Context;

import com.amap.api.maps.AMapUtils;
import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.amap.api.services.route.RouteSearch;

import java.util.ArrayList;
import java.util.List;

import bus.driver.R;
import bus.driver.base.BaseApplication;
import bus.driver.bean.PoiInfo;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import lhy.lhylibrary.http.exception.ApiException;
import lhy.lhylibrary.utils.CommonUtils;

import static lhy.lhylibrary.base.LhyApplication.getContext;


/**
 * Created by Liheyu on 2017/9/19.
 * Email:liheyu999@163.com
 */


public class AMapManager {

    public static final int ZOOM_SCALE_VALUE = 15;

    private static AMapManager instance;
    private Context mContext;


    private AMapManager(Context context) {
        mContext = context;
    }

    public static synchronized AMapManager instance() {
        if (instance == null) {
            synchronized (AMapManager.class) {
                if (instance == null) {
                    instance = new AMapManager(BaseApplication.getContext());
                }
            }
        }
        return instance;
    }

    /**
     * @param keyWord    搜索关键字
     * @param searchType 第二个参数表示POI搜索类型，二者选填其一，选用POI搜索类型时建议填写类型代码
     * @param cityCode   cityCode表示POI搜索区域，可以是城市编码也可以是城市名称，也可以传空字符串，空字符串代表全国在全国范围内进行搜索
     * @return 搜索到的集合
     */
    public Observable<List<PoiInfo>> search(final String keyWord, final String searchType, final String cityCode) {
        return Observable.create(new ObservableOnSubscribe<List<PoiInfo>>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<List<PoiInfo>> e) throws Exception {
                PoiSearch.Query query = new PoiSearch.Query(keyWord, searchType, cityCode);
                query.setPageSize(10);
                query.setPageNum(0);
                PoiSearch poiSearch = new PoiSearch(mContext, query);
                poiSearch.setOnPoiSearchListener(new PoiSearch.OnPoiSearchListener() {
                    @Override
                    public void onPoiSearched(PoiResult poiResult, int i) {
                        if (i == 1000) {
                            ArrayList<PoiItem> pois = poiResult.getPois();
                            List<PoiInfo> list = new ArrayList<PoiInfo>();
                            for (PoiItem item : pois) {
                                PoiInfo poiInfo = convertPoiItem(item);
                                list.add(poiInfo);
                            }
                            e.onNext(list);
                            e.onComplete();
                        } else {
                            e.onError(new ApiException(CommonUtils.getString(R.string.search_address_failure)));
                        }
                    }

                    @Override
                    public void onPoiItemSearched(PoiItem poiItem, int i) {

                    }
                });
                poiSearch.searchPOIAsyn();
            }
        });
    }

    /**
     * 根据经伟度查询周围PoiItem点
     *
     * @param latLng 经伟度
     */
    public Observable<PoiInfo> search(final LatLng latLng) {
        return Observable.create(new ObservableOnSubscribe<PoiInfo>() {
            @Override
            public void subscribe(@NonNull final ObservableEmitter<PoiInfo> e) throws Exception {
                GeocodeSearch geocodeSearch = new GeocodeSearch(getContext());
                LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
                // 第一个参数表示一个Latlng，第二参数表示范围多少米，第三个参数表示是火系坐标系还是GPS原生坐标系
                RegeocodeQuery regeocodeQuery = new RegeocodeQuery(latLonPoint, 100, GeocodeSearch.AMAP);
                geocodeSearch.getFromLocationAsyn(regeocodeQuery);
                geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
                    @Override
                    public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                        if (i == 1000) {//1000成功，其它为失败
                            if (regeocodeResult != null && regeocodeResult.getRegeocodeAddress() != null
                                    && regeocodeResult.getRegeocodeAddress().getFormatAddress() != null) {
                                List<PoiItem> pois = regeocodeResult.getRegeocodeAddress().getPois();//周为点信息
                                PoiInfo poiInfo = new PoiInfo();
                                if (pois.size() > 0) {
                                    poiInfo = convertPoiItem(pois.get(0));
                                } else {
                                    poiInfo.setLatitude(latLng.latitude);
                                    poiInfo.setLongitude(latLng.longitude);
                                    poiInfo.setTitle(regeocodeResult.getRegeocodeAddress().getFormatAddress());
                                }
                                e.onNext(poiInfo);
                                e.onComplete();
                            } else {
                                e.onError(new ApiException("搜索无结果"));
                            }
                        } else {
                            e.onError(new ApiException("定位失败：" + i));
                        }
                    }

                    @Override
                    public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

                    }
                });
            }
        });
    }

    /**
     * 驾车路线规划
     */
    public void routeCaculate(LatLng latLng, LatLng latLng1, RouteSearch.OnRouteSearchListener listener) {
        RouteSearch routeSearch = new RouteSearch(mContext);
        routeSearch.setRouteSearchListener(listener);
        RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(new LatLonPoint(latLng.latitude, latLng.longitude), new LatLonPoint(latLng1.latitude, latLng1.longitude));
        RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, RouteSearch.DRIVING_SINGLE_DEFAULT, null, null, "");
        routeSearch.calculateDriveRouteAsyn(query);
    }

    public PoiInfo convertPoiItem(PoiItem item) {
        PoiInfo poiInfo = new PoiInfo();
        poiInfo.setLatitude(item.getLatLonPoint().getLatitude());
        poiInfo.setLongitude(item.getLatLonPoint().getLongitude());
        poiInfo.setTitle(item.getTitle());
        poiInfo.setCityName(item.getCityName());
        poiInfo.setCityCode(item.getCityCode());
        poiInfo.setProvinceName(item.getProvinceName());
        poiInfo.setAdName(item.getAdName());
        poiInfo.setSnippet(item.getSnippet());
        return poiInfo;
    }

    /**
     * 计算两点之间的距离
     * @param latLng1 点1的经纬度
     * @param latLng2 点2的经纬度
     * @return 两点间的直线距离
     */
    public float calculateLineDistance(LatLng latLng1, LatLng latLng2) {
        return AMapUtils.calculateLineDistance(latLng1, latLng2);
    }

    /**
     * 计算两点之间的距离 高德图层里的算法
     * @return 两点间的距离
     */
    public static int calculateDistance(double x1, double y1, double x2, double y2) {
        final double NF_pi = 0.01745329251994329; // 弧度 PI/180
        x1 *= NF_pi;
        y1 *= NF_pi;
        x2 *= NF_pi;
        y2 *= NF_pi;
        double sinx1 = Math.sin(x1);
        double siny1 = Math.sin(y1);
        double cosx1 = Math.cos(x1);
        double cosy1 = Math.cos(y1);
        double sinx2 = Math.sin(x2);
        double siny2 = Math.sin(y2);
        double cosx2 = Math.cos(x2);
        double cosy2 = Math.cos(y2);
        double[] v1 = new double[3];
        v1[0] = cosy1 * cosx1 - cosy2 * cosx2;
        v1[1] = cosy1 * sinx1 - cosy2 * sinx2;
        v1[2] = siny1 - siny2;
        double dist = Math.sqrt(v1[0] * v1[0] + v1[1] * v1[1] + v1[2] * v1[2]);

        return (int) (Math.asin(dist / 2) * 12742001.5798544);
    }
}
