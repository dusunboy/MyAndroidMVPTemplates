package $Package.core.async_http;

import com.loopj.android.http.RequestParams;

/**
 * AsyncHttp请求参数集合
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class MyRequestParams extends RequestParams  {

    public static final String GET = "get";
    private String json;
    private String downloadSavePath;
    private String downLoadMethod;

    /**
     * 参数与实体json传递
     * @param json
     */
    public void putJsonParams(String json) {
        this.json = json;
    }

    public String getJsonParams() {
        return json;
    }
    /**
     * 设置下载保存路径
     * @param downloadSavePath
     */
    public void setDownloadSavePath(String downloadSavePath){
        this.downloadSavePath = downloadSavePath;
    }

    public String getDownloadSavePath() {
        return downloadSavePath;
    }

    /**
     * 添加Header标识头
     * @param key
     * @param value
     */
    public void addHeader(String key, String value) {
        AsyncHttpReq.addHeader(key, value);
    }

    /**
     * 清除所有标识头
     */
    public void removeAllHeaders() {
        AsyncHttpReq.removeAllHeaders();
    }

    /**
     * 清除指定标识头
     * @param key
     */
    public void removeHeader(String key) {
        AsyncHttpReq.removeHeader(key);
    }

    /**
     * 获取下载请求方式
     * @return
     */
    public String getDownLoadMethod() {
        return downLoadMethod;
    }

    /**
     * 设置下载请求方式
     * @param downLoadMethod
     */
    public void setDownLoadMethod(String downLoadMethod) {
        this.downLoadMethod = downLoadMethod;
    }
}
