package cc.wulian.smarthomev5.event;

import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.ResultUtil;
import cc.wulian.smarthomev5.activity.MainApplication;

public class SigninEvent
{
	private static final String TAG_PKG = MainApplication.getApplication().getPackageName();

	public static final String ACTION_SIGNIN_REQUEST = TAG_PKG + ".action.SIGNIN_REQUEST";
	public static final String ACTION_SIGNIN_RESULT = TAG_PKG + ".action.SIGNIN_RESULT";

	public String action;
	public String gwID;
	public String gwPwd;
	public String gwIP;
	public String gwSerIP;
	public String zoneID;
	public String time;
	public int result;
	public boolean isSigninSuccess;
	public boolean autoSignin;
	public String gwType;

	// this constractor just for connect [request]
	public SigninEvent( String action, String gwID,boolean autoSignin )
	{
		this.action = action;
		this.autoSignin = autoSignin;
		this.gwID = gwID;
	}
	// this constractor just for connect [result]
	public SigninEvent( String action, GatewayInfo info, int result )
	{
		this.action = action;
		gwID = info.getGwID();
		gwPwd = info.getGwPwd();
		gwIP = info.getGwIP();
		gwSerIP = info.getGwSerIP();
		zoneID = info.getZoneID();
		time = info.getTime();
		this.result = result;
		isSigninSuccess = ResultUtil.RESULT_SUCCESS == result;
		gwType=info.getGwType();
	}
}
