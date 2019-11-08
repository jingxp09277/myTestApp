package com.ask.commonLib;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Build;
import android.os.SystemClock;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import static com.ask.commonLib.OtherUtil.getDeviceBrand;
import static com.ask.commonLib.OtherUtil.toast;

/**
 * Created by anc on 2019/4/18
 */
public class PermissionUtil {
    public static final String XIAOMI = "Xiaomi";
    public static final String VIVO = "vivo";
    public static final String HUAWEI = "HUAWEI";
    public static final String KEY_EXTRA_PERMISSIONS = "permissions";
    public static final String KEY_EXTRA_CALLBACK = "callback";
    public static final String KEY_EXTRA_REQUEST_CODE = "request_code";
    private static final String TAG = PermissionUtil.class.getSimpleName();
    private static HashMap<Long, PermissionUtilCallback> mCallbacks = new HashMap<>();

    /**
     * @param context the context who request
     * @return the permissions declared in the manifest file
     */
    public static String[] getAppPermissions(Context context) {
        try {
            PackageInfo packageInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_PERMISSIONS);
            return packageInfo.requestedPermissions;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return new String[]{};
    }

    /**
     * @param context     who request
     * @param permissions permission array
     * @param requestCode r
     * @param callback    the callback of this util
     */
    public static boolean checkAndRequestPermissions(@NonNull Context context, @NonNull String[] permissions, int requestCode, @Nullable PermissionUtilCallback callback) {
        if (permissions.length <= 0) {
            Log.e(TAG, "checkAndRequestPermissions: length illegal");
            return false;
        }
        if (requestCode < 0) requestCode = (int) (Math.random() * 1000);
        boolean isGranted = checkPermission(context, permissions);
        Log.d(TAG, "checkAndRequestPermissions: all granted " + isGranted);
        if (!isGranted) {
            long callbackKey = SystemClock.uptimeMillis();
            mCallbacks.put(callbackKey, callback);
            requestPermissions(context, permissions, callbackKey, requestCode);
        } else {
            if (callback != null) callback.onAllPermissionsGranted(requestCode);
        }
        return isGranted;
    }

    public static boolean shouldShowRequestPermissionRationale(@NonNull Context context, @NonNull String per) {
        boolean rationale;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean isActivity = context instanceof Activity;
            if (!isActivity)
                Log.e(TAG, "shouldShowRequestPermissionRationale: not activity return false.per* = " + per);
            rationale = isActivity && ((Activity) context).shouldShowRequestPermissionRationale(per);
            if (TextUtils.equals(VIVO, getDeviceBrand())) {
                rationale = !rationale;
            }
        } else {
            rationale = true;
        }
        return rationale;
    }

    public static boolean checkPermission(@NonNull Context context, @NonNull String... permissions) {
        return checkPermission(context, false, permissions);
    }

    public static boolean checkPermission(@NonNull Context context, boolean isShowTip, @NonNull String... permissions) {
        if (permissions.length <= 0) {
            throw new RuntimeException("permissions illegal length.");
        }
        for (String per : permissions) {
            if (ContextCompat.checkSelfPermission(context, per) != PackageManager.PERMISSION_GRANTED || !checkPermissionAgain(context, per, isShowTip)) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBluetoothEnable(Context context) {
        @SuppressLint("MissingPermission") boolean enabled = BluetoothAdapter.getDefaultAdapter().isEnabled();
        if (!enabled) {
            toast(context, R.string.please_open_bluetooth);
        }
        return enabled;
    }

    /**
     * when you called this method , you should rewrite the
     * Activity.onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
     * to handler the request result
     *
     * @param context     .
     * @param permissions request permissions
     * @param callbackKey
     * @param requestCode .
     */
    private static void requestPermissions(@NonNull Context context, @NonNull String[] permissions, long callbackKey, @NonNull int requestCode) {
        ArrayList<String> shouldRequest = new ArrayList<>();
        for (String per : permissions) {
            if (!checkPermission(context, per)) {
                shouldRequest.add(per);
            }
        }

        if (shouldRequest.size() <= 0) {
            Log.e(TAG, "requestPermissions: all permission is granted");
            onPermissionResult(context, permissions, callbackKey, requestCode);
            return;
        }
        Intent intent = new Intent(context, PermissionActivity.class);
        intent.putExtra(KEY_EXTRA_PERMISSIONS, permissions);
        intent.putExtra(KEY_EXTRA_CALLBACK, callbackKey);
        intent.putExtra(KEY_EXTRA_REQUEST_CODE, requestCode);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void onPermissionResult(@NonNull Context context, @NonNull String[]
            permissions, @NonNull long keyCallback, @NonNull int requestCode) {
        Log.d(TAG, "onPermissionResult: ");
        if (permissions.length == 0) {
            Log.e(TAG, "onPermissionResult: length illegal ");
            return;
        }
        ArrayList<String> grantedPers = new ArrayList<>();
        ArrayList<String> deniedPers = new ArrayList<>();
        boolean isAllGranted = true;
        for (String per : permissions) {
            boolean isGranted = checkPermission(context, true, per);
            Log.e(TAG, "onPermissionResult: isGranted = " + isGranted + ", per = " + per + ", Rationale = " + shouldShowRequestPermissionRationale(context, per));
            if (isGranted) {
                grantedPers.add(per);
            } else {
                deniedPers.add(per);
            }
            isAllGranted = isAllGranted && isGranted;
        }

        PermissionUtilCallback callback = fetchCallbak(keyCallback);
        if (callback != null) {
            if (isAllGranted) {
                callback.onAllPermissionsGranted(requestCode);
            }
            if (grantedPers.size() > 0)
                callback.onPermissionsGranted(requestCode, grantedPers.toArray(new String[0]));
            if (deniedPers.size() > 0)
                callback.onPermissionsDented(requestCode, deniedPers.toArray(new String[0]));
        }
    }

    public static boolean checkPermissionAgain(final Context context, String permission, boolean isShowTip) {
        boolean b = true;
        switch (permission) {
            case Manifest.permission.ACCESS_FINE_LOCATION:
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !checkGPSIsOpen(context)) {
                    if (isShowTip) toast(context, R.string.open_gps);
                    b = false;
                }
                break;
            case Manifest.permission.RECORD_AUDIO:
                if (Build.VERSION.SDK_INT == Build.VERSION_CODES.M && !checkRecordPermission()) {
                    Log.e(TAG, "checkPermissionAgain: 23 && record do not work normally!");
                    if (isShowTip) toast(context, R.string.recorder_is_unusual);
                    b = false;
                }
                break;
        }
        return b;
    }

    private static boolean checkGPSIsOpen(Context context) {
        boolean isGpsOpened = false;
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            isGpsOpened = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        Log.d(TAG, "checkGPSIsOpen: " + isGpsOpened);
        return isGpsOpened;
    }

    /**
     * check the record permission on the VERSION_CODES bigger than 23 (android 6.0)
     *
     * @return whether the record normal
     */
    private static boolean checkRecordPermission() {
        int minBufferSize = AudioRecord.getMinBufferSize(8000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        int bufferSizeInBytes = 640;
        byte[] audioData = new byte[bufferSizeInBytes];
        int readSize;
        AudioRecord audioRecord = null;
        try {
            audioRecord = new AudioRecord(MediaRecorder.AudioSource.DEFAULT, 8000,
                    AudioFormat.CHANNEL_IN_MONO,
                    AudioFormat.ENCODING_PCM_16BIT, minBufferSize);
            audioRecord.startRecording();
        } catch (Exception e) {
            if (audioRecord != null) {
                audioRecord.release();
            }
            return false;
        }
        if (audioRecord.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord.stop();
            audioRecord.release();
            return false;
        } else {
            readSize = audioRecord.read(audioData, 0, bufferSizeInBytes);
            if (readSize <= 0) {
                audioRecord.stop();
                audioRecord.release();
                Log.e(TAG, "no audio record data.");
                return false;
            } else {
                audioRecord.stop();
                audioRecord.release();
                return true;
            }
        }
    }

    /**
     * 跳转到当前应用对应的设置页面
     *
     * @param context
     */
    public static void gotoSetting(@NonNull Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        context.startActivity(intent);
    }

    private static PermissionUtilCallback fetchCallbak(long key) {
        return mCallbacks.remove(key);
    }


    public interface PermissionUtilCallback extends Serializable {
        void onAllPermissionsGranted(int requestCode);

        void onPermissionsGranted(int requestCode, String[] permissions);

        void onPermissionsDented(int requestCode, String[] permissions);
    }
}
