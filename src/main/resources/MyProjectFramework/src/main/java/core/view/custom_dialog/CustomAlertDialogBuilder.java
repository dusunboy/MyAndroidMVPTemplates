package $Package.core.view.custom_dialog;

import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

/**
 * 自定义AlertDialogBuilder
 * Created by Vincent on $Time.
 */
public class CustomAlertDialogBuilder implements OnDismiss {
    private final CustomAlertDialog.Builder builder;
    private int theme;
    private OnDismiss onDismiss;

    public CustomAlertDialogBuilder(Context context) {
        builder = new CustomAlertDialog.Builder(context);
        builder.setOnDismissListener(this);

    }

    /**
     * 创建Builder类型
     * @param type 0进度条类型 1普通类型
     * @return
     */
    public CustomAlertDialog create(int type) {
        return builder.create(type);
    }

    /**
     * 设置消息文本
     * @param message
     */
    public void setMessage(String message) {
        builder.setMessage(message);
    }

    /**
     * 设置按钮1
     * @param text
     * @param onClickListener
     */
    public void setPositiveButton(String text, DialogInterface.OnClickListener onClickListener) {
        builder.setPositiveButton(text, onClickListener);
    }

    /**
     * 设置按钮2
     * @param text
     * @param onClickListener
     */
    public void setNegativeButton(String text, DialogInterface.OnClickListener onClickListener) {
        builder.setNegativeButton(text, onClickListener);
    }

    /**
     * 设置按钮3
     * @param text
     * @param onClickListener
     */
    public void setNeutralButton(String text, DialogInterface.OnClickListener onClickListener) {
        builder.setNeutralButton(text, onClickListener);
    }

    /**
     * 设置点击屏幕外是否能取消
     * @param cancelable
     */
    public void setCancelable(boolean cancelable) {
        builder.setCancelable(cancelable);
    }

    /**
     * 显示
     */
    public void show() {
        builder.show();
    }

    /**
     * 设置标题
     * @param title
     */
    public void setTitle(String title) {
        builder.setTitle(title);
    }

    /**
     * 设置主题颜色
     * @param theme
     */
    public void setTheme(int theme) {
        builder.setTheme(theme);
    }

    /**
     * 设置自定义View
     * @param view
     */
    public void setView(View view) {
        builder.setView(view);
    }

    /**
     * 隐藏
     */
    public void dismiss() {
        builder.dismiss();
    }


    /**
     * 判断是否显示
     * @return
     */
    public boolean isShowing() {
        return builder.isShowing();
    }

    /**
     * 设置监听提示框接口
     * @param onDismiss
     */
    public void setOnDismissListener(OnDismiss onDismiss) {
        this.onDismiss = onDismiss;
    }

    /**
     * 监听提示框消失
     * @param dialog
     * @param type
     */
    @Override
    public void OnDismissCall(DialogInterface dialog, int type) {
        if (onDismiss != null) {
            onDismiss.OnDismissCall(dialog, type);
        }
    }

    /**
     * 设置数组提示框
     * @param itemsId
     * @param listener
     */
    public void setItems(int itemsId, DialogInterface.OnClickListener listener){
        builder.setItems(itemsId, listener);
    }

    /**
     * 设置数组提示框
     * @param items
     * @param listener
     */
    public void setItems(String[] items, DialogInterface.OnClickListener listener){
        builder.setItems(items, listener);
    }

    /**
     * 设置单选提示框
     * @param items
     * @param checkedItem
     * @param listener
     */
    public void setSingleChoiceItems(String[] items, int checkedItem, final DialogInterface.OnClickListener listener) {
        builder.setSingleChoiceItems(items, checkedItem, listener);
    }

    public void setOnKeyDownListener(CustomAlertDialog.Builder.OnKeyDown onKeyDown) {
        builder.setOnKeyDownListener(onKeyDown);
    }

}
