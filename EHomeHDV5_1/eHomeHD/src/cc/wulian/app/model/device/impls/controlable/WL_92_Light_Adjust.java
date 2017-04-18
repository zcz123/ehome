package cc.wulian.app.model.device.impls.controlable;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.SpannableUtil;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.tools.DeviceTool;

import com.yuantuo.customview.seekcircle.SeekCircle;
import com.yuantuo.customview.seekcircle.SeekCircle.OnSeekCircleChangeListener;

@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_92}, category = Category.C_LIGHT)
public class WL_92_Light_Adjust extends ControlableDeviceImpl{

	private static final String DATA_CTRL_STATE_OPEN_255 = "2255";
	private static final String DATA_CTRL_STATE_CLOSE_0 = "2000";

	private static final int SMALL_OPEN_D = R.drawable.device_d_light_open;
	private static final int SMALL_CLOSE_D = R.drawable.device_d_light_close;

  
	private int light;
	
	private static final String PREFIX_DATA_LIGHT = "2";
	private static final String PREFIX_DATA_GET_INFO = "3";
	
	
	private static final String PREFIX_DATA_LIGHT_ONLINE="52";
	private static final String PREFIX_DATA_LIGHT_02="02";
	private static final String PREFIX_DATA_LIGHT_03="03";
	
	private SeekCircle mSeekBar;
	private TextView mDevStateView;
	private LinearLayout mColorLayout;
	
	public WL_92_Light_Adjust(Context context, String type) {
		super(context, type);
	}

	@Override
	public String getOpenProtocol() {
		// no used for d light
		return DATA_CTRL_STATE_OPEN_255;
	}

	@Override
	public String getCloseProtocol() {
		return DATA_CTRL_STATE_CLOSE_0;
	}

	/*
	 * how d-light express it's open state? data in 1~100 always means opened, when data [not null] and [not close state] always can be express opened state
	 */
	@Override
	public boolean isOpened() {
		// d light has 1~99 means open
		return !isNull(epData) && !isClosed();
	}

	// just for watch code easy
	// 12 0 closed
	// 13 000 closed
	@Override
	public boolean isClosed() {
		return DATA_CTRL_STATE_CLOSE_0.equals(PREFIX_DATA_LIGHT+ StringUtil.appendLeft(light + "", 3, '0'));
	}

	@Override
	public String getOpenSendCmd() {
		return DATA_CTRL_STATE_OPEN_255;
	}

	@Override
	public String getCloseSendCmd() {
		return DATA_CTRL_STATE_CLOSE_0;
	}
	/**
	 * convert data(0~100) to alpha(0~255)
	 */
	public int convert2Alpha( int in ) {
		float alpha = (in / 100F) * 0xFF;
		return (int) Math.min(alpha, 0xFF);
	}

	@Override
	public Drawable getStateSmallIcon() {
		Drawable icon = null;
		if (isOpened()) {
			/*Note: sometimes icon can not see, not use alpha···
			int epDataInt = StringUtil.toInteger(epData);
			icon = getDrawable(SMALL_OPEN_D).mutate();
			icon.setAlpha(convert2Alpha(epDataInt));*/
			icon = getDrawable(SMALL_OPEN_D);
		}
		else if (isClosed()) {
			icon = getDrawable(SMALL_CLOSE_D);
		}
		else {
			icon = WL_92_Light_Adjust.this.getDefaultStateSmallIcon();
		}
		return icon;
	}


	@Override
	public CharSequence parseDataWithProtocol(String epData){

		String state = null;
		int color = COLOR_NORMAL_ORANGE;

		if (isClosed()) {
			state = getString(R.string.device_state_close);
			color = COLOR_NORMAL_ORANGE;
		}
		else {
			int epDataInt = StringUtil.toInteger(light);
			state = epDataInt == 100 ? getString(R.string.device_state_open) : (light + " %");
			color = COLOR_CONTROL_GREEN;
		}
		return SpannableUtil.makeSpannable(state, new ForegroundColorSpan(getColor(color)));
	}

	
	private OnSeekCircleChangeListener seekBarListener = new OnSeekCircleChangeListener() {
		
		@Override
		public void onStopTrackingTouch(SeekCircle seekCircle) {
			int mSeekProgress = seekCircle.getProgress();
			if(mSeekProgress < 5){
				mSeekProgress = 0;
				seekCircle.setProgress(mSeekProgress);
			}
			if(R.id.dev_state_seekbar_0_light == seekCircle.getId()){
				int mSeekLight = (int) Math.ceil(mSeekProgress * 255.0 / 100);
				
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_LIGHT+StringUtil.appendLeft(mSeekLight + "", 3, '0'),true);
			}
		}
		
		@Override
		public void onStartTrackingTouch(SeekCircle seekCircle) {
			
		}
		
		@Override
		public void onProgressChanged(SeekCircle seekCircle, int progress,
				boolean fromUser) {
			if(R.id.dev_state_seekbar_0_light== seekCircle.getId()){
				if (fromUser) {
					if(progress < 5){
						progress = 0;
						seekCircle.setProgress(progress);
					}
					mDevStateView.setText(getResources().getString(R.string.device_light_percentage)+progress + "%");
				}
			}
		}
	};
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_with_d_seekcircle, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		mColorLayout = (LinearLayout) view.findViewById(R.id.dev_iscolor_seekbar);
		mColorLayout.setVisibility(View.INVISIBLE);
		mSeekBar = (SeekCircle) view.findViewById(R.id.dev_state_seekbar_0_light);
		mDevStateView = (TextView) view.findViewById(R.id.dev_state_textview_0);
		mSeekBar.setOnSeekCircleChangeListener(seekBarListener);
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		int lighttext = (int) Math.floor(light * 100 / 255.0);
		mSeekBar.setProgress(StringUtil.toInteger(lighttext));
		mDevStateView.setText(getResources().getString(R.string.device_light_percentage) + lighttext + "%");
	}
	
	private  void disassembleCompoundCmd( WL_92_Light_Adjust device, String epData ) {
		if (isNull(epData)) return;

		if (epData.startsWith(PREFIX_DATA_LIGHT_02) && epData.length() >= 8) {
			device.light = StringUtil.toInteger(epData.substring(6, 8), 16);
		}else if(epData.startsWith(PREFIX_DATA_LIGHT_ONLINE) && epData.length() >= 8){
			device.light = StringUtil.toInteger(epData.substring(6, 8), 16);
		}else if(epData.startsWith(PREFIX_DATA_LIGHT_03) && epData.length() >= 8){
			device.light = StringUtil.toInteger(epData.substring(6, 8), 16);
		}
	}
	
	@Override
	public void refreshDevice() {
		super.refreshDevice();
		disassembleCompoundCmd(WL_92_Light_Adjust.this, epData);
	}

	@Override
	public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater, String ep, String epData) {
		if(epData == null)
			epData = "";
		View view =  inflater.inflate(cc.wulian.smarthomev5.R.layout.scene_task_control_data_temp_light, null);
		linkTaskControlEPData = new StringBuffer(epData);
		final TextView lightTextView = (TextView)view.findViewById(R.id.dev_state_textview_0);
		TextView lightAdjustText = (TextView) view.findViewById(R.id.dev_light_text);
		lightAdjustText.setVisibility(View.GONE);
		SeekBar seekBarLight = (SeekBar)view.findViewById(R.id.dev_state_seekbar_0);
		LinearLayout mColorLyout = (LinearLayout) view.findViewById(R.id.dev_adjust_color);
		mColorLyout.setVisibility(View.GONE);
		seekBarLight.setProgress(0);
		if(epData.startsWith(PREFIX_DATA_LIGHT)){
			String lightText = linkTaskControlEPData.substring(1);
			int lightProcess = StringUtil.toInteger(lightText);
			seekBarLight.setProgress(lightProcess);
			lightTextView.setText((int)(lightProcess/255.0*100)+"%");
		}
		seekBarLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mSeekProgress = seekBar.getProgress();
				if(mSeekProgress < 5){
					mSeekProgress = 0;
					seekBar.setProgress(mSeekProgress);
				}
				linkTaskControlEPData =  new StringBuffer(PREFIX_DATA_LIGHT+StringUtil.appendLeft(mSeekProgress+"", 3, '0'));
				lightTextView.setText((int)(mSeekProgress/255.0*100)+"%");
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				lightTextView.setText((int)(seekBar.getProgress()/255.0*100)+"%");
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int mProgress = (int) Math.floor(progress / 255f * 100);
				if (mProgress < 5)
				{
					mProgress = 0;
					progress = 0;
					seekBar.setProgress(progress);
				}
				lightTextView.setText(mProgress + "%");
			}
		});
		return createControlDataDialog(inflater.getContext(), view);
	}
	
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater, final AutoActionInfo autoActionInfo) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		View contentview =  inflater.inflate(cc.wulian.smarthomev5.R.layout.scene_task_control_data_temp_light, null);
		String epData = autoActionInfo.getEpData();
		final TextView lightTextView = (TextView)contentview.findViewById(R.id.dev_state_textview_0);
		TextView lightAdjustText = (TextView) contentview.findViewById(R.id.dev_light_text);
		lightAdjustText.setVisibility(View.GONE);
		SeekBar seekBarLight = (SeekBar)contentview.findViewById(R.id.dev_state_seekbar_0);
		LinearLayout mColorLyout = (LinearLayout) contentview.findViewById(R.id.dev_adjust_color);
		mColorLyout.setVisibility(View.GONE);
		seekBarLight.setProgress(0);
		if(epData.startsWith(PREFIX_DATA_LIGHT)){
			String lightText = epData.substring(1);
			int lightProcess = StringUtil.toInteger(lightText);
			seekBarLight.setProgress(lightProcess);
			lightTextView.setText((int)(lightProcess/255.0*100)+"%");
		}
		seekBarLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mSeekProgress = seekBar.getProgress();
				if(mSeekProgress < 5){
					mSeekProgress = 0;
					seekBar.setProgress(mSeekProgress);
				}
				autoActionInfo.setEpData(PREFIX_DATA_LIGHT+StringUtil.appendLeft(mSeekProgress+"", 3, '0'));
				lightTextView.setText((int)(mSeekProgress/255.0*100)+"%");
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				lightTextView.setText((int)(seekBar.getProgress()/255.0*100)+"%");
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int mProgress = (int) Math.floor(progress / 255f * 100);
				if (mProgress < 5)
				{
					mProgress = 0;
					progress = 0;
					seekBar.setProgress(progress);
				}
				lightTextView.setText(mProgress + "%");
			}
		});
		holder.setShowDialog(true);
		holder.setContentView(contentview);
		holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}
}
