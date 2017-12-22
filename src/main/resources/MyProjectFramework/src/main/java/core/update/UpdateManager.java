package $Package.core.update;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import com.daimajia.numberprogressbar.NumberProgressBar;
import $Package.BuildConfig;
import $Package.R;
import $Package.core.activities.ActivitiesManager;
import $Package.core.async_http.AsyncHttpReq;
import $Package.core.async_http.AsyncHttpResponse;
import $Package.core.async_http.MyRequestParams;
import $Package.core.async_http.RxAsyncHttpReq;
import $Package.core.config.BaseConstant;
import $Package.core.fuction.SPUtil;
import $Package.core.retrofit.BaseResourceObserver;
import $Package.core.rxjava.RxSchedulers;
import $Package.core.view.CustomToast;
import com.trello.rxlifecycle2.android.ActivityEvent;
import com.trello.rxlifecycle2.components.support.RxAppCompatActivity;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.processors.FlowableProcessor;
import io.reactivex.schedulers.Schedulers;

/**
 * 升级Apk管理类
 * Created by Vincent on 2017-12-02 16:32:29.
 */
public class UpdateManager {

    private RxAppCompatActivity activity;
    private String url;
    private boolean isForceClose;
    private int color;
    private String changeLog;
    private boolean isShow;
    private boolean isUpdate;
    private NumberProgressBar numberProgressBar;
    private AlertDialog alertProgressDialog;

    public UpdateManager(RxAppCompatActivity rxAppCompatActivity) {
        color = 0;
        this.activity = rxAppCompatActivity;
    }

    /**
     * 显示提示提示框
     */
    public void showNoticeDialog() {
        isShow = true;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        alertDialog.setTitle(R.string.soft_update_title);
        if (!changeLog.equals("")) {
            alertDialog.setMessage(activity.getString(R.string.changeLog) + "\n" + changeLog);
        } else {
            alertDialog.setMessage(activity.getString(R.string.soft_update_info));
        }
        alertDialog.setPositiveButton(R.string.update,
                new OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        if (Environment.getExternalStorageState().equals(
                                Environment.MEDIA_MOUNTED)) {
                            showDownloadedDialog();
                        } else {
                            CustomToast.getInstance().show(activity.getString(R.string.sd_failed));
                        }
                    }
                });
        alertDialog.setNegativeButton(R.string.soft_update_later,
                new OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        forceClose();
                    }
                });
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            forceClose();
                            break;
                    }
                }
                return false;
            }
        });
        alertDialog.show();
    }

    /**
     * 强制关闭
     */
    private void forceClose() {
        if (isForceClose) {
            CustomToast.getInstance().show(activity.getString(R.string.please_update_version));
            Observable.just("")
                    .compose(activity.bindToLifecycle())
                    .compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
                    .delay(1000, TimeUnit.MILLISECONDS)
                    .subscribe(new Consumer<String>() {
                        @Override
                        public void accept(@NonNull String s) throws Exception {
                            popAllActivity();
                        }
                    });
        }
    }

    /**
     * 显示下载提示框
     */
    private void showDownloadedDialog() {
        if (url == null || url.equals("")) {
            CustomToast.getInstance().show(activity.getString(R.string.download_link_invalid));
            return;
        }
        isShow = true;
        isUpdate = true;
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(activity);
        View dialog_update_manager = LayoutInflater.from(activity).inflate(R.layout.dialog_uplate_manager, null);
        numberProgressBar = (NumberProgressBar) dialog_update_manager.findViewById(R.id.numberProgressBar);
        alertDialog.setTitle(activity.getString(R.string.update))
                .setView(dialog_update_manager);
        alertDialog.setCancelable(false);
        alertDialog.setNegativeButton(activity.getString(R.string.cancel), new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (isUpdate) {
                    isUpdate = false;
                    AsyncHttpReq.cancelRequestsByTAG(activity.getLocalClassName(), true);
                }
                forceClose();
            }
        });
        alertDialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_DOWN) {
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_BACK:
                            if (isUpdate) {
                                isUpdate = false;
                                AsyncHttpReq.cancelRequestsByTAG(activity.getLocalClassName(), true);
                            }
                            forceClose();
                            break;
                    }
                }
                return false;
            }
        });
        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if (isUpdate) {
                    isUpdate = false;
                    AsyncHttpReq.cancelRequestsByTAG(activity.getLocalClassName(), true);
                }
                forceClose();
            }
        });
        alertDialog.show();
        alertProgressDialog = alertDialog.create();
        MyRequestParams myRequestParams = new MyRequestParams();
        myRequestParams.setDownLoadMethod(MyRequestParams.GET);
        myRequestParams.setDownloadSavePath(SPUtil.getString(BaseConstant.APK_PATH) + "/"
                + activity.getString(R.string.app_name) + ".apk");
        ArrayList<FlowableProcessor<AsyncHttpResponse>> observables = new RxAsyncHttpReq().download(activity, url, myRequestParams);
        observables.get(1).compose(activity.bindUntilEvent(ActivityEvent.DESTROY)).subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .onBackpressureBuffer()
                .subscribe(new Consumer<AsyncHttpResponse>() {
                    @Override
                    public void accept(@NonNull AsyncHttpResponse asyncHttpResponse) throws Exception {
                        numberProgressBar.setProgress(asyncHttpResponse.getProgressPercent());
                    }
                });
        observables.get(0).compose(activity.bindUntilEvent(ActivityEvent.DESTROY))
                .compose(RxSchedulers.flowable_io_mainThread())
                .toObservable()
                .subscribe(new BaseResourceObserver<AsyncHttpResponse>() {
                    @Override
                    public void onComplete() {
                        isUpdate = false;
                        alertProgressDialog.dismiss();
                    }

                    @Override
                    public void onNext(AsyncHttpResponse asyncHttpResponse) {
                        super.onNext(asyncHttpResponse);
                        alertProgressDialog.dismiss();
                        installApk(asyncHttpResponse.getFile());
                    }

                    @Override
                    public void onError(Throwable e) {
                        super.onError(e);
                        alertProgressDialog.dismiss();
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

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if(Build.VERSION.SDK_INT >= 24) {
            //判读版本是否在7.0以上
            //参数1 上下文, 参数2 Provider主机地址 和配置文件中保持一致
            // 参数3  共享的文件
            Uri apkUri = FileProvider.getUriForFile(activity, BuildConfig.file_provider_authorities, apkFile);
            //添加这一句表示对目标应用临时授权该Uri所代表的文件
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            intent.setDataAndType(apkUri, "application/vnd.android.package-archive");
        }else{
            intent.setDataAndType(Uri.fromFile(apkFile), "application/vnd.android.package-archive");
        }
        activity.startActivity(intent);
    }


    /**
     * @return the url
     */
    public String getUrl() {
        return url;
    }

    /**
     * 设置升级地址
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * 升级提示框是否显示
     * @return boolean
     */
    public boolean isShow() {
        return isShow;
    }

    public void setShow(boolean show) {
        isShow = show;
    }

    /**
     * 关闭所有Activity
     */
    private void popAllActivity() {
        ActivitiesManager activitiesManager = ActivitiesManager.getInstance();
        activitiesManager.popAllActivity();
    }


}













