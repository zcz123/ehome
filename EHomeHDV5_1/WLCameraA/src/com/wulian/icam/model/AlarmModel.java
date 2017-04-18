/**
 * Project Name:  iCam
 * File Name:     AlarmModel.java
 * Package Name:  com.wulian.icam.model
 * @Date:         2015年6月12日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.model;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * @ClassName: AlarmModel
 * @Function: 报警消息
 * @Date: 2015年6月12日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class AlarmModel {
	private String id;// 数据库中的主键
	private String name;// 名称
	private String type;// 命令类型
	private String function;// 功能
	private String returnData;// 返回值
	private String from;// 来自
	private String fromNick;// 来自
	private String time, YMDtime, HSMtime;// 时间
	private boolean isDelete;

	// private String isread;// 后续实现
	// private String imgUrl;// 后续实现，目前是默认图片
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @Function 报警类型
	 * @author Wangjj
	 * @date 2015年6月12日
	 * @return 移动报警 200 遮挡报警 201 SD 卡异常 220 SD 卡满 221
	 */

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getFunction() {
		return function;
	}

	public void setFunction(String function) {
		this.function = function;
	}

	public String getReturnData() {
		return returnData;
	}

	public void setReturnData(String returnData) {
		this.returnData = returnData;
	}

	public String getFrom() {
		return from.replaceAll("@wuliangroup.cn", "");
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTime() {
		return time;
	}

	public String getTimeYMD() {
		return YMDtime;
	}

	public String getTimeHMS() {
		return HSMtime;
	}

	/**
	 * @Function    设置时间
	 * @author      Wangjj
	 * @date        2015年6月16日
	 * @param time 单位为秒
	 */
	 
	public void setTime(String time) {
		this.time = time;
		try {
			Date date = new Date(Long.parseLong(time) * 1000);
			SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd",
					Locale.ENGLISH);
			SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm:ss", Locale.ENGLISH);
			YMDtime = sdf1.format(date);
			HSMtime = sdf2.format(date);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
	}

	public boolean isDelete() {
		return isDelete;
	}

	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}

	/**
	 * fromNick.
	 *
	 * @return  the fromNick
	 */
	public String getFromNick() {
		return fromNick;
	}

	/**
	 * fromNick
	 * @param   fromNick    the fromNick to set
	 */
	public void setFromNick(String fromNick) {
		this.fromNick = fromNick;
	}

}
