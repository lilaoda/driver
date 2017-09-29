package bus.driver.module.splash;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.data.SpManager;
import bus.driver.module.login.LoginActivity;
import bus.driver.service.LocationService;
import bus.driver.service.OrderService;
import butterknife.BindView;
import butterknife.ButterKnife;
import lhy.lhylibrary.utils.StatusBarUtil;


public class SplashActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.fl_root)
    FrameLayout flRoot;
    SpManager mSpManager;

    private ArrayList<View> list;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //防止用户安装应用时点击打开而不是完成，按home键时导致应用重启的问题
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        mSpManager = SpManager.instance();
        startService(new Intent(this, LocationService.class).putExtra(LocationService.FIRST_LOCATE, true));
        startService(new Intent(this, OrderService.class));
        boolean isStarted = mSpManager.getBoolean(SpManager.IS_STARTED);
        if (!isStarted) {
            initView();
        } else {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }


    private void initView() {
        View startView = LayoutInflater.from(this).inflate(R.layout.indicator_start, null);
        Button bt_start = (Button) startView.findViewById(R.id.btn_start);
        bt_start.setOnClickListener(this);
        ImageView view1 = new ImageView(this);
        view1.setBackgroundResource(R.mipmap.login_bg);

        list = new ArrayList<>();
        //  list.add(view1);
        list.add(startView);
        viewPager.setAdapter(new SplashAdapter());
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btn_start) {
            mSpManager.putBoolean(SpManager.IS_STARTED, true);
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }
    }

    private class SplashAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View view = list.get(position);
            container.addView(view);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }

    @Override
    public void setStatusBar() {
        StatusBarUtil.setTransparentForImageView(this, null);
    }
}
