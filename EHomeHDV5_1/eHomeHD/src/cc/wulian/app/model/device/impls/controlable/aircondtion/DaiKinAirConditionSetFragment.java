package cc.wulian.app.model.device.impls.controlable.aircondtion;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.provider.ContactsContract.CommonDataKinds.Im;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.controlable.aircondtion.AirConditionManager.AirConditionDataListener;
import cc.wulian.app.model.device.impls.controlable.thermostat_cooperation.CooArcProgressBar;
import cc.wulian.app.model.device.impls.controlable.thermostat_cooperation.CooMyArcProgressBar;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;

public class DaiKinAirConditionSetFragment extends WulianFragment {

	public static final String GWID = "gwid";
	public static final String DEVICE_ID = "device_id";
	public static final String CUR_AIR_CONDITION_ID = "current_id";
	private DeviceCache deviceCache;
	private WulianDevice device;
	private static String curAirConditionID;
	private String gwID;
	private String devID;
	private String devType;
	private AirConditionManager airConditionManager = AirConditionManager
			.getInstance();
	private AirCondition airCondition;

	// 性能
	boolean isHasAirVolumeAdjust;
	boolean isHasArefactionModel;
	boolean isHasAutoModel;
	boolean isHasBlowModel;
	boolean isHasCoolModel;
	boolean isHasHotModel;
	boolean isHasWindDirectionSet;
	
	
	//控制与显示
	private CooMyArcProgressBar progressBar;
	private Button btSwitch;
	private ImageView wind_direction_1,wind_direction_2,wind_direction_3,wind_direction_4;
	private ImageView wind_volume_1,wind_volume_2,wind_volume_3,wind_volume_4,wind_volume_5;
	private TextView AC_model_1,AC_model_2,AC_model_3,AC_model_4;
	private Button getTemp;
	private TextView setTemp;
	private int windPowerBefore;
	private int windPowerAfter;
	private int windDirection;
	private int ACModel;
	
	
	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (airCondition != null && airCondition.getCurStadus() != null
					&& airCondition.getCurWindDirection() != null
					&& airCondition.getCurWindPower() != null
					&& airCondition.getCurModel() != null
					&& airCondition.getCurSetTemp() != null
					&& airCondition.getCurTemp() != null
					&& airCondition.getCurWindSpeed() != null) {
				// 42001data
				String curSwitchStatus = airCondition.getCurStadus();
				String curWindDirection = airCondition.getCurWindDirection();
				String curWindpower = airCondition.getCurWindPower();
				// 42002data
				String curModel = airCondition.getCurModel();
				
				if(arg0 == wind_volume_1){
					windPowerAfter = 1;
					if(isHasAirVolumeAdjust){
						if(windPowerAfter<windPowerBefore){
							sendControlReduceWindSpeedData(curSwitchStatus,curWindDirection, curWindpower);
						}else if(windPowerAfter>windPowerBefore){
							sendControlAddWindSpeedData(curSwitchStatus,curWindDirection, curWindpower);
						}else{
							
						}
					}else{
						Toast.makeText(mActivity, "该设备没有风量调节功能!",Toast.LENGTH_SHORT).show();
					}
				}else if(arg0 == wind_volume_2){
					windPowerAfter = 2;
					if(isHasAirVolumeAdjust){
						if(windPowerAfter<windPowerBefore){
							sendControlReduceWindSpeedData(curSwitchStatus,curWindDirection, curWindpower);
						}else if(windPowerAfter>windPowerBefore){
							sendControlAddWindSpeedData(curSwitchStatus,curWindDirection, curWindpower);
						}else{
							
						}
					}else{
						Toast.makeText(mActivity, "该设备没有风量调节功能!",Toast.LENGTH_SHORT).show();
					}
				}else if(arg0 == wind_volume_3){
					windPowerAfter = 3;
					if(isHasAirVolumeAdjust){
						if(windPowerAfter<windPowerBefore){
							sendControlReduceWindSpeedData(curSwitchStatus,curWindDirection, curWindpower);
						}else if(windPowerAfter>windPowerBefore){
							sendControlAddWindSpeedData(curSwitchStatus,curWindDirection, curWindpower);
						}else{
							
						}
					}else{
						Toast.makeText(mActivity, "该设备没有风量调节功能!",Toast.LENGTH_SHORT).show();
					}
				}else if(arg0 == wind_volume_4){
					windPowerAfter = 4;
					if(isHasAirVolumeAdjust){
						if(windPowerAfter<windPowerBefore){
							sendControlReduceWindSpeedData(curSwitchStatus,curWindDirection, curWindpower);
						}else if(windPowerAfter>windPowerBefore){
							sendControlAddWindSpeedData(curSwitchStatus,curWindDirection, curWindpower);
						}else{
							
						}
					}else{
						Toast.makeText(mActivity, "该设备没有风量调节功能!",Toast.LENGTH_SHORT).show();
					}
				}else if(arg0 == wind_volume_5){
					windPowerAfter = 5;
					if(isHasAirVolumeAdjust){
						if(windPowerAfter<windPowerBefore){
							sendControlReduceWindSpeedData(curSwitchStatus,curWindDirection, curWindpower);
						}else if(windPowerAfter>windPowerBefore){
							sendControlAddWindSpeedData(curSwitchStatus,curWindDirection, curWindpower);
						}else{
							
						}
					}else{
						Toast.makeText(mActivity, "该设备没有风量调节功能!",Toast.LENGTH_SHORT).show();
					}
				}else if(arg0 == wind_direction_1){
					if (isHasWindDirectionSet) {
						windDirection = 1;
						sendControlWindDirectionData(curSwitchStatus,curWindDirection, curWindpower);
					} else {
						Toast.makeText(mActivity, "该设备没有风向设置功能!",
								Toast.LENGTH_SHORT).show();
					}
				}else if(arg0 == wind_direction_2){
					if (isHasWindDirectionSet) {
						windDirection = 2;
						sendControlWindDirectionData(curSwitchStatus,curWindDirection, curWindpower);
					} else {
						Toast.makeText(mActivity, "该设备没有风向设置功能!",
								Toast.LENGTH_SHORT).show();
					}
				}else if(arg0 == wind_direction_3){
					if (isHasWindDirectionSet) {
						windDirection = 3;
						sendControlWindDirectionData(curSwitchStatus,curWindDirection, curWindpower);
					} else {
						Toast.makeText(mActivity, "该设备没有风向设置功能!",
								Toast.LENGTH_SHORT).show();
					}
				}else if(arg0 == wind_direction_4){
					if (isHasWindDirectionSet) {
						windDirection = 4;
						sendControlWindDirectionData(curSwitchStatus,curWindDirection, curWindpower);
					} else {
						Toast.makeText(mActivity, "该设备没有风向设置功能!",
								Toast.LENGTH_SHORT).show();
					}
				}else if(arg0 == AC_model_1){
					ACModel = 1;
					sendControlModelData(curModel);
				}else if(arg0 == AC_model_2){
					ACModel = 2;
					sendControlModelData(curModel);
				}else if(arg0 == AC_model_3){
					ACModel = 3;
					sendControlModelData(curModel);
				}else if(arg0 == AC_model_4){
					ACModel = 4;
					sendControlModelData(curModel);
				}else if (arg0 == btSwitch) {
					sendControlSwitchData(curSwitchStatus, curWindDirection,
							curWindpower);
				}
			} else {
				Toast.makeText(mActivity, "正在初始化, 请稍等...", Toast.LENGTH_SHORT)
						.show();
			}
		}
	};

	/**
	 * 监听设备refreshDevice中的epData
	 */

	private AirConditionDataListener listener = new AirConditionDataListener() {

		@Override
		public void onAirDataChanged(String airID) {
			if (!StringUtil.isNullOrEmpty(airID)
					&& airID.equals(curAirConditionID)) {
				if (airCondition != null && airCondition.getCurStadus() != null
						&& airCondition.getCurWindDirection() != null
						&& airCondition.getCurWindPower() != null
						&& airCondition.getCurModel() != null
						&& airCondition.getCurSetTemp() != null
						&& airCondition.getCurTemp() != null
						&& airCondition.getCurWindSpeed() != null) {
					/**
					 * 性能信息判断
					 */
					isHasAirVolumeAdjust = airCondition.isHasAirVolumeAdjust();
					isHasArefactionModel = airCondition.isHasArefactionModel();
					isHasAutoModel = airCondition.isHasAutoModel();
					isHasBlowModel = airCondition.isHasBlowModel();
					isHasCoolModel = airCondition.isHasCoolModel();
					isHasHotModel = airCondition.isHasHotModel();
					isHasWindDirectionSet = airCondition.isHasWindDirectionSet();

					/**
					 * 状态信息判断
					 */
					// 开关状态
					if (airCondition.getCurStadus().equals("0")) {
						btSwitch.setText(getResources().getString(R.string.default_progress_on));
						progressBar.setClickable(false);
						progressBar.setBackgroundResource(R.drawable.device_thermost_temp_bg_1);
					} else {
						btSwitch.setText(getResources().getString(R.string.default_progress_off));
						progressBar.setBackgroundResource(R.drawable.device_thermost_temp_bg_2);
					}
					/**
					 * 风向
					 */
					if (airCondition.getCurWindDirection().equals("000")) {
						// 水平
						wind_direction_1.setImageResource(R.drawable.icon_daikin_wind_right_1);
						wind_direction_2.setImageResource(R.drawable.icon_daikin_wind_right45);
						wind_direction_3.setImageResource(R.drawable.icon_daikin_wind_blew);
						wind_direction_4.setImageResource(R.drawable.icon_daikin_wind_auto);
					} else if (airCondition.getCurWindDirection().equals("010")
							|| airCondition.getCurWindDirection().equals("001")
							|| airCondition.getCurWindDirection().equals("011")) {
						// 45°
						wind_direction_1.setImageResource(R.drawable.icon_daikin_wind_right);
						wind_direction_2.setImageResource(R.drawable.icon_daikin_wind_right45_1);
						wind_direction_3.setImageResource(R.drawable.icon_daikin_wind_blew);
						wind_direction_4.setImageResource(R.drawable.icon_daikin_wind_auto);
					} else if (airCondition.getCurWindDirection().equals("100")) {
						// 垂直
						wind_direction_1.setImageResource(R.drawable.icon_daikin_wind_right);
						wind_direction_2.setImageResource(R.drawable.icon_daikin_wind_right45);
						wind_direction_3.setImageResource(R.drawable.icon_daikin_wind_blew_1);
						wind_direction_4.setImageResource(R.drawable.icon_daikin_wind_auto);
					} else if (airCondition.getCurWindDirection().equals("111")) {
						// 摆动
						wind_direction_1.setImageResource(R.drawable.icon_daikin_wind_right);
						wind_direction_2.setImageResource(R.drawable.icon_daikin_wind_right45);
						wind_direction_3.setImageResource(R.drawable.icon_daikin_wind_blew);
						wind_direction_4.setImageResource(R.drawable.icon_daikin_wind_auto_1);
					}
					/**
					 * 风量(根据室内机性能不同,风量的风速设定不同)
					 */
					if (airCondition.getCurWindSpeed().equals("1")
							&& !StringUtil.isNullOrEmpty(airCondition
									.getCurWindPower())) {
						wind_volume_1.setImageResource(R.drawable.icon_daikin_wind_util_1);
						wind_volume_2.setImageResource(R.drawable.icon_daikin_wind_util_1);
						wind_volume_3.setImageResource(R.drawable.icon_daikin_wind_util_1);
						wind_volume_4.setImageResource(R.drawable.icon_daikin_wind_util_1);
						wind_volume_5.setImageResource(R.drawable.icon_daikin_wind_util_2);
						windPowerBefore = 4;
					} else if (airCondition.getCurWindSpeed().equals("2")) {
						if (airCondition.getCurWindPower().equals("001")) {
							wind_volume_1.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_2.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_3.setImageResource(R.drawable.icon_daikin_wind_util_2);
							wind_volume_4.setImageResource(R.drawable.icon_daikin_wind_util_2);
							wind_volume_5.setImageResource(R.drawable.icon_daikin_wind_util_2);
							windPowerBefore = 2;
						} else if (airCondition.getCurWindPower().equals("101")) {
							wind_volume_1.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_2.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_3.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_4.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_5.setImageResource(R.drawable.icon_daikin_wind_util_2);
							windPowerBefore = 4;
						}
					} else if (airCondition.getCurWindSpeed().equals("3")) {
						if (airCondition.getCurWindPower().equals("001")) {
							wind_volume_1.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_2.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_3.setImageResource(R.drawable.icon_daikin_wind_util_2);
							wind_volume_4.setImageResource(R.drawable.icon_daikin_wind_util_2);
							wind_volume_5.setImageResource(R.drawable.icon_daikin_wind_util_2);
							windPowerBefore = 2;
						} else if (airCondition.getCurWindPower().equals("011")) {
							wind_volume_1.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_2.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_3.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_4.setImageResource(R.drawable.icon_daikin_wind_util_2);
							wind_volume_5.setImageResource(R.drawable.icon_daikin_wind_util_2);
							windPowerBefore = 3;
						} else if (airCondition.getCurWindPower().equals("101")) {
							wind_volume_1.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_2.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_3.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_4.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_5.setImageResource(R.drawable.icon_daikin_wind_util_2);
							windPowerBefore = 4;
						}
					} else if (airCondition.getCurWindSpeed().equals("5")) {
						if (airCondition.getCurWindPower().equals("001")) {
							wind_volume_1.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_2.setImageResource(R.drawable.icon_daikin_wind_util_2);
							wind_volume_3.setImageResource(R.drawable.icon_daikin_wind_util_2);
							wind_volume_4.setImageResource(R.drawable.icon_daikin_wind_util_2);
							wind_volume_5.setImageResource(R.drawable.icon_daikin_wind_util_2);
							windPowerBefore = 1;
						} else if (airCondition.getCurWindPower().equals("010")) {
							wind_volume_1.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_2.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_3.setImageResource(R.drawable.icon_daikin_wind_util_2);
							wind_volume_4.setImageResource(R.drawable.icon_daikin_wind_util_2);
							wind_volume_5.setImageResource(R.drawable.icon_daikin_wind_util_2);
							windPowerBefore = 2;
						} else if (airCondition.getCurWindPower().equals("011")) {
							wind_volume_1.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_2.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_3.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_4.setImageResource(R.drawable.icon_daikin_wind_util_2);
							wind_volume_5.setImageResource(R.drawable.icon_daikin_wind_util_2);
							windPowerBefore = 3;
						} else if (airCondition.getCurWindPower().equals("100")) {
							wind_volume_1.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_2.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_3.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_4.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_5.setImageResource(R.drawable.icon_daikin_wind_util_2);
							windPowerBefore = 4;
						} else if (airCondition.getCurWindPower().equals("101")) {
							wind_volume_1.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_2.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_3.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_4.setImageResource(R.drawable.icon_daikin_wind_util_1);
							wind_volume_5.setImageResource(R.drawable.icon_daikin_wind_util_1);
							windPowerBefore = 5;
						}

					}
					/**
					 * 运行模式
					 */
					// 送风
					if (airCondition.getCurModel().equals("0000")) {
						AC_model_1.setBackgroundResource(0);
						AC_model_2.setBackgroundResource(0);
						AC_model_3.setBackgroundResource(0);
						AC_model_4.setBackgroundResource(R.drawable.icon_oval_bg);
						AC_model_1.setTextColor(getResources().getColor(R.color.white));
						AC_model_2.setTextColor(getResources().getColor(R.color.white));
						AC_model_3.setTextColor(getResources().getColor(R.color.white));
						AC_model_4.setTextColor(getResources().getColor(R.color.green));
					// 制热
					} else if (airCondition.getCurModel().equals("0001")) {
						AC_model_1.setBackgroundResource(R.drawable.icon_oval_bg);
						AC_model_2.setBackgroundResource(0);
						AC_model_3.setBackgroundResource(0);
						AC_model_4.setBackgroundResource(0);
						AC_model_1.setTextColor(getResources().getColor(R.color.green));
						AC_model_2.setTextColor(getResources().getColor(R.color.white));
						AC_model_3.setTextColor(getResources().getColor(R.color.white));
						AC_model_4.setTextColor(getResources().getColor(R.color.white));
					// 制冷
					} else if (airCondition.getCurModel().equals("0010")) {
						AC_model_1.setBackgroundResource(0);
						AC_model_2.setBackgroundResource(R.drawable.icon_oval_bg);
						AC_model_3.setBackgroundResource(0);
						AC_model_4.setBackgroundResource(0);
						AC_model_1.setTextColor(getResources().getColor(R.color.white));
						AC_model_2.setTextColor(getResources().getColor(R.color.green));
						AC_model_3.setTextColor(getResources().getColor(R.color.white));
						AC_model_4.setTextColor(getResources().getColor(R.color.white));
					// 自动
					} else if (airCondition.getCurModel().equals("0011")) {
						AC_model_1.setBackgroundResource(0);
						AC_model_2.setBackgroundResource(0);
						AC_model_3.setBackgroundResource(0);
						AC_model_4.setBackgroundResource(0);
						AC_model_1.setTextColor(getResources().getColor(R.color.white));
						AC_model_2.setTextColor(getResources().getColor(R.color.white));
						AC_model_3.setTextColor(getResources().getColor(R.color.white));
						AC_model_4.setTextColor(getResources().getColor(R.color.white));
					// 除湿
					} else if (airCondition.getCurModel().equals("0111")) {
						AC_model_1.setBackgroundResource(0);
						AC_model_2.setBackgroundResource(0);
						AC_model_3.setBackgroundResource(R.drawable.icon_oval_bg);
						AC_model_4.setBackgroundResource(0);
						AC_model_1.setTextColor(getResources().getColor(R.color.white));
						AC_model_2.setTextColor(getResources().getColor(R.color.white));
						AC_model_3.setTextColor(getResources().getColor(R.color.green));
						AC_model_4.setTextColor(getResources().getColor(R.color.white));
					}

					/**
					 * 此处根据状态返回数据解析判断该设备是否有冷热选择权
					 */
					if (airCondition.getStatusData_2() != null) {
						String permissionHotAndCoolData = airCondition
								.getStatusData_2().substring(0, 2);
						if (permissionHotAndCoolData.equals("10")
								|| permissionHotAndCoolData.equals("00")) {
							AC_model_1.setClickable(true);
							AC_model_2.setClickable(true);
							AC_model_3.setClickable(true);
							AC_model_4.setClickable(true);
						} else {
							if(airCondition.getCurModel().equals("0001")){
								AC_model_1.setClickable(true);
								AC_model_2.setClickable(false);
								AC_model_3.setClickable(false);
								AC_model_4.setClickable(true);
							}else if(airCondition.getCurModel().equals("0010")){
								AC_model_1.setClickable(false);
								AC_model_2.setClickable(true);
								AC_model_3.setClickable(true);
								AC_model_4.setClickable(true);
							}
						}

					}
					/**
					 * 设定温度
					 */
					StringBuilder sbSetTemp = new StringBuilder();
					sbSetTemp.append(getResources().getString(R.string.device_set_tempure));
					sbSetTemp.append(airCondition.getCurSetTemp());
					sbSetTemp.append("°C");
					setTemp.setText(sbSetTemp);
					/**
					 * 室内温度
					 */
					StringBuilder sbTemp = new StringBuilder();
					sbTemp.append(airCondition.getCurTemp());
					sbTemp.append("°C");
					getTemp.setText(sbTemp);

				} else {
					btSwitch.setText(getResources().getString(R.string.default_progress_off));
					progressBar.setBackgroundResource(R.drawable.device_thermost_temp_bg_2);
					Toast.makeText(mActivity, "设备已准备好!", Toast.LENGTH_SHORT).show();
				}
			}

		}

	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		Bundle bundle = getArguments();
		gwID = bundle.getString(GWID);
		devID = bundle.getString(DEVICE_ID);
		curAirConditionID = bundle.getString(CUR_AIR_CONDITION_ID);
		deviceCache = DeviceCache.getInstance(mActivity);
		device = deviceCache.getDeviceByID(mActivity, gwID, devID);
		devType = device.getDeviceType();
		airCondition = airConditionManager
				.getAirConditionByID(curAirConditionID);
		AirConditionManager.addDataListener(listener);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_daikin_ac_set, container,
				false);
	}

	@Override
	public void onViewCreated(View paramView, Bundle paramBundle) {
		super.onViewCreated(paramView, paramBundle);

		initView(paramView);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		AirConditionManager.removeDataListener(listener);
	}
	
	private void initBar() {
		this.mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setIconText(R.string.nav_device_title);
		getSupportActionBar().setTitle(
				mApplication.getResources().getString(
						R.string.daikin_airconditioner));
	}

	private void initView(View paramView) {
		progressBar = ((CooMyArcProgressBar) paramView.findViewById(R.id.device_daikinac_custom_arcprogressbar));
		btSwitch = (Button) paramView.findViewById(R.id.device_daikinac_custom_switch);
		wind_direction_1 = (ImageView) paramView.findViewById(R.id.iv_wind_level);
		wind_direction_2 = (ImageView) paramView.findViewById(R.id.iv_wind_45);
		wind_direction_3 = (ImageView) paramView.findViewById(R.id.iv_wind_below);
		wind_direction_4 = (ImageView) paramView.findViewById(R.id.iv_wind_45xh);
		wind_volume_1 = (ImageView) paramView.findViewById(R.id.iv_wind_volume_1);
		wind_volume_2 = (ImageView) paramView.findViewById(R.id.iv_wind_volume_2);
		wind_volume_3 = (ImageView) paramView.findViewById(R.id.iv_wind_volume_3);
		wind_volume_4 = (ImageView) paramView.findViewById(R.id.iv_wind_volume_4);
		wind_volume_5 = (ImageView) paramView.findViewById(R.id.iv_wind_volume_5);
		AC_model_1 = (TextView) paramView.findViewById(R.id.tv_model_heating);
		AC_model_2 = (TextView) paramView.findViewById(R.id.tv_model_refrigeration);
		AC_model_3 = (TextView) paramView.findViewById(R.id.tv_model_dehumidification);
		AC_model_4 = (TextView) paramView.findViewById(R.id.tv_model_blowing);
		getTemp = (Button) paramView.findViewById(R.id.device_daikinac_custom_get_temp);
		setTemp = (TextView) paramView.findViewById(R.id.device_daikinac_custom_set_temp);
		
		btSwitch.setOnClickListener(mOnClickListener);
		wind_volume_1.setOnClickListener(mOnClickListener);
		wind_volume_2.setOnClickListener(mOnClickListener);
		wind_volume_3.setOnClickListener(mOnClickListener);
		wind_volume_4.setOnClickListener(mOnClickListener);
		wind_volume_5.setOnClickListener(mOnClickListener);
		wind_direction_1.setOnClickListener(mOnClickListener);
		wind_direction_2.setOnClickListener(mOnClickListener);
		wind_direction_3.setOnClickListener(mOnClickListener);
		wind_direction_4.setOnClickListener(mOnClickListener);
		AC_model_1.setOnClickListener(mOnClickListener);
		AC_model_2.setOnClickListener(mOnClickListener);
		AC_model_3.setOnClickListener(mOnClickListener);
		AC_model_4.setOnClickListener(mOnClickListener);
		progressBar.setProcess(16);
		
		progressBar
				.setOnUpViewValueChanged(new CooArcProgressBar.OnUpViewValueChanged() {
					public void onUpChanged(int paramInt) {
						String str = String.valueOf(paramInt + 16);
						setTemp.setText(getResources().getString(
								R.string.device_set_tempure)
								+ str + "°C");
						if (!airCondition.getCurStadus().equals("0")) {
							int setNewTemp = (Integer.parseInt(str)) * 10;
							String dataNewTemp = Integer.toHexString(setNewTemp);
							sendControlData(getAirConditionControlAddress3(),
									StringUtil.appendLeft(dataNewTemp, 4, '0'));
						}
					}
				});
		progressBar
				.setOnMoveViewValueChanged(new CooArcProgressBar.OnMoveViewValueChanged() {
					public void onMoveChanged(int paramInt) {
						setTemp.setText(getResources().getString(
								R.string.device_set_tempure)
								+ (paramInt + 16) + "°C");
					}
				});
		sendPerformanceData(getAirConditionPerformanceAddress());
		sendStatusData(getAirConditionStatusAddress());
	}
	
	/**
	 * 获取查询性能地址
	 * 
	 * @return
	 */
	public String getAirConditionPerformanceAddress() {
		int[] airPerformanceAddress = DaikinChangeDataAndAddress
				.getAirPerformanceAddress(curAirConditionID);
		String performance = String.valueOf(airPerformanceAddress[0] - 30001);
		String performanceHexAddress = Integer.toHexString(Integer
				.parseInt(performance));
		String performanceAddress = StringUtil.appendLeft(
				performanceHexAddress, 4, '0');
		return performanceAddress;
	}
	
	/**
	 * 获取查询状态地址
	 * 
	 * @return
	 */
	public String getAirConditionStatusAddress() {
		int[] airConditionStatusAddress = DaikinChangeDataAndAddress
				.getAirStatusAddress(curAirConditionID);
		String status = String.valueOf(airConditionStatusAddress[0] - 30001);
		String statusHexAddress = Integer.toHexString(Integer.parseInt(status));
		String statusAddress = StringUtil.appendLeft(statusHexAddress, 4, '0');
		return statusAddress;
	}
	
	/**
	 * 控制命令为单个保持寄存器
	 * 
	 * @return 开关、风向、风量的控制地址
	 */
	public String getAirConditionControlAddress1() {
		int[] airConditionControlAddress = DaikinChangeDataAndAddress
				.getAirControlAddress(curAirConditionID);
		String control = String.valueOf(airConditionControlAddress[0] - 40001);
		String controlHexAddress = Integer.toHexString(Integer
				.parseInt(control));
		String controlAddress = StringUtil
				.appendLeft(controlHexAddress, 4, '0');
		return controlAddress;
	}

	/**
	 * 填充开关、风向、风量数据
	 * 
	 * @param switchStatus
	 * @param windDirection
	 * @param windPower
	 * @return
	 */
	public String getCompleteHexData_1(String switchStatus,
			String windDirection, String windPower) {
		String byte2Data = "";
		String defaultData = "0110";
		// 整体数据
		String curStatusData_1 = airCondition.getStatusData_1();

		String curControlData_1 = airCondition.getControlData_1();

		if (airCondition.getStatusData_1() != null) {
			if (curControlData_1 != null) {
				// 控制数据之间未做处理的数据
				// 开关和风向之间未作处理的的数据
				String curControlDataS_WD = curControlData_1.substring(12, 15);
				// 风向和风量之间未做处理的数据
				String curControlDataWD_WP = curControlData_1.substring(4, 5);
				// 风量之前未做处理的数据
				String curControData_WP = curControlData_1.substring(0, 1);
				byte2Data = curControData_WP + windPower + curControlDataWD_WP
						+ windDirection + defaultData + curControlDataS_WD
						+ switchStatus;
			} else {
				// 状态数据之间未做处理的数据
				// 开关和风向之间未作处理的的数据
				String curStatusDataS_WD = curStatusData_1.substring(12, 15);
				// 风向和风量之间未做处理的数据
				String curStatusDataWD_WP = curStatusData_1.substring(4, 5);
				// 风量之前未做处理的数据
				String curStatusData_WP = curStatusData_1.substring(0, 1);
				byte2Data = curStatusData_WP + windPower + curStatusDataWD_WP
						+ windDirection + defaultData + curStatusDataS_WD
						+ switchStatus;
			}
		}
		int i = Integer.parseInt(byte2Data, 2);
		String hexData = Integer.toHexString(i);
		return hexData;
	}

	/**
	 * 填充控制运转模式数据
	 * 
	 * @param model
	 * @return
	 */
	public String getCompleteHexData_2(String model) {
		String byte2Data = "";
		String curStatusData_2 = airCondition.getStatusData_2();
		String curControlData_2 = airCondition.getControlData_2();
		if (airCondition.getStatusData_2() != null) {
			if (curControlData_2 != null) {
				// 运转状态之前未做处理的数据
				String curControlData_M = curControlData_2.substring(0, 12);
				byte2Data = curControlData_M + model;
			} else {
				// 运转状态之前未做处理的数据
				String curStatusData_M = curStatusData_2.substring(0, 12);
				byte2Data = curStatusData_M + model;
			}
		}
		int i = Integer.parseInt(byte2Data, 2);
		String hexData = Integer.toHexString(i);
		return hexData;
	}

	/**
	 * 
	 * 
	 * @return 运转模式等控制地址
	 */
	public String getAirConditionControlAddress2() {
		int[] airConditionControlAddress = DaikinChangeDataAndAddress
				.getAirControlAddress(curAirConditionID);
		String control = String.valueOf(airConditionControlAddress[1] - 40001);
		String controlHexAddress = Integer.toHexString(Integer
				.parseInt(control));
		String controlAddress = StringUtil
				.appendLeft(controlHexAddress, 4, '0');
		return controlAddress;
	}

	/**
	 * 
	 * @return 设定温度的控制地址
	 */
	public String getAirConditionControlAddress3() {
		int[] airConditionControlAddress = DaikinChangeDataAndAddress
				.getAirControlAddress(curAirConditionID);
		String control = String.valueOf(airConditionControlAddress[2] - 40001);
		String controlHexAddress = Integer.toHexString(Integer
				.parseInt(control));
		String controlAddress = StringUtil
				.appendLeft(controlHexAddress, 4, '0');
		return controlAddress;
	}

	/**
	 * 加大风速
	 * 
	 * @param curSwitchStatus
	 * @param curWindDirection
	 * @param curWindpower
	 */
	public void sendControlAddWindSpeedData(String curSwitchStatus,
			String curWindDirection, String curWindpower) {
		String sendEpData = curWindpower;
		if (airCondition.getCurWindSpeed().equals("7")
				|| airCondition.getCurWindSpeed().equals("1")) {
			Toast.makeText(mActivity, "该设备没有风量调节功能!", Toast.LENGTH_SHORT)
					.show();
		} else if (airCondition.getCurWindSpeed().equals("2")) {
			if (curWindpower.equals("001")) {
				sendEpData = "101";
			}
		} else if (airCondition.getCurWindSpeed().equals("3")) {
			if (curWindpower.equals("001")) {
				sendEpData = "011";
			} else if (curWindpower.equals("011")) {
				sendEpData = "101";
			}

		} else if (airCondition.getCurWindSpeed().equals("5")) {
			if (curWindpower.equals("001")) {
				sendEpData = "010";
			} else if (curWindpower.equals("010")) {
				sendEpData = "011";
			} else if (curWindpower.equals("011")) {
				sendEpData = "100";
			} else if (curWindpower.equals("100")) {
				sendEpData = "101";
			}
		}
		String hexData = getCompleteHexData_1(curSwitchStatus,
				curWindDirection, sendEpData);
		sendControlData(getAirConditionControlAddress1(),
				StringUtil.appendLeft(hexData, 4, '0'));

	}

	/**
	 * 减小风速
	 * 
	 * @param curSwitchStatus
	 * @param curWindDirection
	 * @param curWindpower
	 */
	public void sendControlReduceWindSpeedData(String curSwitchStatus,
			String curWindDirection, String curWindpower) {
		String sendEpData = curWindpower;
		if (airCondition.getCurWindSpeed().equals("7")
				|| airCondition.getCurWindSpeed().equals("1")) {
			Toast.makeText(mActivity, "该设备没有风量调节功能!", Toast.LENGTH_SHORT)
					.show();
		} else if (airCondition.getCurWindSpeed().equals("2")) {
			if (curWindpower.equals("101")) {
				sendEpData = "001";
			}
		} else if (airCondition.getCurWindSpeed().equals("3")) {
			if (curWindpower.equals("011")) {
				sendEpData = "001";
			} else if (curWindpower.equals("101")) {
				sendEpData = "011";
			}
		} else if (airCondition.getCurWindSpeed().equals("5")) {
			if (curWindpower.equals("010")) {
				sendEpData = "001";
			} else if (curWindpower.equals("011")) {
				sendEpData = "010";
			} else if (curWindpower.equals("100")) {
				sendEpData = "011";
			} else if (curWindpower.equals("101")) {
				sendEpData = "100";
			}
		}
		String hexData = getCompleteHexData_1(curSwitchStatus,
				curWindDirection, sendEpData);
		sendControlData(getAirConditionControlAddress1(),
				StringUtil.appendLeft(hexData, 4, '0'));

	}

	/**
	 * 控制风向(便于控制,此处将所有介于垂直与水平之间的角度设置为011)
	 * 
	 * @param curSwitchStatus
	 * @param curWindDirection
	 * @param curWindpower
	 */
	public void sendControlWindDirectionData(String curSwitchStatus,
			String curWindDirection, String curWindpower) {
		String sendEpData = "";
		
		switch (windDirection) {
		case 1:
			sendEpData = "000";
			break;
		case 2:
			sendEpData = "011";
			break;
		case 3:
			sendEpData = "100";
			break;
		case 4:
			sendEpData = "111";
			break;
		}
		String hexData = getCompleteHexData_1(curSwitchStatus, sendEpData,
				curWindpower);
		sendControlData(getAirConditionControlAddress1(),
				StringUtil.appendLeft(hexData, 4, '0'));
	}

	/**
	 * 控制模式
	 * 
	 * @param curModelData
	 */
	public void sendControlModelData(String curModelData) {
		String sendEpData = "";
		String curStatusData_2 = airCondition.getStatusData_2();
		String permissionHotAndCool = curStatusData_2.substring(0, 2);
		String runStateData = curStatusData_2.substring(4, 8);
		// 有冷热选择权 所有模式都可切换
		if (permissionHotAndCool.equals("10")|| permissionHotAndCool.equals("00")) {
			switch (ACModel) {
			case 1:
				sendEpData = "0001";
				break;
			case 2:
				sendEpData = "0010";
				break;
			case 3:
				sendEpData = "0111";
				break;
			case 4:
				sendEpData = "0000";
				break;
			}
			/**
			 * 没有冷热选择权,判断当前运转状态。 1、运转状态为送风时,模式只能是送风。 2、运转模式为制热时,模式只有选择送风、制热。
			 * 3、运转模式为制冷是,模式只有送风、制冷、除湿。
			 */
		} else if (permissionHotAndCool.equals("01")) {

			if (runStateData.equals("0000")) {
				sendEpData = "0000";
			} else if (runStateData.equals("0001")) {
				switch (ACModel) {
				case 1:
					sendEpData = "0001";
					break;
				case 4:
					sendEpData = "0000";
					break;
				}
			} else if (runStateData.equals("0010")) {
				switch (ACModel) {
				case 2:
					sendEpData = "0010";
					break;
				case 3:
					sendEpData = "0111";
					break;
				case 4:
					sendEpData = "0000";
					break;
				}
			}

		}
		String hexData = getCompleteHexData_2(sendEpData);
		sendControlData(getAirConditionControlAddress2(),
				StringUtil.appendLeft(hexData, 4, '0'));
	}

	/**
	 * 控制开关
	 * 
	 * @param curSwitchData
	 * @param curWindDirection
	 * @param curWindpower
	 */
	public void sendControlSwitchData(String curSwitchData,
			String curWindDirection, String curWindpower) {
		String sendEpData = curSwitchData;
		if (curSwitchData.equals("0")) {
			sendEpData = "1";
		} else if (curSwitchData.equals("1")) {
			sendEpData = "0";
		}
		String hexData = getCompleteHexData_1(sendEpData, curWindDirection,
				curWindpower);
		sendControlData(getAirConditionControlAddress1(),
				StringUtil.appendLeft(hexData, 4, '0'));
	}

	
	
	/**
	 * 查询性能信息
	 * 
	 * @param performanceAddress
	 */
	public void sendPerformanceData(String performanceAddress) {
		String strCRC = DaikinChangeDataAndAddress.CMD_DAIKIN_QUERY_04
				+ performanceAddress
				+ DaikinChangeDataAndAddress.REGISTER_NUMBER_0003;
		byte[] crcData = DaikinChangeDataAndAddress.hexStr2ByteArray(strCRC);
		int crc = CRC16_Check.calcCrc16(crcData);
		String CRC = String.format("%04x", crc).toUpperCase();
		String hCRC = CRC.substring(0, 2);
		String lCRC = CRC.substring(2, 4);
		String performanceData = DaikinChangeDataAndAddress.CMD_DAIKIN_QUERY_04
				+ performanceAddress.toUpperCase()
				+ DaikinChangeDataAndAddress.REGISTER_NUMBER_0003 + lCRC + hCRC;
		String qdLength = StringUtil.appendLeft(performanceData.length() / 2
				+ "", 2, '0');
		NetSDK.sendControlDevMsg(gwID, devID, "14", devType, "1" + qdLength
				+ performanceData);
	}

	/**
	 * 查询状态信息
	 * 
	 * @param statusAddress
	 */
	public void sendStatusData(String statusAddress) {
		String strCRC = DaikinChangeDataAndAddress.CMD_DAIKIN_QUERY_04
				+ statusAddress
				+ DaikinChangeDataAndAddress.REGISTER_NUMBER_0006;
		byte[] crcData = DaikinChangeDataAndAddress.hexStr2ByteArray(strCRC);
		int crc = CRC16_Check.calcCrc16(crcData);
		String CRC = String.format("%04x", crc).toUpperCase();
		String hCRC = CRC.substring(0, 2);
		String lCRC = CRC.substring(2, 4);
		String statusData = DaikinChangeDataAndAddress.CMD_DAIKIN_QUERY_04
				+ statusAddress.toUpperCase()
				+ DaikinChangeDataAndAddress.REGISTER_NUMBER_0006 + lCRC + hCRC;
		String qdLength = StringUtil.appendLeft(statusData.length() / 2 + "",
				2, '0');
		NetSDK.sendControlDevMsg(gwID, devID, "14", devType, "1" + qdLength
				+ statusData);
	}
	
	/**
	 * 发送控制命令
	 * 
	 * @param controlAddress
	 * @param data
	 */
	public void sendControlData(String controlAddress, String data) {
		String strCRC = DaikinChangeDataAndAddress.CMD_DAIKIN_CONTROL_06
				+ controlAddress + data;
		byte[] crcData = DaikinChangeDataAndAddress.hexStr2ByteArray(strCRC);
		int crc = CRC16_Check.calcCrc16(crcData);
		String CRC = String.format("%04x", crc).toUpperCase();
		String hCRC = CRC.substring(0, 2);
		String lCRC = CRC.substring(2, 4);
		String controlData = DaikinChangeDataAndAddress.CMD_DAIKIN_CONTROL_06
				+ controlAddress.toUpperCase() + data.toUpperCase() + lCRC
				+ hCRC;
		String qdLength = StringUtil.appendLeft(controlData.length() / 2 + "",
				2, '0');
		// 空调为开状态才发送相关控制命令
		NetSDK.sendControlDevMsg(gwID, devID, "14", devType, "1" + qdLength
				+ controlData);
//		if (airCondition.getCurStadus() == "1") {
//			NetSDK.sendControlDevMsg(gwID, devID, "14", devType, "1" + qdLength
//					+ controlData);
//		}
	}

}
