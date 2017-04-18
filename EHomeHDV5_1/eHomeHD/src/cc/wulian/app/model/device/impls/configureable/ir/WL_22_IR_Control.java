package cc.wulian.app.model.device.impls.configureable.ir;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.configureable.ConfigureableDeviceImpl;
import cc.wulian.app.model.device.impls.configureable.ir.IRViewBuilder.SelectIREpDataListener;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView.DeviceCategoryEntity;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.event.DeviceIREvent;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.utils.CmdUtil;
import de.greenrobot.event.EventBus;

/**
 * 位1:工作模式(1:学习,2:使用)<br/>
 * 位2～4:红外发射码(0～629)
 */
@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_IR_CONTROL}, category = Category.C_CONTROL)
public class WL_22_IR_Control extends ConfigureableDeviceImpl
{
	public static final String CURRENT_SHOW_DEVICE_LIST = "0";
	public static final String CURRENT_SHOW_FRAGMENT_DEVICE = "1";
	public static String CURRENT_SHOW_FRAGMENT = CURRENT_SHOW_FRAGMENT_DEVICE;
	private Map<String,Map<Integer,Integer>> categoryIcons = DeviceUtil.getIRCategoryDrawable();
	private IRViewBuilder builder;
	private IRGroupManager irGroupManager; 
	public WL_22_IR_Control( Context context, String type )
	{
		super(context, type);
	}

	@Override
	public void onDeviceUp(DeviceInfo devInfo) {
		super.onDeviceUp(devInfo);
		NetSDK.sendGetDevIRMsg(gwID, devID, ep, CmdUtil.MODE_BATCH_ADD);
	}

	@Override
	public Drawable getStateSmallIcon() {
		String categoyID = getDeviceInfo().getCategory();
		Drawable icon = getDefaultStateSmallIcon();
		if(categoryIcons.containsKey(categoyID)){
			icon = mResources.getDrawable(categoryIcons.get(categoyID).get(0));
		}
		return icon;
	}

	@Override
	public void onAttachView(Context context) {
		super.onAttachView(context);
		EventBus.getDefault().register(this);
	}

	@Override
	public void onDetachView() {
		// TODO Auto-generated method stub
		super.onDetachView();
		WL_22_IR_Control.CURRENT_SHOW_FRAGMENT = CURRENT_SHOW_FRAGMENT_DEVICE;
		Logger.debug("add:" + "study on onDetachView");
		EventBus.getDefault().unregister(this);
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData){
		StringBuilder sb = new StringBuilder();
		sb.append(getResources().getString(R.string.device_type_22));
		return sb.toString();
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		builder = new IRViewBuilder(inflater.getContext(),IRManager.getInstance().getIrGroupManager(getDeviceGwID(), getDeviceID()));
		builder.initControlView();
		return builder.createControlView(""); 
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		mViewCreated = true;
	}

	@Override
	public void onResume() {
		super.onResume();
		Logger.debug("add:" + "device on onResume");
		if(!StringUtil.equals(WL_22_IR_Control.CURRENT_SHOW_FRAGMENT, CURRENT_SHOW_FRAGMENT_DEVICE)){
			Activity activity = (Activity) mContext;
			Logger.debug("add:" + "device on close");
			activity.finish();
		}else if(!builder.isHasGrouptype() && mDevOnLine){
				Logger.debug("add:" + "study on getSelecttype");
				Intent intent = new Intent(mContext,DeviceSettingActivity.class);
				intent.putExtra(IRSettingFragment.DEV_ID, getDeviceID());
				intent.putExtra(IRSettingFragment.GW_ID, getDeviceGwID());
				intent.putExtra(AbstractDevice.SETTING_LINK_TYPE, AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
				intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, IRSettingFragment.class.getName());
				WL_22_IR_Control.CURRENT_SHOW_FRAGMENT = IRSettingFragment.CURRENT_SHOW_FRAGMENT_IR_SETTING;
				mContext.startActivity(intent);
	}
	}

	@Override
	public void initViewStatus() {
		Logger.debug("add:" + "device on createControlView");
		builder.createControlView("");					
	}
	
	 @Override
	public void onPause() {
		super.onPause();
		WL_22_IR_Control.CURRENT_SHOW_FRAGMENT = IRSettingFragment.CURRENT_SHOW_FRAGMENT_IR_SETTING;
		Logger.debug("add:" + "study on device onPause");
	}
	
	public Intent getSettingIntent() {
		Intent intent = new Intent(mContext,DeviceSettingActivity.class);
		intent.putExtra(IRSettingFragment.DEV_ID, getDeviceID());
		intent.putExtra(IRSettingFragment.GW_ID, getDeviceGwID());
		intent.putExtra(AbstractDevice.SETTING_LINK_TYPE, AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
		intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, IRSettingFragment.class.getName());
		return intent ;
	}
	
	@Override
	protected List<MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
		List<MenuItem> items = super.getDeviceMenuItems(manager);
		MenuItem settingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext
						.getString(cc.wulian.smarthomev5.R.string.set_titel));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				// add by yanzy:不允许被授权用户使用
				if(!UserRightUtil.getInstance().canDo(UserRightUtil.EntryPoint.DEVICE_SET)) {
					return;
				}

				Intent i = getSettingIntent();
				mContext.startActivity(i);
				manager.dismiss();
			}
		};
		if(isDeviceOnLine())
			items.add(settingItem);
		return items;
	}
	
	@Override
	public EditDeviceInfoView onCreateEditDeviceInfoView(LayoutInflater inflater) {
		EditDeviceInfoView view  = super.onCreateEditDeviceInfoView(inflater);
		ArrayList<DeviceCategoryEntity> entities= new ArrayList<EditDeviceInfoView.DeviceCategoryEntity>();
		for(String key : categoryIcons.keySet()){
			DeviceCategoryEntity entity = new DeviceCategoryEntity();
			entity.setCategory(key);
			entity.setResources(categoryIcons.get(key));
			entities.add(entity);
		}
		view.setDeviceIcons(entities);
		return view;
	}

	@Override
	public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater,String ep,
			String epData) {
		linkTaskControlEPData = new StringBuffer(epData);
		IRViewBuilder builder =new IRViewBuilder(inflater.getContext(),IRManager.getInstance().getIrGroupManager(getDeviceGwID(), getDeviceID()));
		builder.initControlView();
		String irType = null;
		if(!builder.isHasGrouptype()){
			irType = IRGroupManager.NO_TYPE;
		}else{
			if(epData != null && epData.length()>=4){
				int data = StringUtil.toInteger(epData.substring(1));
				if(data>= 0 && data <= 255 && builder.isSelectAriGrouptype()){
					irType = IRGroupManager.TYPE_AIR_CONDITION;
				}else if(data >=256 && data <= 510 && builder.isSelectSTBGrouptype()){
					irType = IRGroupManager.TYPE_STB;
				}else if(data>= 511 && data <= 610 && builder.isSelectGeneralGrouptype()){
					irType = IRGroupManager.TYPE_GENERAL;
				}else{
					if(builder.isSelectGeneralGrouptype()){
						irType = IRGroupManager.TYPE_GENERAL;
					}else if(builder.isSelectAriGrouptype()){
						irType = IRGroupManager.TYPE_AIR_CONDITION;
					}else if(builder.isSelectSTBGrouptype()){
						irType = IRGroupManager.TYPE_STB;
					}
				}
			}else{
				if(builder.isSelectGeneralGrouptype()){
					irType = IRGroupManager.TYPE_GENERAL;
				}else if(builder.isSelectAriGrouptype()){
					irType = IRGroupManager.TYPE_AIR_CONDITION;
				}else if(builder.isSelectSTBGrouptype()){
					irType = IRGroupManager.TYPE_STB;
				}
			}
		}
		View view = builder.createLinkView(irType,linkTaskControlEPData,false);
		return createControlDataDialog(inflater.getContext(),view);
	}
	
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater, final AutoActionInfo autoActionInfo) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		String epdata = autoActionInfo.getEpData();
		final StringBuffer epdataBuffer = new StringBuffer(epdata);
		IRViewBuilder builder =new IRViewBuilder(inflater.getContext(),IRManager.getInstance().getIrGroupManager(getDeviceGwID(), getDeviceID()));
		builder.initControlView();
		String irType = null;
		if(!builder.isHasGrouptype()){
			irType = IRGroupManager.NO_TYPE;
		}else{
			if(epdata != null && epdata.length()>=4){
				int data = StringUtil.toInteger(epdata.substring(1));
				if(data>= 0 && data <= 255 && builder.isSelectAriGrouptype()){
					irType = IRGroupManager.TYPE_AIR_CONDITION;
				}else if(data >=256 && data <= 510 && builder.isSelectSTBGrouptype()){
					irType = IRGroupManager.TYPE_STB;
				}else if(data>= 511 && data <= 610 && builder.isSelectGeneralGrouptype()){
					irType = IRGroupManager.TYPE_GENERAL;
				}else{
					if(builder.isSelectGeneralGrouptype()){
						irType = IRGroupManager.TYPE_GENERAL;
					}else if(builder.isSelectAriGrouptype()){
						irType = IRGroupManager.TYPE_AIR_CONDITION;
					}else if(builder.isSelectSTBGrouptype()){
						irType = IRGroupManager.TYPE_STB;
					}
				}
			}else{
				if(builder.isSelectGeneralGrouptype()){
					irType = IRGroupManager.TYPE_GENERAL;
				}else if(builder.isSelectAriGrouptype()){
					irType = IRGroupManager.TYPE_AIR_CONDITION;
				}else if(builder.isSelectSTBGrouptype()){
					irType = IRGroupManager.TYPE_STB;
				}
			}
		}
		View view = builder.createLinkView(irType,epdataBuffer,true);
		builder.setSelectEpDataListener(new SelectIREpDataListener() {
			
			@Override
			public void onSelectIREpData(StringBuffer epData) {
				autoActionInfo.setEpData(epData.toString());
			}
		});
		holder.setShowDialog(false);
		holder.setContentView(view);
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}
	public void onEventMainThread(DeviceIREvent event){
		builder.createControlView("");
	}
}