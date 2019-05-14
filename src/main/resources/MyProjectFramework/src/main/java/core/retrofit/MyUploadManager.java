package $Package.core.retrofit;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.FileNameMap;
import java.net.URLConnection;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Interceptor;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSource;

/**
 * 上传管理
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class MyUploadManager {

    private static final long CONNECT_TIME_OUT = 30;
    private static final long WRITE_TIME_OUT = 30;
    private OkHttpClient okHttpClient;
    private String url;
    private Call call;

    public MyUploadManager(OkHttpClient.Builder okHttpClientBuilder, String url) {
        this.url = url;
        okHttpClient = okHttpClientBuilder
                .connectTimeout(CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS)
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .build();
    }

    public Flowable<JsonObject> uploadFiles(MultipartBody.Builder multipartBodyBuilder) {
        return Flowable.create(new FlowableOnSubscribe<JsonObject>() {
            @Override
            public void subscribe(FlowableEmitter<JsonObject> flowableOnSubscribe) throws Exception {
                RequestBody requestBody = multipartBodyBuilder.build();
                Request.Builder requestBuilder = new Request.Builder();
                requestBuilder.url(url);
                requestBuilder.post(requestBody);
//                requestBuilder.post(new RequestBody() {
//                    @Override
//                    public MediaType contentType() {
//                        return requestBody.contentType();
//                    }
//                    @Override
//                    public long contentLength() throws IOException {
//                        return requestBody.contentLength();
//                    }
//
//
//
//                   @Override
//                    public void writeTo(BufferedSink sink) throws IOException {
////                       requestBody.writeTo(sink);
////                        if (bufferedSink == null) {
////                            bufferedSink = Okio.buffer(new ForwardingSink(sink) {
////
////                                long bytesWritten = 0L;
////                                long contentLength = 0L;
////                                int progress;
////
////                                @Override
////                                public void write(Buffer source, long byteCount) throws IOException {
////                                    super.write(source, byteCount);
////                                    LogUtil.i("write");
////                                    if (contentLength == 0) {
////                                        contentLength = contentLength();
////                                    }
//////                                    if (flowableOnSubscribe.isCancelled()) {
//////                                        Util.closeQuietly(source);
//////                                        Util.closeQuietly(bufferedSink);
//////                                        if (call[0] != null) {
//////                                            call[0].cancel();
//////                                        }
//////                                    } else {
////                                        bytesWritten += byteCount == -1 ? 0 : byteCount;
////                                        int tempProgress = (int) ((bytesWritten * 1.0f / contentLength) * 100);
////                                        if (progressListener != null && tempProgress >= 0
////                                                && tempProgress <= 100 && tempProgress != progress) {
////                                            Observable.just(tempProgress)
////                                                    .compose(RxSchedulers.io_mainThread())
////                                                    .subscribe(new Observer<Integer>() {
////                                                        public Disposable disposable;
////
////                                                        @Override
////                                                        public void onSubscribe(Disposable d) {
////                                                            this.disposable = d;
////                                                        }
////
////                                                        @Override
////                                                        public void onNext(Integer integer) {
////                                                            progressListener.onProgress(integer);
////                                                        }
////
////                                                        @Override
////                                                        public void onError(Throwable e) {
////                                                            if (!disposable.isDisposed()) {
////                                                                disposable.dispose();
////                                                            }
////                                                        }
////
////                                                        @Override
////                                                        public void onComplete() {
////                                                            if (!disposable.isDisposed()) {
////                                                                disposable.dispose();
////                                                            }
////                                                        }
////                                                    });
////
////                                        }
////                                        progress = tempProgress;
////                                        LogUtil.i(tempProgress);
////                                    }
//////                                }
////                            });
////                        }
////                        requestBody.writeTo(bufferedSink);
////                        bufferedSink.flush();
//                    }
//                });
                Request request = requestBuilder.build();
                call = okHttpClient.newCall(request);
                call.enqueue(new Callback() {

                    @Override
                    public void onFailure(Call call, IOException e) {
                        flowableOnSubscribe.onError(e);
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
//
                        ResponseBody responseBody = response.body();
                        BufferedSource source = responseBody.source();
                        source.request(Long.MAX_VALUE); // Buffer the entire body.
                        Buffer buffer = source.buffer();
                        String json = buffer.clone().readString(responseBody.contentType().charset(Charset.forName("UTF-8")));
                        flowableOnSubscribe.onNext(new JsonParser()
                                .parse(json)
                                .getAsJsonObject());
                        flowableOnSubscribe.onComplete();
                    }
                });
            }

        }, BackpressureStrategy.BUFFER);
//        return Flowable.create(new FlowableOnSubscribe<Response>() {
//            @Override
//            public void subscribe(FlowableEmitter<Response> myProgressEmitter) throws Exception {
//                RequestBody requestBody = multipartBodyBuilder.build();
//                Request.Builder requestBuilder = new Request.Builder();
//                requestBuilder.url(url);// 添加URL地址
//                requestBuilder.post(new RequestBody() {
//                    BufferedSink bufferedSink = null;
//                    @Override
//                    public MediaType contentType() {
//                        return requestBody.contentType();
//                    }
//                    @Override
//                    public long contentLength() throws IOException {
//                        return requestBody.contentLength();
//                    }
//
//                    @Override
//                    public void writeTo(BufferedSink sink) throws IOException {
//                        if (bufferedSink == null) {
//                            bufferedSink = Okio.buffer(new ForwardingSink(sink) {
//
//                                long bytesWritten = 0L;
//                                int progress;
//
//                                @Override
//                                public void write(Buffer source, long byteCount) throws IOException {
//                                    super.write(source, byteCount);
//                                    bytesWritten += byteCount == -1 ? 0 : byteCount;
//                                    int tempProgress = (int) ((bytesWritten * 1.0f / contentLength()) * 100);
//                                    if (tempProgress >= 0 && tempProgress <= 100 && tempProgress != progress) {
////                                flowableOnSubscribe.onNext(progress);
//                                    }
//                                    progress = tempProgress;
//                                }
//                            });
//                        }
//                        requestBody.writeTo(bufferedSink);
//                        bufferedSink.flush();
//
//                    }
//                });
//                Request request = requestBuilder.build();
//                Call call = okHttpClient.newCall(request);
//                call.enqueue(new Callback() {
//
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//                        LogUtil.i(e.toString());
////                        flowableOnSubscribe.onComplete();
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        String str = response.body().string();
//                        LogUtil.i(str);
//                    }
//                });
//            }
//        }, BackpressureStrategy.BUFFER);
    }

    public void cancel() {
        if (!call.isCanceled()) {
            call.cancel();
        }
    }

    public static class Builder {

        private String url;
        private OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();

        public MyUploadManager build() {

            return new MyUploadManager(okHttpClientBuilder, url);
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder addInterceptor(Interceptor interceptor) {
            okHttpClientBuilder.addInterceptor(interceptor);
            return this;
        }

        public Builder authenticator(Authenticator authenticator) {
            okHttpClientBuilder.authenticator(authenticator);
            return this;
        }
    }

    public static String getMimeType(String filename) {
        FileNameMap filenameMap = URLConnection.getFileNameMap();
        String contentType = filenameMap.getContentTypeFor(filename);
        if (contentType == null) {
            //* exe,所有的可执行程序
            contentType = "application/octet-stream";
        }
        return contentType;
    }

    public interface ProgressListener {
        void onProgress(Integer progress);
    }

}
