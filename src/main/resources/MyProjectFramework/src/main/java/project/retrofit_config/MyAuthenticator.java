package $Package.project.retrofit_config;

import java.io.IOException;

import okhttp3.Authenticator;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;

/**
 * retrofit认证类
 * Created by Vincent on $Time.
 */
public class MyAuthenticator implements Authenticator {

    @Override
    public Request authenticate(Route route, Response response) throws IOException {
        return response.request();
    }
}
