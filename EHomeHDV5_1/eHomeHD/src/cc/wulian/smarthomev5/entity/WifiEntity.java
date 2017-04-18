package cc.wulian.smarthomev5.entity;

public class WifiEntity {
	public static final String TYPE_SCENE = "0";
	public static final String TYPE_DEVICE = "1";
	public String gwID;
	public String operateID;
	public String operateType;
	public String ep;
	public String epType;
	public String epData;
	public String time;
	public String SSID;
	public String conditionContent;

	public WifiEntity() {

	}

	public WifiEntity(String gwID, String operateID, String operateType,
			String ep, String epType, String epData, String time,
			String SSID, String conditionContent) {
		super();
		this.gwID = gwID;
		this.operateID = operateID;
		this.operateType = operateType;
		this.ep = ep;
		this.epType = epType;
		this.epData = epData;
		this.time = time;
		this.SSID = SSID;
		this.conditionContent = conditionContent;
	}

	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
	}

	public String getOperateID() {
		return operateID;
	}

	public void setOperateID(String operateID) {
		this.operateID = operateID;
	}

	public String getOperateType() {
		return operateType;
	}

	public void setOperateType(String operateType) {
		this.operateType = operateType;
	}

	public String getEp() {
		return ep;
	}

	public void setEp(String ep) {
		this.ep = ep;
	}

	public String getEpType() {
		return epType;
	}

	public void setEpType(String epType) {
		this.epType = epType;
	}

	public String getEpData() {
		return epData;
	}

	public void setEpData(String epData) {
		this.epData = epData;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getSSID() {
		return SSID;
	}

	public void setSSID(String SSID) {
		this.SSID = SSID;
	}

	public String getConditionContent() {
		return conditionContent;
	}

	public void setConditionContent(String conditionContent) {
		this.conditionContent = conditionContent;
	}

}
