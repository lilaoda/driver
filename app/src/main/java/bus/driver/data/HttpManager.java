package bus.driver.data;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import javax.inject.Inject;
import javax.inject.Singleton;

import lhy.lhylibrary.http.OkhttpManager;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class HttpManager {

    private Retrofit mRetrofit;

    @Inject
    public HttpManager() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(OkhttpManager.getInstance().getCacheOKhttp())
                .build();
    }


    public ApiService getApiService() {
        return mRetrofit.create(ApiService.class);
    }
}
