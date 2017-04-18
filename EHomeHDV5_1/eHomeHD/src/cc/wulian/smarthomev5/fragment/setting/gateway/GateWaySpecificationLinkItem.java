package cc.wulian.smarthomev5.fragment.setting.gateway;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.TypedValue;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

import java.util.Locale;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.fragment.setting.router.RouterSettingActivity;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

public class GateWaySpecificationLinkItem extends AbstractSettingItem{

	public static final String URL_SPECIFICATION = "http://qr.sh.gg/dream-flower";
	public static final String URL_SPECIFICATION_ENGLISH = "http://qr.sh.gg/dream-flowerenglish";

	public GateWaySpecificationLinkItem(Context context) {
		super(context, R.drawable.icon_gateway_specification, context.getResources()
				.getString(R.string.set_account_manager_gw_specification));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.system_intent_right);
		infoImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				jumpToRouterSettingActivity();	
			}
		});
	}
	
	@Override
	public void doSomethingAboutSystem() {
		jumpToRouterSettingActivity();	
	}
	
	
	/**
	 * 跳转至RouterSettingActivity
	 */
	private void jumpToRouterSettingActivity() {
//		Intent intent = new Intent();
//		intent.setClass(mContext, RouterSettingActivity.class);
//		mContext.startActivity(intent);

		String title = mContext.getResources().getString(
				R.string.set_account_manager_gw_specification);
		String leftIconText = mContext.getResources().getString(
				R.string.about_back);
		if(Locale.getDefault().toString().equals("zh_CN")||Locale.getDefault().toString().equals("zh_TW") ){
			IntentUtil.startCustomBrowser(mContext, URL_SPECIFICATION, title,
					leftIconText);
		}else {
			IntentUtil.startCustomBrowser(mContext, URL_SPECIFICATION_ENGLISH, title,
					leftIconText);
		}
	}
}
