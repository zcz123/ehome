package cc.wulian.smarthomev5.activity.sxgateway;

import org.json.JSONException;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.activity.minigateway.MiniGatewayRelaySettingActivity;
import cc.wulian.smarthomev5.event.MiniGatewayEvent;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class ConfigDeviceSxGatewayConnectNetAgainActivity extends EventBusActivity
		implements OnClickListener {

	private Button btn_last;
	public String GateWayName;
	private TextView wifi_name_tv;
	protected BaseActivity mActivity;
	private ImageView bt_title_back;
	private String gwID = AccountManager.getAccountManger().getmCurrentInfo()
			.getGwID();
	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setViewContent();
		initView();
		setListener();
		initData();
	}

	private void initView() {
		btn_last = (Button) findViewById(R.id.device_mini_connect_net_bt);
		wifi_name_tv = (TextView) findViewById(R.id.device_mini_gateway_textView);
		bt_title_back = (ImageView) findViewById(R.id.titlebar_back);
		NowConnectWifiname();
	}

	private void NowConnectWifiname() {
		GateWayName = getIntent().getStringExtra("sx_wifiName");
		wifi_name_tv.setText(GateWayName);
	}
	private void initData() {
		if (bt_title_back != null) {
			bt_title_back.setOnClickListener(getLeftClick());
		}
	}

	private void setListener() {
		btn_last.setOnClickListener(this);
	}

	protected void setViewContent() {
		setContentView(R.layout.device_sx_gateway_connect_net_again);
	}

	protected OnClickListener getLeftClick() {
		return new OnClickListener() {
			public void onClick(View v) {
				ConfigDeviceSxGatewayConnectNetAgainActivity.this.finish();
			}
		};
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if(NetSDK.isConnected(gwID)){
			NetSDK.sendMiniGatewayWifiSettingMsg(gwID, "1", "get", null);
		}else{
			
		}
	}

	@Override
	protected boolean finshSelf() {
		return false;
	}

	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.device_mini_connect_net_bt) {
			startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
		} else if (id == R.id.titlebar_back) {
			finish();
		}
	}
	
	//判断是否成功或失败
	public void onEventMainThread(MiniGatewayEvent gatewayevent){
		if (!CmdUtil.MINIGATEWAY_GET_JUDGE_RELAY_SIGN.equals(gatewayevent
				.getCmdindex())) {
			return;
		}
		org.json.JSONObject jsonObject;
		org.json.JSONArray jsonArray;
			try {
				jsonArray = new org.json.JSONArray(gatewayevent.getData());
				jsonObject = jsonArray.getJSONObject(0);
				String wifiFlag= jsonObject.getString("setRepeaterFlag");
				
				if(wifiFlag.equals("0")){
					Intent intent = new Intent();
					intent.putExtra("GateWayName", GateWayName);
					intent.setClass(getApplication(), SxGatewayRelayFailActivity.class);
					startActivity(intent);
					finish();
				}else if(wifiFlag.equals("1")){
					Intent intent = new Intent();
					intent.setClass(getApplication(), SxGatewayRelaySucceedActivity.class);
					startActivity(intent);
					finish();
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
	}
	
	

}
