package $Package.project.$ModuleName.dagger2;

import com.trello.rxlifecycle2.components.support.RxFragment;
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

    private RxFragment fragment;

    public $NameModule(RxFragment fragment, String questTag) {
        super(questTag);
        this.fragment = fragment;
    }

    @Provides
    public $NamePresenterImpl getDagger2PresenterImpl(RetrofitManager retrofitManager) {
        return new $NamePresenterImpl(fragment, retrofitManager);
    }
}
