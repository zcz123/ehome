package cc.wulian.smarthomev5.fragment.device;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.WlDialogUtil;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.wulian.icam.utils.DialogUtils;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

/**
 * OW门锁无RTC普通密码的下发
 * 
 * @author hxc
 * 
 */
public class NoClockCommonPwFragment extends WulianFragment implements
		android.view.View.OnClickListener {

	@ViewInject(R.id.door_lock_pded)
	private EditText doorlock_password;
	@ViewInject(R.id.doorlock_ensure)
	private Button doorlock_ensure;
	@ViewInject(R.id.choose_pw_status)
	private ImageButton choosepdstatus;
	@ViewInject(R.id.btn_share_password)
	private Button btnShare;

	public static final String GWID = "gwid";
	public static final String DEVICEID = "deviceid";
	private static final String OW_DOOR_LOCK_COMMON_PW = "OW_DOOR_LOCK_COMMON_PW";
	private static DeviceCache deviceCache;
	private String gwID, devID;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBar();
		initEditDevice();
	}

	private void initEditDevice() {
		gwID = getActivity().getIntent().getStringExtra("gwid");
		devID = getActivity().getIntent().getStringExtra("deviceid");
	}

	private void initBar() {
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayIconEnabled(true);
		getSupportActionBar().setDisplayIconTextEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(true);
		getSupportActionBar().setDisplayShowMenuEnabled(false);
		getSupportActionBar().setDisplayShowMenuTextEnabled(false);
		getSupportActionBar().setIconText(
				getResources().getString(R.string.device_ir_back));
		getSupportActionBar().setTitle(
				getResources().getString(R.string.OW_nomaluser_pw));
		getSupportActionBar().setLeftIconClickListener(
				new OnLeftIconClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mActivity.finish();
					}
				});
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.doorlock_common_password2,
				container, false);
		ViewUtils.inject(this, rootView);
		return rootView;
	}

	@Override
	public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onViewCreated(view, savedInstanceState);

		doorlock_ensure.setOnClickListener(this);
		btnShare.setOnClickListener(this);
		choosepdstatus.setOnClickListener(this);
		choosepdstatus.setTag("invisable");
		doorlock_password.addTextChangedListener(new TextWatcher() {

			@SuppressLint("NewApi")
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// TODO Auto-generated method stub
				if (doorlock_password.getText().length() >= 6) {
					doorlock_ensure
							.setBackgroundResource(R.color.action_bar_bg);
				}
				if (doorlock_password.getText().length() < 6) {
					doorlock_ensure.setBackgroundResource(R.color.gray);
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable s) {
				// TODO Auto-generated method stub

			}
		});
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		if (v == doorlock_ensure) {
			SendOncePassword();
		} else if (v == choosepdstatus) {
			switchpressState();
		} else if (v == btnShare){
			String msg = "[物联传感]您好，智能门锁开门密码已修改，密码为:"+doorlock_password.getText();
//			IntentUtil.sendMessage(getActivity(),msg);
			WlDialogUtil.owSharePwdDiaolg(getActivity(),msg);
		}
	}

	public void onEventMainThread(DeviceEvent event) {
		if(!StringUtil.isNullOrEmpty(event.deviceInfo.getDevID())&&event.deviceInfo.getDevID().equals(devID)) {
			deviceCache = DeviceCache.getInstance(getActivity());
			WulianDevice wulianDevice = deviceCache.getDeviceByID(getActivity(),
					gwID, devID);
			String epData = wulianDevice.getDeviceInfo().getDevEPInfo().getEpData();
			mDialogManager.dimissDialog(OW_DOOR_LOCK_COMMON_PW, 0);
			if (epData.equals("0801")) {
				WLToast.showToast(getActivity(),
						getResources().getString(R.string.OW_nomalpw_reset), 1000);
//				getActivity().finish();
			}
			if (epData.equals("0802")) {
				WLToast.showToast(getActivity(),
						getResources().getString(R.string.OW_nomalpw_unsafe), 1000);
//				getActivity().finish();
			}
			if (epData.equals("0804")) {
				WLToast.showToast(getActivity(), getResources().getString(R.string.ow_lock_setting_invalid_clock), 1000);
//				getActivity().finish();
			}
			if (epData.equals("0806")) {
				WLToast.showToast(getActivity(),
						getResources().getString(R.string.OW_nomalpw_addsuccess),
						1000);
				btnShare.setBackgroundResource(R.color.action_bar_bg);
				btnShare.setEnabled(true);
			}
			if (epData.equals("0810")) {
				WLToast.showToast(getActivity(),
						getResources().getString(R.string.OW_nomalpw_addfauiled),
						1000);
//				getActivity().finish();
			}
			if (epData.equals("0223")) {
				WLToast.showToast(getActivity(), getResources().getString(R.string.ow_lock_setting_ordinary_users_to_add_full), 1000);
//				getActivity().finish();
			}

		}
	}

	// 密码显示明文和密文add by huxc
	private void switchpressState() {
		// TODO Auto-generated method stub

		if (choosepdstatus.getTag() == "visable") {
			doorlock_password
					.setTransformationMethod(PasswordTransformationMethod
							.getInstance());
			choosepdstatus

					.setBackgroundResource(R.drawable.dm_router_setting_wifi_pwd_invisibale);

			doorlock_password
					.setSelection(doorlock_password.getText().length());// 将光标移至文字末尾
			choosepdstatus.setTag("invisable");

		} else if (choosepdstatus.getTag() == "invisable") {

			choosepdstatus
					.setBackgroundResource(R.drawable.dm_router_setting_wifi_pwd_visibale);
			doorlock_password
					.setTransformationMethod(HideReturnsTransformationMethod
							.getInstance());
			doorlock_password
					.setSelection(doorlock_password.getText().length());// 将光标移至文字末尾
			choosepdstatus.setTag("visable");
		}

	}

	private void SendOncePassword() {
		String password = doorlock_password.getText().toString();
		int Pwlength = password.length();
		if (Pwlength > 5) {
			String data = "3" + Pwlength + "" + password + "00000000000000000000";
			SendMessage.sendControlDevMsg(gwID, devID, "14", "ow", data);
			mDialogManager.showDialog(OW_DOOR_LOCK_COMMON_PW, mActivity, null,
					null);
		}
	}
}