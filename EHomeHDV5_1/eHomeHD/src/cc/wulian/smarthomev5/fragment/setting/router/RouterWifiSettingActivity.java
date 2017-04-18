package cc.wulian.smarthomev5.fragment.setting.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
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
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ToggleButton;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.callback.router.CmdindexTools;
import cc.wulian.smarthomev5.callback.router.KeyTools;
import cc.wulian.smarthomev5.callback.router.RouterDataCacheManager;
import cc.wulian.smarthomev5.callback.router.entity.Get2_4GData;
import cc.wulian.smarthomev5.callback.router.entity.Get5GData;
import cc.wulian.smarthomev5.callback.router.entity.GetRadioEntity;
import cc.wulian.smarthomev5.callback.router.entity.GetWifi_ifaceEntity;
import cc.wulian.smarthomev5.callback.router.entity.SpeedData;
import cc.wulian.smarthomev5.callback.router.entity.SpeedStatusEntity;
import cc.wulian.smarthomev5.event.RouterWifiSettingEvent;
import cc.wulian.smarthomev5.event.RouterWifiSpeedEvent;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

public class RouterWifiSettingActivity extends EventBusActivity {
	private static final String KEY_PROGRESS_DIALOG_GET_2_4G = "KEY_PROGRESS_DIALOG_GET_2_4G";
	private static final String KEY_PROGRESS_DIALOG_GET_2_4G_PWD = "KEY_PROGRESS_DIALOG_GET_2_4G_PWD";
	private static final String KEY_PROGRESS_DIALOG_GET_5G = "KEY_PROGRESS_DIALOG_GET_5G";
	private static final String KEY_PROGRESS_DIALOG_GET_5G_PWD = "KEY_PROGRESS_DIALOG_GET_5G_PWD";
	private static final String KEY_PROGRESS_DIALOG_QOS_STATUS = "KEY_PROGRESS_DIALOG_QOS_STATUS";

	private LayoutInflater inflater;
	private View contentView;
	private ToggleButton tb2_4gSwitch;
	private LinearLayout ll2_4g;
	private RelativeLayout rl2_4gName;
	private RelativeLayout rl2_4gPwd;
	private RelativeLayout rl2_4gEncryption;
	private RelativeLayout rl2_4gChannel;
	private TextView tv2_4gName;
	private TextView tv2_4gEntrypt;

	private ToggleButton tb5gSwitch;
	private LinearLayout ll5g;
	private RelativeLayout rl5gName;
	private RelativeLayout rl5gPwd;
	private RelativeLayout rl5gEncryption;
	private RelativeLayout rl5gChannel;
	private TextView tv5gName;
	private TextView tv5gEntrypt;

	// qos开关
	private ToggleButton tbQosSwitch;
	private LinearLayout llAutoSwitch;
	private ToggleButton tbAutoSwitch;

	// status开关
	private boolean status2_4gSwitch = false;
	private boolean status5gSwitch = false;
	private boolean statusQosSwitch = false;
	private boolean statusAutoSwitch = false;
	// 联网设置
	private RelativeLayout rlConnectedSetting;

	private boolean pwd2_4gflag = false;
	private boolean pwd2_4gflag_true = false;
	private boolean pwd5gflag = false;
	private boolean pwd5gflag_true = false;
	private WLDialog dialog2_4gEntrypt;
	private WLDialog dialog5gEntrypt;
	private int checked2_4g;
	private int checked5g;

	private ProgressDialogManager progressDialogManager = ProgressDialogManager
			.getDialogManager();
	// data
	private RouterDataCacheManager dataCacheManager = RouterDataCacheManager
			.getInstance();
	private String gwID = AccountManager.getAccountManger().getmCurrentInfo()
			.getGwID();
	private final String ConstantStr_0 = "0";
	private final String ConstantStr_1 = "1";
	// wifi加密方式对于数据
	private Map<String, String> entryptMaps = new HashMap<String, String>();
	private Map<String, String> keyMaps = new HashMap<String, String>();
	private Map<String, Integer> entryptKeys = new HashMap<String, Integer>();
	private List<String> listKeys = new ArrayList<String>();
	// 2_4g数据对象
	private GetWifi_ifaceEntity cur2_4gifaceEntity = new GetWifi_ifaceEntity();
	private GetRadioEntity cur2_4gradio0 = new GetRadioEntity();
	// 限速数据对象
	private SpeedStatusEntity statusEntity = new SpeedStatusEntity();
	// 5g数据对象
	private GetWifi_ifaceEntity cur5gifaceEntity = new GetWifi_ifaceEntity();
	private GetRadioEntity cur5gradio1 = new GetRadioEntity();
	private final Handler mHandler = new Handler(Looper.getMainLooper());

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		inflater = LayoutInflater.from(this);
		contentView = inflater.inflate(
				R.layout.device_df_router_setting_wifi_setting, null);
		setContentView(contentView);
		queryWifiData();
		initbar();
		initMapsData();
		initCacheData();
		contentViewCreated();
		refresh2_4GDataAndView();
		refresh5GDataAndView();
		refreshQOSSwitchStatus();
	}

	// 查询请求信息
	private void queryWifiData() {
		NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_3);
		NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_4);
		NetSDK.sendGetRouterConfigMsg(gwID, CmdindexTools.CMDINDEX_5);
		progressDialogManager.showDialog(KEY_PROGRESS_DIALOG_GET_2_4G, this,
				null, null);
		progressDialogManager.showDialog(KEY_PROGRESS_DIALOG_GET_5G, this,
				null, null);
		progressDialogManager.showDialog(KEY_PROGRESS_DIALOG_QOS_STATUS, this,
				null, null);
	}

	// 初始化数据缓存
	private void initMapsData() {
		entryptMaps.put("none", this.getResources().getString(R.string.common_none));
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
				cur2_4gifaceEntity = cache2_4gData.get(0).getWifi_iface()
						.get(0);
			}
			if (cache2_4gData.get(0).getRadio0().size() == 1) {
				cur2_4gradio0 = cache2_4gData.get(0).getRadio0().get(0);
			}
		}
		// 5Gdata初始化赋值
		List<Get5GData> cache5gData = dataCacheManager.getGet5GLists();
		if (cache5gData.size() == 1) {
			if (cache5gData.get(0).getWifi_iface().size() == 1) {
				cur5gifaceEntity = cache5gData.get(0).getWifi_iface().get(0);

			}
			if (cache5gData.get(0).getRadio1().size() == 1) {
				cur5gradio1 = cache5gData.get(0).getRadio1().get(0);
			}
		}
		// 速度初始化数据
		List<SpeedData> cacheSpeedData = dataCacheManager.getSpeedLists();
		if (cacheSpeedData.size() == 1) {
			statusEntity = cacheSpeedData.get(0).getStatus();
		}

	}

	private void initbar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.gateway_router_setting));
		getCompatActionBar().setTitle(
				getResources().getString(R.string.set_titel));

	}

	private void refresh2_4GDataAndView() {
		// 2.4gwifi
		if (cur2_4gradio0.getDisabled() != null) {
			if (ConstantStr_1.equals(cur2_4gradio0.getDisabled())) {
				status2_4gSwitch = false;
			} else if (ConstantStr_0.equals(cur2_4gradio0.getDisabled())) {
				status2_4gSwitch = true;
			}
		}
		tb2_4gSwitch.setChecked(status2_4gSwitch);
		if (status2_4gSwitch) {
			ll2_4g.setVisibility(View.VISIBLE);
		} else {
			ll2_4g.setVisibility(View.GONE);
		}
		tv2_4gName.setText(cur2_4gifaceEntity.getSsid());
		tv2_4gEntrypt.setText(entryptMaps.get(cur2_4gifaceEntity
				.getEncryption()));
	}

	// 刷新qos和只能限速开关状态
	private void refreshQOSSwitchStatus() {
		// Qos开关
		if (statusEntity.getOn() == 0) {
			statusQosSwitch = false;
		} else if (statusEntity.getOn() == 1) {
			statusQosSwitch = true;
		}
		tbQosSwitch.setChecked(statusQosSwitch);
		if (statusQosSwitch) {
			llAutoSwitch.setVisibility(View.VISIBLE);
		} else {
			llAutoSwitch.setVisibility(View.GONE);
		}

		// 智能限速
		if (statusEntity.getMode() == 0) {
			statusAutoSwitch = true;
		} else if (statusEntity.getMode() == 1) {
			statusAutoSwitch = false;
		}
		tbAutoSwitch.setChecked(statusAutoSwitch);
	}

	// 刷新5Gwifi数据和显示
	private void refresh5GDataAndView() {

		// 5Gwifi
		if (cur5gradio1 != null) {
			if (ConstantStr_1.equals(cur5gradio1.getDisabled())) {
				status5gSwitch = false;
			} else if (ConstantStr_0.equals(cur5gradio1.getDisabled())) {
				status5gSwitch = true;
			}
		}
		tb5gSwitch.setChecked(status5gSwitch);
		if (status5gSwitch) {
			ll5g.setVisibility(View.VISIBLE);
		} else {
			ll5g.setVisibility(View.GONE);
		}

		tv5gName.setText(cur5gifaceEntity.getSsid());
		tv5gEntrypt.setText(entryptMaps.get(cur5gifaceEntity.getEncryption()));

	}

	private void contentViewCreated() {
		tb2_4gSwitch = (ToggleButton) contentView
				.findViewById(R.id.router_setting_2_4g_switch);
		ll2_4g = (LinearLayout) contentView
				.findViewById(R.id.router_setting_2_4g_ll);
		rl2_4gName = (RelativeLayout) contentView
				.findViewById(R.id.router_setting_2_4g_wifi_name_setting);
		rl2_4gPwd = (RelativeLayout) contentView
				.findViewById(R.id.router_setting_2_4g_wifi_pwd_setting);
		rl2_4gEncryption = (RelativeLayout) contentView
				.findViewById(R.id.router_setting_2_4g_wifi_encryption_setting);
		rl2_4gChannel = (RelativeLayout) contentView
				.findViewById(R.id.router_setting_2_4g_wifi_channel_setting);
		tv2_4gName = (TextView) contentView
				.findViewById(R.id.router_setting_2_4g_wifi_name_tv);
		tv2_4gEntrypt = (TextView) contentView
				.findViewById(R.id.router_setting_2_4g_wifi_entrypt_tv);

		tb5gSwitch = (ToggleButton) contentView
				.findViewById(R.id.router_setting_5g_switch);
		ll5g = (LinearLayout) contentView
				.findViewById(R.id.router_setting_5g_ll);
		rl5gName = (RelativeLayout) contentView
				.findViewById(R.id.router_setting_5g_wifi_name_setting);
		rl5gPwd = (RelativeLayout) contentView
				.findViewById(R.id.router_setting_5g_wifi_pwd_setting);
		rl5gEncryption = (RelativeLayout) contentView
				.findViewById(R.id.router_setting_5g_wifi_encryption_setting);
		rl5gChannel = (RelativeLayout) contentView
				.findViewById(R.id.router_setting_5g_wifi_channel_setting);
		tv5gName = (TextView) contentView
				.findViewById(R.id.router_setting_5g_wifi_name_tv);
		tv5gEntrypt = (TextView) contentView
				.findViewById(R.id.router_setting_5g_wifi_entrypt_tv);

		tbQosSwitch = (ToggleButton) contentView
				.findViewById(R.id.router_setting_qos_switch);
		llAutoSwitch = (LinearLayout) contentView
				.findViewById(R.id.router_setting_auto_limit_ll);
		tbAutoSwitch = (ToggleButton) contentView
				.findViewById(R.id.router_setting_auto_limit_switch);
		rlConnectedSetting = (RelativeLayout) contentView
				.findViewById(R.id.router_setting_connected_setting);

		// 解决ToggleButton的setchecked方法会导致wifi掉线问题
		tb2_4gSwitch.setOnClickListener(clickListener);
		tb5gSwitch.setOnClickListener(clickListener);

		tb2_4gSwitch.setOnCheckedChangeListener(checkedChangeListener);
		tb5gSwitch.setOnCheckedChangeListener(checkedChangeListener);
		tbQosSwitch.setOnCheckedChangeListener(checkedChangeListener);
		tbAutoSwitch.setOnCheckedChangeListener(checkedChangeListener);

		rl2_4gName.setOnClickListener(clickListener);
		rl2_4gPwd.setOnClickListener(clickListener);
		rl2_4gEncryption.setOnClickListener(clickListener);
		rl2_4gChannel.setOnClickListener(clickListener);
		rl5gName.setOnClickListener(clickListener);
		rl5gPwd.setOnClickListener(clickListener);
		rl5gEncryption.setOnClickListener(clickListener);
		rl5gChannel.setOnClickListener(clickListener);
		rlConnectedSetting.setOnClickListener(clickListener);

	}

	private OnCheckedChangeListener checkedChangeListener = new OnCheckedChangeListener() {

		@Override
		public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
			if (arg0 == tb2_4gSwitch) {
				if (isChecked) {
					ll2_4g.setVisibility(View.VISIBLE);
				} else {
					ll2_4g.setVisibility(View.GONE);
				}

			} else if (arg0 == tb5gSwitch) {
				if (isChecked) {
					ll5g.setVisibility(View.VISIBLE);
				} else {
					ll5g.setVisibility(View.GONE);
				}
			} else if (arg0 == tbQosSwitch) {
				if (isChecked) {
					sendSwitchControlData(CmdindexTools.CMDINDEX_12,
							ConstantStr_1);
					llAutoSwitch.setVisibility(View.VISIBLE);
					progressDialogManager.showDialog(
							KEY_PROGRESS_DIALOG_QOS_STATUS,
							RouterWifiSettingActivity.this, null, null);

				} else {
					sendSwitchControlData(CmdindexTools.CMDINDEX_12,
							ConstantStr_0);
					llAutoSwitch.setVisibility(View.GONE);
					progressDialogManager.showDialog(
							KEY_PROGRESS_DIALOG_QOS_STATUS,
							RouterWifiSettingActivity.this, null, null);

				}

			} else if (arg0 == tbAutoSwitch) {
				if (isChecked) {
					sendAutoSwitchChangeData(CmdindexTools.CMDINDEX_13,
							ConstantStr_0);
					progressDialogManager.showDialog(
							KEY_PROGRESS_DIALOG_QOS_STATUS,
							RouterWifiSettingActivity.this, null, null);
				} else {
					sendAutoSwitchChangeData(CmdindexTools.CMDINDEX_13,
							ConstantStr_1);
					progressDialogManager.showDialog(
							KEY_PROGRESS_DIALOG_QOS_STATUS,
							RouterWifiSettingActivity.this, null, null);

				}
			}

		}
	};

	private OnClickListener clickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (arg0 == rl2_4gName) {
				create2_4gModifyNameDialog();

			} else if (arg0 == rl2_4gPwd) {
				create2_4gModifyPwdDialog();

			} else if (arg0 == rl2_4gEncryption) {
				create2_4gModifyEncryptionDialog();

			} else if (arg0 == rl2_4gChannel) {
				if (cur2_4gradio0.getChannel() != null) {
					Intent intent = new Intent();
					intent.setClass(RouterWifiSettingActivity.this,
							RouterWifiSetting2_4GChannelActivity.class);
					startActivity(intent);
				}

			} else if (arg0 == tb2_4gSwitch) {
				if (status2_4gSwitch) {
					sendSwitchControlData(CmdindexTools.CMDINDEX_7,
							ConstantStr_0);
					progressDialogManager.showDialog(
							KEY_PROGRESS_DIALOG_GET_2_4G,
							RouterWifiSettingActivity.this, null, null);
				} else {
					sendSwitchControlData(CmdindexTools.CMDINDEX_7,
							ConstantStr_1);
					progressDialogManager.showDialog(
							KEY_PROGRESS_DIALOG_GET_2_4G,
							RouterWifiSettingActivity.this, null, null);
				}

			} else if (arg0 == tb5gSwitch) {
				if (status5gSwitch) {
					sendSwitchControlData(CmdindexTools.CMDINDEX_8,
							ConstantStr_0);
					progressDialogManager.showDialog(
							KEY_PROGRESS_DIALOG_GET_5G,
							RouterWifiSettingActivity.this, null, null);
				} else {
					sendSwitchControlData(CmdindexTools.CMDINDEX_8,
							ConstantStr_1);
					progressDialogManager.showDialog(
							KEY_PROGRESS_DIALOG_GET_5G,
							RouterWifiSettingActivity.this, null, null);
				}

			} else if (arg0 == rl5gName) {
				create5gModifyNameDialog();

			} else if (arg0 == rl5gPwd) {
				create5gModifyPwdDialog();

			} else if (arg0 == rl5gEncryption) {
				create5gModifyEncryptionDialog();

			} else if (arg0 == rl5gChannel) {
				if (cur5gradio1.getChannel() != null) {
					Intent intent = new Intent();
					intent.setClass(RouterWifiSettingActivity.this,
							RouterWifiSetting5GChannelActivity.class);
					startActivity(intent);
				}
			} else if (arg0 == rlConnectedSetting) {
				TaskExecutor.getInstance().execute(new Runnable() {

					@Override
					public void run() {
						final boolean urlCanOpen = HttpUtil
								.verificationResponse(getLanguageUrl());
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if (urlCanOpen) {
									IntentUtil
											.startCustomBrowser(
													RouterWifiSettingActivity.this,
													getLanguageUrl(),
													getResources()
															.getString(
																	R.string.gateway_router_setting_connected_notwork),
													getResources().getString(
															R.string.set_titel));
								} else {
									createdOpenUrlPromtDialog();
								}
							}
						});
					}

				});
			}

		}
	};

	// 联网设置
	public void createdOpenUrlPromtDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.gateway_router_setting_dialog_toast));
		View oupView = inflater.inflate(
				R.layout.device_df_router_setting_wifi_encrypt_toast_dialog,
				null);
		TextView tvToast = (TextView) oupView
				.findViewById(R.id.router_setting_toast_tv);
		tvToast.setText(getResources().getString(
				R.string.gateway_router_setting_connected_notwork_toast));
		builder.setContentView(oupView)
				.setPositiveButton(
						getResources().getString(
								R.string.common_ok))
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

	private String getLanguageUrl(){
		if(LanguageUtil.isChina()){
			return "http://df.wulian.cc/cgi-bin/luci/web/init/hello?style=app&lang=zh-cn";
		}else{
			return "http://df.wulian.cc/cgi-bin/luci/web/init/hello?style=app&lang=en";
		}
	}
	// 修改2.4G名称
	private void create2_4gModifyNameDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.gateway_router_setting_wifi_name));
		View mndView = inflater.inflate(
				R.layout.device_df_router_setting_wifi_name_dialog, null);
		final EditText etRename = (EditText) mndView
				.findViewById(R.id.router_setting_wifi_rename_et);
		if (cur2_4gifaceEntity.getSsid() != null) {
			etRename.setText(cur2_4gifaceEntity.getSsid());
		}
		builder.setContentView(mndView)
				.setNegativeButton(getResources().getString(R.string.cancel))
				.setPositiveButton(
						getResources().getString(
								R.string.common_ok))
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						String newName = etRename.getText().toString().trim();
						if (!StringUtil.isNullOrEmpty(newName)) {
							sendModify2_4gNameData(newName);

						}

					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		WLDialog dlg = builder.create();
		dlg.show();
	}

	// 修改2.4G密码
	private void create2_4gModifyPwdDialog() {
		if(entryptMaps.get("none").equals(entryptMaps.get(cur2_4gifaceEntity.getEncryption()))){
			selecEncryptionHintDialog();
			return;
		}
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.gateway_router_setting_wifi_modify_pwd_title));
		final View mpdView = inflater.inflate(
				R.layout.device_df_router_setting_wifi_pwd_dialog, null);
		final EditText etPwd = (EditText) mpdView
				.findViewById(R.id.router_setting_pwd_new_name);
		final EditText etPwd_true = (EditText) mpdView
				.findViewById(R.id.router_setting_pwd_new_name_true);
		final ImageView ivPwd = (ImageView) mpdView
				.findViewById(R.id.router_setting_pwd_visible);
		final ImageView ivPwd_true = (ImageView) mpdView
				.findViewById(R.id.router_setting_pwd_visible_true);
		ivPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (pwd2_4gflag) {
					pwd2_4gflag = false;
					etPwd.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					etPwd.setSelection(etPwd.getText().length());
					ivPwd.setImageDrawable(getResources().getDrawable(
							R.drawable.dm_router_setting_wifi_pwd_visibale));
				} else {
					pwd2_4gflag = true;
					etPwd.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					etPwd.setSelection(etPwd.getText().length());
					ivPwd.setImageDrawable(getResources().getDrawable(
							R.drawable.dm_router_setting_wifi_pwd_invisibale));
				}
			}
		});
		ivPwd_true.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (pwd2_4gflag_true) {
					pwd2_4gflag_true = false;
					etPwd_true
							.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					etPwd_true.setSelection(etPwd_true.getText().length());
					ivPwd_true.setImageDrawable(getResources().getDrawable(
							R.drawable.dm_router_setting_wifi_pwd_visibale));
				} else {
					pwd2_4gflag_true = true;
					etPwd_true.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
					etPwd_true.setSelection(etPwd_true.getText().length());
					ivPwd_true.setImageDrawable(getResources().getDrawable(
							R.drawable.dm_router_setting_wifi_pwd_invisibale));
				}
			}
		});
		builder.setContentView(mpdView)
				.setNegativeButton(getResources().getString(R.string.cancel))
				.setPositiveButton(
						getResources().getString(
								R.string.common_ok))
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						String newPwd = etPwd.getText().toString().trim();
						String newPwdTrue = etPwd_true.getText().toString().trim();
							if (StringUtil.isNullOrEmpty(newPwd)) {
							WLToast.showToast(
									RouterWifiSettingActivity.this,
									getResources()
											.getString(
													R.string.set_password_not_null_hint),
									WLToast.TOAST_SHORT);
							return;
						}
						if (StringUtil.isNullOrEmpty(newPwdTrue)) {
							WLToast.showToast(
									RouterWifiSettingActivity.this,
									getResources()
											.getString(
													R.string.set_password_not_null_hint),
									WLToast.TOAST_SHORT);
							return;
						}
						if (newPwd.length() < 8 || newPwdTrue.length() < 8) {
							WLToast.showToast(
									RouterWifiSettingActivity.this,
									getResources()
											.getString(
													R.string.gateway_router_setting_wifi_modify_pwd_length_toast),
									WLToast.TOAST_SHORT);
							return;
						}
						if (newPwd.length() > 20 || newPwdTrue.length() > 20) {
							WLToast.showToast(
									RouterWifiSettingActivity.this,
									getResources()
											.getString(
													R.string.gateway_router_setting_wifi_modify_pwd_length_max_toast_20),
									WLToast.TOAST_SHORT);
							return;
						}
						if(!(newPwd.equals(newPwdTrue))){
							WLToast.showToast(
									RouterWifiSettingActivity.this,
									getResources()
											.getString(
													R.string.gateway_router_setting_password_authentication_error),
									WLToast.TOAST_SHORT);
						}else{
							sendModify2_4gPwdData(newPwd);
						}

					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		WLDialog dlg = builder.create();
		dlg.show();
	}

	// 修改2.4G加密方式
	private void create2_4gModifyEncryptionDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.gateway_router_setting_wifi_entrypt_choose));
		final View medView = inflater.inflate(
				R.layout.device_df_router_setting_wifi_encrypt_dialog, null);
		ListView lvEncrypt = (ListView) medView
				.findViewById(R.id.router_setting_wifi_encrypt_lv);

		wifi2_4GEncryptionAdapter mAdapter = new wifi2_4GEncryptionAdapter(this,
				listKeys);
		lvEncrypt.setAdapter(mAdapter);
		if (cur2_4gifaceEntity.getEncryption() != null) {
			checked2_4g = entryptKeys.get(cur2_4gifaceEntity.getEncryption());
		}
		lvEncrypt.setSelection(checked2_4g);

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
		dialog2_4gEntrypt = builder.create();
		dialog2_4gEntrypt.show();

	}

	/*
	 * 弹出设置加密方式提示框  entryptMaps.get(cur2_4gifaceEntity.getEncryption())
	 */
	private void selecEncryptionHintDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(R.string.gateway_router_setting_dialog_toast));
		TextView textView=new TextView(getBaseContext());
		textView.setTextColor(getResources().getColor(R.color.black));
		textView.setTextSize(16);
		textView.setHeight(360);
		textView.setGravity(Gravity.CENTER);		
		textView.setText(getResources().getString(R.string.gateway_router_no_encryption_mode));
		builder.setContentView(textView)				
				.setNegativeButton(R.string.common_ok)
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {

					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		dialog2_4gEntrypt = builder.create();
		dialog2_4gEntrypt.show();
		
	}
	
	// 修改5G名称
	private void create5gModifyNameDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.gateway_router_setting_wifi_name));
		View mndView = inflater.inflate(
				R.layout.device_df_router_setting_wifi_name_dialog, null);
		final EditText etRename = (EditText) mndView
				.findViewById(R.id.router_setting_wifi_rename_et);
		if (cur5gifaceEntity.getSsid() != null) {
			etRename.setText(cur5gifaceEntity.getSsid());
		}
		builder.setContentView(mndView)
				.setNegativeButton(getResources().getString(R.string.cancel))
				.setPositiveButton(
						getResources().getString(
								R.string.common_ok))
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						String newName = etRename.getText().toString().trim();
						if (!StringUtil.isNullOrEmpty(newName)) {
							sendModify5gNameData(newName);

						}
					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		WLDialog dlg = builder.create();
		dlg.show();
	}

	// 修改5G密码
	private void create5gModifyPwdDialog() {
		if(entryptMaps.get("none").equals(entryptMaps.get(cur5gifaceEntity.getEncryption()))){
			selecEncryptionHintDialog();
			return;
		}
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.gateway_router_setting_wifi_modify_pwd_title));
		final View mpdView = inflater.inflate(
				R.layout.device_df_router_setting_wifi_pwd_dialog, null);
		final EditText etPwd = (EditText) mpdView
				.findViewById(R.id.router_setting_pwd_new_name);
		final EditText etPwd_true = (EditText) mpdView
				.findViewById(R.id.router_setting_pwd_new_name_true);
		final ImageView ivPwd = (ImageView) mpdView
				.findViewById(R.id.router_setting_pwd_visible);
		final ImageView ivPwd_true = (ImageView) mpdView
				.findViewById(R.id.router_setting_pwd_visible_true);
		ivPwd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (pwd5gflag) {
					pwd5gflag = false;
					ivPwd.setImageDrawable(getResources().getDrawable(
							R.drawable.dm_router_setting_wifi_pwd_visibale));
					etPwd.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					etPwd.setSelection(etPwd.getText().length());
				} else {
					pwd5gflag = true;
					ivPwd.setImageDrawable(getResources().getDrawable(
							R.drawable.dm_router_setting_wifi_pwd_invisibale));
					etPwd.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					etPwd.setSelection(etPwd.getText().length());
				}
			}
		});
		ivPwd_true.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				if (pwd5gflag_true) {
					pwd5gflag_true = false;
					etPwd_true
							.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
					etPwd_true.setSelection(etPwd_true.getText().length());
					ivPwd_true.setImageDrawable(getResources().getDrawable(
							R.drawable.dm_router_setting_wifi_pwd_visibale));
				} else {
					pwd5gflag_true = true;
					etPwd_true.setInputType(InputType.TYPE_CLASS_TEXT
							| InputType.TYPE_TEXT_VARIATION_PASSWORD);
					etPwd_true.setSelection(etPwd_true.getText().length());
					ivPwd_true.setImageDrawable(getResources().getDrawable(
							R.drawable.dm_router_setting_wifi_pwd_invisibale));
				}
			}
		});
		builder.setContentView(mpdView)
				.setNegativeButton(getResources().getString(R.string.cancel))
				.setPositiveButton(
						getResources().getString(
								R.string.common_ok))
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						String newPwd = etPwd.getText().toString().trim();
						String newPwdTrue = etPwd_true.getText().toString().trim();
						if (StringUtil.isNullOrEmpty(newPwd)) {
							WLToast.showToast(
									RouterWifiSettingActivity.this,
									getResources()
											.getString(
													R.string.set_password_not_null_hint),
									WLToast.TOAST_SHORT);
							return;
						}
						if (StringUtil.isNullOrEmpty(newPwdTrue)) {
							WLToast.showToast(
									RouterWifiSettingActivity.this,
									getResources()
											.getString(
													R.string.set_password_not_null_hint),
									WLToast.TOAST_SHORT);
							return;
						}
						if (newPwd.length() < 8 || newPwdTrue.length() < 8) {
							WLToast.showToast(
									RouterWifiSettingActivity.this,
									getResources()
											.getString(
													R.string.gateway_router_setting_wifi_modify_pwd_length_toast),
									WLToast.TOAST_SHORT);
							return;
						}
						if (newPwd.length() > 20 || newPwdTrue.length() > 20) {
							WLToast.showToast(
									RouterWifiSettingActivity.this,
									getResources()
											.getString(
													R.string.gateway_router_setting_wifi_modify_pwd_length_max_toast_20),
									WLToast.TOAST_SHORT);
							return;
						}
						if(!(newPwd.equals(newPwdTrue))){
							WLToast.showToast(
									RouterWifiSettingActivity.this,
									getResources()
											.getString(
													R.string.gateway_router_setting_password_authentication_error),
									WLToast.TOAST_SHORT);
						}else{
							sendModify5gPwdData(newPwd);
						}
					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		WLDialog dlg = builder.create();
		dlg.show();
	}

	// 修改5G加密方式
	private void create5gModifyEncryptionDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.gateway_router_setting_wifi_entrypt_choose));
		final View medView = inflater.inflate(
				R.layout.device_df_router_setting_wifi_encrypt_dialog, null);
		ListView lvEncrypt = (ListView) medView
				.findViewById(R.id.router_setting_wifi_encrypt_lv);

		wifi5GEncryptionAdapter mAdapter = new wifi5GEncryptionAdapter(this,
				listKeys);
		lvEncrypt.setAdapter(mAdapter);
		if (cur5gifaceEntity.getEncryption() != null) {
			checked5g = entryptKeys.get(cur5gifaceEntity.getEncryption());
			lvEncrypt.setSelection(checked5g);
		}

		builder.setContentView(medView)
				.setNegativeButton(getResources().getString(R.string.cancel))
				.setPositiveButton(
						getResources().getString(
								R.string.common_ok))
				.setCancelOnTouchOutSide(true)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {

					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		dialog5gEntrypt = builder.create();
		dialog5gEntrypt.show();
	}

	// 从未加密到加密之后弹出提示框
	private void createdChangeEntryptToastDialog() {
		WLDialog.Builder builder = new WLDialog.Builder(this);
		builder.setTitle(getResources().getString(
				R.string.gateway_router_setting_dialog_toast));
		View cetView = inflater.inflate(
				R.layout.device_df_router_setting_wifi_encrypt_toast_dialog,
				null);
		TextView tvToast = (TextView) cetView
				.findViewById(R.id.router_setting_toast_tv);
		tvToast.setText(getResources().getString(
				R.string.gateway_router_setting_wifi_entrypt_toast));
		builder.setContentView(cetView)
				.setPositiveButton(
						getResources().getString(
								R.string.common_ok))
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

	// 发送控制开关数据,开关字段on
	private void sendSwitchControlData(String cmdIndex, String on) {
		JSONArray dataJsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(KeyTools.on, on);
			dataJsonArray.add(0, jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetSDK.sendSetRouterConfigMsg(gwID, cmdIndex, dataJsonArray);
	}

	// 发送控制开关数据,开关字段mode
	private void sendAutoSwitchChangeData(String cmdIndex, String mode) {
		JSONArray dataJsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(KeyTools.mode, mode);
			dataJsonArray.add(0, jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetSDK.sendSetRouterConfigMsg(gwID, cmdIndex, dataJsonArray);
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

	// 发送修改2.4gwifi名称数据
	private void sendModify2_4gNameData(String newName) {
		if (cur2_4gifaceEntity.getKey() == null
				|| cur2_4gifaceEntity.getMode() == null
				|| cur2_4gifaceEntity.getEncryption() == null
				|| cur2_4gradio0.getDisabled() == null
				|| cur2_4gradio0.getChannel() == null) {
			return;
		}
		progressDialogManager.showDialog(KEY_PROGRESS_DIALOG_GET_2_4G, this,
				null, null);
		sendSetWifiData(CmdindexTools.CMDINDEX_4, cur2_4gifaceEntity.getKey(),
				cur2_4gifaceEntity.getMode(), newName,
				cur2_4gifaceEntity.getEncryption(),
				cur2_4gradio0.getDisabled(), cur2_4gradio0.getChannel());

	}

	// 发送修改2.4gwifi密码数据
	private void sendModify2_4gPwdData(String newPwd) {
		if (cur2_4gifaceEntity.getMode() == null
				|| cur2_4gifaceEntity.getSsid() == null
				|| cur2_4gifaceEntity.getEncryption() == null
				|| cur2_4gradio0.getDisabled() == null
				|| cur2_4gradio0.getChannel() == null) {
			return;
		}
		progressDialogManager.showDialog(KEY_PROGRESS_DIALOG_GET_2_4G_PWD, this,
				null, null);
		sendSetWifiData(CmdindexTools.CMDINDEX_4, newPwd,
				cur2_4gifaceEntity.getMode(), cur2_4gifaceEntity.getSsid(),
				cur2_4gifaceEntity.getEncryption(),
				cur2_4gradio0.getDisabled(), cur2_4gradio0.getChannel());
	}

	// 发送修改2.4gwifi加密方式数据
	private void send2_4GEntryptData(int pos) {
		String newEntrypt = keyMaps.get(listKeys.get(pos));
		if (cur2_4gifaceEntity.getKey() == null
				|| cur2_4gifaceEntity.getMode() == null
				|| cur2_4gifaceEntity.getSsid() == null
				|| cur2_4gradio0.getDisabled() == null
				|| cur2_4gradio0.getChannel() == null) {
			return;
		}
		String key = cur2_4gifaceEntity.getKey();
		// 由加密方式转为不加密
		if (pos == 0 && !cur2_4gifaceEntity.getEncryption().equals("none")) {
			key = "";
			// 由不加密转为加密方式
		} else if (pos != 0
				&& cur2_4gifaceEntity.getEncryption().equals("none")) {
			createdChangeEntryptToastDialog();
			key = "12345678";

		}
		sendSetWifiData(CmdindexTools.CMDINDEX_4, key,
				cur2_4gifaceEntity.getMode(), cur2_4gifaceEntity.getSsid(),
				newEntrypt, cur2_4gradio0.getDisabled(),
				cur2_4gradio0.getChannel());

	}

	// 发送修改5gwifi名称数据
	private void sendModify5gNameData(String newName) {
		if (cur5gifaceEntity.getKey() == null
				|| cur5gifaceEntity.getMode() == null
				|| cur5gifaceEntity.getEncryption() == null
				|| cur5gradio1.getChannel() == null
				|| cur5gradio1.getDisabled() == null) {
			return;
		}
		progressDialogManager.showDialog(KEY_PROGRESS_DIALOG_GET_5G, this,
				null, null);
		sendSetWifiData(CmdindexTools.CMDINDEX_5, cur5gifaceEntity.getKey(),
				cur5gifaceEntity.getMode(), newName,
				cur5gifaceEntity.getEncryption(), cur5gradio1.getDisabled(),
				cur5gradio1.getChannel());
	}

	// 发送修改5gwifi密码数据
	private void sendModify5gPwdData(String newPwd) {
		if (cur5gifaceEntity.getMode() == null
				|| cur5gifaceEntity.getSsid() == null
				|| cur5gifaceEntity.getEncryption() == null
				|| cur5gradio1.getChannel() == null
				|| cur5gradio1.getDisabled() == null) {
			return;
		}
		progressDialogManager.showDialog(KEY_PROGRESS_DIALOG_GET_5G_PWD, this,
				null, null);
		sendSetWifiData(CmdindexTools.CMDINDEX_5, newPwd,
				cur5gifaceEntity.getMode(), cur5gifaceEntity.getSsid(),
				cur5gifaceEntity.getEncryption(), cur5gradio1.getDisabled(),
				cur5gradio1.getChannel());
	}

	// 发送修改5gwifi加密方式数据
	private void send5GEntryptData(int pos) {
		String newEntrypt = keyMaps.get(listKeys.get(pos));
		if (cur5gifaceEntity.getKey() == null
				|| cur5gifaceEntity.getMode() == null
				|| cur5gifaceEntity.getSsid() == null
				|| cur5gradio1.getDisabled() == null
				|| cur5gradio1.getChannel() == null) {
			return;
		}
		String key = cur5gifaceEntity.getKey();
		// 由加密方式转为不加密
		if (pos == 0 && !cur5gifaceEntity.getEncryption().equals("none")) {
			key = "";
			// 由不加密转为加密方式
		} else if (pos != 0 && cur5gifaceEntity.getEncryption().equals("none")) {
			createdChangeEntryptToastDialog();
			key = "12345678";

		}
		sendSetWifiData(CmdindexTools.CMDINDEX_5, key,
				cur5gifaceEntity.getMode(), cur5gifaceEntity.getSsid(),
				newEntrypt, cur5gradio1.getDisabled(), cur5gradio1.getChannel());

	}

	private class wifi2_4GEncryptionAdapter extends WLBaseAdapter<String> {

		public wifi2_4GEncryptionAdapter(Context context, List<String> data) {
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
			// RelativeLayout rlView = (RelativeLayout) view
			// .findViewById(R.id.router_setting_wifi_encrypt_rl);
			TextView tvName = (TextView) view
					.findViewById(R.id.router_setting_wifi_encrypt_tv);
			final ImageView ivChecked = (ImageView) view
					.findViewById(R.id.router_setting_wifi_encrypt_iv);
			tvName.setText(item);
			view.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					checked2_4g = pos;
					if (dialog2_4gEntrypt.isShowing()) {
						dialog2_4gEntrypt.dismiss();
					}
					notifyDataSetChanged();
					send2_4GEntryptData(pos);
				}

			});
			// 刷新标记位,实现类似RadioButton功能
			if (checked2_4g == pos) {
				ivChecked.setVisibility(View.VISIBLE);
				view.setSelected(true);
			} else {
				ivChecked.setVisibility(View.GONE);
				view.setSelected(false);

			}
		}

	}

	private class wifi5GEncryptionAdapter extends WLBaseAdapter<String> {

		public wifi5GEncryptionAdapter(Context context, List<String> data) {
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
			RelativeLayout rlView = (RelativeLayout) view
					.findViewById(R.id.router_setting_wifi_encrypt_rl);
			TextView tvName = (TextView) view
					.findViewById(R.id.router_setting_wifi_encrypt_tv);
			final ImageView ivChecked = (ImageView) view
					.findViewById(R.id.router_setting_wifi_encrypt_iv);
			tvName.setText(item);
			rlView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View arg0) {
					checked5g = pos;
					if (dialog5gEntrypt.isShowing()) {
						dialog5gEntrypt.dismiss();
					}
					notifyDataSetChanged();
					send5GEntryptData(pos);

				}

			});
			// 刷新标记位,实现类似RadioButton功能
			if (checked5g == pos) {
				ivChecked.setVisibility(View.VISIBLE);
				view.setSelected(true);
			} else {
				ivChecked.setVisibility(View.GONE);
				view.setSelected(false);

			}
		}

	}

	// 密码修改成功提示
	private void showModifyPwdSuccessToast() {
		WLToast.showToast(
				this,
				getResources().getString(
						R.string.device_account_modify_password_success),
				WLToast.TOAST_SHORT);
	}
	public void onEventMainThread(RouterWifiSettingEvent event) {
		if (RouterWifiSettingEvent.ACTION_REFRESH.equals(event.getAction())) {
			if (RouterWifiSettingEvent.TYPE_2_4G_WIFI.equals(event.getType())) {
				cur2_4gifaceEntity = event.getWifi_ifaceList().get(0);
				cur2_4gradio0 = event.getRadioList().get(0);
				refresh2_4GDataAndView();
				progressDialogManager.dimissDialog(
						KEY_PROGRESS_DIALOG_GET_2_4G, 0);
				// 在收到2.4g信息后,通过判断progressDialogManager是否含有KEY_PROGRESS_DIALOG_GET_2_4G_PWD,来判断之前操作是否为修改密码
				if (progressDialogManager
						.containsDialog(KEY_PROGRESS_DIALOG_GET_2_4G_PWD)) {
					progressDialogManager.dimissDialog(
							KEY_PROGRESS_DIALOG_GET_2_4G_PWD, 0);
					showModifyPwdSuccessToast();
				}
			} else if (RouterWifiSettingEvent.TYPE_5G_WIFI.equals(event
					.getType())) {
				cur5gifaceEntity = event.getWifi_ifaceList().get(0);
				cur5gradio1 = event.getRadioList().get(0);
				refresh5GDataAndView();
				progressDialogManager.dimissDialog(KEY_PROGRESS_DIALOG_GET_5G,
						0);
				if (progressDialogManager
						.containsDialog(KEY_PROGRESS_DIALOG_GET_5G_PWD)) {
					progressDialogManager.dimissDialog(
							KEY_PROGRESS_DIALOG_GET_5G_PWD, 0);
					showModifyPwdSuccessToast();
				}
			}
		}
	}

	public void onEventMainThread(RouterWifiSpeedEvent event) {
		if (RouterWifiSpeedEvent.ACTION_REFRESH.endsWith(event.getAction())) {
			statusEntity = event.getStatusEntity();
			refreshQOSSwitchStatus();
			progressDialogManager.dimissDialog(KEY_PROGRESS_DIALOG_QOS_STATUS,
					0);
		}

	}

}
