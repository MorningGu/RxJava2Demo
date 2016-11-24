package hero.rxjava.mvp.iview;

import java.util.List;

import hero.rxjava.mvp.model.Photo;
import hero.rxjava.mvp.model.PhotoDir;

/**
 * Created by hero on 2016/11/9 0009.
 */

public interface IGalleryActivityView extends IBaseView{
    void bindData(List<Photo> photos,List<Photo> selectedPhotos,List<PhotoDir> photoDirs);
    void updateState(int count,int max);
    void progress();
    void submit();
}
