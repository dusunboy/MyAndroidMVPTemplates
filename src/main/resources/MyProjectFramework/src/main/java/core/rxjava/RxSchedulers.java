package $Package.core.rxjava;

import org.reactivestreams.Publisher;

import io.reactivex.Flowable;
import io.reactivex.FlowableTransformer;
import io.reactivex.Observable;
import io.reactivex.ObservableSource;
import io.reactivex.ObservableTransformer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;

/**
 * Rx调度器类
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class RxSchedulers {

    /**
     * io->mainThread
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> io_mainThread() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * io->mainThread
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T, T> flowable_io_mainThread() {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * io->io
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> io_io() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io());
            }
        };
    }

    /**
     * io->io
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T, T> flowable_io_io() {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.io())
                        .observeOn(Schedulers.io());
            }
        };
    }

    /**
     * newThread->mainThread
     * @param <T>
     * @return
     */
    public static <T> ObservableTransformer<T, T> newThread_mainThread() {
        return new ObservableTransformer<T, T>() {
            @Override
            public ObservableSource<T> apply(Observable<T> upstream) {
                return upstream.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }

    /**
     * newThread->mainThread
     * @param <T>
     * @return
     */
    public static <T> FlowableTransformer<T, T> flowable_newThread_mainThread() {
        return new FlowableTransformer<T, T>() {
            @Override
            public Publisher<T> apply(Flowable<T> upstream) {
                return upstream.subscribeOn(Schedulers.newThread())
                        .observeOn(AndroidSchedulers.mainThread());
            }
        };
    }
}
