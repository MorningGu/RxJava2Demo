package hero.rxjava.ui.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import hero.rxjava.mvp.iview.IBaseView;
import hero.rxjava.mvp.presenter.BasePresenter;
import hero.rxjava.utils.AppManager;

/**
 * Created by hero on 2016/11/3 0003.
 */

public abstract class BaseActivity<V,T extends BasePresenter<V>> extends AppCompatActivity implements IBaseView{
    protected T mPresenter;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 初始化屏幕区域
        AppManager.INSTANCE.addActivity(this);
        initPresenter();
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
        super.onDestroy();
        AppManager.INSTANCE.finishActivity(this);
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
