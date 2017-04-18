package cc.wulian.smarthomev5.fragment.setting.minigateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wulian.icam.wifidirect.utils.WiFiLinker;
import com.yuantuo.customview.ui.WLToast;

import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.minigateway.MiniGatewayRelayWifiConnectActivity;
import cc.wulian.smarthomev5.adapter.MiniWifiRelayAdapter;
import cc.wulian.smarthomev5.adapter.RouteRemindWifiAdapter.WifiInfoEntity;
import cc.wulian.smarthomev5.event.GatewaInfoEvent;
import cc.wulian.smarthomev5.event.MiniGatewayEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class MiniGateWayRelayConnectWifiWep extends WulianFragment implements
		OnClickListener {
	private EditText textPWD;
	private TextView textType;
	private TextView textname;
	private TextView textPwdType;
	private Button buttonNext;
	private PopupWindow popupwindow_type;
	private PopupWindow popupwindow_pwd;
	private LinearLayout layout;
	private TextView remindText;
	private LinearLayout relayname;
	private ListView wifilistview;
	private WiFiLinker wifiLinker = new WiFiLinker();
	private MiniGateWayRelayConnectWifiNor fragment_wifi_nor;
	private MiniGateWayRelayConnectWifiWep fragment_wifi_wep;
	private MiniWifiRelayAdapter miniWifiRelayAdapter;
	private MiniGatewayRelayWifiConnectActivity miniGatewayRelayWifiConnectActivity;
	private String gwID = AccountManager.getAccountManger().getmCurrentInfo()
			.getGwID();
	private ProgressDialogManager progressDialogManager = ProgressDialogManager
			.getDialogManager();
	List<WifiInfoEntity> entites = new ArrayList<WifiInfoEntity>();

	private AccountManager accountManager = AccountManager.getAccountManger();
	private DeviceCache deviceCache;
	private GatewayInfo info = accountManager.getmCurrentInfo();
	private Boolean is_sxgateway = false;
	private TextView disturb_remind_TV;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		wifiLinker.WifiInit(getActivity());
		miniWifiRelayAdapter = new MiniWifiRelayAdapter(mActivity, null);
		if (info != null && info.getGwVer() != null) {
			String gwver = info.getGwVer();
			if (gwver.length() >= 3) {
				is_sxgateway = (gwver.substring(2, 4) + "").equals("10");
			}
		}
		return inflater.inflate(R.layout.mini_gateway_relay_connectwifi_wep,
				container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		deviceCache = DeviceCache.getInstance(mActivity);
		textPWD = (EditText) view
				.findViewById(R.id.mini_relay_setting_wifi_wep_pwd);
		textType = (TextView) view
				.findViewById(R.id.mini_relay_setting_wifi_type);
		textname = (TextView) view
				.findViewById(R.id.mini_relay_setting_wifi_name);
		textPwdType = (TextView) view
				.findViewById(R.id.mini_relay_setting_wifi_pwd_type);
		buttonNext = (Button) view
				.findViewById(R.id.mini_relay_setting_wifi_wep_next);
		disturb_remind_TV = (TextView) view
				.findViewById(R.id.sx_gateway_rellay_remind_wep);
		relayname = (LinearLayout) view
				.findViewById(R.id.mini_gateway_relay_name_wep_ll);
		wifilistview = (ListView) view
				.findViewById(R.id.mini_gateway_rellay_wep_lv);
		remindText = (TextView) view.findViewById(R.id.mini_input_password_remind_wep);
		textname.setText(getArguments().getString("ssid"));
		layout = (LinearLayout) view
				.findViewById(R.id.mini_gateway_relay_connectwifi_wep);
		buttonNext.setOnClickListener(this);
		textType.setOnClickListener(this);
		textPwdType.setOnClickListener(this);
		
		if (getArguments().getString("encryption").equals("none")) {
			textPWD.setVisibility(View.INVISIBLE);
		}
		//密码框
		textPWD.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				textPWD.setBackgroundResource(R.drawable.shape_round_all_white);
				buttonNext.setBackgroundResource(R.color.action_bar_bg);
			}
		});
	
		if (is_sxgateway) {
			String Channel_int = getArguments().getString("channel");
			int channel_int = Integer.parseInt(Channel_int);
			int zegbeeChannel = getArguments().getInt("zegbeechannel");
			Boolean istrue = (channel_int <= 5 && (zegbeeChannel == 24 ||zegbeeChannel == 25))||((channel_int>= 9&&channel_int<=13)&& zegbeeChannel == 11);
			int	deviceSize = deviceCache.size();
			if(deviceSize != 0){
				if(istrue){
				}else{
					if(zegbeeChannel==24 || zegbeeChannel==25){
						disturb_remind_TV.setText(getResources().getString(R.string.gateway_router_setting_wifi_relay_high_channel_interference_error));
					}else if(zegbeeChannel == 11){
						disturb_remind_TV.setText(getResources().getString(R.string.gateway_router_setting_wifi_relay_low_channel_interference_error));
					}else{
					}
				}
			}else{
				
			}
			
			//存入当前连接的wifi名称
			String nowWifiName = wifiLinker.getSxConnectedWifiSSID();
			Preference.getPreferences().saveNowtimeWifiName(nowWifiName);
		}
		layout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				// TODO Auto-generated method stub
				return true;
			}
		});
		
		relayname.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				wifilistview.setVisibility(View.VISIBLE);
				wifilistview.setAdapter(miniWifiRelayAdapter);
				NetSDK.sendMiniGatewayWifiSettingMsg(gwID, "2", "get", null);
				progressDialogManager.showDialog("WIFIINFO", getActivity(),
						null, null);

				wifilistview.setOnItemClickListener(new OnItemClickListener() {

					@Override
					public void onItemClick(AdapterView<?> arg0, View arg1,
							int arg2, long arg3) {
						FragmentManager fm = getFragmentManager();
						FragmentTransaction transaction = fm.beginTransaction();

						Intent intent = getActivity().getIntent();
						String ss = intent.getStringExtra("FLAG_0");
						Bundle bundle = new Bundle();
						bundle.putString("FLAG_1", ss);
						bundle.putString("ssid", entites.get(arg2).getSsid());
						bundle.putString("address", entites.get(arg2)
								.getBSSID());
						bundle.putString("encryption", entites.get(arg2)
								.getCapabilities());
						bundle.putString("channel", entites.get(arg2)
								.getChanel());
						if (entites.get(arg2).getCapabilities().equals("WEP")) {
							fragment_wifi_wep = new MiniGateWayRelayConnectWifiWep();
							// bundle传入
							fragment_wifi_wep.setArguments(bundle);
							transaction.add(R.id.mini_gateway_relay_body,
									fragment_wifi_wep);
						} else {
							fragment_wifi_nor = new MiniGateWayRelayConnectWifiNor();
							fragment_wifi_nor.setArguments(bundle);
							transaction.add(R.id.mini_gateway_relay_body,
									fragment_wifi_nor);
						}

						transaction
								.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
						transaction.commit();

					}

				});
			}
		});

	}

	// public void onEventMainThread(GatewaInfoEvent gatewayinfoevent) {
	// zegbeeChannel = Integer.parseInt(gatewayinfoevent.getGwChannel());
	// }

	public void onEventMainThread(MiniGatewayEvent gatewayevent) {
		if (!CmdUtil.MINIGATEWAY_GET_RELAY_SEARCH.equals(gatewayevent
				.getCmdindex())) {
			return;
		}
		JSONObject jsonObject;
		try {
			JSONArray jsonArray = new JSONArray(gatewayevent.getData());
			jsonObject = jsonArray.getJSONObject(0);
			String wifiInfo = jsonObject.getString("cell");
			JSONArray wifiinfos = new JSONArray(wifiInfo);
			for (int i = 0; i < wifiinfos.length(); i++) {
				JSONObject object = wifiinfos.getJSONObject(i);
				WifiInfoEntity entity = new WifiInfoEntity();
				entity.setBSSID(object.getString("address"));
				entity.setCapabilities(object.getString("encryption"));
				entity.setChanel(object.getString("channel"));
				entity.setLevel(object.getString("signal"));
				entity.setSsid(object.getString("essid"));
				entites.add(entity);

			}
		} catch (JSONException e) {
			e.printStackTrace();
		}

		final List<WifiInfoEntity> newEntites = new ArrayList<WifiInfoEntity>();
		entites.removeAll(newEntites);
		Collections.sort(entites);
		newEntites.addAll(entites);
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				miniWifiRelayAdapter.swapData(newEntites);
			}
		});
		progressDialogManager.dimissDialog("WIFIINFO", 0);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.mini_relay_setting_wifi_type:
			if (popupwindow_type != null && popupwindow_type.isShowing()) {
				popupwindow_type.dismiss();
				return;
			} else {
				initmPopupWindowView_type();
				popupwindow_type.showAsDropDown(v, 0, 5);
			}
			break;
		case R.id.mini_relay_setting_wifi_pwd_type:
			if (popupwindow_pwd != null && popupwindow_pwd.isShowing()) {
				popupwindow_pwd.dismiss();
				return;
			} else {
				initmPopupWindowView_pwd();
				popupwindow_pwd.showAsDropDown(v, 0, 5);
			}

			break;
		case R.id.mini_relay_setting_wifi_wep_next:
			String pwd = textPWD.getText().toString();
			if (TextUtils.isEmpty(pwd)) {
				if (getArguments().getString("encryption").equals("none")) {
					textPWD.setVisibility(View.INVISIBLE);
					Intent intent = new Intent(mActivity,
							MiniGatewayRelayWifiConnectActivity.class);
					String EXTRA_1 = getArguments().getString("FLAG_1");
					intent.putExtra("FLAG_2", EXTRA_1);
					intent.putExtra("ssid", getArguments().getString("ssid"));
					intent.putExtra("pwd", textPWD.getText().toString());
					intent.putExtra("address",
							getArguments().getString("address"));
					intent.putExtra("encryption",
							getArguments().getString("encryption"));
					intent.putExtra("channel",
							getArguments().getString("channel"));
					intent.putExtra("apcli_encryption", "");
					startActivity(intent);
					mActivity.finish();
				} else {
					textPWD.setBackgroundResource(R.drawable.shape_round_all_red);
					buttonNext.setBackgroundColor(getResources().getColor(R.color.gray));
					WLToast.showToastWithAnimation(getActivity(),
							getResources().getString(R.string.login_input_password_hint), Toast.LENGTH_SHORT);
					return;
				}
			} else {
				int psdword_num = pwd.trim().length();
				if (!(psdword_num >= 8 && psdword_num <= 64)) {
					remindText.setVisibility(View.VISIBLE);
					return;
				}
				Intent intent = new Intent();
				String EXTRA_1 = getArguments().getString("FLAG_1");
				intent.setClass(mActivity,
						MiniGatewayRelayWifiConnectActivity.class);
				intent.putExtra("FLAG_2", EXTRA_1);
				intent.putExtra("ssid", getArguments().getString("ssid"));
				intent.putExtra("pwd", pwd);
				intent.putExtra("address", getArguments().getString("address"));
				intent.putExtra("encryption",
						getArguments().getString("encryption"));
				intent.putExtra("channel", getArguments().getString("channel"));
				intent.putExtra("apcli_encryption", textType.getText()
						.toString());
				intent.putExtra("apcli_index", textPwdType.getText().toString());
				MiniGateWayRelayConnectWifiWep.this.startActivity(intent);
				mActivity.finish();
			}

			break;
		default:
			break;
		}
	}

	public void initmPopupWindowView_type() {

		// // 获取自定义布局文件pop.xml的视图
		View customView = mActivity.getLayoutInflater().inflate(
				R.layout.popwindow_wep_wifi_type, null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		popupwindow_type = new PopupWindow(customView,
				textType.getMeasuredWidth(), LayoutParams.WRAP_CONTENT);
		/** 在这里可以实现自定义视图的功能 */
		final TextView textView1 = (TextView) customView
				.findViewById(R.id.wifi_type_nor);
		final TextView textView2 = (TextView) customView
				.findViewById(R.id.wifi_type_spe);

		textView1.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				textType.setText(textView1.getText().toString());
				popupwindow_type.dismiss();
			}
		});
		textView2.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				textType.setText(textView2.getText().toString());
				popupwindow_type.dismiss();
			}
		});

	}

	public void initmPopupWindowView_pwd() {

		// // 获取自定义布局文件pop.xml的视图
		View customView = mActivity.getLayoutInflater().inflate(
				R.layout.popwindow_wep_wifi_pwd, null, false);
		// 创建PopupWindow实例,200,150分别是宽度和高度
		popupwindow_pwd = new PopupWindow(customView,
				textPwdType.getMeasuredWidth(), LayoutParams.WRAP_CONTENT);

		/** 在这里可以实现自定义视图的功能 */
		final TextView textView_pwd_1234 = (TextView) customView
				.findViewById(R.id.wifi_type_pwd_1234);
		// final TextView textView_pwd_64 = (TextView) customView
		// .findViewById(R.id.wifi_type_pwd_64);
		// final TextView textView_pwd_128 = (TextView) customView
		// .findViewById(R.id.wifi_type_pwd_128);

		textView_pwd_1234.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				textPwdType.setText(textView_pwd_1234.getText().toString());
				popupwindow_pwd.dismiss();
			}
		});

	}
}
