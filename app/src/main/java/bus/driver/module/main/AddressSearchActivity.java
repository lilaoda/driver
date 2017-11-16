package bus.driver.module.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chad.library.adapter.base.BaseQuickAdapter;

import java.util.List;

import bus.driver.R;
import bus.driver.adapter.SearchAdapter;
import bus.driver.bean.PoiInfo;
import bus.driver.data.AMapManager;
import bus.driver.data.SpManager;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import lhy.lhylibrary.base.LhyActivity;
import lhy.lhylibrary.http.ResultObserver;
import lhy.lhylibrary.utils.CommonUtils;
import lhy.lhylibrary.view.SideLetterBar;

import static bus.driver.utils.RxUtils.wrapAsync;

/**
 * Created by Liheyu on 2017/9/18.
 * Email:liheyu999@163.com
 */

public class AddressSearchActivity extends LhyActivity {

    public static final String RESULT_ADDRESS = "result_address";

    @BindView(R.id.text_city)
    TextView textCity;
    @BindView(R.id.edit_input)
    EditText editInput;
    @BindView(R.id.ibt_clear)
    ImageButton ibtClear;
    @BindView(R.id.Ll_loading)
    LinearLayout LlLoading;
    @BindView(R.id.text_error)
    TextView textError;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.letterBar)
    SideLetterBar letterBar;

    List<PoiInfo> mHistoryList;
    private SearchAdapter mAdapter;
    private SpManager mSpManager;
    private AMapManager mAMapManager;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_address);
        ButterKnife.bind(this);
        mSpManager = SpManager.instance();
        mAMapManager = AMapManager.instance();
        initView();
        intListener();
    }

    private void intListener() {
        editInput.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    ibtClear.setVisibility(View.VISIBLE);
                    queryAddress(s.toString().trim());
                } else {
                    ibtClear.setVisibility(View.GONE);
                    mAdapter.setNewData(mHistoryList);
                }
            }
        });
    }

    private void initView() {
        LlLoading.setVisibility(View.GONE);
        textError.setVisibility(View.GONE);
        letterBar.setVisibility(View.GONE);

        mHistoryList = mSpManager.getHistoryAddress();
        mAdapter = new SearchAdapter(R.layout.item_search_address, mHistoryList);
        View headView = View.inflate(this, R.layout.item_search_address_head, null);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        recyclerView.setAdapter(mAdapter);
        mAdapter.addHeaderView(headView);
        mAdapter.setEmptyView(R.layout.item_search_address_empty, recyclerView);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                PoiInfo poiInfo = mAdapter.getData().get(position);
                saveSearchHistory(poiInfo);
                setAddressResult(poiInfo);
            }
        });
    }

    /**
     * 保存历史搜索记录
     */
    private void saveSearchHistory(PoiInfo poiInfo) {
        List<PoiInfo> poiInfos = mSpManager.getHistoryAddress();
        boolean flag = false;
        for (PoiInfo info : poiInfos) {
            if (info.getLongitude() == poiInfo.getLongitude() && info.getLatitude() == poiInfo.getLatitude()) {
                flag = true;
            }
        }
        if (!flag) {
            poiInfos.add(0, poiInfo);
            if (poiInfos.size() == 11) {
                poiInfos.remove(10);
            }
        }
        mSpManager.putHistoryAddress(poiInfos);
    }

    private void setAddressResult(PoiInfo poiInfo) {
        Intent intent = new Intent();
        intent.putExtra(RESULT_ADDRESS, poiInfo);
        setResult(RESULT_OK, intent);
        finish();
    }

    @OnClick({R.id.text_cancel, R.id.ibt_clear, R.id.text_city})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.text_cancel:
                setResult(RESULT_CANCELED);
                finish();
                break;
            case R.id.ibt_clear:
                editInput.setText("");
                break;
            case R.id.text_city:
                break;
        }
    }

    private void queryAddress(String keyWord) {
        LlLoading.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        wrapAsync(mAMapManager.search(keyWord, "", "广州")).subscribe(new ResultObserver<List<PoiInfo>>() {
            @Override
            public void onSuccess(List<PoiInfo> value) {
                LlLoading.setVisibility(View.GONE);
                recyclerView.setVisibility(View.VISIBLE);
                if (!TextUtils.isEmpty(CommonUtils.getString(editInput))) {
                    //如果用户操作过快，输入框为空时应该显示历史内容，而此时如果网络较慢，则结果会覆盖掉历史，所以加此判断
                    mAdapter.setNewData(value);
                }
            }

            @Override
            public void onError(Throwable e) {
                super.onError(e);
                LlLoading.setVisibility(View.GONE);
                textError.setVisibility(View.VISIBLE);
            }
        });
    }

}
