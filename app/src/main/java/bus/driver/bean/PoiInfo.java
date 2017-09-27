package bus.driver.bean;

import android.os.Parcel;
import android.os.Parcelable;

import javax.inject.Inject;

/**
 * Created by Liheyu on 2017/9/19.
 * Email:liheyu999@163.com
 * 地理位置信息
 */

public class PoiInfo implements Parcelable{

    private double latitude;
    private double longitude;
    private String title;
    private String cityName;
    private String cityCode;
    private String provinceName;
    private String adName;
    private String snippet;

    @Inject
    public PoiInfo() {
    }

    protected PoiInfo(Parcel in) {
        latitude = in.readDouble();
        longitude = in.readDouble();
        title = in.readString();
        cityName = in.readString();
        cityCode = in.readString();
        provinceName = in.readString();
        adName = in.readString();
        snippet = in.readString();
    }

    public static final Creator<PoiInfo> CREATOR = new Creator<PoiInfo>() {
        @Override
        public PoiInfo createFromParcel(Parcel in) {
            return new PoiInfo(in);
        }

        @Override
        public PoiInfo[] newArray(int size) {
            return new PoiInfo[size];
        }
    };

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public void setCityCode(String cityCode) {
        this.cityCode = cityCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public String getTitle() {
        return title;
    }

    public String getCityName() {
        return cityName;
    }

    public String getCityCode() {
        return cityCode;
    }


    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getAdName() {
        return adName;
    }

    public void setAdName(String adName) {
        this.adName = adName;
    }

    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    @Override
    public String toString() {
        return "PoiInfo{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", title='" + title + '\'' +
                ", cityName='" + cityName + '\'' +
                ", cityCode='" + cityCode + '\'' +
                ", provinceName='" + provinceName + '\'' +
                ", adName='" + adName + '\'' +
                ", snippet='" + snippet + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
        dest.writeString(title);
        dest.writeString(cityName);
        dest.writeString(cityCode);
        dest.writeString(provinceName);
        dest.writeString(adName);
        dest.writeString(snippet);
    }
}
