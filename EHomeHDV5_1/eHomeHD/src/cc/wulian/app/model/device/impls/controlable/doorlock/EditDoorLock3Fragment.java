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
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.adapter.DoorLockAlarmSettingAdapter;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;

public class EditDoorLock3Fragment extends WulianFragment {

    public static final String DEVICE_PASS_WORD = "DEVICE_PASS_WORD";
    private WulianDevice DoorDevice;
    private LinearLayout mDoorLayout;
    public static final String DEVICE_DOOR_LOCK_3 = "DEVICE_DOOR_LOCK_3";
    public static final String GWID = "gwid";
    public static final String DEVICEID = "deviceid";

    private LinearLayout mChangePWLayout;
    private LinearLayout mSceneBind;
    private ToggleButton alarmRemindButton;
    @ViewInject(R.id.device_door_setting_alarm_listview)
    private ListView alarmRemindListView;
    private DoorLockAlarmSettingAdapter settingDoorLockAdapter;
    private Preference preference = Preference.getPreferences();
    protected String type;
    private String gwID;
    private String devID;
    private String lockPass;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEditDevice();
        DoorDevice.onAttachView(mActivity);
        settingDoorLockAdapter = new DoorLockAlarmSettingAdapter(mActivity);
        initBar();
    }

    private void initEditDevice() {
        gwID = getArguments().getString(GWID);
        devID = getArguments().getString(DEVICEID);
        lockPass = getArguments().getString(DEVICE_PASS_WORD);
        DoorDevice = DeviceCache.getInstance(mActivity).getDeviceByID(
                mActivity, gwID, devID);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.device_door_loack_setting,
                container, false);
        ViewUtils.inject(this, contentView);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mChangePWLayout = (LinearLayout) view
                .findViewById(R.id.door_lock_setting_change_password);
        mSceneBind = (LinearLayout) view
                .findViewById(R.id.door_lock_setting_scenebind);
        mChangePWLayout.setOnClickListener(settingOnClickListener);
        mSceneBind.setOnClickListener(settingOnClickListener);
        alarmRemindButton = (ToggleButton) view
                .findViewById(R.id.device_door_setting_switch);
        alarmRemindListView.setAdapter(settingDoorLockAdapter);
        alarmRemindListView.setVisibility(View.GONE);
        alarmRemindButton.setVisibility(View.GONE);
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
//                            initItem();
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

    private View.OnClickListener settingOnClickListener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.door_lock_setting_change_password:
                    Intent intent1 = new Intent(mActivity,
                            DeviceSettingActivity.class);
                    intent1.putExtra(EditDoorLockFragment.GWID, getArguments()
                            .getString(GWID));
                    intent1.putExtra(EditDoorLockFragment.DEVICEID, getArguments()
                            .getString(DEVICEID));
                    intent1.putExtra(EditDoorLockFragment.DEVICE_DOOR_LOCK_12, type);
                    intent1.putExtra(EditDoorLockFragment.DEVICE_PASS_WORD_69, lockPass);
                    intent1.putExtra(AbstractDevice.SETTING_LINK_TYPE,
                            AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
                    intent1.putExtra(
                            DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME,
                            EditDoorLockFragment.class.getName());
                    mActivity.startActivity(intent1);
                    break;
                case R.id.door_lock_setting_scenebind:
                    Intent intent2 = new Intent(mActivity,
                            DeviceSettingActivity.class);
                    intent2.putExtra(EditDoorLock4Fragment.GWID, getArguments()
                            .getString(GWID));
                    intent2.putExtra(EditDoorLock4Fragment.DEVICEID, getArguments()
                            .getString(DEVICEID));
                    intent2.putExtra(EditDoorLock4Fragment.DEVICE_DOOR_LOCK_4, type);
                    intent2.putExtra(AbstractDevice.SETTING_LINK_TYPE,
                            AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
                    intent2.putExtra("remind", "value");
                    intent2.putExtra(
                            DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME,
                            EditDoorLock4Fragment.class.getName());
                    mActivity.startActivity(intent2);
                    break;
                default:
                    break;
            }
        }
    };

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
//        initItem();
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
        settingItem.add(passwordItem);
        settingItem.add(buttomItem);
        settingItem.add(fingerItem);
        settingItem.add(cardItem);
        settingItem.add(keyItem);

        settingDoorLockAdapter.swapData(settingItem);
    }
}
