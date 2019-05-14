package $Package.core.async_http;

import android.content.Context;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cz.msebera.android.httpclient.HttpEntity;

/**
 * 异步HTTP请求工具类
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class AsyncHttpReq {

    private static AsyncHttpClient client = new AsyncHttpClient();

    /**
     * relative url
     * @param url
     * @param params
     * @param responseHandler
     */
    public static void get(Context context, String url, RequestParams params, AsyncHttpResponseHandler responseHandler) {
    	client.get(context, url, params, responseHandler);
    }
    
    /**
     * relative url
     * @param context
     * @param url
     * @param object
     * @param contentType
     * @param responseHandler
     */
    public static void post(Context context, String url, Object object, String contentType, AsyncHttpResponseHandler responseHandler) {
        if (object instanceof RequestParams) {
            client.post(context, url, (RequestParams) object, responseHandler);
        } else {
            client.post(context, url, (HttpEntity) object, contentType, responseHandler);
        }
    }

    /**
     * relative url
     * @param url
     * @param fileAsyncHttpResponseHandler
     */
    public static void getFile(Context context, String url, FileAsyncHttpResponseHandler fileAsyncHttpResponseHandler) {
        client.get(context, url, fileAsyncHttpResponseHandler);
    }

    /**
     * relative url
     * @param url
     * @param fileAsyncHttpResponseHandler
     */
    public static void postFile(Context context, String url, Object object, String contentType, FileAsyncHttpResponseHandler fileAsyncHttpResponseHandler) {
        if (object instanceof RequestParams) {
            client.post(context, url, (RequestParams) object, fileAsyncHttpResponseHandler);
        } else {
            client.post(context, url, (HttpEntity) object, contentType, fileAsyncHttpResponseHandler);
        }
    }

    /**
     * Allows you to cancel all requests currently in queue or running, by set TAG,
     * if passed TAG is null, will not attempt to cancel any requests, if TAG is null
     * on RequestHandle, it cannot be canceled by this call
     *
     * @param TAG                   TAG to be matched in RequestHandle
     * @param mayInterruptIfRunning specifies if active requests should be cancelled along with
     *                              pending requests.
     */
    public static void cancelRequestsByTAG(Object TAG, boolean mayInterruptIfRunning) {
        client.cancelRequestsByTAG(TAG, mayInterruptIfRunning);
    }

    /**
     * Cancels all pending (or potentially active) requests. <p>&nbsp;</p> <b>Note:</b> This will
     * only affect requests which were created with a non-null android Context. This method is
     * intended to be used in the onDestroy method of your android activities to destroy all
     * requests which are no longer required.
     *
     * @param mayInterruptIfRunning specifies if active requests should be cancelled along with
     *                              pending requests.
     */
    public static void cancelAllRequests(boolean mayInterruptIfRunning) {
    	client.cancelAllRequests(mayInterruptIfRunning);
	}
    
    /**
     * relative url
     * @param context
     * @param url
     * @param responseHandler
     */
    public static void delete(Context context, String url, AsyncHttpResponseHandler responseHandler){
    	client.delete(context, url, responseHandler);
    }

    /**
     * 添加Header标识头
     * @param key
     * @param value
     */
    public static void addHeader(String key, String value) {
        client.addHeader(key, value);
    }

    /**
     * 清除所有标识头
     */
    public static void removeAllHeaders() {
        client.removeAllHeaders();
    }

    /**
     * 清除指定标识头
     * @param key
     */
    public static void removeHeader(String key) {
        client.removeHeader(key);
    }

    /**
     * 请求超时时间,如果不设置则使用重连策略的超时时间,默认3000ms
     * @param timeout
     */
    public static void timeout(int timeout) {
        client.setTimeout(timeout);
    }
}
