package $Package.project.main;

import android.os.Bundle;

import $Package.R;
import $Package.core.base.BasePresenterActivity;
import $Package.project.main.dagger2.DaggerMainComponent;
import $Package.project.main.dagger2.MainComponent;
import $Package.project.main.dagger2.MainModule;
import $Package.project.main.presenter.MainPresenterImpl;
import $Package.project.main.view.MainView;

import javax.inject.Inject;

/**
 * Created by Vincent on $Time.
 */
public class MainActivity extends BasePresenterActivity implements MainView {

    @Inject
    MainPresenterImpl basePresenter;

    @Override
    public int getLayoutView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public void initToolbar() {

    }

    @Override
    public void initView() {
    }

    @Override
    public void init() {
        MainComponent mainComponent = DaggerMainComponent.builder()
                .mainModule(new MainModule(this, getLocalClassName())).build();
        mainComponent.inject(this);
        basePresenter.setViewListener(this);
    }

    @Override
    public void clickEvent() {
    }



    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

}
