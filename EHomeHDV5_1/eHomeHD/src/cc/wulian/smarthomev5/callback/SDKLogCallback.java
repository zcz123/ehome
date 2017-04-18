package cc.wulian.smarthomev5.callback;

import android.util.Log;
import cc.wulian.ihome.wan.LogCallback;

/**
 * Created by Administrator on 2016-9-7
 * Copyright wulian group 2008-2016 All rights reserved. http://www.wuliangroup.com
 **/
public class SDKLogCallback implements LogCallback {
	private static final String TAG = "wlsdk";

	@Override
	public void onLog(LogLevel level, long timestampMS, String loginfo) {
		switch (level) {
		case ERROR:
			Log.e(TAG, loginfo);
			break;
		case WARN:
			Log.w(TAG, loginfo);
			break;
		case INFO:
			Log.i(TAG, loginfo);
			break;
		case DEBUG:
			Log.d(TAG, loginfo);
			break;
		default:
			break;
		}

	}

	@Override
	public void onLog(LogLevel level, long timestampMS, String loginfo, Throwable t) {
		switch (level) {
		case ERROR:
			Log.e(TAG, loginfo, t);
			break;
		case WARN:
			Log.w(TAG, loginfo, t);
			break;
		case INFO:
			Log.i(TAG, loginfo, t);
			break;
		case DEBUG:
			Log.d(TAG, loginfo, t);
			break;
		default:
			break;
		}

	}

}
