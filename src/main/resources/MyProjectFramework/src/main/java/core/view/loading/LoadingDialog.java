package $Package.core.view.loading;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout.LayoutParams;

import $Package.R;

/**
 * 全局加载框
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class LoadingDialog {


	private static LoadingDialog instance;

	private Dialog loadingDialog;

	public LoadingDialog(Context context) {
		View progress_loading = LayoutInflater.from(context).inflate(R.layout.progress_loading, null);
		loadingDialog = new Dialog(context, R.style.loading_dialog);
		loadingDialog.addContentView(progress_loading, new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, 
				LayoutParams.WRAP_CONTENT));
		loadingDialog.setCanceledOnTouchOutside(false);
	}

	public void show() {
		loadingDialog.show();
	}
	
	
	public void hide() {
		loadingDialog.dismiss();
	}


	public Dialog getLoadingDialog() {
		return loadingDialog;
	}


	public void setLoadingDialog(Dialog loadingDialog) {
		this.loadingDialog = loadingDialog;
	}


}
