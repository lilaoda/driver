package bus.driver.module.main;

import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.LruCache;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.FrameLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.base.BaseApplication;
import bus.driver.base.BaseFragment;
import bus.driver.module.order.OrderListFragment;
import bus.driver.service.DriverService;
import butterknife.BindView;
import butterknife.ButterKnife;
import lhy.lhylibrary.utils.ActivityUtils;
import lhy.lhylibrary.utils.StatusBarUtil;
import lhy.lhylibrary.utils.ToastUtils;
import lhy.lhylibrary.view.tablayout.SlidingTabLayout;


public class MainActivity extends BaseActivity {

    private String[] mTitles = {"首页", "订单"};

    @BindView(R.id.fl_left)
    FrameLayout flLeft;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.tabLayout)
    SlidingTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;

    private ActionBarDrawerToggle mDrawerToggle;
    private List<BaseFragment> mFragments;
    private MainFragment mHomeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        DriverService.start(this);
        initToolbar();
        initView();
        long l = Runtime.getRuntime().maxMemory();
        Log.e("meo", "onCreate: "+l );
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        LruCache<String, Bitmap> lruCache = new LruCache<String,Bitmap>(1024*1024){
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
    }


    public void setCurrentTab(int position) {
        if (position < 0 || position > viewPager.getAdapter().getCount() - 1) {
            return;
        }
        viewPager.setCurrentItem(position);
    }

    private void initView() {
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), LeftNavFragment.newInstance(), R.id.fl_left);
        mFragments = new ArrayList<>();
        mHomeFragment = MainFragment.newInstance();
        mFragments.add(mHomeFragment);
        mFragments.add(OrderListFragment.newInstance());
        viewPager.setAdapter(new ManiAdapter(getSupportFragmentManager()));
        tabLayout.setViewPager(viewPager);
    }

    private void initToolbar() {
        toolbar.setTitle("");
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(mDrawerToggle);
        setSupportActionBar(toolbar);
        mDrawerToggle.syncState();

        //设置测滑全屏
//        DisplayMetrics metric = new DisplayMetrics();
//        getWindowManager().getDefaultDisplay().getMetrics(metric);
//        View leftMenu = findViewById(R.id.main_left);
//        ViewGroup.LayoutParams leftParams = leftMenu.getLayoutParams();
//        leftParams.width = metric.widthPixels;
//        leftMenu.setLayoutParams(leftParams);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (mDrawerToggle != null) {
            mDrawerToggle.onConfigurationChanged(newConfig);
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        if (mDrawerToggle != null) {
            mDrawerToggle.syncState();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        //   toolbar.inflateMenu(R.menu.main);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle != null && mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // If not handled by drawerToggle, home needs to be handled by returning to previous
        if (item != null && item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        if (item != null && item.getItemId() == R.id.action_settings) {
            ToastUtils.showString("setting");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        doExit();
    }

    private long exitTime = 0;

    private void doExit() {
        if (System.currentTimeMillis() - exitTime > 2000) {
            ToastUtils.showString("再按一次退出程序");
            exitTime = System.currentTimeMillis();
        } else {
            BaseApplication.getInstance().closeApplication();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void setStatusBar() {
        DrawerLayout drawlayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        StatusBarUtil.setColorForDrawerLayout(this, drawlayout, getResources().getColor(R.color.app_color), 0);
    }

    private class ManiAdapter extends FragmentPagerAdapter {

        public ManiAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mTitles[position];
        }


        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

}
