package cc.wulian.app.model.device.impls.controlable.aircondtion;

import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.IProperties;
import cc.wulian.app.model.device.interfaces.IViewResource;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.DaiKinAirConditionListActivity;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_DAIKIN_AIR_CONDITIONING }, category = Category.C_CONTROL)
public class WL_a0_DaiKin_Air_Conditioner extends AbstractDevice {
	
	
	public WL_a0_DaiKin_Air_Conditioner(Context context, String type) {
		super(context, type);
	}

	private BaseActivity baseActivity;
	protected boolean mUseDualPanel;
	protected String ep;
	protected String epType;
	protected String epData;
	protected String epStatus;
	private AirConditionManager airConditionManager = AirConditionManager
			.getInstance();

	
	@Override
	public void refreshDevice() {
		DeviceEPInfo epInfo = getCurrentEpInfo();
		if (epInfo == null) {
			return;
		}
		epType = epInfo.getEp();
		epType = epInfo.getEpType();
		epData = epInfo.getEpData();
		epStatus = epInfo.getEpStatus();

		if (StringUtil.isNullOrEmpty(epData)) {
			return;
		}
		// 性能信息
		if (epData.length() == 30
				&& epData.substring(8, 12).equals(
						DaikinChangeDataAndAddress.CMD_DAIKIN_QUERY_04)) {
			// 空调地址
			String performanceAddress = epData.substring(4, 8);
			final String performanceID = toFindAirConditionIDByPerformanceAddress(performanceAddress);
			// 性能信息
			String performanceData = epData.substring(14, 18);
			putPerformanceDataToAirCondition(performanceID, performanceData);
			// 制冷温度上下限
			String coolLimitTempData = epData.substring(18, 22);
			putCoolLimitTempDataToAirCondition(performanceID, coolLimitTempData);
			// 制热温度上下限
			String hotLimitTempData = epData.substring(22, 26);
			putHotLimitTempDataToAirCondition(performanceID, hotLimitTempData);
			post(new Runnable() {

				@Override
				public void run() {
					AirConditionManager.fireEpDataListener(performanceID);

				}
			});
			// 状态信息
		} else if (epData.length() == 42
				&& epData.substring(8, 12).equals(
						DaikinChangeDataAndAddress.CMD_DAIKIN_QUERY_04)) {
			String statusAddress = epData.substring(4, 8);
			final String statusID = toFindAirConditionIDByStatusAddress(statusAddress);
			// 运转停止、风向、风量等状态
			String statusData1 = epData.substring(14, 18);
			putStatuData1ToAirCondition(statusID, statusData1);
			// 运转模式、运转状态、冷热选择权
			String statusData2 = epData.substring(18, 22);
			putStatuData2ToAirCondition(statusID, statusData2);
			// 设定温度
			String statusData3 = epData.substring(22, 26);
			putStatuData3ToAirCondition(statusID, statusData3);
			// String statusData4 = epData.substring(26, 30);
			// 室内温度
			String statusData5 = epData.substring(30, 34);
			putStatuData5ToAirCondition(statusID, statusData5);
			// String statusData6 = epData.substring(34, 38);

			post(new Runnable() {

				@Override
				public void run() {
					AirConditionManager.fireEpDataListener(statusID);
				}
			});

			// 控制单个寄存器返回数据
		} else if (epData.length() == 24
				&& epData.substring(8, 12).equals(
						DaikinChangeDataAndAddress.CMD_DAIKIN_CONTROL_06)) {
			String controlAddress = epData.substring(4, 8);
			String controlData = epData.substring(16, 20);
			toFindAirConditionIDAndPutEpData(controlAddress, controlData);

		}
	}

	/**
	 * 通过当前性能地址查找空调ID
	 * 
	 * @param curHexAddress
	 * @return
	 */
	public String toFindAirConditionIDByPerformanceAddress(String curHexAddress) {
		int address = Integer.parseInt(curHexAddress, 16) - 1000;
		int group = address / 48 + 1;
		int id = 0;
		int curID = address / 3;
		if (0 <= curID && curID < 16) {
			id = curID;
		} else if (16 <= curID && curID < 32) {
			id = curID - 16;
		} else if (32 <= curID && curID < 48) {
			id = curID - 32;
		} else if (48 <= curID && curID < 64) {
			id = curID - 48;
		}
		String conditionID = String.valueOf(group) + "-"
				+ StringUtil.appendLeft(String.valueOf(id), 2, '0');
		return conditionID;

	}

	/**
	 * 通过状态地址查找空调ID
	 * 
	 * @param curHexAddress
	 * @return
	 */
	public String toFindAirConditionIDByStatusAddress(String curHexAddress) {
		int address = Integer.parseInt(curHexAddress, 16) - 2000;
		int group = address / 96 + 1;
		int id = 0;
		int curID = address / 6;
		if (0 <= curID && curID < 16) {
			id = curID;
		} else if (16 <= curID && curID < 32) {
			id = curID - 16;
		} else if (32 <= curID && curID < 48) {
			id = curID - 32;
		} else if (48 <= curID && curID < 64) {
			id = curID - 48;
		}
		String conditionID = String.valueOf(group) + "-"
				+ StringUtil.appendLeft(String.valueOf(id), 2, '0');
		return conditionID;

	}

	/**
	 * 通过保持寄存器返回数据查找空调地址并fire
	 * 
	 * @param curHexAddress
	 * @param data
	 */
	public void toFindAirConditionIDAndPutEpData(String curHexAddress,
			String data) {
		int address = Integer.parseInt(curHexAddress, 16) - 2000;
		int group = address / 48 + 1;
		int id = 0;
		int curID = address / 3;
		int type = address % 3;
		if (0 <= curID && curID < 16) {
			id = curID;
		} else if (16 <= curID && curID < 32) {
			id = curID - 16;
		} else if (32 <= curID && curID < 48) {
			id = curID - 32;
		} else if (48 <= curID && curID < 64) {
			id = curID - 48;
		}
		final String conditionID = String.valueOf(group) + "-"
				+ StringUtil.appendLeft(String.valueOf(id), 2, '0');
		AirCondition airCondition = airConditionManager
				.getAirConditionByID(conditionID);
		if (airCondition != null) {
			if (type == 0) {
				String dataType1 = DaikinChangeDataAndAddress
						.hexString2binaryString(data);
				// 需要传递整体数据
				airCondition.setControlData_1(dataType1);
				airCondition.setCurStadus(dataType1.substring(15, 16));
				airCondition.setCurWindDirection(dataType1.substring(5, 8));
				airCondition.setCurWindPower(dataType1.substring(1, 4));
			} else if (type == 1) {
				String dataType2 = DaikinChangeDataAndAddress
						.hexString2binaryString(data);
				// 需要传递整体数据
				airCondition.setControlData_2(dataType2);
				airCondition.setCurModel(dataType2.substring(12, 16));
			} else if (type == 2) {
				String dataType3 = DaikinChangeDataAndAddress
						.hexString2binaryString(data);
				String data3IfIsMinus = dataType3.substring(0, 1);
				if (data3IfIsMinus.equals("0")) {
					int setTempStatus = StringUtil.toInteger(dataType3, 2) / 10;
					airCondition.setCurSetTemp(String.valueOf(setTempStatus));
				} else {
					int setTempMinusStatus = StringUtil.toInteger(dataType3, 2) / 10;
					airCondition.setCurSetTemp("-" + setTempMinusStatus);
				}
			}

			post(new Runnable() {

				@Override
				public void run() {
					AirConditionManager.fireEpDataListener(conditionID);

				}
			});
		}

	}

	/**
	 * 性能信息
	 * 
	 * @param curAirConditionID
	 * @param data
	 */
	public void putPerformanceDataToAirCondition(String curAirConditionID,
			String data) {
		AirCondition airCondition = airConditionManager
				.getAirConditionByID(curAirConditionID);
		if (airCondition != null) {
			String data2Binary = DaikinChangeDataAndAddress
					.hexString2binaryString(data);
			String dataBlowModel = data2Binary.substring(15, 16);
			String dataCoolModel = data2Binary.substring(14, 15);
			String dataHotModel = data2Binary.substring(13, 14);
			String dataAutoModel = data2Binary.substring(12, 13);
			// 风向叶片数设定未解析
			String dataArefactionModel = data2Binary.substring(11, 12);
			String dataWindDirectionSet = data2Binary.substring(4, 5);
			String dataWindSpeed = Integer.valueOf(data2Binary.substring(1, 4),
					2).toString();
			airCondition.setCurWindSpeed(dataWindSpeed);
			String dataAirVolumeAdjust = data2Binary.substring(0, 1);
			if (dataBlowModel.equals("1")) {
				airCondition.setHasBlowModel(true);
			} else {
				airCondition.setHasBlowModel(false);
			}
			if (dataCoolModel.equals("1")) {
				airCondition.setHasCoolModel(true);
			} else {
				airCondition.setHasCoolModel(false);
			}
			if (dataHotModel.equals("1")) {
				airCondition.setHasHotModel(true);
			} else {
				airCondition.setHasHotModel(false);
			}
			if (dataAutoModel.equals("1")) {
				airCondition.setHasAutoModel(true);
			} else {
				airCondition.setHasAutoModel(false);
			}
			if (dataArefactionModel.equals("1")) {
				airCondition.setHasArefactionModel(true);
			} else {
				airCondition.setHasArefactionModel(false);
			}
			if (dataWindDirectionSet.equals("1")) {
				airCondition.setHasWindDirectionSet(true);
			} else {
				airCondition.setHasWindDirectionSet(false);
			}
			if (dataAirVolumeAdjust.equals("1")) {
				airCondition.setHasAirVolumeAdjust(true);
			} else {
				airCondition.setHasAirVolumeAdjust(false);
			}

			// AirConditionManager.fireEpDataListener(curAirConditionID);
		}
	}

	/**
	 * 制冷设定温度上下限
	 * 
	 * @param curAirConditionID
	 * @param coolLimitTempData
	 */
	private void putCoolLimitTempDataToAirCondition(String curAirConditionID,
			String data) {
		AirCondition airCondition = airConditionManager
				.getAirConditionByID(curAirConditionID);
		if (airCondition != null) {
			String data2Binary = DaikinChangeDataAndAddress
					.hexString2binaryString(data);
			String coolMax = Integer.valueOf(data2Binary.substring(9, 16), 16)
					.toString();
			String coolMin = Integer.valueOf(data2Binary.substring(1, 8), 16)
					.toString();
			if (data2Binary.substring(8, 9).equals("0")) {
				airCondition.setCoolMaxTemp(coolMax);
			} else {
				airCondition.setCoolMaxTemp("-" + coolMax);
			}
			if (data2Binary.substring(0, 1).equals("0")) {
				airCondition.setCoolMinTemp(coolMin);
			} else {
				airCondition.setCoolMinTemp("-" + coolMin);
			}
		}
		// AirConditionManager.fireEpDataListener(curAirConditionID);
	}

	/**
	 * 制热设定温度上下限
	 * 
	 * @param curAirConditionID
	 * @param hotLimitTempData
	 */
	private void putHotLimitTempDataToAirCondition(String curAirConditionID,
			String data) {
		AirCondition airCondition = airConditionManager
				.getAirConditionByID(curAirConditionID);
		if (airCondition != null) {
			String data2Binary = DaikinChangeDataAndAddress
					.hexString2binaryString(data);
			String hotMax = Integer.valueOf(data2Binary.substring(9, 16), 16)
					.toString();
			String hotMin = Integer.valueOf(data2Binary.substring(1, 8), 16)
					.toString();
			if (data2Binary.substring(8, 9).equals("0")) {
				airCondition.setHotMaxTemp(hotMax);
			} else {
				airCondition.setHotMaxTemp("-" + hotMax);
			}
			if (data2Binary.substring(0, 1).equals("0")) {
				airCondition.setHotMinTemp(hotMin);
			} else {
				airCondition.setHotMinTemp("-" + hotMin);
			}
		}
		// AirConditionManager.fireEpDataListener(curAirConditionID);

	}

	/**
	 * 开关,风向,风量
	 * 
	 * @param curAirConditionID
	 * @param data
	 */
	public void putStatuData1ToAirCondition(String curAirConditionID,
			String data) {
		AirCondition airCondition = airConditionManager
				.getAirConditionByID(curAirConditionID);
		if (airCondition != null) {
			String data2Binary = DaikinChangeDataAndAddress
					.hexString2binaryString(data);
			// 需要传递整体数据
			airCondition.setStatusData_1(data2Binary);
			String dataSwitchStatus = data2Binary.substring(15, 16);
			String dataWindDirectionStatus = data2Binary.substring(5, 8);
			String dataWindPowerStatus = data2Binary.substring(1, 4);
			airCondition.setCurStadus(dataSwitchStatus);
			airCondition.setCurWindDirection(dataWindDirectionStatus);
			airCondition.setCurWindPower(dataWindPowerStatus);
			// AirConditionManager.fireEpDataListener(curAirConditionID);
		}

	}

	/**
	 * 运转模式
	 * 
	 * 冷热选择权等未做解析
	 * 
	 * @param curAirConditionID
	 * @param data
	 */
	public void putStatuData2ToAirCondition(String curAirConditionID,
			String data) {
		AirCondition airCondition = airConditionManager
				.getAirConditionByID(curAirConditionID);
		if (airCondition != null) {
			String data2Binary = DaikinChangeDataAndAddress
					.hexString2binaryString(data);
			// 需要传递整体数据
			airCondition.setStatusData_2(data2Binary);
			String dataModelStatus = data2Binary.substring(12, 16);
			airCondition.setCurModel(dataModelStatus);
			// 当前运行状态,暂时未做处理
			airCondition.setCurRunStatus(data2Binary.substring(4, 8));
			// AirConditionManager.fireEpDataListener(curAirConditionID);
		}
	}

	/**
	 * 设定温度
	 * 
	 * @param curAirConditionID
	 * @param data
	 */
	public void putStatuData3ToAirCondition(String curAirConditionID,
			String data) {
		AirCondition airCondition = airConditionManager
				.getAirConditionByID(curAirConditionID);
		if (airCondition != null) {
			String data2Binary = DaikinChangeDataAndAddress
					.hexString2binaryString(data);
			String dataSetTempStatus = data2Binary.substring(1, 16);
			String dataIfIsMinus = data2Binary.substring(0, 1);
			if (dataIfIsMinus.equals("0")) {
				int setTempStatus = StringUtil.toInteger(dataSetTempStatus, 2) / 10;
				airCondition.setCurSetTemp(String.valueOf(setTempStatus));
			} else {
				int setTempMinusStatus = StringUtil.toInteger(
						dataSetTempStatus, 2) / 10;
				airCondition.setCurSetTemp("-" + setTempMinusStatus);
			}
			// AirConditionManager.fireEpDataListener(curAirConditionID);
		}
	}
	
	private TextView tvRefreshResult;
	private ImageView ivRefreshDefault;
	private TextView tvRefresh;
	private ImageView ivListAC;
	private Boolean isSuccess ;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		baseActivity = (BaseActivity) inflater.getContext();
		return inflater.inflate(R.layout.device_daikin_airconditioner_home, null);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		tvRefreshResult = (TextView) view.findViewById(R.id.tv_ac_refresh_result);
		ivRefreshDefault = (ImageView) view.findViewById(R.id.iv_ac_failed);
		tvRefresh = (TextView) view.findViewById(R.id.tv_ac_refresh);
		ivListAC = (ImageView) view.findViewById(R.id.iv_list_daikin_acs);
		isSuccess = false;
		
		sendData();
		
		tvRefresh.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
		tvRefresh.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				sendData();
			}
		});
		
		ivListAC.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(isSuccess){
					Bundle bundler = new Bundle();
					bundler.putString(DaikinAirConditionListFragment.DEVICE_ID, devID);
					bundler.putString(DaikinAirConditionListFragment.GWID,gwID);
					bundler.putString(DaikinAirConditionListFragment.EPDATA, epData);
					Intent intent = new Intent(baseActivity ,DaiKinAirConditionListActivity.class);
					intent.putExtras(bundler);
					baseActivity.startActivity(intent);
				}
			}
		});
	}

	/**
	 * 室内温度
	 * 
	 * @param curAirConditionID
	 * @param data
	 */
	public void putStatuData5ToAirCondition(String curAirConditionID,
			String data) {
		AirCondition airCondition = airConditionManager
				.getAirConditionByID(curAirConditionID);
		if (airCondition != null) {
			String data2Binary = DaikinChangeDataAndAddress
					.hexString2binaryString(data);
			String dataTempStatus = data2Binary.substring(1, 16);
			String dataIfIsMinus = data2Binary.substring(0, 1);
			if (dataIfIsMinus.equals("0")) {
				float tempStatus = StringUtil.toInteger(dataTempStatus, 2) / 10.0F;
				airCondition.setCurTemp(String.valueOf(tempStatus));
			} else {
				float tempMinusStatus = StringUtil.toInteger(dataTempStatus, 2) / 10.0F;
				airCondition.setCurTemp("-" + tempMinusStatus);
			}
			// AirConditionManager.fireEpDataListener(curAirConditionID);
		}
	}
	
	private void sendData() {
		// CRC校验 读输入寄存器
		String hexString = DaikinChangeDataAndAddress.CMD_DAIKIN_QUERY_04
				+ "0000" + DaikinChangeDataAndAddress.REGISTER_NUMBER_0009;
		byte[] bytes = StringUtil.hexStringToBytes(hexString);
		int crc = CRC16_Check.calcCrc16(bytes);
		String CRC = String.format("%04x", crc).toUpperCase();
		String hCRC = CRC.substring(0, 2);
		String lCRC = CRC.substring(2, 4);
		String queryData = hexString + lCRC + hCRC;
		String qdLength = StringUtil.appendLeft(
				queryData.length() / 2 + "", 2, '0');

		NetSDK.sendControlDevMsg(gwID, devID, "14", type, "1" + qdLength
				+ queryData);
	}
	
	@Override
	public void initViewStatus() {
		super.initViewStatus();
		if (StringUtil.isNullOrEmpty(epData)) {
			ivRefreshDefault.setVisibility(View.VISIBLE);
			ivListAC.setImageResource(R.drawable.icon_list_daikinairconditioner_grey);
			tvRefreshResult.setText(R.string.device_refresh_aircondtioner_default);
			isSuccess = false;
			return;

		} else {
			ivRefreshDefault.setVisibility(View.GONE);
			ivListAC.setImageResource(R.drawable.icon_list_daikinairconditioner);
			tvRefreshResult.setText(R.string.device_refresh_aircondtioner_success);
			isSuccess = true;
		}
	}
}
