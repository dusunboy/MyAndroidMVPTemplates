package $Package.core.fuction;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.CallLog;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.telephony.TelephonyManager;

import $Package.core.base.BaseApp;
import $Package.core.model.CallLogBean;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

/**
 * 应用工具类
 * Created by Vincent on $Time.
 */
public class AppUtil {

    /**
     * 获取包信息
     * @return
     */
    public static PackageInfo getPackageInfo() {
        PackageManager packageManager = BaseApp.getAppContext().getPackageManager();
        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(BaseApp.getAppContext().getPackageName(), 0);
            return packageInfo;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取IMEI
     * @return
     */
    public static String getIMEI() {
        TelephonyManager tm = (TelephonyManager) BaseApp.getAppContext()
                .getSystemService(BaseApp.getAppContext().TELEPHONY_SERVICE);
        /*
         * 唯一的设备ID： GSM手机的 IMEI 和 CDMA手机的 MEID. Return null if device ID is not
		 * available.
		 */
        String imei = tm.getDeviceId();
        return imei;
    }

    /**
     * 获取手机号
     * @return
     */
    public static String getPhone() {
        TelephonyManager tm = (TelephonyManager) BaseApp.getAppContext()
                .getSystemService(BaseApp.getAppContext().TELEPHONY_SERVICE);
        String phone = tm.getLine1Number();
        return phone;
    }

    /**
     * 字符串转为MD5
     *
     * @param str
     * @return
     */
    public static String strToMd5(String str) {
        String md5Str = null;
        if (str != null && str.length() != 0) {
            try {
                MessageDigest md = MessageDigest.getInstance("MD5");
                md.update(str.getBytes());
                byte b[] = md.digest();
                int i;
                StringBuffer buf = new StringBuffer("");
                for (int offset = 0; offset < b.length; offset++) {
                    i = b[offset];
                    if (i < 0)
                        i += 256;
                    if (i < 16)
                        buf.append("0");

                    buf.append(Integer.toHexString(i));
                }
                // 32λ
                md5Str = buf.toString();
                // 16λ
                // md5Str = buf.toString().substring(8, 24);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return md5Str;
    }

    /**
     * 检测GPS是否打开
     * @return
     */
    public static boolean isGpsEnable() {
        LocationManager locationManager =
                ((LocationManager) BaseApp.getAppContext().getSystemService(Context.LOCATION_SERVICE));
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    /**
     * 打开GPS界面
     *
     * @param activity
     */
    public static void start2GPS(Activity activity) {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            activity.startActivity(intent);
        } catch (Exception ex) {
            // The Android SDK doc says that the location settings activity
            // may not be found. In that case show the general settings.

            // General settings activity
            intent.setAction(Settings.ACTION_SETTINGS);
            try {
                activity.startActivity(intent);
            } catch (Exception e) {
            }
        }
    }

    /**
     * 获取版本号
     */
    public static String getVerNum() {
        String ver = "";
        PackageManager manager = BaseApp.getAppContext().getPackageManager();
        try {
            PackageInfo info = manager.getPackageInfo(BaseApp.getAppContext().getPackageName(), 0);
            ver = info.versionName;
            return ver;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
        return ver;
    }

    /**
     * 调用点话拨号界面
     * @param phoneNumber
     */
    public static void dialPhone(String phoneNumber) {
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_DIAL, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        BaseApp.getAppContext().startActivity(intent);
    }

    /**
     * 直接打电话
     * @param phoneNumber
     */
    public static void callPhone(String phoneNumber) {
        Uri uri = Uri.parse("tel:" + phoneNumber);
        Intent intent = new Intent(Intent.ACTION_CALL, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(BaseApp.getAppContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        BaseApp.getAppContext().startActivity(intent);
    }

    /**
     * 检测apk是否存在
     * @param packageName
     * @return
     */
    public static boolean checkApkExist(String packageName) {
        if (packageName == null || "".equals(packageName)) {
            return false;
        }
        try {
            BaseApp.getAppContext().getPackageManager().getApplicationInfo(packageName, PackageManager.GET_UNINSTALLED_PACKAGES);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    /**
     * 复制Assets的Apk
     * @param fileName
     * @param path
     * @return
     */
    public static boolean copyApkFromAssets(String fileName, String path) {
        boolean copyIsFinish = false;
        try {
            InputStream is = BaseApp.getAppContext().getAssets().open(fileName);
            File file = new File(path);
            if (!file.createNewFile()) {
                throw new IOException("Unable to create file");
            }
            FileOutputStream fos = new FileOutputStream(file);
            byte[] temp = new byte[1024];
            int i = 0;
            while ((i = is.read(temp)) > 0) {
                fos.write(temp, 0, i);
            }
            fos.close();
            is.close();
            copyIsFinish = true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return copyIsFinish;
    }

    /**
     * 获取唯一标识
     * @return
     */
    public static String getMyUUID() {
        TelephonyManager tm = (TelephonyManager) BaseApp.getAppContext().getSystemService(BaseApp.getAppContext().TELEPHONY_SERVICE);
        String tmDevice, tmSerial, tmPhone, androidId;
        tmDevice = "" + tm.getDeviceId();
        tmSerial = "" + tm.getSimSerialNumber();
        androidId = "" + Settings.Secure.getString(BaseApp.getAppContext().getContentResolver(), Settings.Secure.ANDROID_ID);
        UUID deviceUuid = new UUID(androidId.hashCode(), ((long) tmDevice.hashCode() << 32) | tmSerial.hashCode());
        String uniqueId = deviceUuid.toString();
        return uniqueId;
    }

    /**
     * 获取所有通话记录
     * @return
     */
    public static List<CallLogBean> getAllCallLog() {
        if (ActivityCompat.checkSelfPermission(BaseApp.getAppContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        //所有
        Cursor cursor = BaseApp.getAppContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{
                        CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME,
                        CallLog.Calls.TYPE, CallLog.Calls.DATE,
                        CallLog.Calls.DURATION, CallLog.Calls._ID}, null, null,
                CallLog.Calls.DEFAULT_SORT_ORDER);
        List<CallLogBean> callLogBeans = new ArrayList<CallLogBean>();
        if (cursor.moveToFirst()) {
            do {
                CallLogBean callLogBean = new CallLogBean();
                callLogBean.setPhone(cursor.getString(0));  //NUMBER
                callLogBean.setName(cursor.getString(1));  //CACHED_NAME
                String type = "";
                switch (cursor.getInt(2)) { //TYPE
                    case CallLog.Calls.INCOMING_TYPE:
                        type = "呼入";
                        break;
                    case CallLog.Calls.OUTGOING_TYPE:
                        type = "呼出";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        type = "未接";
                        break;
                    default:
                        type = "挂断";
                        break;
                }
                callLogBean.setType(type);
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd hh:mm:ss");
                Date date = new Date(cursor.getLong(3) / 1000); //DATE
                String dates = dateFormat.format(date);
                callLogBean.setDate(dates);
                callLogBean.setDuration(cursor.getString(4)); //DURATION
                callLogBean.setId(String.valueOf(cursor.getInt(5))); //_ID

                callLogBeans.add(callLogBean);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return callLogBeans;
    }

    /**
     * 获取时间段内的通话记录
     * @param selectionArgs
     * @param selection
     * @return
     */
    public static List<CallLogBean> getCallLogByTimePeriod(String selection, String[] selectionArgs) {
//        String selection = " date >? and date <?";
//        String[] selectionArgs = new String[]{ String.valueOf(curreentRecordStartTime), String.valueOf(curreentRecordEndTime) };
        //时间段
        if (ActivityCompat.checkSelfPermission(BaseApp.getAppContext(), Manifest.permission.READ_CALL_LOG) != PackageManager.PERMISSION_GRANTED) {
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        Cursor cursor = BaseApp.getAppContext().getContentResolver().query(CallLog.Calls.CONTENT_URI, new String[]{
                        CallLog.Calls.NUMBER, CallLog.Calls.CACHED_NAME,
                        CallLog.Calls.TYPE, CallLog.Calls.DATE,
                        CallLog.Calls.DURATION, CallLog.Calls._ID}, selection, selectionArgs,
                CallLog.Calls.DEFAULT_SORT_ORDER);
        List<CallLogBean> callLogBeans = new ArrayList<CallLogBean>();
        if (cursor.moveToFirst()) {
            do {
                CallLogBean callLogBean = new CallLogBean();
                callLogBean.setPhone(cursor.getString(0));  //NUMBER
                callLogBean.setName(cursor.getString(1));  //CACHED_NAME
                String type = "";
                switch (cursor.getInt(2)) { //TYPE
                    case CallLog.Calls.INCOMING_TYPE:
                        type = "呼入";
                        break;
                    case CallLog.Calls.OUTGOING_TYPE:
                        type = "呼出";
                        break;
                    case CallLog.Calls.MISSED_TYPE:
                        type = "未接";
                        break;
                    default:
                        type = "挂断";
                        break;
                }
                callLogBean.setType(type);
                SimpleDateFormat dateFormat = new SimpleDateFormat(
                        "yyyy-MM-dd hh:mm:ss");
                Date date = new Date(cursor.getLong(3) / 1000); //DATE
                String dates = dateFormat.format(date);
                callLogBean.setDate(dates);
                callLogBean.setDuration(cursor.getString(4)); //DURATION
                callLogBean.setId(String.valueOf(cursor.getInt(5))); //_ID

                callLogBeans.add(callLogBean);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return callLogBeans;
    }

    /**
     * 获取App当前进程名
     * @param pID
     * @return
     */
    public static String getAppName(int pID) {
        String processName = null;
        ActivityManager am = (ActivityManager) BaseApp.getAppContext().getSystemService(BaseApp.getAppContext().ACTIVITY_SERVICE);
        List l = am.getRunningAppProcesses();
        Iterator i = l.iterator();
        PackageManager pm = BaseApp.getAppContext().getPackageManager();
        while (i.hasNext()) {
            ActivityManager.RunningAppProcessInfo info = (ActivityManager.RunningAppProcessInfo) (i.next());
            try {
                if (info.pid == pID) {
                    processName = info.processName;
                    return processName;
                }
            } catch (Exception e) {
                // Log.d("Process", "Error>> :"+ e.toString());
            }
        }
        return null;
    }

    /**
     * 清除TextLine缓存
     */
    public static void clearTextLineCache(){
        Field textLineCached = null;
        try {
            textLineCached = Class.forName("android.text.TextLine").getDeclaredField("sCached");
            textLineCached.setAccessible(true);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (textLineCached == null) return;
        Object cached = null;
        try {
            // Get reference to the TextLine sCached array.
            cached = textLineCached.get(null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (cached != null) {
            // Clear the array.
            for (int i = 0, size = Array.getLength(cached); i < size; i ++) {
                Array.set(cached, i, null);
            }
        }
    }

    /**
     * 判断某个界面是否在前台
     * @param className
     * @return
     */
    public static boolean isForeground(String className) {
        if (className.equals("")) {
            return false;
        }
        ActivityManager activityManager = (ActivityManager) BaseApp.getAppContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> runningTasks = activityManager.getRunningTasks(1);
        if (runningTasks != null && runningTasks.size() > 0) {
            ComponentName cpn = runningTasks.get(0).topActivity;
            if (cpn.getClassName().contains(className)) {
                return true;
            }
        }
        return false;
    }
}
