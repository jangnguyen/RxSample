package vn.mb360.rxsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*
         * ANDROIDHIVE.INFO
         * Android Introduction To Reactive Programming – RxJava, RxAndroid
         * 3. Introducing Multiple Observer and CompositeDisposable
         *
         * Basic Observable, Observer, Subscriber example
         * Observable emits list of animal names
         * filter() operator filters the data by applying a conditional statement.
         * animal names that starts with letter `b`
         * */

        // Observable
        Observable<String> animalObservable = getAnimalObservable();

        // Observer
        DisposableObserver<String> animalObserver = getAnimalObserver();

        DisposableObserver<String> animalObserverAllCaps = getAnimalAllCapsObserver();

        compositeDisposable.add(
                animalObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String s) throws Exception {
                                return s.toLowerCase().startsWith("b");
                            }
                        })
                        .subscribeWith(animalObserver)
        );

        compositeDisposable.add(
                animalObservable
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .filter(new Predicate<String>() {
                            @Override
                            public boolean test(String s) throws Exception {
                                return s.toLowerCase().startsWith("a");
                            }
                        })
                        .map(new Function<String, String>() {
                            @Override
                            public String apply(String s) throws Exception {
                                return s.toUpperCase();
                            }
                        })
                        .subscribeWith(animalObserverAllCaps)
        );
    }

    private DisposableObserver<String> getAnimalAllCapsObserver() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Log.d(TAG, "ON_NEXT: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "ON_ERROR: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "ALL ITEM ARE EMITTED!");
            }
        };
    }

    private DisposableObserver<String> getAnimalObserver() {
        return new DisposableObserver<String>() {
            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "All items are emitted!");
            }
        };
    }


    private Observable<String> getAnimalObservable() {
        return Observable.fromArray("Ant", "Ape", "Bat", "Bear", "ButterKnife", "Bee", "Cat", "Crab", "Cod", "Dog", "Dove", "Frog", "Fox");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Sau khi sử dụng disposable này xong 1 lần thì bỏ đi, khi hủy Activity
        compositeDisposable.clear();
    }
}
