package bus.driver.module.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;

import bus.driver.R;
import bus.driver.base.BaseFragment;
import bus.driver.base.Constants;
import bus.driver.bean.OrderInfo;
import bus.driver.bean.event.OrderEvent;
import bus.driver.data.HttpManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.view.roundImageView.CircleImageView;

import static bus.driver.utils.RxUtils.wrapHttp;

/**
 * Created by Lilaoda on 2017/11/8.
 * Email:749948218@qq.com
 */

public class CaptureInfoFragment extends BaseFragment {

    @BindView(R.id.civ_icon)
    CircleImageView civIcon;
    @BindView(R.id.text_target)
    TextView textTarget;
    @BindView(R.id.text_order_type)
    TextView textOrderType;
    @BindView(R.id.text_distance)
    TextView textDistance;
    @BindView(R.id.text_start)
    TextView textStart;
    @BindView(R.id.text_detail_start)
    TextView textDetailStart;
    @BindView(R.id.text_dest)
    TextView textDest;
    @BindView(R.id.text_detail_dest)
    TextView textDetailDest;
    @BindView(R.id.text_estimate_distance)
    TextView textEstimateDistance;
    @BindView(R.id.text_estimate_price)
    TextView textEstimatePrice;
    @BindView(R.id.btn_close)
    Button btnClose;
    @BindView(R.id.btn_receive)
    Button btnReceive;
    Unbinder unbinder;
    private OrderInfo mOrderInfo;

    public static CaptureInfoFragment newInstance(OrderInfo orderInfo) {
        Bundle args = new Bundle();
        args.putParcelable(Constants.ORDER_INFO, orderInfo);
        CaptureInfoFragment fragment = new CaptureInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        mOrderInfo = arguments.getParcelable(Constants.ORDER_INFO);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_capture_info, container, false);
        unbinder = ButterKnife.bind(this, view);
        initView();
        return view;
    }

    private void initView() {
        textStart.setText(mOrderInfo.getOriginAddress());
        textDest.setText(mOrderInfo.getDestAddress());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.btn_close, R.id.btn_receive})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_close:
                getActivity().finish();
                break;
            case R.id.btn_receive:
                captureOrder();
                break;
        }
    }

    private void closeDrawLayout(){

    }

    private void captureOrder() {
        wrapHttp(HttpManager.instance().getDriverService().getOrder(mOrderInfo.getOrderUuid()))
                .compose(this.<String>bindToLifecycle())
                .subscribe(new ResultObserver<String>(getActivity(), "正在抢单...", true) {
                    @Override
                    public void onSuccess(String value) {
                        Constants.ORDER_STATSU = Constants.ORDER_STATSU_ONDOING;
                        Intent intent = new Intent(getContext(), OrderOngoingActivity.class);
                        intent.putExtra(Constants.ORDER_INFO, mOrderInfo);
                        startActivity(intent);
                        EventBus.getDefault().post(mOrderInfo);
                        EventBus.getDefault().post(OrderEvent.ORDER_GET_CANCEL_ENABLE);
                        getActivity().finish();
                    }
                });
    }

}
