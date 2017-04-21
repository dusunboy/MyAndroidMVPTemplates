package $Package.core.fuction;

/**
 * 字符串工具类
 * Created by Vincent on $Time.
 */
public class StringUtil {
	/**
	 * 处理空字符串
	 * 
	 * @param str
	 * @return String
	 */
	public static String doEmpty(String str) {
		return doEmpty(str, "");
	}

	/**
	 * 处理空字符串
	 * 
	 * @param str
	 * @param defaultValue
	 * @return String
	 */
	public static String doEmpty(String str, String defaultValue) {
		if (str == null || str.equalsIgnoreCase("null")
				|| str.trim().equals("") || str.trim().equals("－请选择－")) {
			str = defaultValue;
		} else if (str.startsWith("null")) {
			str = str.substring(4, str.length());
		}
		return str.trim();
	}

	/**
	 * 请选择
	 */
	final static String PLEASE_SELECT = "请选择...";

	public static boolean notEmpty(Object o) {
		return o != null && !"".equals(o.toString().trim())
				&& !"null".equalsIgnoreCase(o.toString().trim())
				&& !"undefined".equalsIgnoreCase(o.toString().trim())
				&& !PLEASE_SELECT.equals(o.toString().trim());
	}

	public static boolean empty(Object o) {
		return o == null || "".equals(o.toString().trim())
				|| "null".equalsIgnoreCase(o.toString().trim())
				|| "undefined".equalsIgnoreCase(o.toString().trim())
				|| PLEASE_SELECT.equals(o.toString().trim());
	}

	public static boolean num(Object o) {
		int n = 0;
		try {
			n = Integer.parseInt(o.toString().trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (n > 0) {
			return true;
		} else {
			return false;
		}
	}

	public static boolean decimal(Object o) {
		double n = 0;
		try {
			n = Double.parseDouble(o.toString().trim());
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		if (n > 0.0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 过滤手机号码
	 * @param phone_num
	 * @return
	 */
	public static String filterPhoneNum(String phone_num) {
		return replaceString(phone_num, "****", 3);
	}
	
	/**
	 * 替换指定位置的字符串
	 * @param str
	 * @param rstr
	 * @param startIndex
	 * @return
	 */
	public static String replaceString(String str, String rstr, int startIndex) {
		if (startIndex > str.length() - 1) {
			return str;
		}
		String preStr = str.substring(0, startIndex);
		if (startIndex + rstr.length() < str.length() - 1) {
			String subStr = str.substring(startIndex + rstr.length());
			return preStr + rstr + subStr;
		} else {
			return str;
		}
	}


    /**
     * Returns true if the string is null or 0-length.
     *
     * @param str
     *            the string to be examined
     * @return true if str is null or zero length
     */
    public static boolean isEmpty(String str) {
        if (str == null) {
            return true;
        }
        str = str.trim();
        return str.length() == 0 || str.equals("null");
    }
}