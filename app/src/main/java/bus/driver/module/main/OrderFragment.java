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

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import bus.driver.R;
import bus.driver.adapter.OrderListAdapter;
import bus.driver.base.BaseFragment;
import bus.driver.bean.OrderInfo;
import bus.driver.module.order.OrderOngoingActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * Created by Lilaoda on 2017/9/27.
 * Email:749948218@qq.com
 */

public class OrderFragment extends BaseFragment {

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.refreshLayout)
    SwipeRefreshLayout refreshLayout;
    Unbinder unbinder;
    private ArrayList<OrderInfo> mOrderList;

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
        initView();
        return view;
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        mOrderList = new ArrayList<>();
        for (int i = 0; i < 2; i++) {
            mOrderList.add(new OrderInfo());
        }
        final OrderListAdapter adapter = new OrderListAdapter(mOrderList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                startActivity(new Intent(getActivity(), OrderOngoingActivity.class));
            }
        });
        adapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                Observable.timer(2, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) throws Exception {
                                if (mOrderList.size() > 40) {
                                    adapter.loadMoreEnd();
                                } else {
                                    adapter.addData(mOrderList);
                                    adapter.loadMoreComplete();
                                }
                            }
                        });
            }
        }, recyclerView);
        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Observable.timer(2, TimeUnit.SECONDS).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Long>() {
                            @Override
                            public void accept(@NonNull Long aLong) throws Exception {
                                refreshLayout.setRefreshing(false);
                                adapter.setNewData(mOrderList);
                                adapter.disableLoadMoreIfNotFullPage(recyclerView);

                            }
                        });
            }
        });

        //这句要想有效果必须放在监听器之后 要想不满屏时不能上拉加载，需要放在监听器之后 然后每次刷新数据都要再调用
        adapter.disableLoadMoreIfNotFullPage(recyclerView);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
