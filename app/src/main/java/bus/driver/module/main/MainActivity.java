package bus.driver.module.main;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.base.BaseFragment;
import bus.driver.data.DbManager;
import bus.driver.data.entity.User;
import bus.driver.module.customerservice.CustomerServiceActivity;
import bus.driver.module.route.RouteActivity;
import bus.driver.module.setting.SettingActivity;
import bus.driver.service.LocationService;
import bus.driver.service.OrderService;
import butterknife.BindView;
import butterknife.ButterKnife;
import lhy.lhylibrary.utils.StatusBarUtil;
import lhy.lhylibrary.utils.ToastUtils;
import lhy.lhylibrary.view.tablayout.SlidingTabLayout;


public class MainActivity extends BaseActivity implements NavigationView.OnNavigationItemSelectedListener {

    private String[] mTitles = {"首页", "订单"};

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.nav_view)
    NavigationView navView;
    @BindView(R.id.drawer_layout)
    DrawerLayout drawerLayout;
    @BindView(R.id.tabLayout)
    SlidingTabLayout tabLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;


    private ActionBarDrawerToggle mDrawerToggle;
    private List<BaseFragment> mFragments;
    private HomeFragment mHomeFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        startDriverService();
        initToolbar();
        initView();
    }

    private void startDriverService() {
        startService(new Intent(this, LocationService.class).putExtra(LocationService.FIRST_LOCATE, true));
        startService(new Intent(this, OrderService.class));
    }

    private void initView() {
        mFragments = new ArrayList<>();
        mHomeFragment = HomeFragment.newInstance();
        mFragments.add(mHomeFragment);
        mFragments.add(OrderFragment.newInstance());
        viewPager.setAdapter(new ManiAdapter(getSupportFragmentManager()));
        tabLayout.setViewPager(viewPager);
    }

    private void initToolbar() {
        initNavHeadView();
        toolbar.setTitle("");
        mDrawerToggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(mDrawerToggle);
        setSupportActionBar(toolbar);
        mDrawerToggle.syncState();
        navView.setNavigationItemSelectedListener(this);
    }

    private void initNavHeadView() {
        User mUser = DbManager.instance().getUser();
        View headerView = navView.getHeaderView(0);
        ImageView userIcon = (ImageView) headerView.findViewById(R.id.img_photo);
        TextView userName = (TextView) headerView.findViewById(R.id.text_name);
        if (mUser == null) {
            userName.setText("请登陆");
        } else {
            Glide.with(this).load(mUser.getIconUrl()).into(userIcon);
            userName.setText(mUser.getPhone());
        }
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

        if (item != null && item.getItemId() == R.id.action_settings) {
            ToastUtils.showString("setting");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_route) {
            gotoActivity(RouteActivity.class);
        } else if (id == R.id.nav_service) {
            gotoActivity(CustomerServiceActivity.class);
        } else if (id == R.id.nav_setting) {
            gotoActivity(SettingActivity.class);
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void gotoActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(this, cls));
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
