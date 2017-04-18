package cc.wulian.smarthomev5.activity;

import android.content.Intent;
import android.os.Bundle;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.fragment.device.DeviceFunctionFragment;
import cc.wulian.smarthomev5.fragment.device.MiniGatewayVoiceChooseFragment;

public class MiniGatewayVoiceChooseActivity extends EventBusActivity{
	
	public final static String DEVICE_EP="device_ep";
	public final static String DEVICE_EPTYPE="device_eptype";
	public final static String VOICE_SIZE="voice_size";
	public final static String GWID="gwid";
	public final static String DVID="dvid";
	public static  Intent intent=new Intent();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null){
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content, new MiniGatewayVoiceChooseFragment())
			.commit();
		}
	}
	
	@Override
	public void finish() {
		this.setResult(211, intent); 	
		super.finish();
	}
}
