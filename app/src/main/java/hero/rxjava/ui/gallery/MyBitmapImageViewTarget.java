package hero.rxjava.ui.gallery;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.BitmapImageViewTarget;

/**
 * Created by hero on 2016/11/8 0008.
 */

public class MyBitmapImageViewTarget extends BitmapImageViewTarget {
    public MyBitmapImageViewTarget(ImageView view) {
        super(view);
    }

    @Override
    public void onResourceReady(Bitmap bitmap, GlideAnimation anim) {
        if (bitmap != null && view.getScaleType() != ImageView.ScaleType.FIT_CENTER) {
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        super.onResourceReady(bitmap, anim);
    }

    @Override
    protected void setResource(Bitmap resource) {
        super.setResource(resource);
    }

    @Override
    public void onLoadFailed(Exception e, Drawable errorDrawable) {
        if (errorDrawable != null && view != null && view.getScaleType() != ImageView.ScaleType.FIT_CENTER) {
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        super.onLoadFailed(e, errorDrawable);
    }

    @Override
    public void onLoadStarted(Drawable placeholder) {
        if (placeholder != null && placeholder != null && view != null && view.getScaleType() != ImageView.ScaleType.FIT_CENTER) {
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        super.onLoadStarted(placeholder);
    }

    @Override
    public void onLoadCleared(Drawable placeholder) {
        if (placeholder != null && placeholder != null && view != null && view.getScaleType() != ImageView.ScaleType.FIT_CENTER) {
            view.setScaleType(ImageView.ScaleType.FIT_CENTER);
        }
        super.onLoadCleared(placeholder);
    }

}
