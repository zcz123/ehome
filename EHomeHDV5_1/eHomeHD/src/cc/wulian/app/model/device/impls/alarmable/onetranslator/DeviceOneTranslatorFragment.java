package cc.wulian.app.model.device.impls.alarmable.onetranslator;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.SendMessage;

public class DeviceOneTranslatorFragment extends WulianFragment {

	public static final String DEVICE_ONE_TRANSLATOR = "DEVICE_ONE_TRANSLATOR";
	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	public static final String ISOPEN = "isopen";
	public static final String DEFAULT_STATUS = "defaultStatus";
	public static final String EPTYPE_A1 = "A1";
	public static final String EPTYPE_A2 = "A2";
	public static final String EP_14 = "14";
	public static final String EP_15 = "15";
	public static final String SWITCH_OPEN = "11";
	public static final String SWITCH_CLOSE = "10";
	public static final String DEFAULT_OPEN = "50";
	public static final String DEFAULT_CLOSE = "51";
	private String gwID;
	private String deviceID;
	private String isOpen;
	private String defaultOutputStatus;
	private WulianDevice oneDevice;

	private int isbtnopen = -1;
	private int defaultBtn = -1;

	private TextView mDefaultText;
	private ImageView mDefaultSwitch;
	private FrameLayout mDefaultOpenBtn;
	private FrameLayout mDefaultCloseBtn;

	private TextView mDeviceTextView;
	private ImageView mSwitchImageView;
	private FrameLayout mSwitchBtnOpen;
	private FrameLayout mSwitchBtnClose;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle bundle = getArguments();
		gwID = (String) bundle.getString(GWID);
		deviceID = (String) bundle.getString(DEVICEID);
		isOpen = (String) bundle.getString(ISOPEN);
		defaultOutputStatus = (String) bundle.getString(DEFAULT_STATUS);
		oneDevice = DeviceCache.getInstance(mActivity).getDeviceByID(mActivity,
				gwID, deviceID);
		initBar();

	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setIconText(
				getResources().getString(R.string.device_ir_back));
		getSupportActionBar().setTitle(
				getResources().getString(R.string.device_type_B9));
		getSupportActionBar().setRightIconText(
				getResources().getString(R.string.device_ir_save));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						saveIRKeys();
					}
				});

	}

	// 发送保存设置数据
	private void saveIRKeys() {
		if (isbtnopen == 1) {
			SendMessage.sendControlDevMsg(gwID, deviceID, EP_14, EPTYPE_A1,
					SWITCH_OPEN);

		} else if (isbtnopen == 0) {
			SendMessage.sendControlDevMsg(gwID, deviceID, EP_14, EPTYPE_A1,
					SWITCH_CLOSE);

		}
		if (defaultBtn == 0) {
			SendMessage.sendControlDevMsg(gwID, deviceID, EP_15, EPTYPE_A2,
					DEFAULT_OPEN);

		} else if (defaultBtn == 1) {
			SendMessage.sendControlDevMsg(gwID, deviceID, EP_15, EPTYPE_A2,
					DEFAULT_CLOSE);

		}
		mActivity.finish();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.device_one_wired_wireless_translator_set_fragment,
				null);
		return view;

	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		mDefaultText = (TextView) view
				.findViewById(R.id.device_one_wried_wireless_default_dev_bind);
		mDefaultSwitch = (ImageView) view
				.findViewById(R.id.device_one_wried_wireless_default_switch_status);
		mDefaultOpenBtn = (FrameLayout) view
				.findViewById(R.id.device_one_wried_wireless_default_switch_open);
		mDefaultCloseBtn = (FrameLayout) view
				.findViewById(R.id.device_one_wried_wireless_default_switch_close);

		mDeviceTextView = (TextView) view
				.findViewById(R.id.device_one_wried_wireless_setting_dev_bind);
		mDeviceTextView.setText(DeviceTool.getDeviceShowName(oneDevice));
		mSwitchImageView = (ImageView) view
				.findViewById(R.id.device_one_wried_wireless_setting_switch_status);
		mSwitchBtnOpen = (FrameLayout) view
				.findViewById(R.id.device_one_wried_wireless_setting_switch_open);
		mSwitchBtnClose = (FrameLayout) view
				.findViewById(R.id.device_one_wried_wireless_setting_switch_close);

		initDefaultSettingSwitch();

		if (isOpen != null) {
			if (isOpen.equals("o")) {
				mSwitchBtnOpen.setVisibility(View.VISIBLE);
				mSwitchBtnClose.setVisibility(View.GONE);
				mSwitchImageView
						.setImageDrawable(getResources()
								.getDrawable(
										R.drawable.device_one_wried_wireless_setting_switch_open));

			} else if (isOpen.equals("c")) {
				mSwitchBtnClose.setVisibility(View.VISIBLE);
				mSwitchBtnOpen.setVisibility(View.GONE);
				mSwitchImageView
						.setImageDrawable(getResources()
								.getDrawable(
										R.drawable.device_one_wried_wireless_setting_switch_close));

			}
		}

		mSwitchBtnOpen.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mSwitchBtnClose.setVisibility(View.VISIBLE);
				mSwitchBtnOpen.setVisibility(View.GONE);
				mSwitchImageView
						.setImageDrawable(getResources()
								.getDrawable(
										R.drawable.device_one_wried_wireless_setting_switch_close));
				isbtnopen = 0;

			}
		});
		mSwitchBtnClose.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mSwitchBtnOpen.setVisibility(View.VISIBLE);
				mSwitchBtnClose.setVisibility(View.GONE);
				mSwitchImageView
						.setImageDrawable(getResources()
								.getDrawable(
										R.drawable.device_one_wried_wireless_setting_switch_open));
				isbtnopen = 1;

			}
		});

	}

	// 根据bundle传递数据初始化状态
	private void initDefaultSettingSwitch() {
		WL_B9_One_Wried_Wireless_Translator curDevcie = (WL_B9_One_Wried_Wireless_Translator) oneDevice;
		String childDeviceName = curDevcie.getChildDevice(EP_15)
				.getDeviceInfo().getDevEPInfo().getEpName();
		if (!StringUtil.isNullOrEmpty(childDeviceName)) {
			mDefaultText.setText(childDeviceName);
		} else {
			mDefaultText.setText(DeviceTool.getDeviceShowName(curDevcie));
		}

		if ("00".equals(defaultOutputStatus)) {
			mDefaultOpenBtn.setVisibility(View.VISIBLE);
			mDefaultCloseBtn.setVisibility(View.GONE);
			mDefaultSwitch.setImageDrawable(getResources().getDrawable(
					R.drawable.device_one_wried_wireless_setting_switch_open));
			defaultBtn = 0;
		} else if ("01".equals(defaultOutputStatus)) {
			mDefaultOpenBtn.setVisibility(View.GONE);
			mDefaultCloseBtn.setVisibility(View.VISIBLE);
			mDefaultSwitch.setImageDrawable(getResources().getDrawable(
					R.drawable.device_one_wried_wireless_setting_switch_close));
			defaultBtn = 1;
		}
		mDefaultOpenBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mDefaultOpenBtn.setVisibility(View.GONE);
				mDefaultCloseBtn.setVisibility(View.VISIBLE);
				mDefaultSwitch
						.setImageDrawable(getResources()
								.getDrawable(
										R.drawable.device_one_wried_wireless_setting_switch_close));
				defaultBtn = 1;
			}
		});
		mDefaultCloseBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				mDefaultOpenBtn.setVisibility(View.VISIBLE);
				mDefaultCloseBtn.setVisibility(View.GONE);
				mDefaultSwitch
						.setImageDrawable(getResources()
								.getDrawable(
										R.drawable.device_one_wried_wireless_setting_switch_open));
				defaultBtn = 0;
			}
		});
	}
}
