package cc.wulian.smarthomev5.entity.camera;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.entity.BaseCameraEntity;

import com.wulian.icam.model.Device;

public class MonitorWLCloudEntity extends BaseCameraEntity{
	List<MonitorWLCloudEntity> entities = new ArrayList<MonitorWLCloudEntity>();
	private final MainApplication mApp = MainApplication.getApplication();
	public String isOnline;
	public String deviceDesc;
	public String sipDoMain;
	public String sipUserName;
	public String deviceId;
	public String upDateAt;
	public String deviceNick;
	public boolean isBindDevice;

  
	
	public void setMonitorIsOnline(String isOnline) {
		this.isOnline = isOnline;
	}

	public String getMonitorIsOnline() {
		return isOnline;
	}

	public void setMonitorDeviceDesc(String deviceDesc) {
		this.deviceDesc = deviceDesc;
	}

	public String getMonitorDeviceDesc() {
		return deviceDesc;
	}

	public void setMonitorSipDoMain(String sipDoMain) {
		this.sipDoMain = sipDoMain;
	}

	public String getMonitorSipDoMain() {
		return sipDoMain;
	}

	public void setMonitorSipUserName(String sipUserName) {
		this.sipUserName = sipUserName;
	}

	public String getMonitorSipUserName() {
		return sipUserName;
	}

	public void setMonitorDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getMonitorDeviceId() {
		return deviceId;
	}

	public void setMonitorUpDatedAt(String upDateAt) {
		this.upDateAt = upDateAt;
	}

	public String getMonitorUpDatedAt() {
		return upDateAt;
	}

	public void setMonitorDeviceNick(String deviceNick) {
		this.deviceNick = deviceNick;
	}

	public String getMonitorDeviceNick() {
		return deviceNick;
	}

	public void setMonitorIsBindDevice(boolean isBindDevice) {
		this.isBindDevice = isBindDevice;
	}

	public boolean getMonitorIsBindDevice() {
		return isBindDevice;
	}

	public MonitorWLCloudEntity() {
		// TODO Auto-generated constructor stub
	}

	public void addMonitorEntity(MonitorWLCloudEntity entity) {
		entities.add(entity);
	}

	public List<MonitorWLCloudEntity> findListAll() {
		return entities;
	}

	public List<MonitorWLCloudEntity> clearAll() {
		return entities;
	}

	public Device getDevice() {
		Device device = new Device();
		try {
			device.setIs_online(StringUtil.toInteger(getMonitorIsOnline()));
		} catch (Exception e) {
			device.setIs_online(1);
		}
		device.setIs_lan(false);
		device.setDevice_desc(getMonitorDeviceDesc());
		device.setDevice_id(getMonitorDeviceId());
		device.setDevice_nick(getMonitorDeviceNick());
		device.setIs_BindDevice(getMonitorIsBindDevice());
		device.setSip_domain(getMonitorSipDoMain());
		device.setSip_username(getMonitorSipUserName());
		try {
			device.setUpdated_at(Long.parseLong(getMonitorUpDatedAt()));
		} catch (NumberFormatException e) {
			device.setUpdated_at(0);
		}
		Logger.debug(device.getDevice_desc() + device.getDevice_id()
				+ device.getDevice_nick());
		return device;
	}
}