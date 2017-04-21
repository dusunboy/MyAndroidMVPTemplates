package $Package.project.retrofit_config;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;


/**
 * retrofit响应拦截类
 * Created by Vincent on $Time.
 */
public class ResponseInterceptor implements Interceptor {

    private String tag;

    public ResponseInterceptor() {
    }

    public ResponseInterceptor(String tag) {
        this.tag = tag;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        Request originalRequest = chain.request();
        Request.Builder requestBuilder = originalRequest.newBuilder();
        if (tag != null && !tag.equals("")) {
            requestBuilder.tag(tag);
        }
        Request authorised = requestBuilder
                .build();

        Response response = chain.proceed(authorised);
        return response;
    }



}
