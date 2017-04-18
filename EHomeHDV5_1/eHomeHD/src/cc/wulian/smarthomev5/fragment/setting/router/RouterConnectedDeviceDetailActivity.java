package cc.wulian.smarthomev5.fragment.setting.router;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.callback.router.CmdindexTools;
import cc.wulian.smarthomev5.callback.router.KeyTools;
import cc.wulian.smarthomev5.callback.router.RouterDataCacheManager;
import cc.wulian.smarthomev5.callback.router.entity.SpeedListQosEntity;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.view.CommonSingleWheelView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

public class RouterConnectedDeviceDetailActivity extends EventBusActivity {
	public static final String KEY_ITEM_MAC = "KEY_ITEM_MAC";
	public static final String KEY_ITEM_NAME = "KEY_ITEM_NAME";
	public static final String KEY_ITEM_IP = "KEY_ITEM_IP";
	public static final String CMD_MODE_2 = "2";

	private List<String> speedLists = new ArrayList<String>();
	@SuppressLint("UseSparseArrays")
	private Map<Integer, Integer> spinnerMaps = new HashMap<Integer, Integer>();
	private SpeedListQosEntity speedQosEntity = new SpeedListQosEntity();

	private View contentView;
	private String deviceName;
	private String deviceIp;
	private String deviceMac;
//	private Spinner upSpinner;
//	private Spinner downSpinner;
	private TextView upTextView;
	private TextView downTextView;
	private WLDialog dialog;

	private String[] persionValus = { "0","5","10", "15", "20", "25", "30", "35", "40", "45", "50","55", "60","65", "70", "75", "80", "85", "90", "95", "100"};
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LayoutInflater inflater = LayoutInflater.from(this);
		Bundle bundle = getIntent().getExtras();
		deviceMac = (String) bundle.get(KEY_ITEM_MAC);
		deviceName = (String) bundle.getString(KEY_ITEM_NAME);
		deviceIp = (String) bundle.getString(KEY_ITEM_IP);
		speedQosEntity = RouterDataCacheManager.getInstance()
				.getDeviceQosEntity(deviceMac);
//		initCacheData();
		initBar();
		contentView = inflater.inflate(
				R.layout.device_df_router_setting_connected_device, null);
		setContentView(contentView);
		contentViewCreated();
	}

//	private void initCacheData() {
//		speedLists.add(getResources().getString(
//				R.string.gateway_router_setting_device_limit_speed_none));
//		// 50~500为显示给用户的效果图值,实际数据并非此值
//		speedLists.add("50kb/s");
//		speedLists.add("100kb/s");
//		speedLists.add("200kb/s");
//		speedLists.add("500kb/s");
//
//		// Spinner中index和对应比例
//		spinnerMaps.put(0, 100);
//		spinnerMaps.put(1, 25);
//		spinnerMaps.put(2, 50);
//		spinnerMaps.put(3, 75);
//		// 暂定限速500kb/s为不到100,所以在手动限速模式下设置最大值为99,注意100默认代表未限速
//		spinnerMaps.put(4, 99);
//	}

	private void initBar() {
		resetActionMenu();
		getCompatActionBar().setDisplayHomeAsUpEnabled(true);
		getCompatActionBar().setIconText(
				getResources().getString(R.string.gateway_router_setting));
		getCompatActionBar().setTitle(
				getResources().getString(
						R.string.gateway_router_setting_device_manager));
		getCompatActionBar().setDisplayShowMenuTextEnabled(true);
		getCompatActionBar().setRightIconText(
				getResources().getString(
						R.string.set_sound_notification_bell_prompt_choose_complete));
		getCompatActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						setSpeedLimitQuery();
						RouterConnectedDeviceDetailActivity.this.finish();

					}

				});
	}

	/**
	 * mode为1为设置总体的上下限制，数据 (JsonArry[JsonObject{mode,upload,download}])
	 * mode为2时为设置单一的上下限制 ，数据(JsonArry[JsonObject{mode,mac,level
	 * ,upload,download}]) level:3表示优 2表示中 1表示差
	 */
	private void setSpeedLimitQuery() {
		String level = "1";
		// 在speedQosEntity为空时会给level默认为0
		if (speedQosEntity != null) {
			if (speedQosEntity.getLevel() != 0) {
				level = String.valueOf(speedQosEntity.getLevel());
			}
		}
//		int upPos = upSpinner.getSelectedItemPosition();
//		int downPos = downSpinner.getSelectedItemPosition();
		int upPos = StringUtil.toInteger(upTextView.getText().toString());
		int downPos = StringUtil.toInteger(downTextView.getText().toString());

		JSONArray jsonArray = new JSONArray();
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put(KeyTools.mode, CMD_MODE_2);
			jsonObject.put(KeyTools.mac, deviceMac);
			jsonObject.put(KeyTools.level, level);
			jsonObject.put(KeyTools.upload,upPos + "");
			jsonObject.put(KeyTools.download,downPos + "");
//			jsonObject.put(KeyTools.upload,
//					String.valueOf(spinnerMaps.get(upPos)));
//			jsonObject.put(KeyTools.download,
//					String.valueOf(spinnerMaps.get(downPos)));
			jsonArray.add(0, jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		NetSDK.sendSetRouterConfigMsg(
				AccountManager.getAccountManger().getmCurrentInfo().getGwID(),
				CmdindexTools.CMDINDEX_3, jsonArray);
	}

	private void contentViewCreated() {
		TextView tvName = (TextView) contentView
				.findViewById(R.id.router_setting_device_name);
		TextView tvMac = (TextView) contentView
				.findViewById(R.id.router_setting_device_mac);
		TextView tvIp = (TextView) contentView
				.findViewById(R.id.router_setting_device_ip);
		Button btnAddBlack = (Button) contentView
				.findViewById(R.id.router_setting_device_add_black);
//		upSpinner = (Spinner) contentView
//				.findViewById(R.id.router_setting_device_upspeed_spinner);
//		downSpinner = (Spinner) contentView
//				.findViewById(R.id.router_setting_device_downspeed_spinner);
		upTextView = (TextView) contentView.findViewById(R.id.router_setting_device_upspeed_textview);
		downTextView = (TextView) contentView.findViewById(R.id.router_setting_device_downspeed_textview);
		upTextView.setOnClickListener(upAndDownSpeedLinestener);
		downTextView.setOnClickListener(upAndDownSpeedLinestener);
		
//		SpeedLimitAdapter mAdapter = new SpeedLimitAdapter(this, speedLists);
//		upSpinner.setAdapter(mAdapter);
//		downSpinner.setAdapter(mAdapter);
//		downSpinner.setOnItemSelectedListener(new OnItemSelectedListener() {
//
//			@Override
//			public void onItemSelected(AdapterView<?> arg0, View arg1,
//					int arg2, long arg3) {
//				// 设置Spinner的显示数据
//				setTitle(speedLists.get(arg2));
//			}
//
//			@Override
//			public void onNothingSelected(AdapterView<?> arg0) {
//
//			}
//		});
		if (speedQosEntity != null) {
			int upMax = speedQosEntity.getUpmaxper();
			int downMax = speedQosEntity.getMaxdownper();
			upTextView.setText(upMax + "");
			downTextView.setText(downMax + "");
//			initUpSpinnerPos(upMax);
//			initdownSpinnerPos(downMax);
		}
		tvName.setText(deviceName);
		tvMac.setText(deviceMac);
		tvIp.setText(deviceIp);

		btnAddBlack.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// 添加黑名单
				/**
				 * "data":[{"name":"tttttt","mac":"44:44:33:33:22:66","type":"2"
				 * }]
				 */
				RouterDataCacheManager dataCacheManager = RouterDataCacheManager
						.getInstance();
				if (!CmdindexTools.SET_DATA_2.equals(dataCacheManager
						.getCurMode())) {
					WLToast.showToast(
							RouterConnectedDeviceDetailActivity.this,
							getResources().getString(
									R.string.gateway_router_setting_white_list),
							WLToast.TOAST_SHORT);
					return;
				}
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put(KeyTools.name, deviceName);
					jsonObject.put(KeyTools.mac, deviceMac);
					jsonObject.put(KeyTools.type, CmdindexTools.SET_DATA_2);
					JSONArray jsonArray = new JSONArray();
					jsonArray.add(0, jsonObject);
					NetSDK.sendSetRouterConfigMsg(AccountManager
							.getAccountManger().getmCurrentInfo().getGwID(),
							CmdindexTools.CMDINDEX_14, jsonArray);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});

	}
	
//	// 初始化上传速度
//	private void initUpSpinnerPos(int upMax) {
//		int upSelection = 0;
//		if (upMax == 0) {
//			upSelection = 0;
//		} else if (upMax > 0 && upMax <= 25) {
//			upSelection = 1;
//		} else if (upMax > 25 && upMax <= 50) {
//			upSelection = 2;
//		} else if (upMax > 50 && upMax <= 75) {
//			upSelection = 3;
//		} else if (upMax > 75 && upMax < 100) {
//			upSelection = 4;
//		}
//		upSpinner.setSelection(upSelection);
//
//	}

//	// 初始化下载速度,getMaxdownper中数据为比例0~100
//	private void initdownSpinnerPos(int downMax) {
//		int downSelection = 0;
//		if (downMax == 0) {
//			downSelection = 0;
//		} else if (downMax > 0 && downMax <= 25) {
//			downSelection = 1;
//		} else if (downMax > 25 && downMax <= 50) {
//			downSelection = 2;
//		} else if (downMax > 50 && downMax <= 75) {
//			downSelection = 3;
//		} else if (downMax > 75 && downMax < 100) {
//			downSelection = 4;
//		}
//		downSpinner.setSelection(downSelection);
//	}

//	private class SpeedLimitAdapter extends WLBaseAdapter<String> {
//
//		public SpeedLimitAdapter(Context context, List<String> data) {
//			super(context, data);
//		}
//
//		@Override
//		protected View newView(Context context, LayoutInflater inflater,
//				ViewGroup parent, int pos) {
//			return inflater.inflate(
//					R.layout.device_df_router_setting_limit_speed_item, null);
//		}
//
//		@Override
//		protected void bindView(Context context, View view, int pos, String item) {
//			super.bindView(context, view, pos, item);
//			TextView textView = (TextView) view
//					.findViewById(R.id.router_setting_limit_speed_tv);
//			textView.setText(item);
//		}
//
//	}
	
	
	private void createSelectValuesDialog(final TextView valuesText, final String[] valuesStr,String unit,String pos) {
		WLDialog.Builder builder = new WLDialog.Builder(this);
		final CommonSingleWheelView commonSingleWheelView = new CommonSingleWheelView(this, valuesStr, unit, pos);
		builder.setTitle(this.getResources().getString(R.string.gateway_router_setting_select_percentage))
		.setContentView(commonSingleWheelView)
		.setNegativeButton(this.getResources().getString(R.string.cancel))
		.setPositiveButton(this.getResources().getString(R.string.common_ok))
		.setListener(new MessageListener() {
			
			@Override
			public void onClickPositive(View contentViewLayout) {
				valuesText.setText(valuesStr[commonSingleWheelView.getValuesWheel().getSelectedItemPosition()]);
			}
			
			@Override
			public void onClickNegative(View contentViewLayout) {
				
			}
		});
		dialog = builder.create();
		dialog.show();
	}
	

	OnClickListener upAndDownSpeedLinestener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			if(v.getId() == upTextView.getId()){
				String position = getSettingPosition(upTextView,"0",persionValus);
				createSelectValuesDialog(upTextView, persionValus, "%", position);
			}else if(v.getId() == downTextView.getId()){
				String position = getSettingPosition(downTextView,"0",persionValus);
				createSelectValuesDialog(downTextView, persionValus, "%", position);
			}
		}
		
		private String getSettingPosition(TextView text,String position,String[] valuesStr) {
			for(int i = 0; i < valuesStr.length; i++){
				if(StringUtil.equals(text.getText(), valuesStr[i])){
					position = i + "";
					break;
				}
			}
			return position;
		}
	};
	
}
