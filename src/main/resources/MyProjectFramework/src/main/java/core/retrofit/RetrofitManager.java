package $Package.core.retrofit;

import $Package.core.config.BaseConstant;
import $Package.core.fuction.SPUtil;
import $Package.project.retrofit_config.MyAuthenticator;
import $Package.project.retrofit_config.ResponseInterceptor;

import java.net.ProxySelector;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Call;
import okhttp3.ConnectionPool;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Protocol;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Retrofit管理
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class RetrofitManager {

    public static final int CONNECT_TIME_OUT = 30;
    public static final int READ_TIME_OUT = 20;
    public static final int MAX_IDLE_CONNECTIONS = 50;
    public static final int KEEP_ALIVE_DURATION_MS = 50;
    private Retrofit retrofit;
    private OkHttpClient client;

    public RetrofitManager() {
        init(null);
    }

    public RetrofitManager(String tag) {
        init(tag);
    }

    public RetrofitManager(OkHttpClient client) {
        this.client = client;
    }

    private void init(String tag) {
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(SPUtil.getBoolean(BaseConstant.IS_DEBUG) ? HttpLoggingInterceptor.Level.BODY
                : HttpLoggingInterceptor.Level.NONE);
        client = new OkHttpClientBuilder()
                .connectionPool(new ConnectionPool(MAX_IDLE_CONNECTIONS, KEEP_ALIVE_DURATION_MS, TimeUnit.SECONDS))
                .addInterceptor(httpLoggingInterceptor)
                .addInterceptor(tag == null ? new ResponseInterceptor()
                        : new ResponseInterceptor(tag))
                .connectTimeout(RetrofitManager.CONNECT_TIME_OUT, TimeUnit.SECONDS)
                .readTimeout(RetrofitManager.READ_TIME_OUT, TimeUnit.SECONDS)
                .retryOnConnectionFailure(false)
                .authenticator(new MyAuthenticator())
                .getBuilder().build();
        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(SPUtil.getString(BaseConstant.BASE_URL))
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public OkHttpClient getClient() {
        return client;
    }

    /**
     * 取消请求
     * @param tag
     */
    public void cancel(String tag) {
        for(Call call : client.dispatcher().queuedCalls()) {
            if(call.request().tag().equals(tag))
                call.cancel();
        }
        for(Call call : client.dispatcher().runningCalls()) {
            if(call.request().tag().equals(tag))
                call.cancel();
        }

    }

    /**
     * 取消所有
     */
    public void cancelAll() {
        client.dispatcher().cancelAll();
    }

    public static final class OkHttpClientBuilder {
        private OkHttpClient.Builder builder;

        public OkHttpClientBuilder() {
            builder = new OkHttpClient.Builder();
        }

        public RetrofitManager build() {
            return new RetrofitManager(builder.build());
        }

        public OkHttpClientBuilder addInterceptor(Interceptor interceptor) {
            builder.addInterceptor(interceptor);
            return this;
        }

        /**
         * Sets the default connect timeout for new connections. A value of 0 means no timeout,
         * otherwise values must be between 1 and {@link Integer#MAX_VALUE} when converted to
         * milliseconds.
         */
        public OkHttpClientBuilder connectTimeout(int connectTimeOut, TimeUnit timeUnit) {
            builder.connectTimeout(connectTimeOut, timeUnit);
            return this;
        }

        /**
         * Sets the default read timeout for new connections. A value of 0 means no timeout, otherwise
         * values must be between 1 and {@link Integer#MAX_VALUE} when converted to milliseconds.
         */
        public OkHttpClientBuilder readTimeout(int readTimeOut, TimeUnit timeUnit) {
            builder.readTimeout(readTimeOut, timeUnit);
            return this;
        }

        /**
         * Configure this client to retry or not when a connectivity problem is encountered. By default,
         * this client silently recovers from the following problems:
         *
         * <ul>
         *   <li><strong>Unreachable IP addresses.</strong> If the URL's host has multiple IP addresses,
         *       failure to reach any individual IP address doesn't fail the overall request. This can
         *       increase availability of multi-homed services.
         *   <li><strong>Stale pooled connections.</strong> The {@link ConnectionPool} reuses sockets
         *       to decrease request latency, but these connections will occasionally time out.
         *   <li><strong>Unreachable proxy servers.</strong> A {@link ProxySelector} can be used to
         *       attempt multiple proxy servers in sequence, eventually falling back to a direct
         *       connection.
         * </ul>
         *
         * Set this to false to avoid retrying requests when doing so is destructive. In this case the
         * calling application should do its own recovery of connectivity failures.
         */
        public OkHttpClientBuilder retryOnConnectionFailure(boolean retryOnConnectionFailure) {
            builder.retryOnConnectionFailure(retryOnConnectionFailure);
            return this;
        }

        /**
         * Sets the authenticator used to respond to challenges from origin servers
         *
         * <p>If unset, the {@linkplain Authenticator#NONE no authentication will be attempted}.
         */
        public OkHttpClientBuilder authenticator(Authenticator authenticator) {
            builder.authenticator(authenticator);
            return this;
        }

        /**
         * Sets the connection pool used to recycle HTTP and HTTPS connections.
         *
         * <p>If unset, a new connection pool will be used.
         */
        public OkHttpClientBuilder connectionPool(ConnectionPool connectionPool) {
            builder.connectionPool(connectionPool);
            return this;
        }

        /**
         * Configure the protocols used by this client to communicate with remote servers. By default
         * this client will prefer the most efficient transport available, falling back to more
         * ubiquitous protocols. Applications should only call this method to avoid specific
         * compatibility problems, such as web servers that behave incorrectly when HTTP/2 is enabled.
         *
         * <p>The following protocols are currently supported:
         *
         * <ul>
         *     <li><a href="http://www.w3.org/Protocols/rfc2616/rfc2616.html">http/1.1</a>
         *     <li><a href="http://tools.ietf.org/html/draft-ietf-httpbis-http2-17">h2</a>
         * </ul>
         *
         * <p><strong>This is an evolving set.</strong> Future releases include support for transitional
         * protocols. The http/1.1 transport will never be dropped.
         *
         * <p>If multiple protocols are specified, <a
         * href="http://tools.ietf.org/html/draft-ietf-tls-applayerprotoneg">ALPN</a> will be used to
         * negotiate a transport.
         *
         * <p>{@link Protocol#HTTP_1_0} is not supported in this set. Requests are initiated with {@code
         * HTTP/1.1} only. If the server responds with {@code HTTP/1.0}, that will be exposed by {@link
         * Response#protocol()}.
         *
         * @param protocols the protocols to use, in order of preference. The list must contain {@link
         * Protocol#HTTP_1_1}. It must not contain null or {@link Protocol#HTTP_1_0}.
         */
        public OkHttpClientBuilder protocols(List<Protocol> protocols) {
            builder.protocols(protocols);
            return this;
        }

        public OkHttpClient.Builder getBuilder() {
            return builder;
        }
    }
}
