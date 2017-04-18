package cc.wulian.app.model.device.impls.controlable.doorlock.iot;

public interface WL_89_DoorLock_Logic {
	public String isLowPower(String epData);	// 欠压报警
	public String isAntiPrizing(String epData);// 防撬报警
	public boolean isAntiLock();// 反锁报警
	public boolean isDissolveAntiLock();// 解除反锁报警
	public boolean isAntiStress();// 防劫持报警
	public boolean isCheckAdminRight();// 管理员认证成功
	public boolean isCheckAdminWrong();// 管理员认证失败
	public boolean isAppPasswordWrong();// App密码验证失败
	public boolean isClose();//是否关闭
	public boolean isOpen();//是否打开
	public void finish();
}
