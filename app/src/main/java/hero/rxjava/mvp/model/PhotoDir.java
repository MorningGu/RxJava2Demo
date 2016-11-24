package hero.rxjava.mvp.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hero on 2016/11/10 0010.
 * 图库中的相片目录类
 */

public class PhotoDir {
    private String name;//目录名
    private String firstPhotoPath; //首张图片的地址
    private List<Photo> photos; //目录包含的所有图片

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstPhotoPath() {
        return firstPhotoPath;
    }

    public void setFirstPhotoPath(String firstPhotoPath) {
        this.firstPhotoPath = firstPhotoPath;
    }

    public List<Photo> getPhotos() {
        if(photos == null){
            photos = new ArrayList<Photo>();
        }
        return photos;
    }

    public void setPhotos(List<Photo> photos) {
        this.photos = photos;
    }
}
