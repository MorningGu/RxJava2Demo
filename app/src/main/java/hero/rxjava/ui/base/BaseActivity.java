package hero.rxjava.ui.base;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

import java.lang.ref.WeakReference;

import hero.rxjava.mvp.iview.IBaseView;
import hero.rxjava.mvp.presenter.BasePresenter;
import hero.rxjava.utils.AppManager;
import hero.rxjava.utils.LogUtils;

/**
 * Created by hero on 2016/11/3 0003.
 */

public abstract class BaseActivity<V,T extends BasePresenter<V>> extends AppCompatActivity implements IBaseView{
    public int mScreenWidth;
    public int mScreenHeight;
    protected T mPresenter;
    private WeakReference<Activity> mWeakReference;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mWeakReference = new WeakReference<Activity>(this);
        AppManager.INSTANCE.addActivity(mWeakReference);
        initPresenter();
        initScreenData();
    }
    private void initPresenter(){
        //初始化Presenter
        mPresenter = createPresenter();
        //presenter与View绑定
        if(null != mPresenter){
            mPresenter.attachView((V)this);
        }
    }
    /**
     * 创建presenter
     * @return
     */
    protected abstract T createPresenter();
    @Override
    protected void onDestroy() {
        detachPresenter();
        AppManager.INSTANCE.removeTask(mWeakReference);
        LogUtils.d(getClass().getName(),"---------->销毁了");
        super.onDestroy();
    }
    private void initScreenData(){
        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        mScreenWidth = metric.widthPixels;
        mScreenHeight = metric.heightPixels;
    }
    private void detachPresenter(){
        //presenter与activity解绑定
        if(null != mPresenter){
            mPresenter.dispose();
            mPresenter.detachView();
            mPresenter = null;
        }
    }
    @Override
    public void showUpdateDialog(boolean isForce, String url) {

    }
}
