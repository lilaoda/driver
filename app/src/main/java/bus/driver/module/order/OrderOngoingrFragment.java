package bus.driver.module.order;

import android.location.Location;
import android.os.Bundle;
import android.support.annotation.Nullable;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.LatLng;
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
    public void onResume() {
        super.onResume();
        initLocation(this);
        Logger.d(mOrderInfo);
        addStartEndMark(new LatLng(mOrderInfo.getOriginLat(), mOrderInfo.getOriginLng()), mOrderInfo.getOriginAddress()
                , new LatLng(mOrderInfo.getDestLat(), mOrderInfo.getDestLng()), mOrderInfo.getDestAddress());
    //    routeCaculate(new LatLng(mOrderInfo.getOriginLat(), mOrderInfo.getOriginLng()), new LatLng(mOrderInfo.getDestLat(), mOrderInfo.getDestLng()), null);
    }

    @Override
    public void onMyLocationChange(Location location) {
        if (mLocaitonChangeListener != null) {
            mLocaitonChangeListener.locationChange(location);
        }
    }

    private LocaitonChageListener mLocaitonChangeListener;

    public interface LocaitonChageListener {
        void locationChange(Location location);
    }
    public void setLocationChangeListener(LocaitonChageListener listener){
        this.mLocaitonChangeListener = listener;
    }
}
