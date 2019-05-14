package $Package.core.view.custom_dialog;

import android.content.DialogInterface;

/**
 * 监听提示框消失
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public interface OnDismiss {
    /**
     * 监听提示框取消
     * @param dialog
     * @param type
     */
    void onDismissCall(DialogInterface dialog, int type);
}
