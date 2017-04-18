package cc.wulian.smarthomev5.activity;

import android.os.Bundle;
import cc.wulian.smarthomev5.fragment.setting.voice.VoiceReminderFragment;

public class VoiceReminderActivity extends EventBusActivity{

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        getSupportFragmentManager().beginTransaction().add(android.R.id.content, new VoiceReminderFragment()).commit();
	}
	@Override
	protected boolean finshSelf() {
		return false;
	}
}
