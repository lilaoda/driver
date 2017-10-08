package bus.driver.bean;

/**
 * Created by Lilaoda on 2017/9/28.
 * Email:749948218@qq.com
 */

public class OrderInfo {

    private String startLocation;//起始位置
    private String endLocation;//终点位置
    private String orderId;//订单ID

    public String getStartLocation() {
        return startLocation;
    }

    public void setStartLocation(String startLocation) {
        this.startLocation = startLocation;
    }

    public String getEndLocation() {
        return endLocation;
    }

    public void setEndLocation(String endLocation) {
        this.endLocation = endLocation;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
}
