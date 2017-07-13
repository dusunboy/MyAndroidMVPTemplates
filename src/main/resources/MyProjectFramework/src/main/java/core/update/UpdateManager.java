package $Package.core.update;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;

import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;
import $Package.R;
import $Package.core.activities.ActivitiesManager;
import $Package.core.config.BaseConstant;
import $Package.core.fuction.DensityUtil;
import $Package.core.fuction.SPUtil;
import $Package.core.retrofit.BaseResourceObserver;
import $Package.core.rxjava.RxSchedulers;
import $Package.core.view.CustomToast;
import $Package.core.view.custom_dialog.CustomAlertDialog;
import $Package.core.view.custom_dialog.CustomProgressDialog;
import $Package.core.view.custom_dialog.OnDismiss;
import $Package.core.async_http.AsyncHttpReq;
import $Package.core.async_http.AsyncHttpResponse;
import $Package.core.async_http.MyRequestParams;
import $Package.core.async_http.RxAsyncHttpReq;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.schedulers.Schedulers;

/**
 * 升级Apk管理类
 * Created by Vincent on $Time.
 */
public class UpdateManager implements OnDismiss {

    private RxAppCompatActivity activity;
    private CustomProgressDialog customProgressDialog;
    private String url;
    private boolean isForceClose;
    private boolean isConfirmDialog;
    private String directory;
    private int color;
    private String changeLog;
    private boolean isShow;
    private boolean isUpdate;

    public UpdateManager(RxAppCompatActivity rxAppCompatActivity) {
        color = 0;
        directory = SPUtil.getString(BaseConstant.DIRECTORY);
        this.activity = rxAppCompatActivity;
    }

    /**
     * 显示提示提示框
     */
    public void showNoticeDialog() {
        isShow = true;
        CustomAlertDialog.Builder builder = new CustomAlertDialog.Builder(activity);
        builder.setTheme(color);
        builder.create(CustomAlertDialog.NORMAL);
        builder.setTitle(R.string.soft_update_title);
        builder.setOnDismissListener(this);
        if (!changeLog.equals("")) {
            builder.setMessageTextSize(DensityUtil.dp2px(10));
            builder.setMessage(activity.getString(R.string.changeLog) + "\n" + changeLog);
        } else {
            builder.setMessageTextSize(DensityUtil.dp2px(13));
            builder.setMessage(activity.getString(R.string.soft_update_info));
        }
        builder.setPositiveButton(R.string.update,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        isConfirmDialog = true;
                        dialog.dismiss();
                        if (Environment.getExternalStorageState().equals(
                                Environment.MEDIA_MOUNTED)) {
                            showDownloadedDialog();
                        } else {
                            CustomToast.getInstance().show(activity.getString(R.string.sd_failed));
                        }
                    }
                });
        builder.setNegativeButton(R.string.soft_update_later,
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        builder.show();
    }


    /**
     * 显示下载提示框
     */
    private void showDownloadedDialog() {
        isShow = true;
        isUpdate = true;
        customProgressDialog = new CustomProgressDialog(activity, activity.getString(R.string.update));
        customProgressDialog.setTheme(color);
        customProgressDialog.create();
        customProgressDialog.getBuilder().setOnDismissListener(this);
        customProgressDialog.setNegativeButton(activity.getString(R.string.cancel), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        customProgressDialog.setMax(100);
        customProgressDialog.show();
        MyRequestParams myRequestParams = new MyRequestParams();
        myRequestParams.setDownloadSavePath(directory + "/apk/"
                + activity.getString(R.string.app_name) + ".apk");
        ArrayList<FlowableProcessor<AsyncHttpResponse>> observables = new RxAsyncHttpReq().download(activity, url, myRequestParams);
        observables.get(1).compose(activity.bindUntilEvent(ActivityEvent.DESTROY)).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onBackpressureBuffer()
                .subscribe(new Consumer<AsyncHttpResponse>() {
                    @Override
                    public void accept(@NonNull AsyncHttpResponse asyncHttpResponse) throws Exception {
                        customProgressDialog.setProgress(asyncHttpResponse.getProgressPercent());
                    }
                });
        observables.get(0).compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(RxSchedulers.flowable_io_mainThread())
                .toObservable()
                .subscribe(new BaseResourceObserver<AsyncHttpResponse>() {
                    @Override
                    public void onComplete() {
                        isUpdate = false;
                        customProgressDialog.dismiss();
                    }

                    @Override
                    public void onNext(AsyncHttpResponse asyncHttpResponse) {
                        super.onNext(asyncHttpResponse);
                        installApk(asyncHttpResponse.getFile());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        CustomToast.getInstance().show(activity.getString(R.string.download_failed));
                    }
                });
    }


    /**
     * 设置是否强制关闭
     *
     * @param isForceClose
     */
    public void setIsForceClose(boolean isForceClose) {
        this.isForceClose = isForceClose;
    }

    /**
     * 设置颜色主题
     *
     * @param color
     */
    public void setTheme(int color) {
        this.color = color;
    }

    /**
     * 设置更新日志
     *
     * @param changeLog
     */
    public void setChangeLog(String changeLog) {
        this.changeLog = changeLog;
    }

    private void installApk(File apkFile) {
        if (!apkFile.exists()) {
            CustomToast.getInstance().show(activity.getString(R.string.apk_failed));
            return;
        }
        CustomToast.getInstance().show(activity.getString(R.string.soft_update_complete));

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkFile.toString()),
                "application/vnd.android.package-archive");
        activity.startActivity(i);
    }


    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * @param url the url to set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 是否显示
     * @return
     */
    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    @Override
    public void onDismissCall(DialogInterface dialog, int type) {
        dialog.dismiss();
        if (!isConfirmDialog) {
            if (isUpdate) {
                isUpdate = false;
                AsyncHttpReq.cancelRequestsByTAG(activity.getLocalClassName(), true);
            }
            if (isForceClose) {
                CustomToast.getInstance().show(activity.getString(R.string.please_update_version));
                popAllActivity();
            }
        } else {
            isConfirmDialog = false;
        }
        isShow = false;
    }

    /**
     * 关闭所有Activity
     */
    private void popAllActivity() {
        ActivitiesManager activitiesManager = ActivitiesManager.getInstance();
        activitiesManager.popAllActivity();
    }


}













