package $Package.core.fuction;

import android.text.TextUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 数字校验工具类
 * Created by Vincent on 2017-08-16 23:56:33.
 */
public class RegexUtil {


	/**
	 * 车牌号码Pattern
	 */
	public static final Pattern PLATE_NUMBER_PATTERN = Pattern
			.compile("^[\u0391-\uFFE5]{1}[a-zA-Z0-9]{6}$");

	/**
	 * 证件号码Pattern
	 */
	public static final Pattern ID_CODE_PATTERN = Pattern
			.compile("^[a-zA-Z0-9]+$");

	/**
	 * 编码Pattern
	 */
	public static final Pattern CODE_PATTERN = Pattern
			.compile("^[a-zA-Z0-9]+$");

	/**
	 * 固定电话编码Pattern
	 */
	public static final Pattern PHONE_NUMBER_PATTERN = Pattern
			.compile("0\\d{2,3}-[0-9]+");

	/**
	 * 邮政编码Pattern
	 */
	public static final Pattern POST_CODE_PATTERN = Pattern.compile("\\d{6}");

	/**
	 * 面积Pattern
	 */
	public static final Pattern AREA_PATTERN = Pattern.compile("\\d*.?\\d*");

	/**
	 * 移动：134、135、136、137、138、139、150、151、157(TD)、158、159、187、188
	 * 联通：130、131、132、152、155、156、185、186 电信：133、153、180、189、（1349卫通）
	 * 总结起来就是第一位必定为1，第二位必定为3或5或8，其他位置的可以为0-9
	 */
	public static final Pattern MOBILE_NUMBER_PATTERN = Pattern
			.compile("[1][34578]\\d{9}");

	/**
	 * 银行帐号Pattern
	 */
	public static final Pattern ACCOUNT_NUMBER_PATTERN = Pattern
			.compile("\\d{16,21}");
	/**
	 * 邮箱Pattern
	 */
	public static final Pattern EMAIL_PATTERN = Pattern
			.compile("^([a-zA-Z0-9_\\-\\.]+)@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.)|(([a-zA-Z0-9\\-]+\\.)+))([a-zA-Z]{2,4}|[0-9]{1,3})(\\]?)$");

	/**
	 * 数字Pattern
	 */
	public static final Pattern NUMBER_PATTERN = Pattern
			.compile("[0-9]*");

	/**
	 * 车牌号码是否正确
	 *
	 * @param s
	 * @return
	 */
	public static boolean isPlateNumber(String s) {
		Matcher m = PLATE_NUMBER_PATTERN.matcher(s);
		return m.matches();
	}

	/**
	 * 证件号码是否正确
	 *
	 * @param s
	 * @return
	 */
	public static boolean isIDCode(String s) {
		Matcher m = ID_CODE_PATTERN.matcher(s);
		return m.matches();
	}

	/**
	 * 编码是否正确
	 *
	 * @param s
	 * @return
	 */
	public static boolean isCode(String s) {
		Matcher m = CODE_PATTERN.matcher(s);
		return m.matches();
	}

	/**
	 * 固话编码是否正确
	 *
	 * @param s
	 * @return
	 */
	public static boolean isPhoneNumber(String s) {
		Matcher m = PHONE_NUMBER_PATTERN.matcher(s);
		return m.matches();
	}

	/**
	 * 邮政编码是否正确
	 *
	 * @param s
	 * @return
	 */
	public static boolean isPostCode(String s) {
		Matcher m = POST_CODE_PATTERN.matcher(s);
		return m.matches();
	}

	/**
	 * 面积是否正确
	 *
	 * @param s
	 * @return
	 */
	public static boolean isArea(String s) {
		Matcher m = AREA_PATTERN.matcher(s);
		return m.matches();
	}

	/**
	 * 手机号码否正确
	 *
	 * @param phone
	 * @return
	 */
	public static boolean isMobileNumber(String phone) {
		if (TextUtils.isEmpty(phone))
			return false;
		else {
			// "[1]"代表第1位为数字1，"[358]"代表第二位可以为3、5、8中的一个，"\\d{9}"代表后面是可以是0～9的数字，有9位。
			Matcher matcher = MOBILE_NUMBER_PATTERN.matcher(phone);
			return matcher.matches();
		}
	}

	/**
	 * 银行账号否正确
	 *
	 * @param s
	 * @return
	 */
	public static boolean isAccountNumber(String s) {
		Matcher m = ACCOUNT_NUMBER_PATTERN.matcher(s);
		return m.matches();
	}

	/**
	 * 是否是数字
	 *
	 * @param num
	 * @return
	 */
	public static boolean isNumber(String num) {
		Matcher m = NUMBER_PATTERN.matcher(num);
		return m.matches();
	}

	/**
	 * 是否是邮箱帐号
	 *
	 * @param mail
	 * @return
	 */
	public static boolean isEmail(String mail) {
		Matcher m = EMAIL_PATTERN.matcher(mail);
		return m.matches();
	}

	/**
	 * 比较真实完整的判断身份证号码的工具
	 *
	 * @param IdCard 用户输入的身份证号码
	 * @return [true符合规范, false不符合规范]
	 */
	public static boolean isRealIDCard(String IdCard) {
		if (IdCard != null) {
			int correct = new IdCardUtil(IdCard).isCorrect();
			if (0 == correct) {// 符合规范
				return true;
			}
		}
		return false;
	}

}

