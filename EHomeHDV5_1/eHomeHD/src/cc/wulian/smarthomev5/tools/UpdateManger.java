package cc.wulian.smarthomev5.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Looper;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.DownloadManager.DownloadListener;
import cc.wulian.smarthomev5.utils.FileUtil;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.VersionUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

public class UpdateManger
{
	public static String BASE_URL = "https://oht2fful5.qnssl.com/";
//	public static final String BASE_URL = "http://192.168.0.61:8080/wulian/V5/";
	public static final String FILE_NAME = "smarthomev5.apk";
	public static final String SPLASH_NAME = "splash.png";
	public static String LOAD_PATH = BASE_URL + FILE_NAME;
	public static String LOAD_FILE_URL_VER_ZH = BASE_URL+"smarthomev5_zh.xml";
	public static String LOAD_FILE_URL_VER_EN = BASE_URL+"smarthomev5_en.xml";
	public 	static final int 		SPLASH_DISPLAY_TIME 			= 2500;
	private static final String LOAD_VERSION_KEY_ROOT 		= "versionInfo";
	private static final String LOAD_VERSION_KEY_CODE 		= "versionCode";
	private static final String LOAD_VERSION_REMINDTIMES 	= "remindTimes";
	private static final String LOAD_VERSION_KEY_NAME 		= "versionName";
	private static final String LOAD_VERSION_KEY_TXTS 		= "versionTxts";
	private static final String LOAD_VERSION_KEY_IMGS 		= "versionImgs";
	private static final String LOAD_VERSION_KEY_MD5 		= "versionMD5";
	private static final String LOAD_VERSION_KEY_IMGS_VER = "version";

	private Context mContext;
	private NewVersionDownloadListener downloadListener ;
	private ServerAppInfo appInfo;
	private boolean isRunning = false;
	private static UpdateManger instance = null;
	private Preference preference = Preference.getPreferences();
	private String apkPath;
	private String splashPath;
	private Handler handler = new Handler(Looper.getMainLooper());
	private UpdateManger(Context context)
	{
		this.mContext = context;
		BASE_URL = context.getResources().getString(R.string.BASE_URL);
		LOAD_PATH = BASE_URL + FILE_NAME;
		LOAD_FILE_URL_VER_ZH = BASE_URL+"smarthomev5_zh.xml";
		LOAD_FILE_URL_VER_EN = BASE_URL+"smarthomev5_en.xml";
		apkPath = FileUtil.getUpdatePath()+"/"+FILE_NAME;
		splashPath = FileUtil.getSplashPath()+"/"+SPLASH_NAME;
	}

	public static synchronized UpdateManger getInstance(Context context){
		if(instance == null)
			instance = new UpdateManger(context);
		else{
			if(context != instance.getContext()){
				instance.setContext(context);
			}
		}
		return instance;
	}
	
	public NewVersionDownloadListener getNewVersionDownloadListener() {
		return downloadListener;
	}
	public void setNewVersionDownloadListener(NewVersionDownloadListener downloadListener) {
		this.downloadListener = downloadListener;
	}
	
	public ServerAppInfo getAppInfo() {
		return appInfo;
	}

	public void setAppInfo(ServerAppInfo appInfo) {
		this.appInfo = appInfo;
	}

	private void updateNewVersion(){
		
		if(isDownloaded()){
			startInstall();
		}else{
			if(!isChecked())
				return ;
			final DownloadManager downloadManager  = new DownloadManager(LOAD_PATH, apkPath);
			downloadManager.addProcessListener(new DownloadListener() {
				
				@Override
				public void processing(long totalSize, long hasDownloadSize) {
					
					int present = (int)(hasDownloadSize*100.0/totalSize);
					if(present >= 100){
						setRunning(false);
					}else{
						setRunning(true);
					}
					downloadListener.processing(present);
				}
				
				@Override
				public void processError(Exception e) {
					downloadListener.processError(e);
					setRunning(false);
				}
			});
			TaskExecutor.getInstance().execute(new Runnable() {
				
				@Override
				public void run() {
					downloadManager.startDonwLoadFile();
					
				}
			});
			
		}
		
	}
	public void checkUpdate(final boolean isAutoShowDialog){
		TaskExecutor.getInstance().execute(new Runnable()
		{
			@Override
			public void run(){
				try{
					appInfo = getServerAppInfo(getLocaleLanguage());
					if(appInfo != null){
						Logger.debug("appVersion:"+appInfo.getVersionTxts());
						int localVersionCode = preference.getAppVersion();
						if (appInfo.getVersionCode() > localVersionCode){
							preference.saveAppVersion(appInfo.getVersionCode());
						}
						if(isNewVersion()){
							if(isAutoShowDialog){
								int localVersionShowTimes = preference.getAppVersionRemindTimes(appInfo.getVersionCode());
								if (localVersionShowTimes == -1){
									preference.saveAppVersionRemindTimes(appInfo.getVersionCode(),appInfo.getRemindTimes());
									localVersionShowTimes = appInfo.getRemindTimes();
								}
								if(localVersionShowTimes > 0 ){
									handler.post(new Runnable() {
										
										@Override
										public void run() {
											showChangeVersionUpdateDialog();
										}
									});
								}
							}else{
								handler.post(new Runnable() {
									
									@Override
									public void run() {
										showChangeVersionUpdateDialog();
									}
								});
							}
							
						}
						int localImageVersion = preference.getLastSaveImageVersion();
						if(appInfo.getImgVersion() > localImageVersion){
							String imagePath = appInfo.getVersionImgs();
							if(!StringUtil.isNullOrEmpty(imagePath)){
								byte bytes[] = HttpUtil.getPicture(imagePath);
								if(bytes != null){
									Bitmap bitmap = FileUtil.Bytes2Bitmap(bytes);
									if(bitmap != null){
										boolean result = FileUtil.saveBitmapToPng(bitmap, FileUtil.getSplashPath(),SPLASH_NAME);
										if(result){
											preference.saveLastSaveImageVersion(appInfo.getImgVersion());
										}
									}
								}
							}else{
								boolean result =  FileUtil.deleteFile(splashPath);
								if(result){
									preference.saveLastSaveImageVersion(appInfo.getImgVersion());
								}
							}
						}
					}
				}
				catch (Exception e){
				}
			}
		});
	}

	public boolean isDownloaded(){
		int downloadCode = VersionUtil.getVersionCodeByAPK(mContext, apkPath);
		if(isChecked()){
			if(appInfo.getVersionCode() > downloadCode){
				return false;
			}
		}
		if(downloadCode > VersionUtil.getVersionCode(mContext)){
			return true;
		}
		return false;
	}
	public boolean isNewVersion(){
		int sysVersionCode = VersionUtil.getVersionCode(mContext);
		int appVersionCode = preference.getAppVersion();
		if(appVersionCode > sysVersionCode){
			return true;
		}
		return false;
	}
	
	public Context getContext() {
		return mContext;
	}

	public void setContext(Context mContext) {
		this.mContext = mContext;
	}

	/**
	 * <h1>提示更新对话框</h1>
	 */
	private void showChangeVersionUpdateDialog() {
		final WLDialog dialog;
		WLDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.set_account_manager_software_version_have_new_text)
				.setPositiveButton(R.string.set_account_manager_gw_version_have_new_btn_now_update)
				.setNegativeButton(R.string.set_account_manager_gw_version_have_new_btn_next)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						updateNewVersion();
					}

					@Override
					public void onClickNegative(View contentViewLayout) {
						int localVersionShowTimes = preference.getAppVersionRemindTimes(appInfo.getVersionCode());
						if(localVersionShowTimes >0)
							localVersionShowTimes = localVersionShowTimes -1;
							preference.saveAppVersionRemindTimes(appInfo.getVersionCode(),localVersionShowTimes);
					}

				});
		
		if(getAppInfo() != null && !StringUtil.isNullOrEmpty(getAppInfo().getVersionTxts())){
			
			View contentView = LayoutInflater.from(mContext).inflate(R.layout.version_update_dialog, null);
			TextView textView = (TextView)contentView.findViewById(R.id.update_tv);
			textView.setText(getAppInfo().getVersionTxts());
			builder.setContentView(contentView);
		}
		dialog = builder.create();
		dialog.show();
	}
	private String getLocaleLanguage(){
		String language = LanguageUtil.getLanguage();
		String verAddress = null;
		if (LanguageUtil.LANGUAGE_EN.equals(language)){
			verAddress = LOAD_FILE_URL_VER_EN;
		}
		else if (LanguageUtil.LANGUAGE_ZH.equals(language)){
			verAddress = LOAD_FILE_URL_VER_ZH;
		}
		else{
			verAddress = LOAD_FILE_URL_VER_EN;
		}
		return verAddress;
	}
	public ServerAppInfo getServerAppInfo( String url ) throws MalformedURLException, IOException, XmlPullParserException{
		ServerAppInfo appInfo = null;
		XmlPullParser parse = Xml.newPullParser();
		HttpURLConnection httpUrlCon = (HttpURLConnection) new URL(url).openConnection();
		httpUrlCon.setConnectTimeout(SPLASH_DISPLAY_TIME);
		httpUrlCon.setReadTimeout(SPLASH_DISPLAY_TIME);
		httpUrlCon.setUseCaches(false);
		httpUrlCon.connect();
		parse.setInput(httpUrlCon.getInputStream(), "utf-8");

		int type = parse.getEventType();
		while (type != XmlPullParser.END_DOCUMENT){
			switch (type){
				case XmlPullParser.START_TAG :
					String tag = parse.getName();
					if (tag.equalsIgnoreCase(LOAD_VERSION_KEY_ROOT)){
						appInfo = new ServerAppInfo();
					}
					else if (tag != null){
						if (tag.equalsIgnoreCase(LOAD_VERSION_KEY_CODE)){
							appInfo.setVersionCode(StringUtil.toInteger(parse.nextText()));
						}
						else if (tag.equalsIgnoreCase(LOAD_VERSION_KEY_NAME)){
							appInfo.setVersionName(parse.nextText());
						}
						else if (tag.equalsIgnoreCase(LOAD_VERSION_KEY_TXTS)){
							appInfo.setVersionTxts(parse.nextText());
						}
						else if (tag.equalsIgnoreCase(LOAD_VERSION_KEY_IMGS)){
							appInfo.setImgVersion(StringUtil.toInteger(parse.getAttributeValue(null, LOAD_VERSION_KEY_IMGS_VER)));
							appInfo.setVersionImgs(parse.nextText());
						}
						else if (tag.equalsIgnoreCase(LOAD_VERSION_REMINDTIMES)){
							appInfo.setRemindTimes(StringUtil.toInteger(parse.nextText()));
						}
						else if(tag.equalsIgnoreCase(LOAD_VERSION_KEY_MD5)){
							appInfo.setVersionMD5(parse.nextText());
						}
					}
					break;
				case XmlPullParser.END_TAG :
					if (parse.getName().equalsIgnoreCase(LOAD_VERSION_KEY_ROOT) && appInfo != null){
					}
					break;
				default :
					break;
			}
			type = parse.next();
		}
		return appInfo;
	}

	public boolean isChecked() {
		if(appInfo == null)
			return false;
		return true;
	}

	public boolean isRunning() {
		return isRunning;
	}

	public void setRunning(boolean isRunning) {
		this.isRunning = isRunning;
	}
	public void startInstall(){
		if(isMD5Right()&&isPackageNameRight()){
			IntentUtil.startInstallAPK(mContext, apkPath);
		}else{
			WLToast.showToast(mContext,mContext.getString(R.string.smarthome_app_download_hint),WLToast.TOAST_SHORT);
		}
	}

	public boolean isPackageNameRight(){
		PackageManager pm = mContext.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath, PackageManager.GET_ACTIVITIES);
		if(info != null) {
			ApplicationInfo appInfo = info.applicationInfo;
			String appName = appInfo.packageName;
			if(appName.equals("cc.wulian.smarthomev5")||appName.equals("cc.wulian.monitor")){
				return true;
			}else{
				return false;
			}
		}else {
			return false;
		}
	}

	public boolean isMD5Right(){
		File apkFile=new File(apkPath);
		String apkMD5String=getFileMD5(apkFile);
		return apkMD5String.equals(appInfo.getVersionMD5());
	}

	public interface NewVersionDownloadListener{
		public void processing(int present);
		public void processError(Exception e);
	}
	public static class ServerAppInfo
	{
		int versionCode;
		String versionName;
		String versionTxts;
		String versionImgs;
		int remindTimes;
		int imgVersion;

		public String getVersionMD5() {
			return versionMD5;
		}

		public void setVersionMD5(String versionMD5) {
			this.versionMD5 = versionMD5;
		}

		String versionMD5;

		public int getVersionCode(){
			return versionCode;
		}

		public void setVersionCode( int versionCode ){
			this.versionCode = versionCode;
		}

		public String getVersionName(){
			return versionName;
		}

		public void setVersionName( String versionName ){
			this.versionName = versionName;
		}

		public String getVersionTxts(){
			return versionTxts;
		}

		public void setVersionTxts( String versionTxts ){
			this.versionTxts = versionTxts;
		}

		public String getVersionImgs(){
			return versionImgs;
		}

		public void setVersionImgs( String versionImgs ){
			this.versionImgs = versionImgs;
		}

		public int getImgVersion(){
			return imgVersion;
		}

		public void setImgVersion( int imgVersion ){
			this.imgVersion = imgVersion;
		}

		public int getRemindTimes() {
			return remindTimes;
		}

		public void setRemindTimes(int remindTimes) {
			this.remindTimes = remindTimes;
		}
	}
	public static String getFileMD5(File file) {
		if (!file.isFile()) {
			return null;
		}
		MessageDigest digest = null;
		FileInputStream in = null;
		byte buffer[] = new byte[1024];
		int len;
		try {
			digest = MessageDigest.getInstance("MD5");
			in = new FileInputStream(file);
			while ((len = in.read(buffer, 0, 1024)) != -1) {
				digest.update(buffer, 0, len);
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		BigInteger bigInt = new BigInteger(1, digest.digest());
		return bigInt.toString(16);
	}
}
