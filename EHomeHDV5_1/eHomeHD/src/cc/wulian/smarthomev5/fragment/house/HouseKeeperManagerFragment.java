package cc.wulian.smarthomev5.fragment.house;

import java.util.List;
import java.util.TimeZone;

import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.AutoProgramTaskInfo;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.house.HouseKeeperAddRulesActivity;
import cc.wulian.smarthomev5.activity.house.HouseKeeperTimeZoneActivity;
import cc.wulian.smarthomev5.adapter.house.AutoProgramTaskAdapter;
import cc.wulian.smarthomev5.callback.CallBackGateway;
import cc.wulian.smarthomev5.event.MigrationTaskEvent;
import cc.wulian.smarthomev5.fragment.home.HomeFragment;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperTimeZoneFragment.ZoneSettingListener;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.fragment.navigation.NavigationFragment;
import cc.wulian.smarthomev5.fragment.setting.flower.items.CustomPopupWindow;
import cc.wulian.smarthomev5.fragment.setting.flower.items.CustomPopupWindow.PopEvent;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.CountDownTimer;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenu;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter.OnMenuItemClickListener;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuCreator;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView.OpenOrCloseListener;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

public class HouseKeeperManagerFragment extends WulianFragment {

	private AutoProgramTaskManager autoProgramTaskManager = AutoProgramTaskManager
			.getInstance();
	private AutoProgramTaskAdapter autoProgramTaskAdapter;
	private SwipeMenuListView autoProgramListView;
	private LinearLayout autoAddTaskRemind;
	private TextView autoRemindText;
	private static final String TASK_KEY = "task_key";
	private Preference preference = Preference.getPreferences();
	// 特殊dialog对象,升级失败的dialog,因为在升级失败的内容框中单击跳转dialog,而在方法中无法获取当前dialog对象,通过设为全局变量可获取该对象
	private WLDialog upgradeFailDialog;
	private PopupWindow countDownPopupWindow;
	private View rootView;
	private NavigationFragment navigationFragment;
	private boolean isFirstCreateView = true;
	private String zoneId;
	//added by 殷田  规则介绍弹窗  在onViewCreated中显示出来的
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		FragmentManager manager = mActivity.getSupportFragmentManager();
		Fragment fragment = manager.findFragmentByTag(NavigationFragment.class
				.getName());
		if (fragment != null) {
			navigationFragment = (NavigationFragment) fragment;
		}
		autoProgramTaskAdapter = new AutoProgramTaskAdapter(mActivity, null);
	}
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		rootView = inflater.inflate(R.layout.task_manager_fragment_content, null);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		autoProgramListView = (SwipeMenuListView) view.findViewById(R.id.house_keeper_listview);
		autoAddTaskRemind = (LinearLayout) view.findViewById(R.id.house_keeper_task_remind_layout);
		autoRemindText = (TextView) view.findViewById(R.id.house_keeper_task_remind_layout_text);
		autoProgramTaskAdapter.setMenuCreator(creatLeftDeleteItem());
		autoProgramListView.setAdapter(autoProgramTaskAdapter);
		initAutoProgramTaskList();	
	}
	/**
	 * 创建左划删除item样式
	 */
	private SwipeMenuCreator creatLeftDeleteItem() {
		SwipeMenuCreator creator = new SwipeMenuCreator() {
			@Override
			public void create(SwipeMenu menu,int position) {
				SwipeMenuItem deleteItem = new SwipeMenuItem(mActivity);
				deleteItem.setBackground(new ColorDrawable(Color.rgb(0xF9,
						0x3F, 0x25)));
				deleteItem.setWidth(DisplayUtil.dip2Pix(mActivity, 90));
				deleteItem.setIcon(R.drawable.ic_delete);
				menu.addMenuItem(deleteItem);
			}
		};
		return creator;
	}
	private void initAutoProgramTaskList() {
		autoProgramTaskAdapter
				.setOnMenuItemClickListener(new OnMenuItemClickListener() {

					@Override
					public void onMenuItemClick(int position, SwipeMenu menu,
							int index) {
						AutoProgramTaskInfo item = autoProgramTaskManager.getAutoProgramTypeHouseKeeper().get(
								position);
						switch (index) {
						case 0:
							if (item != null) {
								// taskEntity.removeTask(info.getGwID(), info.getProgramID());
								JsonTool.deleteAndQueryAutoTaskList("D", item);
								mDialogManager.showDialog(TASK_KEY, mActivity, null, null);
							}
							break;
						}
					}

				});
		// 解决左划删除与右划菜单栏冲突
		autoProgramListView.setOnOpenOrCloseListener(new OpenOrCloseListener() {

			@Override
			public void isOpen(boolean isOpen) {
			}
		});

		autoProgramListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// showAutoTaskDetails((AutoProgramTaskInfo) parent.getAdapter()
				// .getItem(position),position);

				AutoProgramTaskInfo autoTaskInfo = (AutoProgramTaskInfo) parent
						.getAdapter().getItem(position);
				JsonTool.deleteAndQueryAutoTaskList("R", autoTaskInfo);
				preference.putBoolean(mAccountManger.getmCurrentInfo().getGwID() + autoTaskInfo.getProgramID() + IPreferenceKey.P_KEY_HOUSE_OWN_SEND_QUERY,true);
				mDialogManager.showDialog(TASK_KEY, mActivity, null, null);
			}
		});
	}
	@Override
	public void onResume() {
		super.onResume();
		if(navigationFragment.isShownFragment(HouseKeeperManagerFragment.class.getName()) && isFirstCreateView){
			getHouseUpgradeStatus();
			isFirstCreateView = false;
		}
		TaskExecutor.getInstance().executeDelay(new Runnable() {
			@Override
			public void run() {
				loadAllAutoProgramTasks();
			}
		},500);
	}
	
	@Override
	public void onShow() {
		super.onShow();
		initBar();
		getHouseUpgradeStatus();
	}
	private void getHouseUpgradeStatus() {
		boolean isUpgrade = Preference.getPreferences().getBoolean(
				IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, false);
		if(isUpgrade){
			showIntroducePopWindow();
			return ;
		}
		if(!isGatewaySupportHouseKeeper() ){
			autoRemindText.setText(mApplication.getResources().getString(R.string.house_rule_version_notice));
		}
		else{
//			preference.putBoolean(gwID + IPreferenceKey.P_KEY_HOUSE_OWN_SEND_GET_UPGRADE,true);
			SendMessage.addRequest(CallBackGateway.REQUEST_KEY_HOUSE_SEND_UPGRADE_GET);
			NetSDK.sendOperateMigrationTaskMsg(mAccountManger.getmCurrentInfo().getGwID(),
					CmdUtil.GET_HOUSE_STATUS, null);
//			mDialogManager.showDialog(KEY_HOUSE_UPGRADE_LISTENER, mActivity, null, null);
		}
	}
	public boolean isGatewaySupportHouseKeeper(){
		String gwVer = preference.getGateWayVersion(mAccountManger.getmCurrentInfo().getGwID());
		if(!StringUtil.isNullOrEmpty(gwVer) && gwVer.split("[.]").length == 3){
			return GatewayInfo.GW_GENERATION_THREE.equals(gwVer.split("[.]")[0]) && StringUtil.toInteger(gwVer.split("[.]")[2]) >= 5;
		}
		return false;
	}
	//初始化ActionBar
	private void initBar() {
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuEnabled(true);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setTitle(mApplication.getResources().getString(R.string.nav_house_title));
		getSupportActionBar().setRightIcon(R.drawable.common_use_add);
		getSupportActionBar().setRightMenuClickListener(
				new OnRightMenuClickListener() {
					@Override
					public void onClick(View v) {
						if(Preference.getPreferences().getBoolean(
								IPreferenceKey.P_KEY_HOUSE_HAS_UPGRADE, false)){
							Intent intent = new Intent();
							intent.setClass(mActivity, HouseKeeperAddRulesActivity.class);
							mActivity.startActivity(intent);
						}
					}
			});
	}
	
	//加载所有的自动任务
	private void loadAllAutoProgramTasks() {
		final List<AutoProgramTaskInfo> infos = autoProgramTaskManager.getAutoProgramTypeHouseKeeper();
		mActivity.runOnUiThread(new Runnable() {

			@Override
			public void run() {
				if (infos == null || infos.size() == 0) {
					autoProgramListView.setVisibility(View.GONE);
					autoAddTaskRemind.setVisibility(View.VISIBLE);
					if(isGatewaySupportHouseKeeper()){
						autoRemindText.setText(mApplication.getResources().getString(R.string.house_rule_add_rule_remind));
					}
				} else {
					autoProgramListView.setVisibility(View.VISIBLE);
					autoAddTaskRemind.setVisibility(View.GONE);
					autoProgramTaskAdapter.swapData(infos);
				}
			}
		});
	}

//	/**
//	 * 网关版本过低
//	 */
//	public void createGatewayVersionLowPopupWidow() {
//		View cushView = inflater.inflate(
//				R.layout.task_maneger_fragment_upgrade_popupwindow, null);
//		createPopuWindowBackground(cushView);
//		TextView startUpgradeBtn = (TextView) cushView.findViewById(R.id.house_keeper_upgrade_start_btn);
//		startUpgradeBtn.setVisibility(View.VISIBLE);
//		startUpgradeBtn.setText(mActivity.getResources().getString(R.string.house_rule_version_notice));
//	}
	
	/**
	 * 启动管家 按钮
	 */
	public void createStartHousePopupWidow() {
		View cushView = inflater.inflate(
				R.layout.task_maneger_fragment_upgrade_popupwindow, null);
		final PopupWindow popupWindow = createPopuWindowBackground(cushView);
		TextView tvStartBtn = (TextView) cushView.findViewById(R.id.house_keeper_upgrade_start_btn);
		tvStartBtn.setVisibility(View.VISIBLE);
		tvStartBtn.setText(mApplication.getResources().getString(R.string.house_rule_upgrade_start));
		tvStartBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
//				if(!mAccountManger.getmCurrentInfo().isGatewaySupportHouseKeeper()){
//					WLToast.showToast(mActivity,mActivity.getResources().getString(R.string.house_rule_version_notice), WLToast.TOAST_SHORT);
//					return ;
//				}
				popupWindow.dismiss();
				createUpgradePromtDialog();
			}
		});
	}

	private PopupWindow createPopuWindowBackground(View contentView) {
		PopupWindow popupWindow = new PopupWindow(contentView,
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);
		popupWindow.setOutsideTouchable(false);
		popupWindow.setFocusable(true);
		ColorDrawable dw = new ColorDrawable(0xb0000000);
		popupWindow.setBackgroundDrawable(dw);
		popupWindow.showAtLocation(rootView, Gravity.CENTER, 0, 0);
		return popupWindow;
	}
	
	/**
	 * 管家升级中，请等待... 按钮
	 */
	public void createUpgradeWaitPopupWindow() {
		View cushView = inflater.inflate(
				R.layout.task_maneger_fragment_upgrade_popupwindow, null);
		createPopuWindowBackground(cushView);
		TextView startUpgradeBtn = (TextView) cushView.findViewById(R.id.house_keeper_upgrade_start_btn);
		startUpgradeBtn.setVisibility(View.VISIBLE);
		startUpgradeBtn.setText(mApplication.getResources().getString(R.string.house_rule_upgrade_please_waiting));
	}

	/**
	 * 管家升级中的popupwindow
	 */
	public PopupWindow createStartingPopupWidow() {
		View startingView = inflater.inflate(
				R.layout.task_maneger_upgrading_popupwindow, null);
		final PopupWindow countDownPopupWindow = createPopuWindowBackground(startingView);
		final TextView countDowntimeTextView = (TextView) startingView.findViewById(R.id.house_upgrade_count_down_time);
		final CountDownTimer countDownTimer = new CountDownTimer(120000, 1000) {
			
			@Override
			public void onTick(long millisUntilFinished) {
				countDowntimeTextView.setText(millisUntilFinished/1000 + "");
			}
			
			@Override
			public void onFinish() {
				countDownPopupWindow.dismiss();
				SendMessage.removeRequest(CallBackGateway.REQUEST_KEY_HOUSE_SEND_UPGRADE_DO);
				createUpgradeTimeoutDialog();
			}
		};
		countDownTimer.start();
		countDownPopupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
			@Override
			public void onDismiss() {
				if(countDownTimer != null){
					countDownTimer.cancel();
				}
			}
		});
		return countDownPopupWindow;
	}
	
	// 提示升级管家对话框
	private void createUpgradePromtDialog() {
		// popupwindow背景
		View popupView = inflater.inflate(
				R.layout.task_maneger_fragment_upgrade_popupwindow, null);
		final PopupWindow popupWindowBg = createPopuWindowBackground(popupView);
		// popupwindow上的dialog
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(R.string.house_rule_upgrade);
		View createUpgradeView = inflater.inflate(
				R.layout.task_maneger_fragment_upgrade_dialog, null);
		TextView upgradeRemind = (TextView) createUpgradeView
				.findViewById(R.id.house_keeper_upgrade_promt);
		upgradeRemind.setText(mApplication.getResources().getString(R.string.house_rule_upgrade_dialog_remind));
		builder.setContentView(createUpgradeView)
				.setNegativeButton(mApplication.getResources().getString(R.string.cancel))
				.setPositiveButton(mApplication.getResources().getString(R.string.common_ok))
				.setCancelOnTouchOutSide(false)
				.setListener(new MessageListener() {

					@Override
					public void onClickPositive(View contentViewLayout) {
						Intent intent = new Intent();
						intent.setClass(mActivity, HouseKeeperTimeZoneActivity.class);
						mActivity.startActivity(intent);
						HouseKeeperTimeZoneFragment.setZoneSettingListener(new ZoneSettingListener() {
							
							@Override
							public void onZoneSettingListenerBacked(boolean isback, String zoneID) {
								if(isback){
									createStartHousePopupWidow();
								}else{
									//发送开始迁移命令 do
//									preference.putBoolean(gwID + IPreferenceKey.P_KEY_HOUSE_OWN_SEND_DO_UPGRADE,true);
									zoneId = zoneID;
									if(StringUtil.isNullOrEmpty(zoneId)){
										zoneId = getLocalTimeZoneIndex();
									}
									SendMessage.addRequest(CallBackGateway.REQUEST_KEY_HOUSE_SEND_UPGRADE_DO);
									NetSDK.sendOperateMigrationTaskMsg(mAccountManger.getmCurrentInfo().getGwID(),
											CmdUtil.DO_HOUSE_STATUS, zoneId);
									countDownPopupWindow = createStartingPopupWidow();
								}
								
							}
						});
					}

					@Override
					public void onClickNegative(View contentViewLayout) {
						createStartHousePopupWidow();
					}
				});
		WLDialog dlg = builder.create();
		dlg.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				// wldialog监听,如果dialog消失的话将popupwindow背景dismiss
				popupWindowBg.dismiss();
			}
		});
		dlg.show();
	}

	private String getLocalTimeZoneIndex() {
		TimeZone zone = TimeZone.getDefault();
		long zoneOffset=zone.getOffset(System.currentTimeMillis());
		int zoneOffsetHours = (int) (zoneOffset / 60 / 60 / 1000);
		return zoneOffsetHours > 0 ? "+" + String.valueOf(zoneOffsetHours)
				: String.valueOf(zoneOffsetHours);
	}
	// 升级失败对话框
	private void createUpgradeFailDialog() {
		View popupView = inflater.inflate(
				R.layout.task_maneger_fragment_upgrade_popupwindow, null);
		final PopupWindow popupWindowBg = createPopuWindowBackground(popupView);
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(R.string.house_rule_upgrade_failure_remind_title);
		View UpgradeFailView = inflater.inflate(
				R.layout.task_maneger_fragment_upgrade_fail_dialog, null);
		TextView UpgradeFailTextView = (TextView) UpgradeFailView
				.findViewById(R.id.house_keeper_upgrade_fail_promt);
//		TextView tvClearScene = (TextView) UpgradeFailView
//				.findViewById(R.id.house_keeper_upgrade_fail_clear_scene);
		String clearSceneStr = mApplication.getResources().getString(R.string.house_rule_upgrade_remove_scene_info);
		SpannableString clearSceneSpan = new SpannableString(clearSceneStr);
		UpgradeFailTextView.setText(mApplication.getResources().getString(R.string.house_rule_upgrade_failure_remind));
		ClickableSpan clicktClearScene = new ClickableSpan() {
			
			 @Override
			    public void updateDrawState(TextPaint ds) {
			        ds.setColor(Color.BLUE);
			    }
			 
			 
			    @Override
			    public void onClick(View widget) {
			    	upgradeFailDialog.dismiss();
			    	createClearSceneDialog();
			    }
		};
		clearSceneSpan.setSpan(clicktClearScene, 0, clearSceneStr.length(), Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		UpgradeFailTextView.append(clearSceneSpan);
		UpgradeFailTextView.setMovementMethod(LinkMovementMethod.getInstance());
		builder.setContentView(UpgradeFailView)
		.setNegativeButton(mApplication.getResources().getString(R.string.house_rule_upgrade_cancel))
		.setPositiveButton(mApplication.getResources().getString(R.string.house_rule_upgrade_remove_try_again))
		.setCancelOnTouchOutSide(false)
		.setListener(new MessageListener() {
			@Override
			public void onClickPositive(View contentViewLayout) {
//				preference.putBoolean(gwID + IPreferenceKey.P_KEY_HOUSE_OWN_SEND_DO_UPGRADE,true);
				SendMessage.addRequest(CallBackGateway.REQUEST_KEY_HOUSE_SEND_UPGRADE_DO);
				NetSDK.sendOperateMigrationTaskMsg(AccountManager
						.getAccountManger().getmCurrentInfo().getGwID(),CmdUtil.DO_HOUSE_STATUS, zoneId);
				countDownPopupWindow = createStartingPopupWidow();
			}
			@Override
			public void onClickNegative(View contentViewLayout) {
				createStartHousePopupWidow();
			}
		});
		upgradeFailDialog = builder.create();
		upgradeFailDialog.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				popupWindowBg.dismiss();
			}
		});
		upgradeFailDialog.show();
	}

	//升级超时 升级超时，网关将继续在后台为您自动升级
	private void createUpgradeTimeoutDialog() {
		View popupView = inflater.inflate(
				R.layout.task_maneger_fragment_upgrade_popupwindow, null);
		final PopupWindow popupWindowBg = createPopuWindowBackground(popupView);
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		View cupdView = inflater.inflate(
				R.layout.task_maneger_fragment_upgrade_dialog, null);
		TextView tvToast = (TextView) cupdView
				.findViewById(R.id.house_keeper_upgrade_promt);
		tvToast.setText(mApplication.getResources().getString(R.string.house_rule_upgrade_timeout));
		builder.setTitle(mApplication.getResources().getString(R.string.gateway_router_setting_dialog_toast))
		.setContentView(cupdView)
		.setPositiveButton(android.R.string.ok)
		.setCancelOnTouchOutSide(false)
		.setListener(new MessageListener() {
			@Override
			public void onClickPositive(View contentViewLayout) {
				FragmentManager manager = mActivity.getSupportFragmentManager();
				Fragment fragment = manager.findFragmentByTag(NavigationFragment.class
						.getName());
				if (fragment != null) {
					NavigationFragment navFragment = (NavigationFragment) fragment;
					navFragment.changeFragement(HomeFragment.class.getName());
				}
			}
			@Override
			public void onClickNegative(View contentViewLayout) {
			}
		});
		WLDialog dlg = builder.create();
		dlg.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				popupWindowBg.dismiss();
			}
		});
		dlg.show();
	}
	// 清除场景的对话框
	private void createClearSceneDialog() {
		View popupView = inflater.inflate(
				R.layout.task_maneger_fragment_upgrade_popupwindow, null);
		final PopupWindow popupWindowBg = createPopuWindowBackground(popupView);
		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
		builder.setTitle(R.string.house_rule_upgrade_warning);
		View ccsdView = inflater.inflate(
				R.layout.task_maneger_fragment_upgrade_dialog, null);
		TextView tvToast = (TextView) ccsdView
				.findViewById(R.id.house_keeper_upgrade_promt);
		tvToast.setText(mApplication.getResources().getString(R.string.house_rule_upgrade_remove_scene_info_remind));
		builder.setContentView(ccsdView)
			.setNegativeButton(mApplication.getResources().getString(R.string.cancel))
			.setPositiveButton(mApplication.getResources().getString(R.string.common_ok))
			.setCancelOnTouchOutSide(false)
			.setListener(new MessageListener() {
				@Override
				public void onClickPositive(View contentViewLayout) {
					// TODO 发送清除场景指令
//					preference.putBoolean(gwID + IPreferenceKey.P_KEY_HOUSE_OWN_SEND_CLEAR_UPGRADE,true);
					SendMessage.addRequest(CallBackGateway.REQUEST_KEY_HOUSE_SEND_UPGRADE_CLEAR);
					NetSDK.sendOperateMigrationTaskMsg(mAccountManger.getmCurrentInfo().getGwID(),
					CmdUtil.CLEAR_HOUSE_STATUS, null);
				}
				@Override
				public void onClickNegative(View contentViewLayout) {
//					preference.putBoolean(P_KEY_UPGRADE_CANCEL, true);
					createUpgradeFailDialog();
				}
		});
		WLDialog dlg = builder.create();
		dlg.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss(DialogInterface arg0) {
				popupWindowBg.dismiss();
			}
		});
		dlg.show();
	}

//	private void createUpgradeSuccessDialog() {
//		View popupView = inflater.inflate(
//				R.layout.task_maneger_fragment_upgrade_popupwindow, null);
//		final PopupWindow popupWindowBg = createPopuWindowBackground(popupView);
//		WLDialog.Builder builder = new WLDialog.Builder(mActivity);
//		builder.setTitle(R.string.house_rule_upgrade_success_remind);
//		View cupdView = inflater.inflate(
//				R.layout.task_maneger_fragment_upgrade_dialog, null);
//		TextView tvToast = (TextView) cupdView
//				.findViewById(R.id.house_keeper_upgrade_promt);
//		tvToast.setText(mActivity.getResources().getString(R.string.house_rule_upgrade_know_remind));
//		builder.setContentView(cupdView)
//				.setPositiveButton(
//						getResources().getString(
//								R.string.common_ok))
//				.setCancelOnTouchOutSide(false)
//				.setListener(new MessageListener() {
//
//					@Override
//					public void onClickPositive(View contentViewLayout) {
//						SendMessage.sendGetProgramTaskMsg(gwID);
//					}
//
//					@Override
//					public void onClickNegative(View contentViewLayout) {
//						SendMessage.sendGetProgramTaskMsg(gwID);
//					}
//				});
//		WLDialog dlg = builder.create();
//		dlg.setOnDismissListener(new OnDismissListener() {
//
//			@Override
//			public void onDismiss(DialogInterface arg0) {
//				popupWindowBg.dismiss();
//			}
//		});
//		dlg.show();
//	}
	
	public void onEventMainThread(AutoTaskEvent event) {
		if (AutoTaskEvent.QUERY.equals(event.action)
				&& event.taskInfo != null && preference.getBoolean(event.taskInfo.getGwID() + event.taskInfo.getProgramID() + IPreferenceKey.P_KEY_HOUSE_OWN_SEND_QUERY, false)) {
			preference.putBoolean(event.taskInfo.getGwID() + event.taskInfo.getProgramID() + IPreferenceKey.P_KEY_HOUSE_OWN_SEND_QUERY,false);
			mDialogManager.dimissDialog(TASK_KEY, 0);
			Bundle args = new Bundle();
			args.putString(HouseKeeperAddRulesFragment.AUTO_TASK_GWID,
					event.taskInfo.getGwID());
			args.putString(HouseKeeperAddRulesFragment.AUTO_TASK_PROGRAM_ID,
					event.taskInfo.getProgramID());
			mActivity.JumpTo(HouseKeeperAddRulesActivity.class, args);
		}else{
			mDialogManager.dimissDialog(TASK_KEY, 0);
		}
		TaskExecutor.getInstance().execute(new Runnable() {

			@Override
			public void run() {
				loadAllAutoProgramTasks();
			}
		});
	}

	public void onEventMainThread(MigrationTaskEvent event) {
		// 管家升级后立即返回
		if(StringUtil.equals(event.getAction(), CmdUtil.DO_HOUSE_STATUS)){
			if (MigrationTaskEvent.ACTION_CONPLETE_MIGRATION_SUCCESS.equals(event
					.getData())) {
				if(countDownPopupWindow != null)
					countDownPopupWindow.dismiss();
				WLToast.showToast(mActivity, mApplication.getResources().getString(R.string.house_rule_upgrade_success_remind),WLToast.TOAST_SHORT);
				//升级成功后的导航页
				showIntroducePopWindow();
			}else if (MigrationTaskEvent.ACTION_MIGRATION_FAIL.equals(event.getData())) {
				if(countDownPopupWindow != null)
					countDownPopupWindow.dismiss();
				createUpgradeFailDialog();
			}
			
		}else if(StringUtil.equals(event.getAction(), CmdUtil.GET_HOUSE_STATUS)){
			//登录后有次获取
			//进入管家模块后获取
			if(countDownPopupWindow != null)
				countDownPopupWindow.dismiss();
			if (MigrationTaskEvent.ACTION_MIGRATIONING.equals(event
					.getData())) {
				createUpgradeWaitPopupWindow();
			} else if (MigrationTaskEvent.ACTION_MIGRATION_FAIL.equals(event.getData())) {
				createUpgradeFailDialog();
			} else if(MigrationTaskEvent.ACTION_NO_MIGRATION.equals(event.getData())){
				createStartHousePopupWidow();
			}
		}else if(StringUtil.equals(event.getAction(), CmdUtil.CLEAR_HOUSE_STATUS)){
			//清除场景的任务和定时数据，直接标记为已经迁移完成
			if(countDownPopupWindow != null)
				countDownPopupWindow.dismiss();
			if (MigrationTaskEvent.ACTION_CONPLETE_MIGRATION_SUCCESS.equals(event
					.getData())) {
				//升级成功 
				WLToast.showToast(mActivity, mApplication.getResources().getString(R.string.house_rule_upgrade_success_remind),WLToast.TOAST_SHORT);
				//升级成功后的导航页
				showIntroducePopWindow();
			}
		}
	}
	
	/**
	 * 初始化规则介绍弹窗
	 */
	private void showIntroducePopWindow(){
		boolean isShowGuide = preference.getBoolean(IPreferenceKey.P_KEY_HOUSE_INTRODUCE_GUIDE, false);
		if(isShowGuide)
			return ;
		final CustomPopupWindow popupWindow=new CustomPopupWindow(getActivity(), R.layout.house_rule_introduction_layout);
		popupWindow.initEvent(new PopEvent(){

			@Override
			public void initWidget(View view) {
				//added by 殷田   规则介绍控件
				TextView guideOpenCurtain=(TextView) view.findViewById(R.id.guide_open_curtain);
				guideOpenCurtain.setText(Html.fromHtml(mApplication.getResources().getString(R.string.house_rule_execute_curtain)));
				TextView guideOpenLight=(TextView) view.findViewById(R.id.guide_open_light);
				guideOpenLight.setText(Html.fromHtml(mApplication.getResources().getString(R.string.house_rule_execute_light)));
				Button introduceOver=(Button) view.findViewById(R.id.introduce_over);
				introduceOver.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						popupWindow.dismiss();
					}
				});
			}
			
		});
		popupWindow.setBackgroundDrawable(null); //屏蔽系统返回键
		popupWindow.showAtLocation(rootView, Gravity.BOTTOM, 0, 0);
		preference.putBoolean(IPreferenceKey.P_KEY_HOUSE_INTRODUCE_GUIDE, true);
//		View view=getActivity().getWindow().findViewById(Window.ID_ANDROID_CONTENT);
//		popupWindow.setSize(view.getWidth(),view.getHeight()); //设置弹窗大小  默认是全屏大小
	}
}