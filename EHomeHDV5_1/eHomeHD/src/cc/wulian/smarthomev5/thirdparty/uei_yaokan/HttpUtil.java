package cc.wulian.smarthomev5.thirdparty.uei_yaokan;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.StatusLine;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import android.os.SystemClock;
import android.util.Base64;


public class HttpUtil {

	private String userAgent = "(Liunx; u; Android ; en-us;Media)";

	private YkanSDKManager sdkManager;
	
	private String key = "demo.fortest1234";
	
	public HttpUtil(Context ctx) {
		sdkManager = YkanSDKManager.getInstance();
	}

	public String postMethod(String url, List<String> list) {
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>() ;
		String total = "" ;
		if(list!=null&&list.size()>0){
			for (String str : list) {
				if(!Utility.isEmpty(str)){
					 String[] kv = str.split("=");
					 if(kv.length==2){
						 //避免空指针异常
						 nameValuePairs.add(new BasicNameValuePair(kv[0], kv[1]));
						 total = total + kv[1] ;
					 }
				}
			}
		}
		String deviceId = sdkManager.getDeviceId();
		nameValuePairs.add(new BasicNameValuePair("f", deviceId));
		String appid = sdkManager.getAppId();
		nameValuePairs.add(new BasicNameValuePair("appid", appid));
		String time = SystemClock.uptimeMillis() + "" ;
		total = total + deviceId + time ;
		String auth = Encrypt.encryptSpecial(total);
		AndroidHttpClient httpClient = AndroidHttpClient.newInstance(userAgent);
		try {
			HttpPost request = new HttpPost(url);
			request.addHeader("accept-encoding", "gzip,deflate");
			request.addHeader("client", time+"_" + auth);
			HttpEntity httpEntity = new UrlEncodedFormEntity(nameValuePairs, "UTF-8");
			request.setEntity(httpEntity);
			HttpResponse response = httpClient.execute(request);
			StatusLine statusLine = response.getStatusLine();
			HttpEntity entity = response.getEntity();
			if (statusLine.getStatusCode() == HttpStatus.SC_OK) {
				byte[] srcData = EntityUtils.toByteArray(entity);
				byte[] nData = null ;
				if(!Utility.isEmpty(entity.getContentEncoding())
                        && entity.getContentEncoding().getValue().contains("gzip")){
					 nData = unzip(srcData); // 解压
				}else{
					nData = srcData ;
				}
				String iv = "testfor.demo4213";
				Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
				SecretKeySpec keyspec = new SecretKeySpec(key.getBytes(), "AES");
				IvParameterSpec ivspec = new IvParameterSpec(iv.getBytes());
				cipher.init(Cipher.DECRYPT_MODE, keyspec, ivspec);
				byte[] original = cipher.doFinal(nData);
				String originalString = new String(original, "UTF-8");
				originalString = originalString.trim(); // 源文
				Logger.d("wave", "originalString: e is " + originalString);
				return originalString;
			}
			return "";
		} catch (Exception e) {
			Logger.d("wave", "postMethod: e is " + e);
//			e.printStackTrace();
			return "";
		} finally {
			if (httpClient != null) {
				httpClient.close();
			}
		}
	}

	public byte[] unzip(byte[] srcData) throws IOException {
		InputStream inputStream = new GZIPInputStream(new ByteArrayInputStream(
				srcData));
		ByteArrayOutputStream arrayOutputStream = new ByteArrayOutputStream();
		byte[] temp = new byte[1024];
		int len = 0;
		while ((len = inputStream.read(temp, 0, 1024)) != -1) {
			arrayOutputStream.write(temp, 0, len);
		}
		arrayOutputStream.close();
		inputStream.close();
		return arrayOutputStream.toByteArray();
	}
	
	public String encrypt(String sSrc, String sKey) {
		if (sSrc == null || sKey == null) {
			return null;
		}
		try {
			sKey = new String(Base64.encode(key.getBytes("UTF-8"),Base64.DEFAULT));
			byte[] raw = Base64.decode(sKey.getBytes("UTF-8"), Base64.DEFAULT);
			SecretKeySpec skeySpec = new SecretKeySpec(raw, "AES");
			Cipher cipher = Cipher.getInstance("AES");
			cipher.init(Cipher.ENCRYPT_MODE, skeySpec);
			byte[] encrypted = cipher.doFinal(sSrc.getBytes("UTF-8"));
			return URLEncoder.encode(Base64.encodeToString(encrypted, Base64.DEFAULT),"UTF-8");
		} catch (Exception e) {
			Logger.e("wave", "error:" + e.getMessage());
		}
		return sSrc;
	}
}
