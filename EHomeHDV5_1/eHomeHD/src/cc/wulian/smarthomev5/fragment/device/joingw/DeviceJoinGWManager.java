package cc.wulian.smarthomev5.fragment.device.joingw;

import java.util.LinkedList;
import java.util.Queue;

import cc.wulian.app.model.device.WulianDevice;

public class DeviceJoinGWManager {
	public static DeviceJoinGWManager instance;
	private Queue<WulianDevice> deviceQueue = new LinkedList<WulianDevice>();

	private DeviceJoinGWManager() {

	}

	public static DeviceJoinGWManager getInstance() {
		if (instance == null) {
			instance = new DeviceJoinGWManager();
		}
		return instance;
	}

	public void add(WulianDevice device) {
		deviceQueue.add(device); 
	}

	public Queue<WulianDevice> getAllDevice() {
		return deviceQueue;
	}
	
}
