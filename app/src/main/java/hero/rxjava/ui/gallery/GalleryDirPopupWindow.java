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
import hero.rxjava.mvp.model.PhotoDir;
import hero.rxjava.mvp.model.factory.PhotoFactory;
import hero.rxjava.utils.ViewUtils;

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
	private int mDuration = 300;

	/**
	 *
	 * @param context
	 * @param photoDirs
	 * @param height 	之所以要设置高度而不用match_parent,是因为安卓7.0的window的属性有变化，
	 *             设置为match_parent之后直接全屏，showasdrapdown的位移失效，所以必须设置精确的高度
     */
	public GalleryDirPopupWindow(Context context,List<PhotoDir> photoDirs,int height) {
		this.mPhotoDirs = photoDirs;
		this.mConvertView = LayoutInflater.from(context).inflate(R.layout.gallery_list_dir, null);
		this.mContext = context;
		// 设置SelectPicPopupWindow的View
		this.setContentView(mConvertView);
		// 设置SelectPicPopupWindow弹出窗体的宽
		this.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
		// 设置SelectPicPopupWindow弹出窗体的高
		this.setHeight(height);
		// 设置SelectPicPopupWindow弹出窗体可点击
		this.setFocusable(true);
		// 加上它之后，setOutsideTouchable（）才会生效;并且PopupWindow才会对手机的返回按钮有响应
		this.setBackgroundDrawable(new BitmapDrawable());
		// 设置popWindow的显示和消失动画
		//this.setAnimationStyle(R.style.pop_anim_style);
		this.setOutsideTouchable(true);
		initViews();
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
		super.showAsDropDown(anchor,x,y,gravity);
		startShowAnimator();
		initShowAnimation();
	}

	@Override
	public void showAtLocation(View parent, int gravity, int x, int y) {
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
			mPlaceViewDismissAnimation = ObjectAnimator.ofFloat(view, "alpha", 1f, 0f);
			mPlaceViewDismissAnimation.setInterpolator(new AccelerateInterpolator  ());
			mPlaceViewDismissAnimation.addListener(new Animator.AnimatorListener() {
				@Override
				public void onAnimationStart(Animator animation) {

				}

				@Override
				public void onAnimationEnd(Animator animation) {
					mConvertView.setVisibility(View.GONE);
					GalleryDirPopupWindow.super.dismiss();
					isDismissing = false;
				}

				@Override
				public void onAnimationCancel(Animator animation) {
					mConvertView.setVisibility(View.GONE);
					GalleryDirPopupWindow.super.dismiss();
					isDismissing = false;
				}

				@Override
				public void onAnimationRepeat(Animator animation) {

				}
			});
			ViewUtils.measureView(lv_dirs);
			//ListView的出场动画和退场动画
			mListViewShowAnimation = ObjectAnimator.ofFloat(lv_dirs, "translationY", lv_dirs.getHeight(), 0f);
			mListViewShowAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
			mListViewDismissAnimation =ObjectAnimator.ofFloat(lv_dirs, "translationY", 0f, lv_dirs.getHeight());
			mListViewDismissAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
		}
	}

	/**
	 * 入场动画，设置Visibility为VISIABLE
	 */
	private void startShowAnimator(){
		if(animatorSet!=null && animatorSet.isRunning()){
			animatorSet.cancel();
		}
		animatorSet = new AnimatorSet();
		animatorSet.play(mListViewShowAnimation).with(mPlaceViewShowAnimation);
		animatorSet.setDuration(mDuration);
		animatorSet.start();
		mConvertView.setVisibility(View.VISIBLE);
	}

	/**
	 * 退场动画
	 */
	private void startDismissAnimator(){
		if(animatorSet!=null && animatorSet.isRunning()){
			animatorSet.cancel();
		}
		animatorSet = new AnimatorSet();
		animatorSet.play(mListViewDismissAnimation).with(mPlaceViewDismissAnimation);
		animatorSet.setDuration(mDuration);
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
						((GalleryActivity)mContext).resetPhotos(PhotoFactory.getInstance().getPhotos(),position);
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
			holder.tv_dir_count.setText("");
		}
	}
}
