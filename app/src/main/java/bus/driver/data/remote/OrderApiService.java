package bus.driver.data.remote;

import java.util.List;

import bus.driver.bean.OrderInfo;
import io.reactivex.Observable;
import retrofit2.http.POST;

/**
 * Created by Liheyu on 2017/9/11.
 * Email:liheyu999@163.com
 * 订单服务接口
 */

public interface OrderApiService {

    String BASE_URL = "http://120.24.79.21:8883/";

    @POST("communication/pushOrderCancel")
    Observable<HttpResult<List<OrderInfo>>> pushOrderCancel();

}
