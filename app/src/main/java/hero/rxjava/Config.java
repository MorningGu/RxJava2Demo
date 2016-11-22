package hero.rxjava;

import android.os.Environment;

import java.io.File;

/**
 * Created by gulei on 2016/5/4 0004.
 */
public class Config {
    //主目录
    public static final String BASE_DIR = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + File.separator
            + "GApplication";
    public static final String CACHE_DIR = BASE_DIR +
            File.separator +"Cache";
    public static final String PHOTO_DIR = BASE_DIR +
            File.separator +"Photo";
    public static final int CACHE_SIZE_DISK = 200*1024*1024;
    public static final int MEMORY_SIZE = (int) (Runtime.getRuntime().maxMemory()) / 4;  // 取1/4最大内存作为最大缓存
    public static final int IMAGE_SMALL = 200;
    public static final int IMAGE_NORMAL = 600;
    public static final int IMAGE_BIG = 800;
    //图库中可以选择图片的最大数量
    public static final int GALLERY_MAX = 9;
}
