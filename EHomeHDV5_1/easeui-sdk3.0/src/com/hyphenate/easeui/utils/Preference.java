package com.hyphenate.easeui.utils;

import android.app.AlarmManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 系统参数
 * 
 */
public class Preference {
	public static final String ENTER_TYPE_ACCOUNT = "account";
	public static final String ENTER_TYPE_GW = "gateway";
	public static final String ISADMIN_TRUE = "1";
	public static final String ISADMIN_FALSE = "0";
	
	private final Context mContext;
	private final SharedPreferences mPreferences;
	private final Editor mEditor;

	private SharedPreferences mGwIDPreferences;
	private Editor mGwIDEditor;

	private static Preference mInstance;

	public static Preference getPreferences(Context context) {
		if (mInstance == null)
			mInstance = new Preference(context);
		return mInstance;
	}
	
	private Preference(Context context) {
		mContext = context;
		mPreferences = mContext.getSharedPreferences(
				IPreferenceKey.P_KEY_PREFERENCE, Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();
	}

	public void clearAllData() {
		mEditor.clear().commit();
	}

	public void saveUserNickName(String str,String ID) {
		mEditor.putString(IPreferenceKey.P_KEY_CHAT_USER_NICK_NAME+ID, str)
				.commit();
	}

	public String getUserNickName(String ID){
		return mPreferences.getString(IPreferenceKey.P_KEY_CHAT_USER_NICK_NAME+ID, "");
	}

	public void saveUserNickNameTime(String str,String ID) {
		mEditor.putString(IPreferenceKey.P_KEY_CHAT_USER_NICK_NAME_TIME+ID, str)
				.commit();
	}

	public String getUserNickNameTime(String ID){
		return mPreferences.getString(IPreferenceKey.P_KEY_CHAT_USER_NICK_NAME_TIME+ID, "0");
	}

}