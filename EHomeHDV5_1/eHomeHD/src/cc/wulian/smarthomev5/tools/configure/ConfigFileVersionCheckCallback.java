package cc.wulian.smarthomev5.tools.configure;

public interface ConfigFileVersionCheckCallback {
	
	public void onIsNormal(double version);

	public void onIsUpdata(double newVersion);
	
	public void onCheckError(double version);
}
