package bus.driver.module.setting;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import bus.driver.R;
import bus.driver.base.BaseActivity;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.utils.ToastUtils;

/**
 * 反馈建议
 */
public class SuggestActivity extends BaseActivity {

    @BindView(R.id.edit_suggest)
    EditText editSuggest;
    @BindView(R.id.text_watch)
    TextView textWatch;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_suggest);
        ButterKnife.bind(this);
        initToolbar("建议");
        initListener();
    }

    private void initListener() {
        editSuggest.addTextChangedListener(new TextWatcher() {
            private int selectionEnd;
            private int selectionStart;

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {


            }

            @Override
            public void afterTextChanged(Editable s) {
                selectionStart = editSuggest.getSelectionStart();
                selectionEnd = editSuggest.getSelectionEnd();
                Log.e("selectionStart", "" + selectionStart);
                Log.e("selectionEnd", "" + selectionEnd);
                if (s.length() <= 200) {
                    textWatch.setText(s.length() + " /200字");
                } else {
                    ToastUtils.showString("您输入的字数已达上线");
                    s.delete(selectionStart - 1, selectionEnd);
                }
            }
        });
    }

    private void doSubmit() {
        String content = CommonUtils.getString(editSuggest);
        if (TextUtils.isEmpty(content)) {
            ToastUtils.showString("内容不能为空");
        } else {
            ToastUtils.showString("提交成功");
            finish();
        }
    }

    @OnClick(R.id.btn_commit)
    public void onViewClicked() {
        doSubmit();
    }
}
