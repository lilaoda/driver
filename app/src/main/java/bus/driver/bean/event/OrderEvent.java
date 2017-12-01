package bus.driver.bean.event;

/**
 * Created by Lilaoda on 2017/10/11.
 * Email:749948218@qq.com
 * 订单事件
 */

public enum OrderEvent {

    /**
     * 订单推送可用
     */
    ORDER_PULL_ENABLE(1),
    /**
     * 订单推送不可用
     */
    ORDER_PULL_UNABLE(2),
    /**
     * 接单后循环获取订单是否被取消是否可用
     */
    ORDER_GET_CANCEL_ENABLE(3),

    /**
     * 接单后循环获取订单是否被取消是否可用
     */
    ORDER_GET_CANCEL_UNABLE(4);

    private int OrderEventValue;


    OrderEvent(int locationValue) {
        this.OrderEventValue = locationValue;
    }

    public int getLocationValue() {
        return OrderEventValue;
    }

}
