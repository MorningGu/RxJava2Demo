package hero.rxjava.mvp.presenter;

import java.util.ArrayList;
import java.util.List;

import hero.rxjava.mvp.iview.IGalleryActivityView;
import hero.rxjava.mvp.model.gallery.Photo;
import hero.rxjava.mvp.model.gallery.PhotoDir;
import hero.rxjava.retrofit.ApiManager;
import hero.rxjava.utils.PhotoHelper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by hero on 2016/11/9 0009.
 * 这个presenter有点特殊，带有数据共享的功能，GalleryPreviewActivity和GalleryActivity都会用到
 * 在Gallery点击返回上一层或者提交时（处理完图片也会回到GalleryActivity,提交只在GalleryActivity中提交）要销毁该presenter
 * 原因是有一个不保留活动的设置，为了保证在不保留活动时数据不清空，所以不在Activity的onDestroy方法中销毁
 *
 */

public class GalleryActivityPresenter extends BasePresenter<IGalleryActivityView> {
    private static Object monitor = new Object();
    private static GalleryActivityPresenter sInstance;
    //所有图片 用于在GalleryActivity中显示，
    // 只要显示所有图片就是使用这个list，其他使用纯数据的时候都用mPhotoDirs中的list
    private List<Photo> mPhotos;
    //所有文件夹
    private List<PhotoDir> mPhotoDirs;
    //选中的图片
    private List<Photo> mSelectedPhotos;

    /**
     * 这个presenter比较特殊，两个activity同时使用里面的数据
     */
    private GalleryActivityPresenter(){
        mPhotos = new ArrayList<>();
        mPhotoDirs = new ArrayList<>();
        mSelectedPhotos = new ArrayList<>();
    }
    public static GalleryActivityPresenter getInstance(){
        if(sInstance==null){
            synchronized (monitor){
                if(sInstance==null){
                    sInstance = new GalleryActivityPresenter();
                }
            }
        }
        return sInstance;
    }
    /**
     * 扫描图库中的图片
     */
    public void startScan(final boolean hasCamera){
        Flowable<Integer> flowable = Flowable.create(new FlowableOnSubscribe<Integer>() {
            @Override
            public void subscribe(FlowableEmitter<Integer> e) throws Exception {
                //获取数据
                PhotoHelper photoHelper = new PhotoHelper();
                photoHelper.getAllPhotos(mPhotos);
                photoHelper.getAllPhotoDirs(mPhotos,mPhotoDirs);
                //加工数据
                if(hasCamera) {
                    //插入照相机
                    Photo photo = new Photo();
                    photo.isCamera(true);
                    List<Photo> photos = new ArrayList<Photo>();
                    photos.add(photo);
                    photos.addAll(mPhotos);
                    mPhotos = photos;
                }
                e.onNext(1);
                e.onComplete();
            }
        }, BackpressureStrategy.DROP); //指定背压处理策略
        ResourceSubscriber resultSubscriber = new ResourceSubscriber<Integer>() {
            @Override
            public void onNext(Integer integer) {
                IGalleryActivityView view = getView();
                if(view==null)
                    return;
                view.updateGridView(mPhotos,mSelectedPhotos,mPhotoDirs);
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
    //销毁presenter  在数据使用完毕后销毁数据
    public static void destroy(){
        if(sInstance!=null){
            sInstance.mPhotoDirs.clear();
            sInstance.mPhotos.clear();
            sInstance.mSelectedPhotos.clear();
            sInstance = null;
        }
    }

    public List<PhotoDir> getPhotoDirs(){
        return mPhotoDirs;
    }
    public List<Photo> getSelectedPhotos(){
        return mSelectedPhotos;
    }
    public List<Photo> getPhotos(){
        return mPhotos;
    }
}
