package $Package.project.$ModuleName.dagger2;

import $Package.project.$ModuleName.$Name;

import dagger.Component;
/**
 * Created by Vincent on $Time.
 */
@Component(modules = $NameModule.class)
public interface $NameComponent {
    void inject($Name fragment);
}
