package cc.wulian.smarthomev5.service.html5plus.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;

public class FileDownload {

	private FileDownloadCallBack downloadCallback;

//	public void formDownload(final String drcPath) {
//		String urlString = checkFileExist(drcPath); // 判断服务器是否有该文件
//		if (urlString == null) {
//			if (downloadCallback != null)
//				downloadCallback.doWhatOnFailed(new Exception(MainApplication.getApplication().getString(R.string.html_user_hint_file_not_exist)));
//			return;
//		}
//		InputStream is;
//		FileOutputStream fos;
//		try {
//			URL url = new URL(urlString);
//			HttpURLConnection conn = (HttpURLConnection) url
//					.openConnection();
//			conn.setRequestMethod("GET");
//			conn.setConnectTimeout(5000);
//			int code = conn.getResponseCode();
//			if (code == 200) {
//				File drcFile = new File(drcPath);
//				if(!drcFile.exists())drcFile.createNewFile();
//				is = conn.getInputStream();
//				fos = new FileOutputStream(drcFile);
//				byte[] buffer = new byte[1024];
//				int readLength = 0;
//				while ((readLength = is.read(buffer)) != -1) {
//					fos.write(buffer, 0, readLength);
//				}
//				fos.close();
//				is.close();
//				if (downloadCallback != null)
//					downloadCallback.doWhatOnSuccess(drcPath);
//			} else {
//				if (downloadCallback != null)
//					downloadCallback.doWhatOnFailed(new Exception(
//							MainApplication.getApplication().getString(R.string.html_user_hint_file_not_exist)));
//			}
//		} catch (IOException e) {
//			if (downloadCallback != null)
//				downloadCallback.doWhatOnFailed(e);
//		}
//	}

	public void formDownload(final String drcPath,final String fileName) {
		String token = SmarthomeFeatureImpl.getData("token", "");
		String urlString = SmarthomeFeatureImpl.getData("_FILE_ADDRESS", "")+ "/download.php?token=" + token+"&fileName="+fileName;
		InputStream is=null;
		FileOutputStream fos=null;
		try {
			URL url = new URL(urlString);
			HttpURLConnection conn = (HttpURLConnection) url .openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			int code = conn.getResponseCode();
			if (code == 200) {
				File drcFile = new File(drcPath);
				if(!drcFile.exists())drcFile.createNewFile();
				is = conn.getInputStream();
				fos = new FileOutputStream(drcFile);
				byte[] buffer = new byte[1024*1024];
				int readLength = 0;
				while ((readLength = is.read(buffer)) != -1) {
					fos.write(buffer, 0, readLength);
				}
				fos.flush();
				if (downloadCallback != null) downloadCallback.doWhatOnSuccess(drcPath);
			} else {
				if (downloadCallback != null)downloadCallback.doWhatOnFailed(new Exception(MainApplication.getApplication().getString(R.string.home_network_error_hint)));
			}
		} catch (IOException e) {
			if (downloadCallback != null)downloadCallback.doWhatOnFailed(e);
		}finally{
			try {
				if(fos!=null)fos.close();
				if(is!=null)is.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private String checkFileExist(String drcPath) {

		String result = null;
		String token = SmarthomeFeatureImpl.getData("token", "");
		String fileSuffix = drcPath.substring(drcPath.lastIndexOf('.') + 1);
		String urlText = SmarthomeFeatureImpl.getData("_FILE_ADDRESS", "")
				+ "/getfileinfo.php?fileSuffix=" + fileSuffix + "&isNew=true"
				+ "&token=" + token;
		try {
			URL url = new URL(urlText);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(5000);
			int code = conn.getResponseCode();
			StringBuffer buff = new StringBuffer();
			String data = null;
			if (code == 200) {
				BufferedReader reader = new BufferedReader(
						new InputStreamReader(conn.getInputStream()));
				while ((data = reader.readLine()) != null) {
					buff.append(data);
				}
			}
			String[] resultArray = buff.toString().split(",");
			if (resultArray.length < 3)
				return null;
			result = resultArray[2];
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (result != null && result.contains("fileFullName:")) {
			result = result.replace("fileFullName:", "").replace("\"}", "");
		} else {
			result = null;
		}
		return result;
	}

	public void setDownloadCallback(FileDownloadCallBack callback) {
		this.downloadCallback = callback;
	}

	public interface FileDownloadCallBack {
		public void doWhatOnSuccess(String path);
		public void doWhatOnFailed(Exception e);
	}

}
