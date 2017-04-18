package cc.wulian.app.model.device.impls.controlable.doorlock;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputFilter;
import android.text.InputType;
import android.text.LoginFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wulian.iot.Config;
import com.wulian.iot.server.receiver.DoorLockReceiver;
import com.wulian.iot.view.device.play.PlayDoorLock_89;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;
import com.yuantuo.netsdk.TKCamHelper;

import java.util.List;
import java.util.Map;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.activity.iotc.config.IOTCDevConfigActivity;
import cc.wulian.smarthomev5.event.DeviceUeiItemEvent;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import de.greenrobot.event.EventBus;

@DeviceClassify(devTypes = { "Bd" }, category = Category.C_SECURITY)
public class WL_Bd_DoorLock_6 extends ControlableDeviceImpl implements
		OnClickListener, Alarmable {

	private static final String TAG = "WL_89_DoorLock_6";
	private static String passwordNumber = "";
	private SharedPreferences sharedPreferences;// 鹰眼专用 通用sharedPreferences 请勿
												// 在子类中随便添加
	private Editor editor;
	private Builder mBuilder;
	private WLDialog mMessageDialogCheck;
	private static Boolean mRight = false;
	private EditText mDialogEditText;
	private TextView mDialogErroeTextView;
	protected StringBuilder sb = new StringBuilder();

	@ViewInject(R.id.password1)
	private Button pointOne;
	@ViewInject(R.id.password2)
	private Button pointTwo;
	@ViewInject(R.id.password3)
	private Button pointThree;
	@ViewInject(R.id.password4)
	private Button pointFour;
	@ViewInject(R.id.password5)
	private Button pointFive;
	@ViewInject(R.id.password6)
	private Button pointSix;

	@ViewInject(R.id.one)
	private Button numberOne;
	@ViewInject(R.id.two)
	private Button numberTwo;
	@ViewInject(R.id.three)
	private Button numberThree;
	@ViewInject(R.id.four)
	private Button numberFour;
	@ViewInject(R.id.five)
	private Button numberFive;
	@ViewInject(R.id.six)
	private Button numberSix;
	@ViewInject(R.id.seven)
	private Button numberSeven;
	@ViewInject(R.id.eight)
	private Button numberEight;
	@ViewInject(R.id.nine)
	private Button numberNine;
	@ViewInject(R.id.del)
	private Button numberDel;
	@ViewInject(R.id.zero)
	private Button numberZero;
	@ViewInject(R.id.ok)
	private Button numberOK;
	@ViewInject(R.id.hawkeye_imageview)
	private ImageView doorLockDefenceView; // hawkeye_lock_relative
	private BaseActivity mActivity;
	@ViewInject(R.id.lock_on_imageview)
	private ImageView lockStatusView;
	@ViewInject(R.id.lock_imageview)
	private ImageView doorLockUndefenceView;
	// 鹰眼通道的相关参数
	private boolean isDoorLockDefenceClick = false;
	private boolean isSendPassword = false;
	private String tutkUid = null;

	public WL_Bd_DoorLock_6(Context context, String type) {
		super(context, type);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		Log.i(TAG, "===onCreateView===");
		View view = inflater.inflate(R.layout.device_door_lock_89, container,
				false);
		view.setFocusableInTouchMode(false);
		ViewUtils.inject(this, view);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		Log.i(TAG, "===onViewCreated===");
		mActivity = (BaseActivity) mContext;
		numberOne.setOnClickListener(this);
		numberTwo.setOnClickListener(this);
		numberThree.setOnClickListener(this);
		numberFour.setOnClickListener(this);
		numberFive.setOnClickListener(this);
		numberSix.setOnClickListener(this);
		numberSeven.setOnClickListener(this);
		numberEight.setOnClickListener(this);
		numberNine.setOnClickListener(this);
		numberDel.setOnClickListener(this);
		numberZero.setOnClickListener(this);
		numberOK.setOnClickListener(this);
		doorLockDefenceView.setOnClickListener(this);
		doorLockUndefenceView.setOnClickListener(this);
		doorLockDefenceView.setImageDrawable(getResources().getDrawable(
				R.drawable.setting_lock_protect_normal));
		doorLockUndefenceView.setImageDrawable(getResources().getDrawable(
				R.drawable.setting_lock_unprotect_normal));
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.i(TAG, "===onPause===");
		EventBus.getDefault().unregister(this);
	}

	@Override
	public void onResume() {
		Log.i(TAG, "===onResume===");
		passwordNumber = "";
		SendMessage
				.sendControlDevMsg(gwID, devID, ep, epType, "34");
	}

	@Override
	public void initViewStatus() {
		// TODO Auto-generated method stub
		super.initViewStatus();
		Log.i(TAG, "===initViewStatus===");
		Log.i(TAG, "=======epData===:" + epData);
		if (epData == null) {
			epData = "";
		}
		// 管理员验证
		if (isCheckAdminRight() && mRight) {
			Intent intent = getSettingIntent();
			mContext.startActivity(intent);
			mRight = false;
			mMessageDialogCheck.dismiss();
		} else if (isCheckAdminWrong() && mRight) {
			mDialogEditText.setText("");
			mDialogErroeTextView.setVisibility(View.VISIBLE);
		}
		// 设防与撤防
		if (epStatus.equals("1")) {
			if (isDoorLockDefenceClick) {
				showResult(getString(R.string.smartLock_defend_success));
				isDoorLockDefenceClick = false;
			}
			doorLockDefenceView.setImageDrawable(getResources().getDrawable(
					R.drawable.setting_lock_protect));
			doorLockUndefenceView.setImageDrawable(getResources().getDrawable(
					R.drawable.setting_lock_unprotect_normal));
		} else if (epStatus.equals("0")) {
			if (isDoorLockDefenceClick) {
				showResult(getString(R.string.smartLock_undefend_success));
				isDoorLockDefenceClick = false;
			}
			doorLockDefenceView.setImageDrawable(getResources().getDrawable(
					R.drawable.setting_lock_protect_normal));
			doorLockUndefenceView.setImageDrawable(getResources().getDrawable(
					R.drawable.setting_lock_unprotect));
		}
		// 门锁开关信息上报
		if (isClose()) {
			lockStatusView.setImageDrawable(getResources().getDrawable(
					R.drawable.lock_on));
		} else if (isOpen()) {
			Log.i(TAG, "===开锁成功===");
			passwordNumber = "";
			addPoint(passwordNumber.length());
			lockStatusView.setImageDrawable(getResources().getDrawable(
					R.drawable.lock_off));
			return;
		}
		// 反锁与解除反锁
		if (isAntiLock()) {
			lockStatusView.setImageDrawable(getResources().getDrawable(
					R.drawable.lock_antilock));
		} else if (isDissolveAntiLock()) {
			lockStatusView.setImageDrawable(getResources().getDrawable(
					R.drawable.lock_on));
		}
		// 门锁信息上报
		if (DoorLockStatusReport()) {
			changeViewStatus(epData);
		}

		if (isAppPasswordWrong() && isSendPassword) {
			isSendPassword = false;
			showResult(getString(R.string.camera_settings_wrong_password));
			passwordNumber = "";
			addPoint(passwordNumber.length());
		}

		if (isDoorClosed()) {
			WLToast.showToast(mActivity, getString(R.string.smartLock_door_close), WLToast.TOAST_SHORT);
		} else if (isDoorUnClosed()) {
			WLToast.showToast(mActivity, getString(R.string.smartLock_door_without_close), WLToast.TOAST_SHORT);
		}

	}

	private void showResult(String showResult) {
		// 弹出含有动态密码的对话框
		Builder builder = new Builder(mActivity);
		builder.setTitle(getString(R.string.gateway_router_setting_dialog_toast));
		LayoutInflater inflater = LayoutInflater.from(mActivity);
		View view = inflater.inflate(
				R.layout.device_door_lock_setting_account_dynamic, null);
		TextView textView = (TextView) view
				.findViewById(R.id.device_new_door_lock_account_dynamic_textview);
		textView.setText(showResult);

		builder.setContentView(view);
		builder.setPositiveButton(getString(R.string.html_disk_format_confirm));
		builder.setNegativeButton(null);
		WLDialog mMessageDialog = builder.create();
		mMessageDialog.show();
	}

	private void changeViewStatus(String epData) {
		String antiLockStatus = epData.substring(8, 10);
		if (antiLockStatus.equals("01")) {
			lockStatusView.setImageDrawable(getResources().getDrawable(
					R.drawable.lock_antilock));
		} else {
			lockStatusView.setImageDrawable(getResources().getDrawable(
					R.drawable.lock_on));
		}
	}

	// gwID devID 绑定设备 跳转到另一个Activity
	public Intent getSettingIntent() {
		Intent intent = new Intent(mContext, DeviceSettingActivity.class);
		intent.putExtra(EditDoorLock7Fragment.GWID, gwID);
		intent.putExtra(EditDoorLock7Fragment.DEVICEID, devID);
		intent.putExtra(EditDoorLock7Fragment.DEVICE_DOOR_LOCK_TYPE, type);
		intent.putExtra(AbstractDevice.SETTING_LINK_TYPE,
				AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
		intent.putExtra(EditDoorLock7Fragment.TOKEN, epData.substring(4));
		intent.putExtra(Config.tutkUid, tutkUid);
		intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME,
				EditDoorLock7Fragment.class.getName());
		return intent;
	}

	@SuppressLint("NewApi")
	private void addPoint(int length) {
		switch (length) {
		case 0:
			pointOne.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointTwo.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointThree.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointFour.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointFive.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointSix.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			break;
		case 1:
			pointOne.setBackground(getResources().getDrawable(R.drawable.point));
			pointTwo.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointThree.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointFour.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointFive.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointSix.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			break;
		case 2:
			pointOne.setBackground(getResources().getDrawable(R.drawable.point));
			pointTwo.setBackground(getResources().getDrawable(R.drawable.point));
			pointThree.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointFour.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointFive.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointSix.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			break;
		case 3:
			pointOne.setBackground(getResources().getDrawable(R.drawable.point));
			pointTwo.setBackground(getResources().getDrawable(R.drawable.point));
			pointThree.setBackground(getResources().getDrawable(
					R.drawable.point));
			pointFour.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointFive.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointSix.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			break;
		case 4:
			pointOne.setBackground(getResources().getDrawable(R.drawable.point));
			pointTwo.setBackground(getResources().getDrawable(R.drawable.point));
			pointThree.setBackground(getResources().getDrawable(
					R.drawable.point));
			pointFour.setBackground(getResources()
					.getDrawable(R.drawable.point));
			pointFive.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			pointSix.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			break;
		case 5:
			pointOne.setBackground(getResources().getDrawable(R.drawable.point));
			pointTwo.setBackground(getResources().getDrawable(R.drawable.point));
			pointThree.setBackground(getResources().getDrawable(
					R.drawable.point));
			pointFour.setBackground(getResources()
					.getDrawable(R.drawable.point));
			pointFive.setBackground(getResources()
					.getDrawable(R.drawable.point));
			pointSix.setBackground(getResources().getDrawable(
					R.drawable.ic_action_tick_on));
			break;
		case 6:
			pointOne.setBackground(getResources().getDrawable(R.drawable.point));
			pointTwo.setBackground(getResources().getDrawable(R.drawable.point));
			pointThree.setBackground(getResources().getDrawable(
					R.drawable.point));
			pointFour.setBackground(getResources()
					.getDrawable(R.drawable.point));
			pointFive.setBackground(getResources()
					.getDrawable(R.drawable.point));
			pointSix.setBackground(getResources().getDrawable(R.drawable.point));
			break;
		default:
			break;
		}
	}

	@Override
	public void onClick(View arg0) {
		if (arg0 == doorLockDefenceView) {
			isDoorLockDefenceClick = true;
			doorLockUndefenceView.setImageDrawable(getResources().getDrawable(R.drawable.setting_lock_protect));
			NetSDK.sendSetDevMsg(gwID, CmdUtil.MODE_SWITCH, devID, null, null, null, null, ep, epType, null, "1", null, null,null);
		}
		if (passwordNumber.length() < 6) {
			if (arg0 == numberOne) {
				changePasswordNumber("1");
			} else if (arg0 == numberTwo) {
				changePasswordNumber("2");
			} else if (arg0 == numberThree) {
				changePasswordNumber("3");
			} else if (arg0 == numberFour) {
				changePasswordNumber("4");
			} else if (arg0 == numberFive) {
				changePasswordNumber("5");
			} else if (arg0 == numberSix) {
				changePasswordNumber("6");
			} else if (arg0 == numberSeven) {
				changePasswordNumber("7");
			} else if (arg0 == numberEight) {
				changePasswordNumber("8");
			} else if (arg0 == numberNine) {
				changePasswordNumber("9");
			} else if (arg0 == numberZero) {
				changePasswordNumber("0");
			} else if (arg0 == doorLockUndefenceView) {
				isDoorLockDefenceClick = true;
				doorLockUndefenceView.setImageDrawable(getResources().getDrawable(R.drawable.setting_lock_unprotect));
				NetSDK.sendSetDevMsg(gwID, CmdUtil.MODE_SWITCH, devID, null, null, null, null, ep, epType, null, "0", null, null,null);
			}
		}
		if (arg0 == numberDel) {
			passwordNumber = "";
		} else if (arg0 == numberOK) {
			if (passwordNumber.length() > 0) {
				passwordNumber = passwordNumber.substring(0,
						passwordNumber.length() - 1);
			}
		}
		if (passwordNumber.length() == 6) {
			isSendPassword = true;
			createControlOrSetDeviceSendData(1, "11" + passwordNumber.length()
					+ passwordNumber, true);
			// 密码长度回复初始
		}
		// 更新密码个数的显示
		addPoint(passwordNumber.length());
		System.out.println("-----------pass" + passwordNumber);
	}

	// 更改密码
	private void changePasswordNumber(String string) {
		passwordNumber += string;
	}

	// 增添列表选项
	@Override
	protected List<MenuItem> getDeviceMenuItems(
			final MoreMenuPopupWindow manager) {
		List<MenuItem> items = super.getDeviceMenuItems(manager);
		MenuItem settingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext
						.getString(R.string.set_titel));
				iconImageView
						.setImageResource(R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				// Intent intent = getSettingIntent();
				// mContext.startActivity(intent);
				creatCheckAdminDialog();

				manager.dismiss();
			}
		};
		if (isDeviceOnLine())
			items.add(settingItem);
		return items;
	}

	// 弹出dialog进行管理员密码输入验证
	public void creatCheckAdminDialog() {
		mBuilder = new Builder(DeviceDetailsActivity.instance);
		// LayoutInflater inflater = (LayoutInflater) mContext
		// .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		final View view = LayoutInflater.from(mContext).inflate(
				R.layout.device_door_lock_89_check_admin_dialog, null);
		mDialogEditText = (EditText) view
				.findViewById(R.id.device_new_door_lock_dialog_edittext);
		mDialogErroeTextView = (TextView) view
				.findViewById(R.id.device_new_door_lock_dialog_error_tv);
		mDialogEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
		// 设置EditText中的数字显示与否
		mDialogEditText.setTransformationMethod(PasswordTransformationMethod
				.getInstance());
		// 输入的限制
		mDialogEditText.setFilters(new InputFilter[] {
				new InputFilter.LengthFilter(16),
				new LoginFilter.PasswordFilterGMail() });
		mBuilder.setContentView(view)
				.setDismissAfterDone(false)
				.setPositiveButton(getResources().getString(R.string.common_ok))
				.setNegativeButton(getResources().getString(R.string.cancel))
				.setListener(new MessageListener() {
					@Override
					public void onClickPositive(View contentViewLayout) {
						// 判断管理员密码是否正确

						String confirmPwd = mDialogEditText.getText()
								.toString();
						mRight = true;
						// 发送管理员账户密码
						// createControlOrSetDeviceSendData(1, "1500116"
						// + "123456", true);
						createControlOrSetDeviceSendData(1, "1500116"
								+ confirmPwd, true);

					}

					@Override
					public void onClickNegative(View contentViewLayout) {
					}
				});
		mMessageDialogCheck = mBuilder.create();
		mMessageDialogCheck.show();
	}

	@Override
	public boolean isAlarming() {
		return isDestory() || isLowPower() || isSerialError()
				|| isRemoveLock() || isOpen()
				|| isAntiPrizing() || isAntiLock() || isOpened()
				|| isDissolveAntiLock() || isAntiStress()
				|| isPeopleStay() || isClosed()
				|| isKnockDoor() || isSmashDoor() || isDoorBellRing()
				|| isDoorClosed() || isDoorUnClosed() || setSecure()
				|| dismissSecure();
	}

	@Override
	public void onDeviceUp(DeviceInfo devInfo) {
		super.onDeviceUp(devInfo);
		createControlOrSetDeviceSendData(1, "34", true);
	}

	@Override
	public boolean isNormal() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getCancleAlarmProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlarmProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNormalProtocol() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAlarmString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNormalString() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isDestory() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		StringBuilder sb = new StringBuilder();
		String temporaryEpData = this.epData;
		this.epData = epData;
		if (isLowPower()) {
			sb.append(getString(R.string.home_message_low_power_warn));
		} else if (isAntiPrizing()) {
			sb.append(getString(R.string.home_device_alarm_type_doorlock_destroy));
		} else if (isAntiLock()) {
			sb.append(getString(R.string.device_state_unlock_reverse));
		} else if (isDissolveAntiLock()) {
			sb.append(getString(R.string.device_state_lock_remove));
		} else if (isAntiStress()) {
			sb.append(epData.substring(10) +getString(R.string.device_user)+ getString(R.string.home_device_alarm_type_32_voice));
		} else if (isClose()) {
			sb.append(getString(R.string.device_state_lock));
		} else if (epData.startsWith("0808")) {
			String cName=epData.substring(10);
			int number=Integer.parseInt(epData.substring(6, 8), 16);
			String numString;
			if(StringUtil.isNullOrEmpty(cName)){
				numString=number>9?number+"":"0"+number;
			}else{
				numString=cName;
			}
			switch (epData.substring(4, 6)) {
			case "00":
				sb.append(getString(R.string.device_lock_user_manager)+ numString);
				break;
			case "01":
				sb.append(getString(R.string.device_lock_user_common) + numString);
				break;
			case "02":
				sb.append(getString(R.string.device_lock_user_temp) + numString);
				break;
			}
			switch (epData.substring(8, 10)) {
			case "00":
				sb.append(getString(R.string.device_alarm_type_doorlock_pwd));
				break;
			case "01":
				sb.append(getString(R.string.home_device_alarm_type_doorlock_finger));
				break;
			case "02":
				sb.append(getString(R.string.home_device_alarm_type_doorlock_card));
				break;
			case "03":
				sb.append(getString(R.string.home_device_alarm_type_doorlock_app));
				break;
			}
		} else if (isSerialError()) {
			sb.append(getString(R.string.device_lock_op_system_locked));
		} else if (isRemoveLock()) {
			sb.append(getString(R.string.device_lock_op_lift_system_locked));
		} else if (isPeopleStay()) {
			sb.append(getString(R.string.smartLock_setting_stay_detection_someone_hint));
		}  else if (isKnockDoor()) {
			sb.append(getString(R.string.smartLock_knock_at_door));
		} else if (isSmashDoor()) {
			sb.append(getString(R.string.smartLock_pound_at_door));
		} else if (isDoorBellRing()) {
			sb.append(getString(R.string.smartLock_doorbell));
		} else if (isDoorClosed()) {
			sb.append(getString(R.string.smartLock_door_close));
		} else if (isDoorUnClosed()) {
			sb.append(getString(R.string.smartLock_door_without_close));
		} else if (setSecure()) {
			sb.append(getString(R.string.smartLock_safing_lock_door));
		} else if (dismissSecure()) {
			sb.append(getString(R.string.smartLock_safing_unlock_door));
		}
		this.epData = temporaryEpData;
		return sb;
	}

	@Override
	public CharSequence parseAlarmProtocol(String epData) {
		String temporaryEpData = this.epData;
		this.epData = epData;
		sb.replace(0, sb.length(), "");
		sb.append(DeviceTool.getDeviceAlarmAreaName(this));
		sb.append(DeviceTool.getDeviceShowName(this));
		if (LanguageUtil.isChina() || LanguageUtil.isTaiWan()) {
			sb.append(mContext
					.getString(R.string.home_device_alarm_default_voice_detect));
		} else {
			sb.append(" "
					+ mContext
							.getString(R.string.home_device_alarm_default_voice_detect)
					+ " ");
		}
		if (isLowPower()) {
			sb.append(getString(R.string.home_message_low_power_warn));
		} else if (isAntiPrizing()) {
			sb.append(getString(R.string.home_device_alarm_type_doorlock_destroy));
		} else if (isAntiLock()) {
			sb.append(getString(R.string.device_state_unlock_reverse));
		} else if (isDissolveAntiLock()) {
			sb.append(getString(R.string.device_state_lock_remove));
		} else if (isAntiStress()) {
			sb.append(epData.substring(10) +getString(R.string.device_user)+ getString(R.string.home_device_alarm_type_32_voice));
		} else if (isClose()) {
			sb.append(getString(R.string.device_state_lock));
		} else if (epData.startsWith("0808")) {
			String cName=epData.substring(10);
			int number=Integer.parseInt(epData.substring(6, 8), 16);
			String numString;
			if(StringUtil.isNullOrEmpty(cName)){
				numString=number>9?number+"":"0"+number;
			}else{
				numString=cName;
			}
			switch (epData.substring(4, 6)) {
				case "00":
					sb.append(getString(R.string.device_lock_user_manager)+ numString);
					break;
				case "01":
					sb.append(getString(R.string.device_lock_user_common) + numString);
					break;
				case "02":
					sb.append(getString(R.string.device_lock_user_temp) + numString);
					break;
			}
			switch (epData.substring(8, 10)) {
				case "00":
					sb.append(getString(R.string.device_alarm_type_doorlock_pwd));
					break;
				case "01":
					sb.append(getString(R.string.home_device_alarm_type_doorlock_finger));
					break;
				case "02":
					sb.append(getString(R.string.home_device_alarm_type_doorlock_card));
					break;
				case "03":
					sb.append(getString(R.string.home_device_alarm_type_doorlock_app));
					break;
			}
		} else if (isSerialError()) {
			sb.append(getString(R.string.device_lock_op_system_locked));
		} else if (isRemoveLock()) {
			sb.append(getString(R.string.device_lock_op_lift_system_locked));
		} else if (isPeopleStay()) {
			sb.append(getString(R.string.smartLock_setting_stay_detection_someone_hint));
		} else if (isKnockDoor()) {
			sb.append(getString(R.string.smartLock_knock_at_door));
		} else if (isSmashDoor()) {
			sb.append(getString(R.string.smartLock_pound_at_door));
		} else if (isDoorBellRing()) {
			sb.append(getString(R.string.smartLock_doorbell));
		} else if (isDoorClosed()) {
			sb.append(getString(R.string.smartLock_door_close));
		} else if (isDoorUnClosed()) {
			sb.append(getString(R.string.smartLock_door_without_close));
		} else if (setSecure()) {
			sb.append(getString(R.string.smartLock_safing_lock_door));
		} else if (dismissSecure()) {
			sb.append(getString(R.string.smartLock_safing_unlock_door));
		}
		this.epData = temporaryEpData;
		return sb;
	}

	// 管理员认证成功
	private boolean isCheckAdminRight() {
		// TODO Auto-generated method stub
		return epData.startsWith("0105");
	}

	//
	private boolean isCheckAdminWrong() {
		// TODO Auto-generated method stub
		return epData.equals("0A11");
	}

	// App密码验证失败
	public boolean isAppPasswordWrong() {
		return epData.equals("0A10");
	};

	public boolean isClose() {
		return epData.equals("020D");
	};

	public boolean isOpen() {
		return epData.startsWith("0808") && epData.length() > 8;
	};

	// 欠压报警
	public boolean isLowPower() {
		return epData.equals("021C");
	}

	// 防撬报警
	public boolean isAntiPrizing() {
		return epData.equals("021D");
	}

	// 反锁报警
	public boolean isAntiLock() {
		return epData.equals("020A");
	}

	// 解除反锁报警
	public boolean isDissolveAntiLock() {
		return epData.equals("020B");
	}

	// 防劫持报警
	public boolean isAntiStress() {
		return epData.startsWith("0809");
	}
/*
	@Override
	public void receiveFrameData(Camera camera, int avChannel, Bitmap bmp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveFrameInfo(Camera camera, int avChannel, long bitRate,
			int frameRate, int onlineNm, int frameCount,
			int incompleteFrameCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveSessionInfo(Camera camera, int resultCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveChannelInfo(Camera camera, int avChannel, int resultCode) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveIOCtrlData(Camera camera, int avChannel,
			int avIOCtrlMsgType, byte[] data) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveFrameDataForMediaCodec(Camera camera, int i,
			byte[] abyte0, int j, int k, byte[] abyte1, boolean flag, int l) {
		// TODO Auto-generated method stub

	}*/

	// 开门连续出错 系统锁定
	public boolean isSerialError() {
		return epData.equals("021F");
	}

	// 解除系统锁定
	public boolean isRemoveLock() {
		return epData.equals("021E");
	}

	// 检测到人员逗留
	public boolean isPeopleStay() {
		return epData.equals("0202");
	}

	// 检测到有人敲门
	public boolean isKnockDoor() {
		return epData.startsWith("020300");
	}

	// 检测到有人敲门
	public boolean isSmashDoor() {
		return epData.startsWith("020301");
	}

	// 开启摄像头成功
	public boolean isOpenCameraSuccess() {
		return epData.equals("0106");
	}

	// 开启摄像头失败
	public boolean isOpenCameraFailure() {
		return epData.equals("0A15");
	}

	// 关闭摄像头成功
	public boolean isCloseCameraSuccess() {
		return epData.equals("0107");
	}

	// 关闭摄像头失败
	public boolean isCloseCameraFailure() {
		return epData.equals("0A16");
	}

	// 门铃
	public boolean isDoorBellRing() {
		return epData.equals("0205");
	}

	// 门关了
	public boolean isDoorClosed() {
		return epData.equals("0206");
	}

	// 门未关好
	public boolean isDoorUnClosed() {
		return epData.equals("0207");
	}

	// 门锁信息上报
	public boolean DoorLockStatusReport() {
		return epData.startsWith("080C");
	}

	// 上保险
	public boolean setSecure() {
		return epData.startsWith("080B00");
	}

	// 解除保险|
	public boolean dismissSecure() {
		return epData.startsWith("080B01");
	}

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(
			DeviceShortCutControlItem item, LayoutInflater inflater) {
		if (item == null) {
			item = new ControlableDeviceShortCutControlItem(
					inflater.getContext());
		}
		item.setWulianDevice(this);
		return item;
	}

	public class ControlableDeviceShortCutControlItem extends
			DeviceShortCutControlItem {
		private LinearLayout controlableLineLayout;
		private ImageView setupImageView;
		private ImageView unSetupImageView;
		private OnClickListener cliclListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v == setupImageView) {
					clickSetup();
				} else if (v == unSetupImageView) {
					clickUnsetup();
				}
			}

		};

		public ControlableDeviceShortCutControlItem(Context context) {
			super(context);
			controlableLineLayout = (LinearLayout) inflater.inflate(
					R.layout.device_short_cut_control_defenseable, null);
			setupImageView = (ImageView) controlableLineLayout
					.findViewById(R.id.device_short_cut_defense_setup_iv);
			unSetupImageView = (ImageView) controlableLineLayout
					.findViewById(R.id.device_short_cut_defense_unsetup_iv);
			setupImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.device_doorlock_ctrl_setup_selector));
			unSetupImageView.setImageDrawable(getResources().getDrawable(
					R.drawable.device_doorlock_ctrl_unsetup_selector));
			controlLineLayout.addView(controlableLineLayout);
		}
		protected void clickClose() {
			NetSDK.sendSetDevMsg(gwID, CmdUtil.MODE_SWITCH, devID,
					null, null, null, null, ep, epType, null, "0", null, null,null);
		}

		protected void clickUnsetup() {
			setupImageView.setSelected(false);
			unSetupImageView.setSelected(true);
			NetSDK.sendSetDevMsg(gwID, CmdUtil.MODE_SWITCH, devID, null, null,
					null, null, ep, epType, null, "0",null,null,null);
		}

		protected void clickSetup() {
			setupImageView.setSelected(true);
			unSetupImageView.setSelected(false);
			NetSDK.sendSetDevMsg(gwID, CmdUtil.MODE_SWITCH, devID, null, null,
					null, null, ep, epType, null, "1",null,null,null);
		}

		@Override
		public void setWulianDevice(WulianDevice device) {
			super.setWulianDevice(device);
			Map<String, DeviceEPInfo> infoMap = device.getDeviceInfo()
					.getDeviceEPInfoMap();
			if (infoMap == null)
				return;
			setupImageView.setOnClickListener(cliclListener);
			unSetupImageView.setOnClickListener(cliclListener);
			if (isDefenseUnSetup()) {
				setupImageView.setSelected(false);
				unSetupImageView.setSelected(true);
			}
			if (isDefenseSetup()) {
				setupImageView.setSelected(true);
				unSetupImageView.setSelected(false);
			}
		}

		protected boolean isDefenseUnSetup() {
			return isSameAs("0", epStatus);
		}

		protected boolean isDefenseSetup() {
			return isSameAs("1", epStatus);
		}
	}

	@Override
	public boolean isAutoControl(boolean isSimple) {
		return false;
	}
}
