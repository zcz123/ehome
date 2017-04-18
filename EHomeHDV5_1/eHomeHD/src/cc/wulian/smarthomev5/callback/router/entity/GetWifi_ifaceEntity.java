package cc.wulian.smarthomev5.callback.router.entity;

public class GetWifi_ifaceEntity {
	private String encryption;
	private String key;
	private String mode;
	private String ssid;


	public String getChannel() {
		return channel;
	}

	public void setChannel(String channel) {
		this.channel = channel;
	}

	private String channel;

	public String getSet_channel() {
		return set_channel;
	}

	public void setSet_channel(String set_channel) {
		this.set_channel = set_channel;
	}

	private String set_channel;

	public String getEncryption() {
		return encryption;
	}

	public void setEncryption(String encryption) {
		this.encryption = encryption;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getSsid() {
		return ssid;
	}

	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

}
