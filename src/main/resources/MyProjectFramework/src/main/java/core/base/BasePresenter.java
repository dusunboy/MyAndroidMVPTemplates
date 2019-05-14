package $Package.core.base;

import $Package.core.mvp.BaseView;

/**
 * 引导器接口基类
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public interface BasePresenter {

    /**
     * 跳转到登陆界面
     */
    void start2Login();


    /**
     * 设置界面监听
     * @param view
     */
    void setViewListener(BaseView view);

}
