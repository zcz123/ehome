package cc.wulian.app.model.device.impls.controlable.doorlock.iot.impl;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import cc.wulian.app.model.device.impls.controlable.doorlock.iot.WL_89_DoorLock_Logic;
import cc.wulian.smarthomev5.R;
public class WL_89_DoorLockImpl implements WL_89_DoorLock_Logic {
	private Context mContext = null;
    private static Map<String,String> doorLockState  = new HashMap<String, String>();
	public WL_89_DoorLockImpl(){
	}
	public WL_89_DoorLockImpl(Context mContext){
		this.mContext = mContext;
	}
	static {
		doorLockState.put("lowPower", "021C");
		doorLockState.put("antiPrizing", "021D");
	}
	@Override
	public String isLowPower(String epData) {
		return null;
	}
	@Override
	public String isAntiPrizing(String epData){
	return null;
	}
	@Override
	public boolean isAntiLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isDissolveAntiLock() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAntiStress() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCheckAdminRight() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isCheckAdminWrong() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isAppPasswordWrong() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isClose() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isOpen() {
		// TODO Auto-generated method stub
		return false;
	}
	@Override
	public void finish() {
		if(mContext != null){
			mContext = null;
		}
		if(doorLockState != null){
			doorLockState = null;
		}
	}
}
