package cc.wulian.smarthomev5.entity;
/**
 * 406命令返回的信息
 * @author yuxiaoxuan
 * @date 2016年7月20日13:50:59
 */
public class Command406Result {
	private String gwID, devID, mode, time, key, data,appID;
	public Command406Result(){}
	public Command406Result(String gwID, String devID, String mode,String  time,String  key,String  data){
		this.gwID=gwID;
		this.devID=devID;
		this.mode=mode;
		this.time=time;
		this.key=key;
		this.data=data;
	}
	

	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
	}

	public String getDevID() {
		return devID;
	}

	public void setDevID(String devID) {
		this.devID = devID;
	}

	public String getMode() {
		return mode;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	@Override
	public String toString() {
		return "Command406Result{" +
				"gwID='" + gwID + '\'' +
				", devID='" + devID + '\'' +
				", mode='" + mode + '\'' +
				", time='" + time + '\'' +
				", key='" + key + '\'' +
				", data='" + data + '\'' +
				", appID='" + appID + '\'' +
				'}';
	}
}
