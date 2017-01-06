package $Package;

/**
 * Created by Vincent on $Time.
 */
public class $NamePresenterImpl extends BasePresenterActivityImpl implements $NamePresenter {

    private $NameView baseView;

    public $NamePresenterImpl(RxAppCompatActivity activity) {
        super(activity);
    }


    @Override
    public void start2Login() {

    }

    @Override
    public void setViewListener(BaseView view) {
        baseView = ($NameView) view;
    }
}
