package vn.mb360.rxsample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Function;
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
         * 5. Custom Data Type, Operators
         * Basic Observable, Observer, Subscriber example
         * You can also notice we got rid of the below declarations
         * Observable<Note> notesObservable = getNotesObservable();
         * DisposableObserver<Note> notesObserver = getNotesObserver();
         */
        compositeDisposable.add(
                getNotesObservable()
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .map(new Function<Note, Note>() {
                            @Override
                            public Note apply(Note note) throws Exception {
                                note.setNote(note.getNote().toUpperCase());
                                return note;
                            }
                        })
                        .subscribeWith(getNoteObserver())
        );
    }

    private Observable<Note> getNotesObservable() {
        final List<Note> notes = prepareNotes();

        return Observable.create(new ObservableOnSubscribe<Note>() {
            @Override
            public void subscribe(ObservableEmitter<Note> emitter) throws Exception {
                for (Note note :
                        notes) {
                    if (!emitter.isDisposed()) {
                        emitter.onNext(note);
                    }
                }

                if (!emitter.isDisposed()) {
                    emitter.onComplete();
                }
            }
        });
    }

    private List<Note> prepareNotes() {
        List<Note> notes = new ArrayList<>();
        notes.add(new Note(1, "Làm mấy bài tập về RxJava"));
        notes.add(new Note(2, "Nhận giao hàng của Tiki"));
        notes.add(new Note(3, "Viết tài liệu chia sẻ về kiến thức reactive programming"));
        notes.add(new Note(4, "Dịch CV sang tiếng Anh, lâu lắm oy"));
        notes.add(new Note(5, "Bóc tem hộp bình nước Lock&Lock ahihi"));

        return notes;
    }

    // Nơi nhân dữ liệu phát ra
    private DisposableObserver<Note> getNoteObserver() {
        return new DisposableObserver<Note>() {
            @Override
            public void onNext(Note note) {
                Log.d(TAG, "onNote: "  + " - + note.getNote());
            }

            @Override
            public void onError(Throwable e) {
                Log.e(TAG, "onError: " + e.getMessage());
            }

            @Override
            public void onComplete() {
                Log.d(TAG, "onComplete: all notes are committed");
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Sau khi sử dụng disposable này xong 1 lần thì bỏ đi, khi hủy Activity
        compositeDisposable.clear();
    }

    class Note {
        int id;
        String note;

        public Note(int id, String note) {
            this.id = id;
            this.note = note;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getNote() {
            return note;
        }

        public void setNote(String note) {
            this.note = note;
        }
    }
}
