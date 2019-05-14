package $Package.project.$ModuleName.presenter;

import com.trello.rxlifecycle3.components.support.RxAppCompatActivity;
import $Package.core.mvp.BasePresenterActivityImpl;
import $Package.core.mvp.BaseView;
import $Package.core.retrofit.RetrofitManager;
import $Package.project.$ModuleName.view.$NameView;

import javax.inject.Inject;

/**
 * Created by Vincent on $Time.
 */
public class $NamePresenterImpl extends BasePresenterActivityImpl implements $NamePresenter {

    private $NameView baseView;

    @Inject
    public $NamePresenterImpl(RxAppCompatActivity activity, RetrofitManager retrofitManager) {
        super(activity, retrofitManager);
    }


    @Override
    public void start2Login() {

    }

    @Override
    public void setViewListener(BaseView view) {
        baseView = ($NameView) view;
    }


}
