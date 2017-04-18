package cc.wulian.smarthomev5.fragment.more;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.ScanQRCodeActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

/**
 * GPS Item
 * @author Administrator
 *
 */
public class ScanQRCodeItem extends AbstractSettingItem {

	public ScanQRCodeItem(Context context) {
		super(context, R.drawable.scan_qrcode_item, R.string.login_rich_scan);
	}
	
	@Override
	public void initSystemState() {
		super.initSystemState();
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.arrow_cutover_gateway);
	}

	@Override
	public void doSomethingAboutSystem() {
		Intent intent = new Intent();
		intent.setClass(mContext, ScanQRCodeActivity.class);
		mContext.startActivity(intent);
	}

}
