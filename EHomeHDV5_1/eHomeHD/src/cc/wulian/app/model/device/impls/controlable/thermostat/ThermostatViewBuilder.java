package cc.wulian.app.model.device.impls.controlable.thermostat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.impls.controlable.thermostat.ArcProgressBar.OnMoveViewValueChanged;
import cc.wulian.app.model.device.impls.controlable.thermostat.ArcProgressBar.OnUpViewValueChanged;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.app.model.device.view.AnimationUtil;
import cc.wulian.smarthomev5.R;

public class ThermostatViewBuilder {
	public static final int WIND_SPEED_0 = 0;
	public static final int WIND_SPEED_1 = 1;
	public static final int WIND_SPEED_2 = 2;
	public static final int WIND_SPEED_3 = 3;
	public static final int WIND_SPEED_4 = 4;
	public static final int CUR_MODEL_HOT_0 = 0;
	public static final int CUR_MODEL_COOL_1 = 1;
	public static final int CUR_MODEL_FAN_2 = 2;
	private static final String DATA_CTRL_STATE_TEMP_SIGN_NEGATIVE = "01";
	private static final String UNIT_C = "\u00B0C";

	private Context mContext;
	private CurTempListener curTempListener;
	private CurWindSpeedListener curWindSpeedListener;
	private CurModelListener curModelListener;
	private CurSwitchListener curSwitchListener;

	private boolean isShowSettingTemp = false;
	private boolean isSwitchOpen = false;
	private float curTemp;
	private int curWindSpeed;
	private int curModel;
	private String mTempSign;

	private View contentView;
	private MyArcProgressBar progressBar;
	private LinearLayout progressBarLayout;
	private Button getTemp;
	private TextView setTemp;
	private TextView currentTemp;

	private LinearLayout windPowerLayout;
	private LinearLayout windPowerbg;
	private ImageView wind;
	private LinearLayout windPower;
	private LinearLayout windPower1layout;
	private LinearLayout windPower2layout;
	private LinearLayout windPower3layout;
	private ImageView windPower1;
	private ImageView windPower2;
	private ImageView windPower3;
	private ImageView windAuto;

	private LinearLayout modelbg;
	private ImageView hotbg;
	private ImageView refrigerationbg;
	private ImageView airsupplaybg;
	private ImageView hot;
	private ImageView refrigeration;
	private ImageView airsupplay;

	private LinearLayout mSwitchLayout;
	private Button mSwitch;

	public View getContentView() {
		return contentView;
	}

	public void setContentView(View contentView) {
		this.contentView = contentView;
	}

	public ThermostatViewBuilder(Context context) {
		this.mContext = context;
		initContentView();
	}

	public boolean isShowSettingTemp() {
		return isShowSettingTemp;
	}

	public void setShowSettingTemp(boolean isShowSettingTemp) {
		this.isShowSettingTemp = isShowSettingTemp;
	}

	public boolean isSwitchOpen() {
		return isSwitchOpen;
	}

	public void setSwitchOpen(boolean isSwitchOpen) {
		this.isSwitchOpen = isSwitchOpen;
	}

	public float getCurTemp() {
		return curTemp;
	}

	public void setCurTemp(float curTemp) {
		this.curTemp = curTemp;
	}

	public int getCurWindSpeed() {
		return curWindSpeed;
	}

	public void setCurWindSpeed(int curWindSpeed) {
		this.curWindSpeed = curWindSpeed;
	}

	public int getCurModel() {
		return curModel;
	}

	public void setCurModel(int curModel) {
		this.curModel = curModel;
	}

	public String getmTempSign() {
		return mTempSign;
	}

	public void setmTempSign(String mTempSign) {
		this.mTempSign = mTempSign;
	}

	public void setCurProgress(int curProgress) {
		progressBar.setProcess(curProgress);
		setTemp.setText(mContext.getResources().getString(
				R.string.device_set_tempure)
				+ curProgress + "°C");
	}

	public void setCurTempListener(CurTempListener listener) {
		this.curTempListener = listener;
	}

	public void setCurWindSpeedListener(CurWindSpeedListener listener) {
		this.curWindSpeedListener = listener;
	}

	public void setCurModelListener(CurModelListener listener) {
		this.curModelListener = listener;
	}

	public void setCurSwitchListener(CurSwitchListener listener) {
		this.curSwitchListener = listener;
	}

	public void showThermostatView() {
		initThermostatView();
		initCurTemp();
		initWindSpeed();
		initCurModel();
		initSwitchStatus();
		initSpeedShow();

	}

	public void initContentView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		contentView = inflater.inflate(R.layout.device_thermost, null);
	}

	private void initThermostatNoProgress() {
		progressBarLayout = (LinearLayout) contentView
				.findViewById(R.id.device_thermost_arcprogressbar_layout);
		progressBar = (MyArcProgressBar) contentView
				.findViewById(R.id.device_thermost_arcprogressbar);
		getTemp = (Button) contentView
				.findViewById(R.id.device_thermost_get_temp);
		setTemp = (TextView) contentView
				.findViewById(R.id.device_thermost_set_temp);
		setTemp.setVisibility(View.VISIBLE);
		currentTemp = (TextView) contentView
				.findViewById(R.id.device_thermost_current_temp);

		windPowerLayout = (LinearLayout)contentView.findViewById(R.id.device_thermost_wind_power_layout);
		windPowerLayout.setVisibility(View.VISIBLE);
		windPowerbg = (LinearLayout) contentView
				.findViewById(R.id.device_thermost_wind_power_bg);
		wind = (ImageView) contentView.findViewById(R.id.device_thermost_wind);
		windPower = (LinearLayout) contentView
				.findViewById(R.id.device_thermost_wind_power);
		windPower1layout = (LinearLayout) contentView
				.findViewById(R.id.device_thermost_wind_power1_layout);
		windPower2layout = (LinearLayout) contentView
				.findViewById(R.id.device_thermost_wind_power2_layout);
		windPower3layout = (LinearLayout) contentView
				.findViewById(R.id.device_thermost_wind_power3_layout);
		windPower1 = (ImageView) contentView
				.findViewById(R.id.device_thermost_wind_power1);
		windPower2 = (ImageView) contentView
				.findViewById(R.id.device_thermost_wind_power2);
		windPower3 = (ImageView) contentView
				.findViewById(R.id.device_thermost_wind_power3);
		windAuto = (ImageView) contentView
				.findViewById(R.id.device_thermost_wind_power_auto);
		wind.setOnClickListener(mClickListener);
		windPower1layout.setOnClickListener(mClickListener);
		windPower2layout.setOnClickListener(mClickListener);
		windPower3layout.setOnClickListener(mClickListener);
		windAuto.setOnClickListener(mClickListener);

		modelbg = (LinearLayout) contentView
				.findViewById(R.id.device_thermost_model_bg);
		hotbg = (ImageView) contentView
				.findViewById(R.id.device_thermost_model_hot_bg);
		refrigerationbg = (ImageView) contentView
				.findViewById(R.id.device_thermost_model_refrigeration_bg);
		airsupplaybg = (ImageView) contentView
				.findViewById(R.id.device_thermost_model_airsupply_bg);
		hot = (ImageView) contentView
				.findViewById(R.id.device_thermost_model_hot);
		refrigeration = (ImageView) contentView
				.findViewById(R.id.device_thermost_model_refrigeration);
		airsupplay = (ImageView) contentView
				.findViewById(R.id.device_thermost_model_airsupply);
		hot.setOnClickListener(mClickListener);
		refrigeration.setOnClickListener(mClickListener);
		airsupplay.setOnClickListener(mClickListener);

		mSwitchLayout = (LinearLayout) contentView.findViewById(R.id.device_thermost_switch_layout);
		mSwitch = (Button) contentView
				.findViewById(R.id.device_thermost_switch);
		mSwitch.setOnClickListener(mClickListener);

	}

	public void initThermostatView(){
		initThermostatNoProgress();
		progressBar.setOnMoveViewValueChanged(new OnMoveViewValueChanged() {

			@Override
			public void onMoveChanged(int value) {
				setTemp.setText(mContext.getResources().getString(
						R.string.device_set_tempure)
						+ (value + 16) + "°C");
			}
		});

		progressBar.setOnUpViewValueChanged(new OnUpViewValueChanged() {

			@Override
			public void onUpChanged(int value) {
				String temp = String.valueOf(value + 16);
				setTemp.setText(mContext.getResources().getString(
						R.string.device_set_tempure)
						+ temp + "°C");
				if (curTempListener != null) {
					curTempListener.onTempChanged(value + 16);
				}
			}
		});
	}
	
	public void initCurIrAirTemp(){
		StringBuilder sb = new StringBuilder();
		sb.append(curTemp);
		sb.append(UNIT_C);
		getTemp.setText(sb);
	}
	public void initCurTemp() {
		StringBuilder sb = new StringBuilder();
		if (mTempSign != null) {
			if (DeviceUtil.isSameAs(DATA_CTRL_STATE_TEMP_SIGN_NEGATIVE,
					mTempSign)) {
				sb.append("-");
			}
		}
		sb.append(curTemp);
		sb.append(UNIT_C);
		getTemp.setText(sb);
		if (isShowSettingTemp) {
			setTemp.setVisibility(View.VISIBLE);
		} else {
			setTemp.setVisibility(View.INVISIBLE);
		}

	}

	public void initWindSpeed() {
		if (WIND_SPEED_0 == curWindSpeed) {
			windPowerbg
					.setBackgroundResource(R.drawable.device_thermost_control_bg_1);
			windPower1.setVisibility(View.INVISIBLE);
			windPower2.setVisibility(View.INVISIBLE);
			windPower3.setVisibility(View.INVISIBLE);
			windAuto.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_thermost_auto1));
		} else if (WIND_SPEED_1 == curWindSpeed) {
			openWindSpeedBackground();
			windPower1.setVisibility(View.VISIBLE);
			windPower2.setVisibility(View.INVISIBLE);
			windPower3.setVisibility(View.INVISIBLE);
			windAuto.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_thermost_auto1));

		} else if (WIND_SPEED_2 == curWindSpeed) {
			openWindSpeedBackground();
			windPower1.setVisibility(View.VISIBLE);
			windPower2.setVisibility(View.VISIBLE);
			windPower3.setVisibility(View.INVISIBLE);
			windAuto.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_thermost_auto1));

		} else if (WIND_SPEED_3 == curWindSpeed) {
			openWindSpeedBackground();
			windPower1.setVisibility(View.VISIBLE);
			windPower2.setVisibility(View.VISIBLE);
			windPower3.setVisibility(View.VISIBLE);
			windAuto.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_thermost_auto1));

		} else if (WIND_SPEED_4 == curWindSpeed) {
			windPowerbg
					.setBackgroundResource(R.drawable.device_thermost_control_bg_1);
			windPower.setVisibility(View.VISIBLE);
			windAuto.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_thermost_auto2));

		}

	}

	
	public void initCurModel() {
		if (CUR_MODEL_HOT_0 == curModel) {
			progressBar
					.setBackgroundResource(R.drawable.device_thermost_temp_bg_2);
			refrigerationbg.setVisibility(View.INVISIBLE);
			hotbg.setVisibility(View.VISIBLE);
			airsupplaybg.setVisibility(View.INVISIBLE);
			hot.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_thermost_model_hot_g));
			refrigeration.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_thermost_model_cool_w));
			airsupplay.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_thermost_model_wind_w));
		} else if (CUR_MODEL_COOL_1 == curModel) {
			progressBar
					.setBackgroundResource(R.drawable.device_thermost_temp_bg_2);
			refrigerationbg.setVisibility(View.VISIBLE);
			hotbg.setVisibility(View.INVISIBLE);
			airsupplaybg.setVisibility(View.INVISIBLE);
			hot.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_thermost_model_hot_w));
			refrigeration.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_thermost_model_cool_g));
			airsupplay.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_thermost_model_wind_w));

		} else if (CUR_MODEL_FAN_2 == curModel) {
			progressBar
					.setBackgroundResource(R.drawable.device_thermost_temp_bg_1);
			refrigerationbg.setVisibility(View.INVISIBLE);
			hotbg.setVisibility(View.INVISIBLE);
			airsupplaybg.setVisibility(View.VISIBLE);
			hot.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_thermost_model_hot_w));
			refrigeration.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_thermost_model_cool_w));
			airsupplay.setImageDrawable(mContext.getResources().getDrawable(
					R.drawable.device_thermost_model_wind_g));
		}
	}

	public void initIRAirEditNewNameView(){
		initThermostatNoProgress();
//		mSwitchLayout.setVisibility(View.GONE);
		getTemp.setText("16°C");
		setTemp.setVisibility(View.INVISIBLE);
		mSwitch.setPadding(0, 0, 0, 20);
		progressBarLayout.setPadding(20, 0, 20, 0);
//		progressBar.setProcess(16);
		currentTemp.setText(mContext.getResources().getString(R.string.device_set_tempure));
		progressBar.setOnMoveViewValueChanged(new OnMoveViewValueChanged() {

			@Override
			public void onMoveChanged(int value) {
				getTemp.setText((value + 16) + "°C");
			}
		});

		progressBar.setOnUpViewValueChanged(new OnUpViewValueChanged() {

			@Override
			public void onUpChanged(int value) {
				String temp = String.valueOf(value + 16);
				getTemp.setText(temp + "°C");
				if (curTempListener != null) {
					curTempListener.onTempChanged(value + 16);
				}
			}
		});
	}
	public void irAirEditNewNameNoShowSwitch(){
		windPowerLayout.setVisibility(View.GONE);
			wind.setClickable(true);
			windAuto.setClickable(true);
			hot.setClickable(true);
			refrigeration.setClickable(true);
			airsupplay.setClickable(true);
			progressBar.setBackgroundResource(R.drawable.device_thermost_temp_bg_2);
			windPower.setVisibility(View.VISIBLE);
			closeWindSpeedBackground();
			modelbg.setBackgroundResource(R.drawable.device_thermost_control_bg_2);
	}
	public void initSwitchStatus() {
		progressBar.setClickable(isSwitchOpen);
		wind.setClickable(isSwitchOpen);
		windAuto.setClickable(isSwitchOpen);
		hot.setClickable(isSwitchOpen);
		refrigeration.setClickable(isSwitchOpen);
		airsupplay.setClickable(isSwitchOpen);
		if (isSwitchOpen) {
			progressBar
					.setBackgroundResource(R.drawable.device_thermost_temp_bg_2);
			windPower.setVisibility(View.VISIBLE);
			openWindSpeedBackground();

			modelbg.setBackgroundResource(R.drawable.device_thermost_control_bg_2);
			mSwitch.setText("OFF");
		} else {
			progressBar
					.setBackgroundResource(R.drawable.device_thermost_temp_bg_1);
			windPower.setVisibility(View.INVISIBLE);
			closeWindSpeedBackground();
			modelbg.setBackgroundResource(R.drawable.device_thermost_control_bg_1);
			mSwitch.setText("ON");
		}
	}

	
	public void initAirSpeedShow(){
		int fanSpeed = curWindSpeed;
		int speedIcon;
		if (fanSpeed == WIND_SPEED_4) {
			fanSpeed = WIND_SPEED_0;
		}
		speedIcon = R.drawable.device_thermost_wind;
		wind.setImageResource(speedIcon);
		Animation animation = AnimationUtil
				.getRotateAnimation(fanSpeed2AnimationSpeed(fanSpeed));
		wind.startAnimation(animation);
	}
	public void initSpeedShow() {
		if (isSwitchOpen) {

			int fanSpeed = curWindSpeed;
			int speedIcon;
			if (fanSpeed == WIND_SPEED_4) {
				fanSpeed = WIND_SPEED_0;
			}
			speedIcon = R.drawable.device_thermost_wind;
			wind.setImageResource(speedIcon);
			Animation animation = AnimationUtil
					.getRotateAnimation(fanSpeed2AnimationSpeed(fanSpeed));
			wind.startAnimation(animation);
		} else {
			wind.clearAnimation();
		}
	}

	private int fanSpeed2AnimationSpeed(int fanSpeed) {
		return fanSpeed * 1000;
	}

	public void openWindSpeedBackground() {
		windPowerbg
				.setBackgroundResource(R.drawable.device_thermost_control_bg_2);
		windAuto.setImageDrawable(mContext.getResources().getDrawable(
				R.drawable.device_thermost_auto2));
	}

	public void closeWindSpeedBackground() {
		windPowerbg
				.setBackgroundResource(R.drawable.device_thermost_control_bg_1);
		windPower1.setVisibility(View.INVISIBLE);
		windPower2.setVisibility(View.INVISIBLE);
		windPower3.setVisibility(View.INVISIBLE);
		windAuto.setImageDrawable(mContext.getResources().getDrawable(
				R.drawable.device_thermost_auto1));
	}

	public OnClickListener mClickListener = new OnClickListener() {

		@Override
		public void onClick(View arg0) {
			if (arg0 == wind) {
				if (curWindSpeedListener != null) {
					curWindSpeedListener.onWindSpeedChanged(WIND_SPEED_0);
				}

			} else if (arg0 == windPower1layout) {
				if (curWindSpeedListener != null) {
					curWindSpeedListener.onWindSpeedChanged(WIND_SPEED_1);
				}

			} else if (arg0 == windPower2layout) {
				if (curWindSpeedListener != null) {
					curWindSpeedListener.onWindSpeedChanged(WIND_SPEED_2);
				}

			} else if (arg0 == windPower3layout) {
				if (curWindSpeedListener != null) {
					curWindSpeedListener.onWindSpeedChanged(WIND_SPEED_3);
				}

			} else if (arg0 == windAuto) {
				if (curWindSpeedListener != null) {
					curWindSpeedListener.onWindSpeedChanged(WIND_SPEED_4);
				}

			} else if (arg0 == hot) {
				if (curModelListener != null) {
					curModelListener.onModelChanged(CUR_MODEL_HOT_0);
				}

			} else if (arg0 == refrigeration) {
				if (curModelListener != null) {
					curModelListener.onModelChanged(CUR_MODEL_COOL_1);
				}
			} else if (arg0 == airsupplay) {
				if (curModelListener != null) {
					curModelListener.onModelChanged(CUR_MODEL_FAN_2);
				}
			} else if (arg0 == mSwitch) {
				if (curSwitchListener != null) {
					if (isSwitchOpen) {
						curSwitchListener.oSwitchChanged(false);
					} else {
						curSwitchListener.oSwitchChanged(true);
					}
				}
			}
		}
	};

	public interface CurTempListener {
		public void onTempChanged(int temp);
	}

	public interface CurWindSpeedListener {
		public void onWindSpeedChanged(int speed);
	}

	public interface CurModelListener {
		public void onModelChanged(int model);
	}

	public interface CurSwitchListener {
		public void oSwitchChanged(boolean open);
	}

}
