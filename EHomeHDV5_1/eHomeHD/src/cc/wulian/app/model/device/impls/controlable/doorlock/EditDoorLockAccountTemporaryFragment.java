package cc.wulian.app.model.device.impls.controlable.doorlock;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.accounts.AccountsException;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.controlable.cooker.ElectricCookerTimeView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.NewDoorLockEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenu;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuCreator;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter.OnMenuItemClickListener;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuListView.OpenOrCloseListener;

import com.alibaba.fastjson.JSONObject;
import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;

public class EditDoorLockAccountTemporaryFragment extends WulianFragment
		implements OnClickListener {
	private static final String NEW_DOOR_LOCK_ACCOUNT_TEMPORARY_KEY = "new_door_lock_account_temporary_key";
	private Context mContext;
	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	public static final String USERID = "userID";
	public static final String TOKEN = "token";
	public static final String PASSWORD = "password";
	public static final String PEROID = "peroid";
	public static final String CNAME = "cname";
	private String mToken;
	private String gwID;
	private String devID;
	private String userID;
	private String peroid;
	private String cname;
	private String password;
	private DeviceDoorLockAccountTemporaryTimeView endView;

	private final static int EFFECT_TEXTVIEW = 0;
	private final static int UNEFFECT_TEXTVIEW = 1;
	private final static int NOCHOOSE = 2;
	private int timePosition = NOCHOOSE;

	private List<String> timeEffectString;
	private List<String> timeUneffectString;

	@ViewInject(R.id.device_new_door_lock_account_temporary_psw_edittext)
	private EditText mAccountTemporaryPSWEdittext;
	@ViewInject(R.id.device_new_door_lock_account_temporary_user_edittext)
	private EditText mAccountTemporaryUserEditext;
	@ViewInject(R.id.device_new_door_lock_temporary_effect_tv)
	private TextView mAccountTemporaryEffectTextview;
	@ViewInject(R.id.device_new_door_lock_temporary_uneffect_tv)
	private TextView mAccountTemporaryUneffectTextview;
	@ViewInject(R.id.device_new_door_lock_temporary_time_view)
	private LinearLayout mAccountTemporaryTimeViewLinearLayout;
	@ViewInject(R.id.device_new_door_lock_temporary_time_ensure_tv)
	private TextView mAccountTemporaryTimeViewEnsureLinearLayout;
	@ViewInject(R.id.device_new_door_lock_temporary_time_cancle_tv)
	private TextView mAccountTemporaryTimeViewCancleLinearLayout;
	@ViewInject(R.id.device_new_door_lock_temporary_time_show_tv)
	private TextView mAccountTemporaryTimeViewShowLinearTextView;
	@ViewInject(R.id.device_new_door_lock_temporary_time_show_linearlayout)
	private LinearLayout mAccountTemporaryTimeViewShowLinearLayout;
	@ViewInject(R.id.device_new_door_lock_account_temporary_password_ll)
	private LinearLayout mAccountPasswordLinearLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		mContext = getActivity();
		timeEffectString = new ArrayList<String>();
		timeUneffectString = new ArrayList<String>();
		mToken = getArguments().getString(TOKEN);
		gwID = getArguments().getString(GWID);
		devID = getArguments().getString(DEVICEID);
		peroid = getArguments().getString(PEROID);
		cname = getArguments().getString(CNAME);
		password = getArguments().getString(PASSWORD);
		intiBar();
	}

	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View contentView = inflater.inflate(
				R.layout.device_door_lock_setting_account_temporary, container,
				false);
		ViewUtils.inject(this, contentView);
		if (!peroid.equals("noData")) {
			mAccountTemporaryUserEditext.setText(cname);
			String str = "";
			for (int j = 0; j < password.length(); j++) {
				if (j % 2 == 1) {
					str += password.charAt(j) + "";
				}
			}
			mAccountTemporaryPSWEdittext.setText(str);
			// mAccountPasswordLinearLayout.setVisibility(View.GONE);
			mAccountTemporaryEffectTextview.setText("20"
					+ Integer.parseInt(peroid.substring(0, 2), 16) + getString(R.string.device_adjust_year_common)
					+ Integer.parseInt(peroid.substring(2, 4), 16) + getString(R.string.home_alarm_message_month)
					+ Integer.parseInt(peroid.substring(4, 6), 16) +getString(R.string.scene_sun)+"  "
					+ Integer.parseInt(peroid.substring(6, 8), 16) + ":"
					+ Integer.parseInt(peroid.substring(8, 10), 16));
			mAccountTemporaryUneffectTextview.setText("20"
					+ Integer.parseInt(peroid.substring(12, 14), 16) + getString(R.string.device_adjust_year_common)
					+ Integer.parseInt(peroid.substring(14, 16), 16) + getString(R.string.home_alarm_message_month)
					+ Integer.parseInt(peroid.substring(16, 18), 16) + getString(R.string.scene_sun)+"  "
					+ Integer.parseInt(peroid.substring(18, 20), 16) + ":"
					+ Integer.parseInt(peroid.substring(20, 22), 16));
			mAccountTemporaryEffectTextview.setClickable(false);
			mAccountTemporaryEffectTextview.setOnClickListener(null);
			mAccountTemporaryUneffectTextview.setClickable(false);
			mAccountTemporaryUneffectTextview.setOnClickListener(null);
		} else {
		}
		return contentView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);
		mAccountTemporaryPSWEdittext.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				if(mAccountTemporaryPSWEdittext.getText().toString().trim().length()>6){
					showResult(getString(R.string.smartLock_add_Casual_users_password_placehold_hint));
					mAccountTemporaryPSWEdittext.setText(mAccountTemporaryPSWEdittext.getText().toString().trim().substring(0,6));
				}
			}
		});
		mAccountTemporaryUserEditext.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
				if(mAccountTemporaryUserEditext.getText().toString().trim().length()>5){
					showResult(getString(R.string.smartLock_add_Casual_users_username_placehold_hint));
					mAccountTemporaryUserEditext.setText(mAccountTemporaryUserEditext.getText().toString().trim().substring(0,5));
				}
			}
		});
		if (peroid.equals("noData")) {
			mAccountTemporaryTimeViewEnsureLinearLayout.setOnClickListener(this);
			mAccountTemporaryTimeViewCancleLinearLayout.setOnClickListener(this);
			mAccountTemporaryEffectTextview.setOnClickListener(this);
			mAccountTemporaryUneffectTextview.setOnClickListener(this);
			endView = new DeviceDoorLockAccountTemporaryTimeView(mContext);
			mAccountTemporaryTimeViewLinearLayout.addView(endView);
		}else{
			mAccountTemporaryPSWEdittext.setKeyListener(null);
			mAccountTemporaryUserEditext.setKeyListener(null);
		}

	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}

	private void intiBar() {
		// TODO Auto-generated method stub
		mActivity.resetActionMenu();
		getSupportActionBar().setDisplayShowMenuTextEnabled(true);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setIconText(getString(R.string.device_lock_user_manage));
		getSupportActionBar().setRightIconText("");
		getSupportActionBar().setTitle(getString(R.string.device_lock_user_temp));
		if (peroid.equals("noData")) {
			getSupportActionBar().setRightIconText(getString(R.string.set_save));
			getSupportActionBar().setRightMenuClickListener(
					new OnRightMenuClickListener() {
						@Override
						public void onClick(View v) {
							if (allRight()) {
								String peroidString = "";
								peroidString = timeEffectString.get(0)
										.substring(2)
										+ timeEffectString.get(1)
										+ timeEffectString.get(2)
										+ timeEffectString.get(3)
										+ timeEffectString.get(4)
										+ "00"
										+ timeUneffectString.get(0)
												.substring(2)
										+ timeUneffectString.get(1)
										+ timeUneffectString.get(2)
										+ timeUneffectString.get(3)
										+ timeUneffectString.get(4) + "00";
								String userName = mAccountTemporaryUserEditext
										.getText().toString().trim();
								// TODO Auto-generated method stub
								// if (userID.equals("noData")) {
								JSONObject jsonObject = new JSONObject();
								jsonObject.put("token", mToken);
								jsonObject.put("userType", "2");
								jsonObject.put("password",
										mAccountTemporaryPSWEdittext.getText()
												.toString().trim());
								jsonObject.put("peroid", peroidString);
								jsonObject.put("cname", userName);
								mDialogManager.showDialog(
										NEW_DOOR_LOCK_ACCOUNT_TEMPORARY_KEY,
										mActivity, null, null);
								NetSDK.sendSetDoorLockData(gwID, devID,
										"2", jsonObject);
								// } else {
								// JSONObject jsonObject = new JSONObject();
								// jsonObject.put("token", mToken);
								// jsonObject.put("userID", userID);
								// jsonObject.put("userType", "2");
								// jsonObject.put("password",
								// mAccountTemporaryPSWEdittext.getText()
								// .toString().trim());
								// jsonObject.put("cname", userName);
								// jsonObject.put("peroid", peroidString);
								// mDialogManager.showDialog(
								// NEW_DOOR_LOCK_ACCOUNT_TEMPORARY_KEY,
								// mActivity, null, null);
								// NetSDK.sendSetDoorLockData(gwID, null, devID,
								// "4", jsonObject);
								// }

							}
						}
					});
		}

		getSupportActionBar().setLeftIconClickListener(
				new OnLeftIconClickListener() {
					@Override
					public void onClick(View v) {
						mActivity.finish();
					}
				});
	}

	public void onEventMainThread(NewDoorLockEvent event) {
		String showResult = "";
		System.out.println("event.gwID = " + event.gwID + "event.cmdtype = "
				 + "event.data = " + event.data.toString());
		if (StringUtil.equals(event.operType, "2")) {

			switch (event.data.getString("result")) {
			case "-1":
				showResult = getString(R.string.device_lock_op_addUser_Fail);
				break;
			case "0":
				showResult = getString(R.string.scene_save_task_success);
				((Activity)mContext).finish();
				break;
			case "2":
				showResult = getString(R.string.device_state_password_mistake);
				break;
			case "3":
				showResult = getString(R.string.device_lock_op_psd_already);
				break;
			}
			mDialogManager.dimissDialog(NEW_DOOR_LOCK_ACCOUNT_TEMPORARY_KEY, 0);
			showResult(showResult);
		} else if (StringUtil.equals(event.operType, "4")) {

			switch (event.data.getString("result")) {
			case "-1":
				showResult = getString(R.string.device_account_modify_password_fail);
				break;
			case "0":
				showResult = getString(R.string.device_E4_change_success);
				break;
			case "2":
				showResult = getString(R.string.device_state_password_mistake);
				break;
			case "3":
				showResult = getString(R.string.device_lock_op_psd_already);
				break;
			}
			mDialogManager.dimissDialog(NEW_DOOR_LOCK_ACCOUNT_TEMPORARY_KEY, 0);
			showResult(showResult);
		}
	}

	private void showResult(String showResult) {
		// 弹出含有动态密码的对话框
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		builder.setTitle(getString(R.string.gateway_router_setting_dialog_toast));
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(
				R.layout.device_door_lock_setting_account_dynamic, null);
		TextView textView = (TextView) view
				.findViewById(R.id.device_new_door_lock_account_dynamic_textview);
		textView.setText(showResult);
		builder.setContentView(view);
		builder.setPositiveButton(null);
		builder.setNegativeButton(null);
		WLDialog mMessageDialog = builder.create();
		mMessageDialog.show();
	}

	protected boolean allRight() {
		// Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
		if (mAccountTemporaryPSWEdittext.getText().toString().trim().equals("")
				|| mAccountTemporaryUserEditext.getText().toString().trim()
						.equals("")
				|| mAccountTemporaryEffectTextview.getText().toString().trim()
						.equals("")
				|| mAccountTemporaryUneffectTextview.getText().toString()
						.trim().equals("")) {
//			showWarnnig("请输入完整用户信息！");device_lock_op_input_AllInfo
			showWarnnig(getString(R.string.device_lock_op_input_AllInfo));
			return false;
		} else if (mAccountTemporaryPSWEdittext.getText().toString().trim()
				.length() != 6) {
//			showWarnnig("请输入六位数字密码");device_lock_op_input_AllInfo
			showWarnnig(getString(R.string.ow_set_password_enter_6_digits));
			return false;
		} else if (mAccountTemporaryUserEditext.getText().toString().trim()
				.length() <1) {
//			showWarnnig("1-5位英文字母");
			showWarnnig(getString(R.string.ow_set_name_1_5_digits_letters));
			return false;
		} else if (!isSixNumber(mAccountTemporaryPSWEdittext.getText()
				.toString().trim())) {
//			showWarnnig("请输入六位数字密码");
			showWarnnig(getString(R.string.ow_set_password_enter_6_digits));
			return false;
		} else if (!compare_date(timeEffectString.get(0) + "-"
				+ timeEffectString.get(1) + "-" + timeEffectString.get(2) + " "
				+ timeEffectString.get(3) + ":" + timeEffectString.get(4),
				new Date(System.currentTimeMillis()))) {
//			showWarnnig("生效时期必须大于当前时间");
			showWarnnig(getString(R.string.ow_set_password_valid_time));
			return false;
		} else if (!compare_date(
				timeUneffectString.get(0) + "-" + timeUneffectString.get(1)
						+ "-" + timeUneffectString.get(2) + " "
						+ timeUneffectString.get(3) + ":"
						+ timeUneffectString.get(4),
				timeEffectString.get(0) + "-" + timeEffectString.get(1) + "-"
						+ timeEffectString.get(2) + " "
						+ timeEffectString.get(3) + ":"
						+ timeEffectString.get(4))) {
			// compare_date("1995-11-12 15:21", "1999-12-11 09:59");
//			showWarnnig("失效日期必须大于生效日期");
			showWarnnig(getString(R.string.device_lock_op_endTimeRequireLargeStartTime));
			return false;
		}
		return true;

	}

	private boolean compare_date(String DATE2, Date date) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		try {
			Date dt2 = df.parse(DATE2);
			if (dt2.getTime() > date.getTime()) {
				return true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}

	private boolean isSixNumber(String str) {
		for (int i = 0; i < str.length(); i++) {
			if ((int) (str.charAt(i)) - 48 < 0
					|| (int) (str.charAt(i)) - 48 > 9) {
				return false;
			}
		}
		return true;
	}

	public boolean compare_date(String DATE1, String DATE2) {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		try {
			Date dt1 = df.parse(DATE1);
			Date dt2 = df.parse(DATE2);
			if (dt1.getTime() > dt2.getTime()) {
				return true;
			}
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return false;
	}

	private void showWarnnig(String showResult) {
		// 弹出含有动态密码的对话框
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		builder.setTitle(getString(R.string.gateway_router_setting_dialog_toast));
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(
				R.layout.device_door_lock_setting_account_dynamic, null);
		TextView textView = (TextView) view
				.findViewById(R.id.device_new_door_lock_account_dynamic_textview);
		textView.setText(showResult);

		builder.setContentView(view);
		builder.setPositiveButton(null);
		builder.setNegativeButton(null);
		WLDialog mMessageDialog = builder.create();
		mMessageDialog.show();
	}

	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.device_new_door_lock_temporary_time_ensure_tv:
			if (timePosition == EFFECT_TEXTVIEW) {
				timeEffectString.clear();
				mAccountTemporaryEffectTextview.setText(endView
						.getSettingYearTime()
						+ getString(R.string.device_adjust_year_common)
						+ endView.getSettingMonthTime()
						+ getString(R.string.home_alarm_message_month)
						+ endView.getSettingDayTime()
						+ getString(R.string.scene_sun)+"  "
						+ endView.getSettingHourTime()
						+ ":"
						+ endView.getSettingMinuesTime());
				timeEffectString.add(endView.getSettingYearTime());
				timeEffectString.add(endView.getSettingMonthTime());
				timeEffectString.add(endView.getSettingDayTime());
				timeEffectString.add(endView.getSettingHourTime());
				timeEffectString.add(endView.getSettingMinuesTime());
			} else if (timePosition == UNEFFECT_TEXTVIEW) {
				timeUneffectString.clear();
				mAccountTemporaryUneffectTextview.setText(endView
						.getSettingYearTime()
						+ getString(R.string.device_adjust_year_common)
						+ endView.getSettingMonthTime()
						+ getString(R.string.home_alarm_message_month)
						+ endView.getSettingDayTime()
						+ getString(R.string.scene_sun)+"  "
						+ endView.getSettingHourTime()
						+ ":"
						+ endView.getSettingMinuesTime());
				timeUneffectString.add(endView.getSettingYearTime());
				timeUneffectString.add(endView.getSettingMonthTime());
				timeUneffectString.add(endView.getSettingDayTime());
				timeUneffectString.add(endView.getSettingHourTime());
				timeUneffectString.add(endView.getSettingMinuesTime());
			}

			mAccountTemporaryTimeViewLinearLayout.setVisibility(View.INVISIBLE);
			mAccountTemporaryTimeViewShowLinearLayout
					.setVisibility(View.INVISIBLE);
			break;
		case R.id.device_new_door_lock_temporary_time_cancle_tv:
			mAccountTemporaryTimeViewLinearLayout.setVisibility(View.INVISIBLE);
			mAccountTemporaryTimeViewShowLinearLayout
					.setVisibility(View.INVISIBLE);
			break;
		case R.id.device_new_door_lock_temporary_effect_tv:
			mAccountTemporaryTimeViewLinearLayout.setVisibility(View.VISIBLE);
			mAccountTemporaryTimeViewShowLinearLayout
					.setVisibility(View.VISIBLE);
			timePosition = EFFECT_TEXTVIEW;
			break;
		case R.id.device_new_door_lock_temporary_uneffect_tv:
			if (timePosition == NOCHOOSE
					&& mAccountTemporaryEffectTextview.getText().equals("")) {
				showWarnnig(getString(R.string.device_lock_op_input_StartTime));
			} else {
				mAccountTemporaryTimeViewLinearLayout
						.setVisibility(View.VISIBLE);
				mAccountTemporaryTimeViewShowLinearLayout
						.setVisibility(View.VISIBLE);
				timePosition = UNEFFECT_TEXTVIEW;
			}
			break;
		}
	}
}
