package com.ask.commonLib;

/**
 * Created by dfqin on 2017/1/22.
 */

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PermissionActivity extends AppCompatActivity {
    private static final String TAG = PermissionActivity.class.getSimpleName();
    private final ArrayList<String> mRationalePermissions = new ArrayList<>();
    private String[] mRequestPermissions;
    private Activity mActivity;
    private int mRequestCode;
    private boolean mIsRequesting;
    private long mCallbackKey;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() == null || !getIntent().hasExtra(PermissionUtil.KEY_EXTRA_PERMISSIONS)) {
            finish();
            return;
        }
        mActivity = this;
        mRequestPermissions = getIntent().getStringArrayExtra(PermissionUtil.KEY_EXTRA_PERMISSIONS);
        mRequestCode = getIntent().getIntExtra(PermissionUtil.KEY_EXTRA_REQUEST_CODE, -1);
        mCallbackKey = getIntent().getLongExtra(PermissionUtil.KEY_EXTRA_CALLBACK, -1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestPermissions(); // 请求权限,回调时会触发onResume
    }

    // 请求权限兼容低版本
    private void requestPermissions() {
        Log.d(TAG, "requestPermissions: ");
        if (mIsRequesting) return;
        mIsRequesting = true;
        List<String> requestablePermissions = new ArrayList<>();
        for (String per : mRequestPermissions) {
            if (PermissionUtil.shouldShowRequestPermissionRationale(mActivity, per)) {
                if (!(Build.VERSION.SDK_INT < Build.VERSION_CODES.P && TextUtils.equals(per, Manifest.permission.FOREGROUND_SERVICE)))
                    mRationalePermissions.add(per);
            }
            if (!PermissionUtil.checkPermission(mActivity, per)) {
                Log.d(TAG, "requestPermissions: request per = " + per);
                requestablePermissions.add(per);
            }
        }

        if (!requestablePermissions.isEmpty()) {
            ActivityCompat.requestPermissions(mActivity, requestablePermissions.toArray(new String[0]), mRequestCode);
        } else {
            onRequestResult();
        }
    }

    private void onRequestResult() {
        PermissionUtil.onPermissionResult(mActivity, mRequestPermissions, mCallbackKey, mRequestCode);
        if (!mRationalePermissions.isEmpty() && !PermissionUtil.checkPermission(mActivity, mRationalePermissions.toArray(new String[0]))) {
            showHasPermissionUnrequestableDialog();
        } else {
            finish();
        }
    }


    /**
     * 用户权限处理,
     * 如果全部获取, 则直接过.
     * 如果权限缺失, 则提示Dialog.
     *
     * @param requestCode  请求码
     * @param permissions  权限
     * @param grantResults 结果
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        //部分厂商手机系统返回授权成功时，厂商可以拒绝权限，所以要用PermissionChecker二次判断
        if (requestCode == mRequestCode) {
            onRequestResult();
        }
    }

    // 显示缺失权限提示
    private void showHasPermissionUnrequestableDialog() {

        final AlertDialog.Builder builder = new AlertDialog.Builder(PermissionActivity.this);
        builder.setTitle(R.string.dialog_prompt);
        builder.setMessage(R.string.dialog_content);
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });

        builder.setPositiveButton(R.string.setting, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                PermissionUtil.gotoSetting(PermissionActivity.this);
                finish();
            }
        });

        builder.setCancelable(false);
        builder.show();
    }


    protected void onDestroy() {
        super.onDestroy();
    }

}

