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

    private String driver;
    private String driverExt;
    private UserBean user;
    private String driverContract;

    public String getDriver() {
        return driver;
    }

    public void setDriver(String driver) {
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
}
