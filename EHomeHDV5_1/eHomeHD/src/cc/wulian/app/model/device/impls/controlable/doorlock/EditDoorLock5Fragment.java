package cc.wulian.app.model.device.impls.controlable.doorlock;

import java.util.HashMap;
import java.util.Map;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.ChoosePasswordTypeActivity;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.activity.NoClockChoosePwActivity;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.device.ChoosePasswordTypeFragment;
import cc.wulian.smarthomev5.fragment.device.NoClockChoosePwFragment;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.JsonTool;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SceneList;
import cc.wulian.smarthomev5.tools.SceneList.OnSceneListItemClickListener;
import cc.wulian.smarthomev5.tools.SendMessage;

import com.lidroid.xutils.ViewUtils;
import com.wulian.icam.utils.StringUtil;
import com.yuantuo.customview.ui.CustomToast;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

public class EditDoorLock5Fragment extends WulianFragment {
    private WulianDevice DoorDevice;
    public static final String DEVICE_DOOR_LOCK_5 = "DEVICE_DOOR_LOCK_5";
    public static final String GWID = "gwid";
    public static final String DEVICEID = "deviceid";
    private static final String OW_DOOR_LOCK_PASSWORD_ENSURE = "ow_door_lock_password_ensure";

    String epName[] = {"14", "15", "16", "17"};
    protected String type;
    private String gwID;
    private String devID;
    private WLDialog dialog;
    protected LinearLayout contentLineLayout;
    private static DeviceCache deviceCache;
    String editString = null;
    protected Map<String, SceneInfo> bindScenesMap;
    protected Map<String, DeviceInfo> bindDevicesMap;
    protected Preference preference = Preference.getPreferences();

    private LinearLayout setting_open_lock_password;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initEditDevice();
        DoorDevice.onAttachView(mActivity);
        initBar();
    }

    private void initEditDevice() {
        gwID = getArguments().getString(GWID);
        devID = getArguments().getString(DEVICEID);
        type = getArguments().getString(DEVICE_DOOR_LOCK_5);
        DoorDevice = DeviceCache.getInstance(mActivity).getDeviceByID(
                mActivity, gwID, devID);
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View contentView = inflater.inflate(R.layout.device_door_lock_5,
                container, false);
        contentLineLayout = (LinearLayout) contentView
                .findViewById(R.id.touch_bind_content_ll);
        getBindScenesMap();
        for (int i = 0; i < 4; i++) {
            final String ep = epName[i];
            Log.e("epName", ep);
            LinearLayout itemView = (LinearLayout) inflater.inflate(
                    R.layout.device_door_scenebind_item, null);
            TextView deviceNameTextView = (TextView) itemView
                    .findViewById(R.id.touch_bind_ep_name);
            deviceNameTextView.setText(DeviceUtil.epNameString(ep, mActivity));
            final TextView sceneNameTextView = (TextView) itemView
                    .findViewById(R.id.touch_bind_scene_device_name);
            String sceneName = getResources()
                    .getString(R.string.device_no_bind);
            if (bindScenesMap.containsKey(ep)) {
                SceneInfo sceneInfo = bindScenesMap.get(ep);
                if (sceneInfo != null) {
                    sceneName = sceneInfo.getName();
                }
            }
            sceneNameTextView.setText(sceneName);

            itemView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    final SceneList sceneList = new SceneList(mActivity, true);
                    sceneList
                            .setOnSceneListItemClickListener(new OnSceneListItemClickListener() {

                                @Override
                                public void onSceneListItemClicked(
                                        SceneList list, int pos, SceneInfo info) {
                                    sceneNameTextView.setText(info.getName());
                                    bindScenesMap.put(ep, info);
                                    JsonTool.uploadBindList(mActivity,
                                            bindScenesMap, bindDevicesMap,
                                            gwID, devID, type);
                                    sceneList.dismiss();
                                }
                            });
                    sceneList.show(v);
                }
            });
            contentLineLayout.addView(itemView);
        }
        ViewUtils.inject(this, contentView);
        return contentView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setting_open_lock_password = (LinearLayout) view
                .findViewById(R.id.setting_open_lock_password);
        setting_open_lock_password.setOnClickListener(settingOnClickListener);

    }

    protected void getBindScenesMap() {
        bindScenesMap = MainApplication.getApplication().bindSceneInfoMap
                .get(gwID + devID);
        if (bindScenesMap == null) {
            bindScenesMap = new HashMap<String, SceneInfo>();
        }
    }

    private OnClickListener settingOnClickListener = new OnClickListener() {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.setting_open_lock_password:
                    inputPasswordDialoge();
                    break;

                default:
                    break;
            }
        }
    };

    private void inputPasswordDialoge() {
        WLDialog.Builder builder = new WLDialog.Builder(getActivity());

        builder.setContentView(R.layout.input_admin_password)
                .setTitle(getResources().getString(R.string.OW_input_autoPW))
                .setPositiveButton(R.string.common_ok)
                .setNegativeButton(R.string.cancel)
                .setListener(new MessageListener() {
                    @Override
                    public void onClickPositive(View contentViewLayout) {
                        EditText passwordeditEditText = (EditText) contentViewLayout
                                .findViewById(R.id.admin_password_edittext);
                        editString = passwordeditEditText.getText().toString();
                        int passwordlength = editString.length();
                        if (passwordlength < 6 || passwordlength > 9) {
                            WLToast.showToast(getActivity(), getResources().getString(R.string.ow_lock_setting_password_length_is_6_9_digits), 1000);
                        } else {
                            String epdata = "2" + passwordlength + ""
                                    + editString;
                            SendMessage.sendControlDevMsg(gwID, devID, "14",
                                    "OW", epdata);
                            mDialogManager.showDialog(
                                    OW_DOOR_LOCK_PASSWORD_ENSURE, mActivity,
                                    null, null);
                            // TODO 数据来源
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

    // 接收网关返回的epdata
    public void onEventMainThread(DeviceEvent event) {
        deviceCache = DeviceCache.getInstance(getActivity());
        WulianDevice wulianDevice = deviceCache.getDeviceByID(getActivity(),
                gwID, devID);
        DeviceInfo deviceInfo = event.deviceInfo;
        if (deviceInfo.getDevEPInfo().getEpType().equals("OW")) {
            mDialogManager.dimissDialog(OW_DOOR_LOCK_PASSWORD_ENSURE, 0);
            String epData = deviceInfo.getDevEPInfo().getEpData();
            if (!StringUtil.isNullOrEmpty(epData)) {
                if (epData.equals("0220")) {
                    String checkRtc = preference.getOWRtcResult(devID);
                    if (!StringUtil.isNullOrEmpty(checkRtc)) {
                        if (checkRtc.equals("00")) {
                            Intent it = new Intent(getActivity(),
                                    NoClockChoosePwActivity.class);
                            it.putExtra(NoClockChoosePwFragment.GWID, gwID);
                            it.putExtra(NoClockChoosePwFragment.DEVICEID, devID);
                            mActivity.startActivity(it);

                        } else if (checkRtc.equals("01")) {// 带RTC
                            Intent it = new Intent(getActivity(),
                                    ChoosePasswordTypeActivity.class);
                            it.putExtra(ChoosePasswordTypeFragment.GWID, gwID);
                            it.putExtra(ChoosePasswordTypeFragment.DEVICEID, devID);
                            mActivity.startActivity(it);
                        }
                    }
                } else if (epData.equals("0810")) {
//                WLToast.showToast(getActivity(), getResources().getString(R.string.smartLock_adminPass_err), 1000);
                    inputPasswordDialoge();
                } else if (epData.equals("0801")) {
                    WLToast.showToast(getActivity(), getResources().getString(R.string.OW_pw_reset_all), 1000);
                } else if (epData.equals("0802")) {
                    WLToast.showToast(getActivity(), getResources().getString(R.string.OW_pw_unsafe_all), 1000);
                }

            }
        }
    }
}
