package cc.wulian.app.model.device.impls.alarmable;


/**
 * 设备是否支持报警系统
 */
public interface Alarmable
{
	/**
	 * 是否报警中
	 */
	public boolean isAlarming();
	
	/**
	 * 是否正常状态
	 */
	public boolean isNormal();

	/**
	 * 获取消警的协议
	 */
	public String getCancleAlarmProtocol();
	/**
	 * 报警协议
	 */
	public String getAlarmProtocol();

	/**
	 * 正常协议
	 */
	public String getNormalProtocol();

	/**
	 * 管家报警条件显示文字
	 */
	public String getAlarmString();

	/**
	 * 管家不报警条件显示文字
	 */
	public String getNormalString();
	
	
	public boolean isDestory();
	
	
	public boolean isLowPower();
	/**
	 * 控制门锁操作记录并没有语音报警
	 */
	public CharSequence parseAlarmProtocol(String epData);
}
