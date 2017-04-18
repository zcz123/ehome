package cc.wulian.smarthomev5.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.RoomInfo;

public class DeviceAreaEntity extends RoomInfo implements Serializable {
	private boolean isDelete =true;
	private List<DeviceInfo> devices = new ArrayList<DeviceInfo>();
	public DeviceAreaEntity(){
		
	}
	public DeviceAreaEntity(RoomInfo info) {
		this.setGwID(info.getGwID());
		this.setIcon(info.getIcon());
		this.setName(info.getName());
		this.setRoomID(info.getRoomID());
	}
	public List<DeviceInfo> getDevices() {
		return new ArrayList<>(devices);
	}
	public void setDevices(List<DeviceInfo> devices) {
		this.devices = devices;
	}
	public void addDevice(DeviceInfo device) {
		this.devices.add(device);
	}
	public void removeDevice(DeviceInfo device){
		this.devices.remove(device);
	}
	public boolean isDelete() {
		return isDelete;
	}
	public void setDelete(boolean isDelete) {
		this.isDelete = isDelete;
	}
	
	public void clearDevices(){
		devices.clear();
	}
}
