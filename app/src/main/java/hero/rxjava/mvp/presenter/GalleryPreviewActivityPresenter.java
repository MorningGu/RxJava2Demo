package hero.rxjava.mvp.presenter;

import java.util.List;

import hero.rxjava.Config;
import hero.rxjava.mvp.iview.IGalleryPreviewActivityView;
import hero.rxjava.mvp.model.Photo;
import hero.rxjava.mvp.model.factory.PhotoFactory;
import hero.rxjava.utils.ToastUtils;

/**
 * Created by hero on 2016/11/22 0022.
 */

public class GalleryPreviewActivityPresenter extends BasePresenter<IGalleryPreviewActivityView> {
    PhotoFactory factory = PhotoFactory.getInstance();
    public void initView(boolean isPreview,int dirPosition){
        IGalleryPreviewActivityView view = getView();
        if(view!=null){
            List<Photo> photos;
            if(isPreview){
                photos = factory.getSelectedPhotos();
            }else{
                photos = factory.getPhotoDirs().get(dirPosition).getPhotos();
            }
            view.initUI(photos);
        }
    }
    /**
     * 判断一个相片是否被选中
     * @param dirPosition 文件夹位置
     * @param position 照片在文件夹中的位置
     * @return
     */
    public boolean isSelected(int dirPosition,int position){
        if(factory.getSelectedPhotos().size() == 0){
            return false;
        }
        if(factory.getSelectedPhotos().contains(factory.getPhotoDirs().get(dirPosition).getPhotos().get(position))){
            return true;
        }
        return false;
    }

    /**
     * 选中的图片数量是否已达到上限
     * @return
     */
    public boolean isSelectedFull(){
        if(factory.getSelectedPhotos().size()>= Config.GALLERY_MAX){
            return true;
        }
        return false;
    }

    /**
     * 增加选中的图片
     * @param dirPosition
     * @param position
     * @return 返回码 0为添加成功，1为异常，2为数量满了
     */
    public int addSelectedPhoto(int dirPosition,int position){
        List<Photo> photos = factory.getPhotoDirs().get(dirPosition).getPhotos();
        if(photos==null || photos.size()==1){
            ToastUtils.showToast("图片加载错误");
            return 1;
        }
        if(isSelectedFull()){
            ToastUtils.showToast("不能再多了！");
            return 2;
        }
        factory.getSelectedPhotos().add(photos.get(position));
        IGalleryPreviewActivityView view = getView();
        if(view!=null){
            view.changeState(position,factory.getSelectedPhotos().size());
        }
        return 0;
    }

    /**
     * 删除选中图片
     * @param dirPosition
     * @param position
     */
    public void removeSelectedPhoto(int dirPosition,int position){
        List<Photo> photos = factory.getPhotoDirs().get(dirPosition).getPhotos();
        if(photos==null || photos.size()==1){
            ToastUtils.showToast("图片加载错误");
            return ;
        }
        if(isSelected(dirPosition,position)){
            factory.getSelectedPhotos().remove(photos.get(position));
        }
        IGalleryPreviewActivityView view = getView();
        if(view!=null){
            view.changeState(position,factory.getSelectedPhotos().size());
        }
    }

}
