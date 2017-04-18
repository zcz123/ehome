package cc.wulian.smarthomev5.entity;


public class ShakeEntity
{
	public static final String TYPE_SCENE = "0";
	public static final String TYPE_DEVICE = "1";
	public String gwID;
	public String operateID;
	public String operateType;
	public String ep;
	public String epType;
	public String epData;
	public String time;

	public ShakeEntity(){
	}

	public ShakeEntity(String gwID, String operateID, String operateType,
			String ep, String epType, String epData, String time) {
		super();
		this.gwID = gwID;
		this.operateID = operateID;
		this.operateType = operateType;
		this.ep = ep;
		this.epType = epType;
		this.epData = epData;
		this.time = time;
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
}
