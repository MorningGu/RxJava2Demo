package hero.rxjava.retrofit;


import java.util.Map;

import hero.rxjava.mvp.model.JsonResult;
import hero.rxjava.mvp.model.Version;
import hero.rxjava.mvp.model.user.UserData;
import io.reactivex.Flowable;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Streaming;
import retrofit2.http.Url;


/**
 * @Description:  网络请求接口
 */
public interface INetInterface {

    /**
     * 上传
     * @param file 源文件
     * @param type 文件类型
     * @param mobileType  设备类型
     * @return 请求结果
     */
    @Multipart
    @POST("mobile/picture/upload.do")
    Flowable<JsonResult<Map<String, String>>> postUpload(@Part("file\"; filename=\"image.jpg") RequestBody file, @Part("type") String type, @Part("mobileType") String mobileType);

    /**
     * 检查更新
     * @return
     */
    @GET("mobile/version/getVersion.do")
    Flowable<JsonResult<Version>> postCheckUpdate();

    /**
     * 登录
     * @param phoneNum 电话号码
     * @param password 密码
     * @return 请求结果
     */
    @FormUrlEncoded
    @POST("mobile/login/authentication/login.do")
    Flowable<JsonResult<UserData>> postLogin(@Field("mobile") String phoneNum, @Field("password") String password);

    /**
     * 下载apk文件
     * @param url
     * @return
     */
    @Streaming
    @GET
    Call<ResponseBody> downloadAPK (@Url String url);

}
