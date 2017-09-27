package bus.driver.bean.event;

/**
 * Created by Lilaoda on 2017/9/27.
 * Email:749948218@qq.com
 * 用于发送定位结果
 */

public class LocationResultEvent {

    private boolean isLocationSuccess;//定位是否成功，如果成功，Service里的经纬度才是最新的
    private String errorInfo;

    public LocationResultEvent(boolean isLocationSuccess) {
        this.isLocationSuccess = isLocationSuccess;
    }

    public LocationResultEvent(boolean isLocationSuccess, String errorInfo) {
        this.isLocationSuccess = isLocationSuccess;
        this.errorInfo = errorInfo;
    }

    public boolean isLocationSuccess() {
        return isLocationSuccess;
    }

    public void setLocationSuccess(boolean locationSuccess) {
        isLocationSuccess = locationSuccess;
    }

    public String getErrorInfo() {
        return errorInfo;
    }

    public void setErrorInfo(String errorInfo) {
        this.errorInfo = errorInfo;
    }
}
