package cc.wulian.app.model.device.impls.controlable;

/**
 * 设备是否支持控制系统
 */
public interface Controlable 
{
	/**
	 * 开设备控制命令码(如果是多位设备将是合并的发送命令)
	 */
	public String getOpenSendCmd();
	
	/**
	 * 关设备控制命令码(如果是多位设备将是合并的发送命令)
	 */
	public String getCloseSendCmd();
	
	/**
	 * 控制系统发送的控制命令码( epData column)
	 * <br/>
	 * 如果指定发送的sendData不为空，则使用sendData作为发送控制码
	 *//*
	public String controlDevice(String sendData);*/
	
	/**
	 * 是否停止
	 */
	public boolean isStoped();

	/**
	 * 停止设备控制命令码
	 */
	public String getStopSendCmd();

	/**
	 * 停止设备协议
	 */
	public String getStopProtocol();
	/**
	 * 是否开状态
	 */
	public boolean isOpened();
	
	/**
	 * 是否关状态
	 */
	public boolean isClosed();

	/**
	 * 开状态协议
	 */
	public String getOpenProtocol();

	/**
	 * 关状态协议
	 */
	public String getCloseProtocol();
}
