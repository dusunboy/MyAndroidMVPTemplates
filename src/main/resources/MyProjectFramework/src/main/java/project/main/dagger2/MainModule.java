package $Package.project.main.dagger2;

import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;
import $Package.core.base.BaseModule;
import $Package.core.retrofit.RetrofitManager;
import $Package.project.main.presenter.MainPresenterImpl;

import dagger.Module;
import dagger.Provides;

/**
 * Created by Vincent on $Time.
 */
@Module
public class MainModule extends BaseModule {

    private RxAppCompatActivity activity;

    public MainModule(RxAppCompatActivity activity, String questTag) {
        super(questTag);
        this.activity = activity;
    }

    @Provides
    public MainPresenterImpl getDagger2PresenterImpl(RetrofitManager retrofitManager) {
        return new MainPresenterImpl(activity, retrofitManager);
    }
}
