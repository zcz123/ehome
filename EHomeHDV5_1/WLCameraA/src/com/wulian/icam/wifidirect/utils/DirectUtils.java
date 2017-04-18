/**
 * Project Name:  iCam
 * File Name:     DirectUtils.java
 * Package Name:  com.wulian.icam.wifidirect.utils
 * @Date:         2015年6月15日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.wifidirect.utils;

import android.os.Build;

/**
 * @ClassName: DirectUtils
 * @Function: Wi-Fi直连的常用方法类
 * @Date: 2015年6月15日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class DirectUtils {
	static final int SECURITY_WEP = 1;
	static final int SECURITY_PSK = 2;
	static final int SECURITY_PSK2 = 3;
	static final int SECURITY_OPEN = 4;

	public static String ParseSsidPasswd() {
		String result = null;
		return result;
	}

	/**
	 * @MethodName isAdHoc
	 * @Function 是否是AdHoc模式(点对点模式)
	 * @author Puml
	 * @date: 2015年6月15日
	 * @email puml@wuliangroup.cn
	 * @param capabilities
	 * @return
	 */
	public static boolean isAdHoc(String capabilities) {
		return capabilities.indexOf("IBSS") != -1;
	}

	public static boolean isPacketSendTimeDevice() {
		if ((Build.MANUFACTURER.equalsIgnoreCase("Samsung"))
				&& (Build.MODEL.equalsIgnoreCase("G9008"))) {
			return true;
		}
		return false;
	}

	public static String getStringSecurityByCap(String cap) {
		if (cap.contains("WPA-PSK") && !cap.contains("WPA2-PSK")) {
			return "psk";
		}
		if (!cap.contains("WPA-PSK") && cap.contains("WPA2-PSK")) {
			return "psk2";
		}
		if (cap.contains("WPA-PSK") && cap.contains("WPA2-PSK")) {
			return "psk";
		}
		if (cap.contains("WEP")) {
			return "wep";
		}
		return "open";
	}

	public static int getTypeSecurityByCap(String cap) {
		if (cap.equalsIgnoreCase("open")) {
			return SECURITY_OPEN;
		} else if (cap.equalsIgnoreCase("wep")) {
			return SECURITY_WEP;
		} else if (cap.equalsIgnoreCase("psk")) {
			return SECURITY_PSK;
		} else if (cap.equalsIgnoreCase("psk2")) {
			return SECURITY_PSK2;
		}
		return SECURITY_PSK;
	}

	public static boolean isOpenNetwork(String capabilities) {
		return SECURITY_OPEN == getTypeSecurityByCap(capabilities);
	}

	public static boolean isOpenNetwork(int security) {
		return SECURITY_OPEN == security;
	}

}
