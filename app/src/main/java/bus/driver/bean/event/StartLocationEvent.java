package bus.driver.bean.event;

/**
 * Created by Lilaoda on 2017/9/27.
 * Email:749948218@qq.com
 * 用于开启定位
 */

public class StartLocationEvent {

    private boolean isStartLocation;//是否需要床位，是 开始定位，否关闭定位
    private boolean isNeedResult;//是否需要发射定位结果

    public StartLocationEvent(boolean isStartLocation, boolean isNeedResult) {
        this.isStartLocation = isStartLocation;
        this.isNeedResult = isNeedResult;
    }

    public StartLocationEvent(boolean isStartLocation) {
        this.isStartLocation = isStartLocation;
    }

    public boolean isStartLocation() {
        return isStartLocation;
    }

    public void setStartLocation(boolean startLocation) {
        isStartLocation = startLocation;
    }

    public boolean isNeedResult() {
        return isNeedResult;
    }

    public void setNeedResult(boolean needResult) {
        isNeedResult = needResult;
    }
}
