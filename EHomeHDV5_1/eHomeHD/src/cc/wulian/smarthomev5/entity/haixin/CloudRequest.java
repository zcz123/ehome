package cc.wulian.smarthomev5.entity.haixin;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hisense.hitv.hicloud.bean.account.AppCodeReply;
import com.hisense.hitv.hicloud.bean.account.AppCodeSSO;
import com.hisense.hitv.hicloud.bean.account.SignonReplyInfo;
import com.hisense.hitv.hicloud.bean.global.HiSDKInfo;
import com.hisense.hitv.hicloud.bean.wgapi.CmdMeta;
import com.hisense.hitv.hicloud.factory.HiCloudServiceFactory;
import com.hisense.hitv.hicloud.service.HiCloudAccountService;
import com.hisense.hitv.hicloud.service.WgApiService;
import com.hisense.hitv.hicloud.util.DeviceConfig;
import com.hisense.hitv.hicloud.util.Params;

import java.util.HashMap;
import java.util.List;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.TargetConfigure;

/*
* 1.如何支持多用户Token；
* 2.当前使用Login的地方有点多；
* */

/**
 * Created by YUXIAOXUAN on 2016/12/20.
 * 海信云平台提供支持
 */

public class CloudRequest {
//    public static String Token="";
//    public static String AppCode="";
    private static final String app_key="1583412093";
    private static final String app_secret="er5t368426d7mf760v6wgn257xy4fg6y";
    private static String token="";
    private static long customerID=0L;
    private static String  appCode="";
    private static HiSDKInfo hiSDKInfoForCloud=null;
    private static String loginName_default="18630235804";
    private static String loginPass_default="123456";
    private static boolean isHiCloudAuth=false;
    private static WgApiService wgApiService=null;
//    private static
    public static HiSDKInfo getIntenceSDKInfo(Context context){
        if(hiSDKInfoForCloud==null){
//            token="";
//            initToken(context);

            hiSDKInfoForCloud.setToken(token);//
            hiSDKInfoForCloud=new HiSDKInfo();
            hiSDKInfoForCloud.setCountryCode("");//国家码
            hiSDKInfoForCloud.setZipCode("");//邮编
            hiSDKInfoForCloud.setLanguageId("0");
//            hiSDKInfo.setDomainName("");//域名
        }
        return hiSDKInfoForCloud;
    }
    /**
     * 初始化Token</br>
     * SSO认证，非登陆状态下用来获取tokenSSO或token
     * @param context
     */
    public static void initToken(Context context){
        HashMap<String,String> map=new HashMap<>();
        String deviceId=DeviceConfig.getDeviceId(context);
        map.put(Params.APPKEY,app_key);
        map.put(Params.APPSECRET,app_secret);
        map.put(Params.DEVICEID,deviceId);
        HiSDKInfo info=new HiSDKInfo();
        info.setDomainName(getCloudIp());
        HiCloudAccountService service= HiCloudServiceFactory.getHiCloudAccountService(info);
        AppCodeSSO appCodeSSO=service.appAuthSSO(map);
        if(appCodeSSO==null||appCodeSSO.getReply()==1){
            showLog("appCodeSSO is null");
        }else if(appCodeSSO.getReply()==2){
            //获取token
            token=appCodeSSO.getToken();
            customerID=appCodeSSO.getCustomerId();
            Preference.getPreferences().saveHxAppCode(appCodeSSO.getCode());
            Preference.getPreferences().saveHxToken(token);
            Preference.getPreferences().saveHxCustomerID(customerID);
            showLog("Find sso, auth successfully");
        }else{// 无SSO, 继续匿名认证
            Log.d("HttpHandler", "No sso, continue auth with anoynomous name");
            String appCode = appCodeSSO.getCode();
            HashMap<String, String> map1 = new HashMap<String, String>();
            map1.put(Params.APPCODE, appCode);
            map1.put(Params.LOGINNAME, "");
            map1.put(Params.DEVICEID, deviceId);
            SignonReplyInfo signonReply = service.signon(map1);
            if (signonReply == null || signonReply.getReply() == 1){
                showLog("signon error");
            } else {
                showLog("obtain tokensso");
                token=signonReply.getToken();
                signonReply.getCustomerId();
                Preference.getPreferences().saveHxToken(token);
                Preference.getPreferences().saveHxCustomerID(customerID);
            }
        }
    }


    public static HashMap<String,String> GetTaskTime(String exectime){
        HashMap<String, String> mapTaskTime = new HashMap<>();

        String wifiAndDeviceId= Preference.getPreferences().getHxWifiAndDeviceId();
        //mapTaskTime需要初始化
        mapTaskTime.put("wifiId",wifiAndDeviceId);
        mapTaskTime.put("available","1");//时间表是否可用， 0：不可用（不调度执行）， 1：可用
        mapTaskTime.put("taskMode","1");//时间表调度模式， 1：执行一次； 2：周期性执行（天的周期）
        com.alibaba.fastjson.JSONArray taskItemList=new com.alibaba.fastjson.JSONArray();
        if(!StringUtil.isNullOrEmpty(exectime)){
            com.alibaba.fastjson.JSONObject taskItem=new com.alibaba.fastjson.JSONObject();
            taskItem.put("exectime",exectime);
            com.alibaba.fastjson.JSONArray taskCmdArray=new com.alibaba.fastjson.JSONArray();
            com.alibaba.fastjson.JSONObject taskCmd=new com.alibaba.fastjson.JSONObject();
            taskCmd.put("cmdId","4");
            taskCmd.put("cmdOrder","1");
            taskCmd.put("cmdParm","0");
            taskCmdArray.add(taskCmd);
            taskItemList.add(taskItem);
        }
        mapTaskTime.put("taskItemList",taskItemList.toJSONString());
        showLog(mapTaskTime.toString());
        return mapTaskTime;
    }

    /**
     * 获取空调的原始数据
     * @return 空调的原始数据
     */
    public static JSONObject getOriginStatusWithdevID(List<CmdMeta> cmdMetaList){
        JSONObject jsonObject=new JSONObject();
        StringBuilder strMsg=new StringBuilder();

        if(cmdMetaList!=null&&cmdMetaList.size()>0){
            for(CmdMeta cmdMeta:cmdMetaList){
                strMsg.append("***********************\r\n");
                strMsg.append("cmdName="+cmdMeta.getCmdName()+",");
                strMsg.append("cmdDesc="+cmdMeta.getCmdDesc()+",");
                strMsg.append("cmdCode="+cmdMeta.getCmdCode()+",");
                strMsg.append("cmdValue="+cmdMeta.getCmdValue()+",");
                strMsg.append("cmdParm="+cmdMeta.getCmdParm()+",");
                strMsg.append("taskFlag="+cmdMeta.getTaskFlag()+",");
                strMsg.append("parmType="+cmdMeta.getParmType()+",");
                strMsg.append("StatusValueList="+cmdMeta.getStatusValueList()+",");
                if(cmdMeta.getCmdParmMetaList()!=null&&cmdMeta.getCmdParmMetaList().size()>0){

                }

//                strMsg.append("cmdName="+cmdMeta.getCmdName());
//                strMsg.append("cmdName="+cmdMeta.getCmdName());
//                strMsg.append("cmdName="+cmdMeta.getCmdName());
//                strMsg.append("cmdName="+cmdMeta.getCmdName());
//                strMsg.append("cmdName="+cmdMeta.getCmdName());

                strMsg.append("***********************\r\n");
            }
        }
        return jsonObject;
    }

    /**
     * 组装用于wifiid及devID的json字符串
     * @param wifiID wifiID
     * @param devID devID
     * @return
     */
    public static void SaveHxWifiAndDeviceId(String wifiID,String devID){
        JSONObject jsonObject=new JSONObject();
        jsonObject.put("kCloudWifiId",wifiID);
        jsonObject.put("kCloudDeviceId",devID);
        Preference.getPreferences().saveHxWifiAndDeviceId(jsonObject.toString());
    }

    /**
     * 从缓存中获取保存的wifiID
     * @return
     */
    public static String GetHxWifiID(){
        String wifiID="";
        String strJson= Preference.getPreferences().getHxWifiAndDeviceId();
        JSONObject jsonObject= JSON.parseObject(strJson);
        if(jsonObject.containsKey("kCloudWifiId")){
            wifiID=jsonObject.getString("kCloudWifiId");
        }
        return wifiID;
    }

    /**
     * 从缓存中获取保存的DevID
     * @return
     */
    public static String GetHxDevID(){
        String devID="";
        String strJson= Preference.getPreferences().getHxWifiAndDeviceId();
        JSONObject jsonObject= JSON.parseObject(strJson);
        if(jsonObject.containsKey("kCloudDeviceId")){
            devID=jsonObject.getString("kCloudDeviceId");
        }
        return devID;
    }

    private static int  tokenExpiredTime;
    private static long tokenCreate;
    private static String refreshToken;
    private static int refreshTokenExpire;
    private static int customerId;

    /**
     * 登录获取Token及customerId
     * @param mContext Context
     * @param loginName 用户名
     * @param password 密码
     */
    public static void CloudHisLogin(Context mContext,String loginName,String password){
        hiSDKInfoForCloud=null;
        if(StringUtil.isNullOrEmpty(loginName)){
            loginName=loginName_default;
        }
        if(StringUtil.isNullOrEmpty(password)){
            password=loginPass_default;
        }
        HashMap<String, String> map = new HashMap<>();
        String deviceId = DeviceConfig.getDeviceId(mContext);
        map.put(Params.DEVICEID, deviceId);
        map.put(Params.LOGINNAME, loginName);
        map.put(Params.PASSWORD, password);
        appAuthBlock();

        map.put(Params.APPCODE, appCode);
        HiSDKInfo hiSDKInfo = new HiSDKInfo();
        hiSDKInfo.setDomainName(getCloudIpForAccount());
        HiCloudAccountService service = HiCloudServiceFactory.getHiCloudAccountService(hiSDKInfo);
        SignonReplyInfo reply = service.signon(map);

        if(reply.getReply() == 0){
            token = reply.getToken();
            tokenExpiredTime = reply.getTokenExpireTime();
            tokenCreate = reply.getTokenCreateTime();

            refreshToken = reply.getRefreshToken();
            refreshTokenExpire = reply.getRefreshTokenExpiredTime();
            customerId = reply.getCustomerId();
            hiSDKInfo.setToken(refreshToken);
            hiSDKInfo.setCountryCode("");//国家码
            hiSDKInfo.setZipCode("");//邮编
            hiSDKInfo.setLanguageId("0");
            hiSDKInfo.setSign("");
            Preference.getPreferences().saveHxToken(refreshToken);
            Preference.getPreferences().saveHxCustomerID(customerID);

            hiSDKInfoForCloud=new HiSDKInfo();
            hiSDKInfoForCloud.setToken(token);
            hiSDKInfoForCloud.setCountryCode("");//国家码
            hiSDKInfoForCloud.setZipCode("");//邮编
            hiSDKInfoForCloud.setLanguageId("0");
            hiSDKInfoForCloud.setSign("");
            hiSDKInfoForCloud.setDomainName(getCloudIp());


            showLog("Login Success!");
        }else{
            showLog("Login Failed!");
        }

    }

    public static void appAuthBlock(){
        HashMap<String, String> map = new HashMap<String, String>();
        map.put(Params.APPKEY, app_key);
        map.put(Params.APPSECRET, app_secret);
        HiSDKInfo info = new HiSDKInfo();
        info.setDomainName(getCloudIpForAccount());  //设置云端域名
        HiCloudAccountService service = HiCloudServiceFactory
                .getHiCloudAccountService(info);
        AppCodeReply reply = service.appAuth(map);
        if (reply != null && reply.getReply() == 0) {
            appCode = reply.getCode();
            isHiCloudAuth = true;
            showLog("appAuthBlock success");
        } else {
            showLog("appAuthBlock faill");
        }
    }
   private  static String getCloudIpForAccount(){
        return "bas.wg.hismarttv.com";//账号相关
//        return "";
   }

    private static String getCloudIp(){
        return "api.wg.hismarttv.com";//非账号相关
    }


    private static void showLog(String msg){
        if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
        	Log.d("HXKT", msg);
        }
    }
}

