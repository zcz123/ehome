package cc.wulian.smarthomev5.fragment.setting.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;


import org.json.JSONArray;
import org.json.JSONException;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.minigateway.MiniGatewayChannelConflictHelpIdeaActivity;
import cc.wulian.smarthomev5.activity.minigateway.MiniGatewayRelaySettingActivity;
import cc.wulian.smarthomev5.event.MiniGatewayEvent;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;
import cc.wulian.smarthomev5.fragment.setting.minigateway.MiniRouterSettingActivity;
import cc.wulian.smarthomev5.tools.AccountManager;
import de.greenrobot.event.EventBus;

public class MiniRouterSettingItem extends AbstractSettingItem{

	private boolean isSend=false;
	public MiniRouterSettingItem(Context context) {
		super(context, R.drawable.account_information_mini_router_icon, context.getResources()
				.getString(R.string.gateway_router_setting_wifi_relay_setting));
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		EventBus.getDefault().register(this);
		infoImageView.setVisibility(View.VISIBLE);
		infoImageView.setImageResource(R.drawable.system_intent_right);
		infoImageView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
			StartToMiniRouterSettingActivity();	
			}
		});
		// 增加一条去查询wifi 是否配置的请求
		getWifiName();

	}
	
	@Override
	public void doSomethingAboutSystem() {
		// TODO Auto-generated method stub
		StartToMiniRouterSettingActivity();
	}

	private void StartToMiniRouterSettingActivity(){
		Intent intent = new Intent();
		if (StringUtil.isNullOrEmpty(ssid)||bssid.equals("00:00:00:00:00:00")){
			intent.putExtra("FLAG_0", "EXTRA_0");
			intent.setClass(mContext,MiniGatewayRelaySettingActivity.class);
		}else {
			intent.putExtra("miniwifiname",ssid);
			intent.setClass(mContext, MiniRouterSettingActivity.class);

		}
		mContext.startActivity(intent);
	}
	private void getWifiName(){
		String gwID = AccountManager.getAccountManger().getmCurrentInfo()
				.getGwID();
		NetSDK.sendMiniGatewayWifiSettingMsg(gwID,"6","get",null);
		isSend=true;
	}
	private String ssid="", bssid ="";
	public void onEventMainThread(MiniGatewayEvent event){
		if (isSend){
			if (event.getCmdindex().equals("6")){
				try {
					JSONArray jsonArray = new JSONArray(event.getData());
					ssid=jsonArray.getJSONObject(0).getString("ssid");
					bssid= jsonArray.getJSONObject(0).getString("bssid");
					EventBus.getDefault().unregister(this);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
