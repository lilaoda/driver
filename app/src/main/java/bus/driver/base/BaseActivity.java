package bus.driver.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import bus.driver.R;
import lhy.lhylibrary.base.LhyActivity;
import lhy.lhylibrary.utils.StatusBarUtil;

/**
 * Created by Liheyu on 2017/9/20.
 * Email:liheyu999@163.com
 */

public class BaseActivity extends LhyActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    protected void initToolbar(String title) {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        TextView textTitle = (TextView) findViewById(R.id.toolbar_title);
        if (toolbar == null) {
            return;
        }
        toolbar.setTitle("");
        if (textTitle != null) {
            textTitle.setText(title);
        }
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void setStatusBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.app_color), 0);
//        StatusBarUtil.setColor(this, getResources().getColor(R.color.common_statusbar_color), 0);
    }

//    public void setOrderServiceEnable(boolean isEnable) {
//        Intent service = new Intent(this, OrderService.class);
//        if (isEnable) {
//            service.putExtra(OrderService.PULL_ORDER, OrderService.PULL_ORDER_START);
//        } else {
//            service.putExtra(OrderService.PULL_ORDER, OrderService.PULL_ORDER_END);
//        }
//        startService(service);
//    }
}
