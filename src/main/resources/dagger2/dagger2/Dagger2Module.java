package $Package.project.$ModuleName.dagger2;

import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;
import $Package.core.base.BaseModule;
import $Package.core.retrofit.RetrofitManager;
import $Package.project.$ModuleName.presenter.$NamePresenterImpl;

import dagger.Module;
import dagger.Provides;
/**
 * Created by Vincent on $Time.
 */
@Module
public class $NameModule extends BaseModule {

    private RxAppCompatActivity activity;

    public $NameModule(RxAppCompatActivity activity, String questTag) {
        super(questTag);
        this.activity = activity;
    }

    @Provides
    public $NamePresenterImpl getDagger2PresenterImpl(RetrofitManager retrofitManager) {
        return new $NamePresenterImpl(activity, retrofitManager);
    }
}
