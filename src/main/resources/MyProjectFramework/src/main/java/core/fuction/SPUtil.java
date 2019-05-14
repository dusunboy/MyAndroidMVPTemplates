package $Package.core.fuction;

import android.content.Context;
import android.content.SharedPreferences;

import $Package.core.base.BaseApp;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * SharedPreferences存储工具类
 * Created by Vincent on 2019-05-10 11:33:28
 */
public class SPUtil {
    /**
     * 保存在手机里面的文件名
     */
    public static final String FILE_NAME = "sp_data";
 
    /**
     * 保存数据的方法，我们需要拿到保存数据的具体类型，然后根据类型调用不同的保存方法
     * 
     * @param key
     * @param object
     */
    public static void put(String key, Object object)
    {

        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        if (object != null) {
            if (object instanceof String)
            {
                editor.putString(key, (String) object);
            } else if (object instanceof Integer)
            {
                editor.putInt(key, (Integer) object);
            } else if (object instanceof Boolean)
            {
                editor.putBoolean(key, (Boolean) object);
            } else if (object instanceof Float)
            {
                editor.putFloat(key, (Float) object);
            } else if (object instanceof Long)
            {
                editor.putLong(key, (Long) object);
            } else
            {
                editor.putString(key, object.toString());
            }
        } else {
            editor.putString(key, "");
        }

        SharedPreferencesCompat.apply(editor);
    }
 
    /**
     * 得到保存数据的方法，我们根据默认值得到保存的数据的具体类型，然后调用相对于的方法获取值
     * 
     * @param key
     * @param defaultObject
     * @return
     */
    public static Object get(String key, Object defaultObject)
    {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
 
        if (defaultObject instanceof String)
        {
            return sp.getString(key, (String) defaultObject);
        } else if (defaultObject instanceof Integer)
        {
            return sp.getInt(key, (Integer) defaultObject);
        } else if (defaultObject instanceof Boolean)
        {
            return sp.getBoolean(key, (Boolean) defaultObject);
        } else if (defaultObject instanceof Float)
        {
            return sp.getFloat(key, (Float) defaultObject);
        } else if (defaultObject instanceof Long)
        {
            return sp.getLong(key, (Long) defaultObject);
        }
 
        return null;
    }

    /**
     * 获取字符型存储的数据
     * @param key
     * @return
     */
    public static String getString(String key) {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(key, "");
    }

    /**
     * 获取字符型存储的数据
     * @param key
     * @param defaultString
     * @return
     */
    public static String getString(String key, String defaultString) {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getString(key, defaultString);
    }

    /**
     * 获取整型存储的数据
     * @param key
     * @return
     */
    public static int getInt(String key) {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getInt(key, 0);
    }

    /**
     * 获取整型存储的数据
     * @param key
     * @param defaultInt
     * @return
     */
    public static int getInt(String key, int defaultInt) {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getInt(key, defaultInt);
    }

    /**
     * 获取布尔型存储的数据
     * @param key
     * @return
     */
    public static boolean getBoolean(String key) {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, false);
    }

    /**
     * 获取布尔型存储的数据
     * @param key
     * @param defaultBoolean
     * @return
     */
    public static boolean getBoolean(String key, boolean defaultBoolean) {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getBoolean(key, defaultBoolean);
    }

    /**
     * 获取浮点型存储的数据
     * @param key
     * @return
     */
    public static float getFloat(String key) {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getFloat(key, 0);
    }

    /**
     * 获取浮点型存储的数据
     * @param key
     * @param defaultFloat
     * @return
     */
    public static float getFloat(String key, float defaultFloat) {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getFloat(key, defaultFloat);
    }

    /**
     * 获取长整型型存储的数据
     * @param key
     * @return
     */
    public static long getLong(String key) {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getLong(key, 0);
    }

    /**
     * 获取长整型型存储的数据
     * @param key
     * @param defaultLong
     * @return
     */
    public static long getLong(String key, long defaultLong) {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getLong(key, defaultLong);
    }


    /**
     * 移除某个key值已经对应的值
     * @param key
     */
    public static void remove(String key)
    {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(key);
        SharedPreferencesCompat.apply(editor);
    }
 
    /**
     * 清除所有数据
     */
    public static void clear()
    {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        SharedPreferencesCompat.apply(editor);
    }
 
    /**
     * 查询某个key是否已经存在
     * @param key
     * @return
     */
    public static boolean contains(String key)
    {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.contains(key);
    }
 
    /**
     * 返回所有的键值对
     * 
     * @return
     * @return
     */
    public static Map<String, ?> getAll()
    {
        SharedPreferences sp = BaseApp.getAppContext().getSharedPreferences(FILE_NAME,
                Context.MODE_PRIVATE);
        return sp.getAll();
    }
 
    /**
     * 创建一个解决SharedPreferencesCompat.apply方法的一个兼容类
     * 
     * @author zhy
     * 
     */
    private static class SharedPreferencesCompat
    {
        private static final Method sApplyMethod = findApplyMethod();
 
        /**
         * 反射查找apply的方法
         * 
         * @return
         */
        private static Method findApplyMethod()
        {
            try
            {
                Class clz = SharedPreferences.Editor.class;
				return clz.getMethod("apply");
            } catch (NoSuchMethodException e)
            {
            }
 
            return null;
        }
 
        /**
         * 如果找到则使用apply执行，否则使用commit
         * 
         * @param editor
         */
        public static void apply(SharedPreferences.Editor editor)
        {
            try
            {
                if (sApplyMethod != null)
                {
                    sApplyMethod.invoke(editor);
                    return;
                }
            } catch (IllegalArgumentException e)
            {
            } catch (IllegalAccessException e)
            {
            } catch (InvocationTargetException e)
            {
            }
            editor.commit();
        }
    }
 
}
