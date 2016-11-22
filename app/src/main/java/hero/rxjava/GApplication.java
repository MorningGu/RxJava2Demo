package hero.rxjava;

import android.app.Application;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

import com.bumptech.glide.Glide;
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader;
import com.bumptech.glide.load.model.GlideUrl;
import com.squareup.leakcanary.LeakCanary;

import java.io.InputStream;

import hero.rxjava.utils.LogUtils;
import okhttp3.OkHttpClient;


/**
 * Created by hero on 2016/4/29 0029.
 */
public class GApplication extends Application {

    private static GApplication sInstance; //s的前缀，表示static m的前缀表示member

    private boolean isDebug = false;

    public void onCreate() {
        super.onCreate();
        init();
    }
    private void init(){
        sInstance = this;
        initLeakCanary();
        initDebug();
        initGlide();

        //// FIXME: 2016/8/29 0029  当网络请求出错时会崩溃，之前测试404必崩 后来好了，原因不明
//        RxJavaPlugins.getInstance().registerErrorHandler(new RxJavaErrorHandler() {
//            @Override
//            public void handleError(Throwable e) {
//                LogUtils.e("rxJava Error",e.getMessage());
//            }
//        });
    }
    private void initLeakCanary(){
        //内存分析工具的初始化 针对1.5版本
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        LeakCanary.install(this);
    }
    private void initGlide(){
        Glide.get(this).register(GlideUrl.class,InputStream.class,new OkHttpUrlLoader.Factory(new OkHttpClient()));
    }
    /**
     * 初始化是否是debug
     */
    private void initDebug(){
        ApplicationInfo appInfo = null;
        try {
            appInfo = GApplication.getInstance().getPackageManager()
                    .getApplicationInfo(GApplication.getInstance().getPackageName(),
                            PackageManager.GET_META_DATA);
            isDebug =  appInfo.metaData.getBoolean("IS_DEBUG");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        LogUtils.isDebug(isDebug);
    }

    /**
     * 是不是debug
     * @return
     */
    public boolean isDebug(){
        return isDebug;
    }

    /**
     * 得到Application实例
     * @return
     */
    public static GApplication getInstance() {
        return sInstance;
    }

}
