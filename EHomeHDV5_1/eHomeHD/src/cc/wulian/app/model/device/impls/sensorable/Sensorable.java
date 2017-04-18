package cc.wulian.app.model.device.impls.sensorable;

/**
 * 设备是否支持检测系统
 */
public interface Sensorable
{
 // do nothing, later may be add something, e.e.e···
	public String checkDataRatioFlag();
	/**
	 * 单位符号
	 * @return
	 */
	public String unit(String ep,String epType);
	/**
	 * 单位名字
	 * @return
	 */
	public String unitName();
}
