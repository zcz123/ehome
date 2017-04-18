package cc.wulian.app.model.device.impls.alarmable;

import cc.wulian.ihome.wan.NetSDK;

/**
 * 设备是否支持撤防布防系统
 * 
 * alarm device can be set defense state, use {@linkplain NetSDK#sendSetDevMsg(String, String, String, String, String, String, String, String, String)}
 */
public interface Defenseable
{
	/**
	 * 是否布防
	 */
	public boolean isDefenseSetup();
	
	/**
	 * 是否撤防
	 */
	public boolean isDefenseUnSetup();

	/**
	 * 是否长期设防
	 */
	public boolean isLongDefenSetup();
	/**
	 * 布防设备控制命令码
	 */
	public String getDefenseSetupCmd();

	/**
	 * 撤防设备控制命令码
	 */
	public String getDefenseUnSetupCmd();

	/**
	 * 布防设备协议
	 */
	public String getDefenseSetupProtocol();

	/**
	 * 撤防设备协议
	 */
	public String getDefenseUnSetupProtocol();
}