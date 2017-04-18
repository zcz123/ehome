package cc.wulian.app.model.device.impls.controlable.floorwarm.setting;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.ToastProxy;
import com.yuantuo.customview.ui.WLDialog;

import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmUtil;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.SendMessage;

public class OverTempProtectFragment extends WulianFragment implements View.OnClickListener{

	private final String TAG = getClass().getSimpleName();
	
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private String mEpData;
	private DeviceCache cache;
	private AbstractDevice device;

	private String mReturnId;
	private String overTempProtectState;
	private String overTempProtectTemp;
	private OverTempProtectView overTempProtectView;
	private WLDialog tempSettingDialog;
	private WLDialog overTempNoticeDialog;

	@ViewInject(R.id.floorheating_setting_overtemp_open_layout)
	private LinearLayout stateLayout;
	@ViewInject(R.id.floorheating_setting_overtemp_select_layout)
	private LinearLayout settingLayout;
	@ViewInject(R.id.floorheating_setting_overtemp_open_iv)
	private ImageView stateImage;
	@ViewInject(R.id.floorheating_setting_overtemp_tv)
	private TextView tempSelectTextView;
	@ViewInject(R.id.floorheating_setting_overtemp_temp_tv)
	private TextView tempTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		Bundle bundle = getArguments().getBundle("OverTempProtectFragmentInfo");
		mGwId = bundle.getString(FloorWarmUtil.GWID);
		mDevId = bundle.getString(FloorWarmUtil.DEVID);
		mEp = bundle.getString(FloorWarmUtil.EP);
		mEpType = bundle.getString(FloorWarmUtil.EPTYPE);
		overTempProtectState = bundle.getString("overTempProtectState");
		overTempProtectTemp = bundle.getString("overTempProtectTemp");
		
		cache= DeviceCache.getInstance(mActivity);
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText("");
		getSupportActionBar().setTitle(mApplication.getResources().getString(R.string.AP_over_protect));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.device_floorwarm_setting_overtemp, container, false);
		ViewUtils.inject(this,rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		stateImage.setOnClickListener(this);
		settingLayout.setOnClickListener(this);
	}
	
	@Override
	public void onShow() {
		super.onShow();
		initBar();
		loadData();
	}

	@Override
	public void onResume() {
		super.onResume();
		initBar();
		loadData();
		SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FloorWarmUtil.CURRENT_QUERY_CMD_DATA);
	}

	private void loadData(){
		mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				initSettingItems();
			}
		});
	}
	
	private void initSettingItems(){
		if(!StringUtil.isNullOrEmpty(overTempProtectState)){
			if(StringUtil.equals(overTempProtectState, FloorWarmUtil.STATE_OFF)){
				stateImage.setImageResource(FloorWarmUtil.DRAWABLE_BUTTON_OFF);
				setTempSelectLayoutOff();
			}else if(StringUtil.equals(overTempProtectState, FloorWarmUtil.STATE_ON)){
				stateImage.setImageResource(FloorWarmUtil.DRAWABLE_BUTTON_ON);
				setTempSelectLayoutOn();
			}
		}
		if(!StringUtil.isNullOrEmpty(overTempProtectTemp)){
			String tempText = FloorWarmUtil.hexStr2Str100(overTempProtectTemp);
			tempTextView.setText(FloorWarmUtil.getTempFormat(tempText)+ FloorWarmUtil.TEMP_UNIT_C_TEXT);
		}
	}

	private void setTempSelectLayoutOn(){
		settingLayout.setEnabled(true);
		tempSelectTextView.setTextColor(Color.parseColor("#3e3e3e"));
		tempTextView.setTextColor(Color.parseColor("#3e3e3e"));
	}

	private void setTempSelectLayoutOff(){
		settingLayout.setEnabled(false);
		tempSelectTextView.setTextColor(Color.GRAY);
		tempTextView.setTextColor(Color.GRAY);
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()){

			case R.id.floorheating_setting_overtemp_select_layout:
				showTempSettingDialog();
				break;
			case R.id.floorheating_setting_overtemp_open_iv:
				if(!StringUtil.isNullOrEmpty(overTempProtectState)){
					if(StringUtil.equals(overTempProtectState, FloorWarmUtil.STATE_ON)){
						showOverTempNoticeDialog();
					}else{
						SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FloorWarmUtil.OVER_TEMP_CMD_ON);
					}
				}
				break;
			default:
				break;
		}
	}

	private void showOverTempNoticeDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(mApplication.getResources().getString(R.string.operation_title));
		builder.setMessage(mActivity.getResources().getString(R.string.floor_feating_off_protection));
		builder.setPositiveButton(mActivity.getResources().getString(R.string.common_ok));
		builder.setNegativeButton(mActivity.getResources().getString(R.string.common_cancel));
		builder.setListener(new WLDialog.MessageListener() {
			@Override
			public void onClickPositive(View view) {
				SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FloorWarmUtil.OVER_TEMP_CMD_OFF);
			}

			@Override
			public void onClickNegative(View view) {

			}
		});
		overTempNoticeDialog = builder.create();
		overTempNoticeDialog.show();
	}

	private void showTempSettingDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(mApplication.getResources().getString(R.string.AP_over_protect));
		builder.setContentView(creatDialogView());
		builder.setPositiveButton(mActivity.getResources().getString(R.string.common_ok));
		builder.setNegativeButton(mActivity.getResources().getString(R.string.common_cancel));
		builder.setListener(new WLDialog.MessageListener() {
			@Override
			public void onClickPositive(View view) {
				String temp = overTempProtectView.getSettingTempValue();
				double tempdou = Double.parseDouble(temp) * 100;
				String tempstr = (int) tempdou + "";
				String tempCmd = StringUtil.appendLeft(tempstr, 4, '0');
				if(StringUtil.isNullOrEmpty(overTempProtectState)){
					ToastProxy.makeText(mActivity,mApplication.getResources().getString(R.string.smartLock_setting_fail_hint), ToastProxy.LENGTH_SHORT).show();
					return;
				}
				String sendCmd = FloorWarmUtil.OVER_TEMP_CMD + overTempProtectState.substring(1,2)
						+ tempCmd + FloorWarmUtil.OVER_TEMP_SETTING_TEMP;
				Log.i("overtempfragment","temp:"+temp+",tempCmd:"+tempCmd+",sendCmd:"+sendCmd);
				SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, sendCmd);
			}

			@Override
			public void onClickNegative(View view) {

			}
		});
		tempSettingDialog = builder.create();
		tempSettingDialog.show();
	}

	private View creatDialogView(){
		overTempProtectView = new OverTempProtectView(mActivity);
		String tempText = FloorWarmUtil.hexStr2Str100(overTempProtectTemp);
		overTempProtectView.setSettingTempValue(FloorWarmUtil.getTempFormat(tempText));
		return overTempProtectView;
	}
	
	public void onEventMainThread(DeviceEvent event){
		device=(AbstractDevice) cache.getDeviceByID(mActivity, event.deviceInfo.getGwID(), event.deviceInfo.getDevID());
		mEpData=device.getDeviceInfo().getDevEPInfo().getEpData();
		Log.i(TAG+"-epdata", mEpData+"-"+mEpData.length());
		handleEpData(mEpData);
		initSettingItems();
	}

	private void handleEpData(String epData) {
		
		if(!StringUtil.isNullOrEmpty(epData)){
			if(epData.length() == 46){
				mReturnId = epData.substring(0, 2);
				overTempProtectState = epData.substring(4, 6);
				overTempProtectTemp = epData.substring(6, 10);
			}
			if(StringUtil.equals(epData.substring(0,2), FloorWarmUtil.OVER_TEMP_TAG)){
				mReturnId = epData.substring(0, 2);
				//开关数据
				if(StringUtil.equals(epData.substring(2,4), FloorWarmUtil.STATE_OFF)){
					overTempProtectState = epData.substring(4 , 6);
				}
				//设置温度数据
				if(StringUtil.equals(epData.substring(2,4), FloorWarmUtil.STATE_ON)){
					overTempProtectTemp = epData.substring(6 , 10);
				}
			}
		}
	}



}
