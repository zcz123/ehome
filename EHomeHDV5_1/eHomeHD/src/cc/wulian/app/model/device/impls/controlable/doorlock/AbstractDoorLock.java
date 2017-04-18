package cc.wulian.app.model.device.impls.controlable.doorlock;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.SceneList;
import cc.wulian.smarthomev5.tools.SceneList.OnSceneListItemClickListener;

public abstract class AbstractDoorLock extends ControlableDeviceImpl implements Alarmable{
	public static final String	WINDOWS_PWD					= "1111";
	public static final String	WINDOWS_PWD_MD5			= MD5Util.encrypt(WINDOWS_PWD);
	
	protected static final String DATA_CTRL_STATE_OPEN_1 = "1";
	protected static final String DATA_CTRL_STATE_CLOSE_2 = "2";
	protected static final String DATA_CTRL_STATE_OPEN_3 = "3";

	// door lock protocol
	protected static final String DEVICE_STATE_10 = "10";
	protected static final String DEVICE_STATE_11 = "11";
	protected static final String DEVICE_STATE_19 = "19";
	protected static final String DEVICE_STATE_20 = "20";
	protected static final String DEVICE_STATE_21 = "21";
	protected static final String DEVICE_STATE_22 = "22";
	protected static final String DEVICE_STATE_23 = "23";
	protected static final String DEVICE_STATE_24 = "24";
	protected static final String DEVICE_STATE_25 = "25";
	protected static final String DEVICE_STATE_26 = "26";
	protected static final String DEVICE_STATE_29 = "29";
	protected static final String DEVICE_STATE_30 = "30";
	protected static final String DEVICE_STATE_32 = "32";
	protected static final String DEVICE_STATE_FF = "FF";

	protected static final String DEVICE_STATE_144 = "144";
	protected static final String DEVICE_STATE_145 = "145";
	// door lock protocol

	protected static final int SMALL_OPEN_D = R.drawable.device_door_lock_open;
	protected static final int SMALL_CLOSE_D = R.drawable.device_door_lock_close;
	protected Map<String, SceneInfo> bindScenesMap;
	protected Map<String, DeviceInfo> bindDevicesMap;
	protected LinearLayout contentLineLayout;

	public abstract String[] getDoorLockEPResources();

	public AbstractDoorLock(Context context, String type) {
		super(context, type);
	}

	@Override
	public String getOpenSendCmd() {
		return DATA_CTRL_STATE_OPEN_1;
	}

	@Override
	public String getCloseSendCmd() {
		return DATA_CTRL_STATE_CLOSE_2;
	}
	public String getCancleAlarmProtocol(){
		return null;
	}
	/**
	 * 门锁状态是否未知
	 */
	public abstract boolean isStateUnknow();

	/**
	 * 上保险
	 */
	public abstract boolean isSecureLocked();

	/**
	 * 解除保险
	 */
	public abstract boolean isSecureUnLocked();

	/**
	 * 锁已关
	 */
	public abstract boolean isLocked();

	/**
	 * 锁已开
	 */
	public abstract boolean isUnLocked();

	/**
	 * 是否反锁
	 */
	public boolean isReverseLock(){
		return false;
	}
	/**
	 * 是否解除反锁
	 */
	public boolean isRemoveLock(){
		return false;
	}
	/**
	 * 是否密码开门
	 */
	public boolean isPasswordUnLocked() {
		return false;
	}

	/**
	 * 是否门已开
	 */
	public boolean isDoorUnLocked() {
		return false;
	}
	/**
	 * 是否门已关
	 */
	public boolean isDoorLocked() {
		return false;
	}
	/**
	 * 是否纽扣开门
	 */
	public boolean isButtonUnLocked(String epData) {
		return false;
	}

	/**
	 * 是否指纹开门
	 */
	public boolean isFingerUnLocked(String epData) {
		return false;
	}

	/**
	 * 是否卡片开门
	 */
	public boolean isCardUnLocked(String epData) {
		return false;
	}

	/**
	 * 是否钥匙开门
	 */
	public boolean isKeyUnLocked() {
		return false;
	}

	/**
	 * 密码是否正确
	 */
	public boolean isPWCorrect() {
		return false;
	}

	/**
	 * 密码是否错误
	 */
	public boolean isPWWrong() {
		return false;
	}

	@Override
	public boolean isOpened() {
		return false;
	}

	@Override
	public boolean isClosed() {
		return false;
	}


//	public boolean isAlarming() {
//		return false;
//	}
//
//
//	public boolean isNormal() {
//		return false;
//	}

	
	@Override
	public CharSequence parseAlarmProtocol(String epData) {
		return null;
	}

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item,LayoutInflater inflater){
		return getSensorableDeviceShortCutView(item, inflater);
	}
	
	@Override
	public boolean isAutoControl(boolean isNormal) {
		if(isNormal)
			return false;
		else
			return true;
	}

	@Override
	public View onCreateSettingView(LayoutInflater inflater, ViewGroup container) {
		if (getDoorLockEPResources() == null
				|| getDoorLockEPResources().length <= 0) {
			return null;
		}
		View view = inflater.inflate(R.layout.device_touch_bind_scene, null);
		contentLineLayout = (LinearLayout) view
				.findViewById(R.id.touch_bind_content_ll);
		getBindScenesMap();
		for (int i = 0; i < getDoorLockEPResources().length; i++) {
			final String ep = getDoorLockEPResources()[i];
			LinearLayout itemView = (LinearLayout) inflater.inflate(
					R.layout.device_door_scenebind_item, null);
			// itemView.setLayoutParams(new
			// LayoutParams(LayoutParams.MATCH_PARENT, 40));
			TextView deviceNameTextView = (TextView) itemView
					.findViewById(R.id.touch_bind_ep_name);
			deviceNameTextView.setText(DeviceUtil.epNameString(ep, mContext));
			final TextView sceneNameTextView = (TextView) itemView
					.findViewById(R.id.touch_bind_scene_device_name);
			String sceneName = getResources()
					.getString(R.string.device_no_bind);
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
	
	@Override
	public String getAlarmString() {
		return mResources.getString(R.string.home_device_alarm_default_voice_notification);
	}

	@Override
	public String getNormalString() {
		return mResources.getString(R.string.home_device_alarm_default_voice_notification);
	}
}