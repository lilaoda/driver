package bus.driver.module.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.AppCompatRatingBar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import bus.driver.R;
import bus.driver.base.BaseFragment;
import bus.driver.data.DbManager;
import bus.driver.data.HttpManager;
import bus.driver.data.local.entity.User;
import bus.driver.data.remote.DriverService;
import bus.driver.module.customerservice.CustomerServiceActivity;
import bus.driver.module.setting.MyInfoActivity;
import bus.driver.module.setting.SettingActivity;
import bus.driver.utils.GlideUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.view.roundImageView.CircleImageView;

import static bus.driver.utils.RxUtils.wrapHttp;

/**
 * Created by Lilaoda on 2017/11/9.
 * Email:749948218@qq.com
 */

public class LeftNavFragment extends BaseFragment {

    @BindView(R.id.civ_icon)
    CircleImageView civIcon;
    @BindView(R.id.rating_bar)
    AppCompatRatingBar ratingBar;
    @BindView(R.id.text_name)
    TextView textName;
    @BindView(R.id.text_car_type)
    TextView textCarType;
    @BindView(R.id.text_car_num)
    TextView textCarNum;
    @BindView(R.id.ll_money)
    LinearLayout llMoney;
    @BindView(R.id.ll_route)
    LinearLayout llRoute;
    @BindView(R.id.ll_setting)
    LinearLayout llSetting;
    @BindView(R.id.fl_my_car)
    FrameLayout flMyCar;
    @BindView(R.id.fl_server_score)
    FrameLayout flServerScore;
    @BindView(R.id.fl_recommend)
    FrameLayout flRecommend;
    @BindView(R.id.fl_server_center)
    FrameLayout flServerCenter;
    @BindView(R.id.fl_assess)
    FrameLayout flAssess;
    @BindView(R.id.btn_login_out)
    Button btnLoginOut;
    @BindView(R.id.main_left)
    LinearLayout mainLeft;

    Unbinder unbinder;
    private HttpManager mHttpManager;
    private DbManager mDbManager;
    private FragmentActivity mActivity;
    private User mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHttpManager = HttpManager.instance();
        mDbManager = DbManager.instance();
    }

    public static LeftNavFragment newInstance() {
        Bundle args = new Bundle();
        LeftNavFragment fragment = new LeftNavFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_left_nav, container, false);
        unbinder = ButterKnife.bind(this, view);
        mActivity = getActivity();
        mUser = mDbManager.getUser();
        initView();
        EventBus.getDefault().register(this);
        return view;
    }

    private void initView() {
        Logger.d(mUser);
        if (mUser == null) {
            textName.setText("请登陆");
        } else {
            GlideUtil.instance().loadUserIcon(civIcon,DriverService.imgBaseUrl+mUser.getIconUrl());
            String phone = mUser.getPhone();
            if (TextUtils.isEmpty(mUser.getName())) {
                textName.setText(phone.substring(phone.length() - 5, phone.length()));
            } else {
                textName.setText(mUser.getName());
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.civ_icon, R.id.ll_money, R.id.ll_route, R.id.ll_setting, R.id.fl_my_car, R.id.fl_server_score, R.id.fl_recommend, R.id.fl_server_center, R.id.fl_assess, R.id.btn_login_out})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.civ_icon:
                gotoActivity(MyInfoActivity.class);
                break;
            case R.id.ll_money:
                break;
            case R.id.ll_route:
                break;
            case R.id.ll_setting:
                gotoActivity(SettingActivity.class);
                break;
            case R.id.fl_my_car:
                break;
            case R.id.fl_server_score:
                break;
            case R.id.fl_recommend:
                break;
            case R.id.fl_server_center:
                gotoActivity(CustomerServiceActivity.class);
                break;
            case R.id.fl_assess:
                break;
            case R.id.btn_login_out:
                loginOut();
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(User user) {
        mUser = user;
        initView();
    }

    private void gotoActivity(Class<? extends Activity> cls) {
        startActivity(new Intent(mActivity, cls));
    }

    private void loginOut() {
        wrapHttp(mHttpManager.getDriverService().loginOut()).compose(this.<String>bindToLifecycle())
                .subscribe(new ResultObserver<String>(mActivity, "正在退出...", true) {
                    @Override
                    public void onSuccess(String value) {
                        mDbManager.clearUserInfo();
                    }
                });
    }
}
