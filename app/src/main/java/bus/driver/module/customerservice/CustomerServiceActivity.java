package bus.driver.module.customerservice;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.module.setting.SuggestActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.utils.CommonUtils;

/**
 * Created by Lilaoda on 2017/9/26.
 * Email:749948218@qq.com
 * 客服中心
 */

public class CustomerServiceActivity extends BaseActivity {

    @BindView(R.id.text_call)
    TextView textCall;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_service);
        ButterKnife.bind(this);
        initToolbar("客服中心");
    }


    @OnClick({R.id.fl_call, R.id.text_suggest})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fl_call:
                CommonUtils.doCall(this, CommonUtils.getString(textCall));
                break;
            case R.id.text_suggest:
                startActivity(new Intent(this, SuggestActivity.class));
                break;
        }
    }
}
