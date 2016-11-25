package hero.rxjava.ui.gallery;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import hero.rxjava.Config;
import hero.rxjava.R;
import hero.rxjava.mvp.iview.IGalleryPreviewActivityView;
import hero.rxjava.mvp.model.Photo;
import hero.rxjava.mvp.presenter.GalleryPreviewActivityPresenter;
import hero.rxjava.ui.base.BaseActivity;
import hero.rxjava.utils.ViewUtils;

public class GalleryPreviewActivity extends BaseActivity<IGalleryPreviewActivityView,GalleryPreviewActivityPresenter> implements View.OnClickListener, IGalleryPreviewActivityView {
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
    private TextView tv_select_count,tv_index;
    private CheckBox cb_state;
    private HackyViewPager mPager;
    private Toolbar toolbar;

    private PhotoViewPagerAdapter mAdapter;
    //动画相关
    AnimatorSet animatorSet ;
    ObjectAnimator bottomShowAnimator;
    ObjectAnimator bottomDismissAnimator;
    ObjectAnimator titleShowAnimator;
    ObjectAnimator titleDismissAnimator;

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        //不设背景则在状态栏隐藏之后会有留白
        getWindow().setBackgroundDrawableResource(R.drawable.shape_gallery_statusbar);
        setContentView(R.layout.activity_gallery_preview);
        initActionBar();
        initData(savedInstanceState);
        mPresenter.initView(isPreview,dirPosition,photoPosition);
        getWindow().getDecorView().setOnSystemUiVisibilityChangeListener(new View.OnSystemUiVisibilityChangeListener() {
            @Override
            public void onSystemUiVisibilityChange(int visibility) {
                if(visibility==View.VISIBLE){
                    showBar();
                }
            }
        });
        initAnimation();
    }

    @Override
    protected GalleryPreviewActivityPresenter createPresenter() {
        return new GalleryPreviewActivityPresenter();
    }
    private void initActionBar(){
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
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
        if(!isPreview){
            //全图片浏览
            dirPosition = getIntent().getIntExtra(DIR_POSITION,0);
        }
    }

    private void initAnimation(){
        ViewUtils.measureView(toolbar);
        bottomShowAnimator = ObjectAnimator.ofFloat(rl_bottom, "alpha", 0f, 1f);
        bottomDismissAnimator = ObjectAnimator.ofFloat(rl_bottom, "alpha", 1f, 0f);
        titleShowAnimator = ObjectAnimator.ofFloat(toolbar, "translationY", -toolbar.getMeasuredHeight(), 0f);
        titleDismissAnimator = ObjectAnimator.ofFloat(toolbar, "translationY", 0f, -toolbar.getMeasuredHeight());
        titleDismissAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                toolbar.setVisibility(View.GONE);
                rl_bottom.setVisibility(View.GONE);
                ViewUtils.hideStatusBar(getWindow());
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                toolbar.setVisibility(View.GONE);
                rl_bottom.setVisibility(View.GONE);
                ViewUtils.hideStatusBar(getWindow());
            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
    }

    /**
     * 显示bottombar和toolbar
     */
    private void showBar(){
        if(animatorSet!=null && animatorSet.isRunning()){
            animatorSet.cancel();
        }
        animatorSet = new AnimatorSet();
        animatorSet.play(bottomShowAnimator).with(titleShowAnimator);
        animatorSet.setDuration(300);
        animatorSet.start();
        toolbar.setVisibility(View.VISIBLE);
        rl_bottom.setVisibility(View.VISIBLE);
    }

    /**
     * 隐藏bottombar和toolbar
     */
    private void hideBar(){
        if(animatorSet!=null && animatorSet.isRunning()){
            animatorSet.cancel();
        }
        animatorSet = new AnimatorSet();
        animatorSet.play(bottomDismissAnimator).with(titleDismissAnimator);
        animatorSet.setDuration(300);
        animatorSet.start();
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
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.iv_back:
                onBackPressed();
                break;
            case R.id.ll_ok:
               //进入下一页
                setResult(true);
                finish();
                break;
            case R.id.ll_cb:
                if(!cb_state.isChecked()){
                    mPresenter.addSelectedPhoto(dirPosition,photoPosition);
                }else{
                    mPresenter.removeSelectedPhoto(dirPosition,photoPosition);
                }
                break;
        }
    }


    @Override
    public void initUI(List<Photo> photos) {
        mPager = (HackyViewPager) findViewById(R.id.pager);
        tv_select_count = (TextView) findViewById(R.id.tv_select_count);
        tv_index = (TextView)findViewById(R.id.tv_index);
        rl_title = (RelativeLayout)findViewById(R.id.rl_title);
        rl_bottom = (RelativeLayout)findViewById(R.id.rl_preview_bottom);
        ll_cb = (LinearLayout)findViewById(R.id.ll_cb);
        iv_back = (ImageView)findViewById(R.id.iv_back);
        ll_ok = (LinearLayout)findViewById(R.id.ll_ok);
        cb_state = (CheckBox)findViewById(R.id.cb_state);
        ll_ok.setOnClickListener(this);
        iv_back.setOnClickListener(this);
        ll_cb.setOnClickListener(this);

        mAdapter = new PhotoViewPagerAdapter(this, photos, new PhotoViewPagerAdapter.OnTapListener() {
            @Override
            public void onViewTap() {
                if(toolbar.getVisibility()==View.GONE){
                    getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
                }else{
                    hideBar();
                }

            }
        });
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
    /**
     * 更新选中的状态
     * @param position 当前位置
     */
    private void changeState(int position){
        if (mPresenter.isSelected(dirPosition,position)) {
            cb_state.setChecked(true);
        } else {
            cb_state.setChecked(false);
        }
        tv_index.setText(getString(R.string.gallery_index, position+1, mPager
                .getAdapter().getCount()));
    }
    /**
     * 更新选中的状态
     * @param position 当前位置
     * @param size 已选中的图片数量
     */
    @Override
    public void changeState(int position ,int size) {
        changeState(position);
        tv_select_count.setText(getString(R.string.gallery_count_max, size, Config.GALLERY_MAX));
    }

}
