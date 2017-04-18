package cc.wulian.smarthomev5.tools;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Handler;
import android.os.Looper;
import android.util.Xml;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.tools.DownloadManager.DownloadListener;
import cc.wulian.smarthomev5.utils.FileUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.VersionUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class UpdateCameraAPKManger
{
//	public static final String BASE_URL = "http://192.168.0.61:8080/wulian/V5/";
	public static final String BASE_URL = "https://oht2fful5.qnssl.com/";
	public static final String FILE_NAME = "smarthomemonitor.apk";
	public static final String LOAD_PATH = BASE_URL + FILE_NAME;
	public static final String LOAD_FILE_URL_VER_ZH = BASE_URL+"smarthomemonitor_zh.xml";
	public static final String LOAD_FILE_URL_VER_EN = BASE_URL+"smarthomemonitor_en.xml";
	public 	static final int 		SPLASH_DISPLAY_TIME 			= 2500;
	private static final String LOAD_VERSION_KEY_ROOT 		= "versionInfo";
	private static final String LOAD_VERSION_KEY_CODE 		= "versionCode";
	private static final String LOAD_VERSION_KEY_TXTS 		= "versionTxts";

	private Context mContext;
	private NewVersionDownloadListener downloadListener ;
	private ServerAppInfo appInfo;
	private boolean isRunning = false;
	private static UpdateCameraAPKManger instance = null;
	private String apkPath;
	private Handler handler = new Handler(Looper.getMainLooper());
	private int smartHomeMonitorAppVersionCode=0;
	private int smartHomeMonitorApkVersionCode=1;
	private int smartHomeMonitorserverApkVersionCode=1;
	
	private UpdateCameraAPKManger(Context context)
	{
		this.mContext = context;
		apkPath = FileUtil.getUpdatePath()+"/"+FILE_NAME;
	}

	public static synchronized UpdateCameraAPKManger getInstance(Context context){
		if(instance == null)
			instance = new UpdateCameraAPKManger(context);
		else{
			if(context != instance.getContext()){
				instance.setContext(context);
			}
		}
		return instance;
	}
	
    /**
    * @Title: isIcamAppInstalled 
    * @Description: TODO(判断是否存在SmartHomeMonitor软件) 
    * @return boolean    返回类型 
    * @throws
     */
	public boolean isIcamAppInstalled() {
		PackageInfo packageInfo;
		try {
			packageInfo = mContext.getPackageManager().getPackageInfo(
					"cc.wulian.monitor", 0);
		} catch (NameNotFoundException e) {
			packageInfo = null;
			e.printStackTrace();
		}
		if (packageInfo == null) {
			// System.out.println("没有安装");
			return false;
		} else {
			// System.out.println("已经安装");
			smartHomeMonitorAppVersionCode = packageInfo.versionCode;
			return true;
		}
	}
	
	/**
	* @Title: hasSmartHomeMonitorApk 
	* @Description: TODO(   * 获取是否本地存在SmartHomeMonitorApk   ) 
	* @param @return    设定文件 
	* @return boolean    本地是否存在apk 
	* @throws
	 */
	public boolean hasSmartHomeMonitorApk() {
		boolean hasApk = false;
		PackageManager pm = mContext.getPackageManager();
		PackageInfo info = pm.getPackageArchiveInfo(apkPath,PackageManager.GET_ACTIVITIES);
		if (Preference.getPreferences().getInt(
				IPreferenceKey.P_CAMERA_APK_DOWNLOAD_COMPLETE, 0) == 100) {
			if (info != null) {
				hasApk = true;
				String version = info.versionName; // 得到版本信息
				smartHomeMonitorApkVersionCode = info.versionCode;
			}
		}
		return hasApk;
	}
	/**
	 * 
	* @Title: isTheNewestVersionCode 
	* @Description: TODO(判断当前版本是否为最新版本) 
	* @return boolean    是否是最新版本 
	* @throws
	 */
	public boolean isTheNewestVersionCode(int nowCode) {
		boolean isTheNewestCode = false;
		if (nowCode>=smartHomeMonitorserverApkVersionCode) {
			isTheNewestCode = true;
		}
		return isTheNewestCode;
	}
	public int getsmartHomeMonitorApkVersionCode(){
		return smartHomeMonitorApkVersionCode;
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
	public void setSeverAppInfo(){
		TaskExecutor.getInstance().execute(new Runnable()
		{
			@Override
			public void run(){
				try{
					appInfo = getServerAppInfo(getLocaleLanguage());
					smartHomeMonitorserverApkVersionCode = appInfo.versionCode;
					if(appInfo != null){
					}
				}
				catch (Exception e){
				}
			}
		});
	}
	
	public void checkUpdate(final CameraInfo info){
		TaskExecutor.getInstance().execute(new Runnable()
		{
			@Override
			public void run(){
				try{
//					appInfo = getServerAppInfo(getLocaleLanguage());
					if(appInfo != null){
						if(!isTheNewestVersionCode(smartHomeMonitorAppVersionCode)){
							handler.post(new Runnable() {
								
								@Override
								public void run() {
									showChangeVersionUpdateDialog(info);
								}
							});
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
			if(smartHomeMonitorserverApkVersionCode > downloadCode){
				return false;
			}
			else{
				return true;
			}
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
		 * 
		* @Title: showChangeVersionUpdateDialog 
		* @Description: TODO(显示dialog，让用户选择是否下载最新的apk) 
		* @throws
		 */
	public void showChangeVersionUpdateDialog(final CameraInfo info) {
		final WLDialog dialog;
		WLDialog.Builder builder = new Builder(mContext);
		builder.setTitle(R.string.download);
		builder.setPositiveButton(R.string.download);
		if(isIcamAppInstalled()){
			builder.setTitle(R.string.set_account_manager_software_version_have_new_text);
			builder.setPositiveButton(R.string.set_account_manager_gw_version_have_new_btn_now_update);
		}
		builder.setNegativeButton(R.string.set_account_manager_gw_version_have_new_btn_next)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						updateNewVersion();
					}

					@Override
					public void onClickNegative(View contentViewLayout) {
						if(isIcamAppInstalled()){
						jumpToSmartHomeMonitorApplication(info);
						}
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
	/**
	* @Title: jumpToSmartHomeMonitorApplication 
	* @Description: TODO(跳转至摄像机独立APP) 
	 */
	public void jumpToSmartHomeMonitorApplication(
			final CameraInfo info) {
		Intent intent = new Intent(Intent.ACTION_MAIN);
		intent.addCategory(Intent.CATEGORY_LAUNCHER);
//		ComponentName cn = null;
		switch (info.getCamType()) {
		case 1:
			intent.setClassName("cc.wulian.monitor", "cc.wulian.monitor.activity.MonitorTTActivity");
		break;
		case 4:
			intent.setClassName("cc.wulian.monitor", "cc.wulian.monitor.activity.MonitorHCActivity");
			break;
		case 8:
			intent.setClassName("cc.wulian.monitor", "cc.wulian.monitor.activity.MonitorHCActivity");
			break;
		case 11:
			intent.setClassName("cc.wulian.monitor", "cc.wulian.monitor.activity.MonitorTKActivity");
			break;
		case 12:
			intent.setClassName("cc.wulian.monitor", "cc.wulian.monitor.activity.MonitorVSActivity");
			break;
		case 13:
			intent.setClassName("cc.wulian.monitor", "cc.wulian.monitor.activity.MonitorSTActivity");
			break;
		default:
			intent.setClassName("cc.wulian.monitor", "cc.wulian.monitor.activity.MonitorTTActivity");
			break;
		}
		if(StringUtil.isNullOrEmpty(info.host)){
			info.setHost(AccountManager.getAccountManger().getmCurrentInfo().getGwSerIP());
		}
		intent.putExtra(CameraInfo.CAMERA_KEY_UID, info.getUid());
		intent.putExtra(CameraInfo.CAMERA_KEY_PASS, info.getPassword());
		intent.putExtra(CameraInfo.CAMERA_KEY_HOST, info.getHost());
		intent.putExtra(CameraInfo.CAMERA_KEY_PORT, info.getPort()+"");
		intent.putExtra(CameraInfo.CAMERA_KEY_CAMERATYPE, info.getCamType()+"");
		intent.putExtra(CameraInfo.CAMERA_KEY_USERNAME, info.getUsername());
		intent.putExtra(CameraInfo.CAMERA_KEY_CHANNEL, info.getChannel()+"");
		mContext.startActivity(intent);
	}
	/**
	 * 
	* @Title: getLocaleLanguage 
	* @Description: TODO(获取本地化语言，以选择下载文件所用的服务器地址) 
	* @return String    服务器地址
	* @throws
	 */
	public String getLocaleLanguage(){
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
	/**
	 * 
	* @Title: needUpdateOrDownload 
	* @Description: TODO(判断是否需要更新或下载) 
	* @return boolean    是否需要更新或下载
	* @throws
	 */
	public boolean needUpdateOrDownload() {
		boolean isNeedUpdateOrDownload = true;
		if(isIcamAppInstalled()){
			if(isTheNewestVersionCode(smartHomeMonitorAppVersionCode)){//是最新的app，无需下载
				isNeedUpdateOrDownload = false;
			}
			else{//是最新的app，提醒用户下载最新；
				isNeedUpdateOrDownload = true;
			}
		}
		return isNeedUpdateOrDownload;
	}
//	/**
//	* @Title: wantToUpdateOrDownload 
//	* @Description: TODO(判断用户是否选择下载更新) 
//	* @return boolean    用户是否选择下载更新； 
//	 */
//	public boolean wantToUpdateOrDownload(){
//		showChangeVersionUpdateDialog();
//		return wantToUpdate ;
//	}
	/**
	 * 
	* @Title: getServerAppInfo 
	* @Description: TODO(获取服务器上安装包相应数据) 
	* @param @param url
	* @return ServerAppInfo    服务器上安装包实体类 
	* @throws
	 */
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
						else if (tag.equalsIgnoreCase(LOAD_VERSION_KEY_TXTS)){
							appInfo.setVersionTxts(parse.nextText());
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
		smartHomeMonitorserverApkVersionCode = appInfo.versionCode;
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
		IntentUtil.startInstallAPK(mContext, apkPath);
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
		int imgVersion;

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
	}
}
