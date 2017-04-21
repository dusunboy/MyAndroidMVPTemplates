package $Package.project.main.dagger2;

import $Package.project.main.MainActivity;

import dagger.Component;

/**
 * Created by Vincent on $Time.
 */
@Component(modules = MainModule.class)
public interface MainComponent {
    void inject(MainActivity activity);
}
