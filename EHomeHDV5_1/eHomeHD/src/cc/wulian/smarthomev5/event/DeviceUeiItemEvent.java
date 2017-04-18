package cc.wulian.smarthomev5.event;

public class DeviceUeiItemEvent {
	/**
	 * 网关ID
	 */
	public String gwID;
	/**
	 * 设备ID
	 */
	public String devID;
	/**1:新增</br>
	 *2:更新</br>
	 *3:获取</br>
	 *4:删除</br>
	 */
	public String mode;
	/**
	 * 数据更新时间戳
	 */
	public long time;
	/**
	 * mode_deviceCode
	 */
	public String key;
	/**
	 * 返回的json串</br>
	 * 根据不同的mode返回的串有所不同，见《开发协议》“附录”中 万能红外转发器（MK001）
	 */
	public String data;
	public DeviceUeiItemEvent(String gwID,String devID,String mode,Long time,String key,String data){
		this.gwID=gwID;
		this.devID=devID;
		this.mode=mode;
		this.time=time;
		this.key=key;
		this.data=data;
	}
}
