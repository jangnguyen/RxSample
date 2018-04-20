package vn.mb360.rxsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private Disposable disposable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
         * ANDROIDHIVE.INFO
         * Android Introduction To Reactive Programming – RxJava, RxAndroid
         * 2. Introducing Disposable
         *
         * Basic Observable, Observer, Subscriber example
         * Observable emits list of animal names
         * You can see Disposable introduced in this example
         * */

        // Observable
        Observable<String> animalObservable = getAnimalObservable();

        // Observer
        Observer<String> animalObserver = getAnimalObserver();

        animalObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(animalObserver);
    }

    private Observer<String> getAnimalObserver() {
        return new Observer<String>() {
            @Override
            public void onSubscribe(Disposable d) {
                Log.d(TAG, "onSubscribe");
                disposable = d;
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: Name = " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: All item emitted.");
            }
        };
    }

    private Observable<String> getAnimalObservable() {
        return Observable.just("Ant", "Bee", "Cat", "Dog", "Fox");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Sau khi sử dụng disposable này xong 1 lần thì bỏ đi, khi hủy Activity
        disposable.dispose();
    }
}