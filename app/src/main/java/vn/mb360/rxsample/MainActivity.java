package vn.mb360.rxsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Predicate;
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
         * 2. Introducing Operator
         *
         * Basic Observable, Observer, Subscriber example
         * Observable emits list of animal names
         * filter() operator filters the data by applying a conditional statement.
         * The data which meets the condition will be emitted and the remaining will be ignored.
         * */

        // Observable
        Observable<String> animalObservable = getAnimalObservable();

        // Observer
        Observer<String> animalObserver = getAnimalObserver();

        animalObservable
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .filter(new Predicate<String>() {
                    @Override
                    public boolean test(String s) throws Exception {
                        // lọc những object nào có chữ f ở đầu
                        return s.toLowerCase().startsWith("f");
                    }
                })
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
