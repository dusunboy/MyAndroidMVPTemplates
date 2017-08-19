package $Package.core.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.leakcanary.RefWatcher;
import com.trello.rxlifecycle2.components.support.RxFragment;
import $Package.MyApp;

/**
 * Fragment基类
 * Created by Vincent on $Time.
 */
public abstract class BasePresenterFragment extends RxFragment {

    protected boolean isFragmentVisible;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return getLayoutView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initToolbar(view, savedInstanceState);
        initView(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        init(savedInstanceState);
        clickEvent();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getUserVisibleHint()) {
            isFragmentVisible = true;
            onVisible();
        } else {
            isFragmentVisible = false;
            onInvisible();
        }
    }

    /**
     * 加载视图
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    protected abstract View getLayoutView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState);

    /**
     * 初始化Toolbar
     * @param view
     * @param savedInstanceState
     */
    public abstract void initToolbar(View view, Bundle savedInstanceState);

    /**
     * 初始化试图
     */
    protected abstract void initView(View view, @Nullable Bundle savedInstanceState);

    /**
     * 初始化数据
     */
    protected abstract void init(@Nullable Bundle savedInstanceState);

    /**
     * RxBinding处理点击事件
     */
    public abstract void clickEvent();

    /**
     * fragment可见
     */
    protected void onVisible(){
        delayLoad();
    }

    /**
     * 当fragment可见时加载数据
     */
    protected abstract void delayLoad();

    /**
     * fragment不可见
     */
    protected void onInvisible(){}

    @Override
    public void onDestroy() {
        super.onDestroy();
        //监控fragment泄漏
        RefWatcher refWatcher = MyApp.getRefWatcher(getActivity());
        refWatcher.watch(this);
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onStop() {
        super.onStop();
    }
}
