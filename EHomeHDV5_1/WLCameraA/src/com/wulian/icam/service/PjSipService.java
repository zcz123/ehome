/**
 * Project Name:  iCam
 * File Name:     PjSipService.java
 * Package Name:  com.wulian.icam.service
 * @Date:         2015年3月2日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.service;

import android.app.IntentService;
import android.content.Intent;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.utils.Utils;

/**
 * @ClassName: PjSipService
 * @Function: pjsip服务
 * @Date: 2015年3月2日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class PjSipService extends IntentService {
	public PjSipService() {
		super("pjsipservice");
	}

	ICamGlobal app;

	@Override
	public void onCreate() {
		Utils.sysoInfo("pjsip service onCreate");
		super.onCreate();
		app = ICamGlobal.getInstance();//(ICamApplication) getApplication();

	}

	@Override
	protected void onHandleIntent(Intent intent) {
		app.initSip();
		app.registerAccount();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Utils.sysoInfo("pjsip service onDestroy");
		app.destorySip();
	}

}
