package bus.driver.base;

import com.liulishuo.filedownloader.FileDownloader;
import com.squareup.leakcanary.LeakCanary;

import lhy.lhylibrary.base.LhyApplication;

/**
 * Created by Liheyu on 2017/9/11.
 * Email:liheyu999@163.com
 */

public class BaseApplication extends LhyApplication {

    private static ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        applicationComponent = DaggerApplicationComponent.builder().applicationModule(new ApplicationModule(this)).build();
        initLeakCanary();
        FileDownloader.setup(this);
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }


    public static ApplicationComponent getApplicationComponent() {
        return applicationComponent;
    }
}
