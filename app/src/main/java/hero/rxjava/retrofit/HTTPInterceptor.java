package hero.rxjava.retrofit;


import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

/**
 * @Description: 定义http拦截器，用于设置http协议和日志调试
 */
public class HTTPInterceptor implements Interceptor {
    private static String TAG = "HTTP-Interceptor";

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request request = chain.request();
//                    if (false) {//"token" == null
//                        request.newBuilder()
//                                .header("platform", "Android")
//                                .header("version", "2.2")
//                                .build();
//                        return chain.proceed(request);
//                    }
        Request authorised = request
                .newBuilder()
                .header("registeredChannels", "2")//来自1：iOS,2:Android,3:web
                .build();
        return chain.proceed(authorised);
    }
}
