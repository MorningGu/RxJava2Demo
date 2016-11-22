package hero.rxjava.ui.gallery;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AccelerateInterpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import hero.rxjava.R;
import hero.rxjava.mvp.model.gallery.PhotoDir;
import hero.rxjava.mvp.presenter.GalleryActivityPresenter;

public class GalleryDirPopupWindow extends PopupWindow{
	private View mConvertView;
	private ListView lv_dirs;
	private View view;
	private BaseAdapter mAdapter;
	private List<PhotoDir> mPhotoDirs;
	//动画相关
	AnimatorSet animatorSet ;
	ObjectAnimator mPlaceViewShowAnimation;
	ObjectAnimator mPlaceViewDismissAnimation;
	ObjectAnimator mListViewShowAnimation;
	ObjectAnimator mListViewDismissAnimation;

	//是否正在消失，为了解决快速的两次点击出现的问题
	boolean isDismissing = false;

	private Context mContext;
	public GalleryDirPopupWindow(Context context,List<PhotoDir> photoDirs, boolean mHasCamera) {
		this.mPhotoDirs = photoDirs;
		this.mConvertView = LayoutInflater.from(context).inflate(R.layout.gallery_list_dir, null);;
		this.mContext = context;
		// 设置SelectPicPopupWindow的View
		this.setContentView(mConvertView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
//		// 实例化一个ColorDrawable颜色为半透明
//		ColorDrawable dw = new ColorDrawable(0x60000000);
		// 设置SelectPicPopupWindow弹出窗体的背景
		this.setBackgroundDrawable(new BitmapDrawable());
		// 设置popWindow的显示和消失动画
		this.setAnimationStyle(R.style.pop_anim_style);
		this.setOutsideTouchable(true);
		initViews();
		initShowAnimation();
	}

	public void initViews() {
		lv_dirs = (ListView) mConvertView.findViewById(R.id.lv_dirs);
		view = (View)mConvertView.findViewById(R.id.view);
		// mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
		mConvertView.setOnTouchListener(new View.OnTouchListener() {

			public boolean onTouch(View v, MotionEvent event) {
				int height = lv_dirs.getTop();
				int y = (int) event.getY();
				if (event.getAction() == MotionEvent.ACTION_UP) {
					if (y < height) {
						dismiss();
					}
				}
				return true;
			}
		});
		mAdapter =new GalleryDirsAdapter(mContext,mPhotoDirs);
		lv_dirs.setAdapter(mAdapter);
	}

	public void updatePopWindow() {
		mAdapter.notifyDataSetChanged();
	}


	@Override
	public void dismiss() {
		if(isDismissing)
			return ;
		isDismissing = true;
		startDismissAnimator();
	}

	@Override
	public void showAsDropDown(View anchor,int x,int y,int gravity) {
		//先设置成不可见的，不然会有一个默认的动画，影响效果
		//通过设置现在的AnimationStyle也能解决问题
//		mConvertView.setVisibility(View.GONE);
		super.showAsDropDown(anchor,x,y,gravity);
		startShowAnimator();
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
//		mConvertView.setVisibility(View.GONE);
		super.showAtLocation(parent, gravity, x, y);
		startShowAnimator();
	}

	/**
	 * 初始化动画
	 */
	private void initShowAnimation(){
		if(mPlaceViewShowAnimation==null){
			//占位控件的出场动画和退场动画
			mPlaceViewShowAnimation = ObjectAnimator.ofFloat(view, "alpha", 0f, 1f);
			mPlaceViewShowAnimation.setInterpolator(new AccelerateInterpolator  ());
			mPlaceViewShowAnimation.setDuration(300);
			mPlaceViewDismissAnimation = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
			mPlaceViewDismissAnimation.setInterpolator(new AccelerateInterpolator  ());
			mPlaceViewDismissAnimation.setDuration(100);
			mPlaceViewDismissAnimation.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					mConvertView.setVisibility(View.GONE);
					//需要延时，不然效果不好
					mConvertView.postDelayed(new Runnable() {
						@Override
						public void run() {
							GalleryDirPopupWindow.super.dismiss();
							isDismissing = false;
						}
					},100);
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					mConvertView.setVisibility(View.GONE);
					mConvertView.postDelayed(new Runnable() {
						@Override
						public void run() {
							GalleryDirPopupWindow.super.dismiss();
							isDismissing = false;
						}
					},100);
				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}
			});
			measureView(lv_dirs);
			//ListView的出场动画和退场动画
			mListViewShowAnimation = ObjectAnimator.ofFloat(lv_dirs, "translationY", lv_dirs.getMeasuredHeight(), 0f);
			mListViewShowAnimation.setDuration(200);
			mListViewShowAnimation.setInterpolator(new AccelerateInterpolator());
			mListViewDismissAnimation =ObjectAnimator.ofFloat(lv_dirs, "translationY", 0f, lv_dirs.getMeasuredHeight());
			mListViewDismissAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
			mListViewDismissAnimation.setDuration(300);
		}
	}
	/**
	 * 目前该方法只支持预计算宽高设置为准确值或wrap_content的情况，
	 * 不支持match_parent的情况，因为view的父view还未预计算出宽高
	 * @param v 要预计算的view
	 */
	private void measureView(View v) {
		ViewGroup.LayoutParams lp = v.getLayoutParams();
		if (lp == null) {
			return;
		}
		int width;
		int height;
		if (lp.width > 0) {
			// xml文件中设置了该view的准确宽度值，例如android:layout_width="150dp"
			width = View.MeasureSpec.makeMeasureSpec(lp.width, View.MeasureSpec.EXACTLY);
		} else {
			// xml文件中使用wrap_content设定该view宽度，例如android:layout_width="wrap_content"
			width = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		}

		if (lp.height > 0) {
			// xml文件中设置了该view的准确高度值，例如android:layout_height="50dp"
			height = View.MeasureSpec.makeMeasureSpec(lp.height, View.MeasureSpec.EXACTLY);
		} else {
			// xml文件中使用wrap_content设定该view高度，例如android:layout_height="wrap_content"
			height = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
		}

		v.measure(width, height);
	}
	/**
	 * 启动动画，设置Visibility为VISIABLE
	 */
	private void startShowAnimator(){
		if(animatorSet!=null && animatorSet.isRunning()){
			animatorSet.cancel();
		}
		animatorSet = new AnimatorSet();
		animatorSet.play(mListViewShowAnimation).with(mPlaceViewShowAnimation);
		animatorSet.start();
		mConvertView.setVisibility(View.VISIBLE);
	}

	/**
	 * 退场动画
	 */
	private void startDismissAnimator(){
		//如果
		if(animatorSet!=null && animatorSet.isRunning()){
			animatorSet.cancel();
		}
		animatorSet = new AnimatorSet();
		animatorSet.play(mListViewDismissAnimation).with(mPlaceViewDismissAnimation);
		animatorSet.start();
	}
	class GalleryDirsAdapter extends BaseAdapter {
		private List<PhotoDir> mPhotoDirs;
		private Context mContext;
		public GalleryDirsAdapter(Context context,List<PhotoDir> photoDirs){
			mContext = context;
			mPhotoDirs = photoDirs;
		}
		@Override
		public int getCount() {
			return mPhotoDirs.size();
		}

		@Override
		public Object getItem(int position) {
			return mPhotoDirs.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final PhotoDir item = mPhotoDirs.get(position);
			ImgViewHolder holder = null;
			if(convertView==null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.gallery_list_dir_item, null);
				holder = new ImgViewHolder();
				holder.tv_dir_name = (TextView) convertView.findViewById(R.id.tv_dir_name);
				holder.iv_dir_image=(ImageView)convertView.findViewById(R.id.iv_dir_image);
				holder.tv_dir_count = (TextView) convertView.findViewById(R.id.tv_dir_count);
				convertView.setTag(holder);
			} else {
				holder=(ImgViewHolder) convertView.getTag();
				resetHolder(holder);
			}
			convertView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if(position==0){
						((GalleryActivity)mContext).resetPhotos(GalleryActivityPresenter.getInstance().getPhotos(),position);
					}else{
						((GalleryActivity)mContext).resetPhotos(item.getPhotos(),position);
					}
					dismiss();
				}
			});
			holder.tv_dir_name.setText(item.getName());
			Glide.with(mContext).load(item.getFirstPhotoPath()).override(200,200).into(holder.iv_dir_image);
			return convertView;
		}
		class ImgViewHolder{
			public TextView tv_dir_name;
			public ImageView iv_dir_image;
			public TextView tv_dir_count;
		}

		private void resetHolder(ImgViewHolder holder) {
			holder.tv_dir_name.setText("");
			holder.iv_dir_image.setImageResource(R.mipmap.ic_launcher);
			holder.tv_dir_count.setText("");
		}
	}
}
