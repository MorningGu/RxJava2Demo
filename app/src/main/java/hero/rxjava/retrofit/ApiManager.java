package hero.rxjava.retrofit;


import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;

import org.reactivestreams.Subscription;

import java.util.concurrent.TimeUnit;

import hero.rxjava.GApplication;
import hero.rxjava.utils.LogUtils;
import io.reactivex.Flowable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.LongConsumer;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.ResourceSubscriber;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hero on 2016/9/6 0006.
 */

public enum ApiManager {
    INSTANCE;
    private INetInterface iNetInterface;
    private Object monitor = new Object();
    /**
     * 这一部分配置常量，可以抽取出常量类
     */
    private static final long DEFAULT_TIMEOUT = 10000;//默认超时时间(毫秒)

    /**
     * 取得实例化的Retrofit
     * 可以根据不同的需求获取不同的Retrofit实例
     * @return
     */
    public INetInterface getINetInterface(){
        if (iNetInterface == null) {
            synchronized (monitor) {
                if (iNetInterface == null) {
                    //打印拦截器
                    HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
                    logging.setLevel(GApplication.getInstance().isDebug()?HttpLoggingInterceptor.Level.BODY:HttpLoggingInterceptor.Level.NONE);
                    // 公私密匙
                    //MarvelSigningInterceptor signingInterceptor = new MarvelSigningInterceptor(KeyValue.MARVEL_PUBLIC_KEY, KeyValue.MARVEL_PRIVATE_KEY);
                    OkHttpClient.Builder okHttpClient = new OkHttpClient.Builder();
                    okHttpClient.addNetworkInterceptor(new HTTPInterceptor())
                            .retryOnConnectionFailure(true)//设置出现错误进行重新连接。;
                            //.addInterceptor(signingInterceptor)//加密处理
                            .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.MILLISECONDS)
                            .readTimeout(DEFAULT_TIMEOUT,TimeUnit.MILLISECONDS)
                            .addNetworkInterceptor(logging);

                    iNetInterface = new Retrofit.Builder()
                            .client(okHttpClient.build())
                            .addConverterFactory(GsonConverterFactory.create())
                            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                            .baseUrl("url")
                            .build().create(INetInterface.class);
                }
            }
        }
        return iNetInterface;
    }
    /**
     * 初始化通用的观察者
     * @param observable 观察者
     */
    public ResourceSubscriber startObservable(Flowable observable, ResourceSubscriber subscriber) {
       return (ResourceSubscriber)observable.subscribeOn(Schedulers.io())
               .observeOn(Schedulers.io())
               .doOnLifecycle(new Consumer<Subscription>() {
                   @Override
                   public void accept(Subscription subscription) throws Exception {
                       LogUtils.d("doOnLifecycle","OnSubscribe");
                   }
               }, new LongConsumer() {
                   @Override
                   public void accept(long t) throws Exception {
                       LogUtils.d("doOnLifecycle","OnRequest");
                   }
               }, new Action() {
                   @Override
                   public void run() throws Exception {
                       LogUtils.d("doOnLifecycle","OnCancel");
                   }
               })
               .subscribeWith(subscriber);
    }
}
