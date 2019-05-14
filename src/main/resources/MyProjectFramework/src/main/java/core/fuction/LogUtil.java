package $Package.core.fuction;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Log日志管理
 * Created by Vincent on 2019-05-10 11:33:28.
 */
public class LogUtil {

    private static final String logMark;
    private static final String logMark2;
    private static boolean isDebug;
    public static final String NETWORK_DATA_SEPARATOR = "&:&";

    private static final int maxLength;

    private static final int JSON_INDENT;

    static {
        logMark = "╔════════════════════════════════════";
        logMark2 = "╚════════════════════════════════════";
        maxLength = 4000;
        JSON_INDENT = 4;
    }

    /**
     * 设置开启调试模式
     *
     * @param mode
     */
    public static void setDebugMode(boolean mode) {
        isDebug = mode;
    }

    public static void i(Object object) {
        String[] info = getAutoJumpLogInfo();
        if (isDebug) {
            if (object != null) {
                String msg = String.valueOf(object);
                if (parseJson(msg) != null) {
                    printJson(msg, info);
                    return;
                }
                int index = 0;
                int length = msg.length();
                int countOfMessage = length / maxLength;
                String tempMsg;
                if (msg.contains(NETWORK_DATA_SEPARATOR)) {
                    tempMsg = msg.substring(msg.indexOf(NETWORK_DATA_SEPARATOR) + 3, msg.length());
                } else {
                    tempMsg = msg;
                }
                printHead(info[0]);
                tempMsg = parseJson(tempMsg);
                if (tempMsg != null) {
                    Log.i(info[0], "║ " + msg.substring(0, msg.indexOf(NETWORK_DATA_SEPARATOR)));
                    String[] json = tempMsg.split(System.getProperty("line.separator"));
                    for (String str : json) {
                        Log.i(info[0], "║ " + str);
                    }
                } else {
                    if (countOfMessage > 0) {
                        for (int i = 0; i < countOfMessage; i++) {
                            Log.i(info[0], "║ " + msg.substring(index, index + maxLength));
                            index += maxLength;
                        }
                    } else {
                        Log.i(info[0], "║ " + msg);
                    }
                }
                printEnd(info);
            } else {
                Log.i(info[0], "null");
            }
        }
    }

    public static void e(Object object) {
        String[] info = getAutoJumpLogInfo();
        if (isDebug) {
            if (object != null) {
                String msg = String.valueOf(object);
                if (parseJson(msg) != null) {
                    printJson(msg, info);
                    return;
                }
                int index = 0;
                int length = msg.length();
                int countOfMessage = length / maxLength;
                String tempMsg;
                if (msg.contains(NETWORK_DATA_SEPARATOR)) {
                    tempMsg = msg.substring(msg.indexOf(NETWORK_DATA_SEPARATOR) + 3, msg.length());
                } else {
                    tempMsg = msg;
                }
                printHead(info[0]);
                tempMsg = parseJson(tempMsg);
                if (tempMsg != null) {
                    Log.e(info[0], "║ " + msg.substring(0, msg.indexOf(NETWORK_DATA_SEPARATOR)));
                    String[] json = tempMsg.split(System.getProperty("line.separator"));
                    for (String str : json) {
                        Log.e(info[0], "║ " + str);
                    }
                } else {
                    if (countOfMessage > 0) {
                        for (int i = 0; i < countOfMessage; i++) {
                            Log.e(info[0], "║ " + msg.substring(index, index + maxLength));
                            index += maxLength;
                        }
                    } else {
                        Log.e(info[0], "║ " + msg);
                    }
                }
                printEnd(info);
            } else {
                Log.e(info[0], "null");
            }
        }
    }

    /**
     * 带来尾部
     * @param info
     */
    private static void printEnd(String[] info) {
        Log.i(info[0], "║ " + info[1]);
        Log.i(info[0], logMark2);
    }

    /**
     * 打印头部
     * @param tag
     */
    private static void printHead(String tag) {
        Log.i(tag, logMark);
        Log.i(tag, "║ " + DateUtil.second2TimeFormat("yyyy-MM-dd HH:mm:ss.sss",
                String.valueOf(System.currentTimeMillis() / 1000)));
    }

    /**
     * 打印json
     * @param object
     * @param info
     */
    public static void printJson(Object object, String[] info) {
        if (isDebug) {
            if (object != null) {
                String msg = String.valueOf(object);
                msg = parseJson(msg);
                String[] json = msg.split(System.getProperty("line.separator"));
                printHead(info[0]);
                for (String str : json) {
                    Log.i(info[0], "║ " + str);
                }
                printEnd(info);
            } else {
                Log.i(info[0], "null");
            }
        }
    }
    /**
     * 解析json
     *
     * @param msg
     */
    private static String parseJson(String msg) {
        String message = null;
        try {
            if (msg.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(msg);
                message = jsonObject.toString(JSON_INDENT);
            } else if (msg.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(msg);
                message = jsonArray.toString(JSON_INDENT);
            }
        } catch (JSONException e) {
        }
        return message;
    }


    /**
     * 获取打印信息所在方法名，行号等信息
     *
     * @return
     */
    private static String[] getAutoJumpLogInfo() {
        String[] info = new String[]{"", "", ""};
        StackTraceElement[] elements = Thread.currentThread().getStackTrace();
        if (elements.length < 5) {
            Log.e("MyLogger", "Stack is too shallow!!!");
            return info;
        } else {
            info[0] = elements[4].getClassName().substring(
                    elements[4].getClassName().lastIndexOf(".") + 1);
            info[1] = elements[4].getMethodName() + "()->" +
                    "(" + elements[4].getFileName() + ":" +
                    elements[4].getLineNumber() + ")";
            return info;
        }
    }

}

