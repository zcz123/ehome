package cc.wulian.app.model.device.impls.controlable.aircondtion;

import android.content.Context;
import android.view.View;

public interface AirCondition {

	public String getGwID();

	public void setGwID(String gwID);

	public String getDevID();

	public void setDevID(String devID);

	public String getKeyID();

	public void setKeyID(String keyID);

	public String getKeyName();

	public void setKeyName(String keyName);

	/**
	 * 
	 * @return 性能信息
	 */
	/**
	 * 是否有送风模式
	 * 
	 * @return
	 */
	public boolean isHasBlowModel();

	public void setHasBlowModel(boolean hasBlowModel);

	/**
	 * 是否有制冷模式
	 * 
	 * @return
	 */
	public boolean isHasCoolModel();

	public void setHasCoolModel(boolean hasCoolModel);

	/**
	 * 是否有制热模式
	 * 
	 * @return
	 */
	public boolean isHasHotModel();

	public void setHasHotModel(boolean hasHotModel);

	/**
	 * 是否有自动模式
	 * 
	 * @return
	 */
	public boolean isHasAutoModel();

	public void setHasAutoModel(boolean hasAutoModel);

	/**
	 * 是否有除湿模式
	 * 
	 * @return
	 */
	public boolean isHasArefactionModel();

	public void setHasArefactionModel(boolean hasArefactionModel);

	/**
	 * 是否有风向设置
	 * 
	 * @return
	 */
	public boolean isHasWindDirectionSet();

	public void setHasWindDirectionSet(boolean hasWindDirectionSet);

	/**
	 * 风速性能
	 * 
	 * @return
	 */
	public String getCurWindSpeed();

	public void setCurWindSpeed(String curWindSpeed);

	/**
	 * 是否有风量调节
	 * 
	 * @return
	 */
	public boolean isHasAirVolumeAdjust();

	public void setHasAirVolumeAdjust(boolean hasAirVolumeAdjust);

	/**
	 * 
	 * @return 制冷设定温度上下限
	 */

	public String getCoolMaxTemp();

	public void setCoolMaxTemp(String coolMaxTemp);

	public String getCoolMinTemp();

	public void setCoolMinTemp(String coolMinTemp);

	/**
	 * 
	 * @return 制热设定温度上下限
	 */

	public String getHotMaxTemp();

	public void setHotMaxTemp(String hotMaxTemp);

	public String getHotMinTemp();

	public void setHotMinTemp(String hotMinTemp);

	/**
	 * 
	 * @return 状态信息
	 */

	public String getCurTemp();

	public void setCurTemp(String curTemp);

	public String getCurSetTemp();

	public void setCurSetTemp(String curSetTemp);

	public String getCurModel();

	public void setCurModel(String curModel);

	public String getCurRunStatus();

	public void setCurRunStatus(String curRunStatus);

	public String getCurWindPower();

	public void setCurWindPower(String curWindPower);

	public String getCurWindDirection();

	public void setCurWindDirection(String curWindDirection);

	public String getCurStadus();

	public void setCurStadus(String curStadus);

	public String getCurDevName();

	public void setCurDevName(String curDevName);

	public String getCurID();

	public void setCurID(String curID);

	/**
	 * 开空调
	 */
	public void open();

	/**
	 * 关空调
	 */
	public void close();

	/**
	 * 增加温度
	 */
	public void addTemp();

	/**
	 * 降低温度
	 */
	public void reduceTemp();

	/**
	 * 增加风量
	 */
	public void addWindPower();

	/**
	 * 减小风量
	 */
	public void reduceWindPower();

	/**
	 * 设置模式
	 */
	public void setModel(String model);

	/**
	 * 设置风向
	 */
	public void setWindDirction(String dirction);

	/**
	 * 获取视图
	 */
	public View getView(Context context);

	/**
	 * 一些包含不做解析的数据,控制时需要这些数据
	 */
	public String getStatusData_1();

	public void setStatusData_1(String statusData_1);

	public String getStatusData_2();

	public void setStatusData_2(String statusData_2);

	public String getControlData_1();

	public void setControlData_1(String controlData_1);

	public String getControlData_2();

	public void setControlData_2(String controlData_2);

}
