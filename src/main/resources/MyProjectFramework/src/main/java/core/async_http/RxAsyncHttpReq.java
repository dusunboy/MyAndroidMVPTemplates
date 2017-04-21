package $Package.core.async_http;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.FileAsyncHttpResponseHandler;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import $Package.core.base.BaseApp;
import $Package.core.config.BaseConstant;
import $Package.core.fuction.LogUtil;
import $Package.core.fuction.SPUtil;
import $Package.project.retrofit_config.MyAsyncRetryPolicy;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.entity.StringEntity;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.processors.PublishProcessor;
import io.reactivex.schedulers.Schedulers;

/**
 * RxAsyncHttp封装类
 * Created by Vincent on $Time.
 */
public class RxAsyncHttpReq {


    //30s
    private static final int TIME_OUT = 30000;

    public Flowable<AsyncHttpResponse> get(RxAppCompatActivity activity, String url, MyRequestParams myRequestParams) {
        String original_url = url;
        if (!url.contains("http://") && !url.contains("https://")) {
            url = SPUtil.getString(BaseConstant.BASE_URL) + url;
        }
        return new Builder(activity)
                .url(url)
                .httpMethod(Method.GET)
                .params(myRequestParams)
                .timeout(TIME_OUT)
                .setTag(activity.getLocalClassName())
                .retryPolicy(new MyAsyncRetryPolicy(activity))
                .getResult()
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<AsyncHttpResponse>() {
                    @Override
                    public void accept(@NonNull AsyncHttpResponse asyncHttpResponse) throws Exception {
                        LogUtil.i(original_url + LogUtil.NETWORK_DATA_SEPARATOR + asyncHttpResponse.getStrResponse());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        LogUtil.i(original_url + LogUtil.NETWORK_DATA_SEPARATOR + throwable.getMessage());
                    }
                });
    }


    public Flowable<AsyncHttpResponse> post(RxAppCompatActivity activity, String url, MyRequestParams myRequestParams) {
        String original_url = url;
        if (!url.contains("http://") && !url.contains("https://")) {
            url = SPUtil.getString(BaseConstant.BASE_URL) + url;
        }
        return new Builder(activity)
                .url(url)
                .httpMethod(Method.POST)
                .params(myRequestParams)
                .timeout(TIME_OUT)
                .setTag(activity.getLocalClassName())
                .retryPolicy(new MyAsyncRetryPolicy(activity))
                .getResult()
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<AsyncHttpResponse>() {
                    @Override
                    public void accept(@NonNull AsyncHttpResponse asyncHttpResponse) throws Exception {
                        LogUtil.i(original_url + LogUtil.NETWORK_DATA_SEPARATOR + asyncHttpResponse.getStrResponse());
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        LogUtil.i(original_url + LogUtil.NETWORK_DATA_SEPARATOR + throwable.getMessage());
                    }
                });
    }


    public ArrayList<FlowableProcessor<AsyncHttpResponse>> download(RxAppCompatActivity activity, String url, MyRequestParams myRequestParams) {
        String original_url = url;
        if (!url.contains("http://") && !url.contains("https://")) {
            url = SPUtil.getString(BaseConstant.BASE_URL) + url;
        }
        return new Builder(activity)
                .url(url)
                .httpMethod(Method.DOWNLOAD)
                .params(myRequestParams)
                .timeout(TIME_OUT)
                .setTag(activity.getLocalClassName())
                .retryPolicy(new MyAsyncRetryPolicy(activity))
                .getResultWithProgress();
    }


    public ArrayList<FlowableProcessor<AsyncHttpResponse>> upload(RxAppCompatActivity activity, String url,
                                                                  MyRequestParams myRequestParams) {
        String original_url = url;
        if (!url.contains("http://") && !url.contains("https://")) {
            url = SPUtil.getString(BaseConstant.BASE_URL) + url;
        }
        return new Builder(activity)
                .url(url)
                .httpMethod(Method.UPLOAD)
                .params(myRequestParams)
                .timeout(TIME_OUT)
                .setTag(activity.getLocalClassName())
                .retryPolicy(new MyAsyncRetryPolicy(activity))
                .getResultWithProgress();
    }

    /**
     * 支持的请求方式
     */
    public interface Method {
        int GET = 0;
        int POST = 1;
        int PUT = 2;
        int DELETE = 3;
        int HEAD = 4;
        int OPTIONS = 5;
        int TRACE = 6;
        int PATCH = 7;
        int DOWNLOAD = 8;
        int UPLOAD = 9;
    }

    /**
     * 构建器
     */
    public static class Builder {

        private final WeakReference<RxAppCompatActivity> activityInstance;
        private String url;
        private int httpMethod;
        private MyRequestParams myRequestParams;
        private Object tag;
        private String encoding = "UTF-8";
        private String contentType = "application/json";
        private MyAsyncRetryPolicy myAsyncRetryPolicy;
        private FlowableProcessor<AsyncHttpResponse> progressSerializedSubject;
        private PublishProcessor<AsyncHttpResponse> publishProcessor = PublishProcessor.create();
        private FlowableProcessor<AsyncHttpResponse> responseSerializedSubject = publishProcessor.toSerialized();

        public Builder(RxAppCompatActivity activity) {
            this.activityInstance = new WeakReference<RxAppCompatActivity>(activity);
        }

        /**
         * 网络请求接口url
         *
         * @param url
         * @return
         */
        public Builder url(String url) {
            this.url = url;
            return this;
        }

        /**
         * http请求方式
         *
         * @param httpMethod GET/POST/PUT/DELETE/HEAD/OPTIONS/TRACE/PATCH
         * @return
         */
        public Builder httpMethod(int httpMethod) {
            this.httpMethod = httpMethod;
            return this;
        }

        /**
         * Http请求参数
         *
         * @param myRequestParams
         * @return
         */
        public Builder params(MyRequestParams myRequestParams) {
            this.myRequestParams = myRequestParams;
            return this;
        }

        /**
         * 每个request可以设置一个标志,用于在cancel()时找到
         *
         * @param tag
         * @return
         */
        public Builder setTag(Object tag) {
            this.tag = tag;
            return this;
        }

        /**
         * 编码,默认UTF-8
         *
         * @param encoding
         * @return
         */
        public Builder encoding(String encoding) {
            this.encoding = encoding;
            return this;
        }

        /**
         * 参数的类型:
         *
         * @param contentType application/json
         * @return
         */
        public Builder contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        /**
         * 添加Header标识头
         *
         * @param key
         * @param value
         */
        public Builder addHeader(String key, String value) {
            AsyncHttpReq.addHeader(key, value);
            return this;
        }

        /**
         * 清除所有标识头
         */
        public void removeAllHeaders() {
            AsyncHttpReq.removeAllHeaders();
        }

        /**
         * 清除指定标识头
         *
         * @param key
         */
        public void removeHeader(String key) {
            AsyncHttpReq.removeHeader(key);
        }

        /**
         * 取消所有请求
         *
         * @param mayInterruptIfRunning
         */
        public static void cancelAllRequests(boolean mayInterruptIfRunning) {
            AsyncHttpReq.cancelAllRequests(mayInterruptIfRunning);
        }

        /**
         * 通过标签取消请求
         *
         * @param TAG
         * @param mayInterruptIfRunning
         */
        public static void cancelRequestsByTAG(Object TAG, boolean mayInterruptIfRunning) {
            AsyncHttpReq.cancelRequestsByTAG(TAG, mayInterruptIfRunning);
        }

        /**
         * 请求超时时间,如果不设置则使用重连策略的超时时间,默认3000ms
         */
        public Builder timeout(int timeout) {
            AsyncHttpReq.timeout(timeout);
            return this;
        }

        /**
         * 重试策略
         *
         * @param myAsyncRetryPolicy
         * @return
         */
        public Builder retryPolicy(MyAsyncRetryPolicy myAsyncRetryPolicy) {
            this.myAsyncRetryPolicy = myAsyncRetryPolicy;
            return this;
        }

        public FlowableProcessor<AsyncHttpResponse> getResult() {
            doTask();
            return responseSerializedSubject;
        }

        public ArrayList<FlowableProcessor<AsyncHttpResponse>> getResultWithProgress() {
            PublishProcessor<AsyncHttpResponse> asyncHttpResponsePublishProcessor = PublishProcessor.create();
            progressSerializedSubject = asyncHttpResponsePublishProcessor.toSerialized();;
            doTask();
            ArrayList<FlowableProcessor<AsyncHttpResponse>> observables = new ArrayList<>();
            observables.add(responseSerializedSubject);
            observables.add(progressSerializedSubject);
            return observables;
        }

        private void doTask() {
            switch (httpMethod) {
                case Method.GET:
                    AsyncHttpResponseHandler asyncHttpResponseHandler = new AsyncHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                AsyncHttpResponse asyncHttpResponse = new AsyncHttpResponse();
                                String strResponse;
                                try {
                                    strResponse = new String(responseBody, encoding);
                                } catch (UnsupportedEncodingException e) {
                                    responseSerializedSubject.onError(e);
                                    return;
                                }
                                try {
                                    asyncHttpResponse.setJsonObject(parseJson(strResponse));
                                } catch (Exception e) {
                                }
                                asyncHttpResponse.setHeaders(headers);
                                asyncHttpResponse.setStatusCode(statusCode);
                                asyncHttpResponse.setStrResponse(strResponse);
                                responseSerializedSubject.onNext(asyncHttpResponse);
                            } else {
                                responseSerializedSubject.onError(new Throwable());
                                myAsyncRetryPolicy.retry(statusCode, headers, responseBody, null);
                            }
                            responseSerializedSubject.onComplete();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            if (statusCode == 200) {
                                AsyncHttpResponse asyncHttpResponse = new AsyncHttpResponse();
                                asyncHttpResponse.setStatusCode(statusCode);
                                asyncHttpResponse.setHeaders(headers);
                                try {
                                    String strResponse = new String(responseBody, encoding);
                                    asyncHttpResponse.setStrResponse(strResponse);
                                } catch (Exception e) {
                                }
                                asyncHttpResponse.setThrowable(error);
                                responseSerializedSubject.onNext(asyncHttpResponse);
                            } else {
                                responseSerializedSubject.onError(error);
                                myAsyncRetryPolicy.retry(statusCode, headers, responseBody, error);
                            }
                            responseSerializedSubject.onComplete();
                        }
                    };
                    asyncHttpResponseHandler.setTag(tag);
                    asyncHttpResponseHandler.setCharset(encoding);
                    AsyncHttpReq.get(BaseApp.getAppContext(), url, myRequestParams, asyncHttpResponseHandler);
                    break;
                case Method.POST:
                    asyncHttpResponseHandler = new AsyncHttpResponseHandler() {

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                AsyncHttpResponse asyncHttpResponse = new AsyncHttpResponse();
                                String strResponse;
                                try {
                                    strResponse = new String(responseBody, encoding);
                                } catch (UnsupportedEncodingException e) {
                                    responseSerializedSubject.onError(e);
                                    return;
                                }
                                try {
                                    asyncHttpResponse.setJsonObject(parseJson(strResponse));
                                } catch (Exception e) {
                                }
                                asyncHttpResponse.setHeaders(headers);
                                asyncHttpResponse.setStatusCode(statusCode);
                                asyncHttpResponse.setStrResponse(strResponse);
                                responseSerializedSubject.onNext(asyncHttpResponse);
                            } else {
                                responseSerializedSubject.onError(new Throwable());
                                myAsyncRetryPolicy.retry(statusCode, headers, responseBody, null);
                            }
                            responseSerializedSubject.onComplete();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            if (statusCode == 200) {
                                AsyncHttpResponse asyncHttpResponse = new AsyncHttpResponse();
                                asyncHttpResponse.setStatusCode(statusCode);
                                asyncHttpResponse.setHeaders(headers);
                                try {
                                    String strResponse = new String(responseBody, encoding);
                                    asyncHttpResponse.setStrResponse(strResponse);
                                } catch (Exception e) {
                                }
                                asyncHttpResponse.setThrowable(error);
                                responseSerializedSubject.onNext(asyncHttpResponse);
                            } else {
                                responseSerializedSubject.onError(error);
                                myAsyncRetryPolicy.retry(statusCode, headers, responseBody, error);
                            }
                            responseSerializedSubject.onComplete();
                        }
                    };
                    asyncHttpResponseHandler.setTag(tag);
                    asyncHttpResponseHandler.setCharset(encoding);
                    AsyncHttpReq.post(BaseApp.getAppContext(), url,
                            (myRequestParams.getJsonParams() == null || myRequestParams.getJsonParams().equals(""))
                                    ? myRequestParams : new StringEntity(myRequestParams.getJsonParams(), encoding),
                            contentType, asyncHttpResponseHandler);
                    break;
                case Method.DOWNLOAD:
                    RxAppCompatActivity activity = activityInstance.get();
                    if (activity == null || activity.isFinishing()) {
                        return;
                    }
                    FileAsyncHttpResponseHandler fileAsyncHttpResponseHandler = new FileAsyncHttpResponseHandler(new File(myRequestParams.getDownloadSavePath())) {

                        int progress = 0;

                        @Override
                        public void onProgress(long bytesWritten, long totalSize) {
                            int tempProgress = (totalSize > 0) ? (int) ((bytesWritten * 1.0 / totalSize) * 100) : 0;
                            if (tempProgress > 0 && tempProgress <= 100 &&
                                    tempProgress != progress) {
                                AsyncHttpResponse asyncHttpResponse = new AsyncHttpResponse();
                                asyncHttpResponse.setProgressPercent(tempProgress);
                                progressSerializedSubject.onNext(asyncHttpResponse);
                            }
                            progress = tempProgress;
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, Throwable throwable, File file) {
                            if (statusCode == 200) {
                                AsyncHttpResponse asyncHttpResponse = new AsyncHttpResponse();
                                asyncHttpResponse.setStatusCode(statusCode);
                                asyncHttpResponse.setHeaders(headers);
                                asyncHttpResponse.setThrowable(throwable);
                                asyncHttpResponse.setFile(file);
                                responseSerializedSubject.onNext(asyncHttpResponse);
                            } else {
                                responseSerializedSubject.onError(throwable);
                                myAsyncRetryPolicy.retry(statusCode, headers, null, throwable);
                            }
                            responseSerializedSubject.onComplete();
                            progressSerializedSubject.onComplete();
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, File file) {
                            if (statusCode == 200) {
                                AsyncHttpResponse asyncHttpResponse = new AsyncHttpResponse();
                                asyncHttpResponse.setHeaders(headers);
                                asyncHttpResponse.setStatusCode(statusCode);
                                asyncHttpResponse.setFile(file);
                                responseSerializedSubject.onNext(asyncHttpResponse);
                            } else {
                                responseSerializedSubject.onError(new Throwable());
                                myAsyncRetryPolicy.retry(statusCode, headers, null, null);
                            }
                            responseSerializedSubject.onComplete();
                            progressSerializedSubject.onComplete();
                        }
                    };
                    fileAsyncHttpResponseHandler.setTag(tag);
                    AsyncHttpReq.postFile(BaseApp.getAppContext(), url,
                            (myRequestParams.getJsonParams() == null || myRequestParams.getJsonParams().equals(""))
                                    ? myRequestParams : new StringEntity(myRequestParams.getJsonParams(), encoding),
                            contentType, fileAsyncHttpResponseHandler);
                    break;

                case Method.UPLOAD:
                    activity = activityInstance.get();
                    if (activity == null || activity.isFinishing()) {
                        return;
                    }
                    asyncHttpResponseHandler = new AsyncHttpResponseHandler() {

                        int progress = 0;

                        @Override
                        public void onProgress(long bytesWritten, long totalSize) {
                            int tempProgress = (totalSize > 0) ? (int) ((bytesWritten * 1.0 / totalSize) * 100) : 0;
                            if (tempProgress > 0 && tempProgress <= 100 &&
                                    tempProgress != progress) {
                                AsyncHttpResponse asyncHttpResponse = new AsyncHttpResponse();
                                asyncHttpResponse.setProgressPercent(tempProgress);
                                progressSerializedSubject.onNext(asyncHttpResponse);
                            }
                            progress = tempProgress;
                        }

                        @Override
                        public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                            if (statusCode == 200) {
                                AsyncHttpResponse asyncHttpResponse = new AsyncHttpResponse();
                                String strResponse;
                                try {
                                    strResponse = new String(responseBody, encoding);
                                } catch (UnsupportedEncodingException e) {
                                    responseSerializedSubject.onError(e);
                                    return;
                                }
                                try {
                                    asyncHttpResponse.setJsonObject(parseJson(strResponse));
                                } catch (Exception e) {
                                }
                                asyncHttpResponse.setHeaders(headers);
                                asyncHttpResponse.setStatusCode(statusCode);
                                asyncHttpResponse.setStrResponse(strResponse);
                                responseSerializedSubject.onNext(asyncHttpResponse);
                            } else {
                                responseSerializedSubject.onError(new Throwable());
                                myAsyncRetryPolicy.retry(statusCode, headers, responseBody, null);
                            }
                            responseSerializedSubject.onComplete();
                            progressSerializedSubject.onComplete();
                        }

                        @Override
                        public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                            if (statusCode == 200) {
                                AsyncHttpResponse asyncHttpResponse = new AsyncHttpResponse();
                                asyncHttpResponse.setStatusCode(statusCode);
                                asyncHttpResponse.setHeaders(headers);
                                try {
                                    String strResponse = new String(responseBody, encoding);
                                    asyncHttpResponse.setStrResponse(strResponse);
                                } catch (Exception e) {
                                }
                                asyncHttpResponse.setThrowable(error);
                                responseSerializedSubject.onNext(asyncHttpResponse);
                            } else {
                                responseSerializedSubject.onError(error);
                                myAsyncRetryPolicy.retry(statusCode, headers, responseBody, error);
                            }
                            responseSerializedSubject.onComplete();
                            progressSerializedSubject.onComplete();
                        }
                    };
                    asyncHttpResponseHandler.setTag(tag);
                    asyncHttpResponseHandler.setCharset(encoding);
                    AsyncHttpReq.post(BaseApp.getAppContext(), url,
                            (myRequestParams.getJsonParams() == null || myRequestParams.getJsonParams().equals(""))
                                    ? myRequestParams : new StringEntity(myRequestParams.getJsonParams(), encoding),
                            contentType, asyncHttpResponseHandler);
                    break;
            }
        }

    }

    /**
     * 解析Json
     *
     * @param strResponse
     * @return
     * @throws Exception
     */
    protected static JsonObject parseJson(String strResponse) throws Exception {
        JsonParser jsonParser = new JsonParser();
        return jsonParser.parse(strResponse).getAsJsonObject();
    }


}
