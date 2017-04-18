package cc.wulian.smarthomev5.fragment.setting.gateway;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.yuantuo.customview.ui.WLToast;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.tools.AccountManager;

public class CloneGatewayItem extends AbstractSettingItem{

	public CloneGatewayItem(Context context) {
		super(context, R.drawable.icon_gateway_id, "备份及恢复");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		getInfoImageView().setVisibility(View.VISIBLE);
	}

	public void setLongClickListener(View.OnLongClickListener onLongClickListener){
		view.setOnLongClickListener(onLongClickListener);
	}

	@Override
	public void doSomethingAboutSystem() {
		Intent intent= new Intent();
		intent.setClass(mContext, Html5PlusWebViewActvity.class);
		intent.putExtra(Html5PlusWebViewActvity.KEY_URL, "file:///android_asset/cloneGateway/cloneGateway.html");
		mContext.startActivity(intent);
	}
}
