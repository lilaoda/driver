package bus.driver.base;

import android.content.Context;

import com.google.gson.Gson;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Liheyu on 2017/9/19.
 * Email:liheyu999@163.com
 * 此类为全局Module，提供第三方的单一实例
 * 非第三方提供者{@link bus.driver.base.ApplicationComponent}
 */

@Singleton
@Module
public final class ApplicationModule {

    private final Context context;

    public ApplicationModule(Context context) {
        this.context = context;
    }

    @Provides
    Context provideContext() {
        return context;
    }

    @Singleton
    @Provides
    public Gson provideGson() {
        return new Gson();
    }
}
