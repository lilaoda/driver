package bus.driver.adapter;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

import bus.driver.R;
import bus.driver.bean.PoiInfo;


/**
 * Created by Liheyu on 2017/9/19.
 * Email:liheyu999@163.com
 * 地址搜索页面的适配器
 */

public class SearchAdapter extends BaseQuickAdapter<PoiInfo, BaseViewHolder> {

    public SearchAdapter(@LayoutRes int layoutResId, @Nullable List<PoiInfo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, PoiInfo item) {
        helper.setText(R.id.item_title, item.getTitle());
        helper.setText(R.id.item_address, item.getCityName() + item.getAdName() + item.getSnippet());
    }

    @Override
    public int addHeaderView(View header) {
        return super.addHeaderView(header);
    }

}
