package cc.wulian.smarthomev5.fragment.device;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.adapter.IconChooseAdapter;
import cc.wulian.smarthomev5.collect.Lists;
import cc.wulian.smarthomev5.databases.entitys.Area;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.entity.IconResourceEntity;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.view.IconChooseView;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

public class AreaGroupManager {

	public static final String DEVICE_AREA_PREFIX = "_";
	private DeviceAreaEntity selectedDeviceAreaEntity;
	private static AreaGroupManager instance = null;
	private DeviceAreaEntity defaultAreaEntity;
	private MainApplication applicaiton = MainApplication.getApplication();
	// 最后一个放默认分组，不可删除分组
	private List<DeviceAreaEntity> deviceAreaEnties = new ArrayList<DeviceAreaEntity>();

	public static synchronized AreaGroupManager getInstance() {
		if (instance == null) {
			instance = new AreaGroupManager();
		}
		return instance;
	}

	private AreaGroupManager() {
		createDefaultAreaEntity();
	}

	private void createDefaultAreaEntity() {
		defaultAreaEntity = new DeviceAreaEntity();
		defaultAreaEntity.setGwID("");
		defaultAreaEntity.setDelete(false);
		defaultAreaEntity.setName(applicaiton.getResources().getString(
				R.string.device_config_edit_dev_area_type_other_default));
		defaultAreaEntity.setRoomID(Area.AREA_DEFAULT);
	}

	public DeviceAreaEntity getDefaultAreaEntity() {
		if (defaultAreaEntity == null) {
			createDefaultAreaEntity();
		}
		defaultAreaEntity.setName(applicaiton.getResources().getString(
				R.string.device_config_edit_dev_area_type_other_default));
		return defaultAreaEntity;
	}

	public List<DeviceAreaEntity> getDeviceAreaEnties() {
		List<DeviceAreaEntity> entites  = new ArrayList<DeviceAreaEntity>();
		entites.add(getDefaultAreaEntity());
		entites.addAll(deviceAreaEnties);
		return entites;
	}

	public synchronized DeviceAreaEntity getDeviceAreaEntity(String gwID,
			String areaID) {
		if (Area.AREA_DEFAULT.equals(areaID)) {
			return defaultAreaEntity;
		} else if (StringUtil.isNullOrEmpty(gwID)
				|| StringUtil.isNullOrEmpty(areaID)) {
			return defaultAreaEntity;
		}
		for (DeviceAreaEntity entity : deviceAreaEnties) {
			if (gwID.equals(entity.getGwID())
					&& areaID.equals(entity.getRoomID())) {
				return entity;
			}
		}
		return defaultAreaEntity;
	}

	public void update(DeviceAreaEntity entity) {
		DeviceAreaEntity e = getDeviceAreaEntity(entity.getGwID(),
				entity.getRoomID());
		if (e != null) {
			e.setName(entity.getName());
			e.setIcon(entity.getIcon());
		}
	}

	public void addDeviceAreaEntity(DeviceAreaEntity deviceAreaEntity) {
		this.deviceAreaEnties.add(0, deviceAreaEntity);
	}

	public void clear() {
		deviceAreaEnties.clear();
		createDefaultAreaEntity();
	}

	public void remove(String gwID, String areaID) {
		if (StringUtil.isNullOrEmpty(gwID) || StringUtil.isNullOrEmpty(areaID)) {
			return;
		}
		for (DeviceAreaEntity entity : deviceAreaEnties) {
			if (gwID.equals(entity.getGwID())
					&& areaID.equals(entity.getRoomID())) {
				deviceAreaEnties.remove(entity);
				return;
			}
		}
	}

}
