package bus.driver.data.remote;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Liheyu on 2017/9/11.
 * Email:liheyu999@163.com
 * 订单服务接口
 */

public interface OrderService {

    String BASE_URL = "http://192.168.8.41:8185/";

    @POST("order/getPushOrderInfo")
    @FormUrlEncoded
    Observable<HttpResult<String>> uploadLocation(@FieldMap Map<String, String> map);

}
