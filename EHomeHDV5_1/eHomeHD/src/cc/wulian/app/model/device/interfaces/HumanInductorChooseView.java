package cc.wulian.app.model.device.interfaces;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperSelectSensorDeviceDataFragment;
import cc.wulian.smarthomev5.tools.SensorMenuList;
import cc.wulian.smarthomev5.tools.SensorMenuList.SensorMenuItem;
import cc.wulian.smarthomev5.tools.SingleChooseManager;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.utils.InputMethodUtils;
import cc.wulian.smarthomev5.view.WheelTextView;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;
import com.yuantuo.customview.ui.wheel.TosAdapterView;
import com.yuantuo.customview.ui.wheel.TosAdapterView.OnItemSelectedListener;
import com.yuantuo.customview.ui.wheel.TosGallery;
import com.yuantuo.customview.ui.wheel.WheelView;

public class HumanInductorChooseView {

	private String[] tempValus = { "-10","-09", "-08", "-07", "-06", "-05", "-04", "-03", "-02", "-01","00", "01","02", "03", "04", "05", "06", "07", "08", "09", "10", 
            "11", "12", "13", "14", "15", "16", "17", "18", "19", "20", "21", "22", "23","24", "25", "26", "27","28", "29", "30"};
	private String[] humValus = { "45", "46","47", "48", "49", "50", "51", "52", "53", "54", "55", 
		"56", "57", "58", "59", "60", "61", "62", "63", "64", "65", "66", "67", "68","69", "70"};
	
    private TextView temp1TextView;
    private TextView temp2TextView;
    private TextView hum1TextView;
    private TextView hum2TextView;

    private NumberAdapter temp1Adapter;
    private NumberAdapter temp2Adapter;
    private NumberAdapter hum1Adapter;
    private NumberAdapter hum2Adapter;
    private Context mContext;
	private LayoutInflater inflater;
    private String temp1Value;
    private String temp2Value;
    private String hum1Value;
    private String hum2Value;
    private ImageView lessImageView;
	private ImageView moreImageView;
	private TextView lessLevels;
	private TextView moreLevels;
	private EditText valueLessEditText;
	private EditText valueMoreEditText;
	private FrameLayout lessFrameLayout;
	private FrameLayout moreFrameLayout;
	private LinearLayout sensorLayout;
	private LinearLayout lessLayout;
	private LinearLayout moreLayout;
    private LinearLayout rootView;
    private LinearLayout selectAlarmLayout;
    private ImageView greaterTempImageView;
    private ImageView lessTempImageView;
    private ImageView greaterHumImageView;
    private ImageView lessHumImageView;
    private ImageView alarmImageView;
    private ImageView normalImageView;
    private ImageView selectAlarmImageView;
    private Button ensureButton;
    private static final String SPLIT_MORE = ">";
    private static final String SPLIT_MORE_LESS = "<";
    private static final String SPLIT_MORE_EQUEAL = "=";
    private static final String DERAULT_VALUES = "10";
    private DeviceCache mDeviceCache ;
    private SingleChooseManager manager;
    private String values = "";
    private String describe = "";
    private String alarmValues = "";
    private String normalValues = "";
    private String ep;
    private String epType;
    private WLDialog dialog;
    private WheelView valuesWheel;
    
	public HumanInductorChooseView(Context context) {
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		mDeviceCache = DeviceCache.getInstance(context);
		rootView = (LinearLayout)inflater.inflate( R.layout.task_manager_human_inductor_view, null);
		
		sensorLayout = (LinearLayout) rootView.findViewById(R.id.task_sensor_device_choose_layout);
		greaterTempImageView = (ImageView) rootView.findViewById(R.id.tem_hum_select_image1);
		lessTempImageView = (ImageView) rootView.findViewById(R.id.tem_hum_select_image2);
		greaterHumImageView = (ImageView) rootView.findViewById(R.id.tem_hum_select_image3);
		lessHumImageView = (ImageView) rootView.findViewById(R.id.tem_hum_select_image4);
		LinearLayout alarmLayout = (LinearLayout) rootView.findViewById(R.id.human_inductor_alarm);
		LinearLayout normalLayout = (LinearLayout) rootView.findViewById(R.id.human_inductor_normal);
		selectAlarmLayout = (LinearLayout) rootView.findViewById(R.id.human_inductor_select_alarm_layout);
		selectAlarmImageView = (ImageView) rootView.findViewById(R.id.human_inductor_select_alarm_img);
		alarmLayout.setVisibility(View.VISIBLE);
		normalLayout.setVisibility(View.VISIBLE);
		alarmImageView = (ImageView) rootView.findViewById(R.id.human_inductor_alarm_imageview);
		normalImageView = (ImageView) rootView.findViewById(R.id.human_inductor_normal_imageview);
		
		temp1TextView = (TextView) rootView.findViewById(R.id.human_inductor_select_less_temhum);
		temp2TextView = (TextView) rootView.findViewById(R.id.human_inductor_select_more_temhum);
		hum1TextView = (TextView) rootView.findViewById(R.id.human_inductor_select_less_humidity);
		hum2TextView = (TextView) rootView.findViewById(R.id.human_inductor_select_more_humidity);
		lessLayout = (LinearLayout) rootView.findViewById(R.id.task_manager_choose_level_less);
		moreLayout = (LinearLayout) rootView.findViewById(R.id.task_manager_choose_level_more);
		lessImageView = (ImageView) rootView.findViewById(R.id.task_manager_select_less);
		moreImageView = (ImageView) rootView.findViewById(R.id.task_manager_select_more);
		lessLevels = (TextView) rootView.findViewById(R.id.task_manager_level_text_less);
		moreLevels = (TextView) rootView.findViewById(R.id.task_manager_level_text_more);
		lessFrameLayout = (FrameLayout) rootView.findViewById(R.id.task_manager_level_values_edit_less_layout);
		moreFrameLayout = (FrameLayout) rootView.findViewById(R.id.task_manager_level_values_edit_more_layout);
		valueLessEditText = (EditText) rootView.findViewById(R.id.task_manager_level_values_edit_less);
		valueMoreEditText = (EditText) rootView.findViewById(R.id.task_manager_level_values_edit_more);
		TextView valueLessText = (TextView) rootView.findViewById(R.id.task_manager_level_values_edit_less_unit);
		TextView valueMoreText = (TextView) rootView.findViewById(R.id.task_manager_level_values_edit_more_unit);
		valueLessText.setText("LUX");
		valueMoreText.setText("LUX");
		valueLessEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
		valueMoreEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(5)});
		manager= new SingleChooseManager(R.drawable.task_manager_select,R.drawable.task_manager_no_select);
		manager.addImageView(greaterTempImageView);
		manager.addImageView(lessTempImageView);
		manager.addImageView(greaterHumImageView);
		manager.addImageView(lessHumImageView);
		manager.addImageView(alarmImageView);
		manager.addImageView(normalImageView);
		manager.addImageView(lessImageView);
		manager.addImageView(moreImageView);
		OnClickListener checkClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				manager.setChecked(v.getId());
				if(v.getId() == alarmImageView.getId()){
					selectAlarmLayout.setVisibility(View.VISIBLE);
					selectAlarmImageView.setSelected(true);
				}else if(v.getId() == normalImageView.getId()){
					selectAlarmLayout.setVisibility(View.GONE);
				}else if(manager.getCheckID() == lessImageView.getId()){
					describe = "LUX";
					values = "<" + 10;
					ep = WulianDevice.EP_17;
					epType = "D3";
					if(lessFrameLayout.getVisibility() == View.VISIBLE && !StringUtil.isNullOrEmpty(valueLessEditText.getText().toString())){
						values = "<" + valueLessEditText.getText().toString();
						iniPopupWidow(v,lessLevels,lessFrameLayout,lessLayout);
					}
				}else if(manager.getCheckID() == moreImageView.getId()) {
					describe = "LUX";
					values = ">" + 10;
					ep = WulianDevice.EP_17;
					epType = "D3";
					if(moreFrameLayout.getVisibility() == View.VISIBLE && !StringUtil.isNullOrEmpty(valueMoreEditText.getText().toString())){
						values = ">" + valueMoreEditText.getText().toString();
						iniPopupWidow(v, moreLevels,moreFrameLayout,moreLayout);
					}
				}
				closeSoftKeyBoard(mContext);
			}
		};
		OnClickListener selectClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(v.getId() == temp1TextView.getId()){
					manager.setChecked(greaterTempImageView.getId());
					String position = getSettingPosition(temp1TextView,DERAULT_VALUES,tempValus);
					createSelectValuesDialog(temp1TextView,tempValus,"\u2103",position);
				}else if(v.getId() == temp2TextView.getId()){
					manager.setChecked(lessTempImageView.getId());
					String position = getSettingPosition(temp2TextView,DERAULT_VALUES,tempValus);
					createSelectValuesDialog(temp2TextView,tempValus,"\u2103",position);
				}else if(v.getId() == hum1TextView.getId()){
					manager.setChecked(greaterHumImageView.getId());
					String position = getSettingPosition(hum1TextView,DERAULT_VALUES,humValus);
					createSelectValuesDialog(hum1TextView,humValus,"%",position);
				}else if(v.getId() == hum2TextView.getId()){
					manager.setChecked(lessHumImageView.getId());
					String position = getSettingPosition(hum2TextView,DERAULT_VALUES,humValus);
					createSelectValuesDialog(hum2TextView,humValus,"%",position);
				}
				closeSoftKeyBoard(mContext);
			}

			private String getSettingPosition(TextView text,String position,String[] valuesStr) {
				for(int i = 0; i < valuesStr.length; i++){
					if(StringUtil.equals(text.getText(), valuesStr[i])){
						position = i + "";
						break;
					}
				}
				return position;
			}
		};
		greaterTempImageView.setOnClickListener(checkClickListener);
		lessTempImageView.setOnClickListener(checkClickListener);
		greaterHumImageView.setOnClickListener(checkClickListener);
		lessHumImageView.setOnClickListener(checkClickListener);
		alarmImageView.setOnClickListener(checkClickListener);
		normalImageView.setOnClickListener(checkClickListener);
		temp1TextView.setOnClickListener(selectClickListener);
		temp2TextView.setOnClickListener(selectClickListener);
		hum1TextView.setOnClickListener(selectClickListener);
		hum2TextView.setOnClickListener(selectClickListener);
		lessImageView.setOnClickListener(checkClickListener);
		moreImageView.setOnClickListener(checkClickListener);
		selectAlarmImageView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(selectAlarmImageView.isSelected()){
					selectAlarmImageView.setSelected(false);
				}else{
					selectAlarmImageView.setSelected(true);
				}
			}
		});
        ensureButton = (Button) rootView.findViewById(R.id.house_keeper_task_scene_ensure);
        ensureButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				if(manager.getCheckID() == alarmImageView.getId()){
					if(selectAlarmImageView.isSelected()){
						values = SPLIT_MORE_EQUEAL + alarmValues;
					}else{
						values = SPLIT_MORE_EQUEAL + alarmValues + "$";
					}
					describe = "";
					ep = WulianDevice.EP_14;
					epType = ConstUtil.DEV_TYPE_FROM_GW_HUMANINDUCTOR;
				}else if(manager.getCheckID() == normalImageView.getId()){
					values = SPLIT_MORE_EQUEAL + normalValues;
					describe = "";
					ep = WulianDevice.EP_14;
					epType = ConstUtil.DEV_TYPE_FROM_GW_HUMANINDUCTOR;
				}else{
					if(manager.getCheckID() == greaterTempImageView.getId()){
						values = SPLIT_MORE + temp1TextView.getText().toString();
						describe = "\u2103";
						ep = WulianDevice.EP_15;
						epType = ConstUtil.DEV_TYPE_FROM_GW_TEMPERATURE;
					}else if(manager.getCheckID() == lessTempImageView.getId()){
						values = SPLIT_MORE_LESS + temp2TextView.getText().toString();
						describe = "\u2103";
						ep = WulianDevice.EP_15;
						epType = ConstUtil.DEV_TYPE_FROM_GW_TEMPERATURE;
					}else if(manager.getCheckID() == greaterHumImageView.getId()){
						values = SPLIT_MORE + hum1TextView.getText().toString();
						describe = "%";
						ep = WulianDevice.EP_16;
						epType = ConstUtil.DEV_TYPE_FROM_GW_HUMIDITY;
					}else if(manager.getCheckID() == lessHumImageView.getId()){
						values = SPLIT_MORE_LESS + hum2TextView.getText().toString();
						describe = "%";
						ep = WulianDevice.EP_16;
						epType = ConstUtil.DEV_TYPE_FROM_GW_HUMIDITY;
					}
				}
				
				if(lessFrameLayout.getVisibility() == View.VISIBLE && StringUtil.isNullOrEmpty(valueLessEditText.getText().toString())){
					WLToast.showToast(mContext, mContext.getResources().getString(R.string.house_rule_add_new_action_no_edit), WLToast.TOAST_SHORT);
				}else if(moreFrameLayout.getVisibility() == View.VISIBLE && StringUtil.isNullOrEmpty(valueMoreEditText.getText().toString())){
					WLToast.showToast(mContext, mContext.getResources().getString(R.string.house_rule_add_new_action_no_edit), WLToast.TOAST_SHORT);
				}else{
					Activity activity = (Activity) mContext;
					activity.finish();
					HouseKeeperSelectSensorDeviceDataFragment.fireSelectDeviceDataListener(ep, epType, values,describe);
				}
				
			}
		});
        
		OnClickListener ShowLessPopuClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				describe = "LUX";
				iniPopupWidow(v,lessLevels,lessFrameLayout,lessLayout);
				manager.setChecked(lessImageView.getId());
				closeSoftKeyBoard(mContext);
			}

		};
		OnClickListener ShowMorePopuClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				describe = "LUX";
				iniPopupWidow(v, moreLevels,moreFrameLayout,moreLayout);
				manager.setChecked(moreImageView.getId());
				closeSoftKeyBoard(mContext);
			}
		};
		lessLayout.setOnClickListener(ShowLessPopuClickListener);
		moreLayout.setOnClickListener(ShowMorePopuClickListener);
		valueLessEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				manager.setChecked(lessImageView.getId());
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				values = "<" + valueLessEditText.getText().toString();
			}
		});
		valueMoreEditText.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
			
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				manager.setChecked(moreImageView.getId());
			}
			
			@Override
			public void afterTextChanged(Editable s) {
				values = ">" + valueMoreEditText.getText().toString();
			}
		});
		sensorLayout.setFocusable(true);
		sensorLayout.setFocusableInTouchMode(true);
		sensorLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				closeSoftKeyBoard(mContext);
				return true;
			}
		});
	}

	private void closeSoftKeyBoard(Context mContext) {
		valueLessEditText.clearFocus();
		valueMoreEditText.clearFocus();
		// 如果输入法显示,就隐藏
		if (InputMethodUtils.isShow(mContext)) {
			InputMethodUtils.hide(mContext);
		}
	}
	
	private void createSelectValuesDialog(final TextView valuesText, final String[] valuesStr,String unit,String pos) {
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		builder.setTitle(mContext.getResources().getString(R.string.house_rule_task_sensor_device_select_values))
		.setContentView(createSelectView(valuesStr,unit,pos))
		.setNegativeButton(mContext.getResources().getString(R.string.cancel))
		.setPositiveButton(mContext.getResources().getString(R.string.common_ok))
		.setListener(new MessageListener() {
			
			@Override
			public void onClickPositive(View contentViewLayout) {
				valuesText.setText(valuesStr[valuesWheel.getSelectedItemPosition()]);
			}
			
			@Override
			public void onClickNegative(View contentViewLayout) {
				
			}
		});
		dialog = builder.create();
		dialog.show();
	}
	private View createSelectView(String[] valuesStr,String unit,String pos){
		View selectValuesView = inflater.inflate(R.layout.task_manager_human_inductor_select_values_dialog, null);
		valuesWheel = (WheelView) selectValuesView.findViewById(R.id.sensor_device_choose_wheel_values);
		TextView unitText = (TextView) selectValuesView.findViewById(R.id.sensor_device_choose_wheel_values_unit);
		unitText.setText(unit);
		valuesWheel.setScrollCycle(true);
		NumberAdapter valuesAdapter = new NumberAdapter(valuesStr);
		valuesWheel.setAdapter(valuesAdapter);
		valuesWheel.setSelection(StringUtil.toInteger(pos), true);
		((WheelTextView)valuesWheel.getSelectedView()).setTextSize(30);
		valuesWheel.setOnItemSelectedListener(new OnItemSelectedListener() {

			@Override
			public void onItemSelected(TosAdapterView<?> parent, View view,
					int position, long id) {
				((WheelTextView)view).setTextSize(30);
				  int index = StringUtil.toInteger(view.getTag().toString());
				  int count = parent.getChildCount();
				  if(index < count-1){
	                ((WheelTextView)parent.getChildAt(index+1)).setTextSize(20);
				  }
				  if(index>0){
	                ((WheelTextView)parent.getChildAt(index-1)).setTextSize(20);
				  }
//				  if(valuesWheel.getId() == parent.getId()){
//					  manager.setChecked(greaterTempImageView.getId());
//				  }else if(temp2.getId() == parent.getId()){
//					  manager.setChecked(lessTempImageView.getId());
//				  }else if(hum1.getId() == parent.getId()){
//					  manager.setChecked(greaterHumImageView.getId());
//				  }else if(hum2.getId() == parent.getId()){
//					  manager.setChecked(lessHumImageView.getId());
//				  }
			}

			@Override
			public void onNothingSelected(TosAdapterView<?> parent) {
				// TODO Auto-generated method stub
				
			}
		});
		valuesWheel.setUnselectedAlpha(0.5f);
		
		return selectValuesView;
	}
	private void initAlarmView(String value,String describe,SingleChooseManager manager) {
		manager.setChecked(greaterTempImageView.getId());
		ep = WulianDevice.EP_15;
		epType = ConstUtil.DEV_TYPE_FROM_GW_TEMPERATURE;
		temp1TextView.setText(tempValus[StringUtil.toInteger(DERAULT_VALUES)]);
		temp2TextView.setText(tempValus[StringUtil.toInteger(DERAULT_VALUES)]);
		hum1TextView.setText(humValus[StringUtil.toInteger(DERAULT_VALUES)]);
		hum2TextView.setText(humValus[StringUtil.toInteger(DERAULT_VALUES)]);
		values = SPLIT_MORE + tempValus[StringUtil.toInteger(DERAULT_VALUES)];
	}

	
	public void setmAlarmAndNormal(String alarm,String normal){
		this.alarmValues = alarm;
		this.normalValues = normal;
	}
	public void setmAlarmDeviceValues(String value,String describe){
		this.values = value;
		this.describe = describe;
		initAlarmView(value,describe,manager);
	}

//	public AutoConditionInfo getAutoConditionInfo() {
//		if(autoConditionInfo == null){
//			autoConditionInfo = new AutoConditionInfo();
//		}
//		return autoConditionInfo;
//	}
	
	private class NumberAdapter extends BaseAdapter{

		int mHeight = 50;
        String[] mData = null;
        public NumberAdapter(String[] data) {
            mHeight = (int) DisplayUtil.dip2Pix(mContext, mHeight);
            this.mData = data;
        }
        
		@Override
		public int getCount() {
			 return (null != mData) ? mData.length : 0;
		}

		@Override
		public String getItem(int arg0) {
			return mData[arg0];
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			 WheelTextView textView = null;

	            if (null == convertView) {
	                convertView = new WheelTextView(mContext);
	                convertView.setLayoutParams(new TosGallery.LayoutParams(-1, mHeight));
	                textView = (WheelTextView) convertView;
	                textView.setTextSize(20);
	                textView.setGravity(Gravity.CENTER);
	                textView.setTextColor(mContext.getResources().getColor(R.color.black));
	            }
	            
	            
	            String text = mData[position];
	            if (null == textView) {
	                textView = (WheelTextView) convertView;
	            }
	            
	            textView.setText(text);
	            return convertView;
		}
		
	}
	
	private void iniPopupWidow(final View v, final TextView text,final FrameLayout frameLayout,final LinearLayout layout) {
		final SensorMenuList menuList = new SensorMenuList(mContext,
				R.string.house_rule_condition_device_select_degree);
		SensorMenuItem itemDarker = new SensorMenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext.getResources().getString(R.string.house_rule_condition_device_pm_dark));
			}

			@Override
			public void doSomething() {
				text.setText(mContext.getResources().getString(R.string.house_rule_condition_device_pm_dark));
				if(v.getId() == lessLayout.getId()){
					values = "<" + 10;
				}else{
					values = ">" + 10;
				}
				describe = "LUX";
				ep = WulianDevice.EP_17;
				epType = "D3";
				frameLayout.setVisibility(View.GONE);
				layout.setVisibility(View.VISIBLE);
				menuList.dismiss();
			}
		};
		SensorMenuItem itemWeaker = new SensorMenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext.getResources().getString(R.string.house_rule_condition_device_pm_weak_light));
			}

			@Override
			public void doSomething() {
				text.setText(mContext.getResources().getString(R.string.house_rule_condition_device_pm_weak_light));
				if(v.getId() == lessLayout.getId()){
					values = "<" + 50;
				}else{
					values = ">" + 50;
				}
				describe = "LUX";
				ep = WulianDevice.EP_17;
				epType = "D3";
				frameLayout.setVisibility(View.GONE);
				layout.setVisibility(View.VISIBLE);
				menuList.dismiss();
			}
		};
		SensorMenuItem itemComfortable = new SensorMenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext.getResources().getString(R.string.device_link_task_sensor_degree_comfortable));
			}

			@Override
			public void doSomething() {
				text.setText(mContext.getResources().getString(R.string.device_link_task_sensor_degree_comfortable));
				if(v.getId() == lessLayout.getId()){
					values = "<" + 150;
				}else{
					values = ">" + 150;
				}
				describe = "LUX";
				ep = WulianDevice.EP_17;
				epType = "D3";
				frameLayout.setVisibility(View.GONE);
				layout.setVisibility(View.VISIBLE);
				menuList.dismiss();
			}
		};

		SensorMenuItem itemBright = new SensorMenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext.getResources().getString(R.string.house_rule_condition_device_pm_weak_bright));
				// titleTextView.setText(mContext.getResources().getString(
				// R.string.scene_info_delete_scene));
			}

			@Override
			public void doSomething() {
				text.setText(mContext.getResources().getString(R.string.house_rule_condition_device_pm_weak_bright));
				if(v.getId() == lessLayout.getId()){
					values = "<" + 750;
				}else{
					values = ">" + 750;
				}
				describe = "LUX";
				ep = WulianDevice.EP_17;
				epType = "D3";
				frameLayout.setVisibility(View.GONE);
				layout.setVisibility(View.VISIBLE);
				menuList.dismiss();
			}
		};
		SensorMenuItem itemLighter = new SensorMenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext.getResources().getString(R.string.house_rule_condition_device_pm_lighter));
				// titleTextView.setText(mContext.getResources().getString(
				// R.string.scene_info_delete_scene));
			}

			@Override
			public void doSomething() {
				text.setText(mContext.getResources().getString(R.string.house_rule_condition_device_pm_lighter));
				frameLayout.setVisibility(View.GONE);
				layout.setVisibility(View.VISIBLE);
				if(v.getId() == lessLayout.getId()){
					values = "<" + 900;
				}else{
					values = ">" + 900;
				}
				describe = "LUX";
				ep = WulianDevice.EP_17;
				epType = "D3";
				menuList.dismiss();
			}
		};
		 SensorMenuItem cusromlItem= new SensorMenuItem(mContext) {
		
		 @Override
		 public void initSystemState() {
			 titleTextView.setText(mContext.getResources().getString(R.string.scene_icon_new));
		 }
		
		 @Override
		 public void doSomething() {
			 describe = "LUX";
			 ep = WulianDevice.EP_17;
			 epType = "D3";
			 layout.setVisibility(View.GONE);
			 frameLayout.setVisibility(View.VISIBLE);
			 menuList.dismiss();
		 }
		 };
		ArrayList<SensorMenuItem> menus = new ArrayList<SensorMenuList.SensorMenuItem>();
		menus.add(itemDarker);
		menus.add(itemWeaker);
		menus.add(itemComfortable);
		menus.add(itemBright);
		menus.add(itemLighter);
		menus.add(cusromlItem);
		menuList.addMenu(menus);
		menuList.show(v);
	}
	public View getView(){
		return rootView;
	}



	
}
