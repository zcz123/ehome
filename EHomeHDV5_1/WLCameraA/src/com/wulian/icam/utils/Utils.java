/**
 * Project Name:  FamilyRoute
 * File Name:     Utils.java
 * Package Name:  com.wulian.familyroute.utils
 * @Date:         2014-9-9
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;
import com.wulian.icam.ICamGlobal;
import com.wulian.icam.R;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.model.UserInfo;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.icam.view.widget.Lock9View;
import com.wulian.icam.view.widget.Lock9View.CallBack;

/**
 * @ClassName: Utils
 * @Function: 常用辅助类
 * @Date: 2014-9-9
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class Utils {
	public static final String KEY_ALGORITHM = "AES";
	public static final String CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";
	private static boolean isSyso = ICamGlobal.CURRENT_VERSION == ICamGlobal.DEVING_VERSION;// 是否显示输出信息

	// public static final String CIPHER_ALGORITHM =
	// "AES/ECB/ZeroBytePadding";//解决4.3以上的bug？

	/**
	 * 如果键盘没有收回 自动关闭键盘
	 * 
	 * @param activity
	 *            Activity
	 * @param v
	 *            控件View
	 */
	public static void autoCloseKeyboard(Activity activity, View v) {
		/** 收起键盘 */
		View view = activity.getWindow().peekDecorView();
		if (view != null && view.getWindowToken() != null) {
			InputMethodManager imm = (InputMethodManager) activity
					.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
		}

	}

	/**
	 * @Function 获取屏幕尺寸
	 * @author Wangjj
	 * @date 2014年10月5日
	 * @param context
	 * @return
	 */
	public static DisplayMetrics getDeviceSize(Context context) {
		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager windowManager = (WindowManager) context
				.getSystemService(Context.WINDOW_SERVICE);
		windowManager.getDefaultDisplay().getMetrics(metrics);
		return metrics;
	}

	/**
	 * 
	 * @Function 显示密码框文本
	 * @author Wangjj
	 * @date 2014年10月29日
	 * @param et
	 *            编辑的文本框
	 */
	public static void showEditTextPwd(EditText et) {
		et.setTransformationMethod(HideReturnsTransformationMethod
				.getInstance());
	}

	/**
	 * 
	 * @Function 隐藏密码框文本
	 * @author Wangjj
	 * @date 2014年10月29日
	 * @param et
	 *            编辑的文本框
	 */
	public static void hideEditTextPwd(EditText et) {
		et.setTransformationMethod(PasswordTransformationMethod.getInstance());
	}

	/**
	 * @Function 加密数据
	 * @author Wangjj
	 * @date 2014年10月9日
	 * @param originStr
	 *            原始字符串
	 * @param pwd
	 *            加密密码
	 * @return base64编码的加密数据
	 */
	public static String encrypt(String originStr, String pwd) {

		try {
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);

			SecretKey secretKey = generateKey(pwd);

			cipher.init(Cipher.ENCRYPT_MODE, secretKey);
			// string->utf8 bytes->final->base64
			return Base64
					.encodeToString(
							cipher.doFinal(originStr.getBytes("UTF-8")),
							Base64.DEFAULT);
		} catch (Exception e) {

			e.printStackTrace();

		}
		return "";
	}

	/**
	 * 
	 * @Function 解密数据
	 * @author Wangjj
	 * @date 2014年10月9日
	 * @param encryptedStr
	 *            已经使用base64编码的加密数据
	 * @param pwd
	 *            解密密码
	 * @return
	 */
	public static String decrypt(String encryptedStr, String pwd) {
		try {
			Cipher cipher = Cipher.getInstance(KEY_ALGORITHM);
			SecretKey secretKey = generateKey(pwd);
			cipher.init(Cipher.DECRYPT_MODE, secretKey);
			// base64->final->utf8 bytes->string
			// DONE: 华为手机4.2.2 BadPaddingException
			// 在4.2.1及之前，都用的是好好的，但一到4.2.2及其以后的版本后就抛出Bad-Padding Exception.这个异常。

			return new String(cipher.doFinal(Base64.decode(encryptedStr,
					Base64.DEFAULT)), "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
		}

		return "";
	}

	/**
	 * 
	 * @Function 通过KeyGenerator 产生key
	 * @author Wangjj
	 * @date 2014年10月10日
	 * @param pwd
	 * @return
	 * @throws NoSuchAlgorithmException
	 * @throws NoSuchProviderException
	 */
	private static SecretKey generateKey(String pwd)
			throws NoSuchAlgorithmException, NoSuchProviderException {
		KeyGenerator kgen = KeyGenerator.getInstance(KEY_ALGORITHM);

		SecureRandom sr = SecureRandom.getInstance("SHA1PRNG", "Crypto");// 解决4.2.2及之后的bug
		sr.setSeed(pwd.getBytes());

		// SecureRandom sr =new SecureRandom(pwd.getBytes())//4.2.1及之前可以
		kgen.init(128, sr);
		SecretKey secretKey = kgen.generateKey();
		return secretKey;
		// return new SecretKeySpec(pwd.getBytes(), KEY_ALGORITHM);
	}

	/**
	 * 
	 * @Function 通过反射获取Bean对象实体 缺陷：只能反射单个实体 不能反射集合 可以换用Gson库来解析
	 * @author Wangjj
	 * @date 2014年11月3日
	 * @param clazz
	 * @param jsonStr
	 * @return
	 */
	public static <T> T parseBean(Class<T> clazz, String jsonStr) {
		Field[] fields = clazz.getDeclaredFields();
		T object = null;

		try {
			// JavaBean默认拥有一个无参构造函数，如果有其他有参构造函数，必须补全无参构造函数
			object = clazz.newInstance();
		} catch (Exception e2) {
			e2.printStackTrace();
			return null;
		}

		if (object != null) {
			JSONObject jsonObject;
			try {
				jsonObject = new JSONObject(jsonStr);
			} catch (JSONException e1) {
				e1.printStackTrace();
				return null;

			}
			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				String fName = field.getName();
				String fUpName = upFirstCharacter(field.getName());
				try {
					if (field.getType() == int.class) {

						Method method = clazz.getMethod("set" + fUpName,
								int.class);
						method.invoke(object, jsonObject.optInt(fName));

					} else if (field.getType() == String.class) {
						Method method = clazz.getMethod("set" + fUpName,
								String.class);
						method.invoke(object, jsonObject.optString(fName));

					} else if (field.getType() == Boolean.class) {
						Method method = clazz.getMethod("set" + fUpName,
								String.class);
						method.invoke(object, jsonObject.optBoolean(fName));

					}
				} catch (Exception e) {
					Utils.sysoInfo("没有该方法:set" + fUpName);
					e.printStackTrace();
				}
			}
		}
		return object;
	}

	public static <T> T parseBean(Class<T> clazz, JSONObject jsonObject) {
		Field[] fields = clazz.getDeclaredFields();
		T object = null;

		try {
			// JavaBean默认拥有一个无参构造函数，如果有其他有参构造函数，必须补全无参构造函数
			object = clazz.newInstance();
		} catch (Exception e2) {
			e2.printStackTrace();
			return null;
		}

		if (object != null) {

			for (int i = 0; i < fields.length; i++) {
				Field field = fields[i];
				String fName = field.getName();
				String fUpName = upFirstCharacter(field.getName());
				try {
					if (field.getType() == int.class) {

						Method method = clazz.getMethod("set" + fUpName,
								int.class);
						method.invoke(object, jsonObject.optInt(fName));

					} else if (field.getType() == String.class) {
						Method method = clazz.getMethod("set" + fUpName,
								String.class);
						method.invoke(object, jsonObject.optString(fName));

					} else if (field.getType() == Boolean.class) {
						Method method = clazz.getMethod("set" + fUpName,
								String.class);
						method.invoke(object, jsonObject.optBoolean(fName));

					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		return object;
	}

	/**
	 * 
	 * @Function 保存全局UserInfo
	 * @author Wangjj
	 * @date 2014年11月3日
	 * @param activity
	 * @param json
	 */
	public static void saveUserInfo(String json) {
		UserInfo userInfo = Utils.parseBean(UserInfo.class, json);
		ICamGlobal.getInstance().setUserinfo(userInfo);
		// ((ICamApplication) activity.getApplication())
	}

	/**
	 * @Function 将3转成03
	 * @author Wangjj
	 * @date 2015年5月28日
	 * @param in
	 * @return
	 */

	public static String fillZeroBeforeSingleNum(String in) {
		if (TextUtils.isEmpty(in)) {
			return "";
		}
		return in.length() == 1 ? "0" + in : in;
	}

	/**
	 * @Function 将数组里的所有3转成03
	 * @author Wangjj
	 * @date 2015年5月28日
	 * @param ins
	 */

	public static void formatSingleNum(String[] ins) {
		for (int i = 0; i < ins.length; i++) {
			ins[i] = fillZeroBeforeSingleNum(ins[i]);
		}
	}

	/**
	 * @Function 取消注销标记
	 * @author Wangjj
	 * @date 2014年11月5日
	 * @param context
	 */
	public static void cancleLoginOut(Context context) {

		SharedPreferences sp = context.getSharedPreferences(
				APPConfig.SP_CONFIG, 0);
		Editor editor = sp.edit();
		editor.putBoolean(APPConfig.IS_LOGIN_OUT, false);// 取消注销标记
		editor.commit();
	}

	/**
	 * @Function 首字母大写
	 * @author Wangjj
	 * @date 2014年11月3日
	 * @return
	 */
	public static String upFirstCharacter(String str) {
		return (str.charAt(0) + "").toUpperCase(Locale.US) + str.substring(1);
	}

	/**
	 * @Function 摇动控件，提示用户
	 * @author Wangjj
	 * @date 2014年11月4日
	 * @param context
	 * @param view
	 */
	public static void shake(Context context, View view) {
		Animation shake = AnimationUtils.loadAnimation(context, R.anim.shake);
		view.startAnimation(shake);
	}

	/**
	 * 
	 * @Function 显示键盘
	 * @author Wangjj
	 * @date 2014年11月4日
	 * @param activity
	 * @param view
	 */
	public static void showIme(Activity activity, View view) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
	}

	/**
	 * 
	 * @Function 获取当前客户端版本信息
	 * @author Wangjj
	 * @date 2014年11月6日
	 * @param mContext
	 * @return
	 */
	public static PackageInfo getPackageInfo(Context mContext) {
		PackageInfo info = null;
		try {
			info = mContext.getPackageManager().getPackageInfo(
					mContext.getPackageName(), 0);

		} catch (NameNotFoundException e) {
			e.printStackTrace(System.err);
			info = new PackageInfo();// 即使遇到异常也要返回一个实体对象，不要返回null去让调用者做null判断？
		}
		return info;
	}

	/**
	 * 
	 * @Function 显示错误消息
	 * @author Wangjj
	 * @date 2014年11月8日
	 * @param context
	 * @param json
	 * 
	 */
	public static void showErrorMsg(Context context, String json) {

		JSONObject jsonObject;
		try {// 服务器返回的失败json信息
			jsonObject = new JSONObject(json);
			CustomToast.show(context, jsonObject.optString("error_msg"));
		} catch (JSONException e) {
			e.printStackTrace();
			CustomToast.show(context, json);
		}
	}

	/**
	 * 
	 * @Function 获取请求url的参数
	 * @author Wangjj
	 * @date 2014年11月25日
	 * @param url
	 * @return
	 */
	public static HashMap<String, String> getRequestParams(String url) {
		Pattern p = Pattern.compile("(\\w+)=(\\w+)");
		Matcher matcher = p.matcher(url);
		HashMap<String, String> params = new HashMap<String, String>();
		while (matcher.find()) {
			params.put(matcher.group(1), matcher.group(2));
		}
		return params;

	}

	/**
	 * @Function 从xml字符中提取字段值
	 * @author Wangjj
	 * @date 2014年12月23日
	 * @param xmlString
	 *            xml字符串
	 * @param param
	 *            要提取的字段,格式只能是<name>param</name>
	 * @return
	 */

	public static String getParamFromXml(String xmlString, String param) {
		// \\w+不能匹配local_mac中的':',如00:11:22
		Pattern p = Pattern.compile("<" + param + ">(.+)</" + param + ">");
		Matcher matcher = p.matcher(xmlString);
		if (matcher.find())
			return matcher.group(1).trim();
		return "";
	}

	/* <area id="0">105,41,164,155</area> */
	/**
	 * @Function 解析移动侦测区域
	 * @author Wangjj
	 * @date 2014年12月26日
	 * @param xmlString
	 *            xml字符串
	 * @param maxCount
	 *            监测个数上限
	 * @return
	 */
	public static String[] getMotionArea(String xmlString, int maxCount) {
		ArrayList<String> results = new ArrayList<String>();
		Pattern p = null;
		Matcher matcher = null;
		for (int i = 0; i < maxCount; i++) {
			p = Pattern.compile("<area id=\"" + i + "\">(.+)</area>");
			matcher = p.matcher(xmlString);
			if (matcher.find()) {
				results.add(matcher.group(1).trim());
			}
		}
		String[] areas = new String[results.size()];
		for (int i = 0; i < areas.length; i++) {
			areas[i] = results.get(i);
		}
		return areas;
	}

	/**
	 * 
	 * @Function 将20位的deviceId 后12位提取成mac地址
	 * @author Wangjj
	 * @date 2014年11月26日
	 * @param deviceId
	 * @return
	 */
	public static String deviceIdToMac(String deviceId) {
		if (deviceId.length() != 20) {
			return "";
		}
		String macStr = deviceId.substring(8, 20);
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < 12; i++) {
			sb.append(macStr.charAt(i));
			if (i % 2 == 1) {
				sb.append(":");
			}
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}

	public static void hideKeyboard(Activity context) {
		InputMethodManager imm = (InputMethodManager) context
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm.isActive()) {
			// imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			imm.hideSoftInputFromWindow(context.getCurrentFocus()
					.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}

	/**
	 * 
	 * @Function 隐藏键盘
	 * @author Wangjj
	 * @date 2014年11月4日
	 * @param activity
	 * @param view
	 */
	public static void hideIme(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(
				activity.getCurrentFocus().getWindowToken(),
				InputMethodManager.HIDE_NOT_ALWAYS); // 强制隐藏键盘
	}

	public static String converTime(Context context, long timestamp) {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		// sdf.parse(date).getTime()/1000;
		// long timestamp = date.getTime();
		Resources rs = context.getResources();
		long currentSeconds = System.currentTimeMillis();
		long timeGap = (currentSeconds - timestamp) / 1000;// 与现在时间相差秒数
		// Utils.sysoInfo("timeGap:" + timeGap);
		String timeStr = "";
		if (timeGap > 24 * 60 * 60 * 30) {// 1月以上
			timeStr = timeGap / (24 * 60 * 60 * 30)
					+ rs.getString(R.string.common_month_ago);
		} else if (timeGap > 24 * 60 * 60) {// 1天以上
			timeStr = timeGap / (24 * 60 * 60)
					+ rs.getString(R.string.common_day_ago);

		} else if (timeGap > 60 * 60) {// 1小时-24小时
			timeStr = timeGap / (60 * 60)
					+ rs.getString(R.string.common_hour_ago);
		} else if (timeGap > 60) {// 1分钟-59分钟
			timeStr = timeGap / 60 + rs.getString(R.string.common_min_ago);
		} else {// 1秒钟-59秒钟
			timeStr = rs.getString(R.string.common_just);
		}
		return timeStr;
	}

	public static void sysoInfo(String info) {
		if (isSyso) {
			System.out.println(info);
		}
	}

	public static String convertFileSize(long sizeInB) {
		long kb = 1024;
		long mb = kb * 1024;
		long gb = mb * 1024;

		if (sizeInB >= gb) {
			return String.format("%.1f GB", (float) sizeInB / gb);
		} else if (sizeInB >= mb) {
			float f = (float) sizeInB / mb;
			return String.format(f > 100 ? "%.0f MB" : "%.1f MB", f);
		} else if (sizeInB >= kb) {
			float f = (float) sizeInB / kb;
			return String.format(f > 100 ? "%.0f KB" : "%.1f KB", f);
		} else
			return String.format("%d B", sizeInB);
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	public static String getLocalMac(Context context) {
		WifiManager wifi = (WifiManager) context
				.getSystemService(Context.WIFI_SERVICE);
		if (wifi.isWifiEnabled()) {
			WifiInfo info = wifi.getConnectionInfo();
			if (info != null) {
				return info.getMacAddress();
			}
			return "";
		}
		return "";
	}

	/**
	 * @Function 检索字符串是否包含于 字符数组中，不用全等于
	 * @author Wangjj
	 * @date 2015年1月5日
	 * @param dest
	 *            模板字符串，如 dfda
	 * @param strArray
	 *            字符数组 如["dfda@xx","2332@xx","fdasd@xx"]
	 * @return
	 */

	public static boolean isContainInStringArray(String dest, String[] strArray) {
		if (TextUtils.isEmpty(dest) || strArray == null) {
			return false;
		}
		for (String str : strArray) {
			if (str.contains(dest))
				return true;
		}
		return false;
	}

	public static int getIndex(String dest, String[] strArray) {
		if (TextUtils.isEmpty(dest) || strArray == null) {
			return -1;
		}
		for (int i = 0; i < strArray.length; i++) {
			if (strArray[i].contains(dest))
				return i;
		}
		return -1;
	}

	public static void saveCrashInfo2Qiniu(Context context, final File file) {
		// 发送到七牛
		Utils.sysoInfo("发送到七牛");
		AsyncHttpClient client = new AsyncHttpClient();
		String tokenUrl = "https://" + ICamGlobal.getInstance().getServerPath()
				+ "/device/upload_token?timestamp=";
		final String keyPath = "upload/android_familycamera/"
				+ Utils.getPackageInfo(context).versionName + "/";
		client.get(tokenUrl + System.currentTimeMillis() / 1000,
				new AsyncHttpResponseHandler() {

					@Override
					public void onSuccess(int paramInt,
							Header[] paramArrayOfHeader, byte[] paramArrayOfByte) {

						UploadManager uploadManager = new UploadManager();
						uploadManager.put(file, keyPath + file.getName(),
								new String(paramArrayOfByte),
								new UpCompletionHandler() {

									@Override
									public void complete(String paramString,
											ResponseInfo paramResponseInfo,
											JSONObject paramJSONObject) {
										if (!paramResponseInfo.isOK()) {
											Utils.sysoInfo("上传七牛失败");
										} else {
											Utils.sysoInfo("上传七牛成功");
											if (file.delete()) {
												Utils.sysoInfo("本地崩溃文件"
														+ file.getName()
														+ "已删除");
											}
										}
									}
								}, null);
					}

					@Override
					public void onFailure(int paramInt,
							Header[] paramArrayOfHeader,
							byte[] paramArrayOfByte, Throwable paramThrowable) {
						Utils.sysoInfo("获取上传token失败");
					}
				});

	}

	public static void enableEditTextDel(Activity activity, final EditText et) {
		final Drawable[] drawables = et.getCompoundDrawables();
		final Drawable delDrawable = drawables[2];
		// final Drawable delDrawable = activity.getResources().getDrawable(
		// R.drawable.edittext_del);

		// 判断无效，页面还没有渲染
		// if (et.length() <= 0) {
		// et.setCompoundDrawables(drawables[0], drawables[1], null,
		// drawables[3]);
		// et.postInvalidate();
		// }

		if (delDrawable != null) {
			et.setOnTouchListener(new OnTouchListener() {

				@Override
				public boolean onTouch(View v, MotionEvent event) {
					int left = et.getWidth() - et.getPaddingRight()
							- delDrawable.getIntrinsicWidth();
					int right = et.getWidth() - et.getPaddingRight();
					int clickX = (int) event.getX();
					Utils.sysoInfo("left clickX right " + left + ":" + clickX
							+ ":" + right);
					if (clickX > left && clickX < right) {// 点击了删除
						et.setText("");
						et.setCompoundDrawables(drawables[0], drawables[1],
								null, drawables[3]);
					}
					return false;
				}
			});
			et.setFocusable(true);
			// et.setFocusableInTouchMode(true);
			// et.setOnFocusChangeListener(new OnFocusChangeListener() {// 无效？
			//
			// @Override
			// public void onFocusChange(View v, boolean hasFocus) {
			// if (hasFocus) {
			// Utils.sysoInfo("hasFocus");
			// if (et.length() <= 0) {
			// et.setCompoundDrawables(drawables[0], drawables[1],
			// null, drawables[3]);
			// } else {
			// et.setCompoundDrawables(drawables[0], drawables[1],
			// delDrawable, drawables[3]);
			// }
			// } else {
			// Utils.sysoInfo("no hasFocus");
			// et.setCompoundDrawables(drawables[0], drawables[1],
			// null, drawables[3]);
			// }
			// }
			// });
			et.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {

				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {

				}

				@Override
				public void afterTextChanged(Editable s) {
					if (s.length() > 0) {
						et.setCompoundDrawables(drawables[0], drawables[1],
								delDrawable, drawables[3]);
					} else {
						et.setCompoundDrawables(drawables[0], drawables[1],
								null, drawables[3]);
					}
				}
			});

		}

	}

	/**
	 * @Function 修改弹窗宽度为屏幕宽度 show方法之后调用
	 * @author Wangjj
	 * @date 2015年1月20日
	 * @param activity
	 * @param dialog
	 * @param padding
	 */

	public static void updateDialogWidth2ScreenWidth(Activity activity,
			Dialog dialog, int padding) {
		WindowManager windowManager = activity.getWindowManager();
		Display display = windowManager.getDefaultDisplay();
		Point point = new Point();
		display.getSize(point);
		WindowManager.LayoutParams lp = dialog.getWindow().getAttributes();
		lp.width = point.x - padding;
		dialog.getWindow().setAttributes(lp);
	}

	/**
	 * @Function 修改弹窗宽度为屏幕宽度左右各20像素 show方法之后调用
	 * @author Wangjj
	 * @date 2015年1月28日
	 * @param activity
	 * @param dialog
	 */

	public static void updateDialogWidth2ScreenWidthDefault(Activity activity,
			Dialog dialog) {
		updateDialogWidth2ScreenWidth(activity, dialog, 40);// 居中时,左右各20像素
	}

	/**
	 * @Function 修改弹窗到底部 show方法之前调用
	 * @author Wangjj
	 * @date 2015年1月20日
	 * @param dialog
	 * @param padding
	 */

	public static void updateDialog2Bottom(Dialog dialog, int padding) {
		Window window = dialog.getWindow();
		window.setGravity(Gravity.BOTTOM);
		WindowManager.LayoutParams lp = window.getAttributes();
		lp.y = padding;
		window.setAttributes(lp);
	}

	/**
	 * @Function 修改弹窗到底部10像素 show方法之前调用
	 * @author Wangjj
	 * @date 2015年1月28日
	 * @param dialog
	 */

	public static void updateDialog2BottomDefault(Dialog dialog) {
		updateDialog2Bottom(dialog, 10);// 底部10像素
	}

	/**
	 * @Function 保存bitmap到sp中
	 * @author Wangjj
	 * @date 2015年7月11日
	 * @param key
	 *            主要使用场景：key为deviceid时，保存的是截屏；key为userid时，保存的是头像
	 * @param bitmap
	 * @param context
	 */

	public synchronized static void saveBitmap(String key, Bitmap bitmap,
			Context context) {
		long start = System.currentTimeMillis();
		int bitsize = bitmap.getByteCount();
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		// 四分之一 压缩到5%左右
		// Options opts = new Options();
		// opts.inSampleSize = 2;
		// opts.inJustDecodeBounds = false;
		// bitmap = BitmapFactory.decodeByteArray(baos.toByteArray(), 0,
		// baos.size(), opts);
		// baos.reset();
		// bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
		sysoInfo("压缩:" + baos.size() + "/" + bitsize + "="
				+ ((float) baos.size() / bitsize));// jpeg 100 压缩10%;png 压缩20%。
		try {
			SharedPreferences sp = context.getSharedPreferences(
					APPConfig.SP_SNAPSHOT, Activity.MODE_PRIVATE);
			sp.edit()
					.putString(
							key + "_snapshot_or_avatar",
							Base64.encodeToString(baos.toByteArray(),
									Base64.DEFAULT)).commit();
			baos.close();
			System.out
					.println("------>" + (System.currentTimeMillis() - start));
			Utils.sysoInfo(key + "保存截图成功,耗时:"
					+ (System.currentTimeMillis() - start));
		} catch (IOException e) {

			// TODO Auto-generated catch block
			e.printStackTrace();

		}
	}

	public static SoftReference<Bitmap> getBitmap(String key, Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPConfig.SP_SNAPSHOT, Activity.MODE_PRIVATE);
		String base64str = sp.getString(key + "_snapshot_or_avatar", "");
		if (!TextUtils.isEmpty(base64str)) {
			byte[] data = Base64.decode(base64str, Base64.DEFAULT);
			return new SoftReference<Bitmap>(BitmapFactory.decodeByteArray(
					data, 0, data.length));
		}
		return new SoftReference<Bitmap>(null);
	}

	/**
	 * @MethodName: saveHandInput2Sp
	 * @Function: 保存用户输入的设备id或扫描的设备id
	 * @author: yuanjs
	 * @date: 2015年9月29日
	 * @email: jiansheng.yuan@wuliangroup.com
	 * @param key
	 *            为uuid
	 * @param deviceid
	 * @param context
	 */
	public static void saveHandInput2Sp(String key, String deviceid,
			Context context) {
		SharedPreferences sp = context.getSharedPreferences(
				APPConfig.SP_CONFIG, Activity.MODE_PRIVATE);
		String idsCache = sp.getString(key
				+ APPConfig.HAND_INPUT_DEVICEID_CACHE, "");
		StringBuffer sBuffer = new StringBuffer();
		String[] enter_deviceIds = new String[0];
		if (idsCache != null && !idsCache.equals("")) {
			sBuffer.append(idsCache);
			enter_deviceIds = idsCache.split("##");
		}
		boolean flag = false;
		for (int i = 0; i < enter_deviceIds.length; i++) {
			if (enter_deviceIds[i].equalsIgnoreCase(deviceid)) {
				flag = true;
				break;
			}
		}
		// 已有的不添加到sharedpreference
		if (!flag) {
			Editor edit = sp.edit();
			edit.putString(key + APPConfig.HAND_INPUT_DEVICEID_CACHE, sBuffer
					.append(deviceid + "##").toString());
			edit.commit();
		}
	}

	/**
	 * 根据ImageView获得适当的压缩的宽和高
	 * 
	 * @param imageView
	 * @return
	 */
	public static String getImageViewMetrics(ImageView imageView) {
		String imageSize = null;
		final DisplayMetrics displayMetrics = imageView.getContext()
				.getResources().getDisplayMetrics();
		final LayoutParams params = imageView.getLayoutParams();

		int width = params.width == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getWidth(); // Get actual image width
		if (width <= 0)
			width = params.width; // Get layout width parameter
		if (width <= 0)
			width = getImageViewFieldValue(imageView, "mMaxWidth"); // Check
																	// maxWidth
																	// parameter
		if (width <= 0)
			width = displayMetrics.widthPixels;

		int height = params.height == LayoutParams.WRAP_CONTENT ? 0 : imageView
				.getHeight(); // Get actual image height
		if (height <= 0)
			height = params.height; // Get layout height parameter
		if (height <= 0)
			height = getImageViewFieldValue(imageView, "mMaxHeight"); // Check
																		// maxHeight
																		// parameter
		if (height <= 0)
			height = displayMetrics.heightPixels;
		return width + "#" + height;

	}

	/**
	 * 反射获得ImageView设置的最大宽度和高度
	 * 
	 * @param object
	 * @param fieldName
	 * @return
	 */
	private static int getImageViewFieldValue(Object object, String fieldName) {
		int value = 0;
		try {
			Field field = ImageView.class.getDeclaredField(fieldName);
			field.setAccessible(true);
			int fieldValue = (Integer) field.get(object);
			if (fieldValue > 0 && fieldValue < Integer.MAX_VALUE) {
				value = fieldValue;
			}
		} catch (Exception e) {
		}
		return value;
	}

	/**
	 * @Function 手势验证，由于dialog依赖于上下文环境，所以存在一定的限制
	 * @author Wangjj
	 * @date 2015年6月8日
	 * @param context
	 */
	@Deprecated
	public static void checkGesturePwdDialog(final Context context) {
		final SharedPreferences sp = context.getSharedPreferences(
				APPConfig.SP_CONFIG, Context.MODE_PRIVATE);
		if (!sp.getBoolean(ICamGlobal.getInstance().getUserinfo().getUuid()
				+ APPConfig.IS_GESTURE_PROTECT, false)
				|| context == null) {
			return;
		}
		final AlertDialog gestureDialog = new AlertDialog.Builder(context,
				R.style.alertDialogFullScreen).create();
		final View noticeDialogView = LinearLayout.inflate(context,
				R.layout.custom_alertdialog_gesture, null);
		Lock9View lock9View = (Lock9View) noticeDialogView
				.findViewById(R.id.lockView);
		final TextView tv_info = (TextView) noticeDialogView
				.findViewById(R.id.tv_info);
		lock9View.setCallBack(new CallBack() {
			int try_times = APPConfig.GESTURE_TRY_TIMES;
			String gesturePwd = sp.getString(ICamGlobal.getInstance()
					.getUserinfo().getUuid()
					+ APPConfig.GESTURE_PWD, "");

			@Override
			public void onFinish(String password) {
				if (gesturePwd.equals(password)) {
					gestureDialog.dismiss();
				} else {

					if (--try_times > 0) {
						tv_info.setText(String.format(
								context.getString(R.string.gesture_try_times),
								try_times));
					} else {
						Editor editor = sp.edit();
						// 清空手势密码
						editor.putBoolean(
								ICamGlobal.getInstance().getUserinfo()
										.getUuid()
										+ APPConfig.IS_GESTURE_PROTECT, false)
								.commit();
						// 注销
						if (sp.getBoolean(APPConfig.IS_THIRD_LOGIN, false)) {
							editor.putString(APPConfig.ACCOUNT_NAME, "");// 如果第三方登陆则清除用户名
						}
						if (sp.getString(APPConfig.ACCOUNT_NAME, "")
								.toLowerCase(Locale.ENGLISH).startsWith("sr-")) {// 清除网关登录用户名
							editor.putString(APPConfig.ACCOUNT_NAME, "");
						}
						editor.putBoolean(APPConfig.IS_THIRD_LOGIN, false);// 一律取消第三方登陆的标记
						editor.putBoolean(APPConfig.IS_LOGIN_OUT, true);// 标记为注销，即取消自动登录
						editor.commit();
					}

				}
			}
		});

		if (!gestureDialog.isShowing()) {
			gestureDialog.setCancelable(false);
			gestureDialog.setCanceledOnTouchOutside(false);
			gestureDialog.show();
			gestureDialog.setContentView(noticeDialogView);
		}
	}
}
