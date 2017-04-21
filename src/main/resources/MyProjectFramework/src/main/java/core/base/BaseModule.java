package $Package.core.base;

import $Package.core.retrofit.RetrofitManager;

import dagger.Module;
import dagger.Provides;

/**
 * Dagger2的Module基类
 * Created by Vincent on $Time.
 */
@Module
public class BaseModule {

    private String requestTag;

    public BaseModule(String requestTag) {
        this.requestTag = requestTag;
    }

    @Provides
    public RetrofitManager getRetrofitManager() {
        return new RetrofitManager(requestTag);
    }
}
