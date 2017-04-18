package cc.wulian.app.model.device.impls.controlable.doorlock;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.text.LoginFilter;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.yuantuo.customview.ui.CustomToast;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.dao.Command406_DeviceConfigMsg;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.LanguageUtil;

/**
 * 1:解锁(不解析),2:上锁(不解析),
 * 10:上保险,11:解除保险,
 * 20:反锁,
 * 21:锁已开(暂无),22:锁已关(暂无),
 * 23:入侵报警,24:报警解除,25:强制上锁,
 * 26:自动上锁(暂无),27:登记密码(暂无),28:欠压报警,29:破坏报警
 * 30:密码开锁, 31:密码连续出错,
 * 33～52:纽扣1～20开锁,
 * 65～84:指纹1～20开锁,
 * 97～136:射频卡1～40开锁,
 * 138:钥匙开锁
 */
@DeviceClassify(
        devTypes = {ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_4},
        category = Category.C_SECURITY)
public class WL_70_DoorLock_4 extends WL_69_DoorLock_3 {
    private static final String DATA_CTRL_STATE_OPEN_11 = "11";
    private static final String DATA_CTRL_STATE_CLOSE_12 = "12";

    public static final String DATA_CONFIRM_PWD_SUCCESS = "144";
    public static final String DATA_CONFIRM_PWD_FAIL = "145";
    private static final String DOOR_LOCK_ACCOUNT_KEY_RAME = "door_lock_account_key_rame";

    private boolean isUnLockingDoor = false;
    private WLDialog.Builder customerNicknameDialog;
    private View customerNickName;
    private EditText etCustomerNickName;
    private TextView tvNickname;

    public WL_70_DoorLock_4(Context context, String type) {
        super(context, type);
    }

    @Override
    public String getOpenSendCmd() {
        return DATA_CTRL_STATE_OPEN_11;
    }

    @Override
    public String getCloseSendCmd() {
        return DATA_CTRL_STATE_CLOSE_12;
    }


    @Override
    public boolean isLocked() {
        return isSameAs(DATA_CTRL_STATE_CLOSE_2, epData) ||
                isSameAs(DEVICE_STATE_25, epData) ||
                isSameAs(DEVICE_STATE_10, epData);
    }

    @Override
    public void refreshDevice() {
        super.refreshDevice();
        Intent it = new Intent();
        it.setAction("sendepData");
        it.putExtra("epData", epData);
        it.putExtra("epType", epType);
        mContext.sendBroadcast(it);
        Log.e("epdata", epData);
    }

//	//FIXME  newly add
//	@Override
//	public boolean isSecureUnLocked(){
//		return isSameAs(DEVICE_STATE_11, epData) || isSameAs(DATA_CTRL_STATE_OPEN_1, epData);
//	}

    @Override
    public boolean isSecureLocked() {
        return isSameAs(DEVICE_STATE_10, epData) || isSameAs(DATA_CTRL_STATE_CLOSE_2, epData);
    }

    private ImageView mDoorCenterView;
    private ImageView mDoorRightView;
    private EditText mDoorLockPWEditText;
    private TextView mErrorView;
    private TextView mEnsurePWTextView;
    private LinearLayout mDoorLockPWLayout;
    private LinearLayout mDoorLockedLayout;

    //创建四代门锁对应的视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
        return inflater.inflate(R.layout.device_door_lock_4, container, false);
    }

    //视图创建后初始化控件并触发相关事件
    @Override
    public void onViewCreated(View view, Bundle saveState) {
        super.onViewCreated(view, saveState);
        mDoorCenterView = (ImageView) view.findViewById(R.id.device_door_lock_big);
        mDoorCenterView.setImageDrawable(getResources().getDrawable(R.drawable.lock_locked_img));
        mDoorRightView = (ImageView) view.findViewById(R.id.device_door_lock_small);
        mDoorLockPWEditText = (EditText) view.findViewById(R.id.door_lock_password_edittext);
        mDoorLockPWEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mDoorLockPWEditText.setTransformationMethod(PasswordTransformationMethod.getInstance());
        mDoorLockPWEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(16), new LoginFilter.PasswordFilterGMail()});
        mDoorLockPWEditText.setVisibility(View.VISIBLE);
        mEnsurePWTextView = (TextView) view.findViewById(R.id.ensure_door_password);
        mDoorLockPWLayout = (LinearLayout) view.findViewById(R.id.door_lock_password_layout);
        mDoorLockedLayout = (LinearLayout) view.findViewById(R.id.door_locked_layout);
        mDoorLockedLayout.setVisibility(View.INVISIBLE);
        mEnsurePWTextView.setOnClickListener(viewDoorLoakClickListener);
    }

    /**
     * 重写此方法是为了不让此类调用父类的onResume方法，不然会发送命令请求返回远程密码
     */
    @Override
    public void onResume() {

    }

    //解析协议 动态改变视图状态
    @Override
    public void initViewStatus() {
        super.initViewStatus();
        if (!isDeviceOnLine()) {
            return;
        }
        if (isPWCorrect() && !isUnLockingDoor) {
            createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, "11", true);
//			controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),"11");
            isUnLockingDoor = true;
            return;
        }
        if (isStateUnknow()) {
            mDoorRightView.setVisibility(View.INVISIBLE);
            mDoorCenterView.setImageDrawable(getResources().getDrawable(R.drawable.lock_locked_img));
            return;
        } else if (isClosed()) {
            isUnLockingDoor = false;
            mDoorCenterView.setImageDrawable(getResources().getDrawable(R.drawable.lock_locked_img));
            mDoorLockedLayout.setVisibility(View.INVISIBLE);
            mDoorLockPWLayout.setVisibility(View.VISIBLE);
            mDoorRightView.setVisibility(View.INVISIBLE);
            mDoorLockPWEditText.clearFocus();
            mDoorLockPWEditText.setText("");
            if (isPWWrong()) {
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorCenterView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_close_big));
                mDoorRightView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_mistake));
            }
            return;
        } else if (isOpened()) {
            isUnLockingDoor = false;
            mDoorCenterView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_open_big));
            mDoorLockPWLayout.setVisibility(View.GONE);
            mDoorRightView.setVisibility(View.INVISIBLE);
            mDoorLockedLayout.setVisibility(View.INVISIBLE);
            mDoorLockedLayout.setOnClickListener(viewDoorLoakClickListener);
            if (isPasswordUnLocked() || isPasswordUnLocked(epData)) {
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_open_pass));
            } else if (isButtonUnLocked(epData)) {
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_open_button));
            } else if (isFingerUnLocked(epData) || isNewFingerUnLocked(epData)) {
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_open_finger));
                //如果是指纹开锁，提示客户可立即设置昵称
                if (StringUtil.isNullOrEmpty(mExtData)) {
                    promptCustomerNickname(epData);
                }
            } else if (isCardUnLocked(epData) || isNewCardUnLocked(epData)) {
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_open_card));
                //如果是卡片开门，提示客户可立即设置昵称
                if (StringUtil.isNullOrEmpty(mExtData)) {
                    promptCustomerNickname(epData);
                }
            } else if (isKeyUnLocked()) {
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_open_key));
            } else {
                mDoorLockPWLayout.setVisibility(View.GONE);
                mDoorRightView.setVisibility(View.INVISIBLE);
                mDoorCenterView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_open_big));
            }
            return;
        }

        if (isAlarm()) {
            mDoorCenterView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_ids_big));
            mDoorRightView.setVisibility(View.INVISIBLE);
            mDoorLockedLayout.setVisibility(View.INVISIBLE);
            mDoorLockPWLayout.setVisibility(View.VISIBLE);
            mDoorLockPWEditText.clearFocus();
            mDoorLockPWEditText.setText("");
            if (isIDSAlarming()) {
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_invasion));
            } else if (isDestory()) {
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_broke));
            } else if (isLowPower()) {
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_close_low_power));
            } else if (isPassAlwaysError()) {
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_mistake));
            } else {
                mDoorCenterView.setImageDrawable(getResources().getDrawable(R.drawable.device_door_lock_ids_big));
            }
        }

    }

    /**
     * 提示客户设置昵称，门卡和指纹开锁
     */
    private void promptCustomerNickname(final String epdata) {
        if (StringUtil.isNullOrEmpty(epdata)) {
            return;
        }
        String state = "";
        int dataInt = StringUtil.toInteger(epdata);
        if (isFingerUnLocked(epdata)) {
            dataInt -= 64;
            state = getResources().getString(R.string.unlock_records_fingerprint_unlock, dataInt + "");
        } else if (isNewFingerUnLocked(epdata)) {
            dataInt -= 230;
            state = getResources().getString(R.string.unlock_records_fingerprint_unlock, dataInt + "");
        } else if (isCardUnLocked(epdata)) {
            dataInt -= 96;
            state = getResources().getString(R.string.unlock_records_card_unlock, dataInt + "");
        } else if (isNewCardUnLocked(epdata)) {
            dataInt -= 110;
            state = getResources().getString(R.string.unlock_records_card_unlock, dataInt + "");
        } else if (isPasswordUnLocked(epdata)) {
            dataInt -= 350;
            state = getResources().getString(R.string.unlock_records_user_unlock, dataInt + "");
        }

        customerNicknameDialog = new WLDialog.Builder(DeviceDetailsActivity.instance);
        customerNickName = LayoutInflater.from(mContext).inflate(cc.wulian.smarthomev5.R.layout.customer_nick_name, null);
        etCustomerNickName = (EditText) customerNickName.findViewById(cc.wulian.smarthomev5.R.id.et_customer_nick_name);
        tvNickname = (TextView) customerNickName.findViewById(R.id.tv_nickname);
        tvNickname.setText(state);
        customerNicknameDialog.setTitle(mContext.getResources().getString(cc.wulian.smarthomev5.R.string.operation_title))
                .setCancelOnTouchOutSide(false)
                .setContentView(customerNickName)
                .setPositiveButton(mContext.getResources().getString(android.R.string.ok))
                .setNegativeButton(mContext.getResources().getString(android.R.string.cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view) {
                        String nickName = etCustomerNickName.getText().toString().trim();
                        if (StringUtil.isNullOrEmpty(nickName)) {
                            WLToast
                                    .showToast(
                                            mContext,
                                            mContext.getResources().getString(cc.wulian.smarthomev5.R.string.home_monitor_cloud_1_not_null),
                                            CustomToast.LENGTH_LONG, false);
                            customerNicknameDialog.setDismissAfterDone(false);
                        } else {
                            customerNicknameDialog.setDismissAfterDone(true);
                        }
                        NetSDK.sendCommonDeviceConfigMsg(gwID, devID, "1", System.currentTimeMillis() + "", epdata, nickName);//发送新增或者更新命令
                    }

                    @Override
                    public void onClickNegative(View view) {
                    }
                });
        Dialog dialog = customerNicknameDialog.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }

    }


    //相关控件的监听事件
    public View.OnClickListener viewDoorLoakClickListener = new View.OnClickListener() {
        String confirmPwd;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ensure_door_password:
                    if (confirmPwd()) {
                        //获取Edittext中的密码并对其加密处理
                        confirmPwd = mDoorLockPWEditText.getText().toString();
                        createControlOrSetDeviceSendData(1, "9" + confirmPwd.length() + confirmPwd, true);
//					controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),"9"+ confirmPwd.length() + confirmPwd);
                    }
                    break;
                default:
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
        Intent intent = new Intent(mContext, DeviceSettingActivity.class);
        intent.putExtra(EditDoorLock4Fragment.GWID, gwID);
        intent.putExtra(EditDoorLock4Fragment.DEVICEID, devID);
        intent.putExtra(EditDoorLock4Fragment.DEVICE_DOOR_LOCK_4, "70");
        intent.putExtra(AbstractDevice.SETTING_LINK_TYPE, AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
        intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, EditDoorLock4Fragment.class.getName());
        return intent;
    }

    @Override
    public void onDeviceUp(cc.wulian.ihome.wan.entity.DeviceInfo devInfo) {
        super.onDeviceUp(devInfo);
        SendMessage.sendGetBindSceneMsg(gwID, devID);
    }

    @Override
    public CharSequence parseAlarmProtocol(String epData) {
        CharSequence alarmMsg = super.parseAlarmProtocol(this.epData);
        StringBuilder sb = new StringBuilder();
        if (StringUtil.isNullOrEmpty(sb.toString())) {
            sb.append(DeviceTool.getDeviceAlarmAreaName(this));
            sb.append(DeviceTool.getDeviceShowName(this));
            if (LanguageUtil.isChina() || LanguageUtil.isTaiWan()) {
                sb.append(mContext.getString(R.string.home_device_alarm_default_voice_detect));
            } else {
                sb.append(" " + mContext.getString(R.string.home_device_alarm_default_voice_detect) + " ");
            }

            switch (StringUtil.toInteger(this.epData)) {
                case 31:
                    getKeyObtainAlarmVoice(IPreferenceKey.P_KEY_ALARM_KEY_DOOR_LOCK, mContext.getString(R.string.home_device_alarm_type_doorlock_error));
                    break;
                default:
                    sb.replace(0, sb.length(), "");
                    break;
            }
        }

        return sb.toString() + alarmMsg;
    }

    @Override
    public CharSequence parseDataWithExtData(String extData) {
        sb.replace(0, sb.length(), "");
        sb.append(extData);
        sb.append(getString(R.string.device_state_unlock));
        return sb;
    }
}