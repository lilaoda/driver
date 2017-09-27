package bus.driver.module.login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.module.main.MainActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.StatusBarUtil;
import lhy.lhylibrary.utils.ToastUtils;
import lhy.lhylibrary.utils.ValidateUtils;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_pwd)
    EditText editPwd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
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
        if (TextUtils.equals(CommonUtils.getString(editPhone), "13922239152") && TextUtils.equals(CommonUtils.getString(editPwd), "123456")) {
            startActivity(new Intent(this, MainActivity.class));
        }else {
            ToastUtils.showString("账号13922239152+\n密码123456");
        }

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
