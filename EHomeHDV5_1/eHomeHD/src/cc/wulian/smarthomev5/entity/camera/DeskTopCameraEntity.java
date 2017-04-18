package cc.wulian.smarthomev5.entity.camera;
import com.wulian.icam.model.Device;
import com.yuantuo.netsdk.TKCamHelper;

import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.entity.BaseCameraEntity;
public class DeskTopCameraEntity extends BaseCameraEntity{

	public static final String GW_GENERATION_ONE = "1";
	public static final String GW_GENERATION_TWO = "2";
	public static final String GW_GENERATION_THREE = "3";
	public static final String GW_VERTICAL = "1";
	public static final String GW_YUNJIA = "2";
	public static final String GW_NORMAL_5350 = "3";
	public static final String GW_ARM = "4";
	public static final String GW_MONITOR = "5";
	public static final String GW_FLOWER_WITH_DISK = "6";
	public static final String GW_FLOWER_NO_DISK = "7";
	public static final String GW_AUTHORITY_BIND = "bind";
	public static final String GW_AUTHORITY_AUTH = "auth";
	private static final long serialVersionUID = 1L;
	private String appID;
	private String appType;
	private String gwID;
	private String gwPwd;
	private String gwIP;
	private String gwSerIP;
	private String zoneID;
	private String time;
	private String data;
	private String gwVer;
	private String gwName;
	private String gwChanel;
	private String gwRoomID;
	private String gwPath;
	private String status;
	private String authority;
	private String tutkUID;
	private String tutkPASSWD;
	private TKCamHelper mCamera;
    public DeskTopCameraEntity(){
    	
    }
    public Device getDevice(){
		Device de = new Device();
		de.setDevice_id(getGwID());
		de.setDevice_nick(getGwName());
		de.setIp(getGwIP());
		return de;
	}
	public DeskTopCameraEntity(GatewayInfo obj) {
       	setAppID(obj.getAppID());
       	setAppType(obj.getAppType());
       	setData(obj.getData());
       	setGwAuthority(obj.getGwAuthority());
       	setGwChanel(obj.getGwChanel());
       	setGwID(obj.getGwID());
       	setGwIP(obj.getGwIP());
       	setGwName(obj.getGwName());
       	setGwPath(obj.getGwPath());
       	setGwPwd(obj.getGwPwd());
       	setGwRoomID(obj.getGwRoomID());
       	setGwSerIP(obj.getGwSerIP());
       	setGwStatus(obj.getGwStatus());
       	setGwVer(obj.getGwVer());
       	setTime(obj.getTime());
       	setTutkPASSWD(obj.getTutkPASSWD());
       	setTutkUID(obj.getTutkUID());
       	setZoneID(obj.getZoneID());
	}
	
	public void clear() {
		this.appID = null;
		this.appType = null;
		this.gwID = null;
		this.gwPwd = null;
		this.gwIP = null;
		this.gwSerIP = null;
		this.zoneID = null;
		this.time = null;
		this.data = null;
	}
    public void setmCamera(TKCamHelper mCamera) {
		this.mCamera = mCamera;
	}
    public TKCamHelper getmCamera() {
		return mCamera;
	}
	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getAppType() {
		return appType;
	}

	public void setAppType(String appType) {
		this.appType = appType;
	}

	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
	}

	public String getGwPwd() {
		return gwPwd;
	}

	public void setGwPwd(String gwPwd) {
		this.gwPwd = gwPwd;
	}

	public String getGwIP() {
		return gwIP;
	}

	public void setGwIP(String gwIP) {
		this.gwIP = gwIP;
	}

	public String getGwSerIP() {
		return gwSerIP;
	}

	public void setGwSerIP(String gwSerIP) {
		this.gwSerIP = gwSerIP;
	}

	public String getZoneID() {
		return zoneID;
	}

	public void setZoneID(String zoneID) {
		this.zoneID = zoneID;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getGwVer() {
		return gwVer;
	}

	public void setGwVer(String gwVer) {
		this.gwVer = gwVer;
	}

	public String getGwName() {
		return gwName;
	}

	public void setGwName(String gwName) {
		this.gwName = gwName;
	}

	public String getGwChanel() {
		return gwChanel;
	}

	public void setGwChanel(String gwChanel) {
		this.gwChanel = gwChanel;
	}

	public String getGwRoomID() {
		return gwRoomID;
	}

	public void setGwRoomID(String gwRoomID) {
		this.gwRoomID = gwRoomID;
	}

	public String getGwPath() {
		return gwPath;
	}

	public void setGwPath(String gwPath) {
		this.gwPath = gwPath;
	}

	public String getGwStatus() {
		return status;
	}

	public void setGwStatus(String status) {
		this.status = status;
	}

	public void setGwAuthority(String authority) {
		this.authority = authority;
	}

	public String getGwAuthority() {
		return authority;
	}

	public String getTutkUID() {
		return tutkUID;
	}

	public void setTutkUID(String tutkUID) {
		this.tutkUID = tutkUID;
	}

	public String getTutkPASSWD() {
		return tutkPASSWD;
	}

	public void setTutkPASSWD(String tutkPASSWD) {
		this.tutkPASSWD = tutkPASSWD;
	}

	public boolean isGenerationOneGateway() {
		if (!StringUtil.isNullOrEmpty(gwVer) && gwVer.split("[.]").length == 3) {
			return GW_GENERATION_ONE.equals(gwVer.split("[.]")[0]);
		}
		return false;
	}

	public boolean isGenerationTwoGateway() {
		if (!StringUtil.isNullOrEmpty(gwVer) && gwVer.split("[.]").length == 3) {
			return GW_GENERATION_TWO.equals(gwVer.split("[.]")[0]);
		}
		return false;
	}

	public boolean isGenerationThreeGateway() {
		if (!StringUtil.isNullOrEmpty(gwVer) && gwVer.split("[.]").length == 3) {
			return GW_GENERATION_THREE.equals(gwVer.split("[.]")[0]);
		}
		return false;
	}

	public boolean isFlowerGatewayWithDisk() {
		if (!StringUtil.isNullOrEmpty(gwVer) && gwVer.split("[.]").length == 3) {
			return GW_FLOWER_WITH_DISK.equals(gwVer.split("[.]")[1]);
		}
		return false;
	}

	public boolean isFlowerGatewayNoDisk() {
		if (!StringUtil.isNullOrEmpty(gwVer) && gwVer.split("[.]").length == 3) {
			return GW_FLOWER_NO_DISK.equals(gwVer.split("[.]")[1]);
		}
		return false;
	}

	public boolean isMonitorGateway() {
		if (!StringUtil.isNullOrEmpty(gwVer) && gwVer.split("[.]").length == 3) {
			return GW_MONITOR.equals(gwVer.split("[.]")[1]);
		}
		return false;
	}
}
