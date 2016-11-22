package hero.rxjava.ui.gallery;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import hero.rxjava.Config;
import hero.rxjava.R;
import hero.rxjava.mvp.model.gallery.Photo;
import hero.rxjava.utils.PixelUtil;
import hero.rxjava.utils.ToastUtils;

/**
 * Created by hero on 2016/11/10 0010.
 */

public class GalleryAdapter extends BaseAdapter {
    private List<Photo> mPhotos;
    private Context mContext;
    private int itemHeight ;
    private int dirPosition = 0;
    private OnSelectListener mOnSelectListener;
    private boolean hasCamera;
    /**
     * 用户选择的图片
     */
    private List<Photo> mSelectedPhotos;
    public GalleryAdapter(Context context,List<Photo> selectedPhotos,OnSelectListener listener){
        mContext = context;
        mSelectedPhotos = selectedPhotos;
        mOnSelectListener = listener;
    }

    @Override
    public int getCount() {
        return mPhotos.size();
    }

    @Override
    public Object getItem(int position) {
        return mPhotos.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_gallery, null);
            holder = new ViewHolder();
            holder.iv_photo = (ImageView) convertView
                    .findViewById(R.id.iv_photo);
            holder.cb_select = (CheckBox) convertView
                    .findViewById(R.id.cb_select);
            ViewGroup.LayoutParams lp = holder.iv_photo.getLayoutParams();
            lp.height = itemHeight;
            holder.iv_photo.setLayoutParams(lp);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
            reset(holder);
        }
        if(mPhotos==null || mPhotos.size()==0){
            return convertView;
        }
        final Photo photo = mPhotos.get(position);
        if(photo.isCamera()!=null && photo.isCamera()){
            //相机标记
            holder.cb_select.setVisibility(View.GONE);
            holder.iv_photo.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            holder.iv_photo.setImageResource(R.mipmap.ic_gallery_camera);
            holder.iv_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mSelectedPhotos.size() < Config.GALLERY_MAX) {
                        mOnSelectListener.onSelectCamera();
                    }else{
                        ToastUtils.showToast("不能再多了！");
                    }
                }
            });
        }else{
            if(photo.getPath()==null ||"".equals(photo.getPath().trim())){
                //如果图片地址是空 checkbox不显示
                holder.cb_select.setVisibility(View.GONE);
                holder.iv_photo.setImageBitmap(null);
            }else{
                //图片地址不为空
                if(mSelectedPhotos.contains(photo)){
                    holder.cb_select.setChecked(true);
                    holder.iv_photo.setColorFilter(Color
                            .parseColor("#77000000"));
                }else{
                    holder.cb_select.setChecked(false);
                    holder.iv_photo.setColorFilter(null);
                }
                Glide.with(mContext)
                        .load(photo.getPath())
                        .override(300,300)
                        .into(holder.iv_photo);
                final ImageView imageView = holder.iv_photo;
                holder.cb_select.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked){
                            if (mSelectedPhotos.size() < Config.GALLERY_MAX) {
                                mSelectedPhotos.add(photo);
                                imageView.setColorFilter(Color.parseColor("#77000000"));
                                if(mOnSelectListener!=null){
                                    mOnSelectListener.onSelectPhoto(mSelectedPhotos);
                                }
                            } else {
                                ToastUtils.showToast("不能再多了！");
                                buttonView.setChecked(false);
                            }
                        }else{
                            mSelectedPhotos.remove(photo);
                            if(mOnSelectListener!=null){
                                mOnSelectListener.onSelectPhoto(mSelectedPhotos);
                            }
                            imageView.setColorFilter(null);
                        }
                    }
                });
            }
            // 设置ImageView的点击事件
            holder.iv_photo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(mContext,GalleryPreviewActivity.class);
                    if(hasCamera){
                        intent.putExtra(GalleryPreviewActivity.PHOTO_POSITION, position-1);
                    }else{
                        intent.putExtra(GalleryPreviewActivity.PHOTO_POSITION, position);
                    }
                    intent.putExtra(GalleryPreviewActivity.DIR_POSITION, dirPosition);
                    intent.putExtra(GalleryPreviewActivity.IS_PREVIEW, false);
                    ((GalleryActivity)mContext).startActivityForResult(intent,GalleryActivity.TO_PREVIEW);
                }
            });
        }
        return convertView;
    }

    public void calculateItemHeight(int screenWidth){
        itemHeight = ((screenWidth - PixelUtil.dp2px(4))/3);
    }
    private void reset(ViewHolder holder){
        holder.iv_photo.setColorFilter(null);
        holder.iv_photo.setScaleType(ImageView.ScaleType.CENTER_CROP);
        holder.cb_select.setOnCheckedChangeListener(null);
        holder.cb_select.setVisibility(View.VISIBLE);
        holder.cb_select.setChecked(false);
    }

    /**
     * 重设数据源
     * @param photos
     * @param dirPosition
     * @param hasCamera 更新adapter中的hasCamera的值，因为会根据这个值来确定position是否减1
     */
    public void resetData(List<Photo> photos,int dirPosition,boolean hasCamera){
        this.dirPosition = dirPosition;
        this.mPhotos = photos;
        this.hasCamera = hasCamera;
    }
    private class ViewHolder {
        public ImageView iv_photo;
        public CheckBox cb_select;
    }
    interface OnSelectListener{
        void onSelectPhoto(List<Photo> photos);
        void onSelectCamera();
    }
}
