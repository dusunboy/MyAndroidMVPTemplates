package $Package;

/**
 * Created by Vincent on $Time.
 */
public class $NameActivity extends BasePresenterActivity<$NamePresenterImpl> implements $NameView {

    @Override
    public int getLayoutView(Bundle savedInstanceState) {
        return "";
    }

    @Override
    public void initToolbar() {

    }

    @Override
    public void initView() {

    }

    @Override
    public void init() {
        basePresenter.setViewListener(this);
    }

    @Override
    public void clickEvent() {

    }

    @Override
    public Class<$NamePresenterImpl> getPresenterClass() {
        return $NamePresenterImpl.class;
    }

    @Override
    public void showLoading(String prompt) {

    }

    @Override
    public void hideLoading() {

    }
}
