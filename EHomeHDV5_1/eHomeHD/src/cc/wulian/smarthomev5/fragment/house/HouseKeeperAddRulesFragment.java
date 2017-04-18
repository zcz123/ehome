package cc.wulian.smarthomev5.fragment.house;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.house.HouseKeeperActionTaskActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperConditionActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperCustomMessageActivity;
import cc.wulian.smarthomev5.databases.entitys.AutoTask;
import cc.wulian.smarthomev5.entity.GuideEntity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperActionTaskFragment.AddLinkTaskListener;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperConditionFragment.ConditionListener;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperCustomMessageItem.OnMessageItemClickListener;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SingleChooseManager;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.GuideUtil;
import cc.wulian.smarthomev5.utils.GuideUtil.GuideCallback;

public class HouseKeeperAddRulesFragment extends WulianFragment {

	public static final String AUTO_TASK_INFO_SERIAL = "auto_task_info_serial";
	public static final String AUTO_TASK_GWID = "auto_task_gwid";
	public static final String AUTO_TASK_PROGRAM_ID = "auto_task_program_id";

	private static final String SPLIT_SPACE = " ";
	private EditText taskNameEditText;
	// private EditText taskDescribe;
	private LinearLayout triggerLayout;
	private LinearLayout chooseConditionLayout;
	private TextView chooseConditionAnd;
	private TextView chooseConditionOr;
	private LinearLayout conditionLayout;
	private LinearLayout triggerItemLayout;
	private TextView conditionText;
	private LinearLayout conditionItemLayout;
	private LinearLayout executeLinkLayout;
	private TextView linkNumberTextView;
	private FrameLayout customMessageLayout;
	private TextView customMessageTextView;
//	private TextView customMessageEditTextView;

	private static final String ADD_AND_UPDATE_TASK_KEY = "add_and_update_task_key";

	private WLDialog dialog;
	private String relative;
	public static AutoProgramTaskInfo autoProgramTaskInfo;

	private List<AutoConditionInfo> triggerList;
	private List<AutoConditionInfo> conditionList = new ArrayList<AutoConditionInfo>();
	private List<AutoActionInfo> actionList;
	private AutoProgramTaskManager autoProgramTaskManager = AutoProgramTaskManager
			.getInstance();
	// private static RuleDetailListener ruleDetailListener;
	private Preference preference = Preference.getPreferences();
	private String mTitleName;
	/**
	 * 当前页面是否已编辑标识，如果编辑了，在退出时要弹出dialog选择保存还是放弃编辑
	 */
	public static boolean isEditChange = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		isEditChange = false;
		final Bundle arg = getActivity().getIntent().getExtras();
		if (arg != null) {
			String gwID = arg.getString(AUTO_TASK_GWID);
			String programID = arg.getString(AUTO_TASK_PROGRAM_ID);
			autoProgramTaskInfo = autoProgramTaskManager.getProgramTaskinfo(
					gwID, programID);
		} else {
			autoProgramTaskInfo = new AutoProgramTaskInfo();
			autoProgramTaskInfo.setGwID(getAccountManger().getmCurrentInfo()
					.getGwID());
		}
		initBarChange();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.task_manager_fragment_editor_item,
				null);
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		taskNameEditText = (EditText) view
				.findViewById(R.id.house_keeper_task_edit_name);
		// taskDescribe = (EditText)
		// view.findViewById(R.id.house_keeper_task_edit_describe);
		triggerLayout = (LinearLayout) view
				.findViewById(R.id.house_keeper_task_trigger_layout);
		chooseConditionLayout = (LinearLayout) view
				.findViewById(R.id.task_manager_condition_layout);
		chooseConditionAnd = (TextView) view
				.findViewById(R.id.task_manager_condition_textview_and);
		chooseConditionOr = (TextView) view
				.findViewById(R.id.task_manager_condition_textview_or);

		conditionLayout = (LinearLayout) view
				.findViewById(R.id.house_keeper_task_condition_layout);
		conditionText = (TextView) view
				.findViewById(R.id.house_keeper_task_condition_limit_text);

		triggerItemLayout = (LinearLayout) view
				.findViewById(R.id.house_keeper_task_trigger_listview);
		conditionItemLayout = (LinearLayout) view
				.findViewById(R.id.house_keeper_task_condition_listview);

		executeLinkLayout = (LinearLayout) view
				.findViewById(R.id.house_keeper_task_link_layout);
		linkNumberTextView = (TextView) view
				.findViewById(R.id.house_keeper_task_link_number);

		customMessageLayout = (FrameLayout) view
				.findViewById(R.id.house_keeper_task_custom_message_layout);
		customMessageTextView = (TextView) view
				.findViewById(R.id.houser_keeper_custom_message_textview);
//		customMessageEditTextView = (TextView) view
//				.findViewById(R.id.house_keeper_custom_message_edit_textview);
		customMessageTextLayout = (LinearLayout) view.findViewById(R.id.house_keeper_task_custom_message_text_layout);

		customMessageLayout.setOnClickListener(conditionClickListener);
		triggerLayout.setOnClickListener(conditionClickListener);
		conditionLayout.setOnClickListener(conditionClickListener);
		executeLinkLayout.setOnClickListener(conditionClickListener);
		taskNameEditText
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(20) });
		taskNameEditText.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence arg0, int start, int before,
					int count) {
				if (autoProgramTaskInfo.getProgramName()!=null&&(!autoProgramTaskInfo.getProgramName().equals(taskNameEditText.getText().toString()))){
					isEditChange = true;
				}
				changeIniBar();
				mTitleName = arg0.toString();
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int start,
					int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable arg0) {
			}
		});
		chooseConditionAnd.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				chooseConditionLayout.setSelected(false);
				List<String> conditionRoot = autoProgramTaskInfo.getRoot()
						.toTreeStrings();
				for (int i = 0; i < conditionRoot.size(); i++) {
					if (StringUtil.equals(conditionRoot.get(i), "or")) {
						autoProgramTaskInfo.updateConditionTree(
								conditionRoot.get(i), "and");
						isEditChange = true;
						// EventBus.getDefault().post(new
						// AutoTaskEvent(AutoTaskEvent.MODIFY));
					}
				}
			}
		});
		chooseConditionOr.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				chooseConditionLayout.setSelected(true);
				List<String> conditionRoot = autoProgramTaskInfo.getRoot()
						.toTreeStrings();
				for (int i = 0; i < conditionRoot.size(); i++) {
					if (StringUtil.equals(conditionRoot.get(i), "and")) {
						autoProgramTaskInfo.updateConditionTree(
								conditionRoot.get(i), "or");
						isEditChange = true;
						// EventBus.getDefault().post(new
						// AutoTaskEvent(AutoTaskEvent.MODIFY));
					}
				}
			}
		});
		// taskDescribe.setFilters(new InputFilter[]{new
		// InputFilter.LengthFilter(500)});
		initAutoProgramItem();

		// 管家规则使用引导开始
		beginGuid(view);
	}

	/**
	 * 初始化限制条件view
	 */
	private void initHouseKeeperConditionItem() {
		// 得到服务器传来的root-->转为List<String>数据
		List<String> condition = new ArrayList<String>();
		if (autoProgramTaskInfo.getRoot() != null) {
			condition.addAll(autoProgramTaskInfo.getRoot().toTreeStrings());
		}
		conditionList.clear();
		if (condition.size() <= 1) {
			chooseConditionLayout.setVisibility(View.INVISIBLE);
		}
		for (String str : condition) {
			if (!str.equals("and") && !str.equals("or")) {
				AutoConditionInfo conditionInfo = new AutoConditionInfo();
				String type = str.substring(0, 1);
				conditionInfo.setType(type);
				String[] splits = str.split(SPLIT_SPACE);
				String object = splits[0].substring(2);
				conditionInfo.setObject(object);
				if (StringUtil.equals(type, "0")
						|| StringUtil.equals(type, "1")) {
					if (StringUtil.equals(splits[1], "in")
							&& splits.length == 3) {
						conditionInfo.setExp(splits[1] + " " + splits[2]);
					} else if (StringUtil.equals(splits[1], "not")
							&& splits.length == 4) {
						conditionInfo.setExp(splits[1] + " " + splits[2] + " "
								+ splits[3]);
					}
				} else if (StringUtil.equals(type, "2") && splits.length == 3) {
					conditionInfo.setExp(splits[1] + splits[2]);
				}
				conditionList.add(conditionInfo);
			} else {
				if (str.equals("and")) {
					relative = "and";
					chooseConditionLayout.setVisibility(View.VISIBLE);
					chooseConditionLayout.setSelected(false);
				} else if (str.equals("or")) {
					relative = "or";
					chooseConditionLayout.setVisibility(View.VISIBLE);
					chooseConditionLayout.setSelected(true);
					// chooseConditionLayout.setBackgroundResource(R.drawable.task_manager_condition_or);
				}
			}
		}
		// conditionList = autoProgramTaskInfo.getConditionList();
		conditionItemLayout.removeAllViews();
		if (conditionList.size() != 0) {
			conditionItemLayout.setVisibility(View.VISIBLE);
			for (int i = 0; i < conditionList.size(); i++) {
				final AutoConditionInfo conditionInfo = conditionList.get(i);
				HouseKeeperConditionItem conditionItem = new HouseKeeperConditionItem(
						mActivity, conditionInfo);
				conditionItemLayout.addView(conditionItem.getView());
				// 左滑删除
				conditionItem.getDeleteButton().setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View arg0) {
								String deleteCondition;
								if (StringUtil.equals(conditionInfo.getType(),
										"0")) { // 场景
									deleteCondition = conditionInfo.getType()
											+ "." + conditionInfo.getObject()
											+ " " + conditionInfo.getExp();
								} else if (StringUtil.equals( // 时间
										conditionInfo.getType(), "1")) {
									deleteCondition = conditionInfo.getType()
											+ "." + conditionInfo.getObject()
											+ " " + conditionInfo.getExp();
								} else { // 设备
									deleteCondition = conditionInfo.getType()
											+ "."
											+ conditionInfo.getObject()
											+ " "
											+ conditionInfo.getExp().substring(
													0, 1)
											+ " "
											+ conditionInfo.getExp().substring(
													1);

									// 获取设备Id，并删除autoProgramTaskInfo中的自定义消息
									String devId = conditionInfo.getObject()
											.split(">")[0];
									deleteCustomMessageFromAutoProgram(devId);

								}
								autoProgramTaskInfo
										.deleteConditionTree(deleteCondition);
								initHouseKeeperConditionItem();
								isEditChange = true;
							}

						});
			}
			executeLinkLayout.setClickable(true);
			executeLinkLayout
					.setBackgroundResource(R.drawable.house_keeper_rule_task_edit_name_img);
		} else {
			if (executeLinkLayout.isClickable()
					&& autoProgramTaskInfo.getTriggerList().size() == 1
					&& StringUtil.equals(autoProgramTaskInfo.getTriggerList()
							.get(0).getType(), "0")) {
				executeLinkLayout.setClickable(false);
				executeLinkLayout
						.setBackgroundResource(R.drawable.house_keeper_rule_task_condition_black_img);
			}
			conditionItemLayout.setVisibility(View.GONE);
		}
		changeIniBar();
	}

	/**
	 * 根据设备Id删除AutoProgramTaskInfo中的自定义消息
	 * 
	 * @param devId
	 */
	private void deleteCustomMessageFromAutoProgram(String devId) {
		List<AutoActionInfo> actionList = autoProgramTaskInfo.getActionList();
		ArrayList<AutoActionInfo> copyActionList = new ArrayList<>();
		copyActionList.addAll(actionList);
		for (AutoActionInfo autoActionInfo : copyActionList) {
			if ("3".equals(autoActionInfo.getType())) {
				if (devId.equals(autoActionInfo.getObject().split(">")[0])) {
					actionList.remove(autoActionInfo);
				}
			}
		}
		copyActionList.clear();
		copyActionList = null;
		// 刷新
		initHouseKeeperCustomMessage();
	}

	/**
	 * 初始化触发view
	 */
	private void initHouseKeeperTriggerItem() {
		triggerList = autoProgramTaskInfo.getTriggerList();
		triggerItemLayout.getParent().requestDisallowInterceptTouchEvent(true);
		triggerItemLayout.removeAllViews();
		if (triggerList.size() != 0) { // 如果有触发事件
			triggerItemLayout.setVisibility(View.VISIBLE);
			// 触发事件为1个，并且触发类型为场景，并且限制条件没有时，将执行任务设为不可点击
			if (triggerList.size() == 1
					&& StringUtil.equals(triggerList.get(0).getType(), "0")
					&& conditionList.size() == 0) {
				executeLinkLayout.setClickable(false);
				executeLinkLayout
						.setBackgroundResource(R.drawable.house_keeper_rule_task_condition_black_img);
			} else {
				executeLinkLayout.setClickable(true);
				executeLinkLayout
						.setBackgroundResource(R.drawable.house_keeper_rule_task_edit_name_img);
			}
			// 遍历触发事件的集合，创建itemview
			for (int i = 0; i < triggerList.size(); i++) {
				final AutoConditionInfo triggerInfo = triggerList.get(i);
				HouseKeeperTriggerItem triggerItem = new HouseKeeperTriggerItem(
						mActivity, triggerInfo);
				triggerItemLayout.addView(triggerItem.getView());
				triggerItem.getDeleteButton().setOnClickListener(
						new OnClickListener() {

							// 滑动后，点击删除按钮时，删除这一事件
							@Override
							public void onClick(View arg0) {
								autoProgramTaskInfo.getTriggerList().remove(
										triggerInfo);

								// 如果删除的是设备类型
								if ("2".equals(triggerInfo.getType())) {
									// 获取设备Id，并删除autoProgramTaskInfo中的自定义消息
									String devId = triggerInfo.getObject()
											.split(">")[0];
									deleteCustomMessageFromAutoProgram(devId);
								}

								initHouseKeeperTriggerItem();
								isEditChange = true;
							}
						});
			}
		} else {
			triggerItemLayout.setVisibility(View.GONE);
			executeLinkLayout.setClickable(true);
			executeLinkLayout
					.setBackgroundResource(R.drawable.house_keeper_rule_task_edit_name_img);
		}
		changeIniBar();
	}

	/**
	 * 初始化执行任务view
	 */
	public void initHouseKeeperLinkTask() {

		// 去除自定义消息的个数
		actionList = autoProgramTaskInfo.getActionList();
		if (actionList.size() != 0) {
			int size = actionList.size();
			for (AutoActionInfo actionInfo : actionList) {
				if (StringUtil.equals(actionInfo.getType(), "3")) {
					size--;
				}
			}
			if (size != 0) {
				linkNumberTextView.setVisibility(View.VISIBLE);
				linkNumberTextView.setText(size
						+ getResources().getString(
								R.string.house_rule_add_new_link_task_number));
			} else {
				linkNumberTextView.setVisibility(View.INVISIBLE);
			}

		} else {
			linkNumberTextView.setVisibility(View.INVISIBLE);
		}
		changeIniBar();
	}

	/**
	 * 初始化自定义消息View
	 */
	private void initHouseKeeperCustomMessage() {
		actionList = autoProgramTaskInfo.getActionList();
		customMessageTextLayout.removeAllViews();
		if (actionList.size() != 0) {

			int count = 0;

			for (AutoActionInfo autoActionInfo : actionList) {
				String type = autoActionInfo.getType();
				if ("3".equals(type)) {
					count++;
					actionInfo = autoActionInfo;

				}
			}

			// 有自定义消息
			if (count != 0 && actionInfo != null) {
				// customMessageTextView.setVisibility(View.INVISIBLE);
				// customMessageEditTextView.setVisibility(View.VISIBLE);
				// customMessageEditTextView.setText(customMessage);
				customMessageLayout.setVisibility(View.GONE);
				customMessageTextLayout.setVisibility(View.VISIBLE);
				
				HouseKeeperCustomMessageItem customMessageItem = new HouseKeeperCustomMessageItem(
						mActivity, actionInfo);
				
				customMessageTextLayout.addView(customMessageItem.getView());
				customMessageItem
						.setOnMessageItemClickListener(new OnMessageItemClickListener() {

							@Override
							public void onMessageItemClick() {
								// 跳转到自定义消息准备添加发送消息的设备 页面
								HouseKeeperCustomMessageAddDeviceFragment.autoProgramTaskInfo = autoProgramTaskInfo;
								// 将规则名传递给添加设备页面
								Bundle bundle = new Bundle();
								bundle.putString(
										HouseKeeperCustomMessageAddDeviceFragment.PROGRAM_NAME,
										taskNameEditText.getText().toString()
												.trim());
								mActivity.JumpTo(
										HouseKeeperCustomMessageActivity.class,
										bundle);
							}
						});

				// 删除按钮监听
				customMessageItem.getDeleteButton()
						.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View arg0) {
								autoProgramTaskInfo.getActionList().remove(
										actionInfo);
								
								isEditChange = true;
								initHouseKeeperCustomMessage();
							}
						});

			} else {
				customMessageTextLayout.setVisibility(View.GONE);
				customMessageLayout.setVisibility(View.VISIBLE);
			}
		} else {
			customMessageTextLayout.setVisibility(View.GONE);
			customMessageLayout.setVisibility(View.VISIBLE);
		}
	}

	private void initAutoProgramItem() {
		if (autoProgramTaskInfo != null) {
			// if(position >= 0 && autoProgramTaskInfo != null){
			taskNameEditText.setText(autoProgramTaskInfo.getProgramName());
			// taskDescribe.setText(autoProgramTaskInfo.getProgramDesc());
			initHouseKeeperView();
		} else {

		}
	}

	public void addAutoTriggerInfo(AutoConditionInfo info) {
		autoProgramTaskInfo.addTrigger(info);
	}

	@Override
	public void onResume() {
		super.onResume();
		initHouseKeeperView();
		changeIniBar();
	}

	/**
	 * 判断规则状态，如果没有编辑任何内容 则右上角完成按钮不可点击，否则可以点击
	 */
	private void changeIniBar() {
		if (autoProgramTaskInfo.getTriggerList().size() != 0
				&& autoProgramTaskInfo.getActionList().size() != 0
				&& !StringUtil.isNullOrEmpty(taskNameEditText.getText()
						.toString()) && executeLinkLayout.isClickable()) {
			initBar();
		} else {
			initBarChange();
		}
	}

	/**
	 * 初始化三个条件view
	 */
	private void initHouseKeeperView() {
		initHouseKeeperTriggerItem();
		initHouseKeeperConditionItem();
		initHouseKeeperLinkTask();
		initHouseKeeperCustomMessage();
	}

	/**
	 * 点击事件监听
	 */
	private OnClickListener conditionClickListener = new OnClickListener() {
		@Override
		public void onClick(View v) {

			Intent intent = new Intent();
			String condition = null;
			// 点击添加触发条件
			if (v == triggerLayout) {
				// 对触发事件添加页面设置监听，当选则了某一属性，回调方法
				HouseKeeperConditionFragment
						.setConditionListener(new ConditionListener() {

							@Override
							public void onConditionListenerChanged(String type,
									String object, String exp, String des) {
								if (type == null || object == null
										|| exp == null) {

								} else {
									isEditChange = true;
									AutoConditionInfo triggerInfo = new AutoConditionInfo();
									triggerInfo.setType(type);
									triggerInfo.setObject(object);
									triggerInfo.setExp(exp);
									triggerInfo.setDes(des);
									autoProgramTaskInfo.addTrigger(triggerInfo);
								}
							}
						});
				condition = "trigger";
				intent.putExtra(
						HouseKeeperConditionFragment.TRIGGER_OR_CONDITION,
						condition);
				intent.setClass(mActivity, HouseKeeperConditionActivity.class);
				mActivity.startActivity(intent);
			} else if (v == conditionLayout) { // 点击添加限制条件
				HouseKeeperConditionFragment
						.setConditionListener(new ConditionListener() {

							@Override
							public void onConditionListenerChanged(String type,
									String object, String exp, String des) {
								if (type == null || object == null
										|| exp == null) {

								} else {
									isEditChange = true;
									String addConditionString = null;
									if (StringUtil.equals(type, "0")) {
										addConditionString = type + "."
												+ "CURSCENE" + " " + "in" + " "
												+ "(" + object + ")";
									} else if (StringUtil.equals(type, "1")) {
										addConditionString = type + "."
												+ object + " " + exp;
									} else {
										addConditionString = type + "."
												+ object + " "
												+ exp.substring(0, 1) + " "
												+ exp.substring(1);
									}
									// AutoConditionInfo conditionInfo = new
									// AutoConditionInfo();
									// conditionInfo.setType(type);
									// conditionInfo.setObject(object);
									// conditionInfo.setExp(exp);
									// conditionInfo.setDes(des);
									if (chooseConditionLayout.isEnabled()) {
										if (chooseConditionLayout.isSelected()) {
											relative = "or";
										} else {
											relative = "and";
										}
									}
									autoProgramTaskInfo.addConditionTree(
											relative, addConditionString);
								}
							}
						});
				condition = "condition";

				if (conditionList.size() == 1) {
					showChooseConditionDialog(intent, condition);
				} else {
					intent.putExtra(
							HouseKeeperConditionFragment.TRIGGER_OR_CONDITION,
							condition);
					intent.setClass(mActivity,
							HouseKeeperConditionActivity.class);
					mActivity.startActivity(intent);
				}

			} else if (v == executeLinkLayout) { // 点击添加执行任务
				HouseKeeperActionTaskFragment
						.setAddLinkDeviceListener(new AddLinkTaskListener() {

							@Override
							public void onAddLinkTaskListenerChanged(
									AutoProgramTaskInfo taskInfo) {
								if (taskInfo != null) {
									isEditChange = true;
									autoProgramTaskInfo.setActionList(taskInfo
											.getActionList());
								}
								// if(taskInfo != null){
								// linkNumberTextView.setVisibility(View.VISIBLE);
								// linkNumberTextView.setText((taskInfo.getActionList().size()
								// + 1) + "个任务");
								// autoProgramTaskInfo.setActionList(taskInfo.getActionList());
								// }else{
								// linkNumberTextView.setVisibility(View.INVISIBLE);
								// }
							}
						});
				AutoProgramTaskInfo taskInfo = autoProgramTaskInfo;
				Bundle args = new Bundle();
				args.putSerializable("AutoProgramTaskInfo", taskInfo);
				mActivity.JumpTo(HouseKeeperActionTaskActivity.class, args);
			} else if (v == customMessageLayout) { // 添加自定义消息
				// 跳转到自定义消息准备添加发送消息的设备 页面
				HouseKeeperCustomMessageAddDeviceFragment.autoProgramTaskInfo = autoProgramTaskInfo;
				// 将规则名传递给添加设备页面
				Bundle bundle = new Bundle();
				bundle.putString(
						HouseKeeperCustomMessageAddDeviceFragment.PROGRAM_NAME,
						taskNameEditText.getText().toString().trim());
				mActivity
						.JumpTo(HouseKeeperCustomMessageActivity.class, bundle);

			}

		}

	};
	private AutoActionInfo actionInfo;
	private LinearLayout customMessageTextLayout;

	private void showChooseConditionDialog(final Intent intent,
			final String condition) {
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		View conditionView = inflater.inflate(
				R.layout.task_manager_dialog_condition, null);
		ImageView andIamgeView = (ImageView) conditionView
				.findViewById(R.id.task_manager_and_condition);
		final ImageView orIamgeView = (ImageView) conditionView
				.findViewById(R.id.task_manager_or_condition);
		final SingleChooseManager manager = new SingleChooseManager(
				R.drawable.task_manager_select,
				R.drawable.task_manager_no_select);
		manager.addImageView(andIamgeView);
		manager.addImageView(orIamgeView);
		OnClickListener conditionAndOrListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				manager.setChecked(v.getId());
			}
		};
		andIamgeView.setOnClickListener(conditionAndOrListener);
		orIamgeView.setOnClickListener(conditionAndOrListener);
		manager.setChecked(andIamgeView.getId());
		builder.setTitle(R.string.house_rule_add_new_limit_select_condition_relative);
		builder.setContentView(conditionView);
		builder.setPositiveButton(android.R.string.ok);
		builder.setNegativeButton(android.R.string.cancel);
		builder.setListener(new MessageListener() {
			@Override
			public void onClickPositive(View contentViewLayout) {
				if (manager.getCheckID() == orIamgeView.getId()) {
					chooseConditionLayout.setVisibility(View.VISIBLE);
					chooseConditionLayout.setSelected(true);
					// chooseConditionLayout.setBackgroundResource(R.drawable.task_manager_condition_or);
					relative = "or";
				} else {
					chooseConditionLayout.setVisibility(View.VISIBLE);
					chooseConditionLayout.setSelected(false);
					// chooseConditionLayout.setBackgroundResource(R.drawable.task_manager_condition_and);
					relative = "and";
				}
				intent.putExtra(
						HouseKeeperConditionFragment.TRIGGER_OR_CONDITION,
						condition);
				intent.setClass(mActivity, HouseKeeperConditionActivity.class);
				mActivity.startActivity(intent);
			}

			@Override
			public void onClickNegative(View contentViewLayout) {
				dialog.dismiss();
			}
		});

		dialog = builder.create();
		dialog.show();
	}

	private void initBar() {
		// 初始化ActionBar
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(R.string.nav_house_title);
		getSupportActionBar().setTitle(R.string.house_rule_add_rule);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar()
				.setRightGrayIconText(
						getResources()
								.getString(
										R.string.set_sound_notification_bell_prompt_choose_complete),
						getResources().getColor(R.color.white));
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {

					@Override
					public void onClick(View v) {

						List<AutoProgramTaskInfo> autoProgramTaskInfoList = autoProgramTaskManager
								.getAutoTaskList(mAccountManger.getmCurrentInfo()
										.getGwID());
						List<AutoConditionInfo> triggerInfo = autoProgramTaskInfo.getTriggerList();
						List<AutoActionInfo> actionInfo = autoProgramTaskInfo.getActionList();
						String programNames = taskNameEditText.getText()
								.toString();
						boolean istriggerInfoContainScene = false;
						boolean isactionInfoContainScene = false;
						boolean isRepeat = false;
						String gwID = mAccountManger.getmCurrentInfo().getGwID();
						String programID = autoProgramTaskInfo.getProgramID();
						if (StringUtil.isNullOrEmpty(programID)) {
							for (AutoProgramTaskInfo info : autoProgramTaskInfoList) {
								if (!StringUtil.equals(info.getProgramID(),
										programID)
										&& StringUtil.equals(
												info.getProgramName(),
												programNames)) {
									isRepeat = true;
									break;
								}
							}
						}
						if (!isRepeat) {
							autoProgramTaskInfo.setProgramName(programNames);
							// autoProgramTaskInfo.setProgramDesc(taskDescribe.getText().toString());
							boolean isCustomMessage = false;
							for (AutoActionInfo autoInfo : actionList) {
								if (StringUtil.equals("3", autoInfo.getType())) {
									isCustomMessage = true;
								}
							}
							if (!StringUtil.isNullOrEmpty(autoProgramTaskInfo
									.getProgramName())
									&& triggerList.size() != 0
									&& (actionList.size() != 0 || isCustomMessage)) {
								for (AutoConditionInfo info: triggerInfo){
									if (StringUtil.equals("0", info.getType())){
										istriggerInfoContainScene = true;
										break;
									}
								}
								for (AutoActionInfo info: actionInfo){
									if (StringUtil.equals("0", info.getType())){
										isactionInfoContainScene = true;
										break;
									}
								}
								if (istriggerInfoContainScene && isactionInfoContainScene){
									WLToast.showToast(
											mActivity,
											getResources().getString(R.string.explore_edit_conflict),
											WLToast.TOAST_SHORT);
								}else {
									String operType = getHouseRuleoperType(programID);
									String programName = taskNameEditText.getText()
											.toString();
									// String programDesc =
									// taskDescribe.getText().toString();
									String programType = "2";
									String status = getHouseRuleStatus(autoProgramTaskInfo);

									JSONArray triggerArray = new JSONArray();
									for (int i = 0; i < triggerList.size(); i++) {
										AutoConditionInfo info = triggerList.get(i);
										JSONObject obj = new JSONObject();
										JsonTool.makeTaskTriggerJSONObject(obj,
												info);
										triggerArray.add(obj);
									}

									JSONArray conditionArray = new JSONArray();
									if (autoProgramTaskInfo.getRoot() != null) {
										List<String> conditionRoot = autoProgramTaskInfo
												.getRoot().toTreeStrings();
										for (int i = 0; i < conditionRoot.size(); i++) {
											conditionArray.add(conditionRoot.get(i));
										}
									} else {
										conditionArray = null;
									}

									// 将AutoActionInfo集合封装为json对象
									JSONArray actionArray = new JSONArray();
									for (int i = 0; i < actionList.size(); i++) {
										AutoActionInfo info = actionList.get(i);
										JSONObject obj = new JSONObject();
										JsonTool.makeTaskActionJSONObject(obj, info);
										actionArray.add(obj);
									}
									preference
											.putBoolean(
													gwID
															+ IPreferenceKey.P_KEY_HOUSE_OWN_SEND_MODIFY,
													true);
									//发送自动程序任务信息
									NetSDK.sendSetProgramTask(gwID, operType,
											programID, programName, null,
											programType, status, triggerArray,
											conditionArray, actionArray);
									mDialogManager.showDialog(
											ADD_AND_UPDATE_TASK_KEY, mActivity,
											null, null);
								}
							} else {
								WLToast.showToast(
										mActivity,
										mActivity
												.getString(R.string.house_rule_add_new_rule_no_complete),
										WLToast.TOAST_SHORT);
							}
						} else {
							WLToast.showToast(
									mActivity,
									mActivity
											.getString(R.string.house_rule_add_new_rule_name_exist),
									WLToast.TOAST_SHORT);
						}
					}

				});
		getSupportActionBar().setLeftIconClickListener(
				new OnLeftIconClickListener() {

					@Override
					public void onClick(View v) {
						createBackDialog();
					}
				});
	}

	private String getHouseRuleStatus(AutoProgramTaskInfo taskInfo) {
		String status;
		if (!StringUtil.isNullOrEmpty(taskInfo.getStatus())) {
			status = taskInfo.getStatus();
		} else {
			status = CmdUtil.HOUSE_RULES_USING;
		}
		return status;
	}

	private String getHouseRuleoperType(String programID) {
		String operType;
		if (!StringUtil.isNullOrEmpty(programID)) {
			operType = AutoTask.AUTO_TASK_OPER_TYPE_MODIFY;
		} else {
			operType = AutoTask.AUTO_TASK_OPER_TYPE_ADD;
		}
		return operType;
	}

	private void initBarChange() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(R.string.nav_house_title);
		getSupportActionBar().setTitle(R.string.house_rule_add_rule);
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar()
				.setRightGrayIconText(
						getResources()
								.getString(
										R.string.set_sound_notification_bell_prompt_choose_complete),
						getResources().getColor(R.color.v5_gray_mid));
		getSupportActionBar().setLeftIconClickListener(
				new OnLeftIconClickListener() {

					@Override
					public void onClick(View v) {
						createBackDialog();
					}
				});
	}

	/**
	 * 点击左上角返回按钮时弹出对话框
	 */
	private void createBackDialog() {
		if (isEditChange) {
			backAddLinkDialog();
		} else {
			mActivity.finish();
		}
	}

	private void backAddLinkDialog() {
		WLDialog.Builder builder = new Builder(mActivity);
		View contentView = inflater.inflate(R.layout.common_dialog_text_prompt,
				null);
		TextView promptText = (TextView) contentView
				.findViewById(R.id.common_dialog_text_prompt);
		promptText.setText(mActivity.getResources().getString(
				R.string.house_rule_back_exit));
		builder.setContentView(contentView)
				.setTitle(R.string.gateway_router_setting_dialog_toast)
				.setPositiveButton(
						R.string.house_rule_condition_device_messagebox_edit)
				.setNegativeButton(
						R.string.house_rule_condition_device_messagebox_abandon)
				.setListener(new MessageListener() {
					@Override
					public void onClickPositive(View contentViewLayout) {
					}

					@Override
					public void onClickNegative(View contentViewLayout) {
						isEditChange = false;
						mActivity.finish();
					}
				});
		dialog = builder.create();
		dialog.show();
	}

	public void onEventMainThread(AutoTaskEvent event) {
		// initHouseKeeperView();
		changeIniBar();
		if (AutoTaskEvent.ADDRULE.equals(event.action)
				&& preference.getBoolean(mAccountManger.getmCurrentInfo().getGwID()
						+ IPreferenceKey.P_KEY_HOUSE_OWN_SEND_MODIFY, false)) {
			mDialogManager.dimissDialog(ADD_AND_UPDATE_TASK_KEY, 0);
			mActivity.finish();
		} else if (AutoTaskEvent.MODIFY.equals(event.action)
				&& preference.getBoolean(mAccountManger.getmCurrentInfo().getGwID()
						+ IPreferenceKey.P_KEY_HOUSE_OWN_SEND_MODIFY, false)) {
			initHouseKeeperView();
			mDialogManager.dimissDialog(ADD_AND_UPDATE_TASK_KEY, 0);
			mActivity.finish();
		}
		preference.putBoolean(mAccountManger.getmCurrentInfo().getGwID()
				+ IPreferenceKey.P_KEY_HOUSE_OWN_SEND_MODIFY, false);
	}

	// 开始引导页
	private void beginGuid(View view) {
		// 如果引导过，就返回
		if (preference.getBoolean(IPreferenceKey.P_KEY_HOUSE_ADD_RULE_GUIDE,
				false))
			return;

		FrameLayout guidRootLayout = (FrameLayout) view
				.findViewById(R.id.guid_root);
		; // 引导页根节点
		LayoutInflater inflater = (LayoutInflater) getActivity()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		GuideUtil guidUtil = new GuideUtil(getActivity(), guidRootLayout);
		guidUtil.setCallback(new GuideCallback() {

			@Override
			public void onGuideOver() {
				preference.putBoolean(
						IPreferenceKey.P_KEY_HOUSE_ADD_RULE_GUIDE, true); // 设为以引导过
			}
		});
		List<GuideEntity> entitys = new ArrayList<GuideEntity>();
		entitys.add(new GuideEntity(triggerLayout, inflater.inflate(
				R.layout.guid_house_rule_step_one, null), Gravity.BOTTOM));
		entitys.add(new GuideEntity(conditionLayout, inflater.inflate(
				R.layout.guid_house_rule_step_two, null), Gravity.BOTTOM));
		entitys.add(new GuideEntity(executeLinkLayout, inflater.inflate(
				R.layout.guid_house_rule_step_three, null), Gravity.TOP));
		guidUtil.beginGuide(entitys);
	}
}
