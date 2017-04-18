package com.wulian.iot.connect;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import com.wulian.iot.Config;
import com.wulian.iot.bean.CameraEagleUpdateInfo;
import com.wulian.iot.bean.CameraUpdateInfo;
import com.wulian.iot.view.manage.FirmwareUpManage;

import android.util.Log;
import android.util.Xml;

public class Connect {
	private String outcome;
	public Connect(){		
	}
	/**将InputStream转为string*/
	public String isToString(InputStream is) {
		ByteArrayOutputStream baop = new ByteArrayOutputStream();
		int b;
		try {
			while ((b = is.read()) != -1) {
				baop.write(b);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				is.close();
				baop.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return baop.toString();
	}
	/**
	 * 网络请求 得到xml
	 * 猫眼
	 * @param url
	 * @param mConnectEagleManage
	 */
	public void parseXMLInfo(final String url,
			final ConnectEagleManage mConnectEagleManage) {
		new Thread() {
			@Override
			public void run() {
				// TODO Auto-generated method stub
				super.run();
				URL httpUrl = null;
				HttpURLConnection httpcon = null;
				try {
					httpUrl = new URL(url);
					httpcon = (HttpURLConnection) httpUrl.openConnection();

					if (httpcon.getResponseCode() == 200) {
						outcome = new Connect().isToString(httpcon
								.getInputStream());
						if (outcome != null) {
							// getXMLParseString(outcome)
							mConnectEagleManage
									.success(getXMLParseString(outcome));
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}.start();
	}

	/**
	 * 解析xml
	 * 猫眼
	 * @param xmlStr
	 * @return
	 */
	public CameraEagleUpdateInfo getXMLParseString(String xmlStr) {
		CameraEagleUpdateInfo mCameraEagleUpdateInfo = null;
		ByteArrayInputStream tInputStringStream = null;
		try {
			if (xmlStr != null && !xmlStr.trim().equals("")) {
				tInputStringStream = new ByteArrayInputStream(xmlStr.getBytes());
			}
		} catch (Exception e) {
			throw new RuntimeException();
		}

		XmlPullParser parser = Xml.newPullParser();
		try {
			parser.setInput(tInputStringStream, "UTF-8");
			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				switch (eventType) {
				case XmlPullParser.START_DOCUMENT:
					mCameraEagleUpdateInfo = new CameraEagleUpdateInfo();
					break;
				case XmlPullParser.START_TAG:
					String name = parser.getName();
					if (name.equalsIgnoreCase("versionCode")) {
						int vCode = Integer.parseInt(parser.nextText());
						mCameraEagleUpdateInfo.setVersionCode(vCode);
					} else if (name.equalsIgnoreCase("versionName")) {
						String vName = parser.nextText();
						mCameraEagleUpdateInfo.setVersionName(vName);
					} else if (name.equalsIgnoreCase("remindTimes")) {
						int rTime = Integer.parseInt(parser.nextText());
						mCameraEagleUpdateInfo.setRemindTimes(rTime);
					} else if (name.equals("devmodel")) {
						String mdevmodel = parser.nextText();
						mCameraEagleUpdateInfo.setDevmodel(mdevmodel);
					} else if (name.equals("fwmodel")) {
						String mfwmodel = parser.nextText();
						mCameraEagleUpdateInfo.setFwmodel(mfwmodel);
					} else if (name.equalsIgnoreCase("versionTxts")) {
						String vTxts = parser.nextText();
						mCameraEagleUpdateInfo.setVersionTxts(vTxts);
					} else if (name.equalsIgnoreCase("url")) {
						String url = parser.nextText();
						mCameraEagleUpdateInfo.setUrl(url);
					}
					break;
				case XmlPullParser.END_TAG:
					break;
				}
				eventType = parser.next();
			}
			tInputStringStream.close();
		} catch (XmlPullParserException e) {
			return null;
		} catch (IOException e) {
			return null;
		}
		return mCameraEagleUpdateInfo;
	}
}
