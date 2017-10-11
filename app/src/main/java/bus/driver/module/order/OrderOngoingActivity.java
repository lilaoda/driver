package bus.driver.module.order;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.Poi;
import com.amap.api.navi.AmapNaviType;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.module.AMapFragment;
import bus.driver.service.LocationService;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.utils.ActivityUtils;

/**
 * Created by Lilaoda on 2017/9/29.
 * Email:749948218@qq.com
 */

public class OrderOngoingActivity extends BaseActivity {

    private AMapFragment mAMapFragment;
    LatLng mTargetLatLng = new LatLng(23.220226, 113.306295);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing);
        ButterKnife.bind(this);
        mAMapFragment = AMapFragment.newInstance();
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), mAMapFragment,R.id.fl_content);
    }

    @OnClick({R.id.btn_navi, R.id.btn_call, R.id.ib_reLocate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ib_reLocate:
                mAMapFragment.refreshLocation();
                break;
            case R.id.btn_navi:
                mAMapFragment.startNavi(new Poi("我的位置", new LatLng(LocationService.latitude, LocationService.longitude), null), new Poi("终点", mTargetLatLng, null), AmapNaviType.DRIVER);
                break;
            case R.id.btn_call:
                break;
        }
    }
}
