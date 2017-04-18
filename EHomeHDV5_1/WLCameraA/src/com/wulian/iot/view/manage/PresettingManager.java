package com.wulian.iot.view.manage;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONObject;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.Handler.Callback;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.GridView;
import android.widget.AdapterView.OnItemClickListener;
import com.wulian.icam.R;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.bean.PresettingModel;
import com.wulian.iot.server.controller.CamPresetting;
import com.wulian.iot.server.controller.logic.CamPresettingLogicImpl;
import com.wulian.iot.server.receiver.Return406_Receiver;
import com.wulian.iot.server.receiver.Smit406_Receiver;
import com.wulian.iot.utils.IotUtil;
import com.wulian.iot.view.adapter.PresetAdapter;
import com.wulian.iot.widght.PresetWindow;
import com.wulian.iot.widght.PresetWindow.DialogPojo;
import com.yuantuo.customview.ui.WLToast;
public class PresettingManager {
	private static String TAG = "PresettingManager";
	private PresetWindow presetWindow = null;
	private Context mContext = null;
	private GridView choose = null;
	private PresetAdapter presettingAdapter = null;
	private EditText specialTxt = null;
	private static PresettingManager instance = null;
	private CamPresetting camPresettingImpl = null;// 预置位 add syf
	private static List<PresettingModel> pModels = null;
	private String presettingPath = null;//add syf 预置位路径
	private Return406_Receiver return406_Receiver = null;
	private int position = -2;
	private Handler runOnUiThread = null;
	private List<Smit406_Pojo_Item> smit406_Pojo_Items = null;
	private List<Smit406_Pojo_Item> smit406_Pojo_Items_operation = null;
	private PresettingManager(Context context){
		this.mContext = context;
		this.initView();
		  this.registerReceiver();
		this.camPresettingImpl = new CamPresettingLogicImpl(mContext);
		this.runOnUiThread = new Handler(Looper.getMainLooper(), dataCallback);
	}
  private Callback dataCallback = new Callback() {
	@Override
	public boolean handleMessage(Message msg) {
	     switch(msg.what){
	     case HandlerConstant.SUCCESS:
	    		if (presettingAdapter != null) {
	    			  Log.i(TAG, "dataCallback("+smit406_Pojo_Items.size()+")");
	    			  setSmit406_Pojo_Items_operation(smit406_Pojo_Items);
					  presettingAdapter.add(getPresettingModels(presettingPath,smit406_Pojo_Items), true);
					  setSmit406_Pojo_Items(null);
				}					
	    	 break;
	     }
		return false;
	}
};
	public static PresettingManager getInstance(Context context){
		if(instance == null){
			synchronized(PresettingManager.class){
				if(instance == null){
					instance = new PresettingManager(context);
				}
			}
		} else {
			if(context!= instance.getmContext()){
				instance.setmContext(context);
			}
		}
		return instance;
	}
	public void setmContext(Context mContext) {
		this.mContext = mContext;
	}
	public Context getmContext() {
		return mContext;
	}
	public void setSmit406_Pojo_Items(List<Smit406_Pojo_Item> smit406_Pojo_Items) {
		this.smit406_Pojo_Items = smit406_Pojo_Items;
	}
	public void setSmit406_Pojo_Items_operation(
			List<Smit406_Pojo_Item> smit406_Pojo_Items_operation) {
		this.smit406_Pojo_Items_operation = smit406_Pojo_Items_operation;
	}
	public void destroy(){
		dismiss();
		unregisterReceiver();
		presettingPath = null;
		presettingAdapter = null;
		presetWindow = null;
		instance = null;
	}
	private void initView() {
		if (presetWindow == null) {
			  presetWindow = new PresetWindow(mContext, R.layout.preset_popwindow) {
				@Override
				public void getView(View view) {
					choose = (GridView) view.findViewById(R.id.gridViews);
					choose.setAdapter(getPresetAdapter());
				}
			};
			initEvent();
		}
	}
	public void showDialog(Context mContext,int position){
		if(presetWindow!=null){
			presetWindow.alertDialog(getDialogPojo(mContext));
			this.position = position;
		}
	}
	public void  showPopWindow(final View view,final LinearLayout locationLayout,final String gwId){
		if(presetWindow!=null){
			presetWindow.runOnUiThread(new Runnable() {
				@Override
				public void run() {
					initData(gwId);
					initLocation(view,locationLayout);
					presettingAdapter.add(getPresettingModels(presettingPath,smit406_Pojo_Items), true);
				}
			});
		}
	}
	public void dismiss() {
		if(presetWindow!=null){
			presetWindow.dismiss();
		}
	}
	private Smit406_Pojo smit406_Pojo(){
		return new Smit406_Pojo(Smit406_Receiver.Query,null,null);
	}
	private Smit406_Pojo smit406_Pojo(String name,String location){
		Log.i(TAG,"smit406_Pojo_Items_operation("+smit406_Pojo_Items_operation.size()+")" );
		for(Smit406_Pojo_Item obj:smit406_Pojo_Items_operation){
			   if(obj.getLocation().equals(location) ){//位置相同
				   Log.i(TAG, name);
				   obj.setName(name);
			   }
		}
	        Map<String,Object> dataMap = new HashMap<String,Object>();
	        dataMap.put("datas",smit406_Pojo_Items_operation);
	        return new Smit406_Pojo(Smit406_Receiver.Update,Smit406_Receiver.DesktopCamera_406_Preset,dataMap);
	}
    private void initEvent() {
		choose.setOnItemClickListener(onItemClickListener);
		choose.setOnItemLongClickListener(onItemLongClickListener);
	}
   private void initLocation(View view,LinearLayout locationLayout){
		int[] location = new int[2];
		locationLayout.getLocationOnScreen(location);
		presetWindow.showAtLocation(view, Gravity.NO_GRAVITY, location[0],location[1] - view.getHeight());
   }
   private void initData(final String presettingPath){
	   this.presettingPath = presettingPath;
      send406Broadcast();
   }
   private void registerReceiver(){
	   if(return406_Receiver == null){
		   return406_Receiver = new Return406_Receiver() {
			@Override
			public void retrunData(final Return406 return406) {
				Log.i(TAG, "returnData 406");
				if(return406!=null){
					Log.i(TAG, "retrunData("+return406.getSmit406_Pojo_Items().size()+")");
					setSmit406_Pojo_Items(return406.getSmit406_Pojo_Items());
					runOnUiThread.sendEmptyMessage(HandlerConstant.SUCCESS);
				}
			}
     		};
     		IntentFilter mIntentFilter =  new IntentFilter();
     		mIntentFilter.addAction(Return406_Receiver.ACTION);
			mContext.registerReceiver(return406_Receiver, mIntentFilter);
	   }
   }
   private void unregisterReceiver(){
	   if(return406_Receiver!=null){
		   mContext.unregisterReceiver(return406_Receiver);
	   }
	   return406_Receiver = null;
   }
   private PresetAdapter getPresetAdapter(){
	   if(presettingAdapter == null){
		   presettingAdapter = new PresetAdapter(mContext,null) {
			@Override
			public void getView(View view, final int position) {
				//删除当前数据并且同步到网关
				view.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						if(view!=null){
							PresettingModel presettingModel = presettingAdapter.getItem(position);
							if(onItemMenuClickListener!=null){
								onItemMenuClickListener.clearDevicePresetting(presettingModel.getRotateIndex());
							}
							presettingAdapter.delete(position);
							IotUtil.delFilePassWay(presettingPath,presettingModel.getRotateIndex());
							send406Broadcast("", String.valueOf(presettingModel.getRotateIndex()));
						}
					}
				});
			}
		 };
	   }
	   return presettingAdapter;
   }
	private String getResourcesById(int id){
		return mContext.getResources().getString( id);
	}
	private Map<String,DialogInterface.OnClickListener>getMapDialogOnClick(){
		Map<String,DialogInterface.OnClickListener> mapOnClick = new HashMap<String,DialogInterface.OnClickListener>();
		mapOnClick.put(DialogPojo.CLOSK, closeDialog);
		mapOnClick.put(DialogPojo.CONFIRM, confirmDialog);
		return mapOnClick;
	}
	@SuppressLint("NewApi")
	private View getView(){
		specialTxt = new EditText(mContext);
		specialTxt.setSingleLine(true);
		specialTxt.setHint(R.string.input_position_name);
		specialTxt.setBackground(null);
		return specialTxt;
	}
	private DialogPojo getDialogPojo(Context mContext){
		PresetWindow.DialogPojo dialogPojo = new PresetWindow. DialogPojo();
		dialogPojo.setTitle(getResourcesById(R.string.preset_title));
		dialogPojo.setmContext(mContext);
		dialogPojo.setView(getView());
		dialogPojo.setCloseTitle(getResourcesById(R.string.common_cancel));
		dialogPojo.setConfirmTitle(getResourcesById(R.string.common_sure));
		dialogPojo.setOnClickListenerMap(getMapDialogOnClick());
		return dialogPojo;
	}
	private OnItemLongClickListener onItemLongClickListener = new OnItemLongClickListener() {
		@Override
		public boolean onItemLongClick(AdapterView<?> parent, View view,
				int position, long id) {
			Log.i(TAG, "===onItemLongClick===");
			PresettingModel presettingModel = presettingAdapter.getItem(position);
			if(!(presettingModel.getpName().equals(""))){
				presettingModel.setExit(true);
				presettingAdapter.notifyDataSetChanged();//显示叉号
				}
			return true;
		}
	};
	// adapter call back item is grivdview
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			PresettingModel presettingModel = presettingAdapter.getItem(position);
			if(presettingModel.getpName().equals("")){
				if(onItemMenuClickListener!=null){
					onItemMenuClickListener.onMenuItemClick(position);
				}
				Log.i(TAG, "show alert dialog");
				return;
			}
			if(onItemMenuClickListener!=null){
				onItemMenuClickListener.rotateDevicePresetting(presettingModel.getRotateIndex());
			}
			dismiss();
			Log.i(TAG, "rotation location");
			return;
		}
	};
	public OnItemMenuClickListener<PresettingModel> onItemMenuClickListener = null;
	public void setOnItemMenuClickListener(OnItemMenuClickListener<PresettingModel> onItemMenuClickListener) {
		this.onItemMenuClickListener = onItemMenuClickListener;
	}
	   public static  interface OnItemMenuClickListener<T>{
	    	public void onMenuItemClick(int position);
	    	public void rotateDevicePresetting(int position);
	    	public void createDevicePresettingImage(String title);
	    	public void saveDevicePresetting(int position);
	    	public void clearDevicePresetting(int position);
	    }
	DialogInterface.OnClickListener closeDialog = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.i(TAG, "===closeDialog===");
			InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(specialTxt.getWindowToken(), 0);
		}
	};
	DialogInterface.OnClickListener confirmDialog = new DialogInterface.OnClickListener() {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			Log.i(TAG, "===confirmDialog===");
			String title = specialTxt.getText().toString();
			if(checkStrLegal(title)){
				if(onItemMenuClickListener!= null){
					onItemMenuClickListener.createDevicePresettingImage(title+PresettingManager.this.position);
					onItemMenuClickListener.saveDevicePresetting(PresettingManager.this.position);
				}
				send406Broadcast(title,String.valueOf(PresettingManager.this.position));
			}
		}
	};
	private void send406Broadcast(){
		Log.i(TAG,"send406Broadcast("+"Query"+")");
		Intent mIntent = new Intent();
		mIntent.setAction(Smit406_Receiver.ACTION);
		mIntent.putExtra(Smit406_Receiver.OPER_406, smit406_Pojo());
		mContext.sendBroadcast(mIntent);
		setSmit406_Pojo_Items_operation(null);
	}
	private void send406Broadcast(String data,String location){
		Log.i(TAG,"send406Broadcast("+"Update"+")");
		if(smit406_Pojo_Items_operation == null){
			WLToast.showToast(mContext, mContext.getResources().getString(R.string.camera_preset_syn), Toast.LENGTH_SHORT);
			return;
		}
		Intent mIntent = new Intent();
		mIntent.setAction(Smit406_Receiver.ACTION);
		mIntent.putExtra(Smit406_Receiver.OPER_406, smit406_Pojo(data,location));
		mContext.sendBroadcast(mIntent);
		setSmit406_Pojo_Items_operation(null);
	}
	private boolean checkStrLegal(String posName){
		if(posName ==null||posName.trim().equals("")){
			CustomToast.show(mContext,mContext.getResources().getString(R.string.position_name_notnull));
			return false;
		}  else if(posName.contains("<")||posName.contains(">")) {
			CustomToast.show(mContext,mContext.getResources().getString(R.string.exception_1104));
			return false;
		} else if(posName.length()>10){
			CustomToast.show(mContext,mContext.getResources().getString(R.string.exception_1002));
			return false;
		}
		return true;
	}
	/** 获取本地预知位置信息 */
	private final List<PresettingModel> getPresettingModels(String presettingPath,List<Smit406_Pojo_Item> smit406_Pojo_Items) {
		return camPresettingImpl.findPresettingListAll(presettingPath,smit406_Pojo_Items);
	}
	@SuppressWarnings("serial")
	public static class Return406 implements Serializable{
		public Return406(){
			
		}
		public Return406(String gwId,List<Smit406_Pojo_Item> smit406_Pojo_Items){
			this.gwId = gwId;
			this.smit406_Pojo_Items = smit406_Pojo_Items;
		}
		private String gwId;
		private List<Smit406_Pojo_Item> smit406_Pojo_Items;
		public void setGwId(String gwId) {
			this.gwId = gwId;
		}
		public String getGwId() {
			return gwId;
		}
		public void setSmit406_Pojo_Items(
				List<Smit406_Pojo_Item> smit406_Pojo_Items) {
			this.smit406_Pojo_Items = smit406_Pojo_Items;
		}
		public List<Smit406_Pojo_Item> getSmit406_Pojo_Items() {
			return smit406_Pojo_Items;
		}
	}
	@SuppressWarnings("serial")
	public static class Smit406_Pojo implements Serializable{
		private String operate;
        private String key;
        private Map<String, Object> datas;
        public Smit406_Pojo(){
        	
        }
        public Smit406_Pojo(String operate,String key,Map<String, Object> datas){
        	this.datas = datas;
        	this.key = key;
        	this.operate = operate;
        }
        public void setDatas(Map<String, Object> datas) {
			this.datas = datas;
		}
        public Map<String, Object> getDatas() {
			return datas;
		}
        public void setKey(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
        public void setOperate(String operate) {
			this.operate = operate;
		}
        public String getOperate() {
			return operate;
		}
    }
    @SuppressWarnings("serial")
	public static class Smit406_Pojo_Item implements Serializable{
        public Smit406_Pojo_Item(String name, String location, String picture) {
            this.name = name;
            this.location = location;
            this.picture = picture;
        }
        private String name;
        private String location;
        private String picture;
        public void setLocation(String location) {
            this.location = location;
        }
        public String getLocation() {
            return location;
        }
        public void setName(String name) {
            this.name = name;
        }
        public String getName() {
            return name;
        }
        public void setPicture(String picture) {
            this.picture = picture;
        }
        public String getPicture() {
            return picture;
        }
    }
}
