package cc.wulian.app.model.device.impls.controlable.austkey;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
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
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.alarmable.onetranslator.DeviceOneTranslatorFragment;
import cc.wulian.app.model.device.impls.configureable.touch.TouchDeviceEditFragment;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
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

public abstract class AbstractAustKeyDevice extends ControlableDeviceImpl {
	protected Map<String, SceneInfo> bindScenesMap;
	protected Map<String, DeviceInfo> bindDevicesMap;
	protected LinearLayout settingLineLayout;
	protected LinearLayout sceneSwitchView;

	private static String STATUS_OPEN = "01";
	private static String STATUS_CLOSE = "00";
	protected static String CMD_OPEN = "1";
	protected static String CMD_CLOSE = "0";

	public AbstractAustKeyDevice(Context context, String type) {
		super(context, type);
	}

	@Override
	public boolean isAutoControl(boolean isSimple) {
		return false;
	}
	
	@Override
	protected boolean isMultiepDevice() {
		return true;
	}

	@Override
	public String getOpenSendCmd() {
		return CMD_OPEN;
	}

	@Override
	public String getCloseSendCmd() {
		return CMD_CLOSE;
	}

	@Override
	public String getOpenProtocol() {
		return CMD_OPEN;
	}

	@Override
	public String getCloseProtocol() {
		return CMD_CLOSE;
	}

	@Override
	public boolean isOpened() {
		return STATUS_OPEN.equals(epData);
	}

	@Override
	public boolean isClosed() {
		return STATUS_CLOSE.equals(epData);
	}

	@Override
	public void onDeviceUp(DeviceInfo devInfo) {
		super.onDeviceUp(devInfo);
		SendMessage.sendGetBindSceneMsg(gwID, devID);
	}

	@Override
	public synchronized void onDeviceData(String gwID, String devID,
			DeviceEPInfo devEPInfo,String cmd,String mode) {
		String ep = devEPInfo.getEp();
		WulianDevice device = getChildDevice(ep);
		if (device != null) {
			device.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
			removeCallbacks(mRefreshStateRunnable);
			post(mRefreshStateRunnable);
			fireDeviceRequestControlData();
		} else {
			super.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
		}
	}

	/**
	 * 创建界面
	 */
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		getBindScenesMap();
		sceneSwitchView = (LinearLayout) inflater.inflate(
				R.layout.device_other_touch_scene, container, false);
		return sceneSwitchView;

	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
	}

	@Override
	public View onCreateSettingView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.device_touch_bind_scene, null);
		settingLineLayout = (LinearLayout) view
				.findViewById(R.id.touch_bind_content_ll);
		getBindScenesMap();
		for (int i = 0; i < getSceneSwitchEPResources().length; i++) {
			final String ep = getSceneSwitchEPResources()[i];
			LinearLayout itemView = (LinearLayout) inflater.inflate(
					R.layout.device_touch_bind_scene_item, null);
			TextView deviceNameTextView = (TextView) itemView
					.findViewById(R.id.touch_bind_ep_name);
			deviceNameTextView.setText(getSceneSwitchEPNames()[i]);
			final TextView sceneNameTextView = (TextView) itemView
					.findViewById(R.id.touch_bind_scene_device_name);
			String sceneName = getResources().getString(
					R.string.device_no_bind_scene);
			if (bindScenesMap.containsKey(ep)) {
				SceneInfo sceneInfo = bindScenesMap.get(ep);
				if (sceneInfo != null) {
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
			settingLineLayout.addView(itemView);
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

	/**
	 * 给子类实现多端口
	 * 
	 * @return
	 */
	public abstract String[] getLightEPInfo();

	public abstract String[] getSwitchEPName();

	public abstract String[] getSceneSwitchEPResources();

	public abstract String[] getSceneSwitchEPNames();

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
				Intent i = getSettingIntent();
				mContext.startActivity(i);
				manager.dismiss();
			}
		};
		if(isDeviceOnLine())
			items.add(settingItem);
		return items;
	}
	
	@SuppressLint("NewApi")
	public void showView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		int bindSceneLength = getSceneSwitchEPResources().length;
		int accountRow = bindSceneLength;
		sceneSwitchView.removeAllViews();
		for (int i = 0; i < accountRow; i++) {
			LinearLayout rowLinearLayout = new LinearLayout(mContext);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.WRAP_CONTENT);
			rowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
			rowLinearLayout.setGravity(Gravity.CENTER);
			rowLinearLayout.setLayoutParams(lp);
			sceneSwitchView.addView(rowLinearLayout);
		}
		for (int j = 0; j < bindSceneLength; j++) {
			final String lightEP = getLightEPInfo()[j];
			final String ep = getSceneSwitchEPResources()[j];
			int rowIndex = j;
			LinearLayout itemView = (LinearLayout) inflater.inflate(
					R.layout.device_aust_switch_scene_child, null);

			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			lp.weight = 1;
			itemView.setGravity(Gravity.CENTER_HORIZONTAL);
			itemView.setLayoutParams(lp);

			LinearLayout layoutSwitch = (LinearLayout) itemView
					.findViewById(R.id.device_aust_switch_scene_child_linearlayout_switch);
			final ImageView switchImageView = (ImageView) itemView
					.findViewById(R.id.device_aust_switch_scene_child_linearlayout_switch_imageview);
			TextView switchTextView = (TextView) itemView
					.findViewById(R.id.device_aust_switch_scene_child_bind_switch_textview);
			switchTextView.setText(getSwitchEPName()[j]);

			LinearLayout layoutbackground = (LinearLayout) itemView
					.findViewById(R.id.device_aust_switch_scene_child_linearlayout_config);
			ImageView deviceNameImageView = (ImageView) itemView
					.findViewById(R.id.device_aust_switch_scene_child_linearlayout_config_imageview);
			TextView deviceNameTextView = (TextView) itemView
					.findViewById(R.id.device_aust_switch_scene_child_linearlayout_config_textview);
			deviceNameTextView.setText((j + 1) + "."
					+ getResources().getString(R.string.device_no_bind_scene));
			if (bindScenesMap.containsKey(ep)) {
				SceneInfo sceneInfo = bindScenesMap.get(ep);
				if (sceneInfo != null) {
					layoutbackground.setBackground(mContext.getResources()
							.getDrawable(R.drawable.device_light_module_bg0));
					deviceNameImageView.setImageDrawable(SceneManager
							.getSceneIconDrawable_Light_Small(mContext,
									sceneInfo.getIcon()));
					deviceNameTextView.setText((j + 1) + "."
							+ sceneInfo.getName());
				} else {
					layoutbackground.setBackground(mContext.getResources()
							.getDrawable(R.drawable.device_light_module_bg0));
					deviceNameImageView.setImageDrawable(getResources()
							.getDrawable(R.drawable.device_light_module_scene));
					deviceNameTextView.setText((j + 1)
							+ "."
							+ getResources().getString(
									R.string.device_no_bind_scene));
				}
			}

			layoutSwitch.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					setControlDevice(lightEP);
				}

			});

			layoutbackground.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {

					if (null != bindScenesMap.get(ep)) {
						sendCmd(ep);
					} else {
						WLToast.showToast(mContext, mContext.getResources()
								.getString(R.string.device_no_bind_scene),
								WLToast.TOAST_SHORT);
					}
				}
			});
			setConvertersChecked(lightEP, switchImageView);
			LinearLayout rowLineLayout = (LinearLayout) sceneSwitchView
					.getChildAt(rowIndex);
			rowLineLayout.addView(itemView);
		}
	}

	/**
	 * 数据返回后界面的刷新
	 * 
	 * @param ep
	 */

	public void sendCmd(String ep) {
		SendMessage.sendSetSceneMsg(mContext, bindScenesMap.get(ep).getGwID(),
				CmdUtil.MODE_SWITCH, bindScenesMap.get(ep).getSceneID(), null,
				null, "2", true);
	}

	@Override
	public void initViewStatus() {
		showView();
	}

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item,LayoutInflater inflater){
		return getDefaultShortCutControlView(item,inflater);
	}

	public void setConvertersChecked(String ep, ImageView switchImageView) {
		WulianDevice device = getChildDevice(ep);
		if (device instanceof Controlable) {
			Controlable control = (Controlable) device;
			if (control.isOpened()) {
				switchImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.device_light_module_open));
			} else {
				switchImageView.setImageDrawable(getResources().getDrawable(
						R.drawable.device_light_module_close));
			}
		}
	}

	public void setControlDevice(String ep) {
		fireWulianDeviceRequestControlSelf();
		getChildDevice(ep).controlDevice(ep, getDeviceInfo().getType(),
				null);
	}
}
