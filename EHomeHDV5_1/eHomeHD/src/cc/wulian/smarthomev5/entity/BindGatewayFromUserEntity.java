package cc.wulian.smarthomev5.entity;

import java.util.ArrayList;
import java.util.List;

public class BindGatewayFromUserEntity {
	List<BindGatewayFromUserEntity> entities = new ArrayList<BindGatewayFromUserEntity>();

	public String deviceName;
	public String status;
	public String deviceType;
	public String deviceID;

	public void setBindGatewayDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getBindGatewayDeviceName() {
		return deviceName;
	}
	public void setBindGatewayStatus(String status) {
		this.status = status;
	}

	public String getBindGatewayStatus() {
		return status;
	}
	public void setBindGatewayDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}

	public String getBindGatewayDeviceType() {
		return deviceType;
	}
	public void setBindGatewayDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	public String getBindGatewayDeviceID() {
		return deviceID;
	}
}