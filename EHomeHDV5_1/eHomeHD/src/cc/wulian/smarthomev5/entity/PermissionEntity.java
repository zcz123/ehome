package cc.wulian.smarthomev5.entity;

import cc.wulian.ihome.wan.util.StringUtil;

public class PermissionEntity implements Comparable<PermissionEntity>{

	public String gwID;
	public String userID;
	public String userName;
	public String status;
	public String address;
	public String phone;
	
	public String getGwID() {
		return gwID;
	}
	public void setGwID(String gwID) {
		this.gwID = gwID;
	}
	public String getUserID() {
		return userID;
	}
	public void setUserID(String userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	@Override
	public int compareTo(PermissionEntity arg0) {
		long userIDLeft =  StringUtil.toLong(this.userID);
		long userIDRight =  StringUtil.toLong(arg0.getUserID());
		if(userIDLeft < userIDRight)
			return -1;
		else if(userIDLeft > userIDRight)
			return 1;
		return 0;
	}
	
	
}
