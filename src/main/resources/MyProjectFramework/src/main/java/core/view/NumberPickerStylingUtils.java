package $Package.core.view;

import android.app.TimePickerDialog;
import android.graphics.drawable.ColorDrawable;
import android.widget.DatePicker;
import android.widget.NumberPicker;
import android.widget.TimePicker;

import $Package.core.base.BaseApp;

import java.lang.reflect.Field;

/**
 * 数字选择组件样式工具
 * Created by Vincent on $Time.
 */
public class NumberPickerStylingUtils {

    private NumberPickerStylingUtils() {}

    public static void applyStyling(TimePickerDialog timePickerDialog, int colorId) {
        try {
            Field field = TimePickerDialog.class.getDeclaredField("mTimePicker");
            field.setAccessible(true);
            applyStyling((TimePicker) field.get(timePickerDialog), colorId);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void applyStyling(TimePicker timePicker, int colorId) {
        try {
            Field fields[] = TimePicker.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().equals(NumberPicker.class)) {
                    field.setAccessible(true);
                    applyStyling((NumberPicker) field.get(timePicker), colorId);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void applyStyling(DatePicker datePicker, int colorId) {
        try {
            Field fields[] = DatePicker.class.getDeclaredFields();
            for (Field field : fields) {
                if (field.getType().equals(NumberPicker.class)) {
                    field.setAccessible(true);
                    applyStyling((NumberPicker) field.get(datePicker), colorId);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public static void applyStyling(NumberPicker numberPicker, int colorId) {
        try {
            Field field = NumberPicker.class.getDeclaredField("mSelectionDivider");
            field.setAccessible(true);
            ColorDrawable colorDrawable = new ColorDrawable(BaseApp.getAppContext()
                    .getResources().getColor(colorId));
            field.set(numberPicker, colorDrawable);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
