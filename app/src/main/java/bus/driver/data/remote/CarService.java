package bus.driver.data.remote;

import java.util.Map;

import io.reactivex.Observable;
import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Liheyu on 2017/9/11.
 * Email:liheyu999@163.com
 * 车辆服务接口
 */

public interface CarService {

    String BASE_URL = "http://192.168.8.58:8882/";

    @POST("car/sendCarTrack")
    @FormUrlEncoded
    Observable<HttpResult<String>> uploadLocation(@FieldMap Map<String,String> map);

}
