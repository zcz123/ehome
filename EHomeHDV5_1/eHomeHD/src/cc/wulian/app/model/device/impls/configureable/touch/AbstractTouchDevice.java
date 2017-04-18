package cc.wulian.app.model.device.impls.configureable.touch;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.alarmable.onetranslator.DeviceOneTranslatorFragment;
import cc.wulian.app.model.device.impls.configureable.ConfigureableDeviceImpl;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.SceneList;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.SceneList.OnSceneListItemClickListener;
import cc.wulian.smarthomev5.tools.SceneManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.ui.WLToast;

public abstract class AbstractTouchDevice extends ConfigureableDeviceImpl {
	protected Map<String, SceneInfo> bindScenesMap;
	protected Map<String, SceneInfo> cloneBindScenesMap;
	protected Map<String, DeviceInfo> bindDevicesMap;
	protected LinearLayout contentLineLayout;
	private LinearLayout sceneView;
	
	@Override
	public void onDeviceUp(DeviceInfo devInfo) {
		super.onDeviceUp(devInfo);
		SendMessage.sendGetBindSceneMsg(gwID, devID);
	}

	@Override
	public boolean isAutoControl(boolean isNormal) {
		return false;
	}

	/**
	 * 创建界面
	 */
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle saveState ){
		getBindScenesMap();
		sceneView = (LinearLayout)inflater.inflate(R.layout.device_other_touch_scene, container, false);
		return sceneView;
		 
	}

	@Override
	public void onViewCreated( View view, Bundle saveState ){
		showView();
	}
		
	@Override
	public void initViewStatus() {
		super.initViewStatus();
		showView();
	}
	/**
	 * 界面的添加与界面的数据处理
	 * 
	 */
	public void showView(){
		LayoutInflater inflater = LayoutInflater.from(mContext);
		int bindSceneLength = getTouchEPResources().length;
		int accountRow = (bindSceneLength+1)/2;
		sceneView.removeAllViews();
		for(int i = 0;i<accountRow;i++){
			LinearLayout rowLinearLayout = new LinearLayout(mContext);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,0);
			lp.weight = 1;
			rowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
			rowLinearLayout.setGravity(Gravity.CENTER);
			rowLinearLayout.setLayoutParams(lp);
			sceneView.addView(rowLinearLayout);
		}
		for(int j = 0; j < bindSceneLength; j++){
			final String ep = getTouchEPResources()[j];
			int rowIndex = j/2;
			LinearLayout itemView = (LinearLayout) inflater.inflate(
					R.layout.device_other_touch_scene_child,null);
			
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
			lp.weight = 1;
			itemView.setGravity(Gravity.CENTER_HORIZONTAL);
			itemView.setLayoutParams(lp);
			
			LinearLayout  layoutbackground = (LinearLayout)itemView.findViewById(
					R.id.device_other_touch_scene_child_linearlayout_background);
			ImageView deviceNameImageView = (ImageView) itemView
					.findViewById(R.id.device_other_touch_scene_child_linearlayout_imageview);
			TextView deviceNameTextView = (TextView) itemView
					.findViewById(R.id.device_other_touch_scene_child_linearlayout_textview);
			layoutbackground.setBackgroundResource(R.drawable.device_other_touch_scene_blackbind);
			deviceNameTextView.setText((j+1)+"."+getResources().getString(R.string.device_no_bind_scene));
			if (bindScenesMap.containsKey(ep)) {
				SceneInfo sceneInfo = bindScenesMap.get(ep);
				if(sceneInfo != null){
					deviceNameImageView.setVisibility(View.VISIBLE);
					layoutbackground.setBackgroundResource(R.drawable.device_abstract_touch_device_background);
					deviceNameImageView.setImageDrawable(
					SceneManager.getSceneIconDrawable_Light_Small(mContext, sceneInfo.getIcon()));
					deviceNameTextView.setText((j+1)+"."+sceneInfo.getName());
				}
				else{
					layoutbackground.setBackgroundResource(R.drawable.device_other_touch_scene_blackbind);
					deviceNameImageView.setVisibility(View.INVISIBLE);
					deviceNameTextView.setText((j+1)+"."+getResources().getString(R.string.device_no_bind_scene));
				}
			}
			
			layoutbackground.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					if(null!= bindScenesMap.get(ep)){
	                    sendCmd(ep);
					}
					else{
						WLToast.showToast(mContext, mContext.getResources().getString(R.string.device_no_bind_scene),WLToast.TOAST_SHORT);
					}
				}
			});
			LinearLayout rowLineLayout = (LinearLayout)sceneView.getChildAt(rowIndex);
			rowLineLayout.addView(itemView);
		  }
	}

	public void sendCmd(String ep) {
		SendMessage.sendSetSceneMsg(mContext, bindScenesMap.get(ep).getGwID(),
				CmdUtil.MODE_SWITCH, bindScenesMap.get(ep).getSceneID(), null, null,
				"2", true);
	}

	
	/**
	 * 给子类实现多端口
	 * 
	 * @return
	 */
	public abstract String[] getTouchEPResources();

	public abstract String[] getTouchEPNames();

	public AbstractTouchDevice(Context context, String type) {
		super(context, type);
	}

	@Override
	public View onCreateSettingView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.device_touch_bind_scene, null);
		contentLineLayout = (LinearLayout) view
				.findViewById(R.id.touch_bind_content_ll);
		getBindScenesMap();
		for (int i = 0; i < getTouchEPResources().length; i++) {
			final String ep = getTouchEPResources()[i];
			LinearLayout itemView = (LinearLayout) inflater.inflate(
					R.layout.device_touch_bind_scene_item, null);
			TextView deviceNameTextView = (TextView) itemView
					.findViewById(R.id.touch_bind_ep_name);
			deviceNameTextView.setText(getTouchEPNames()[i]);
			final TextView sceneNameTextView = (TextView) itemView
					.findViewById(R.id.touch_bind_scene_device_name);
			String sceneName = getResources().getString(
					R.string.device_no_bind_scene);
			if (bindScenesMap.containsKey(ep)) {
				SceneInfo sceneInfo = bindScenesMap.get(ep);
				if(sceneInfo != null){
					sceneName = sceneInfo.getName();
				}
			}
			sceneNameTextView.setText(sceneName);
			itemView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					final SceneList sceneList = new SceneList(mContext, true);
					sceneList
							.setOnSceneListItemClickListener(new OnSceneListItemClickListener() {

								@Override
								public void onSceneListItemClicked(
										SceneList list, int pos, SceneInfo info) {
									sceneNameTextView.setText(info.getName());
									bindScenesMap.put(ep, info);
									JsonTool.uploadBindList(mContext,
											bindScenesMap, bindDevicesMap,
											gwID, devID, type);
									sceneList.dismiss();
								}
							});
					sceneList.show(v);
				}
			});
			contentLineLayout.addView(itemView);
		}
		return view;
	}

	protected void getBindScenesMap() {
		bindScenesMap = MainApplication.getApplication().bindSceneInfoMap
				.get(getDeviceGwID() + getDeviceID());
		if (bindScenesMap == null) {
			bindScenesMap = new HashMap<String, SceneInfo>();
		}
	}
	
	public Intent getSettingIntent() {
		Intent intent = new Intent(mContext, DeviceSettingActivity.class);
		intent.putExtra(DeviceOneTranslatorFragment.GWID, gwID);
		intent.putExtra(DeviceOneTranslatorFragment.DEVICEID, devID);
		intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME,
				TouchDeviceEditFragment.class.getName());
		intent.putExtra(AbstractDevice.SETTING_LINK_TYPE,
				AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
		return intent;
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
	
}
