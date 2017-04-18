
package cc.wulian.app.model.device.impls.controlable.doorlock;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.utils.SpannableUtil;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.utils.InputMethodUtils;
import cc.wulian.smarthomev5.utils.LanguageUtil;

/**
 * <p>位1:控制状态 
 * 			1:解锁,2:上锁,3:解锁3s</p>
 * <p>位2～3:保险状态 
 * 		 10:上保险,11:解除保险</p> 
 * <p>位4～5:锁状态 
 * 		 21:锁已开,22:锁已关,23:入侵报警</p>
 */
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK}, 
		category = Category.C_SECURITY)
public class WL_67_DoorLock_1 extends AbstractDoorLock
{
	
	private static final int[] DEVICE_STATE_SPLIT_ARR_0_1 = {0, 1};
	private static final int[] DEVICE_STATE_SPLIT_ARR_1_3 = {1, 3};
	private static final int[] DEVICE_STATE_SPLIT_ARR_3_5 = {3, 5};
	protected static final int 		BIG_OPEN_ALARM_D 				= R.drawable.device_door_lock_ids_big;
	private ImageView mDoorCenterView;
	private ImageView mDoorRightView;
	private EditText mDoorLockPWEditText;
	private TextView mErrorView;
	private TextView mEnsurePWTextView;
	private LinearLayout mDoorLockPWLayout;
	private LinearLayout mDoorLockedLayout;
	public WL_67_DoorLock_1( Context context, String type )
	{
		super(context, type);
	}
	
	@Override
	public String[] getDoorLockEPResources() {
		return new String[]{};
	}
	@Override
	public String getAlarmProtocol() {
		return DEVICE_STATE_23;
	}
	@Override
	public String getNormalProtocol() {
		return null;
	}
	@Override
	public boolean isDestory() {
		return false;
	}
	@Override
	public boolean isLowPower() {
		return false;
	}
	@Override
	public boolean isAlarming(){
		return isAlarm() || isOpened() || isUnLocked() || isLocked() || isOpend3S() || isClosed();
	}
	
	public boolean isAlarm(){
		if (isNull(epData)) return false;

		String lockState = substring(epData, DEVICE_STATE_SPLIT_ARR_3_5);
		return isSameAs(getAlarmProtocol(), lockState);
	}
	@Override
	public boolean isNormal(){
		if (isNull(epData)) return false;

		String lockState = substring(epData, DEVICE_STATE_SPLIT_ARR_3_5);
		return !isSameAs(DEVICE_STATE_FF, lockState) && !isAlarm();
	}
	@Override
	public boolean isOpened(){
		if (isNull(epData)) return false;

		String openState = substring(epData, DEVICE_STATE_SPLIT_ARR_0_1);

		return isSameAs(DATA_CTRL_STATE_OPEN_1, openState) 
				|| isOpend3S();
	}

	private boolean isOpend3S(){
		if (isNull(epData)) return false;

		String openState = substring(epData, DEVICE_STATE_SPLIT_ARR_0_1);

		return isSameAs(DATA_CTRL_STATE_OPEN_3, openState);
	}
	
	@Override
	public boolean isClosed(){
		if (isNull(epData)) return true;

		String openState = substring(epData, DEVICE_STATE_SPLIT_ARR_0_1);
		return !isSameAs(DEVICE_STATE_FF.substring(0, 1), openState) && !isOpened();
	}

	@Override
	public boolean isStateUnknow(){
		if (isNull(epData)) return true;

		String openState = substring(epData, DEVICE_STATE_SPLIT_ARR_0_1);
		return isSameAs(DEVICE_STATE_FF.substring(0, 1), openState);
	}
	
	@Override
	public boolean isSecureLocked(){
		if (isNull(epData)) return true;

		String secureState = substring(epData, DEVICE_STATE_SPLIT_ARR_1_3);
		return isSameAs(DEVICE_STATE_10, secureState);
	}
	
	@Override
	public boolean isSecureUnLocked(){
		if (isNull(epData)) return true;

		String secureState = substring(epData, DEVICE_STATE_SPLIT_ARR_1_3);
		return isSameAs(DEVICE_STATE_11, secureState);
	}
	
	@Override
	public boolean isLocked(){
		if (isNull(epData)) return true;

		String lockState = substring(epData, DEVICE_STATE_SPLIT_ARR_3_5);
		return isSameAs(DEVICE_STATE_22, lockState);
	}
	
	@Override
	public boolean isUnLocked(){
		if (isNull(epData)) return true;

		String lockState = substring(epData, DEVICE_STATE_SPLIT_ARR_3_5);
		return isSameAs(DEVICE_STATE_21, lockState);
	}
	

	@Override
	public Drawable getStateSmallIcon(){
		return isOpened() ? getDrawable(SMALL_OPEN_D) : 
					 isClosed() ? getDrawable(SMALL_CLOSE_D) : 
				   getDrawable(SMALL_CLOSE_D);
	}

	@Override
	public Drawable[] getStateBigPictureArray(){
		return null;
	}

	@Override
	public CharSequence parseAlarmProtocol(String epData) {
		StringBuilder sb = new StringBuilder();
		sb.append(DeviceTool.getDeviceAlarmAreaName(this));
		sb.append(DeviceTool.getDeviceShowName(this));
		if(LanguageUtil.isChina() || LanguageUtil.isTaiWan()){
			sb.append(mContext.getString(R.string.home_device_alarm_default_voice_detect));
		}else{
			sb.append(" "+ mContext.getString(R.string.home_device_alarm_default_voice_detect) + " ");
		}
//		if(isUnLocked()){
//			sb.append(mContext.getString(R.string.device_alarm_type_doorlock_open));
//		}else if(isLocked()){
//			sb.append(mContext.getString(R.string.device_alarm_type_doorlock_close));
//		}else 
		if(isAlarm()){
			sb.append(mContext.getString(R.string.home_device_alarm_type_doorlock_invasion));
		}else{
			sb.replace(0, sb.length(), "");
		}
		return sb.toString();
	}
	@Override
	public CharSequence parseDataWithProtocol(String epData){
		SpannableStringBuilder ssb = new SpannableStringBuilder();
		
		String state;
		int color;

		state = "";
		color = COLOR_NORMAL_ORANGE;
		int dataInt = StringUtil.toInteger(epData);
		
		switch (dataInt){
			case  1 :
				state = getString(R.string.device_state_unlock);
				color = COLOR_NORMAL_ORANGE;
				break;
			case  2 :
				state = getString(R.string.device_state_lock);
				color = COLOR_NORMAL_ORANGE;
				break;
			case 10 :
				state = getString(R.string.device_state_lock);
				color = COLOR_NORMAL_ORANGE;
				break;
			case 11 :
				state = getString(R.string.device_state_unlock);
				color = COLOR_CONTROL_GREEN;
				break;
			case 21 :
				state = getString(R.string.device_state_unlock);
				color = COLOR_ALARM_RED;
				break;
			case 22 :
				state = getString(R.string.device_state_lock);
				color = COLOR_ALARM_RED;
				break;
			case 23 :
				state = getString(R.string.device_state_alarm_inbreak);
				color = COLOR_ALARM_RED;
				break;
		}

		ssb.append(SpannableUtil.makeSpannable(state, new ForegroundColorSpan(getResources().getColor(color))));
		
		return ssb;
	}
		
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_door_lock_4, container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		mDoorCenterView = (ImageView) view.findViewById(R.id.device_door_lock_big);
		mDoorRightView = (ImageView) view.findViewById(R.id.device_door_lock_small);
		mDoorLockPWEditText = (EditText) view.findViewById(R.id.door_lock_password_edittext);
		mEnsurePWTextView = (TextView) view.findViewById(R.id.ensure_door_password);
		mDoorLockPWLayout = (LinearLayout) view.findViewById(R.id.door_lock_password_layout);
		mDoorLockedLayout = (LinearLayout) view.findViewById(R.id.door_locked_layout);
		mDoorLockedLayout.setVisibility(View.INVISIBLE);
		mEnsurePWTextView.setOnClickListener(viewDoorLoakClickListener);
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		if (!(WL_67_DoorLock_1.this).isDeviceOnLine()) {
			return;
		}
		if (isOpened()){
			mDoorLockPWLayout.setVisibility(View.GONE);
			mDoorLockedLayout.setVisibility(View.VISIBLE);
			mDoorRightView.setVisibility(View.INVISIBLE);
			mDoorLockedLayout.setOnClickListener(viewDoorLoakClickListener);
			mDoorCenterView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_open_big));
		}
		else if (isStateUnknow()){
			mDoorRightView.setVisibility(View.INVISIBLE);
			mDoorCenterView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_close_big));
		}
		else{
			mDoorRightView.setVisibility(View.INVISIBLE);
			mDoorCenterView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_close_big));
		}
		
		if (isAlarm()){
			mDoorCenterView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_ids_big));
			mDoorLockedLayout.setVisibility(View.INVISIBLE);
			mDoorLockPWLayout.setVisibility(View.VISIBLE);
			mDoorLockPWEditText.clearFocus();
			mDoorLockPWEditText.setText("");
			mDoorRightView.setVisibility(View.VISIBLE);
			mDoorRightView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_invasion));
		}
	}
	
	public View.OnClickListener viewDoorLoakClickListener = new View.OnClickListener(){

		@Override
		public void onClick(View v) {
			switch(v.getId()){
			case R.id.ensure_door_password :
				if(confirmPwd()){
					//获取Edittext中的密码并对其加密处理
					String confirmPwd = mDoorLockPWEditText.getText().toString();
					String confirmdoorpwd = MD5Util.encrypt(confirmPwd);
					String savedMD5Pwd = Preference.getPreferences().getString(
							IPreferenceKey.P_KEY_DEVICE_DOOR_LOCK_PWD,
							WINDOWS_PWD_MD5);
					if (confirmdoorpwd.equals(savedMD5Pwd)) {
						createControlOrSetDeviceSendData(1,null, true, -1);
					} else {
						mDoorLockPWEditText.clearFocus();
						mDoorLockPWEditText.setText("");
						// 如果输入法显示,就隐藏
						if (InputMethodUtils.isShow(mContext)) {
							InputMethodUtils.hide(mContext);
						}
						mDoorRightView.setVisibility(View.VISIBLE);
						mDoorRightView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_mistake));
					}
				}
				break;
			case R.id.door_locked_layout :
				createControlOrSetDeviceSendData(1,null, true, -1);
				break;
			default :
				break;
			}
		}
	};
	
	public boolean confirmPwd() {
		if (mDoorLockPWEditText == null
				|| "".equals(mDoorLockPWEditText.getText().toString())) {
			mErrorView = mDoorLockPWEditText;
			mErrorView.requestFocus();
			mErrorView.setError(getResources()
					.getString(R.string.hint_not_null_edittext));
			return false;
		} else {
			return true;
		}
	}

	public Intent getSettingIntent() {
		Intent intent = new Intent(mContext,DeviceSettingActivity.class);
		intent.putExtra(EditDoorLockFragment.GWID, gwID);
		intent.putExtra(EditDoorLockFragment.DEVICEID, devID);
		intent.putExtra(EditDoorLockFragment.DEVICE_DOOR_LOCK_12, type);
		intent.putExtra(AbstractDevice.SETTING_LINK_TYPE, AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
		intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, EditDoorLockFragment.class.getName());
		return intent ;
	}
	
	@Override
	protected List<MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
		List<MenuItem> items = super.getDeviceMenuItems(manager);
		MenuItem settingItem = new MenuItem(mContext) {

			@Override
			public void initSystemState() {
				titleTextView.setText(mContext
						.getString(cc.wulian.smarthomev5.R.string.set_titel));
				iconImageView
						.setImageResource(cc.wulian.smarthomev5.R.drawable.device_setting_more_setting);
			}

			@Override
			public void doSomething() {
				Intent i = getSettingIntent();
				mContext.startActivity(i);
				manager.dismiss();
			}
		};
		if(isDeviceOnLine())
			items.add(settingItem);
		return items;
	}

	
}
