package cc.wulian.smarthomev5.fragment.device;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.format.DateFormat;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.controlable.cooker.DeviceNewDoorLockAccountTemporaryTimeView;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.utils.WlDialogUtil;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * OW门锁带RTC普通密码下发
 *
 * @author hxc
 */
public class TempPasswordFragment extends WulianFragment implements
        android.view.View.OnClickListener {

    private DeviceNewDoorLockAccountTemporaryTimeView timingSettingView;
    @ViewInject(R.id.door_lock_pded)
    private EditText doorlock_password;
    @ViewInject(R.id.common_starttime)
    private TextView starttime;
    @ViewInject(R.id.common_stoptime)
    private TextView stoptime;
    @ViewInject(R.id.doorlock_ensure)
    private Button doorlock_ensure;
    @ViewInject(R.id.choose_pw_status)
    private ImageButton choosepdstatus;
    @ViewInject(R.id.btn_share_password)
    private Button btnShare;

    private static final String RTC_OW_DOOR_LOCK_COMMON_PW = "RTC_OW_DOOR_LOCK_COMMON_PW";
    private String startyear;
    private String startmonths;
    private String startday;
    private String starthour;
    private String startminute;
    private String endyear;
    private String endmonths;
    private String endday;
    private String endhour;
    private String endminute;
    private String selectTime;

    public static final String GWID = "gwid";
    public static final String DEVICEID = "deviceid";
    private static DeviceCache deviceCache;
    private String gwID, devID;
    private WLDialog dialog;

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
        this.mActivity.resetActionMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayIconEnabled(true);
        getSupportActionBar().setDisplayIconTextEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setDisplayShowMenuEnabled(false);
        getSupportActionBar().setDisplayShowMenuTextEnabled(false);
        getSupportActionBar().setIconText(getResources().getString(R.string.device_ir_back));
        getSupportActionBar().setTitle(getResources().getString(R.string.ow_temporary_password));
        getSupportActionBar().setLeftIconClickListener(
                new OnLeftIconClickListener() {
                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        getActivity().finish();
                    }
                });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.doorlock_commom_password,
                container, false);
        ViewUtils.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        starttime.setOnClickListener(this);
        stoptime.setOnClickListener(this);
        doorlock_ensure.setOnClickListener(this);
        choosepdstatus.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        choosepdstatus.setTag("invisable");
        doorlock_password.addTextChangedListener(new TextWatcher() {

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

        if (v == starttime) {
            showChooseTimeDialog(starttime);
        } else if (v == stoptime) {
            showChooseTimeDialog(stoptime);
        } else if (v == doorlock_ensure) {
            if (!StringUtil.isNullOrEmpty(starttime.getText().toString()) &&
                    !StringUtil.isNullOrEmpty(stoptime.getText().toString())) {
                SendCommonPassword();
            }
        } else if (v == choosepdstatus) {
            switchpressState();
        } else if(v == btnShare){
            String msg = "【物联传感】您好，智能门锁开门密码已修改，密码为："+doorlock_password.getText()+",有效期至"+stoptime.getText();
            WlDialogUtil.owSharePwdDiaolg(getActivity(),msg);
        }
    }

    // 密码显示明文和密文add by huxc
    private void switchpressState() {
        // TODO Auto-generated method stub

        if (choosepdstatus.getTag() == "invisable") {
            doorlock_password
                    .setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
            choosepdstatus
                    .setBackgroundResource(R.drawable.dm_router_setting_wifi_pwd_visibale);
            doorlock_password
                    .setSelection(doorlock_password.getText().length());// 将光标移至文字末尾
            choosepdstatus.setTag("visable");

        } else if (choosepdstatus.getTag() == "visable") {
            choosepdstatus
                    .setBackgroundResource(R.drawable.dm_router_setting_wifi_pwd_invisibale);
            doorlock_password
                    .setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
            doorlock_password
                    .setSelection(doorlock_password.getText().length());// 将光标移至文字末尾
            choosepdstatus.setTag("invisable");
        }

    }

    protected View createViewTime(String data[]) {
        timingSettingView = new DeviceNewDoorLockAccountTemporaryTimeView(
                mActivity,data);
        return timingSettingView;
    }

    // 选择时间
    private void showChooseTimeDialog(final View v) {
        Date nowTime = new Date(System.currentTimeMillis());
        SimpleDateFormat sf = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
        String currentTime = sf.format(nowTime);
        String data[] = currentTime.split(":");
        WLDialog.Builder builder = new WLDialog.Builder(mActivity);
        builder.setTitle(R.string.gateway_dream_flower_time_show_select_time);
        builder.setContentView(createViewTime(data));
        builder.setPositiveButton(android.R.string.ok);
        builder.setNegativeButton(android.R.string.cancel);
        builder.setListener(new MessageListener() {
            @Override
            public void onClickPositive(View contentViewLayout) {

                if (v == starttime) {
                    startyear = timingSettingView.getSettingYearTime();
                    startmonths = timingSettingView.getSettingMonthTime();
                    startday = timingSettingView.getSettingDayTime();
                    starthour = timingSettingView.getSettingHourTime();
                    startminute = timingSettingView.getSettingMinuesTime();
                    startTime = StringUtil.appendLeft(startyear + "", 2, '0')
                            + "."
                            + StringUtil.appendLeft(startmonths + "", 2, '0')
                            + "."
                            + StringUtil.appendLeft(startday + "", 2, '0')
                            + " "
                            + StringUtil.appendLeft(starthour + "", 2, '0')
                            + ":"
                            + StringUtil.appendLeft(startminute + "", 2, '0');
                    starttime.setText(startTime);
                    starttime.setTextColor(getResources().getColor(R.color.action_bar_bg));
                } else if (v == stoptime) {
                    endyear = timingSettingView.getSettingYearTime();
                    endmonths = timingSettingView.getSettingMonthTime();
                    endday = timingSettingView.getSettingDayTime();
                    endhour = timingSettingView.getSettingHourTime();
                    endminute = timingSettingView.getSettingMinuesTime();
                    endTime = StringUtil.appendLeft(endyear + "", 2, '0')
                            + "."
                            + StringUtil.appendLeft(endmonths + "", 2, '0')
                            + "." + StringUtil.appendLeft(endday + "", 2, '0')
                            + " " + StringUtil.appendLeft(endhour + "", 2, '0')
                            + ":"
                            + StringUtil.appendLeft(endminute + "", 2, '0');
                    stoptime.setText(endTime);
                    stoptime.setTextColor(getResources().getColor(R.color.action_bar_bg));
                }
            }

            @Override
            public void onClickNegative(View contentViewLayout) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    private String startTime = "";
    private String endTime = "";

    private void SendCommonPassword() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd HH:mm");
        long interval = 0;
        try {
            long takeTime = sdf.parse(startTime).getTime();
            long loseTime = sdf.parse(endTime).getTime();
            interval = loseTime - takeTime;

        } catch (Exception e) {
            e.printStackTrace();
        }

        String password = doorlock_password.getText().toString();
        int length = password.length();
        String clockstarttime = startyear.substring(2, 4) + startmonths + startday + starthour
                + startminute;
        String clockendtime = endyear.substring(2, 4) + endmonths + endday + endhour
                + endminute;

        String data = 3 + "" + length + password + clockstarttime
                + clockendtime;
        Log.i("test", (Integer.parseInt(endmonths) - 01) + "");
        if (interval <= 0) {
            WLToast.showToast(mActivity, getResources().getString(R.string.device_lock_op_endTimeRequireLargeStartTime), 1000);
        } else if ((starttime.getText().length() > 0)
                && (stoptime.getText().length() > 0)) {
            SendMessage.sendControlDevMsg(gwID, devID, "14", "ow", data);
            mDialogManager.showDialog(RTC_OW_DOOR_LOCK_COMMON_PW, mActivity,
                    null, null);
        } else {
            WLToast.showToast(mActivity, getResources().getString(R.string.ow_lock_setting_is_not_complete), 1000);
        }
    }

    public void onEventMainThread(DeviceEvent event) {
        if(!StringUtil.isNullOrEmpty(event.deviceInfo.getDevID())&&event.deviceInfo.getDevID().equals(devID)) {
            deviceCache = DeviceCache.getInstance(getActivity());
            WulianDevice wulianDevice = deviceCache.getDeviceByID(getActivity(),
                    gwID, devID);
            String epData = wulianDevice.getDeviceInfo().getDevEPInfo().getEpData();
            Log.e("epdata", epData);
            mDialogManager.dimissDialog(RTC_OW_DOOR_LOCK_COMMON_PW, 0);
            if (PasswordRepeat(epData)) {
                WLToast.showToast(getActivity(),
                        getResources().getString(R.string.OW_pw_reset_all), 1000);
            }
            if (PasswordIllegal(epData)) {
                WLToast.showToast(getActivity(),
                        getResources().getString(R.string.OW_pw_unsafe_all), 1000);
            }
            if (TimeUseless(epData)) {
                WLToast.showToast(getActivity(), getResources().getString(R.string.ow_lock_setting_invalid_clock), 1000);
            }
            if (AddSuccessful(epData)) {
                WLToast.showToast(getActivity(),
                        getResources().getString(R.string.OW_pw_addin_success_all),
                        1000);
                btnShare.setBackgroundResource(R.color.action_bar_bg);
                btnShare.setEnabled(true);
//                getActivity().finish();
            }
            if (OperationFail(epData)) {
                WLToast.showToast(getActivity(),
                        getResources().getString(R.string.OW_oncepw_getin_fauiled),
                        1000);
            }
            if (isFullTempPwd(epData)) {
                WLToast.showToast(getActivity(), getResources().getString(R.string.ow_temporary_user_is_full),
                        1000);
            }
        }

    }

    /**
     * 密码重复
     *
     * @param epdata
     * @return
     */
    public boolean PasswordRepeat(String epdata) {
        if (!StringUtil.isNullOrEmpty(epdata) && epdata.equals("0801")) {
            return true;
        }
        return false;
    }

    /**
     * 密码非法
     *
     * @param epdata
     * @return
     */
    public boolean PasswordIllegal(String epdata) {
        if (!StringUtil.isNullOrEmpty(epdata) && epdata.equals("0802")) {
            return true;
        }
        return false;
    }

    /**
     * 时钟无效
     *
     * @param epdata
     * @return
     */
    public boolean TimeUseless(String epdata) {
        if (!StringUtil.isNullOrEmpty(epdata) && epdata.equals("0804")) {
            return true;
        }
        return false;
    }

    /**
     * 添加成功
     *
     * @param epdata
     * @return
     */
    public boolean AddSuccessful(String epdata) {
        if (!StringUtil.isNullOrEmpty(epdata) && epdata.equals("0806")) {
            return true;
        }
        return false;
    }

    private boolean isFullTempPwd(String epdata) {
        if (!StringUtil.isNullOrEmpty(epdata) && epdata.equals("0224")) {
            return true;
        }
        return false;
    }

    /**
     * 操作失败
     *
     * @param epdata
     * @return
     */
    public boolean OperationFail(String epdata) {
        if (!StringUtil.isNullOrEmpty(epdata) && epdata.equals("0810")) {
            return true;
        }
        return false;
    }
}
