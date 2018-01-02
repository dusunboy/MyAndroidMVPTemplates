package $Package;

import android.content.Context;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;
import com.tencent.bugly.crashreport.CrashReport;
import $Package.core.base.BaseApp;
import $Package.core.config.BaseConstant;
import $Package.core.fuction.AppUtil;
import $Package.core.fuction.LogUtil;
import $Package.core.fuction.SDCardUtil;
import $Package.core.fuction.SPUtil;

/**
 * Created by Vincent on $Time.
 */
public class MyApp extends BaseApp {

    private RefWatcher refWatcher;

    @Override
    public void onCreate() {
        super.onCreate();
        initData();
        if(!SPUtil.getBoolean(BaseConstant.IS_DEBUG)) {
            initCrash();
        }
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.880-75124316
            return;
        }
        refWatcher = LeakCanary.install(this);
    }

    public static RefWatcher getRefWatcher(Context context) {
        MyApp application = (MyApp) context.getApplicationContext();
        return application.refWatcher;
    }

    /**
     * 初始化崩溃
     */
    private void initCrash() {
        CrashReport.UserStrategy strategy = new CrashReport.UserStrategy(this);
//        strategy.setAppChannel("myChannel");  //设置渠道
        strategy.setAppVersion(AppUtil.getVerNum());      //App的版本
//        strategy.setAppReportDelay(20000);   //改为20s
//        strategy.setAppPackageName("com.tencent.xx");  //App的包名
//        CrashReport.setUserSceneTag(context, 9527); // 上报后的Crash会显示该标签
        CrashReport.initCrashReport(this, Constant.CRASH_APP_ID, false, strategy);
        if(!SPUtil.getString(BaseConstant.USER_ID).equals("")) {
            CrashReport.setUserId(SPUtil.getString(BaseConstant.USER_ID));  //本次启动后的异常日志用户ID
        }
    }

    /**
     * 初始化数据
     */
    private void initData() {
        /**
         * 是否调试
         */
        SPUtil.put(BaseConstant.IS_DEBUG, true);
        /**
         * 判断APP版本 official test dev
         */
        SPUtil.put(BaseConstant.VERSION, "dev");
        // 接口地址
        if (SPUtil.getString(BaseConstant.VERSION).equals("dev")) { //开发版本
            SPUtil.put(BaseConstant.BASE_URL, "http://dev.com");
        } else if (SPUtil.getString(BaseConstant.VERSION).equals("test")) { //测试版本
            SPUtil.put(BaseConstant.BASE_URL, "http://test.com");
        } else { //正式版本
            SPUtil.put(BaseConstant.BASE_URL, "https://official.com");
        }
        /**
         * 开启调试模式true，关闭调试模式false
         */
        LogUtil.setDebugMode(SPUtil.getBoolean(BaseConstant.IS_DEBUG));
        /**
         * SD数据库文件位置
         */
        SPUtil.put(BaseConstant.DB_PATH, Constant.APP_NAME);
        /**
         * 数据库名字
         */
        SPUtil.put(BaseConstant.DATABASE_NAME, Constant.APP_NAME);
        /**
         * 数据库版本
         */
        SPUtil.put(BaseConstant.DATABASE_VERSION, 1);
        /**
         * SD卡根路径
         */
        SPUtil.put(BaseConstant.EXTERNAL_STORAGE_DIRECTORY, SDCardUtil.getAvailableSDCardPath());
        /**
         * SD卡文件名
         */
        SPUtil.put(BaseConstant.DIRECTORY, SPUtil.getString(BaseConstant.EXTERNAL_STORAGE_DIRECTORY) + "/" + Constant.APP_NAME);
//        /**
//         * IMEI
//         */
//        SPUtil.put(BaseConstant.IMEI, AppUtil.getIMEI());
//        /**
//         * 手机号
//         */
//        SPUtil.put(BaseConstant.PHONE, AppUtil.getPhone());
        /**
         * 图片路径
         */
        SPUtil.put(BaseConstant.IMAGE_PATH, SPUtil.get(BaseConstant.DIRECTORY, Constant.APP_NAME) + "/Images");
        /**
         * apk路径
         */
        SPUtil.put(BaseConstant.APK_PATH, SPUtil.get(BaseConstant.DIRECTORY, Constant.APP_NAME) + "/Apk");
        /**
         * matisse库的图片路径
         */
        SPUtil.put(BaseConstant.PICTURES_PATH, SPUtil.getString(BaseConstant.EXTERNAL_STORAGE_DIRECTORY, "") + "/Pictures");
    }
}
