package cc.wulian.app.model.device.impls.controlable.fancoil.program;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.fancoil.FanCoilUtil;
import cc.wulian.app.model.device.impls.controlable.floorwarm.FloorWarmDialogManager;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.dao.Command406_DeviceConfigMsg;
import cc.wulian.smarthomev5.dao.ICommand406_Result;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.DisplayUtil;

public class FanCoilProgramFragment extends WulianFragment implements View.OnClickListener ,ICommand406_Result {

	private final String TAG = getClass().getSimpleName();
	private FloorWarmDialogManager dialogManager = FloorWarmDialogManager.getDialogManager();
	private String mGwId;
	private String mDevId;
	private String mEp;
	private String mEpType;
	private String mEpData;
	private String mReturnId;
	private String mMode ;
	private DeviceCache cache;
	private AbstractDevice device;

	@ViewInject(R.id.fancoil_program_monday)
	private TextView btnMonday;
	@ViewInject(R.id.fancoil_program_tuesday)
	private TextView btnTuesday;
	@ViewInject(R.id.fancoil_program_wednesday)
	private TextView btnWednesday;
	@ViewInject(R.id.fancoil_program_thursday)
	private TextView btnThursday;
	@ViewInject(R.id.fancoil_program_friday)
	private TextView btnFriday;
	@ViewInject(R.id.fancoil_program_saturday)
	private TextView btnSaturday;
	@ViewInject(R.id.fancoil_program_sunday)
	private TextView btnSunday;
	@ViewInject(R.id.fancoil_program_cancel)
	private ImageButton btnCancel;
	@ViewInject(R.id.fancoil_program_copy)
	private Button btnCopy;
	@ViewInject(R.id.fancoil_program_sync)
	private TextView btnSync;
	@ViewInject(R.id.fancoil_programView)
	private ProgramView programView;
	@ViewInject(R.id.fancoil_program_temp_tv)
	private TextView tvMode;

	private List<ProgramBall> initialDataList;
	private Map<String, List<ProgramBall>> initialDataMap;
	private Map<String, List<ProgramBall>> curDataMap;
	private String week;
	private int ballWidth;
	//数据改变提示窗口
	private WLDialog warningDialog;
	//sync同步二次确认，提示窗口
	private WLDialog syncDialog;
	private boolean isCopyFromOtherDay = false;
	private String syncMessage;
	private int SYNC_DIALOG_WIDTH = DisplayUtil.dip2Pix(mApplication, 300);
	private int SYNC_DIALOG_HEIGHT = DisplayUtil.dip2Pix(mApplication, 200);

	private static final String MON_TAG = "1";
	private static final String TUE_TAG = "2";
	private static final String WED_TAG = "3";
	private static final String THUR_TAG = "4";
	private static final String FRI_TAG = "5";
	private static final String SAT_TAG = "6";
	private static final String SUN_TAG = "0";

	//CMD406命令
	private Command406_DeviceConfigMsg command406=null;
	private boolean isCmd406Send = false;

	private static final String COPY_DAY_WARNING_TAG = "1";
	private static final String CHANGE_DAY_WARNING_TAG = "2";
	private static final String CANCEL_WARNING_TAG = "3";

	private int DRAWERABLE_SYNC_NORMAL = R.drawable.floorheating_program_btn_selector;
	private int DRAWERABLE_SYNC_CHANGE = R.drawable.floorheating_program_btn_selector_02;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		Bundle bundle = getArguments().getBundle("FanCoilProgramFragmentInfo");
		mGwId = bundle.getString(FanCoilUtil.GWID);
		mDevId = bundle.getString(FanCoilUtil.DEVID);
		mEp = bundle.getString(FanCoilUtil.EP);
		mEpType = bundle.getString(FanCoilUtil.EPTYPE);
		mMode = bundle.getString("programMode");
		cache= DeviceCache.getInstance(mActivity);
		ballWidth = DisplayUtil.dip2Pix(mActivity, 13);

		command406=new Command406_DeviceConfigMsg(mActivity);
		command406.setConfigMsg(this);
		command406.setDevID(mDevId);
		command406.setGwID(mGwId);

		curDataMap = new HashMap<String, List<ProgramBall>>();
	}

	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().hide();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.device_fancoil_program, container, false);
		ViewUtils.inject(this,rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		btnMonday.setOnClickListener(this);
		btnTuesday.setOnClickListener(this);
		btnWednesday.setOnClickListener(this);
		btnThursday.setOnClickListener(this);
		btnFriday.setOnClickListener(this);
		btnSaturday.setOnClickListener(this);
		btnSunday.setOnClickListener(this);
		btnCopy.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		btnSync.setOnClickListener(this);

		loadData();
		if(!isCmd406Send){
			SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, FanCoilUtil.CURRENT_QUERY_CMD_DATA);
			command406.SendCommand_Get();
			isCmd406Send = true;
		}
		programView.setmMoveChanged(new ProgramView.OnMoveValueChangedable() {
			@Override
			public void onMoveChanged() {
				btnSync.setBackgroundResource(DRAWERABLE_SYNC_CHANGE);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		initBar();
		setMonSelected();
	}

	private void loadData(){
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				initView();
				initDataList();
				setMonSelected();
				setProgramViewThumb();
			}
		});
	}

	private void setProgramViewThumb(){
		programView.setThumbList(curDataMap.get(week));
	}

	private void initView(){
		if(!StringUtil.isNullOrEmpty(mMode)){
			if(StringUtil.equals(mMode ,FanCoilUtil.MODE_HEAT)){
				tvMode.setText(mApplication.getResources().getString(R.string.AP_get_hot));
			}else if(StringUtil.equals(mMode ,FanCoilUtil.MODE_COOL)){
				tvMode.setText(mApplication.getResources().getString(R.string.air_conditioner_cooling_mode));
			}
			programView.setMode(mMode);
		}
	}

	private void initDataList(){
		if(initialDataList==null) {
			initialDataList = new ArrayList<ProgramBall>();
			if(!StringUtil.isNullOrEmpty(mMode)){
				if(StringUtil.equals(mMode,FanCoilUtil.MODE_HEAT)){
					initialDataList.add(new ProgramBall("21", "24", ballWidth));
					initialDataList.add(new ProgramBall("16", "32", ballWidth));
					initialDataList.add(new ProgramBall("16", "48", ballWidth));
					initialDataList.add(new ProgramBall("16", "56", ballWidth));
					initialDataList.add(new ProgramBall("21", "72", ballWidth));
					initialDataList.add(new ProgramBall("16", "88", ballWidth));
				}else if(StringUtil.equals(mMode,FanCoilUtil.MODE_COOL)){
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

	private void setMonSelected(){
		week = MON_TAG;
		btnMonday.setTextColor(Color.parseColor("#7FA82F"));
		btnMonday.setBackgroundColor(Color.parseColor("#FFFFFF"));
		btnTuesday.setTextColor(Color.parseColor("#FFFFFF"));
		btnTuesday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnWednesday.setTextColor(Color.parseColor("#FFFFFF"));
		btnWednesday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnThursday.setTextColor(Color.parseColor("#FFFFFF"));
		btnThursday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnFriday.setTextColor(Color.parseColor("#FFFFFF"));
		btnFriday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnSaturday.setTextColor(Color.parseColor("#FFFFFF"));
		btnSaturday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnSunday.setTextColor(Color.parseColor("#FFFFFF"));
		btnSunday.setBackgroundColor(Color.parseColor("#7FA82F"));

		syncMessage = mApplication.getResources().getString(R.string.AP_sync_monday);
	}

	private void setTuesSelected(){
		week = TUE_TAG;
		btnMonday.setTextColor(Color.parseColor("#FFFFFF"));
		btnMonday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnTuesday.setTextColor(Color.parseColor("#7FA82F"));
		btnTuesday.setBackgroundColor(Color.parseColor("#FFFFFF"));
		btnWednesday.setTextColor(Color.parseColor("#FFFFFF"));
		btnWednesday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnThursday.setTextColor(Color.parseColor("#FFFFFF"));
		btnThursday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnFriday.setTextColor(Color.parseColor("#FFFFFF"));
		btnFriday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnSaturday.setTextColor(Color.parseColor("#FFFFFF"));
		btnSaturday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnSunday.setTextColor(Color.parseColor("#FFFFFF"));
		btnSunday.setBackgroundColor(Color.parseColor("#7FA82F"));

		syncMessage = mApplication.getResources().getString(R.string.AP_sync_tuesday);
	}

	private void setWedSelected(){
		week = WED_TAG;
		btnMonday.setTextColor(Color.parseColor("#FFFFFF"));
		btnMonday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnTuesday.setTextColor(Color.parseColor("#FFFFFF"));
		btnTuesday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnWednesday.setTextColor(Color.parseColor("#7FA82F"));
		btnWednesday.setBackgroundColor(Color.parseColor("#FFFFFF"));
		btnThursday.setTextColor(Color.parseColor("#FFFFFF"));
		btnThursday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnFriday.setTextColor(Color.parseColor("#FFFFFF"));
		btnFriday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnSaturday.setTextColor(Color.parseColor("#FFFFFF"));
		btnSaturday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnSunday.setTextColor(Color.parseColor("#FFFFFF"));
		btnSunday.setBackgroundColor(Color.parseColor("#7FA82F"));

		syncMessage = mApplication.getResources().getString(R.string.AP_sync_wedneday);
	}

	private void setThurSelected(){
		week = THUR_TAG;
		btnMonday.setTextColor(Color.parseColor("#FFFFFF"));
		btnMonday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnTuesday.setTextColor(Color.parseColor("#FFFFFF"));
		btnTuesday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnWednesday.setTextColor(Color.parseColor("#FFFFFF"));
		btnWednesday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnThursday.setTextColor(Color.parseColor("#7FA82F"));
		btnThursday.setBackgroundColor(Color.parseColor("#FFFFFF"));
		btnFriday.setTextColor(Color.parseColor("#FFFFFF"));
		btnFriday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnSaturday.setTextColor(Color.parseColor("#FFFFFF"));
		btnSaturday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnSunday.setTextColor(Color.parseColor("#FFFFFF"));
		btnSunday.setBackgroundColor(Color.parseColor("#7FA82F"));
		syncMessage = mApplication.getResources().getString(R.string.AP_sync_thursday);
	}

	private void setFriSelected(){
		week = FRI_TAG;
		btnMonday.setTextColor(Color.parseColor("#FFFFFF"));
		btnMonday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnTuesday.setTextColor(Color.parseColor("#FFFFFF"));
		btnTuesday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnWednesday.setTextColor(Color.parseColor("#FFFFFF"));
		btnWednesday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnThursday.setTextColor(Color.parseColor("#FFFFFF"));
		btnThursday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnFriday.setTextColor(Color.parseColor("#7FA82F"));
		btnFriday.setBackgroundColor(Color.parseColor("#FFFFFF"));
		btnSaturday.setTextColor(Color.parseColor("#FFFFFF"));
		btnSaturday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnSunday.setTextColor(Color.parseColor("#FFFFFF"));
		btnSunday.setBackgroundColor(Color.parseColor("#7FA82F"));
		syncMessage = mApplication.getResources().getString(R.string.AP_sync_friday);
	}

	private void setSatSelected(){
		week = SAT_TAG;
		btnMonday.setTextColor(Color.parseColor("#FFFFFF"));
		btnMonday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnTuesday.setTextColor(Color.parseColor("#FFFFFF"));
		btnTuesday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnWednesday.setTextColor(Color.parseColor("#FFFFFF"));
		btnWednesday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnThursday.setTextColor(Color.parseColor("#FFFFFF"));
		btnThursday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnFriday.setTextColor(Color.parseColor("#FFFFFF"));
		btnFriday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnSaturday.setTextColor(Color.parseColor("#7FA82F"));
		btnSaturday.setBackgroundColor(Color.parseColor("#FFFFFF"));
		btnSunday.setTextColor(Color.parseColor("#FFFFFF"));
		btnSunday.setBackgroundColor(Color.parseColor("#7FA82F"));
		syncMessage = mApplication.getResources().getString(R.string.AP_sync_saturday);
	}

	private void setSunSelected(){
		week = SUN_TAG;
		btnMonday.setTextColor(Color.parseColor("#FFFFFF"));
		btnMonday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnTuesday.setTextColor(Color.parseColor("#FFFFFF"));
		btnTuesday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnWednesday.setTextColor(Color.parseColor("#FFFFFF"));
		btnWednesday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnThursday.setTextColor(Color.parseColor("#FFFFFF"));
		btnThursday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnFriday.setTextColor(Color.parseColor("#FFFFFF"));
		btnFriday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnSaturday.setTextColor(Color.parseColor("#FFFFFF"));
		btnSaturday.setBackgroundColor(Color.parseColor("#7FA82F"));
		btnSunday.setTextColor(Color.parseColor("#7FA82F"));
		btnSunday.setBackgroundColor(Color.parseColor("#FFFFFF"));

		syncMessage = mApplication.getResources().getString(R.string.AP_sync_sunday);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.fancoil_program_monday:
				if(programView.isBallChanged()){
					showWarningDialog(CHANGE_DAY_WARNING_TAG , MON_TAG);
				}else{
					setMonSelected();
					setProgramViewThumb();
				}
				break;
			case R.id.fancoil_program_tuesday:
				if(programView.isBallChanged()){
					showWarningDialog(CHANGE_DAY_WARNING_TAG , TUE_TAG);
				}else {
					setTuesSelected();
					setProgramViewThumb();
				}
				break;
			case R.id.fancoil_program_wednesday:
				if(programView.isBallChanged()){
					showWarningDialog(CHANGE_DAY_WARNING_TAG , WED_TAG);
				}else {
					setWedSelected();
					setProgramViewThumb();
				}
				break;
			case R.id.fancoil_program_thursday:
				if(programView.isBallChanged()){
					showWarningDialog(CHANGE_DAY_WARNING_TAG , THUR_TAG);
				}else {
					setThurSelected();
					setProgramViewThumb();
				}
				break;
			case R.id.fancoil_program_friday:
				if(programView.isBallChanged()){
					showWarningDialog(CHANGE_DAY_WARNING_TAG , FRI_TAG);
				}else {
					setFriSelected();
					setProgramViewThumb();
				}
				break;
			case R.id.fancoil_program_saturday:
				if(programView.isBallChanged()){
					showWarningDialog(CHANGE_DAY_WARNING_TAG , SAT_TAG);
				}else {
					setSatSelected();
					setProgramViewThumb();
				}
				break;
			case R.id.fancoil_program_sunday:
				if(programView.isBallChanged()){
					showWarningDialog(CHANGE_DAY_WARNING_TAG , SUN_TAG);
				}else {
					setSunSelected();
					setProgramViewThumb();
				}
				break;
			case R.id.fancoil_program_cancel:
				if(programView.isBallChanged() || isCopyFromOtherDay){
					showWarningDialog(CANCEL_WARNING_TAG , null);
				}else {
					cancel();
				}
				break;
			case R.id.fancoil_program_copy:
				if(programView.isBallChanged() || isCopyFromOtherDay){
					showWarningDialog(COPY_DAY_WARNING_TAG , null);
				}else {
					copyDay();
				}
				isCopyFromOtherDay = true;
				btnSync.setBackgroundResource(DRAWERABLE_SYNC_CHANGE);
				break;
			case R.id.fancoil_program_sync:
				showSyncDialog();
				break;
		}

	}

	private void copyDay(){
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
	}

	private void showSyncDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);

		builder.setTitle(mApplication.getResources().getString(R.string.operation_title))
				.setSubTitleText(null)
				.setMessage(syncMessage)
				.setNegativeButton(mApplication.getResources().getString(R.string.common_cancel))
				.setPositiveButton(mApplication.getResources().getString(R.string.common_ok))
				.setHeight(SYNC_DIALOG_HEIGHT)
				.setWidth(SYNC_DIALOG_WIDTH)
				.setDismissAfterDone(true).setListener(new WLDialog.MessageListener() {
			@Override
			public void onClickPositive(View view) {
				List<ProgramBall>  syncDataList = new ArrayList<ProgramBall>();
				for (ProgramBall ball : programView.getThumbList()) {
					syncDataList.add(ball);
				}
				if(syncDataList != null && (!syncDataList.isEmpty())) {
					curDataMap.put(week, syncDataList);
					sync();
					showResetDialog();
				}
				isCopyFromOtherDay = false;
				command406.SendCommand_Get();
			}

			@Override
			public void onClickNegative(View view) {

			}
		});
		syncDialog = builder.create();
		syncDialog.show();
	}

	private void sync(){
		String dataCmd = week+getDataCmd();
		String sendCmd = FanCoilUtil.PROGRAM_SETTING_CMD + mMode.substring(1,2) + dataCmd;
		SendMessage.sendControlDevMsg(mGwId, mDevId, mEp, mEpType, sendCmd);
		command406.SendCommand_Add(getCmd406(week), getDataCmd406(sendCmd));
	}

	private void showWarningDialog(final String warningType,final String selectWeek){
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(mApplication.getResources().getString(R.string.operation_title));
		builder.setSubTitleText(null);
		if(StringUtil.equals(warningType ,COPY_DAY_WARNING_TAG)){
			builder.setMessage(mApplication.getResources().getString(R.string.floor_heating_set_out_2));
		}else if(StringUtil.equals(warningType ,CHANGE_DAY_WARNING_TAG)){
			builder.setMessage(mApplication.getResources().getString(R.string.floor_heating_set_out_3));
		}else if(StringUtil.equals(warningType ,CANCEL_WARNING_TAG)){
			builder.setMessage(mApplication.getResources().getString(R.string.floor_heating_set_out_1));
		}
		builder.setNegativeButton(mApplication.getResources().getString(R.string.common_cancel));
		builder.setPositiveButton(mApplication.getResources().getString(R.string.common_ok));
		builder.setHeight(SYNC_DIALOG_HEIGHT);
		builder.setWidth(SYNC_DIALOG_WIDTH);
		builder.setListener(new WLDialog.MessageListener() {
			@Override
			public void onClickPositive(View view) {
				if(StringUtil.equals(warningType ,COPY_DAY_WARNING_TAG)){
					copyDay();
					programView.setBallChanged(false);
				//	btnSync.setBackgroundResource(DRAWERABLE_SYNC_NORMAL);
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
		warningDialog.show();
	}

	private void cancel(){
		getActivity().finish();
	}

	private String getCmd406(String mWeek){

		StringBuilder cmd406Builder = new StringBuilder();
		cmd406Builder.append("programData");
		cmd406Builder.append(Integer.parseInt(mMode));
		cmd406Builder.append(mWeek);

		return cmd406Builder.toString();
	}

	private String getDataCmd(){
		StringBuilder cmdBulider = new StringBuilder();
		for (ProgramBall ball : curDataMap.get(week)) {
			cmdBulider.append(StringUtil.appendLeft(ball.time, 2, '0'));
			cmdBulider.append(ball.temp);
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
		if(StringUtil.equals(mEpData, FanCoilUtil.PROGRAM_SETTING_TAG)){
			showResultSuccessDialog();
			programView.setBallChanged(false);
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
		Log.i(TAG+"-epdata", mEpData+"-"+mEpData.length());
		handleEpData(mEpData);
		showResetResultDialog();
	}

	private void handleEpData(String epData) {

		if(!StringUtil.isNullOrEmpty(epData)){
			if(epData.length() == 40){
				mReturnId = epData.substring(0,2);
			}
		}
	}

	@Override
	public void Reply406Result(Command406Result result) {
		String reciveData = result.getData();
		if(!StringUtil.isNullOrEmpty(reciveData)){

			handleResult406(reciveData);
		}
	}
	private void handleResult406(String reciveData) {
		try {
			JSONObject jsonObject = new JSONObject(reciveData);
			if (jsonObject.has("programData")) {
				String resultData = jsonObject.getString("programData");
				String tag = resultData.substring(0, 1);
				if (StringUtil.equals(tag, FanCoilUtil.PROGRAM_SETTING_CMD)) {
					String mode = "0" + resultData.substring(1, 2);
					if (StringUtil.equals(mode, mMode)) {
						String sWeek = resultData.substring(2, 3);
						List<String> dataList = new ArrayList<String>();
						List<ProgramBall> ballList = new ArrayList<ProgramBall>();
						dataList.add(resultData.substring(3, 7));
						dataList.add(resultData.substring(7, 11));
						dataList.add(resultData.substring(11, 15));
						dataList.add(resultData.substring(15, 19));
						dataList.add(resultData.substring(19, 23));
						dataList.add(resultData.substring(23, 27));

						for (String data : dataList) {
							String time = Integer.parseInt(data.substring(0, 2)) + "";
							String temp = data.substring(2, 4);
							ballList.add(new ProgramBall(temp, time, ballWidth));
						}
						curDataMap.put(sWeek, ballList);
						programView.setThumbList(curDataMap.get(week));
					}

				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void Reply406Result(List<Command406Result> results) {
		if(results != null){
			for(Command406Result result:results){
				Reply406Result(result);
			}
		}
	}
}
