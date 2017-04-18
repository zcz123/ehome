package cc.wulian.h5plus.feature.ajax;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.webkit.WebSettings;

/**
 * Created by Administrator on 2016/2/17 0017.
 */
@SuppressLint("TrulyRandom")
public class HttpRequestEntity {
	private HttpURLConnection httpUrlConnection;
	private static SSLContext sc = null;
	static {
		try {
			sc = SSLContext.getInstance("TLS");
			sc.init(null, null, new SecureRandom());
			HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
		} catch (NoSuchAlgorithmException | KeyManagementException e) {
			throw new RuntimeException(e);
		}
	}

	@SuppressLint("NewApi")
	public void open(final Context context, String method, String url, int timeout) throws IOException {
		URL urlEntity = new URL(url);
		// 打开连接
		httpUrlConnection = (HttpURLConnection) urlEntity.openConnection();
		if ("POST".equals(method)) {
			// 设置是否向httpUrlConnection输出，因为这个是post请求，参数要放在http正文内，因此需要设为true,
			httpUrlConnection.setDoOutput(true);
			// Post 请求不能使用缓存
			httpUrlConnection.setUseCaches(false);
		}
		// 设置是否从httpUrlConnection读入，默认情况下是true;
		httpUrlConnection.setDoInput(true);

		httpUrlConnection.setConnectTimeout(timeout);
		httpUrlConnection.setRequestMethod(method);
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.JELLY_BEAN_MR1) {
			httpUrlConnection.setRequestProperty("User-Agent", WebSettings.getDefaultUserAgent(context));
		}
	}

	public void setRequestHeader(String header, String type) {
		httpUrlConnection.setRequestProperty(header, type);
	}

	public String getResponseHeader(String header) {
		return httpUrlConnection.getHeaderField(header);
	}

	public int send(String param) throws Exception {
		httpUrlConnection.connect();
		if (param != null && param.length() > 0) {
			OutputStream outStream = httpUrlConnection.getOutputStream();
			outStream.write(param.getBytes("UTF-8"));
			outStream.flush();
			outStream.close();
		}
		int responseCode = httpUrlConnection.getResponseCode();
		return responseCode;
	}

	public String getResult() throws Exception {
		StringBuffer buffer = new StringBuffer();
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(httpUrlConnection.getInputStream(), "UTF-8"));
			String line = null;
			while ((line = reader.readLine()) != null) {
				buffer.append(line);
			}
		} finally {
			if (reader != null)
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
		System.out.println(buffer.toString());
		return buffer.toString();
	}

	public void disconnect() {
		httpUrlConnection.disconnect();
	}
}
