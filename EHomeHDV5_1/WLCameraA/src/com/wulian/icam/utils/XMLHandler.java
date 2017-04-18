/**
 * Project Name:  iCam
 * File Name:     XMLHandler.java
 * Package Name:  com.wulian.icam.utils
 * @Date:         2014年12月4日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.utils;

import java.io.StringReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.wulian.icam.model.AlarmMessage;
import com.wulian.icam.model.DeviceDetailMsg;
import com.wulian.icam.model.VideoTimePeriod;
import com.wulian.siplibrary.model.linkagedetection.TimePeriod;
import com.wulian.oss.model.GetObjectDataModel;

/**
 * @ClassName: XMLHandler
 * @Function: XML处理
 * @Date: 2014年12月4日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class XMLHandler {
	public static AlarmMessage handleAlarmMsgXML(String data) {
		AlarmMessage mAlarmMessage = null;
		try {
			StringReader xmlReader = new StringReader(data);
			XmlPullParserFactory pullFactory = XmlPullParserFactory
					.newInstance();
			XmlPullParser xmlPullParser = pullFactory.newPullParser();
			xmlPullParser.setInput(xmlReader); // 保存创建的xml
			int eventType = xmlPullParser.getEventType();
			boolean isDone = false;// 具体解析xml
			while ((eventType != XmlPullParser.END_DOCUMENT)
					&& (isDone != true)) {
				String localName = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT: {
					mAlarmMessage = new AlarmMessage();
				}
					break;
				case XmlPullParser.START_TAG:
					localName = xmlPullParser.getName();
					if ("uri".equalsIgnoreCase(localName)) {
						mAlarmMessage.setSendUri(xmlPullParser.nextText());
					} else if ("alarm".equalsIgnoreCase(localName)) {
						int msgType = -1;
						try {
							msgType = Integer.valueOf(xmlPullParser
									.getAttributeValue(0));
						} catch (NumberFormatException e) {
							e.printStackTrace();
							msgType = -1;
							mAlarmMessage = null;
							break;
						}
						mAlarmMessage.setMsgType(msgType);
						Date mDate = new Date();
						try {
							DateFormat df = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							String time = xmlPullParser.nextText();
							mDate = df.parse(time);
						} catch (java.text.ParseException e) {
							e.printStackTrace();
							mAlarmMessage = null;
							break;
						}
						long time = mDate.getTime();
						mAlarmMessage.setStime(time);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
			mAlarmMessage = null;
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			mAlarmMessage = null;
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			mAlarmMessage = null;
		} catch (Exception e) {
			e.printStackTrace();
			mAlarmMessage = null;
		}
		return mAlarmMessage;
	}

	// public static String getStatus(String xmlData) {
	//
	// Pattern pattern = Pattern.compile("<status>(\\w+)</status>");
	// Matcher matcher = pattern.matcher(xmlData);
	// if (matcher.find())
	// return matcher.group(1);
	// return "";
	// }
	/**
	 * @MethodName: getDeviceDetailMsg
	 * @Function: 解析设备详细信息XML
	 * @author: yuanjs
	 * @date: 2015年10月19日
	 * @email: jiansheng.yuan@wuliangroup.com
	 * @return
	 */
	// XML模型：
	// <answer>
	// <cmd>00</cmd>
	// <seq>3</seq>
	// <uri>sip:cmic0365aea213cd8a3a@sh.gg</uri>
	// <model>ICAM-0001</model>
	// <version>V1.3.3</version>
	// <hardware>WL-ZNCD-3516-1-0.3</hardware>
	// <DPIs>320x240,640x480,1280x720</DPIs>
	// <wifi_ssid>TP-LINK_EGeeks</wifi_ssid>
	// <wifi_signal>100</wifi_signal>
	// <ip>192.168.1.101</ip>
	// <mac>ac:a2:13:cd:8a:3a</mac>
	// </answer>
	public static DeviceDetailMsg getDeviceDetailMsg(String xml) {
		DeviceDetailMsg deviceDetailMsg = null;
		StringReader xmlReader = new StringReader(xml);
		XmlPullParserFactory pullFactory;
		try {
			pullFactory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = pullFactory.newPullParser();
			xmlPullParser.setInput(xmlReader); // 保存创建的xml
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String localName = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT: {
					deviceDetailMsg = new DeviceDetailMsg();
				}
					break;
				case XmlPullParser.START_TAG:
					localName = xmlPullParser.getName();
					if ("version".equalsIgnoreCase(localName)) {
						deviceDetailMsg.setVersion(xmlPullParser.nextText());
					} else if ("wifi_ssid".equalsIgnoreCase(localName)) {
						deviceDetailMsg.setWifi_ssid(xmlPullParser.nextText()
								.trim());
					} else if ("wifi_signal".equalsIgnoreCase(localName)) {
						deviceDetailMsg
								.setWifi_signal(xmlPullParser.nextText());
					} else if ("ip".equalsIgnoreCase(localName)) {
						deviceDetailMsg.setWifi_ip(xmlPullParser.nextText());
					} else if ("mac".equalsIgnoreCase(localName)) {
						deviceDetailMsg.setWifi_mac(xmlPullParser.nextText());
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
			deviceDetailMsg = null;
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			deviceDetailMsg = null;
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			deviceDetailMsg = null;
		} catch (Exception e) {
			e.printStackTrace();
			deviceDetailMsg = null;
		}
		return deviceDetailMsg;
	}

	/**
	 * @MethodName: getHistoryRecordList
	 * @Function: 解析获取获取视频回看时间段
	 * @author: yuanjs
	 * @date: 2015年10月22日
	 * @email: jiansheng.yuan@wuliangroup.com
	 * @param xml
	 *            xml文件
	 * @return
	 */
	public static List<VideoTimePeriod> getHistoryRecordList(String xml) {
		List<VideoTimePeriod> recordList = null;
		StringReader xmlReader = new StringReader(xml);
		XmlPullParserFactory pullFactory;
		try {
			pullFactory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = pullFactory.newPullParser();
			xmlPullParser.setInput(xmlReader); // 保存创建的xml
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String localName = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT: {
					recordList = new ArrayList<VideoTimePeriod>();
				}
					break;
				case XmlPullParser.START_TAG:
					localName = xmlPullParser.getName();
					if ("time".equalsIgnoreCase(localName)) {
						try {
							long StartTime = Integer.valueOf(xmlPullParser
									.getAttributeValue(0));
							long EndTime = Integer.valueOf(xmlPullParser
									.getAttributeValue(1));
							if (StartTime > 0 && EndTime > 0) {
								VideoTimePeriod vtp = new VideoTimePeriod();
								if (StartTime >= EndTime) {
									vtp.setTimeStamp(EndTime);
									vtp.setEndTimeStamp(StartTime);
								} else {
									vtp.setTimeStamp(StartTime);
									vtp.setEndTimeStamp(EndTime);
								}
								vtp.setFileName("");
								recordList.add(vtp);
							}
						} catch (Exception e) {

						}
						// String startTime =
						// millisecondTransformToYMD(xmlPullParser
						// .getAttributeValue(0));
						// String endTime =
						// millisecondTransformToYMD(xmlPullParser
						// .getAttributeValue(1));
						// recordList.add(startTime + "#" + endTime);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
			recordList = null;
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			recordList = null;
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			recordList = null;
		} catch (Exception e) {
			e.printStackTrace();
			recordList = null;
		} finally {
			if (xmlReader != null) {
				xmlReader.close();
			}
		}
		return recordList;
	}

	/**
	 * @MethodName: getHistoryRecordList
	 * @Function: 解析获取获取视频回看时间段
	 * @author: yuanjs
	 * @date: 2015年10月22日
	 * @email: jiansheng.yuan@wuliangroup.com
	 * @param xml
	 *            xml文件
	 * @return
	 */
	public static List<String> getHistoryRecordList1(String xml) {
		List<String> recordList = null;
		StringReader xmlReader = new StringReader(xml);
		XmlPullParserFactory pullFactory;
		try {
			pullFactory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = pullFactory.newPullParser();
			xmlPullParser.setInput(xmlReader); // 保存创建的xml
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String localName = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT: {
					recordList = new ArrayList<String>();
				}
					break;
				case XmlPullParser.START_TAG:
					localName = xmlPullParser.getName();
					if ("time".equalsIgnoreCase(localName)) {
						String startTime = millisecondTransformToYMD(xmlPullParser
								.getAttributeValue(0));
						String endTime = millisecondTransformToYMD(xmlPullParser
								.getAttributeValue(1));
						recordList.add(startTime + "#" + endTime);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
			recordList = null;
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			recordList = null;
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			recordList = null;
		} catch (Exception e) {
			e.printStackTrace();
			recordList = null;
		} finally {
			if (xmlReader != null) {
				xmlReader.close();
			}
		}
		return recordList;
	}

	// 解析XML数据
		public static String parseXMLDataGetStatus(String xmlData) {
			String regEx = "<status>[^>]*</status>";
			String result = "null";
			Pattern pattern = Pattern.compile(regEx);
			Matcher m = pattern.matcher(xmlData);
			if (m.find()) {
				result = m.group();
			}
			int start = "<status>".length();
			int end = result.length() - "</status>".length();
			if (!result.equals("null")) {
				result = result.substring(start, end);
				return result;
			}
			return null;
		}
	
	// 解析XML数据
	public static String parseXMLDataGetSessionID(String xmlData) {
		String regEx = "<sessionID>[^>]*</sessionID>";
		String result = "null";
		Pattern pattern = Pattern.compile(regEx);
		Matcher m = pattern.matcher(xmlData);
		if (m.find()) {
			result = m.group();
		}
		int start = "<sessionID>".length();
		int end = result.length() - "</sessionID>".length();
		if (!result.equals("null")) {
			result = result.substring(start, end);
			return result;
		}
		return null;
	}

	public static GetObjectDataModel getObjectData(String xml, String deviceID) {
		GetObjectDataModel objectData = null;
		StringReader xmlReader = new StringReader(xml);
		XmlPullParserFactory pullFactory;
		try {
			pullFactory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = pullFactory.newPullParser();
			xmlPullParser.setInput(xmlReader); // 保存创建的xml
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String localName = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT: {
					objectData = new GetObjectDataModel();
				}
					break;
				case XmlPullParser.START_TAG:
					localName = xmlPullParser.getName();
					if ("status".equalsIgnoreCase(localName)) {
						objectData.setObjectName(deviceID + "/"
								+ xmlPullParser.nextText());
					} else if ("size".equalsIgnoreCase(localName)) {
						int fileSize = -1;
						try {
							fileSize = Integer.parseInt(xmlPullParser
									.nextText().trim());
						} catch (Exception e) {
							fileSize = -1;
						}
						objectData.setFileSize(fileSize);
					} else if ("timestamp".equalsIgnoreCase(localName)) {
						long time = -1;
						try {
							time = Integer.parseInt(xmlPullParser.nextText()
									.trim());
						} catch (Exception e) {
							time = -1;
						}
						objectData.setTimeStamp(time);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
			objectData = null;
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			objectData = null;
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			objectData = null;
		} catch (Exception e) {
			e.printStackTrace();
			objectData = null;
		}
		return objectData;
	}

	// 解析XML数据
	public static String parseXMLDataGetFilename(String xmlData) {
		String regEx = "<status>[^>]*</status>";
		String result = "null";
		Pattern pattern = Pattern.compile(regEx);
		Matcher m = pattern.matcher(xmlData);
		if (m.find()) {
			result = m.group();
		}
		int start = "<status>".length();
		int end = result.length() - "</status>".length();
		if (!result.equals("null")) {
			result = result.substring(start, end);
			return result;
		}
		return null;
	}

	public static String parseXMLDataGetFileSize(String xmlData) {
		String regEx = "<size>[^>]*</size>";
		String result = "null";
		Pattern pattern = Pattern.compile(regEx);
		Matcher m = pattern.matcher(xmlData);
		if (m.find()) {
			result = m.group();
		}
		int start = "<size>".length();
		int end = result.length() - "</size>".length();
		if (!result.equals("null")) {
			result = result.substring(start, end);
			return result;
		}
		return null;
	}

	// 解析XML数据
	public static boolean parseXMLDataJudgeEnd(String xmlData) {
		StringReader xmlReader = new StringReader(xmlData);
		XmlPullParserFactory pullFactory;
		try {
			pullFactory = XmlPullParserFactory.newInstance();
			XmlPullParser xmlPullParser = pullFactory.newPullParser();
			xmlPullParser.setInput(xmlReader); // 保存创建的xml
			int eventType = xmlPullParser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				String localName = null;
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT: {

				}
					break;
				case XmlPullParser.START_TAG:
					localName = xmlPullParser.getName();
					if ("history".equalsIgnoreCase(localName)) {
						int tailValue = Integer.valueOf(xmlPullParser
								.getAttributeValue(1));
						if (tailValue == 1) {
							return true;
						}
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (xmlReader != null) {
				xmlReader.close();
			}
		}
		return false;
	}

	// 将毫秒转换成"yyyy-MM-dd HH:mm:ss"格式
	private static String millisecondTransformToYMD(String millisecond) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String result = "";
		if (millisecond != null) {
			result = df.format(new Date(Long.parseLong(millisecond)));
		}
		return result;
	}
}
