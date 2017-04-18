package cc.wulian.smarthomev5.fragment.setting;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.dao.SceneDao;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.WulianCloudURLManager;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.URLConstants;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;

public class AccountPermissionItem extends AbstractSettingItem {

	private AccountManager mAccountManger = AccountManager.getAccountManger();

	public AccountPermissionItem(Context context) {
		super(context, R.drawable.setting_control_permission_item, context
				.getResources().getString(
						R.string.set_account_manager_permission));
	}

	@Override
	public void doSomethingAboutSystem() {
		startActivity();

	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.system_intent_right);
		infoImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity();
			}
		});
	}

	private void startActivity() {
		// add by yanzy:不允许被授权用户使用
		if (!UserRightUtil.getInstance().canDo(
				UserRightUtil.EntryPoint.GATEWAY_AUTHORIZE)) {
			return;
		}

		JSONArray jsonDeviceString = loadAllDeviceToJsonObject(mAccountManger.getmCurrentInfo()
				.getGwID());
		JSONArray jsonSceneString = loadAllSceneToJsonObject();
		JSONObject json = new JSONObject();
		json.put("device", jsonDeviceString);
		json.put("scene", jsonSceneString);
		String url = URLConstants.LOCAL_BASEURL
				+ "index.html";

		if (json.toJSONString() != null) {
			SmarthomeFeatureImpl.setData(
					SmarthomeFeatureImpl.Constants.DEVICEPARAM,
					json.toJSONString());
		} else {
			SmarthomeFeatureImpl.setData(
					SmarthomeFeatureImpl.Constants.DEVICEPARAM, "");
		}
		SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID,
				mAccountManger.getmCurrentInfo().getGwID());
		IntentUtil.startHtml5PlusActivity(mContext, url);
	}

	public JSONArray loadAllDeviceToJsonObject(String gwID) {
		DeviceCache deviceCache = DeviceCache.getInstance(mContext);
		JSONArray jsonArray = new JSONArray();
		for (WulianDevice device : deviceCache.getAllDevice()) {
			JSONObject obj = new JSONObject();
			obj.put("isOffline", !device.isDeviceOnLine());
			obj.put("deviceName", DeviceTool.getDeviceShowName(device));
			obj.put("deviceType", device.getDeviceType());
			obj.put("deviceId", device.getDeviceID());
			obj.put("deviceRight", "0");
			jsonArray.add(obj);
		}
		return jsonArray;
	}

	public JSONArray loadAllSceneToJsonObject() {
		SceneInfo info = new SceneInfo();
		info.setGwID(mAccountManger.getmCurrentInfo().getGwID());
		SceneDao sceneDao = SceneDao.getInstance();
		final List<SceneInfo> sceneList = sceneDao.findListAll(info);
		JSONArray jsonArray = new JSONArray();
		for (SceneInfo sInfo : sceneList) {
			JSONObject obj = new JSONObject();
			obj.put("gwID", sInfo.getGwID());
			obj.put("sceneID", sInfo.getSceneID());
			obj.put("name", sInfo.getName());
			obj.put("icon", sInfo.getIcon());
			obj.put("groupID", sInfo.getGroupID());
			obj.put("groupName", sInfo.getGroupName());
			obj.put("status", sInfo.getStatus());
			jsonArray.add(obj);
		}
		return jsonArray;
	}

	public static class DeviceInfo {
		private String deviceName;
		private String deviceIcon;
		private String deviceType;
		private String deviceId;
		private String deviceRight;
		private boolean isOffline;

		public String getDeviceName() {
			return deviceName;
		}

		public void setDeviceName(String deviceName) {
			this.deviceName = deviceName;
		}

		public String getDeviceIcon() {
			return deviceIcon;
		}

		public void setDeviceIcon(String deviceIcon) {
			this.deviceIcon = deviceIcon;
		}

		public boolean isOffline() {
			return isOffline;
		}

		public void setOffline(boolean isOffline) {
			this.isOffline = isOffline;
		}

		public String getDeviceType() {
			return deviceType;
		}

		public void setDeviceType(String deviceType) {
			this.deviceType = deviceType;
		}

		public String getDeviceId() {
			return deviceId;
		}

		public void setDeviceId(String deviceId) {
			this.deviceId = deviceId;
		}

		public String getDeviceRight() {
			return deviceRight;
		}

		public void setDeviceRight(String deviceRight) {
			this.deviceRight = deviceRight;
		}

	}
}
