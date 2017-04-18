package cc.wulian.app.model.device.impls.controlable.newthermostat.program;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;

import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.dao.Command406_DeviceConfigMsg;
import cc.wulian.smarthomev5.dao.ICommand406_Result;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.app.model.device.impls.controlable.newthermostat.Thermostat82ViewBulider;
import cc.wulian.app.model.device.impls.controlable.newthermostat.ThermostatDialogManager;
import cc.wulian.app.model.device.impls.controlable.newthermostat.setting.ThermostatSettingFragment;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.DisplayUtil;

public class ThermostatProgramFragment extends WulianFragment implements OnClickListener,ICommand406_Result{

	private final String TAG = getClass().getSimpleName();
	
	private ThermostatDialogManager dialogManager = ThermostatDialogManager.getDialogManager();
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	
	private DeviceCache cache;
	private AbstractDevice device;
	private String mEpData;
	private String mReturnId;
	private String mMode;
	private String mTempUnit;
	
	@ViewInject(R.id.thermost_program_monday)
	private ImageButton btnMonday;
	@ViewInject(R.id.thermost_program_tuesday)
	private ImageButton btnTuesday;
	@ViewInject(R.id.thermost_program_wednesday)
	private ImageButton btnWednesday;
	@ViewInject(R.id.thermost_program_thursday)
	private ImageButton btnThursday;
	@ViewInject(R.id.thermost_program_friday)
	private ImageButton btnFriday;
	@ViewInject(R.id.thermost_program_saturday)
	private ImageButton btnSaturday;
	@ViewInject(R.id.thermost_program_sunday)
	private ImageButton btnSunday;
	@ViewInject(R.id.thermost_program_copy)
	private ImageButton btnCopyday;
	@ViewInject(R.id.thermost_program_data_bg)
	private ImageView imageDataBg;
	@ViewInject(R.id.thermost_program_delete)
	private ImageButton btnCancel;
	@ViewInject(R.id.thermost_program_sync)
	private ImageButton btnSync;
	@ViewInject(R.id.thermost_program_temp_tv)
	private TextView tvTemp;
	@ViewInject(R.id.thermostat_programView)
	private ProgramView programView;
	@ViewInject(R.id.thermostat_programViewAuto)
	private ProgramViewAuto programViewAuto;
	
	private List<ProgramBall> initialDataList;
	private Map<String, List<ProgramBall>> initialDataMap;
	private List<ProgramBall> curDataList;
	private Map<String, List<ProgramBall>> curDataMap;
	private boolean isViewAutoShow = false;
	private List<ProgramBallAuto> initialDataListAuto;
	private Map<String, List<ProgramBallAuto>> initialDataMapAuto;
	private List<ProgramBallAuto> curDataListAuto;
	private Map<String, List<ProgramBallAuto>> curDataMapAuto;
	private String week;
	private int ballWidth;
	//数据改变提示窗口
	private WLDialog warningDialog;
	//sync同步二次确认，提示窗口
	private WLDialog syncDialog;
	private String syncMessage;
	private int SYNC_DIALOG_WIDTH = DisplayUtil.dip2Pix(mApplication, 350);
	private int SYNC_DIALOG_HEIGHT = DisplayUtil.dip2Pix(mApplication, 220);

	private static final String CURRENT_QUERY_CMD_DATA  = "11";
	private static final String PROGRAM_CMD_TAG = "B";
	private static final String PROGRAM_AUTO_CMD_TAG = "C";
	
	private static final String HEAT_CMD_TAG = "1";
	private static final String COOL_CMD_TAG = "2";
	private static final String AUTO_CMD_TAG = "3";
	
	private static final String MON_TAG = "1";
	private static final String TUE_TAG = "2";
	private static final String WED_TAG = "3";
	private static final String THUR_TAG = "4";
	private static final String FRI_TAG = "5";
	private static final String SAT_TAG = "6";
	private static final String SUN_TAG = "0";
	
	private static final String RECIVE_FAILED_CMD = "8";
	private static final String RECIVE_SUCCESS_CMD1 = "0B";
	private static final String RECIVE_SUCCESS_CMD2 = "0C";
	
	private int DRAWERBLE_MONDAY_01 = R.drawable.thermost_program_monday_01;
	private int DRAWERBLE_MONDAY_02 = R.drawable.thermost_program_monday_02;
	private int DRAWERBLE_TUESDAY_01 = R.drawable.thermost_program_tuesday_01;
	private int DRAWERBLE_TUESDAY_02 = R.drawable.thermost_program_tuesday_02;
	private int DRAWERBLE_WEDNESDAY_01 = R.drawable.thermost_program_wednesday_01;
	private int DRAWERBLE_WEDNESDAY_02 = R.drawable.thermost_program_wednesday_02;
	private int DRAWERBLE_THURSDAY_01 = R.drawable.thermost_program_thursday_01;
	private int DRAWERBLE_THURSDAY_02 = R.drawable.thermost_program_thursday_02;
	private int DRAWERBLE_FRIDAY_01 = R.drawable.thermost_program_friday_01;
	private int DRAWERBLE_FRIDAY_02 = R.drawable.thermost_program_friday_02;
	private int DRAWERBLE_SATURDAY_01 = R.drawable.thermost_program_saturday_01;
	private int DRAWERBLE_SATURDAY_02 = R.drawable.thermost_program_saturday_02;
	private int DRAWERBLE_SUNDAY_01 = R.drawable.thermost_program_sunday_01;
	private int DRAWERBLE_SUNDAY_02 = R.drawable.thermost_program_sunday_02;
	
	private int DRAWERBLE_TEMP_C = R.drawable.thermost_program_data;
	private int DRAWERBLE_TEMP_F = R.drawable.thermost_program_data_02;

	private int DRAWERABLE_SYNC_NORMAL = R.drawable.thermost_program_sync_selector;
	private int DRAWERABLE_SYNC_CHANGE = R.drawable.thermost_program_sync_selector_02;

	private static final String COPY_DAY_WARNING_TAG = "1";
	private static final String CHANGE_DAY_WARNING_TAG = "2";
	private static final String CANCEL_WARNING_TAG = "3";

	private static final String COPY_DAY_WARNING_MESSAGE =
			"Modified information has not been synchronized into device, conform to copy to the next day? ";
	private static final String CANCEL_WARNING_MESSAGE  =
			"Modified information has not been synchronized into device, conform to exit now? ";
	private static final String CHANGE_DAY_WARNING_MESSAGE =
			"Modified information has not been synchronized into device, conform to leave now? ";

	private static final String MODE_TEXT_HEAT = "heat temperature";
	private static final String MODE_TEXT_COOL = "cool temperature";
	private static final String MODE_TEXT_AUTO = "auto temperature";

	private static final String TEXT_WORNING_MONDAY = "Sync the time (set on the App) to the device on Monday";
	private static final String TEXT_WORNING_TUESDAY = "Sync the time (set on the App) to the device on Tuesday";
	private static final String TEXT_WORNING_WEDNEDAY= "Sync the time (set on the App) to the device on Wednesday";
	private static final String TEXT_WORNING_THRUSDAY = "Sync the time (set on the App) to the device on Thursday";
	private static final String TEXT_WORNING_FRIDAY = "Sync the time (set on the App) to the device on Friday";
	private static final String TEXT_WORNING_SATURDAY= "Sync the time (set on the App) to the device on Saturday";
	private static final String TEXT_WORNING_SUNDAY= "Sync the time (set on the App) to the device on Sunday";
	//CMD406命令
	private Command406_DeviceConfigMsg command406=null;
	private boolean isCmd406Send = false;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		Bundle bundle = getArguments().getBundle("ThermostatProgramInfo");
		mGwId = bundle.getString(ThermostatSettingFragment.GWID);
		mDevId = bundle.getString(ThermostatSettingFragment.DEVID);
		mEp = bundle.getString(ThermostatSettingFragment.EP);
		mEpType = bundle.getString(ThermostatSettingFragment.EPTYPE);
		mMode = bundle.getString("mode");
		mTempUnit = bundle.getString("tempUnit");
		cache=DeviceCache.getInstance(mActivity);
		ballWidth = DisplayUtil.dip2Pix(mActivity, 13);
		
		command406=new Command406_DeviceConfigMsg(mActivity);
		command406.setConfigMsg(this);
		command406.setDevID(mDevId);
		command406.setGwID(mGwId);
		
		curDataList = new ArrayList<ProgramBall>();
		curDataMap = new HashMap<String, List<ProgramBall>>();
		curDataListAuto = new ArrayList<ProgramBallAuto>();
		curDataMapAuto = new HashMap<String, List<ProgramBallAuto>>();
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.device_thermostat82_program, container, false);
		ViewUtils.inject(this,rootView);
		return rootView;
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		initView();
		if(!isCmd406Send){
			SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, CURRENT_QUERY_CMD_DATA);
			command406.SendCommand_Get();
			isCmd406Send = true;
		}
		programView.setmMoveChanged(new ProgramView.OnMoveValueChangedable() {
			@Override
			public void onMoveChanged() {
				btnSync.setBackgroundResource(DRAWERABLE_SYNC_CHANGE);
			}
		});
		programViewAuto.setmMoveChanged(new ProgramViewAuto.OnMoveValueChangedable() {
			@Override
			public void onMoveChanged() {
				btnSync.setBackgroundResource(DRAWERABLE_SYNC_CHANGE);
			}
		});
	}
	
	@Override
	public void onStart() {
		super.onStart();
	}
	
	private void initView(){
		setmodeView(mMode);
		setTempType(mTempUnit);
		setImageDataBg(mTempUnit);
		loadData();
		btnMonday.setOnClickListener(this);
		btnTuesday.setOnClickListener(this);
		btnWednesday.setOnClickListener(this);
		btnThursday.setOnClickListener(this);
		btnFriday.setOnClickListener(this);
		btnSaturday.setOnClickListener(this);
		btnSunday.setOnClickListener(this);
		btnCopyday.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnSync.setOnClickListener(this);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		initBar();
		setmodeView(mMode);
		setTempType(mTempUnit);
		setImageDataBg(mTempUnit);
		setMonSelected();
	}
	
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().hide();
	}
	
	
	private String getCmd406(String mWeek){
		
		StringBuilder cmd406Builder = new StringBuilder();
		cmd406Builder.append("programData");
		cmd406Builder.append(Integer.parseInt(mMode));
		cmd406Builder.append(mWeek);
		
		return cmd406Builder.toString();
	}
	
	private void setTempType(String tempUnit){
		if(!StringUtil.isNullOrEmpty(tempUnit)){
			if(StringUtil.equals(tempUnit, Thermostat82ViewBulider.TEMP_UNIT_C)){
				programView.setTempUnitC(true);
				programViewAuto.setTempUnitC(true);
			}else{
				programView.setTempUnitC(false);
				programViewAuto.setTempUnitC(false);
			}
		}
		programView.setMaxTemp(32);
		programView.setMinTemp(10);
		programViewAuto.setMaxTemp(32);
		programViewAuto.setMinTemp(10);
	}
	
	private void setmodeView(String mode){
		if(!StringUtil.isNullOrEmpty(mode)){
			programView.setMode(mode);
			if(StringUtil.equals(mode, Thermostat82ViewBulider.MODE_HEAT)){
				tvTemp.setText(MODE_TEXT_HEAT);
				programView.setVisibility(View.VISIBLE);
				programViewAuto.setVisibility(View.GONE);
				isViewAutoShow = false;
			}
			else if(StringUtil.equals(mode, Thermostat82ViewBulider.MODE_COOL)){
				tvTemp.setText(MODE_TEXT_COOL);
				programView.setVisibility(View.VISIBLE);
				programViewAuto.setVisibility(View.GONE);
				isViewAutoShow = false;
			}else{
				tvTemp.setText(MODE_TEXT_AUTO);
				programView.setVisibility(View.GONE);
				programViewAuto.setVisibility(View.VISIBLE);
				isViewAutoShow = true;
			}
		}
	}
	
	private void setImageDataBg(String tempUnit){
		if(!StringUtil.isNullOrEmpty(tempUnit)){
			if(StringUtil.equals(tempUnit, Thermostat82ViewBulider.TEMP_UNIT_C)){
				imageDataBg.setBackgroundResource(DRAWERBLE_TEMP_C);
			}else{
				imageDataBg.setBackgroundResource(DRAWERBLE_TEMP_F);
			}
		}else{
			imageDataBg.setBackgroundResource(DRAWERBLE_TEMP_C);
		}
	}
	
	private void loadData(){
		mActivity.runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				initDataList();
				setMonSelected();
				setProgramViewThumb();
			}
		});
		
	}
	
	private void initDataList(){
		if(isViewAutoShow){
			if(initialDataListAuto==null){
				initialDataListAuto = new ArrayList<ProgramBallAuto>();
				initialDataListAuto.add(new ProgramBallAuto("25","21","24", ballWidth));
				initialDataListAuto.add(new ProgramBallAuto("29","16","32", ballWidth));
				initialDataListAuto.add(new ProgramBallAuto("29","16","48", ballWidth));
				initialDataListAuto.add(new ProgramBallAuto("29","16","56", ballWidth));
				initialDataListAuto.add(new ProgramBallAuto("25","21","72", ballWidth));
				initialDataListAuto.add(new ProgramBallAuto("28","16","88", ballWidth));
			}
			if(initialDataMapAuto==null){
				initialDataMapAuto = new HashMap<String, List<ProgramBallAuto>>();
				initialDataMapAuto.put(MON_TAG, initialDataListAuto);
				initialDataMapAuto.put(TUE_TAG, initialDataListAuto);
				initialDataMapAuto.put(WED_TAG, initialDataListAuto);
				initialDataMapAuto.put(THUR_TAG, initialDataListAuto);
				initialDataMapAuto.put(FRI_TAG, initialDataListAuto);
				initialDataMapAuto.put(SAT_TAG, initialDataListAuto);
				initialDataMapAuto.put(SUN_TAG, initialDataListAuto);
			}
			
			for (String key : initialDataMapAuto.keySet()) {
				curDataMapAuto.put(key, initialDataMapAuto.get(key));
			}
			
		}else{
			
			if(initialDataList==null){
				initialDataList = new ArrayList<ProgramBall>();
				if(!StringUtil.isNullOrEmpty(mMode)){
					if(StringUtil.equals(mMode, Thermostat82ViewBulider.MODE_HEAT)) {
						initialDataList.add(new ProgramBall("21", "24", ballWidth));
						initialDataList.add(new ProgramBall("16", "32", ballWidth));
						initialDataList.add(new ProgramBall("16", "48", ballWidth));
						initialDataList.add(new ProgramBall("16", "56", ballWidth));
						initialDataList.add(new ProgramBall("21", "72", ballWidth));
						initialDataList.add(new ProgramBall("16", "88", ballWidth));
					}
					else if(StringUtil.equals(mMode, Thermostat82ViewBulider.MODE_COOL)){
						initialDataList.add(new ProgramBall("25", "24", ballWidth));
						initialDataList.add(new ProgramBall("29", "32", ballWidth));
						initialDataList.add(new ProgramBall("29", "48", ballWidth));
						initialDataList.add(new ProgramBall("29", "56", ballWidth));
						initialDataList.add(new ProgramBall("25", "72", ballWidth));
						initialDataList.add(new ProgramBall("28", "88", ballWidth));
					}
				}
			}
			if(initialDataMap==null){
				initialDataMap = new HashMap<String, List<ProgramBall>>();
				initialDataMap.put(MON_TAG, initialDataList);
				initialDataMap.put(TUE_TAG, initialDataList);
				initialDataMap.put(WED_TAG, initialDataList);
				initialDataMap.put(THUR_TAG, initialDataList);
				initialDataMap.put(FRI_TAG, initialDataList);
				initialDataMap.put(SAT_TAG, initialDataList);
				initialDataMap.put(SUN_TAG, initialDataList);
			}
			
			for (String key : initialDataMap.keySet()) {
				curDataMap.put(key, initialDataMap.get(key));
			}
		}
		
	}
	
	private void setProgramViewThumb(){
		if(isViewAutoShow){
			programViewAuto.setThumbList(curDataMapAuto.get(week));
		}else{
	
			programView.setThumbList(curDataMap.get(week));
		}
	}
	
	private void setMonSelected(){
		week = MON_TAG;
		btnMonday.setBackgroundResource(DRAWERBLE_MONDAY_02);
		btnTuesday.setBackgroundResource(DRAWERBLE_TUESDAY_01);
		btnWednesday.setBackgroundResource(DRAWERBLE_WEDNESDAY_01);
		btnThursday.setBackgroundResource(DRAWERBLE_THURSDAY_01);
		btnFriday.setBackgroundResource(DRAWERBLE_FRIDAY_01);
		btnSaturday.setBackgroundResource(DRAWERBLE_SATURDAY_01);
		btnSunday.setBackgroundResource(DRAWERBLE_SUNDAY_01);
		
		syncMessage = TEXT_WORNING_MONDAY;
	}
	
	private void setTuesSelected(){
		week = TUE_TAG;
		btnMonday.setBackgroundResource(DRAWERBLE_MONDAY_01);
		btnTuesday.setBackgroundResource(DRAWERBLE_TUESDAY_02);
		btnWednesday.setBackgroundResource(DRAWERBLE_WEDNESDAY_01);
		btnThursday.setBackgroundResource(DRAWERBLE_THURSDAY_01);
		btnFriday.setBackgroundResource(DRAWERBLE_FRIDAY_01);
		btnSaturday.setBackgroundResource(DRAWERBLE_SATURDAY_01);
		btnSunday.setBackgroundResource(DRAWERBLE_SUNDAY_01);

		syncMessage = TEXT_WORNING_TUESDAY;
	}
	
	private void setWedSelected(){
		week = WED_TAG;
		btnMonday.setBackgroundResource(DRAWERBLE_MONDAY_01);
		btnTuesday.setBackgroundResource(DRAWERBLE_TUESDAY_01);
		btnWednesday.setBackgroundResource(DRAWERBLE_WEDNESDAY_02);
		btnThursday.setBackgroundResource(DRAWERBLE_THURSDAY_01);
		btnFriday.setBackgroundResource(DRAWERBLE_FRIDAY_01);
		btnSaturday.setBackgroundResource(DRAWERBLE_SATURDAY_01);
		btnSunday.setBackgroundResource(DRAWERBLE_SUNDAY_01);

		syncMessage = TEXT_WORNING_WEDNEDAY;
	}
	
	private void setThurSelected(){
		week = THUR_TAG;
		btnMonday.setBackgroundResource(DRAWERBLE_MONDAY_01);
		btnTuesday.setBackgroundResource(DRAWERBLE_TUESDAY_01);
		btnWednesday.setBackgroundResource(DRAWERBLE_WEDNESDAY_01);
		btnThursday.setBackgroundResource(DRAWERBLE_THURSDAY_02);
		btnFriday.setBackgroundResource(DRAWERBLE_FRIDAY_01);
		btnSaturday.setBackgroundResource(DRAWERBLE_SATURDAY_01);
		btnSunday.setBackgroundResource(DRAWERBLE_SUNDAY_01);

		syncMessage = TEXT_WORNING_THRUSDAY;
	}

	private void setFriSelected(){
		week = FRI_TAG;
		btnMonday.setBackgroundResource(DRAWERBLE_MONDAY_01);
		btnTuesday.setBackgroundResource(DRAWERBLE_TUESDAY_01);
		btnWednesday.setBackgroundResource(DRAWERBLE_WEDNESDAY_01);
		btnThursday.setBackgroundResource(DRAWERBLE_THURSDAY_01);
		btnFriday.setBackgroundResource(DRAWERBLE_FRIDAY_02);
		btnSaturday.setBackgroundResource(DRAWERBLE_SATURDAY_01);
		btnSunday.setBackgroundResource(DRAWERBLE_SUNDAY_01);

		syncMessage = TEXT_WORNING_FRIDAY;
	}
	
	private void setSatSelected(){
		week = SAT_TAG;
		btnMonday.setBackgroundResource(DRAWERBLE_MONDAY_01);
		btnTuesday.setBackgroundResource(DRAWERBLE_TUESDAY_01);
		btnWednesday.setBackgroundResource(DRAWERBLE_WEDNESDAY_01);
		btnThursday.setBackgroundResource(DRAWERBLE_THURSDAY_01);
		btnFriday.setBackgroundResource(DRAWERBLE_FRIDAY_01);
		btnSaturday.setBackgroundResource(DRAWERBLE_SATURDAY_02);
		btnSunday.setBackgroundResource(DRAWERBLE_SUNDAY_01);

		syncMessage = TEXT_WORNING_SATURDAY;
	}
	
	private void setSunSelected(){
		week = SUN_TAG;
		btnMonday.setBackgroundResource(DRAWERBLE_MONDAY_01);
		btnTuesday.setBackgroundResource(DRAWERBLE_TUESDAY_01);
		btnWednesday.setBackgroundResource(DRAWERBLE_WEDNESDAY_01);
		btnThursday.setBackgroundResource(DRAWERBLE_THURSDAY_01);
		btnFriday.setBackgroundResource(DRAWERBLE_FRIDAY_01);
		btnSaturday.setBackgroundResource(DRAWERBLE_SATURDAY_01);
		btnSunday.setBackgroundResource(DRAWERBLE_SUNDAY_02);

		syncMessage = TEXT_WORNING_SUNDAY;
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.thermost_program_monday:
			if(programView.isBallChanged() || programViewAuto.isBallChanged()){
				showWarningDialog(CHANGE_DAY_WARNING_TAG , MON_TAG);
			}else {
				setMonSelected();
				setProgramViewThumb();
			}
//			command406.SendCommand_Get(getCmd406(week));
			break;
		case R.id.thermost_program_tuesday:
			if(programView.isBallChanged() || programViewAuto.isBallChanged()){
				showWarningDialog(CHANGE_DAY_WARNING_TAG , TUE_TAG);
			}else {
				setTuesSelected();
				setProgramViewThumb();
			}
			break;
		case R.id.thermost_program_wednesday:
			if(programView.isBallChanged() || programViewAuto.isBallChanged()){
				showWarningDialog(CHANGE_DAY_WARNING_TAG , WED_TAG);
			}else {
				setWedSelected();
				setProgramViewThumb();
			}
			break;
		case R.id.thermost_program_thursday:
			if(programView.isBallChanged() || programViewAuto.isBallChanged()){
				showWarningDialog(CHANGE_DAY_WARNING_TAG , THUR_TAG);
			}else {
				setThurSelected();
				setProgramViewThumb();
			}
			break;
		case R.id.thermost_program_friday:
			if(programView.isBallChanged() || programViewAuto.isBallChanged()){
				showWarningDialog(CHANGE_DAY_WARNING_TAG , FRI_TAG);
			}else {
				setFriSelected();
				setProgramViewThumb();
			}
			break;
		case R.id.thermost_program_saturday:
			if(programView.isBallChanged() || programViewAuto.isBallChanged()){
				showWarningDialog(CHANGE_DAY_WARNING_TAG , SAT_TAG);
			}else {
				setSatSelected();
				setProgramViewThumb();
			}
			break;
		case R.id.thermost_program_sunday:
			if(programView.isBallChanged() || programViewAuto.isBallChanged()){
				showWarningDialog(CHANGE_DAY_WARNING_TAG , SUN_TAG);
			}else {
				setSunSelected();
				setProgramViewThumb();
			}
			break;
		case R.id.thermost_program_copy:
			if(programView.isBallChanged() || programViewAuto.isBallChanged()){
				showWarningDialog(COPY_DAY_WARNING_TAG , null);
			}else {
				copyDay();
			}
			break;
		case R.id.thermost_program_delete:
			if(programView.isBallChanged() || programViewAuto.isBallChanged()){
				showWarningDialog(CANCEL_WARNING_TAG , null);
			}else{
				cancel();
			}
			break;
		case R.id.thermost_program_sync:
			showSyncDialog();
			break;
		}
	}

	private void showSyncDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);

		builder.setContentView(R.layout.device_thermostat82_program_dialog_content)
				.setTitle("Prompt")
				.setSubTitleText(null)
//				.setMessage(syncMessage)
				.setNegativeButton("Cancel")
				.setPositiveButton("Ok")
				.setHeight(SYNC_DIALOG_HEIGHT)
				.setWidth(SYNC_DIALOG_WIDTH)
				.setDismissAfterDone(true).setListener(new WLDialog.MessageListener() {

			@Override
			public void onClickPositive(View contentViewLayout) {
				if(isViewAutoShow){
					List<ProgramBallAuto>  syncDataListAuto = new ArrayList<ProgramBallAuto>();
					for (ProgramBallAuto ball : programViewAuto.getThumbList()) {
						syncDataListAuto.add(ball);
					}
					if(syncDataListAuto != null && (!syncDataListAuto.isEmpty())){
						curDataMapAuto.put(week, syncDataListAuto);
						sync();
						showResetDialog();
					}
				}else{
					List<ProgramBall>  syncDataList = new ArrayList<ProgramBall>();
					for (ProgramBall ball : programView.getThumbList()) {
						syncDataList.add(ball);
					}
					if(syncDataList != null && (!syncDataList.isEmpty())){
						curDataMap.put(week, syncDataList);
						sync();
						showResetDialog();
					}
				}
				command406.SendCommand_Get();
			}

			@Override
			public void onClickNegative(View contentViewLayout) {

			}
		});
		syncDialog = builder.create();
		TextView tvDialog = (TextView) syncDialog.findViewById(R.id.thermost_program_dialog_tv);
		tvDialog.setText(syncMessage);
		syncDialog.show();
	}

	private void showWarningDialog(final String warningType,final String selectWeek){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle("Prompt");
		builder.setSubTitleText(null);
		builder.setContentView(R.layout.device_thermostat82_program_dialog_content);
		builder.setNegativeButton("Cancel");
		builder.setPositiveButton("Ok");
		builder.setHeight(SYNC_DIALOG_HEIGHT);
		builder.setWidth(SYNC_DIALOG_WIDTH);
		builder.setListener(new WLDialog.MessageListener() {
			@Override
			public void onClickPositive(View view) {
				if(StringUtil.equals(warningType ,COPY_DAY_WARNING_TAG)){
					copyDay();
					programView.setBallChanged(false);
					programViewAuto.setBallChanged(false);
					btnSync.setBackgroundResource(DRAWERABLE_SYNC_NORMAL);
				}else if(StringUtil.equals(warningType ,CHANGE_DAY_WARNING_TAG)){
					if(StringUtil.equals( selectWeek,MON_TAG)){
						setMonSelected();
					}else if(StringUtil.equals( selectWeek,TUE_TAG)){
						setTuesSelected();
					}else if(StringUtil.equals( selectWeek,WED_TAG)){
						setWedSelected();
					}else if(StringUtil.equals( selectWeek,THUR_TAG)){
						setThurSelected();
					}else if(StringUtil.equals( selectWeek,FRI_TAG)){
						setFriSelected();
					}else if(StringUtil.equals( selectWeek,SAT_TAG)){
						setSatSelected();
					}else if(StringUtil.equals( selectWeek,SUN_TAG)){
						setSunSelected();
					}
					setProgramViewThumb();
					programView.setBallChanged(false);
					programViewAuto.setBallChanged(false);
					btnSync.setBackgroundResource(DRAWERABLE_SYNC_NORMAL);
				}else if(StringUtil.equals(warningType ,CANCEL_WARNING_TAG)){
					cancel();
				}
			}

			@Override
			public void onClickNegative(View view) {

			}
		});
		warningDialog = builder.create();
		TextView tvDialog = (TextView) warningDialog.findViewById(R.id.thermost_program_dialog_tv);
		if(StringUtil.equals(warningType ,COPY_DAY_WARNING_TAG)){
			tvDialog.setText(COPY_DAY_WARNING_MESSAGE);
		}else if(StringUtil.equals(warningType ,CHANGE_DAY_WARNING_TAG)){
			tvDialog.setText(CHANGE_DAY_WARNING_MESSAGE);
		}else if(StringUtil.equals(warningType ,CANCEL_WARNING_TAG)){
			tvDialog.setText(CANCEL_WARNING_MESSAGE);
		}
		warningDialog.show();
	}
	
	private void copyDay(){
		if(isViewAutoShow){
			curDataListAuto = programViewAuto.getThumbList();
//			curDataMapAuto.put(week, curDataListAuto);
		}else{
			curDataList = programView.getThumbList();
//			curDataMap.put(week, curDataList);
		}
		if(StringUtil.equals(week, MON_TAG)){
			setTuesSelected();
		}
		else if(StringUtil.equals(week, TUE_TAG)){
			setWedSelected();
		}
		else if(StringUtil.equals(week, WED_TAG)){
			setThurSelected();
		}
		else if(StringUtil.equals(week, THUR_TAG)){
			setFriSelected();
		}
		else if(StringUtil.equals(week, FRI_TAG)){
			setSatSelected();
		}
		else if(StringUtil.equals(week, SAT_TAG)){
			setSunSelected();
		}
		else{
			setMonSelected();
		}
		
//		if(isViewAutoShow){
//			curDataMapAuto.put(week, curDataListAuto);
//		}else{
//			curDataMap.put(week, curDataList);
//		}
		
//		setProgramViewThumb();
	}
	
	private void sync(){
//		curDataList = dataMap.get(week);
		String dataCmd = week+getDataCmd();
		String sendCmd;
		
		if(StringUtil.equals(mMode, Thermostat82ViewBulider.MODE_HEAT)){
			sendCmd = PROGRAM_CMD_TAG + HEAT_CMD_TAG + dataCmd;
		}
		else if(StringUtil.equals(mMode, Thermostat82ViewBulider.MODE_COOL)){
			
			sendCmd = PROGRAM_CMD_TAG + COOL_CMD_TAG + dataCmd;
		}else{
			sendCmd = PROGRAM_AUTO_CMD_TAG + AUTO_CMD_TAG + dataCmd;
		}

		SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, sendCmd);
		command406.SendCommand_Add(getCmd406(week), getDataCmd406(sendCmd));
	}

	private void cancel(){
		getActivity().finish();
	}
	
	private String getDataCmd(){
		StringBuilder cmdBulider = new StringBuilder();
		if(isViewAutoShow){
			
			for (ProgramBallAuto ball : curDataMapAuto.get(week)) {
				cmdBulider.append(StringUtil.appendLeft(ball.time, 2, '0'));
				cmdBulider.append(ball.tempHeat);
				cmdBulider.append(ball.tempCool);
			}
		}else{
			
			for (ProgramBall ball : curDataMap.get(week)) {
				cmdBulider.append(StringUtil.appendLeft(ball.time, 2, '0'));
				cmdBulider.append(ball.temp);
			}
		}
		return cmdBulider.toString();
	}
	
	private String getDataCmd406(String sendData){
		JSONObject jsonObject = new JSONObject();
		try {
			jsonObject.put("programData", sendData);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return jsonObject.toString();
	}
	
	private void showResetDialog(){
		dialogManager.showResetDialog(mActivity);
		new Handler().postDelayed(new Runnable() {
		    @Override
		    public void run() {
		    	showResultFailedDialog();
		    }
		},20000);
	}
	
	private void showResetResultDialog(){
		if(StringUtil.equals(mEpData.substring(0, 1), RECIVE_FAILED_CMD)){
			showResultFailedDialog();
		}

		if(StringUtil.equals(mEpData.substring(0, 2), RECIVE_SUCCESS_CMD1) || 
				StringUtil.equals(mEpData.substring(0, 2), RECIVE_SUCCESS_CMD2)){
			showResultSuccessDialog();
			programView.setBallChanged(false);
			programViewAuto.setBallChanged(false);
			btnSync.setBackgroundResource(DRAWERABLE_SYNC_NORMAL);
		}
		
	}
	
	private void showResultFailedDialog(){
		dialogManager.showFailedDialog(mActivity);
		new Handler().postDelayed(new Runnable() {
		    @Override
		    public void run() {
		    	dialogManager.diamissFialedDialog();
		    }
		},1000);
	}
	
	private void showResultSuccessDialog(){
		dialogManager.showSuccessDialog(mActivity);
		new Handler().postDelayed(new Runnable() {
		    @Override
		    public void run() {
		    	dialogManager.dismissSuccessDialog();
		    }
		},1000);
	}
	
	public void onEventMainThread(DeviceEvent event){
		device=(AbstractDevice) cache.getDeviceByID(mActivity, event.deviceInfo.getGwID(), event.deviceInfo.getDevID());
		mEpData=device.getDeviceInfo().getDevEPInfo().getEpData();
		
		handleEpData(mEpData);
		setmodeView(mMode);
		setTempType(mTempUnit);
		setImageDataBg(mTempUnit);
		showResetResultDialog();
	}

	private void handleEpData(String epData) {
		
		if(!StringUtil.isNullOrEmpty(epData)){
			Log.i(TAG+"-epdata", epData+"-"+epData.length());
			if(epData.length() == 38){
				
				mReturnId = epData.substring(0,2);
				mTempUnit = epData.substring(6, 8);
				mMode = epData.substring(12,14);
			}
			
		}
	}

	//接收406数据
	@Override
	public void Reply406Result(Command406Result result) {
		
		String reciveData = result.getData();
		if(!StringUtil.isNullOrEmpty(reciveData)){
			
			handleResult406(reciveData);
		}
	}
	
	private void handleResult406(String reciveData){
		
		try {
			JSONObject jsonObject = new JSONObject(reciveData);
			if(jsonObject.has("programData")){
				String	resultData = jsonObject.getString("programData");
				String tag = resultData.substring(0,1);
				if(StringUtil.equals(tag, PROGRAM_CMD_TAG)){
					String mode = "0"+resultData.substring(1,2);
					if(StringUtil.equals(mode, mMode)){
						String sWeek = resultData.substring(2,3);
						List<String> dataList = new ArrayList<String>();
						List<ProgramBall> ballList = new ArrayList<ProgramBall>();
						dataList.add(resultData.substring(3,7)); 
						dataList.add(resultData.substring(7,11));
						dataList.add(resultData.substring(11,15));
						dataList.add(resultData.substring(15,19));
						dataList.add(resultData.substring(19,23));
						dataList.add(resultData.substring(23,27));
						
						for (String data : dataList) {
							String time = Integer.parseInt(data.substring(0, 2))+"";
							String temp = data.substring(2, 4);
							ballList.add(new ProgramBall(temp, time, ballWidth));
						}
						curDataMap.put(sWeek, ballList);
						programView.setThumbList(curDataMap.get(week));
					}
					
				}else{
					String sWeek = resultData.substring(2,3);
					List<String> dataList = new ArrayList<String>();
					List<ProgramBallAuto> ballList = new ArrayList<ProgramBallAuto>();
					dataList.add(resultData.substring(3,9)); 
					dataList.add(resultData.substring(9,15));
					dataList.add(resultData.substring(15,21));
					dataList.add(resultData.substring(21,27));
					dataList.add(resultData.substring(27,33));
					dataList.add(resultData.substring(33,39));
					
					for (String data : dataList) {
						String time = Integer.parseInt(data.substring(0, 2))+"";
						String tempHeat = data.substring(2, 4);
						String tempCool = data.substring(4, 6);
						ballList.add(new ProgramBallAuto(tempCool,tempHeat ,time, ballWidth));
					}
					curDataMapAuto.put(sWeek, ballList);
					programViewAuto.setThumbList(curDataMapAuto.get(week));
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		
	}

	@Override
	public void Reply406Result(List<Command406Result> results) {
		
		for(Command406Result result:results){
			Reply406Result(result);
		}
	}

	
	
	
}
