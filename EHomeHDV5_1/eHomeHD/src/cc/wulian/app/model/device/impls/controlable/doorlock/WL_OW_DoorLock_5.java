package cc.wulian.app.model.device.impls.controlable.doorlock;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.LoginFilter;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.yuantuo.customview.ui.ToastProxy;

import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.utils.SpannableUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow.MenuItem;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.LetterToEnUtil;

/**
 * @author huxc
 * @ClassName: WL_OW_DoorLock_5
 * @Function: OW门锁
 * @Date: 2016年6月24日
 */
@DeviceClassify(devTypes = {"OW"}, category = Category.C_SECURITY)
public class WL_OW_DoorLock_5 extends AbstractDoorLock {

    protected static final String DEVICE_STATE_021C = "021C";// 欠压报警
    protected static final String DEVICE_STATE_021F = "021F";// 密码连续出错
    protected static final String DEVICE_STATE_0217 = "0217";// 入侵报警
    protected static final String DEVICE_STATE_021D = "021D";// 破坏报警
    protected static final String DEVICE_STATE_0213 = "0213";// 不反锁
    protected static final String DEVICE_STATE_0214 = "0214";// 反锁
    protected static final String DEVICE_STATE_0102 = "0102";// 自动上锁
    protected static final String DEVICE_STATE_078A = "078A";// 钥匙开门

    protected StringBuilder sb = new StringBuilder();
    protected Preference preference = Preference.getPreferences();
    private ImageView mDoorCenterView;
    private ImageView mDoorRightView;
    private TextView mDoorRightText;
    private TextView DoorLockAlarmMes;
    private EditText mDoorLockPWEditText;
    private TextView mErrorView;
    private TextView mEnsurePWTextView;
    private RelativeLayout mDoorLockPWLayout;
    private ImageButton choosepdstatus;

    private int sum = 0;

    // 密码输入错误  是否提示
    private boolean isPWwrongWarming = false;

    public WL_OW_DoorLock_5(Context context, String type) {
        super(context, type);

        // TODO Auto-generated constructor stub
    }

    @Override
    public String getDefaultEndPoint() {
        return EP_0;
    }

    /**
     * 密码连续出错
     */
    public boolean isPassAlwaysError() {
        return isSameAs(DEVICE_STATE_021F, epData);
    }

    /**
     * 入侵报警
     */
    public boolean isIDSAlarming() {
        return isSameAs(DEVICE_STATE_0217, epData);
    }

    /**
     * 破坏报警
     */
    @Override
    public boolean isDestory() {
        return isSameAs(DEVICE_STATE_021D, epData);
    }

    /**
     * 欠压报警
     */
    @Override
    public boolean isLowPower() {
        return isSameAs(DEVICE_STATE_021C, epData);
    }

    /**
     * 反锁
     */
    @Override
    public boolean isReverseLock() {
        return isSameAs(DEVICE_STATE_0214, epData);
    }

    /**
     * 不反锁
     */
    @Override
    public boolean isRemoveLock() {
        return isSameAs(DEVICE_STATE_0213, epData);
    }

    /**
     * 自动上锁
     */

    public boolean isAutoLock() {
        return isSameAs(DEVICE_STATE_0102, epData);
    }

    /**
     * 钥匙开门
     */

    @Override
    public boolean isKeyUnLocked() {
        return isSameAs(DEVICE_STATE_078A, epData);
    }

    @Override
    public void refreshDevice() {
        super.refreshDevice();
        if(epData.startsWith("0812")&&epType.equals("OW")){
            String checkRtc = epData.substring(epData.length() - 2,
                    epData.length());
            preference.saveOWRtcResult(checkRtc,devID);
        }
        Intent it = new Intent();
        it.setAction("sendepData");
        it.putExtra("epData", epData);
        it.putExtra("epType", epType);
        mContext.sendBroadcast(it);
        Log.e("epdata", epData);
    }

    /**
     * 4种开锁方式 密码、纽扣、指纹、射频卡
     */

    public boolean isPasswordUnLocked(String epData) {
        if (!isNull(epData) && (epData.substring(0, 2).equals("03"))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isButtonUnLocked(String epData) {
        if (!isNull(epData) && (epData.substring(0, 2).equals("04"))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isFingerUnLocked(String epData) {
        if (!isNull(epData) && (epData.substring(0, 2).equals("05"))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean isCardUnLocked(String epData) {
        if (!isNull(epData) && (epData.substring(0, 2).equals("06"))) {
            return true;
        } else {
            return false;
        }
    }

    // 通过app开锁
    public boolean isAppUnLocked(String epData) {
        if (!isNull(epData) && (epData.substring(0, 4).equals("0807"))) {
            return true;
        } else {
            return false;
        }
    }

    // 管理员认证成功
    public boolean isCheckAdminRight() {
        return isSameAs("0220", epData);
    }

    // 普通用户添加已满
    public boolean isBeyondCommonPassword() {
        return isSameAs("0223", epData);
    }

    // 单次用户添加已满
    public boolean isBeyondOncePassword() {
        return isSameAs("0221", epData);
    }

    // 单次用户添加已满
    public boolean isBeyondTempPassword() {
        return isSameAs("0224", epData);
    }

    // 密码下发成功
    public boolean isSendPasswordSuccess() {
        return isSameAs("0806", epData);
    }

    // 密码下发失敗
    public boolean isSendPasswordFailed() {
        return isSameAs("0801", epData) || isSameAs("0802", epData);
    }

    @Override
    public boolean isOpened() {
        return isSecureUnLocked() || isUnLocked();
    }

    @Override
    public boolean isClosed() {
        return isSecureLocked() || isLocked() || isPWWrong() || isAutoLock();
    }

    @Override
    public boolean isStateUnknow() {
        return isSameAs("0810", epData);
    }

    @Override
    public boolean isLocked() {
        return isSameAs(DATA_CTRL_STATE_CLOSE_2, epData) || isReverseLock()
                || isDoorLocked() || isPassAlwaysError();
    }

    @Override
    public boolean isUnLocked() {
        return isSameAs(DATA_CTRL_STATE_OPEN_1, epData)
                || isPasswordUnLocked(epData) || isButtonUnLocked(epData)
                || isFingerUnLocked(epData) || isCardUnLocked(epData)
                || isKeyUnLocked() || isAppUnLocked(epData);
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
        Log.e("parseAlarmProtocol", epData);
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
        if (isButtonUnLocked(this.epData)) {
            return getKeyObtainAlarmVoice(
                    IPreferenceKey.P_KEY_ALARM_BUTTON_DOOR_LOCK,
                    R.string.device_state_button_open, epData);
        } else if (isFingerUnLocked(epData)) {//指纹解锁
            if (LanguageUtil.isEnglish()) {
                sb.append(getResources().getString(
                        R.string.device_lock_opened_fingerprint)
                        + LetterToEnUtil.convertLessThanOneThousand(Integer.parseInt(epData.substring(2, 6), 16))
                        + getResources().getString(R.string.OW_lock_open));
            } else {
                sb.append(getResources().getString(
                        R.string.device_lock_opened_fingerprint)
                        + Integer.parseInt(epData.substring(2, 6), 16)
                        + getResources().getString(R.string.OW_lock_open));
            }
        } else if (isCardUnLocked(this.epData)) {
            return getKeyObtainAlarmVoice(
                    IPreferenceKey.P_KEY_ALARM_CARD_DOOR_LOCK,
                    R.string.device_state_card_open, epData);
        }

        if (epData.equals("0102")) {
            sb.append(mContext.getString(R.string.device_state_auto_lock));
        }
        if (isKeyUnLocked()) {
            sb.append(mContext.getString(R.string.device_state_key_open));
        }
        if (isAppUnLocked(epData)) {
            if (LanguageUtil.isEnglish()) {
                AppUnlockAlarmWithEnglishStatus(4, 8, epData, R.string.home_device_alarm_type_doorlock_app);
            } else {
                AppUnlockAlarmWithChineseStatus(4, 8, epData, R.string.home_device_alarm_type_doorlock_app);
            }
        }

        if (epData.equals("0213")) {//解除反锁
            sb.append(getResources().getString(R.string.device_state_lock_remove));
        }
        if (epData.equals("0214")) {// 反锁标志
            getKeyObtainAlarmVoice(IPreferenceKey.P_KEY_ALARM_CLOSE_DOOR_LOCK,
                    mContext.getString(R.string.device_state_unlock_reverse));
        }
        if (epData.equals("0217")) {// 入侵报警
            sb.append(mContext
                    .getString(R.string.home_device_alarm_type_doorlock_invasion));
        }
        if (epData.equals("021F")) { // 连续密码出错
            sb.append(mContext
                    .getString(R.string.home_device_alarm_type_doorlock_error));
        }
        if (epData.equals("021C")) {// 欠压报警
            sb.append(mContext.getString(R.string.home_message_low_power_warn));
        }
        if (epData.equals("021D")) {// 锁体破坏
            sb.append(mContext
                    .getString(R.string.home_device_alarm_type_doorlock_destroy));
        }
        if (epData.equals("0218")) {// 报警解除
            sb.append(mContext.getString(R.string.device_state_alarm_removed));
        }
        if (isCheckAdminRight()) {
            sb.append(getResources().getString(R.string.OW_autoPW_checksuccess));
        }
        if (isSendPasswordSuccess()) {
            sb.append(getResources().getString(
                    R.string.device_defenseable_password)
                    + getResources()
                    .getString(R.string.OW_pw_addin_success_all));
        }
        if (isSendPasswordFailed()) {
            sb.append(getResources().getString(R.string.ow_lock_setting_password_failed_to_be_added));
        }
        if (isPasswordUnLocked(epData)) {
            if (LanguageUtil.isEnglish()) {
                AppUnlockAlarmWithEnglishStatus(2, 6, epData, R.string.device_alarm_type_doorlock_pwd);
            } else {
                AppUnlockAlarmWithChineseStatus(2, 6, epData, R.string.device_alarm_type_doorlock_pwd);
            }
        }
        if (epData.equals("0810")) {
            sb.append(getResources().getString(
                    R.string.device_state_password_mistake));
        }
        if (isBeyondCommonPassword()) {
            sb.append(getResources().getString(cc.wulian.app.model.device.R.string.ow_lock_setting_ordinary_users_to_add_full));
        }
        if(isBeyondTempPassword()){
            sb.append(getResources().getString(R.string.ow_temporary_user_is_full));
        }
        if (isBeyondOncePassword()) {
            sb.append(getResources().getString(cc.wulian.app.model.device.R.string.ow_lock_setting_one_time_users_to_add_full));
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

    /*
     *@Function：当系统为英文状态下通过app开锁的语音播报（包括单次密码和普通密码）
     * @Param: start:epData 截取的起始位 end :epData截取的终止位 epData：网关返回的值
     */
    private void AppUnlockAlarmWithEnglishStatus(int start, int end, String epData, int ResId) {
        if (epData.substring(start, end).equals("0065")) {
            sb.append(getResources().getString(R.string.OW_onceuser_pw) + LetterToEnUtil.convertLessThanOneThousand(1)
                    + mContext
                    .getString(ResId));

        } else if (epData.substring(start, end).equals("0066")) {
            sb.append(getResources().getString(R.string.OW_onceuser_pw) + LetterToEnUtil.convertLessThanOneThousand(2)
                    + mContext
                    .getString(ResId));

        } else if (epData.substring(start, end).equals("0067")) {
            sb.append(getResources().getString(R.string.OW_onceuser_pw) + LetterToEnUtil.convertLessThanOneThousand(3)
                    + mContext
                    .getString(ResId));

        } else {
            sb.append(getResources().getString(R.string.OW_lock_user));
            sb.append(LetterToEnUtil.convertLessThanOneThousand(Integer.parseInt(epData.substring(start, 8), 16)));
            sb.append(mContext
                    .getString(R.string.OW_lock_open));
        }
    }

    /*
    *@function：当系统为中文状态下通过app开锁的语音播报（包括单次密码和普通密码）
    * @Param: start:epData 截取的起始位 end :epData截取的终止位 epData：网关返回的值
    */
    private void AppUnlockAlarmWithChineseStatus(int start, int end, String epData, int ResId) {
        if (epData.substring(start, end).equals("0065")) {
            sb.append(getResources().getString(R.string.OW_onceuser_pw) + 1
                    + mContext
                    .getString(ResId));

        } else if (epData.substring(start, end).equals("0066")) {
            sb.append(getResources().getString(R.string.OW_onceuser_pw) + 2
                    + mContext
                    .getString(ResId));

        } else if (epData.substring(start, end).equals("0067")) {
            sb.append(getResources().getString(R.string.OW_onceuser_pw) + 3
                    + mContext
                    .getString(ResId));

        } else {
            sb.append(getResources().getString(R.string.OW_lock_user));
            sb.append(Integer.parseInt(epData.substring(start, end), 16));
            sb.append(mContext
                    .getString(R.string.OW_lock_open));
        }
    }


    // 列表显示状态并且為報警顯示內容
    @Override
    public CharSequence parseDataWithProtocol(String epData) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();

        String state;
        int color;
        state = "";
        // state = getString(R.string.device_state_lock);
        color = R.color.white;
        if (!StringUtil.isNullOrEmpty(epData)) {
            if (epData.equals("0102")) {
                state = getString(R.string.device_state_lock);
                color = COLOR_NORMAL_ORANGE;
            } else if (epData.equals("0214")) {
                state = getString(R.string.device_state_unlock_reverse);
                color = COLOR_NORMAL_ORANGE;
            } else if (epData.equals("0217")) {
                state = getString(R.string.device_state_alarm_inbreak);
                color = COLOR_ALARM_RED;
            } else if (epData.equals("0218")) {
                state = getString(R.string.device_state_alarm_removed);
                color = COLOR_NORMAL_ORANGE;
            } else if (epData.equals("021C")) {
                state = getString(R.string.home_message_low_power_warn);
                color = COLOR_ALARM_RED;
            } else if (epData.equals("021D")) {
                state = getString(R.string.device_state_alarm_destory);
                color = COLOR_ALARM_RED;
            } else if (epData.equals("021F")) {
                state = getString(R.string.home_device_alarm_type_doorlock_error);
                color = COLOR_ALARM_RED;
            }
            if (isAppUnLocked(epData)) {
                state = getResources().getString(R.string.device_user)
                        + Integer.parseInt(epData.substring(4, 8), 16)
                        + getString(R.string.home_device_alarm_type_doorlock_app);
                color = COLOR_CONTROL_GREEN;
            }
            if (isButtonUnLocked(epData)) {
                state = getResources().getString(
                        R.string.device_lock_fastener)
                        + Integer.parseInt(epData.substring(2, 6), 16)
                        + getResources().getString(
                        R.string.device_state_button_open);
                color = COLOR_CONTROL_GREEN;
            } else if (isPasswordUnLocked(epData)) {
                state = getResources().getString(
                        R.string.device_alarm_type_doorlock_pwd);
                color = COLOR_CONTROL_GREEN;
            } else if (isFingerUnLocked(epData)) {
                state = getResources()
                        .getString(
                                R.string.device_lock_opened_fingerprint)
                        + Integer.parseInt(epData.substring(2, 6), 16)
                        + getResources()
                        .getString(
                                R.string.home_device_alarm_type_doorlock_finger);
                color = COLOR_CONTROL_GREEN;

            } else if (isCardUnLocked(epData)) {
                state = getResources().getString(
                        R.string.device_lock_opened_card)
                        + Integer.parseInt(epData.substring(2, 6), 16)
                        + getResources().getString(
                        R.string.device_state_card_open);
                color = COLOR_CONTROL_GREEN;
            }
            ssb.append(SpannableUtil.makeSpannable(state,
                    new ForegroundColorSpan(getResources().getColor(color))));
            return ssb;
        } else {
            return "";
        }

    }

    // 创建ow门锁对应的视图
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle saveState) {
        View view = inflater.inflate(
                R.layout.device_door_lock_ow, container,
                false);
        return view;
    }

    // 视图创建后初始化控件并触发相关事件
    @Override
    public void onViewCreated(View view, Bundle saveState) {
        super.onViewCreated(view, saveState);
        choosepdstatus = (ImageButton) view.findViewById(R.id.choose_pw_status);
        choosepdstatus.setTag("invisable");

        DoorLockAlarmMes = (TextView) view
                .findViewById(R.id.door_lock_alarmMes);
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
                new InputFilter.LengthFilter(9),
                new LoginFilter.PasswordFilterGMail()});
        mEnsurePWTextView = (TextView) view
                .findViewById(R.id.ensure_door_password);
        mEnsurePWTextView.setInputType(InputType.TYPE_CLASS_NUMBER);
        mDoorLockPWLayout = (RelativeLayout) view
                .findViewById(R.id.door_lock_password_layout);
        // mDoorLockedLayout = (LinearLayout) view
        // .findViewById(R.id.door_locked_layout);
        // - mDoorLockedLayout.setVisibility(View.INVISIBLE);
        mEnsurePWTextView.setOnClickListener(viewDoorLoakClickListener);
        choosepdstatus.setOnClickListener(viewDoorLoakClickListener);
        mDoorLockPWEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                if (mDoorLockPWEditText.getText().length() < 6) {
                    mEnsurePWTextView
                            .setBackgroundResource(R.drawable.abs__ab_solid_light_holo);
                }
                if (mDoorLockPWEditText.getText().length() == 6) {
                    mEnsurePWTextView
                            .setBackgroundResource(R.drawable.device_door_lock_ensure);
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

        SendMessage.sendControlDevMsg(gwID,devID,"14","OW","7");
    }

    @Override
    public boolean isPWWrong() {
        return super.isPWWrong();
    }

    // 解析协议 动态改变视图状态
    @Override
    public void initViewStatus() {
        super.initViewStatus();
        if (!(WL_OW_DoorLock_5.this).isDeviceOnLine()) {
            return;
        }
        if (isStateUnknow()) {
            if (isPWwrongWarming) {
                //只有在密码错误时 提示开锁失败
                ToastProxy.makeText(mContext, getResources().getString(R.string.OW_lock_default), ToastProxy.LENGTH_SHORT).show();
                isPWwrongWarming = false;
            }
            mDoorRightView.setVisibility(View.INVISIBLE);
            mDoorRightText.setVisibility(View.GONE);
            mDoorLockPWEditText.setText(null);
            DoorLockAlarmMes.setText(null);
            mDoorCenterView.setImageDrawable(getResources().getDrawable(
                    R.drawable.device_door_lock_close_big));
        } else if (isOpened()) {
            mDoorCenterView.setImageDrawable(getResources().getDrawable(
                    R.drawable.device_door_lock_open_big));
            mDoorLockPWLayout.setVisibility(View.GONE);
            mDoorRightView.setVisibility(View.VISIBLE);
            mDoorRightView.setImageDrawable(null);
            if (isUnLocked()) {// 锁已开，判断开锁方式加载不同视图
                mDoorRightView.setVisibility(View.VISIBLE);
                if (isPasswordUnLocked(epData)) {
                    mDoorRightView.setImageDrawable(getResources().getDrawable(
                            R.drawable.device_door_lock_open_pass));
                    PasswordUnlockShowText(
                            R.string.device_alarm_type_doorlock_pwd,
                            R.string.OW_lock_open);
                } else if (isButtonUnLocked(epData)) {
                    mDoorRightView.setImageDrawable(getResources().getDrawable(
                            R.drawable.device_door_lock_open_button));
                    // } else if (isPasswordUnLocked(epData)) {
                    // mDoorRightView.setImageDrawable(getResources().getDrawable(
                    // R.drawable.device_door_lock_open_pass));
                } else if (isFingerUnLocked(epData)) {
                    mDoorRightView.setImageDrawable(getResources().getDrawable(
                            R.drawable.device_door_lock_open_finger));
                    FingerUnlockShowtext();
                } else if (isCardUnLocked(epData)) {
                    mDoorRightView.setImageDrawable(getResources().getDrawable(
                            R.drawable.device_door_lock_open_card));
                    CardUnlockShowText();
                } else if (isKeyUnLocked()) {
                    mDoorRightView.setImageDrawable(getResources().getDrawable(
                            R.drawable.device_door_lock_open_key));
                } else if (isAppUnLocked(epData)) {
                    AppUnlockShowText();
                }
            }
        } else {
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
            } else if (isAutoLock()) {
                sum++;
                if (sum == 0) {
                    mDoorCenterView
                            .setImageDrawable(getResources().getDrawable(
                                    R.drawable.lock_locked_img));
                    DoorLockAlarmMes.setText("");
                } else {
                    mDoorCenterView.setImageDrawable(getResources()
                            .getDrawable(R.drawable.lock_locked_img));
                    DoorLockAlarmMes.setText(getResources().getString(
                            R.string.device_state_auto_lock));
                    DoorLockAlarmMes.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            DoorLockAlarmMes.setText("");
                        }
                    }, 3000);
                }
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

    /**
     * @param resId1 单次密码开锁显示的后缀
     * @param resId2 本地密码开锁时显示的内容后缀
     */
    private void PasswordUnlockShowText(int resId1, int resId2) {
        if (epData.substring(2, 6).equals("0065")) {
            DoorLockAlarmMes.setText(getResources().getString(R.string.OW_onceuser_pw) + 1 + mContext.getString(resId1));

        } else if (epData.substring(2, 6).equals("0066")) {
            DoorLockAlarmMes.setText(getResources().getString(R.string.OW_onceuser_pw) + 2 + mContext.getString(resId1));

        } else if (epData.substring(2, 6).equals("0067")) {
            DoorLockAlarmMes.setText(getResources().getString(R.string.OW_onceuser_pw) + 3 + mContext.getString(resId1));
        } else {
            DoorLockAlarmMes.setText(getResources().getString(
                    R.string.OW_local_pwUser)
                    + (Integer.parseInt(epData.substring(2, 6), 16))
                    + getResources().getString(resId2));
        }

    }

    // 以下2个方法为指纹和卡片下的ow开锁页面的报警信息的展示
    private void FingerUnlockShowtext() {
        DoorLockAlarmMes.setText(getResources().getString(
                R.string.device_lock_opened_fingerprint)
                + Integer.parseInt(epData.substring(2, 6), 16)
                + getResources().getString(R.string.OW_lock_open));
    }

    private void CardUnlockShowText() {
        DoorLockAlarmMes.setText(getResources().getString(
                R.string.device_lock_opened_card)
                + Integer.parseInt(epData.substring(2, 6), 16)
                + getResources().getString(R.string.device_state_card_open));
    }

    private void AppUnlockShowText() {

        if (epData.substring(4, 8).equals("0065")) {
            DoorLockAlarmMes
                    .setText(getResources().getString(R.string.OW_onceuser_pw) + 1
                            + mContext
                            .getString(R.string.home_device_alarm_type_doorlock_app));

        } else if (epData.substring(4, 8).equals("0066")) {
            DoorLockAlarmMes
                    .setText(getResources().getString(R.string.OW_onceuser_pw) + 2
                            + mContext
                            .getString(R.string.home_device_alarm_type_doorlock_app));

        } else if (epData.substring(4, 8).equals("0067")) {
            DoorLockAlarmMes
                    .setText(getResources().getString(R.string.OW_onceuser_pw) + 3
                            + mContext
                            .getString(R.string.home_device_alarm_type_doorlock_app));
        } else {

            DoorLockAlarmMes.setText(getResources().getString(
                    R.string.OW_lock_user)
                    + Integer.parseInt(epData.substring(4, 8), 16)
                    + getString(R.string.home_device_alarm_type_doorlock_app));
        }

    }

    // 相关控件的监听事件
    public View.OnClickListener viewDoorLoakClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ensure_door_password:
                    String confirmPwd;
                    confirmPwd = mDoorLockPWEditText.getText().toString();
                    if ((confirmPwd.length() >= 6) && (confirmPwd.length() <= 9)) {
                        createControlOrSetDeviceSendData(1,
                                "5" + confirmPwd.length() + confirmPwd, true);
                    }
                    isPWwrongWarming = true;
                    break;
                case R.id.door_locked_layout:
                    createControlOrSetDeviceSendData(1, null, true, -1);
                    break;
                case R.id.choose_pw_status:
                    switchpressState();
                default:
                    break;
            }

        }
    };

    private void switchpressState() {
        if (choosepdstatus.getTag() == "invisable") {
            choosepdstatus
                    .setBackgroundResource(R.drawable.inco_monitor);
            mDoorLockPWEditText
                    .setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
            mDoorLockPWEditText.setSelection(mDoorLockPWEditText.getText()
                    .length());// 将光标移至文字末尾
            choosepdstatus.setTag("visable");

        } else if (choosepdstatus.getTag() == "visable") {
            mDoorLockPWEditText
                    .setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
            choosepdstatus
                    .setBackgroundResource(R.drawable.modul_monitor_tab_normal);
            mDoorLockPWEditText.setSelection(mDoorLockPWEditText.getText()
                    .length());// 将光标移至文字末尾
            choosepdstatus.setTag("invisable");

        }

    }

    // gwID devID 绑定设备 跳转到另一个Activity
    public Intent getSettingIntent() {
        Intent intent = new Intent(mContext, DeviceSettingActivity.class);
        intent.putExtra(EditDoorLock5Fragment.GWID, gwID);
        intent.putExtra(EditDoorLock5Fragment.DEVICEID, devID);
        intent.putExtra(EditDoorLock5Fragment.DEVICE_DOOR_LOCK_5, type);
        intent.putExtra(AbstractDevice.SETTING_LINK_TYPE,
                AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
        intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME,
                EditDoorLock5Fragment.class.getName());
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
    public void onDeviceUp(cc.wulian.ihome.wan.entity.DeviceInfo devInfo) {
        super.onDeviceUp(devInfo);
        SendMessage.sendGetBindSceneMsg(gwID, devID);
    }

    @Override
    public boolean isAlarming() {
        return isIDSAlarming() || isDestory() || isLowPower()
                || isPassAlwaysError() || isReverseLock() || isDoorLocked()
                || isRemoveLock() || isStateUnknow() || isOpened()
                || isPasswordUnLocked() || isButtonUnLocked(epData)
                || isPasswordUnLocked(epData) || isClosed()
                || isFingerUnLocked(epData) || isCardUnLocked(epData)
                || isPWCorrect() || isKeyUnLocked() || isDoorUnLocked()
                || isCheckAdminRight() || isSendPasswordSuccess()
                || isSendPasswordFailed()||isBeyondTempPassword()||isBeyondOncePassword()||isBeyondCommonPassword();
    }

    public boolean isAlarm() {
        return isIDSAlarming() || isDestory() || isLowPower()
                || isPassAlwaysError();
    }

    @Override
    public boolean isNormal() {
        return isSameAs(DEVICE_STATE_24, epData);
    }

    @Override
    public String[] getDoorLockEPResources() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isSecureLocked() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean isSecureUnLocked() {
        // TODO Auto-generated method stub
        return false;
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
}
