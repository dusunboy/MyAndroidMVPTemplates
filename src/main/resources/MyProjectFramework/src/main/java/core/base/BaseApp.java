package $Package.core.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import $Package.core.fuction.WindowUtil;


/**
 * 入口驱动
 * Created by Vincent on $Time.
 */
public class BaseApp extends Application {

    private static BaseApp context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public static Context getAppContext() {
        return context;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        WindowUtil.computeScaleRatio();
        WindowUtil.computeWindowRotation();
    }
}
