package cc.wulian.smarthomev5.service.html5plus.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.tools.DownloadManager;
import cc.wulian.smarthomev5.tools.DownloadManager.DownloadListener;
import cc.wulian.smarthomev5.utils.FileUtil;
import cc.wulian.smarthomev5.utils.TargetConfigure;
import cc.wulian.smarthomev5.utils.URLConstants;
import cc.wulian.smarthomev5.view.UpdateProcessDialog;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class PluginsManager {
	
	public static final String pluginManagerConfigFileName="plugin_config.cfg";
	public static final String TAG = "h5plugin";

	private  String pluginRootFolder;
	
	private  String pluginsFolder;

	private  String pluginsFolderTemp;
	
	private static PluginsManager instance;
	
	private Map<String,PluginModel> pluginsMap;
	
	private Handler handler=new Handler(Looper.getMainLooper());

	private Context context;
	
	private String tempFolder; //临时目录
	
	private PluginsManager(){
		pluginsFolder=URLConstants.getPluginRootFold();
		pluginsFolderTemp=URLConstants.getPluginRootFold_Temp();
		pluginRootFolder = pluginsFolder;
		tempFolder=FileUtil.getTempDirectoryPath();
		pluginsMap=getAllPlugins(pluginRootFolder, pluginManagerConfigFileName);
	}
	
	public static PluginsManager getInstance(){
		if(instance==null)instance=new PluginsManager();
		return instance;		
	}
	
	/**
	 * 查找插件
	 * @param pluginName
	 * @return
	 */
	public PluginModel searchPuglin(String pluginName){
		if(pluginsMap.containsKey(pluginName)){
			return pluginsMap.get(pluginName);
		}
		return null;
	}
	
	/**
	 * 修改配置文件
	 */
	private void  modifyConfigurationFile(){
		
		PrintWriter writer=null;
		File config=new File(pluginRootFolder,pluginManagerConfigFileName);
		if(!config.exists()){
			try {
				config=FileUtil.getRealFile(pluginRootFolder,pluginManagerConfigFileName);
				config.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		try {
			writer=new PrintWriter(new OutputStreamWriter(new FileOutputStream(config,false)));
			JSONArray jsonArray=new JSONArray();
			Iterator<Entry<String, PluginModel>> iterator = pluginsMap.entrySet().iterator();
			while(iterator.hasNext()){
				PluginModel model=iterator.next().getValue();
				JSONObject jsonObj=parsePluginModel2JsonObj(model);
				jsonArray.add(jsonObj);
			}
			writer.println(jsonArray.toJSONString());
			writer.flush();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}finally{
			if(writer!=null)writer.close();
		}
	}
	
	/**
	 * 读取配置文件 获取所有插件的信息
	 * @return
	 */
	private Map<String,PluginModel>  getAllPlugins(String storageFolder,String filename){		
		Map<String,PluginModel> plugins=new HashMap<String,PluginModel>();
		File config=new File(storageFolder,filename);
		if(!config.exists()){
			config=FileUtil.getRealFile(storageFolder,filename);
			try {
				config.createNewFile();
				return plugins;
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		BufferedReader reader = null;
		StringBuilder configJsonString=new StringBuilder();
		try {
			reader=new BufferedReader(new InputStreamReader(new FileInputStream(config)));
			String line=null;
			while((line=reader.readLine())!=null){
				configJsonString.append(line);
			}
			String contentText=configJsonString.toString();
			if(contentText==null||contentText.length()==0){
				return plugins;
			}
			JSONArray jsonArray= JSONObject.parseArray(configJsonString.toString());
			if(jsonArray!=null){
				for(int i=0;i<jsonArray.size();i++){
					PluginModel model=parsePluginString2Model(jsonArray.getString(i));
					plugins.put(model.getName(), model);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				if(reader!=null)reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return plugins;
	}
	
	/**
	 * 往插件管理文件中添加一条插件信息
	 * @param model
	 */
	public void addPluginConfigInfo(PluginModel model){
		if(model==null)return;
		if(pluginsMap.containsKey(model.getName())){
			pluginsMap.remove(model.getName());
		}
		pluginsMap.put(model.getName(),model);
		modifyConfigurationFile();
	}
	
	/**
	 * 将插件实例解析成jsonObject
	 * @param model
	 * @return
	 */
	private JSONObject parsePluginModel2JsonObj(PluginModel model){
		if(model==null)return null;
		JSONObject obj=new JSONObject();
		obj.put(PluginModel.NAME, model.getName());
		obj.put(PluginModel.VERSION, model.getVersion());
		obj.put(PluginModel.FOLDER, model.getFolder());	
		obj.put(PluginModel.ENTITY, model.getEntry());
		return obj;
	}
	
	/**
	 * 将jsonString解析称插件对象
	 * @param jsonString
	 * @return
	 */
	private PluginModel parsePluginString2Model(String jsonString){
		if(jsonString==null||jsonString.length()==0)return null;
		JSONObject jsonObj=JSONObject.parseObject(jsonString);
		PluginModel plugin=new PluginModel();
		plugin.setName(jsonObj.getString(PluginModel.NAME));
		plugin.setVersion(jsonObj.getString(PluginModel.VERSION));
		if(jsonObj.getString(PluginModel.ROOT_FOLDER)==null){ 
			//本地的没有PluginModel.ROOT_FOLDER
			plugin.setFolder(jsonObj.getString(PluginModel.FOLDER));
		}else{
			//从服务器获取的有PluginModel.ROOT_FOLDER
			plugin.setFolder(this.pluginsFolder+jsonObj.getString(PluginModel.ROOT_FOLDER));
		}
		plugin.setEntry(jsonObj.getString(PluginModel.ENTITY));
		return plugin;
	}
	
	/**
	 * 下载插件的配置文件并解析成model
	 * @param pluginName
	 * @return
	 */
	private PluginModel getPluginConfigFileModel(String pluginName){
		String noCache="?_="+(long)(Math.random()*100000); //尝试去除CDN 缓存
		StringBuffer url=new StringBuffer();
		url.append(URLConstants.PLUGIN_SERVER_URL).append(getPluginConfigFileName(pluginName)).append(noCache);
		
		BasicHttpParams params=new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, 2000);
		HttpConnectionParams.setSoTimeout(params, 4000);
		HttpGet get=new HttpGet(url.toString());
		HttpClient client=new DefaultHttpClient(params);
		BufferedReader reader=null;
		try {
			HttpResponse response=client.execute(get);
			if(response.getStatusLine().getStatusCode()==200){
				reader=new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
				StringBuffer configJsonString=new StringBuffer();
				String line="";
				while((line=reader.readLine())!=null){
					configJsonString.append(line);
				}
				return parsePluginString2Model(configJsonString.toString());
			}
		}catch (Exception e) {
//			e.printStackTrace();
			if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
				Log.e(TAG, "", e);
			}
		}finally{
			if(reader!=null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		return null;
	}
	
	/**
	 * 获取插件的配置信息，以便对比插件是否更新
	 * @param localconfigModel
	 * @param serverConfigModel
	 * @return
	 */
	private PluginState pluginVersionComparison(PluginModel localconfigModel,PluginModel serverConfigModel){
		if(localconfigModel==null){
			return PluginState.NOTEXIST;
		}else if(serverConfigModel!=null&&StringUtil.toDouble(localconfigModel.getVersion())<StringUtil.toDouble(serverConfigModel.getVersion())){
			return PluginState.UPDATE;
		}else if(serverConfigModel!=null){
			return PluginState.NORMAL;
		}
		return null;
	}
	
	/**
	 *  显示提示对话框
	 * @param title 对话框标题
	 * @param message
	 * @param listener 对话框动作监听器
	 */	
	private void  showHintDialog(final String title,final String message,final DialogActionListener listener){
		handler.post(new Runnable() {
			@Override
			public void run() {
				try{
					final WLDialog.Builder builder = new WLDialog.Builder(context);

					builder.setTitle(title).setMessage(message)
					.setPositiveButton(android.R.string.ok)
					.setNegativeButton(android.R.string.cancel)
					.setListener(new MessageListener() {
						@Override
						public void onClickPositive(View contentViewLayout) {
							listener.onClickPositive(builder.create());
						}
						@Override
						public void onClickNegative(View contentViewLayout) {
							listener.onClickNegative(builder.create());
						}
					});

                    if(PluginsManager.this.isShowProcess){
                        final WLDialog dialog = builder.create();
                        dialog.show();
                    }else {//如果在没有标题（即更新的状态），则直接执行“确定”要执行的操作
                        listener.onClickPositive(builder.create());
                        if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
                        	Log.d(PluginsManager.class.getName(), "更新插件...");
                        }

                    }
				}catch(Exception e){
					e.printStackTrace();
					handler.removeCallbacks(this);
				}
			}
		});
	}
	
	/**
	 * 获取下载进度监听器
	 * @return
	 */
	private DownLoadProgressListener getProgressListener(){

		final UpdateProcessDialog dialog=new UpdateProcessDialog(context);
		if(PluginsManager.this.isShowProcess){
			dialog.show();
		}
		DownLoadProgressListener progressListener=new DownLoadProgressListener(){

			@Override
			public void onBeginDownload(long totalSize) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						dialog.setProgess(0);
					}
				});
			}
			@Override
			public void onDownloading(final long totalSize,final long progress) {
				handler.post(new Runnable() {
					@Override
					public void run() {
						dialog.setProgess((int) (progress*100.0/totalSize));
					}
				});
			}
			@Override
			public void onDownloadFinish() {
				handler.post(new Runnable() {
					@Override
					public void run() {
						dialog.dismiss();
					}
				});
			}
		};
		return progressListener;
	}
	
	/**
	 * 获取插件的配置文件名
	 * @param pluginName  插件全名
	 * @return
	 */
	private String getPluginConfigFileName(String pluginName) {
		// 插件配置文件的名称
		final StringBuffer configFileName = new StringBuffer();
		int index = pluginName.lastIndexOf(".");
		if (index == -1)
			index = pluginName.length();
		configFileName.append(pluginName.substring(0, index));
		configFileName.append(".json");
		return configFileName.toString();
	}
	private int runSuccessCount=0;//执行onGetPluginSuccess的次数
	private boolean isShowProcess=true;
	private PluginModel serverConfigModel=null;
	/**
	 * 获取插件对象
	 * @param context
	 * @param pluginName
	 * @param getPluginCallback  插件是否获取成功回调
	 */
	public synchronized void getHtmlPlugin(final Context context,final String pluginName,final PluginsManagerCallback getPluginCallback){
		getHtmlPlugin(context,pluginName,true,getPluginCallback);
	}

	public synchronized void getHtmlPlugin(final Context context,final String pluginName,final boolean isCloseActivity,final PluginsManagerCallback getPluginCallback){
		this.context=context;
		final String downPath=getDonwPath(pluginName);
		//查询是否存在需要的插件
		final PluginModel localconfigModel=searchPuglin(pluginName);
		PluginsManager.this.runSuccessCount=0;
		PluginsManager.this.isShowProcess=false;
		boolean copyIsEnd=copyPluginsFromTemp(pluginName);//先从临时目录中copy
		if(copyIsEnd){
			if(getPluginCallback!=null&&localconfigModel!=null){
				getPluginCallback.onGetPluginSuccess(localconfigModel);
				this.runSuccessCount++;
			}
		}
		serverConfigModel=null;
		PluginState state=null;
		if(localconfigModel==null){
			state=PluginState.NOTEXIST;
		}else {
			serverConfigModel=getPluginConfigFileModel(pluginName);
			state=pluginVersionComparison(localconfigModel,serverConfigModel);
		}

		if(state==null){
			if(getPluginCallback!=null)
				getPluginCallback.onGetPluginFailed(context.getString(R.string.plugin_download_failed));
		}else if(state==PluginState.NORMAL){
			if(getPluginCallback!=null&&this.runSuccessCount==0)
				getPluginCallback.onGetPluginSuccess(localconfigModel) ;
			this.runSuccessCount++;
		}else{
			String title="",message="";
			if(state==PluginState.NOTEXIST){
				PluginsManager.this.isShowProcess=true;
				title=context.getString(R.string.plugin_download_plugin);
				message=context.getString(R.string.plugin_download_plugin_hint);
			}else if(state==PluginState.UPDATE){//更新改为后台更新，不进行提醒
//				title=context.getString(R.string.plugin_upload_plugin);
//				message=context.getString(R.string.plugin_upload_plugin_hint);
				PluginsManager.this.isShowProcess=false;
			}

			showHintDialog(title,message,new DialogActionListener() {
				@Override
				public void onClickPositive(Dialog dialog) {
					dialog.dismiss();

					final DownLoadProgressListener progressListener=getProgressListener();
					new Thread(new Runnable() {
						@Override
						public void run() {
							if(serverConfigModel==null){
								serverConfigModel=getPluginConfigFileModel(pluginName);
							}
							downLoadPlugin(tempFolder,pluginName,progressListener,new DownLoadListener(){
								@Override
								public void onSuccess(File discFile) {
									Log.d(PluginsManager.class.getName(), "onSuccess: discFile="+discFile);
									//下载成功后解压并删除
									try {

										FileUtil.unZipFile(discFile,downPath);
										addPluginConfigInfo(serverConfigModel);
										discFile.delete();
										if(getPluginCallback!=null&&PluginsManager.this.runSuccessCount==0)
											getPluginCallback.onGetPluginSuccess(serverConfigModel) ;

									} catch (Exception e) {
										e.printStackTrace();
									}
								}
								@Override
								public void onFailed() {
									if(getPluginCallback!=null)getPluginCallback.onGetPluginFailed(context.getString(R.string.plugin_download_failed));
								}
							});
						}
					}).start();
				}
				@Override
				public void onClickNegative(Dialog dialog) {
					if(isCloseActivity){
						((Activity)context).finish();
					}
				}
			});
		}
	}
	
	/**
	 * 下载文件
	 * @param destPath 下载到那个文件夹下
	 * @param pluginName  插件全名
	 * @param progressListener  下载进度监听器
	 * @param downloadListener  监听下载是否成功
	 */
	public void downLoadPlugin(final String destPath,final String pluginName,final DownLoadProgressListener progressListener,final DownLoadListener downloadListener){
		
		String noCache="?_="+(long)(Math.random()*100000); //尝试去除CDN 缓存
		StringBuffer url=new StringBuffer();
		url.append(URLConstants.PLUGIN_SERVER_URL).append(pluginName).append(noCache);
		final File discFile=new File(destPath,pluginName);
		DownloadManager manager=new DownloadManager(url.toString(),discFile.getAbsolutePath());
		manager.addProcessListener(new DownloadListener() {
			
			@Override
			public void processing(long totalSize, long hasDownloadSize) {
				progressListener.onDownloading(totalSize, hasDownloadSize);
				if(totalSize==hasDownloadSize){
					progressListener.onDownloadFinish();
					downloadListener.onSuccess(discFile);
				}
			}
			
			@Override
			public void processError(Exception e) {
				e.printStackTrace();
				progressListener.onDownloadFinish();
				downloadListener.onFailed();
			}
		});
		manager.startDonwLoadFile();
	}
	
	/**
	 * 本地插件状态
	 */
	public enum PluginState{
		NORMAL, //本地存在 ，不需要更新
		UPDATE, //需要更新
		NOTEXIST //本地不存在
	}
	
	/**
	 * Dialog动作监听
	 */
	public interface DialogActionListener {

		public void onClickPositive(Dialog dialog);

		public void onClickNegative(Dialog dialog);
	}
	
	/**
	 * 下载进度监听
	 */
	public interface DownLoadProgressListener{
		
		public void onBeginDownload(long totalSize);
		
		public void onDownloading(long totalSize,long progress);
		
		public void onDownloadFinish();
	}
	
	/**
	 * 下载监听器
	 */
	public interface DownLoadListener{
		
		public void onSuccess(File destfile);
		
		public void onFailed();
	}
	
	/**
	 * 插件管理类的接口
	 */
	public interface PluginsManagerCallback{
		public void onGetPluginSuccess(PluginModel model);
		public void onGetPluginFailed(String hint);
	}

	/**
	 * 把插件从临时区复制到正式使用的区域
	 * @param pluginsName
	 * @return  copy操作是否已结束
     */
	public boolean copyPluginsFromTemp(String pluginsName){
		boolean copyIsEnd=false;
		String pluginsDirName=pluginsName.replace(".zip","");
		String tempDirPath=pluginsFolderTemp+pluginsDirName;
		String markDirPath=pluginsFolder+pluginsDirName;
		File filetemp=new File(tempDirPath);
        File fileMark=new File(markDirPath);
		if(filetemp.exists()){
			FileUtil.copyFolder(tempDirPath,markDirPath);
			if(fileMark.exists()){
				FileUtil.delFolder(tempDirPath);//如果已经从临时区复制成功，那么把临时的删除掉
			}
		}
        //如果已存在，认为已copy完毕
        if(fileMark.exists()){
            copyIsEnd=true;
        }
		return copyIsEnd;
	}

	/**
	 * 获取下载目录路径
	 * @return
     */
	public String getDonwPath(String pluginsName){
		String pluginsDirName=pluginsName.replace(".zip","");
		String downPath="";
		/*
		* 如果plugins中已经有目录插件，则下载到临时目录，否则下载到plugins中
		* */
		String markDirPath=pluginsFolder+pluginsDirName;
		File fileMark=new File(markDirPath);
		if(fileMark.exists()){
			downPath=pluginsFolderTemp;
		}else {
			downPath=pluginsFolder;
		}
		return downPath;
	}
}
