package cc.wulian.app.model.device.impls.controlable.doorlock;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.LoginFilter;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.yuantuo.customview.ui.CustomToast;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.utils.SpannableUtil;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.DeviceDetailsActivity;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.dao.Command406_DeviceConfigMsg;
import cc.wulian.smarthomev5.dao.ICommand406_Result;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.InputMethodUtils;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.view.DropdownSpinner;

/**
 * 1:解锁(不解析),2:上锁(不解析), 10:上保险,11:解除保险, 20:反锁, 21:锁已开(暂无),22:锁已关(暂无),
 * 23:入侵报警,24:报警解除,25:强制上锁, 26:自动上锁(暂无),27:登记密码(暂无),28:欠压报警,29:破坏报警 30:密码开锁,
 * 31:密码连续出错, 33～52:纽扣1～20开锁, 65～84:指纹1～20开锁, 97～136:射频卡1～40开锁, 138:钥匙开锁
 * <p>
 * <p>
 * <b>Chang Log</b>
 * <p>
 * 1.控制命令(解锁，上锁)也作为判断门锁开关状态的条件
 */
@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_3}, category = Category.C_SECURITY)
public class WL_69_DoorLock_3 extends AbstractDoorLock implements ICommand406_Result {
    /**
     * 33～52:纽扣1～20开锁
     */
    private static final Rect OPEN_BUTTON_RANGE = new Rect(32, 0, 53, 1);

    /**
     * 65～84:指纹1～20开锁
     */
    private static final Rect OPEN_FINGER_MARK_RANGE = new Rect(64, 0, 85, 1);

    /**
     * 251~350：指纹121-220开锁
     */
    private static final Rect NEW_OPEN_FINGER_MARK_RANGE = new Rect(251, 0,
            351, 1);

    /**
     * 97～136:射频卡1～40开锁
     */
    private static final Rect OPEN_CARD_RANGE = new Rect(96, 0, 137, 1);

    /**
     * 151~250：射频卡41-140开锁
     */
    private static final Rect NEW_OPEN_CARD_RANGE = new Rect(151, 0, 251, 1);

    /**
     * 351~400：密码1至50开锁
     */
    private static final Rect OPEN_PASSWORD_RANGE = new Rect(351, 0, 401, 1);

    private static final String[] EP_SEQUENCE = {EP_14, EP_15, EP_16, EP_17};

    protected static final String DEVICE_STATE_28 = "28";
    protected static final String DEVICE_STATE_31 = "31";
    protected static final String DEVICE_STATE_138 = "138";
    public long t1 = 0;

    protected StringBuilder sb = new StringBuilder();
    protected ProgressDialogManager mDialogManager = ProgressDialogManager
            .getDialogManager();
    protected Preference preference = Preference.getPreferences();
    private ImageView mDoorCenterView;
    private ImageView mDoorRightView;
    private TextView mDoorRightText;
    private EditText mDoorLockPWEditText;
    private TextView mErrorView;
    private TextView mEnsurePWTextView;
    private LinearLayout mDoorLockPWLayout;
    private LinearLayout mDoorLockedLayout;
    private static final String DOOR_LOCK_ACCOUNT_KEY_PASS_WORD = "DOOR_LOCK_69_PASS_WORD";

    private String lackPass;
    protected Command406_DeviceConfigMsg command406 = null;
    private EditText etInitialPassWord;
    private View passwordDialog;
    private WLDialog.Builder lockDialog;
    private WLDialog dialog;
    private WLDialog.Builder comLockDialog;
    private View comPasswordDialog;
    private EditText etCommitPassWord;
    private WLDialog comDialog;
    private String imeistring;
    protected String mExtData;
    protected String reName;

    public WL_69_DoorLock_3(Context context, String type) {
        super(context, type);
    }

    @Override
    public String getDefaultEndPoint() {
        return EP_0;
    }

    @Override
    public void refreshDevice() {
        super.refreshDevice();
//        DeviceEPInfo mDeviceEPInfo = mDeviceInfo.getDevEPInfoByEP("14");
        if(mDeviceInfo!=null&&mDeviceInfo.getDevEPInfo()!=null){
            mExtData = mDeviceInfo.getDevEPInfo().getExtData();
        }
        Intent it = new Intent();
        it.setAction("sendepData");
        it.putExtra("epData", epData);
        it.putExtra("epType", epType);
        mContext.sendBroadcast(it);
        Log.e("epdata", epData);
    }

    /**
     * 密码连续出错
     */
    public boolean isPassAlwaysError() {
        return isSameAs(DEVICE_STATE_31, epData);
    }

    // /**
    // * 入侵报警
    // */
    // public boolean isIDSAlarming(){
    // return isSameAs(DEVICE_STATE_23, epData);
    // }

    // /**
    // * 欠压报警
    // */
    // public boolean isLowPowerDestoryAlarming(){
    // return isSameAs(DEVICE_STATE_28, epData);
    // }
    //
    // /**
    // * 破坏报警
    // */
    // public boolean isDestoryAlarming(){
    // return isSameAs(DEVICE_STATE_29, epData);
    // }

    @Override
    public boolean isPWCorrect() {
        return isSameAs(DEVICE_STATE_144, epData);
    }

    @Override
    public boolean isPWWrong() {
        return isSameAs(DEVICE_STATE_145, epData);
    }

    @Override
    public boolean isOpened() {
        return isSecureUnLocked() || isUnLocked();
    }

    @Override
    public boolean isClosed() {
        return isSecureLocked() || isLocked() || isPWWrong();
    }

    @Override
    public boolean isStateUnknow() {
        return isSameAs(DEVICE_STATE_FF, epData);
    }

    @Override
    public boolean isSecureLocked() {
        return isSameAs(DEVICE_STATE_10, epData);
    }

    @Override
    public boolean isSecureUnLocked() {
        return isSameAs(DEVICE_STATE_11, epData);
    }

    @Override
    public boolean isLocked() {
        return isSameAs(DATA_CTRL_STATE_CLOSE_2, epData)
                || isSameAs(DEVICE_STATE_25, epData) || isReverseLock()
                || isDoorLocked() || isRemoveLock();
    }

    @Override
    public boolean isUnLocked() {
        if (isNull(epData)) {
            return false;
        } else {
            return isSameAs(DATA_CTRL_STATE_OPEN_1, epData)
                    || isPasswordUnLocked() || isButtonUnLocked(epData)
                    || isPasswordUnLocked(epData) || isFingerUnLocked(epData)
                    || isNewFingerUnLocked(epData) || isCardUnLocked(epData)
                    || isNewCardUnLocked(epData) || isKeyUnLocked()
                    || isDoorUnLocked() || isAntiStress(epData)
                    || isAppUnLocked();
        }

    }

    public boolean isAppUnLocked() {
        return isSameAs("1", epData);
    }

    @Override
    public boolean isReverseLock() {
        return isSameAs(DEVICE_STATE_20, epData);
    }

    @Override
    public boolean isRemoveLock() {
        return isSameAs(DEVICE_STATE_19, epData);
    }

    @Override
    public boolean isDoorUnLocked() {
        return isSameAs(DEVICE_STATE_21, epData);
    }

    @Override
    public boolean isDoorLocked() {
        return isSameAs(DEVICE_STATE_22, epData);
    }

    @Override
    public boolean isPasswordUnLocked() {
        return isSameAs(DEVICE_STATE_30, epData);
    }

    @Override
    public boolean isKeyUnLocked() {
        return isSameAs(DEVICE_STATE_138, epData);
    }

    @Override
    public boolean isButtonUnLocked(String epData) {
        int secureState = StringUtil.toInteger(epData);
        return OPEN_BUTTON_RANGE.contains(secureState, 0);
    }

    @Override
    public boolean isFingerUnLocked(String epData) {
        int secureState = StringUtil.toInteger(epData);
        return OPEN_FINGER_MARK_RANGE.contains(secureState, 0);
    }

    public boolean isNewFingerUnLocked(String epData) {
        int secureState = StringUtil.toInteger(epData);
        return NEW_OPEN_FINGER_MARK_RANGE.contains(secureState, 0);
    }

    @Override
    public boolean isCardUnLocked(String epData) {
        int secureState = StringUtil.toInteger(epData);
        return OPEN_CARD_RANGE.contains(secureState, 0);
    }

    public boolean isNewCardUnLocked(String epData) {
        int secureState = StringUtil.toInteger(epData);
        return NEW_OPEN_CARD_RANGE.contains(secureState, 0);
    }

    public boolean isPasswordUnLocked(String epData) {
        int secureState = StringUtil.toInteger(epData);
        return OPEN_PASSWORD_RANGE.contains(secureState, 0);
    }

    @Override
    public Drawable getStateSmallIcon() {
        return isOpened() ? getDrawable(SMALL_OPEN_D)
                : isClosed() ? getDrawable(SMALL_CLOSE_D)
                : getDrawable(SMALL_CLOSE_D);
    }

    // 首页报警显示门锁操作记录，并语音报警三种报警信息
    @Override
    public CharSequence parseAlarmProtocol(String epData) {
        int dataInt = StringUtil.toInteger(this.epData);
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
        Logger.debug("DoorLock: epData" + dataInt);

        if (isButtonUnLocked(this.epData)) {
            dataInt -= 32;
            reName = epData;
            Logger.debug("DoorLock: Button" + dataInt);
            if (StringUtil.isNullOrEmpty(mExtData)) {
                return getKeyObtainAlarmVoice(
                        IPreferenceKey.P_KEY_ALARM_BUTTON_DOOR_LOCK,
                        R.string.device_state_button_open, dataInt);
            } else {

                String msg=sb+mExtData+getString(R.string.device_state_unlock);
                return msg;
            }

        } else if (isPasswordUnLocked(this.epData)) {
            dataInt -= 350;
            reName = epData;
            Logger.debug("DoorLock: Password" + dataInt);
            if (StringUtil.isNullOrEmpty(mExtData)) {
                return getKeyObtainAlarmVoice(
                        IPreferenceKey.P_KEY_ALARM_PASSWORD_DOOR_LOCK,
                        R.string.device_state_password_open, dataInt);
            } else {

                String msg=sb+mExtData+getString(R.string.device_state_unlock);
                return msg;
            }
        } else if (isFingerUnLocked(this.epData)) {
            dataInt -= 64;
            reName = epData;
            Logger.debug("DoorLock: Finger" + dataInt);
            if (StringUtil.isNullOrEmpty(mExtData)) {
                return getKeyObtainAlarmVoice(
                        IPreferenceKey.P_KEY_ALARM_FINGER_DOOR_LOCK,
                        R.string.device_state_finger_open, dataInt);
            } else {
                String msg=sb+mExtData+getString(R.string.device_state_unlock);
                return msg;
            }
//			return sb
//					.append(getResources()
//							.getString(
//									cc.wulian.smarthomev5.R.string.device_lock_opened_fingerprint)
//							+ dataInt
//							+ getResources().getString(
//									R.string.device_state_unlock));
        } else if (isNewFingerUnLocked(this.epData)) {
            dataInt -= 230;
            reName = epData;
            Logger.debug("DoorLock: Finger" + dataInt);
            if (StringUtil.isNullOrEmpty(mExtData)) {
                return getKeyObtainAlarmVoice(
                        IPreferenceKey.P_KEY_ALARM_FINGER_DOOR_LOCK,
                        R.string.device_state_finger_open, dataInt);
            } else {
                String msg=sb+mExtData+getString(R.string.device_state_unlock);
                return msg;
            }
        } else if (isCardUnLocked(this.epData)) {
            dataInt -= 96;
            reName = epData;
            Logger.debug("DoorLock: Card" + dataInt);
            if (StringUtil.isNullOrEmpty(mExtData)) {
                return getKeyObtainAlarmVoice(
                        IPreferenceKey.P_KEY_ALARM_CARD_DOOR_LOCK,
                        R.string.device_state_card_open, dataInt);
            } else {
                String msg=sb+mExtData+getString(R.string.device_state_unlock);
                return msg;
            }
//			return sb.append(getResources().getString(
//					cc.wulian.smarthomev5.R.string.device_lock_opened_card)
//					+ dataInt
//					+ getResources().getString(R.string.device_state_unlock));
        } else if (isNewCardUnLocked(this.epData)) {
            dataInt -= 110;
            reName = epData;
            Logger.debug("DoorLock: Card" + dataInt);
            if (StringUtil.isNullOrEmpty(mExtData)) {
                return getKeyObtainAlarmVoice(
                        IPreferenceKey.P_KEY_ALARM_CARD_DOOR_LOCK,
                        R.string.device_state_card_open, dataInt);
            } else {
                String msg=sb+mExtData+getString(R.string.device_state_unlock);
                return msg;
            }
        }
        switch (dataInt) {
            case 1:
                getKeyObtainAlarmVoice(
                        IPreferenceKey.P_KEY_ALARM_OPEN_DOOR_LOCK,
                        mContext.getString(R.string.home_device_alarm_type_doorlock_app));
                break;
            case 2:
                getKeyObtainAlarmVoice(IPreferenceKey.P_KEY_ALARM_OPEN_DOOR_LOCK,
                        mContext.getString(R.string.device_state_lock));
                break;
            case 11:
                getKeyObtainAlarmVoice(
                        IPreferenceKey.P_KEY_ALARM_OPEN_DOOR_LOCK,
                        mContext.getString(R.string.home_device_alarm_type_doorlock_open));
                break;
            case 10:
                getKeyObtainAlarmVoice(
                        IPreferenceKey.P_KEY_ALARM_CLOSE_DOOR_LOCK,
                        mContext.getString(R.string.home_device_alarm_type_doorlock_close));
                break;
            case 19:
                getKeyObtainAlarmVoice(
                        IPreferenceKey.P_KEY_ALARM_CLOSE_DOOR_LOCK,
                        mContext.getString(R.string.device_lock_op_lift_locked_alarm));
                break;
            case 20:
                getKeyObtainAlarmVoice(IPreferenceKey.P_KEY_ALARM_CLOSE_DOOR_LOCK,
                        mContext.getString(R.string.device_lock_op_locked_alarm));
                break;

            case 23:
                sb.append(mContext
                        .getString(R.string.home_device_alarm_type_doorlock_invasion));
                break;
            case 28:
                sb.append(mContext.getString(R.string.home_message_low_power_warn));
                break;
            case 29:
                sb.append(mContext
                        .getString(R.string.home_device_alarm_type_doorlock_destroy));
                break;
            case 30:
                getKeyObtainAlarmVoice(
                        IPreferenceKey.P_KEY_ALARM_PASSWORD_DOOR_LOCK,
                        mContext.getString(R.string.device_alarm_type_doorlock_pwd));
                break;
            case 31:
                sb.append(mContext
                        .getString(R.string.home_device_alarm_type_doorlock_error));
                break;

            case 138:
                getKeyObtainAlarmVoice(
                        IPreferenceKey.P_KEY_ALARM_KEY_DOOR_LOCK,
                        mContext.getString(R.string.home_device_alarm_type_doorlock_key));
                break;
            case 144:
                sb.append(mContext
                        .getString(R.string.home_device_alarm_type_doorlock_app));
                break;
            default:
                sb.replace(0, sb.length(), "");
                break;
        }
        return sb.toString();
    }

    protected void getKeyObtainAlarmVoice(String key, String alarmVoice) {
        if (preference.getBoolean(gwID + devID + key, true))
            sb.append(alarmVoice);
        else
            sb.replace(0, sb.length(), "");
    }

    private CharSequence getKeyObtainAlarmVoice(String key, int alarmVoice,
                                                Object data) {
        if (preference.getBoolean(gwID + devID + key, true))
            sb.append(mContext.getString(alarmVoice, data));
        else
            sb.replace(0, sb.length(), "");
        return sb.toString();
    }

    @Override
    public CharSequence parseDataWithProtocol(String epData) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        String state;
        int color;
        state = "";
        color = COLOR_NORMAL_ORANGE;
        int dataInt = StringUtil.toInteger(epData);

        switch (dataInt) {
            case 1:
                state = getResources().getString(
                        R.string.home_device_alarm_type_doorlock_app);
                color = COLOR_CONTROL_GREEN;
                break;
            case 2:
                state = getString(R.string.device_state_lock);
                color = COLOR_NORMAL_ORANGE;
                break;
            case 3:
                state = getString(R.string.device_state_lock);
                color = COLOR_NORMAL_ORANGE;
                break;
            case 10:
                state = getString(R.string.device_state_lock);
                color = COLOR_NORMAL_ORANGE;
                break;
            case 11:
                state = getString(R.string.device_state_unlock);
                color = COLOR_CONTROL_GREEN;
                break;
            case 23:
                state = getString(R.string.device_state_alarm_inbreak);
                color = COLOR_ALARM_RED;
                break;
            case 24:
                state = getString(R.string.device_state_alarm_removed);
                color = COLOR_NORMAL_ORANGE;
                break;
            case 25:
                state = getString(R.string.device_state_force_lock);
                color = COLOR_NORMAL_ORANGE;
                break;
            case 28:
                state = getString(R.string.home_message_low_power_warn);
                color = COLOR_ALARM_RED;
                break;
            case 29:
                state = getString(R.string.device_state_alarm_destory);
                color = COLOR_ALARM_RED;
                break;
            case 30:
                state = getString(R.string.device_state_pass_open);
                color = COLOR_CONTROL_GREEN;
                break;
            case 31:
                state = getString(R.string.home_device_alarm_type_doorlock_error);
                color = COLOR_ALARM_RED;
                break;
            case 32:
                state = getString(R.string.home_device_alarm_type_doorlock_anti_stress);
                color = COLOR_ALARM_RED;
                break;
            case 138:
                state = getString(R.string.device_state_key_open);
                color = COLOR_CONTROL_GREEN;
                break;
            // this is password check cmd, it will not appear here
            case 144:
                state = getString(R.string.device_state_key_password_success);
                color = COLOR_CONTROL_GREEN;
                break;
            case 145:
                state = getString(R.string.device_state_key_password_fail);
                color = COLOR_ALARM_RED;
                break;
        }
        if (isAntiStress(epData)) {
            state = getResources().getString(
                    R.string.home_device_alarm_type_doorlock_anti_stress);
            color = COLOR_ALARM_RED;
        }
        if (isButtonUnLocked(epData)) {
            dataInt -= 32;
            if (StringUtil.isNullOrEmpty(mExtData)) {
                state = getResources().getString(R.string.device_state_button_open,
                        dataInt);
            } else {
                state = mExtData;
            }
            color = COLOR_CONTROL_GREEN;
        } else if (isPasswordUnLocked(epData)) {
            dataInt -= 350;
            if (StringUtil.isNullOrEmpty(mExtData)) {
                state = getResources().getString(
                        R.string.device_state_password_open, dataInt);
            } else {
                state = mExtData;
            }
            color = COLOR_CONTROL_GREEN;
        } else if (isFingerUnLocked(epData)) {
            dataInt -= 64;
            if (StringUtil.isNullOrEmpty(mExtData)) {
                state = getResources().getString(
                        R.string.device_state_finger_open, dataInt);
            } else {
                state = mExtData;
            }
            color = COLOR_CONTROL_GREEN;
        } else if (isNewFingerUnLocked(epData)) {
            dataInt -= 230;
            if (StringUtil.isNullOrEmpty(mExtData)) {
                state = getResources().getString(
                        R.string.device_state_finger_open, dataInt);
            } else {
                state = mExtData;
            }
            color = COLOR_CONTROL_GREEN;
        } else if (isCardUnLocked(epData)) {
            dataInt -= 96;
            if (StringUtil.isNullOrEmpty(mExtData)) {
                state = getResources().getString(R.string.device_state_card_open,
                        dataInt);
            } else {
                state = mExtData;
            }
            color = COLOR_CONTROL_GREEN;
        } else if (isNewCardUnLocked(epData)) {
            dataInt -= 110;
            if (StringUtil.isNullOrEmpty(mExtData)) {
                state = getResources().getString(R.string.device_state_card_open,
                        dataInt);
            } else {
                state = mExtData;
            }
            color = COLOR_CONTROL_GREEN;
        } else if (isAppUnLocked()) {
            color = COLOR_CONTROL_GREEN;
            state = getResources().getString(
                    R.string.home_device_alarm_type_doorlock_app);
        }
        ssb.append(SpannableUtil.makeSpannable(state, new ForegroundColorSpan(
                getResources().getColor(color))));
        return ssb;
    }

    // 创建三代门锁对应的视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveState) {
        View view = inflater.inflate(R.layout.device_door_lock_4, container,
                false);
        return view;
    }

    // 视图创建后初始化控件并触发相关事件
    @Override
    public void onViewCreated(View view, Bundle saveState) {
        super.onViewCreated(view, saveState);
        mDoorCenterView = (ImageView) view
                .findViewById(R.id.device_door_lock_big);
        mDoorRightView = (ImageView) view
                .findViewById(R.id.device_door_lock_small);
        mDoorRightText = (TextView) view
                .findViewById(R.id.device_door_lock_small_text);
        mDoorLockPWEditText = (EditText) view
                .findViewById(R.id.door_lock_password_edittext);
        mDoorLockPWEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        mDoorLockPWEditText
                .setTransformationMethod(PasswordTransformationMethod
                        .getInstance());
        mDoorLockPWEditText.setFilters(new InputFilter[]{
                new InputFilter.LengthFilter(4),
                new LoginFilter.PasswordFilterGMail()});
        mEnsurePWTextView = (TextView) view
                .findViewById(R.id.ensure_door_password);
        mEnsurePWTextView.setInputType(InputType.TYPE_CLASS_NUMBER);
        mDoorLockPWLayout = (LinearLayout) view
                .findViewById(R.id.door_lock_password_layout);
        mDoorLockedLayout = (LinearLayout) view
                .findViewById(R.id.door_locked_layout);
        mDoorLockedLayout.setVisibility(View.INVISIBLE);
        mEnsurePWTextView.setOnClickListener(viewDoorLoakClickListener);

        mDoorLockPWEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (mDoorLockPWEditText.getText().length() < 4) {
                    mEnsurePWTextView
                            .setBackgroundResource(R.drawable.abs__ab_solid_light_holo);
                }
                if (mDoorLockPWEditText.getText().length() == 4) {
                    mEnsurePWTextView
                            .setBackgroundResource(R.drawable.device_door_lock_ensure);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }

        });

    }

    @Override
    public void onResume() {
        if (!(WL_69_DoorLock_3.this).isDeviceOnLine()) {
            return;
        }
        if (command406 == null) {
            command406 = new Command406_DeviceConfigMsg(this.mContext);
            command406.setConfigMsg(this);
            command406.setDevID(devID);
            command406.setGwID(gwID);
        }
        mDialogManager.showDialog(DOOR_LOCK_ACCOUNT_KEY_PASS_WORD, mContext, null, null);
        //发送获取密码的命令
        isThePopupDialogUpdataPwd = true;
        command406.SendCommand_Get("lock_pass");
        super.onResume();
        //在界面获取焦点的时候，判断密码连续输错的时间是否已经过了五分钟，如果未过，在剩余的时间之后，进行可以点击
        long historyTime = preference.getContinuouInputErrorsLockTime();
        if (historyTime != 0) {
            long time = System.currentTimeMillis();
            if ((time - historyTime) < IPreferenceKey.P_KEY_LOCK_FIVE_MINUTES_LATER) {
                mEnsurePWTextView.setEnabled(false);
                performDelayMessage(time - historyTime);
            } else {
                preference.setClickOnTheNumberOfTimes(0);
                clickOnTheNumberOfTimes = 0;
            }
        }

        int historyNumberOfTimes = preference.getClickOnTheNumberOfTimes();
        clickOnTheNumberOfTimes = historyNumberOfTimes;
    }


    // 解析协议 动态改变视图状态
    @Override
    public void initViewStatus() {
        super.initViewStatus();
        if (!(WL_69_DoorLock_3.this).isDeviceOnLine()) {
            return;
        }
        if (isStateUnknow()) {
            mDoorRightView.setVisibility(View.INVISIBLE);
            mDoorRightText.setVisibility(View.GONE);
            mDoorCenterView.setImageDrawable(getResources().getDrawable(
                    R.drawable.lock_locked_img));
        } else if (isOpened()) {
            mDoorCenterView.setImageDrawable(getResources().getDrawable(
                    R.drawable.device_door_lock_open_big));
            mDoorLockPWLayout.setVisibility(View.GONE);
            mDoorLockedLayout.setVisibility(View.VISIBLE);
            mDoorRightView.setVisibility(View.INVISIBLE);
            mDoorRightText.setVisibility(View.GONE);
            mDoorRightView.setImageDrawable(null);
            mDoorLockedLayout.setOnClickListener(viewDoorLoakClickListener);
            if (isSecureUnLocked()) {

                mDoorCenterView.setImageDrawable(getResources().getDrawable(
                        R.drawable.device_door_lock_open_big));
            } else if (isUnLocked()) {
                mDoorRightView.setVisibility(View.VISIBLE);

                if (isPasswordUnLocked()) {
                    mDoorRightView.setImageDrawable(getResources().getDrawable(
                            R.drawable.device_door_lock_open_pass));
                } else if (isButtonUnLocked(epData)) {
                    mDoorRightView.setImageDrawable(getResources().getDrawable(
                            R.drawable.device_door_lock_open_button));
                } else if (isPasswordUnLocked(epData)) {
                    mDoorRightView.setImageDrawable(getResources().getDrawable(
                            R.drawable.device_door_lock_open_pass));
                } else if (isFingerUnLocked(epData)
                        || isNewFingerUnLocked(epData)) {
                    mDoorRightView.setImageDrawable(getResources().getDrawable(
                            R.drawable.device_door_lock_open_finger));
                } else if (isCardUnLocked(epData) || isNewCardUnLocked(epData)) {
                    mDoorRightView.setImageDrawable(getResources().getDrawable(
                            R.drawable.device_door_lock_open_card));
                } else if (isKeyUnLocked()) {
                    mDoorRightView.setImageDrawable(getResources().getDrawable(
                            R.drawable.device_door_lock_open_key));
                } else if (isDoorUnLocked()) {
                    mDoorRightView.setImageDrawable(getResources().getDrawable(
                            R.drawable.device_door_lock_open_already));
                } else if (isAntiStress(epData)) {
                    mDoorRightView.setImageDrawable(getResources().getDrawable(
                            R.drawable.lock_op_force));
                }
            }
        } else {
            mDoorLockedLayout.setVisibility(View.INVISIBLE);
            mDoorLockPWLayout.setVisibility(View.VISIBLE);
            mDoorLockPWEditText.clearFocus();
            mDoorLockPWEditText.setText("");
            if (isReverseLock()) {
                mDoorRightText.setVisibility(View.VISIBLE);
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(
                        R.drawable.device_door_lock_reverse));
                mDoorCenterView.setImageDrawable(getResources().getDrawable(
                        R.drawable.lock_locked_img));
                mDoorRightText.setText(getResources().getText(
                        R.string.device_state_unlock_reverse));
            } else if (isDoorLocked()) {
                mDoorRightText.setVisibility(View.GONE);
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(
                        R.drawable.device_door_lock_already));
            } else if (isRemoveLock()) {
                mDoorRightText.setVisibility(View.VISIBLE);
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(
                        R.drawable.device_door_remove_ock));
                mDoorRightText.setText(getResources().getText(
                        R.string.device_state_lock_remove));
            } else {
                mDoorRightView.setVisibility(View.INVISIBLE);
                mDoorRightText.setVisibility(View.GONE);
                mDoorCenterView.setImageDrawable(getResources().getDrawable(
                        R.drawable.lock_locked_img));
            }
        }

        if (isAlarm()) {
            mDoorCenterView.setImageDrawable(getResources().getDrawable(
                    R.drawable.device_door_lock_ids_big));
            mDoorRightView.setVisibility(View.INVISIBLE);
            mDoorRightText.setVisibility(View.GONE);
            mDoorLockedLayout.setVisibility(View.INVISIBLE);
            mDoorLockPWLayout.setVisibility(View.VISIBLE);
            mDoorLockPWEditText.clearFocus();
            mDoorLockPWEditText.setText("");
            if (isIDSAlarming()) {
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(
                        R.drawable.device_door_lock_invasion));
            } else if (isDestory()) {
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(
                        R.drawable.device_door_lock_broke));
            } else if (isLowPower()) {
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(
                        R.drawable.device_door_lock_close_low_power));
            } else if (isPassAlwaysError()) {
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(
                        R.drawable.device_door_lock_mistake));
            } else {
                mDoorCenterView.setImageDrawable(getResources().getDrawable(
                        R.drawable.device_door_lock_ids_big));
            }
        }
    }

    private Handler handler = new Handler(Looper.getMainLooper());
    private int clickOnTheNumberOfTimes = 0;
    // 相关控件的监听事件
    public View.OnClickListener viewDoorLoakClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ensure_door_password:
                    EnsureBtnClick();
                    break;
                case R.id.door_locked_layout:
                    createControlOrSetDeviceSendData(1, null, true, -1);
                    break;
                default:
                    break;
            }

        }
    };

    private void EnsureBtnClick() {
        if (confirmPwd()) {
            // 获取Edittext中的密码并对其加密处理
            String confirmPwd = mDoorLockPWEditText.getText().toString();
            String confirmdoorpwd = MD5Util.encrypt(confirmPwd);
            String savedMD5Pwd = Preference.getPreferences().getString(
                    IPreferenceKey.P_KEY_DEVICE_DOOR_LOCK_PWD, WINDOWS_PWD_MD5);
            //点击确认，只判断网关获取的密码和输入的密码进行比较
            if (confirmdoorpwd.equals(lackPass)) {
                clickOnTheNumberOfTimes = 0;
                createControlOrSetDeviceSendData(1, null, true, -1);
            } else {
                if (clickOnTheNumberOfTimes >= 4) {
                    mEnsurePWTextView.setEnabled(false);
                    long currenttime = System.currentTimeMillis();
                    preference.setContinuouInputErrorsLockTime(currenttime);
                    performDelayMessage(IPreferenceKey.P_KEY_LOCK_FIVE_MINUTES_LATER);
                } else {
                    clickOnTheNumberOfTimes = clickOnTheNumberOfTimes + 1;
                    preference.setClickOnTheNumberOfTimes(clickOnTheNumberOfTimes);
                }
                mDoorLockPWEditText.clearFocus();
                mDoorLockPWEditText.setText("");
                // 如果输入法显示,就隐藏
                if (InputMethodUtils.isShow(mContext)) {
                    InputMethodUtils.hide(mContext);
                }
                mDoorRightView.setVisibility(View.VISIBLE);
                mDoorRightView.setImageDrawable(getResources().getDrawable(
                        R.drawable.device_door_lock_mistake));
            }
        }
    }

    /**
     * 执行延时消息
     *
     * @param time 延时的时间
     */
    private void performDelayMessage(long time) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                clickOnTheNumberOfTimes = 0;
                preference.setClickOnTheNumberOfTimes(clickOnTheNumberOfTimes);
                preference.setContinuouInputErrorsLockTime(0);
                mEnsurePWTextView.setEnabled(true);
            }
        }, time);
    }

    // 密码是否为空
    public boolean confirmPwd() {
        if (mDoorLockPWEditText == null
                || "".equals(mDoorLockPWEditText.getText().toString())) {
            mErrorView = mDoorLockPWEditText;
            mErrorView.requestFocus();
            mErrorView.setError(getResources().getString(
                    R.string.hint_not_null_edittext));
            return false;
        } else {
            return true;
        }
    }

    // gwID devID 绑定设备 跳转到另一个Activity
    public Intent getSettingIntent() {
        Intent intent = new Intent(mContext, DeviceSettingActivity.class);
        intent.putExtra(EditDoorLock3Fragment.GWID, gwID);
        intent.putExtra(EditDoorLock3Fragment.DEVICEID, devID);
        intent.putExtra(EditDoorLock3Fragment.DEVICE_DOOR_LOCK_3, type);
        intent.putExtra(EditDoorLock3Fragment.DEVICE_PASS_WORD, lackPass);
        intent.putExtra(AbstractDevice.SETTING_LINK_TYPE,
                AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
        intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME,
                EditDoorLock3Fragment.class.getName());
        return intent;
    }

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
                Intent i = getSettingIntent();
                mContext.startActivity(i);
                manager.dismiss();
            }
        };
        if (isDeviceOnLine())
            items.add(settingItem);
        return items;
    }

    @Override
    public String[] getDoorLockEPResources() {
        return EP_SEQUENCE;
    }

    @Override
    public void onDeviceUp(cc.wulian.ihome.wan.entity.DeviceInfo devInfo) {
        super.onDeviceUp(devInfo);
        SendMessage.sendGetBindSceneMsg(gwID, devID);
    }

    // 增添报警语音提示
    @Override
    public String getAlarmProtocol() {
        return DEVICE_STATE_23;
    }

    @Override
    public String getNormalProtocol() {
        return DEVICE_STATE_24;
    }

    /**
     * 入侵报警
     */
    public boolean isIDSAlarming() {
        return isSameAs(DEVICE_STATE_23, epData);
    }

    /**
     * 破坏报警
     */
    @Override
    public boolean isDestory() {
        return isSameAs(DEVICE_STATE_29, epData);
    }

    /**
     * 欠压报警
     */
    @Override
    public boolean isLowPower() {
        return isSameAs(DEVICE_STATE_28, epData);
    }

    /**
     * 防劫持报警
     */
    public boolean isAntiStress(String epData) {
        if (isNull(epData)) {
            Log.e("length", "epData=Null");
        } else {
            Log.e("length", "epData=" + epData + " lenth=" + epData.length());
        }
        if (!isNull(epData) && epData.length() == 4) {
            if (isSameAs(DEVICE_STATE_32, epData.substring(0, 2))
                    && epData.length() == 4) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isAlarming() {
        return isIDSAlarming() || isDestory() || isLowPower()
                || isPassAlwaysError() || isReverseLock() || isDoorLocked()
                || isRemoveLock() || isStateUnknow() || isOpened()
                || isPasswordUnLocked() || isButtonUnLocked(epData)
                || isPasswordUnLocked(epData) || isClosed()
                || isFingerUnLocked(epData) || isNewFingerUnLocked(epData)
                || isCardUnLocked(epData) || isNewCardUnLocked(epData)
                || isAntiStress(epData) || isPWCorrect() || isKeyUnLocked()
                || isDoorUnLocked() || isAppUnLocked();
    }

    public boolean isAlarm() {
        return isIDSAlarming() || isDestory() || isLowPower()
                || isPassAlwaysError();
    }

    @Override
    public boolean isNormal() {
        return isSameAs(DEVICE_STATE_24, epData);
    }


    /**
     * 对于设置密码，无论本地有没有密码，都会先请求网关是否有密码，如果有密码，直接判断输入的与网络获取的是否一致，如果没有，直接设置到网络
     *
     * @param result
     */
    private boolean isCommonSettingSuccess = false;
    private boolean isThePopupDialogUpdataPwd = false;

    @Override
    public void Reply406Result(Command406Result result) {
        Log.d(this.getClass().getName(), result.toString());
        //判断是不是更新密码，如果是，表示第一次设置密码成功
        if (isCommonSettingSuccess && "1".equals(result.getMode())) {
            WLToast
                    .showToast(
                            mContext,
                            mContext.getResources().getString(R.string.common_setting_success),
                            CustomToast.LENGTH_LONG, false);
        }
        String json = result.getData();
        JSONObject jsonObject = null;
        if (json != null && json.length() > 0) {
            jsonObject = JSON.parseObject(json);
        }
        if (jsonObject != null) {
            lackPass = jsonObject.getString("pass");
        }
        mDialogManager.dimissDialog(DOOR_LOCK_ACCOUNT_KEY_PASS_WORD, 0);
        if (isThePopupDialogUpdataPwd && StringUtil.isNullOrEmpty(lackPass)) {
            //如果网关没有密码，则弹出设置密码的弹框
            initialPassWord();
        } else {
//
        }

    }


    /**
     * 如果406协议上面没有设置远程开门密码，则设置，本方法是第一次输入设置的密码，后面还需要输入再次确认密码
     */
    private void initialPassWord() {
        lockDialog = new WLDialog.Builder(mContext);
        passwordDialog = LayoutInflater.from(mContext).inflate(R.layout.initial_pass_word, null);
        etInitialPassWord = (EditText) passwordDialog.findViewById(R.id.et_initial_pass_word);
        etInitialPassWord.setInputType(InputType.TYPE_CLASS_NUMBER);
        lockDialog.setTitle(mContext.getResources().getString(R.string.lock_70_set_password))
                .setCancelOnTouchOutSide(false)
                .setContentView(passwordDialog)
                .setPositiveButton(mContext.getResources().getString(android.R.string.ok))
                .setNegativeButton(mContext.getResources().getString(android.R.string.cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view) {
                        String initialPaw = etInitialPassWord.getText().toString().trim();
                        if (judgeStr(initialPaw)) {
                            //需要再次输入密码，并且判断与第一次输入的密码是否一致
                            consistentPassWord(initialPaw);
                            lockDialog.setDismissAfterDone(true);
                        } else {
                            //判断输入的设置密码是否规范
                            lockDialog.setDismissAfterDone(false);
                        }
                    }

                    @Override
                    public void onClickNegative(View view) {
                        DeviceDetailsActivity.instance.finish();
                    }
                });
        dialog = lockDialog.create();
        if (!dialog.isShowing()) {
            dialog.show();
        }
    }


    /**
     * 判断刚输入的密码和第一次属于的密码是否一致，如果一致将密码上传到网络，如果不一样提示让客户从第一次输入密码进行从新操作
     * 输入的再次确认密码
     *
     * @param initialPaw
     */
    private void consistentPassWord(final String initialPaw) {
        comLockDialog = new WLDialog.Builder(mContext);
        comPasswordDialog = LayoutInflater.from(mContext).inflate(R.layout.initial_pass_word, null);
        etCommitPassWord = (EditText) comPasswordDialog.findViewById(R.id.et_initial_pass_word);
        etInitialPassWord.setInputType(InputType.TYPE_CLASS_NUMBER);
        comLockDialog.setTitle(mContext.getResources().getString(R.string.lock_70_set_password_2))
                .setCancelOnTouchOutSide(false)
                .setContentView(comPasswordDialog)
                .setPositiveButton(mContext.getResources().getString(android.R.string.ok))
                .setNegativeButton(mContext.getResources().getString(android.R.string.cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view) {
                        String commitPaw = etCommitPassWord.getText().toString().trim();
                        if (judgeStr(commitPaw)) {
                            comLockDialog.setDismissAfterDone(true);
                            if (commitPaw.equals(initialPaw)) {
                                isCommonSettingSuccess = true;
                                //如果输入的设置密码规范，将密码上传到406协议
                                commitPaw = MD5Util.encrypt(commitPaw);
                                updateWindowsInfo(commitPaw);
                            } else {
                                WLToast
                                        .showToast(
                                                mContext,
                                                mContext.getResources().getString(R.string.gesture_notmatch),
                                                CustomToast.LENGTH_LONG, false);
                                initialPassWord();
                            }
                        } else {
                            //判断输入的设置密码是否规范
                            comLockDialog.setDismissAfterDone(false);
                        }
                    }

                    @Override
                    public void onClickNegative(View view) {
                        initialPassWord();
                    }
                });
        comDialog = comLockDialog.create();
        if (!comDialog.isShowing()) {
            comDialog.show();
        }
    }


    /**
     * 判断输入的密码，是否是4位数字，且无空格
     *
     * @param initialPaw
     * @return
     */
    private boolean judgeStr(String initialPaw) {
        if (StringUtil.isNullOrEmpty(initialPaw)) {
            WLToast
                    .showToast(
                            mContext,
                            mContext.getResources().getString(R.string.set_password_not_null_hint),
                            CustomToast.LENGTH_LONG, false);
            return false;
        } else if (initialPaw.length() != 4) {
            WLToast
                    .showToast(
                            mContext,
                            mContext.getResources().getString(R.string.lock_70_password_wrong),
                            CustomToast.LENGTH_LONG, false);
            return false;
        } else if (containWhiteSpace(initialPaw)) {
            WLToast
                    .showToast(
                            mContext,
                            mContext.getResources().getString(R.string.device_passwords_space_hint),
                            CustomToast.LENGTH_LONG, false);
        }
        return true;
    }

    private void updateWindowsInfo(String initialPaw) {
        String paw = "{\"pass\" : \"" + initialPaw + "\"}";
        mDialogManager.showDialog(DOOR_LOCK_ACCOUNT_KEY_PASS_WORD, mContext, null, null);
        //将设置的密码，上传到网关
        command406.SendCommand_Add("lock_pass", paw);
    }

    @Override
    public void Reply406Result(List<Command406Result> results) {

    }

    /**
     * 去除空格
     *
     * @param input
     * @return
     */
    public static boolean containWhiteSpace(String input) {
        Pattern pattern = Pattern.compile("\\s");
        Matcher matcher = pattern.matcher(input);
        boolean found = matcher.find();
        return found;
    }

    /**
     * 此方法在activity的onDestroy中调用，在方法里面，将handler销毁，防止内存泄漏
     */
    @Override
    public void onDetachView() {
        super.onDetachView();
        handler.removeCallbacksAndMessages(null);
    }
}