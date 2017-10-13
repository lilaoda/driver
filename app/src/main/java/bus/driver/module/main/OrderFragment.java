package bus.driver.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import bus.driver.R;
import bus.driver.adapter.OrderListAdapter;
import bus.driver.base.BaseFragment;
import bus.driver.base.GlobeConstants;
import bus.driver.bean.OrderInfo;
import bus.driver.data.HttpManager;
import bus.driver.module.order.OrderOngoingActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lhy.lhylibrary.http.ResultObserver;

import static bus.driver.utils.RxUtils.wrapHttp;

/**
 * Created by Lilaoda on 2017/9/27.
 * Email:749948218@qq.com
 */

public class OrderFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    Unbinder unbinder;
    private HttpManager mHttpManager;
    private OrderListAdapter mOrderAdapter;

    public static OrderFragment newInstance() {
        Bundle args = new Bundle();
        OrderFragment fragment = new OrderFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, null);
        unbinder = ButterKnife.bind(this, view);
        mHttpManager = HttpManager.instance();
        initView();
        getOrderList();
        return view;
    }

    private void getOrderList() {
        wrapHttp(mHttpManager.getDriverService().getOrderList()).
        compose(this.<List<OrderInfo>>bindToLifecycle())
                .subscribe(new ResultObserver<List<OrderInfo>>() {
                    @Override
                    public void onSuccess(List<OrderInfo> value) {
                        refreshAdapter(value);
                    }
                });
    }

    private void refreshAdapter(List<OrderInfo> value) {
        mOrderAdapter.setNewData(value);
      //  mOrderAdapter.disableLoadMoreIfNotFullPage(recyclerView);
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mOrderAdapter = new OrderListAdapter(null);
        mOrderAdapter.setEmptyView(R.layout.empty_order, (ViewGroup) recyclerView.getParent());
        recyclerView.setAdapter(mOrderAdapter);
        mOrderAdapter.setOnItemClickListener(this);
      //  mOrderAdapter.setOnLoadMoreListener(this, recyclerView);
        refreshLayout.setOnRefreshListener(this);
        //这句要想有效果必须放在监听器之后 要想不满屏时不能上拉加载，需要放在监听器之后 然后每次刷新数据都要再调用
      //  mOrderAdapter.disableLoadMoreIfNotFullPage(recyclerView);
    }

    private void refresh(final boolean isLoadMore) {
        wrapHttp(mHttpManager.getDriverService().getOrderList())
                .compose(this.<List<OrderInfo>>bindToLifecycle())
                .subscribe(new ResultObserver<List<OrderInfo>>(true) {
                    @Override
                    public void onSuccess(List<OrderInfo> value) {
                        if (isLoadMore) {
                            if (value == null || value.size() == 0) {
                                mOrderAdapter.loadMoreEnd();
                            } else {
                                mOrderAdapter.addData(value);
                                mOrderAdapter.loadMoreComplete();
                            }
                        } else {
                            refreshLayout.setRefreshing(false);
                            refreshAdapter(value);
                        }
                    }

                    @Override
                    public void onFailure(Throwable e) {
                        super.onFailure(e);
                        if (isLoadMore) {
                            mOrderAdapter.loadMoreFail();
                        } else {
                            refreshLayout.setRefreshing(false);
                        }
                    }
                });
    }

    @Override
    public void onRefresh() {
        refresh(false);
    }


    @Override
    public void onLoadMoreRequested() {
      //  refresh(true);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        OrderInfo orderInfo = mOrderAdapter.getData().get(position);
        Intent intent = new Intent(getActivity(), OrderOngoingActivity.class);
        intent.putExtra(GlobeConstants.ORDER_INFO,orderInfo);
        startActivity(intent);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
