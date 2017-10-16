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
                .setText(R.id.text_status, "订单状态：" + getOrderStatus(item.getSubStatus()));
    }

    private String getOrderStatus(int status) {
        //* 订单子状态(100.等待应答（拼车中） 200.等待接驾-预约 201.等待接驾-已出发未到达 202.等待接驾-已到达 210.出发接乘客 220.司机到达等待乘客 300.行程开始 301到达目的地400.待支付 500.已完成(待评价) 501.已完成-已评价 600.取消-自主取消 601.取消-后台取消 602.取消-应答前取消)
        if (status < 200) {
            return "等待接单";
        } else if (status < 300) {
            return "等待乘客上车";
        } else if (status < 400) {
            return "行程正在进行中";
        } else if (status < 500) {
            return "订单待支付";
        } else if (status < 600) {
            return "订单已完成";
        } else {
            return "订单已取消";
        }
    }

}
