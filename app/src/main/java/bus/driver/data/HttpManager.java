package bus.driver.data;


import com.ihsanbal.logging.Level;
import com.ihsanbal.logging.LoggingInterceptor;
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import bus.driver.BuildConfig;
import bus.driver.data.remote.DriverApi;
import bus.driver.data.remote.HeadIntercepter;
import bus.driver.data.remote.OrderApiService;
import okhttp3.ConnectionSpec;
import okhttp3.OkHttpClient;
import okhttp3.internal.platform.Platform;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class HttpManager {

    private static final int CONNECTIMEOUT = 5000;
    private static final int READTIMEOUT = 5000;
    private static final String CACHE_DIRECTORY_NAME = "file_cache";
    private static final long CACHE_MAX_SIZE = 1024 * 1024 * 100;//100M

    private static HttpManager instance;
    private Retrofit.Builder mRetrofitBuilder;
    private DriverApi mApiService;
    private OrderApiService mOrderApi;

    private HttpManager() {
        mRetrofitBuilder = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(getOkHttp());
    }

    public static synchronized HttpManager instance() {
        if (instance == null) {
            synchronized (HttpManager.class) {
                if (instance == null) {
                    instance = new HttpManager();
                }
            }
        }
        return instance;
    }

    private OkHttpClient getOkHttp() {
        return new OkHttpClient.Builder()
                .connectTimeout(CONNECTIMEOUT, TimeUnit.MILLISECONDS)
                .readTimeout(READTIMEOUT, TimeUnit.MILLISECONDS)
                .retryOnConnectionFailure(false)
//                .cache(new Cache(FileUtils.getCacheFile(LhyApplication.getContext(), CACHE_DIRECTORY_NAME), CACHE_MAX_SIZE))
//                .addInterceptor(new CacheIntercepter(LhyApplication.getContext()))
                .addInterceptor(new HeadIntercepter(DbManager.instance()))
                .addInterceptor(getHttpLogIntercept())
                .connectionSpecs(Arrays.asList(ConnectionSpec.CLEARTEXT, ConnectionSpec.MODERN_TLS))
                .build();
    }

    private LoggingInterceptor getHttpLogIntercept() {
        return new LoggingInterceptor.Builder()
                .loggable(true)
                .loggable(BuildConfig.DEBUG)
                .setLevel(Level.BASIC)
                .log(Platform.INFO)
                .request("Request")
                .response("Response")
                .addHeader("version", android.support.graphics.drawable.animated.BuildConfig.VERSION_NAME)
                .build();
    }

    public DriverApi getDriverService() {
        if (mApiService == null) {
            mApiService = mRetrofitBuilder.baseUrl(DriverApi.BASE_URL).build().create(DriverApi.class);
        }
        return mApiService;
    }

    public OrderApiService getOrderApi() {
        if (mOrderApi == null) {
            mOrderApi = mRetrofitBuilder.baseUrl(OrderApiService.BASE_URL).build().create(OrderApiService.class);
        }
        return mOrderApi;
    }
}
