package $Package.project.$ModuleName.presenter;

import com.trello.rxlifecycle3.components.support.RxFragment;
import $Package.core.mvp.BasePresenterFragmentImpl;
import $Package.core.mvp.BaseView;
import $Package.core.retrofit.RetrofitManager;
import $Package.project.$ModuleName.view.$NameView;

import javax.inject.Inject;

/**
 * Created by Vincent on $Time.
 */
public class $NamePresenterImpl extends BasePresenterFragmentImpl implements $NamePresenter {

    private $NameView baseView;

    @Inject
    public $NamePresenterImpl(RxFragment rxFragment, RetrofitManager retrofitManager) {
        super(rxFragment, retrofitManager);
    }


    @Override
    public void start2Login() {

    }

    @Override
    public void setViewListener(BaseView view) {
        baseView = ($NameView) view;
    }


}
