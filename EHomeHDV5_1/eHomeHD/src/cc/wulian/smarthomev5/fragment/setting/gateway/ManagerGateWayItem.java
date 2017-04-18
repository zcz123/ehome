package cc.wulian.smarthomev5.fragment.setting.gateway;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Toast;

import java.io.File;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.ManagerAccountActivity;
import cc.wulian.smarthomev5.activity.SwitchAccountActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.URLConstants;

public class ManagerGateWayItem extends AbstractSettingItem {
	private String pluginName = "ManagerGW.zip";


	public ManagerGateWayItem(Context context) {
		super(context, R.drawable.manager_gateway_item, R.string.controlCenter_managergw);
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.arrow_cutover_gateway);
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
		Intent intent= new Intent();
		intent.setClass(mContext, ManagerAccountActivity.class);
		mContext.startActivity(intent);
	}
}
