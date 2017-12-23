package $Package.core.mvp;

import android.content.Intent;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import $Package.core.config.BaseConstant;
import $Package.core.fuction.SPUtil;
import $Package.core.retrofit.RetrofitManager;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import retrofit2.Retrofit;


/**
 * 引导器实例基类
 * Created by Vincent on $Time.
 */
public class BasePresenterActivityImpl {

    protected Retrofit retrofit;
    protected RetrofitManager retrofitManager;
    protected RxAppCompatActivity activity;

	public BasePresenterActivityImpl(RxAppCompatActivity activity, RetrofitManager retrofitManager) {
		this.activity = activity;
		this.retrofitManager = retrofitManager;
        retrofit = retrofitManager.getRetrofit();
	}

    /**
     * 解析Json
     * @param strResponse
     * @return
     * @throws Exception
     */
    protected JsonObject parseJson(String strResponse) throws Exception {
        return new JsonParser().parse(strResponse).getAsJsonObject();
    }

    public void clearUserInfo() {
        SPUtil.put(BaseConstant.PHONE_LOGIN, "");
        SPUtil.put(BaseConstant.IMEI, "");
        SPUtil.put(BaseConstant.USER_PHONE, "");
        SPUtil.put(BaseConstant.USER_ID, "");
        SPUtil.put(BaseConstant.USER_NAME, "");
    }

    /**
     * 结束当前页面
     */
    protected void finish() {
        activity.finish();
    }

    /**
     * get System Strings
     * @param str
     * @return
     */
    protected String getString(int str) {
        return activity.getString(str);
    }


    /**
     * get System Colors
     * @param color
     * @return
     */
    protected int getColor(int color) {
        return activity.getResources().getColor(color);
    }


    /**
     * 跳转到Activity
     * @param aClass
     */
    public void start2Activity(Class aClass){
        Intent intent = new Intent(activity, aClass);
        activity.startActivity(intent);
    }

    /**
     * 跳转到Activity 关闭后会向前面的Activity 传回数据
     * @param aClass
     * @param requestCode
     */
    public void start2ActivityForResult(Class aClass, int requestCode){
        Intent intent = new Intent(activity, aClass);
        activity.startActivityForResult(intent, requestCode);
    }

    /**
     * 取消请求
     * @param tag
     */
    public void cancelRequest(String tag) {
        if (retrofitManager != null) {
            retrofitManager.cancel(tag);
        }
    }

    /**
     * 取消全部请求
     */
    public void cancelAllRequest() {
        if (retrofitManager != null) {
            retrofitManager.cancelAll();
        }
    }
}
