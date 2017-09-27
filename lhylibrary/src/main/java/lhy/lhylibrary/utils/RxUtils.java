package lhy.lhylibrary.utils;


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
                .subscribeOn(AndroidSchedulers.mainThread());
    }
}
