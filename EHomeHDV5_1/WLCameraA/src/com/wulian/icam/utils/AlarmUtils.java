/**
 * Project Name:  iCam
 * File Name:     AlarmUtils.java
 * Package Name:  com.wulian.icam.utils
 * @Date:         2015年6月12日
 * Copyright (c)  2015, wulian All Rights Reserved.
 */

package com.wulian.icam.utils;

import com.wulian.icam.R;


/**
 * @ClassName: AlarmUtils
 * @Function: 报警工具类
 * @Date: 2015年6月12日
 * @author Wangjj
 * @email wangjj@wuliangroup.cn
 */
public class AlarmUtils {
	public int getDrawableIdByCmd(int cmd) {
		switch (cmd) {
		case 200:// 移动布防
			return R.drawable.icon_start_wulian_logo;

		}
		return 0;
	}
}
