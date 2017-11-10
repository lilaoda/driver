package bus.driver.module.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.data.DbManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.ToastUtils;

/**
 * 修改登陆密码第一步
 *
 */
public class ChangePwdActivity extends BaseActivity {

    @BindView(R.id.edit_oldpwd)
    EditText editOldpwd;
    @BindView(R.id.ll_first)
    LinearLayout llFirst;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_login_pwd);
        ButterKnife.bind(this);
        initToolbar("修改密码");
    }

    @OnClick({R.id.btn_first})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_first:
                if (checkOldPwd()) {
                    startActivity(new Intent(this, ChangePwd2Activity.class));
                    finish();
                }
                break;
        }
    }

    private boolean checkOldPwd() {
        String oldPwd = CommonUtils.getString(editOldpwd);
        if (TextUtils.isEmpty(oldPwd)) {
            ToastUtils.showString(getString(R.string.toast_empty_pwd));
            return false;
        }

        String password = DbManager.instance().getUser().getPassword();
        if (TextUtils.equals(oldPwd, password)) {
            return true;
        } else {
            ToastUtils.showString(getString(R.string.error_pwd));
            return false;
        }
    }
}
