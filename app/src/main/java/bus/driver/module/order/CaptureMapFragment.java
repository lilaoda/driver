package bus.driver.module.order;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.amap.api.maps.model.LatLng;

import bus.driver.base.GlobeConstants;
import bus.driver.bean.OrderInfo;
import bus.driver.module.AMapFragment;

/**
 * Created by Lilaoda on 2017/10/11.
 * Email:749948218@qq.com
 */

public class CaptureMapFragment extends AMapFragment {

    private OrderInfo mOrderInfo;

    public static CaptureMapFragment newInstance(OrderInfo orderInfo) {
        Bundle args = new Bundle();
        args.putParcelable(GlobeConstants.ORDER_INFO, orderInfo);
        CaptureMapFragment fragment = new CaptureMapFragment();
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
        routeCaculate(new LatLng(mOrderInfo.getOriginLat(), mOrderInfo.getOriginLng()), new LatLng(mOrderInfo.getDestLat(), mOrderInfo.getDestLng()), null);
    }
}
