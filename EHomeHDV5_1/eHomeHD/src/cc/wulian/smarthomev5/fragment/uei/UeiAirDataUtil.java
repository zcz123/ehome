package cc.wulian.smarthomev5.fragment.uei;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.text.MessageFormat;

import org.apache.http.entity.StringEntity;

import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.FileUtil;
import cc.wulian.smarthomev5.utils.TargetConfigure;

/**
 * 为获取UEI-空调码库提供方法
 * 
 * @author yuxiaoxuan
 * @date 2016年7月19日13:32:08
 */
public class UeiAirDataUtil {
	public UeiAirDataUtil() {
		ueiOnline = new UeiOnlineUtil();
	}
//	private String dirPath_airData = "file://" + Environment.getExternalStorageDirectory().getPath() + "/wulian/airdata/";
	private String TAG="UeiAirDataUtil";
	private String dirPath_airData="";
//	private String url_ueiRc = "http://74.209.252.205/QuickSetLite.svc/CreateUser";
//	private String url_getPicks = "http://74.209.252.205/QuickSetLite.svc/GetPicks";

	private String url_ueiRc = "http://www.ueiwsp.com/QuickSetLite.svc/CreateUser";
	private String url_getPicks = "http://www.ueiwsp.com/QuickSetLite.svc/GetPicks";
	private String deviceType = "Z";
	private UeiOnlineUtil ueiOnline;
	private String brandCode = "";
	private boolean isSaveFile=true;
    private boolean isFirstReadLocal=true;
	private String devID;
	public void setdevID(String devID){
		this.devID=devID;
	}
    public void setIsFirstReadLocal(boolean firstReadLocal){
    	this.isFirstReadLocal=firstReadLocal;
    }
	public void setIsSvaeFile(boolean isSaveFile){
		this.isSaveFile=isSaveFile;
	}

	/**
	 * 获取空调码
	 * 
	 * @param brandCode
	 *            空调品牌编码
	 * @return
	 */
	public String getAirData(String brandCode) {
		this.brandCode = brandCode;
		dirPath_airData=FileUtil.getUeiAirDataPath();
		String airData = readAirDataFromLocalFile();//1.先读取本地的
		if(!StringUtil.isNullOrEmpty(airData)){
			return airData;
		}		
		String ueiRc=getUeiRc();//2.执行CreateUser
		if(StringUtil.isNullOrEmpty(ueiRc)){
			return "";
		}		
		airData=getPicks(ueiRc);//3.执行GetPicks
		if(this.isSaveFile){
			saveAirDataToLocalFile(airData);//4.把获取到的码库保存到本地
		}
		return airData;
	}

	/**
	 * 从本地文件中获取空调码
	 * @return 空调码库
	 */
	private String readAirDataFromLocalFile() {
		String airData = "";
		if(this.isFirstReadLocal){
//			devID = Preference.getPreferences().getString("uei_devID", "");
			String filepath=MessageFormat.format(dirPath_airData+"/{0}_{1}.txt", devID,brandCode);
			try {
				File urlFile = new File(filepath);
				if (urlFile.exists()) {
					InputStreamReader isr = new InputStreamReader(
							new FileInputStream(urlFile), "UTF-8");
					BufferedReader br = new BufferedReader(isr);
					String mimeTypeLine = null;
					while ((mimeTypeLine = br.readLine()) != null) {
						airData = airData + mimeTypeLine;
					}
					br.close();
					isr.close();
				}

			} catch (UnsupportedEncodingException | FileNotFoundException e) {
				airData = "";
				if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
					Log.d(TAG, e.toString());
				}


			} catch (IOException e) {
				airData = "";
				if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
					Log.d(TAG, e.toString());
				}
			}
		}
		return airData;
	}
	/**
	 * 保存码库到文本文件中
	 * @param airData
	 * @return
	 */
	private void saveAirDataToLocalFile(String airData) {
		String devID = Preference.getPreferences().getString("uei_devID", "");
		String filepath=MessageFormat.format(dirPath_airData+"/{0}_{1}.txt", devID,brandCode);
		FileUtil.deleteFile(filepath);
		FileUtil.createNewFile(filepath, airData);
	}
	/**
	 * 执行CreateUser方法，获取UEIRc
	 * 
	 * @return
	 */
	private String getUeiRc() {
		String ueiRc = "";
		String devID = Preference.getPreferences().getString("uei_devID", "");
		com.alibaba.fastjson.JSONObject param_RcDic = new com.alibaba.fastjson.JSONObject();
		param_RcDic.put("oemId", "wulian");
		String userId= SmarthomeFeatureImpl.getData("UeiUserID");
//		param_RcDic
//				.put("userId",
//						"hLWPynjM0kWPiJ493ZHCln/DJ54Oqz607QGtluScCjiipqZoX8hzmNijmcCeMtr4OuEIOMLxvYAKq89T4hrgEH9PzhcCwSfH9oMMisISDQ5//b+Jgi2iwZdkKtOMnihpQV/5+zSVMmCYzcf6POToirsMDfZH1IQK7MR4F4eW1UQsn6KiwaDpkt3C1b4m46Qrl5lTIM3UGE9ngTybKd5unNKw/CUDmguQyKZjvPLNZxKVBHJ79AR0+AAghXuEk3DCF7w0vl+eCIPAF6IDIoABRyqR8qlcZxsdte2BMFEQozDQW/pSyUXZP4BFaiTAc0wEJ5LPS/ah+72C5QAxqRvPFw==");
		param_RcDic.put("userId", userId);
		param_RcDic.put("productId", devID);
		StringEntity entity_createuser;
		try {
			entity_createuser = new StringEntity(param_RcDic.toJSONString(),"UTF-8");
			String result_createuse = ueiOnline.sendPost(url_ueiRc,
					entity_createuser);// 结果格式：{"CreateUserResult":0,"ueiRc":"C53D1599-1A2E-427D-AA58-ECC8EDDD6A51"}
			Log.d("WL_23", "result_createuse=" + result_createuse);
			com.alibaba.fastjson.JSONObject json_createuse = com.alibaba.fastjson.JSONObject
					.parseObject(result_createuse);
			String createUserResult = json_createuse
					.getString("CreateUserResult");
			if (createUserResult.equals("0")) {
				ueiRc = json_createuse.getString("ueiRc");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			ueiRc = "";
		}
		return ueiRc;
	}
	/**
	 * 获取码库
	 * @param ueiRc
	 * @return
	 */
	private String getPicks(String ueiRc) {
		String picks = "";
		com.alibaba.fastjson.JSONObject param_getPicks = new com.alibaba.fastjson.JSONObject();
		param_getPicks.put("ueiRc", ueiRc);
		param_getPicks.put("code", this.brandCode);
		param_getPicks.put("deviceType", deviceType);
		try {
			StringEntity entity_getPicks = new StringEntity(
					param_getPicks.toJSONString(), "UTF-8");
			String result_getPicks = ueiOnline.sendPost(url_getPicks,
					entity_getPicks);
			Log.d("WL_23", "result_getPicks=" + result_getPicks);
			com.alibaba.fastjson.JSONObject json_picks = com.alibaba.fastjson.JSONObject
					.parseObject(result_getPicks);
			String getPicksResult = json_picks.getString("GetPicksResult");
			if (getPicksResult.equals("0")) {
				picks = json_picks.getString("picks");
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			picks="";
		}
		return picks;
	}

}
