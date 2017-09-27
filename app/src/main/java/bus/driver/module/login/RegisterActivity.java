package bus.driver.module.login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Timer;
import java.util.TimerTask;

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


public class RegisterActivity extends BaseActivity implements View.OnClickListener {

    public static final int ZERO_TIME = 0;
    public static final int LONG_DELAY = 1000;//每次推迟1秒执行

    @BindView(R.id.edit_phone)
    EditText editPhone;
    @BindView(R.id.edit_code)
    EditText editCode;
    @BindView(R.id.identify_code)
    TextView identifyCode;

    private int mExtraTime = 60;
    private Timer mTimer;
    private TimerTask mTimeTask;

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

    private void startActivity() {
    }

    /**
     * 发送验证码
     */
    private void sendCode() {
        ToastUtils.showString("发送验证码");
//        UserService service = RetrofitUtils.getService();
//        Call<JsonObject> call = service.sendCode(mTelephone);
//        call.enqueue(new Callback<JsonObject>() {
//            @Override
//            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                JsonObject body = response.body();
//                int status = body.get("status").getAsInt();
//                if (status == 1) {
//                    beginTimer();
//                    identifyCode.setClickable(false);
//                    identifyCode.setEnabled(false);
//                    ToastUtils.showString(getApplicationContext(), "验证码已发送到" + mTelephone);
//                } else {
//                    String info = body.get("info").getAsString();
//                    ToastUtils.showString(getApplicationContext(), info);
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<JsonObject> call, Throwable t) {
//                ToastUtils.showString(getApplicationContext(), getString(R.string.code_send_failure));
//            }
//        });
    }

    /**
     * 下一步，同时提交手机号和验证码成功后跳到输入密码页面
     */
    private void doNext() {
        ToastUtils.showString("OK");
        startActivity(new Intent(this, MainActivity.class));
//        showLoadingDialog();
//        UserService service = RetrofitUtils.getService();
//        Call<HttpResult> call = service.checkCode(mTelephone, mCode);
//        call.enqueue(new Callback<HttpResult>() {
//            @Override
//            public void onResponse(Call<HttpResult> call, Response<HttpResult> response) {
//                dismissLoadingDialog();
//
//                HttpResult body = response.body();
//                String info = body.info;
//                int status = body.status;
//
//                if (status == 1) {
//                    getAccount().setTelephone(mTelephone);
//                    startActivity();
//                } else {
//                    ToastUtils.showString(getApplicationContext(), info);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<HttpResult> call, Throwable t) {
//                dismissLoadingDialog();
//                ToastUtils.showNetError(getApplicationContext());
//            }
//        });
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
                if (checkPhone() && checkCode()) {
                    doNext();
                }
                break;
        }
    }


    /**
     * 开启定时器
     */
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


//        subscribe = Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> e) throws Exception {
//                e.onNext(20);
//                for (int i = 19; i > 0; i--) {
//                    Thread.sleep(1000);
//                    e.onNext(i);
//                }
//                e.onComplete();
//            }
//        })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Integer>() {
//                    @Override
//                    public void accept(Integer integer) throws Exception {
//                        identifyCode.setText(integer + "s");
//                        if (integer == 1) {
//                            mExtraTime = 20;
//                            identifyCode.setClickable(true);
//                            identifyCode.setEnabled(true);
//                            identifyCode.setText(getString(R.string.get_identify_code));
//                        }
//                    }
//                });

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
