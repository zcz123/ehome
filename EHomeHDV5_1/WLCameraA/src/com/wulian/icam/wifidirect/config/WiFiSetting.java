/**
 * Project Name:  iCam
 * File Name:     WiFiSetting.java
 * Package Name:  com.wulian.icam.wifidirect
 * @Date:         2015年6月15日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.wifidirect.config;

/**
 * @ClassName: WiFiSetting
 * @Function: 设置参数
 * @Date: 2015年6月15日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class WiFiSetting {
	/* Is PIN enable */
	public static boolean Is_PIN_Enable = false;
	/* Do not send SSID */
	public static boolean IsSendBcastSsid = true;
	/* Maxium Configuring Time(ms): */
	public static int TotalConfigTimeMs = 120000;
	/* Old Mode Configuring Time(ms): */
	public static int OldModeConfigTimeMs = 0;
	/* Profile Continuous Sending Rounds: */
	public static byte ProfileSendRounds = 1;
	/* Profile Sending Rounds Time Interval(ms): */
	public static int ProfileSendTimeIntervalMs = 1000;
	/* Packet Sending Time Interval(ms): */
	public static int PacketSendTimeIntervalMs = 0;
	/* Each Packet Sending Counts: */
	public static byte EachPacketSendCounts = 1;

}
