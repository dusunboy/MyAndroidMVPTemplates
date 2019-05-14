package $Package.core.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import com.squareup.leakcanary.RefWatcher;
import com.trello.rxlifecycle3.components.support.RxFragment;
import $Package.MyApp;

/**
 * Fragment基类
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public abstract class BaseFragment extends RxFragment {

    /**
     * Fragment是否可见
     */
    protected boolean isFragmentVisible;
    private View rootView;
    /**
     * 是否是第一次加载Fragment
     */
    protected boolean isFirst;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = getLayoutView(inflater, container, savedInstanceState);
        }
        isFirst = true;
        initToolbar(rootView, savedInstanceState);
        initView(rootView, savedInstanceState);
        init(savedInstanceState);
        clickEvent();
        //可见，但是并没有加载过
        if (isFragmentVisible && isFirst) {
            onFragmentVisibleChange(true);
        }
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    /**
     * 当前fragment可见状态发生变化时会回调该方法
     * 如果当前fragment是第一次加载，等待onCreateView后才会回调该方法，其它情况回调时机跟 {@link #setUserVisibleHint(boolean)}一致
     * 在该回调方法中你可以做一些加载数据操作，甚至是控件的操作.
     *
     * @param isVisible true  不可见 -> 可见
     *                  false 可见  -> 不可见
     */
    protected void onFragmentVisibleChange(boolean isVisible) {

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            isFragmentVisible = true;
        }
        if (rootView == null) {
            return;
        }
        //可见，并且没有加载过
        if (isFirst && isFragmentVisible) {
            onFragmentVisibleChange(true);
            return;
        }
        //由可见——>不可见 已经加载过
        if (isFragmentVisible) {
            onFragmentVisibleChange(false);
            isFragmentVisible = false;
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        //监控fragment泄漏
        RefWatcher refWatcher = MyApp.getRefWatcher(getActivity());
        if (refWatcher != null) {
            refWatcher.watch(this);
        }
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
