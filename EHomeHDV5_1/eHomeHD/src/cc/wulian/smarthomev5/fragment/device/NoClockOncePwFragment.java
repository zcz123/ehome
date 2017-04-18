package cc.wulian.smarthomev5.fragment.device;

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
import android.widget.Toast;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.WlDialogUtil;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

/**
 * OW门锁无RTC单次密码下发
 *
 * @author Administrator
 */
public class NoClockOncePwFragment extends WulianFragment implements
        android.view.View.OnClickListener {

    @ViewInject(R.id.doorlock_password)
    private EditText doorlock_password;
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
    private static final String OW_DOOR_LOCK_ONCE_PW = "OW_DOOR_LOCK_ONCE_PW";
    private static DeviceCache deviceCache;
    private String gwID, devID;
    private WLDialog dialog;
    private int Sum = 0;

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
                getResources().getString(R.string.OW_onceuser_pw));
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
        View contentView = inflater.inflate(R.layout.doorlock_once_password2,
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
        once_ensure.setBackgroundResource(R.color.gray);
        once_delete.setOnClickListener(this);
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

    @Override
    public void onClick(View v) {
        // TODO Auto-generated method stub

        if (v == once_ensure) {
            SendOncePassword();
        } else if (v == once_delete) {
            DeleteExistPasswordDialog();
        } else if (v == choosepdstatus) {
            switchpressState();
        } else if (v == btnShare) {
            String msg = "[物联传感]您好，智能门锁开门密码已修改，密码为:" + doorlock_password.getText() + ",单次有效";
            WlDialogUtil.owSharePwdDiaolg(getActivity(), msg);
        }

    }

    // 密码重复
    public Boolean isReset(String epData) {
        return DeviceUtil.isSameAs("0801", epData);
    }

    // 密码非法
    public Boolean isUnsafe(String epData) {
        return DeviceUtil.isSameAs("0802", epData);
    }

    // 时钟无效
    public Boolean isClockInvald(String epData) {
        return DeviceUtil.isSameAs("0804", epData);
    }

    // 密码下发成功
    public Boolean isGetinSuccess(String epData) {
        return DeviceUtil.isSameAs("0806", epData);
    }

    // 操作失敗
    public Boolean isGetinFailed(String epData) {
        return DeviceUtil.isSameAs("0810", epData);
    }

    // 删除成功
    public Boolean isDeleteSuccess(String epData) {
        return DeviceUtil.isSameAs("0813", epData);
    }

    // 单次用户添加已满
    public boolean isBeyondOncePassword(String epData) {
        return DeviceUtil.isSameAs("0221", epData);
    }

    public void onEventMainThread(DeviceEvent event) {
        if (!StringUtil.isNullOrEmpty(event.deviceInfo.getDevID()) && event.deviceInfo.getDevID().equals(devID)) {
            deviceCache = DeviceCache.getInstance(getActivity());
            WulianDevice wulianDevice = deviceCache.getDeviceByID(getActivity(),
                    gwID, devID);
            String epData = wulianDevice.getDeviceInfo().getDevEPInfo().getEpData();
            mDialogManager.dimissDialog(OW_DOOR_LOCK_ONCE_PW, 0);
            if (isReset(epData)) {
                WLToast.showToast(mActivity,
                        getResources().getString(R.string.OW_oncepw_reset), 1000);
                mActivity.finish();
            }
            if (isUnsafe(epData)) {
                WLToast.showToast(getActivity(),
                        getResources().getString(R.string.OW_oncepw_unsafe), 1000);
                mActivity.finish();
            }
            if (isClockInvald(epData)) {
                WLToast.showToast(getActivity(), getResources().getString(R.string.ow_lock_setting_invalid_clock), 1000);
                mActivity.finish();
            }
            if (isGetinSuccess(epData)) {
                WLToast.showToast(
                        mActivity,
                        getResources().getString(
                                R.string.OW_oncepw_getin_success), 1000);
                btnShare.setBackgroundResource(R.color.action_bar_bg);
                btnShare.setEnabled(true);
            }
            if (isGetinFailed(epData)) {
                WLToast.showToast(getActivity(),
                        getResources().getString(R.string.OW_oncepw_getin_fauiled),
                        1000);
                mActivity.finish();
            }
            if (isDeleteSuccess(epData)) {
                WLToast.showToast(getActivity(),
                        getResources().getString(R.string.OW_once_delete_success),
                        1000);
            }
            if (isBeyondOncePassword(epData)) {
                WLToast.showToast(getActivity(), getResources().getString(R.string.ow_lock_setting_ordinary_users_to_add_full), 1000);
            }
        }
    }

    // 密码显示明文和密文add by huxc
    private void switchpressState() {
        // TODO Auto-generated method stub

        if (choosepdstatus.getTag() == "invisable") {
            choosepdstatus.setBackgroundResource(R.drawable.dm_router_setting_wifi_pwd_visibale);
            doorlock_password
                    .setTransformationMethod(HideReturnsTransformationMethod
                            .getInstance());
            doorlock_password
                    .setSelection(doorlock_password.getText().length());// 将光标移至文字末尾
            choosepdstatus.setTag("visable");

        } else if (choosepdstatus.getTag() == "visable") {
            doorlock_password
                    .setTransformationMethod(PasswordTransformationMethod
                            .getInstance());
            choosepdstatus
                    .setBackgroundResource(R.drawable.dm_router_setting_wifi_pwd_invisibale);
            doorlock_password
                    .setSelection(doorlock_password.getText().length());// 将光标移至文字末尾
            choosepdstatus.setTag("invisable");

        }

    }

    private void SendOncePassword() {
        String password = doorlock_password.getText().toString();
        int Pwlength = password.length();
        if (Pwlength > 5) {
            String data = "4" + Pwlength + "" + password + "0000000000";
            SendMessage.sendControlDevMsg(gwID, devID, "14", "ow", data);
            mDialogManager.showDialog(OW_DOOR_LOCK_ONCE_PW, mActivity, null,
                    null);
        }
    }

    // 删除已有单次密码对话框 add by huxc
    private void DeleteExistPasswordDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(getActivity());
        builder.setMessage(getResources().getString(R.string.ow_lock_setting_one_time_password_delete))
                .setPositiveButton(R.string.common_ok)
                .setNegativeButton(R.string.cancel)
                .setListener(new MessageListener() {

                    @Override
                    public void onClickPositive(View contentViewLayout) {
                        SendMessage.sendControlDevMsg(gwID, devID, "14", "ow",
                                "8");
                        mDialogManager.showDialog(OW_DOOR_LOCK_ONCE_PW,
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
}
