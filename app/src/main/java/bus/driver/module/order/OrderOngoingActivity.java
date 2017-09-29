package bus.driver.module.order;

import android.os.Bundle;
import android.support.annotation.Nullable;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import lhy.lhylibrary.utils.ActivityUtils;

/**
 * Created by Lilaoda on 2017/9/29.
 * Email:749948218@qq.com
 */

public class OrderOngoingActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ongoing);
        ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),OrderOngoingFragment.newInstance(),R.id.fl_content);
    }
}
