package bus.driver.module.order;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import bus.driver.R;
import bus.driver.adapter.OrderListAdapter;
import bus.driver.base.BaseFragment;
import bus.driver.base.Constants;
import bus.driver.bean.OrderInfo;
import bus.driver.bean.PageParam;
import bus.driver.data.HttpManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.ToastUtils;

import static bus.driver.utils.RxUtils.wrapHttp;

/**
 * Created by Lilaoda on 2017/9/27.
 * Email:749948218@qq.com
 */

public class OrderListFragment extends BaseFragment implements BaseQuickAdapter.OnItemClickListener,
        BaseQuickAdapter.RequestLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;

    Unbinder unbinder;
    private HttpManager mHttpManager;
    private OrderListAdapter mOrderAdapter;
    private PageParam mPageParam;
    private int mCurrentPage =1;

    public static OrderListFragment newInstance() {
        Bundle args = new Bundle();
        OrderListFragment fragment = new OrderListFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_order, null);
        EventBus.getDefault().register(this);
        unbinder = ButterKnife.bind(this, view);
        mHttpManager = HttpManager.instance();
        initView();
        getOrderList();
        return view;
    }

    private void getOrderList() {
        mPageParam = new PageParam();
        mPageParam.setPageNo(1);
        mPageParam.setPageSize(10);
        wrapHttp(mHttpManager.getDriverService().getOrderList(mPageParam)).
                compose(this.<List<OrderInfo>>bindToLifecycle())
                .subscribe(new ResultObserver<List<OrderInfo>>() {
                    @Override
                    public void onSuccess(List<OrderInfo> value) {
                        refreshAdapter(value);
                    }
                });
    }

    private void refreshAdapter(List<OrderInfo> value) {
        sort(value);
        mOrderAdapter.setNewData(value);
        mOrderAdapter.disableLoadMoreIfNotFullPage(recyclerView);
    }

    private void sort(List<OrderInfo> list) {
        Collections.sort(list, new Comparator<OrderInfo>() {
            @Override
            public int compare(OrderInfo o1, OrderInfo o2) {
                return o1.getSubStatus() - o2.getSubStatus();
            }
        });
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mOrderAdapter = new OrderListAdapter(null);
        mOrderAdapter.setEmptyView(R.layout.empty_order, (ViewGroup) recyclerView.getParent());
        recyclerView.setAdapter(mOrderAdapter);
        mOrderAdapter.setOnItemClickListener(this);
        mOrderAdapter.setOnLoadMoreListener(this, recyclerView);
        refreshLayout.setOnRefreshListener(this);
        //这句要想有效果必须放在监听器之后 要想不满屏时不能上拉加载，需要放在监听器之后 然后每次刷新数据都要再调用
        mOrderAdapter.disableLoadMoreIfNotFullPage(recyclerView);
    }

    private void refresh(final boolean isLoadMore) {
        if (isLoadMore){
            mPageParam.setPageNo(mCurrentPage+1);
        }else {
            mPageParam.setPageNo(1);
        }
        wrapHttp(mHttpManager.getDriverService().getOrderList(mPageParam))
                .compose(this.<List<OrderInfo>>bindToLifecycle())
                .subscribe(new ResultObserver<List<OrderInfo>>(true) {
                    @Override
                    public void onSuccess(List<OrderInfo> value) {
                        if (isLoadMore) {
                            if (value == null || value.size() == 0) {
                                mOrderAdapter.loadMoreEnd();
                            } else {
                                mCurrentPage++;
                                mOrderAdapter.addData(value);
                                mOrderAdapter.loadMoreComplete();
                            }
                        } else {
                            refreshLayout.setRefreshing(false);
                            refreshAdapter(value);
                        }
                    }

                    @Override
                    public void onFailure(String e) {
                        super.onFailure(e);
                        if (isLoadMore) {
                            mOrderAdapter.loadMoreFail();
                        } else {
                            refreshLayout.setRefreshing(false);
                            if (TextUtils.equals("暂时没有订单数据", e)) {
                                refreshAdapter(new ArrayList<OrderInfo>());
                            }
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
          refresh(true);
    }

    @Override
    public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
        OrderInfo orderInfo = mOrderAdapter.getData().get(position);
        if (orderInfo.getSubStatus() == 301) {
            //到达目的地未确认费用
            toActivity(orderInfo, ConfirmExpensesActivity.class);
        } else if (orderInfo.getSubStatus() == 400) {
            //确认费用了未支付
            ToastUtils.showString("去支付");
        } else {
            toActivity(orderInfo, OrderOngoingActivity.class);
        }
    }

    private void toActivity(OrderInfo orderInfo, Class cls) {
        Intent intent = new Intent(getActivity(), cls);
        intent.putExtra(Constants.ORDER_INFO, orderInfo);
        startActivity(intent);
    }

    /**
     * 订单信息有改变 在抢单，行程中的订单，确认费用页面对订单状态就行了更改，此时回到这个页面，需要刷新数据
     *
     * @param orderInfo 更改的订单信息,备用
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEventMessage(OrderInfo orderInfo) {
        getOrderList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        unbinder.unbind();
    }
}
