package cc.wulian.smarthomev5.fragment.uei;

import java.text.MessageFormat;
import java.util.List;

import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLToast;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Resource;
import cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Resource.WL23_ResourceInfo;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.uei.SetACRemooteControlActivity;
import cc.wulian.smarthomev5.dao.Command406_DeviceConfigMsg;
import cc.wulian.smarthomev5.dao.ICommand406_Result;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.entity.uei.AirStateStandard;
import cc.wulian.smarthomev5.entity.uei.UEIEntity_Air;
import cc.wulian.smarthomev5.entity.uei.UeiUiArgs;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.SendMessage;

public class ACRemoteControlFragment extends WulianFragment implements ICommand406_Result{

	private GridView gvtype;
	UeiUiArgs args_value=null;
	UEIEntity_Air uei=null;
	ACTypeAdapter typeAdapter=null;
	String title="";
	Command406_DeviceConfigMsg command406;
	private String curEpdata="";
	private TextView addWarningTv=null;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		if(bundle!=null){
			args_value=bundle.getParcelable("args");
			uei=(UEIEntity_Air)args_value.ConvertToEntity();
			command406=new Command406_DeviceConfigMsg(this.mActivity);
			command406.setDevID(args_value.getDevID());
			command406.setGwID(args_value.getGwID());
			command406.setConfigMsg(this);
			command406.SendCommand_Get(uei.getKey());
		}
		initBar();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_haierac_remotecontrol, container, false);
	}
	@Override
	public void onViewCreated(View paramView, Bundle paramBundle){
	    super.onViewCreated(paramView, paramBundle);
	    initView(paramView);
//		command406.SendCommand_Get(args_value.getKey());
	  }

	
	 private void initBar() {
		String brandTypeName = "";
		WL23_ResourceInfo resourceInfo = WL_23_IR_Resource.getResourceInfo(uei
				.getDeviceType());
		if (resourceInfo.name > 0) {
			brandTypeName = getString(resourceInfo.name);
		}
		title=uei.getBrandName()+brandTypeName;
	    this.mActivity.resetActionMenu();
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);	   
	    getSupportActionBar().setDisplayIconTextEnabled(true);
	    getSupportActionBar().setDisplayShowTitleEnabled(true);
 
	    getSupportActionBar().setTitle(title); 
		if(args_value.getViewMode()==0){
		    getSupportActionBar().setDisplayShowMenuEnabled(true);	  
			 getSupportActionBar().setIconText(getString(R.string.nav_device_title));
			 getSupportActionBar().setDisplayIconEnabled(true);
			 getSupportActionBar().setDisplayShowMenuTextEnabled(false);	 
			 getSupportActionBar().setRightIcon(R.drawable.common_use_add);
			 getSupportActionBar().setRightMenuClickListener(
						new OnRightMenuClickListener() {
							@Override
							public void onClick(View v) {
								startActivityForEditState(true,null);
							}
				});
	    }else if(args_value.getViewMode()==1){
		     getSupportActionBar().setDisplayShowMenuEnabled(false);	  
	    	 getSupportActionBar().setIconText(getString(R.string.about_back));
	    	 getSupportActionBar().setDisplayIconEnabled(false);
	    	 getSupportActionBar().setDisplayShowMenuTextEnabled(true);	 
	    	 getSupportActionBar().setRightIconText(R.string.common_ok);
	    	 getSupportActionBar().setRightMenuClickListener(new OnRightMenuClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent=new Intent();
						intent.putExtra("epdata", curEpdata);
						mActivity.setResult(1, intent);
						mActivity.finish();
					}
				});
	    }
	 }
	 
	 private void initView(View paramView){
		 gvtype = (GridView) paramView.findViewById(R.id.gv_airconditioner_type);
		 addWarningTv= (TextView) paramView.findViewById(R.id.addWarningTv);
		 if(args_value.getViewMode()==0){
			 addWarningTv.setText("请添加快捷模式");
		 }else if(args_value.getViewMode()==1){
			 addWarningTv.setText("暂无快捷模式");
		 }
		 this.mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				typeAdapter=new ACTypeAdapter(ACRemoteControlFragment.this.mActivity,uei.getAirStates());
				int viewMode=ACRemoteControlFragment.this.args_value.getViewMode();
				typeAdapter.SetViewMode(viewMode);
				if(viewMode==0){
					typeAdapter.SetOnClickPopupWidowItem(onClickPopuWindowItem_fordevice);
				}
				else if(viewMode==1){
					typeAdapter.SetOnClickPopupWidowItem(onClickPopuWindowItem_forhouse);
				}
				gvtype.setAdapter(typeAdapter);
			}
		});
	}
	 
	 
	 String deleteIndex="";
	 ACTypeAdapter.OnClickPopupWidowItem onClickPopuWindowItem_fordevice=new ACTypeAdapter.OnClickPopupWidowItem() {
		
		@Override
		public void ItemUpdateClick(AirStateStandard airStates) {			
			startActivityForEditState(false,airStates);
		}
		
		@Override
		public void ItemDeleteClick(AirStateStandard airStates) {
			com.alibaba.fastjson.JSONArray jsonarrayCurr =com.alibaba.fastjson.JSONArray.parseArray(uei.getVirkey());	
			int markIndex=0;
        	for(int i=0;i<jsonarrayCurr.size();i++){
        		String ac=jsonarrayCurr.getJSONObject(i).getString("ac");
        		if(ac.endsWith(airStates.getIndex())){
        			markIndex=i;
        			break;
        		}
        	}
			deleteIndex=airStates.getIndex();
        	jsonarrayCurr.remove(markIndex);
			JSONObject jsonUpdatedata=new JSONObject();
			jsonUpdatedata.put("b", uei.getBrandName());
			jsonUpdatedata.put("m", uei.getBrandType());
			jsonUpdatedata.put("kcs", jsonarrayCurr);
			command406.SendCommand_Update(uei.getKey(), jsonUpdatedata.toJSONString());
			commandQuickcode(0,airStates.getIndex());			
			uei.getAirStates().remove(markIndex);
			typeAdapter.swapData(uei.getAirStates());
		}
		@Override
		public void ItemShortTimeClick(AirStateStandard airStates) {
			if(!StringUtil.isNullOrEmpty(airStates.getIndex())){
//				Toast.makeText(ACRemoteControlFragment.this.getActivity(), "快捷码："+airStates.getIndex(), Toast.LENGTH_SHORT).show();
				commandQuickcode(1,airStates.getIndex());
			}			
		};
	};
	
	 ACTypeAdapter.OnClickPopupWidowItem onClickPopuWindowItem_forhouse=new ACTypeAdapter.OnClickPopupWidowItem() {
		
		@Override
		public void ItemUpdateClick(AirStateStandard airStates) {			
			//管家功能无此操作，这里不要写代码
		}
		
		@Override
		public void ItemDeleteClick(AirStateStandard airStates) {
			//管家功能无此操作，这里不要写代码
		}
		@Override
		public void ItemShortTimeClick(AirStateStandard airStates) {
			curEpdata=getEpdata(1,airStates.getIndex());
		};
	};
	/**
	 * 跳转到编辑空调状态的页面
	 * @param isAdd 是否添加
	 * @param airStates 当前状态
	 */
	private void startActivityForEditState(boolean isAdd,AirStateStandard airStates){
		Intent intent = new Intent(mActivity,SetACRemooteControlActivity.class);
		Bundle args=new Bundle();
		args.putBoolean("isAdd", isAdd);
		args.putString("title", title);
		if(airStates==null){
			args.putString("airstatus",AirStateStandard.defaultState);
		}
		else{
			args.putString("airstatus",airStates.getStatus());
		}
		args.putString("devicecode", uei.getDeviceCode());
		args.putString("brandtype", uei.getBrandType());
		args.putString("brandname", uei.getBrandName());
		args.putString("gwID", uei.getGwID());
		args.putString("devID", uei.getDevID());
		args.putString("ep", args_value.getEp());
		args.putString("epType", args_value.getEpType());
		args.putString("virkey", uei.getVirkey());
		if(isAdd==false){
			args.putString("curIndex", airStates.getIndex());
		}
		intent.putExtra("args",args);
		int requestCode=isAdd?1:0;
		startActivityForResult(intent, requestCode);
	}
	 
	@Override
	public void onActivityResult(final int requestCode, int resultCode, final Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode>-1&&data!=null&&resultCode>0){
			this.mActivity.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					String strAirstate=data.getStringExtra("airstatus");
					String curIndex=data.getStringExtra("index");
					String customName=data.getStringExtra("customName");
					AirStateStandard newAirState=new AirStateStandard(strAirstate);
					newAirState.setCustomName(customName);
					newAirState.setIndex(curIndex);
					
					if(requestCode==0){//修改
						for(AirStateStandard airState:uei.getAirStates()){
							if(airState.getIndex().equals(curIndex)){
								airState.setCustomName(customName);
								airState.setIndex(curIndex);
								airState.setStatus(strAirstate);
								break;
							}
						}						
					}else if(requestCode==1){//添加
						Log.d("WL_23", "requestCode==0 添加操作回调");						
//						uei.getAirStates().add(newAirState);
					}
					command406.SendCommand_Get(uei.getKey());
//					typeAdapter.swapData(uei.getAirStates());
				}
			});
		}
	}
	/**
	 * 获取快捷键命令
	 * @param mode 0 删除；1 执行
	 * @param index 快捷键的索引
	 */
	private String getEpdata(int mode, String index) {
		String epdata = "";
		if (mode == 1) {
			epdata = "OC" + index;
		} else if (mode == 0) {
			epdata = "OD" + index;
		}
		return epdata;
	}
	
	/**
	 * 发送快捷键命令
	 * @param mode 0 删除；1 执行
	 * @param index 快捷键的索引
	 */
	private void commandQuickcode(int mode, String index){
		String sendEpData=getEpdata(mode,index);
		JSONObject jsonObj = new JSONObject();
        jsonObj.put("cmd","12");
		jsonObj.put("gwID", args_value.getGwID());
		jsonObj.put("devID", args_value.getDevID());
		jsonObj.put("ep", args_value.getEp());
		jsonObj.put("epType", args_value.getEpType());
		jsonObj.put("epData", sendEpData);
//		Toast.makeText(this.getActivity(), "epdata="+sendEpData, Toast.LENGTH_SHORT).show();
        String jsonData = jsonObj.toString();
        com.alibaba.fastjson.JSONObject msgBody = com.alibaba.fastjson.JSONObject.parseObject(jsonData);
		SendMessage.sendControlDevMsg(args_value.getGwID(),args_value.getDevID(), args_value.getEp(),
				args_value.getEpType(),sendEpData);
	}
	
	@Override
	public void Reply406Result(final Command406Result result) {
		this.mActivity.runOnUiThread(new Runnable() {
			@Override
			public void run() {
				Log.d("ACRemoteControlFragment", "mode="+result.getMode());
				if(result.getMode().equals(Command406_DeviceConfigMsg.mode_update)){
					if(!StringUtil.isNullOrEmpty(deleteIndex)){
						int deleteitem=0;
						for(int i=0;i< uei.getAirStates().size();i++){
							AirStateStandard airstates=uei.getAirStates().get(i);
							if(airstates.getIndex().equals(deleteIndex)){
								deleteitem=i;
								break;
							}
						}
						if(deleteitem>-1){
//							typeAdapter.swapData(uei.getAirStates());
							deleteIndex="";
							command406.SendCommand_Get(uei.getKey());
						}
					}
				}else if(result.getMode().equals(Command406_DeviceConfigMsg.mode_get)){
					//currentIndex：快捷键最大索引
					if(!result.getKey().equals("currentIndex")){
						String data=result.getData();
						Log.d("ACRemoteControlFragment", "data="+data);
						uei.setValue(data);
						typeAdapter.swapData(uei.getAirStates());
						if(typeAdapter.getCount()>0){
							addWarningTv.setVisibility(View.GONE);
							gvtype.setVisibility(View.VISIBLE);
						}else {
							addWarningTv.setVisibility(View.VISIBLE);
							gvtype.setVisibility(View.GONE);
						}
					}
				}				
			}
		});
	}

	@Override
	public void Reply406Result(List<Command406Result> results) {
		
		
	} 
}
