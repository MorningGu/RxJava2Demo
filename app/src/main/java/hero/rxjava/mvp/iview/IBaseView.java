package hero.rxjava.mvp.iview;

/**
 * Created by hero on 2016/5/3 0003.
 */
public interface IBaseView {

    /**
     * 显示更新的弹窗
     * @param isForce 是否为强制更新
     * @param url 下载地址
     */
    void showUpdateDialog(boolean isForce, String url);

}
