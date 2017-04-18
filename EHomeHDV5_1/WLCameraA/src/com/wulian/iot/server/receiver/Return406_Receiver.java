package com.wulian.iot.server.receiver;

import com.wulian.iot.view.manage.PresettingManager.Return406;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public abstract class Return406_Receiver extends BroadcastReceiver{
	public static String ACTION = "Return406_Receiver" ;
	@Override
	public void onReceive(Context context, Intent intent) {
		if(intent!=null){
			retrunData((Return406)intent.getSerializableExtra("return"));
		}
	}
	public abstract void retrunData(Return406 return406);
}
