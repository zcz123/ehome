package com.wulian.iot.bean;

import java.io.Serializable;

import com.wulian.iot.utils.CmdUtil;
import com.yuantuo.netsdk.TKCamHelper;


public class IOTCameraBean extends BaseCameraInfo
{
	private static final long serialVersionUID = 1L;

	public static final int CAMERA_TYPE_IP = 1;

	public static final int CAMERA_TYPE_DVR_4 = 4;
	public static final int CAMERA_TYPE_DVR_8 = 8;

	public static final int CAMERA_TYPE_CLOUD_1 = 11;
	public static final int CAMERA_TYPE_CLOUD_2 = 12;
	public static final int CAMERA_TYPE_CLOUD_3 = 13;
	
	public static final int CAMERA_TYPE_CLOUD_WLPG = 21;
	public static final int CAMERA_TYPE_CLOUD_WLIC = 22;
	
	public static final String EXTRA_CAMERA_INFO = "extra_camera";

	public static final String CAMERA_KEY_UID = "_UID";
	public static final String CAMERA_KEY_PASS = "_PASS";
	public static final String CAMERA_KEY_HOST = "_HOST";
	public static final String CAMERA_KEY_PORT = "_PORT";
	public static final String CAMERA_KEY_CAMERATYPE = "_CAMERATYPE";
	public static final String CAMERA_KEY_USERNAME = "_USERNAME";
	public static final String CAMERA_KEY_CHANNEL = "_CHANNEL";
	public static final String CAMERA_KEY_AREAID = "_AREAID";
	public static final String CAMERA_KEY_GWID = "_GWID";
	public static final String CAMERA_DETAULT_ID = "-1";

	public int camId;
	public int camType;
	public String camName;
	public int iconId;
	public String uid;
	public String host;
	public int port;
	public String username;
	public String password;
	public int channel;
	public int istream;
	public String areaID;
	public String bindDev;
	public String gwId;
	public IOTCameraBean()
	{
		istream = 3;
	}

	public IOTCameraBean( int camId, int camType, String camName, int iconId, String uid, String host, int port, String username, String password, int channel, String bindDev,
			String gwId )
	{
		this.camId = camId;
		this.camType = camType;
		this.camName = camName;
		this.iconId = iconId;
		this.uid = uid;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.channel = channel;
		this.bindDev = bindDev;
		this.gwId = gwId;
	}

	public IOTCameraBean( int camId, int camType, String camName, int iconId , String uid, String host, int port, String username, String password, int channel, int istream,
			String bindDev, String gwId )
	{
		this.camId = camId;
		this.camType = camType;
		this.camName = camName;
		this.iconId = iconId;
		this.uid = uid;
		this.host = host;
		this.port = port;
		this.username = username;
		this.password = password;
		this.channel = channel;
		this.istream = istream;
		this.bindDev = bindDev;
		this.gwId = gwId;
	}
     // TODO 
	@Override
	public String toString() {
		return camName != "" ? camName : (port == 12201 ? host : (host +CmdUtil.COMPANY_COLON+ port));
	}

	public int getCamId() {
		return camId;
	}

	public void setCamId( int camId ) {
		this.camId = camId;
	}

	public int getCamType() {
		return camType;
	}

	public void setCamType( int camType ) {
		this.camType = camType;
	}

	public String getCamName() {
		return camName;
	}

	public void setCamName( String camName ) {
		this.camName = camName;
	}
	
	public void setIconId( int iconId ) {
		this.iconId = iconId;
	}
	
	public int getIconId() {
		return iconId ;
	}
	
	public String getUid() {
		return uid;
	}

	public void setUid( String uid ) {
		this.uid = uid;
	}

	public String getHost() {
		return host;
	}

	public void setHost( String host ) {
		this.host = host;
	}

	public int getPort() {
		return port;
	}

	public void setPort( int port ) {
		this.port = port;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername( String username ) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword( String password ) {
		this.password = password;
	}

	public int getChannel() {
		return channel;
	}

	public void setChannel( int channel ) {
		this.channel = channel;
	}

	public int getIstream() {
		return istream;
	}

	public void setIstream( int istream ) {
		this.istream = istream;
	}

	public String getAreaID() {
		return areaID;
	}

	public void setAreaID( String areaID ) {
		this.areaID = areaID;
	}

	public String getBindDev() {
		return bindDev;
	}

	public void setBindDev( String bindDev ) {
		this.bindDev = bindDev;
	}

	public String getGwId() {
		return gwId;
	}

	public void setGwId( String gwId ) {
		this.gwId = gwId;
	}
}