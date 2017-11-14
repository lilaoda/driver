package bus.driver.bean.event;

/**
 * Created by Lilaoda on 2017/10/11.
 * Email:749948218@qq.com
 * 定位事件
 */

public enum LocationEvent {

    LOCATION_ENABLE(1),
    LOCATION_UNABLE(2),
    LOCATION_ONCE(3),
    LOCATION_REFRESH(4),
    LOCATION_DISTANCE_START(5),
    LOCATION_DISTANCE_STOP(6);

    private int locationValue;

    LocationEvent(int locationValue) {
        this.locationValue = locationValue;
    }

    public int getLocationValue() {
        return locationValue;
    }
}
