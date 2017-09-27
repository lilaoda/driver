package bus.driver.module;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.animation.LinearInterpolator;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptorFactory;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Marker;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.Poi;
import com.amap.api.maps.model.animation.Animation;
import com.amap.api.maps.model.animation.ScaleAnimation;
import com.amap.api.navi.AmapNaviPage;
import com.amap.api.navi.AmapNaviParams;
import com.amap.api.navi.AmapNaviType;

import lhy.lhylibrary.base.LhyFragment;

/**
 * Created by Liheyu on 2017/9/12.
 * Email:liheyu999@163.com
 */

public class AMapFragment extends LhyFragment implements  AMap.OnPOIClickListener, AMap.OnMarkerClickListener {

    private Marker mGrowMarker;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

    protected void startNavi(Poi start, Poi end, AmapNaviType naviType){
        AmapNaviParams amapNaviParams = new AmapNaviParams(start, null, end, naviType);
        AmapNaviPage.getInstance().showRouteActivity(getContext(), amapNaviParams, null);
    }
}
