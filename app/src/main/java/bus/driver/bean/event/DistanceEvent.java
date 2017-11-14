package bus.driver.bean.event;

/**
 * Created by Lilaoda on 2017/11/14.
 * Email:749948218@qq.com
 * 实时测算距离的结果
 */

public class DistanceEvent {


    /**
     * 实时定位次数
     */
    private  int locationNumber = 0;

    /**
     * 实时行驶距离 单位 米
     */
    private  double loacationDistance = 0;


    public int getLocationNumber() {
        return locationNumber;
    }

    public void setLocationNumber(int locationNumber) {
        this.locationNumber = locationNumber;
    }

    public double getLoacationDistance() {
        return loacationDistance;
    }

    public void setLoacationDistance(double loacationDistance) {
        this.loacationDistance = loacationDistance;
    }
}
