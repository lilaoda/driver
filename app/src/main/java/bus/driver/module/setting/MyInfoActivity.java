package bus.driver.module.setting;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.TextView;

import com.orhanobut.logger.Logger;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import bus.driver.data.DbManager;
import bus.driver.data.HttpManager;
import bus.driver.data.local.entity.User;
import bus.driver.data.remote.DriverApi;
import bus.driver.utils.EventBusUtls;
import bus.driver.utils.GlideUtil;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.view.roundImageView.CircleImageView;
import me.nereo.multi_image_selector.MultiImageSelector;
import me.nereo.multi_image_selector.MultiImageSelectorActivity;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;

import static bus.driver.utils.RxUtils.wrapHttp;
import static okhttp3.RequestBody.create;

/**
 * 个人信息页面 个人信息的展现及修改
 * Created by Administrator on 2016/9/20.
 */
public class MyInfoActivity extends BaseActivity {

    private static final int CODE_REQUEST_IMG = 10;
    public static final int CHANGE_NAME = 5;
    public static final int CHANGE_PHONE = 6;
    public static final String CHANGE_INFO = "change_info";

    @BindView(R.id.civ_icon)
    CircleImageView civIcon;
    @BindView(R.id.text_nickname)
    TextView textNickname;
    @BindView(R.id.text_email)
    TextView textEmail;
    @BindView(R.id.text_pwd)
    TextView textPwd;
    @BindView(R.id.text_phone)
    TextView textPhone;

    private AlertDialog mPhotoDialog;
    private ArrayList<String> mImgList;
    private User mUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        initToolbar("个人信息");
        mUser = DbManager.instance().getUser();
        initView();
    }

    private void initView() {
        GlideUtil.instance().loadUserIcon(civIcon, DriverApi.imgBaseUrl + mUser.getIconUrl());
        textNickname.setText(mUser.getName());
        textPhone.setText(mUser.getPhone());
    }

    @OnClick({R.id.rl_icon, R.id.ll_phone, R.id.ll_nickName, R.id.ll_email, R.id.text_pwd})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_icon:
                showPhotoDiolog();
                break;
            case R.id.ll_nickName:
                startActivity(new Intent(this, ChangeInfoActivity.class).putExtra(CHANGE_INFO, CHANGE_NAME));
                break;
            case R.id.ll_phone:
                startActivity(new Intent(this, ChangeInfoActivity.class).putExtra(CHANGE_INFO, CHANGE_PHONE));
                break;
            case R.id.ll_email:
                break;
            case R.id.text_pwd:
                startActivity(new Intent(this, ChangePwdActivity.class));
                break;
        }
    }

    /**
     * 更换头像对话框 包含相册和拍照
     */
    private void showPhotoDiolog() {
        if (mPhotoDialog == null) {
            String[] items = new String[]{"打开相册"};
            mPhotoDialog = new AlertDialog.Builder(this)
                    .setItems(items, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            openAlbum();
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create();
        }
        mPhotoDialog.show();
    }

    private void openAlbum() {
        mImgList = new ArrayList<>();
        MultiImageSelector.create().count(1)
                .showCamera(true)
                .origin(mImgList)
                .start(this, CODE_REQUEST_IMG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CODE_REQUEST_IMG) {
                List<String> paths = data.getStringArrayListExtra(MultiImageSelectorActivity.EXTRA_RESULT);
                mImgList.addAll(paths);
                uploadUserIcon(paths.get(0));
            }
        }
    }

    private void uploadUserIcon(String imgPath) {
        wrapHttp(HttpManager.instance().getDriverService().uploadUserIcon(getMultiBody(imgPath)))
                .compose(this.<String>bindToLifecycle())
                .subscribe(new ResultObserver<String>(this, "正在上传...", true) {
                    @Override
                    public void onSuccess(String value) {
                        mUser.setIconUrl(value);
                        DbManager.instance().updateUser(mUser);
                        EventBusUtls.notifyUserInfoChanged(mUser);
                    }
                });
    }

    public MultipartBody getMultiBody(String imgPath) {
        File file = new File(imgPath);
        Logger.d(file.length());
        Logger.d(file.getName());
        RequestBody requestBody = create(MediaType.parse("multipart/form-data"), file);
        MultipartBody.Part part = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
        return new MultipartBody.Builder().addPart(part).build();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessage(User user) {
        mUser = user;
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
