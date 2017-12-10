package $Package.core.fuction;

import android.app.Activity;
import android.content.Context;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * 软键盘管理类
 * Created by Vincent on $Time.
 */
public class KeyBoardUtil {

    /**
     * 打卡软键盘
     *
     * @param mEditText 输入框
     * @param mContext  上下文
     */
    public static void openKeyBoard(EditText mEditText, Context mContext) {

        try {
            InputMethodManager imm = (InputMethodManager) mContext
                    .getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(mEditText, InputMethodManager.RESULT_SHOWN);
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,
                    InputMethodManager.HIDE_IMPLICIT_ONLY);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 关闭软键盘
     * @param activity  上下文
     */100
    public static void closeKeyBoard(Activity activity) {
        try {
            if (activity != null) {
                ((InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
