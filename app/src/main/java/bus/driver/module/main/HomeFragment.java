package bus.driver.module.main;

import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import bus.driver.R;
import bus.driver.base.BaseFragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by Lilaoda on 2017/9/27.
 * Email:749948218@qq.com
 */

public class HomeFragment extends BaseFragment {

    public static final int STATUS_NORMAL = 1;//正常
    public static final int STATUS_GO_CAR = 2;//出车

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

    private int mCurrentStatus = 1;

    public static HomeFragment newInstance() {
        Bundle args = new Bundle();
        HomeFragment fragment = new HomeFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, null);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        imgIndicate.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_style, R.id.btn_go_car})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_style:
                break;
            case R.id.btn_go_car:
                reSetView();
                break;
        }
    }

    private void reSetView() {
        if (mCurrentStatus == STATUS_NORMAL) {
            mCurrentStatus = STATUS_GO_CAR;
            btnGoCar.setText("点击收车");
            startAnim();
        } else {
            mCurrentStatus = STATUS_NORMAL;
            imgIndicate.setVisibility(View.GONE);
            btnGoCar.setText("点击出车");
        }
    }

    private void startAnim() {
        imgIndicate.setVisibility(View.VISIBLE);
        ObjectAnimator translationX = ObjectAnimator.ofFloat(imgIndicate, "rotation", 0,360F).setDuration(2000);
        translationX.setRepeatCount(Integer.MAX_VALUE);
        translationX.setRepeatMode(ObjectAnimator.RESTART);
        translationX.start();
    }
}
