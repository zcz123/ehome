package cc.wulian.smarthomev5.tools;

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

public class DownloadManager  {

	private List<DownloadListener> notifies = new ArrayList<DownloadListener>();
	private String srcUrl;
	private String destUrl;
	private Handler handler = new Handler(Looper.getMainLooper());
	
	public DownloadManager(String srcUrl, String destUrl) {
		super();
		this.srcUrl = srcUrl;
		this.destUrl = destUrl;
	}
	public void addProcessListener(DownloadListener listener){
		this.notifies.add(listener);
	}
	public void removeListener(DownloadListener listener){
		this.notifies.remove(listener);
	}
	public void startDonwLoadFile(){
		HttpURLConnection httpUrlCon = null;
		InputStream is;
		FileOutputStream fos;
		try{
			httpUrlCon = (HttpURLConnection) (new URL(srcUrl)).openConnection();
			httpUrlCon.setConnectTimeout(2500);
			httpUrlCon.setReadTimeout(2500*2);
			httpUrlCon.setRequestProperty("Accept-Encoding", "identity");
			httpUrlCon.setUseCaches(false);
			httpUrlCon.connect();

			is = httpUrlCon.getInputStream();
			fos = new FileOutputStream(destUrl);

			long totalSize = httpUrlCon.getContentLength();
			byte[] buf = new byte[1024*20];
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
}
