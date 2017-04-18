package cc.wulian.smarthomev5.service.html5plus.plugins;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.util.List;

import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.event.DialogEvent;
import cc.wulian.smarthomev5.event.GatewaInfoEvent;
import cc.wulian.smarthomev5.event.MiniGatewayEvent;
import cc.wulian.smarthomev5.tools.AccountManager;
import de.greenrobot.event.EventBus;

public class FeatureImplSendCommand {
	public FeatureImplSendCommand(H5PlusWebView pWebview,String callBackID){
		this.pWebview=pWebview;
		this.callBackID=callBackID;
		EventBus.getDefault().register(this);
	}
	private String currType="";
	public void SetCurrType(String currType){
		this.currType=currType;
	}
	private H5PlusWebView pWebview;
	private String callBackID;
	public void SendDevMsg(String gwID, JSONObject msgBody){
		NetSDK.sendDevMsg(gwID, msgBody);
	}
	
	public void getWifiInfoFromGateway(){
		String gwID=AccountManager.getAccountManger().getmCurrentInfo().getGwID();
		NetSDK.sendMiniGatewayWifiSettingMsg(gwID, "2", "get", null);
	}
	
	public void getZigbeeInfo(){
		String gwID=AccountManager.getAccountManger().getmCurrentInfo().getGwID();
		NetSDK.setGatewayInfo(gwID, "0", null, null,null,null,null,null,null,null);
	}
	
	private void getWifiInfoFromPhone() {
		WifiManager wifiManager = (WifiManager) MainApplication.getApplication()
				.getSystemService(Context.WIFI_SERVICE);
		List<ScanResult> wifilist = wifiManager.getScanResults();
		JSONArray jsonArray=new JSONArray();
		for (ScanResult scanResult : wifilist) {
			JSONObject jsonObject=new JSONObject();
			jsonObject.put("essid", scanResult.SSID);
			jsonObject.put("channel",getChannelByFrequency(scanResult.frequency));
			jsonObject.put("signal",scanResult.level+" dBm" );
			jsonArray.add(jsonObject);
		}
		JSONObject dataObject=new JSONObject();
		dataObject.put("data", jsonArray);
		dataObject.put("isFromPhone","1");
		JsUtil.getInstance().execCallback(pWebview, callBackID,dataObject.toString(), JsUtil.OK, true);
	}

	public void onEventMainThread(DeviceEvent event){
		String epdata="";
		String eptype="";
		if(event!=null&&event.deviceInfo!=null&&event.deviceInfo.getDevEPInfo()!=null){
			epdata=event.deviceInfo.getDevEPInfo().getEpData();
			eptype=event.deviceInfo.getDevEPInfo().getEpType();
		}
		else{
////			Log.d("sendCommad", "eptype="+eptype+" epdata="+epdata);
////			epdata="000100";
			Log.e("sendCommad", "未获取到epdata!");

		}
		Log.d("sendCommad", "eptype="+eptype+" epdata="+epdata);
		if(!StringUtil.isNullOrEmpty(epdata)){
			if(StringUtil.isNullOrEmpty(this.currType)||eptype.equals(this.currType)){
				JsUtil.getInstance().execCallback(pWebview, callBackID,epdata, JsUtil.OK, true);
				EventBus.getDefault().unregister(this);
			}
		}else {
			JsUtil.getInstance().execCallback(pWebview, callBackID,epdata, JsUtil.ERROR, true);
			EventBus.getDefault().unregister(this);
		}
	}
	public void onEventMainThread(DialogEvent event){
		Log.d("sendCommad", "event.actionKey="+event.actionKey+" event.resultCode="+event.resultCode);
//		Log.d("sendCommad", "epdata="+epdata);
		if(event.resultCode==0){
//			JsUtil.getInstance().execCallback(pWebview, callBackID,"000100", JsUtil.OK, true);
		}else{
//			JsUtil.getInstance().execCallback(pWebview, callBackID,"", JsUtil.OK, true);
		}
		
	}

	protected void finalize(){		
		EventBus.getDefault().unregister(this);
		Log.e("CommonDeviceConfigMsg", "finalize()执行");
	}
	
	public void onEventMainThread(GatewaInfoEvent event){
		AccountManager accountManager=AccountManager.getAccountManger();
		GatewayInfo gatewayInfo=accountManager.getmCurrentInfo();
		JSONArray jsonArray=new JSONArray();
		JSONObject infoJsonObject=new JSONObject();
		infoJsonObject.put("gwVer", gatewayInfo.getGwVer());
		if(gatewayInfo.getGwName()!=null&&gatewayInfo.getGwName().equals("")){
			infoJsonObject.put("gwName", AccountManager.getAccountManger().getmCurrentInfo().getGwID());
		}else{
			infoJsonObject.put("gwName", gatewayInfo.getGwName());
		}
		infoJsonObject.put("gwChannel", gatewayInfo.getGwChanel());
		jsonArray.add(infoJsonObject);
		JSONObject dataJsonObject=new JSONObject();
		dataJsonObject.put("data", jsonArray);
		JsUtil.getInstance().execCallback(pWebview, callBackID,dataJsonObject.toString(), JsUtil.OK, true);
	}
	
	public void onEventMainThread(MiniGatewayEvent event){
		String disable=event.getDisable();
		if(disable!=null&&disable.equals("1")){
			getWifiInfoFromPhone();
		}else{
			if(event.getCmdindex().equals("2")&&event.getCmdtype().equals("get")){
				JSONArray array=JSONObject.parseArray(event.getData());
				String data=array.getJSONObject(0).getString("cell");
				JSONObject jsonObject=new JSONObject();
				JSONArray dataJsonObject=JSONArray.parseArray(data);
				jsonObject.put("data", dataJsonObject);
				jsonObject.put("isFromPhone","1");
				JsUtil.getInstance().execCallback(pWebview, callBackID,jsonObject.toJSONString(), JsUtil.OK, true);
			}
		}
	}
	
	public static int getChannelByFrequency(int frequency) {
		int channel = frequency;
		switch (frequency) {
		case 2412:
			channel = 1;
			break;
		case 2417:
			channel = 2;
			break;
		case 2422:
			channel = 3;
			break;
		case 2427:
			channel = 4;
			break;
		case 2432:
			channel = 5;
			break;
		case 2437:
			channel = 6;
			break;
		case 2442:
			channel = 7;
			break;
		case 2447:
			channel = 8;
			break;
		case 2452:
			channel = 9;
			break;
		case 2457:
			channel = 10;
			break;
		case 2462:
			channel = 11;
			break;
		case 2467:
			channel = 12;
			break;
		case 2472:
			channel = 13;
			break;
		case 2484:
			channel = 14;
			break;
		case 5180:
			channel = 36;
			break;
			
		case 5200:
			channel = 40;
			break;
		case 5220:
			channel = 44;
			break;
		case 5240:
			channel = 48;
			break;
		case 5260:
			channel = 52;
			break;
		case 5280:
			channel = 56;
			break;
		case 5300:
			channel = 60;
			break;
		case 5320:
			channel = 64;
			break;
		case 5500:
			channel = 100;
			break;
		case 5520:
			channel = 104;
			break;
		case 5540:
			channel = 108;
			break;
		case 5560:
			channel = 112;
			break;
		case 5580:
			channel = 116;
			break;
		case 5600:
			channel = 120;
			break;
		case 5620:
			channel = 124;
			break;
		case 5640:
			channel = 128;
			break;
		case 5660:
			channel = 132;
			break;
		case 5680:
			channel = 136;
			break;
		case 5700:
			channel = 140;
			break;
		case 5745:
			channel = 149;
			break;
		case 5765:
			channel = 153;
			break;
		case 5785:
			channel = 157;
			break;
		case 5805:
			channel = 161;
			break;
		case 5825:
			channel = 165;
			break;
		}
		return channel;
	}
	public void getUeiLearnIndex(String gwID,String devID){
		NetSDK.sendCommonDeviceConfigMsg(gwID, devID, "3", null, "LearnIndex", null);
	}
	public void onEventMainThread(Command406Result result){
		if(result.getKey().equals("LearnIndex")){
			String resultdata="0";
			if(!StringUtil.isNullOrEmpty(result.getData())){
				resultdata=result.getData();
			}
			JsUtil.getInstance().execCallback(pWebview, callBackID,resultdata, JsUtil.OK, true);
		}
//		Toast.makeText(pWebview.getContext(), "LearnIndex"+result.getKey(), Toast.LENGTH_SHORT).show();
		Log.d("LearnIndex",  "result.getMode()="+result.getMode()+" result.key="+result.getKey()+" result.data="+result.getData());
	}

}
