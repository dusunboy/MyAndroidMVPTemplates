package $Package.$ModuleName;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import $Package.R;
import $Package.core.base.BasePresenterFragment;
import $Package.project.$ModuleName.dagger2.Dagger$NameComponent;
import $Package.project.$ModuleName.dagger2.$NameComponent;
import $Package.project.$ModuleName.dagger2.$NameModule;
import $Package.project.$ModuleName.presenter.$NamePresenterImpl;
import $Package.project.$ModuleName.view.$NameView;

import javax.inject.Inject;
/**
 * Created by Vincent on $Time.
 */
public class $Name extends BasePresenterFragment implements $NameView {

    @Inject
    $NamePresenterImpl basePresenter;

    @Override
    protected View getLayoutView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.$ModuleName, container, false);
    }

    @Override
    public void initToolbar(View view, Bundle savedInstanceState) {

    }

    @Override
    public void initView(View view, @Nullable Bundle savedInstanceState) {
    }

    @Override
    public void init(@Nullable Bundle savedInstanceState) {
        $NameComponent $LowerNameCaseComponent = Dagger$NameComponent.builder()
                .$LowerNameCaseModule(new $NameModule(this, getActivity().getLocalClassName())).build();
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
