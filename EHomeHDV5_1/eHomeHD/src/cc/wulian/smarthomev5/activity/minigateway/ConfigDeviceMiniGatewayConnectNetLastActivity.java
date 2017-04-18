package cc.wulian.smarthomev5.activity.minigateway;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.utils.WifiUtil;

@SuppressLint("DefaultLocale")
public class ConfigDeviceMiniGatewayConnectNetLastActivity extends Activity
		implements OnClickListener {

	private Button btn_last, btn_no_wifi;
	private Context context = ConfigDeviceMiniGatewayConnectNetLastActivity.this;
	private View tocDialog;
	public String WifiName;
	public String GateWayName;
	public String wifinameString;
	private TextView wifi_name_tv;
	protected WLDialog dialog;
	protected BaseActivity mActivity;
	private ImageView bt_title_back;

	private WifiUtil wifiUtil = null;

	@Override
	protected void onCreate(Bundle arg0) {
		super.onCreate(arg0);
		setViewContent();
		NowConnectWifiname();
		initView();
		setListener();
		initData();
	}

	private void initView() {
		btn_last = (Button) findViewById(R.id.device_mini_connect_net_bt);
		wifi_name_tv = (TextView) findViewById(R.id.device_mini_gateway_textView);
		wifi_name_tv.setText(wifinameString);
		btn_no_wifi = (Button) findViewById(R.id.mini_please_connect_wifi);
		btn_no_wifi.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		bt_title_back = (ImageView) findViewById(R.id.titlebar_back);
	}

	private void NowConnectWifiname() {
		GateWayName = getIntent().getStringExtra("Deviceid");
		WifiName = GateWayName.substring(12).toUpperCase();
		wifinameString = "Mini_" + WifiName;
	}

	private void initData() {
		wifiUtil = new WifiUtil();
	}

	private void setListener() {
		btn_last.setOnClickListener(this);
		btn_no_wifi.setOnClickListener(this);
		if (bt_title_back != null) {
			bt_title_back.setOnClickListener(getLeftClick());
		}
	}

	protected void setViewContent() {
		setContentView(R.layout.device_mini_connect_net_last);
	}

	protected String getActivityTitle() {
		return getResources().getString(R.string.setting_wifi_setting_connect);
	}

	protected OnClickListener getLeftClick() {
		return new OnClickListener() {
			public void onClick(View v) {
				ConfigDeviceMiniGatewayConnectNetLastActivity.this.finish();
			}
		};
	}

	private void ShowConnectWifiIdea(boolean showPositiveButton,
			boolean showNegativeButton) {
		tocDialog = View.inflate(
				ConfigDeviceMiniGatewayConnectNetLastActivity.this,
				R.layout.device_mini_geteway_no_wifi, null);
		WLDialog.Builder builder = new WLDialog.Builder(context);
		builder.setTitle(ConfigDeviceMiniGatewayConnectNetLastActivity.this
				.getResources().getString(
						R.string.device_mini_geteway_no_wifi_prompt));

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
	public void onClick(View v) {
		int id = v.getId();
		if (id == R.id.device_mini_connect_net_bt) {
			startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
		} else if (id == R.id.mini_please_connect_wifi) {
			ShowConnectWifiIdea(true, true);

		} else if (id == R.id.titlebar_back) {
			finish();
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (wifiUtil.getSSID().contains(wifinameString)){
			startActivity();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		//更改跳转方式
		if (wifiUtil != null){
			if (wifiUtil.getSSID().contains(wifinameString)){
				startActivity();
			}else {
				WLToast.showToast(this,getResources().getString(R.string.miniGW_please_connectmini),0);
			}
		}
	}
	private void startActivity(){
		Intent intent = new Intent(
				ConfigDeviceMiniGatewayConnectNetLastActivity.this,
				MiniGatewayRelaySettingActivity.class);
		intent.putExtra("extra", 100);
		Bundle bundlekey=new Bundle();
		bundlekey.putString("Wifiname_key", wifinameString);
		intent.putExtra("Wifiname_key", bundlekey);
		startActivity(intent);
		finish();
	}
}
