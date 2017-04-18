package cc.wulian.app.model.device.impls.controlable.led;

import java.util.Arrays;
import java.util.List;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.SpannableUtil;
import cc.wulian.app.model.device.view.ColorPickerView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupMenuPopupWindow;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.WLEditText;

import com.alibaba.fastjson.JSONArray;
import com.yuantuo.customview.seekcircle.SeekCircle;
import com.yuantuo.customview.seekcircle.SeekCircle.OnSeekCircleChangeListener;
import com.yuantuo.customview.ui.WLToast;

/**
 * 0:关,100:开,1～99:亮度
 */

//000000FF 暂定FF为暖白色值(关闭后再打开灯会回00和000000FF) 前面为RGB相应值  炫彩也表示为第一个on
//以后升级可能在FF后面加上相应的状态
@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_LIGHT_LED}, category = Category.C_LIGHT)
public class WL_90_Light_Led extends ControlableDeviceImpl
{
	private static final String DATA_CTRL_STATE_OPEN_2255 = "2255";
	private static final String DATA_CTRL_STATE_OPEN_D255 = "D255";
	private static final String DATA_CTRL_STATE_CLOSE_D000 = "D000";
	private static final String DATA_CTRL_STATE_CLOSE_2000 = "2000";
	
	private static final String DATA_CTRL_STATE_OPEN = "02FF";
	private static final String DATA_CTRL_STATE_OPEN2 = "44FF";
	private static final String DATA_CTRL_STATE_CLOSE = "0200";
	private static final String DATA_CTRL_STATE_CLOSE2 = "4400";

	private static final int SMALL_OPEN_D = R.drawable.device_light_led_auto;
	private static final int SMALL_CLOSE_D = R.drawable.device_light_led_comman;

	private static final int BIG_BOTTOM_MASK_D = R.drawable.device_d_light_bg_big;
	private static final int BIG_COVER_MASK_D = R.drawable.device_d_light_big;
	private static final String PREFIX_DATA_DEFAULT = "00";
	private static final String PREFIX_DATA_PALETTE = "01";
	private static final String PREFIX_DATA_DIMMING = "02";
	private static final String PREFIX_DATA_DATE = "03";
	private static final String PREFIX_DATA_ADD_GROUP = "05";
	private static final String PREFIX_DATA_QUERY = "06";
	private static final String PREFIX_DATA_BACK_GROUND = "07";
	private static final String PREFIX_DATA_AUTO = "09";
	private static final String PREFIX_DATA_WARM = "44";
	private static final String PREFIX_DATA_OBTAIN_DATA = "08";
//	private static final String PREFIX_DATA_SEND_OBTAIN_DATA = "8";
	
	private static final String PREFIX_DATA_Temp = "D";
	private static final String PREFIX_DATA_Light_color = "1";
	private static final String PREFIX_DATA_Light = "2";
	private static final String PREFIX_DATA_Light_colorful = "9";
	private static final String PREFIX_DATA_colorful_time = "3";

	private int rr = 0;// 色度
	private int gg = 0;// 饱和度
	private int bb = 0;// 亮度
	
	private int luminanceValue = 0;// 亮度值
	private int luminabcePresent = 0;
	private int addGroupResult;// 加组结果
	private int groupCount;// 组数
	private int groupNumber;// 组号
	private int backGroupResult;// 加组结果
	public  int time = 0;
	private boolean isAuto = false;// 是否为炫彩模式
	
	private int warmValue = 0;
	private int warmPresent = 0;
	
	private SeekCircle mSeekBar;
	private TextView percent;
	private Button mColorMode;
	private ColorPickerView colorPickerView;
	private RelativeLayout colorLayout;
	private View layoutView;
	private SeekCircle seekBarWarm;
	private TextView warmTextView;
	
	private Button mLedBrightBn;
	private Button mLedColorBn;
	private LinearLayout mLedBrightLayout;
	private LinearLayout mLedColorLayout;
	private LinearLayout mLedBrightEnableLayout;
	private LinearLayout mLedColorEnableLayout;
	private EditText mLedColorViewEdit;
	
	
	private boolean isLightOn = false;
	private boolean isLightColorOn = false;
	private boolean idLightOff = false;
	
	public WL_90_Light_Led( Context context, String type )
	{
		super(context, type);

	}


	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if(v == mLedBrightBn){
				mLedBrightBn.setBackgroundResource(R.drawable.device_one_wried_wireless_input_btn_pressed);
				mLedColorBn.setBackgroundResource(R.drawable.device_one_wried_wireless_output_btn_normal);
				mLedBrightLayout.setVisibility(View.VISIBLE);
				mLedColorLayout.setVisibility(View.GONE);
			}else if(v == mLedColorBn){
				mLedBrightBn.setBackgroundResource(R.drawable.device_one_wried_wireless_input_btn_normal);
				mLedColorBn.setBackgroundResource(R.drawable.device_one_wried_wireless_output_btn_pressed);
				mLedBrightLayout.setVisibility(View.GONE);
				mLedColorLayout.setVisibility(View.VISIBLE);
			}
		}
		
	};
	@Override
	public String getOpenProtocol() {
		// no used for d light
		return DATA_CTRL_STATE_OPEN_2255;
	}

	@Override
	public String getCloseProtocol() {
		return DATA_CTRL_STATE_CLOSE_D000;
	}


	@Override
	public String getOpenSendCmd() {
		return DATA_CTRL_STATE_OPEN_2255;
	}

	@Override
	public String getCloseSendCmd() {
		return DATA_CTRL_STATE_CLOSE_D000;
	}


	public String getOpenColorSendCmd() {
		return DATA_CTRL_STATE_OPEN_D255;
	}

	public String getOpenColorProtocol() {
		return getOpenColorSendCmd();
	}
	
	@Override
	public boolean isOpened() {
		return isLightOn;
	}
	public boolean isOpenedColor() {
		return isLightColorOn;
	}
	@Override
	public boolean isClosed() {
		return idLightOff;
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
			if (isOpened() || isStoped()) {
				/*
				 * Note: sometimes icon can not see, not use alpha··· int epDataInt = StringUtil.toInteger(epData); icon = getDrawable(SMALL_OPEN_D).mutate();
				 * icon.setAlpha(convert2Alpha(epDataInt));
				 */
				icon = getDrawable(SMALL_OPEN_D);
			}
			else if (isClosed()) {
				icon = getDrawable(SMALL_CLOSE_D);
			}
			else {
				icon = WL_90_Light_Led.this.getDefaultStateSmallIcon();
			}
			return icon;
		}

		@Override
		public CharSequence parseDataWithProtocol(String epData) {
			String state = null;
			int color = COLOR_NORMAL_ORANGE;

			if (isClosed()) {
				state = getString(R.string.device_state_close);
				color = COLOR_NORMAL_ORANGE;
			}
			else {
				int epDataInt = StringUtil.toInteger(epData);
				state = epDataInt == 100 ? getString(R.string.device_state_open) : (epData + " %");
				color = COLOR_CONTROL_GREEN;
			}
			return SpannableUtil.makeSpannable(state, new ForegroundColorSpan(getColor(color)));
		}


		@Override
		public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle saveState ) {
		 layoutView = inflater.inflate(R.layout.device_with_led_color, container, false);
		 colorLayout = (RelativeLayout) layoutView.findViewById(R.id.dev_state_colorlayout0);	
		 return layoutView;
		}
		@Override
		public void onViewCreated( View view, Bundle saveState ) {
			super.onViewCreated(view, saveState);
			
			mLedBrightBn = (Button) view.findViewById(R.id.device_led_bright);
			mLedColorBn = (Button) view.findViewById(R.id.device_led_color);
			mLedBrightBn.setOnClickListener(mOnClickListener);
			mLedColorBn.setOnClickListener(mOnClickListener);
			mLedBrightLayout = (LinearLayout) view.findViewById(R.id.device_led_bright_layout);
			mLedColorLayout = (LinearLayout) view.findViewById(R.id.device_led_color_layout);
			mLedBrightEnableLayout = (LinearLayout) view.findViewById(R.id.dev_state_seekbar_light_led_layout);
			mLedColorEnableLayout = (LinearLayout) view.findViewById(R.id.dev_state_seekbar_color_led_layout);
			mLedColorViewEdit = (EditText) view.findViewById(R.id.dev_state_colorlayout_edit);

			mSeekBar = (SeekCircle) view.findViewById(R.id.dev_state_seekbar_light_led);
			mSeekBar.setOnSeekCircleChangeListener(new OnSeekCircleChangeListener() {

				@Override
				public void onProgressChanged(SeekCircle seekCircle,
						int progress, boolean fromUser) {
					if (fromUser) {
						percent.setText(progress+"%");
					}
				}

				@Override
				public void onStartTrackingTouch(SeekCircle seekCircle) {
				}

				@Override
				public void onStopTrackingTouch(SeekCircle seekCircle) {
					int mSeekProgress = seekCircle.getProgress();
					if(mSeekProgress < 2){
						mSeekProgress = 0;
					}else if(mSeekProgress > 98){
						mSeekProgress = 100;
					}else {
						mSeekProgress = mSeekProgress + 1;
					}
					final int mAdjustLight = mSeekProgress *255 / 100;
					fireWulianDeviceRequestControlSelf();
					if(mAdjustLight == 0){
						controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),PREFIX_DATA_Light + StringUtil.appendLeft(mAdjustLight+"",3,'0'));
						controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),PREFIX_DATA_Temp + StringUtil.appendLeft(mAdjustLight+"",3,'0'));
					}else{
						controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),PREFIX_DATA_Light + StringUtil.appendLeft(mAdjustLight+"",3,'0'));
					}
//					if(mAdjustLight == 0){
//						TaskExecutor.getInstance().executeDelay(new Runnable() {
//							
//							@Override
//							public void run() {
//								controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),PREFIX_DATA_Temp + StringUtil.appendLeft(mAdjustLight+"",3,'0'));
//							}
//						},600);
//					}
				}
			});
			percent = (TextView) view.findViewById(R.id.dev_state_textview_0);
			mColorMode = (Button) view.findViewById(R.id.dev_state_imageview_0);
			mColorMode.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick( View v ) {
					
					if (isAuto) {
//						mColorMode.setBackgroundResource(R.drawable.device_light_led_bright_mode);
//						isAuto = false;
//						createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, "90",true);
						controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),"90");
						fireWulianDeviceRequestControlSelf();
					}
					else {
//						mColorMode.setBackgroundResource(R.drawable.device_light_led_bright_mode1);
//						isAuto = true;
//						createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, "91",true);
						controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),"91");
						fireWulianDeviceRequestControlSelf();
					}

				}
			});
			warmTextView = (TextView)view.findViewById(R.id.dev_state_textview_warm);
			seekBarWarm = (SeekCircle)view.findViewById(R.id.dev_state_seekbar_warm);
			seekBarWarm.setOnSeekCircleChangeListener(new OnSeekCircleChangeListener() {

				@Override
				public void onProgressChanged(SeekCircle seekCircle,
						int progress, boolean fromUser) {
					if(fromUser){
						warmTextView.setText(progress+"%");
					}
				}

				@Override
				public void onStartTrackingTouch(SeekCircle seekCircle) {
				}

				@Override
				public void onStopTrackingTouch(SeekCircle seekCircle) {
					int mSeekProgress = seekCircle.getProgress();
					if(mSeekProgress < 2){
						mSeekProgress = 0;
					}else if(mSeekProgress > 98){
						mSeekProgress = 100;
					}else {
						mSeekProgress = mSeekProgress + 1;
					}
					int mAdjustWarm = mSeekProgress *255 / 100;
//					createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, "D"+StringUtil.appendLeft(mAdjustWarm + "", 3, '0'), true);
					controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),"D"+StringUtil.appendLeft(mAdjustWarm + "", 3, '0'));
					fireWulianDeviceRequestControlSelf();
				}
			});
			warmTextView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(warmPresent == 0){
						controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),DATA_CTRL_STATE_OPEN_D255);
					}else{
						controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),DATA_CTRL_STATE_CLOSE_D000);
					}
					
				}
			});
			percent.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if(luminabcePresent == 0){
						controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),DATA_CTRL_STATE_OPEN_2255);
					}else{
						controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),DATA_CTRL_STATE_CLOSE_2000);
					}
					
				}
			});
			addColorView();

			mLedColorViewEdit.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					mLedColorViewEdit.setCursorVisible(true);
				}
			});

			mLedColorViewEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_DONE || actionId==EditorInfo.IME_ACTION_GO
							||actionId==EditorInfo.IME_ACTION_SEARCH||actionId==EditorInfo.IME_ACTION_SEND
							||actionId == EditorInfo.IME_ACTION_NEXT ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) {
						String colorValue = mLedColorViewEdit.getText().toString();
						if (!StringUtil.isNullOrEmpty(colorValue) && colorValue.length() == 6) {
							int rrInt = Integer.valueOf(colorValue.substring(0, 2), 16);
							int ggInt = Integer.valueOf(colorValue.substring(2, 4), 16);
							int bbInt = Integer.valueOf(colorValue.substring(4, 6), 16);
							colorPickerView.setColor(Color.rgb(rrInt, ggInt, bbInt));
							int max = rrInt;
							if (max < ggInt) {
								max = ggInt;
							}
							if (max < bbInt) {
								max = bbInt;
							}
							String data = PREFIX_DATA_Light_color + StringUtil.appendLeft(String.valueOf(rrInt), 3, '0')
									+ StringUtil.appendLeft(String.valueOf(ggInt), 3, '0')
									+ StringUtil.appendLeft(String.valueOf(bbInt), 3, '0')
									+ StringUtil.appendLeft(String.valueOf(max), 3, '0');
							controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),data);
							fireWulianDeviceRequestControlSelf();
						}
					}
					return false;
				}
			});
//			controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),PREFIX_DATA_SEND_OBTAIN_DATA);
		}
		@Override
		public void initViewStatus() {
			if(isAuto){
				mLedBrightBn.setEnabled(false);
				mLedColorBn.setEnabled(false);
				mLedBrightEnableLayout.setVisibility(View.VISIBLE);
				mLedColorEnableLayout.setVisibility(View.VISIBLE);
				mLedBrightEnableLayout.setEnabled(false);
				mLedColorEnableLayout.setEnabled(false);
			}else{
				mLedBrightBn.setEnabled(true);
				mLedColorBn.setEnabled(true);
				mLedBrightEnableLayout.setVisibility(View.GONE);
				mLedColorEnableLayout.setVisibility(View.GONE);
				mLedBrightEnableLayout.setEnabled(true);
				mLedColorEnableLayout.setEnabled(true);
			}
			mSeekBar.setProgress(StringUtil.toInteger(luminabcePresent));
			if(luminabcePresent == 0){
				percent.setText(getResources().getString(R.string.default_progress_on));
			}else{
				percent.setText(getResources().getString(R.string.default_progress_off));
			}
			colorPickerView.setColor(Color.rgb(rr, gg, bb));
			showColorValueOnEditView(rr , gg, bb);
			mColorMode.setBackgroundResource(isAuto ? R.drawable.device_light_led_bright_mode1 : R.drawable.device_light_led_bright_mode);
			seekBarWarm.setProgress(StringUtil.toInteger(warmPresent));
			if(warmPresent == 0){
				warmTextView.setText(getResources().getString(R.string.default_progress_on));
			}else {
				warmTextView.setText(getResources().getString(R.string.default_progress_off));
			}
		}

		private void addColorView(){
			WindowManager wm = (WindowManager)mContext
	                .getSystemService(Context.WINDOW_SERVICE);

			int width = (int)(wm.getDefaultDisplay().getWidth()/2.2);
			int height = (int)(wm.getDefaultDisplay().getHeight()/2.2);
			if(width > height)
				width = height;
			colorPickerView = new ColorPickerView(mContext,width, width, Color.rgb(rr, gg,bb), new ColorPickerView.OnColorChangedListener()
			{
				@Override
				public void colorChanged( String color ) {
					
					if (color == null || color.length() < 8) return;

					int r = StringUtil.toInteger((color).substring(2, 4), 16);
					int g = StringUtil.toInteger((color).substring(4, 6), 16);
					int b = StringUtil.toInteger((color).substring(6, 8), 16);
					int max = r;
					if (max < g) {
						max = g;
					}
					if (max < b) {
						max = b;
					}
					showColorValueOnEditView(r, g, b);
					String data = "1" + StringUtil.appendLeft(r+"", 3, '0') + StringUtil.appendLeft(g+"", 3, '0') + StringUtil.appendLeft(b+"", 3, '0') +StringUtil.appendLeft(max+"", 3, '0');
//					createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, data,true);
					controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),data);
					fireWulianDeviceRequestControlSelf();
				}
			});

			RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
			params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
			colorPickerView.setLayoutParams(params);
			colorLayout.addView(colorPickerView);
		}	

	//设置颜色值显示
	private void showColorValueOnEditView(int r, int g, int b){
		//显示颜色十六进制值
		String rF = parseIntToHexStr(r);
		String gF = parseIntToHexStr(g);
		String bF = parseIntToHexStr(b);
		mLedColorViewEdit.setText(rF + gF +bF);
		mLedColorViewEdit.setCursorVisible(false);
	}

	private void disassembleCompoundCmd(String epData ) {
		if (isNull(epData)) return;
		//0101F20DF201
		if (epData.startsWith(PREFIX_DATA_PALETTE) && epData.length() >= 10 ) {
			if(!isAuto){
				if(StringUtil.toInteger((epData).substring(8, 10), 16) == 0){
					warmValue = 0;
					warmPresent = (int)(warmValue * 100 / 255.0);
					luminanceValue = 0;
					luminabcePresent =  (int)(luminanceValue * 100 / 255.0);
					idLightOff = true;
					isLightOn = false;
					isLightColorOn = false;
				}else{
					if(StringUtil.toInteger(epData.substring(2, 4), 16) != 0
							|| StringUtil.toInteger(epData.substring(4, 6), 16) != 0
							|| StringUtil.toInteger(epData.substring(6, 8), 16) != 0){
						rr = StringUtil.toInteger(epData.substring(2, 4), 16) ;
						gg = StringUtil.toInteger(epData.substring(4, 6), 16) ;
						bb = StringUtil.toInteger(epData.substring(6, 8), 16);
						luminanceValue = StringUtil.toInteger(epData.substring(8, 10), 16);
						luminabcePresent =  (int)(luminanceValue * 100 / 255.0);
						warmValue = 0;
						warmPresent = (int)(warmValue * 100 / 255.0);
						idLightOff = false;
						isLightOn = true;
						isLightColorOn = false;
					}else{
						warmValue = StringUtil.toInteger(epData.substring(8, 10), 16);
						warmPresent = (int)(warmValue * 100 / 255.0);
						luminanceValue = 0;
						luminabcePresent =  (int)(luminanceValue * 100 / 255.0);
						idLightOff = false;
						isLightOn = false;
						isLightColorOn = true;
					}
				}
				isAuto = false;
			}
		}
		else if (epData.startsWith(PREFIX_DATA_DIMMING) && epData.length() >= 4) {
			luminanceValue = StringUtil.toInteger(epData.substring(2, 4), 16);
			luminabcePresent =  (int)(luminanceValue * 100 / 255.0);
			warmValue = 0;
			warmPresent = (int)(warmValue * 100 / 255.0);
			isAuto = false;
			if(StringUtil.toInteger(epData.substring(2, 4), 16) == 0){
				idLightOff = true;
				isLightOn = false;
			}else{
				idLightOff = false;
				isLightOn = true;
			}
			isLightColorOn = false;
			isAuto = false;
		}
		else if (epData.startsWith(PREFIX_DATA_ADD_GROUP) && epData.length() >= 4) {
			addGroupResult = StringUtil.toInteger(epData.substring(2, 4), 16);
		}
		else if (epData.startsWith(PREFIX_DATA_QUERY) && epData.length() >= 6) {
			groupCount = StringUtil.toInteger(epData.substring(2, 4), 16);
			groupNumber = StringUtil.toInteger(epData.substring(4, 6), 16);
		}
		else if (epData.startsWith(PREFIX_DATA_BACK_GROUND) && epData.length() >= 4) {
			backGroupResult = StringUtil.toInteger(epData.substring(2, 4), 16);
		}
		else if (epData.startsWith(PREFIX_DATA_AUTO) && epData.length() >= 4) {
			isAuto = (StringUtil.toInteger(epData.substring(2, 4), 16) == 1) ? true : false;
			warmValue = 0;
			warmPresent = (int)(warmValue * 100 / 255.0);
			luminanceValue = 0;
			luminabcePresent =  (int)(luminanceValue * 100 / 255.0);
			idLightOff = false;
			isLightOn = true;
			isLightColorOn = false;
		}
		else if (epData.startsWith(PREFIX_DATA_DATE) && epData.length() >= 6) {
			time = StringUtil.toInteger(epData.substring(2, 6), 16);
		}else if(epData.startsWith(PREFIX_DATA_OBTAIN_DATA) && epData.length() >= 12){
			
		}else if(epData.startsWith(PREFIX_DATA_WARM)){
			warmValue = StringUtil.toInteger(epData.substring(2, 4), 16);
			warmPresent = (int)(warmValue * 100 / 255.0);
			luminanceValue = 0;
			luminabcePresent =  (int)(luminanceValue * 100 / 255.0);
			isAuto = false;
			if(StringUtil.toInteger(epData.substring(2, 4), 16) == 0){
				idLightOff = true;
				isLightColorOn = false;
			}else{
				idLightOff = false;
				isLightColorOn = true;
			}
			isLightOn = false;
		}else if(epData.length() >= 8 ){
			if(!isAuto){
				if(StringUtil.toInteger((epData).substring(6, 8), 16) == 0){
					warmValue = 0;
					warmPresent = (int)(warmValue * 100 / 255.0);
					luminanceValue = 0;
					luminabcePresent =  (int)(luminanceValue * 100 / 255.0);
					idLightOff = true;
					isLightOn = false;
					isLightColorOn = false;
				}else{
					if(StringUtil.toInteger((epData).substring(0, 2), 16) != 0 
							|| StringUtil.toInteger((epData).substring(2, 4), 16) != 0
							|| StringUtil.toInteger((epData).substring(4, 6), 16) != 0){
						rr = StringUtil.toInteger(epData.substring(0, 2), 16) ;
						gg = StringUtil.toInteger(epData.substring(2, 4), 16) ;
						bb = StringUtil.toInteger(epData.substring(4, 6), 16);
						luminanceValue = StringUtil.toInteger(epData.substring(6, 8), 16);
						luminabcePresent =  (int)(luminanceValue * 100 / 255.0);
						warmValue = 0;
						warmPresent = (int)(warmValue * 100 / 255.0);
						idLightOff = false;
						isLightOn = true;
						isLightColorOn = false;
					}else{
						warmValue = StringUtil.toInteger(epData.substring(6, 8), 16);
						warmPresent = (int)(warmValue * 100 / 255.0);
						luminanceValue = 0;
						luminabcePresent =  (int)(luminanceValue * 100 / 255.0);
						idLightOff = false;
						isLightOn = false;
						isLightColorOn = true;
					}
				}
				isAuto = false;
			}
		}else if(epData.length() >= 2 && epData.startsWith(PREFIX_DATA_DEFAULT)){
			if(StringUtil.toInteger((epData).substring(0, 2), 16) == 0){
				idLightOff = true;
				isLightOn = false;
				isLightColorOn = false;
			}
			warmValue = 0;
			warmPresent = (int)(warmValue * 100 / 255.0);
			luminanceValue = 0;
			luminabcePresent =  (int)(luminanceValue * 100 / 255.0);
			isAuto = false;
		}
	}

	
	public static int[] rgb2hsb( int rgbR, int rgbG, int rgbB ) {
		assert 0 <= rgbR && rgbR <= 255;
		assert 0 <= rgbG && rgbG <= 255;
		assert 0 <= rgbB && rgbB <= 255;
		int[] rgb = new int[]{rgbR, rgbG, rgbB};
		Arrays.sort(rgb);
		int max = rgb[2];
		int min = rgb[0];

		float hsbB = (max / 255.0f) * 255;
		float hsbS = (max == 0 ? 0 : (max - min) / (float) max) * 255;

		float hsbH = 0;
		if (max == rgbR && rgbG >= rgbB) {
			hsbH = ((rgbG - rgbB) * 60f / (max - min) + 0) / 360f * 255;
		}
		else if (max == rgbR && rgbG < rgbB) {
			hsbH = ((rgbG - rgbB) * 60f / (max - min) + 360) / 360f * 255;
		}
		else if (max == rgbG) {
			hsbH = ((rgbB - rgbR) * 60f / (max - min) + 120) / 360f * 255;
		}
		else if (max == rgbB) {
			hsbH = ((rgbR - rgbG) * 60f / (max - min) + 240) / 360f * 255;
		}

		return new int[]{(int) hsbH, (int) hsbS, (int) hsbB};
	}

	public static int[] hsb2rgb( float h, float s, float v ) {
		assert Float.compare(h, 0.0f) >= 0 && Float.compare(h, 360.0f) <= 0;
		assert Float.compare(s, 0.0f) >= 0 && Float.compare(s, 1.0f) <= 0;
		assert Float.compare(v, 0.0f) >= 0 && Float.compare(v, 1.0f) <= 0;

		float r = 0, g = 0, b = 0;
		int i = (int) ((h / 60) % 6);
		float f = (h / 60) - i;
		float p = v * (1 - s);
		float q = v * (1 - f * s);
		float t = v * (1 - (1 - f) * s);
		switch (i) {
			case 0 :
				r = v;
				g = t;
				b = p;
				break;
			case 1 :
				r = q;
				g = v;
				b = p;
				break;
			case 2 :
				r = p;
				g = v;
				b = t;
				break;
			case 3 :
				r = p;
				g = q;
				b = v;
				break;
			case 4 :
				r = t;
				g = p;
				b = v;
				break;
			case 5 :
				r = v;
				g = p;
				b = q;
				break;
			default :
				break;
		}
		return new int[]{(int) (r * 255.0), (int) (g * 255.0), (int) (b * 255.0)};
	}

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item,LayoutInflater inflater){
		if (item == null) {
			item = new ControlableDeviceShortCutControlItem(inflater.getContext());
		}
		item.setWulianDevice(this);
		return item;
	}
	
	@Override
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,
			AutoActionInfo autoActionInfo) {
		if(item == null){
			item = new ShortCutControlableDeviceSelectDataItem(inflater.getContext());
		}
		item.setWulianDeviceAndSelectData(this, autoActionInfo);
		return item;
	}
	
	
	public void clickOpenLight() {
		controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),getOpenProtocol());
	}
	public void clickOpenLightColor() {
		controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),getOpenColorProtocol());
	}
	public void clickCloseWarmWhite() {
		controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),getCloseProtocol());
	}
	public void clickClose() {
		controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),getCloseProtocol());
	}
	//guanjia
	private class ShortCutControlableDeviceSelectDataItem extends DeviceShortCutSelectDataItem{
		private LinearLayout controlableLineLayout;
		private ImageView openImageView;
		private ImageView openImageViewColor;
		private ImageView closeImageView;
		private ImageView stopImageView;
		
		private boolean isOpenLightVisiable = true;
		private boolean isOpenlightColorVisiable = true;
		private boolean isCloseVisiable = true;
		private OnClickListener cliclListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(v == openImageView){
					clickOpenLight();
				}else if(v == openImageViewColor){
					clickOpenLightColor();
				}else if(v == closeImageView){
//					if(isOpenedColor()){
//						clickCloseWarmWhite();
//					}else if(isOpened()){
//						clickClose();
//					}
					// 场景、管家中选择关闭
					clickClose();
				}
			}
		};
		public ShortCutControlableDeviceSelectDataItem(Context context) {
			super(context);
			controlableLineLayout = (LinearLayout)inflater.inflate(R.layout.device_short_cut_control_controlable, null);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
			lp.rightMargin = DisplayUtil.dip2Pix(mContext, 10);
			openImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_open_iv);
			openImageViewColor = new ImageView(controlableLineLayout.getContext());
			openImageViewColor.setBackgroundResource(R.drawable.device_ctrl_open_color);
			controlableLineLayout.addView(openImageViewColor,1,lp);
			stopImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_stop_iv);
			stopImageView.setVisibility(View.GONE);
			closeImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_close_iv);
			controlLineLayout.addView(controlableLineLayout);
		}
		@Override
		public void setWulianDeviceAndSelectData(WulianDevice device,
				AutoActionInfo autoActionInfo) {
			super.setWulianDeviceAndSelectData(device, autoActionInfo);
			final String SPLIT_SYMBOL = ">";
			final String[] type = autoActionInfo.getObject().split(SPLIT_SYMBOL);
			if(device instanceof Controlable){
				if(isOpenLightVisiable){
					openImageView.setVisibility(View.VISIBLE);
					if(isOpenLight() && StringUtil.equals(type[2], WulianDevice.EP_0)){
						openImageView.setSelected(true);
						contentBackgroundLayout.setBackgroundResource(R.drawable.account_manager_item_background);
					}else{
						openImageView.setSelected(false);
						openImageView.setOnClickListener(cliclListener);
					}
				}else{
					openImageView.setVisibility(View.GONE);
				}
				if(isOpenlightColorVisiable){
					openImageViewColor.setVisibility(View.VISIBLE);
					if(isOpenLightColor() && StringUtil.equals(type[2], WulianDevice.EP_0)){
						openImageViewColor.setSelected(true);
						contentBackgroundLayout.setBackgroundResource(R.drawable.account_manager_item_background);
					}else{
						openImageViewColor.setSelected(false);
						openImageViewColor.setOnClickListener(cliclListener);
					}
				}
				else{
					openImageViewColor.setVisibility(View.GONE);
				}
				if(isCloseVisiable){
					if((isClosed() || isCloseWarmWhite()) && StringUtil.equals(type[2], WulianDevice.EP_0)){
						closeImageView.setSelected(true);
						contentBackgroundLayout.setBackgroundResource(R.drawable.account_manager_item_background);
					}else{
						closeImageView.setSelected(false);
						closeImageView.setOnClickListener(cliclListener);
					}
				}else{
					closeImageView.setVisibility(View.GONE);
				}
			}
		}
		private void clickClose() {
//			autoActionInfo.setEpData(DATA_CTRL_STATE_CLOSE_2000);
			autoActionInfo.setEpData(getCloseProtocol());
			autoActionInfo.setObject(getDeviceID()+">"+getDeviceType()+">"+WulianDevice.EP_0+">"+getDeviceType());
			fireShortCutSelectDataListener();
		}

		private void clickCloseWarmWhite() {
			autoActionInfo.setEpData(getCloseProtocol());
			autoActionInfo.setObject(getDeviceID()+">"+getDeviceType()+">"+WulianDevice.EP_0+">"+getDeviceType());
			fireShortCutSelectDataListener();
		}

		private void clickOpenLight() {
			autoActionInfo.setEpData(getOpenProtocol());
			autoActionInfo.setObject(getDeviceID()+">"+getDeviceType()+">"+WulianDevice.EP_0+">"+getDeviceType());
			fireShortCutSelectDataListener();
		}
		private void clickOpenLightColor() {
			autoActionInfo.setEpData(getOpenColorProtocol());
			autoActionInfo.setObject(getDeviceID()+">"+getDeviceType()+">"+WulianDevice.EP_0+">"+getDeviceType());
			fireShortCutSelectDataListener();
		}
		
		private boolean isOpenLightColor(){
			String epData = getOpenColorProtocol();
			return StringUtil.equals(epData, this.autoActionInfo.getEpData());
		}
		private boolean isOpenLight(){
				String epData = getOpenProtocol();
				return StringUtil.equals(epData, this.autoActionInfo.getEpData());
		}
		protected boolean isClosed() {
			String epData = DATA_CTRL_STATE_CLOSE_2000;
			return StringUtil.equals(epData, this.autoActionInfo.getEpData());
		}
		protected boolean isCloseWarmWhite() {
			String epData = getCloseProtocol();
			return StringUtil.equals(epData, this.autoActionInfo.getEpData());
		}
	}
	//shebei
	private class ControlableDeviceShortCutControlItem extends DeviceShortCutControlItem{

		private LinearLayout controlableLineLayout;
		private ImageView openImageView;
		private ImageView openImageViewColor;
		private ImageView closeImageView;
		private ImageView stopImageView;
		private LinearLayout openimageviewcolorlayout;
		
		private boolean isOpenLightVisiable = true;
		private boolean isOpenlightColorVisiable = true;
		private boolean isCloseVisiable = true;
		private OnClickListener cliclListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(v == openImageView){
					clickOpenLight();
				}else if(v == openImageViewColor){
					clickOpenLightColor();
				}else if(v == closeImageView){
					if(isOpenedColor()){
						clickCloseWarmWhite();
					}else if(isOpened()){
						clickClose();
					}
				}
			}
		};
		private ControlableDeviceShortCutControlItem(Context context) {
			super(context);
			controlableLineLayout = (LinearLayout)inflater.inflate(R.layout.device_short_cut_control_controlable, null);
//			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 0);
//			lp.rightMargin = DisplayUtil.dip2Pix(mContext, 10);
			openImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_open_iv);
			openImageViewColor = (ImageView) controlableLineLayout.findViewById(R.id.open_imageview_color);
			openimageviewcolorlayout = (LinearLayout) controlableLineLayout.findViewById(R.id.open_imageview_color_layout);//直接通过布局加载是为了能够在设备列表中对齐
//			openImageViewColor = new ImageView(controlableLineLayout.getContext());
//			openImageViewColor.setBackgroundResource(R.drawable.device_ctrl_open_color);
//			controlableLineLayout.addView(openImageViewColor,1,lp);
			stopImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_stop_iv);
			stopImageView.setVisibility(View.GONE);
			closeImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_close_iv);
			controlLineLayout.addView(controlableLineLayout);
		}

		
		@Override
		public void setWulianDevice(WulianDevice device) {
			super.setWulianDevice(device);
			if(device instanceof Controlable){
				if(isOpenLightVisiable){
					openImageView.setVisibility(View.VISIBLE);
					if(isOpened()){
						openImageView.setSelected(true);
					}else{
						openImageView.setSelected(false);
						openImageView.setOnClickListener(cliclListener);
					}
				}else{
					openImageView.setVisibility(View.GONE);
				}
				if(isOpenlightColorVisiable){
//					openImageViewColor.setVisibility(View.VISIBLE);
					openimageviewcolorlayout.setVisibility(View.VISIBLE);
					if(isOpenedColor()){
						openImageViewColor.setSelected(true);
					}else{
						openImageViewColor.setSelected(false);
						openImageViewColor.setOnClickListener(cliclListener);
					}
				}
				else{
					openImageViewColor.setVisibility(View.GONE);
				}
				if(isCloseVisiable){
					if(isClosed()){
						closeImageView.setSelected(true);
					}else{
						closeImageView.setSelected(false);
						closeImageView.setOnClickListener(cliclListener);
					}
				}else{
					closeImageView.setVisibility(View.GONE);
				}
			}
		}
		
	}
	
	public Intent getSettingIntent() {
		Intent intent = new Intent(mContext, DeviceSettingActivity.class);
		intent.putExtra(SettingColorTimeFragment.GWID, gwID);
		intent.putExtra(SettingColorTimeFragment.DEVICEID, devID);
		intent.putExtra(AbstractDevice.SETTING_LINK_TYPE,
				AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
		intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, SettingColorTimeFragment.class.getName());
		return intent;
		
	}

	@Override
	protected List<MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
		List<MenuItem> items = super.getDeviceMenuItems(manager);
		MenuItem settingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext
						.getString(cc.wulian.smarthomev5.R.string.set_titel));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				Intent i = getSettingIntent();
				mContext.startActivity(i);
				manager.dismiss();
			}
		};
		if(isDeviceOnLine())
			items.add(settingItem);
		return items;
	}
	
	@Override
	public void refreshDevice() {
		super.refreshDevice();
		disassembleCompoundCmd( epData);
	}
	@Override
	public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater,String ep,
			String epData) {
		if(epData == null)
			epData = "";
		View view =  inflater.inflate(R.layout.scene_task_control_led_light, null);
		linkTaskControlEPData = new StringBuffer(epData);
		
		RelativeLayout mLedColorLayout = (RelativeLayout) view.findViewById(R.id.dev_state_colorlayout0);
		ColorPickerView colorPickerViewDialog;
		final ToggleButton mColorfulModeDialog = (ToggleButton) view.findViewById(R.id.device_led_toggle_2);
		final ToggleButton mColorModeDialog = (ToggleButton) view.findViewById(R.id.device_led_toggle_1);
		final ToggleButton mLightModeDialog = (ToggleButton) view.findViewById(R.id.device_led_toggle_3);
		final ToggleButton mTempModeDialog = (ToggleButton) view.findViewById(R.id.device_led_toggle_4);

		int rrDialog = 0;
		int ggDialog = 0;
		int bbDialog = 0;
		
		WindowManager wm = (WindowManager)mContext
                .getSystemService(Context.WINDOW_SERVICE);

		int width = (int)(wm.getDefaultDisplay().getWidth()/2.5);
		int height = (int)(wm.getDefaultDisplay().getHeight()/2.5);
		if(width > height)
			width = height;
		colorPickerViewDialog = new ColorPickerView(mContext,width, width, Color.rgb(rrDialog, ggDialog,bbDialog), new ColorPickerView.OnColorChangedListener()
		{
			@Override
			public void colorChanged( String color ) {
				
				if (color == null || color.length() < 8) return;

				int r = StringUtil.toInteger((color).substring(2, 4), 16);
				int g = StringUtil.toInteger((color).substring(4, 6), 16);
				int b = StringUtil.toInteger((color).substring(6, 8), 16);
				int max = r;
				if (max < g) {
					max = g;
				}
				if (max < b) {
					max = b;
				}
				mColorModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
				mTempModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mLightModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mColorfulModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				linkTaskControlEPData = new StringBuffer(PREFIX_DATA_Light_color + StringUtil.appendLeft(r+"", 3, '0') + StringUtil.appendLeft(g+"", 3, '0') + StringUtil.appendLeft(b+"", 3, '0') +StringUtil.appendLeft(max+"", 3, '0'));
//				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, data,true);
			}
		});

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		colorPickerViewDialog.setLayoutParams(params);
		mLedColorLayout.addView(colorPickerViewDialog);
		
//		final boolean isAutoDialog = false;
		ImageView mColorful = (ImageView) view.findViewById(R.id.device_led_colorful_imag);
		mColorful.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick( View v ) {
				
//				if (isAutoDialog) {
//					mColorfulModeDialog.setBackgroundResource(cc.wulian.smarthomev5.R.drawable.device_led_adjust_normal);
//					isAutoDialog = false;
//					linkTaskControlEPData = new StringBuffer("90");
//					
//				}
//				else {
//					mColorfulModeDialog.setBackgroundResource(cc.wulian.smarthomev5.R.drawable.device_led_adjust_select);
//					isAutoDialog = true;
//					linkTaskControlEPData = new StringBuffer("91");
//					
//				}
				mColorfulModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
				mColorModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mTempModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mLightModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				linkTaskControlEPData = new StringBuffer("91");
			}
		});
		
		final TextView bightTextView = (TextView)view.findViewById(R.id.dev_state_textview_0);
		SeekBar seekBarLight = (SeekBar)view.findViewById(R.id.dev_state_seekbar_0);
		seekBarLight.setProgress(0);
		final TextView tempTextView = (TextView)view.findViewById(R.id.dev_state_textview_1);
		SeekBar seekBarTemp = (SeekBar)view.findViewById(R.id.dev_state_seekbar_1);
		seekBarTemp.setProgress(0);
		if(epData.startsWith(PREFIX_DATA_Light)){
			String lightText= linkTaskControlEPData.substring(1);
			int brightProcess = StringUtil.toInteger(lightText);
			seekBarLight.setProgress(brightProcess);
			bightTextView.setText((int)(brightProcess/255.0*100)+"%");
			mLightModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
		}else if(epData.startsWith(PREFIX_DATA_Temp)){
			String tempText= linkTaskControlEPData.substring(1);
			int tempProcess = StringUtil.toInteger(tempText);
			seekBarTemp.setProgress(tempProcess);
			tempTextView.setText((int)(tempProcess/255.0*100)+"%");
			mTempModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
		}else if(epData.startsWith(PREFIX_DATA_Light_color)){
			rrDialog = StringUtil.toInteger(linkTaskControlEPData.substring(1,4));
			ggDialog = StringUtil.toInteger(linkTaskControlEPData.substring(4,7));
			bbDialog = StringUtil.toInteger(linkTaskControlEPData.substring(7,10));
			colorPickerViewDialog.setColor(Color.rgb(rrDialog, ggDialog, bbDialog));
			mColorModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
		}else if(epData.startsWith(PREFIX_DATA_Light_colorful)){
			mColorfulModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
		}
		seekBarLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mSeekProgress = seekBar.getProgress();
				if(mSeekProgress == 0){
					linkTaskControlEPData = new StringBuffer(PREFIX_DATA_Temp+StringUtil.appendLeft(mSeekProgress+"", 3, '0')); 
				}else{
					linkTaskControlEPData = new StringBuffer(PREFIX_DATA_Light+StringUtil.appendLeft(mSeekProgress+"", 3, '0'));
				}
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				bightTextView.setText((int)(seekBar.getProgress()/255.0*100)+"%");
				mLightModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
				mTempModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mColorModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mColorfulModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
			}
		});
		seekBarTemp.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mSeekProgress = seekBar.getProgress();
				linkTaskControlEPData = new StringBuffer(PREFIX_DATA_Temp+StringUtil.appendLeft(mSeekProgress+"", 3, '0'));
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				tempTextView.setText((int)(seekBar.getProgress()/255.0*100)+"%");
				mTempModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
				mColorfulModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mColorModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mLightModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
			}
		});
		
		
		return createControlDataDialog(inflater.getContext(), view);
	}
	

	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater, final AutoActionInfo autoActionInfo) {
		final boolean[] isColorfulTimeWarningShow = {false};
		final boolean[] isColorTimeDialogShow = {true};
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		holder.setShowDialog(false);
		String epData = autoActionInfo.getEpData();
		final View contentView =  inflater.inflate(R.layout.scene_task_control_led_light, null);
		RelativeLayout mLedColorLayout = (RelativeLayout) contentView.findViewById(R.id.dev_state_colorlayout0);
		final ColorPickerView colorPickerViewDialog;
		final ToggleButton mColorModeDialog = (ToggleButton) contentView.findViewById(R.id.device_led_toggle_1);
		final ToggleButton mLightModeDialog = (ToggleButton) contentView.findViewById(R.id.device_led_toggle_3);
		final ToggleButton mTempModeDialog = (ToggleButton) contentView.findViewById(R.id.device_led_toggle_4);
		final ToggleButton mColorfulModeDialog = (ToggleButton) contentView.findViewById(R.id.device_led_toggle_2);
		final ToggleButton mColorfulTimeDialog = (ToggleButton) contentView.findViewById(R.id.device_led_toggle_5);
		final TextView mColorValueTv = (TextView) contentView.findViewById(R.id.device_led_colorful_value);
		final EditText mColorValueEdit = (EditText) contentView.findViewById(R.id.device_led_colorful_edit);

		int rrDialog = 0;
		int ggDialog = 0;
		int bbDialog = 0;
		
		WindowManager wm = (WindowManager)mContext
                .getSystemService(Context.WINDOW_SERVICE);

		int width = (int)(wm.getDefaultDisplay().getWidth()/2.5);
		int height = (int)(wm.getDefaultDisplay().getHeight()/2.5);
		if(width > height)
			width = height;
		colorPickerViewDialog = new ColorPickerView(mContext,width, width, Color.rgb(rrDialog, ggDialog,bbDialog), new ColorPickerView.OnColorChangedListener()
		{
			@Override
			public void colorChanged( String color ) {
				
				if (color == null || color.length() < 8) return;

				int r = StringUtil.toInteger((color).substring(2, 4), 16);
				int g = StringUtil.toInteger((color).substring(4, 6), 16);
				int b = StringUtil.toInteger((color).substring(6, 8), 16);
				int max = r;
				if (max < g) {
					max = g;
				}
				if (max < b) {
					max = b;
				}
				//显示颜色十六进制值
				String rF = parseIntToHexStr(r);
				String gF = parseIntToHexStr(g);
				String bF = parseIntToHexStr(b);
				mColorValueEdit.setText(rF + gF +bF);

				mColorModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
				mTempModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mLightModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mColorfulModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mColorfulTimeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				String epData = PREFIX_DATA_Light_color + StringUtil.appendLeft(r+"", 3, '0') + StringUtil.appendLeft(g+"", 3, '0') + StringUtil.appendLeft(b+"", 3, '0') +StringUtil.appendLeft(max+"", 3, '0');
				autoActionInfo.setEpData(epData);
			}
		});

		mColorValueEdit.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mColorValueEdit.setCursorVisible(true);
			}
		});
		//键盘输入 RGB值 设置颜色
		mColorValueEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE || actionId==EditorInfo.IME_ACTION_GO
						||actionId==EditorInfo.IME_ACTION_SEARCH||actionId==EditorInfo.IME_ACTION_SEND
						||actionId == EditorInfo.IME_ACTION_NEXT ||(event!=null&&event.getKeyCode()== KeyEvent.KEYCODE_ENTER)) {
					String colorValue = mColorValueEdit.getText().toString();
					if(!StringUtil.isNullOrEmpty(colorValue) && colorValue.length() == 6){
						int rrInt = Integer.valueOf(colorValue.substring(0, 2), 16);
						int ggInt = Integer.valueOf(colorValue.substring(2, 4), 16);
						int bbInt = Integer.valueOf(colorValue.substring(4, 6), 16);
						colorPickerViewDialog.setColor(Color.rgb(rrInt, ggInt, bbInt));
						int max = rrInt;
						if (max < ggInt) {
							max = ggInt;
						}
						if (max < bbInt) {
							max = bbInt;
						}
						String epData = PREFIX_DATA_Light_color + StringUtil.appendLeft(String.valueOf(rrInt), 3, '0')
											+StringUtil.appendLeft(String.valueOf(ggInt), 3, '0')
											+ StringUtil.appendLeft(String.valueOf(bbInt), 3, '0')
											+StringUtil.appendLeft(String.valueOf(max), 3, '0');
						autoActionInfo.setEpData(epData);
					}
				}
				return false;
			}
		});

		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		colorPickerViewDialog.setLayoutParams(params);
		mLedColorLayout.addView(colorPickerViewDialog);

		//	选择炫彩模式
		LinearLayout mColorfulLayout = (LinearLayout) contentView.findViewById(R.id.device_led_colorful_layout);
		mColorfulLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mColorfulModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
				mColorModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mTempModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mLightModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mColorfulTimeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				String epData = "91";
				autoActionInfo.setEpData(epData);
			}
		});

		//选中时间间隔提示窗
		final AreaGroupMenuPopupWindow colorTimeWarningPop= new AreaGroupMenuPopupWindow(mContext);
		final View colorTimeWarningContent = LayoutInflater.from(mContext).
				inflate(R.layout.scene_task_control_led_light_prompt_dialog, null);
		colorTimeWarningPop.setContentView(colorTimeWarningContent);
		TextView colorTimeWarningOk = (TextView) colorTimeWarningContent.findViewById(R.id.device_led_colorful_time_prompt);

		//  选中时间间隔
		LinearLayout mColorfulTimeLayout = (LinearLayout) contentView.findViewById(R.id.device_led_colorful_time_layout);
		//炫彩时间显示
		final TextView mColorfulTimeTv = (TextView) contentView.findViewById(R.id.device_led_colorful_time);
		//炫彩时间弹窗
		final AreaGroupMenuPopupWindow colorTimePopWindow =  new AreaGroupMenuPopupWindow(mContext);
		final SettingColorTimeDialog  colorTimeDialog= new SettingColorTimeDialog(mContext);

		mColorfulTimeLayout.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isColorfulTimeWarningShow[0]){
					colorTimeWarningPop.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
				}
				mColorfulTimeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
				mColorModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mTempModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mLightModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mColorfulModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				String epData = PREFIX_DATA_colorful_time + colorTimeDialog.getSettingTime();
				autoActionInfo.setEpData(epData);
				isColorfulTimeWarningShow[0] = true;
			}
		});

		colorTimeWarningOk.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				colorTimeWarningPop.dismiss();
				if(!isColorTimeDialogShow[0]){
					colorTimePopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
					colorTimePopWindow.setContentView(colorTimeDialog);
					colorTimePopWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
					isColorTimeDialogShow[0] = true;
				}
			}
		});

		//点击时间弹出时间选择窗口
		mColorfulTimeTv.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(!isColorfulTimeWarningShow[0]){
					colorTimeWarningPop.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
					isColorTimeDialogShow[0] = false;
				}else{
					colorTimePopWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
					colorTimePopWindow.setContentView(colorTimeDialog);
					colorTimePopWindow.showAtLocation(contentView, Gravity.BOTTOM, 0, 0);
				}
				mColorfulTimeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
				mColorModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mTempModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mLightModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mColorfulModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				String epData = PREFIX_DATA_colorful_time + colorTimeDialog.getSettingTime();
				autoActionInfo.setEpData(epData);
				isColorfulTimeWarningShow[0] = true;
			}
		});
		// 弹出窗口  取消、确定按钮点击事件
		TextView tvDialogPositive = (TextView) colorTimeDialog.findViewById(R.id.colortime_ok);
		TextView tvDialogNagetive = (TextView) colorTimeDialog.findViewById(R.id.colortime_cancel);
		tvDialogNagetive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String epData =  PREFIX_DATA_colorful_time + colorTimeDialog.getSettingTime();
				autoActionInfo.setEpData(epData);
				colorTimePopWindow.dismiss();
			}
		});
		//确定按钮，设置时间间隔
		tvDialogPositive.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String epData =  PREFIX_DATA_colorful_time + colorTimeDialog.getSettingTime();
				autoActionInfo.setEpData(epData);
			//	createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, 3 + colorTimeDialog.getSettingTime(),true);
				int s = colorTimeDialog.getSe();
				int ms = colorTimeDialog.getMss();
				mColorfulTimeTv.setText(s + mContext.getResources().getString(R.string.device_adjust_second_common)+"\b"+
						ms + mContext.getResources().getString(R.string.device_led_ms));
				colorTimePopWindow.dismiss();
			}
		});

		final TextView bightTextView = (TextView)contentView.findViewById(R.id.dev_state_textview_0);
		SeekBar seekBarLight = (SeekBar)contentView.findViewById(R.id.dev_state_seekbar_0);
		seekBarLight.setProgress(0);
		final TextView tempTextView = (TextView)contentView.findViewById(R.id.dev_state_textview_1);
		SeekBar seekBarTemp = (SeekBar)contentView.findViewById(R.id.dev_state_seekbar_1);
		seekBarTemp.setProgress(0);
		if(epData.startsWith(PREFIX_DATA_Light)){
			String lightText= epData.substring(1);
			int brightProcess = StringUtil.toInteger(lightText);
			seekBarLight.setProgress(brightProcess);
			bightTextView.setText((int)(brightProcess/255.0*100)+"%");
			mColorValueEdit.setText("000000");
			mLightModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
		}else if(epData.startsWith(PREFIX_DATA_Temp)){
			String tempText= epData.substring(1);
			int tempProcess = StringUtil.toInteger(tempText);
			seekBarTemp.setProgress(tempProcess);
			tempTextView.setText((int)(tempProcess/255.0*100)+"%");
			mColorValueEdit.setText("000000");
			mTempModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
		}else if(epData.startsWith(PREFIX_DATA_Light_color)){
			rrDialog = StringUtil.toInteger(epData.substring(1,4));
			ggDialog = StringUtil.toInteger(epData.substring(4,7));
			bbDialog = StringUtil.toInteger(epData.substring(7,10));
			//显示颜色十六进制值
			String rF = parseIntToHexStr(rrDialog);
			String gF = parseIntToHexStr(ggDialog);
			String bF = parseIntToHexStr(bbDialog);
			mColorValueEdit.setText(rF + gF +bF);
			colorPickerViewDialog.setColor(Color.rgb(rrDialog, ggDialog, bbDialog));
			mColorModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
		}else if(epData.startsWith(PREFIX_DATA_Light_colorful)){
			mColorValueEdit.setText("000000");
			mColorfulModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
		}else if(epData.startsWith(PREFIX_DATA_colorful_time)){
			int setTime = Integer.parseInt(epData.substring(1 , epData.length()));
			colorTimeDialog.setTime(setTime);
			int s = setTime / 1000;
			int ms = setTime - (s * 1000);
			mColorfulTimeTv.setText(s + mContext.getResources().getString(R.string.device_adjust_second_common)+"\b"+
					ms + mContext.getResources().getString(R.string.device_led_ms));
			mColorValueEdit.setText("000000");
			mColorfulTimeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
		}else{
			mColorValueEdit.setText("000000");
		}
		seekBarLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mSeekProgress = seekBar.getProgress();
				String epData = "";
				if(mSeekProgress == 0){
					epData= PREFIX_DATA_Temp+StringUtil.appendLeft(mSeekProgress+"", 3, '0'); 
				}else{
					epData= PREFIX_DATA_Light+StringUtil.appendLeft(mSeekProgress+"", 3, '0'); 
				}
				autoActionInfo.setEpData(epData);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				bightTextView.setText((int)(seekBar.getProgress()/255.0*100)+"%");
				mLightModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
				mTempModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mColorModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mColorfulModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mColorfulTimeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
			}
		});
		seekBarTemp.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mSeekProgress = seekBar.getProgress();
				String epData =  PREFIX_DATA_Temp+StringUtil.appendLeft(mSeekProgress+"", 3, '0');
				autoActionInfo.setEpData(epData);
			}
			
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				tempTextView.setText((int)(seekBar.getProgress()/255.0*100)+"%");
				mTempModeDialog.setBackgroundResource(R.drawable.device_led_adjust_select);
				mColorfulModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mColorModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mLightModeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
				mColorfulTimeDialog.setBackgroundResource(R.drawable.device_led_adjust_normal);
			}
		});
		
		holder.setContentView(contentView);
		return holder;
	}

	private String parseIntToHexStr(int i){
		String hexStr = "";
		String str = Integer.toHexString(i).toUpperCase();
		hexStr = StringUtil.appendLeft(str, 2, '0');
		return hexStr;
	}
}
