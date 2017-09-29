package bus.driver.module.login;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.Timer;
import java.util.TimerTask;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.data.HttpManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.StatusBarUtil;
import lhy.lhylibrary.utils.ToastUtils;
import lhy.lhylibrary.utils.ValidateUtils;

import static bus.driver.utils.RxUtils.wrapHttp;


public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    public static final int ZERO_TIME = 0;
    public static final int LONG_DELAY = 1000;//每次推迟1秒执行

    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_code)
    EditText editCode;
    @BindView(R.id.identify_code)
    TextView identifyCode;
    @BindView(R.id.edit_pwd)
    EditText editPwd;

    private int mExtraTime = 60;
    private Timer mTimer;
    private TimerTask mTimeTask;

    HttpManager httpManager;
    //更改验证码剩余秒数
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            identifyCode.setText(--mExtraTime + "s");
            if (mExtraTime == ZERO_TIME) {
                mExtraTime = 60;
                mTimer.cancel();
                mTimer.purge();
                mTimeTask = null;
                mTimer = null;
                mHandler.removeCallbacksAndMessages(null);
                identifyCode.setClickable(true);
                identifyCode.setEnabled(true);
                identifyCode.setText(getString(R.string.get_identify_code));
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        httpManager = HttpManager.instance();
    }

    @Override
    public void setStatusBar() {
        StatusBarUtil.setTransparentForImageView(this, null);
    }


    private boolean checkPhone() {
        String phone = CommonUtils.getString(editPhone);
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

    private boolean checkPwd() {
        String pwd = CommonUtils.getString(editPwd);
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

    private boolean checkCode() {
        String code = editCode.getText().toString().trim();
        if (TextUtils.isEmpty(code)) {
            ToastUtils.showString(getString(R.string.please_input_code));
            return false;
        }
        if (code.length() != 4) {
            ToastUtils.showString(getString(R.string.input_four_code));
            return false;
        }
        return true;
    }

    private void sendCode() {
        ToastUtils.showString("发送验证码");
    }


    private void doNext() {
        wrapHttp(httpManager.getApiService().regist(0, CommonUtils.getString(editPhone), CommonUtils.getString(editPwd)))
                .compose(this.<String>bindToLifecycle())
                .subscribe(new ResultObserver<String>(this, "正在注册", true) {
                    @Override
                    public void onSuccess(String value) {
                        Logger.d(value);
                    }
                });
    }

    @OnClick({R.id.identify_code, R.id.btn_next, R.id.ib_back})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ib_back:
                finish();
                break;
            case R.id.identify_code:
                if (checkPhone()) {
                    sendCode();
                }
                break;
            case R.id.btn_next:
                if (checkPhone() && checkPwd()) {
                    doNext();
                }
                break;
        }
    }

    private void beginTimer() {
        if (mTimer == null) {
            mTimer = new Timer();
        }

        if (mTimeTask == null) {
            mTimeTask = new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessageDelayed(ZERO_TIME, ZERO_TIME);
                }
            };
        }
        mTimer.schedule(mTimeTask, ZERO_TIME, LONG_DELAY);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimeTask = null;
            mTimer = null;
        }

        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
