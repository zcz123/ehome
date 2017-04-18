/**
 * Project Name:  FamilyRoute
 * File Name:     BaseActivity.java
 * Package Name:  com.wulian.familyroute.view.base
 * Date:          2014-9-9
 * Copyright (c)  2014, wulian All Rights Reserved.
 */
package com.wulian.icam.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;

import com.wulian.icam.R;

/**
 * 
 * @ClassName: InputChecker
 * @Function: 输入检查
 * @date: 2014-9-9
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class InputChecker {
	/**
	 * validation numeric
	 * 
	 * @Title: isNumeric
	 * @param str
	 * @return
	 */
	public static boolean isNumeric(String str) {
		return isMatcher("[0-9]+", str);
	}

	/**
	 * validation mobile
	 * 
	 * @Title: checkMobile
	 * @param mobile
	 * @return
	 */
	public static boolean isMobile(String str) {
		// String regex = "^1(3[0-9]|5[012356789]|8[0789])\\d{8}$";
		return isMatcher("^1\\d{10}$", str);
	}

	/**
	 * validation email
	 * 
	 * @Title: checkEmail
	 * @param mail
	 * @return
	 */
	public static boolean isEmail(String str) {
		return isMatcher(
				"^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$",
				str);
	}

	/**
	 * validation is null
	 * 
	 * @Title: isNull
	 * @param str
	 * @return ture is null
	 */
	public static boolean isEmpty(String str) {
		if (str == null || "".equals(str.trim())) {
			return true;
		}
		return false;
	}

	/**
	 * 
	 * @Function 正则校验
	 * @author Wangjj
	 * @date 2014年11月12日
	 * @param regex
	 *            正则表达式
	 * @param str
	 *            待验证字符串
	 * @return
	 */
	private static boolean isMatcher(String regex, String str) {
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(str);
		return m.find();
	}

	// 1、长度为6-18
	// 2、密码不允许是连续（包含正序和倒序）的字母或数字 abcd dcba 1234 4321
	// 3、不允许是相同的字母或数字 111 aaa
	// 不允许完全等同于帐号 UI处判断
	// 不允许完全等同于原密码 UI处判断

	/**
	 * @Function 密码限制
	 * @author Wangjj
	 * @date 2014年11月12日
	 * @param str
	 *            待测试字符串
	 * @param context
	 *            用于国际化的上下文
	 * @return
	 */

	public static CheckResult isPassword(String str, Context context) {
		if (!isMatcher("^.{6,18}$", str)) {

			return new CheckResult(false,
					context.getString(R.string.common_regex_pwd_range));
		}
		if (isAllEqual(str)) {

			return new CheckResult(false,
					context.getString(R.string.common_regex_pwd_all_equal));
		}
		if (isContiStr(str)) {// isContiStr已经包含了isAllEqual的功能，isAllEqual放上面，否则在isContiStr之后判断永远为false，而且isAllEqual放上面提示更友好

			return new CheckResult(false,
					context.getString(R.string.common_regex_pwd_conti));
		}

		return new CheckResult(true);
	}

	// 3-15位 字母开头、3-15位、字母或数字结尾
	public static boolean isUserName(String str) {
		return isMatcher("^[a-zA-Z]{1}[\\w\\-]{1,13}[a-zA-Z0-9]{1}$", str);
	}

	public static boolean isUserID(String str) {
		return isMatcher("^\\d{5,10}$", str);
	}

	/**
	 * @Function 全等字符或数字 如aaa 111
	 * @author Wangjj
	 * @date 2014年11月12日
	 * @param str
	 * @return
	 */
	private static boolean isAllEqual(String str) {
		return isMatcher("^(\\w)(\\1)+$", str);
	}

	/**
	 * 
	 * @Function 判断连续的字母或数字，正序或倒序 abcd(1) dcba(-1) 1234(1) 4321(-1) 或者相同的字母 aaa
	 *           bbb也算,标准差为0
	 * @author Wangjj
	 * @date 2014年11月12日
	 * @param str
	 * @return
	 */
	private static boolean isContiStr(String str) {
		if (isMatcher("[a-zA-Z0-9]+", str)) {
			int len = str.length();
			int[] paddings = new int[len - 1];// 字符间隔
			char preChar = str.charAt(0);
			for (int i = 1; i < len; i++) {
				paddings[i - 1] = str.charAt(i) - preChar;
				preChar = str.charAt(i);
			}
			// 排除aaa 111 标准差为0的情况
			// if (paddings[0] != 1 && paddings[0] != -1) {
			// return false;
			// }
			for (int j = 1; j < paddings.length; j++) {

				if (paddings[0] != paddings[j]) {
					return false;
				}
			}
			return true;
		}

		return false;
	}

	/**
	 * 
	 * @Function 添加分隔符
	 * @author Wangjj
	 * @date 2014年11月12日
	 * @return abc=>a,b,c
	 */
	private static String addSplitPoint(String str) {
		int len = str.length();
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			sb.append(str.charAt(i)).append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	/**
	 * @Function: 验证结果
	 * @date: 2014年11月12日
	 * @author Wangjj
	 */
	public static class CheckResult {
		boolean isPass;
		String desc;

		public CheckResult(boolean isPass, String desc) {
			this.isPass = isPass;
			this.desc = desc;
		}

		public CheckResult(boolean isPass) {
			this.isPass = isPass;
		}

		public boolean isPass() {
			return isPass;
		}

		public void setPass(boolean isPass) {
			this.isPass = isPass;
		}

		public String getDesc() {
			return desc;
		}

		public void setDesc(String desc) {
			this.desc = desc;
		}

	}
}
