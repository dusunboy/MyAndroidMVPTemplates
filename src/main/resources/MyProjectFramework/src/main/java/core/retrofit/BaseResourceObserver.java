package $Package.core.retrofit;

import com.google.gson.JsonObject;
import $Package.R;
import $Package.core.base.BaseApp;
import $Package.core.fuction.LogUtil;
import $Package.core.view.CustomToast;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import io.reactivex.observers.ResourceObserver;

/**
 * retrofit2 ResourceObserver的基类
 * Created by Vincent on $Time.
 */
public abstract class BaseResourceObserver<T> extends ResourceObserver<T> {

    private static final String SOCKET_TIMEOUT_EXCEPTION = BaseApp.getAppContext().getString(R.string.network_socket_timeout);
    private static final String CONNECT_EXCEPTION = BaseApp.getAppContext().getString(R.string.network_connection);
    private static final String UNKNOWN_HOST_EXCEPTION = BaseApp.getAppContext().getString(R.string.network_connection);


    @Override
    public void onNext(T t) {
        if(t instanceof JsonObject) {
            LogUtil.i(t.toString());
        }
    }

    @Override
    public void onError(Throwable e) {
        if (e instanceof SocketTimeoutException) {
            CustomToast.getInstance().show(SOCKET_TIMEOUT_EXCEPTION);
            LogUtil.e("onError: SocketTimeoutException----" + SOCKET_TIMEOUT_EXCEPTION);
        } else if (e instanceof ConnectException) {
            CustomToast.getInstance().show(CONNECT_EXCEPTION);
            LogUtil.e("onError: ConnectException-----" + CONNECT_EXCEPTION);
        } else if (e instanceof UnknownHostException) {
            CustomToast.getInstance().show(UNKNOWN_HOST_EXCEPTION);
            LogUtil.e("onError: UnknownHostException-----" + UNKNOWN_HOST_EXCEPTION);
        } else {
            CustomToast.getInstance().show(e.getMessage());
            LogUtil.e("onError:----" + e.getMessage());
        }
    }


}
