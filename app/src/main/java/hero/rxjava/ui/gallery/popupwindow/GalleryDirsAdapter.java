package hero.rxjava.ui.gallery.popupwindow;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import hero.rxjava.R;
import hero.rxjava.mvp.model.PhotoDir;

/**
 * Created by hero on 2016/11/18 0018.
 */

public class GalleryDirsAdapter extends BaseAdapter {
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
    public View getView(int position, View convertView, ViewGroup parent) {
        PhotoDir item = mPhotoDirs.get(position);
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
        holder.tv_dir_name.setText(item.getName());
        Glide.with(mContext).load(item.getFirstPhotoPath()).override(200,200).into(holder.iv_dir_image);
        return convertView;
    }
    private class ImgViewHolder{
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
