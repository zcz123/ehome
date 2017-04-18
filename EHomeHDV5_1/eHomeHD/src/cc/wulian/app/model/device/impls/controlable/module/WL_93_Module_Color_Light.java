package cc.wulian.app.model.device.impls.controlable.module;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.SceneList;
import cc.wulian.smarthomev5.tools.SceneList.OnSceneListItemClickListener;
import cc.wulian.smarthomev5.tools.SendMessage;

import com.yuantuo.customview.ui.ColorSquareView;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_93_Module }, category = Category.C_LIGHT)
public class WL_93_Module_Color_Light extends ControlableDeviceImpl {

	private static final String DATA_CTRL_STATE_OPEN_2255 = "2255";
	private static final String DATA_CTRL_STATE_CLOSE_2000 = "2000";
	
	protected Map<String, SceneInfo> bindScenesMap;
	protected Map<String, DeviceInfo> bindDevicesMap;
	
	private static final String PREFIX_DATA_PALETTE = "01";
	private static final String PREFIX_DATA_DIMMING = "02";
	private static final String PREFIX_DATA_DATE = "03";
	private static final String PREFIX_DATA_ADD_GROUP = "05";
	private static final String PREFIX_DATA_QUERY = "06";
	private static final String PREFIX_DATA_BACK_GROUND = "07";
	private static final String PREFIX_DATA_BACK_ALL = "08";
	private static final String PREFIX_DATA_AUTO = "09";

	private static final String PREFIX_DATA_MODE = "F";
	private static final String PREFIX_DATA_ISOPEN = "F00";
	private static final String PREFIX_DATA_LIGHT = "EB";
	private static final String PREFIX_DATA_SPEED = "ES";
	private static final String PREFIX_DATA_REFRESH = "8";

	private int luminabcePresent = 0;
	private boolean isAuto;
	private boolean isOpen;
	private boolean adjustLight;
	private boolean adjustSpeed;

	// private int luminanceValue = 0;// 亮度值
	// private int addGroupResult;// 加组结果
	// private int groupCount;// 组数
	// private int groupNumber;// 组号
	// private int backGroupResult;// 加组结果

	private ColorSquareView colorPickerView;
	public static int time = 0;
	private int rr = 0;// 色度
	private int gg = 0;// 饱和度
	private int bb = 0;// 亮度
	private String modeModule;
	private int changeMode;

	private View layoutView;
//	private ImageView mModuleScene;
//	private TextView mScenetext;
//	private LinearLayout mModuleBindScene;
	private LinearLayout mModuleBasicLayout;
//	private LinearLayout mModuleSceneLayout;
	private LinearLayout mModuleSeniorLayout;
	private RelativeLayout colorLayout;
	private ImageView openImageView;
	
	private LinearLayout contentLineLayout;
	
//	private LinearLayout mLightLayout;
	private LinearLayout mSpeedLayout;

	private Button mModuleBasic;
	private Button mModuleSenior;

	private boolean isModeSetting = true;
	
	private TextView mModeLeft;
	private TextView mModeRight;
	private TextView mLightAdjust;
	private TextView mSpeedAdjust;
//	private ImageView mLightImage;
	private ImageView mSpeedImage;

	private SeekBar mSeekBar;
	private TextView mSeekText;
	private Button mColorMode;
	private TextView mModeText;

	private CreatModuleInterface moduleLightStatue;
	private String currentMode;
	private ModuleManager modeManager = ModuleManager.getInstance();
	private TaskExecutor taskExecutor = TaskExecutor.getInstance();
	private static long HEAT_HEART_TIME = 2000;
	private boolean longClicked = false;
	private Runnable controlLightRunnable = new Runnable() {
		@Override
		public void run() {
//			createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,PREFIX_DATA_LIGHT, true);
			controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),PREFIX_DATA_LIGHT);
			longClicked = true;
		}
	};

	public WL_93_Module_Color_Light(Context context, String type) {
		super(context, type);
	}

	@Override
	public String getOpenSendCmd() {
		// TODO Auto-generated method stub
		return DATA_CTRL_STATE_OPEN_2255;
	}

	@Override
	public String getCloseSendCmd() {
		// TODO Auto-generated method stub
		return DATA_CTRL_STATE_CLOSE_2000;
	}

	@Override
	public String getOpenProtocol() {
		// TODO Auto-generated method stub
		return DATA_CTRL_STATE_OPEN_2255;
	}

	@Override
	public String getCloseProtocol() {
		// TODO Auto-generated method stub
		return DATA_CTRL_STATE_CLOSE_2000;
	}

	@Override
	public void onDeviceUp(DeviceInfo devInfo) {
		super.onDeviceUp(devInfo);
		SendMessage.sendGetBindSceneMsg(gwID, devID);
	}

	private OnTouchListener mOnTouchListener = new OnTouchListener() {
		
		@Override
		public boolean onTouch(View v, MotionEvent event) {
			if(event.getAction() == MotionEvent.ACTION_DOWN){
				Logger.debug("Listener onTouch  ACTION_DOWN");
			}else if(event.getAction() == MotionEvent.ACTION_UP){
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,PREFIX_DATA_ISOPEN, true);
				longClicked = false;
				Logger.debug("Listener onTouch  ACTION_UP");
			}else if(event.getAction() == MotionEvent.ACTION_MOVE){
				Logger.debug("Listener onTouch  ACTION_MOVE");
			}
			return true;
		}
	};
	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == mModuleBasic) {
				mModuleBasic
						.setBackgroundResource(R.drawable.device_one_wried_wireless_input_btn_pressed);
				mModuleSenior
						.setBackgroundResource(R.drawable.device_one_wried_wireless_output_btn_normal);
				mModuleBasicLayout.setVisibility(View.VISIBLE);
				mModuleSeniorLayout.setVisibility(View.GONE);
				mColorMode.setVisibility(View.GONE);
				mModeText.setVisibility(View.VISIBLE);
				isModeSetting = true;
			} else if (v == mModuleSenior) {
				mModuleBasic
						.setBackgroundResource(R.drawable.device_one_wried_wireless_input_btn_normal);
				mModuleSenior
						.setBackgroundResource(R.drawable.device_one_wried_wireless_output_btn_pressed);
				mModuleBasicLayout.setVisibility(View.GONE);
				mModuleSeniorLayout.setVisibility(View.VISIBLE);
				mColorMode.setVisibility(View.VISIBLE);
				mModeText.setVisibility(View.GONE);
				isModeSetting = false;
			}
			//之前的场景绑定
//			else if(v == mModuleScene){
//				if(null!= bindScenesMap.get(ep)){
//                    sendCmd(ep);
//				}else{
//					showToast();
//				}
//			} 
			else if (v == mModeLeft) {
				modeModule = moduleLightStatue.getModuleMode();
				int mode = StringUtil.toInteger(modeModule);
				if(!(mode >= 1 && mode <= 4)){
					changeMode = 1;
				}else if (StringUtil.isNullOrEmpty(modeModule) || "04".equals(modeModule)) {
					changeMode = 1;
				} else {
					changeMode = StringUtil.toInteger(modeModule) + 1;
				}
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_MODE + StringUtil.appendLeft(changeMode + "", 2, '0'), true);
			} else if (v == mModeRight) {
				modeModule = moduleLightStatue.getModuleMode();
				int mode = StringUtil.toInteger(modeModule);
				if(!(mode >= 5 && mode <= 8)){
					changeMode = 5;
				}else if (StringUtil.isNullOrEmpty(modeModule) || "08".equals(modeModule)) {
					changeMode = 5;
				} else {
					changeMode = StringUtil.toInteger(modeModule) + 1;
				}
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, PREFIX_DATA_MODE + StringUtil.appendLeft(changeMode + "", 2, '0'), true);
			} else if (v == mSpeedLayout) {
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,PREFIX_DATA_SPEED, true);
			}
//			else if(v == openImageView){
//				Logger.debug("Listener onClick  onClick");
//				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,PREFIX_DATA_ISOPEN, true);
//			}else if (v == mLightLayout) {
//			createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,PREFIX_DATA_LIGHT, true);
//		}
		}

//		private void showToast() {
//			WLToast.showToast(mContext, mContext.getResources().getString(R.string.device_module_no_bind),WLToast.TOAST_SHORT);
//		}
//
//		private void sendCmd(String ep) {
//			SendMessage.sendSetSceneMsg(mContext, bindScenesMap.get(ep).getGwID(),
//					CmdUtil.MODE_SWITCH, bindScenesMap.get(ep).getSceneID(), null, null,"2", true);
//		}

	};

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		getBindScenesMap();
		layoutView = inflater.inflate(R.layout.device_with_light_module,
				container, false);
		colorLayout = (RelativeLayout) layoutView
				.findViewById(R.id.dev_state_colorlayout0);
		return layoutView;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		
//		createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,PREFIX_DATA_REFRESH, false);
		controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),PREFIX_DATA_REFRESH);
		
		mModuleBasic = (Button) view.findViewById(R.id.device_light_module_basic);
		mModuleSenior = (Button) view.findViewById(R.id.device_light_module_senior);
		mModuleBasic.setOnClickListener(mOnClickListener);
		mModuleSenior.setOnClickListener(mOnClickListener);

//		mModuleSceneLayout = (LinearLayout) view.findViewById(R.id.device_light_module_scene);
//		mModuleScene = (ImageView) view.findViewById(R.id.device_module_scene_bg);
//		mModuleScene.setOnClickListener(mOnClickListener);
//		mScenetext = (TextView) view.findViewById(R.id.device_module_scene_text);
		mModuleBasicLayout = (LinearLayout) view.findViewById(R.id.device_module_basic_layout);
		mModuleSeniorLayout = (LinearLayout) view.findViewById(R.id.device_module_senior_layout);

		openImageView = (ImageView) view.findViewById(R.id.device_light_module_open);
		
//		mLightLayout = (LinearLayout) view.findViewById(R.id.device_mode_light_bg);
		mSpeedLayout = (LinearLayout) view.findViewById(R.id.device_mode_speed_bg);

		mModeLeft = (TextView) view.findViewById(R.id.device_module_mode_left);
		mModeRight = (TextView) view.findViewById(R.id.device_module_mode_right);
		mLightAdjust = (TextView) view.findViewById(R.id.device_module_light_adjust);
		mSpeedAdjust = (TextView) view.findViewById(R.id.device_module_speed_adjust);
//		mLightImage = (ImageView) view.findViewById(R.id.device_module_light_img);
		mSpeedImage = (ImageView) view.findViewById(R.id.device_module_speed_img);
		
		mModeLeft.setOnClickListener(mOnClickListener);
		mModeRight.setOnClickListener(mOnClickListener);

		mSeekBar = (SeekBar) view.findViewById(R.id.device_module_seekbar);
		mSeekText = (TextView) view.findViewById(R.id.device_module_textview);
		mModeText = (TextView) view.findViewById(R.id.device_mode_module_text);

		mColorMode = (Button) view.findViewById(R.id.dev_state_imageview_0);
		mColorMode.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (modeManager.getOtherMode().isAuto()) {
					mColorMode.setBackgroundResource(R.drawable.device_light_led_bright_mode1);
					modeManager.getOtherMode().setAuto(false);
					createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,"90", true);
				} else {
					mColorMode.setBackgroundResource(R.drawable.device_light_led_bright_mode);
					modeManager.getOtherMode().setAuto(true);
					createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,"91", true);
				}
			}
		});
		addColorView();
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		if(moduleLightStatue.isAdjustLight()){
//			mLightLayout.setBackgroundResource(R.drawable.device_light_module_bg0);
			mLightAdjust.setTextColor(mContext.getResources().getColor(R.color.holo_green_light));
//			mLightImage.setBackgroundResource(R.drawable.device_module_light_adjust);
//			mLightLayout.setEnabled(true);
//			mLightLayout.setOnClickListener(mOnClickListener);
		}else {
//			mLightLayout.setEnabled(false);
			mLightAdjust.setTextColor(mContext.getResources().getColor(R.color.black));
//			mLightImage.setBackgroundResource(R.drawable.device_module_light_adjust0);
		}
		if(moduleLightStatue.isAdjustSpeed()){
			mSpeedLayout.setBackgroundResource(R.drawable.device_light_module_bg0);
			mSpeedAdjust.setTextColor(mContext.getResources().getColor(R.color.holo_green_light));
			mSpeedImage.setBackgroundResource(R.drawable.device_module_speed_adjust);
			mSpeedLayout.setEnabled(true);
			mSpeedLayout.setOnClickListener(mOnClickListener);
		}else{
			mSpeedLayout.setEnabled(false);
			mSpeedAdjust.setTextColor(mContext.getResources().getColor(R.color.black));
			mSpeedImage.setBackgroundResource(R.drawable.device_module_speed_adjust0);
		}
		
		if(moduleLightStatue.isOpen()){
			openImageView.setBackgroundResource(R.drawable.device_light_module_open);
			if(StringUtil.equals(moduleLightStatue.getModuleMode(), "09")){
				mModeLeft.setTextColor(mContext.getResources().getColor(R.color.black));
				mModeRight.setTextColor(mContext.getResources().getColor(R.color.black));
				openImageView.setOnTouchListener(mOnTouchListener);
			}else {
				if(StringUtil.toInteger(moduleLightStatue.getModuleMode()) >= 1 && StringUtil.toInteger(moduleLightStatue.getModuleMode()) <= 4){
					mModeLeft.setTextColor(mContext.getResources().getColor(R.color.holo_green_light));
					mModeRight.setTextColor(mContext.getResources().getColor(R.color.black));
				}else{
					mModeLeft.setTextColor(mContext.getResources().getColor(R.color.black));
					mModeRight.setTextColor(mContext.getResources().getColor(R.color.holo_green_light));
				}
				openImageView.setOnTouchListener(new OnTouchListener() {
					
					@Override
					public boolean onTouch(View v, MotionEvent event) {
						if(event.getAction() == MotionEvent.ACTION_DOWN){
							taskExecutor.addScheduled(controlLightRunnable, HEAT_HEART_TIME, 1000, TimeUnit.MILLISECONDS);
							Logger.debug("Listener onTouch  ACTION_DOWN");
						}else if(event.getAction() == MotionEvent.ACTION_UP){
							if(!longClicked){
								createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,PREFIX_DATA_ISOPEN, true);
							}
							taskExecutor.removeScheduled(controlLightRunnable);
							longClicked = false;
							Logger.debug("Listener onTouch  ACTION_UP");
						}else if(event.getAction() == MotionEvent.ACTION_MOVE){
							Logger.debug("Listener onTouch  ACTION_MOVE");
						}
						return true;
					}
				});
			}
//			openImageView.setOnClickListener(mOnClickListener);
//			openImageView.setOnLongClickListener(new OnLongClickListener() {
//				
//				@Override
//				public boolean onLongClick(View v) {
//					longClicked = true;
//					Logger.debug("Listener onLong  onLongClick");
//					return true;
//				}
//			});
			
		}else{
			mModeLeft.setTextColor(mContext.getResources().getColor(R.color.black));
			mModeRight.setTextColor(mContext.getResources().getColor(R.color.black));
			openImageView.setBackgroundResource(R.drawable.device_light_module_close);
			mLightAdjust.setTextColor(mContext.getResources().getColor(R.color.black));
//			mLightImage.setBackgroundResource(R.drawable.device_module_light_adjust0);
			mSpeedAdjust.setTextColor(mContext.getResources().getColor(R.color.black));
			mSpeedImage.setBackgroundResource(R.drawable.device_module_speed_adjust0);
//			mLightLayout.setEnabled(false);
			mSpeedLayout.setEnabled(false);
//			openImageView.setOnClickListener(mOnClickListener);
			openImageView.setOnTouchListener(mOnTouchListener);
		}
		if(isModeSetting){
			mModuleBasic.setBackgroundResource(R.drawable.device_one_wried_wireless_input_btn_pressed);
			mModuleSenior.setBackgroundResource(R.drawable.device_one_wried_wireless_output_btn_normal);
			mModuleBasicLayout.setVisibility(View.VISIBLE);
			mModuleSeniorLayout.setVisibility(View.GONE);
			mColorMode.setVisibility(View.GONE);
			mModeText.setVisibility(View.VISIBLE);
		}else{
			mModuleBasic.setBackgroundResource(R.drawable.device_one_wried_wireless_input_btn_normal);
			mModuleSenior.setBackgroundResource(R.drawable.device_one_wried_wireless_output_btn_pressed);
			mModuleBasicLayout.setVisibility(View.GONE);
			mModuleSeniorLayout.setVisibility(View.VISIBLE);
			mColorMode.setVisibility(View.VISIBLE);
			mModeText.setVisibility(View.GONE);
		}
		
		if("09".equals(moduleLightStatue.getModuleMode()) && moduleLightStatue.isOpen() == true && modeManager.getOtherMode().isAuto() == false){
			mSeekBar.setEnabled(true);
			mSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

				@Override
				public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
					mSeekText.setText(progress + "%");
				}

				@Override
				public void onStartTrackingTouch(SeekBar seekBar) {

				}

				@Override
				public void onStopTrackingTouch(SeekBar seekBar) {
					int mSeekProgress = seekBar.getProgress();
					if(mSeekProgress < 2){
						mSeekProgress = 0;
					}else if(mSeekProgress > 98){
						mSeekProgress = 100;
					}else {
						mSeekProgress = mSeekProgress + 1;
					}
					int mAdjustLight = mSeekProgress * 255 / 100;
					createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,2 + StringUtil.appendLeft(mAdjustLight + "", 3, '0'),true);
				}

			});
		}else{
			mSeekBar.setEnabled(false);
		}
		
		mSeekBar.setProgress(StringUtil.toInteger(luminabcePresent));
		mSeekText.setText(luminabcePresent + "%");
		mModeText.setText(String.format(mResources.getString(R.string.device_current_mode), StringUtil.toInteger(moduleLightStatue.getModuleMode())));
		colorPickerView.setColor(Color.rgb(rr, gg, bb));
		mColorMode.setBackgroundResource(modeManager.getOtherMode().isAuto() ? R.drawable.device_light_led_bright_mode1
						: R.drawable.device_light_led_bright_mode);
		//场景绑定功能
//		if(null!=bindScenesMap.get(EP_14)){
//			mModuleScene.setEnabled(true);
//			mScenetext.setText(bindScenesMap.get(EP_14).getName());
//			mScenetext.setTextColor(mContext.getResources().getColor(R.color.holo_green_light));
//			mModuleScene.setImageDrawable(
//					SceneManager.getSceneIconDrawable_Light_Small(mContext, bindScenesMap.get(EP_14).getIcon()));
//			
//		}else{
//			mModuleScene.setImageDrawable(mContext.getResources().getDrawable(R.drawable.device_light_module_scene));
//			mScenetext.setText(mResources.getString(R.string.device_no_bind));
//			mModuleScene.setEnabled(false);
//		}
	}
	
	private void addColorView() {
		WindowManager wm = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);

		int width = (int) (wm.getDefaultDisplay().getWidth() / 2);
		int height = (int) (wm.getDefaultDisplay().getHeight() / 2);
		if (width > height)
			width = height;
//		colorPickerView = new ColorSquareView(mContext, width, width,Color.rgb(rr, gg, bb),new ColorSquareView.OnColorChangedListener() {
//					@Override
//					public void colorChanged(String color) {
//
//						if (color == null || color.length() < 8)
//							return;
//
//						int r = StringUtil.toInteger((color).substring(2, 4),16);
//						int g = StringUtil.toInteger((color).substring(4, 6),16);
//						int b = StringUtil.toInteger((color).substring(6, 8),16);
//						int max = r;
//						if (r < g) {
//							max = g;
//						}
//						if (g < b) {
//							max = b;
//						}
//						String data = "1"
//								+ StringUtil.appendLeft(r + "", 3, '0')
//								+ StringUtil.appendLeft(g + "", 3, '0')
//								+ StringUtil.appendLeft(b + "", 3, '0')
//								+ StringUtil.appendLeft(max + "", 3, '0');
//						createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,data, true);
//					}
//				});

		colorPickerView= new ColorSquareView(mContext, width, width,Color.rgb(rr, gg, bb),new ColorSquareView.OnColorChangedListenerD() {

			@Override
			public void onColorChanged(int color, String hexStrColor) {


				if (hexStrColor == null || hexStrColor.length() < 6) 
					return;
				
				int r = StringUtil.toInteger((hexStrColor).substring(0, 2),16);
				int g = StringUtil.toInteger((hexStrColor).substring(2, 4),16);
				int b = StringUtil.toInteger((hexStrColor).substring(4, 6),16);
				int max = r; 
				if (max < g) {
					max = g;
				}
				if (max < b) {
					max = b;
				}
				
				String data = "1"
						+ StringUtil.appendLeft(r + "", 3, '0')
						+ StringUtil.appendLeft(g + "", 3, '0')
						+ StringUtil.appendLeft(b + "", 3, '0')
						+ StringUtil.appendLeft(max + "", 3, '0');
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL,data, true);
			
			
			}
			
			
		});
		
		RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.MATCH_PARENT,
				RelativeLayout.LayoutParams.MATCH_PARENT);
		params.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
		colorPickerView.setLayoutParams(params);
		colorLayout.addView(colorPickerView);

	}
	@Override
	public void refreshDevice() {
		// TODO Auto-generated method stub
		super.refreshDevice();
		disassembleCompoundCmd(epData);
	}
	private void disassembleCompoundCmd(String epData) {
		if (isNull(epData))
			return;
		
		if (epData.startsWith("45") && epData.length() >= 10) {
			if ("53".equals(epData.substring(2, 4))) {
				currentMode = StringUtil.appendLeft(StringUtil.toInteger(epData.substring(4, 6), 16) + "",2, '0');
				int lightValue = StringUtil.toInteger(epData.substring(6, 8),16);
				int speedValue = StringUtil.toInteger(epData.substring(8, 10),16);
				moduleLightStatue = modeManager.getModuleMode(currentMode);
				moduleLightStatue.setLightValues(lightValue + "");
				moduleLightStatue.setSpeedValues(speedValue + "");
			}
			luminabcePresent = 0;
			modeManager.getOtherMode().setAuto(false);
		} else if (epData.startsWith("45") && epData.length() >= 8) {
			if ("42".equals(epData.substring(2, 4))) {
				currentMode = StringUtil.appendLeft(StringUtil.toInteger(epData.substring(4, 6), 16) + "",2, '0');
				int lightValue = StringUtil.toInteger(epData.substring(6, 8),16);
				moduleLightStatue = modeManager.getModuleMode(currentMode);
				moduleLightStatue.setModuleMode(currentMode);
				moduleLightStatue.setLightValues(lightValue + "");
			}
			luminabcePresent = 0;
			modeManager.getOtherMode().setAuto(false);
		} else if (epData.startsWith("46") && epData.length() >= 6) {
			currentMode = StringUtil.appendLeft(StringUtil.toInteger(epData.substring(2, 4), 16) + "", 2,'0');
			int isopen = StringUtil.toInteger(epData.substring(4, 6), 16);
			moduleLightStatue = modeManager.getModuleMode(currentMode);
			moduleLightStatue.setModuleMode(currentMode);
			moduleLightStatue.open(isopen + "");
			luminabcePresent = 0;
			modeManager.getOtherMode().setAuto(false);
		} else if (epData.startsWith(PREFIX_DATA_PALETTE)
				&& epData.length() >= 10) {
			currentMode = "09";
			moduleLightStatue = modeManager.getModuleMode(currentMode);
			moduleLightStatue.setModuleMode(currentMode);
			moduleLightStatue.setRr(StringUtil.appendLeft(StringUtil.toInteger(epData.substring(2, 4), 16) + "", 2,'0'));
			moduleLightStatue.setGg(StringUtil.appendLeft(StringUtil.toInteger(epData.substring(4, 6), 16) + "", 2,'0'));
			moduleLightStatue.setBb(StringUtil.appendLeft(StringUtil.toInteger(epData.substring(6, 8), 16) + "", 2,'0'));
			rr = StringUtil.toInteger(moduleLightStatue.getRr());
			gg = StringUtil.toInteger(moduleLightStatue.getGg());
			bb = StringUtil.toInteger(moduleLightStatue.getBb());
			int lightValue = StringUtil.toInteger(epData.substring(8, 10), 16);
			moduleLightStatue.setLightValues(lightValue + "");
			moduleLightStatue.open(01 + "");
			luminabcePresent = (int) (StringUtil.toInteger(moduleLightStatue.getLightValues()) * 100 / 255.0);
			modeManager.getOtherMode().setAuto(false);
		} else if (epData.startsWith(PREFIX_DATA_DIMMING)
				&& epData.length() >= 4) {
			currentMode = "09";
			moduleLightStatue = modeManager.getModuleMode(currentMode);
			moduleLightStatue.setModuleMode(currentMode);
			int lightValue = StringUtil.toInteger(epData.substring(2, 4), 16);
			moduleLightStatue.setLightValues(lightValue + "");
			luminabcePresent = (int) (StringUtil.toInteger(moduleLightStatue.getLightValues()) * 100 / 255.0);
			modeManager.getOtherMode().setAuto(false);
		}
		// else if (epData.startsWith(PREFIX_DATA_ADD_GROUP) && epData.length()
		// >= 4) {
		// device.addGroupResult = StringUtil.toInteger(epData.substring(2, 4),
		// 16);
		// }
		// else if (epData.startsWith(PREFIX_DATA_QUERY) && epData.length() >=
		// 6) {
		// device.groupCount = StringUtil.toInteger(epData.substring(2, 4), 16);
		// device.groupNumber = StringUtil.toInteger(epData.substring(4, 6),
		// 16);
		// }
		// else if (epData.startsWith(PREFIX_DATA_BACK_GROUND) &&
		// epData.length() >= 4) {
		// device.backGroupResult = StringUtil.toInteger(epData.substring(2, 4),
		// 16);
		// }
		else if (epData.startsWith(PREFIX_DATA_AUTO) && epData.length() >= 4) {
			currentMode = "09";
			moduleLightStatue = modeManager.getModuleMode(currentMode);
			moduleLightStatue.setModuleMode(currentMode);
			if (StringUtil.toInteger(epData.substring(2, 4), 16) == 1) {
				modeManager.getOtherMode().setAuto(true);
				mSeekBar.setEnabled(false);
			}else if(StringUtil.toInteger(epData.substring(2, 4), 16) == 0){
				modeManager.getOtherMode().setAuto(false);
				mSeekBar.setEnabled(true);
			}
			moduleLightStatue.open(01 + "");
		}else if (epData.startsWith(PREFIX_DATA_BACK_ALL) && epData.length() >= 16) {
			currentMode = StringUtil.appendLeft(StringUtil.toInteger(epData.substring(2, 4), 16) + "", 2,'0');
			moduleLightStatue = modeManager.getModuleMode(currentMode);
			moduleLightStatue.setModuleMode(currentMode);
			moduleLightStatue.setRr(StringUtil.appendLeft(StringUtil.toInteger(epData.substring(4, 6), 16) + "", 2,'0'));
			moduleLightStatue.setGg(StringUtil.appendLeft(StringUtil.toInteger(epData.substring(6, 8), 16) + "", 2,'0'));
			moduleLightStatue.setBb(StringUtil.appendLeft(StringUtil.toInteger(epData.substring(8, 10), 16) + "", 2,'0'));
			rr = StringUtil.toInteger(moduleLightStatue.getRr());
			gg = StringUtil.toInteger(moduleLightStatue.getGg());
			bb = StringUtil.toInteger(moduleLightStatue.getBb());
			int lightValue = StringUtil.toInteger(epData.substring(10, 12), 16);
			int isopen = StringUtil.toInteger(epData.substring(14, 16), 16);
			moduleLightStatue.open(isopen + "");
			moduleLightStatue.setLightValues(lightValue + "");
			if (StringUtil.toInteger(epData.substring(12, 14), 16) == 2) {
				modeManager.getOtherMode().setAuto(true);
			}
			luminabcePresent = (int) (StringUtil.toInteger(moduleLightStatue.getLightValues()) * 100 / 255.0);
		}else if (epData.startsWith(PREFIX_DATA_DATE) && epData.length() >= 6) {
			currentMode = "09";
			moduleLightStatue = modeManager.getModuleMode(currentMode);
			moduleLightStatue.setModuleMode(currentMode);
			time = StringUtil.toInteger(epData.substring(2, 6), 16);
		}else{
			currentMode = "09";
			moduleLightStatue = modeManager.getModuleMode(currentMode);
			moduleLightStatue.setModuleMode(currentMode);
		}

	}

	
	@Override
	public boolean isAutoControl(boolean isNormal) {
		// TODO Auto-generated method stub
		return false;
	}

//	@Override
//	public Intent getSettingIntent() {
//		Intent intent = new Intent(mContext,DeviceSettingActivity.class);
//		if(isModeSetting){
//			intent.putExtra(ModuleLightSettingFragment.GWID, gwID);
//			intent.putExtra(ModuleLightSettingFragment.DEVICEID, devID);
//			intent.putExtra(ModuleLightSettingFragment.MODULE_LIGHT_COLOR, type);
//			intent.putExtra(AbstractDevice.SETTING_LINK_TYPE, AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
//			intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, TouchDeviceEditFragment.class.getName());
////			intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, ModuleLightSettingFragment.class.getName());
//		}else{
//			intent.putExtra(settingModuleTimeFragment.GWID, gwID);
//			intent.putExtra(settingModuleTimeFragment.DEVICEID, devID);
//			intent.putExtra(AbstractDevice.SETTING_LINK_TYPE,
//					AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
//			intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, settingModuleTimeFragment.class.getName());
//		}
//		return intent ;
//	}

	@Override
	public View onCreateSettingView(LayoutInflater inflater, ViewGroup container) {
		View view = inflater.inflate(R.layout.device_touch_bind_scene, null);
		contentLineLayout = (LinearLayout) view
				.findViewById(R.id.touch_bind_content_ll);
		getBindScenesMap();
		
		LinearLayout itemView = (LinearLayout) inflater.inflate(
				R.layout.device_module_light_setting, null);
		final TextView sceneNameTextView = (TextView) itemView
				.findViewById(R.id.device_module_bind_scene);
		String sceneName = getResources().getString(
				R.string.device_no_bind);
		if (bindScenesMap.containsKey(ep)) {
			SceneInfo sceneInfo = bindScenesMap.get(ep);
			if(sceneInfo != null){
				sceneName = sceneInfo.getName();
			}
		}
		sceneNameTextView.setText(sceneName);
		sceneNameTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final SceneList sceneList = new SceneList(mContext, true);
				sceneList
						.setOnSceneListItemClickListener(new OnSceneListItemClickListener() {

							@Override
							public void onSceneListItemClicked(
									SceneList list, int pos, SceneInfo info) {
								sceneNameTextView.setText(info.getName());
								bindScenesMap.put(ep, info);
								JsonTool.uploadBindList(mContext,
										bindScenesMap, bindDevicesMap,
										gwID, devID, type);
								sceneList.dismiss();
							}
						});
				sceneList.show(v);
			}
		});
		contentLineLayout.addView(itemView);
		return view;
	}

	private void getBindScenesMap() {
		bindScenesMap = MainApplication.getApplication().bindSceneInfoMap
				.get(getDeviceGwID() + getDeviceID());
		if (bindScenesMap == null) {
			bindScenesMap = new HashMap<String, SceneInfo>();
		}
	}
	
	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item,LayoutInflater inflater){
		return getDefaultShortCutControlView(item,inflater);
	}
	
}
