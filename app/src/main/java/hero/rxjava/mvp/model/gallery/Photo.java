package hero.rxjava.mvp.model.gallery;

/**
 * Created by hero on 2016/11/10 0010.
 * 图库中的相片类
 */

public class Photo {
    private String path; //图片地址  缩略图地址在这里没加，原因是系统缩略图的数目跟所有图片的数目不相符
    private String dirName;//所在文件夹
    private Boolean isCamera;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDirName() {
        return dirName;
    }

    public void setDirName(String dirName) {
        this.dirName = dirName;
    }

    public Boolean isCamera() {
        return isCamera;
    }

    public void isCamera(Boolean isCamera) {
        this.isCamera = isCamera;
    }
}
