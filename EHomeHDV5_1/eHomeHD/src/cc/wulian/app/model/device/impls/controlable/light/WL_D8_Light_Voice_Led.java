package cc.wulian.app.model.device.impls.controlable.light;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.activity.MiniGatewayVoiceChooseActivity;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.adapter.MiniGatewayVoiceChooseAdapter;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.SizeUtil;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_MINI_LIGHT }, category = Category.C_LIGHT)
public class WL_D8_Light_Voice_Led extends ControlableDeviceImpl {
	private static final String DATA_CTRL_MINI_LIGHT = "1";
	private static final String DATA_CTRL_MINI_VOICE = "3";

	private Button lightButton;
	private Button voiceButton;
	private LinearLayout lightLayout, voiceLayout;

	private String colorLight = "00";
	private String colorMode = "00";
	private String voiceMode = "01";
	private String voiceSize = "50";

	private final static String MINI_GATEWAY_LIGHT_MODE_OFF = "00";
	private final static String MINI_GATEWAY_LIGHT_MODE_OFTEN_LIGHT = "01";
	private final static String MINI_GATEWAY_LIGHT_MODE_STREAM = "02";
	private final static String MINI_GATEWAY_LIGHT_MODE_FAST_FLASH = "03";
	private final static String MINI_GATEWAY_LIGHT_MODE_SLOW_FLASH = "04";

	private final static String MINI_GATEWAY_LIGHT_RED = "01";
	private final static String MINI_GATEWAY_LIGHT_GREEN = "02";
	private final static String MINI_GATEWAY_LIGHT_YELLOW = "03";
	private final static String MINI_GATEWAY_LIGHT_BLUE = "04";
	private final static String MINI_GATEWAY_LIGHT_PURPLE = "05";
	private final static String MINI_GATEWAY_LIGHT_CYAN = "06";
	private final static String MINI_GATEWAY_LIGHT_WHITE = "07";


	@ViewInject(R.id.color_red_imagview)
	private ImageView lightRedImageView;
	@ViewInject(R.id.color_green_imagview)
	private ImageView lightGreenImageView;
	@ViewInject(R.id.color_yellow_imagview)
	private ImageView lightYellowImageView;
	@ViewInject(R.id.color_blue_imagview)
	private ImageView lightBlueImageView;
	@ViewInject(R.id.color_purple_imagview)
	private ImageView lightPurpleImageView;
	@ViewInject(R.id.color_cyan_imagview)
	private ImageView lightCyanImageView;
	@ViewInject(R.id.color_white_imagview)
	private ImageView lightWhiteImageView;

	@ViewInject(R.id.color_red_tv)
	private TextView lightRedTextView;
	@ViewInject(R.id.color_green_tv)
	private TextView lightGreenTextView;
	@ViewInject(R.id.color_yellow_tv)
	private TextView lightYellowTextView;
	@ViewInject(R.id.color_blue_tv)
	private TextView lightBlueTextView;
	@ViewInject(R.id.color_purple_tv)
	private TextView lightPurpleTextView;
	@ViewInject(R.id.color_cyan_tv)
	private TextView lightCyanTextView;
	@ViewInject(R.id.color_white_tv)
	private TextView lightWhiteTextView;

	@ViewInject(R.id.mini_geteway_light_mode_often_light)
	private ImageView lightModeOftenLight;
	@ViewInject(R.id.mini_geteway_light_mode_stream)
	private ImageView lightModeStream;
	@ViewInject(R.id.mini_geteway_light_mode_fast_flash)
	private ImageView lightModeFastFlash;
	@ViewInject(R.id.mini_geteway_light_mode_slow_flash)
	private ImageView lightModeSlowFlash;
	@ViewInject(R.id.mini_geteway_light_mode_off)
	private ImageView lightModeOff;

	@ViewInject(R.id.mini_gateway_voice_item)
	private RelativeLayout miniGatewayVoiceItemRelativeLayout;
	@ViewInject(R.id.voice_list_choose_name)
	private TextView chooce_voice_TV;

	private SeekBar voiceSeekbar;

	private RelativeLayout lightRelativeLayout;
	private RelativeLayout voiceRelativeLayout;
	private TextView voiceTextView;
	private TextView lightTextView;

	private WLDialog dialog;
	private List<String> voiceTypeList;
	private MiniGatewayVoiceChooseAdapter voiceChooseAdapter;
	private ListView voiceChooseListView;
	public String voise_position;
	private  String [] musicArrary={"",getResources().getString(R.string.miniGW_DeviceVoice_Dingdong),getResources().getString(R.string.miniGW_DeviceVoice_jingle),
			getResources().getString(R.string.miniGW_DeviceVoice_crisp),getResources().getString(R.string.miniGW_DeviceVoice_Long_tone),
			getResources().getString(R.string.miniGW_DeviceVoice_fluctuation),getResources().getString(R.string.miniGW_DeviceVoice_Cuckoo),
			getResources().getString(R.string.miniGW_DeviceVoice_Didi),getResources().getString(R.string.miniGW_DeviceVoice_fierce),
			getResources().getString(R.string.miniGW_DeviceVoice_rapid),getResources().getString(R.string.miniGW_DeviceVoice_sharp),
			getResources().getString(R.string.miniGW_DeviceVoice_police)};

	public WL_D8_Light_Voice_Led(Context context, String type) {
		super(context, type);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		View view = inflater.inflate(R.layout.device_mini_voice_light,
				container, false);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		lightButton = (Button) view
				.findViewById(R.id.device_mini_geteway_led_light_button);
		voiceButton = (Button) view
				.findViewById(R.id.device_mini_geteway_led_voice_button);
		lightLayout = (LinearLayout) view
				.findViewById(R.id.device_mini_geteway_led_light_layout);
		voiceLayout = (LinearLayout) view
				.findViewById(R.id.device_mini_geteway_led_voice_layout);
		voiceSeekbar = (SeekBar) view
				.findViewById(R.id.device_mini_geteway_led_voice_seekbar);
		lightButton.setOnClickListener(mOnClickListener);
		voiceButton.setOnClickListener(mOnClickListener);

		lightRedImageView.setOnClickListener(mOnClickListener);
		lightGreenImageView.setOnClickListener(mOnClickListener);
		lightYellowImageView.setOnClickListener(mOnClickListener);
		lightBlueImageView.setOnClickListener(mOnClickListener);
		lightPurpleImageView.setOnClickListener(mOnClickListener);
		lightCyanImageView.setOnClickListener(mOnClickListener);
		lightWhiteImageView.setOnClickListener(mOnClickListener);

		lightModeOftenLight.setOnClickListener(mOnClickListener);
		lightModeStream.setOnClickListener(mOnClickListener);
		lightModeFastFlash.setOnClickListener(mOnClickListener);
		lightModeSlowFlash.setOnClickListener(mOnClickListener);
		lightModeOff.setOnClickListener(mOnClickListener);

		miniGatewayVoiceItemRelativeLayout.setOnClickListener(mOnClickListener);
		// 得到存入本地的所选声音的名称
		String chooce_voice_name = Preference.getPreferences()
				.getVoiceChooseName();
		chooce_voice_TV.setText(chooce_voice_name);
		//得到存入本地的声音大小
		voiceSize = Preference.getPreferences().getVoiceChooseSize()
				+ "";
		voiceSeekbar.setProgress(Integer.parseInt(voiceSize));
		voiceSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				int voiceNumber = arg0.getProgress();
				if (voiceNumber < 10) {
					voiceSize = "0" + voiceNumber;
				} else {
					voiceSize = voiceNumber + "";
				}
				//存入声音大小
				Preference.getPreferences().saveVoiceChooseSize(Integer.parseInt(voiceSize));
				// 得到存入本地的所选声音的位置
				voiceMode = Preference.getPreferences().getVoiceChooseNum()
						+ "";
				int voicemode = Integer.parseInt(voiceMode);
				if (voicemode < 10) {
					voiceMode = "0" + voiceMode;
				} else {
					voiceMode = voiceMode + "";
				}
				controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo()
						.getEpType(), DATA_CTRL_MINI_VOICE + voiceMode
						+ voiceSize);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

			}

		});

	}

	@Override
	public void onResume() {
		super.onResume();
		getMiniLight();
		getMiniVoice();
	}

	private void getMiniVoice(){
		controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo()
				.getEpType(), "4" + "xx"
				+ "xx");
	}
	private void getMiniLight(){
		controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo()
				.getEpType(), "a" + "xx"
				+ "xx");
	}

	// 设备的点击事件
	private OnClickListener mOnClickListener = new OnClickListener() {

		public void onClick(View v) {
			if (v == voiceButton) {
				getMiniVoice();
				lightButton
						.setBackgroundResource(R.drawable.device_one_wried_wireless_input_btn_normal);
				voiceButton
						.setBackgroundResource(R.drawable.device_one_wried_wireless_output_btn_pressed);
				voiceLayout.setVisibility(View.VISIBLE);
				lightLayout.setVisibility(View.GONE);
			} else if (v == lightButton) {
				lightButton
						.setBackgroundResource(R.drawable.device_one_wried_wireless_input_btn_pressed);
				voiceButton
						.setBackgroundResource(R.drawable.device_one_wried_wireless_output_btn_normal);
				voiceLayout.setVisibility(View.GONE);
				lightLayout.setVisibility(View.VISIBLE);
			} else if (v == lightRedImageView) {
				colorLight = MINI_GATEWAY_LIGHT_RED;
				changeImageViewStatus(lightRedImageView);
				changeTextViewStatus(lightRedTextView);
			}else if(v == lightPurpleImageView){
				colorLight = MINI_GATEWAY_LIGHT_PURPLE;
				changeImageViewStatus(lightPurpleImageView);
				changeTextViewStatus(lightPurpleTextView);
			}else if(v == lightBlueImageView){
				colorLight = MINI_GATEWAY_LIGHT_BLUE;
				changeImageViewStatus(lightBlueImageView);
				changeTextViewStatus(lightBlueTextView);
			}else if(v == lightCyanImageView){
				colorLight = MINI_GATEWAY_LIGHT_CYAN;
				changeImageViewStatus(lightCyanImageView);
				changeTextViewStatus(lightCyanTextView);
			}else if(v ==lightYellowImageView){
				colorLight = MINI_GATEWAY_LIGHT_YELLOW;
				changeImageViewStatus(lightYellowImageView);
				changeTextViewStatus(lightYellowTextView);
			}else if(v == lightGreenImageView){
				colorLight = MINI_GATEWAY_LIGHT_GREEN;
				changeImageViewStatus(lightGreenImageView);
				changeTextViewStatus(lightGreenTextView);
			}else if(v == lightWhiteImageView){
				colorLight = MINI_GATEWAY_LIGHT_WHITE;
				changeImageViewStatus(lightWhiteImageView);
				changeTextViewStatus(lightWhiteTextView);
			} else if (v == lightModeOftenLight) {
				colorMode = MINI_GATEWAY_LIGHT_MODE_OFTEN_LIGHT;
			} else if (v == lightModeStream) {
				colorMode = MINI_GATEWAY_LIGHT_MODE_STREAM;
			} else if (v == lightModeFastFlash) {
				colorMode = MINI_GATEWAY_LIGHT_MODE_FAST_FLASH;
			} else if (v == lightModeSlowFlash) {
				colorMode = MINI_GATEWAY_LIGHT_MODE_SLOW_FLASH;
			} else if (v == lightModeOff) {
				colorMode = MINI_GATEWAY_LIGHT_MODE_OFF;
			} else if (v == miniGatewayVoiceItemRelativeLayout) {
				Intent intent = new Intent();
				intent.putExtra(MiniGatewayVoiceChooseActivity.DEVICE_EP,
						getCurrentEpInfo().getEp());
				intent.putExtra(MiniGatewayVoiceChooseActivity.DEVICE_EPTYPE,
						getCurrentEpInfo().getEpType());
				intent.putExtra(MiniGatewayVoiceChooseActivity.VOICE_SIZE,
						voiceSize);
				intent.putExtra(MiniGatewayVoiceChooseActivity.GWID, gwID);
				intent.putExtra(MiniGatewayVoiceChooseActivity.DVID, devID);
				intent.setClass(mContext, MiniGatewayVoiceChooseActivity.class);

				DeviceDetailsActivity.instance.startActivityForResult(intent,
						11);

			}
			if (!(v == miniGatewayVoiceItemRelativeLayout || v == voiceButton || v == lightButton)) {
				controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo()
						.getEpType(), DATA_CTRL_MINI_LIGHT + colorLight
						+ colorMode);
			}
		}

	};

	// 初始化灯光模式的图片资源
	private void initLightMode() {
		lightModeOftenLight
				.setBackgroundResource(R.drawable.mini_geteway_light_mode_often_light);
		lightModeStream
				.setBackgroundResource(R.drawable.mini_geteway_light_mode_stream);
		lightModeFastFlash
				.setBackgroundResource(R.drawable.mini_geteway_light_mode_fast_flash);
		lightModeSlowFlash
				.setBackgroundResource(R.drawable.mini_geteway_light_mode_slow_flash);
		lightModeOff
				.setBackgroundResource(R.drawable.mini_geteway_light_mode_off);
	}

	protected void changeTextViewStatus(TextView textView) {
		initTextViewStatus();
		textView.setVisibility(View.VISIBLE);
	}

	private void initTextViewStatus() {
		lightRedTextView.setVisibility(View.INVISIBLE);
		lightGreenTextView.setVisibility(View.INVISIBLE);
		lightYellowTextView.setVisibility(View.INVISIBLE);
//		lightBlueTextView.setVisibility(View.INVISIBLE);
		lightPurpleTextView.setVisibility(View.INVISIBLE);
		lightCyanTextView.setVisibility(View.INVISIBLE);
		lightWhiteTextView.setVisibility(View.INVISIBLE);
	}

	protected void changeImageViewStatus(ImageView imageView) {
		initImageViewStatus();
		changeImageViewSize(imageView, 35);
	}

	private void changeImageViewSize(ImageView imageView, int dp) {
		LinearLayout.LayoutParams mParams = new LinearLayout.LayoutParams(
				(int) SizeUtil.dp2px(mContext, dp), (int) SizeUtil.dp2px(
						mContext, dp));
		imageView.setLayoutParams(mParams);
	}

	// 初始设备中灯光的颜色图片大小
	private void initImageViewStatus() {
		changeImageViewSize(lightRedImageView, 25);
		changeImageViewSize(lightGreenImageView, 25);
		changeImageViewSize(lightYellowImageView, 25);
//		changeImageViewSize(lightBlueImageView, 25);
		changeImageViewSize(lightPurpleImageView, 25);
		changeImageViewSize(lightCyanImageView, 25);
		changeImageViewSize(lightWhiteImageView, 25);
	}

	// 管家中的点击事件
	private OnClickListener houseKeeperOnClickListener = new OnClickListener() {

		public void onClick(View v) {
			if (v == lightRedImageView) {
				colorLight = MINI_GATEWAY_LIGHT_RED;
				changeImageViewStatus(lightRedImageView);
				changeTextViewStatus(lightRedTextView);
				changeTaskTextViewStatus(lightTextView,
						R.string.miniGW_DeviceColor_Red_flames);
			} else if (v == lightGreenImageView) {
				colorLight = MINI_GATEWAY_LIGHT_GREEN;
				changeImageViewStatus(lightGreenImageView);
				changeTextViewStatus(lightGreenTextView);
				changeTaskTextViewStatus(lightTextView,
						R.string.miniGW_DeviceColor_Jade_green);
			} else if (v == lightYellowImageView) {
				colorLight = MINI_GATEWAY_LIGHT_YELLOW;
				changeImageViewStatus(lightYellowImageView);
				changeTextViewStatus(lightYellowTextView);
				changeTaskTextViewStatus(lightTextView,
						R.string.miniGW_DeviceColor_Mango_yellow);
			} else if (v == lightBlueImageView) {
				colorLight = MINI_GATEWAY_LIGHT_BLUE;
				changeImageViewStatus(lightBlueImageView);
				changeTextViewStatus(lightBlueTextView);
				changeTaskTextViewStatus(lightTextView,
						R.string.miniGW_DeviceColor_Quiet_blue);
			} else if (v == lightPurpleImageView) {
				colorLight = MINI_GATEWAY_LIGHT_PURPLE;
				changeImageViewStatus(lightPurpleImageView);
				changeTextViewStatus(lightPurpleTextView);
				changeTaskTextViewStatus(lightTextView,
						R.string.miniGW_DeviceColor_Romantic_purple);
			} else if (v == lightCyanImageView) {
				colorLight = MINI_GATEWAY_LIGHT_CYAN;
				changeImageViewStatus(lightCyanImageView);
				changeTextViewStatus(lightCyanTextView);
				changeTaskTextViewStatus(lightTextView,
						R.string.miniGW_DeviceColor_Science_green);
			} else if (v == lightWhiteImageView) {
				colorLight = MINI_GATEWAY_LIGHT_WHITE;
				changeImageViewStatus(lightWhiteImageView);
				changeTextViewStatus(lightWhiteTextView);
				changeTaskTextViewStatus(lightTextView,
						R.string.device_mini_light_color7);
			} else if (v == lightModeOftenLight) {
				colorMode = MINI_GATEWAY_LIGHT_MODE_OFTEN_LIGHT;
				initLightMode();
				lightModeOftenLight
						.setBackgroundResource(R.drawable.mini_geteway_light_mode_often_light_pressd);
			} else if (v == lightModeStream) {
				colorMode = MINI_GATEWAY_LIGHT_MODE_STREAM;
				initLightMode();
				lightModeStream
						.setBackgroundResource(R.drawable.mini_geteway_light_mode_stream_pressed);
			} else if (v == lightModeFastFlash) {
				colorMode = MINI_GATEWAY_LIGHT_MODE_FAST_FLASH;
				initLightMode();
				lightModeFastFlash
						.setBackgroundResource(R.drawable.mini_geteway_light_mode_fast_flash_pressed);
			} else if (v == lightModeSlowFlash) {
				colorMode = MINI_GATEWAY_LIGHT_MODE_SLOW_FLASH;
				initLightMode();
				lightModeSlowFlash
						.setBackgroundResource(R.drawable.mini_geteway_light_mode_slow_flash_pressed);
			} else if (v == lightModeOff) {
				colorMode = MINI_GATEWAY_LIGHT_MODE_OFF;
				initLightMode();
				lightModeOff
						.setBackgroundResource(R.drawable.mini_geteway_light_mode_off_pressed);
			}
		}

	};

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		if (!(epData == null || epData.equals(""))) {
			String type = epData.charAt(0) + "";
			String currentcolor = epData.substring(1, 3);
			String currentMode = epData.substring(3, 5);
			if (type.equals(DATA_CTRL_MINI_LIGHT)) {
				switch (currentcolor) {
				case MINI_GATEWAY_LIGHT_RED:
					colorLight = MINI_GATEWAY_LIGHT_RED;
					changeImageViewStatus(lightRedImageView);
					changeTextViewStatus(lightRedTextView);
					break;
				case MINI_GATEWAY_LIGHT_GREEN:
					colorLight = MINI_GATEWAY_LIGHT_GREEN;
					changeImageViewStatus(lightGreenImageView);
					changeTextViewStatus(lightGreenTextView);

					break;
				case MINI_GATEWAY_LIGHT_YELLOW:
					colorLight = MINI_GATEWAY_LIGHT_YELLOW;
					changeImageViewStatus(lightYellowImageView);
					changeTextViewStatus(lightYellowTextView);
					break;
				case MINI_GATEWAY_LIGHT_BLUE:
//					colorLight = MINI_GATEWAY_LIGHT_BLUE;
//					changeImageViewStatus(lightBlueImageView);
//					changeTextViewStatus(lightBlueTextView);

					break;
				case MINI_GATEWAY_LIGHT_PURPLE:
					colorLight = MINI_GATEWAY_LIGHT_PURPLE;
					changeImageViewStatus(lightPurpleImageView);
					changeTextViewStatus(lightPurpleTextView);

					break;
				case MINI_GATEWAY_LIGHT_CYAN:
					colorLight = MINI_GATEWAY_LIGHT_CYAN;
					changeImageViewStatus(lightCyanImageView);
					changeTextViewStatus(lightCyanTextView);
					break;
				case MINI_GATEWAY_LIGHT_WHITE:
					colorLight = MINI_GATEWAY_LIGHT_WHITE;
					changeImageViewStatus(lightWhiteImageView);
					changeTextViewStatus(lightWhiteTextView);
					break;
				}

				switch (currentMode) {
				case MINI_GATEWAY_LIGHT_MODE_OFTEN_LIGHT:
					initLightMode();
					lightModeOftenLight
							.setBackgroundResource(R.drawable.mini_geteway_light_mode_often_light_pressd);
					break;
				case MINI_GATEWAY_LIGHT_MODE_STREAM:
					initLightMode();
					lightModeStream
							.setBackgroundResource(R.drawable.mini_geteway_light_mode_stream_pressed);
					break;
				case MINI_GATEWAY_LIGHT_MODE_SLOW_FLASH:
					initLightMode();
					lightModeSlowFlash
							.setBackgroundResource(R.drawable.mini_geteway_light_mode_slow_flash_pressed);
					break;
				case MINI_GATEWAY_LIGHT_MODE_FAST_FLASH:
					initLightMode();
					lightModeFastFlash
							.setBackgroundResource(R.drawable.mini_geteway_light_mode_fast_flash_pressed);
					break;
				case MINI_GATEWAY_LIGHT_MODE_OFF:
					initLightMode();
					lightModeOff
							.setBackgroundResource(R.drawable.mini_geteway_light_mode_off_pressed);
					break;
				}
			}
			if (type.equals(DATA_CTRL_MINI_VOICE)) {
				//she
				voiceSeekbar.setProgress(Integer.parseInt(currentMode));
				int a=Integer.parseInt(currentcolor);
				chooce_voice_TV.setText(musicArrary[a]);
				//选择声音名称存入本地
				if (a!=0){
					Preference.getPreferences().saveVoiceChooseNum(a);
				}

			}
		}
	}
	protected void changeTaskTextViewStatus(TextView textView, int stringID) {
		textView.setText(getString(stringID));
	}

	// 声音选择Dialog
	protected void showVoiceChooseDialog(final AutoActionInfo autoActionInfo) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.scene_task_control_mini_gateway_voice_dialog, null);
		initDate();
		String epData = autoActionInfo.getEpData();
		voiceChooseAdapter = new MiniGatewayVoiceChooseAdapter(mContext);
		voiceChooseAdapter.swapData(voiceTypeList);
		voiceChooseListView = (ListView) view
				.findViewById(R.id.voice_choose_lv);
		voiceSeekbar = (SeekBar) view
				.findViewById(R.id.device_mini_geteway_led_voice_seekbar);
		voiceChooseListView.setAdapter(voiceChooseAdapter);
		if (epData.length() != 0) {
			voiceChooseListView.setSelection(Integer.parseInt(epData.substring(
					1, 3)));
			voiceSeekbar.setProgress(Integer.parseInt(epData.substring(3)));
		}
		voiceChooseListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				initDate();
				voiceTypeList.set(arg2, "1");
				voiceChooseAdapter.swapData(voiceTypeList);
				int position = arg2 + 1;
				if (position < 10) {
					voiceMode = "0" + position;
				} else {
					voiceMode = position + "";
				}
				controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo()
						.getEpType(), DATA_CTRL_MINI_VOICE + voiceMode
						+ voiceSize);
				showVoiceChooseListViewStatus(arg2);
			}
		});
		voiceSeekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar arg0) {
				int voiceNumber = arg0.getProgress();

				if (voiceNumber < 10) {
					voiceSize = "0" + voiceNumber;
				} else {
					voiceSize = voiceNumber + "";
				}
				controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo()
						.getEpType(), DATA_CTRL_MINI_VOICE + voiceMode
						+ voiceSize);
			}

			@Override
			public void onStartTrackingTouch(SeekBar arg0) {

			}

			@Override
			public void onProgressChanged(SeekBar arg0, int arg1, boolean arg2) {

			}
		});
		WLDialog.Builder builder = new WLDialog.Builder(
				DeviceSettingActivity.instance);
		builder.setTitle(
				mContext.getResources().getString(
						R.string.device_led_mini_task_voice))
				.setContentView(view)
				.setNegativeButton(
						mContext.getResources().getString(R.string.cancel))
				.setPositiveButton(
						mContext.getResources().getString(R.string.common_ok))
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						voiceTextView.setVisibility(View.VISIBLE);
						lightTextView.setVisibility(View.INVISIBLE);
						autoActionInfo.setEpData(DATA_CTRL_MINI_VOICE
								+ voiceMode + voiceSize);
					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		dialog = builder.create();
		dialog.show();
	}

	protected void showVoiceChooseListViewStatus(int position) {
		switch (position) {
		case 0:
			changeTaskTextViewStatus(voiceTextView,
					R.string.miniGW_DeviceVoice_Dingdong);
			break;
		case 1:
			changeTaskTextViewStatus(voiceTextView,
					R.string.miniGW_DeviceVoice_jingle);
			break;
		case 2:
			changeTaskTextViewStatus(voiceTextView,
					R.string.miniGW_DeviceVoice_crisp);
			break;
		case 3:
			changeTaskTextViewStatus(voiceTextView,
					R.string.miniGW_DeviceVoice_Long_tone);
			break;
		case 4:
			changeTaskTextViewStatus(voiceTextView,
					R.string.miniGW_DeviceVoice_fluctuation);
			break;
		case 5:
			changeTaskTextViewStatus(voiceTextView,
					R.string.miniGW_DeviceVoice_Cuckoo);
			break;
		case 6:
			changeTaskTextViewStatus(voiceTextView,
					R.string.miniGW_DeviceVoice_Didi);
			break;
		case 7:
			changeTaskTextViewStatus(voiceTextView,
					R.string.miniGW_DeviceVoice_fierce);
			break;
		case 8:
			changeTaskTextViewStatus(voiceTextView,
					R.string.miniGW_DeviceVoice_rapid);
			break;
		case 9:
			changeTaskTextViewStatus(voiceTextView,
					R.string.miniGW_DeviceVoice_sharp);
			break;
		case 10:
			changeTaskTextViewStatus(voiceTextView,
					R.string.miniGW_DeviceVoice_police);
			break;
		}
	}

	private void initDate() {
		if (voiceTypeList == null) {
			voiceTypeList = new ArrayList<String>();
		}
		voiceTypeList.clear();
		for (int i = 0; i < 11; i++) {
			voiceTypeList.add("0");
		}
	}

	// 灯效选择Dialog
	protected void showLightChooseDialog(final AutoActionInfo autoActionInfo) {
		View view = LayoutInflater.from(mContext).inflate(
				R.layout.scene_task_control_mini_gateway_light_dialog, null);
		ViewUtils.inject(this, view);
		lightRedImageView.setOnClickListener(houseKeeperOnClickListener);
		lightGreenImageView.setOnClickListener(houseKeeperOnClickListener);
		lightYellowImageView.setOnClickListener(houseKeeperOnClickListener);
//		lightBlueImageView.setOnClickListener(houseKeeperOnClickListener);
		lightPurpleImageView.setOnClickListener(houseKeeperOnClickListener);
		lightCyanImageView.setOnClickListener(houseKeeperOnClickListener);
		lightWhiteImageView.setOnClickListener(houseKeeperOnClickListener);

		lightModeOftenLight.setOnClickListener(houseKeeperOnClickListener);
		lightModeStream.setOnClickListener(houseKeeperOnClickListener);
		lightModeFastFlash.setOnClickListener(houseKeeperOnClickListener);
		lightModeSlowFlash.setOnClickListener(houseKeeperOnClickListener);
		lightModeOff.setOnClickListener(houseKeeperOnClickListener);
		WLDialog.Builder builder = new WLDialog.Builder(
				DeviceSettingActivity.instance);
		builder.setTitle(
				mContext.getResources().getString(
						R.string.device_led_mini_task_light))
				.setContentView(view)
				.setNegativeButton(
						mContext.getResources().getString(R.string.cancel))
				.setPositiveButton(
						mContext.getResources().getString(R.string.common_ok))
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						voiceTextView.setVisibility(View.INVISIBLE);
						lightTextView.setVisibility(View.VISIBLE);
						autoActionInfo.setEpData(DATA_CTRL_MINI_LIGHT
								+ colorLight + colorMode);
					}

					@Override
					public void onClickNegative(View contentViewLayout) {

					}
				});
		dialog = builder.create();
		dialog.show();
	}

	private void initLightChooseDialogView(String colorLight) {
		switch (colorLight) {
		case MINI_GATEWAY_LIGHT_RED:
			changeTaskTextViewStatus(lightTextView,
					R.string.miniGW_DeviceColor_Red_flames);
			break;
		case MINI_GATEWAY_LIGHT_GREEN:
			changeTaskTextViewStatus(lightTextView,
					R.string.miniGW_DeviceColor_Jade_green);
			break;
		case MINI_GATEWAY_LIGHT_YELLOW:
			changeTaskTextViewStatus(lightTextView,
					R.string.miniGW_DeviceColor_Mango_yellow);
			break;
		case MINI_GATEWAY_LIGHT_BLUE:
			changeTaskTextViewStatus(lightTextView,
					R.string.device_mini_light_color4);
			break;
		case MINI_GATEWAY_LIGHT_PURPLE:
			changeTaskTextViewStatus(lightTextView,
					R.string.miniGW_DeviceColor_Romantic_purple);
			break;
		case MINI_GATEWAY_LIGHT_CYAN:
			changeTaskTextViewStatus(lightTextView,
					R.string.miniGW_DeviceColor_Science_green);
			break;
		case MINI_GATEWAY_LIGHT_WHITE:
			changeTaskTextViewStatus(lightTextView,
					R.string.device_mini_light_color7);
			break;

		default:
			break;
		}
	}

	// 管家模块
	@Override
	public DeviceShortCutControlItem onCreateShortCutView(
			DeviceShortCutControlItem item, LayoutInflater inflater) {
		if (item == null) {
			item = new ControlableDeviceShortCutControlItem(
					inflater.getContext());
		}
		((ControlableDeviceShortCutControlItem) item).setOpenVisiable(false);
		((ControlableDeviceShortCutControlItem) item).setCloseVisiable(false);
		item.setWulianDevice(this);
		return item;
	}

	@Override
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,
			AutoActionInfo autoActionInfo) {

		if (item == null) {
			ShortCutControlableDeviceSelectDataItem shortCutItem = new ShortCutControlableDeviceSelectDataItem(
					inflater.getContext());
			shortCutItem.setCloseVisiable(false);
			shortCutItem.setOpenVisiable(false);
			item = shortCutItem;
		}
		item.setWulianDeviceAndSelectData(this, autoActionInfo);
		return item;
	}

	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater, final AutoActionInfo autoActionInfo) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		holder.setShowDialog(false);
		String epData = autoActionInfo.getEpData();
		View view = inflater.inflate(
				R.layout.scene_task_control_mini_voice_light, null);
		lightRelativeLayout = (RelativeLayout) view
				.findViewById(R.id.mini_gateway_task_light_rvlay);
		voiceRelativeLayout = (RelativeLayout) view
				.findViewById(R.id.mini_gateway_task_voice_rvlay);
		voiceTextView = (TextView) view
				.findViewById(R.id.mini_gateway_task_voice_tv);
		lightTextView = (TextView) view
				.findViewById(R.id.mini_gateway_task_light_tv);
		lightRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showLightChooseDialog(autoActionInfo);
			}
		});
		voiceRelativeLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				showVoiceChooseDialog(autoActionInfo);
			}
		});
		holder.setContentView(view);
		if (!(epData == null || epData.equals(""))) {
			if (epData.subSequence(0, 1).equals(DATA_CTRL_MINI_LIGHT)) {
				colorLight = epData.substring(1, 3);
				initLightChooseDialogView(colorLight);
			} else if (epData.subSequence(0, 1).equals(DATA_CTRL_MINI_VOICE)) {
				voiceMode = epData.substring(1, 3);
				showVoiceChooseListViewStatus(Integer.parseInt(voiceMode) - 1);
			}
		}
		return holder;
	}

	@Override
	public void OnRefreshResultData(Intent data) {
		 String argString=data.getStringExtra("arg");
		 if(!StringUtil.isNullOrEmpty(argString)){
		 chooce_voice_TV.setText(argString);
		 }else{
		
		 }

	}
}
