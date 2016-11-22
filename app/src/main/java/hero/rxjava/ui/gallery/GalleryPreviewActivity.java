package hero.rxjava.ui.gallery;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import hero.rxjava.Config;
import hero.rxjava.R;
import hero.rxjava.mvp.iview.IGalleryActivityView;
import hero.rxjava.mvp.model.gallery.Photo;
import hero.rxjava.mvp.presenter.GalleryActivityPresenter;
import hero.rxjava.ui.base.BaseActivity;
import hero.rxjava.utils.ToastUtils;

public class GalleryPreviewActivity extends BaseActivity<IGalleryActivityView,GalleryActivityPresenter> implements View.OnClickListener{
    public static final String DIR_POSITION = "dirPosition";
    //当前文件夹
    public int dirPosition = 0;
    //当前显示的图片在当前文件夹中的位置 名字
    public static final String PHOTO_POSITION = "photoPosition";
    //当前显示的图片在当前文件夹中的位置
    private int photoPosition;
    public static final String IS_PREVIEW = "isPreview";
    //是不是预览，两种状态，一种是全图图片，一种是选中的图片预览
    private boolean isPreview = false;

    private RelativeLayout rl_title,rl_bottom;
    private LinearLayout ll_ok,ll_cb;
    private ImageView iv_back;
    private TextView tv_select_index,tv_count_all;
    private CheckBox cb_state;
    private HackyViewPager mPager;

    private PhotoViewPagerAdapter mAdapter;

    //选中的图片，来自presenter中的数据
    private List<Photo> mSelectedPhotos;
    //当前展示的数据源 来自presenter中的数据
    private List<Photo> mPhotos;

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery_preview);
        initData(savedInstanceState);
        initView();
    }

    @Override
    protected GalleryActivityPresenter createPresenter() {
        return GalleryActivityPresenter.getInstance();
    }

    /**
     * 初始化数据
     * @param savedInstanceState
     */
    private void initData(Bundle savedInstanceState){
        //处理异常退出时缓存的数据
        if(savedInstanceState==null){
            photoPosition = getIntent().getIntExtra(PHOTO_POSITION, 0);
        }else{
            photoPosition = savedInstanceState.getInt(PHOTO_POSITION, 0);
        }
        isPreview = getIntent().getBooleanExtra(IS_PREVIEW,false);
        if(isPreview){
            //选中图片的预览
            mPhotos = mPresenter.getSelectedPhotos();
        }else{
            //全图片浏览
            dirPosition = getIntent().getIntExtra(DIR_POSITION,0);
            mPhotos = mPresenter.getPhotoDirs().get(dirPosition).getPhotos();
        }
        mSelectedPhotos = mPresenter.getSelectedPhotos();
    }
    private void initView(){
        mPager = (HackyViewPager) findViewById(R.id.pager);
        tv_select_index = (TextView) findViewById(R.id.tv_select_index);
        tv_count_all = (TextView)findViewById(R.id.tv_count_all);
        rl_title = (RelativeLayout)findViewById(R.id.rl_title);
        rl_bottom = (RelativeLayout)findViewById(R.id.rl_preview_bottom);
        ll_cb = (LinearLayout)findViewById(R.id.ll_cb);
        iv_back = (ImageView)findViewById(R.id.iv_back);
        ll_ok = (LinearLayout)findViewById(R.id.ll_ok);
        cb_state = (CheckBox)findViewById(R.id.cb_state);
        ll_ok.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        ll_cb.setOnClickListener(this);
        mAdapter = new PhotoViewPagerAdapter(this, mPhotos);
        mPager.setAdapter(mAdapter);
        mPager.setOffscreenPageLimit(5);
        // 更新下标
        mPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                changeState(arg0);
                photoPosition = arg0;
            }

        });
        mPager.setCurrentItem(photoPosition);
        changeState(photoPosition);
    }
    @Override
    public void onBackPressed() {
        setResult(false);
        super.onBackPressed();
    }

    /**
     * 主动退出时都要走这个方法
     * @param okOrCancel
     */
    private void setResult(boolean okOrCancel) {
        if(okOrCancel){
            setResult(RESULT_OK);
        }else{
            setResult(RESULT_CANCELED);
        }
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        //异常退出时的数据保存
        outState.putInt(PHOTO_POSITION, mPager.getCurrentItem());
        super.onSaveInstanceState(outState);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mPager != null) {
            mPager.destroyDrawingCache();
        }
        mPhotos = null;
        mSelectedPhotos = null;
    }

    /**
     * 判断一个相片是否被选中
     * @param photo
     * @return
     */
    private boolean isSelected(Photo photo){
        if(mSelectedPhotos==null){
            return false;
        }
        if(mSelectedPhotos.contains(photo)){
            return true;
        }
        return false;
    }

    /**
     * 更新选中的状态
     * @param position
     */
    private void changeState(int position){
        if (isSelected(mPhotos.get(position))) {
            cb_state.setChecked(true);
        } else {
            cb_state.setChecked(false);
        }
        tv_count_all.setText(getString(R.string.gallery_count, position+1, mPager
                .getAdapter().getCount()));
        tv_select_index.setText(getString(R.string.gallery_count, mSelectedPhotos.size(), Config.GALLERY_MAX));
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                setResult(false);
                finish();
                break;
            case R.id.ll_ok:
               //进入下一页
                setResult(true);
                finish();
                break;
            case R.id.ll_cb:
                if(!cb_state.isChecked()){
                    if(mPhotos==null || mPhotos.size()==0){
                        ToastUtils.showToast("图片加载错误");
                        cb_state.setChecked(false);
                        return;
                    }
                    if(mSelectedPhotos.size()>=Config.GALLERY_MAX){
                        ToastUtils.showToast("不能再多了！");
                        cb_state.setChecked(false);
                        return;
                    }
                    mSelectedPhotos.add(mPhotos.get(photoPosition));
                    tv_select_index.setText(getString(R.string.gallery_count, mSelectedPhotos.size(), Config.GALLERY_MAX));
                    cb_state.setChecked(true);
                }else{
                    if(isSelected(mPhotos.get(photoPosition))){
                        mSelectedPhotos.remove(mPhotos.get(photoPosition));
                        tv_select_index.setText(getString(R.string.gallery_count, mSelectedPhotos.size(), Config.GALLERY_MAX));
                        cb_state.setChecked(false);
                    }
                }
                break;
        }
    }

}
