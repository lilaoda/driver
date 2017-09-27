package bus.driver.base;

import com.google.gson.Gson;

import javax.inject.Singleton;

import bus.driver.data.AMapManager;
import bus.driver.data.DbManager;
import bus.driver.data.HttpManager;
import bus.driver.data.SpManager;
import dagger.Component;

/**
 * Created by Liheyu on 2017/9/19.
 * Email:liheyu999@163.com
 * //全局组件 ，所有的单例；
 */

@Singleton
@Component(modules = ApplicationModule.class)
public interface ApplicationComponent {

    HttpManager getHttpManager();

    AMapManager getAMapManager();

    SpManager getSpManager();

    DbManager getDbManager();

    Gson getGson();
}
