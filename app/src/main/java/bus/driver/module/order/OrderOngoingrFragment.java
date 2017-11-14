package bus.driver.module.order;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Polyline;
import com.orhanobut.logger.Logger;

import bus.driver.base.GlobeConstants;
import bus.driver.bean.OrderInfo;
import bus.driver.module.AMapFragment;

/**
 * Created by Lilaoda on 2017/10/11.
 * Email:749948218@qq.com
 */

public class OrderOngoingrFragment extends AMapFragment implements AMap.OnMyLocationChangeListener {

    private OrderInfo mOrderInfo;
    private Polyline polyline;

    public static OrderOngoingrFragment newInstance(OrderInfo orderInfo) {
        Bundle args = new Bundle();
        args.putParcelable(GlobeConstants.ORDER_INFO, orderInfo);
        OrderOngoingrFragment fragment = new OrderOngoingrFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mOrderInfo = arguments.getParcelable(GlobeConstants.ORDER_INFO);
    }

    @Override
    protected void onMapCreated() {
        super.onMapCreated();
        Logger.d(mOrderInfo);
        initLocation(this);
        addStartEndMark(new LatLng(mOrderInfo.getOriginLat(), mOrderInfo.getOriginLng()), mOrderInfo.getOriginAddress()
                , new LatLng(mOrderInfo.getDestLat(), mOrderInfo.getDestLng()), mOrderInfo.getDestAddress());
//            routeCaculate(new LatLng(mOrderInfo.getOriginLat(), mOrderInfo.getOriginLng()), new LatLng(mOrderInfo.getDestLat(), mOrderInfo.getDestLng()), null);
    }

//    private LatLng startLatlng = null;
//    private List<LatLng> mMoveList = new ArrayList<>();
//    double a = 0.001;
//    double startLat = 23.119246;
//    double startLng = 113.264945;

    @Override
    public void onMyLocationChange(Location location) {
        if (mLocaitonChangeListener != null) {
            mLocaitonChangeListener.locationChange(location);
        }


//        // 113.264945, 23.119246,   113.294603 23.069417
//        Logger.d("画线");
//        if (polyline == null) {
//            mMoveList.add(new LatLng(23.069417, 113.294603));
//            mMoveList.add(new LatLng(23.119246, 113.264945));
//            mMoveList.add(new LatLng(location.getLatitude(), location.getLongitude()));
//            PolylineOptions polylineOptions = new PolylineOptions();
//            polylineOptions.setPoints(mMoveList);
//            polylineOptions.setCustomTexture(BitmapDescriptorFactory.fromResource(R.mipmap.custtexture));
//            polylineOptions.width(18);
//            polylineOptions.useGradient(true);
//            polyline = mAMap.addPolyline(polylineOptions);
//        } else {
//            startLat += a;
//            startLng += a;
//            mMoveList.add(new LatLng(startLat, startLng));
//            polyline.setPoints(mMoveList);//连续画线
//            if (!polyline.getOptions().isVisible()) {
//                polyline.getOptions().visible(true);
//            }
//            if (!polyline.isVisible()) {
//                polyline.setVisible(true);
//            }
//        }
    }

    private LocaitonChageListener mLocaitonChangeListener;

    public interface LocaitonChageListener {
        void locationChange(Location location);
    }

    public void setLocationChangeListener(LocaitonChageListener listener) {
        this.mLocaitonChangeListener = listener;
    }
}
