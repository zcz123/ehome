package cc.wulian.smarthomev5.fragment.setting.voice;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.view.View;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.VoiceReminderActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

/**
 * Created by Administrator on 2016/11/17 0017.
 */

public class PushItem extends AbstractSettingItem {

    public PushItem(Context context) {
        super(context, R.drawable.setting_voice_push, context.getResources().getString(R.string.gateway_settings_alarm_push_hint));
    }

    @Override
    public void initSystemState() {
        super.initSystemState();
        infoImageView.setVisibility(View.VISIBLE);
        infoImageView.setImageResource(R.drawable.voice_remind_right);
        infoImageView.setOnClickListener(new View.OnClickListener() {

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
        intent.putExtra("BASENAME" , "P");
        intent.setClass(mContext, VoiceReminderActivity.class);
        mContext.startActivity(intent);
    }
}
