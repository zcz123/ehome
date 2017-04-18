package cc.wulian.smarthomev5.utils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

public class VersionUtil
{
	private static final String[] SYSTEM_COLUMNS = {"ID", "DISPLAY", "HOST", "MANUFACTURER", "MODEL", "USER"};
	private static final String APP_VERSION = "AppVersion: ";
	private static final String SYS_VERSION = "SystemVersion: ";
	private static final String VERSION_07 = "2.1";
	private static final String VERSION_08 = "2.2";
	private static final String VERSION_09 = "2.3";
	private static final String VERSION_10 = "2.3.3";
	private static final String VERSION_11 = "3.0";
	private static final String VERSION_12 = "3.1";
	private static final String VERSION_13 = "3.2";
	private static final String VERSION_14 = "4.0";
	private static final String VERSION_15 = "4.0.3";
	private static final String VERSION_16 = "4.1";
	private static final String VERSION_17 = "4.2";
	private static final String VERSION_18 = "4.3";
	private static final String VERSION_19 = "4.4";
	private static final String VERSION_20 = "4.4W";
	private static final String VERSION_21 = "5.0";
	
	private static final Map<Integer, String> mVersionMap = new HashMap<Integer, String>();
	static{
		mVersionMap.put( 7, VERSION_07);
		mVersionMap.put( 8, VERSION_08);
		mVersionMap.put( 9, VERSION_09);
		mVersionMap.put(10, VERSION_10);
		mVersionMap.put(11, VERSION_11);
		mVersionMap.put(12, VERSION_12);
		mVersionMap.put(13, VERSION_13);
		mVersionMap.put(14, VERSION_14);
		mVersionMap.put(15, VERSION_15);
		mVersionMap.put(16, VERSION_16);
		mVersionMap.put(17, VERSION_17);
		mVersionMap.put(18, VERSION_18);
		mVersionMap.put(19, VERSION_19);
		mVersionMap.put(20, VERSION_20);
		mVersionMap.put(21, VERSION_21);
	}

	public static int getVersionCodeByAPK(Context context,String path){
		int code = 0;
		try{
			PackageManager pm = context.getPackageManager();    
	        PackageInfo info = pm.getPackageArchiveInfo(path, PackageManager.GET_ACTIVITIES);    
	        if(info != null){    
	            code =info.versionCode;    
	        }  
		}catch(Exception e){
		}
        return code;
	}
	public static int getVersionCode( Context context ){
		int localVersionCode = 9999;
		try{
			PackageInfo info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			localVersionCode = info.versionCode;
		}
		catch (Exception e){
		}
		return localVersionCode;
	}

	public static String getVersionName( Context context ){
		String version = "0.0.0";

		PackageManager packageManager = context.getPackageManager();
		try{
			PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
			version = packageInfo.versionName;
		}
		catch (NameNotFoundException e){
		}
		return version;
	}

	public static String getVersionInfo(){
		StringBuffer sb = new StringBuffer();
		final String[] columns = SYSTEM_COLUMNS;
		int length = columns.length;
		for (int i = 0; i < length; i++){
			try{
				Field field = Build.class.getDeclaredField(columns[i]);
				field.setAccessible(true);
				sb.append(field.getName());
				sb.append(":");
				sb.append(field.get(null).toString());
				sb.append("\r\n");
			}
			catch (Exception e){
			}
		}
		return sb.toString();
	}

	public static int getSystemSdkInt(){
		return Build.VERSION.SDK_INT;
	}

	public static String getSystemEdition( Context context ){
		int version = getSystemSdkInt();
		String edition = mVersionMap.get(version);
		String temp = SYS_VERSION + edition;
		String temp2 = APP_VERSION + getVersionName(context);

		return temp + "\r\n" + temp2;
	}
}
