package hero.rxjava.mvp.iview;

import java.util.List;

import hero.rxjava.mvp.model.Photo;

/**
 * Created by hero on 2016/11/22 0022.
 */

public interface IGalleryPreviewActivityView extends IBaseView {
    void initUI(List<Photo> photos);
    void changeState(int position,int size);
}
