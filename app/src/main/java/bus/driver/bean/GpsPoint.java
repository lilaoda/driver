package bus.driver.bean;

/**
 * Created by Lilaoda on 2017/11/14.
 * Email:749948218@qq.com
 * 每次定位的GPS点
 */

public class GpsPoint {

    /**
     * 纬度
     */
    private double lat;
    /**
     * 经度
     */
    private double lng;
    /**
     * 时间戳
     */
    private long timemap;
    /**
     * 定位精度 单位:米
     */
    private float accuracy;

    /**
     * 速度
     */
    private float speed ;

    /**
     * 方向角
     * 获取方向角(单位：度） 默认值：0.0
     取值范围：【0，360】，其中0度表示正北方向，90度表示正东，180度表示正南，270度表示正西
     3.1.0之前的版本只有定位类型为 AMapLocation.LOCATION_TYPE_GPS时才有值
     自3.1.0版本开始，不限定定位类型，当定位类型不是AMapLocation.LOCATION_TYPE_GPS时，可以通过 AMapLocationClientOption.setSensorEnable(boolean) 控制是否返回方向角，当设置为true时会通过手机传感器获取方向角,如果手机没有对应的传感器会返回0.0 注意：
     定位类型为AMapLocation.LOCATION_TYPE_GPS时，方向角指的是运动方向
     定位类型不是AMapLocation.LOCATION_TYPE_GPS时，方向角指的是手机朝向
     */
    private float bearing ;

    /**
     * 坐标系，
     * 0：WGS84
     * 1：GCJ02 火星坐标系
     * 2：BD09 百度坐标系
     */
    private int coorType;

    /**
     * 定位类型
     * 0：gps定位
     * 1：wifi定位
     * 2：基站定位
     */
    private int locType ;

    @Override
    public GpsPoint clone() {
        GpsPoint point = new GpsPoint();
        point.bearing = this.bearing;
        point.accuracy = this.accuracy;
        point.coorType = this.coorType;
        point.lat = this.lat;
        point.lng = this.lng;
        point.locType = this.locType;
        point.speed = this.speed;
        point.timemap = this.timemap;
        return point;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public long getTimemap() {
        return timemap;
    }

    public void setTimemap(long timemap) {
        this.timemap = timemap;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getSpeed() {
        return speed;
    }

    public void setSpeed(float speed) {
        this.speed = speed;
    }

    public float getBearing() {
        return bearing;
    }

    public void setBearing(float bearing) {
        this.bearing = bearing;
    }

    public int getCoorType() {
        return coorType;
    }

    public void setCoorType(int coorType) {
        this.coorType = coorType;
    }

    public int getLocType() {
        return locType;
    }

    public void setLocType(int locType) {
        this.locType = locType;
    }


}
