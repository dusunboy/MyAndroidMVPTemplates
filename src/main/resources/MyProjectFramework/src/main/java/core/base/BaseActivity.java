package $Package.core.base;

import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import $Package.core.system_bar_tint.SystemBarTintManager;

/**
 * 基类 <br/>
 * Created by Vincent on $Time.
 */
public abstract class BaseActivity extends RxAppCompatActivity {


    protected Bundle savedInstanceState;

    @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        this.savedInstanceState = savedInstanceState;
        setContentView(getLayoutView(savedInstanceState));
//        setStateBarColor(ColorUtil.Hex2Color(SPUtil.getString(BaseConstant.COLOR_PRIMARY_DARK)));

        initToolbar();
        initView();
        init();
        clickEvent();
    }

    /**
	 * 加载视图
	 * @return
     * @param savedInstanceState
	 */
	public abstract int getLayoutView(Bundle savedInstanceState);

    /**
     * 初始化Toolbar
     */
    public abstract void initToolbar();
    /**
     * 初始化试图
     */
    public abstract void initView();

	/**
	 * 初始化数据
	 */
	public abstract void init();

    /**
     * RxBinding处理点击事件
     */
    public abstract void clickEvent();

	@Override
	protected void onDestroy() {
        System.gc();
        Runtime.getRuntime().gc();
		super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * 设置状态栏颜色
     * @param color
     */
    protected void setStateBarColor(int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window win = getWindow();
            WindowManager.LayoutParams winParams = win.getAttributes();
            final int bits = WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS;
            winParams.flags |= bits;
            win.setAttributes(winParams);
            SystemBarTintManager tintManager = new SystemBarTintManager(this);
            tintManager.setStatusBarTintEnabled(true);
            tintManager.setStatusBarTintColor(color);
            tintManager.setStatusBarDarkMode(true, this);
            SystemBarTintManager.SystemBarConfig config = tintManager.getConfig();
            ((ViewGroup)findViewById(android.R.id.content)).getChildAt(0).
            setPadding(0, config.getPixelInsetTop(true), 0, config.getPixelInsetBottom());
        }
    }

}
