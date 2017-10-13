package bus.driver.data.remote;

import java.util.List;
import java.util.Map;

import bus.driver.bean.DriverInfo;
import bus.driver.bean.OrderInfo;
import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Liheyu on 2017/9/11.
 * Email:liheyu999@163.com
 */

public interface DriverService {

    String BASE_URL = "http://192.168.8.58:8881/";

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
    Observable<HttpResult<String>> confirmPassenger(@Field("order_uuid") String order_uuid);

    //确认到达
    @POST("driver/order/confirmDestination")
    @FormUrlEncoded
    Observable<HttpResult<String>> confirmArrive(@Field("order_uuid") String order_uuid);

    //确认费用
    @POST("driver/order/confirmExpenses")
    @FormUrlEncoded
    Observable<HttpResult<String>> confirmExpenses(@Field("order_uuid") String order_uuid);

    //获取订单列表
    @POST("driver/getOrderList")
    Observable<HttpResult<List<OrderInfo>>> getOrderList();


}
