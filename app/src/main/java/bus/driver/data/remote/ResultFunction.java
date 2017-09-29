package bus.driver.data.remote;


import io.reactivex.functions.Function;
import lhy.lhylibrary.http.exception.ApiException;

/**
 * Created by Liheyu on 2017/3/9.
 * Email:liheyu999@163.com
 * APP数据接口异常处理
 */

public class ResultFunction<T> implements Function<HttpResult<T>, T> {

    @Override
    public T apply(HttpResult<T> tHttpResult) throws Exception {
        if (!tHttpResult.isResult()) {
            throw new ApiException(tHttpResult.getMessage());
        }
        return tHttpResult.getData();
    }
}
