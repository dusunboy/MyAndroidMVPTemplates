package $Package.core.rxbus;

import io.reactivex.Flowable;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Predicate;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;

/**
 * 用于替换EventBus的RxBus实现,同时用做Http响应数据的分发
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class MyRxBus {

    private MyRxBus() {
    }

    private static volatile MyRxBus mInstance;

    public static MyRxBus getDefault() {
        //多线程单例模式
        if (mInstance == null) {
            synchronized (MyRxBus.class) {
                if (mInstance == null) {
                    mInstance = new MyRxBus();
                }
            }
        }
        return mInstance;
    }


    private final FlowableProcessor<Object> bus = PublishProcessor.create().toSerialized();

    public void post(Object event) {
        bus.onNext(event);
    }

    public <T> Flowable<T> take(final Class<T> eventType) {
        return bus.filter(new Predicate<Object>() {
            @Override
            public boolean test(@NonNull Object o) throws Exception {
                return eventType.isInstance(o);
            }
        }).cast(eventType);
    }

    public void close() {
        staticClose();
    }

    private static void staticClose() {
        mInstance = null;
    }
}
