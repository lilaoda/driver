package bus.driver.bean.event;

import com.amap.api.maps.model.LatLng;

/**
 * Created by Lilaoda on 2017/10/11.
 * Email:749948218@qq.com
 * 定位事件
 */

public enum LocationEvent {

    /**
     * 定位可用
     */
    LOCATION_ENABLE(1),
    /**
     * 定位不可用
     */
    LOCATION_UNABLE(2),

    /**
     * 刷新位置时使用，发送定位结果（成功或失败）
     */
    LOCATION_REFRESH(4),
    /**
     * 测算距离 可用
     */
    LOCATION_DISTANCE_START(5),
    /**
     * 测算距离 ，不可用
     */
    LOCATION_DISTANCE_STOP(6);

    private int locationValue;

    private String orderUuid;
    /**
     * 最后一次定位的坐标点
     */
    private LatLng lastLatLng;

    /**
     * 已行驶距离
     */
    private String tripDistance;

    LocationEvent(int locationValue) {
        this.locationValue = locationValue;
    }

    public int getLocationValue() {
        return locationValue;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public LatLng getLastLatLng() {
        return lastLatLng;
    }

    public void setLastLatLng(LatLng lastLatLng) {
        this.lastLatLng = lastLatLng;
    }

    public String getTripDistance() {
        return tripDistance;
    }

    public void setTripDistance(String tripDistance) {
        this.tripDistance = tripDistance;
    }
}
