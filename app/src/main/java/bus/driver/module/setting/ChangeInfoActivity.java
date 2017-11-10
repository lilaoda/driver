package bus.driver.module.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.data.DbManager;
import bus.driver.data.HttpManager;
import bus.driver.data.local.entity.User;
import bus.driver.utils.EventBusUtls;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.ToastUtils;

import static bus.driver.utils.RxUtils.wrapHttp;

/**
 * Created by Lilaoda on 2017/11/9.
 * Email:749948218@qq.com
 *
 * 修改手机号和姓名
 */

public class ChangeInfoActivity extends BaseActivity {

    @BindView(R.id.text_save)
    TextView textSave;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.edit)
    EditText edit;

    private DbManager mDbManager;
    private int mCurrentType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_nick_name);
        ButterKnife.bind(this);
        initTitle();
        mDbManager = DbManager.instance();
    }

    private void initTitle() {
        mCurrentType = getIntent().getIntExtra(MyInfoActivity.CHANGE_INFO, MyInfoActivity.CHANGE_NAME);
        if (mCurrentType == MyInfoActivity.CHANGE_NAME) {
            initToolbar("修改姓名");
        } else if (mCurrentType == MyInfoActivity.CHANGE_PHONE) {
            initToolbar("修改手机号");
        }
    }

    @OnClick({R.id.text_save})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_save:
                checkData();
                break;
        }
    }

    private void checkData() {
        String editContent = CommonUtils.getString(edit);
        if (TextUtils.isEmpty(editContent)) {
            ToastUtils.showString("内容不能为空");
            return;
        }
        switch (mCurrentType) {
            case MyInfoActivity.CHANGE_NAME:
                saveName();
                break;
            case MyInfoActivity.CHANGE_PHONE:
                savePhone();
                break;
        }
    }

    private void saveName() {
        final String name = CommonUtils.getString(edit);
        wrapHttp(HttpManager.instance().getDriverService().changeName(name))
                .subscribe(new ResultObserver<String>(this, "正在加载...", true) {
                    @Override
                    public void onSuccess(String value) {
                        User user = mDbManager.getUser();
                        user.setName(name);
                        mDbManager.updateUser(user);
                        EventBusUtls.notifyUserInfoChanged(user);
                        finish();
                    }
                });
    }

    private void savePhone() {
        final String name = CommonUtils.getString(edit);
        wrapHttp(HttpManager.instance().getDriverService().changePhone(name))
                .subscribe(new ResultObserver<String>(this, "正在加载...", true) {
                    @Override
                    public void onSuccess(String value) {
                        User user = mDbManager.getUser();
                        user.setPhone(name);
                        mDbManager.updateUser(user);
                        EventBusUtls.notifyUserInfoChanged(user);
                        finish();
                    }
                });
    }
}
