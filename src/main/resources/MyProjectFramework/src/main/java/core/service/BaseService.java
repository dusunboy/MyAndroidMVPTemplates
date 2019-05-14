package $Package.core.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Service基类
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class BaseService extends Service {
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }
}
