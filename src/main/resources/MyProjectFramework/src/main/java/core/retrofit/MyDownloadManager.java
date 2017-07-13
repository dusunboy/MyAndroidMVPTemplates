package $Package.core.retrofit;

import $Package.R;
import $Package.core.base.BaseApp;
import $Package.core.config.CustomException;
import $Package.core.fuction.LogUtil;
import $Package.core.rxjava.RxSchedulers;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.logging.HttpLoggingInterceptor;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;

/**
 * 下载管理
 * Created by Vincent on $Time.
 */
public class MyDownloadManager {


    private static final String PATH_INVALID = BaseApp.getAppContext().getString(R.string.path_invalid);
    private Retrofit retrofit;
    private String url;
    private String path;
    private ProgressListener progressListener;

    public MyDownloadManager(String url, String path) {
        this.url = url;
        this.path = path;
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.HEADERS))
                .addNetworkInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Response originalResponse = chain.proceed(chain.request());//对结果重新处理
                        return originalResponse
                                .newBuilder()
                                .body(new ResponseBody() {
                                    @Override
                                    public MediaType contentType() {
                                        return originalResponse.body().contentType();
                                    }

                                    @Override
                                    public long contentLength() {
                                        return originalResponse.body().contentLength();
                                    }

                                    @Override
                                    public BufferedSource source() {
                                        return Okio.buffer(new ForwardingSource(originalResponse.body().source()) {
                                            long bytesReading = 0;
                                            int progress = 0;

                                            @Override
                                            public long read(Buffer sink, long byteCount) {
                                                long bytesRead = 0;
                                                try {
                                                    bytesRead = super.read(sink, byteCount);
                                                    bytesReading += bytesRead == -1 ? 0 : bytesRead;
                                                    int tempProgress = (int) ((bytesReading * 1.0 / contentLength()) * 100);
                                                    if (progressListener != null && tempProgress > 0 && tempProgress <= 100 &&
                                                            tempProgress != progress) {
                                                        Observable.just(progress)
                                                                .compose(RxSchedulers.io_mainThread())
                                                                .subscribe(new Observer<Integer>() {
                                                                    public Disposable disposable;

                                                                    @Override
                                                                    public void onSubscribe(Disposable d) {
                                                                        this.disposable = d;
                                                                    }

                                                                    @Override
                                                                    public void onNext(Integer integer) {
                                                                        progressListener.onProgress(integer);
                                                                    }

                                                                    @Override
                                                                    public void onError(Throwable e) {
                                                                        if (!disposable.isDisposed()) {
                                                                            disposable.dispose();
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onComplete() {
                                                                        if (!disposable.isDisposed()) {
                                                                            disposable.dispose();
                                                                        }
                                                                    }
                                                                });
                                                    }
                                                    progress = tempProgress;
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                                return bytesRead;
                                            }
                                        });
                                    }
                                })
                                .build();
                    }
                })
                .build();
        try {
            URL tempUrl = new URL(url);
            retrofit = new Retrofit.Builder()
                    .client(client)
                    .baseUrl("http://" + tempUrl.getHost())
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    public Observable<ResponseBody> downloadFile(ProgressListener progressListener) {
        if (retrofit == null) {
            return null;
        }
        this.progressListener = progressListener;
        DownloadService downloadService = retrofit.create(DownloadService.class);
        return downloadService.downloadFile(url)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .doOnNext(new Consumer<ResponseBody>() {

                    @Override
                    public void accept(@NonNull ResponseBody responseBody) throws Exception {
                        if (path == null || path.equals("")) {
                            throw new CustomException(PATH_INVALID);
                        } else {
                            writeFile(responseBody.source(), new File(path));
                        }
                    }
                })
                .doOnError(new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        LogUtil.i(throwable.toString());
                    }
                });

    }


    public static class Builder {
        private String url;
        private String path;

        public MyDownloadManager build() {

            return new MyDownloadManager(url, path);
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder path(String path) {
            this.path = path;
            return this;
        }

    }

    public interface DownloadService {
        @Streaming
        @GET
        Observable<ResponseBody> downloadFile(@Url String fileUrl);
    }

    public interface ProgressListener {
        void onProgress(int progress);
    }


    /**
     * 写入文件
     */
    private static void writeFile(BufferedSource source, File file) throws Exception {
        if (!file.getParentFile().exists()) {
            if (!file.getParentFile().mkdirs()) {
                throw new IOException("Unable to create path");
            }
        }
        if (file.exists()) {
            if (!file.delete()) {
                throw new IOException("Unable to delete file");
            }
        }
        BufferedSink bufferedSink = Okio.buffer(Okio.sink(file));
        bufferedSink.writeAll(source);
        bufferedSink.close();
        source.close();

    }

//    //加入到订阅列表
//    protected void addSubscription(Disposable disposable) {
//        if (disposable == null) return;
//        if (mDisposables == null) {
//            mDisposables = new CompositeDisposable();
//        }
//        mDisposables.add(disposable);
//    }
//    public void dispose(Disposable disposable){
//        if(mDisposables!=null){
//            mDisposables.remove(disposable);
//        }
//    }
//    //取消所有的订阅
//    public void dispose(){
//        if(mDisposables!=null){
//            mDisposables.clear();
//        }
//    }

}
