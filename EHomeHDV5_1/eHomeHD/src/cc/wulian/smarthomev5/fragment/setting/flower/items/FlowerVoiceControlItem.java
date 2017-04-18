package cc.wulian.smarthomev5.fragment.setting.flower.items;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.flower.FlowerVoiceControlActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class FlowerVoiceControlItem extends AbstractSettingItem {

	public FlowerVoiceControlItem(Context context) {
		super(context);
		this.name = "语音控制";
		
	}
	@Override
	public void initSystemState() {
		super.initSystemState();
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.voice_remind_right);
	}
	@Override
	public void doSomethingAboutSystem() {
		mContext.startActivity(new Intent(mContext, FlowerVoiceControlActivity.class));
	}

}
