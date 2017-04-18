package cc.wulian.smarthomev5.fragment.setting.minigateway;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.wulian.icam.wifidirect.utils.WiFiLinker;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.activity.minigateway.MiniGatewayChannelConflictHelpIdeaActivity;
import cc.wulian.smarthomev5.activity.minigateway.MiniGatewayRelaySettingActivity;
import cc.wulian.smarthomev5.activity.minigateway.RemindUserGatewayDisconnectDialog;
import cc.wulian.smarthomev5.activity.sxgateway.ConfigDeviceSxGatewayConnectNetActivity;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.callback.router.CmdindexTools;
import cc.wulian.smarthomev5.callback.router.KeyTools;
import cc.wulian.smarthomev5.callback.router.RouterDataCacheManager;
import cc.wulian.smarthomev5.callback.router.entity.GatewayCloseRouter;
import cc.wulian.smarthomev5.callback.router.entity.GatewayModeData;
import cc.wulian.smarthomev5.callback.router.entity.Get2_4GData;
import cc.wulian.smarthomev5.callback.router.entity.GetRadioEntity;
import cc.wulian.smarthomev5.callback.router.entity.GetWifi_ifaceEntity;
import cc.wulian.smarthomev5.event.RouterWifiSettingEvent;
import cc.wulian.smarthomev5.fragment.setting.router.RouterWifiSetting2_4GChannelActivity;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;

public class MiniRouterSettingActivity extends EventBusActivity {

	private static final String KEY_PROGRESS_DIALOG_GET_2_4G = "KEY_PROGRESS_DIALOG_GET_2_4G";
	private LayoutInflater inflater;
	private View contentView;
	private LinearLayout llRouter;
	private LinearLayout llname;
	private LinearLayout llpassword;
	private LinearLayout llchangeword;
	private LinearLayout llrelay;
	private LinearLayout llinformation;
	private ImageView rlclose_router;
	private ToggleButton tbswitch;
	private RelativeLayout close_router_rl;
	private TextView tv2_4gName;
	private TextView tv2_4gEntrypt;
	private String newName;

	private TextView wifiNote;

	private TextView tvwifiname;
	private TextView tvpsdway;
	private TextView tvchangepsd;
	private TextView tvwifirelay;
	private ImageView ivchangepsd;
	private ImageView ivwifirelay;

	private AccountManager accountManager = AccountManager.getAccountManger();
	private GatewayInfo info = accountManager.getmCurrentInfo();
	private WiFiLinker wifiLinker = new WiFiLinker() ;
	private Boolean is_sxgateway = false;
	private Boolean is_minigateway = false;
	private TextView tv_remind_word;
	private TextView sx_gateway_disturb;
	private ImageView sx_gateway_warn;
	private boolean pwdflag = false;
	private WLDialog dialogEntrypt;
	private int checked;
	private String modeNum = "5";
	private String routerOn = "5";
	private boolean wifiJudgeConnect;

	private ProgressDialogManager progressDialogManager = ProgressDialogManager
			.getDialogManager();
	RemindUserGatewayDisconnectDialog mdialog = new RemindUserGatewayDisconnectDialog(MiniRouterSettingActivity.this);

	// data
	private RouterDataCacheManager dataCacheManager = RouterDataCacheManager
			.getInstance();
	private String gwID = AccountManager.getAccountManger().getmCurrentInfo()
			.getGwID();
	// wifi加密方式对于数据
	private Map<String, String> entryptMaps = new HashMap<String, String>();
	private Map<String, String> keyMaps = new HashMap<String, String>();
	private Map<String, Integer> entryptKeys = new HashMap<String, Integer>();
	private List<String> listKeys = new ArrayList<String>();
	// 数据对象
	private GetWifi_ifaceEntity curifaceEntity = new GetWifi_ifaceEntity();
	private GetRadioEntity curgradio0 = new GetRadioEntity();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = LayoutInflater.from(this);
		contentView = inflater.inflate(
				R.layout.device_mini_router_setting_fragment, null);
		setContentView(contentView);
		wifiLinker.WifiInit(this);
		getGatewayModeAndSetRouterSwitch();
//		queryWifiData(); // 查询请求信息
		initbar();
		initMapsData(); // 初始化数据缓存， wif加密方式对于数据
		initCacheData();
		contentViewCreated();
		refresh2_4GDataAndView();
		if (is_sxgateway) {
			close_router_rl.setVisibility(View.INVISIBLE);
			llinformation.setVisibility(View.INVISIBLE);
		}
	}

	// 查询请求信息
	private void queryWifiData() {
		NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_4);
		progressDialogManager.showDialog(KEY_PROGRESS_DIALOG_GET_2_4G, this,
				null, null);
	}
	private void getGatewayModeAndSetRouterSwitch(){
		if (info != null && info.getGwVer() != null) {
			String gwver = info.getGwVer();
			if (gwver.length() >= 3) {
				try {
					is_minigateway = (gwver.charAt(2) + "").equals("8");
					is_sxgateway = (gwver.substring(2, 4) + "").equals("10");
					if(is_sxgateway){
						NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_15);
					}else if(is_minigateway){
						NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_7);
					}else{
					}
				} catch (Exception ex) {
					Toast.makeText(this.getBaseContext(), "网关版本：" + gwver,
							Toast.LENGTH_SHORT).show();
					ex.printStackTrace();
				}
			}
		}
	}

	private void initbar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.gateway_control_center));
		getCompatActionBar().setTitle(
				getResources().getString(R.string.gateway_router_setting));

	}

	// 初始化数据缓存
	private void initMapsData() {
		entryptMaps.put("none",
				this.getResources().getString(R.string.common_none));
		entryptMaps.put("psk", "WAP-PSK");
		entryptMaps.put("psk2", "WAP2-PSK");
		entryptMaps.put("psk-mixed", "WAP/WAP2-PSK");

		entryptKeys.put("none", 0);
		entryptKeys.put("psk", 1);
		entryptKeys.put("psk2", 2);
		entryptKeys.put("psk-mixed", 3);

		listKeys.add(this.getResources().getString(R.string.common_none));
		listKeys.add("WAP-PSK");
		listKeys.add("WAP2-PSK");
		listKeys.add("WAP/WAP2-PSK");

		keyMaps.put(this.getResources().getString(R.string.common_none), "none");
		keyMaps.put("WAP-PSK", "psk");
		keyMaps.put("WAP2-PSK", "psk2");
		keyMaps.put("WAP/WAP2-PSK", "psk-mixed");

	}

	// 初始化数据对象
	private void initCacheData() {
		// 2-4Gdata初始化赋值
		List<Get2_4GData> cache2_4gData = dataCacheManager.getGet2_4GLists();
		if (cache2_4gData.size() == 1) {
			if (cache2_4gData.get(0).getWifi_iface().size() == 1) {
				curifaceEntity = cache2_4gData.get(0).getWifi_iface().get(0);
			}
			if (cache2_4gData.get(0).getRadio0().size() == 1) {
				curgradio0 = cache2_4gData.get(0).getRadio0().get(0);
			}
		}

	}

	private void refresh2_4GDataAndView() {
		// 2.4gwifi
		tv2_4gName.setText(curifaceEntity.getSsid());
		tv2_4gEntrypt.setText(entryptMaps.get(curifaceEntity.getEncryption()));
	}

	private void contentViewCreated() {
		llRouter = (LinearLayout) contentView
				.findViewById(R.id.mini_router_setting_ll);
		llname = (LinearLayout) contentView
				.findViewById(R.id.setting_mini_device_router_name_ll);
		llpassword = (LinearLayout) contentView
				.findViewById(R.id.setting_mini_device_router_password_ll);
		llchangeword = (LinearLayout) contentView
				.findViewById(R.id.setting_mini_device_router_changeword_ll);
		llrelay = (LinearLayout) contentView
				.findViewById(R.id.setting_mini_device_router_relay_ll);
		llinformation = (LinearLayout) contentView
				.findViewById(R.id.setting_mini_device_router_information_ll);
		rlclose_router = (ImageView) findViewById(R.id.mini_gateway_know_help);
		tbswitch = (ToggleButton) findViewById(R.id.close_router_togbtn);
		close_router_rl = (RelativeLayout) findViewById(R.id.close_router_function_rl);

		tv2_4gName = (TextView) findViewById(R.id.setting_3);
		tv2_4gEntrypt = (TextView) findViewById(R.id.setting_4);
		tvwifiname = (TextView) findViewById(R.id.router_set_wifi_name);
		tvpsdway = (TextView) findViewById(R.id.router_set_encryption_way);
		tvchangepsd = (TextView) findViewById(R.id.router_set_change_password);
		tvwifirelay = (TextView) findViewById(R.id.router_set_wifi_relay);
		tvwifirelay = (TextView) findViewById(R.id.router_set_wifi_relay);
		tvwifirelay = (TextView) findViewById(R.id.router_set_wifi_relay);
		ivchangepsd = (ImageView) findViewById(R.id.router_set_change_psd_iv);
		ivwifirelay = (ImageView) findViewById(R.id.router_set_wifi_relay_iv);

		sx_gateway_disturb = (TextView) findViewById(R.id.sxgateway_router_disturb);
		sx_gateway_warn = (ImageView) findViewById(R.id.sxgateway_router_warn);

		wifiNote= (TextView) findViewById(R.id.tv_mini_show_wifi_connect);

		llname.setOnClickListener(clickListener);
		llpassword.setOnClickListener(clickListener);
		llchangeword.setOnClickListener(clickListener);
		llrelay.setOnClickListener(clickListener);
		llinformation.setOnClickListener(clickListener);
		rlclose_router.setOnClickListener(clickListener);
		tbswitch.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if (tbswitch.isChecked()) {
				String jsonDateRouter = "["+"{\"on\":" + "\"" + "1" + "\"" + "}"+"]";
				JSONArray jsonarrayRouter = JSONArray.parseArray(jsonDateRouter);
//				NetSDK.sendSetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_7, jsonarrayRouter);
				} else {
					String jsonDateRouter2 = "["+"{\"on\":" + "\"" + "0" + "\"" + "}"+"]";
					JSONArray jsonarrayRouter = JSONArray.parseArray(jsonDateRouter2);
//					NetSDK.sendSetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_7, jsonarrayRouter);
				}
			}
		});
		setWifiNoteText();
	}

	private OnClickListener clickListener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			ConnectivityManager connectivityManager= (ConnectivityManager)MiniRouterSettingActivity.this.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo  wifiNetInfo=connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			wifiJudgeConnect = wifiNetInfo.isConnected();
			if (arg0 == llname) {
				createModifyNameDialog();

			} else if (arg0 == llchangeword) {
				createModifyPwdDialog();

			} else if (arg0 == llpassword) {
				createModifyEncryptionDialog();

			} else if (arg0 == llinformation) {
				if (curifaceEntity==null){
					return;
				}
				String setchannel= curifaceEntity.getSet_channel();
				if (StringUtil.isNullOrEmpty(setchannel)){
					return;
				}
				if (curgradio0.getChannel() != null&&setchannel.equals("ok")) {
					Intent intent = new Intent();
					intent.setClass(MiniRouterSettingActivity.this,
							RouterWifiSetting2_4GChannelActivity.class);
					startActivity(intent);
				}else {
					WLToast.showToast(getApplicationContext(),getResources().getString(R.string.gateway_router_channel_no_set_hint),0);
				}

			} else if (arg0 == llrelay) {
				if (is_sxgateway) {
					System.out.println("modeNum------------->"+modeNum);
					if(modeNum.equals("1")){
						routerChangeToRelayModelDialog();
						return;
					}
					String lastWifiName = curifaceEntity.getSsid();
					String nowWifiName = wifiLinker.getSxConnectedWifiSSID();

					if(wifiJudgeConnect){
						Intent intent = new Intent();
						intent.putExtra("FLAG_0", "EXTRA_0");
						intent.putExtra("sxWifiName", nowWifiName);
						intent.setClass(MiniRouterSettingActivity.this,
								MiniGatewayRelaySettingActivity.class);
						startActivity(intent);
						finish();
					}else{
						Intent it = new Intent(MiniRouterSettingActivity.this,
								ConfigDeviceSxGatewayConnectNetActivity.class);
						it.putExtra("sx_wifiName",lastWifiName);
						startActivity(it);
					}
				} else {
					Intent intent = new Intent();
					intent.putExtra("FLAG_0", "EXTRA_0");
					intent.setClass(MiniRouterSettingActivity.this,
							MiniGatewayRelaySettingActivity.class);
					startActivity(intent);
				}
			} else if (arg0 == rlclose_router) {
				Intent intent = new Intent(MiniRouterSettingActivity.this,
						MiniGatewayChannelConflictHelpIdeaActivity.class);
				startActivity(intent);
				finish();
			}
		}
	};

	// 修改名称
	private void createModifyNameDialog() {
		final WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.gateway_router_setting_wifi_name));
		View mndView = inflater.inflate(
				R.layout.device_df_router_setting_wifi_name_dialog, null);
		tv_remind_word = (TextView) mndView
				.findViewById(R.id.router_setting_wifi_remind_word_tv);
		final EditText etRename = (EditText) mndView
				.findViewById(R.id.router_setting_wifi_rename_et);
		if (curifaceEntity.getSsid() != null) {
			etRename.setText(curifaceEntity.getSsid());
		}
		builder.setContentView(mndView)
				.setNegativeButton(getResources().getString(R.string.cancel))
				.setPositiveButton(getResources().getString(R.string.common_ok))
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						 newName = etRename.getText().toString().trim();
						boolean isRight = !StringUtil.isNullOrEmpty(newName)
								&& newName.length() <= 18
								&& newName.length() > 0;
						if (isRight) {
							tv2_4gName.setText(newName);
							sendModifyNameData(newName);
							builder.setDismissAfterDone(true);
							mdialog.remindUserGatewayRestart();
						} else {
							tv_remind_word.setVisibility(View.VISIBLE);
							builder.setDismissAfterDone(false);
						}
					}

					@Override
					public void onClickNegative(View contentViewLayout) {
					}
				});
		WLDialog dlg = builder.create();
		dlg.show();
	}

	private void setWifiNoteText(){
		String wifiName=getIntent().getStringExtra("miniwifiname");
		if (StringUtil.isNullOrEmpty(wifiName)){
			wifiNote.setVisibility(View.INVISIBLE);
		}else {
			String note="当前已连接：<font color=\"#222222\"> "+wifiName+ " </font>\n点击下方WiFi配置可切换WiFi";
			wifiNote.setText(Html.fromHtml(note));
		}
	}
	// 竖形网关中路由模式切换到中继模式提示dialog
	private void routerChangeToRelayModelDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(
				getResources().getString(
						R.string.gateway_router_setting_dialog_toast))
				.setMessage(getResources().getString(R.string.gateway_set_router_configure_relay_hint))
				.setPositiveButton(getResources().getString(R.string.common_ok))
				.setNegativeButton(getResources().getString(R.string.cancel))
				.setListener(new MessageListener() {
					@Override
					public void onClickPositive(View contentViewLayout) {
						String lastWifiName = curifaceEntity.getSsid();
						String nowWifiName =wifiLinker.getSxConnectedWifiSSID();
						if (lastWifiName.equals(nowWifiName)) {
							Intent intent = new Intent();
							intent.putExtra("FLAG_0", "EXTRA_0");
							intent.putExtra("sxWifiName", nowWifiName);
							intent.setClass(MiniRouterSettingActivity.this,
									MiniGatewayRelaySettingActivity.class);
							startActivity(intent);
						} else {
							Intent it = new Intent(MiniRouterSettingActivity.this,
									ConfigDeviceSxGatewayConnectNetActivity.class);
							it.putExtra("sx_wifiName", newName);
							startActivity(it);
						}
					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		WLDialog dlg = builder.create();
		dlg.show();

	}

	// 发送修改2.4gwifi名称数据
	private void sendModifyNameData(String newName) {

		sendSetWifiData(CmdindexTools.CMDINDEX_4, curifaceEntity.getKey(),
				curifaceEntity.getMode(), newName,
				curifaceEntity.getEncryption(), curgradio0.getDisabled(),
				curgradio0.getChannel());

	}

	// 发送修改2.4gwifi密码数据
	private void sendModify2_4gPwdData(String newPwd) {
		if (curifaceEntity.getSsid() == null
				|| curifaceEntity.getEncryption() == null
				|| curgradio0.getChannel() == null) {
			return;
		}
		sendSetWifiData(CmdindexTools.CMDINDEX_4, newPwd,
				curifaceEntity.getMode(), curifaceEntity.getSsid(),
				curifaceEntity.getEncryption(), curgradio0.getDisabled(),
				curgradio0.getChannel());
	}

	// 发送修改2.4gwifi加密方式数据
	private void sendEntryptData(int pos) {
		String newEntrypt = keyMaps.get(listKeys.get(pos));

		String key = curifaceEntity.getKey();
		// 由加密方式转为不加密
		if (pos == 0 && !curifaceEntity.getEncryption().equals("none")) {
			key = "";
			// 由不加密转为加密方式
		} else if (pos != 0 && curifaceEntity.getEncryption().equals("none")) {
			createdChangeEntryptToastDialog();
			key = "12345678";
		}
		//目的是刷新刚点击的加密方式并显示
		NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_4);

		sendSetWifiData(CmdindexTools.CMDINDEX_4, key,
				curifaceEntity.getMode(), curifaceEntity.getSsid(), newEntrypt,
				curgradio0.getDisabled(), curgradio0.getChannel());

	}

	// 发送设置wifi信息数据
	private void sendSetWifiData(String cmdIndex, String key, String mode,
			String ssid, String encryption, String disabled, String channel) {
		JSONArray dataJsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(KeyTools.key, key);
			jsonObject.put(KeyTools.mode, mode);
			jsonObject.put(KeyTools.ssid, ssid);
			jsonObject.put(KeyTools.encryption, encryption);
			jsonObject.put(KeyTools.disabled, disabled);
			jsonObject.put(KeyTools.channel, channel);
			dataJsonArray.add(0, jsonObject);
			NetSDK.sendSetRouterConfigMsg(gwID, cmdIndex, dataJsonArray);

		} catch (JSONException e) {
			e.printStackTrace();
		}

	}

	// 修改2.4G密码
	private void createModifyPwdDialog() {
		if (entryptMaps.get("none").equals(
				entryptMaps.get(curifaceEntity.getEncryption()))) {
			selecEncryptionHintDialog();
			return;
		}

		final WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.gateway_router_setting_wifi_modify_pwd_title));
		final View mpdView = inflater.inflate(
				R.layout.device_router_setting_wifi_pwd_dialog, null);
		final EditText etPwd = (EditText) mpdView
				.findViewById(R.id.router_setting_pwd_new_name);
		final EditText etPwd_true = (EditText) mpdView
				.findViewById(R.id.router_setting_pwd_new_name_true);

		final TextView sx_pwd_num_remind = (TextView) mpdView
				.findViewById(R.id.password_remind_tv);
		final TextView sx_pwd_is_error = (TextView) mpdView
				.findViewById(R.id.password_is_error_tv);
		final ImageView ivPwd = (ImageView) mpdView
				.findViewById(R.id.router_setting_pwd_visible);
		final ImageView ivPwd_true = (ImageView) mpdView
				.findViewById(R.id.router_setting_pwd_visible_true);

		ivPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (pwdflag) {
					pwdflag = false;
					etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					ivPwd.setImageDrawable(getResources().getDrawable(
							R.drawable.dm_router_setting_wifi_pwd_visibale));
				} else {
					pwdflag = true;
					etPwd.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					ivPwd.setImageDrawable(getResources().getDrawable(
							R.drawable.dm_router_setting_wifi_pwd_invisibale));
				}
			}
		});

		ivPwd_true.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (pwdflag) {
					pwdflag = false;
					etPwd_true
							.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					ivPwd_true.setImageDrawable(getResources().getDrawable(
							R.drawable.dm_router_setting_wifi_pwd_visibale));
				} else {
					pwdflag = true;
					etPwd_true.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					ivPwd_true.setImageDrawable(getResources().getDrawable(
							R.drawable.dm_router_setting_wifi_pwd_invisibale));
				}
			}
		});

		etPwd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				sx_pwd_num_remind.setVisibility(View.INVISIBLE);
				etPwd.setBackgroundResource(R.drawable.account_sigin_records_background);
				etPwd_true
						.setBackgroundResource(R.drawable.account_sigin_records_background);
				etPwd_true.setFocusable(true);
				etPwd_true.setFocusableInTouchMode(true);
			}
		});

		etPwd_true.setOnFocusChangeListener(new View.OnFocusChangeListener() {
			@Override
			public void onFocusChange(View view, boolean isFocus) {
				int numpwd = etPwd.getText().toString().length();
				sx_pwd_num_remind.setVisibility(View.INVISIBLE);
				sx_pwd_is_error.setVisibility(View.INVISIBLE);
				etPwd_true
						.setBackgroundResource(R.drawable.account_sigin_records_background);
				boolean istrue = numpwd > 7 && numpwd < 64;
				if (!istrue) {
					etPwd.setBackgroundResource(R.drawable.gateway_setting_router_change_password);
					sx_pwd_num_remind.setVisibility(View.VISIBLE);
					etPwd_true.setFocusable(false);
					etPwd_true.setFocusableInTouchMode(false);
				} else if (istrue) {
					etPwd.setBackgroundResource(R.drawable.account_sigin_records_background);
					sx_pwd_num_remind.setVisibility(View.INVISIBLE);
					etPwd_true.setFocusable(true);
					etPwd_true.setFocusableInTouchMode(true);
				}
			}
		});
		etPwd_true.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				sx_pwd_num_remind.setVisibility(View.INVISIBLE);
				sx_pwd_is_error.setVisibility(View.INVISIBLE);
				etPwd_true
						.setBackgroundResource(R.drawable.account_sigin_records_background);
			}
		});

		builder.setContentView(mpdView)
				.setNegativeButton(getResources().getString(R.string.cancel))
				.setPositiveButton(getResources().getString(R.string.common_ok))
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {
					@Override
					public void onClickPositive(View contentViewLayout) {

						final String newPwd = etPwd.getText().toString().trim();
						final String newPwd_true = etPwd_true.getText()
								.toString().trim();
						int newpwd = etPwd.getText().toString().length();
						int newpwd_true = etPwd_true.getText().toString()
								.length();

						if (!(newpwd > 7 && newpwd < 21)) {
							etPwd.setBackgroundResource(R.drawable.gateway_setting_router_change_password);
						}
						if (!(newpwd_true > 7 && newpwd_true < 21)) {
							etPwd_true
									.setBackgroundResource(R.drawable.gateway_setting_router_change_password);
						}
						if (!(newPwd.equals(newPwd_true))) {
							sx_pwd_is_error.setVisibility(View.VISIBLE);
							builder.setDismissAfterDone(false);
						} else if (newpwd == 0 && newpwd_true == 0) {
							sx_pwd_num_remind.setVisibility(View.VISIBLE);
							etPwd.setBackgroundResource(R.drawable.gateway_setting_router_change_password);
							etPwd_true
									.setBackgroundResource(R.drawable.gateway_setting_router_change_password);
							builder.setDismissAfterDone(false);
						} else {
							sendModify2_4gPwdData(newPwd);
							WLToast.showToast(
									MiniRouterSettingActivity.this,
									getResources()
											.getString(
													R.string.set_account_manager_modify_gw_password_success),
									WLToast.TOAST_SHORT);
							mdialog.remindUserGatewayRestart();
							builder.setDismissAfterDone(true);
						}
					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		WLDialog dlg = builder.create();
		dlg.show();
	}

	// 修改加密方式
	private void createModifyEncryptionDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.gateway_router_setting_wifi_entrypt_choose));
		final View medView = inflater.inflate(
				R.layout.device_df_router_setting_wifi_encrypt_dialog, null);
		ListView lvEncrypt = (ListView) medView
				.findViewById(R.id.router_setting_wifi_encrypt_lv);

		wifiEncryptionAdapter mAdapter = new wifiEncryptionAdapter(this,
				listKeys);
		lvEncrypt.setAdapter(mAdapter);
		if (curifaceEntity.getEncryption() != null) {
			checked = entryptKeys.get(curifaceEntity.getEncryption());
		}
		lvEncrypt.setSelection(checked);

		builder.setContentView(medView)
				.setNegativeButton(getResources().getString(R.string.cancel))
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {

					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		dialogEntrypt = builder.create();
		dialogEntrypt.show();

	}

	/*
	 * 弹出设置加密方式提示框 entryptMaps.get(cur2_4gifaceEntity.getEncryption())
	 */
	private void selecEncryptionHintDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.gateway_router_setting_dialog_toast));
		TextView textView = new TextView(getBaseContext());
		textView.setTextColor(getResources().getColor(R.color.black));
		textView.setTextSize(16);
		textView.setHeight(360);
		textView.setGravity(Gravity.CENTER);
		textView.setText(getResources().getString(
				R.string.gateway_router_no_encryption_mode));
		builder.setContentView(textView).setNegativeButton(R.string.common_ok)
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {

					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		dialogEntrypt = builder.create();
		dialogEntrypt.show();

	}

	// 从未加密到加密之后弹出提示框
	private void createdChangeEntryptToastDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.gateway_router_setting_dialog_toast));
		View cetView = inflater.inflate(
				R.layout.device_df_router_setting_wifi_encrypt_toast_dialog,
				null);

		builder.setContentView(cetView)
				.setPositiveButton(getResources().getString(R.string.common_ok))
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		WLDialog dlg = builder.create();
		dlg.show();
	}

	private void showCloseRouterDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.gateway_router_setting_dialog_toast));
		View closeRouter = View.inflate(this,
				R.layout.device_mini_geteway_close_router_remind, null);
		builder.setContentView(closeRouter);
		builder.setPositiveButton(getResources().getString(R.string.common_ok));
		builder.setNegativeButton(getResources().getString(R.string.cancel));
		builder.setListener(new MessageListener() {
			@Override
			public void onClickPositive(View contentViewLayout) {
				progressDialogManager.showDialog("11", getApplicationContext(), null, null);
				setRouterInformationChangeStatusPositive();
				progressDialogManager.dimissDialog("11", 0);
			}

			@Override
			public void onClickNegative(View contentViewLayout) {

			}
		});
		WLDialog dialog = builder.create();
		dialog.show();
	}

	private void setRouterInformationChangeStatusPositive() {
		tv2_4gName.setTextColor(getResources().getColor(R.color.whitegray));
		tv2_4gEntrypt.setTextColor(getResources().getColor(R.color.whitegray));
		tvwifiname.setTextColor(getResources().getColor(R.color.whitegray));
		tvpsdway.setTextColor(getResources().getColor(R.color.whitegray));
		tvchangepsd.setTextColor(getResources().getColor(R.color.whitegray));
		tvwifirelay.setTextColor(getResources().getColor(R.color.whitegray));
		ivchangepsd.setVisibility(View.INVISIBLE);
		ivwifirelay.setVisibility(View.INVISIBLE);
		llpassword.setOnClickListener(null);
		llname.setOnClickListener(null);
		llchangeword.setOnClickListener(null);
		llrelay.setOnClickListener(null);
	}

	private void setRouterInformationChangeStatusNegative() {
		tv2_4gName.setTextColor(getResources().getColor(R.color.v5_green_dark));
		tv2_4gEntrypt.setTextColor(getResources().getColor(
				R.color.v5_green_dark));
		tvwifiname.setTextColor(getResources().getColor(R.color.black));
		tvpsdway.setTextColor(getResources().getColor(R.color.black));
		tvchangepsd.setTextColor(getResources().getColor(R.color.black));
		tvwifirelay.setTextColor(getResources().getColor(R.color.black));
		ivchangepsd.setVisibility(View.VISIBLE);
		ivwifirelay.setVisibility(View.VISIBLE);
		llpassword.setOnClickListener(clickListener);
		llname.setOnClickListener(clickListener);
		llchangeword.setOnClickListener(clickListener);
		llrelay.setOnClickListener(clickListener);
	}

	private class wifiEncryptionAdapter extends WLBaseAdapter<String> {

		public wifiEncryptionAdapter(Context context, List<String> data) {
			super(context, data);
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(
					R.layout.device_df_router_setting_wifi_encrypt_item, null);
		}

		@Override
		protected void bindView(Context context, final View view,
				final int pos, String item) {
			super.bindView(context, view, pos, item);

			TextView tvName = (TextView) view
					.findViewById(R.id.router_setting_wifi_encrypt_tv);
			final ImageView ivChecked = (ImageView) view
					.findViewById(R.id.router_setting_wifi_encrypt_iv);
			tvName.setText(item);

			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					checked = pos;
					if (dialogEntrypt.isShowing()) {
						dialogEntrypt.dismiss();
					}
					notifyDataSetChanged();
					sendEntryptData(pos);
					mdialog.remindUserGatewayRestart();
				}

			});
			// 刷新标记位,实现类似RadioButton功能
			if (checked == pos) {
				ivChecked.setVisibility(View.VISIBLE);
				view.setSelected(true);
			} else {
				ivChecked.setVisibility(View.GONE);
				view.setSelected(false);
			}
		}

	}

	public void onEventMainThread(RouterWifiSettingEvent event) {
		if (RouterWifiSettingEvent.ACTION_REFRESH.equals(event.getAction())) {
			if (RouterWifiSettingEvent.TYPE_2_4G_WIFI.equals(event.getType())) {
				curifaceEntity = event.getWifi_ifaceList().get(0);
				curgradio0 = event.getRadioList().get(0);
				if (is_sxgateway) {
					int i = Integer.parseInt(curgradio0.getChannel());
					if (i < 6) {
					} else {
						sx_gateway_disturb.setVisibility(View.VISIBLE);
						sx_gateway_warn.setVisibility(View.VISIBLE);
					}
				}
				refresh2_4GDataAndView();
				progressDialogManager.dimissDialog(
						KEY_PROGRESS_DIALOG_GET_2_4G, 0);
			}
		}
	}
	
	public void onEventMainThread(GatewayModeData modedata){
		modeNum =modedata.getMsg();
	}
	
	public void onEventMainThread(GatewayCloseRouter modedata){
		routerOn = modedata.getMsg();
		if(routerOn.equals("1")){
			tbswitch.setChecked(true);
			setRouterInformationChangeStatusNegative();
		}else if(routerOn.equals("0")){
			tbswitch.setChecked(false);
//			showCloseRouterDialog();
		}else{
			
		}
	}
}
