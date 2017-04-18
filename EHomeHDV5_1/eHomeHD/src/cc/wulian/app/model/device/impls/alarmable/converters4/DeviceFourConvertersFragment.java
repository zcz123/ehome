package cc.wulian.app.model.device.impls.alarmable.converters4;

import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.SendMessage;

public class DeviceFourConvertersFragment extends WulianFragment {
	
	public static final String STATUS_OPEN ="1"; 
	public static final String STATUS_CLOSE ="0"; 
	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	private String gwID;
	private String deviceID;
	private Map<String,String> statusDataMap = new HashMap<String, String>();
	private DeviceCache deviceCache;
	private WL_A1_Converters_Input_4 wulianDevcie;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getActivity().getIntent().getExtras();
		gwID = (String) bundle.getString(GWID);
		deviceID = (String) bundle.getString(DEVICEID);
		initBar();
		deviceCache = DeviceCache.getInstance(mActivity);
		wulianDevcie= (WL_A1_Converters_Input_4)deviceCache.getDeviceByID(mActivity,gwID, deviceID);
	}
	//actionBar
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setIconText(
				getResources().getString(R.string.device_ir_back));
		getSupportActionBar().setTitle(
				getResources().getString(R.string.device_type_A1));
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

	private void saveIRKeys() {
		/**
		 * 重写设置方法与发送数据
		 */
		SendMessage.sendControlDevMsg(gwID,deviceID, 
				"14", "A1", "1"+statusDataMap.get(WulianDevice.EP_14)+statusDataMap.get(WulianDevice.EP_15)+statusDataMap.get(WulianDevice.EP_16)+statusDataMap.get(WulianDevice.EP_17));
		mActivity.finish();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		
		View view = inflater.inflate(
				R.layout.device_four_wired_wireless_translator_setting_container,
				null);
		LinearLayout containerView = (LinearLayout)view.findViewById(R.id.device_one_wried_wireless_setting_container);
		int i = 1;
		for(final String ep : wulianDevcie.getChildDevices().keySet()){
			WulianDevice d = wulianDevcie.getChildDevice(ep);
			if(d instanceof WL_A1_Converters_Input_4){
				WL_A1_Converters_Input_4 devcie = (WL_A1_Converters_Input_4)d;
				statusDataMap.put(ep, devcie.data_switch);
				View itemView = inflater.inflate(
						R.layout.device_four_wired_wireless_translator_setting_item,
						null);
				TextView nameTextView = (TextView) itemView
						.findViewById(R.id.device_one_wried_wireless_setting_dev_bind1);
				if(!StringUtil.isNullOrEmpty(devcie.getDeviceInfo().getDevEPInfo().getEpName())){
					nameTextView.setText(devcie.getDeviceInfo().getDevEPInfo()
							.getEpName());
				} else {
//					nameTextView.setText(getResources().getString(R.string.device_type_A1));
					nameTextView.setText(i+",key"+i);
				}
				final ImageView mSwitchImageView = (ImageView) itemView
						.findViewById(R.id.device_one_wried_wireless_setting_switch_status1);
				final FrameLayout mSwitchBtnOpen = (FrameLayout) itemView
						.findViewById(R.id.device_one_wried_wireless_setting_switch_open1);
				final FrameLayout mSwitchBtnClose = (FrameLayout) itemView
						.findViewById(R.id.device_one_wried_wireless_setting_switch_close1);
				if (WL_A1_Converters_Input_4.DATA_STATUS_OPEN.equals(devcie.data_switch)) {
					mSwitchBtnOpen.setVisibility(View.VISIBLE);
					mSwitchBtnClose.setVisibility(View.GONE);
					mSwitchImageView.setImageDrawable(getResources().getDrawable(
							R.drawable.device_one_wried_wireless_setting_switch_open));
				} else {
					mSwitchBtnClose.setVisibility(View.VISIBLE);
					mSwitchBtnOpen.setVisibility(View.GONE);
					mSwitchImageView.setImageDrawable(getResources().getDrawable(
							R.drawable.device_one_wried_wireless_setting_switch_close));
				}
				mSwitchBtnOpen.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						statusDataMap.put(ep, STATUS_CLOSE);
						mSwitchBtnClose.setVisibility(View.VISIBLE);
						mSwitchBtnOpen.setVisibility(View.GONE);
						mSwitchImageView
								.setImageDrawable(getResources()
										.getDrawable(
												R.drawable.device_one_wried_wireless_setting_switch_close));

					}
				});
				mSwitchBtnClose.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View arg0) {
						statusDataMap.put(ep, STATUS_OPEN);
						mSwitchBtnOpen.setVisibility(View.VISIBLE);
						mSwitchBtnClose.setVisibility(View.GONE);
						mSwitchImageView
								.setImageDrawable(getResources()
										.getDrawable(
												R.drawable.device_one_wried_wireless_setting_switch_open));

					}
				});
				containerView.addView(itemView);
			}
			i++;

		}
		return view;
	}
}
