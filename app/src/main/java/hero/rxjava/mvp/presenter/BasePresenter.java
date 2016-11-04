package hero.rxjava.mvp.presenter;


import java.lang.ref.WeakReference;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;


/**
 * Created by hero on 2016/5/3 0003.
 */
public abstract class BasePresenter<T>  {
    protected WeakReference<T> mViewReference;
    private CompositeDisposable mDisposables;
    /**
     * Presenter与View关联
     * @param view
     */
    public void attachView(T view){
        mViewReference = new WeakReference<T>(view);
    }

    /**
     * Presenter与View解除关联
     */
    public void detachView(){
        if(mViewReference != null){
            mViewReference.clear();
            mViewReference = null;
        }
    }

    protected T getView(){
        if(mViewReference != null){
            return mViewReference.get();
        }
        return null;
    }

    /**
     * Presenter与View是否已关联
     * @return
     */
    public boolean isViewAttached(){
        return mViewReference != null && mViewReference.get() != null;
    }
    public void dispose(Disposable disposable){
        if(mDisposables!=null){
            mDisposables.delete(disposable);
        }
    }
    //取消所有的订阅
    public void dispose(){
        if(mDisposables!=null){
            mDisposables.clear();
        }
    }

    protected void addSubscription(Disposable disposable) {
        if (disposable == null) return;
        if (mDisposables == null) {
            mDisposables = new CompositeDisposable();
        }
        mDisposables.add(disposable);
    }
}
