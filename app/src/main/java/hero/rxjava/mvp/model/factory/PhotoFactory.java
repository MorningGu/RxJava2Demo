package hero.rxjava.mvp.model.factory;

import java.util.ArrayList;
import java.util.List;

import hero.rxjava.mvp.model.Photo;
import hero.rxjava.mvp.model.PhotoDir;
import hero.rxjava.utils.PhotoHelper;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;

/**
 * Created by hero on 2016/11/22 0022.
 */

public class PhotoFactory {
    private static Object monitor = new Object();
    private static PhotoFactory sInstance;
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
    private PhotoFactory(){
        mPhotos = new ArrayList<>();
        mPhotoDirs = new ArrayList<>();
        mSelectedPhotos = new ArrayList<>();
    }
    public static PhotoFactory getInstance(){
        if(sInstance==null){
            synchronized (monitor){
                if(sInstance==null){
                    sInstance = new PhotoFactory();
                }
            }
        }
        return sInstance;
    }

    /**
     * 得到扫描图片的flowable
     * @param hasCamera
     * @return
     */
    public Flowable getScanPhotoFlowable(final boolean hasCamera){
        return Flowable.create(new FlowableOnSubscribe<Integer>() {
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
    }
    //销毁presenter  在数据使用完毕后销毁数据
    public static void destroy(){
        if(sInstance==null){
            return;
        }
        sInstance.mPhotoDirs.clear();
        sInstance.mPhotos.clear();
        sInstance.mSelectedPhotos.clear();
        sInstance = null;
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
    public void setPhotos(List<Photo> photos){
        if(photos==null){
            return;
        }
        mPhotos = photos;
    }
}
