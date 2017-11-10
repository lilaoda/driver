package bus.driver.utils;

import android.widget.ImageView;

import com.bumptech.glide.load.model.GlideUrl;
import com.bumptech.glide.load.model.LazyHeaders;

import bus.driver.R;
import bus.driver.data.DbManager;
import lhy.lhylibrary.base.GlideApp;

/**
 * Created by Lilaoda on 2017/11/9.
 * Email:749948218@qq.com
 */

public class GlideUtil {

    private final LazyHeaders tokenHeader;
    public static GlideUtil instance;

    private GlideUtil() {
        String token = DbManager.instance().getUser().getToken();
        tokenHeader = new LazyHeaders.Builder().addHeader("token", token).build();
    }

    public static GlideUtil instance() {
        if (instance == null) {
            instance = new GlideUtil();
        }
        return instance;
    }

    public void loadUserIcon(ImageView view, String url) {
        GlideApp.with(view).load(new GlideUrl(url, tokenHeader)).placeholder(R.mipmap.icon_user_default).error(R.mipmap.icon_user_default).into(view);
//        Glide.with(context)
//                .load(url)
//                .apply(new RequestOptions().placeholder(R.mipmap.icon_user_default)
//                        .error(R.mipmap.icon_user_default))
//                .into(view);
    }
}
