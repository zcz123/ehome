package cc.wulian.app.model.device.impls.controlable.newthermostat;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;

/**
 * 新控温器界面创建器
 * 
 * @author Administrator
 * 
 */
public class NewThermostatViewBuilder {

	private Context mContext;
	private View contentView;
	private TextView mSetTempTextView;
	private TextView mSymbolTextView;

	private String mFnMode;
	private boolean mIsEheat;
	private String mMode;
	private String mTemperature;
	private double mHeatLimit;
	private double mCoolLimit;
	private double mHeatPoint;
	private double mCoolPoint;
	// private String mSymbol;

	public static final String UNIT_CELSIUS = "℃";
	public static final String UNIT_FAHRENHEIT = "℉";
	public static final String DATA_CTRL_UNIT_0 = "0";
	public static final String DATA_CTRL_UNIT_1 = "1";

	// fnmode
	public static final String FNMODE_MANUAL_1 = "1";
	public static final String FNMODE_AUTO_2 = "2";
	public static final String FNMODE_HEATONLY_3 = "3";
	public static final String FNMODE_COOLONLY_4 = "4";
	public static final String FNMODE_AO_5 = "5";

	// mode
	public static final String MODE_OFF = "0";
	public static final String MODE_HEAT = "1";
	public static final String MODE_COOL = "2";
	public static final String MODE_AUTO = "3";
	public static final String MODE_EHEAT = "4";

	private CheckBox mLayout1_Btn1;
	private CheckBox mLayout1_Btn2;
	private CheckBox mLayout2_Btn1;
	private CheckBox mLayout2_Btn2;
	private CheckBox mLayout2_Btn3;
	private CheckBox mLayout3_Btn1;
	private CheckBox mLayout3_Btn2;
	private CheckBox mLayout3_Btn3;
	private CheckBox mLayout3_Btn4;
	private CheckBox mLayout4_Btn1;
	private CheckBox mLayout4_Btn2;
	private CheckBox mLayout4_Btn3;
	private CheckBox mLayout4_Btn4;
	private CheckBox mLayout4_Btn5;
	private LinearLayout mLayout1;
	private LinearLayout mLayout2;
	private LinearLayout mLayout3;
	private LinearLayout mLayout4;
	private TextView mRoomTempTextView;
	private TextView mRoomTempSymbolText;
	private ImageView mReduceBtn;
	private ImageView mAddBtn;

	public static final int COOL_IMG = R.drawable.device_thermost_cool_selector;
	public static final int HEAT_IMG = R.drawable.device_thermost_heat_selector;
	public static final int EHEAT_IMG = R.drawable.device_thermost_eheat_selector;
	public static final int AUTO_IMG = R.drawable.device_thermost_auto_selector;
	public static final int SWITCH_IMG = R.drawable.device_thermost_switch_selector;

	private LinearLayout[] mLayouts;

	public NewThermostatViewBuilder(Context context) {
		this.mContext = context;
		initContentView();
	}

	private void initContentView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		contentView = inflater.inflate(R.layout.device_new_thermost, null);
	}

	public View getContentView() {
		return contentView;
	}

	private void initThermostatNoProgress() {
		mSetTempTextView = (TextView) contentView
				.findViewById(R.id.device_thermost_setting_temperature_text);
		mSymbolTextView = (TextView) contentView
				.findViewById(R.id.device_thermost_current_symbol_text);
		mRoomTempTextView = (TextView) contentView
				.findViewById(R.id.device_thermost_room_temperature_text);
		mRoomTempSymbolText = (TextView) contentView
				.findViewById(R.id.device_thermost_room_temp_symbol_text);

		mReduceBtn = (ImageView) contentView
				.findViewById(R.id.device_thermost_reduce_imageview);
		mAddBtn = (ImageView) contentView
				.findViewById(R.id.device_thermost_add_imageview);

		mLayout1 = (LinearLayout) contentView
				.findViewById(R.id.device_thermost_layout1);
		mLayout2 = (LinearLayout) contentView
				.findViewById(R.id.device_thermost_layout2);
		mLayout3 = (LinearLayout) contentView
				.findViewById(R.id.device_thermost_layout3);
		mLayout4 = (LinearLayout) contentView
				.findViewById(R.id.device_thermost_layout4);

		mLayout1_Btn1 = (CheckBox) contentView
				.findViewById(R.id.device_thermost_layout1_btn1);
		mLayout1_Btn2 = (CheckBox) contentView
				.findViewById(R.id.device_thermost_layout1_btn2);
		mLayout2_Btn1 = (CheckBox) contentView
				.findViewById(R.id.device_thermost_layout2_btn1);
		mLayout2_Btn2 = (CheckBox) contentView
				.findViewById(R.id.device_thermost_layout2_btn2);
		mLayout2_Btn3 = (CheckBox) contentView
				.findViewById(R.id.device_thermost_layout2_btn3);
		mLayout3_Btn1 = (CheckBox) contentView
				.findViewById(R.id.device_thermost_layout3_btn1);
		mLayout3_Btn2 = (CheckBox) contentView
				.findViewById(R.id.device_thermost_layout3_btn2);
		mLayout3_Btn3 = (CheckBox) contentView
				.findViewById(R.id.device_thermost_layout3_btn3);
		mLayout3_Btn4 = (CheckBox) contentView
				.findViewById(R.id.device_thermost_layout3_btn4);
		mLayout4_Btn1 = (CheckBox) contentView
				.findViewById(R.id.device_thermost_layout4_btn1);
		mLayout4_Btn2 = (CheckBox) contentView
				.findViewById(R.id.device_thermost_layout4_btn2);
		mLayout4_Btn3 = (CheckBox) contentView
				.findViewById(R.id.device_thermost_layout4_btn3);
		mLayout4_Btn4 = (CheckBox) contentView
				.findViewById(R.id.device_thermost_layout4_btn4);
		mLayout4_Btn5 = (CheckBox) contentView
				.findViewById(R.id.device_thermost_layout4_btn5);

		mLayouts = new LinearLayout[] { mLayout1, mLayout2, mLayout3, mLayout4 };

	}

	/**
	 * 初始化界面
	 */
	public void initThermostat() {
		initThermostatNoProgress();
	}

	/**
	 * 设置FNMode
	 * 
	 * @param isEheat
	 * @param fnMode
	 */
	public void setFnMode(boolean isEheat, String fnMode, String mode) {
		mIsEheat = isEheat;
		mFnMode = fnMode;
		mMode = mode;
	}

	/**
	 * 根据模式显示页面
	 */
	public void initModeView() {

		switch (mFnMode) {
		case FNMODE_MANUAL_1:
			showManualView(mIsEheat);
			break;
		case FNMODE_AUTO_2:
			showAutoView(mIsEheat);
			break;
		case FNMODE_HEATONLY_3:
			showHeatOnlyView(mIsEheat);
			break;
		case FNMODE_COOLONLY_4:
			showCoolOnlyView(mIsEheat);
			break;
		case FNMODE_AO_5:
			showAoView(mIsEheat);
			break;

		default:
			break;
		}
	}

	// ao界面
	private void showAoView(boolean mIsEheat) {
		setVisible(mLayout1);
		mLayout1_Btn1.setBackgroundResource(AUTO_IMG);
		mLayout1_Btn2.setBackgroundResource(SWITCH_IMG);
		mLayout1_Btn1.setChecked(false);
		mLayout1_Btn2.setChecked(false);
		switch (mMode) {
		case MODE_AUTO:
			mLayout1_Btn1.setChecked(true);
			break;
		case MODE_OFF:
			mLayout1_Btn2.setChecked(true);
			break;

		default:
			break;
		}
	}

	// 只制冷界面
	private void showCoolOnlyView(boolean mIsEheat) {

		setVisible(mLayout1);
		mLayout1_Btn1.setBackgroundResource(COOL_IMG);
		mLayout1_Btn2.setBackgroundResource(SWITCH_IMG);
		mLayout1_Btn1.setChecked(false);
		mLayout1_Btn2.setChecked(false);
		switch (mMode) {
		case MODE_HEAT:
			mLayout1_Btn1.setChecked(true);
			break;
		case MODE_OFF:
			mLayout1_Btn2.setChecked(true);
			break;

		default:
			break;
		}

	}

	// 只制热界面
	private void showHeatOnlyView(boolean mIsEheat) {
		if (mIsEheat) {
			setVisible(mLayout2);
			mLayout2_Btn1.setBackgroundResource(EHEAT_IMG);
			mLayout2_Btn2.setBackgroundResource(HEAT_IMG);
			mLayout2_Btn3.setBackgroundResource(SWITCH_IMG);
			mLayout2_Btn1.setChecked(false);
			mLayout2_Btn2.setChecked(false);
			mLayout2_Btn3.setChecked(false);

			switch (mMode) {
			case MODE_HEAT:
				mLayout2_Btn2.setChecked(true);
				break;
			case MODE_OFF:
				mLayout2_Btn3.setChecked(true);
				break;
			case MODE_EHEAT:
				mLayout2_Btn1.setChecked(true);
				break;

			default:
				break;
			}
		} else {
			setVisible(mLayout1);
			mLayout1_Btn1.setBackgroundResource(HEAT_IMG);
			mLayout1_Btn2.setBackgroundResource(SWITCH_IMG);
			mLayout1_Btn1.setChecked(false);
			mLayout1_Btn2.setChecked(false);
			
			switch (mMode) {
			case MODE_HEAT:
				mLayout1_Btn1.setChecked(true);
				break;
			case MODE_OFF:
				mLayout1_Btn2.setChecked(true);
				break;

			default:
				break;
			}
		}
	}

	// auto模式下的界面
	private void showAutoView(boolean mIsEheat) {
		if (mIsEheat) {
			setVisible(mLayout4);
			mLayout4_Btn1.setBackgroundResource(EHEAT_IMG);
			mLayout4_Btn2.setBackgroundResource(HEAT_IMG);
			mLayout4_Btn3.setBackgroundResource(AUTO_IMG);
			mLayout4_Btn4.setBackgroundResource(COOL_IMG);
			mLayout4_Btn5.setBackgroundResource(SWITCH_IMG);
			mLayout4_Btn1.setChecked(false);
			mLayout4_Btn2.setChecked(false);
			mLayout4_Btn3.setChecked(false);
			mLayout4_Btn4.setChecked(false);
			mLayout4_Btn5.setChecked(false);

			switch (mMode) {
			case MODE_EHEAT:
				mLayout4_Btn1.setChecked(true);
				break;
			case MODE_HEAT:
				mLayout4_Btn2.setChecked(true);
				break;
			case MODE_AUTO:
				mLayout4_Btn3.setChecked(true);
				break;
			case MODE_COOL:
				mLayout4_Btn4.setChecked(true);
				break;
			case MODE_OFF:
				mLayout4_Btn5.setChecked(true);
				break;

			default:
				break;
			}
		} else {
			setVisible(mLayout3);
			mLayout3_Btn1.setBackgroundResource(HEAT_IMG);
			mLayout3_Btn2.setBackgroundResource(AUTO_IMG);
			mLayout3_Btn3.setBackgroundResource(COOL_IMG);
			mLayout3_Btn4.setBackgroundResource(SWITCH_IMG);
			mLayout3_Btn1.setChecked(false);
			mLayout3_Btn2.setChecked(false);
			mLayout3_Btn3.setChecked(false);
			mLayout3_Btn4.setChecked(false);
			
			switch (mMode) {
			case MODE_HEAT:
				mLayout3_Btn1.setChecked(true);
				break;
			case MODE_AUTO:
				mLayout3_Btn2.setChecked(true);
				break;
			case MODE_COOL:
				mLayout3_Btn3.setChecked(true);
				break;
			case MODE_OFF:
				mLayout3_Btn4.setChecked(true);
				break;

			default:
				break;
			}
		}
	}

	// manual模式下的界面
	private void showManualView(boolean mIsEheat) {
		if (mIsEheat) {
			setVisible(mLayout3);
			mLayout3_Btn1.setBackgroundResource(EHEAT_IMG);
			mLayout3_Btn2.setBackgroundResource(HEAT_IMG);
			mLayout3_Btn3.setBackgroundResource(COOL_IMG);
			mLayout3_Btn4.setBackgroundResource(SWITCH_IMG);
			mLayout3_Btn1.setChecked(false);
			mLayout3_Btn2.setChecked(false);
			mLayout3_Btn3.setChecked(false);
			mLayout3_Btn4.setChecked(false);
			
			switch (mMode) {
			case MODE_EHEAT:
				mLayout3_Btn1.setChecked(true);
				break;
			case MODE_HEAT:
				mLayout3_Btn2.setChecked(true);
				break;
			case MODE_COOL:
				mLayout3_Btn3.setChecked(true);
				break;
			case MODE_OFF:
				mLayout3_Btn4.setChecked(true);
				break;

			default:
				break;
			}

		} else {
			setVisible(mLayout2);
			mLayout2_Btn1.setBackgroundResource(HEAT_IMG);
			mLayout2_Btn2.setBackgroundResource(COOL_IMG);
			mLayout2_Btn3.setBackgroundResource(SWITCH_IMG);
			mLayout3_Btn1.setChecked(false);
			mLayout3_Btn2.setChecked(false);
			mLayout3_Btn3.setChecked(false);
			
			switch (mMode) {
			case MODE_OFF:
				mLayout2_Btn3.setChecked(true);
				break;
			case MODE_HEAT:
				mLayout2_Btn1.setChecked(true);
				break;
			case MODE_COOL:
				mLayout2_Btn2.setChecked(true);
				break;

			default:
				break;
			}
		}
	}

	// 只显示该种模式下的布局
	private void setVisible(LinearLayout mLayout) {
		for (LinearLayout layout : mLayouts) {
			if (mLayout == layout) {
				layout.setVisibility(View.VISIBLE);
			} else {
				layout.setVisibility(View.INVISIBLE);
			}
		}
	}

	/**
	 * 设置温度的单位 0华氏度，1设施度
	 * 
	 * @param symbol
	 */
	public void setSymbol(String symbol) {
		if (StringUtil.equals(symbol, DATA_CTRL_UNIT_0)) {
			mSymbolTextView.setText(UNIT_CELSIUS);
			mRoomTempSymbolText.setText(UNIT_CELSIUS);
		} else if (StringUtil.equals(symbol, DATA_CTRL_UNIT_1)) {
			mSymbolTextView.setText(UNIT_FAHRENHEIT);
			mRoomTempSymbolText.setText(UNIT_FAHRENHEIT);
		}
	}

	// 设置温度
	public void setTemperature() {
		if (mFnMode == null) {
			return;
		}
		switch (mFnMode) {
		case FNMODE_MANUAL_1:
			if (StringUtil.equals(mMode, MODE_HEAT)) {
				mSetTempTextView.setText(String.valueOf(mHeatPoint));
			} else if (StringUtil.equals(mMode, MODE_COOL)) {
				mSetTempTextView.setText(String.valueOf(mCoolPoint));
			}
			break;
		case FNMODE_AUTO_2:
			mSetTempTextView.setText(String.valueOf(mCoolLimit) + "~"
					+ String.valueOf(mHeatLimit));
			break;
		case FNMODE_HEATONLY_3:
			mSetTempTextView.setText(String.valueOf(mHeatPoint));
			break;
		case FNMODE_COOLONLY_4:
			mSetTempTextView.setText(String.valueOf(mCoolPoint));
			break;
		case FNMODE_AO_5:
			mSetTempTextView.setText(String.valueOf(mCoolLimit) + "~"
					+ String.valueOf(mHeatLimit));
			break;

		default:
			break;
		}
	}

	// 设置室内温度
	public void setRoomTemperature(double temperature) {
		mRoomTempTextView.setText(String.valueOf(temperature));
	}

	public void setHeatLimit(double heatlimit) {
		mHeatLimit = heatlimit;
	}

	public void setCoolLimit(double coollimit) {
		mCoolLimit = coollimit;
	}

	public void setHeatPoint(double heatpoint) {
		mHeatPoint = heatpoint;
	}

	public void setCoolPoint(double coolpoint) {
		mCoolPoint = coolpoint;
	}
}
