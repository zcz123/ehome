/**
 * Project Name:  iCam
 * File Name:     FeedbackInfo.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2015年7月10日
 * Copyright (c)  2015, wulian All Rights Reserved.
*/

package com.wulian.icam.model;
/**
 * @ClassName: FeedbackInfo
 * @Function:  TODO
 * @Date:      2015年7月10日
 * @author:    yuanjs
 * @email:     yuanjsh@wuliangroup.cn
 */
public class FeedbackInfo {
	private int type;//1--服务器端；0--客户端;3--错误类型
	private String feedback;//内容
	private Long creadet;//时间戳
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getFeedback() {
		return feedback;
	}
	public void setFeedback(String feedback) {
		this.feedback = feedback;
	}
	public Long getCreadet() {
		return creadet;
	}
	public void setCreadet(Long creadet) {
		this.creadet = creadet;
	}
}

