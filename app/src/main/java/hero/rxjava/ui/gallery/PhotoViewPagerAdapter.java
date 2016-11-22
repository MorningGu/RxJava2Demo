package hero.rxjava.ui.gallery;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.List;

import hero.rxjava.Config;
import hero.rxjava.R;
import hero.rxjava.mvp.model.gallery.Photo;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by hero on 2016/11/14 0014.
 */

public class PhotoViewPagerAdapter extends PagerAdapter {

        private Context mContext;
        private List<Photo> mPhotos;
        public PhotoViewPagerAdapter(Context context,List<Photo> photos){
            this.mContext = context;
            this.mPhotos = photos;
        }


        @Override
        public int getCount() {
            return mPhotos.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            PhotoView photoView = new PhotoView(container.getContext());
            photoView.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Glide.with(mContext)
                    .load(mPhotos.get(position).getPath())
                    .override(Config.IMAGE_BIG,Config.IMAGE_BIG)
                    .error(R.mipmap.ic_launcher)
                    .into(photoView);
            photoView.setOnViewTapListener(new PhotoViewAttacher.OnViewTapListener() {
                @Override
                public void onViewTap(View view, float x, float y) {
                    //点击图片 退出本页面
                    ((Activity)mContext).finish();
                }
            });
            // Now just add PhotoView to ViewPager and return it
            container.addView(photoView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            return photoView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

}
