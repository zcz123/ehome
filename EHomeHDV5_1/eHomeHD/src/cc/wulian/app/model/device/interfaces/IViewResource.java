package cc.wulian.app.model.device.interfaces;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;

/**
 * for big view show ui [not necessary]<br/>
 * 
 * <b>Chang Log</b> <br/>
 * 1.change{@link IViewResource#onAttachView(Context)} set context param
 */
public interface IViewResource
{
	/**
	 * device be attached to some layout
	 */
	public void onAttachView(Context context);

	/**
	 * create device big view for show some state
	 */
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle saveState );

	/**
	 * when device big created after {@link #onCreateView(LayoutInflater, ViewGroup, Bundle)}
	 */
	public void onViewCreated( View view, Bundle saveState );
	
	public void onResume();
	public void onPause();
	
	
	/**
	 * 创建设置界面
	 * @param inflater
	 * @param container
	 * @return
	 */
	public View onCreateSettingView(LayoutInflater inflater, ViewGroup container);
	//快捷展示和控制
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item,LayoutInflater inflater); 
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(DeviceShortCutSelectDataItem item,LayoutInflater inflater,AutoActionInfo autoActionInfo); 
	//批量控制
	public boolean isAutoControl(boolean isNormal);
	public void setControlEPDataListener(ControlEPDataListener listener);
	public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater,String ep,String epData);
	public EditDeviceInfoView onCreateEditDeviceInfoView(LayoutInflater inflater);
	public AbstractLinkTaskView onCreateLinkTaskView(BaseActivity context,TaskInfo taskInfo);
	
	
	public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(LayoutInflater inflater,AutoConditionInfo autoConditionInfo,boolean isTriggerCondition);
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(LayoutInflater inflater,AutoActionInfo autoActionInfo);
	/**
	 * init device big view state
	 */
	public void initViewStatus();

	/**
	 * device be detach from some layout
	 */
	public void onDetachView();
	
	public MoreMenuPopupWindow getDeviceMenu();
	
	public void OnRefreshResultData(Intent data);
}
