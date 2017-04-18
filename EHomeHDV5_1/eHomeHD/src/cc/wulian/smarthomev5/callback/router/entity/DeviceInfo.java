package cc.wulian.smarthomev5.callback.router.entity;

public class DeviceInfo {
	private int assoc;
	private int up;
	private int uptime;
	private int down;
	private String mac;
	private String name;
	private String ip;

	public String getMac() {
		return mac;
	}

	public void setMac(String mac) {
		this.mac = mac;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getUp() {
		return up;
	}

	public void setUp(int up) {
		this.up = up;
	}

	public int getAssoc() {
		return assoc;
	}

	public void setAssoc(int assoc) {
		this.assoc = assoc;
	}

	public int getUptime() {
		return uptime;
	}

	public void setUptime(int uptime) {
		this.uptime = uptime;
	}

	public int getDown() {
		return down;
	}

	public void setDown(int down) {
		this.down = down;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

}
