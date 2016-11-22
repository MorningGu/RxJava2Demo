package hero.rxjava.ui.gallery.popupwindow;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.LinearLayout;
import android.widget.ListView;


import hero.rxjava.R;
import hero.rxjava.utils.PixelUtil;

/**
 * Created by hero on 2016/11/17 0017.
 * 上下布局，上面一个固定高度的占位透明View 下面一个listview
 */

public class GalleryDirsContainer extends LinearLayout {
    AnimatorSet animatorSet ;
    View mPlaceView;
    ListView mListView;

    float mPlaceViewHeight = PixelUtil.dp2px(100);//单位是dp
    int mPlaceViewBackgroundColor = 0x60000000; //占位控件背景色
    int mDuration = 300; //动画时间

    ObjectAnimator mPlaceViewShowAnimation;
    ObjectAnimator mPlaceViewDismissAnimation;
    ObjectAnimator mListViewShowAnimation;
    ObjectAnimator mListViewDismissAnimation;

    boolean isDismissing = false;
    public GalleryDirsContainer(Context context) {
        this(context,null);
    }

    public GalleryDirsContainer(Context context, AttributeSet attrs) {
        this(context, attrs,R.attr.layout);
    }

    public GalleryDirsContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initData(attrs);
        initView(context);
        postDelayed(new Runnable() {
            @Override
            public void run() {
                setVisibility(GONE);
            }
        },100);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        initShowAnimation();
    }

    private void initData(AttributeSet attrs){
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.GalleryDirsContainer);
        mPlaceViewBackgroundColor = a.getColor(R.styleable.GalleryDirsContainer_placeViewBackgroundColor, mPlaceViewBackgroundColor);
        mPlaceViewHeight = a.getDimension(R.styleable.GalleryDirsContainer_placeViewHeight,mPlaceViewHeight);
        mDuration = a.getInt(R.styleable.GalleryDirsContainer_animationDuration, mDuration);
        a.recycle();
    }
    private void initView(Context context){
        setOrientation(VERTICAL);
        mPlaceView = new View(context);
        mListView = new ListView(context);
        mPlaceView.setBackgroundColor(mPlaceViewBackgroundColor);
        mPlaceView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mListView.setBackgroundColor(0xffffffff);
        addView(mPlaceView,LayoutParams.MATCH_PARENT, (int)mPlaceViewHeight);
        addView(mListView,LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
    }
    public void setListAdapter(GalleryDirsAdapter adapter){
        mListView.setAdapter(adapter);
    }
    void initShowAnimation(){
        if(mPlaceViewShowAnimation==null){
            //占位控件的出场动画和退场动画
            mPlaceViewShowAnimation = ObjectAnimator.ofFloat(mPlaceView, "alpha", 0f, 1f);
            mPlaceViewShowAnimation.setInterpolator(new AccelerateInterpolator());
            mPlaceViewShowAnimation.setDuration(300);
            mPlaceViewDismissAnimation = ObjectAnimator.ofFloat(mPlaceView, "alpha", 1f, 0f);
            mPlaceViewDismissAnimation.setInterpolator(new AccelerateInterpolator());
            mPlaceViewDismissAnimation.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    setVisibility(View.GONE);
                    isDismissing = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {
                    setVisibility(View.GONE);
                    isDismissing = false;
                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            mPlaceViewDismissAnimation.setDuration(300);
            //ListView的出场动画和退场动画
            mListViewShowAnimation = ObjectAnimator.ofFloat(mListView, "translationY", mListView.getMeasuredHeight(), 0f);
            mListViewShowAnimation.setDuration(200);
            mListViewShowAnimation.setInterpolator(new AccelerateDecelerateInterpolator ());
            mListViewDismissAnimation =ObjectAnimator.ofFloat(mListView, "translationY", 0f, mListView.getMeasuredHeight());
            mListViewDismissAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
            mListViewDismissAnimation.setDuration(200);
        }
    }
    public void show(){
        //启动动画，设置Visibility为VISIABLE
        if(animatorSet!=null && animatorSet.isRunning()){
            animatorSet.cancel();
        }
        animatorSet = new AnimatorSet();
        animatorSet.play(mListViewShowAnimation).with(mPlaceViewShowAnimation);
        animatorSet.start();
        setVisibility(VISIBLE);
    }
    public void dismiss(){
        //退场动画，设置 Visibility为GONE
        if(animatorSet!=null && animatorSet.isRunning()){
            animatorSet.cancel();
        }
        isDismissing = true;
        animatorSet = new AnimatorSet();
        animatorSet.play(mPlaceViewDismissAnimation).with(mListViewDismissAnimation);
        animatorSet.start();
    }

}
