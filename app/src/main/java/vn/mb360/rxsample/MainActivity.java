package vn.mb360.rxsample;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;

import com.jakewharton.rxbinding2.view.RxView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    @BindView(R.id.button)
    Button button;
    @BindView(R.id.tvOne)
    TextView tvOne;
    @BindView(R.id.tvTwo)
    TextView tvTwo;

    private Disposable disposable;
    private Unbinder unbinder;
    private int maxTaps = 0;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        unbinder = ButterKnife.bind(this);

        /*
         * ANDROIDHIVE.INFO
         * RxJava introduction to Buffer
         */
        RxView.clicks(button)
                .map(new Function<Object, Integer>() {
                    @Override
                    public Integer apply(Object o) throws Exception {
                        return 1;
                    }
                })
                .buffer(500, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        disposable = d;
                    }

                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onNext(List<Integer> integers) {
                        Log.d(TAG, "onNext: " + integers.size() + " taps received.");
                        if (integers.size() > 0) {
                            // So sánh nếu số integer.size lớn hơn thì gán vô ko thì giữ như cũ.
                            maxTaps = integers.size() > maxTaps ? integers.size() : maxTaps;
                            tvOne.setText(String.format("Received %d in 5 seconds.", integers.size()));
                            tvTwo.setText(String.format("Maximum of %d received in this session.", maxTaps));
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: " + e.getMessage());
                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

        // Concat()
        Observable
                .concat(getMaleObservable(), getFemaleObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "CONCAT:");
                    }

                    @Override
                    public void onNext(User user) {
                        Log.d(TAG, user.getName() + " là " + user.getGender());
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        Log.d(TAG, "onComplete");
                    }
                });

        // Merge - chay ko theo thu tu
        Observable
                .merge(getMaleObservable(), getFemaleObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new Observer<User>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        Log.d(TAG, "MERGE:");
                    }

                    @Override
                    public void onNext(User user) {
                        Log.d(TAG, user.getName() + " là " + user.getGender());

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private Observable<User> getMaleObservable() {
        String[] names = new String[]{"Trúc Xinh Bên Bờ", "Anh Bải", "Đàm Lê Vinh"};

        List<User> users = new ArrayList<>();
        for (String name : names) {
            User user = new User();
            user.setName(name);
            user.setGender("trai thẳng!");
            users.add(user);
        }
        return Observable
                .create(new ObservableOnSubscribe<User>() {
                    @Override
                    public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                        for (User user : users) {
                            if (!emitter.isDisposed()) {
                                Thread.sleep(1000);
                                emitter.onNext(user);
                            }
                        }

                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        }
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    private Observable<User> getFemaleObservable() {
        String[] names = new String[]{"Thúy Loan", "Chị Dậu", "Bích Nụ", "Mỹ Lệ", "Khánh Thi", "A Dẹo", "Kiều Sương", "Lô Đề"};

        List<User> users = new ArrayList<>();
        for (String name : names) {
            User user = new User();
            user.setName(name);
            user.setGender("con gái nè mấy mẹ!");
            users.add(user);
        }
        return Observable
                .create(new ObservableOnSubscribe<User>() {
                    @Override
                    public void subscribe(ObservableEmitter<User> emitter) throws Exception {
                        for (User user : users) {
                            if (!emitter.isDisposed()) {
                                Thread.sleep(4500);
                                emitter.onNext(user);
                            }
                        }

                        if (!emitter.isDisposed()) {
                            emitter.onComplete();
                        }
                    }
                })
                .subscribeOn(Schedulers.io());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Sau khi sử dụng disposable này xong 1 lần thì bỏ đi, khi hủy Activity
        unbinder.unbind();
        disposable.dispose();
    }

    class User {
        String name;
        String gender;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getGender() {
            return gender;
        }

        public void setGender(String gender) {
            this.gender = gender;
        }
    }
}
