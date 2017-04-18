package cc.wulian.smarthomev5.entity;

public class DeviceDetialsEntity 
{
	public static final int deviceDetailsSettingMoreDelate = 0;
	public static final int deviceDetailsSettingMoreRefresh = 1;
	public static final int deviceDetailsSettingMoreFind = 2;
	public static final int deviceDetailsSettingMoreRename = 3;
	public static final int deviceDetailsSettingMoreFavorite = 4;
	public static final int deviceDetailsSettingMoreAreaSetting = 5;
	public static final int deviceDetailsSettingMoreDeviceSet = 6;
	public static final int deviceDetailsSettingMoreDeviceHelp = 7;
	/** 
	* @Fields serialVersionUID : TODO(用一句话描述这个变量表示什么) 
	*/
	public int deviceDetailsIcon;
	public int deviceDetailsTypeId;
	public String deviceDetailsType;

	public int getdeviceDetailsTypeId() {
		return deviceDetailsTypeId;
	}

	public void setdeviceDetailsTypeId( int deviceDetailsTypeId ) {
		this.deviceDetailsTypeId = deviceDetailsTypeId;
	}
	
	public int getdeviceDetailsIcon() {
		return deviceDetailsIcon;
	}

	public void setdeviceDetailsIcon( int deviceDetailsIcon ) {
		this.deviceDetailsIcon = deviceDetailsIcon;
	}

	public String getdeviceDetailsType() {
		return deviceDetailsType;
	}

	public void setdeviceDetailsType( String deviceDetailsType ) {
		this.deviceDetailsType = deviceDetailsType;
	}
}