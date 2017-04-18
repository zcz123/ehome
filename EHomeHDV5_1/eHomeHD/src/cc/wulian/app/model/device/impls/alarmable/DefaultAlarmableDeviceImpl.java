package cc.wulian.app.model.device.impls.alarmable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.AccountManager;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

public class DefaultAlarmableDeviceImpl extends AlarmableDeviceImpl {

	private ProgressBar mSecurityAlarm;
	private ImageView mSecurityNormal;
	
	public ImageView mSecuritySetUp;
	public ImageView mSecurityUnSetUp;
	private ImageView mRemoveAlarmView;
	private FrameLayout mRemoveAlarmLayout;
	protected AccountManager accountManager = AccountManager.getAccountManger();
	WLDialog dialog = null;
	public DefaultAlarmableDeviceImpl(Context context, String type) {
		super(context, type);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		View view = super.onCreateView(inflater, container, saveState);
		if (view != null)
			return view;
		return inflater.inflate(R.layout.device_security_alarm_common_layout, container, false);
	}
	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);

		mSecurityAlarm = (ProgressBar) view.findViewById(R.id.device_security_alarm);
		mSecurityNormal = (ImageView) view.findViewById(R.id.device_security_normal);
		
		mSecuritySetUp = (ImageView) view.findViewById(R.id.device_security_setup);
		mSecurityUnSetUp = (ImageView) view.findViewById(R.id.device_security_unsetup);
		mRemoveAlarmView = (ImageView) view.findViewById(R.id.device_security_remove_image);
		
		mRemoveAlarmLayout = (FrameLayout) view.findViewById(R.id.device_security_remove);
		
		mSecuritySetUp.setOnClickListener(setUpOnClickListener);
		mSecurityUnSetUp.setOnClickListener(setUpOnClickListener);
		if(!StringUtil.isNullOrEmpty(getCancleAlarmProtocol())){
			mRemoveAlarmLayout.setVisibility(View.VISIBLE);
			mRemoveAlarmView.setVisibility(View.VISIBLE);
			mRemoveAlarmView.setOnClickListener(setUpOnClickListener);
		}

	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();

		Drawable[] stateArr = getStateBigPictureArray();

		Drawable drawable = stateArr[0];
		mSecurityNormal.setImageDrawable(drawable);

		drawable = mSecurityNormal.getDrawable();

		if (isDefenseUnSetup()) {
			mSecurityUnSetUp.setVisibility(View.VISIBLE);
			mSecuritySetUp.setVisibility(View.INVISIBLE);
			mSecurityAlarm.setVisibility(View.INVISIBLE);
		} else {
			mSecuritySetUp.setVisibility(View.VISIBLE);
			mSecurityUnSetUp.setVisibility(View.INVISIBLE);
			if (isNormal()) {
				mSecurityAlarm.setVisibility(View.INVISIBLE);
			} else if (isAlarming()) {
				mSecurityAlarm.setVisibility(View.VISIBLE);
			}
		}
	}
	
	private View.OnClickListener setUpOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.device_security_setup:
				if(isLongDefenSetup()){
					showCheckPasswordDialog();
				}else{
					controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(), null);
				}
				break;
			case R.id.device_security_unsetup:
				controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(), null);
				break;
			case R.id.device_security_remove_image:
				controlDeviceWidthEpData(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(), getCancleAlarmProtocol());
				break;
			default:
				break;
			}

		}
	};
	
	private  void showCheckPasswordDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		builder.setTitle(mContext.getString(R.string.device_songname_refresh_title))
		.setContentView(R.layout.device_security_setup_dialog)
		 .setPositiveButton(android.R.string.ok)
		 .setNegativeButton(android.R.string.cancel)
		 .setDismissAfterDone(false)
		 .setListener(new MessageListener() {
			
			@Override
			public void onClickPositive(View contentViewLayout) {
				//获取Edittext中的密码并对其加密处理
				EditText passwordEditText = (EditText) contentViewLayout.findViewById(R.id.device_setup_editText_input);
				String password = passwordEditText.getText().toString();
				String md5pwd = MD5Util.encrypt(password);
				String gwMD5Pwd = accountManager.getmCurrentInfo().getGwPwd();
				if(StringUtil.isNullOrEmpty(password)){
					WLToast.showToast(mContext, mContext.getResources().getString(R.string.hint_not_null_edittext),WLToast.TOAST_SHORT);
				}
				else if (md5pwd.equals(gwMD5Pwd)) {
					controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(), null);
					dialog.dismiss();
				} else {
					passwordEditText.clearFocus();
					passwordEditText.setText("");
					WLToast.showToast(mContext, mContext.getResources().getString(R.string.hint_pwd_error_edittext),WLToast.TOAST_SHORT);
				}
			}
			
			@Override
			public void onClickNegative(View contentViewLayout) {
			}
			
		});
		dialog = builder.create();
		dialog.show();
	}

}
