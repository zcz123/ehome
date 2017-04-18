package cc.wulian.smarthomev5.dao;

import java.util.List;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.tools.AccountManager;
import de.greenrobot.event.EventBus;
import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

/**
 *封装调用406接口的方法</br>
 *1.支持本地缓存管理；</br>
 *2.支持EventBus；</br>
 * @author yuxiaoxuan
 * @date 2016年7月19日19:39:24
 */
public class Command406_DeviceConfigMsg {
	/**
	 * 新增
	 */
	public static final String mode_add="1";
	/**
	 * 修改
	 */
	public static final String mode_update="2";
	/**
	 * 获取数据
	 */
	public static final String mode_get="3";
	/**
	 * 删除
	 */
	public static final String mode_delete="4";
	/**获取自增变量的值*/
	public static final String mode_getSIN="5";
	/**创建自增变量*/
	public static final String mode_createSIN="6";
	/**清空*/
	public static final String mode_clear="7";

	private ICommand406_Result commandResult;
	private Context context;
	private Command406_Dao commandDao;
	public Command406_DeviceConfigMsg(){
		commandDao=Command406_Dao.getInstance();
		appID = AccountManager.getAccountManger().getmCurrentInfo().getAppID();
		EventBus.getDefault().register(this);
	}
	public Command406_DeviceConfigMsg(Context context){
		this.context=context;
		commandDao=Command406_Dao.getInstance();
		appID = AccountManager.getAccountManger().getmCurrentInfo().getAppID();
		EventBus.getDefault().register(this);
	}
	private String gwID="";
	private String devID="";
	private String appID = "";
	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
	}

	public String getDevID() {
		return devID;
	}

	public void setDevID(String devID) {
		this.devID = devID;
	}
	
	public ICommand406_Result getConfigMsg() {
		return commandResult;
	}

	public void setConfigMsg(ICommand406_Result commandResult) {
		this.commandResult = commandResult;
	}

	/**
	 * 向网关发送添加命令
	 * @param key 配置项的key值</br>
	 * <p>不同的设备的含义可能不一样</br>
	 * 如MK001中的设备，该值是由mode+deviceCode组成
	 * @param addData 需要添加的数据,json格式
	 */
	public void SendCommand_Add(String key,String addData){		
		NetSDK.sendCommonDeviceConfigMsg(gwID, devID, mode_add, System.currentTimeMillis()+"", key, addData);
	}

	/**
	 * 创建自增变量
	 * @param key 自增变量的Key值
	 * @param initial 自增变量的初始值
	 * @param interval 自增变量的间隔
     */
	public void SendCommand_CreateSIN(String key,int initial,int interval){
		JSONObject jsondata=new JSONObject();
		jsondata.put("initial",initial);
		jsondata.put("interval",interval);
		NetSDK.sendCommonDeviceConfigMsg(gwID, devID, mode_createSIN, System.currentTimeMillis()+"", key, jsondata.toJSONString());
	}

	/**
	 * 获取自增变量
	 * @param key
     */
	public void SendCommand_GetSIN(String key){
		NetSDK.sendCommonDeviceConfigMsg(gwID, devID, mode_getSIN, System.currentTimeMillis()+"", key, "");
	}

	public void SendCommand_ClearV2(String key){
		NetSDK.sendCommonDeviceConfigMsg(gwID, devID, mode_clear, System.currentTimeMillis()+"", key, "");
	}
	/**
	 * 向网关发送获取数据的命令
	 */
	public void SendCommand_Get(){
		String time=commandDao.GetLastTime();
		if(isTimeOut(time)){
			time=null;
			commandDao.DeleteItems(this.gwID, this.devID);
		}else{
			if(this.commandResult!=null){
				List<Command406Result> resultsFromDb=commandDao.GetItemsByKey(gwID, devID);				
				this.commandResult.Reply406Result(resultsFromDb);
			}
		}				
		NetSDK.sendCommonDeviceConfigMsg(gwID, devID, mode_get, null, null, null);
	}
	public void SendCommand_Get(String key){
		/* 这个地方是否需要从本地获取？
		 * 当前用这个方法的地方只有一个，就是取“最大空调快捷码”。
		 * 而这个值从本地获取基本上是没有意义的。
		 * 因为这个空调快捷码最新的肯定在网关存储的，即使通过时间戳过滤，也必须执行一次406命令才知道是否有最新的
		 * */
//		String time=commandDao.GetLastTime();
//		if(isTimeOut(time)){
//			time=null;
//			commandDao.DeleteItems(this.gwID, this.devID,key);
//		}else{
//			if(this.commandResult!=null){
//				List<Command406Result> resultsFromDb=commandDao.GetItemsByKey(gwID, devID,key);
//				this.commandResult.Reply406Result(resultsFromDb);
//			}
//		}				
		NetSDK.sendCommonDeviceConfigMsg(gwID, devID, mode_get, null, key, null);
	}
	/**
	 * 向网关发送修改的命令
	 * @param updateData 需要修改的数据,json格式
	 */
	public void SendCommand_Update(String key,String updateData){
		NetSDK.sendCommonDeviceConfigMsg(gwID, devID, mode_update,  System.currentTimeMillis()+"", key, updateData);
	}
	
	public void SendCommand_Delete(String key,String deleteData){
		NetSDK.sendCommonDeviceConfigMsg(gwID, devID, mode_delete,  System.currentTimeMillis()+"", key, deleteData);
	}
	
	public void SendCommand_Clear(){
		NetSDK.sendCommonDeviceConfigMsg(gwID, devID, mode_delete,  null, null, null);
	}
	public void ClearDbCache(){
		commandDao.DeleteItems(this.gwID, this.devID);
	}
	public void onEventMainThread(Command406Result result){
		result.setAppID(appID);
		//把获取的数据更新到数据库中
		if(result.getMode().equals(mode_delete)){
			commandDao.delete(result);
		}else if(result.getMode().equals(mode_clear)){
			commandDao.DeleteItems(this.gwID, this.devID);
		}
		else{
			commandDao.UpdateOrInsert(result);
		}		
		//用于外部执行操作		
		if(this.commandResult!=null){			
			this.commandResult.Reply406Result(result);
		}
		Log.d("CommonDeviceConfigMsg",  "result.getMode()="+result.getMode()+" result.key="+result.getKey()+" result.data="+result.getData());
	}
	
	
	/**
	 * 时间是否超时</br>
	 * 时间与当前时间相差7天即为超时
	 * @param time
	 * @return
	 */
	private boolean isTimeOut(String time){
		boolean timeout=false;
		if(!StringUtil.isNullOrEmpty(time)){
			long curtime=System.currentTimeMillis();
			long lasttime=Long.parseLong(time);
			double day=(double)(curtime-lasttime)/1000/3600/24/7;
			if(day>=7){
				time="";
			}
		}
		return timeout;
	}
	
	protected void finalize(){		
		EventBus.getDefault().unregister(this);
		Log.e("CommonDeviceConfigMsg", "finalize()执行");
	}
}
