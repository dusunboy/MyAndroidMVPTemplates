package $Package.core.mvp;

import android.content.Intent;

import androidx.fragment.app.Fragment;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;
import $Package.core.fuction.AppUtil;
import $Package.core.retrofit.RetrofitManager;

import retrofit2.Retrofit;


/**
 * 引导器实例的基类
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class BasePresenterFragmentImpl {

    protected RxAppCompatActivity activity;
    protected RetrofitManager retrofitManager;
    protected Retrofit retrofit;

	public BasePresenterFragmentImpl(Fragment fragment, RetrofitManager retrofitManager) {
		this.activity = (RxAppCompatActivity) fragment.getActivity();
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

    /**
     * 清除用户信息
     */
    public void clearUserInfo() {
        AppUtil.clearUserInfo();
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
     * get System Strings
     * @param str
     * @return
     */
    protected String getString(int str, String x) {
        return activity.getString(str, x);
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
