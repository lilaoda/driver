package bus.driver.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lilaoda on 2017/9/28.
 * Email:749948218@qq.com
 * 获取服务器推送的订单信息
 */

public class OrderInfo implements Parcelable{


    /**
     * 订单创建乘客ID
     */
    private String passengerUuid;

    /**
     * 订单id
     */
    private String orderUuid;

    /**
     * 真实乘车人姓名
     */
    private String actualPasNam;

    /**
     * 真实乘客人手机号
     */
    private String actualPasMob;

    /**
     * 实际乘车人数
     */
    private int actualPasNum;

    /**
     * 出发时间
     */
    private String leaveTime;

    /**
     * 使用导航地图类型：0为高德、1百度、2谷歌、3其他
     */
    private int mapType;

    /**
     * 出发城市uuid
     */
    private String originCityUuid;

    /**
     * 出发商圈uuid
     */
    private String originBuscircleUuid;

    /**
     * 出发地经度
     */
    private double originLng;

    /**
     * 出发地纬度
     */
    private double originLat;

    /**
     * 出发城市
     */
    private String originCity;

    /**
     * 出发地点
     */
    private String originAddress;

    /**
     * 出发详情地址
     */
    private String originDetailAddress;

    /**
     * 目的地城市uuid
     */
    private String destCityUuid;

    /**
     * 目的地商圈uuid
     */
    private String destBuscircleUuid;

    /**
     * 目的地经度
     */
    private double destLng;

    /**
     * 目的地纬度
     */
    private double destLat;

    /**
     * 目的地城市
     */
    private String destCity;

    /**
     * 目的地地址
     */
    private String destAddress;

    /**
     * 目的地详细地址
     */
    private String destDetailAddress;

    /**
     * 订单主状态1：订单初识化,2订单进行中3：订单结束（待支付）4：支付完成5.取消
     */
    private int mainStatus;

    /**
     * 订单子状态(100.等待应答（拼车中） 200.等待接驾-预约 201.等待接驾-已出发未到达 202.等待接驾-已到达 210.出发接乘客 220.司机到达等待乘客 300.行程开始 301到达目的地400.待支付 500.已完成(待评价) 501.已完成-已评价 600.取消-自主取消 601.取消-后台取消 602.取消-应答前取消)
     */
    private int subStatus;

    public OrderInfo() {
    }

    protected OrderInfo(Parcel in) {
        passengerUuid = in.readString();
        orderUuid = in.readString();
        actualPasNam = in.readString();
        actualPasMob = in.readString();
        actualPasNum = in.readInt();
        leaveTime = in.readString();
        mapType = in.readInt();
        originCityUuid = in.readString();
        originBuscircleUuid = in.readString();
        originLng = in.readDouble();
        originLat = in.readDouble();
        originCity = in.readString();
        originAddress = in.readString();
        originDetailAddress = in.readString();
        destCityUuid = in.readString();
        destBuscircleUuid = in.readString();
        destLng = in.readDouble();
        destLat = in.readDouble();
        destCity = in.readString();
        destAddress = in.readString();
        destDetailAddress = in.readString();
        mainStatus = in.readInt();
        subStatus = in.readInt();
    }

    public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel in) {
            return new OrderInfo(in);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(passengerUuid);
        dest.writeString(orderUuid);
        dest.writeString(actualPasNam);
        dest.writeString(actualPasMob);
        dest.writeInt(actualPasNum);
        dest.writeString(leaveTime);
        dest.writeInt(mapType);
        dest.writeString(originCityUuid);
        dest.writeString(originBuscircleUuid);
        dest.writeDouble(originLng);
        dest.writeDouble(originLat);
        dest.writeString(originCity);
        dest.writeString(originAddress);
        dest.writeString(originDetailAddress);
        dest.writeString(destCityUuid);
        dest.writeString(destBuscircleUuid);
        dest.writeDouble(destLng);
        dest.writeDouble(destLat);
        dest.writeString(destCity);
        dest.writeString(destAddress);
        dest.writeString(destDetailAddress);
        dest.writeInt(mainStatus);
        dest.writeInt(subStatus);
    }

    public String getPassengerUuid() {
        return passengerUuid;
    }

    public void setPassengerUuid(String passengerUuid) {
        this.passengerUuid = passengerUuid;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }

    public String getActualPasNam() {
        return actualPasNam;
    }

    public void setActualPasNam(String actualPasNam) {
        this.actualPasNam = actualPasNam;
    }

    public String getActualPasMob() {
        return actualPasMob;
    }

    public void setActualPasMob(String actualPasMob) {
        this.actualPasMob = actualPasMob;
    }

    public int getActualPasNum() {
        return actualPasNum;
    }

    public void setActualPasNum(int actualPasNum) {
        this.actualPasNum = actualPasNum;
    }

    public String getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }

    public int getMapType() {
        return mapType;
    }

    public void setMapType(int mapType) {
        this.mapType = mapType;
    }

    public String getOriginCityUuid() {
        return originCityUuid;
    }

    public void setOriginCityUuid(String originCityUuid) {
        this.originCityUuid = originCityUuid;
    }

    public String getOriginBuscircleUuid() {
        return originBuscircleUuid;
    }

    public void setOriginBuscircleUuid(String originBuscircleUuid) {
        this.originBuscircleUuid = originBuscircleUuid;
    }

    public double getOriginLng() {
        return originLng;
    }

    public void setOriginLng(double originLng) {
        this.originLng = originLng;
    }

    public double getOriginLat() {
        return originLat;
    }

    public void setOriginLat(double originLat) {
        this.originLat = originLat;
    }

    public String getOriginCity() {
        return originCity;
    }

    public void setOriginCity(String originCity) {
        this.originCity = originCity;
    }

    public String getOriginAddress() {
        return originAddress;
    }

    public void setOriginAddress(String originAddress) {
        this.originAddress = originAddress;
    }

    public String getOriginDetailAddress() {
        return originDetailAddress;
    }

    public void setOriginDetailAddress(String originDetailAddress) {
        this.originDetailAddress = originDetailAddress;
    }

    public String getDestCityUuid() {
        return destCityUuid;
    }

    public void setDestCityUuid(String destCityUuid) {
        this.destCityUuid = destCityUuid;
    }

    public String getDestBuscircleUuid() {
        return destBuscircleUuid;
    }

    public void setDestBuscircleUuid(String destBuscircleUuid) {
        this.destBuscircleUuid = destBuscircleUuid;
    }

    public double getDestLng() {
        return destLng;
    }

    public void setDestLng(double destLng) {
        this.destLng = destLng;
    }

    public double getDestLat() {
        return destLat;
    }

    public void setDestLat(double destLat) {
        this.destLat = destLat;
    }

    public String getDestCity() {
        return destCity;
    }

    public void setDestCity(String destCity) {
        this.destCity = destCity;
    }

    public String getDestAddress() {
        return destAddress;
    }

    public void setDestAddress(String destAddress) {
        this.destAddress = destAddress;
    }

    public String getDestDetailAddress() {
        return destDetailAddress;
    }

    public void setDestDetailAddress(String destDetailAddress) {
        this.destDetailAddress = destDetailAddress;
    }

    public int getMainStatus() {
        return mainStatus;
    }

    public void setMainStatus(int mainStatus) {
        this.mainStatus = mainStatus;
    }

    public int getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(int subStatus) {
        this.subStatus = subStatus;
    }

    public static Creator<OrderInfo> getCREATOR() {
        return CREATOR;
    }
}
