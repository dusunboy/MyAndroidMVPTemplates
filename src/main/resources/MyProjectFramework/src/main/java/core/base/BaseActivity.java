package $Package.core.base;

import android.os.Bundle;

import $Package.R;
import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import $Package.core.activities.ActivitiesManager;
import $Package.core.fuction.AppUtil;

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

        ActivitiesManager.getInstance().pushActivity(this);
        initSystemBarTintManager();
        initToolbar();
        initView();
        init();
        clickEvent();
    }

    /**
     * 初始化SystemBarTintManager
     */
    private void initSystemBarTintManager() {
        // create our manager instance after the content view is set
        SystemBarTintManager tintManager = new SystemBarTintManager(this);
        // enable status bar tint
        tintManager.setStatusBarTintEnabled(true);
        // enable navigation bar tint
        tintManager.setNavigationBarTintEnabled(true);
        // set a custom tint color for all system bars
        tintManager.setStatusBarTintResource(R.color.colorPrimaryDark);
//        tintManager.setTintColor(getResources().getColor(android.R.color.black));
//        // set a custom navigation bar resource
//        tintManager.setNavigationBarTintResource(R.drawable.my_tint);
//        // set a custom status bar drawable
//        tintManager.setStatusBarTintDrawable(MyDrawable);
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
        AppUtil.clearTextLineCache();
        ActivitiesManager.getInstance().popActivity(this);
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

}
