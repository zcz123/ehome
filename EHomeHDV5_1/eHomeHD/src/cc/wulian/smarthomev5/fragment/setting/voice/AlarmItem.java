package cc.wulian.smarthomev5.fragment.setting.voice;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.view.View;
import android.view.View.OnClickListener;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.VoiceReminderActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class AlarmItem extends AbstractSettingItem {

	public AlarmItem(Context context) {
		super(context, R.drawable.setting_voice_alarm, context
				.getString(R.string.scene_alarm));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.voice_remind_right);
		infoImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startVoiceActivity();
			}
		});
	}

	@Override
	public void doSomethingAboutSystem() {
		startVoiceActivity();
	}

	private void startVoiceActivity() {
		Intent intent = new Intent();
		intent.putExtra("BASENAME" , "A");
		intent.setClass(mContext, VoiceReminderActivity.class);
		mContext.startActivity(intent);
	}

}
