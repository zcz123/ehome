package cc.wulian.smarthomev5.tools.configure;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cc.wulian.smarthomev5.service.html5plus.plugins.FileDownload;
import cc.wulian.smarthomev5.service.html5plus.plugins.FileDownload.FileDownloadCallBack;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.FileUpload;

public class ConfigureFileManager {

	private static ConfigureFileManager instance;

	private ConfigureFileManager() {
	}

	public static ConfigureFileManager getInstance() {

		if (instance == null) {
			synchronized (ConfigureFileManager.class) {
				if (instance == null) {
					instance = new ConfigureFileManager();
				}
			}
		}
		return instance;
	}

	/**
	 * 下载配置文件
	 * @throws IOException 
	 */
//	public void downLoadConfigureFile(String storageFolder,String fileName, FileDownloadCallBack callback){
//		FileDownload downloader = new FileDownload();
//		if (callback != null) downloader.setDownloadCallback(callback);
//		File file=new File(storageFolder,fileName);
//		downloader.formDownload(file.getAbsolutePath(),fileName);
//	}

	/**
	 * 上传配置文件到服务器
	 * 
	 * @param configFile
	 */
//	public void uploadConfigureFile(String srcFolder, String fileName) {
//		try {
//			File srcFile = new File(srcFolder, fileName);
//			if (!srcFile.exists()) return;
//			String filepath = srcFile.getAbsolutePath();
//			String url = SmarthomeFeatureImpl.getData("_FILE_ADDRESS", "") + "/upload?X-Progress-ID=" + fileName;
//			Map<String, String> textMap = new HashMap<String, String>();
//			textMap.put("token", SmarthomeFeatureImpl.getData("token", ""));
//			textMap.put("fileName", fileName);
//			textMap.put("reName", "false");
//			Map<String, String> fileMap = new HashMap<String, String>();
//			fileMap.put("filename", filepath);
//			FileUpload.formUpload(url, textMap, fileMap);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//	}

	
//	public class ConfigFileInfo {
//
//		private String name;
//		private String version;
//		private String path;
//
//		public String getName() {
//			return name;
//		}
//
//		public void setName(String name) {
//			this.name = name;
//		}
//
//		public String getVersion() {
//			return version;
//		}
//
//		public void setVersion(String version) {
//			this.version = version;
//		}
//
//		public String getPath() {
//			return path;
//		}
//
//		public void setPath(String path) {
//			this.path = path;
//		}
//
//	}

}
