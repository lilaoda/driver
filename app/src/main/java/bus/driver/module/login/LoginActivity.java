package bus.driver.module.login;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;

import com.orhanobut.logger.Logger;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.bean.DriverInfo;
import bus.driver.data.DbManager;
import bus.driver.data.HttpManager;
import bus.driver.data.local.entity.User;
import bus.driver.data.remote.DriverApi;
import bus.driver.data.remote.HttpResult;
import bus.driver.module.main.MainActivity;
import bus.driver.utils.GlideUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.http.exception.ApiException;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.StatusBarUtil;
import lhy.lhylibrary.utils.ToastUtils;
import lhy.lhylibrary.utils.ValidateUtils;
import lhy.lhylibrary.view.roundImageView.RoundedImageView;

import static bus.driver.utils.RxUtils.wrapHttp;


public class LoginActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_pwd)
    EditText editPwd;
    @BindView(R.id.riv)
    RoundedImageView riv;

    private HttpManager mHttpManager;
    private DbManager mDbManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        mHttpManager = HttpManager.instance();
        mDbManager = DbManager.instance();
    }

    private void initView() {
        User user = DbManager.instance().getUser();
        if (user != null) {
            editPhone.setText(user.getPhone());
            editPwd.setText(user.getPassword());
            GlideUtil.instance().loadUserIcon(riv, DriverApi.imgBaseUrl + user.getIconUrl());
        }
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
//                showUpdateDialog();
                if (checkData()) {
                    User user = mDbManager.getUser();
                    if (user != null) {
                        user.setToken("");
                        mDbManager.updateUser(user);
                    }
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
        Observable<HttpResult<String>> login = mHttpManager.getDriverService().login(0, CommonUtils.getString(editPhone), CommonUtils.getString(editPwd));
        final Observable<HttpResult<DriverInfo>> driverInfo = mHttpManager.getDriverService().getDriverInfo();
        wrapHttp(login.flatMap(new Function<HttpResult<String>, ObservableSource<HttpResult<DriverInfo>>>() {
            @Override
            public ObservableSource<HttpResult<DriverInfo>> apply(@NonNull HttpResult<String> stringHttpResult) throws Exception {
                Logger.d("apply");
                if (stringHttpResult.isResult()) {
                    User user = new User();
                    user.setPhone(CommonUtils.getString(editPhone));
                    user.setPassword(CommonUtils.getString(editPwd));
                    user.setToken(stringHttpResult.getData());
                    mDbManager.saveUser(user);
                } else {
                    throw new ApiException(stringHttpResult.getMessage());
                }
                return driverInfo;
            }
        }))
                .compose(this.<DriverInfo>bindToLifecycle())
                .subscribe(new ResultObserver<DriverInfo>(this, "正在登陆...", true) {
                    @Override
                    public void onSuccess(DriverInfo value) {
                        User user = mDbManager.getUser();
                        if (user != null) {
                            user.setUuid(value.getUser().getUuid());
                            user.setIconUrl(value.getUser().getFace());
                            if (value.getDriver() != null) {
                                user.setName(value.getDriver().getName());
                            }
                        }
                        mDbManager.updateUser(user);
                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
                        finish();
                    }
                });
    }

//    //获取个人信息接口
//    private void getDriverInfo() {
//        Observable<HttpResult<DriverInfo>> driverInfo = mHttpManager.getDriverService().getDriverInfo();
//        wrapHttp(driverInfo)
//                .compose(this.<DriverInfo>bindToLifecycle())
//                .subscribe(new ResultObserver<DriverInfo>(this, "正在加载...", true) {
//                    @Override
//                    public void onSuccess(DriverInfo value) {
//                        mDbManager.getUser().setUuid(value.getUser().getUuid());
//                        startActivity(new Intent(LoginActivity.this, MainActivity.class));
//                        finish();
//                    }
//                });
//    }

    private boolean checkData() {
        String phone = CommonUtils.getString(editPhone);
        String pwd = CommonUtils.getString(editPwd);
        return checkMobile(phone) && checkPwd(pwd);
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

    private void showUpdateDialog() {
        ProgressDialog progressDialog = new ProgressDialog(this, ProgressDialog.STYLE_SPINNER);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();
    }

}
