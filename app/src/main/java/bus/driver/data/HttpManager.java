package bus.driver.data;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import lhy.lhylibrary.http.OkhttpManager;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

@Singleton
public class HttpManager {

    private Retrofit mRetrofit;

    @Inject
    public HttpManager() {
        mRetrofit = new Retrofit.Builder()
                .baseUrl(ApiService.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(OkhttpManager.getInstance().getCacheOKhttp())
                .build();
    }


    public ApiService getApiService() {
        return mRetrofit.create(ApiService.class);
    }

    //提交单文件表单示例
    public void oneFormFileSample(String imgPath) {
        File file = new File(imgPath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("application/otcet-stream"), file);
        MultipartBody.Part body = MultipartBody.Part.createFormData("aFile", file.getName(), requestFile);
        String descriptionString = "This is a description";
        RequestBody description = RequestBody.create(MediaType.parse("multipart/form-data"), descriptionString);
    }


    public MultipartBody getMultipartBody(List<String> imgPaths) {
        final MultipartBody.Builder builder = new MultipartBody.Builder();
        if (imgPaths != null && imgPaths.size() > 0) {
            for (int i = 0; i < imgPaths.size(); i++) {
                File file = new File(imgPaths.get(i));
                RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
//            RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
                String des = "photo" + i + 1;
                builder.addFormDataPart(des, file.getName(), requestBody);
            }
        }
        builder.setType(MultipartBody.FORM);
        return builder.build();
    }


    public List<MultipartBody.Part> getMultipartBodyPartList(List<String> imgPaths) {
        List<MultipartBody.Part> list = new ArrayList<>();
        if (imgPaths != null && imgPaths.size() > 0) {
            for (int i = 0; i < imgPaths.size(); i++) {
                File file = new File(imgPaths.get(i));
                RequestBody requestBody = RequestBody.create(MultipartBody.FORM, file);
//            RequestBody requestBody = RequestBody.create(MediaType.parse("image/png"), file);
                String des = "aFile";
                MultipartBody.Part part = MultipartBody.Part.createFormData(des, file.getName(), requestBody);
                list.add(part);
            }
        }
        return list;
    }
}
