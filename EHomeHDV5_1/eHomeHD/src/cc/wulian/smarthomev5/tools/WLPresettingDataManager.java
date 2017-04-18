package cc.wulian.smarthomev5.tools;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.alibaba.fastjson.JSON;
import com.wulian.iot.server.controller.CamPresetting;
import com.wulian.iot.server.receiver.Return406_Receiver;
import com.wulian.iot.server.receiver.Smit406_Receiver;
import com.wulian.iot.view.manage.PresettingManager.Return406;
import com.wulian.iot.view.manage.PresettingManager.Smit406_Pojo;
import com.wulian.iot.view.manage.PresettingManager.Smit406_Pojo_Item;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.event.DeviceUeiItemEvent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import de.greenrobot.event.EventBus;
public class WLPresettingDataManager {
	private static String TAG  = "WLPresettingDataManager";
	public Smit406_Receiver smit406_Receiver = null;
	private Context mContext = null;
	private WLPresettingDataManager instance = null;
	private List<Smit406_Pojo_Item> smit406_pojo_items = null;
	public WLPresettingDataManager(){
		Log.i(TAG, "WLPresettingDataManager");
		init();
	}
	public WLPresettingDataManager(Context context){
		Log.i(TAG, "WLPresettingDataManager");
		this.mContext = context;
		init();
	}
	private void init(){
		instance = this;
		EventBus.getDefault().register(instance);
		if(mContext!=null){
			registSmit406();
		}
	}
	public void destroy(){
		EventBus.getDefault().unregister(instance);
		unregisterReceiver();
		unMsgCallback();
		Log.i(TAG, "destroy");
	}
	public void onEventMainThread( final Command406Result command406Result ) {//获取预知位置数据
		Log.i(TAG, "onEventMainThread");
		new Thread(new Runnable() {
			@Override
			public void run() {
				if(command406Result != null){
					 if(!command406Result.getKey().equals("")){
						 if(command406Result.getKey().equals(Smit406_Receiver.DesktopCamera_406_Preset)){
							 if(command406Result.getData() !=null){
								 smit406_pojo_items = new ArrayList<Smit406_Pojo_Item>();
								 Log.i(TAG, "fill data");
									try {
										JSONObject json = new JSONObject(command406Result.getData());
										JSONArray jsonArray = json.getJSONArray("datas");
										for(int var =0;var<jsonArray.length();var++){
											JSONObject obj = jsonArray.getJSONObject(var);
											smit406_pojo_items.add(new Smit406_Pojo_Item(obj.get("name").toString(), obj.get("location").toString(), obj.get("picture").toString()));
										}
										Log.i(TAG, "onEventMainThread("+smit406_pojo_items.size()+")");
										if (smit406_pojo_items != null && smit406_pojo_items.size() > 0) {
											Return406 return406 = new Return406();
											return406.setGwId(command406Result.getGwID());
											return406.setSmit406_Pojo_Items(smit406_pojo_items);
											if (mContext != null) //其他包获取数据
												sendReturnDataBroadcast(return406);
											if (smit406MsgCallback != null)//v5获取数据
												smit406MsgCallback.retrunData(return406);
											return;
										}
									} catch (JSONException e) {
										Log.e(TAG, "===onEventMainThread("+e.getLocalizedMessage().toString()+")===");
										return;
									}
								 return;
							 }
							 Log.i(TAG, "is not data");
							 sendCommonDeviceConfigMsg(smit406_Pojo());
						 }
						 return;
					 }
					 Log.i(TAG, "is not key");
					 sendCommonDeviceConfigMsg(smit406_Pojo());
					}
			}
		}).start();
	}
	private void sendReturnDataBroadcast(Return406 return406){
		Log.i(TAG, "sendReturnDataBroadcast");
		Intent mIntent = new Intent();
		mIntent.setAction(Return406_Receiver.ACTION);
		mIntent.putExtra("return", return406);
		mContext.sendBroadcast(mIntent);
	}
	private void registSmit406(){
		if(smit406_Receiver == null){
			smit406_Receiver = new Smit406_Receiver() {
				@Override
				public void sendCommand_Add(Smit406_Pojo smit406_Pojo ) {
					Log.i(TAG, "receive  synchronization  command");
					sendCommonDeviceConfigMsg(smit406_Pojo);
				}
				@Override
				public void sendCommand_Query() {
					Log.i(TAG, "receive  query command");
					sendCommonDeviceConfigMsg();
				}
			};
			IntentFilter mIntentFilter =  new IntentFilter();
			mIntentFilter.addAction(Smit406_Receiver.ACTION);
			mContext.registerReceiver(smit406_Receiver, mIntentFilter);
		}
	}
	private void unregisterReceiver(){
		if(smit406_Receiver!=null){
			mContext.unregisterReceiver(smit406_Receiver);
		}
	}
	private void unMsgCallback(){
		if(smit406MsgCallback!=null){
			smit406MsgCallback = null;
		}
	}
	public void sendCommonDeviceConfigMsg(){
		NetSDK.sendCommonDeviceConfigMsg(params()[0], params()[1], Smit406_Receiver.Query, null, Smit406_Receiver.DesktopCamera_406_Preset, null);
	}
	public void sendCommonDeviceConfigMsg(Smit406_Pojo smit406_Pojo ){
		NetSDK.sendCommonDeviceConfigMsg(params()[0], params()[1], smit406_Pojo.getOperate(), System.currentTimeMillis()+"", smit406_Pojo.getKey(), JSON.toJSONString(smit406_Pojo.getDatas()));
	}
	private String[] params(){
		return new String[]{AccountManager.getAccountManger().getmCurrentInfo().getGwID(),"self"};
	}
	public Smit406_Pojo smit406_Pojo(){
	  List<Smit406_Pojo_Item> smit406_pojo_items = new ArrayList<Smit406_Pojo_Item>();
      smit406_pojo_items.add(new Smit406_Pojo_Item("","1",""));
      smit406_pojo_items.add(new Smit406_Pojo_Item("","2",""));
      smit406_pojo_items.add(new Smit406_Pojo_Item("","3",""));
      smit406_pojo_items.add(new Smit406_Pojo_Item("","4",""));
      Map<String,Object> map = new HashMap<String,Object>();
      map.put("datas",smit406_pojo_items);
      return new Smit406_Pojo(Smit406_Receiver.Add, Smit406_Receiver.DesktopCamera_406_Preset, map);
	}

	public void setSmit406MsgCallback(Smit406MsgCallback smit406MsgCallback) {
		this.smit406MsgCallback = smit406MsgCallback;
	}

	public Smit406MsgCallback smit406MsgCallback = null;
	public interface Smit406MsgCallback{
		void retrunData(Return406 return406);
	}
}
