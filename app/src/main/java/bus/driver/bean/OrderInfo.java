package bus.driver.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lilaoda on 2017/9/28.
 * Email:749948218@qq.com
 * 获取服务器推送的订单信息
 */

public class OrderInfo implements Parcelable {


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
     * 订单子状态(100.等待应答（拼车中） 200.等待接驾-预约 201.等待接驾-已出发未到达 202.等待接驾-已到达 210.出发接乘客 220.司机到达等待乘客 300.行程开始未到达目的地 301到达目的地未确认费用400.待支付 500.已完成(待评价) 501.已完成-已评价 600.取消-自主取消 601.取消-后台取消 602.取消-应答前取消)
     */
    private int subStatus;


    /**
     * 出发时间(后台说是从接到乘客开始算起)2017-10-17 16:50:45
     */
    private String leaveTime;


    /**
     * 订单创建时间 "yyyy-MM-dd HH:mm:ss"
     */
    private String createTime;

    /**
     * 派单成功时间 "yyyy-MM-dd HH:mm:ss"
     */
    private String distributeTime;


    /**
     * 司机抵达上车地点时间 "yyyy-MM-dd HH:mm:ss"
     */
    private String driArrTime;

    /**
     * 接到乘客时间（行程开始时间）
     */
    private String pasArrTime;

    /**
     * 到达目的地时间
     */
    private String arriveTime;

    /**
     * 费用确认时间
     **/
    private String fareConfirmTime;

    /**
     * 支付时间
     */
    private String payTime;

    /**
     * 订单总费用（=司机收入=各费用之和*高峰溢价率+附加服务费等+（后台）调整价格）
     */
    private Double totalFare;

    public OrderInfo() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.passengerUuid);
        dest.writeString(this.orderUuid);
        dest.writeString(this.actualPasNam);
        dest.writeString(this.actualPasMob);
        dest.writeInt(this.actualPasNum);
        dest.writeInt(this.mapType);
        dest.writeString(this.originCityUuid);
        dest.writeString(this.originBuscircleUuid);
        dest.writeDouble(this.originLng);
        dest.writeDouble(this.originLat);
        dest.writeString(this.originCity);
        dest.writeString(this.originAddress);
        dest.writeString(this.originDetailAddress);
        dest.writeString(this.destCityUuid);
        dest.writeString(this.destBuscircleUuid);
        dest.writeDouble(this.destLng);
        dest.writeDouble(this.destLat);
        dest.writeString(this.destCity);
        dest.writeString(this.destAddress);
        dest.writeString(this.destDetailAddress);
        dest.writeInt(this.mainStatus);
        dest.writeInt(this.subStatus);
        dest.writeString(this.leaveTime);
        dest.writeString(this.createTime);
        dest.writeString(this.distributeTime);
        dest.writeString(this.driArrTime);
        dest.writeString(this.pasArrTime);
        dest.writeString(this.arriveTime);
        dest.writeString(this.fareConfirmTime);
        dest.writeString(this.payTime);
        dest.writeValue(this.totalFare);
    }

    protected OrderInfo(Parcel in) {
        this.passengerUuid = in.readString();
        this.orderUuid = in.readString();
        this.actualPasNam = in.readString();
        this.actualPasMob = in.readString();
        this.actualPasNum = in.readInt();
        this.mapType = in.readInt();
        this.originCityUuid = in.readString();
        this.originBuscircleUuid = in.readString();
        this.originLng = in.readDouble();
        this.originLat = in.readDouble();
        this.originCity = in.readString();
        this.originAddress = in.readString();
        this.originDetailAddress = in.readString();
        this.destCityUuid = in.readString();
        this.destBuscircleUuid = in.readString();
        this.destLng = in.readDouble();
        this.destLat = in.readDouble();
        this.destCity = in.readString();
        this.destAddress = in.readString();
        this.destDetailAddress = in.readString();
        this.mainStatus = in.readInt();
        this.subStatus = in.readInt();
        this.leaveTime = in.readString();
        this.createTime = in.readString();
        this.distributeTime = in.readString();
        this.driArrTime = in.readString();
        this.pasArrTime = in.readString();
        this.arriveTime = in.readString();
        this.fareConfirmTime = in.readString();
        this.payTime = in.readString();
        this.totalFare = (Double) in.readValue(Double.class.getClassLoader());
    }

    public static final Creator<OrderInfo> CREATOR = new Creator<OrderInfo>() {
        @Override
        public OrderInfo createFromParcel(Parcel source) {
            return new OrderInfo(source);
        }

        @Override
        public OrderInfo[] newArray(int size) {
            return new OrderInfo[size];
        }
    };

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

    public String getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getDistributeTime() {
        return distributeTime;
    }

    public void setDistributeTime(String distributeTime) {
        this.distributeTime = distributeTime;
    }

    public String getDriArrTime() {
        return driArrTime;
    }

    public void setDriArrTime(String driArrTime) {
        this.driArrTime = driArrTime;
    }

    public String getPasArrTime() {
        return pasArrTime;
    }

    public void setPasArrTime(String pasArrTime) {
        this.pasArrTime = pasArrTime;
    }

    public String getArriveTime() {
        return arriveTime;
    }

    public void setArriveTime(String arriveTime) {
        this.arriveTime = arriveTime;
    }

    public String getFareConfirmTime() {
        return fareConfirmTime;
    }

    public void setFareConfirmTime(String fareConfirmTime) {
        this.fareConfirmTime = fareConfirmTime;
    }

    public String getPayTime() {
        return payTime;
    }

    public void setPayTime(String payTime) {
        this.payTime = payTime;
    }

    public Double getTotalFare() {
        return totalFare;
    }

    public void setTotalFare(Double totalFare) {
        this.totalFare = totalFare;
    }

    public static Creator<OrderInfo> getCREATOR() {
        return CREATOR;
    }

    @Override
    public String toString() {
        return "OrderInfo{" +
                "passengerUuid='" + passengerUuid + '\'' +
                ", orderUuid='" + orderUuid + '\'' +
                ", actualPasNam='" + actualPasNam + '\'' +
                ", actualPasMob='" + actualPasMob + '\'' +
                ", actualPasNum=" + actualPasNum +
                ", mapType=" + mapType +
                ", originCityUuid='" + originCityUuid + '\'' +
                ", originBuscircleUuid='" + originBuscircleUuid + '\'' +
                ", originLng=" + originLng +
                ", originLat=" + originLat +
                ", originCity='" + originCity + '\'' +
                ", originAddress='" + originAddress + '\'' +
                ", originDetailAddress='" + originDetailAddress + '\'' +
                ", destCityUuid='" + destCityUuid + '\'' +
                ", destBuscircleUuid='" + destBuscircleUuid + '\'' +
                ", destLng=" + destLng +
                ", destLat=" + destLat +
                ", destCity='" + destCity + '\'' +
                ", destAddress='" + destAddress + '\'' +
                ", destDetailAddress='" + destDetailAddress + '\'' +
                ", mainStatus=" + mainStatus +
                ", subStatus=" + subStatus +
                ", leaveTime='" + leaveTime + '\'' +
                ", createTime='" + createTime + '\'' +
                ", distributeTime='" + distributeTime + '\'' +
                ", driArrTime='" + driArrTime + '\'' +
                ", pasArrTime='" + pasArrTime + '\'' +
                ", arriveTime='" + arriveTime + '\'' +
                ", fareConfirmTime='" + fareConfirmTime + '\'' +
                ", payTime='" + payTime + '\'' +
                ", totalFare=" + totalFare +
                '}';
    }
}
