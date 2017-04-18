package cc.wulian.smarthomev5.utils;

import android.util.Log;

public class LogUtil
{
	private static final String TAG = LogUtil.class.getSimpleName();
	
	// FIXME modify "isDebug = false" when publishing
	public static final boolean isDebug = false;

	public static void log( Object msg ){
		if (isDebug) log(TAG, msg);
	}

	public static void log( String tag, Object msg ){
		if (isDebug) Log.d(tag, String.valueOf(msg));
	}

	public static void logWarn( Object msg ){
		if (isDebug) logWarn(TAG, msg);
	}

	public static void logWarn( String tag, Object msg ){
		if (isDebug) Log.w(tag, String.valueOf(msg));
	}

	public static void logErr( Object msg ){
		if (isDebug) logErr(TAG, msg);
	}

	public static void logErr( String tag, Object msg ){
		if (isDebug) Log.e(tag, String.valueOf(msg));
	}

	public static void logException( String msg, Throwable throwable ){
		if (isDebug) Log.w(TAG, msg, throwable);
	}
}