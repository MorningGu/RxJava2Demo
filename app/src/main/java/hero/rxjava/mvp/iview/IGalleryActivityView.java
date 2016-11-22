package hero.rxjava.mvp.iview;

import java.util.List;

import hero.rxjava.mvp.model.gallery.Photo;
import hero.rxjava.mvp.model.gallery.PhotoDir;

/**
 * Created by hero on 2016/11/9 0009.
 */

public interface IGalleryActivityView extends IBaseView{
    void updateGridView(List<Photo> photos,List<Photo> selectedPhotos,List<PhotoDir> photoDirs);

}
