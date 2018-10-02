package $Package.$ModuleName;

import android.annotation.SuppressLint;
import android.os.Bundle;

import $Package.R;
import $Package.core.base.BasePresenterActivity;
import $Package.project.$ModuleName.dagger2.Dagger$NameComponent;
import $Package.project.$ModuleName.dagger2.$NameComponent;
import $Package.project.$ModuleName.dagger2.$NameModule;
import $Package.project.$ModuleName.presenter.$NamePresenterImpl;
import $Package.project.$ModuleName.view.$NameView;

import javax.inject.Inject;
/**
 * Created by Vincent on $Time.
 */
public class $NameActivity extends BasePresenterActivity implements $NameView {

    @Inject
    $NamePresenterImpl basePresenter;

    @Override
    public int getLayoutView(Bundle savedInstanceState) {
        return R.layout.activity_$ModuleName;
    }

    @Override
    public void initToolbar() {

    }

    @Override
    public void initView() {
    }

    @Override
    public void init() {
        $NameComponent $LowerNameCaseComponent = Dagger$NameComponent.builder()
                .$LowerNameCaseModule(new $NameModule(this, getLocalClassName())).build();
        $LowerNameCaseComponent.inject(this);
        basePresenter.setViewListener(this);
    }

    @SuppressLint("CheckResult")
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
