package cc.wulian.app.model.device.impls.controlable.module;


public class ModuleLightStatue implements CreatModuleInterface{

	//状态信息
	private String lightValues;
	private String speedValues;
	private String modeModule;
	private String rr;
	private String gg;
	private String bb;
	
	//性能信息
	private boolean adjustLight;
	private boolean adjustSpeed;
	private String mOpen;
	
	public ModuleLightStatue(String modeModule){
		this.modeModule = modeModule;
		this.adjustLight = true;
		this.adjustSpeed = true;
	}
	
	@Override
	public boolean isAdjustLight() {
		// TODO Auto-generated method stub
		return adjustLight;
	}

	@Override
	public void setAdjustLight(boolean adjustLight) {
		this.adjustLight = adjustLight;
	}
	
	@Override
	public boolean isAdjustSpeed() {
		// TODO Auto-generated method stub
		return adjustSpeed;
	}

	@Override
	public void setAdjustSpeed(boolean adjustSpeed) {
		this.adjustSpeed = adjustSpeed;
	}

	@Override
	public String getLightValues() {
		return lightValues;
	}

	@Override
	public void setLightValues(String lightValues) {
		this.lightValues = lightValues;
	}

	@Override
	public String getSpeedValues() {
		return speedValues;
	}

	@Override
	public void setSpeedValues(String speedValues) {
		this.speedValues = speedValues;
	}

	@Override
	public String getModuleMode() {
		return modeModule;
	}

	@Override
	public void setModuleMode(String moduleMode) {
		this.modeModule = moduleMode;
	}

	@Override
	public void open(String isopen) {
		this.mOpen = isopen;
	}

	@Override
	public boolean isOpen() {
		return "1".equals(this.mOpen);
	}

	public String getRr() {
		return rr;
	}

	public void setRr(String rr) {
		this.rr = rr;
	}

	public String getGg() {
		return gg;
	}

	public void setGg(String gg) {
		this.gg = gg;
	}

	public String getBb() {
		return bb;
	}

	public void setBb(String bb) {
		this.bb = bb;
	}

}
