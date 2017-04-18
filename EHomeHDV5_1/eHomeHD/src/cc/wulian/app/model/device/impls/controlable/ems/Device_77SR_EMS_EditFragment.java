package cc.wulian.app.model.device.impls.controlable.ems;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.SendMessage;

public class Device_77SR_EMS_EditFragment extends WulianFragment {
	
	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	
	public static final String StartPower = "100";
	public static final String StartPowerCalibration = "200";
	public static final String StartPowerRange = "500";
	
	private SeekBar editPowerSeekbar;
	private SeekBar editPowerRangeSeekbar;
	
	private TextView editPowerTextView;
	private TextView editPowerRangeTextView;
	private EditText editPowerCalibrationEditText;
	
	private String editPower;
	private String editCalibration;
	private String editPowerRange;
	
	private String gwID;
	private String deviceID;
	
   //一路
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Bundle bundle = getArguments();
		gwID = (String) bundle.getString(GWID);
		deviceID = (String) bundle.getString(DEVICEID);
		//查询当前状态
		initBar();
	}
	//actionBar
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setIconText(
				getResources().getString(R.string.device_ir_back));
		getSupportActionBar().setTitle(
				getResources().getString(R.string.device_type_15));
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
		editCalibration =  editPowerCalibrationEditText.getText().toString();
		if("".equals(editCalibration)|editCalibration ==null){
			editCalibration = StartPowerCalibration;
		}
		
		SendMessage.sendControlDevMsg(gwID,deviceID, 
				"14", ConstUtil.DEV_TYPE_FROM_GW_EMS_SR, 6+StringUtil.appendLeft(editPower, 4, '0'));
		SendMessage.sendControlDevMsg(gwID,deviceID, 
				"14", ConstUtil.DEV_TYPE_FROM_GW_EMS_SR, 7+StringUtil.appendLeft(editCalibration, 4, '0'));
		SendMessage.sendControlDevMsg(gwID,deviceID, 
				"14", ConstUtil.DEV_TYPE_FROM_GW_EMS_SR, 8+StringUtil.appendLeft(editPowerRange, 4, '0'));
		SendMessage.sendControlDevMsg(gwID,deviceID, 
				"14", ConstUtil.DEV_TYPE_FROM_GW_EMS_SR, 12+"");
		mActivity.finish();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(
				R.layout.device_edit_ems_fragment,
				null);
		editPower =  StartPower;
		editCalibration = StartPowerCalibration;
		editPowerRange = StartPowerRange;
		
		editPowerSeekbar = (SeekBar) view.findViewById(R.id.device_edit_power_seekbar);
		editPowerRangeSeekbar = (SeekBar) view.findViewById(R.id.device_edit_specify_power_range_seekbar);
		editPowerTextView = (TextView) view.findViewById(R.id.device_edit_power_textview_shownum);
		editPowerRangeTextView = (TextView) view.findViewById(R.id.device_edit_power_range_textview_shownum);
		editPowerCalibrationEditText = (EditText) view.findViewById(R.id.device_ems_edit_power_calibration_edittext);
		
		
		editPowerTextView.setText(editPower + "w");
		editPowerRangeTextView.setText(editPowerRange + "w");
		
		editPowerSeekbar.setOnSeekBarChangeListener(mSeekBarChangeListener);
		editPowerRangeSeekbar.setOnSeekBarChangeListener(mSeekBarChangeListener);
		return view;
	}
	
	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
	
		/**
		 * 状态位选择
		 */
	}
	private OnSeekBarChangeListener mSeekBarChangeListener = new OnSeekBarChangeListener() {

		@Override
		public void onStopTrackingTouch(SeekBar arg0) {
			if (arg0 == editPowerSeekbar) {
				editPower = arg0.getProgress()+10+"";
				editPowerTextView.setText(arg0.getProgress()+10 + "w");
			} else if (arg0 == editPowerRangeSeekbar) {
				editPowerRange = arg0.getProgress()+500+"";
				editPowerRangeTextView.setText(arg0.getProgress()+500 + "w");
			}
		}

		@Override
		public void onStartTrackingTouch(SeekBar arg0) {

		}

		@Override
		public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {
			if (arg0 == editPowerSeekbar) {
				editPowerTextView.setText(arg0.getProgress()+10 + "w");
			} else if (arg0 == editPowerRangeSeekbar) {
				editPowerRangeTextView.setText(arg0.getProgress()+500 + "w");
			}
		}

	};

}
