/**
 * Project Name:  iCam
 * File Name:     XMLHandler.java
 * Package Name:  com.wulian.icam.utils
 * @Date:         2014年12月4日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.wifidirect.utils;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import com.wulian.icam.wifidirect.model.DeviceDescriptionModel;

import android.text.TextUtils;

/**
 * @ClassName: XMLHandler
 * @Function: XML处理
 * @Date: 2014年12月4日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class XMLHandler {
	private static final String XML_KEY = "context-text:\n";

	public static List<DeviceDescriptionModel> getDeviceList(String json) {
		List<DeviceDescriptionModel> data = new ArrayList<DeviceDescriptionModel>();
		try {
			JSONObject jsonObj = new JSONObject(json);
			int status = jsonObj.getInt("status");// 肯定会有的
			if (status == 1) {
				String dataJson = jsonObj.getString("data");// 肯定会有
				if (!TextUtils.isEmpty(dataJson)) {
					JSONArray jsonArray = new JSONArray(dataJson);
					int size = jsonArray.length();
					for (int i = 0; i < size; i++) {
						JSONObject itemJson = jsonArray.getJSONObject(i);
						String remoteIp = itemJson.getString("ip");// 肯定会有
						String itemData = itemJson.getString("item");// 肯定会有
						DeviceDescriptionModel device = handleDeviceDescriptionXML(
								itemData, remoteIp);
						if (device != null) {
							data.add(device);
						}
					}
				}
			}
		} catch (JSONException e) {
			data.clear();
		}
		return data;
	}

	public static DeviceDescriptionModel handleDeviceDescriptionXML(
			String data, String remoteIP) {
		String realData = data;
		int startIndex = realData.indexOf(XML_KEY);
		if (startIndex > 0) {
			realData = realData.substring(startIndex + XML_KEY.length());
		}
		DeviceDescriptionModel mDeviceDescriptionModel = null;
		try {
			StringReader xmlReader = new StringReader(realData);
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
					mDeviceDescriptionModel = new DeviceDescriptionModel();
					mDeviceDescriptionModel.setRemoteIP(remoteIP);
				}
					break;
				case XmlPullParser.START_TAG:
					localName = xmlPullParser.getName();
					if ("local_mac".equalsIgnoreCase(localName)) {
						mDeviceDescriptionModel.setLocal_mac((xmlPullParser
								.nextText()));
					} else if ("model".equalsIgnoreCase(localName)) {
						mDeviceDescriptionModel.setLocal_mac((xmlPullParser
								.nextText()));
					} else if ("model".equalsIgnoreCase(localName)) {
						mDeviceDescriptionModel.setModel((xmlPullParser
								.nextText()));
					} else if ("serialnum".equalsIgnoreCase(localName)) {
						mDeviceDescriptionModel.setSerialnum((xmlPullParser
								.nextText()));
					} else if ("version".equalsIgnoreCase(localName)) {
						mDeviceDescriptionModel.setVersion((xmlPullParser
								.nextText()));
					} else if ("hardware".equalsIgnoreCase(localName)) {
						mDeviceDescriptionModel.setHardware((xmlPullParser
								.nextText()));
					} else if ("sipaccount".equalsIgnoreCase(localName)) {
						mDeviceDescriptionModel.setSipaccount((xmlPullParser
								.nextText()));
					} else if ("video_port".equalsIgnoreCase(localName)) {
						Integer video_port = -1;
						try {
							String video_portStr = xmlPullParser.nextText();
							video_port = Integer.parseInt(video_portStr, 10);
						} catch (NumberFormatException e) {
							video_port = -1;
						}
						mDeviceDescriptionModel.setVideo_port(video_port);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = xmlPullParser.next();
			}
		} catch (XmlPullParserException e) { // XmlPullParserFactory.newInstance
			e.printStackTrace();
			mDeviceDescriptionModel = null;
		} catch (IllegalArgumentException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			mDeviceDescriptionModel = null;
		} catch (IllegalStateException e) { // xmlSerializer.setOutput
			e.printStackTrace();
			mDeviceDescriptionModel = null;
		} catch (Exception e) {
			e.printStackTrace();
			mDeviceDescriptionModel = null;
		}
		return mDeviceDescriptionModel;
	}
}
