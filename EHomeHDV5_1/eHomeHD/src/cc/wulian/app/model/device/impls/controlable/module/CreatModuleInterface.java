package cc.wulian.app.model.device.impls.controlable.module;


public interface CreatModuleInterface {

	/**
	 * 是否可调亮度
	 * @return
	 */
	public boolean isAdjustLight();
	
	public void setAdjustLight(boolean adjustLight);
	
	/**
	 * 是否可调频率
	 * @return
	 */
	public boolean isAdjustSpeed();
	
	public void setAdjustSpeed(boolean adjustSpeed);
	
	
	public boolean isOpen();
	
	/**
	 * 状态信息
	 * @return
	 */
	public String getLightValues();
	
	public void setLightValues(String lightValues);
	
	public String getSpeedValues();
	
	public void setSpeedValues(String speedValues);
	
	public String getModuleMode();

	public void setModuleMode(String moduleMode);

	/**
	 * RGB颜色
	 * @param rr
	 */
	public String getRr();
	
	public void setRr(String rr);

	public String getGg();

	public void setGg(String gg);

	public String getBb();

	public void setBb(String bb);
	
	/**
	 * 开
	 */
	public void open(String isopen);
	
}
