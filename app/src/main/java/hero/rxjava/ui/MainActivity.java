package hero.rxjava.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import hero.rxjava.R;
import hero.rxjava.mvp.iview.IMainActivityView;
import hero.rxjava.mvp.presenter.GalleryActivityPresenter;
import hero.rxjava.mvp.presenter.MainActivityPresenter;
import hero.rxjava.ui.base.BaseActivity;
import hero.rxjava.ui.gallery.GalleryActivity;
import hero.rxjava.utils.LogUtils;


public class MainActivity extends BaseActivity<IMainActivityView,MainActivityPresenter> implements IMainActivityView {
    private final int TO_GALLERY = 0x001;
    private Button btn_start;
    private Button btn_release;
    private Button btn_gallery;
    private Button btn_rv_gallery;
     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }
    private void initView(){
        btn_release = (Button) findViewById(R.id.btn_release);
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_gallery = (Button)findViewById(R.id.btn_gallery);
        btn_rv_gallery = (Button)findViewById(R.id.btn_rv_gallery);
        btn_rv_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(MainActivity.this, RecyclerGalleryActivity.class);
//                startActivity(intent);
            }
        });
        btn_gallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GalleryActivity.class);
                startActivityForResult(intent,TO_GALLERY);
            }
        });
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.method();
            }
        });
        btn_release.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mPresenter.dispose();
            }
        });
    }

    @Override
    protected MainActivityPresenter createPresenter() {
        return new MainActivityPresenter();
    }

    @Override
    public void updateUI() {
        Log.d("Hero_____MainActivity", "更新了UI");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case TO_GALLERY: {
                    LogUtils.d("Hero_____MainActivity","选中了图片："+GalleryActivityPresenter.getInstance().getSelectedPhotos().toArray().toString());
                    break;
                }
            }
        }
        //应该在被选中的数据使用完毕后释放里面的数据
        GalleryActivityPresenter.destroy();
    }
}
