package hero.rxjava.utils;

import android.database.Cursor;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hero.rxjava.GApplication;
import hero.rxjava.mvp.model.Photo;
import hero.rxjava.mvp.model.PhotoDir;

/**
 * Created by hero on 2016/11/10 0010.
 * 相片的帮助类，包括获取相片，处理相片等
 */

public class PhotoHelper {

    /**
     * 获取系统中所有的图片
     * @param photos 待填充的容器
     */
    public void getAllPhotos(@NonNull List<Photo> photos) {
        photos.clear();
        Cursor cursor = null;
        try{
            String[] columns = { MediaStore.Images.Media.DATA,
                    MediaStore.Images.Media._ID,
                    MediaStore.Images.Media.BUCKET_DISPLAY_NAME };
            String orderBy = MediaStore.Images.Media.DATE_TAKEN;
            cursor = GApplication.getInstance().getApplicationContext().getContentResolver().query(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI, columns, null,
                    null, orderBy + " DESC");
            if(cursor == null  ){
                return;
            }
            cursor.moveToFirst();
            do {
                String path = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.DATA));
                String dirName = cursor.getString(cursor
                        .getColumnIndex(MediaStore.Images.Media.BUCKET_DISPLAY_NAME));
                if(!TextUtils.isEmpty(path)&&!TextUtils.isEmpty(dirName)){
                    if(isFileExistAndNotGIF(path)){
                        Photo photo = new Photo();
                        photo.setPath(path);
                        photo.setDirName(dirName);
                        photos.add(photo);
                    }
                }
            } while (cursor.moveToNext());

        }catch(Exception e){

        }finally{
            if(cursor != null){
                cursor.close();
            }
        }
    }

    /**
     * 获取所有photos中图片的文件夹列表
     * @param photos  所有照片
     * @param photoDirs  待填充的文件夹列表
     */
    public void getAllPhotoDirs(@NonNull List<Photo> photos, @NonNull List<PhotoDir> photoDirs) {
        photoDirs.clear();
        //先插入所有图片能够提高效率 ArrayList 读取快，插入慢
        PhotoDir firstPhotoDir = new PhotoDir();
        firstPhotoDir.setPhotos(photos);
        firstPhotoDir.setName("所有图片");
        photoDirs.add(firstPhotoDir);
        Map<String, PhotoDir> hashPhotos = new HashMap<String, PhotoDir>();
        for (Photo photo : photos) {
            if (hashPhotos.containsKey(photo.getDirName())) {
                PhotoDir dir = hashPhotos.get(photo.getDirName());
                dir.getPhotos().add(photo);
            } else {
                PhotoDir dir = new PhotoDir();
                dir.setFirstPhotoPath(photo.getPath());
                dir.setName(photo.getDirName());
                dir.getPhotos().add(photo);
                hashPhotos.put(photo.getDirName(), dir);
                photoDirs.add(dir);
            }
        }
        hashPhotos.clear();
        //先把所有图片的文件夹搞出来，可以少一个第一张是不是照相机的判断
        if(photos.size()>0){
            firstPhotoDir.setFirstPhotoPath(photos.get(0).getPath());
        }else{
            firstPhotoDir.setFirstPhotoPath("");
        }
    }
    private Boolean isFileExistAndNotGIF(String path) {
        if(!TextUtils.isEmpty(path)){
            File file = new File(path);
            if (file.exists()) {
                if(path.endsWith("gif")||path.endsWith("GIF"))
                    return false;
                return true;
            }
        }
        return false;
    }
}
