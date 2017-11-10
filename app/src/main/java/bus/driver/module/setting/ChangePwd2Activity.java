package bus.driver.module.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.data.DbManager;
import bus.driver.data.HttpManager;
import bus.driver.data.local.entity.User;
import bus.driver.module.login.LoginActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.base.LhyApplication;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.ToastUtils;

import static bus.driver.utils.RxUtils.wrapHttp;

/**
 * 修改登陆密码第二步
 * Created by lilaoda on 2016/12/15.
 */
public class ChangePwd2Activity extends BaseActivity {

    @BindView(R.id.edit_newpwd1)
    EditText editNewpwd1;
    @BindView(R.id.edit_newpwd2)
    EditText editNewpwd2;
    private String mNewPwd;
    private DbManager mDbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_login_pwd2);
        ButterKnife.bind(this);
        initToolbar("修改密码");
    }


    @OnClick({R.id.btn_second})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_second:
                if (checkNewPwd()) {
                    doSubmit();
                }
                break;
        }
    }

    private void doSubmit() {
        mDbManager = DbManager.instance();
        final User mUser = mDbManager.getUser();
        String oldPwd = mUser.getPassword();
        wrapHttp(HttpManager.instance().getDriverService().changePwd(oldPwd, mNewPwd))
                .subscribe(new ResultObserver<String>(this, "正在加载...", true) {
                    @Override
                    public void onSuccess(String value) {
                        mUser.setPassword(mNewPwd);
                        mDbManager.updateUser(mUser);
                        LhyApplication application = (LhyApplication) getApplication();
                        application.finishOtherActivity(ChangePwd2Activity.this);
                        startActivity(new Intent(ChangePwd2Activity.this, LoginActivity.class));
                        finish();
                    }
                });
    }

    private boolean checkNewPwd() {
        mNewPwd = CommonUtils.getString(editNewpwd1);
        String newPwd2 = CommonUtils.getString(editNewpwd2);
        if (TextUtils.isEmpty(mNewPwd) || TextUtils.isEmpty(newPwd2)) {
            ToastUtils.showString(getString(R.string.toast_empty_pwd));
            return false;
        }
        if (!TextUtils.equals(mNewPwd, newPwd2)) {
            ToastUtils.showString(getString(R.string.toast_notsame_pwd));
            return false;
        }
        if (mNewPwd.length() < 6) {
            ToastUtils.showString(getString(R.string.pwd_too_small));
            return false;
        }
        if (mNewPwd.length() > 18) {
            ToastUtils.showString(getString(R.string.pwd_too_long));
            return false;
        }
        return true;
    }
}
