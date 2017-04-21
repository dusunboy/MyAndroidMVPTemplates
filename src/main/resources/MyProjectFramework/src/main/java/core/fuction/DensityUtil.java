package $Package.core.fuction;

import android.util.TypedValue;

import $Package.core.base.BaseApp;


/**
 * 常用单位转换的辅助类
 * Created by Vincent on $Time.
 */
public class DensityUtil
{
    private DensityUtil()
    {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * dp转px
     *
     * @param dpVal
     * @return
     */
    public static int dp2px(float dpVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, BaseApp.getAppContext().getResources().getDisplayMetrics());
    }

    /**
     * sp转px
     *
     * @param spVal
     * @return
     */
    public static int sp2px(float spVal)
    {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                spVal, BaseApp.getAppContext().getResources().getDisplayMetrics());
    }

    /**
     * px转dp
     *
     * @param pxVal
     * @return
     */
    public static float px2dp(float pxVal)
    {
        final float scale = BaseApp.getAppContext().getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }

    /**
     * px转sp
     *
     * @param pxVal
     * @return
     */
    public static float px2sp(float pxVal)
    {
        return (pxVal / BaseApp.getAppContext().getResources().getDisplayMetrics().scaledDensity);
    }

}