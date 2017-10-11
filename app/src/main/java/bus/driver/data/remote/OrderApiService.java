package bus.driver.data.remote;

import io.reactivex.Observable;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Liheyu on 2017/9/11.
 * Email:liheyu999@163.com
 * 订单服务接口
 */

public interface OrderApiService {

    String BASE_URL = "http://192.168.8.58:8883/";

    @POST("communication/pushOrderCancel")
    @FormUrlEncoded
    Observable<HttpResult<String>> pushOrderCancel();

}
