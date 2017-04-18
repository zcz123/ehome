package cc.wulian.smarthomev5.fragment.device;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.TextWatcher;
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
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.WlDialogUtil;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

/**
 * OW门锁带RTC单次密码下发
 *
 * @author hxc
 */
public class OncePasswordFragment extends WulianFragment implements
        android.view.View.OnClickListener {
    @ViewInject(R.id.doorlock_password)
    private EditText doorlock_password;
    @ViewInject(R.id.once_starttime)
    private TextView once_starttime;
    @ViewInject(R.id.once_ensure)
    private Button once_ensure;
    @ViewInject(R.id.once_delete)
    private Button once_delete;
    @ViewInject(R.id.choosepdstatus)
    private ImageButton choosepdstatus;
    @ViewInject(R.id.btn_share_password)
    private Button btnShare;

    public static final String GWID = "gwid";
    public static final String DEVICEID = "deviceid";
    private static DeviceCache deviceCache;
    private static final String RTC_OW_DOOR_LOCK_ONCE_PW = "RTC_OW_DOOR_LOCK_ONCE_PW";
    private static final String RTC_OW_PASSWORD_DELETE = "RTC_OW_PASSWORD_DELETE";
    private DeviceNewDoorLockAccountTemporaryTimeView timingSettingView;
    private String gwID, devID;
    private WLDialog dialog;
    private String startyear;
    private String startmonths;
    private String startday;
    private String starthour;
    private String startminute;
    private String selectTime;

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
        getSupportActionBar().setIconText(
                getResources().getString(R.string.device_ir_back));
        getSupportActionBar().setTitle(
                getResources().getString(R.string.OW_onceuser_pw));
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
        View contentView = inflater.inflate(R.layout.doorlock_once_password,
                container, false);
        ViewUtils.inject(this, contentView);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onViewCreated(view, savedInstanceState);
        init();
    }

    private void init() {
        doorlock_password.setOnClickListener(this);
        once_ensure.setOnClickListener(this);
        once_delete.setOnClickListener(this);
        once_starttime.setOnClickListener(this);
        choosepdstatus.setOnClickListener(this);
        btnShare.setOnClickListener(this);
        choosepdstatus.setTag("invisable");
        doorlock_password.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                // TODO Auto-generated method stub
                if (doorlock_password.getText().length() >= 6) {
                    once_ensure.setBackgroundResource(R.color.action_bar_bg);
                }
                if (doorlock_password.getText().length() < 6) {
                    once_ensure.setBackgroundResource(R.color.gray);
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

    protected View createViewTime(String data[]) {
        timingSettingView = new DeviceNewDoorLockAccountTemporaryTimeView(
                mActivity,data);
        return timingSettingView;
    }

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub
        // TODO Auto-generated method stub
        if (v == once_starttime) {
            showChooseTimeDialog(once_starttime);
        } else if (v == choosepdstatus) {
            switchpressState();
        } else if (v == once_ensure) {
            if (!StringUtil.isNullOrEmpty(once_starttime.getText().toString())) {
                SendOncePassword();
            }
        } else if (v == once_delete) {
            DeleteExistPasswordDialog();
        } else if (v == btnShare) {
            String msg = "[物联传感]您好，智能门锁开门密码已修改，密码为:" + doorlock_password.getText() +
                    ",生效时间为:" + once_starttime.getText() + ",单次有效";
//            IntentUtil.sendMessage(getActivity(),msg);
            WlDialogUtil.owSharePwdDiaolg(getActivity(),msg);
        }
    }

    public void onEventMainThread(DeviceEvent event) {
        if (!StringUtil.isNullOrEmpty(event.deviceInfo.getDevID()) && event.deviceInfo.getDevID().equals(devID)) {
            deviceCache = DeviceCache.getInstance(getActivity());
            WulianDevice wulianDevice = deviceCache.getDeviceByID(getActivity(),
                    gwID, devID);
            String epData = wulianDevice.getDeviceInfo().getDevEPInfo().getEpData();
            Log.e("epdata", epData);
            mDialogManager.dimissDialog(RTC_OW_DOOR_LOCK_ONCE_PW, 0);
            mDialogManager.dimissDialog(RTC_OW_PASSWORD_DELETE, 0);
            if (epData.equals("0801")) {
                WLToast.showToast(getActivity(),
                        getResources().getString(R.string.OW_pw_reset_all), 1000);
            }
            if (epData.equals("0802")) {
                WLToast.showToast(getActivity(),
                        getResources().getString(R.string.OW_pw_unsafe_all), 1000);
            }
            if (epData.equals("0804")) {
                WLToast.showToast(getActivity(), getResources().getString(R.string.ow_lock_setting_invalid_clock), 1000);
            }
            if (epData.equals("0806")) {
                WLToast.showToast(getActivity(),
                        getResources().getString(R.string.OW_oncepw_getin_success),
                        1000);
//                getActivity().finish();
                btnShare.setBackgroundResource(R.color.action_bar_bg);
                btnShare.setEnabled(true);
            }
            if (epData.equals("0810")) {
                WLToast.showToast(getActivity(),
                        getResources().getString(R.string.OW_oncepw_getin_fauiled),
                        1000);
            }
            if (epData.equals("0813")) {
                WLToast.showToast(getActivity(),
                        getResources().getString(R.string.OW_once_delete_success),
                        1000);
            }
            if (epData.equals("0221")) {
                WLToast.showToast(getActivity(),
                        getResources().getString(cc.wulian.app.model.device.R.string.ow_lock_setting_one_time_users_to_add_full),
                        1000);
            }
        }
    }

    // 选择时间
    private void showChooseTimeDialog(final View v) {
         Date nowTime = new Date(System.currentTimeMillis());
         SimpleDateFormat sf = new SimpleDateFormat("yyyy:MM:dd:HH:mm");
         String currentTime = sf.format(nowTime);
        String data[] = currentTime.split(":");
        Log.i("currentTime",currentTime);
        WLDialog.Builder builder = new WLDialog.Builder(mActivity);
        builder.setTitle(R.string.gateway_dream_flower_time_show_select_time);
        builder.setContentView(createViewTime(data));
        builder.setPositiveButton(android.R.string.ok);
        builder.setNegativeButton(android.R.string.cancel);
        builder.setListener(new MessageListener() {
            @Override
            public void onClickPositive(View contentViewLayout) {

                if (v == once_starttime) {
                    startyear = timingSettingView.getSettingYearTime();
                    startmonths = timingSettingView.getSettingMonthTime();
                    startday = timingSettingView.getSettingDayTime();
                    starthour = timingSettingView.getSettingHourTime();
                    startminute = timingSettingView.getSettingMinuesTime();
                    selectTime = StringUtil.appendLeft(startyear + "", 2, '0')
                            + "."
                            + StringUtil.appendLeft(startmonths + "", 2, '0')
                            + "."
                            + StringUtil.appendLeft(startday + "", 2, '0')
                            + " "
                            + StringUtil.appendLeft(starthour + "", 2, '0')
                            + ":"
                            + StringUtil.appendLeft(startminute + "", 2, '0');

                    // Date nowTime = new Date(System.currentTimeMillis());
                    // SimpleDateFormat sdFormatter = new
                    // SimpleDateFormat("yyyy-MM-dd");
                    // String retStrFormatNowDate = sdFormatter.format(nowTime);

                    Calendar c = Calendar.getInstance();
                    c.setTime(new java.util.Date());
                    int year = c.get(Calendar.YEAR);
                    int month = c.get(Calendar.MONTH) + 1;
                    int day = c.get(Calendar.DAY_OF_MONTH);
                    int hour = c.get(Calendar.HOUR_OF_DAY);
                    int minute = c.get(Calendar.MINUTE);
                    int second = c.get(Calendar.SECOND);
                    System.out.println(c);
                    String currentTime = year + "." + month + "." + day + "  "
                            + hour + ":" + minute + ":" + second;
                    Log.e("time", currentTime);
                    Log.e("time", selectTime);
                    once_starttime.setText(selectTime);
                    once_starttime.setTextColor(getResources().getColor(R.color.action_bar_bg));
                    if (compare_date(currentTime, selectTime) == 1) {
                        once_starttime.setText(selectTime);
                        once_starttime.setTextColor(getResources().getColor(R.color.action_bar_bg));
                    }
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

    public static int compare_date(String DATE1, String DATE2) {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        try {
            Date dt1 = df.parse(DATE1);
            Date dt2 = df.parse(DATE2);
            if (dt1.getTime() > dt2.getTime()) {
                System.out.println("dt1 在dt2前");
                return 1;
            } else if (dt1.getTime() < dt2.getTime()) {
                System.out.println("dt1在dt2后");
                return -1;
            } else {
                return 0;
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return 0;
    }

    // 删除已有单次密码对话框 add by huxc
    private void DeleteExistPasswordDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.common_delete))
                .setPositiveButton(R.string.common_ok)
                .setNegativeButton(R.string.cancel)
                .setListener(new MessageListener() {

                    @Override
                    public void onClickPositive(View contentViewLayout) {
                        SendMessage.sendControlDevMsg(gwID, devID, "14", "ow",
                                "8");
                        mDialogManager.showDialog(RTC_OW_PASSWORD_DELETE,
                                mActivity, null, null);

                    }

                    @Override
                    public void onClickNegative(View contentViewLayout) {
                        dialog.dismiss();
                    }

                });
        dialog = builder.create();
        dialog.show();

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

    private void SendOncePassword() {
        String password = doorlock_password.getText().toString();
        String Pwlength = password.length() + "";
        String ClockTime = startyear.substring(2, 4) + startmonths + startday + starthour
                + startminute;
        String data = "4" + Pwlength + password + ClockTime;
        if (once_starttime.getText().length() > 0) {
            SendMessage.sendControlDevMsg(gwID, devID, "14", "ow", data);
            Log.e("data", data);
            mDialogManager.showDialog(RTC_OW_DOOR_LOCK_ONCE_PW, mActivity,
                    null, null);
        } else {
            WLToast.showToast(mActivity, getResources().getString(R.string.ow_lock_setting_is_not_complete), 1000);
        }
    }
}
