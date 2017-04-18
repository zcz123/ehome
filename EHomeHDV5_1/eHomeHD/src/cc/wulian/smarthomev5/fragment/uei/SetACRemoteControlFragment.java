package cc.wulian.smarthomev5.fragment.uei;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.uei.control.ACEService;
import com.uei.control.AirConFunction;
import com.uei.control.AirConSDKManager;
import com.uei.control.AirConState;
import com.uei.control.AirConWidgetStatus;
import com.uei.control.ResultCode;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.dao.Command406_DeviceConfigMsg;
import cc.wulian.smarthomev5.dao.ICommand406_Result;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.entity.uei.AirStateStandard;
import cc.wulian.smarthomev5.event.DeviceEvent;

import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl.Constants;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.utils.TargetConfigure;


/*————————————————————————————————————整体说明—————————————————————————————————
 * 1.启动的时候
 * 		1.1 获取码库--使用UeiAirDataUtil；
 * 		1.2启动ACEService--使用ACEngineStart方法；
 * 		1.3获取空调当前状态--使用ACEService_ACGetKeys()；
 * 		1.4根据空调状态设置界面数据--使用holderView.FillData();
 * 		1.5同时获取网关上保存的最大索引，通过406命令，Key值是currentIndex；
 * 2.单击虚拟键时
 * 		2.1获取空调要发送的指令（虚拟键的Tag属性），通过ACEService.ACProcessKey发送指令；
 * 		2.2获取空调当前状态--使用ACEService_ACGetKeys()；
 * 		2.3根据空调状态设置界面数据--使用holderView.FillData();
 * 3.单击“发送给空调时”
 * 		3.1获取指令（epdata）--使用getEpData_forrun()；
 * 		3.2执行12命令--使用sendCommand12_forair
 * 4.单击“保存”
 * 		4.1获取当前需保存的快捷键索引，变量是：saveIndex;
 * 		4.2获取epdata--getEpData_forsave;
 * 		4.3执行12命令，保存快捷码信息
 * 			--使用sendCommand12_forair()；
 * 			--返回信息判断在：onEventMainThread(DeviceEvent event)
 * 		4.4当12命令执行成功后，执行406命令，保存快捷键信息到网关--save406();
 * 		4.5更新currentIndex的值；
 * 		4.6完全成功后，退出当前页面；
 * 5.关于ACEService的调用顺序：
 *   下面的信息来自Eric Tang（etang@uei.com）、Saw（saw.xu@honestar.com）及Allen的回复
 * 	 1.    Host app is launched
	 2.    Initialize AS SDK: Call QuicksetSDKManage.initialize(Context).
	 3.    User activities:
		a.    Loading an AirCon device: call ACEService.ACEngineStart(Data, State)
		b.    Controlling AirCon device
		c.    Closing AirCon device: call ACEService.ACEngineStop()
		d.    Repeat a-c
	4.    User has exited the host app or the host app is forced to be closed
	5.    Shut down AS SDK: call QuicksetSDKManager.singleton().close()
	6.    App is closed
 * ————————————————————————————————————————————————————————————————————————————————————*/


@SuppressLint("DefaultLocale")
public class SetACRemoteControlFragment extends WulianFragment implements
		OnClickListener, ICommand406_Result {
	private String log_tag="air_control";
	private View parentView;
	private AirStateStandard airStatus = null;//界面使用到的标准空调状态
	private AirStatusHolderView holderView = null;//空调状态显示面板
	private UeiCommonEpdata ueiCommEpdata=null;//用于发送和组合12命令的对象
	private WLDialog dialog;
	private Vibrator mVibrator01;  //声明一个振动器对象
	/*-----------------------带arg前缀的参数都是从其它页面传递过来的------------------------*/
	String argtitle = "";//标题
	String argairstatus = "";//从外界传递过来的空调状态
	String argdevicecode = "";//设备编号
	String argbrandtype="";//品牌类型
	String argbrandname="";//品牌名称
	String airData = "";//空调码库
	private String arggwID="";
	private String argdevID="";
	private String argepType="";
	private String argep="";
	private boolean argisAdd=false;
	private String argcurIndex="";
	String argcurvirkey="";
	/*-----------------------带arg前缀的参数都是从其它页面传递过来的------------------------*/
	
	private static String _states = "";//空调状态
	private ArrayList<AirConFunction> _currentAirConFunctions = new ArrayList<AirConFunction>();
	com.uei.control.AirConDevice airConIRDevice;//设备信息
	public static String _currentAirconStates = "";//当前空调的状态，貌似和_states有所重复，需要优化
	boolean isStart = false;//ACEngineStart是否执行成功
	private String sendData;//执行命令成功后获取的发送码，这个信息保存的时候需要使用
	Command406_DeviceConfigMsg command406;
	private String mark_currIndex="currentIndex";
	private String _currentIndexStr="";//当前从网关上获取到的最大快捷码
	private boolean isSaving=false;//是否正在执行保存操作
	private String saveIndex;//当前保存时使用的快捷码
	private Map<String,Integer> map_btnTagAirid=new HashMap<String,Integer>();
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
				.detectDiskReads().detectDiskWrites().detectAll().penaltyLog()
				.build());
		command406=new Command406_DeviceConfigMsg(this.mActivity);
		command406.setConfigMsg(this);
		AirConSDKManager.initialize(this.mActivity);
		mVibrator01 = ( Vibrator ) MainApplication.getApplication().getSystemService(Service.VIBRATOR_SERVICE);
		sendData="";
		initBar();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		parentView = inflater.inflate(R.layout.fragment_setac_remootecontrol,
				container, false);
		return parentView;
	}

	@Override
	public void onViewCreated(View paramView, Bundle paramBundle) {
		super.onViewCreated(paramView, paramBundle);
		initView(paramView);
	}

	private void initBar() {
		Bundle args = getArguments();
		argtitle = args.getString("title");
		argairstatus = args.getString("airstatus",AirStateStandard.defaultState);
		argdevicecode = args.getString("devicecode","");
		argbrandtype=args.getString("brandtype","");
		argbrandname=args.getString("brandname","");
		arggwID=args.getString("gwID","");
		argdevID=args.getString("devID","");
		argepType=args.getString("epType","");
		argep=args.getString("ep","");
		argisAdd=args.getBoolean("isAdd",false);
		argcurvirkey=args.getString("virkey", "");		
		if(args.containsKey("curIndex")){
			argcurIndex=args.getString("curIndex");
			}
		command406.setDevID(argdevID);
		command406.setGwID(arggwID);
		
		ueiCommEpdata=new UeiCommonEpdata(arggwID,argdevID,argep);
		
		this.mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setIconText(R.string.remote_control);
		getSupportActionBar().setTitle(argtitle);

		getSupportActionBar().setRightIconText(
				mApplication.getResources().getString(R.string.set_save));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {
					@Override
					public void onClick(View v) {
						saveAirData();
					}
				});
		
		
	}
	private String Down_AirData="Down_AirData";
	private String Save_QuickKey="Save_QuickKey";
	byte[] arrBytedata=null;
	private void initView(View paramView) {

		
		parentView.findViewById(R.id.modebtn).setOnClickListener(this);
		parentView.findViewById(R.id.fanspeedbtn).setOnClickListener(this);
		parentView.findViewById(R.id.lin_ac_add).setOnClickListener(this);
		parentView.findViewById(R.id.lin_ac_sub).setOnClickListener(this);
		parentView.findViewById(R.id.iv_controltype).setOnClickListener(this);
		parentView.findViewById(R.id.swing_up_downbtn).setOnClickListener(this);
		parentView.findViewById(R.id.swing_left_rightbtn).setOnClickListener(
				this);
		parentView.findViewById(R.id.editorbtn).setOnClickListener(this);
		parentView.findViewById(R.id.send_to_airconditiontv).setOnClickListener(this);
		if (StringUtil.isNullOrEmpty(argairstatus)) {
			airStatus = new AirStateStandard(AirStateStandard.defaultState);
		} else {
			airStatus = new AirStateStandard(argairstatus);
		}
		airStatus.setIndex(argcurIndex);
		holderView = new AirStatusHolderView(this.mActivity);
		holderView.setIsShowAll(true);
		holderView.modecommlayout = (LinearLayout) parentView.findViewById(R.id.modecommlayout);
		holderView.modecommimage = (ImageButton) parentView.findViewById(R.id.modecommimage);
		holderView.modecommName1 = (TextView) parentView.findViewById(R.id.modecommName1);
		holderView.modecommName2 = (TextView) parentView.findViewById(R.id.modecommName2);
		holderView.modecomplexlayout = (LinearLayout) parentView.findViewById(R.id.modecomplexlayout);
		holderView.windspeedImage = (ImageButton) parentView.findViewById(R.id.windspeedImage);
		holderView.airmodeimage = (ImageButton) parentView.findViewById(R.id.airmodeimage);
		holderView.temperaturetv = (TextView) parentView.findViewById(R.id.temperaturetv);
		holderView.temperatureunittv = (TextView) parentView.findViewById(R.id.temperatureunittv);
		holderView.wind_left_rightimage = (ImageButton) parentView.findViewById(R.id.wind_left_rightimage);
		holderView.wind_up_downimage = (ImageButton) parentView.findViewById(R.id.wind_up_downimage);
		holderView.customernametv= (TextView) parentView.findViewById(R.id.customernametv);
		holderView.airspeedflagimage=(ImageButton) parentView.findViewById(R.id.airspeedflagimage);
		holderView.FillData(airStatus);
	}
	@Override
	public void onStart() {
		super.onStart();
		sendData="";
		_currentAirconStates="";
		getDialogManager().showDialog(Down_AirData, getActivity(), "加载空调码库...", null);
		Thread thread = new Thread() {
			@Override
			public void run() {
				SetACRemoteControlFragment.this.getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						command406.SendCommand_Get(mark_currIndex);
						UeiAirDataUtil ueiAriData = new UeiAirDataUtil();
						ueiAriData.setIsFirstReadLocal(true);
						ueiAriData.setdevID(argdevID);
						airData = ueiAriData.getAirData(argdevicecode);
						if (StringUtil.isNullOrEmpty(airData)) {
//							String msg="获取空调码库失败！";
							String msg=getString(cc.wulian.app.model.device.R.string.uei_remote_control_failed_get_library);
							Log.d(log_tag, msg);
							Toast.makeText(SetACRemoteControlFragment.this.getActivity(), msg, Toast.LENGTH_SHORT).show();
						} else {
							Log.d(log_tag, "获取空调码库成功！");
							//启动ACEService
							Log.d(log_tag, "_currentAirconStates="+_currentAirconStates);
							arrBytedata = Base64.decode(airData, Base64.DEFAULT);
							int startflag=-1;
							try {
								startflag = ACEService.ACEngineStart(arrBytedata, _currentAirconStates);	
							} catch (Exception ex) {
								Log.e(log_tag, ex.toString());
							}					
							if(ResultCode.SUCCESS==startflag){
								Log.d(log_tag, "ACEService.ACEngineStart启动成功!");
								isStart=true;
								getSendData();
								sendData="";
								ACEService_ACGetKeys();
							}else{
								isStart=false;
								Log.d(log_tag, "ACEService.ACEngineStart启动失败! startflag=="+startflag);
							}
							
						}
						getDialogManager().dimissDialog(Down_AirData,0);
					}
				});
				super.run();
			}
		};
		thread.start();
	}
	
	@Override
	public void onClick(View view) {
		//判断空调码库是否获取到
		if (StringUtil.isNullOrEmpty(airData)) {
			String strWarning=getString(cc.wulian.app.model.device.R.string.uei_remote_control_get_library_failed_hint);
			Toast.makeText(SetACRemoteControlFragment.this.getActivity(), strWarning, Toast.LENGTH_SHORT).show();
			return;
		}
		if(view.getTag()!=null&&!StringUtil.isNullOrEmpty(view.getTag().toString())){
			int keyid=-1;
			if(map_btnTagAirid!=null&&map_btnTagAirid.size()>0){
				String tag=view.getTag().toString();
				if(map_btnTagAirid.containsKey(tag)){
					keyid=map_btnTagAirid.get(tag);
				}
			}
			if(keyid==-1){
				if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
					Log.d(log_tag, "未找到对应的键值！");
				}
//				Toast.makeText(this.getActivity(), "未找到对应的键值！", Toast.LENGTH_SHORT).show();
				return;
			}
			int result=ACEService.ACProcessKey(Integer.valueOf(keyid));
			if(ResultCode.SUCCESS==result){
				Log.d(log_tag, "ACEService.ACProcessKey 执行成功！");
				getSendData();
				ACEService_ACGetKeys();
				holderView.FillData(airStatus);
			}else{
				Log.d(log_tag, "ACEService.ACProcessKey 执行失败！");
			}
		}else{
			switch (view.getId()) {
			case R.id.editorbtn:
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.DEVICE_SET)) {
					return;
				}
				editCustomerStatus();
				break;
			case R.id.send_to_airconditiontv:{
				if(!StringUtil.isNullOrEmpty(sendData)){
					mVibrator01.vibrate(300);
					String codeData=ueiCommEpdata.getEpdataForAirRun(sendData);
					ueiCommEpdata.sendCommand12(getContext(),codeData);//执行12命令
				}
			}
			default:
				break;
			}
		}
	}

	private void editCustomerStatus() {
		WLDialog.Builder builder = new Builder(this.mActivity);
		//"自定义模式名称"
//		String title=getString(cc.wulian.app.model.device.R.string.uei_html_custom_name);
		builder.setContentView(R.layout.aboutus_feedback_send_comments_dialog)
				.setTitle("自定义模式名称")
				.setPositiveButton(
						R.string.common_ok)
				.setNegativeButton(
						R.string.cancel)
				.setDismissAfterDone(false).setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						EditText oldPasswordEditTextView = (EditText) contentViewLayout
								.findViewById(R.id.aboutus_feedback_send_comments_et);
						airStatus.setCustomName(oldPasswordEditTextView.getText().toString());
						holderView.FillData(airStatus);
						dialog.dismiss();
					}
					public void onClickNegative(View contentViewLayout) {
						dialog.dismiss();
					}

				});
		dialog = builder.create();
		dialog.show();
	}
	
	

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(log_tag, "_states=" + _states);
	}

	/**
	 * 执行ACEService.ACGetKeys
	 */
	private void ACEService_ACGetKeys(){
		List<AirConState> states = new ArrayList<AirConState>();
		List<AirConFunction> aFunctions = new ArrayList<AirConFunction>();
		List<AirConWidgetStatus> status = new ArrayList<AirConWidgetStatus>();
		int result = ACEService.ACGetKeys(states, status, aFunctions);
		if (result == ResultCode.SUCCESS && aFunctions != null && states != null) {
			Log.d(log_tag, "ACEService.ACGetKeys 执行成功！");
			initIRDevice(aFunctions);			
			AirConState[] allStates = new AirConState[states.size()];
			if(allStates.length > 0) {
	        	states.toArray(allStates);
	        }	
			if(states != null) {
				_states = states.toString();
			}			
			for(AirConFunction aircon:aFunctions){
				Log.d("air_fun", "aircon.Id="+aircon.Id+" aircon.Name="+aircon.Name+" aircon.funtype="+aircon.getFunctionType());
			}
			setAirStatus(allStates);
		}else{
			Log.d(log_tag, "ACEService.ACGetKeys 执行失败！");
		}
	}
	/**
	 * 初始化设备对象
	 * @param aFunctions
	 */
	private void initIRDevice(List<AirConFunction> aFunctions){
		if(airConIRDevice==null){
			String [] functions = new String[aFunctions.size()];
			for (int i = 0; i < aFunctions.size(); i++) {
				AirConFunction af = aFunctions.get(i);
				if (af != null) {
					functions[i] = af.Name;
					this._currentAirConFunctions.add(af);
				}
			}
			airConIRDevice = new com.uei.control.AirConDevice(0, "Z", argdevicecode,
					"", 
					"", 
					this._currentAirConFunctions, 
					Base64.decode(this.airData, Base64.DEFAULT),
					this._currentAirconStates);
			airConIRDevice.Codeset = this.airData;
			Log.d(log_tag, "airConIRDevice已初始化！");
		}
	}
	/**
	 * 设置airStatus，并且刷新界面
	 */
	private void setAirStatus(final AirConState[] states){
		this.getActivity().runOnUiThread(new Runnable() {
			@Override
			public void run() {
				try {
					if(states != null) {
						UeiAirStateSwap stateswap=new UeiAirStateSwap(airConIRDevice, states);
						map_btnTagAirid=stateswap.GetBtnTagsValue();
						AirStateStandard airstateStandard=stateswap.ConvertToLocalState();
						airStatus.setFanspeed(airstateStandard.getFanspeed());
						airStatus.setMode(airstateStandard.getMode());
						airStatus.setStatus(airstateStandard.getStatus());
						airStatus.setSwing_left_right(airstateStandard.getSwing_left_right());
						airStatus.setSwing_up_down(airstateStandard.getSwing_up_down());
						airStatus.setTemperature(airstateStandard.getTemperature());
						airStatus.setTemperature_unit(airstateStandard.getTemperature_unit());
						holderView.FillData(airStatus);
						if(!StringUtil.isNullOrEmpty(airStatus.getCustomName())){
							holderView.customernametv.setVisibility(View.VISIBLE);
						}
//						getSendData();
//						_currentAirconStates = ACEService.ACEngineStop();
					}						
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	/**
	 * 检索冷气设备的IR数据
	 */
	private void getSendData(){
		byte[] irData = ACEService.ACGetLastKeyPatternData();
		if(irData!=null&&irData.length>0){
			sendData=bytesToHexString(irData);
			Log.d(log_tag, "ACEService.ACGetLastKeyPatternData 执行成功！\r\nsendData="+sendData);
		}else{
			Log.d(log_tag, "ACEService.ACGetLastKeyPatternData 执行失败！");
		}
	}
	
	@SuppressLint("DefaultLocale")
	private void saveAirData(){
		if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.DEVICE_SET)) {
			return;
		}
		if(isSaving){
			return;
		}
		if(!StringUtil.isNullOrEmpty(sendData)){
			//正在保存...
			getDialogManager().showDialog(Save_QuickKey, getActivity(), "", null);
			setUseIndex();
			Log.d(log_tag, "开始执行保存操作！saveIndex="+saveIndex);
			isSaving=true;
			String codeData=ueiCommEpdata.getEpdataForAirSave(sendData,saveIndex);
			ueiCommEpdata.sendCommand12(getContext(),codeData.toUpperCase());//执行12命令
		}else{
			Log.d(log_tag, "发送码为空，不能保存！");
		}
	}	
	
	/**
	 * 设置当前保存时使用的快捷键索引
	 */
	private void setUseIndex(){
		saveIndex="0001";
		if(argisAdd){
			if(!StringUtil.isNullOrEmpty(_currentIndexStr)){
				int indexInt=Integer.parseInt(_currentIndexStr)+1;
				saveIndex=addZeroForNum(indexInt+"",4);
			}
		}else{				
			saveIndex=argcurIndex;
		}
	}
	private void save406(){
		 	airStatus.setIndex(saveIndex);
	        com.alibaba.fastjson.JSONObject jsonitemNew=airStatus.getJsonItem();
	        com.alibaba.fastjson.JSONArray  jsonarrayCurr=null;
	        
	        if(StringUtil.isNullOrEmpty(argcurvirkey)){
	        	jsonarrayCurr=new JSONArray(); 
			}else{
				try {
					jsonarrayCurr =com.alibaba.fastjson.JSONArray.parseArray(argcurvirkey);	
				} catch (com.alibaba.fastjson.JSONException e) {
					Log.e(log_tag, "json解析错误！~~~~ json="+argcurvirkey);
					jsonarrayCurr=new JSONArray(); 
				}		       
			} 
	        JSONObject jsonUpdateData=new JSONObject();
	        jsonUpdateData.put("b", argbrandname);
	        jsonUpdateData.put("m", argbrandtype);
	        jsonUpdateData.put("nm", "");
	        if(argisAdd){	        
	        	jsonarrayCurr.add(jsonitemNew);
	        	jsonUpdateData.put("kcs", jsonarrayCurr);
	        	command406.SendCommand_Update("3_"+argdevicecode, jsonUpdateData.toJSONString());
	        	command406.SendCommand_Add(mark_currIndex, saveIndex);
	        }
	        else{
	        	//这个需要把相应节点的值替换掉
	        	int markIndex=0;	        	
	        	for(int i=0;i<jsonarrayCurr.size();i++){
	        		String ac=jsonarrayCurr.getJSONObject(i).getString("ac");
	        		if(ac.endsWith(saveIndex)){
	        			markIndex=i;
	        			break;
	        		}
	        	}
	        	jsonarrayCurr.set(markIndex, jsonitemNew);
	        	jsonUpdateData.put("kcs", jsonarrayCurr);
	        	command406.SendCommand_Update("3_"+argdevicecode, jsonUpdateData.toJSONString());
	        }
	}
	@Override
	public void Reply406Result(Command406Result result) {
		Log.d(log_tag, " result.key="+result.getKey()+"result.data="+result.getData());
		if(result.getKey().equals(mark_currIndex)){
			_currentIndexStr=result.getData();
//			Toast.makeText(this.getActivity(), "maxIndex="+_currentIndexStr, Toast.LENGTH_SHORT).show();
			Log.d(log_tag, "_currentIndexStr="+_currentIndexStr);
		}else{
			getDialogManager().dimissDialog(Save_QuickKey,0);
			Intent data=new Intent();
			data.putExtra("airstatus", airStatus.getStatus());
			data.putExtra("index", airStatus.getIndex());
			data.putExtra("customName", airStatus.getCustomName());
			if(this.getActivity()!=null){
				this.getActivity().setResult(1, data);
//			this.getActivity().setResult(1);
				this.getActivity().finish();
			}
		}
	}

	@Override
	public void Reply406Result(List<Command406Result> results) {
				
	}
	private String addZeroForNum(String str,int strLength) {
	     int strLen = str.length();
	     StringBuffer sb = null;
	     while (strLen < strLength) {
	           sb = new StringBuffer();
	           sb.append("0").append(str);// 左(前)补0
	           str = sb.toString();
	           strLen = str.length();
	     }
	     return str;
	 }
	public void onEventMainThread(Command406Result result){
		Log.d(log_tag, " result"+result.getKey()+"result.data="+result.getData());	
		if(result.getKey().equals("currentIndex")){
			_currentIndexStr=result.getData();
			Log.d(log_tag, "_currentIndexStr="+_currentIndexStr);
		}else{
			//此处无代码
		}
	}
	
	public void onEventMainThread(DeviceEvent event){
		String epdata="";
		String eptype="";
		if(event!=null&&event.deviceInfo!=null&&event.deviceInfo.getDevEPInfo()!=null){
			epdata=event.deviceInfo.getDevEPInfo().getEpData();
			eptype=event.deviceInfo.getDevEPInfo().getEpType();
		}
		else{
			//此处无代码
		}		
		if(eptype.equals("23")){
			Log.d("sendCommad", "epdata="+epdata);
			if (isSaving)
			{
				isSaving=false;
				//0B010028
				if(!StringUtil.isNullOrEmpty(epdata)){
					String returncode=epdata.substring(4, epdata.length());
					if(returncode.equals(saveIndex)){
						save406();
					}else{
						getDialogManager().dimissDialog(Save_QuickKey,0);
//						Toast.makeText(this.mActivity,"epdata="+epdata+" currIndex="+_currentIndexStr, Toast.LENGTH_SHORT).show();
					}
				}else{
					getDialogManager().dimissDialog(Save_QuickKey,0);
//					Toast.makeText(this.mActivity,"epdata=null", Toast.LENGTH_SHORT).show();
				}
			}
			else{
				if(epdata.equals("000100")){					
					Toast.makeText(this.mActivity,R.string.html_user_operation_success, Toast.LENGTH_SHORT).show();
				}
				else{
//					String msg=String.format()
					inputLogD("Failed epdata="+epdata);
//					Toast.makeText(this.mActivity, "Failed epdata="+epdata, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	private void inputLogD(String msg)
	{
		if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
			Log.d(log_tag, msg);
		}

	}
	/**
	  * 数组转换成十六进制字符串
	  * @param bArray
	  * @return HexString
	  */
	private final String bytesToHexString(byte[] bArray) {
		StringBuffer bytesb=new StringBuffer();
		StringBuffer sb = new StringBuffer(bArray.length);
		String sTemp;
		for (int i = 0; i < bArray.length; i++) {
			bytesb.append(bArray[i]+",");
			sTemp = Integer.toHexString(0xFF & bArray[i]);
			if (sTemp.length() < 2)
				sb.append(0);
			sb.append(sTemp);
		}
		Log.d(log_tag, "byte[]="+bytesb);	
		return sb.toString();
	}
}
