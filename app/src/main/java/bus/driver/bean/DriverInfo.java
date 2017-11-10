package bus.driver.bean;

import java.util.Date;

/**
 * Created by Lilaoda on 2017/10/8.
 * Email:749948218@qq.com
 */

public class DriverInfo {

    /**
     * driver : null
     * driverExt : null
     * user : {"uuid":"913a1df9e7014f1d90707a6af6e1b1ad","accountType":0,"userAccount":"13922239152","password":"E10ADC3949BA59ABBE56E057F20F883E","face":null,"userType":null,"userRole":null,"status":1,"resetPasswordDate":null,"loginErrorTime":null,"loginErrorDate":null,"token":"ddcf41cb468d4fa3a9ae83ec67e6dff3","operationRemark":null,"createdBy":null,"createdOn":null,"updatedBy":null,"updatedOn":null}
     * driverContract : null
     */

    private Driver driver;
    private String driverExt;
    private UserBean user;
    private String driverContract;

    public Driver getDriver() {
        return driver;
    }

    public void setDriver(Driver driver) {
        this.driver = driver;
    }

    public String getDriverExt() {
        return driverExt;
    }

    public void setDriverExt(String driverExt) {
        this.driverExt = driverExt;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public String getDriverContract() {
        return driverContract;
    }

    public void setDriverContract(String driverContract) {
        this.driverContract = driverContract;
    }

    public static class UserBean {
        /**
         * uuid : 913a1df9e7014f1d90707a6af6e1b1ad
         * accountType : 0
         * userAccount : 13922239152
         * password : E10ADC3949BA59ABBE56E057F20F883E
         * face : null
         * userType : null
         * userRole : null
         * status : 1
         * resetPasswordDate : null
         * loginErrorTime : null
         * loginErrorDate : null
         * token : ddcf41cb468d4fa3a9ae83ec67e6dff3
         * operationRemark : null
         * createdBy : null
         * createdOn : null
         * updatedBy : null
         * updatedOn : null
         */

        private String uuid;
        private int accountType;
        private String userAccount;
        private String password;
        private String face;
        private String userType;
        private String userRole;
        private int status;
        private String resetPasswordDate;
        private String loginErrorTime;
        private String loginErrorDate;
        private String token;
        private String operationRemark;
        private String createdBy;
        private String createdOn;
        private String updatedBy;
        private String updatedOn;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public int getAccountType() {
            return accountType;
        }

        public void setAccountType(int accountType) {
            this.accountType = accountType;
        }

        public String getUserAccount() {
            return userAccount;
        }

        public void setUserAccount(String userAccount) {
            this.userAccount = userAccount;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public String getFace() {
            return face;
        }

        public void setFace(String face) {
            this.face = face;
        }

        public String getUserType() {
            return userType;
        }

        public void setUserType(String userType) {
            this.userType = userType;
        }

        public String getUserRole() {
            return userRole;
        }

        public void setUserRole(String userRole) {
            this.userRole = userRole;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getResetPasswordDate() {
            return resetPasswordDate;
        }

        public void setResetPasswordDate(String resetPasswordDate) {
            this.resetPasswordDate = resetPasswordDate;
        }

        public String getLoginErrorTime() {
            return loginErrorTime;
        }

        public void setLoginErrorTime(String loginErrorTime) {
            this.loginErrorTime = loginErrorTime;
        }

        public String getLoginErrorDate() {
            return loginErrorDate;
        }

        public void setLoginErrorDate(String loginErrorDate) {
            this.loginErrorDate = loginErrorDate;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }

        public String getOperationRemark() {
            return operationRemark;
        }

        public void setOperationRemark(String operationRemark) {
            this.operationRemark = operationRemark;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public String getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(String createdOn) {
            this.createdOn = createdOn;
        }

        public String getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
        }

        public String getUpdatedOn() {
            return updatedOn;
        }

        public void setUpdatedOn(String updatedOn) {
            this.updatedOn = updatedOn;
        }
    }

    public static class Driver{
        /**  */
        private String uuid;

        /**  */
        private String userUuid;

        /** 手机号码 */
        private String mobile;

        /** 姓名 */
        private String name;

        /** 性别 1.男、2.女 */
        private Integer sex;

        /** 成功订单数 */
        private Integer orderCount;

        /** 上下班状态（1：下班、2：上班） */
        private Integer isWork;

        /** 平均分数 */
        private Double score;

        /** 司机状态(1:正常、2:短期封号、3:长期封号、4:未审核、5:删除) */
        private Integer status;

        /** 终止时间（针对短期封号>status） */
        private Date abortTime;

        /** 封号备注 */
        private String abortRemark;

        /** 是否首次登录 1：是、2：否 */
        private Integer isFirst;

        /** 账号余额 */
        private Double balance;

        /** 最后一次登录时间 */
        private Date lastLogin;

        /** 推送id */
        private String pushId;

        /** 设备类型（1.android，2.IOS） */
        private Integer deviceType;

        /** 设备唯一标识 */
        private String deviceToken;

        /** 设备版本 */
        private String deviceVersion;

        /** 使用的app版本 */
        private String appVersion;

        /** 当前经度 */
        private Double currentLng;

        /** 当前纬度 */
        private Double currentLat;

        /** 当前角度 */
        private String currentAngle;

        /** 推送等级（1：最低，9999：最高） */
        private Integer pushLevel;

        /** 预约时间段 ：开始时间 */
        private String appointTimeStart;

        /** 预约时间段：结束时间 */
        private String appointTimeEnd;

        /** 听单模式 1：全部，2：实时 3：预约 */
        private Integer remindType;

        /** 身份证号 */
        private String idCard;

        /** 联系地址 */
        private String address;

        /** 出生日期 */
        private Date birthday;

        /** 民族 */
        private Integer nation;

        /**  */
        private String operationRemark;

        /** 创建人 */
        private String createdBy;

        /** 创建时间 */
        private Date createdOn;

        /** 更新人 */
        private String updatedBy;

        /** 更新时间 */
        private Date updatedOn;

        public String getUuid() {
            return uuid;
        }

        public void setUuid(String uuid) {
            this.uuid = uuid;
        }

        public String getUserUuid() {
            return userUuid;
        }

        public void setUserUuid(String userUuid) {
            this.userUuid = userUuid;
        }

        public String getMobile() {
            return mobile;
        }

        public void setMobile(String mobile) {
            this.mobile = mobile;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getSex() {
            return sex;
        }

        public void setSex(Integer sex) {
            this.sex = sex;
        }

        public Integer getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(Integer orderCount) {
            this.orderCount = orderCount;
        }

        public Integer getIsWork() {
            return isWork;
        }

        public void setIsWork(Integer isWork) {
            this.isWork = isWork;
        }

        public Double getScore() {
            return score;
        }

        public void setScore(Double score) {
            this.score = score;
        }

        public Integer getStatus() {
            return status;
        }

        public void setStatus(Integer status) {
            this.status = status;
        }

        public Date getAbortTime() {
            return abortTime;
        }

        public void setAbortTime(Date abortTime) {
            this.abortTime = abortTime;
        }

        public String getAbortRemark() {
            return abortRemark;
        }

        public void setAbortRemark(String abortRemark) {
            this.abortRemark = abortRemark;
        }

        public Integer getIsFirst() {
            return isFirst;
        }

        public void setIsFirst(Integer isFirst) {
            this.isFirst = isFirst;
        }

        public Double getBalance() {
            return balance;
        }

        public void setBalance(Double balance) {
            this.balance = balance;
        }

        public Date getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(Date lastLogin) {
            this.lastLogin = lastLogin;
        }

        public String getPushId() {
            return pushId;
        }

        public void setPushId(String pushId) {
            this.pushId = pushId;
        }

        public Integer getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(Integer deviceType) {
            this.deviceType = deviceType;
        }

        public String getDeviceToken() {
            return deviceToken;
        }

        public void setDeviceToken(String deviceToken) {
            this.deviceToken = deviceToken;
        }

        public String getDeviceVersion() {
            return deviceVersion;
        }

        public void setDeviceVersion(String deviceVersion) {
            this.deviceVersion = deviceVersion;
        }

        public String getAppVersion() {
            return appVersion;
        }

        public void setAppVersion(String appVersion) {
            this.appVersion = appVersion;
        }

        public Double getCurrentLng() {
            return currentLng;
        }

        public void setCurrentLng(Double currentLng) {
            this.currentLng = currentLng;
        }

        public Double getCurrentLat() {
            return currentLat;
        }

        public void setCurrentLat(Double currentLat) {
            this.currentLat = currentLat;
        }

        public String getCurrentAngle() {
            return currentAngle;
        }

        public void setCurrentAngle(String currentAngle) {
            this.currentAngle = currentAngle;
        }

        public Integer getPushLevel() {
            return pushLevel;
        }

        public void setPushLevel(Integer pushLevel) {
            this.pushLevel = pushLevel;
        }

        public String getAppointTimeStart() {
            return appointTimeStart;
        }

        public void setAppointTimeStart(String appointTimeStart) {
            this.appointTimeStart = appointTimeStart;
        }

        public String getAppointTimeEnd() {
            return appointTimeEnd;
        }

        public void setAppointTimeEnd(String appointTimeEnd) {
            this.appointTimeEnd = appointTimeEnd;
        }

        public Integer getRemindType() {
            return remindType;
        }

        public void setRemindType(Integer remindType) {
            this.remindType = remindType;
        }

        public String getIdCard() {
            return idCard;
        }

        public void setIdCard(String idCard) {
            this.idCard = idCard;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }

        public Date getBirthday() {
            return birthday;
        }

        public void setBirthday(Date birthday) {
            this.birthday = birthday;
        }

        public Integer getNation() {
            return nation;
        }

        public void setNation(Integer nation) {
            this.nation = nation;
        }

        public String getOperationRemark() {
            return operationRemark;
        }

        public void setOperationRemark(String operationRemark) {
            this.operationRemark = operationRemark;
        }

        public String getCreatedBy() {
            return createdBy;
        }

        public void setCreatedBy(String createdBy) {
            this.createdBy = createdBy;
        }

        public Date getCreatedOn() {
            return createdOn;
        }

        public void setCreatedOn(Date createdOn) {
            this.createdOn = createdOn;
        }

        public String getUpdatedBy() {
            return updatedBy;
        }

        public void setUpdatedBy(String updatedBy) {
            this.updatedBy = updatedBy;
        }

        public Date getUpdatedOn() {
            return updatedOn;
        }

        public void setUpdatedOn(Date updatedOn) {
            this.updatedOn = updatedOn;
        }
    }
}
