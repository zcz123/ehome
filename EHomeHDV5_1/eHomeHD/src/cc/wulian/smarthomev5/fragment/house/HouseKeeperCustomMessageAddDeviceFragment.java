package cc.wulian.smarthomev5.fragment.house;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.ToggleButton;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.house.HouseKeeperCustomMessageSelectDeviceActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperConditionSelectDeviceFragment.ConditionDeviceListener;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperCustomMessageSelectDeviceFragment.OnSelectListener;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;

import com.yuantuo.customview.ui.WLToast;

/**
 * 准备添加发送消息的设备的页面
 * 
 * @author Administrator
 * 
 */
public class HouseKeeperCustomMessageAddDeviceFragment extends WulianFragment {

	/**
	 * 设备是否已经选择，如果已选择，右上角ActionBar显示保存按钮
	 */
	public static boolean isEditChange = false;

	public static AutoProgramTaskInfo autoProgramTaskInfo;

	private LinearLayout selectDeviceLayout;

	private TextView selectDeviceTextView;

	private TextView deviceTextView;

	private EditText customMessageEditText;

	private FrameLayout selectDeviceTvLayout;

	private TextView customMessageHintText;

	private LinearLayout customMessageToggleLayout;

	private static WulianDevice mDevice;

	private boolean isBack = false; // 是否是回调

	public static final String PROGRAM_NAME = "programName";

	private ToggleButton customMessageToggleButton;

	private TextView customMessageTypeTextView;

	private LinearLayout customMessageEditLayout;

	// 规则名称
	private String mProgramName;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBarChange();

	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

		return inflater
				.inflate(R.layout.task_manager_fragment_add_device, null);
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		selectDeviceLayout = (LinearLayout) view
				.findViewById(R.id.house_keeper_task_select_device_layout);
		selectDeviceTextView = (TextView) view
				.findViewById(R.id.house_keeper_task_select_send_message_device_textview);
		deviceTextView = (TextView) view
				.findViewById(R.id.house_keeper_send_message_device_textview);
		customMessageEditText = (EditText) view
				.findViewById(R.id.house_keeper_task_custom_message_edittext);
		selectDeviceTvLayout = (FrameLayout) view
				.findViewById(R.id.house_keeper_task_select_send_message_device_layout);
		customMessageHintText = (TextView) view
				.findViewById(R.id.house_keeper_task_custom_message_textview);
		customMessageEditLayout = (LinearLayout) view
				.findViewById(R.id.house_keeper_task_custom_message_edit_layout);

		customMessageToggleLayout = (LinearLayout) view
				.findViewById(R.id.house_keeper_custom_message_toggle_layout);
		customMessageToggleButton = (ToggleButton) view
				.findViewById(R.id.house_keeper_custom_message_toggle_button);
		customMessageTypeTextView = (TextView) view
				.findViewById(R.id.house_keeper_custom_message_type_textview);

		// ToggleButton按钮切换
		customMessageToggleButton
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton arg0,
							boolean arg1) {
						if (arg1) {
							customMessageTypeTextView
									.setText(R.string.home_warning_message);
						} else {
							customMessageTypeTextView
									.setText(R.string.home_return_message_titel);
						}
					}
				});

		customMessageEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int arg1, int arg2,
					int arg3) {
				if (s.length() == 0) {
					if (!TextUtils.isEmpty(mProgramName)) {
						customMessageEditText.setHint(getResources().getString(
								R.string.nav_house_title)
								+ "\""
								+ mProgramName
								+ "\""
								+ getResources().getString(
										R.string.house_rule_is_carried_out));

					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void afterTextChanged(Editable arg0) {

			}
		});

		// customMessageEditText.setOnEditorActionListener(l)
		selectDeviceTvLayout.setOnClickListener(messageClickListener);
		deviceTextView.setOnClickListener(messageClickListener);
		initCustomMessageView();
	}

	@Override
	public void onResume() {
		super.onResume();
		initCustomMessageView();
	}

	/**
	 * 初始化添加设备View
	 */
	private void initCustomMessageView() {

		checkAutoProgramTaskInfo(autoProgramTaskInfo);

		Bundle bundle = getActivity().getIntent().getExtras();
		if (!StringUtil.isNullOrEmpty(bundle.getString(PROGRAM_NAME))) {
			mProgramName = bundle.getString(PROGRAM_NAME);
		}

		if (mDevice == null) {
			selectDeviceTvLayout.setVisibility(View.VISIBLE);
			deviceTextView.setVisibility(View.GONE);

			customMessageEditLayout.setVisibility(View.GONE);
			customMessageHintText.setVisibility(View.GONE);
			customMessageToggleLayout.setVisibility(View.GONE);

		} else {
			selectDeviceTvLayout.setVisibility(View.GONE);
			deviceTextView.setVisibility(View.VISIBLE);

			deviceTextView.setText(getDeviceName(mDevice));
			customMessageEditLayout.setVisibility(View.VISIBLE);

			List<AutoActionInfo> actionList = autoProgramTaskInfo
					.getActionList();

			for (AutoActionInfo autoActionInfo : actionList) {
				if ("3".equals(autoActionInfo.getType())) {
					String text = autoActionInfo.getEpData().substring(1);
					mAutoInfo = autoActionInfo;
					int first = text.indexOf(getResources().getString(R.string.house_rule_detect));
					if(first>=0){
						text = text.substring(first+3);
					}

					customMessageEditText.setText(text);
					customMessageEditText.setSelection(text.length());
				}
			}

			if (StringUtil.isNullOrEmpty(customMessageEditText.getText()
					.toString().trim())) {
				if (StringUtil.isNullOrEmpty(mProgramName)) {

					customMessageEditText.setHint("");

				} else {
					customMessageEditText.setHint(getResources().getString(
							R.string.nav_house_title)
							+ "\""
							+ mProgramName
							+ "\""
							+ getResources().getString(
									R.string.house_rule_is_carried_out));
				}
			}

			customMessageHintText.setVisibility(View.VISIBLE);
			customMessageToggleLayout.setVisibility(View.VISIBLE);

			initBar();
		}

		// 切换选择的设备
		HouseKeeperCustomMessageSelectDeviceFragment
				.setOnSelectListener(new OnSelectListener() {

					@Override
					public void OnSelect(WulianDevice device) {
						mDevice = device;
						isBack = true;
					}
				});

	}

	/**
	 * 获取设备名称
	 * 
	 * @param mDevice
	 */
	private String getDeviceName(WulianDevice mDevice) {
		if (!StringUtil.isNullOrEmpty(mDevice.getDeviceName())) {
			return mDevice.getDeviceName();
		} else {
			return mDevice.getDefaultDeviceName();
		}
	}

	private void checkAutoProgramTaskInfo(
			AutoProgramTaskInfo autoProgramTaskInfo) {
		// 是否为选择设备页面的回调
		if (isBack) {
			isBack = false;
		} else {
			mDevice = null;
			List<AutoActionInfo> actionList = autoProgramTaskInfo
					.getActionList();
			for (AutoActionInfo actionInfo : actionList) {
				if ("3".equals(actionInfo.getType())) {
					String devId = actionInfo.getObject().split(">")[0];
					mDevice = DeviceCache.getInstance(mActivity).getDeviceByID(
							mActivity, autoProgramTaskInfo.getGwID(), devId);
					String epData = actionInfo.getEpData();
					String messageType = epData.substring(0, 1);

					if ("M".equals(messageType)) {
						customMessageTypeTextView
								.setText(R.string.home_return_message_titel);
						customMessageToggleButton.setChecked(false);
					} else if ("W".equals(messageType)) {
						customMessageTypeTextView
								.setText(R.string.home_warning_message);
						customMessageToggleButton.setChecked(true);
					}
				}
			}
		}

	}

	/**
	 * 点击跳转添加设备
	 */
	private OnClickListener messageClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			if (v == selectDeviceTvLayout) {
				HouseKeeperCustomMessageSelectDeviceFragment.autoProgramTaskInfo = autoProgramTaskInfo;
				mActivity
						.JumpTo(HouseKeeperCustomMessageSelectDeviceActivity.class);
			} else if (v == deviceTextView) {
				HouseKeeperCustomMessageSelectDeviceFragment.autoProgramTaskInfo = autoProgramTaskInfo;
				mActivity
						.JumpTo(HouseKeeperCustomMessageSelectDeviceActivity.class);
			}
		}

	};

	private AutoActionInfo mAutoInfo;

	/**
	 * 第一次显示此界面或没有编辑任何内容时的ActionBar
	 */
	private void initBarChange() {
		mActivity.resetActionMenu();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(false);
		getSupportActionBar().setTitle(R.string.house_rule_custom_message);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setLeftIconClickListener(
				new OnLeftIconClickListener() {

					@Override
					public void onClick(View v) {
						mActivity.finish();
					}
				});
	}

	/**
	 * 选择过发送消息的设备后，更改右侧按钮
	 */
	private void initBar() {
		// 初始化ActionBar
		mActivity.resetActionMenu();

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

		getSupportActionBar().setTitle(R.string.house_rule_custom_message);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(false);
		getSupportActionBar().setRightGrayIconText(
				getResources().getString(R.string.set_save),
				getResources().getColor(R.color.white));

		// 保存自定义消息的编辑
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {
						// EditText不能为空

						List<AutoActionInfo> actionList = autoProgramTaskInfo
								.getActionList();
						List<AutoActionInfo> copyActionList = new ArrayList<AutoActionInfo>();
						copyActionList.addAll(actionList);
						// 清除原来的自定义消息
						for (AutoActionInfo autoActionInfo : copyActionList) {
							if ("3".equals(autoActionInfo.getType())) {
								actionList.remove(autoActionInfo);
							}
						}
						copyActionList.clear();
						copyActionList = null;

						// 将消息拼接并返回
						AutoActionInfo autoActionInfo = new AutoActionInfo();

						DeviceEPInfo devEPInfo = mDevice.getDeviceInfo()
								.getDevEPInfo();
						autoActionInfo.setSortNum(autoProgramTaskInfo
								.getActionList().size() + "");
						autoActionInfo.setType(3 + "");
						autoActionInfo.setObject(mDevice.getDeviceID() + ">"
								+ mDevice.getDeviceType() + ">"
								+ devEPInfo.getEp() + ">"
								+ devEPInfo.getEpType());
						String epData = "";

						Editable edTv = customMessageEditText.getText();
						CharSequence csHint = customMessageEditText.getHint();

						//getText为空且getHint不为空，用hint
						if (TextUtils.isEmpty(edTv)
								&& !TextUtils.isEmpty(csHint)) {
							epData = csHint.toString().trim();
						}

						//getText和getHint均为空,看规则名称
						if (TextUtils.isEmpty(edTv)
								&& TextUtils.isEmpty(csHint)) {
							//规则名称也为空,返回
							if (TextUtils.isEmpty(mProgramName)) {
								WLToast.showToast(
										mActivity,
										getResources()
												.getString(
														R.string.house_rule_message_cant_be_null),
										WLToast.TOAST_SHORT);
								return;
								
							} else {	//规则名称不为空，用规则名称
								customMessageEditText
										.setHint(getResources().getString(
												R.string.nav_house_title)
												+ "\""
												+ mProgramName
												+ "\""
												+ getResources()
														.getString(
																R.string.house_rule_is_carried_out));

								epData = customMessageEditText.getHint()
												.toString().trim();
							}
						}

						//getText不为空，用getText
						if (!TextUtils.isEmpty(edTv)) {
							epData = edTv.toString().trim();
						}


						// 根据按钮的状态判断消息类型
						if (customMessageToggleButton.isChecked()) {
							epData = "W" + epData;
						} else {
							epData = "M" + epData;
						}
						autoActionInfo.setEpData(epData);
						autoProgramTaskInfo.addActionTask(autoActionInfo);
						HouseKeeperAddRulesFragment.isEditChange=true;
						mActivity.finish();
					}
				});
	}

}
