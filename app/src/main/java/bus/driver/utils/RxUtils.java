package bus.driver.utils;


import bus.driver.data.remote.HttpResult;
import bus.driver.data.remote.ResultFunction;
import io.reactivex.Flowable;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by Liheyu on 2017/9/19.
 * Email:liheyu999@163.com
 */

public class RxUtils {

    public static <T> Observable<T> wrapAsync(Observable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Flowable<T> wrapAsync(Flowable<T> observable) {
        return observable.subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    public static <T> Observable<T> wrapHttp(Observable<HttpResult<T>> observable) {
        return observable.map(new ResultFunction<T>())
                .subscribeOn(Schedulers.io())
                .unsubscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }
}
