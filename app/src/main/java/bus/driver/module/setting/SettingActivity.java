package bus.driver.module.setting;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import java.util.concurrent.TimeUnit;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.base.BaseApplication;
import bus.driver.base.Constants;
import bus.driver.data.DbManager;
import bus.driver.module.login.LoginActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.annotations.NonNull;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.FileUtils;
import lhy.lhylibrary.utils.GlideCacheUtils;
import lhy.lhylibrary.utils.ToastUtils;

import static bus.driver.utils.RxUtils.wrapAsync;

/**
 * 设置
 */
public class SettingActivity extends BaseActivity {

    @BindView(R.id.text_clean)
    TextView textClean;
    @BindView(R.id.fl_msg)
    FrameLayout flMsg;
    @BindView(R.id.text_version)
    TextView textVersion;
    @BindView(R.id.switch_msg)
    Switch switchMsg;

    DbManager mDbManager;
    @BindView(R.id.text_call)
    TextView textCall;

    private AlertDialog mUpdateDialog;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        ButterKnife.bind(this);
        mDbManager = DbManager.instance();
        initToolbar("设置");
        initView();
    }

    /**
     * 初始化VIEW,初始化缓存大小和版本号
     */
    private void initView() {
        String size = FileUtils.getFormatSize(getCacheSize());
        textClean.setText(size);
        textVersion.setText(getVersionName());
    }

    /**
     * 退出登陆，清除账号信息
     */
    private void closeApplication() {
        mDbManager.clearUserInfo();
        BaseApplication application = (BaseApplication) getApplication();
        application.closeApplication();
        startActivity(new Intent(this, LoginActivity.class));
    }

    private void cleanCache() {
        wrapAsync(Observable.timer(1, TimeUnit.SECONDS)).compose(this.<Long>bindToLifecycle())
                .subscribe(new ResultObserver<Long>(this, "正在清理缓存") {
                    @Override
                    public void onSuccess(Long value) {
                        textClean.setText("0kb");
                        Logger.d("清理成功!");
                        ToastUtils.showString("清理成功!");
                    }
                });
        wrapAsync(Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(@NonNull ObservableEmitter<String> e) throws Exception {
                doCleanCache();
                e.onNext("");
                e.onComplete();
            }
        })).subscribe();
    }

    /**
     * 清理缓存(图片缓存+文件缓存)
     */
    private void doCleanCache() {
        GlideCacheUtils.getInstance().clearAllImgCache(this);
        FileUtils.deleteFolderFile(FileUtils.getExtraCacheFile(this, Constants.CACHE_FILE_NAME).getAbsolutePath(), false);
        FileUtils.deleteFolderFile(FileUtils.getInternalCacheFile(this, Constants.CACHE_FILE_NAME).getAbsolutePath(), false);
    }

    /**
     * 得到缓存大小,包括文件缓存和图片缓存
     *
     * @return
     */
    private long getCacheSize() {
        return FileUtils.getFolderSize(FileUtils.getInternalCacheFile(this, Constants.CACHE_FILE_NAME)) +
                FileUtils.getFolderSize(FileUtils.getExtraCacheFile(this, Constants.CACHE_FILE_NAME)) +
                GlideCacheUtils.getInstance().getGlideCacheSize(this);
    }

    @OnClick({R.id.fl_msg, R.id.fl_clean, R.id.text_us, R.id.text_suggest, R.id.fl_update, R.id.fl_call, R.id.btn_over})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fl_msg:
                doSwitchMsgSetting();
                break;
            case R.id.fl_clean:
                cleanCache();
                break;
            case R.id.text_us:
                ToastUtils.showString("关于我们");
                break;
            case R.id.text_suggest:
                startActivity(new Intent(this, SuggestActivity.class));
                break;
            case R.id.fl_update:
                doUpdata();
                break;
            case R.id.fl_call:
                doCall();
                break;
            case R.id.btn_over:
                closeApplication();
                break;
        }
    }

    private void showLoadingDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
        }
        mProgressDialog.show();
    }

    /**
     * 消息通知设置
     */
    private void doSwitchMsgSetting() {
        switchMsg.setChecked(!switchMsg.isChecked());
        //TODO 保存消息通知设置结果到本地
    }

    /**
     * 更新APK
     */
    private void doUpdata() {
        if (mUpdateDialog == null) {
            mUpdateDialog = new AlertDialog.Builder(this).setTitle("发现新版本")
                    .setMessage("增加了很多功能，快来体验吧")
                    .setPositiveButton("立即体验", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ToastUtils.showString("更新完毕");
                        }
                    })
                    .setNegativeButton("暂不更新", null)
                    .create();
        }
        mUpdateDialog.show();
    }

    /**
     * 打电话
     */
    private void doCall() {
        Intent intent = new Intent(Intent.ACTION_DIAL);
        intent.setData(Uri.parse("tel:" + CommonUtils.getString(textCall)));
        startActivity(intent);
    }

    /**
     * 得到版本名
     *
     * @return
     */
    private String getVersionName() {
        PackageManager pm = getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(getPackageName(), 0);
            String versionName = packageInfo.versionName;
            return versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mUpdateDialog != null) {
            mUpdateDialog.dismiss();
        }
    }
}
