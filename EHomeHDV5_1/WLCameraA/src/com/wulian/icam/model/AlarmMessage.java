/**
 * Project Name:  iCam
 * File Name:     AlarmMessage.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2014年12月4日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.model;

import java.io.Serializable;

/**
 * @ClassName: AlarmMessage
 * @Function: 报警消息
 * @Date: 2014年12月4日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public class AlarmMessage implements Serializable {

	/**
	 * serialVersionUID 作用:序列号
	 */
	private static final long serialVersionUID = -8397705798251184499L;

	private long id;// 消息ID
	private String deviceName;// 设备名
	private String sendUri;// 发送者URI
	private int msgType;// 消息类型
	private long stime;// 时间戳

	public AlarmMessage() {
		id = -1;
		deviceName = "No Device";//should never show
	}

	public void setId(long id) {
		this.id = id;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public long getId() {
		return id;
	}

	public void setSendUri(String sendUri) {
		this.sendUri = sendUri;
	}

	public String getSendUri() {
		return sendUri;
	}

	public void setMsgType(int msgType) {
		this.msgType = msgType;
	}

	public int getMsgType() {
		return msgType;
	}

	public void setStime(long stime) {
		this.stime = stime;
	}

	/**
	 * @Function    毫秒级
	 * @author      Wangjj
	 * @date        2015年6月16日
	 * @return
	 */
	public long getStime() {
		return stime;
	}
}
