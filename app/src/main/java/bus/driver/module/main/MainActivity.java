package bus.driver.module.main;

import android.app.Activity;
import android.content.DialogInterface;
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
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.base.BaseFragment;
import bus.driver.bean.OrderInfo;
import bus.driver.data.DbManager;
import bus.driver.data.HttpManager;
import bus.driver.data.entity.User;
import bus.driver.module.customerservice.CustomerServiceActivity;
import bus.driver.module.route.RouteActivity;
import bus.driver.module.setting.SettingActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.StatusBarUtil;
import lhy.lhylibrary.utils.ToastUtils;
import lhy.lhylibrary.view.tablayout.SlidingTabLayout;

import static bus.driver.utils.RxUtils.wrapHttp;


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
    private AlertDialog mOrderDialog;
    private User mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initToolbar();
        initView();
    }

    private void initView() {
        mFragments = new ArrayList<>();
        mFragments.add(HomeFragment.newInstance());
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
        mUser = DbManager.instance().getUser();
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
        EventBus.getDefault().unregister(this);
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

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(OrderInfo event) {
        if (event == null) return;
        showOrderDialog(event);
    }

    private void showOrderDialog(final OrderInfo orderInfo) {
        if (mOrderDialog == null) {
            mOrderDialog = new AlertDialog.Builder(this)
                    .setTitle("您有新的订单")
                    .setMessage("乘客位置：" + orderInfo.getStartLocation() + "\n目的地：" + orderInfo.getEndLocation())
                    .setNegativeButton("立即抢单", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                           captureOrder(orderInfo);
                        }
                    }).setNegativeButton("残忍拒绝", null).create();
        }
        mOrderDialog.show();
    }

    private void captureOrder(OrderInfo orderInfo) {
        wrapHttp(HttpManager.instance().getDriverService().getOrder(mUser.getToken(),orderInfo.getOrderId()))
        .compose(this.<String>bindToLifecycle())
        .subscribe(new ResultObserver<String>(this,"正在抢单...",true) {
            @Override
            public void onSuccess(String value) {
                //TODO 接单成功后跳到订单详情页面
            }
        });
    }

}
