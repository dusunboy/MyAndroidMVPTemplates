package $Package.project.main.presenter;


import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import $Package.core.mvp.BasePresenterActivityImpl;
import $Package.core.mvp.BaseView;
import $Package.core.retrofit.RetrofitManager;
import $Package.project.main.view.MainView;
import $Package.project.retrofit_config.ApiService;

import javax.inject.Inject;

/**
 * Created by Vincent on $Time.
 */
public class MainPresenterImpl extends BasePresenterActivityImpl implements MainPresenter {

    private ApiService apiService;
    private MainView baseView;

    @Inject
    public MainPresenterImpl(RxAppCompatActivity activity, RetrofitManager retrofitManager) {
        super(activity, retrofitManager);
        apiService = retrofit.create(ApiService.class);
    }


    @Override
    public void start2Login() {

    }

    @Override
    public void setViewListener(BaseView view) {
        baseView = (MainView) view;
    }


}
