package cc.wulian.app.model.device.impls.sensorable;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager.PluginsManagerCallback;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.LanguageUtil;

/**
 * sphygmomanometer
 * 
 * @author Administrator 血压计
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_SPHYGMOMETER }, category = Category.C_HEALTH)
public class WL_48_Sphy extends SensorableDeviceImpl {

	// private static final String BLOOD_PRESSURE_UNIT = "mmHg";
	// private static final String PULSE_RATE_UNIT = "bpm";
	// private static final String BLOOD_OXYGEN_UNIT = "%";
	private static final String PREFIX_DATA_SYSTEM_TIME = "01";
	private static final String PREFIX_DATA_INTERVAL_TIME = "02";
	private static final String PREFIX_DATA_PAUSE = "03";
	private static final String PREFIX_DATA_BLOOD_PRESSURE = "06";
	private static final String PREFIX_DATA_BLOOD_OXYGEN = "07";

	private String systemTime;
	private String intervalTime;
	private boolean pause;
	private String bpUserID;

	private String highPressure;
	private String lowPressure;
	private String bpPulseRate;
	private String averagePressure;
	private String time;

	private String boUserID;
	private String bloodOxygen;
	private String boPulseRate;

	private boolean bloodPressureFlag;
	private boolean bloodOxygenFlag;
	
	private TextView userTextView;
	private TextView systemTimeTextView;
	private TextView highPressureTextView;
	private TextView lowPressureTextView;
	private TextView averagePressureTextView;
	private TextView bloodOxygenTextView;
	private TextView pulseRateTextView;
	private LinearLayout sphyPauseLayout;
	private String pluginName = "sphy.zip";

	public WL_48_Sphy(Context context, String type) {
		super(context, type);
	}

	private static void disassembleCompoundCmd(WL_48_Sphy device, String epData) {
		if (isNull(epData))
			return;

		if (epData.startsWith(PREFIX_DATA_SYSTEM_TIME) && epData.length() >= 12) {
			device.systemTime = ""
					+ (StringUtil.toInteger(epData.substring(4, 6), 16) + 1)
					+ "-" + StringUtil.toInteger(epData.substring(6, 8), 16)
					+ " " + StringUtil.toInteger(epData.substring(8, 10), 16)
					+ ":" + StringUtil.toInteger(epData.substring(10, 12), 16);
		}

		if (epData.startsWith(PREFIX_DATA_INTERVAL_TIME)
				&& epData.length() >= 4) {
			device.intervalTime = ""
					+ StringUtil.toInteger(epData.substring(2, 4), 16);
		}
		if (epData.startsWith(PREFIX_DATA_PAUSE)) {
			device.pause = true;
		}

		if (epData.startsWith(PREFIX_DATA_BLOOD_PRESSURE)
				&& epData.length() >= 24) {

			device.bloodPressureFlag = true;
			device.bloodOxygenFlag = false;
			device.bpUserID = epData.substring(2, 4);

			device.highPressure = StringUtil.toInteger(epData.substring(4, 8),
					16) + "";

			device.lowPressure = StringUtil.toInteger(epData.substring(8, 10),
					16) + "";

			device.bpPulseRate = StringUtil.toInteger(epData.substring(10, 12),
					16) + "";

			device.averagePressure = StringUtil.toInteger(
					epData.substring(12, 14), 16)
					+ "";

			device.time = epData.substring(14, 24);
			// SimpleDateFormat sdf = new SimpleDateFormat("yy-MM-dd HH:mm:ss");
			// sb.append(sdf.format(timeData));

			device.bloodOxygen = "";
			device.boPulseRate = "";

		}

		if (epData.startsWith(PREFIX_DATA_BLOOD_OXYGEN) && epData.length() >= 8) {
			device.bloodOxygenFlag = true;
			device.bloodPressureFlag = false;
			device.boUserID = epData.substring(2, 4);
			device.bloodOxygen = StringUtil.toInteger(epData.substring(4, 6),
					16) + "";
			device.boPulseRate = StringUtil.toInteger(epData.substring(6, 8),
					16) + "";

			device.highPressure = "";
			device.lowPressure = "";
			device.averagePressure = "";
			device.bpPulseRate = "";

		}

	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {

		StringBuilder sb = new StringBuilder();

		if (bloodPressureFlag) {

			if (highPressure != null && !"".equals(highPressure)) {

				String highBlood = getString(R.string.device_sphy_high_blood_pressure);
				sb.append(highBlood);
				sb.append(highPressure).append(" ");
			}

			if (lowPressure != null && !"".equals(lowPressure)) {

				String lowBlood = getString(R.string.device_sphy_low_blood_pressure);
				sb.append(lowBlood);
				sb.append(lowPressure).append(" ");

			}
			if (bpPulseRate != null && !"".equals(bpPulseRate)) {

				String pulseRate = getString(R.string.device_sphy_pulse_rate);
				sb.append(pulseRate);
				sb.append(bpPulseRate).append(" ");
			}

		}

		if (bloodOxygenFlag) {
			if (bloodOxygen != null && !"".equals(bloodOxygen)) {

				String bloodOxygenStr = getString(R.string.device_sphy_blood_oxygen);
				sb.append(bloodOxygenStr);
				sb.append(bloodOxygen).append(" ");

			}

			if (boPulseRate != null && !"".equals(boPulseRate)) {

				String pulseRate = getString(R.string.device_sphy_pulse_rate);
				sb.append(pulseRate);
				sb.append(boPulseRate).append(" ");

			}
		}
		return sb;
	}

	@Override
	public boolean isLinkControl() {
		return false;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_sphygmometer, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		userTextView = (TextView) view.findViewById(R.id.sphy_user_tv);
		systemTimeTextView = (TextView) view.findViewById(R.id.sphy_time_tv);
		highPressureTextView = (TextView) view
				.findViewById(R.id.sphy_high_pressure);
		lowPressureTextView = (TextView) view
				.findViewById(R.id.sphy_low_pressure);
		averagePressureTextView = (TextView) view
				.findViewById(R.id.sphy_average_pressure);
		bloodOxygenTextView = (TextView) view
				.findViewById(R.id.sphy_blood_oxygen);
		pulseRateTextView = (TextView) view.findViewById(R.id.sphy_pulse_rate);
		sphyPauseLayout = (LinearLayout) view.findViewById(R.id.sphy_pause);
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		// SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
		// systemTime.setText(format.format(new
		// Date(System.currentTimeMillis())));
		if (StringUtil.isNullOrEmpty(bpUserID)) {
			userTextView.setText("");
		} else {
			userTextView.setText(getResources().getString(R.string.device_user)
					+ StringUtil.toInteger(bpUserID));
		}
		systemTimeTextView.setText(systemTime);
		highPressureTextView.setText(highPressure);
		lowPressureTextView.setText(lowPressure);
		averagePressureTextView.setText(averagePressure);
		bloodOxygenTextView.setText(bloodOxygen);
		if (bpPulseRate != null && !"".equals(bpPulseRate)) {
			pulseRateTextView.setText(bpPulseRate);
		} else {
			pulseRateTextView.setText(boPulseRate);
		}
		sphyPauseLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SendMessage.sendControlDevMsg(gwID, devID, ep, epType, "3");
			}
		});
	}
	
	//gwID devID 绑定设备 跳转到另一个Activity
//		@Override
//		public Intent getSettingIntent() {
//			Intent intent = new Intent(mContext,DeviceSettingActivity.class);
//			intent.putExtra(EditSphyFragment.GWID, gwID);
//			intent.putExtra(EditSphyFragment.DEVICEID, devID);
//			intent.putExtra(EditSphyFragment.EP, getCurrentEpInfo().getEp());
//			intent.putExtra(EditSphyFragment.EPTYPE, getCurrentEpInfo().getEpType());
//			intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, EditSphyFragment.class.getName());
//			return intent ;
//		}

	@Override
	public void refreshDevice() {
		super.refreshDevice();
		disassembleCompoundCmd(this, epData);
	}

	public String getIntervalTime() {
		return intervalTime;
	}

	@Override
	protected List<MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
		List<MenuItem> items = super.getDeviceMenuItems(manager);
		MenuItem settingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext
						.getString(cc.wulian.smarthomev5.R.string.device_human_traffic_statistics));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				getPlugin();
				manager.dismiss();
			}
		};
		if(isDeviceOnLine())
			items.add(settingItem);
		return items;
	}
	
	private void getPlugin(){
		new Thread(new Runnable() {
			@Override
			public void run() {
				PluginsManager pm=PluginsManager.getInstance();
				pm.getHtmlPlugin(mContext,pluginName,new PluginsManagerCallback() {
					
					@Override
					public void onGetPluginSuccess(PluginModel model) {
						File file=new File(model.getFolder(),model.getEntry());
						String uri="file:///android_asset/disclaimer/error_page_404_en.html";
						if(file.exists()){
							uri="file:///"+file.getAbsolutePath();
						}else if(LanguageUtil.isChina()){
							uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
						}	
						
						Intent intent= new Intent();	
						intent.setClass(mContext, Html5PlusWebViewActvity.class);
						intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
						SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, gwID);
						SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, devID);
						mContext.startActivity(intent);
					}

					@Override
					public void onGetPluginFailed(final String hint) {
						if(hint!=null&&hint.length()>0){
							Handler handler=new Handler(Looper.getMainLooper());
							handler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(mContext, hint, Toast.LENGTH_SHORT).show();
								}
							});
						}
					}
				});
			}
		}).start();
	}

}
