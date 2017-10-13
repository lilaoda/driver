package bus.driver.base;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.squareup.leakcanary.LeakCanary;

import lhy.lhylibrary.base.LhyApplication;

/**
 * Created by Liheyu on 2017/9/11.
 * Email:liheyu999@163.com
 */

public class BaseApplication extends LhyApplication {


    @Override
    public void onCreate() {
        super.onCreate();
        initLeakCanary();
    }

    private void initLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            return;
        }
        LeakCanary.install(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

}
