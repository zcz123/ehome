package cc.wulian.app.model.device.impls.controlable.doorlock;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ToggleButton;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingItemClickActivity;
import cc.wulian.smarthomev5.adapter.DoorLockAlarmSettingAdapter;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;

import com.lidroid.xutils.ViewUtils;
import com.qiniu.android.utils.StringUtils;

public class EditDoorLock4Fragment extends WulianFragment implements View.OnClickListener {

    private WulianDevice DoorDevice;
    private LinearLayout mDoorLayout;
    public static final String DEVICE_DOOR_LOCK_4 = "DEVICE_DOOR_LOCK_4";
    public static final String GWID = "gwid";
    public static final String DEVICEID = "deviceid";
    private LinearLayout device_door_setting_remind;
    private String gwID;
    private String devID;
    private String judgeRemind;

    private ToggleButton alarmRemindButton;
    private ListView alarmRemindListView;
    private DoorLockAlarmSettingAdapter settingDoorLockAdapter;
    private Preference preference = Preference.getPreferences();
    private LinearLayout llDoorLockSettingScenebind;
    private String epType;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        initEditDevice();
        DoorDevice.onAttachView(mActivity);
        settingDoorLockAdapter = new DoorLockAlarmSettingAdapter(mActivity);
        initBar();
    }

    private void initEditDevice() {
        gwID = getArguments().getString(GWID);
        devID = getArguments().getString(DEVICEID);
        epType = getArguments().getString(DEVICE_DOOR_LOCK_4);
        judgeRemind = getArguments().getString("remind");
        DoorDevice = DeviceCache.getInstance(mActivity).getDeviceByID(
                mActivity, gwID, devID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.device_door_lock_layout,
                container, false);
        ViewUtils.inject(this, contentView);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mDoorLayout = (LinearLayout) view
                .findViewById(R.id.config_door_setting_ll);
        View settingDoorView = DoorDevice.onCreateSettingView(
                LayoutInflater.from(mActivity), null);
        mDoorLayout.addView(settingDoorView);
        device_door_setting_remind = (LinearLayout) view
                .findViewById(R.id.device_door_four_setting_remind);
        alarmRemindButton = (ToggleButton) view
                .findViewById(R.id.device_door_four_setting_switch);
        llDoorLockSettingScenebind = (LinearLayout) view.findViewById(R.id.door_lock_setting_scenebind);
        alarmRemindListView = (ListView) view
                .findViewById(R.id.device_door_four_setting_alarm_listview);
        if ("70".equals(epType)) {
            llDoorLockSettingScenebind.setVisibility(View.VISIBLE);
        } else {
            llDoorLockSettingScenebind.setVisibility(View.GONE);
        }
        llDoorLockSettingScenebind.setOnClickListener(this);
        alarmRemindListView.setAdapter(settingDoorLockAdapter);
//		if (!StringUtil.isNullOrEmpty(judgeRemind)
//				&& (judgeRemind.equals("value"))) {// 安朗杰门锁绑定场景页中的提醒按钮给予设置不可见
//			alarmRemindListView.setVisibility(View.INVISIBLE);
//			device_door_setting_remind.setVisibility(View.INVISIBLE);
//		} else {
//			alarmRemindListView.setVisibility(View.VISIBLE);
//			device_door_setting_remind.setVisibility(View.VISIBLE);
//		}
        alarmRemindButton.setChecked(preference.getBoolean(gwID + devID
                + IPreferenceKey.P_KEY_ALARM_ALL_DOOR_LOCK, true));
        if (!alarmRemindButton.isChecked()) {
            alarmRemindListView.setVisibility(View.GONE);
        }
        alarmRemindButton
                .setOnCheckedChangeListener(new OnCheckedChangeListener() {

                    @Override
                    public void onCheckedChanged(CompoundButton buttonView,
                                                 boolean isChecked) {
                        if (isChecked) {
                            alarmRemindListView.setVisibility(View.VISIBLE);
                            preference.putBoolean(gwID + devID
                                            + IPreferenceKey.P_KEY_ALARM_ALL_DOOR_LOCK,
                                    isChecked);
                            preference
                                    .putBoolean(
                                            gwID
                                                    + devID
                                                    + IPreferenceKey.P_KEY_ALARM_OPEN_DOOR_LOCK,
                                            isChecked);
                            preference
                                    .putBoolean(
                                            gwID
                                                    + devID
                                                    + IPreferenceKey.P_KEY_ALARM_CLOSE_DOOR_LOCK,
                                            isChecked);
                            preference
                                    .putBoolean(
                                            gwID
                                                    + devID
                                                    + IPreferenceKey.P_KEY_ALARM_PWD_ERROR_DOOR_LOCK,
                                            isChecked);
                            preference
                                    .putBoolean(
                                            gwID
                                                    + devID
                                                    + IPreferenceKey.P_KEY_ALARM_PASSWORD_DOOR_LOCK,
                                            isChecked);
                            preference
                                    .putBoolean(
                                            gwID
                                                    + devID
                                                    + IPreferenceKey.P_KEY_ALARM_BUTTON_DOOR_LOCK,
                                            isChecked);
                            preference
                                    .putBoolean(
                                            gwID
                                                    + devID
                                                    + IPreferenceKey.P_KEY_ALARM_FINGER_DOOR_LOCK,
                                            isChecked);
                            preference
                                    .putBoolean(
                                            gwID
                                                    + devID
                                                    + IPreferenceKey.P_KEY_ALARM_CARD_DOOR_LOCK,
                                            isChecked);
                            preference.putBoolean(gwID + devID
                                            + IPreferenceKey.P_KEY_ALARM_KEY_DOOR_LOCK,
                                    isChecked);
//							initItem();
                        } else {
                            alarmRemindListView.setVisibility(View.GONE);
                            preference.putBoolean(gwID + devID
                                            + IPreferenceKey.P_KEY_ALARM_ALL_DOOR_LOCK,
                                    isChecked);
                            preference
                                    .putBoolean(
                                            gwID
                                                    + devID
                                                    + IPreferenceKey.P_KEY_ALARM_OPEN_DOOR_LOCK,
                                            isChecked);
                            preference
                                    .putBoolean(
                                            gwID
                                                    + devID
                                                    + IPreferenceKey.P_KEY_ALARM_CLOSE_DOOR_LOCK,
                                            isChecked);
                            preference
                                    .putBoolean(
                                            gwID
                                                    + devID
                                                    + IPreferenceKey.P_KEY_ALARM_PWD_ERROR_DOOR_LOCK,
                                            isChecked);
                            preference
                                    .putBoolean(
                                            gwID
                                                    + devID
                                                    + IPreferenceKey.P_KEY_ALARM_PASSWORD_DOOR_LOCK,
                                            isChecked);
                            preference
                                    .putBoolean(
                                            gwID
                                                    + devID
                                                    + IPreferenceKey.P_KEY_ALARM_BUTTON_DOOR_LOCK,
                                            isChecked);
                            preference
                                    .putBoolean(
                                            gwID
                                                    + devID
                                                    + IPreferenceKey.P_KEY_ALARM_FINGER_DOOR_LOCK,
                                            isChecked);
                            preference
                                    .putBoolean(
                                            gwID
                                                    + devID
                                                    + IPreferenceKey.P_KEY_ALARM_CARD_DOOR_LOCK,
                                            isChecked);
                            preference.putBoolean(gwID + devID
                                            + IPreferenceKey.P_KEY_ALARM_KEY_DOOR_LOCK,
                                    isChecked);
                        }

                    }
                });

    }

    private void initBar() {
        mActivity.resetActionMenu();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setIconText(
                getResources().getString(R.string.device_ir_back));
        getSupportActionBar().setTitle(
                getResources().getString(R.string.device_ir_setting));
        getSupportActionBar().setLeftIconClickListener(
                new OnLeftIconClickListener() {

                    @Override
                    public void onClick(View v) {
                        mActivity.finish();
                    }
                });
    }

    @Override
    public void onResume() {
        super.onResume();
//		initItem();
    }

    private void initItem() {
        List<AbstractDoorLockAlarmItem> settingItem = new ArrayList<AbstractDoorLockAlarmItem>();
        AbstractDoorLockAlarmItem openItem = new AbstractDoorLockAlarmItem(
                mActivity,
                null,
                mActivity
                        .getString(R.string.home_device_alarm_type_doorlock_open),
                gwID + devID + IPreferenceKey.P_KEY_ALARM_OPEN_DOOR_LOCK);

        AbstractDoorLockAlarmItem closeItem = new AbstractDoorLockAlarmItem(
                mActivity,
                null,
                mActivity
                        .getString(R.string.home_device_alarm_type_doorlock_close),
                gwID + devID + IPreferenceKey.P_KEY_ALARM_CLOSE_DOOR_LOCK);

        AbstractDoorLockAlarmItem pwdErrorItem = new AbstractDoorLockAlarmItem(
                mActivity,
                null,
                mActivity
                        .getString(R.string.home_device_alarm_type_doorlock_error),
                gwID + devID + IPreferenceKey.P_KEY_ALARM_PWD_ERROR_DOOR_LOCK);

        AbstractDoorLockAlarmItem passwordItem = new AbstractDoorLockAlarmItem(
                mActivity, null,
                mActivity.getString(R.string.device_alarm_type_doorlock_pwd),
                gwID + devID + IPreferenceKey.P_KEY_ALARM_PASSWORD_DOOR_LOCK);

        AbstractDoorLockAlarmItem buttomItem = new AbstractDoorLockAlarmItem(
                mActivity,
                null,
                mActivity
                        .getString(R.string.home_device_alarm_type_doorlock_button),
                gwID + devID + IPreferenceKey.P_KEY_ALARM_BUTTON_DOOR_LOCK);

        AbstractDoorLockAlarmItem fingerItem = new AbstractDoorLockAlarmItem(
                mActivity,
                null,
                mActivity
                        .getString(R.string.home_device_alarm_type_doorlock_finger),
                gwID + devID + IPreferenceKey.P_KEY_ALARM_FINGER_DOOR_LOCK);

        AbstractDoorLockAlarmItem cardItem = new AbstractDoorLockAlarmItem(
                mActivity,
                null,
                mActivity
                        .getString(R.string.home_device_alarm_type_doorlock_card),
                gwID + devID + IPreferenceKey.P_KEY_ALARM_CARD_DOOR_LOCK);

        AbstractDoorLockAlarmItem keyItem = new AbstractDoorLockAlarmItem(
                mActivity,
                null,
                mActivity
                        .getString(R.string.home_device_alarm_type_doorlock_key),
                gwID + devID + IPreferenceKey.P_KEY_ALARM_KEY_DOOR_LOCK);

        settingItem.add(openItem);
        settingItem.add(closeItem);
        settingItem.add(pwdErrorItem);
        settingItem.add(passwordItem);
        settingItem.add(buttomItem);
        settingItem.add(fingerItem);
        settingItem.add(cardItem);
        settingItem.add(keyItem);

        settingDoorLockAdapter.swapData(settingItem);
    }

    public static final String DEVICE_DOOR_LOCK_TYPE = "device_door_lock_type";

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.door_lock_setting_scenebind:
                Intent intent = new Intent(mActivity, DeviceSettingItemClickActivity.class);
                intent.putExtra(EditDoorLock4Fragment.GWID, gwID);
                intent.putExtra(EditDoorLock4Fragment.DEVICEID, devID);
                intent.putExtra(EditDoorLock4Fragment.DEVICE_DOOR_LOCK_TYPE, "70");
//				intent.putExtra(EditDoorLock6Fragment.TOKEN, mToken);
                intent.putExtra(
                        DeviceSettingItemClickActivity.SETTING_ITEM_FRAGMENT_CLASSNAME,
                        EditDoorLock4HistoryFragment.class.getName());
                startActivity(intent);
                break;
        }
    }
}
