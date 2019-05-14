package $Package.core.fuction;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import $Package.core.base.BaseApp;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

/**
 * 网络工具类
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class NetUtil {
    private NetUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    /**
     * 判断网络是否连接
     *
     * @param context
     * @return
     */
    public static boolean isConnected(Context context) {

        ConnectivityManager connectivity = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != connectivity) {

            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (null != info && info.isConnected()) {
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 判断是否是wifi连接
     */
    public static boolean isWifi(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm == null)
            return false;
        return cm.getActiveNetworkInfo().getType() == ConnectivityManager.TYPE_WIFI;

    }

    /**
     * 打开网络设置界面
     */
    public static void openSetting(Activity activity) {
        Intent intent = new Intent("/");
        ComponentName cm = new ComponentName("com.android.settings",
                "com.android.settings.WirelessSettings");
        intent.setComponent(cm);
        intent.setAction("android.intent.action.VIEW");
        activity.startActivityForResult(intent, 0);
    }

    /**
     * ping IP是否通
     *
     * @param ip
     * @return
     */
    public static boolean pingHost(String ip) {
        boolean result = false;
        try {
            Process p = Runtime.getRuntime().exec("ping -c 1 -w 100 " + ip);
            int status = p.waitFor();
            if (status == 0) {
                result = true;
            } else {
                result = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * 获取IP地址
     * @return
     */
    public static String getIPAddress() {
        try {
            ConnectivityManager connMgr = (ConnectivityManager) BaseApp.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            if (null != connMgr) {
                NetworkInfo info = connMgr.getActiveNetworkInfo();
                if ((null != info) && (info.isAvailable())) {
                    if (1 == info.getType()) {
                        return getIPAddressOnWIFI(BaseApp.getAppContext());
                    }
                    return getIPAddressOnMobile();
                }
            }
        } catch (Throwable t) {
            LogUtil.e("get IP failed(" + t.getClass().getSimpleName() + "): " + t
                    .getMessage());
        }
        return "";
    }

    /**
     * 获取当GPRS下的IP地址
     * @return
     * @throws SocketException
     */
    private static String getIPAddressOnMobile()
            throws SocketException {
        Enumeration<NetworkInterface> elements = NetworkInterface.getNetworkInterfaces();
        while (elements.hasMoreElements()) {
            NetworkInterface element = (NetworkInterface) elements.nextElement();
            Enumeration<InetAddress> addresses = element.getInetAddresses();
            if (null != addresses) {
                while (addresses.hasMoreElements()) {
                    InetAddress addr = (InetAddress) addresses.nextElement();
                    if ((null != addr) && (!addr.isLoopbackAddress()) && ((addr instanceof Inet4Address))) {
                        return addr.getHostAddress();
                    }
                }
            }
        }
        return "";
    }

    /**
     * 获取当wifi下的IP地址
     * @param context
     * @return
     */
    private static String getIPAddressOnWIFI(Context context) {
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        if (wifiManager == null) {
            return "";
        } else {
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            return ip2s(wifiInfo.getIpAddress());
        }
    }

    private static String ip2s(int i) {
        return (i & 0xFF) + "." + (i >> 8 & 0xFF) + "." + (i >> 16 & 0xFF) + "." + (i >> 24 & 0xFF);
    }


}
