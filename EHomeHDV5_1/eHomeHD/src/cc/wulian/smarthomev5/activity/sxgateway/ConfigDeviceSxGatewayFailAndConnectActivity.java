package cc.wulian.smarthomev5.activity.sxgateway;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.minigateway.MiniGatewayRelaySettingActivity;

import com.wulian.icam.wifidirect.utils.WiFiLinker;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

@SuppressLint("DefaultLocale")
public class ConfigDeviceSxGatewayFailAndConnectActivity extends Activity
		implements OnClickListener {

	private Button btn_last, btn_no_wifi;
	private Context context = ConfigDeviceSxGatewayFailAndConnectActivity.this;
	private View tocDialog;
	public String WifiName, GateWayName, wifinameString;
	private WiFiLinker wifiLinker = new WiFiLinker();
	private TextView wifi_name_tv;
	protected WLDialog dialog;
	private ImageView bt_title_back;
	String add_deviceidString;
	String wifiSsid;
	private String wifiname;

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
		btn_last = (Button) findViewById(R.id.device_sx_connect_net_bt);
		wifi_name_tv = (TextView) findViewById(R.id.device_sx_gateway_textView);
		btn_no_wifi = (Button) findViewById(R.id.sx_please_connect_wifi);
		btn_no_wifi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_title_back = (ImageView) findViewById(R.id.titlebar_back);
	    wifiname = getIntent().getStringExtra("sxWifiName");
		wifi_name_tv.setText(wifiname);
		
	}

	private void initData() {
		if (bt_title_back != null) {
			bt_title_back.setOnClickListener(getLeftClick());
		}
	}

	private void setListener() {
		btn_last.setOnClickListener(this);
		btn_no_wifi.setOnClickListener(this);
	}

	protected void setViewContent() {
		setContentView(R.layout.device_sx_gateway_fail_and_connect);
	}
	protected OnClickListener getLeftClick() {
		return new OnClickListener() {
			public void onClick(View v) {
				ConfigDeviceSxGatewayFailAndConnectActivity.this.finish();
			}
		};
	}
	
	private void ShowConnectSxWifiIdea(boolean showPositiveButton,
			boolean showNegativeButton) {
		tocDialog = View.inflate(
				ConfigDeviceSxGatewayFailAndConnectActivity.this,
				R.layout.device_sx_geteway_no_wifi, null);
		WLDialog.Builder builder = new WLDialog.Builder(context);
		builder.setTitle(ConfigDeviceSxGatewayFailAndConnectActivity.this
				.getResources().getString(
						R.string.gateway_router_setting_wifi_relay_not_searth_hint));
		builder.setContentView(tocDialog);
		if (showPositiveButton) {
			builder.setPositiveButton(android.R.string.ok);
		}
		if (showNegativeButton) {

		}
		builder.setListener(new MessageListener() {
			@Override
			public void onClickPositive(View contentViewLayout) {
			}

			@Override
			public void onClickNegative(View contentViewLayout) {

			}
		});
		dialog = builder.create();
		dialog.show();
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		if (wifiLinker.getSxConnectedWifiSSID().equals(wifiname)) {
			Intent intent = new Intent(
					ConfigDeviceSxGatewayFailAndConnectActivity.this,
					MiniGatewayRelaySettingActivity.class);
			intent.putExtra("sxWifiName",wifiname);
			startActivity(intent);
			finish();
		}else{
			
		}
	}
	
	@Override
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.device_sx_connect_net_bt) {
			startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
		} else if (id == R.id.sx_please_connect_wifi) {
			ShowConnectSxWifiIdea(true, true);
		} else if (id == R.id.titlebar_back) {
			finish();
		}
	}

}
