package cc.wulian.smarthomev5.entity.camera;

import cc.wulian.smarthomev5.entity.BaseCameraEntity;

public class EagleCameraEntity extends BaseCameraEntity {

	private String tutkPwd;
	private String tutkUid;
	private boolean isUser;
	
	public void setTutkPwd(String tutkPwd) {
		this.tutkPwd = tutkPwd;
	}
	public String getTutkPwd() {
		return tutkPwd;
	}
	public void setTutkUid(String tutkUid) {
		this.tutkUid = tutkUid;
	}
	public String getTutkUid() {
		return tutkUid;
	}
	public boolean isUser() {
		return isUser;
	}
	public void setUser(boolean isUser) {
		this.isUser = isUser;
	}
}
