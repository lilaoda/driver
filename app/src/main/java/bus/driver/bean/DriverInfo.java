package bus.driver.bean;

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
        private int sex;

        /** 成功订单数 */
        private int orderCount;

        /** 上下班状态（1：下班、2：上班） */
        private int isWork;

        /** 平均分数 */
        private double score;

        /** 司机状态(1:正常、2:短期封号、3:长期封号、4:未审核、5:删除) */
        private int status;

        /** 终止时间（针对短期封号>status） */
        private String abortTime;

        /** 封号备注 */
        private String abortRemark;

        /** 是否首次登录 1：是、2：否 */
        private int isFirst;

        /** 账号余额 */
        private double balance;

        /** 最后一次登录时间 */
        private String lastLogin;

        /** 推送id */
        private String pushId;

        /** 设备类型（1.android，2.IOS） */
        private int deviceType;

        /** 设备唯一标识 */
        private String deviceToken;

        /** 设备版本 */
        private String deviceVersion;

        /** 使用的app版本 */
        private String appVersion;

        /** 当前经度 */
        private double currentLng;

        /** 当前纬度 */
        private double currentLat;

        /** 当前角度 */
        private String currentAngle;

        /** 推送等级（1：最低，9999：最高） */
        private int pushLevel;

        /** 预约时间段 ：开始时间 */
        private String appointTimeStart;

        /** 预约时间段：结束时间 */
        private String appointTimeEnd;

        /** 听单模式 1：全部，2：实时 3：预约 */
        private int remindType;

        /** 身份证号 */
        private String idCard;

        /** 联系地址 */
        private String address;

        /** 出生日期 */
        private String birthday;

        /** 民族 */
        private int nation;

        /**  */
        private String operationRemark;

        /** 创建人 */
        private String createdBy;

        /** 创建时间 */
        private String createdOn;

        /** 更新人 */
        private String updatedBy;

        /** 更新时间 */
        private String updatedOn;

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

        public int getSex() {
            return sex;
        }

        public void setSex(int sex) {
            this.sex = sex;
        }

        public int getOrderCount() {
            return orderCount;
        }

        public void setOrderCount(int orderCount) {
            this.orderCount = orderCount;
        }

        public int getIsWork() {
            return isWork;
        }

        public void setIsWork(int isWork) {
            this.isWork = isWork;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getAbortTime() {
            return abortTime;
        }

        public void setAbortTime(String abortTime) {
            this.abortTime = abortTime;
        }

        public String getAbortRemark() {
            return abortRemark;
        }

        public void setAbortRemark(String abortRemark) {
            this.abortRemark = abortRemark;
        }

        public int getIsFirst() {
            return isFirst;
        }

        public void setIsFirst(int isFirst) {
            this.isFirst = isFirst;
        }

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }

        public String getLastLogin() {
            return lastLogin;
        }

        public void setLastLogin(String lastLogin) {
            this.lastLogin = lastLogin;
        }

        public String getPushId() {
            return pushId;
        }

        public void setPushId(String pushId) {
            this.pushId = pushId;
        }

        public int getDeviceType() {
            return deviceType;
        }

        public void setDeviceType(int deviceType) {
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

        public double getCurrentLng() {
            return currentLng;
        }

        public void setCurrentLng(double currentLng) {
            this.currentLng = currentLng;
        }

        public double getCurrentLat() {
            return currentLat;
        }

        public void setCurrentLat(double currentLat) {
            this.currentLat = currentLat;
        }

        public String getCurrentAngle() {
            return currentAngle;
        }

        public void setCurrentAngle(String currentAngle) {
            this.currentAngle = currentAngle;
        }

        public int getPushLevel() {
            return pushLevel;
        }

        public void setPushLevel(int pushLevel) {
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

        public int getRemindType() {
            return remindType;
        }

        public void setRemindType(int remindType) {
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

        public String getBirthday() {
            return birthday;
        }

        public void setBirthday(String birthday) {
            this.birthday = birthday;
        }

        public int getNation() {
            return nation;
        }

        public void setNation(int nation) {
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
}
