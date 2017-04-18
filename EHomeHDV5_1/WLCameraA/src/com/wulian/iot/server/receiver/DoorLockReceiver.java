package com.wulian.iot.server.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public  class DoorLockReceiver extends BroadcastReceiver{
	public final static String ACTION  = "DoorLock";
	public final static String CLOSEOV788 = "CLOSEOV788";
	public final static  String OPENDOORFROMIOT = "OPENDOORFROMIOT";
	public final static String OPENDOORPWD = "OPENDOORPWD";
	public final static  String DoorLockOperMode = "dlom";//门锁操作类型
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent != null){
			if(intent.getSerializableExtra(DoorLockOperMode).equals(CLOSEOV788)){
				this.closeOV788();
			} else if(intent.getSerializableExtra(DoorLockOperMode).equals(OPENDOORFROMIOT)){
				this.openDoorFromIot(intent);
			}
		}
	}
	public void closeOV788(){
	}
	public void openDoorFromIot(Intent intent){
	}
}
