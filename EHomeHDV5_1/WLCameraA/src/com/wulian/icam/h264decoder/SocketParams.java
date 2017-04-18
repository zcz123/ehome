/**
 * Project Name:  iCam
 * File Name:     SocketController.java
 * Package Name:  com.wulian.icam.h264decoder
 * @Date:         2015年5月31日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.h264decoder;

import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.text.TextUtils;

/**
 * @ClassName: SocketController
 * @Function: TODO
 * @Date: 2015年5月31日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class SocketParams {
	/**
	 * @MethodName ConnectionSocket
	 * @Function 握手协议
	 * @author Puml
	 * @date: 2015年5月31日
	 * @email puml@wuliangroup.cn
	 * @param password
	 *            局域网密码
	 * @param sipaccount
	 *            sip账号
	 * @return
	 */
	public String ConnectionSocket(String password, String sipaccount) {
		SocketMsgApiType mApi = SocketMsgApiType.CONNECTION_SOCKET;
		StringBuilder sb = new StringBuilder();
		sb.append("Action: "
				+ mApi.getAction().name().toLowerCase(Locale.getDefault())
				+ "\r\n");
		String data = "";
		try {
			JSONObject json = new JSONObject();
			json.put("cmd", mApi.getRequestCmd());
			json.put("password", TextUtils.isEmpty(password) ? "" : password);
			json.put("sipaccount", TextUtils.isEmpty(sipaccount) ? ""
					: sipaccount);
			data = json.toString();
		} catch (JSONException e) {
			data = "";
		}
		sb.append("Content-Length: " + data.length());
		sb.append(data);
		sb.append("\r\n\r\n");
		return sb.toString();
	}

	/**
	 * @MethodName GetH264FileInfo
	 * @Function 获取所有H264视频文件信息
	 * @author Puml
	 * @date: 2015年5月31日
	 * @email puml@wuliangroup.cn
	 * @param password
	 *            局域网密码
	 * @param sipaccount
	 *            sip账号
	 * @return
	 */
	public String GetH264FileInfo(String password, String sipaccount) {
		SocketMsgApiType mApi = SocketMsgApiType.GET_H264_FILE_INFO;
		StringBuilder sb = new StringBuilder();
		sb.append("Action: "
				+ mApi.getAction().name().toLowerCase(Locale.getDefault())
				+ "\r\n");
		String data = "";
		try {
			JSONObject json = new JSONObject();
			json.put("cmd", mApi.getRequestCmd());
			json.put("password", TextUtils.isEmpty(password) ? "" : password);
			json.put("sipaccount", TextUtils.isEmpty(sipaccount) ? ""
					: sipaccount);
			data = json.toString();
		} catch (JSONException e) {
			data = "";
		}
		sb.append("Content-Length: " + data.length() );
		sb.append(data);
		sb.append("\r\n\r\n");
		return sb.toString();
	}

	/**
	 * @MethodName GetPictureFileInfo
	 * @Function 获取所有截图文件信息
	 * @author Puml
	 * @date: 2015年5月31日
	 * @email puml@wuliangroup.cn
	 * @param password
	 *            局域网密码
	 * @param sipaccount
	 *            sip账号
	 * @return
	 */
	public String GetPictureFileInfo(String password, String sipaccount) {
		SocketMsgApiType mApi = SocketMsgApiType.GET_PICTURE_FILE_INFO;
		StringBuilder sb = new StringBuilder();
		sb.append("Action: "
				+ mApi.getAction().name().toLowerCase(Locale.getDefault())
				+ "\r\n");
		String data = "";
		try {
			JSONObject json = new JSONObject();
			json.put("cmd", mApi.getRequestCmd());
			json.put("password", TextUtils.isEmpty(password) ? "" : password);
			json.put("sipaccount", TextUtils.isEmpty(sipaccount) ? ""
					: sipaccount);
			data = json.toString();
		} catch (JSONException e) {
			data = "";
		}
		sb.append("Content-Length: " + data.length());
		sb.append(data);
		sb.append("\r\n\r\n");
		return sb.toString();
	}

	/**
	 * @MethodName ControlPlayProgress
	 * @Function 进度控制
	 * @author Puml
	 * @date: 2015年5月31日
	 * @email puml@wuliangroup.cn
	 * @param fileindex
	 *            文件索引
	 * @param time
	 *            时间
	 * @return
	 */
	public String ControlPlayProgress(long fileindex, long time) {
		SocketMsgApiType mApi = SocketMsgApiType.CONTROL_PLAY_PROGRESS;
		StringBuilder sb = new StringBuilder();
		sb.append("Action: "
				+ mApi.getAction().name().toLowerCase(Locale.getDefault())
				+ "\r\n");
		String data = "";
		try {
			JSONObject json = new JSONObject();
			json.put("cmd", mApi.getRequestCmd());
			json.put("file-index",
					fileindex <= 0 ? "" : String.valueOf(fileindex));
			json.put("time", time <= 0 ? "" : String.valueOf(time));
			data = json.toString();
		} catch (JSONException e) {
			data = "";
		}
		sb.append("Content-Length: " + data.length());
		sb.append(data);
		sb.append("\r\n\r\n");
		return sb.toString();
	}

	/**
	 * @MethodName StreamStartPlay
	 * @Function 启动回放
	 * @author Puml
	 * @date: 2015年5月31日
	 * @email puml@wuliangroup.cn
	 * @param fileindex
	 *            文件索引
	 * @param time
	 *            时间
	 * @return
	 */
	public String StreamStartPlay(long fileindex, long time) {
		SocketMsgApiType mApi = SocketMsgApiType.STREAM_START_PLAY;
		StringBuilder sb = new StringBuilder();
		sb.append("Action: "
				+ mApi.getAction().name().toLowerCase(Locale.getDefault())
				+ "\r\n");
		String data = "";
		try {
			JSONObject json = new JSONObject();
			json.put("cmd", mApi.getRequestCmd());
			json.put("file-index",
					fileindex <= 0 ? "" : String.valueOf(fileindex));
			json.put("time", time <= 0 ? "" : String.valueOf(time));
			data = json.toString();
		} catch (JSONException e) {
			data = "";
		}
		sb.append("Content-Length: " + data.length());
		sb.append(data);
		sb.append("\r\n\r\n");
		return sb.toString();
	}

	/**
	 * @MethodName ControlStopPlay
	 * @Function 停止回放
	 * @author Puml
	 * @date: 2015年5月31日
	 * @email puml@wuliangroup.cn
	 * @param fileindex
	 *            文件索引
	 * @return
	 */
	public String ControlStopPlay(long fileindex) {
		SocketMsgApiType mApi = SocketMsgApiType.CONTROL_STOP_PLAY;
		StringBuilder sb = new StringBuilder();
		sb.append("Action: "
				+ mApi.getAction().name().toLowerCase(Locale.getDefault())
				+ "\r\n");
		String data = "";
		try {
			JSONObject json = new JSONObject();
			json.put("cmd", mApi.getRequestCmd());
			json.put("file-index",
					fileindex <= 0 ? "" : String.valueOf(fileindex));
			data = json.toString();
		} catch (JSONException e) {
			data = "";
		}
		sb.append("Content-Length: " + data.length());
		sb.append(data);
		sb.append("\r\n\r\n");
		return sb.toString();
	}

	/**
	 * @MethodName PictureGetFile
	 * @Function 获取jpg
	 * @author Puml
	 * @date: 2015年5月31日
	 * @email puml@wuliangroup.cn
	 * @param fileindex
	 *            文件索引
	 * @return
	 */
	public String PictureGetFile(long fileindex) {
		SocketMsgApiType mApi = SocketMsgApiType.PICTURE_GET_FILE;
		StringBuilder sb = new StringBuilder();
		sb.append("Action: "
				+ mApi.getAction().name().toLowerCase(Locale.getDefault())
				+ "\r\n");
		String data = "";
		try {
			JSONObject json = new JSONObject();
			json.put("cmd", mApi.getRequestCmd());
			json.put("file-index",
					fileindex <= 0 ? "" : String.valueOf(fileindex));
			data = json.toString();
		} catch (JSONException e) {
			data = "";
		}
		sb.append("Content-Length: " + data.length());
		sb.append(data);
		sb.append("\r\n\r\n");
		return sb.toString();
	}
}
