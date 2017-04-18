package cc.wulian.smarthomev5.adapter;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Rect;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.TextAppearanceSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.yuantuo.customview.ui.CustomToast;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.dao.CameraDao;
import cc.wulian.smarthomev5.dao.Command406_DeviceConfigMsg;
import cc.wulian.smarthomev5.dao.ICommand406_Result;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.utils.DateUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

import static cc.wulian.smarthomev5.tools.WLCameraOperationManager.mDialogManager;

/**
 * Created by syf on 2017/1/16.
 */
public class DoorLock4HistoryAdapter extends DoorLockHistoryAdapter implements ICommand406_Result {


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
    private static final String DOOR_LOCK_ACCOUNT_HISTORY = "door_lock_account_history";
    private Command406_DeviceConfigMsg command406;
    private WLDialog.Builder customerNicknameDialog;
    private View customerNickName;
    private EditText etCustomerNickName;
    private TextView tvNickname;
    private String epData;
    private String gwID;
    private String devID;
    private String extData;
    private String reName;
    private String type;

    protected ProgressDialogManager mDialogManager = ProgressDialogManager
            .getDialogManager();

    public DoorLock4HistoryAdapter(Context context, List<MessageEventEntity> data, String gwID, String devID) {
        super(context, data);
        this.gwID = gwID;
        this.devID = devID;
        if (command406 == null) {
            command406 = new Command406_DeviceConfigMsg(this.mContext);
            command406.setConfigMsg(this);
            command406.setDevID(devID);
            command406.setGwID(gwID);
        }
    }

    @Override
    protected View newView(Context context, LayoutInflater inflater,
                           ViewGroup parent, int pos) {
        return inflater.inflate(R.layout.fragment_message_door_look_history_item,
                null);
    }

    @Override
    protected void bindView(Context context, View view, int pos,
                            final MessageEventEntity item) {
//        super.bindView(context, view, pos, item);

        final TextView mTimeView = (TextView) view.findViewById(R.id.tv_history_message_time);
        final TextView mTextView = (TextView) view.findViewById(R.id.tv_history_detail_message);
        TextView tvHistorySet = (TextView) view.findViewById(R.id.tv_history_immediately_set);
        extData = item.getExtData();
        if (StringUtil.isNullOrEmpty(extData)) {
            tvHistorySet.setText(mContext.getResources().getString(R.string.smart_lock_set_now));
        } else {
            tvHistorySet.setText(extData);
        }
        epData = item.getEpData();
        SpannableString lockType = setTheLockType(epData, tvHistorySet);
        if (null != lockType) {
            mTextView.setText(lockType, TextView.BufferType.SPANNABLE);
        }
        String mtime = DateUtil.getHourAndMinu(mContext, Long.parseLong(item.getTime()));
        mTimeView.setText(mtime);
        tvHistorySet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                immediatelySet(extData);
            }
        });

    }

    /**
     * 设置开锁记录显示的问题，颜色字体，以及立即设置是否显示
     *
     * @param epData
     * @param tvHistorySet
     * @return
     */
    private SpannableString setTheLockType(String epData, TextView tvHistorySet) {
        SpannableString ssb = null;

        String state;
        int dataInt = StringUtil.toInteger(epData);
        if ("30".equals(epData)) {
            state = mContext.getResources().getString(
                    R.string.device_state_pass_open);
            tvHistorySet.setVisibility(View.GONE);
            ssb = new SpannableString(state);
            if (LanguageUtil.isChina()) {
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style0), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style1), 2, 4, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if (isButtonUnLocked(epData)) {
            dataInt -= 32;
            if (dataInt < 10) {
                state = mContext.getResources().getString(R.string.device_state_button_open,
                        "0" + dataInt);
            } else {
                state = mContext.getResources().getString(R.string.device_state_button_open,
                        "" + dataInt);
            }
            reName = epData;
            tvHistorySet.setVisibility(View.VISIBLE);
            ssb = new SpannableString(state);
            if (LanguageUtil.isChina()) {
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style0), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style1), 6, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if (isFingerUnLocked(epData)) {
            dataInt -= 64;
            if (dataInt < 10) {
                state = mContext.getResources().getString(R.string.device_state_finger_open,
                        "0" + dataInt);
            } else {
                state = mContext.getResources().getString(R.string.device_state_finger_open,
                        "" + dataInt);
            }
            reName = epData;
            tvHistorySet.setVisibility(View.VISIBLE);
            ssb = new SpannableString(state);
            if (LanguageUtil.isChina()) {
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style0), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style1), 6, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if (isNewFingerUnLocked(epData)) {
            dataInt -= 230;
            if (dataInt < 10) {
                state = mContext.getResources().getString(R.string.device_state_finger_open,
                        "0" + dataInt);
            } else {
                state = mContext.getResources().getString(R.string.device_state_finger_open,
                        "" + dataInt);
            }
            reName = epData;
            tvHistorySet.setVisibility(View.VISIBLE);
            ssb = new SpannableString(state);
            if (LanguageUtil.isChina()) {
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style0), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style1), 6, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if (isCardUnLocked(epData)) {
            dataInt -= 96;
            if (dataInt < 10) {
                state = mContext.getResources().getString(R.string.device_state_card_open,
                        "0" + dataInt);
            } else {
                state = mContext.getResources().getString(R.string.device_state_card_open,
                        "" + dataInt);
            }
            reName = epData;
            tvHistorySet.setVisibility(View.VISIBLE);
            ssb = new SpannableString(state);
            if (LanguageUtil.isChina()) {
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style0), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style1), 6, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if (isNewCardUnLocked(epData)) {
            dataInt -= 110;
            if (dataInt < 10) {
                state = mContext.getResources().getString(R.string.device_state_card_open,
                        "0" + dataInt);
            } else {
                state = mContext.getResources().getString(R.string.device_state_card_open,
                        "" + dataInt);
            }
            reName = epData;
            tvHistorySet.setVisibility(View.VISIBLE);
            ssb = new SpannableString(state);
            if (LanguageUtil.isChina()) {
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style0), 0, 6, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style1), 6, 8, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if (isAppUnLocked()) {
            state = mContext.getResources().getString(
                    R.string.home_device_alarm_type_doorlock_app);
            tvHistorySet.setVisibility(View.GONE);
            ssb = new SpannableString(state);
            if (LanguageUtil.isChina()) {
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style0), 0, 5, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style1), 5, 7, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        } else if (isLocked()) {
            state = mContext.getResources().getString(
                    R.string.device_state_lock);
            tvHistorySet.setVisibility(View.GONE);
            ssb = new SpannableString(state);
            if (LanguageUtil.isChina()) {
                ssb.setSpan(new TextAppearanceSpan(mContext, R.style.history_unlock_style1), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return ssb;
    }


    /**
     * 点击立即设置
     */
    private void immediatelySet(final String extData) {
        customerNicknameDialog = new WLDialog.Builder(mContext);
        customerNickName = LayoutInflater.from(mContext).inflate(cc.wulian.smarthomev5.R.layout.customer_nick_name, null);
        etCustomerNickName = (EditText) customerNickName.findViewById(cc.wulian.smarthomev5.R.id.et_customer_nick_name);
        tvNickname = (TextView) customerNickName.findViewById(R.id.tv_nickname);
        tvNickname.setVisibility(View.GONE);
        customerNicknameDialog.setTitle(mContext.getResources().getString(R.string.html_user_hint_input_nickname))
                .setCancelOnTouchOutSide(true)
                .setContentView(customerNickName)
                .setPositiveButton(mContext.getResources().getString(android.R.string.ok))
                .setNegativeButton(mContext.getResources().getString(android.R.string.cancel))
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view) {
                        String nickName = etCustomerNickName.getText().toString().trim();
                        isSetSuccess = true;
                        if (StringUtil.isNullOrEmpty(nickName)) {
                            WLToast
                                    .showToast(
                                            mContext,
                                            mContext.getResources().getString(R.string.home_monitor_cloud_1_not_null),
                                            CustomToast.LENGTH_LONG, false);
                            customerNicknameDialog.setDismissAfterDone(false);
                        } else {
                            String mode;//如果extData为空的话，说明没有之前没有设置别名，这个时候需要新增mode为1，否则已经有了 需要更新2
                            if (StringUtil.isNullOrEmpty(extData)) {
                                command406.SendCommand_Add(reName, nickName);
                            } else {
                                command406.SendCommand_Update(reName, nickName);
                            }
                            mDialogManager.showDialog(DOOR_LOCK_ACCOUNT_HISTORY, mContext, null, null);
                            customerNicknameDialog.setDismissAfterDone(true);
                        }
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


    public boolean isPWCorrect() {
        return TextUtils.equals("144", epData);
    }

    public boolean isPWWrong() {
        return TextUtils.equals("145", epData);
    }

    public boolean isOpened() {
        return isSecureUnLocked() || isUnLocked();
    }

    public boolean isClosed() {
        return isSecureLocked() || isLocked() || isPWWrong();
    }

    public boolean isStateUnknow() {
        return TextUtils.equals("FF", epData);
    }

    public boolean isSecureLocked() {
        return TextUtils.equals("10", epData);
    }

    public boolean isSecureUnLocked() {
        return TextUtils.equals("11", epData);
    }

    public boolean isLocked() {
        return TextUtils.equals("2", epData)
                || TextUtils.equals("25", epData) || isReverseLock()
                || isDoorLocked() || isRemoveLock();
    }

    public boolean isUnLocked() {
        if (AbstractDevice.isNull(epData)) {
            return false;
        } else {
            return TextUtils.equals("1", epData)
                    || isPasswordUnLocked() || isButtonUnLocked(epData)
                    || isPasswordUnLocked(epData) || isFingerUnLocked(epData)
                    || isNewFingerUnLocked(epData) || isCardUnLocked(epData)
                    || isNewCardUnLocked(epData) || isKeyUnLocked()
                    || isDoorUnLocked() || isAppUnLocked();
        }

    }

    public boolean isAppUnLocked() {
        return TextUtils.equals("1", epData);
    }

    public boolean isReverseLock() {
        return TextUtils.equals("20", epData);
    }

    public boolean isRemoveLock() {
        return TextUtils.equals("19", epData);
    }

    public boolean isDoorUnLocked() {
        return TextUtils.equals("21", epData);
    }

    public boolean isDoorLocked() {
        return TextUtils.equals("22", epData);
    }

    public boolean isPasswordUnLocked() {
        return TextUtils.equals("30", epData);
    }

    public boolean isKeyUnLocked() {
        return TextUtils.equals("138", epData);
    }

    public boolean isButtonUnLocked(String epData) {
        int secureState = StringUtil.toInteger(epData);
        return OPEN_BUTTON_RANGE.contains(secureState, 0);
    }

    public boolean isFingerUnLocked(String epData) {
        int secureState = StringUtil.toInteger(epData);
        return OPEN_FINGER_MARK_RANGE.contains(secureState, 0);
    }

    public boolean isNewFingerUnLocked(String epData) {
        int secureState = StringUtil.toInteger(epData);
        return NEW_OPEN_FINGER_MARK_RANGE.contains(secureState, 0);
    }

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

//    public void updateExtdata(String epdata,String extData){
//        if(this.getData()!=null&&this.getData().size()>0){
//            for(MessageEventEntity item :this.getData()){
//                if (epdata.equals(item.getEpData())){
//                    item.setExtData(extData);
//                }
//            }
//            this.notifyDataSetChanged();
//        }
//
//    }

    private boolean isSetSuccess = false;

    @Override
    public void Reply406Result(Command406Result result) {
        mDialogManager.dimissDialog(DOOR_LOCK_ACCOUNT_HISTORY, 0);
        this.notifyDataSetChanged();
        WLDialog.Builder toOpenTheLockDialog = new WLDialog.Builder(mContext);
        View toOpenTheLock = LayoutInflater.from(mContext).inflate(cc.wulian.smarthomev5.R.layout.to_open_the_lock, null);
        TextView tvOpenLock = (TextView) toOpenTheLock.findViewById(R.id.tv_to_open_the_lock);
        tvOpenLock.setText(mContext.getResources().getString(R.string.smart_clock_recording_card_fingerprint));
        toOpenTheLockDialog.setTitle(mContext.getResources().getString(R.string.PROMPT))
                .setCancelOnTouchOutSide(true)
                .setContentView(toOpenTheLock)
                .setPositiveButton(mContext.getResources().getString(android.R.string.ok))
                .setNegativeButton(null)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View view) {

                    }

                    @Override
                    public void onClickNegative(View view) {
                    }
                });
        Dialog dialog = toOpenTheLockDialog.create();
        if (!dialog.isShowing() && isSetSuccess) {
            dialog.show();
        }


    }

    @Override
    public void Reply406Result(List<Command406Result> results) {

    }
}
