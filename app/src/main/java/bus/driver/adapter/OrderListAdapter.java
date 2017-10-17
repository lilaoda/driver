package bus.driver.adapter;

import android.support.annotation.Nullable;

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
    protected void convert(BaseViewHolder helper, OrderInfo item) {
        helper.setText(R.id.text_start, "始点: " + item.getOriginAddress())
                .setText(R.id.text_end, "终点: " + item.getDestAddress())
                .setText(R.id.text_orderid, "订单编号: " + item.getOrderUuid())
                .setText(R.id.text_status, "订单状态：" + getOrderStatus(item.getMainStatus()));
    }

    private String getOrderStatus(int status) {
        // 订单主状态1：订单初识化,2订单进行中3：订单结束（待支付）4：支付完成5.取消
        if (status == 1) {
            return "订单初始化";
        } else if (status == 2) {
            return "订单进行中";
        } else if (status == 3) {
            return "订单结束（待支付）";
        } else if (status == 4) {
            return "支付完成";
        } else {
            return "取消";
        }
    }

}
