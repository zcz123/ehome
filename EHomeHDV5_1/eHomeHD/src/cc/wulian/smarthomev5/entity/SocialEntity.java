package cc.wulian.smarthomev5.entity;


public class SocialEntity
{
	public String gwID;
	public String mCmd;
	public String socialID;
	public String userType;
	public String userID;
	public String appID;
	public String userName;
	public String data;
	public String time;

	public SocialEntity()
	{

	}

	public String getGwID() {
		return gwID;
	}

	public void setGwID(String gwID) {
		this.gwID = gwID;
	}

	public String getmCmd() {
		return mCmd;
	}

	public void setmCmd(String mCmd) {
		this.mCmd = mCmd;
	}

	public String getSocialID() {
		return socialID;
	}

	public void setSocialID(String socialID) {
		this.socialID = socialID;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserID() {
		return userID;
	}

	public void setUserID(String userID) {
		this.userID = userID;
	}

	public String getAppID() {
		return appID;
	}

	public void setAppID(String appID) {
		this.appID = appID;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}
	
	
//
//	public SocialEntity( Cursor cursor )
//	{
//		gwID = cursor.getString(Social.POS_GW_ID);
//		socialID = cursor.getString(Social.POS_SOCIAL_ID);
//		userType = cursor.getString(Social.POS_USER_TYPE);
//		userID = cursor.getString(Social.POS_USER_ID);
//		appID = cursor.getString(Social.POS_APP_ID);
//		userName = cursor.getString(Social.POS_USER_NAME);
//		data = cursor.getString(Social.POS_DATA);
//		time = cursor.getString(Social.POS_TIME);
//	}

}
