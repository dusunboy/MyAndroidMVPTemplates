package $Package.core.view.custom_dialog;

import android.content.DialogInterface;

/**
 * 监听提示框消失
 * Created by Vincent on $Time.
 */
public interface OnDismiss {
    /**
     * 监听提示框取消
     * @param dialog
     * @param type
     */
    void OnDismissCall(DialogInterface dialog, int type);
}
