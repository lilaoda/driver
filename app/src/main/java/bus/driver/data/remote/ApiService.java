package bus.driver.data.remote;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by Liheyu on 2017/9/11.
 * Email:liheyu999@163.com
 */

public interface ApiService {

    String BASE_URL = "http://192.168.8.58:8881/";

    @POST("driver/user/add")
    @FormUrlEncoded
    Observable<HttpResult<String>> regist(@Field("accountType") int accountType, @Field("mobile") String phone, @Field("password") String pwd);

    @POST("driver/user/login")
    @FormUrlEncoded
    Observable<HttpResult<String>> login(@Field("accountType") int accountType, @Field("mobile") String phone, @Field("password") String pwd);


}
