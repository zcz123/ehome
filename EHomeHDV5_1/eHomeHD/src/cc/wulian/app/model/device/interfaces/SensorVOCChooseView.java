package cc.wulian.app.model.device.interfaces;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperSelectSensorDeviceDataFragment;
import cc.wulian.smarthomev5.tools.SensorMenuList;
import cc.wulian.smarthomev5.tools.SensorMenuList.SensorMenuItem;
import cc.wulian.smarthomev5.tools.SingleChooseManager;
import cc.wulian.smarthomev5.utils.InputMethodUtils;

import com.yuantuo.customview.ui.WLToast;

public class SensorVOCChooseView {

	private Context mContext;
	private LayoutInflater inflater;
	private LinearLayout rootView;

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

	private String values;
	private String describe;
	private String ep;
	private String epType;

	private SingleChooseManager manager;
	private Resources mResources;

	public SensorVOCChooseView(Context context) {
		this.mContext = context;
		inflater = LayoutInflater.from(context);
		mResources = context.getResources();
		rootView = (LinearLayout) inflater.inflate(
				R.layout.task_manager_common_sensor_view, null);

		sensorLayout = (LinearLayout) rootView
				.findViewById(R.id.task_sensor_device_choose_layout);
		lessLayout = (LinearLayout) rootView
				.findViewById(R.id.task_manager_choose_level_less);
		moreLayout = (LinearLayout) rootView
				.findViewById(R.id.task_manager_choose_level_more);
		lessImageView = (ImageView) rootView
				.findViewById(R.id.task_manager_select_less);
		moreImageView = (ImageView) rootView
				.findViewById(R.id.task_manager_select_more);
		lessLevels = (TextView) rootView
				.findViewById(R.id.task_manager_level_text_less);
		moreLevels = (TextView) rootView
				.findViewById(R.id.task_manager_level_text_more);
		lessFrameLayout = (FrameLayout) rootView
				.findViewById(R.id.task_manager_level_values_edit_less_layout);
		moreFrameLayout = (FrameLayout) rootView
				.findViewById(R.id.task_manager_level_values_edit_more_layout);
		valueLessEditText = (EditText) rootView
				.findViewById(R.id.task_manager_level_values_edit_less);
		valueMoreEditText = (EditText) rootView
				.findViewById(R.id.task_manager_level_values_edit_more);
		TextView valueLessText = (TextView) rootView
				.findViewById(R.id.task_manager_level_values_edit_less_unit);
		TextView valueMoreText = (TextView) rootView
				.findViewById(R.id.task_manager_level_values_edit_more_unit);
		valueLessText.setText("PPB");
		valueMoreText.setText("PPB");
		valueLessEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
		valueMoreEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(3)});
		manager = new SingleChooseManager(R.drawable.task_manager_select,R.drawable.task_manager_no_select);
		manager.addImageView(lessImageView);
		manager.addImageView(moreImageView);
		OnClickListener checkClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				manager.setChecked(v.getId());
				if(manager.getCheckID() == lessImageView.getId()){
					describe = "PPB";
					values = "<" + 50;
					if(lessFrameLayout.getVisibility() == View.VISIBLE && !StringUtil.isNullOrEmpty(valueLessEditText.getText().toString())){
						values = "<" + valueLessEditText.getText().toString();
						iniPopupWidow(v,lessLevels,lessFrameLayout,lessLayout);
					}
				}else if(manager.getCheckID() == moreImageView.getId()) {
					describe = "PPB";
					values = ">" + 50;
					if(moreFrameLayout.getVisibility() == View.VISIBLE && !StringUtil.isNullOrEmpty(valueMoreEditText.getText().toString())){
						values = ">" + valueMoreEditText.getText().toString();
						iniPopupWidow(v, moreLevels,moreFrameLayout,moreLayout);
					}
				}
				closeSoftKeyBoard(mContext);
			}
		};
		lessImageView.setOnClickListener(checkClickListener);
		moreImageView.setOnClickListener(checkClickListener);
		OnClickListener ShowLessPopuClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				describe = "PPB";
				iniPopupWidow(v, lessLevels,lessFrameLayout,lessLayout);
				manager.setChecked(lessImageView.getId());
				ep = WulianDevice.EP_14;
				epType = ConstUtil.DEV_TYPE_FROM_GW_DREAMFLOWER_VOC;
				closeSoftKeyBoard(mContext);
			}

		};
		OnClickListener ShowMorePopuClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				describe = "PPB";
				iniPopupWidow(v, moreLevels,moreFrameLayout,moreLayout);
				manager.setChecked(moreImageView.getId());
				ep = WulianDevice.EP_14;
				epType = ConstUtil.DEV_TYPE_FROM_GW_DREAMFLOWER_VOC;
				closeSoftKeyBoard(mContext);
			}

		};
		lessLayout.setOnClickListener(ShowLessPopuClickListener);
		moreLayout.setOnClickListener(ShowMorePopuClickListener);

		Button ensureButton = (Button) rootView
				.findViewById(R.id.house_keeper_task_sensor_ensure);
		ensureButton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Activity activity = (Activity) mContext;
				activity.finish();
				ep = WulianDevice.EP_14;
				epType = ConstUtil.DEV_TYPE_FROM_GW_DREAMFLOWER_VOC;
				if(lessFrameLayout.getVisibility() == View.VISIBLE && StringUtil.isNullOrEmpty(valueLessEditText.getText().toString())){
					WLToast.showToast(mContext, mContext.getResources().getString(R.string.house_rule_add_new_action_no_edit), WLToast.TOAST_SHORT);
				}else if(moreFrameLayout.getVisibility() == View.VISIBLE && StringUtil.isNullOrEmpty(valueMoreEditText.getText().toString())){
					WLToast.showToast(mContext, mContext.getResources().getString(R.string.house_rule_add_new_action_no_edit), WLToast.TOAST_SHORT);
				}else{
					activity.finish();
					HouseKeeperSelectSensorDeviceDataFragment
					.fireSelectDeviceDataListener(ep,epType,values, describe);
				}
			}
		});
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
	private void iniPopupWidow(final View v, final TextView text,final FrameLayout frameLayout,final LinearLayout layout) {
		final SensorMenuList menuList = new SensorMenuList(mContext,
				R.string.house_rule_condition_device_select_degree);
		SensorMenuItem itemOptimal = new SensorMenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext.getResources().getString(R.string.device_link_task_detection_degree_b));
			}

			@Override
			public void doSomething() {
				text.setText(mContext.getResources().getString(R.string.device_link_task_detection_degree_b));
				if(v.getId() == lessLayout.getId()){
					values = "<" + 50;
				}else{
					values = ">" + 50;
				}
				frameLayout.setVisibility(View.GONE);
				layout.setVisibility(View.VISIBLE);
				menuList.dismiss();
			}
		};
		SensorMenuItem itemGood = new SensorMenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext.getResources().getString(R.string.house_rule_add_new_link_task_voc_slight));
			}

			@Override
			public void doSomething() {
				text.setText(mContext.getResources().getString(R.string.house_rule_add_new_link_task_voc_slight));
				if(v.getId() == lessLayout.getId()){
					values = "<" + 150;
				}else{
					values = ">" + 150;
				}
				frameLayout.setVisibility(View.GONE);
				layout.setVisibility(View.VISIBLE);
				menuList.dismiss();
			}
		};
		SensorMenuItem itemPollutionD = new SensorMenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext.getResources().getString(R.string.house_rule_add_new_link_task_voc_serious));
			}

			@Override
			public void doSomething() {
				text.setText(mContext.getResources().getString(R.string.house_rule_add_new_link_task_voc_serious));
				if(v.getId() == lessLayout.getId()){
					values = "<" + 250;
				}else{
					values = ">" + 250;
				}
				frameLayout.setVisibility(View.GONE);
				layout.setVisibility(View.VISIBLE);
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
				 layout.setVisibility(View.GONE);
				 frameLayout.setVisibility(View.VISIBLE);
				 menuList.dismiss();
			 }
			 };
		ArrayList<SensorMenuItem> menus = new ArrayList<SensorMenuList.SensorMenuItem>();
		menus.add(itemOptimal);
		menus.add(itemGood);
		menus.add(itemPollutionD);
		menus.add(cusromlItem);
		menuList.addMenu(menus);
		menuList.show(v);
	}

	public void setmSensorDeviceValues(String value, String describe) {
		this.values = value;
		this.describe = describe;
		initlightSensorView(value, describe, manager);
	}

	private void initlightSensorView(String value, String describe,
			SingleChooseManager manager) {
		if (!StringUtil.isNullOrEmpty(value)
				&& !StringUtil.isNullOrEmpty(describe)) {
			if (value.length() >= 2) {
				String symbol = value.substring(0, 1);
				int data = StringUtil.toInteger(value.substring(1));
				if (StringUtil.equals(symbol, "<")) {
					textViewChange(data, lessLevels);
					manager.setChecked(lessImageView.getId());
					moreLevels.setText(mContext.getResources().getString(R.string.device_link_task_detection_degree_b));
				} else {
					textViewChange(data, moreLevels);
					manager.setChecked(moreImageView.getId());
					lessLevels.setText(mContext.getResources().getString(R.string.house_rule_add_new_link_task_voc_slight));
				}
			}

		} else {
			manager.setChecked(lessImageView.getId());
			values = "<" + 50;
			describe = "PPB";
			lessLevels.setText(mContext.getResources().getString(R.string.device_link_task_detection_degree_b));
			moreLevels.setText(mContext.getResources().getString(R.string.house_rule_add_new_link_task_voc_slight));
		}
	}

	public View getView() {
		return rootView;
	}

	private void textViewChange(int data, TextView textView) {
		if (data > 0 && data < 100) {
			textView.setText(mContext.getResources().getString(R.string.device_link_task_detection_degree_b));
		} else if (data >= 100 && data < 200) {
			textView.setText(mContext.getResources().getString(R.string.house_rule_add_new_link_task_voc_slight));
		} else if (data >= 200) {
			textView.setText(mContext.getResources().getString(R.string.house_rule_add_new_link_task_voc_serious));
		} 
	}
}
