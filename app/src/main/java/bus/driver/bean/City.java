package bus.driver.bean;

/**
 * Created by Lilaoda on 2017/11/13.
 * Email:749948218@qq.com
 */

public class City {

    /**
     * id : 4337
     * areaName : 昆玉市
     * citycode : 1903
     * adcode : 659009
     * latitude : 37.207994
     * longitude : 79.287372
     * level : city
     * typeid : 31
     * levelCount : 2
     */

    private String id;
    private String areaName;
    private String citycode;
    private String adcode;
    private double latitude;
    private double longitude;
    private String level;
    private String typeid;
    private String levelCount;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getCitycode() {
        return citycode;
    }

    public void setCitycode(String citycode) {
        this.citycode = citycode;
    }

    public String getAdcode() {
        return adcode;
    }

    public void setAdcode(String adcode) {
        this.adcode = adcode;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getTypeid() {
        return typeid;
    }

    public void setTypeid(String typeid) {
        this.typeid = typeid;
    }

    public String getLevelCount() {
        return levelCount;
    }

    public void setLevelCount(String levelCount) {
        this.levelCount = levelCount;
    }
}
