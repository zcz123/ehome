package cc.wulian.smarthomev5.tools.configure;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import cc.wulian.smarthomev5.service.html5plus.plugins.FileDownload;
import cc.wulian.smarthomev5.service.html5plus.plugins.FileDownload.FileDownloadCallBack;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.FileUpload;
import cc.wulian.smarthomev5.utils.DateUtil;
import cc.wulian.smarthomev5.utils.FileUtil;

public class UserFileConfig {

	public static final String HEAD_ICON = "temp_head.png";
	private static UserFileConfig instance;
	private long timeout = DateUtil.MILLI_SECONDS_OF_DAY;
	private String userID = "0000";
	private String folder = "";

	private UserFileConfig() {
		this.updateFold();
	}

	private void updateFold() {
		folder = FileUtil.getUserDirectoryPath() + "/" + this.userID;
		FileUtil.isFolderExists(folder);
	}

	public void setUserID(String userID) {
		this.userID = userID;
		this.updateFold();
	}

	public static UserFileConfig getInstance() {
		if (instance == null) {
			instance = new UserFileConfig();
		}
		return instance;
	}
	
	public final String getUserPath() {
		return folder;
	}
	
	public final String getUserFile(String filename) {
		return folder + "/" + filename;
	}
	
	/**
	 * 上传配置文件到服务器
	 * 
	 * @param configFile
	 * @throws IOException
	 */
	public String uploadUserFile(String srcFolder, String fileName) {
		File srcFile = new File(srcFolder, fileName);
		String filepath = srcFile.getAbsolutePath();
		return uploadUserFile(filepath);
	}

	/**
	 * 上传配置文件到服务器
	 * 
	 * @param configFile
	 * @throws IOException
	 */
	public String uploadUserFile(String filepath) {
		String filename = filepath.substring(filepath.lastIndexOf('/') + 1);
		String url = SmarthomeFeatureImpl.getData("_FILE_ADDRESS", "") + "/upload?X-Progress-ID=" + filename;
		Map<String, String> textMap = new HashMap<String, String>();
		textMap.put("token", SmarthomeFeatureImpl.getData("token", ""));
		textMap.put("fileName", filename);
		textMap.put("reName", "false");
		Map<String, String> fileMap = new HashMap<String, String>();
		fileMap.put("filename", filepath);
		String ret = "";
		try {
			ret = FileUpload.formUpload(url, textMap, fileMap);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ret;
	}

	public void downloadFileToFolder(String fileName, FileDownloadCallBack callback) {
		File file = FileUtil.getRealFile(this.getUserPath(), fileName);
		this.downLoadFile(file.getAbsolutePath(), fileName, callback);
	}

	public void downLoadFile(String destPath, String fileName, final FileDownloadCallBack callback) {
//		if(FileUtil.checkFileExistedAndIntime(destPath, timeout)) {
//			callback.doWhatOnSuccess(destPath);
//			return;
//		}
		FileDownload downloader = new FileDownload();
		if (callback != null) {
			downloader.setDownloadCallback(callback);
		}
		downloader.formDownload(destPath, fileName);
	}

}
