package hero.rxjava.ui.gallery;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

import hero.rxjava.Config;
import hero.rxjava.R;
import hero.rxjava.mvp.iview.IGalleryActivityView;
import hero.rxjava.mvp.model.gallery.Photo;
import hero.rxjava.mvp.model.gallery.PhotoDir;
import hero.rxjava.mvp.presenter.GalleryActivityPresenter;
import hero.rxjava.ui.base.BaseActivity;
import hero.rxjava.ui.gallery.popupwindow.GalleryDirsAdapter;
import hero.rxjava.ui.gallery.popupwindow.GalleryDirsContainer;
import hero.rxjava.utils.PixelUtil;
import hero.rxjava.utils.ToastUtils;

/**
 * 图库
 * 仿微信
 */
public class GalleryActivity extends BaseActivity<IGalleryActivityView,GalleryActivityPresenter> implements IGalleryActivityView{
    private final int TO_CAMERA = 0x000001;
    public static final int TO_PREVIEW = 0x00002;
    private final int TO_PROGRESS = 0x00003;

    private boolean needProgress = false;//选中图片后是否需要处理，前提是只有一张图片的情况

    private GridView gv_photos;
    private TextView tv_dir_choose;
    private TextView tv_count;
    private LinearLayout layout_show_big;
    private GalleryDirPopupWindow dirPopupWindow;
    private GalleryDirsContainer layout_dirs;

    private GalleryDirsAdapter dirsAdapter;
    private GalleryAdapter mAdapter;
    //这是相机拍照的图片地址，当选择拍照的时候，
    // 之前选中的图片list还在，但是只传这个到下阶段，仿照微信的交互
    private String cameraPath;
    //是否支持相机
    private boolean hasCamera = false;
    //图片文件夹的位置
    private int dirPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);
        hasCamera = isSupportCamera();
        initView();
        mPresenter.startScan(true);
    }

    @Override
    protected GalleryActivityPresenter createPresenter() {
        return GalleryActivityPresenter.getInstance();
    }

    private void initView(){
        gv_photos = (GridView)findViewById(R.id.gv_photos);
        tv_dir_choose = (TextView) findViewById(R.id.tv_dir_choose);
        tv_count = (TextView) findViewById(R.id.tv_count);
        layout_show_big = (LinearLayout) findViewById(R.id.layout_show_big);
        layout_dirs = (GalleryDirsContainer)findViewById(R.id.layout_dirs);

        tv_dir_choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDirsPopupWindow();
//                if(layout_dirs.getVisibility()==View.GONE){
//                    layout_dirs.show();
//                }else{
//                    layout_dirs.dismiss();
//                }
            }
        });
        layout_show_big.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mPresenter.getSelectedPhotos().size()==0){
                    ToastUtils.showToast("请先选中图片");
                    return;
                }
                //预览已选中的图片
                Intent intent = new Intent(GalleryActivity.this,GalleryPreviewActivity.class);
                //优先展示第一张
                intent.putExtra(GalleryPreviewActivity.PHOTO_POSITION, 0);
                //这里可以不传
//                intent.putExtra(GalleryPreviewActivity.DIR_POSITION, dirPosition);
                intent.putExtra(GalleryPreviewActivity.IS_PREVIEW, true);
                startActivityForResult(intent,GalleryActivity.TO_PREVIEW);
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if(!mPresenter.isViewAttached()){
            //从Preview返回时需要对presenter重新attach
            mPresenter.attachView(this);
        }
    }

    @Override
    public void updateGridView(List<Photo> photos, List<Photo> selectedPhotos,List<PhotoDir> photoDirs) {
        if(mAdapter==null){
            mAdapter = new GalleryAdapter(this, selectedPhotos, new GalleryAdapter.OnSelectListener() {
                @Override
                public void onSelectPhoto(List<Photo> photos) {
                    updateSelectedNum(photos.size(),Config.GALLERY_MAX);
                }

                @Override
                public void onSelectCamera() {
                    //进入相机
                    Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    File dir = new File(Config.PHOTO_DIR);
                    if (!dir.exists()) {
                        dir.mkdirs();
                    }
                    //预设照片地址
                    File file = new File(dir, String.valueOf(System.currentTimeMillis()) + ".jpg");
                    cameraPath = file.getPath();
                    Uri imageUri = Uri.fromFile(file);
                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
                    startActivityForResult(openCameraIntent, TO_CAMERA);
                }
            });
            //初次设置数据源
            mAdapter.resetData(photos,dirPosition,hasCamera);
            //构造方法参数太多，拿到外面设
            mAdapter.calculateItemHeight(mScreenWidth);
            gv_photos.setAdapter(mAdapter);
            initDirsPopupWindow(photoDirs);
            initDirsView(photoDirs);
        }
        mAdapter.notifyDataSetChanged();
    }
    private void updateSelectedNum(int count,int max){
        tv_count.setText(getString(R.string.gallery_count, count, max));
    }

    /**
     * 用于更换目录时重置数据
     * @param photos  目录下的所有图片
     * @param dirPosition  目录的位置
     */
    public void resetPhotos(List<Photo> photos,int dirPosition){
        if(dirPosition==0){
            //只有所有图片这个目录里可能会有照相机，其他目录不存在照相机
            mAdapter.resetData(photos,dirPosition,hasCamera);
        }else{
            mAdapter.resetData(photos,dirPosition,false);
        }
        mAdapter.notifyDataSetChanged();
    }
    @Override
    public void onBackPressed() {
        //清空内存缓存
        Glide.get(this).clearMemory();
        //这里只是做一个提交的例子，实际的时候这里应该是一个cancel
        setResult(RESULT_OK);
        super.onBackPressed();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode){
                case TO_CAMERA:{
                    if (TextUtils.isEmpty(cameraPath)) {
                        ToastUtils.showToast("相片获取失败");
                        return;
                    }
                    //更新系统图片库
                    Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    scanIntent.setData(Uri.fromFile(new File(cameraPath)));
                    sendBroadcast(scanIntent);
                    // 照片可能需要处理
                    progress();
                    break;
                }
                case TO_PREVIEW:{
                    // 图片可能需要处理
                    progress();
                    break;
                }
                case TO_PROGRESS:{
                    // 处理结束之后
                    submit();
                    break;
                }
            }
        }else{
            //数据可能有变动，更新ui
            mAdapter.notifyDataSetChanged();
            updateSelectedNum(mPresenter.getSelectedPhotos().size(),Config.GALLERY_MAX);
        }
    }
    private void initDirsView(List<PhotoDir> photoDirs){
        dirsAdapter = new GalleryDirsAdapter(this,photoDirs);
        layout_dirs.setListAdapter(dirsAdapter);
    }

    private void initDirsPopupWindow(List<PhotoDir> photoDirs){
        dirPopupWindow = new GalleryDirPopupWindow(this,photoDirs,true);
    }
    private void showDirsPopupWindow(){
        if(dirPopupWindow==null){
            return;
        }
        dirPopupWindow.updatePopWindow();
        dirPopupWindow.showAsDropDown(findViewById(R.id.layout_bottom),0,-(mScreenHeight+PixelUtil.dp2px(48)),Gravity.BOTTOM);
    }
    /**
     * 是否支持相机
     * @return
     */
    private boolean isSupportCamera(){
        PackageManager pm = getPackageManager();
        // FEATURE_CAMERA - 后置相机
        // FEATURE_CAMERA_FRONT - 前置相机
        if (!pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)
                && !pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FRONT)) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 处理数据
     */
    private void progress(){
        if(needProgress && (!TextUtils.isEmpty(cameraPath) || mPresenter.getSelectedPhotos().size()==1)){
            // TODO: 2016/11/17 0017 进入下一页面，剪辑修图
        }else{
            submit();
        }
    }
    private void submit(){
        // TODO: 2016/11/17 0017  提交选中数据 等待数据提交完毕后销毁presenter
        mPresenter.getSelectedPhotos();
    }
}
