package bus.driver.module.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.data.DbManager;
import bus.driver.data.HttpManager;
import bus.driver.data.entity.User;
import bus.driver.module.main.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.StatusBarUtil;
import lhy.lhylibrary.utils.ToastUtils;
import lhy.lhylibrary.utils.ValidateUtils;

import static bus.driver.utils.RxUtils.wrapHttp;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_pwd)
    EditText editPwd;
    HttpManager httpManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        httpManager = HttpManager.instance();
    }

    @Override
    public void setStatusBar() {
        StatusBarUtil.setTransparentForImageView(this, null);
    }

    @OnClick({R.id.btn_login, R.id.text_forget, R.id.btn_register, R.id.ib_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.btn_login:
                editPhone.setText("13922239152");
                editPwd.setText("123456");
                if (checkData()) {
                    doSignin();
                }
                break;
            case R.id.text_forget:
                break;
            case R.id.btn_register:
                startActivity(new Intent(this, RegisterActivity.class));
                break;
        }
    }

    private void doSignin() {
        wrapHttp(httpManager.getApiService().login(0, CommonUtils.getString(editPhone), CommonUtils.getString(editPwd)))
                .compose(this.<String>bindToLifecycle())
                .subscribe(new ResultObserver<String>(this, "正在登陆", true) {
                    @Override
                    public void onSuccess(String value) {
                        User user = new User();
                        user.setPhone(CommonUtils.getString(editPhone));
                        user.setPassword(CommonUtils.getString(editPwd));
                        user.setToken(value);
                        DbManager.instance().saveUser(user);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    }
                });
    }

    private boolean checkData() {
        String phone = CommonUtils.getString(editPhone);
        String pwd = CommonUtils.getString(editPwd);
        if (checkMobile(phone) && checkPwd(pwd)) {
            return true;
        }
        return false;
    }

    private boolean checkPwd(String pwd) {
        if (pwd.isEmpty()) {
            ToastUtils.showString(getString(R.string.please_input_pwd));
            return false;
        }
        if (pwd.length() < 6) {
            ToastUtils.showString(getString(R.string.pwd_too_small));
            return false;
        }
        if (pwd.length() > 18) {
            ToastUtils.showString(getString(R.string.pwd_too_long));
            return false;
        }
        return true;
    }


    private boolean checkMobile(String phone) {
        if (phone.isEmpty()) {
            ToastUtils.showString(getString(R.string.please_input_phone));
            return false;
        }
        if (!ValidateUtils.isTelephone(phone)) {
            ToastUtils.showString(getString(R.string.phone_not_yes));
            return false;
        }
        return true;
    }

}
