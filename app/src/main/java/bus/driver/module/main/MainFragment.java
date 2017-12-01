package bus.driver.module.main;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.concurrent.TimeUnit;

import bus.driver.R;
import bus.driver.base.BaseFragment;
import bus.driver.base.Constants;
import bus.driver.bean.event.LocationEvent;
import bus.driver.bean.event.NaviStatus;
import bus.driver.bean.event.OrderEvent;
import bus.driver.data.HttpManager;
import bus.driver.data.SpManager;
import bus.driver.utils.EventBusUtls;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.DateUtils;

import static bus.driver.utils.RxUtils.wrapHttp;

/**
 * Created by Lilaoda on 2017/9/27.
 * Email:749948218@qq.com
 */

public class MainFragment extends BaseFragment {

    public static final int STATUS_REST = 1;//休息状态
    public static final int STATUS_WORK = 2;//工作状态

    @BindView(R.id.text_time)
    TextView textTime;
    @BindView(R.id.text_order_count)
    TextView textOrderCount;
    @BindView(R.id.text_money)
    TextView textMoney;
    @BindView(R.id.btn_style)
    Button btnStyle;
    @BindView(R.id.img_indicate)
    ImageView imgIndicate;
    @BindView(R.id.btn_go_car)
    Button btnGoCar;
    Unbinder unbinder;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    private int mCurrentStatus = STATUS_REST;
    private RotateAnimation mRotateAnim;
    private AnimatorSet mSetAnimStart;
    private AnimatorSet mSetAnimPause;
    private Disposable mTimeDisable;
    private SpManager mSpManager;

    public static MainFragment newInstance() {
        Bundle args = new Bundle();
        MainFragment fragment = new MainFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        unbinder = ButterKnife.bind(this, view);
        mSpManager = SpManager.instance();
        initView();
        checkIsWorking();
        return view;
    }

    private void checkIsWorking() {
        boolean isCarWork = mSpManager.getIsCarWork();
        if (isCarWork) {
            mCurrentStatus = STATUS_WORK;
            //防止服务没开启就发送事件，延迟500MS
            Observable.timer(500, TimeUnit.MILLISECONDS).compose(this.<Long>bindToLifecycle())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Consumer<Long>() {
                        @Override
                        public void accept(@NonNull Long aLong) throws Exception {
                            reSetView();
                        }
                    });
        }
    }

    private void initView() {
        mTimeDisable = Flowable.interval(1, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindToLifecycle())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        textTime.setText(DateUtils.getDate("MM月dd HH:mm:ss") + " " + DateUtils.getWeek());
                    }
                });
        imgIndicate.setVisibility(View.INVISIBLE);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshView();
            }
        });
    }

    //TODO test
    private void refreshView() {
        Observable.timer(2, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .compose(this.<Long>bindToLifecycle())
                .subscribe(new Consumer<Long>() {
                    @Override
                    public void accept(@NonNull Long aLong) throws Exception {
                        refreshLayout.setRefreshing(false);
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        if (mTimeDisable != null && !mTimeDisable.isDisposed()) {
            mTimeDisable.dispose();
            mTimeDisable = null;
        }
        if (mRotateAnim != null) {
            mRotateAnim.cancel();
            mRotateAnim = null;
        }
    }

    @OnClick({R.id.btn_style, R.id.btn_go_car})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_style:
                break;
            case R.id.btn_go_car:
                updateWorkStatus(mCurrentStatus == STATUS_REST ? STATUS_WORK : STATUS_REST);
                break;
        }
    }

    /**
     * 更改司机出车状态
     *
     * @param workStatus 1,下班 2，上班
     */
    private void updateWorkStatus(final int workStatus) {
        wrapHttp(HttpManager.instance().getDriverService().updaeWork(workStatus))
                .compose(this.<String>bindToLifecycle())
                .subscribe(new ResultObserver<String>(getActivity(), "正在加载...", true) {
                    @Override
                    public void onSuccess(String value) {
                        mCurrentStatus = workStatus;
                        mSpManager.putIsCarWork(mCurrentStatus == STATUS_WORK);
                        reSetView();
                    }
                });
    }


    private void reSetView() {
        if (mCurrentStatus == STATUS_WORK) {
            Constants.DRIVER_STATSU = Constants.DRIVER_STATSU_WORK;
            imgIndicate.setVisibility(View.VISIBLE);
            btnGoCar.setText("收车");
            setOrderServiceEnable(true);
            startAnim();
        } else {
            Constants.DRIVER_STATSU = Constants.DRIVER_STATSU_REST;
            imgIndicate.setVisibility(View.GONE);
            setOrderServiceEnable(false);
            btnGoCar.setText("出车");
            pauseAnim();
        }
    }

    private void setOrderServiceEnable(boolean b) {
        EventBusUtls.notifyPullOrder(b ? OrderEvent.ORDER_PULL_ENABLE : OrderEvent.ORDER_PULL_UNABLE);
        EventBusUtls.notifyLocation(b ? LocationEvent.LOCATION_ENABLE : LocationEvent.LOCATION_UNABLE);
    }
    //TODO 需增加接口，是否有正在进行中的订单 判断是否取消循环拉取订单等

    private void startAnim() {
        if (mRotateAnim == null) {
            mRotateAnim = new RotateAnimation(0, 360, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
            mRotateAnim.setRepeatMode(Animation.RESTART);
            mRotateAnim.setRepeatCount(Animation.INFINITE);
            mRotateAnim.setInterpolator(new LinearInterpolator());
            mRotateAnim.setDuration(2000);
            mRotateAnim.setFillBefore(true);
            imgIndicate.setAnimation(mRotateAnim);
        }
        mRotateAnim.start();
        if (mSetAnimStart == null) {
            ObjectAnimator translationX = ObjectAnimator.ofFloat(btnGoCar, "translationX", 0, CommonUtils.dp2px(getContext(), 120));
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(btnGoCar, "scaleX", 1, 0.7f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(btnGoCar, "scaleY", 1, 0.7f);
            translationX.setDuration(1000);
            scaleX.setDuration(1000);
            scaleY.setDuration(1000);
            mSetAnimStart = new AnimatorSet();
            mSetAnimStart.playTogether(translationX, scaleX, scaleY);
        }
        mSetAnimStart.start();
    }

    private void pauseAnim() {
        if (mSetAnimPause == null) {
            ObjectAnimator translationX = ObjectAnimator.ofFloat(btnGoCar, "translationX", CommonUtils.dp2px(getContext(), 120), 0);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(btnGoCar, "scaleX", 0.7f, 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(btnGoCar, "scaleY", 0.7f, 1);
            translationX.setDuration(1000);
            scaleX.setDuration(1000);
            scaleY.setDuration(1000);
            mSetAnimPause = new AnimatorSet();
            mSetAnimPause.playTogether(translationX, scaleX, scaleY);
        }
        mSetAnimPause.start();

        if (mRotateAnim != null) {
            mRotateAnim.cancel();
        }
    }

    public int getCurrentCarStatus() {
        return mCurrentStatus;
    }

    /**
     * 如果司机正在导航，不能进行收车
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(NaviStatus event) {
        if (event.isNaving()) {
            btnGoCar.setEnabled(false);
        } else {
            btnGoCar.setEnabled(true);
        }
    }

    //    private void getCityList() {
//        wrapHttp(HttpManager.instance().getDriverService().getAreaList("city"))
//                .compose(this.<List<City>>bindToLifecycle())
//                .subscribe(new ResultObserver<List<City>>(getActivity(),"正在加载...",true) {
//                    @Override
//                    public void onSuccess(List<City> value) {
//                            if (FileUtils.isSDCardEnabled()) {
//                                File file1 = new File(DIR_CRASH);
//                                if(!file1.exists())file1.mkdirs();
//                                File file = new File(DIR_CRASH, DateUtils.getCurrentTime());
//                                try {
//                                    FileWriter fileWriter = new FileWriter(file);
//                                    fileWriter.write(new Gson().toJson(value));
//                                    fileWriter.flush();
//                                    fileWriter.close();
//                                } catch (IOException e) {
//                                    e.printStackTrace();
//                                }
//                            }
//
//                    }
//                });
//    }
}
