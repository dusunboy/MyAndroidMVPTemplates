package $Package.project.retrofit_config;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import $Package.R;
import $Package.core.view.CustomToast;

import java.lang.ref.WeakReference;

import cz.msebera.android.httpclient.Header;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;

/**
 * AsyncHttp重试策略
 * Created by Vincent on $Time.
 */
public class MyAsyncRetryPolicy {
    private final WeakReference<RxAppCompatActivity> activityInstance;

    public MyAsyncRetryPolicy(RxAppCompatActivity activity) {
        this.activityInstance = new WeakReference<RxAppCompatActivity>(activity);
    }

    public MyAsyncRetryPolicy() {
        activityInstance = null;
    }

    public void retry(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
        if (activityInstance != null) {
            RxAppCompatActivity activity = activityInstance.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }
            if (statusCode == 401) {
//                authenticate
            } else if (statusCode == 404) {
                Observable.empty()
                        .compose(activity.bindToLifecycle())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Object>() {
                            @Override
                            public void accept(@NonNull Object object) throws Exception {
                                CustomToast.getInstance().show(R.string.network_error + ":" + "404");
                            }
                        });
            }
        }
    }
}
