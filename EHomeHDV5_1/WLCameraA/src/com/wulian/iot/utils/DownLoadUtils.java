package com.wulian.iot.utils;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.os.Handler;
import android.os.Looper;
public class DownLoadUtils  {
	private static final String TAG = "DownLoadUtils";
	private List<DownloadListener> notifies = new ArrayList<DownloadListener>();
	private String srcUrl;
	private String destUrl;
	private Handler handler = new Handler(Looper.getMainLooper());
	DownLoadUtils instance = null;
	public DownLoadUtils(DownLoadPojo downLoadPojo){
		instance = this;
		instance.setDownLoadPojo(downLoadPojo);
	}
	public DownLoadUtils(){
		instance = this;
	}
	public void addProcessListener(DownloadListener listener){
		if(listener!=null){
			instance.notifies.add(listener);
		}
	}
	public void removeListener(DownloadListener listener){
		if (listener!=null){
			instance.notifies.remove(listener);
		}
	}
	public void startDonwLoadFile(){
		HttpURLConnection httpUrlCon = null;
		InputStream is;
		FileOutputStream fos;
		try{
			httpUrlCon = (HttpURLConnection) (new URL(downLoadPojo.getSrcUrl())).openConnection();
			httpUrlCon.setConnectTimeout(2500);
			httpUrlCon.setReadTimeout(2500*2);
			httpUrlCon.setUseCaches(false);
			httpUrlCon.connect();
			is = httpUrlCon.getInputStream();
			fos = new FileOutputStream(downLoadPojo.getDestUrl());
			long totalSize = httpUrlCon.getContentLength();
			byte[] buf = new byte[1024*40];
			int readCount = -1;
			long hasReadTottalCount = 0;
			while ((readCount = is.read(buf)) != -1){
				fos.write(buf, 0, readCount);
				hasReadTottalCount += readCount;
				notifyProcess(totalSize, hasReadTottalCount);
			}
			fos.flush();
			fos.close();
			is.close();
			httpUrlCon.disconnect();
		}
		catch (FileNotFoundException e){
			e.printStackTrace();
			notifyError(e);
		}
		catch (MalformedURLException e){
			notifyError(e);
		}
		catch (IOException e){
			notifyError(e);
		}
		catch (Exception e){
			notifyError(e);
		}
		finally{
			fos = null;
			is = null;
			httpUrlCon = null;
		}
	}
	protected void notifyProcess(final long totalSize,final long hasDownloadSize ){
		handler.post(new Runnable() {
			@Override
			public void run() {
				try{
					for(DownloadListener notify : notifies){
						notify.processing(totalSize, hasDownloadSize);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	protected void notifyError(final Exception e ){
		handler.post(new Runnable() {
			
			@Override
			public void run() {
				try{
					for(DownloadListener notify : notifies){
						notify.processError(e);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	
	public interface DownloadListener{
		public void processing(long totalSize,long hasDownloadSize);
		public void processError(Exception e);
	}
	DownLoadPojo downLoadPojo = null;

	public void setDownLoadPojo(DownLoadPojo downLoadPojo) {
		if(downLoadPojo !=null){
			instance.downLoadPojo = downLoadPojo;
			instance.addProcessListener(instance.downLoadPojo.getDownloadListener());
			return;
		}
		instance.downLoadPojo = new DownLoadPojo();
	}

	public DownLoadPojo getDownLoadPojo() {
		return downLoadPojo;
	}
    public void destroy(){
		if (instance!=null){
			instance.removeListener(instance.downLoadPojo.getDownloadListener());
			instance = null;
		}
	}
	public static class DownLoadPojo{
		private String srcUrl;
		private String destUrl;
		private DownloadListener downloadListener;
        public DownLoadPojo(){

		}
		public DownLoadPojo(String srcUrl, String destUrl, DownloadListener downloadListener) {
			this.srcUrl = srcUrl;
			this.destUrl = destUrl;
			this.downloadListener = downloadListener;
		}

		public String getSrcUrl() {
			return srcUrl;
		}

		public void setSrcUrl(String srcUrl) {
			this.srcUrl = srcUrl;
		}

		public String getDestUrl() {
			return destUrl;
		}

		public void setDestUrl(String destUrl) {
			this.destUrl = destUrl;
		}

		public DownloadListener getDownloadListener() {
			return downloadListener;
		}

		public void setDownloadListener(DownloadListener downloadListener) {
			this.downloadListener = downloadListener;
		}
	}
}

