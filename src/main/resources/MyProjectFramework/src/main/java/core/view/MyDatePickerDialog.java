package $Package.core.view;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.view.KeyEvent;
import android.view.View;

import androidx.annotation.RequiresApi;

import $Package.R;

/**
 * 我的日期选择框
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class MyDatePickerDialog extends DatePickerDialog {

    public MyDatePickerDialog(Context context, int themeResId, OnDateSetListener listener, int year, int monthOfYear, int dayOfMonth) {
        super(context, themeResId, listener, year, monthOfYear, dayOfMonth);
    }

    public MyDatePickerDialog(Context context, OnDateSetListener listener, int year, int month, int dayOfMonth) {
        super(context, listener, year, month, dayOfMonth);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MyDatePickerDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public MyDatePickerDialog(Context context) {
        super(context);
    }

    @Override
    protected void onStart() {
        super.onStart();
        init();
    }

    /**
     * 初始化
     */
    private void init() {
        Resources res = getContext().getResources();
        final int titleDividerId = res.getIdentifier("titleDivider", "id", "android");
        final View titleDivider = findViewById(titleDividerId);
        if (titleDivider != null) {
            titleDivider.setBackgroundColor(res.getColor(R.color.colorAccent));
        }
        NumberPickerStylingUtils.applyStyling(getDatePicker(), R.color.colorAccent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }
}
