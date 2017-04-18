package cc.wulian.app.model.device.impls.controlable.fancoil.setting;

import com.yuantuo.customview.ui.WLDialog;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilUtil;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmUtil;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.SendMessage;

public class EnergySavingFragment extends WulianFragment{

	private final String TAG = getClass().getSimpleName();
	
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private String mEpData;
	
	private DeviceCache cache;
	private AbstractDevice device;
	/**
	 * 返回标志位
	 */
	private String mReturnId;
	/**
	 * 制热节能温度
	 */
	private String mHeatEnergyTemp;
	/**
	 * 制冷节能温度
	 */
	private String mCoolEnergyTemp;

	private LinearLayout energySettingCoolLayout;
	private LinearLayout energySettingHeatLayout;
	private TextView energySettingHeatTempTv;
	private TextView energySettingCoolTempTv;
	private WLDialog tempHeatDialog;
	private EnergySavingView energySavingView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		Bundle bundle = getArguments().getBundle("EnergySavingFragmentInfo");
		mGwId = bundle.getString(FanCoilUtil.GWID);
		mDevId = bundle.getString(FanCoilUtil.DEVID);
		mEp = bundle.getString(FanCoilUtil.EP);
		mEpType = bundle.getString(FanCoilUtil.EPTYPE);
		cache=DeviceCache.getInstance(mActivity);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("");
		getSupportActionBar().setTitle(mActivity.getResources().getString(R.string.AP_enegry_mode));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.device_fancoil_setting_energy, container, false);
		energySettingCoolLayout = (LinearLayout) rootView.findViewById(R.id.fancoil_setting_energy_cool_item);
		energySettingHeatLayout = (LinearLayout) rootView.findViewById(R.id.fancoil_setting_energy_heat_item);
		energySettingHeatTempTv = (TextView) rootView.findViewById(R.id.fancoil_setting_energy_heat_temp_tv);
		energySettingCoolTempTv = (TextView) rootView.findViewById(R.id.fancoil_setting_energy_cool_temp_tv);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		energySettingCoolLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showTempCoolDialog();
			}
		});
		
		energySettingHeatLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				showTempHeatDialog();
			}
		});
	}
	
	@Override
	public void onShow() {
		super.onShow();
		initBar();
	}

	@Override
	public void onResume() {
		super.onResume();
		SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FanCoilUtil.CURRENT_QUERY_CMD_DATA);
		initBar();
	}

	private void showTempHeatDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(mActivity.getResources().getString(R.string.thermostat_fans_hot_default_value));
		builder.setContentView(createTempHeatView());
		builder.setPositiveButton(mActivity.getResources().getString(R.string.common_ok));
		builder.setNegativeButton(mActivity.getResources().getString(R.string.common_cancel));
		builder.setListener(new WLDialog.MessageListener() {
			@Override
			public void onClickPositive(View view) {
				String tempHeat = energySavingView.getSettingTempValue();
				String tempHeatCmd = getTempCmd(tempHeat);
				String tempCoolCmd = FloorWarmUtil.hexStr2Str(mCoolEnergyTemp);
				SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FanCoilUtil.ENERGY_SETTING_CMD+tempHeatCmd+tempCoolCmd);
			}

			@Override
			public void onClickNegative(View view) {

			}
		});
		tempHeatDialog = builder.create();
		tempHeatDialog.show();
	}

	private View createTempHeatView(){
		energySavingView = new EnergySavingView(mActivity);
		String currentTemp = FloorWarmUtil.getTempFormat(FloorWarmUtil.tempFormatDevice(
				FloorWarmUtil.hexStr2Str100(mHeatEnergyTemp)));
		energySavingView.setSettingTempValue(currentTemp);
		return energySavingView;
	}
	private void showTempCoolDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(mActivity.getResources().getString(R.string.thermostat_fans_cold_default_value));
		builder.setContentView(createTempCoolView());
		builder.setPositiveButton(mActivity.getResources().getString(R.string.common_ok));
		builder.setNegativeButton(mActivity.getResources().getString(R.string.common_cancel));
		builder.setListener(new WLDialog.MessageListener() {
			@Override
			public void onClickPositive(View view) {
				String tempCool = energySavingView.getSettingTempValue();
				String tempCoolCmd = getTempCmd(tempCool);
				String tempHeatCmd = FloorWarmUtil.hexStr2Str(mHeatEnergyTemp);
				SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FanCoilUtil.ENERGY_SETTING_CMD+tempHeatCmd+tempCoolCmd);
			}

			@Override
			public void onClickNegative(View view) {

			}
		});
		tempHeatDialog = builder.create();
		tempHeatDialog.show();
	}

	private View createTempCoolView(){
		energySavingView = new EnergySavingView(mActivity);
		String currentTemp = FloorWarmUtil.getTempFormat(FloorWarmUtil.tempFormatDevice(
				FloorWarmUtil.hexStr2Str100(mCoolEnergyTemp)));
		energySavingView.setSettingTempValue(currentTemp);
		return energySavingView;
	}

	private String getTempCmd(String temp){
		String tempCmd = "";
		double tempdou=Double.parseDouble(temp)*100;
		String tempstr=String.valueOf((int)tempdou);
		tempCmd=StringUtil.appendLeft(tempstr, 4, '0');
		return tempCmd;

	}

	private void initTempView(){
		if(!StringUtil.isNullOrEmpty(mHeatEnergyTemp)){
			String heatTemp = FloorWarmUtil.tempFormatDevice(FloorWarmUtil.hexStr2Str100(mHeatEnergyTemp));
			energySettingHeatTempTv.setText(FloorWarmUtil.getTempFormat(heatTemp) + FanCoilUtil.TEMP_UNIT_C);
		}
		if(!StringUtil.isNullOrEmpty(mCoolEnergyTemp)){
			String coolTemp = FloorWarmUtil.tempFormatDevice(FloorWarmUtil.hexStr2Str100(mCoolEnergyTemp));
			energySettingCoolTempTv.setText(FloorWarmUtil.getTempFormat(coolTemp) + FanCoilUtil.TEMP_UNIT_C);
		}
	}

	public void onEventMainThread(DeviceEvent event){
		device=(AbstractDevice) cache.getDeviceByID(mActivity, event.deviceInfo.getGwID(), event.deviceInfo.getDevID());
		mEpData=device.getDeviceInfo().getDevEPInfo().getEpData();
		handleEpData(mEpData);
		initTempView();
	}

	private void handleEpData(String epData) {
		
		if(!StringUtil.isNullOrEmpty(epData)){
			Log.i(TAG+"-epdata", mEpData+"-"+mEpData.length());
			if(epData.length() == 40){
				mReturnId = epData.substring(0, 2);
				mHeatEnergyTemp = epData.substring(32, 36);
				mCoolEnergyTemp = epData.substring(36, 40);
			}
			if(StringUtil.equals(epData.substring(0,2) , FanCoilUtil.ENERGY_STATE_TAG)){
				mReturnId = epData.substring(0,2);
				mHeatEnergyTemp = epData.substring(2,6);
				mCoolEnergyTemp = epData.substring(6,10);
			}
		}
	}
	
	
}
