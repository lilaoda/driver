package bus.driver.data.remote;

import java.util.List;
import java.util.Map;

import bus.driver.bean.City;
import bus.driver.bean.DriverInfo;
import bus.driver.bean.OrderInfo;
import bus.driver.bean.PageParam;
import io.reactivex.Observable;
import okhttp3.MultipartBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Liheyu on 2017/9/11.
 * Email:liheyu999@163.com
 */

public interface DriverApi {

    //    String BASE_URL = "http://192.168.8.58:8881/";
    String BASE_URL = "http://120.24.79.21:8881/";

    /**
     * 图片基础路经
     */
    String imgBaseUrl = BASE_URL + "driver/file/";

    @POST("driver/user/add")
    @FormUrlEncoded
    Observable<HttpResult<String>> regist(@Field("accountType") int accountType, @Field("mobile") String phone, @Field("password") String pwd);

    @POST("driver/user/login")
    @FormUrlEncoded
    Observable<HttpResult<String>> login(@Field("accountType") int accountType, @Field("mobile") String phone, @Field("password") String pwd);

    @POST("driver/get")
    Observable<HttpResult<DriverInfo>> getDriverInfo();

    //上传定位
    @POST("driver/sendCarTrack")
    @FormUrlEncoded
    Observable<HttpResult<String>> uploadLocation(@FieldMap Map<String, String> map);

    //获取服务器推送的订单
    @POST("driver/getPushOrderInfo")
    Observable<HttpResult<List<OrderInfo>>> pullOrder();

    //接单
    @POST("driver/order/get")
    @FormUrlEncoded
    Observable<HttpResult<String>> getOrder(@Field("order_uuid") String order_uuid);

    //确认上车
    @POST("driver/order/confirmPassenger")
    @FormUrlEncoded
    Observable<HttpResult<OrderInfo>> confirmPassenger(@Field("order_uuid") String order_uuid);

    //司机到达目的地，等待乘客接口
    @POST("driver/order/waitPassenger")
    @FormUrlEncoded
    Observable<HttpResult<OrderInfo>> waitPassenger(@Field("order_uuid") String order_uuid);

    //确认到达
    @POST("driver/order/confirmDestination")
    @FormUrlEncoded
    Observable<HttpResult<OrderInfo>> confirmArrive(@Field("order_uuid") String order_uuid);

    //确认费用
    @POST("driver/order/confirmExpenses")
    @FormUrlEncoded
    Observable<HttpResult<String>> confirmExpenses(@Field("order_uuid") String order_uuid);

    //获取订单列表
    @POST("driver/getOrderList")
    Observable<HttpResult<List<OrderInfo>>> getOrderList(@Body PageParam pageParam);

    // 司机退出登陆接口
    @POST("driver/user/logout")
    Observable<HttpResult<String>> loginOut();

    // 修改姓名
    @POST("driver/update")
    @FormUrlEncoded
    Observable<HttpResult<String>> changeName(@Field("name") String name);

    // 修改手机号
    @POST("driver/update")
    @FormUrlEncoded
    Observable<HttpResult<String>> changePhone(@Field("mobile") String mobile);

    // 修改密码接口
    @POST("driver/user/updatePassword")
    @FormUrlEncoded
    Observable<HttpResult<String>> changePwd(@Field("oldPassword") String oldPwd, @Field("password") String newPwd);

    // 修改头像码接口
    @POST("driver/user/portrait")
    Observable<HttpResult<String>> uploadUserIcon(@Body MultipartBody body);

    // 修改司机出车、收车接口 1,下班，2上班
    @POST("driver/updateWork")
    @FormUrlEncoded
    Observable<HttpResult<String>> updaeWork(@Field("isWork") int isWork);

    // POST /driver/getAreaList 城市列表 查询省用province、查询城市用city
    @POST("driver/getAreaList")
    @FormUrlEncoded
    Observable<HttpResult<List<City>>> getAreaList(@Field("level") String type);

    //  driver/order/info  获取订单详情接口
    @POST("driver/order/info")
    @FormUrlEncoded
    Observable<HttpResult<OrderInfo>> getOrderDetail(@Field("order_uuid") String orderUuid);

}
