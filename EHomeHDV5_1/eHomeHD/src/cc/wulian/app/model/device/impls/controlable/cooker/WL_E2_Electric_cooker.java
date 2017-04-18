package cc.wulian.app.model.device.impls.controlable.cooker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;


/**
 * 发送：
 * 1sfkphhmmhhmm:1表示控制命令
 * s表示机器运行状态（1表示开始、2表示停止、5表示取消）
 * f表示功能（1表示精煮米饭、2表示快煮米饭、3表示五谷饭、4表示排骨、5表示汤/粥、6表示蛋糕、7表示鸡鸭肉、8表示牛羊肉、9表示豆/蹄筋）
 * k表示口感（1表示标准、2表示清香、3表示浓郁）
 * p表示压力（1表示1档、2表示2档、3表示3档、4表示4档、5表示5档、6表示6档、7表示7档、8表示8档）
 * hhmm表示保压时间（hh 小时，03表示3小时；mm分钟，45表示45分钟）
 * hhmm表示预约时间（hh小时，13表示13小时；mm分钟，30表示30分钟）
 * 2:表示查询命令                               
 * 各功能对应的默认保压时间和压力：精煮米饭（10分钟、3档），快煮米饭（8分钟、3档），五谷饭（10分钟、6档），
 * 排骨（15分钟、7档），汤/粥（20分钟、5档），蛋糕（40分钟、1档），鸡鸭肉（12分钟、7档），牛羊肉（25分钟、8档），豆/蹄筋（30分钟、8档）
 *
 * 接收
 * 03ssffkkpphhmmhhmmss:控制命令、查询命令以及机器主动上报都返回03
 * ss表示机器运行状态（01表示开始、02表示停止、03表示暂停、04表示保温、05表示取消）
 * ff表示功能（01表示精煮米饭、02表示快煮米饭、03表示五谷饭、04表示排骨、05表示汤/粥、06表示蛋糕、07表示鸡鸭肉、08表示牛羊肉、09表示豆/蹄筋）
 * kk表示口感（01表示标准、02表示清香、03表示浓郁）
 * pp表示压力（01表示1档、02表示2档、03表示3档、04表示4档、05表示5档、06表示6档、07表示7档、08表示8档）
 * hhmm表示保压时间（hh 小时，03表示3小时；mm分钟，2d表示45分钟）
 * hhmm表示预约时间（hh小时，0d表示13小时；mm分钟，1e表示30分钟）
 * ss表示工作阶段（1表示升压，2表示沸腾，3表示保压，4表示降压，5表示完成）
 *
 *
 *
 */

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_PRESSURE_COOKER }, category = Category.C_OTHER)
public class WL_E2_Electric_cooker extends ControlableDeviceImpl{

	private static final String PREFIX_DATA_SEND = "1";
	private static final String PREFIX_DATA_SEND_START = "1";
	private static final String PREFIX_DATA_SEND_STOP = "2";
//	private static final String PREFIX_DATA_SEND_SUSPEN = "3";
	private static final String PREFIX_DATA_SEND_TEMP = "4";
	private static final String PREFIX_DATA_SEND_CANCEL = "5";
	
	private String cookerState;
	private String cookerPalate;
	private String cookerPressure;
	private String cookerPressureTimeH;
	private String cookerPressureTimeM;
	private String cookerAppointmentTimeHour;
	private String cookerAppointmentTimeMinues;
	private String cookerWork;
	public static int timeHour = 0;
	public static int timeMinues = 0;
	public static String cookerFunction;
	
	private boolean cakeState;
//	private boolean boilingState;
//	private boolean holdingPresState;
//	private boolean appointState;
	private boolean isSelect;
	private boolean isState;
	
	
	private TextView mTempCooker;
	private TextView mStartCooker;
	private TextView mCancelCooker;
	
	private TextView mCookerModeSweet;
	private TextView mCookerModeNormal;
	private TextView mCookerModeRich;
	
	private ImageView mCookerkpa0;
	private ImageView mCookerkpa20;
	private ImageView mCookerkpa30;
	private ImageView mCookerkpa40;
	private ImageView mCookerkpa50;
	private ImageView mCookerkpa60;
	private ImageView mCookerkpa70;
	private ImageView mCookerkpa80;
	
	private LinearLayout mCookerkpa0Layout;
	private LinearLayout mCookerkpa20Layout;
	private LinearLayout mCookerkpa30Layout;
	private LinearLayout mCookerkpa40Layout;
	private LinearLayout mCookerkpa50Layout;
	private LinearLayout mCookerkpa60Layout;
	private LinearLayout mCookerkpa70Layout;
	private LinearLayout mCookerkpa80Layout;
	
	private TextView mTextViewKpa0;
	private TextView mTextViewKpa20;
	private TextView mTextViewKpa30;
	private TextView mTextViewKpa40;
	private TextView mTextViewKpa50;
	private TextView mTextViewKpa60;
	private TextView mTextViewKpa70;
	private TextView mTextViewKpa80;
	
	private TextView mTextViewComplete;
	private TextView mTextViewbooster;
	private TextView mTextViewBoiling;
	private TextView mTextViewHolding;
	private TextView mTextViewBuck;
	
	private SeekBar mCookerKpaSeekbar;
	private TextView mCookerSeekText;
	
	private TextView mCookerFunction;
	private TextView mCookerTime;
	private TextView mCookerTimeHour;
	private TextView mCookerTimeMInues;
	private ElectricCookerTimeView timingCookerView;
	private ElectricCookerFunctionView functionCookerView;
	
	public WL_E2_Electric_cooker(Context context, String type) {
		super(context, type);
	}

//	
//	
//	
//	@Override
//	public String getOpenSendCmd() {
//		return super.getOpenSendCmd();
//	}
//
//
//	@Override
//	public String getCloseSendCmd() {
//		return super.getCloseSendCmd();
//	}
//
//
//	@Override
//	public String getOpenProtocol() {
//		return super.getOpenProtocol();
//	}
//
//
//	@Override
//	public String getCloseProtocol() {
//		return super.getCloseProtocol();
//	}
//
//
//	@Override
//	public boolean isOpened() {
//		return super.isOpened();
//	}
//
//
//	@Override
//	public boolean isClosed() {
//		return super.isClosed();
//	}




	private OnClickListener mClickListener = new OnClickListener() {

		//发送：1sfkphhmmhhmm
		@Override
		public void onClick(View v) {
			if(v == mCookerFunction){
				final WLDialog dialog;
//				new WLDialog.Builder(context);
				WLDialog.Builder builder = new Builder(mContext);
				builder.setContentView(createViewFunction())
						.setPositiveButton(R.string.device_ok)
						.setNegativeButton(R.string.device_cancel)
						.setTitle(mContext.getString(R.string.device_cooker_function))
						.setListener(new MessageListener() {
							@Override
							public void onClickPositive(View contentViewLayout) {
								String position = functionCookerView.getFunctionPosition();
								if("1".equals(position)){
									cookerPressureTimeM = "10";
									cookerPressure = "03";
								}else if("2".equals(position)){
									cookerPressureTimeM = "08";
									cookerPressure = "03";
								}else if("3".equals(position)){
									cookerPressureTimeM = "10";
									cookerPressure = "06";
								}else if("4".equals(position)){
									cookerPressureTimeM = "15";
									cookerPressure = "07";
								}else if("5".equals(position)){
									cookerPressureTimeM = "20";
									cookerPressure = "05";
								}else if("6".equals(position)){
									cookerPressureTimeM = "40";
									cookerPressure = "01";
								}else if("7".equals(position)){
									cookerPressureTimeM = "12";
									cookerPressure = "07";
								}else if("8".equals(position)){
									cookerPressureTimeM = "25";
									cookerPressure = "08";
								}else if("9".equals(position)){
									cookerPressureTimeM = "30";
									cookerPressure = "08";
								}
								createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_STOP+ functionCookerView.getFunctionPosition()
										+ String.valueOf(StringUtil.toInteger(cookerPalate)) + String.valueOf(StringUtil.toInteger(cookerPressure)) + "00" +StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
										+  StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
							}

							@Override
							public void onClickNegative(View contentViewLayout) {
								// TODO Auto-generated method stub

							}
						});
				dialog = builder.create();
				dialog.show();
			}else if(v == mCookerTime && isSelect == true && cakeState == false && isState == false){
				final WLDialog dialog;
//				new WLDialog.Builder(context);
				WLDialog.Builder builder = new Builder(mContext);
				builder.setContentView(createViewTime())
						.setPositiveButton(R.string.device_ok)
						.setNegativeButton(R.string.device_cancel)
						.setTitle(mContext.getString(R.string.device_cooker_time))
						.setListener(new MessageListener() {
							@Override
							public void onClickPositive(View contentViewLayout) {
								timeHour = timingCookerView.getSettingHourTime();
								timeMinues = timingCookerView.getSettingMinuesTime();
								createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_STOP+ String.valueOf(StringUtil.toInteger(cookerFunction))
										+ String.valueOf(StringUtil.toInteger(cookerPalate)) + String.valueOf(StringUtil.toInteger(cookerPressure)) + "00" + StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
										+ StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
							}

							@Override
							public void onClickNegative(View contentViewLayout) {
								// TODO Auto-generated method stub

							}
						});
				dialog = builder.create();
				dialog.show();
			}else if(v == mCookerModeSweet && isSelect == true && isState == false){
				cookerPalate = "2";
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_STOP+ String.valueOf(StringUtil.toInteger(cookerFunction))
						+ cookerPalate + String.valueOf(StringUtil.toInteger(cookerPressure)) + "00" + StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
						+ StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
			}else if(v == mCookerModeNormal && isSelect == true && isState == false){
				cookerPalate = "1";
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_STOP+ String.valueOf(StringUtil.toInteger(cookerFunction))
						+ cookerPalate + String.valueOf(StringUtil.toInteger(cookerPressure)) + "00" + StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
						+ StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
			}else if(v == mCookerModeRich && isSelect == true && isState == false){
				cookerPalate = "3";
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_STOP+ String.valueOf(StringUtil.toInteger(cookerFunction))
						+ cookerPalate + String.valueOf(StringUtil.toInteger(cookerPressure)) + "00" + StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
						+ StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
			}else if(v == mTempCooker){
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_TEMP+ String.valueOf(StringUtil.toInteger(cookerFunction))
						+ String.valueOf(StringUtil.toInteger(cookerPalate)) + String.valueOf(StringUtil.toInteger(cookerPressure)) + "00" + StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
						+ StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
				isSelect = false;
			}else if(v == mStartCooker && isSelect == true && isState == false){
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_START+ String.valueOf(StringUtil.toInteger(cookerFunction))
						+ String.valueOf(StringUtil.toInteger(cookerPalate)) + String.valueOf(StringUtil.toInteger(cookerPressure)) + "00" + StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
						+ StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
			}else if(v == mCancelCooker){
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_CANCEL+ String.valueOf(StringUtil.toInteger(cookerFunction))
						+ String.valueOf(StringUtil.toInteger(cookerPalate)) + String.valueOf(StringUtil.toInteger(cookerPressure)) + "00" + StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
						+ StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
				isSelect = false;
			}else if((v == mCookerkpa0Layout || v == mCookerkpa0) && isSelect == true && isState == false){
				cookerPressure = "1";
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_STOP+ String.valueOf(StringUtil.toInteger(cookerFunction))
						+ String.valueOf(StringUtil.toInteger(cookerPalate)) + cookerPressure + "00" + StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
						+ StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
			}else if((v == mCookerkpa20Layout || v == mCookerkpa20) && isSelect == true && isState == false){
				cookerPressure = "2";
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_STOP+ String.valueOf(StringUtil.toInteger(cookerFunction))
						+ String.valueOf(StringUtil.toInteger(cookerPalate)) + cookerPressure + "00" + StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
						+ StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
			}else if((v == mCookerkpa30Layout || v == mCookerkpa30) && isSelect == true && isState == false){
				cookerPressure = "3";
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_STOP+ String.valueOf(StringUtil.toInteger(cookerFunction))
						+ String.valueOf(StringUtil.toInteger(cookerPalate)) + cookerPressure + "00" + StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
						+ StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
			}else if((v == mCookerkpa40Layout || v == mCookerkpa40) && isSelect == true && isState == false){
				cookerPressure = "4";
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_STOP+ String.valueOf(StringUtil.toInteger(cookerFunction))
						+ String.valueOf(StringUtil.toInteger(cookerPalate)) + cookerPressure + "00" + StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
						+ StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
			}else if((v == mCookerkpa50Layout || v == mCookerkpa50) && isSelect == true && isState == false){
				cookerPressure = "5";
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_STOP+ String.valueOf(StringUtil.toInteger(cookerFunction))
						+ String.valueOf(StringUtil.toInteger(cookerPalate)) + cookerPressure + "00" + StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
						+ StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
			}else if((v == mCookerkpa60Layout || v == mCookerkpa60) && isSelect == true && isState == false){
				cookerPressure = "6";
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_STOP+ String.valueOf(StringUtil.toInteger(cookerFunction))
						+ String.valueOf(StringUtil.toInteger(cookerPalate)) + cookerPressure + "00" + StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
						+ StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
			}else if((v == mCookerkpa70Layout || v == mCookerkpa70) && isSelect == true && isState == false){
				cookerPressure = "7";
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_STOP+ String.valueOf(StringUtil.toInteger(cookerFunction))
						+ String.valueOf(StringUtil.toInteger(cookerPalate)) + cookerPressure + "00" +StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
						+ StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
			}else if((v == mCookerkpa80Layout || v == mCookerkpa80) && isSelect == true && isState == false){
				cookerPressure = "8";
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_STOP+ String.valueOf(StringUtil.toInteger(cookerFunction))
						+ String.valueOf(StringUtil.toInteger(cookerPalate)) + cookerPressure + "00" +StringUtil.appendLeft(cookerPressureTimeM + "", 2, '0')
						+  StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
			}
		}
		
	};

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_pressure_cooker, null);
	}


	protected View createViewTime() {
//		View view = LayoutInflater.from(mContext).inflate(R.layout.device_pressure_cooker, null);
//		return view;
		timingCookerView = new ElectricCookerTimeView(mContext);
		return timingCookerView;
	}

	protected View createViewFunction() {
//		View view = LayoutInflater.from(mContext).inflate(R.layout.device_pressure_cooker, null);
//		return view;
		functionCookerView = new ElectricCookerFunctionView(mContext);
		return functionCookerView.mFunctionView();
	}

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item,LayoutInflater inflater){
		return getDefaultShortCutControlView(item,inflater);
	}
	
	
	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		
		mTempCooker = (TextView) view.findViewById(R.id.device_cooker_temp);
		mStartCooker = (TextView) view.findViewById(R.id.device_cooker_start);
		mCancelCooker = (TextView) view.findViewById(R.id.device_cooker_cancer);
		mTempCooker.setOnClickListener(mClickListener);
		mStartCooker.setOnClickListener(mClickListener);
		mCancelCooker.setOnClickListener(mClickListener);
		
		
		mCookerModeSweet = (TextView) view.findViewById(R.id.device_cooker_mode_sweet);
		mCookerModeNormal = (TextView) view.findViewById(R.id.device_cooker_mode_normal);
		mCookerModeRich = (TextView) view.findViewById(R.id.device_cooker_mode_rich);
		mCookerModeSweet.setOnClickListener(mClickListener);
		mCookerModeNormal.setOnClickListener(mClickListener);
		mCookerModeRich.setOnClickListener(mClickListener);
		
		mCookerkpa0 = (ImageView) view.findViewById(R.id.device_cooker_kpa_0);
		mCookerkpa20 = (ImageView) view.findViewById(R.id.device_cooker_kpa_20);
		mCookerkpa30 = (ImageView) view.findViewById(R.id.device_cooker_kpa_30);
		mCookerkpa40 = (ImageView) view.findViewById(R.id.device_cooker_kpa_40);
		mCookerkpa50 = (ImageView) view.findViewById(R.id.device_cooker_kpa_50);
		mCookerkpa60 = (ImageView) view.findViewById(R.id.device_cooker_kpa_60);
		mCookerkpa70 = (ImageView) view.findViewById(R.id.device_cooker_kpa_70);
		mCookerkpa80 = (ImageView) view.findViewById(R.id.device_cooker_kpa_80);
		
		mCookerkpa0Layout = (LinearLayout) view.findViewById(R.id.device_cooker_kpa_0_layout);
		mCookerkpa20Layout = (LinearLayout) view.findViewById(R.id.device_cooker_kpa_20_layout);
		mCookerkpa30Layout = (LinearLayout) view.findViewById(R.id.device_cooker_kpa_30_layout);
		mCookerkpa40Layout = (LinearLayout) view.findViewById(R.id.device_cooker_kpa_40_layout);
		mCookerkpa50Layout = (LinearLayout) view.findViewById(R.id.device_cooker_kpa_50_layout);
		mCookerkpa60Layout = (LinearLayout) view.findViewById(R.id.device_cooker_kpa_60_layout);
		mCookerkpa70Layout = (LinearLayout) view.findViewById(R.id.device_cooker_kpa_70_layout);
		mCookerkpa80Layout = (LinearLayout) view.findViewById(R.id.device_cooker_kpa_80_layout);
		mCookerkpa0Layout.setOnClickListener(mClickListener);
		mCookerkpa20Layout.setOnClickListener(mClickListener);
		mCookerkpa30Layout.setOnClickListener(mClickListener);
		mCookerkpa40Layout.setOnClickListener(mClickListener);
		mCookerkpa50Layout.setOnClickListener(mClickListener);
		mCookerkpa60Layout.setOnClickListener(mClickListener);
		mCookerkpa70Layout.setOnClickListener(mClickListener);
		mCookerkpa80Layout.setOnClickListener(mClickListener);
		
		mTextViewKpa0 = (TextView) view.findViewById(R.id.cooker_text_kpa_0);
		mTextViewKpa20 = (TextView) view.findViewById(R.id.cooker_text_kpa_20);
		mTextViewKpa30 = (TextView) view.findViewById(R.id.cooker_text_kpa_30);
		mTextViewKpa40 = (TextView) view.findViewById(R.id.cooker_text_kpa_40);
		mTextViewKpa50 = (TextView) view.findViewById(R.id.cooker_text_kpa_50);
		mTextViewKpa60 = (TextView) view.findViewById(R.id.cooker_text_kpa_60);
		mTextViewKpa70 = (TextView) view.findViewById(R.id.cooker_text_kpa_70);
		mTextViewKpa80 = (TextView) view.findViewById(R.id.cooker_text_kpa_80);
		mTextViewKpa0.setOnClickListener(mClickListener);
		mTextViewKpa20.setOnClickListener(mClickListener);
		mTextViewKpa30.setOnClickListener(mClickListener);
		mTextViewKpa40.setOnClickListener(mClickListener);
		mTextViewKpa50.setOnClickListener(mClickListener);
		mTextViewKpa60.setOnClickListener(mClickListener);
		mTextViewKpa70.setOnClickListener(mClickListener);
		mTextViewKpa80.setOnClickListener(mClickListener);
		
		mTextViewComplete = (TextView) view.findViewById(R.id.device_cooker_text_complete);
		mTextViewbooster = (TextView) view.findViewById(R.id.device_cooker_text_booster);
		mTextViewBoiling = (TextView) view.findViewById(R.id.device_cooker_text_boiling);
		mTextViewHolding = (TextView) view.findViewById(R.id.device_cooker_text_holding);
		mTextViewBuck = (TextView) view.findViewById(R.id.device_cooker_text_buck);
		
		mCookerKpaSeekbar = (SeekBar) view.findViewById(R.id.device_cooker_kpa_seekbar);
		
		
		mCookerSeekText = (TextView) view.findViewById(R.id.device_cooker_textview);
		
		mCookerFunction = (TextView) view.findViewById(R.id.device_cooker_function);
		mCookerFunction.setOnClickListener(mClickListener);
		mCookerTime = (TextView) view.findViewById(R.id.device_cooker_time);
		mCookerTime.setBackgroundResource(R.drawable.device_cooker_time_bg_0);
		mCookerTime.setOnClickListener(mClickListener);
		mCookerTimeHour = (TextView) view.findViewById(R.id.cooker_time_hour);
		mCookerTimeMInues = (TextView) view.findViewById(R.id.cooker_time_minues);
		
//		mCookerTimeHour.setText(timingCookerView.getSettingHourTime());
//		mCookerTimeMInues.setText(timingCookerView.getSettingMinuesTime());
	}


	@SuppressLint("NewApi")
	@Override
	public void initViewStatus() {
		super.initViewStatus();
		
		if (StringUtil.isNullOrEmpty(epData)) {
			return;

		}
		
		//有时候epdata是"00"
		//03ssffkkpphhmmhhmmss
		if (epData.startsWith("03") && epData.length() >=20 ) {
			
			cookerState = epData.substring(2, 4);
			cookerFunction = epData.substring(4, 6);
			cookerPalate = epData.substring(6, 8);
			cookerPressure = epData.substring(8, 10);
			
			cookerPressureTimeH = epData.substring(10, 12);
			cookerPressureTimeM = String.valueOf(StringUtil.toInteger(epData.substring(12, 14),16));
		
			cookerAppointmentTimeHour = epData.substring(14, 16);
			cookerAppointmentTimeMinues = epData.substring(16, 18);
			timeHour = StringUtil.toInteger(cookerAppointmentTimeHour, 16);
			timeMinues = StringUtil.toInteger(cookerAppointmentTimeMinues, 16);
			mCookerTimeHour.setText(StringUtil.appendLeft(timeHour+"",2,'0'));
			mCookerTimeMInues.setText(StringUtil.appendLeft(timeMinues+"",2,'0'));
			
			cookerWork = epData.substring(18, 20);
			
			//"00"或"06"蛋糕，不是主功能其他选项不能选择
			if("00".equals(cookerFunction) || "06".equals(cookerFunction) || isState == true){
				isSelect = false;
				cakeState = true;
				mCookerTime.setBackgroundResource(R.drawable.device_cooker_time_bg_1);
				mCookerModeSweet.setBackground(null);
				mCookerModeNormal.setBackground(null);
				mCookerModeRich.setBackground(null);
			}else{
				isSelect = true;
				cakeState = false;
				mCookerTime.setBackgroundResource(R.drawable.device_cooker_time_bg_0);
			}
			
			if("01".equals(cookerFunction)){
				mCookerFunction.setText(mContext.getString(R.string.device_pure_rice_cooked));
			}else if("02".equals(cookerFunction)){
				mCookerFunction.setText(mContext.getString(R.string.device_quick_cooking_rice));
			}else if("03".equals(cookerFunction)){
				mCookerFunction.setText(mContext.getString(R.string.device_grain_of_rice));
			}else if("04".equals(cookerFunction)){
				mCookerFunction.setText(mContext.getString(R.string.device_cooker_ribs));
			}else if("05".equals(cookerFunction)){
				mCookerFunction.setText(mContext.getString(R.string.device_cooker_soup));
			}else if("06".equals(cookerFunction)){
				mCookerFunction.setText(mContext.getString(R.string.device_cooker_cake));
			}else if("07".equals(cookerFunction)){
				mCookerFunction.setText(mContext.getString(R.string.device_cooker_chickens));
			}else if("08".equals(cookerFunction)){
				mCookerFunction.setText(mContext.getString(R.string.device_cooker_beef));
			}else if("08".equals(cookerFunction)){
				mCookerFunction.setText(mContext.getString(R.string.device_cooker_bean));
			}else{
				mCookerFunction.setText(mContext.getString(R.string.device_cooker_function));
			}
			if(isSelect == true && cakeState == false && isState == false){
				mCookerKpaSeekbar.setEnabled(true);
				mCookerKpaSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

					@Override
					public void onProgressChanged(SeekBar seekBar, int progress,
							boolean fromUser) {
						mCookerSeekText.setText(progress + "%");
					}

					@Override
					public void onStartTrackingTouch(SeekBar seekBar) {
						
					}

					@Override
					public void onStopTrackingTouch(SeekBar seekBar) {
						int mSeekProgress = seekBar.getProgress();
//						int mSeek = mSeekProgress * 60 / 100;
						createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_SEND + PREFIX_DATA_SEND_STOP+ String.valueOf(StringUtil.toInteger(cookerFunction))
								+ String.valueOf(StringUtil.toInteger(cookerPalate)) + String.valueOf(StringUtil.toInteger(cookerPressure)) + StringUtil.appendLeft(mSeekProgress + "", 4, '0')
								+  StringUtil.appendLeft(timeHour + "", 2, '0')+StringUtil.appendLeft(timeMinues + "", 2, '0'), true);
					}
					
				});
			}else{
				mCookerKpaSeekbar.setEnabled(false);
			}
			int mCookerSeekbar = StringUtil.toInteger(cookerPressureTimeM);
			mCookerKpaSeekbar.setProgress(mCookerSeekbar);
			mCookerSeekText.setText(cookerPressureTimeM + "%");
			
			if("01".equals(cookerState)){
				mStartCooker.setSelected(true);
				mTempCooker.setSelected(false);
				mCancelCooker.setSelected(false);
				isState = true;
			}else if("04".equals(cookerState)){
				mTempCooker.setSelected(true);
				mStartCooker.setSelected(false);
				mCancelCooker.setSelected(false);
				isState = true;
			}else if("05".equals(cookerState)){
				mCancelCooker.setSelected(true);
				mStartCooker.setSelected(false);
				mTempCooker.setSelected(false);
				isState = true;
			}else{
				mTempCooker.setSelected(false);
				mStartCooker.setSelected(false);
				mCancelCooker.setSelected(false);
				isState = false;
			}
			
			if("02".equals(cookerPalate) && cakeState == false && isState == false){
				mCookerModeSweet.setBackgroundResource(R.drawable.device_cooker_mode_select);
				mCookerModeNormal.setBackground(null);
				mCookerModeRich.setBackground(null);
			}else if("01".equals(cookerPalate) && cakeState == false && isState == false){
				mCookerModeSweet.setBackground(null);
				mCookerModeNormal.setBackgroundResource(R.drawable.device_cooker_mode_select);
				mCookerModeRich.setBackground(null);
			}else if("03".equals(cookerPalate) && cakeState == false && isState == false){
				mCookerModeSweet.setBackground(null);
				mCookerModeNormal.setBackground(null);
				mCookerModeRich.setBackgroundResource(R.drawable.device_cooker_mode_select);
			}
			
			if("01".equals(cookerPressure)){
				mTextViewKpa0.setSelected(true);
				mTextViewKpa20.setSelected(false);
				mTextViewKpa30.setSelected(false);
				mTextViewKpa40.setSelected(false);
				mTextViewKpa50.setSelected(false);
				mTextViewKpa60.setSelected(false);
				mTextViewKpa70.setSelected(false);
				mTextViewKpa80.setSelected(false);
				mTextViewKpa0.setTextSize(16);
				mTextViewKpa30.setTextSize(12);
				mTextViewKpa40.setTextSize(12);
				mTextViewKpa50.setTextSize(12);
				mTextViewKpa60.setTextSize(12);
				mTextViewKpa70.setTextSize(12);
				mTextViewKpa80.setTextSize(12);
				mCookerkpa0.setVisibility(View.VISIBLE);
				mCookerkpa20.setVisibility(View.INVISIBLE);
				mCookerkpa30.setVisibility(View.INVISIBLE);
				mCookerkpa40.setVisibility(View.INVISIBLE);
				mCookerkpa50.setVisibility(View.INVISIBLE);
				mCookerkpa60.setVisibility(View.INVISIBLE);
				mCookerkpa70.setVisibility(View.INVISIBLE);
				mCookerkpa80.setVisibility(View.INVISIBLE);
			}else if("02".equals(cookerPressure)){
				mTextViewKpa20.setSelected(true);
				mTextViewKpa0.setSelected(false);
				mTextViewKpa30.setSelected(false);
				mTextViewKpa40.setSelected(false);
				mTextViewKpa50.setSelected(false);
				mTextViewKpa60.setSelected(false);
				mTextViewKpa70.setSelected(false);
				mTextViewKpa80.setSelected(false);
				mTextViewKpa0.setTextSize(12);
				mTextViewKpa20.setTextSize(16);
				mTextViewKpa30.setTextSize(12);
				mTextViewKpa40.setTextSize(12);
				mTextViewKpa50.setTextSize(12);
				mTextViewKpa60.setTextSize(12);
				mTextViewKpa70.setTextSize(12);
				mTextViewKpa80.setTextSize(12);
				mCookerkpa0.setVisibility(View.INVISIBLE);
				mCookerkpa20.setVisibility(View.VISIBLE);
				mCookerkpa30.setVisibility(View.INVISIBLE);
				mCookerkpa40.setVisibility(View.INVISIBLE);
				mCookerkpa50.setVisibility(View.INVISIBLE);
				mCookerkpa60.setVisibility(View.INVISIBLE);
				mCookerkpa70.setVisibility(View.INVISIBLE);
				mCookerkpa80.setVisibility(View.INVISIBLE);
			}else if("03".equals(cookerPressure)){
				mTextViewKpa30.setSelected(true);
				mTextViewKpa0.setSelected(false);
				mTextViewKpa20.setSelected(false);
				mTextViewKpa40.setSelected(false);
				mTextViewKpa50.setSelected(false);
				mTextViewKpa60.setSelected(false);
				mTextViewKpa70.setSelected(false);
				mTextViewKpa80.setSelected(false);
				mTextViewKpa0.setTextSize(12);
				mTextViewKpa20.setTextSize(12);
				mTextViewKpa30.setTextSize(16);
				mTextViewKpa40.setTextSize(12);
				mTextViewKpa50.setTextSize(12);
				mTextViewKpa60.setTextSize(12);
				mTextViewKpa70.setTextSize(12);
				mTextViewKpa80.setTextSize(12);
				mCookerkpa0.setVisibility(View.INVISIBLE);
				mCookerkpa20.setVisibility(View.INVISIBLE);
				mCookerkpa30.setVisibility(View.VISIBLE);
				mCookerkpa40.setVisibility(View.INVISIBLE);
				mCookerkpa50.setVisibility(View.INVISIBLE);
				mCookerkpa60.setVisibility(View.INVISIBLE);
				mCookerkpa70.setVisibility(View.INVISIBLE);
				mCookerkpa80.setVisibility(View.INVISIBLE);
			}else if("04".equals(cookerPressure)){
				mTextViewKpa40.setSelected(true);
				mTextViewKpa0.setSelected(false);
				mTextViewKpa20.setSelected(false);
				mTextViewKpa30.setSelected(false);
				mTextViewKpa50.setSelected(false);
				mTextViewKpa60.setSelected(false);
				mTextViewKpa70.setSelected(false);
				mTextViewKpa80.setSelected(false);
				mTextViewKpa0.setTextSize(12);
				mTextViewKpa20.setTextSize(12);
				mTextViewKpa30.setTextSize(12);
				mTextViewKpa40.setTextSize(16);
				mTextViewKpa50.setTextSize(12);
				mTextViewKpa60.setTextSize(12);
				mTextViewKpa70.setTextSize(12);
				mTextViewKpa80.setTextSize(12);
				mCookerkpa0.setVisibility(View.INVISIBLE);
				mCookerkpa20.setVisibility(View.INVISIBLE);
				mCookerkpa30.setVisibility(View.INVISIBLE);
				mCookerkpa40.setVisibility(View.VISIBLE);
				mCookerkpa50.setVisibility(View.INVISIBLE);
				mCookerkpa60.setVisibility(View.INVISIBLE);
				mCookerkpa70.setVisibility(View.INVISIBLE);
				mCookerkpa80.setVisibility(View.INVISIBLE);
			}else if("05".equals(cookerPressure)){
				mTextViewKpa50.setSelected(true);
				mTextViewKpa0.setSelected(false);
				mTextViewKpa20.setSelected(false);
				mTextViewKpa30.setSelected(false);
				mTextViewKpa40.setSelected(false);
				mTextViewKpa60.setSelected(false);
				mTextViewKpa70.setSelected(false);
				mTextViewKpa80.setSelected(false);
				mTextViewKpa0.setTextSize(12);
				mTextViewKpa20.setTextSize(12);
				mTextViewKpa30.setTextSize(12);
				mTextViewKpa40.setTextSize(12);
				mTextViewKpa50.setTextSize(16);
				mTextViewKpa60.setTextSize(12);
				mTextViewKpa70.setTextSize(12);
				mTextViewKpa80.setTextSize(12);
				mCookerkpa0.setVisibility(View.INVISIBLE);
				mCookerkpa20.setVisibility(View.INVISIBLE);
				mCookerkpa30.setVisibility(View.INVISIBLE);
				mCookerkpa40.setVisibility(View.INVISIBLE);
				mCookerkpa50.setVisibility(View.VISIBLE);
				mCookerkpa60.setVisibility(View.INVISIBLE);
				mCookerkpa70.setVisibility(View.INVISIBLE);
				mCookerkpa80.setVisibility(View.INVISIBLE);
			}else if("06".equals(cookerPressure)){
				mTextViewKpa60.setSelected(true);
				mTextViewKpa0.setSelected(false);
				mTextViewKpa20.setSelected(false);
				mTextViewKpa30.setSelected(false);
				mTextViewKpa40.setSelected(false);
				mTextViewKpa50.setSelected(false);
				mTextViewKpa70.setSelected(false);
				mTextViewKpa80.setSelected(false);
				mTextViewKpa0.setTextSize(12);
				mTextViewKpa20.setTextSize(12);
				mTextViewKpa30.setTextSize(12);
				mTextViewKpa40.setTextSize(12);
				mTextViewKpa50.setTextSize(12);
				mTextViewKpa60.setTextSize(16);
				mTextViewKpa70.setTextSize(12);
				mTextViewKpa80.setTextSize(12);
				mCookerkpa0.setVisibility(View.INVISIBLE);
				mCookerkpa20.setVisibility(View.INVISIBLE);
				mCookerkpa30.setVisibility(View.INVISIBLE);
				mCookerkpa40.setVisibility(View.INVISIBLE);
				mCookerkpa50.setVisibility(View.INVISIBLE);
				mCookerkpa60.setVisibility(View.VISIBLE);
				mCookerkpa70.setVisibility(View.INVISIBLE);
				mCookerkpa80.setVisibility(View.INVISIBLE);
			}else if("07".equals(cookerPressure)){
				mTextViewKpa70.setSelected(true);
				mTextViewKpa0.setSelected(false);
				mTextViewKpa20.setSelected(false);
				mTextViewKpa30.setSelected(false);
				mTextViewKpa40.setSelected(false);
				mTextViewKpa50.setSelected(false);
				mTextViewKpa60.setSelected(false);
				mTextViewKpa80.setSelected(false);
				mTextViewKpa0.setTextSize(12);
				mTextViewKpa20.setTextSize(12);
				mTextViewKpa30.setTextSize(12);
				mTextViewKpa40.setTextSize(12);
				mTextViewKpa50.setTextSize(12);
				mTextViewKpa60.setTextSize(12);
				mTextViewKpa70.setTextSize(16);
				mTextViewKpa80.setTextSize(12);
				mCookerkpa0.setVisibility(View.INVISIBLE);
				mCookerkpa20.setVisibility(View.INVISIBLE);
				mCookerkpa30.setVisibility(View.INVISIBLE);
				mCookerkpa40.setVisibility(View.INVISIBLE);
				mCookerkpa50.setVisibility(View.INVISIBLE);
				mCookerkpa60.setVisibility(View.INVISIBLE);
				mCookerkpa70.setVisibility(View.VISIBLE);
				mCookerkpa80.setVisibility(View.INVISIBLE);
			}else if("08".equals(cookerPressure)){
				mTextViewKpa80.setSelected(true);
				mTextViewKpa0.setSelected(false);
				mTextViewKpa20.setSelected(false);
				mTextViewKpa30.setSelected(false);
				mTextViewKpa40.setSelected(false);
				mTextViewKpa50.setSelected(false);
				mTextViewKpa60.setSelected(false);
				mTextViewKpa70.setSelected(false);
				mTextViewKpa0.setTextSize(12);
				mTextViewKpa20.setTextSize(12);
				mTextViewKpa30.setTextSize(12);
				mTextViewKpa40.setTextSize(12);
				mTextViewKpa50.setTextSize(12);
				mTextViewKpa60.setTextSize(12);
				mTextViewKpa70.setTextSize(12);
				mTextViewKpa80.setTextSize(16);
				mCookerkpa0.setVisibility(View.INVISIBLE);
				mCookerkpa20.setVisibility(View.INVISIBLE);
				mCookerkpa30.setVisibility(View.INVISIBLE);
				mCookerkpa40.setVisibility(View.INVISIBLE);
				mCookerkpa50.setVisibility(View.INVISIBLE);
				mCookerkpa60.setVisibility(View.INVISIBLE);
				mCookerkpa70.setVisibility(View.INVISIBLE);
				mCookerkpa80.setVisibility(View.VISIBLE);
			}
			
			
			if("01".equals(cookerWork)){
				mTextViewbooster.setSelected(true);
				mTextViewBoiling.setSelected(false);
				mTextViewHolding.setSelected(false);
				mTextViewBuck.setSelected(false);
				mTextViewComplete.setSelected(false);
			}else if("02".equals(cookerWork)){
				mTextViewbooster.setSelected(false);
				mTextViewBoiling.setSelected(true);
				mTextViewHolding.setSelected(false);
				mTextViewBuck.setSelected(false);
				mTextViewComplete.setSelected(false);
			}else if("03".equals(cookerWork)){
				mTextViewbooster.setSelected(false);
				mTextViewBoiling.setSelected(false);
				mTextViewHolding.setSelected(true);
				mTextViewBuck.setSelected(false);
				mTextViewComplete.setSelected(false);
			}else if("04".equals(cookerWork)){
				mTextViewbooster.setSelected(false);
				mTextViewBoiling.setSelected(false);
				mTextViewHolding.setSelected(false);
				mTextViewBuck.setSelected(true);
				mTextViewComplete.setSelected(false);
			}else if("05".equals(cookerWork)){
				mTextViewbooster.setSelected(false);
				mTextViewBoiling.setSelected(false);
				mTextViewHolding.setSelected(false);
				mTextViewBuck.setSelected(false);
				mTextViewComplete.setSelected(true);
			}
			
		}else{
			cookerState = "05";
			isState = true;
			cookerFunction = "01";
			cookerPalate = "00";
			cookerPressure = "00";
			
			cookerPressureTimeH = "00";
			cookerPressureTimeM = "00";
		
			cookerAppointmentTimeHour = "00";
			cookerAppointmentTimeMinues = "00";
			timeHour = 0;
			timeMinues = 0;
		}
	}
	
	
}
