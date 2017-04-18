package cc.wulian.smarthomev5.fragment.uei;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.DesUtil;
import cc.wulian.smarthomev5.entity.uei.UeiUiArgs;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl.Constants;
import cc.wulian.smarthomev5.tools.AccountManager;

import com.alibaba.fastjson.JSONObject;
import com.uei.control.ACEService;

/**
 * Created by Administrator on 2016/1/12 0012.
 */
public class UeiOnlineUtil {

    //    public static String baseUrl = "http://74.209.252.205/QuickSetLite.svc";
    public static String baseUrl = "http://www.ueiwsp.com/QuickSetLite.svc";
    public static boolean isAuthorization = false;

    public static String ueiRc = "";

    public static String state = "";

    public static String languageCode = "zh";

    public static String region = "0";

    public static String groupId = "1";

    public static String code = "T4137";//"C4232","N4871","N4478"

    public static String brand = "LeTV";

    public static String deviceTypes = "C,N,S";

    public static String deviceType = "C,N,S";

    public static String picks = "";

    public static String model = "C1S";

    public static String[] languageCodesArr = new String[]{
            "All region", "USA", "Europe", "Middle East", "Asia Pacific",
            "Latin America", "Oceania", "Japan", "North America", "China"
    };
    
    public static String[] deviceTypeCodesArr = new String[]{
            "T", "C,N,S", "A,D,M,R", "V,Y", "T", "Z"
    };


    public static final char[] Digit = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static Map<String,Byte> deviceTypeCodesMap=new HashMap<String,Byte>();
    static {
        deviceTypeCodesMap.put("T", (byte) 0x00);
        deviceTypeCodesMap.put("C", (byte) 0x01);
        deviceTypeCodesMap.put("N", (byte) 0x02);
        deviceTypeCodesMap.put("S", (byte) 0x03);
        deviceTypeCodesMap.put("V", (byte) 0x04);
        deviceTypeCodesMap.put("Y", (byte) 0x06);

        deviceTypeCodesMap.put("R", (byte) 0x07);
        deviceTypeCodesMap.put("M", (byte) 0x07);

        deviceTypeCodesMap.put("A", (byte) 0x08);
        deviceTypeCodesMap.put("D", (byte) 0x09);
        deviceTypeCodesMap.put("H", (byte) 0x0A);

    }
    /**
     * 创建用户
     *
     * @return 所代表远程资源的响应结果
     */
    public String createUser() throws UnsupportedEncodingException {
        String url = baseUrl + "/CreateUser";
        JSONObject param = new JSONObject();
        param.put("oemId", "wulian");
        String acEncryptUserId = ACEService.ACEncryptUserId("MyTestingUserId");
//        param.put("userId", "hLWPynjM0kWPiJ493ZHCln/DJ54Oqz607QGtluScCjiipqZoX8hzmNijmcCeMtr4OuEIOMLxvYAKq89T4hrgEH9PzhcCwSfH9oMMisISDQ5//b+Jgi2iwZdkKtOMnihpQV/5+zSVMmCYzcf6POToirsMDfZH1IQK7MR4F4eW1UQsn6KiwaDpkt3C1b4m46Qrl5lTIM3UGE9ngTybKd5unNKw/CUDmguQyKZjvPLNZxKVBHJ79AR0+AAghXuEk3DCF7w0vl+eCIPAF6IDIoABRyqR8qlcZxsdte2BMFEQozDQW/pSyUXZP4BFaiTAc0wEJ5LPS/ah+72C5QAxqRvPFw==");
        param.put("userId", acEncryptUserId);
        param.put("productId", "");
        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        JSONObject response = JSONObject.parseObject(result);
        if (response != null) {
            ueiRc = response.getString("ueiRc");
            isAuthorization = true;
        }
        System.out.println(result);
        return result;
    }

    //获取区域
    public String getRegions() throws UnsupportedEncodingException {
        String url = baseUrl + "/GetRegions";
        JSONObject param = new JSONObject();
        param.put("ueiRc", ueiRc);
        param.put("language", "en");
        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        JSONObject response = JSONObject.parseObject(result);
        System.out.println(result);
        return result;
    }

    //获取语言
    public  String getLanguages() throws UnsupportedEncodingException {
        String url = baseUrl + "/GetLanguages";
        JSONObject param = new JSONObject();
        param.put("ueiRc", ueiRc);
        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        JSONObject response = JSONObject.parseObject(result);
        System.out.println(result);
        return result;
    }

    //获取设备类型
    public  String getDeviceType(int pageIndex) throws UnsupportedEncodingException {
        String url = baseUrl + "/GetDeviceTypes";
        JSONObject param = new JSONObject();
        param.put("ueiRc", ueiRc);
        param.put("language", "en");
        param.put("region", "9");
        param.put("resultPageIndex", pageIndex);
        param.put("resultPageSize", 20);
        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        JSONObject response = JSONObject.parseObject(result);
        System.out.println(result);
        return result;
    }

    //获取设备分组
    public  String getDeviceGroup(String region, String languageCode) throws UnsupportedEncodingException {
        String url = baseUrl + "/GetDeviceGroups";
        JSONObject param = new JSONObject();
        param.put("ueiRc", ueiRc);
        param.put("region", region);
        param.put("language", languageCode);
        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        System.out.println(result);
        return result;
    }

    //获取品牌
    public  String getBrands(int pageIndex, String deviceTypes) throws UnsupportedEncodingException {
        String url = baseUrl + "/GetBrands";
        JSONObject param = new JSONObject();
        param.put("ueiRc", ueiRc);
        param.put("region", "9");
        param.put("resultPageIndex", pageIndex);
        param.put("resultPageSize", 20);
        param.put("deviceTypes", deviceTypes);
        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        return result;
    }

    //获取本地化的品牌名称
    public  String getBrandsXlt(String regionCode, String languageCode, int pageIndex, String deviceTypes) throws UnsupportedEncodingException {
        String url = baseUrl + "/GetBrandsXlt";
        JSONObject param = new JSONObject();
        param.put("ueiRc", ueiRc);
        param.put("region", regionCode);
        param.put("languageCode", languageCode);
        param.put("resultPageIndex", pageIndex);
        param.put("resultPageSize", 20);
        param.put("deviceTypes", deviceTypes);

        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        System.out.println(result);
        return result;
    }

    //获取已XX开头的本地化的品牌名称
    public  String getBrandsLikeXlt(int pageIndex, String regionCode, String languageCode, String deviceTypes, String brandsStartWith) throws UnsupportedEncodingException {
        String url = baseUrl + "/GetBrandsLikeXlt";
        JSONObject param = new JSONObject();
        param.put("ueiRc", ueiRc);
        param.put("region", regionCode);
        param.put("languageCode", languageCode);
        param.put("deviceTypes", deviceTypes);
        param.put("resultPageIndex", pageIndex);
        param.put("resultPageSize", 20);
        param.put("brandsStartWith", brandsStartWith);

        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        System.out.println(result);
        return result;
    }

    //获取已XX开头的本地化的品牌名称
    public  String getBrandsLike(int pageIndex, int regionCode, String deviceTypes, String brandsStartWith) throws UnsupportedEncodingException {
        String url = baseUrl + "/GetBrandsLike";
        JSONObject param = new JSONObject();
        param.put("ueiRc", ueiRc);
        param.put("region", regionCode);
        param.put("deviceTypes", deviceTypes);
        param.put("brandsStartWith", brandsStartWith);
        param.put("resultPageIndex", pageIndex);
        param.put("resultPageSize", 50);

        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        System.out.println(result);
        return result;
    }

    //获取特定品牌或设备的代码
    public  String getCodes(int pageIndex,int region , String brand, String codesToExclude, String deviceTypes) throws UnsupportedEncodingException {
        String url = baseUrl + "/GetCodes";
        JSONObject param = new JSONObject();
        param.put("ueiRc", ueiRc);
        param.put("region", region);
        param.put("deviceTypes", deviceTypes);
        param.put("brand", brand);
        param.put("codesToExclude", codesToExclude);
        param.put("resultPageIndex", pageIndex);
        param.put("resultPageSize", 50);

        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        JSONObject response = JSONObject.parseObject(result);
        System.out.println(result);
        return result;
    }

    //回去最佳选择
    public String getPicks(int pageIndex, String deviceTypes, String code) throws UnsupportedEncodingException {
        String url = baseUrl + "/GetPicks";
        JSONObject param = new JSONObject();
        param.put("ueiRc", ueiRc);
        param.put("code", code);
        param.put("deviceType", deviceTypes);
        param.put("resultPageIndex", pageIndex);
        param.put("resultPageSize", 50);

        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        JSONObject response = JSONObject.parseObject(result);
        if (response != null) picks = response.getString("picks");
        System.out.println(result);
        if (picks!=null&&picks.length()>0) return bytesToHexString(Base64.decode(picks,Base64.NO_WRAP));
        return null;
    }

    public String bytesToHexString(byte[] buffer) {
        char[] chars = new char[buffer.length * 2];
        int k = 0;
        for (byte b : buffer) {
            chars[k++] = Digit[(b >>> 4) & 0xf];
            chars[k++] = Digit[b & 0xf];
        }
        return new String(chars);
    }


    //设备型号查询
    public String modelSearch(int pageIndex, int regionCode, String brand, String model, String deviceTypes) throws UnsupportedEncodingException {
        String url = baseUrl + "/ModelSearch";
        JSONObject param = new JSONObject();
        param.put("ueiRc", ueiRc);
        param.put("region", regionCode);
        param.put("deviceTypes", deviceTypes);
        param.put("brand", brand);
        param.put("model", model);
        param.put("fuzzyThreshold", 70);
        param.put("resultPageIndex", pageIndex);
        param.put("resultPageSize", 50);

        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        System.out.println(result);
        return result;
    }

    /**
     * 获取OSM和NextKey
     *
     * @param brand       品牌
     * @param deviceTypes 设备类型
     * @param groupId     设备分组Id
     * @param region      区域
     * @return
     */
    public String loadOSMAndGetNextKey(String brand, String deviceTypes, String groupId, String region) throws UnsupportedEncodingException {
        String inputXML = "<osm b=\"" + brand + "\" dtypes=\"" + deviceTypes + "\" dgroupId=\"" + groupId + "\" region=\"" + region + "\" />";
        String url = baseUrl + "/LoadOSMAndGetNextKey";
        JSONObject param = new JSONObject();
        param.put("ueiRc", ueiRc);
        param.put("inputXML", inputXML);

        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        JSONObject response = JSONObject.parseObject(result);
        if (response != null) state = response.getString("state");
        System.out.println(result);
        return result;
    }

    /**
     * 返回OSM结果并获取下一个key
     *
     * @param keyworked 上一个按键的测试结果
     * @return
     */
    public String reportOSMResultAndGetNextKey(boolean keyworked) throws UnsupportedEncodingException {
        String inputXML = "<osm keyworked=\"" + keyworked + "\" state= \"" + state + "\" />";
        String url = baseUrl + "/ReportOSMResultAndGetNextKey";
        JSONObject param = new JSONObject();
        param.put("ueiRc", ueiRc);
        param.put("inputXML", inputXML);

        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        JSONObject response = JSONObject.parseObject(result);
        if (response != null) state = response.getString("state");
        System.out.println(result);
        return result;
    }

    //查询匹配结果状态码
    public String getOsmCodeSearchStatusDictionary() throws UnsupportedEncodingException {
        String url = baseUrl + "/GetOsmCodeSearchStatusDictionary";
        JSONObject param = new JSONObject();
        param.put("ueiRc", ueiRc);

        StringEntity entity = new StringEntity(param.toJSONString(), "UTF-8");
        String result = sendPost(url, entity);
        JSONObject response = JSONObject.parseObject(result);
        if (response != null) state = response.getString("state");
        System.out.println(result);
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     *
     * @param url    发送请求的 URL
     * @param entity 请求参数实体
     * @return 所代表远程资源的响应结果
     */
    public String sendPost(String url, HttpEntity entity) {
        BufferedReader reader = null;
        StringBuffer result = new StringBuffer();
        HttpClient client = new DefaultHttpClient();
        try {
            HttpPost post = new HttpPost(url);
            post.addHeader("Content-Type", "application/json");
            post.setEntity(entity);
            HttpResponse response = client.execute(post);
            if (response.getStatusLine().getStatusCode() == 200) {
                String line;
                reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                while ((line = reader.readLine()) != null) {
                    result.append(line);
                }
            }
        } catch (IOException e) {
        	 Log.d("UeiOnlineUtil",  e.getMessage());
//            e.printStackTrace();
        }
        
        finally {
            if (reader != null) try {
                reader.close();
            } catch (IOException e) {
                Log.d("UeiOnlineUtil",  e.getMessage());
            }
        }
        return result.toString();
    }

  //组装下载码库参数
    public  String assembleDownloadDeviceData(String testCode,String data) throws Exception {
        try{
        	 byte[] tempBuffer=Base64.decode(data, Base64.NO_WRAP);
             int codeSetNum = Integer.parseInt(testCode.substring(1, testCode.length()));
             byte[] head = new byte[]{
                     0x22, deviceTypeCodesMap.get(testCode.substring(0,1)), (byte) ((codeSetNum) >> 8), (byte) codeSetNum
             };
             byte[] buffer = new byte[tempBuffer.length + head.length];
             System.arraycopy(head, 0, buffer, 0, head.length);
             System.arraycopy(tempBuffer, 0, buffer, head.length, tempBuffer.length);
             return Base64.encodeToString(buffer, Base64.NO_WRAP);
        }catch(Exception e){
        	return null;
        }
    }

    //将LoadOSMAndGetNextKey的返回结果放到 UeiHelper上
    public String sendToUeiHelper(String code,int testKeyId,String codeData){
    	String result=null;
		try {
            //用于匹配的地址
            //String url="http://222.190.121.158:6014";//测试服务器地址
            String url="https://irt.wulian.cc:33445/";//正式服务器地址
	         JSONObject data = new JSONObject();
	         data.put("method", "getKeyCodeData");
	         data.put("jsonrpc", "2.0");
	         data.put("id", "3");
	         
	         JSONObject param = new JSONObject();
	         param.put("code", code);
	         param.put("key", testKeyId);
	         param.put("codeSetData", codeData);
	         data.put("params", param);
	         StringEntity entity= new StringEntity(data.toJSONString(), "UTF-8");
			 result = sendPost(url, entity);
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
    }
    
    //组装发送测试键参数
    public  String assembleSendKeyData(String testCode,String testKeyId) throws Exception {
    	try{
    		int codeSetNum = Integer.parseInt(testCode.substring(1, testCode.length()));
            System.out.println(codeSetNum);
            byte[] head = new byte[]{0x01,deviceTypeCodesMap.get(testCode.substring(0,1)), (byte) ((codeSetNum) >> 8), (byte) (codeSetNum), Byte.parseByte(testKeyId), 0x00, 0x00, 0x00};
            return Base64.encodeToString(head, Base64.NO_WRAP);
    	}catch(Exception e){
    		return null;
    	}
    }
    
    //组装发送学习的参数
    public  String getSendIrPatternData(String keyId,String keyData) throws Exception {
    	try{
            byte[] head = new byte[]{0x10,Byte.parseByte(keyId),0x10};
            byte[] data=keyData.getBytes();
            byte[] param=new byte[head.length+data.length];
            System.arraycopy(head, 0, param, 0, head.length);
            System.arraycopy(data, 0, param, head.length, data.length);
            return DesUtil.getHEXfromString(param);
    	}catch(Exception e){
    		return null;
    	}
    }
   
   
}
