package cc.wulian.smarthomev5.fragment.setting.minigateway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.wulian.icam.wifidirect.utils.WiFiLinker;
import com.yuantuo.customview.ui.WLToast;

import android.R.integer;
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
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
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
import cc.wulian.smarthomev5.fragment.setting.gateway.AccountInformationSettingManagerFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class MiniGateWayRelayConnectWifiNor extends WulianFragment implements
		OnClickListener {
	private Button wifiSettingNext;
	private EditText wifiSettingPWD;
	private TextView wifisettingname;
	private LinearLayout layout;
	private TextView disturb_remind_TV;
	private LinearLayout relayname;
	private ListView wifilistview;
	private TextView remindText;
	private DeviceCache deviceCache;
	private WiFiLinker wifiLinker = new WiFiLinker();
	private MiniGateWayRelayConnectWifiNor fragment_wifi_nor;
	private MiniGateWayRelayConnectWifiWep fragment_wifi_wep;
	private MiniWifiRelayAdapter miniWifiRelayAdapter;
	private String gwID = AccountManager.getAccountManger().getmCurrentInfo()
			.getGwID();
	private ProgressDialogManager progressDialogManager = ProgressDialogManager
			.getDialogManager();
	List<WifiInfoEntity> entites = new ArrayList<WifiInfoEntity>();

	private AccountManager accountManager = AccountManager.getAccountManger();
	private GatewayInfo info = accountManager.getmCurrentInfo();
	private Boolean is_sxgateway = false;

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		if (info != null && info.getGwVer() != null) {
			String gwver = info.getGwVer();
			if (gwver.length() >= 3) {
				is_sxgateway = (gwver.substring(2, 4) + "").equals("10");
			}
		}
		return inflater.inflate(R.layout.mini_gateway_relay_connectwifi_nor,
				container, false);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		deviceCache = DeviceCache.getInstance(mActivity);
		wifisettingname = (TextView) view
				.findViewById(R.id.mini_relay_setting_wifi_name);
		wifiSettingNext = (Button) view
				.findViewById(R.id.mini_relay_setting_wifi_next);
		wifiSettingPWD = (EditText) view
				.findViewById(R.id.mini_relay_setting_wifi_pwd);
		disturb_remind_TV = (TextView) view
				.findViewById(R.id.sx_gateway_rellay_remind);
		layout = (LinearLayout) view
				.findViewById(R.id.mini_gateway_relay_connectwifi_nor);
		relayname = (LinearLayout) view
				.findViewById(R.id.mini_gateway_relay_name_ll);
		wifilistview = (ListView) view
				.findViewById(R.id.mini_gateway_rellay_lv);
		remindText = (TextView) view
				.findViewById(R.id.mini_input_password_remind);

		wifisettingname.setText(getArguments().getString("ssid"));
		wifisettingname.setOnClickListener(this);
		wifiSettingNext.setOnClickListener(this);

		if (getArguments().getString("encryption").equals("none")) {
			wifiSettingPWD.setVisibility(View.INVISIBLE);
		}
		//密码框
		wifiSettingPWD.setOnFocusChangeListener(new OnFocusChangeListener() {

			@Override
			public void onFocusChange(View arg0, boolean arg1) {
				wifiSettingPWD
						.setBackgroundResource(R.drawable.shape_round_all_white);
			    wifiSettingNext.setBackgroundResource(R.color.action_bar_bg);
			}
		});

		if (is_sxgateway) {
			String Channel_int = getArguments().getString("channel");
			int channel_int = Integer.parseInt(Channel_int);
			int zegbeeChannel = getArguments().getInt("zegbeechannel");
			Boolean istrue = (channel_int <= 5 && (zegbeeChannel == 24 ||zegbeeChannel == 25))||((channel_int>= 9&&channel_int<=13)&& zegbeeChannel == 11);
			
			int	deviceSize = deviceCache.size();
			if(deviceSize != 0){
				if (istrue) {
				} else {
					if(zegbeeChannel==24 || zegbeeChannel==25){
						disturb_remind_TV.setText(getResources().getString(R.string.gateway_router_setting_wifi_relay_high_channel_interference_error));
					}else if(zegbeeChannel == 11){
						disturb_remind_TV.setText(getResources().getString(R.string.gateway_router_setting_wifi_relay_low_channel_interference_error));
					}else{}
				}
			}else{
				
			}

			String wifiName =wifiLinker.getSxConnectedWifiSSID();
			Preference.getPreferences().saveNowtimeWifiName(wifiName);
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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		wifiLinker.WifiInit(getActivity());
		miniWifiRelayAdapter = new MiniWifiRelayAdapter(mActivity, null);
		// wifisettingname.setText(getArguments().getString("ssid"));
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		if (arg0.getId() == R.id.mini_relay_setting_wifi_name) {
			getFragmentManager().popBackStack();

		} else if (arg0.getId() == R.id.mini_relay_setting_wifi_next) {
			String pwd = wifiSettingPWD.getText().toString();
			if (TextUtils.isEmpty(pwd)) {
				if (getArguments().getString("encryption").equals("none")) {
					Intent intent = new Intent(mActivity,
							MiniGatewayRelayWifiConnectActivity.class);
					String EXTRA_1 = getArguments().getString("FLAG_1");
					intent.putExtra("FLAG_2", EXTRA_1);
					intent.putExtra("ssid", getArguments().getString("ssid"));
					intent.putExtra("pwd", wifiSettingPWD.getText().toString());
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
					wifiSettingPWD
							.setBackgroundResource(R.drawable.shape_round_all_red);
					wifiSettingNext.setBackgroundColor(getResources().getColor(R.color.gray));
					WLToast.showToastWithAnimation(getActivity(),
							getResources().getString(R.string.login_input_password_hint), Toast.LENGTH_SHORT);
					return;
				}
			} else {
				int password_num = pwd.trim().length();
				if (!(password_num >= 8 && password_num <= 64)) {
					remindText.setVisibility(View.VISIBLE);
					return;
				}
				Intent intent = new Intent(mActivity,
						MiniGatewayRelayWifiConnectActivity.class);
				String EXTRA_1 = getArguments().getString("FLAG_1");
				intent.putExtra("FLAG_2", EXTRA_1);
				intent.putExtra("ssid", getArguments().getString("ssid"));
				intent.putExtra("pwd", wifiSettingPWD.getText().toString());
				intent.putExtra("address", getArguments().getString("address"));
				intent.putExtra("encryption",
						getArguments().getString("encryption"));
				intent.putExtra("channel", getArguments().getString("channel"));
				intent.putExtra("apcli_encryption", "");
				startActivity(intent);
				mActivity.finish();
			}
		}
	}

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

}
