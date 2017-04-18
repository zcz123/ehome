/**
 * Project Name:  iCam
 * File Name:     OauthRequestCallBack.java
 * Package Name:  com.wulian.icam.callback
 * @Date:         2014年12月17日
 * Copyright (c)  2014, wulian All Rights Reserved.
 */

package com.wulian.icam.view.main;

/**
 * @ClassName: OauthRequestCallBack
 * @Function: TODO
 * @Date: 2014年12月17日
 * @author Puml
 * @email puml@wuliangroup.cn
 */
public interface OauthRequestCallBack {
	public static final int ACCEPT = 3;// 接受
	public static final int DECLINE = 4;// 拒绝
	public static final int IGNORE = 7;// 忽略
	public void handleOauthRequest(int position, int type);
}
