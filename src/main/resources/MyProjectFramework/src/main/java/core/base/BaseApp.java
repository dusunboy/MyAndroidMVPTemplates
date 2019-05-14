package $Package.core.base;

import android.app.Application;
import android.content.Context;
import android.content.res.Configuration;

import $Package.core.fuction.WindowUtil;


/**
 * 入口驱动
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class BaseApp extends Application {

    private static BaseApp context;

    @Override
    public void onCreate() {
        super.onCreate();
        onStaticCreate(this);
    }

    private static void onStaticCreate(BaseApp baseApp) {
        context = baseApp;
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
