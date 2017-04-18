package cc.wulian.app.model.device.impls.controlable;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

import com.alibaba.fastjson.JSONArray;
import com.yuantuo.customview.seekcircle.SeekCircle;
import com.yuantuo.customview.seekcircle.SeekCircle.OnSeekCircleChangeListener;

/**
 * 1ccc：1表示调色温，ccc表示000~510,000~255表示偏暖色，256~510表示偏冷色，000表示100%暖色，510表示100%冷色
 * 2xxx：2表示调光，xxx亮度百分比
 * 3：表示获取状态
 */
@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_91_Temp_led}, category = Category.C_LIGHT)
public class WL_91_D_Temp_Light_Led extends ControlableDeviceImpl
{
	private static final String DATA_CTRL_STATE_OPEN_255 = "2255";
	private static final String DATA_CTRL_STATE_CLOSE_0 = "2000";

	private static final int SMALL_OPEN_D = R.drawable.device_d_light_open;
	private static final int SMALL_CLOSE_D = R.drawable.device_d_light_close;

  
	private int temp;
	private int light;
	
	private static final String PREFIX_DATA_Temp = "1";
	private static final String PREFIX_DATA_Light = "2";
	private static final String PREFIX_DATA_GET_INFO = "3";
	
	private static final String PREFIX_DATA_TEMP_LIGHT="03";
	
	private SeekBar seekBarLight;
	private SeekBar seekBarTemp;
	private TextView lightTextView ;
	private Button seekBarText;
	
	private OnSeekCircleChangeListener seekBarListener = new OnSeekCircleChangeListener(){
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

		@Override
		public void onStartTrackingTouch(SeekCircle seekCircle) {
			
		}

		@Override
		public void onStopTrackingTouch(SeekCircle seekCircle) {

			
			int mSeekProgress = seekCircle.getProgress();
			if(R.id.dev_state_seekbar_0_light == seekCircle.getId()){
				if(mSeekProgress < 5){
					mSeekProgress = 0;
					seekCircle.setProgress(mSeekProgress);
				}
				int mSeekLight = (int) Math.ceil(mSeekProgress * 255.0 / 100);
				
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_Light+StringUtil.appendLeft(mSeekLight + "", 3, '0'),true);
			}else if(R.id.dev_state_seekbar_1_light == seekCircle.getId()){
				int mSeekColor = (int) Math.ceil(mSeekProgress * 510.0 / 100);
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_Temp+StringUtil.appendLeft(mSeekColor + "", 3, '0'),true);
			}
		
		}
	};
	public WL_91_D_Temp_Light_Led( Context context, String type )
	{
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
		return DATA_CTRL_STATE_CLOSE_0.equals(PREFIX_DATA_Light+ StringUtil.appendLeft(light + "", 3, '0'));
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
				icon = WL_91_D_Temp_Light_Led.this.getDefaultStateSmallIcon();
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

		private SeekCircle mSeekBar;
		private SeekCircle mseekBarTemp;
		private TextView mDevStateView;
		private TextView mTempView;

		@Override
		public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle saveState ) {
			disassembleCompoundCmd(WL_91_D_Temp_Light_Led.this, epData);
			return inflater.inflate(R.layout.device_with_d_seekcircle, container, false);
		}

		@Override
		public void onViewCreated( View view, Bundle saveState ) {
			super.onViewCreated(view, saveState);

			mSeekBar = (SeekCircle) view.findViewById(R.id.dev_state_seekbar_0_light);
			mseekBarTemp = (SeekCircle) view.findViewById(R.id.dev_state_seekbar_1_light);
			mDevStateView = (TextView) view.findViewById(R.id.dev_state_textview_0);
			mTempView = (TextView) view.findViewById(R.id.dev_state_textview_1);
			mSeekBar.setOnSeekCircleChangeListener(seekBarListener);
			mseekBarTemp.setOnSeekCircleChangeListener(seekBarListener);
		}


		@Override
		public void initViewStatus() {
			super.initViewStatus();
			disassembleCompoundCmd(WL_91_D_Temp_Light_Led.this, epData);
			int lighttext = light * 100 / 255;
			mSeekBar.setProgress(StringUtil.toInteger(lighttext));
			mseekBarTemp.setProgress(StringUtil.toInteger(temp * 100 / 510));
			/*if(lighttext >=100|lighttext == 0){
			}
			else{
				lighttext = lighttext;
			}*/
			mDevStateView.setText(getResources().getString(R.string.device_light_percentage) + lighttext + "%");
			mColorTemp();
		}

		private void mColorTemp(){
			if (temp >= 0 && temp <= 510) {
				if (temp >= 0 && temp <= 255) {
					if (temp == 255) {
						mTempView.setText(getResources().getString(R.string.device_neutral_white));
					}
					else {
						mTempView.setText(getResources().getString(R.string.device_cold_color)
								+ (int) Math.floor(temp * 100 / 510)
								+ "%");
					}
				}else if (temp > 255 && temp <= 261) {
					mTempView.setText(getResources().getString(R.string.device_neutral_white));
				}else {
					mTempView.setText(getResources().getString(R.string.device_warm_color)
							+ (int) Math.floor(temp * 100 / 510)
							+ "%");
				}
			}
			else{
				mTempView.setText(getResources().getString(R.string.device_neutral_white));
			}
		}
	
	private  void disassembleCompoundCmd( WL_91_D_Temp_Light_Led device, String epData ) {
		if (isNull(epData)) return;

		if (epData.startsWith(PREFIX_DATA_TEMP_LIGHT) && epData.length() >= 8) {
			device.temp = StringUtil.toInteger(epData.substring(2, 6), 16);
			device.light = StringUtil.toInteger(epData.substring(6, 8), 16);
		}
	}

	@Override
	public void refreshDevice() {
		super.refreshDevice();
		disassembleCompoundCmd(WL_91_D_Temp_Light_Led.this, epData);
	}

	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater, final AutoActionInfo autoActionInfo) {
		View view =  inflater.inflate(cc.wulian.smarthomev5.R.layout.scene_task_control_data_temp_light, null);
		lightTextView = (TextView)view.findViewById(R.id.dev_state_textview_0);
		seekBarLight= (SeekBar)view.findViewById(R.id.dev_state_seekbar_0);
		seekBarLight.setProgress(0);
		seekBarTemp= (SeekBar)view.findViewById(R.id.dev_state_seekbar_1);
		seekBarTemp.setProgress(0);
		seekBarText= (Button) view.findViewById(R.id.scene_link_adjust_color);
		changeLightColor(0,seekBarText);
		initSelectDataViewStatus(autoActionInfo);
		seekBarLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mSeekProgress = seekBar.getProgress();
				if(mSeekProgress < 5){
					mSeekProgress = 0;
					seekBar.setProgress(mSeekProgress);
				}
				setActionInfoData(PREFIX_DATA_Light+StringUtil.appendLeft(mSeekProgress+"", 3, '0'),autoActionInfo);
//				autoActionInfo.setEpData(PREFIX_DATA_Light+StringUtil.appendLeft(mSeekProgress+"", 3, '0'));
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
		seekBarTemp.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mSeekProgress = seekBar.getProgress();
//				autoActionInfo.setEpData(PREFIX_DATA_Temp+StringUtil.appendLeft(mSeekProgress+"", 3, '0'));
				setActionInfoData(PREFIX_DATA_Temp+StringUtil.appendLeft(mSeekProgress+"", 3, '0'), autoActionInfo);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int  initLightColor = (int) Math.floor(progress / 510f * 100);
				seekBarText.setText(initLightColor + "%");
				changeLightColor(initLightColor,seekBarText);
			}

			
		});
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		holder.setContentView(view);
		holder.setShowDialog(true);
		holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}
	protected void setActionInfoData(String data, AutoActionInfo autoActionInfo) {
		String epData = autoActionInfo.getEpData();
		boolean isDataSetted=false;
		JSONArray etrDataArr = autoActionInfo.getEtrDataArr();
		if(epData==null||epData.equals("")){
			autoActionInfo.setEpData(data);
		}else{
			if(data.startsWith(epData.charAt(0)+"")){
				autoActionInfo.setEpData(data);
			}else{
				if(etrDataArr!=null&&etrDataArr.size()>0){
					for(int i=0;i<etrDataArr.size();i++){
						System.out.println("--------------"+etrDataArr.get(i));
						if(((String)etrDataArr.get(i)).startsWith(data.charAt(i)+"")){
							autoActionInfo.getEtrDataArr().set(i, data);
							isDataSetted=true;
						}
					}
					if(isDataSetted==false&&etrDataArr.size()<2){
						autoActionInfo.getEtrDataArr().add(data);
					}
				}else{
					autoActionInfo.setEtrDataArr(new JSONArray());
					autoActionInfo.getEtrDataArr().add(data);
				}
				
			}
		}
	}

	private void initSelectDataViewStatus(AutoActionInfo autoActionInfo) {
		String epData = autoActionInfo.getEpData();
//		JSONArray etrDataArr = autoActionInfo.getEtrDataArr();
		JSONArray etrDataArr = autoActionInfo.getEtrDataArr();
		if(epData == null)
			epData = "";
		changeViewStatusByData(epData);
		if(etrDataArr!=null&&etrDataArr.size()>0){
			for(int i=0;i<etrDataArr.size();i++){
				changeViewStatusByData((String)etrDataArr.get(i));
			}
		}
	}

	private void changeViewStatusByData(String data) {
		if(data.startsWith(PREFIX_DATA_Light)){
			String lightText = data.substring(1);
			int lightProcess = StringUtil.toInteger(lightText);
			seekBarLight.setProgress(lightProcess);
			lightTextView.setText((int)(lightProcess/255.0*100)+"%");
		}else if(data.startsWith(PREFIX_DATA_Temp)){
			String tempText = data.substring(1);
			int tempProcess = StringUtil.toInteger(tempText);
			seekBarTemp.setProgress(tempProcess);
			seekBarText.setText((int)(tempProcess/510f * 100) + "%");
			changeLightColor((int)(tempProcess/510f * 100),seekBarText);
		}
	}

	@Override
	public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater,String ep,String epData) {
		if(epData == null)
			epData = "";
		View view =  inflater.inflate(cc.wulian.smarthomev5.R.layout.scene_task_control_data_temp_light, null);
		linkTaskControlEPData = new StringBuffer(epData);
		final TextView lightTextView = (TextView)view.findViewById(R.id.dev_state_textview_0);
		SeekBar seekBarLight = (SeekBar)view.findViewById(R.id.dev_state_seekbar_0);
		seekBarLight.setProgress(0);
		SeekBar seekBarTemp = (SeekBar)view.findViewById(R.id.dev_state_seekbar_1);
		seekBarTemp.setProgress(0);
		final Button seekBarText = (Button) view.findViewById(R.id.scene_link_adjust_color);
		changeLightColor(0,seekBarText);
		if(epData.startsWith(PREFIX_DATA_Light)){
			String lightText = linkTaskControlEPData.substring(1);
			int lightProcess = StringUtil.toInteger(lightText);
			seekBarLight.setProgress(lightProcess);
			lightTextView.setText((int)(lightProcess/255.0*100)+"%");
		}else if(epData.startsWith(PREFIX_DATA_Temp)){
			String tempText = linkTaskControlEPData.substring(1);
			int tempProcess = StringUtil.toInteger(tempText);
			seekBarTemp.setProgress(tempProcess);
			seekBarText.setText((int)(tempProcess/510f * 100) + "%");
			changeLightColor((int)(tempProcess/510f * 100),seekBarText);
		}
		seekBarLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mSeekProgress = seekBar.getProgress();
				if(mSeekProgress < 5){
					mSeekProgress = 0;
					seekBar.setProgress(mSeekProgress);
				}
				linkTaskControlEPData =  new StringBuffer(PREFIX_DATA_Light+StringUtil.appendLeft(mSeekProgress+"", 3, '0'));
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
		seekBarTemp.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mSeekProgress = seekBar.getProgress();
				linkTaskControlEPData =  new StringBuffer(PREFIX_DATA_Temp+StringUtil.appendLeft(mSeekProgress+"", 3, '0'));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				int  initLightColor = (int) Math.floor(progress / 510f * 100);
				seekBarText.setText(initLightColor + "%");
				changeLightColor(initLightColor,seekBarText);
			}

			
		});
		return createControlDataDialog(inflater.getContext(), view);
	}
	
	private void changeLightColor(int initLightColor, Button seekBarText) {

		GradientDrawable bgShape = (GradientDrawable) seekBarText.getBackground();
		if(initLightColor >=0 &&initLightColor <=15){
			bgShape.setColor(getResources().getColor(R.color.coldwhite1));
		}else if(initLightColor > 15 && initLightColor <= 30){
			bgShape.setColor(getResources().getColor(R.color.coldwhite2));
		}else if(initLightColor > 30 && initLightColor <= 50){
			bgShape.setColor(getResources().getColor(R.color.neutralwhite1));
		}else if(initLightColor > 50 && initLightColor <= 70){
			bgShape.setColor(getResources().getColor(R.color.neutralyellow));
		}else if(initLightColor > 70 && initLightColor <= 85){
			bgShape.setColor(getResources().getColor(R.color.warmyellow1));
		}else if(initLightColor > 85 && initLightColor <= 100){
			bgShape.setColor(getResources().getColor(R.color.warmyellow2));
		}
	
	}
}
