package $Package.project.main;

import android.annotation.SuppressLint;
import android.Manifest;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;

import $Package.R;
import $Package.core.base.BasePresenterActivity;
import $Package.core.config.BaseConstant;
import $Package.core.fuction.AppUtil;
import $Package.core.fuction.FileUtil;
import $Package.core.fuction.PermissionUtil;
import $Package.core.fuction.SPUtil;
import $Package.core.view.custom_dialog.CustomAlertDialog;
import $Package.core.view.custom_dialog.CustomAlertDialogBuilder;
import $Package.project.main.dagger2.DaggerMainComponent;
import $Package.project.main.dagger2.MainComponent;
import $Package.project.main.dagger2.MainModule;
import $Package.project.main.presenter.MainPresenterImpl;
import $Package.project.main.view.MainView;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Vincent on $Time.
 */
public class MainActivity extends BasePresenterActivity implements MainView,  EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_EXTERNAL_STORAGE_PERM = 101;
    @Inject
    MainPresenterImpl basePresenter;

    @Override
    public int getLayoutView(Bundle savedInstanceState) {
        return R.layout.activity_main;
    }

    @Override
    public void initToolbar() {

    }

    @Override
    public void initView() {
    }

    @Override
    public void init() {
        MainComponent mainComponent = DaggerMainComponent.builder()
                .mainModule(new MainModule(this, getLocalClassName())).build();
        mainComponent.inject(this);
        basePresenter.setViewListener(this);
        initPermission();
    }

    @Override
    public void initPermission() {
        //检查权限
        String[] permissions = PermissionUtil.checkPermission(this);
        if (permissions.length == 0) {
            //权限都申请了
            try {
                //创建SD卡目录
                FileUtil.createDirFile(SPUtil.getString(BaseConstant.DIRECTORY, ""));
                //创建图片路径
                FileUtil.createDirFile(SPUtil.getString(BaseConstant.IMAGE_PATH, ""));
                //创建APk路径
                FileUtil.createDirFile(SPUtil.getString(BaseConstant.APK_PATH, ""));
            } catch (IOException e) {
                e.printStackTrace();
            }
            SPUtil.put(BaseConstant.PHONE_MODEL, android.os.Build.MODEL);
            SPUtil.put(BaseConstant.PHONE_IMEI, AppUtil.getIMEI());
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //申请权限
                ActivityCompat.requestPermissions(this, permissions, 100);
            }
        }
    }

    @SuppressLint("CheckResult")
    @Override
    public void clickEvent() {
    }



    @Override
    public void showLoading() {
    }

    @Override
    public void hideLoading() {
    }

    /**
     * EsayPermissions接管权限处理逻辑
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        // Forward results to EasyPermissions
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {
        //权限授予成功
        for (String perm : perms) {
            if (perm.equals(Manifest.permission.READ_PHONE_STATE)) {
                SPUtil.put(BaseConstant.PHONE_MODEL, android.os.Build.MODEL);
                SPUtil.put(BaseConstant.PHONE_IMEI, AppUtil.getIMEI());
            } else if (perm.equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                try {
                    //创建SD卡目录
                    FileUtil.createDirFile(SPUtil.getString(BaseConstant.DIRECTORY, ""));
                    //创建图片路径
                    FileUtil.createDirFile(SPUtil.getString(BaseConstant.IMAGE_PATH, ""));
                    //创建APk路径
                    FileUtil.createDirFile(SPUtil.getString(BaseConstant.APK_PATH, ""));
                    //创建matisse库的图片路径
                    FileUtil.createDirFile(SPUtil.getString(BaseConstant.PICTURES_PATH, ""));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {
        //拒绝授予权限
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //用户选择禁止后不再询问才会跑这一段代码
            new AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.prompt))
                    .setRationale(getString(R.string.apply_for_authorize_it))
                    .setPositiveButton(getString(R.string.confirm))
                    .setNegativeButton(getString(R.string.cancel))
                    .setRequestCode(REQUEST_EXTERNAL_STORAGE_PERM)
                    .build()
                    .show();
        } else {
            CustomAlertDialogBuilder customAlertDialogBuilder = new CustomAlertDialogBuilder(this);
            customAlertDialogBuilder.create(CustomAlertDialog.NORMAL);
            customAlertDialogBuilder.setTitle(getString(R.string.prompt));
            customAlertDialogBuilder.setMessage(getString(R.string.denied_permissions_not_use_function));
            customAlertDialogBuilder.setPositiveButton(getString(R.string.got_it), null);
            customAlertDialogBuilder.setCancelable(false);
            customAlertDialogBuilder.show();
        }
    }

}

