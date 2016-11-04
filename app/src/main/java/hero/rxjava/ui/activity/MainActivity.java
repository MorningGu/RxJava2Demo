package hero.rxjava.ui.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import hero.rxjava.R;
import hero.rxjava.mvp.iview.IMainActivityView;
import hero.rxjava.mvp.presenter.MainActivityPresenter;
import hero.rxjava.ui.base.BaseActivity;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseActivity<IMainActivityView,MainActivityPresenter> implements IMainActivityView {
    private Button btn_start;
    private Button btn_release;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        doBackPressure();
//        observableDoBackPressure();
        initView();
    }
    private void initView(){
        btn_release = (Button) findViewById(R.id.btn_release);
        btn_start = (Button) findViewById(R.id.btn_start);
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

    private void observableDoBackPressure(){
        Observable.range(1,10000)
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d("JG", integer.toString());
                        Thread.sleep(100);
                    }
                });
    }
    private void doBackPressure(){
        Flowable<Integer> flowable = Flowable.create(new FlowableOnSubscribe<Integer>() {

                    @Override
                    public void subscribe(FlowableEmitter<Integer> e) throws Exception {

                        for(int i=0;i<10000;i++){
                            e.onNext(i);
                        }
                        e.onComplete();
                    }
                }, BackpressureStrategy.DROP); //指定背压处理策略，抛出异常

        flowable.subscribeOn(Schedulers.computation())
                .observeOn(Schedulers.newThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer integer) throws Exception {
                        Log.d("JG", integer.toString());
                        Thread.sleep(100);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        Log.d("JG",throwable.toString());
                    }
                });
    }

    @Override
    public void updateUI() {
        Log.d("JG", "更新了UI");
    }

}
