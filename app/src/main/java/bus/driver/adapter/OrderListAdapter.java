package bus.driver.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import bus.driver.R;
import bus.driver.bean.OrderInfo;


/**
 * Created by Liheyu on 2017/9/19.
 * Email:liheyu999@163.com
 * 地址搜索页面的适配器
 */

public class OrderListAdapter extends BaseQuickAdapter<OrderInfo, BaseViewHolder> {

    public OrderListAdapter(@Nullable List<OrderInfo> data) {
        super(R.layout.item_order_list, data);
    }


    @Override
    protected View getItemView(@LayoutRes int layoutResId, ViewGroup parent) {
        return super.getItemView(layoutResId, parent);
    }

    @Override
    protected void convert(BaseViewHolder helper, OrderInfo item) {
        helper.setText(R.id.text_start, "始点: "+item.getOriginAddress())
                .setText(R.id.text_end, "终点: "+item.getDestAddress())
                .setText(R.id.text_orderid, "订单编号: " + item.getOrderUuid())
                .setText(R.id.text_status, "订单状态：" + item.getSubStatus() + "");
    }

    @Override
    public int addHeaderView(View header) {
        return super.addHeaderView(header);
    }


}
