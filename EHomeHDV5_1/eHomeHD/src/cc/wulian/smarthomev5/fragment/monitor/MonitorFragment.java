package cc.wulian.smarthomev5.fragment.monitor;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.model.Device;
import com.wulian.icam.model.UserInfo;
import com.wulian.icam.utils.CameraSendCmdManager;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.view.device.config.DeviceIdQueryActivity;
import com.wulian.icam.view.device.play.PlayVideoActivity;
import com.wulian.iot.Config;
import com.wulian.iot.bean.IOTCameraBean;
import com.wulian.iot.view.device.play.PlayDesktopActivity;
import com.wulian.iot.view.device.play.PlayEagleActivity;
import com.wulian.iot.view.device.setting.SetEagleCameraActivity;
import com.wulian.routelibrary.controller.TaskResultListener;
import com.wulian.siplibrary.api.SipController;
import com.wulian.siplibrary.api.SipHandler;
import com.wulian.siplibrary.manage.SipProfile;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;
import com.yuantuo.netsdk.TKCamHelper;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.ihome.wan.sdk.user.entity.AMSDeviceInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.EditMonitorInfoActivity;
import cc.wulian.smarthomev5.activity.QRScanActivity;
import cc.wulian.smarthomev5.activity.iotc.config.IOTCDevConfigActivity;
import cc.wulian.smarthomev5.adapter.MonitorSetInfoAdapter;
import cc.wulian.smarthomev5.adapter.camera.CameraAdapter;
import cc.wulian.smarthomev5.adapter.camera.DeskTopCameraAdapter;
import cc.wulian.smarthomev5.adapter.camera.EagleCameraAdapter;
import cc.wulian.smarthomev5.adapter.camera.OtherCameraAdpater;
import cc.wulian.smarthomev5.adapter.camera.WLCameraAdapter;
import cc.wulian.smarthomev5.dao.CameraDao;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.entity.camera.DeskTopCameraEntity;
import cc.wulian.smarthomev5.entity.camera.MonitorWLCloudEntity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.fragment.internal.WulianFragment;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnRightMenuClickListener;
import cc.wulian.smarthomev5.tools.CameraUtil;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SendCtrlCmd;
import cc.wulian.smarthomev5.tools.UpdateCameraAPKManger;
import cc.wulian.smarthomev5.tools.UpdateCameraAPKManger.NewVersionDownloadListener;
import cc.wulian.smarthomev5.tools.UpdateCameraInfo;
import cc.wulian.smarthomev5.tools.WLCameraOperationManager;
import cc.wulian.smarthomev5.tools.WLDeskCameraOperationManager;
import cc.wulian.smarthomev5.tools.WLDeskCameraOperationManager.DeskCameraDataBackListener;
import cc.wulian.smarthomev5.tools.WLEagleOperationManager;
import cc.wulian.smarthomev5.view.DropDownListView;
import cc.wulian.smarthomev5.view.UpdateProcessDialog;
import cc.wulian.smarthomev5.view.swipemenu.RefreshListView;

public class MonitorFragment extends WulianFragment {
    private static volatile boolean isSipCreated = false;
    private UpdateCameraAPKManger updateManager;
    private UpdateProcessDialog progessDialog = null;
    private Preference preference = Preference.getPreferences();
    public static final String EXTRA_CAMERINFO = "EXTRA_CAM";
    public static final String WL_MONITOR_HTTPSPATH = "api.sh.gg";
    public static final String WL_MONITOR_HTTPPATH = "api.sh.gg";
    public static final String WL_MONITOR_DEFAULTPATH = "httpsPath";
    protected UserInfo userInfo;// 父类维护的一个用户信息引用，重新登录后，需要更新这个引用
    private MonitorSetInfoAdapter mMonitorInfoAdapterset;
    private TaskResultListener mTaskResultListener;
    private LinearLayout monitorContentLineLayout;
    private TextView uselessTextView;
    SharedPreferences sp;
    ProgressDialog progressDialog;// 单个请求时使用,一般由父类管理
    private static SipProfile account;
    private String natType;
    protected EditText url_et;
    private AreaGroupManager areaGroupManager = AreaGroupManager.getInstance();
    private CameraDao mCameraDao = CameraDao.getInstance();
    private WLDialog dialog;
    private int seq = 0;
    ArrayList<Device> deviceList;// 全局设备列表
    private int expendEditLayoutIndex = -1;
    private List<MonitorWLCloudEntity> WlCloudList;
    private MoreMenuPopupWindow manager;
    private WLCameraOperationManager mWLCameraOperationManager;
    private WLDeskCameraOperationManager wlDeskCameraOperationManager = null;
    private WLEagleOperationManager wlEagleOperationManager = null;

    // 此处为多个list和adapter合并为一个  mabo
    private RefreshListView  mCameraListView;
    private CameraAdapter adapter=null;

    // 这两个变量是为了获取Uid用
    private String devicesID = "";
    private boolean eagle = false;
    List<AMSDeviceInfo> savelist = null;
    private String gwID;
    // add syf

    /**
     * 其他类型摄像list集合
     */
    private List<CameraInfo> cameraInfoList = null;
//    private OtherCameraAdpater otherCameraAdapter = null;
//    private EagleCameraAdapter eagleCamerAdapter = null;
    private static final String TAG = "MonitorFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        ICamGlobal.APPFLAG++;
        super.onCreate(savedInstanceState);
        Log.i(TAG, "===onCreate===");
        try {
            MonitorFragment.this.initIOTSDK();
        } catch (Exception e) {
            e.printStackTrace();
            Log.e(TAG, "===(" + e.getMessage() + ")===");
            return;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(TAG, "===onStart===");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        initBar();
        return inflater.inflate(R.layout.monitor_content, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // 摄像机的listview
        mCameraListView = (RefreshListView) view.findViewById(R.id.monitor_wulian_camera_listview_dest);
        adapter=new CameraAdapter(mActivity,null);
        mCameraListView.setAdapter(adapter);
        mCameraListView.setOnItemClickListener(CameraItemListener);
        mCameraListView.setOnRefreshListener(new DropDownListView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //refresh to do something
                adapter.clearData();
                MonitorFragment.this.initDeskCanera();
                MonitorFragment.this.initEagle();
                MonitorFragment.this.initICam();
                MonitorFragment.this.initOtherCamera();
            }
        });
    }

    //add wlIcam item syf
    private List<MonitorWLCloudEntity> getWlCloudList(String json) {
        return CameraUtil.monitorWLjsonArrayToList(json);
    }

    private void createWLICamItems(String json) {
        if (json == null || json.equals("")) {
            return;
        }
        CameraSendCmdManager.setSendCmd(new SendCtrlCmd());
        fillCameraData(json);
    }

    private void fillCameraData(final String json) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "===add icam camera data===");
                if (adapter!=null){
                    adapter.setDate(getWlCloudList(json),Config.WLCLOUD_CAMERA);//随便看摄像机
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private OnItemClickListener wlICamClickListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            if (wlCameraAdapter != null) {
//                jumptoWLACameraView(wlCameraAdapter.getItem(position));
//            }
            if (adapter!=null){
                jumptoWLACameraView((MonitorWLCloudEntity) adapter.getItem(position));
            }
        }
    };

    //add wlIcam item syf
    //add otherCamea item syf
    private void initOtherCamera() {
        cameraInfoList = this.getCameraInfos();
        Log.i("cameraInfoList.size:", "========" + cameraInfoList.size());
        if (cameraInfoList.size() == 0) {
            return;
        }
        fillData(cameraInfoList);

    }

    private void fillData(final List<CameraInfo> cameraInfoList) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "===add other camera data===");
                if (adapter!=null){
                    adapter.setDate(cameraInfoList,Config.OTHER_CAMERA);//其他摄像机 类型
                }
            }
        });
    }

    //add syf
    private void initDeskCanera() {
        if (wlDeskCameraOperationManager == null) {
            wlDeskCameraOperationManager = WLDeskCameraOperationManager.getInstance(mActivity);
            wlDeskCameraOperationManager.setDeskCameraDataBackListener(deskCameraDataBackListener);
        }
        wlDeskCameraOperationManager.getDeviceInfoByGw(getAccountManger().getmCurrentInfo());
    }

    private DeskCameraDataBackListener deskCameraDataBackListener = new DeskCameraDataBackListener() {
        @Override
        public void onDeviceListBack(List<DeskTopCameraEntity> deskTopCameraEntities) {
            MonitorFragment.this.createDeskCamera(deskTopCameraEntities);
        }

        @Override
        public void onDeviceBack(IOTCameraBean iotCameraBean) {
            mWLCameraOperationManager.setChangedSceneToWulianCamera();
            jumpDeskCameraView(iotCameraBean);
        }
    };

    private void createDeskCamera(List<DeskTopCameraEntity> deskTopCameraEntities) {
        MonitorFragment.this.createDeskCameraItem();
        MonitorFragment.this.fillCameraData(deskTopCameraEntities);
    }

    private void createDeskCameraItem() {
        //这个不需要了 只需要有数据填充的就行了
//        if (deskTopCameraAdapter == null) {
//            deskTopCameraAdapter = new DeskTopCameraAdapter(mActivity, null);
//            deskCameraListView.setAdapter(deskTopCameraAdapter);
//            deskCameraListView.setOnItemClickListener(deskCameraItemListener);
//        }
    }

    private void fillCameraData(final List<DeskTopCameraEntity> deskTopCameraEntities) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "===add desk camera data(" + deskTopCameraEntities.get(0).getTutkUID() + ")===");
                if (adapter!=null){
                    adapter.setDate(deskTopCameraEntities,Config.DESK_CAMERA);//桌面摄像机
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    private OnItemClickListener CameraItemListener = new OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            position=position-mCameraListView.getHeaderViewsCount();
            int a=adapter.getItemViewType(position);
            if (a==0){
                wlDeskCameraOperationManager.checkNetwork((DeskTopCameraEntity) adapter.getItem(position));
            }
            if (a==1){
                jumptoWLACameraView((MonitorWLCloudEntity) adapter.getItem(position));
            }
            if (a==2){
                jumptoOtherCameraView((CameraInfo) adapter.getItem(position));
            }
            if (a==3){
                MonitorFragment.this.jumpEagleCameraView((AMSDeviceInfo) adapter.getItem(position));
            }
        }
    };

    private final void jumpDeskCameraView(IOTCameraBean iotCameraBean) {
        Intent mIntent = new Intent(mActivity, PlayDesktopActivity.class);
        mIntent.putExtra(Config.deskBean, iotCameraBean);
        startActivity(mIntent);
//        CustomToast.show(getActivity(),getResources().getString(R.string.data_recving),Toast.LENGTH_SHORT);
        Toast.makeText(getApplication(), getResources().getString(R.string.data_recving),
                Toast.LENGTH_LONG).show();
    }
    //add syf

    /***************************************
     * add syf eagle camera
     ********************************************************/
    private void initEagle() {
        if (Preference.getPreferences().getUserEnterType().equals("account")) {
            if (wlEagleOperationManager == null) {
                wlEagleOperationManager = WLEagleOperationManager.getInstance(getActivity());
                wlEagleOperationManager.setDataBackListener(eagleDataBackListener);
            }
            wlEagleOperationManager.getDeviceOfUser();
        }
    }

    private WLEagleOperationManager.EagleDataBackListener eagleDataBackListener = new WLEagleOperationManager.EagleDataBackListener() {
        @Override
        public void onDeviceListBack(List<AMSDeviceInfo> amsDeviceInfos) {
            Log.i(TAG, "onDeviceListBack");
            MonitorFragment.this.createEagle(amsDeviceInfos);
        }

        @Override
        public void noDevicelist() {
            if (savelist == null) {
                savelist = new ArrayList<>();
            } else {
                savelist.clear();
            }
            if (adapter!=null){
                adapter.notifyDataSetChanged();
            }
        }
    };

    /**
     * 获取鹰眼列表
     */
    private void createEagle(final List<AMSDeviceInfo> amsDeviceInfos) {
        MonitorFragment.this.createEagleItem();
        MonitorFragment.this.fillEagleData(amsDeviceInfos);
    }

    private void createEagleItem() {
            SetEagleCameraActivity.setUpdateCameraName(new UpdateCameraInfo());//为了修改猫眼设备名
//        }

    }

    private void fillEagleData(final List<AMSDeviceInfo> amsDeviceInfos) {
        mActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.e(TAG, "===add eagle camera data===");
                adapter.setDate(amsDeviceInfos,Config.EAGLE_CAMERA);
                savelist = amsDeviceInfos;

            }
        });
        wlEagleOperationManager.findMainUserByAMS(amsDeviceInfos);//查处主要用户注册推送
    }

    private void jumpEagleCameraView(AMSDeviceInfo obj) {
        Intent mIntent = new Intent(mActivity, PlayEagleActivity.class);
        Log.i(TAG, obj.getDeviceId());
        mIntent.putExtra(Config.tutkUid, obj.getDeviceId());
        mIntent.putExtra(Config.tutkPwd, obj.getPassword());
        mIntent.putExtra(Config.isAdmin, obj.getIsAdmin());
        mIntent.putExtra(Config.eagleName, obj.getDeviceName());
        startActivity(mIntent);
    }

    /***************************************
     * add syf eagle camera
     ********************************************************/
    private void initIOTSDK() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                TKCamHelper.init();
                return null;
            }
        }.execute();
    }

    private void uninitIOTSDK() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... params) {
                TKCamHelper.uninit();
                return null;
            }
        }.execute();
    }

    private void destroyManagerInstance() {
        if (wlEagleOperationManager != null) {
            wlEagleOperationManager.destoryInstance();
        }
        if (wlDeskCameraOperationManager != null) {
            wlDeskCameraOperationManager.destoryInstance();
        }
    }

    /**
     * 获取其他摄像机列表
     *
     * @return
     */
    private List<CameraInfo> getCameraInfos() {
        CameraInfo camerainfo = new CameraInfo();
        camerainfo.setGwId(mAccountManger.getmCurrentInfo().getGwID());
        return mCameraDao.findListAll(camerainfo);
    }

    private void initICam() {//modifi syf
        String spJson = preference.getMonitorList();
        String gwId = preference.getMonitorListgwID();
        gwID = mAccountManger.getmCurrentInfo().getGwID();
        //思路：每次在onresume中都先加载对应网关中sp中的摄像机列表，然后根据爱看平台查询到的数据去更新
        if (gwId.equals(gwID)) {
            if (!StringUtil.isNullOrEmpty(spJson)&&!("0".equals(spJson))) {
                MonitorFragment.this.createWLICamItems(preference.getMonitorList());
            }
        }
        if (mWLCameraOperationManager == null) {
            mWLCameraOperationManager = WLCameraOperationManager.getInstance();
            mWLCameraOperationManager.refreshUserInfoIfGatewayChanged();
            mWLCameraOperationManager.setDataBackListener(wlCameraDataHandler);
        }
        mWLCameraOperationManager.getDeviceList();
//        if(!isServiceRunning(getActivity(),"cc.wulian.smarthomev5.service.HeartBeatService")){
//            getActivity().startService(new Intent(getActivity(), HeartBeatService.class));
//        }
//        heatBeat();
        account = mWLCameraOperationManager.getSipProfile();
    }

    public static boolean isServiceRunning(Context mContext,String className) {
        boolean isRunning = false;
        ActivityManager activityManager = (ActivityManager)
                mContext.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> serviceList
                = activityManager.getRunningServices(30);
        if (!(serviceList.size()>0)) {
            return false;
        }
        for (int i=0; i<serviceList.size(); i++) {
            if (serviceList.get(i).service.getClassName().equals(className) == true) {
                isRunning = true;
                break;
            }
        }
        return isRunning;
    }

    public void heatBeat() {
        if(userInfo!=null){
            String sip_ok = "sip:" + userInfo.getUsername() + "@"
                    + userInfo.getSdomain();
            SipController.getInstance().sendMessage(sip_ok.replace("sip:", ""), SipHandler.QueryAppDeviceHeatbeat(sip_ok, seq++, "10"), account);
        }
    }

    private WLCameraOperationManager.DataBackListener wlCameraDataHandler = new WLCameraOperationManager.DataBackListener() {
        @Override
        public void onUserLogin() {
            Log.i("Wulian iCam:", "UserLogin success");
        }

        @Override
        public void onDeviceListBack(String json) {
            Log.e("onDeviceListBack", json);
            String spJson = preference.getMonitorList();
            if (json.equals(spJson)){
                return;
            }
            preference.saveMonitorList(json, gwID);
            MonitorFragment.this.createWLICamItems(json);
        }

        @Override
        public void onDeviceDeleted(String json) {
            mWLCameraOperationManager.getDeviceList();
        }
    };

    protected void jumptoWLACameraView(final MonitorWLCloudEntity mWlCameraEntity) {
        System.out.println("------>jumptoWLACameraView");
        if (mWlCameraEntity.getMonitorIsOnline().equals("1")) {
            account = mWLCameraOperationManager.getSipProfile();
            int lastCode = SipController.getInstance().getAccountInfo(account);
            if (lastCode == 200) {
                mWLCameraOperationManager.setChangedSceneToWulianCamera();
                Intent it = new Intent();
                // TODO 桌面摄像机返回参数于不同 需要再次修改判断
                it.putExtra("device", mWlCameraEntity.getDevice());
                it.setClass(mActivity, PlayVideoActivity.class);
                startActivity(it);
                return;
            }
            Utils.sysoInfo("lastCode:" + lastCode);
            String errMsg = "";
            switch (lastCode) {
                case 100:
                case 401:
                case 407:
                    errMsg = getString(R.string.home_monitor_showview_loading);
                    break;
                case 408:
                    errMsg = getString(R.string.home_monitor_showview_timedout_error);
                    break;
                default:
                    if (lastCode > 500) {
                        errMsg = getString(R.string.home_monitor_wl_for_showview_error_server_exception);
                    } else if (lastCode < 0) {
                        errMsg = getString(R.string.home_monitor_showview_logining);
                    } else {// 超大范围
                        errMsg = "account_info:" + lastCode;
                    }
                    break;
            }
            WLToast.showToast(mActivity, errMsg, WLToast.TOAST_SHORT);
            account = mWLCameraOperationManager.getSipProfileForce();
        } else {
            WLToast.showToast(mActivity, mActivity.getResources().getString(R.string.device_offline),
                    WLToast.TOAST_LONG);
        }
    }

    public static SipProfile getAccount() {
        return account;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(adapter!=null){
            adapter.clearData();
        }
        Log.e(TAG, "===onResume===");
        MonitorFragment.this.initDeskCanera();
        MonitorFragment.this.initEagle();
        MonitorFragment.this.initICam();
        MonitorFragment.this.initOtherCamera();
    }

    // 执行完加号后的执行函数。
    // add mabo
    private void initBar() {
        mActivity.resetActionMenu();
        getSupportActionBar().setDisplayShowMenuEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(mActivity.getResources().getString(R.string.device_alarm_monitor));
        getSupportActionBar().setIconText(R.string.nav_home_title);
        getSupportActionBar().setRightIcon(R.drawable.common_use_add);
        getSupportActionBar().setRightMenuClickListener(new OnRightMenuClickListener() {
            @Override
            public void onClick(View v) {
                // 编辑摄像机的监听
                // showAddMonitorPopWindow(mActivity, uselessTextView);
                Intent it = new Intent(getActivity(), QRScanActivity.class);
                it.putExtra("wulianScan", "wulianScan");
                startActivityForResult(it, 2);
            }
        });
        /**
         * 相册功能
         */
    }

    // 需要在activity中重写此方法才能生效。。。
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        String uid;
        try {
            uid = data.getStringExtra(EditMonitorInfoFragment.RESULT_UID);
        } catch (Exception e) {
            return;
        }
        if (uid.length() == 16 || uid.length() == 14) {
            if (uid.startsWith("ZHJ") || uid.startsWith("OBJ")) {
                if (uid.length() == 14) {
                    String newUid = uid.substring(0, 3) + "-" + uid.substring(3, 9) + "-" + uid.substring(9, uid.length());
                    uid = newUid;
                }
                Resources resources = this.getResources();
                CameraInfo info = new CameraInfo();
                info.setCamId(-1);
                info.setCamType(CameraInfo.CAMERA_TYPE_CLOUD_2);
                info.setUid(uid);
                info.setCamName(resources.getString(R.string.monitor_cloud_two_video_camera));
                Bundle bundle = new Bundle();
                bundle.putSerializable(EditMonitorInfoFragment.CAMERA_INFO, info);
                Intent it = new Intent(getActivity(), EditMonitorInfoActivity.class);
                it.putExtras(bundle);
                startActivity(it);
                return;
            }
            devicesID = "cmic" + uid;
        } else if (uid.length() == 20) {
            if (uid.substring(4, 6).equals("06")) {
                Toast.makeText(getActivity(), R.string.config_error_deviceid, Toast.LENGTH_SHORT).show();
                return;
            }
            devicesID = uid;
        } else if (uid.length() == 26) {
            devicesID = uid;
            eagle = true;// 判断 是否是摄像机
        } else {
            WLToast.showToast(getActivity(),
                    getActivity().getResources().getString(R.string.home_monitor_result_unknow_id), WLToast.TOAST_SHORT);
        }

        Intent mIntent = new Intent();
        if (eagle) {
            mIntent.setClass(getActivity(), IOTCDevConfigActivity.class);
        } else {
            mIntent.setClass(getActivity(), DeviceIdQueryActivity.class);
            mIntent.putExtra("isAddDevice", true);
        }
        mIntent.putExtra("msgData", devicesID);
        getActivity().startActivity(mIntent);
    }

    private void jumptoOtherCameraView(CameraInfo info) {
        updateManager = UpdateCameraAPKManger.getInstance(mActivity);
        updateManager.setSeverAppInfo();
        updateManager.hasSmartHomeMonitorApk();
        if (!updateManager.isIcamAppInstalled()) {
            checkForNewVersion(info);
        } else {
            if (!updateManager.needUpdateOrDownload()) {
                updateManager.jumpToSmartHomeMonitorApplication(info);
            } else {
                checkForNewVersion(info);
            }
        }
    }

    protected void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
//		if (savelist.isEmpty())
        CameraUtil.saveEagleUidList(savelist);
        Log.e(TAG, "onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        MonitorFragment.this.destroyManagerInstance();
        Log.e(TAG, "onDestroyView");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy");
        MonitorFragment.this.uninitIOTSDK();
    }

    // 删除其他摄像机 从本地数据中删除
    private void deleteMonitorInfo(Context context, final CameraInfo info) {
        WLDialog.Builder builder = new Builder(context);
        builder.setTitle(R.string.device_ir_delete).setContentView(R.layout.fragment_message_select_delete)
                .setPositiveButton(android.R.string.ok).setNegativeButton(android.R.string.cancel)
                .setListener(new MessageListener() {
                    @Override
                    public void onClickPositive(View contentViewLayout) {
                        if (info != null) {
                            mCameraDao.delete(info);
                            // TODO 删除摄像机
                            dialog.dismiss();
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

    /**
     * 检查更新
     */
    private void checkForNewVersion(CameraInfo info) {
        showDownloadOrUpdateProgress();
        updateManager.checkUpdate(info);
    }

    /**
     * @throws
     * @Title: showChangeVersionUpdateDialog
     * @Description: TODO(显示dialog，让用户选择是否下载最新的apk)
     */

    private void showDownloadOrUpdateProgress() {
        Preference.getPreferences().putInt(IPreferenceKey.P_CAMERA_APK_DOWNLOAD_COMPLETE, 0);
        updateManager.setNewVersionDownloadListener(new NewVersionDownloadListener() {

            @Override
            public void processing(int present) {
                if (progessDialog == null) {
                    progessDialog = new UpdateProcessDialog(mActivity);
                    progessDialog.show();
                }
                progessDialog.setProgess(present);
                if (present >= 100) {
                    Preference.getPreferences().putInt(IPreferenceKey.P_CAMERA_APK_DOWNLOAD_COMPLETE, 100);
                    progessDialog.dismiss();
                    progessDialog = null;
                    updateManager.startInstall();
                }
            }

            @Override
            public void processError(Exception e) {
                WLToast.showToast(mActivity, getString(R.string.set_version_update_erro), WLToast.TOAST_SHORT);
                if (progessDialog != null) {
                    progessDialog.dismiss();
                    progessDialog = null;
                }
            }
        });
    }
}
