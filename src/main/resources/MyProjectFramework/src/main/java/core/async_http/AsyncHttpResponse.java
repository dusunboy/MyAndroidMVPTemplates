package $Package.core.async_http;

import com.google.gson.JsonObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;

/**
 * 仅用于RxJava返回的封装数据(结构体封装)
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class AsyncHttpResponse {

    private int statusCode;
    private Header[] headers;
    private String strResponse;
    private Throwable throwable;
    private File file;
    private int progressPercent;
    private JsonObject jsonObject;

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Header[] getHeaders() {
        return headers;
    }

    public void setHeaders(Header[] headers) {
        this.headers = headers;
    }

    public String getStrResponse() {
        return strResponse;
    }

    public void setStrResponse(String strResponse) {
        this.strResponse = strResponse;
    }

    public JsonObject getJsonObject() {
        return jsonObject;
    }

    public void setJsonObject(JsonObject jsonObject) {
        this.jsonObject = jsonObject;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public File getFile() {
        return file;
    }

    public int getProgressPercent() {
        return progressPercent;
    }

    public void setProgressPercent(int progressPercent) {
        this.progressPercent = progressPercent;
    }
}



