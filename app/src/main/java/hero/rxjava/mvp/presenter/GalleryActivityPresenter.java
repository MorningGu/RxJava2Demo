package hero.rxjava.mvp.presenter;

import hero.rxjava.Config;
import hero.rxjava.mvp.iview.IGalleryActivityView;
import hero.rxjava.mvp.model.factory.PhotoFactory;
import hero.rxjava.retrofit.ApiManager;
import io.reactivex.Flowable;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by hero on 2016/11/9 0009.
 * 这个presenter有点特殊，带有数据共享的功能，GalleryPreviewActivity和GalleryActivity都会用到
 * 在Gallery点击返回上一层或者提交时（处理完图片也会回到GalleryActivity,提交只在GalleryActivity中提交）要销毁该presenter
 * 原因是有一个不保留活动的设置，为了保证在不保留活动时数据不清空，所以不在Activity的onDestroy方法中销毁
 *
 */

public class GalleryActivityPresenter extends BasePresenter<IGalleryActivityView> {

    PhotoFactory factory = PhotoFactory.getInstance();

    /**
     * 扫描图库中的图片
     */
    public void startScan(boolean hasCamera){
        Flowable flowable = factory.getScanPhotoFlowable(hasCamera);
        ResourceSubscriber resultSubscriber = new ResourceSubscriber<Integer>() {
            @Override
            public void onNext(Integer integer) {
                IGalleryActivityView view = getView();
                if(view==null)
                    return;
                view.bindData(factory.getPhotos(),factory.getSelectedPhotos(),factory.getPhotoDirs());
            }

            @Override
            public void onError(Throwable t) {

            }

            @Override
            public void onComplete() {

            }
        };
        addSubscription(ApiManager.INSTANCE.startObservable(flowable,resultSubscriber));
    }
    public boolean isSelectedPhoto(){
        if(factory.getSelectedPhotos().size()==0){
            return false;
        }
        return true;
    }
    public void updateState(){
        IGalleryActivityView view = getView();
        if(view!=null){
            view.updateState(factory.getSelectedPhotos().size(), Config.GALLERY_MAX);
        }
    }
}
