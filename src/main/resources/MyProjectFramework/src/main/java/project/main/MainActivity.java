package $Package.project.main;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;

import $Package.R;
import $Package.core.base.BasePresenterActivity;
import $Package.core.fuction.AppUtil;
import $Package.core.fuction.PermissionUtil;
import $Package.core.view.custom_dialog.CustomAlertDialog;
import $Package.core.view.custom_dialog.CustomAlertDialogBuilder;
import $Package.project.main.dagger2.DaggerMainComponent;
import $Package.project.main.dagger2.MainComponent;
import $Package.project.main.dagger2.MainModule;
import $Package.project.main.presenter.MainPresenterImpl;
import $Package.project.main.view.MainView;

import java.util.List;

import javax.inject.Inject;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

/**
 * Created by Vincent on $Time.
 */
public class MainActivity extends BasePresenterActivity implements MainView, EasyPermissions.PermissionCallbacks {

    private static final int REQUEST_CODE_PERMISSION = 101;
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
        List<String> permissions = PermissionUtil.checkPermission(this);
        if (permissions.size() == 0) {
            AppUtil.createDefaultConfig(permissions);
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                //申请权限
                ActivityCompat.requestPermissions(this, permissions.toArray(new String[0]), REQUEST_CODE_PERMISSION);
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

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        //权限授予成功
        AppUtil.createDefaultConfig(perms);
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
        //拒绝部分授予权限
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            //用户选择禁止后不再询问才会跑这一段代码
            new AppSettingsDialog.Builder(this)
                    .setTitle(getString(R.string.prompt))
                    .setRationale(getString(R.string.apply_for_authorize_it))
                    .setPositiveButton(getString(R.string.confirm))
                    .setNegativeButton(getString(R.string.cancel))
                    .setRequestCode(REQUEST_CODE_PERMISSION)
                    .build()
                    .show();
        } else {
            CustomAlertDialogBuilder customAlertDialogBuilder = new CustomAlertDialogBuilder(this);
            customAlertDialogBuilder.create(CustomAlertDialog.NORMAL);
            customAlertDialogBuilder.setTitle(getString(R.string.prompt));
            customAlertDialogBuilder.setMessage(getString(R.string.denied_permissions_not_use_function));
            customAlertDialogBuilder.setPositiveButton(getString(R.string.got_it), null);
            customAlertDialogBuilder.setCancelable(false);
            customAlertDialogBuilder.setOnDismissListener((dialog, type) -> {
               finish();
            });
            customAlertDialogBuilder.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQUEST_CODE_PERMISSION:
                finish();
                break;
            default:
                break;
        }
    }

}

