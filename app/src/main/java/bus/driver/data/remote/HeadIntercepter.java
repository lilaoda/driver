package bus.driver.data.remote;

import java.io.IOException;

import bus.driver.data.DbManager;
import bus.driver.data.entity.User;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Liheyu on 2017/4/21.
 * Email:liheyu999@163.com
 */

public class HeadIntercepter implements Interceptor {

    private DbManager dbManager;

    public HeadIntercepter(DbManager dbManager) {
        this.dbManager = dbManager;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        User user = dbManager.getUser();
        Request request = original
                .newBuilder()
                .addHeader("Content-Type", "application/json; charset=UTF-8") // application/x-www-form-urlencoded ; charset=UTF-8
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept", "application/json")
                .addHeader("token", user == null ? "" : user.getToken())
                .method(original.method(), original.body())
                .build();
        Response mResponse = chain.proceed(request);
        return mResponse;
    }
}
