package hero.rxjava.utils;

import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

/**
 * Created by hero on 2016/11/25 0025.
 */

public class ViewUtils {
    /**
     * 目前该方法只支持预计算宽高设置为准确值或wrap_content的情况，
     * 不支持match_parent的情况，因为view的父view还未预计算出宽高
     * @param v 要预计算的view
     */
    public static void measureView(View v) {
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
     * 隐藏状态栏 不更改布局 针对api14以上的版本
     * @param window
     */
    public static void hideStatusBar(Window window){
        int uiOptions = View.SYSTEM_UI_FLAG_LAYOUT_STABLE| //保持布局状态
//                //布局位于状态栏下方
//                View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION|
//                //全屏
                View.SYSTEM_UI_FLAG_FULLSCREEN;
//                //隐藏导航栏
//                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION|
//                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
            //对导航栏的显示的弱化
//        if (Build.VERSION.SDK_INT>=19){
//            uiOptions |= 0x00001000;
//        }else{
//            uiOptions |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
//        }
        window.getDecorView().setSystemUiVisibility(uiOptions);
    }

    /**
     * 显示状态栏
     * @param window
     */
    public static void showStatusBar(Window window){
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
    }
}
