package cc.wulian.smarthomev5.activity.sxgateway;

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


import com.wulian.icam.wifidirect.utils.WiFiLinker;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.activity.minigateway.MiniGatewayRelaySettingActivity;

public class ConfigDeviceSxGatewayConnectNetActivity extends EventBusActivity
		implements OnClickListener {

	private Button btn_last;
	public String WifiName, GateWayName;
	private WiFiLinker wifiLinker = new WiFiLinker();
	private TextView wifi_name_tv;
	protected BaseActivity mActivity;
	private ImageView bt_title_back;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		wifiLinker.WifiInit(this);
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
		setContentView(R.layout.device_sx_gateway_connect_net);
	}

	protected OnClickListener getLeftClick() {
		return new OnClickListener() {
			public void onClick(View v) {
				ConfigDeviceSxGatewayConnectNetActivity.this.finish();
			}
		};
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (wifiLinker.getSxConnectedWifiSSID().equals(GateWayName)) {
			Intent it = new Intent();
			it.putExtra("sxWifiName", GateWayName);
			it.setClass(ConfigDeviceSxGatewayConnectNetActivity.this,
					MiniGatewayRelaySettingActivity.class);
			startActivity(it);
			finish();
		} else {
			Intent it = new Intent();
			it.putExtra("sxWifiName", GateWayName);
			it.setClass(ConfigDeviceSxGatewayConnectNetActivity.this,
					ConfigDeviceSxGatewayFailAndConnectActivity.class);
			startActivity(it);
			finish();

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

}
