package com.wulian.iot.server.receiver;
import com.wulian.iot.view.manage.PresettingManager.Smit406_Pojo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
public abstract class Smit406_Receiver extends BroadcastReceiver {
	public final static String Add = "1";
	public final static String Update = "2";
	public final static String Query = "3";
	public final static String Del = "4";
	public final static String OPER_406 = "406";
	public final static String ACTION = "Smit406_Receiver";
	public final static String DesktopCamera_406_Preset = "DesktopCamera_406_Preset";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (intent != null) {
			Smit406_Pojo oper = (Smit406_Pojo) intent.getSerializableExtra(OPER_406);
			if(oper!=null){
				switch(oper.getOperate()){
				case "2":
					sendCommand_Add(oper);				
				  return;
				case "3":
					sendCommand_Query();
					return;
				}
			}
		}
	}

	public abstract void sendCommand_Add(Smit406_Pojo smit406_Pojo_Item);
	public abstract void sendCommand_Query();
}
