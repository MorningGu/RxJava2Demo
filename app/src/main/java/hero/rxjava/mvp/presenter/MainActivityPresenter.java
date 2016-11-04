package hero.rxjava.mvp.presenter;


import android.util.Log;

import hero.rxjava.mvp.iview.IMainActivityView;
import hero.rxjava.retrofit.ApiManager;
import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.subscribers.ResourceSubscriber;

/**
 * Created by hero on 2016/6/17 0017.
 */
public class MainActivityPresenter extends BasePresenter<IMainActivityView> {
//    private ResourceSubscriber subscriber;
    public void method(){
        doBackPressure();
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
        }, BackpressureStrategy.DROP); //指定背压处理策略
        ResourceSubscriber resultSubscriber = new ResourceSubscriber<Integer>() {
            @Override
            public void onNext(Integer integer) {
                Log.d("ResourceSubscriber", integer.toString());
                IMainActivityView view = getView();
                if(view!=null){
                    view.updateUI();
                }
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(Throwable t) {
                this.dispose();
                Log.d("ResourceSubscriber",t.toString());
            }

            @Override
            public void onComplete() {
                this.dispose();
                Log.d("ResourceSubscriber","onComplete");
            }
        };
        addSubscription(ApiManager.INSTANCE.startObservable(flowable,resultSubscriber));
    }

//    @Override
//    public void dispose() {
//        if(subscriber!=null && !subscriber.isDisposed()){
//            subscriber.dispose();
//        }
//    }
}
