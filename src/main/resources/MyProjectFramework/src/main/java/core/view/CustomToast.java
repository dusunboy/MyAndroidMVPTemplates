package $Package.core.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.widget.Toast;

import $Package.core.base.BaseApp;


/**
 * 自定义Toast
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class CustomToast {

	private static CustomToast instance;
	private Toast toast;
	private Context context;

	@SuppressLint("ShowToast")
	public CustomToast(Context context) {
		toast = new Toast(context);
		toast = Toast.makeText(context, "", Toast.LENGTH_SHORT);
		this.context = context;
	}

	public static CustomToast getInstance() {
		if (instance == null) {
			instance = new CustomToast(BaseApp.getAppContext());
		}
		return instance;
	}
	
	/** 显示自定义Toast提示(来自res) **/
	public void show(int resId) {
//		View toastRoot = LayoutInflater.from(context).inflate(
//				R.layout.custom_toast, null);
//		((TextView) toastRoot.findViewById(R.id.toast_text))
//				.text(context.getString(resId));
//		toast.setGravity(Gravity.BOTTOM, 0, 0);
//		toast.setView(toastRoot);
		if (toast != null) {
			toast.setText(context.getString(resId));
//			toast.setDuration(Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/** 显示自定义Toast提示(来自String) **/
	public void show(String text) {
		if (text != null && !text.equals("")) {
//			View toastRoot = LayoutInflater.from(context).inflate(
//					R.layout.custom_toast, null);
//			((TextView) toastRoot.findViewById(R.id.toast_text)).text(text);
//			toast.setGravity(Gravity.BOTTOM, 0, 0);
//			toast.setView(toastRoot);
			if (toast != null) {
				toast.setText(text);
//				toast.setDuration(Toast.LENGTH_SHORT);
				toast.show();
			}
		}
	}

	public CustomToast setGravity(int gravity, int xOffset, int yOffset) {
		toast.setGravity(gravity, xOffset, yOffset);
		return this;
	}

	public boolean isShow() {
		if (toast == null) {
			return  false;
		} else {
			return toast.getView().isShown();
		}
	}
}
