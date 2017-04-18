package cc.wulian.smarthomev5.fragment.more.nfc;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.NFCActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class NFCItem extends AbstractSettingItem {

	public NFCItem(Context context) {
		super(context, R.drawable.icon_more_nfc, context.getResources()
				.getString(R.string.more_nfc_function));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.system_intent_right);
		infoImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				startActivity();
			}

		});
	}

	@Override
	public void doSomethingAboutSystem() {
		startActivity();
	}

	private void startActivity() {
		Intent intent = new Intent();
		intent.setClass(mContext, NFCActivity.class);
		intent.putExtra(NFCActivity.IS_EXECUTE, false);
		mContext.startActivity(intent);
	}

}
