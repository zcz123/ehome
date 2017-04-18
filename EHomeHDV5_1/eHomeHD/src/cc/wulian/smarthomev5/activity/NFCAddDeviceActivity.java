package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.more.nfc.NFCAddDeviceFragment;

public class NFCAddDeviceActivity extends EventBusActivity{
	private NFCAddDeviceFragment fragement;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		fragement = new NFCAddDeviceFragment();
		fragement.setArguments(getIntent().getExtras());
			getSupportFragmentManager()
			.beginTransaction()
			.add(android.R.id.content,fragement)
			.commit();
		}
	@Override
	public boolean fingerRightFromCenter() {
		return false;
	}
	
}
