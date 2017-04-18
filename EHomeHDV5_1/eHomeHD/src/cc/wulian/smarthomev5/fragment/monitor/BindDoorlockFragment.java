package cc.wulian.smarthomev5.fragment.monitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lidroid.xutils.ViewUtils;
import com.wulian.icam.view.widget.CustomToast;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.controlable.doorlock.AbstractDoorLock;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.BindDoorLockAdapter;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat;
import cc.wulian.smarthomev5.tools.BindDoorLockManager;
import cc.wulian.smarthomev5.tools.SendMessage;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.MODE_WORLD_READABLE;

/**
 * function：70门锁绑定
 * Created by hxc on 2016/11/16.
 */

public class BindDoorlockFragment extends WulianFragment implements View.OnClickListener {
    private RelativeLayout rl_bind_tips_del;
    private ImageView iv_bind_tips_del;
    private ListView lv_doodlock_bind;
    private List<WulianDevice> list;
    private BindDoorLockAdapter bindDoorLockAdapter;
    private String password;
    private WLDialog dialog;
    private String gwID;
    private String epData;
    private String epType;
    private String devID;
    private String deviceName;
    private static DeviceCache deviceCache;
    private static final String VERTIFYPASSWORD = "VERTIFYPASSWORD";
    private static final String TAG = "tag";
    private int postion;
    private com.wulian.icam.model.Device device;
    private String cameraId;
    private boolean isSuccess = false;
    private BindDoorLockManager bindDoorLockManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.activity_binddoorlock,
                container, false);
        ViewUtils.inject(this, rootView);
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        bindDoorLockManager = BindDoorLockManager.getInstance(mActivity);
        initData();
        initView();
        setListener();
        initBar();
        list = new ArrayList<>();
        for (WulianDevice device : DeviceCache.getInstance(getActivity()).getAllDevice()) {
            if (device instanceof AbstractDoorLock && (device.getDeviceType().equals("70")
                    ||device.getDeviceType().equals("69")
                    ||device.getDeviceType().equals("OW"))) {
                list.add(device);
            }
        }
        if (list.size() == 0) {
            WLToast.showToast(getActivity(), getResources().getString(R.string.camera_not_detected), 1000);
        }
//        checkDoorlockNameChanged();

        bindDoorLockAdapter = new BindDoorLockAdapter(getActivity(), list, device, gwID);
        lv_doodlock_bind.setAdapter(bindDoorLockAdapter);
    }

    private void initData() {
        device = (com.wulian.icam.model.Device) getActivity().getIntent().getSerializableExtra("device");
        cameraId = device.getDevice_id().substring(8);
    }

    private void initView() {
        rl_bind_tips_del = (RelativeLayout) getView().findViewById(com.wulian.icam.R.id.doorlock_bind_tips);
        iv_bind_tips_del = (ImageView) getView().findViewById(com.wulian.icam.R.id.tips_del);
        lv_doodlock_bind = (ListView) getActivity().findViewById(R.id.lv_doorlock_bind);
    }

    private void setListener() {
        rl_bind_tips_del.setOnClickListener(this);
        iv_bind_tips_del.setOnClickListener(this);
        lv_doodlock_bind.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                gwID = list.get(i).getDeviceGwID();
                devID = list.get(i).getDeviceID();
                epType = list.get(i).getDeviceType();
                deviceName = list.get(i).getDeviceName();
                if (deviceName.equals("")) {
                    deviceName = bindDoorLockManager.getDefaultDevName(epType);
                }
                postion = i;
                if (list.get(i).isDeviceOnLine()) {
                    saveDoorLockInfoToSp(mActivity, gwID, devID, deviceName, cameraId);
                    Log.i("camId",cameraId);
                    System.out.println("------>camera---"+cameraId);
                    CustomToast.show(getActivity(), getResources().getString(R.string.camera_settings_bind_successfully), 1000);
                    getActivity().finish();
                } else {
                    WLToast.showToast(mActivity, getResources().getString(com.wulian.icam.R.string.main_offline), 1000);
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
                getResources().getString(R.string.camera_settings_door_lock_binding));
        getSupportActionBar().setLeftIconClickListener(
                new ActionBarCompat.OnLeftIconClickListener() {

                    @Override
                    public void onClick(View v) {
                        mActivity.finish();
                    }
                });
    }

    //该方法是查询被绑定的门锁是否改名了，改的话需要在设置界面显示最新的名字
//    private void checkDoorlockNameChanged() {
//        SharedPreferences gwIdSp = getActivity().getSharedPreferences("doorLockGwId", MODE_PRIVATE);
//        String loginGwId = gwIdSp.getString("gwId", "");
//        SharedPreferences bindSp = getActivity().getSharedPreferences(loginGwId + device.getDevice_id().substring(8), MODE_PRIVATE);
//        cameraId = bindSp.getString("cameraId", "");
//        for (int i = 0; i < list.size(); i++) {
//            if (!StringUtil.isNullOrEmpty(cameraId)) {
//                bindSp.edit().putString("devName", list.get(i).getDeviceName()).commit();
//            }
//        }
//    }

//    private void inputPasswordDialoge() {
//        WLDialog.Builder builder = new WLDialog.Builder(getActivity());
//        builder.setContentView(cc.wulian.smarthomev5.R.layout.input_admin_password)
//                .setTitle(getResources().getString(R.string.device_mini_geteway_prompt))
//                .setPositiveButton(cc.wulian.smarthomev5.R.string.common_ok)
//                .setNegativeButton(cc.wulian.smarthomev5.R.string.cancel)
//                .setListener(new WLDialog.MessageListener() {
//                    @Override
//                    public void onClickPositive(View contentViewLayout) {
//                        EditText passwordEditText = (EditText) contentViewLayout
//                                .findViewById(cc.wulian.smarthomev5.R.id.admin_password_edittext);
//                        password = passwordEditText.getText().toString();
//                        int passwordLength = password.length();
//                        if (passwordLength == 0) {
//                            CustomToast.show(getActivity(), getResources().getString(R.string.set_password_not_null_hint), 1000);
//                        } else {
//                            bindDoorLockManager.verifyAdminPassword(gwID,devID,epType,password);
////                            SendMessage.sendControlDevMsg(gwID, devID, "14",
////                                    "70", 9 + "" + passwordLength + password);
//                            mDialogManager.showDialog(
//                                    VERTIFYPASSWORD, mActivity,
//                                    null, null);
//                        }
//                    }
//
//                    @Override
//                    public void onClickNegative(View contentViewLayout) {
//                        dialog.dismiss();
//                    }
//
//                });
//        dialog = builder.create();
//        dialog.show();
//    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.tips_del) {
            rl_bind_tips_del.setVisibility(View.GONE);
        }
    }

    //保存绑定门锁信息到sharepreference，通过gwID+cameraId的保证唯一性
    private void saveDoorLockInfoToSp(Context context, String gwID, String devID, String devName, String cameraId) {
        SharedPreferences sp = context.getSharedPreferences(gwID + cameraId, MODE_PRIVATE);
        Log.i("------1",gwID+"--"+cameraId);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.putString("gwID", gwID);
        editor.putString("devId", devID);
        editor.putString("devName", devName);
        editor.putString("cameraId", cameraId);
        editor.putString("epType",epType);
        editor.commit();
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                View view = lv_doodlock_bind.getChildAt(postion);
                TextView tv_doorlock_unbunding = (TextView) view.findViewById(R.id.tv_doorlock_unbunding);
                tv_doorlock_unbunding.setVisibility(View.VISIBLE);
                getActivity().finish();
            }
        }
    };

    @Override
    public void onResume() {
        super.onResume();
//        lv_doodlock_bind.setAdapter(bindDoorLockAdapter);
    }

//    public void onEventMainThread(DeviceEvent event) {
//        mDialogManager.dimissDialog(VERTIFYPASSWORD, 0);
//        deviceCache = DeviceCache.getInstance(getActivity());
//        WulianDevice wulianDevice = deviceCache.getDeviceByID(getActivity(),
//                gwID, devID);
//        DeviceInfo deviceInfo = event.deviceInfo;
//        epData = deviceInfo.getDevEPInfo().getEpData();
//        isSuccess = bindDoorLockManager.checkBindResult(epData,epType);
//        if(epType.equals("69")||epType.equals("70")||epType.equals("OW")){
//
//            if(isSuccess){
//                CustomToast.show(getActivity(), getResources().getString(R.string.camera_settings_bind_successfully), 1000);
//                saveDoorLockInfoToSp(mActivity, gwID, devID, deviceName, cameraId);
//                getActivity().finish();
//            }else {
//                CustomToast.show(getActivity(), getResources().getString(R.string.home_password_error), 1000);
//            }
//        }
//    }
}
