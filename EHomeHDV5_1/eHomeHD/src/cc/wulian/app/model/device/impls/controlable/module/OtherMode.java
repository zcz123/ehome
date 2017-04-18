package cc.wulian.app.model.device.impls.controlable.module;

public class OtherMode extends ModuleLightStatue{

	private boolean isAuto = false;
	
	public OtherMode(String modeModule) {
		super(modeModule);
	}

	public boolean isAuto() {
		return isAuto;
	}

	public void setAuto(boolean isAuto) {
		this.isAuto = isAuto;
	}

	
}
