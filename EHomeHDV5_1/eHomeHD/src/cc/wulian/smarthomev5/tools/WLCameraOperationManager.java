package cc.wulian.smarthomev5.tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.icam.ICamGlobal;
import com.wulian.icam.common.APPConfig;
import com.wulian.icam.common.iCamConstants;
import com.wulian.icam.model.CheckBind;
import com.wulian.icam.model.ConfigWiFiInfoModel;
import com.wulian.icam.model.Device;
import com.wulian.icam.model.Scene;
import com.wulian.icam.model.Scene.OnSelectionLisenter;
import com.wulian.icam.model.Scene.SData;
import com.wulian.icam.model.UserInfo;
import com.wulian.icam.utils.DeviceType;
import com.wulian.icam.utils.Utils;
import com.wulian.icam.utils.WifiAdmin;
import com.wulian.icam.view.device.config.DeviceGetReadyGuideActivity;
import com.wulian.icam.view.device.config.DeviceIdQueryActivity;
import com.wulian.icam.view.device.setting.WifiSettingActivity;
import com.wulian.icam.view.widget.CustomToast;
import com.wulian.routelibrary.ConfigLibrary;
import com.wulian.routelibrary.common.ErrorCode;
import com.wulian.routelibrary.common.RouteApiType;
import com.wulian.routelibrary.common.RouteLibraryParams;
import com.wulian.routelibrary.controller.RouteLibraryController;
import com.wulian.routelibrary.controller.TaskResultListener;
import com.wulian.siplibrary.manage.SipProfile;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentLinkedQueue;

import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.dao.SceneDao;
import cc.wulian.smarthomev5.entity.camera.MonitorWLCloudEntity;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.ConstantsUtil;
import cc.wulian.smarthomev5.utils.DateUtil;
import cc.wulian.smarthomev5.utils.TargetConfigure;
import cc.wulian.smarthomev5.utils.URLConstants;

public class WLCameraOperationManager {

    public static final int AIKAN_PASSWORD_ENDPOS = 16;

    private static final String ICAM_SERVER_HOST = "ICAM_SERVER_HOST";
    private static final String TAG = "WLCameraOperation";
    public static final String AIKAN_TOKEN = "j4fQ1YVpYG4S4x2ENQ9s8MVe5L9rhEDa";
    public static final String USER_AGENT = "fcsr";
    private SceneDao sceneDao = SceneDao.getInstance();
    protected MainApplication mApplication = MainApplication.getApplication();
    public static ProgressDialogManager mDialogManager = ProgressDialogManager
            .getDialogManager();
    private static final String LOGIN_WAITING = "LOGIN_WAITING";
    private static WLCameraOperationManager instance = null;
    protected AccountManager mAccountManger = AccountManager.getAccountManger();
    protected UserInfo userInfo;// 父类维护的一个用户信息引用，重新登录后，需要更新这个引用
    private SharedPreferences sp;
    private ProgressDialog progressDialog;// 单个请求时使用,一般由父类管理
    private ICamGlobal app;// 子类可覆盖，单例无所谓。
    private String WLCameraLoginUid;
    private String WLCameraLoginPwd;
    private static boolean isWLCameraGateway = false;// 用在首次配网后，登录网关自动绑定摄像机
    private static boolean isCheckingBindingWLCamera = false;
    private WifiAdmin wifiAdmin;
    private static String add_device_id;
    private boolean initialized = false;
    private boolean initializing = false;
    private String wlCameraJson;
    private long lastLoginRequestTime = 0;
    int seq = 0;
    private WLDialog dialog;
    private static final long SHOULD_NOT_REDO_LOGIN_IN_TIME = 2000;

    private static SipProfile account;

    public String getWlCameraJson() {
        return wlCameraJson;
    }

    public SipProfile getSipProfile() {
        if (account == null) {
            account = app.registerAccountForce();
        }
        return account;
    }

    public SipProfile getSipProfileForce() {
        account = app.registerAccountForce();
        return account;
    }

    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    private OnSelectionLisenter sceneSelectionListener = new OnSelectionLisenter() {
        @Override
        public boolean onSeleted(int idx, String sceneID, final String status) {
            try {
                if (StringUtil.equals("2", status)) {
                    SendMessage.sendSetSceneMsg(
                            WLCameraOperationManager.this.mApplication,
                            mAccountManger.getmCurrentInfo().getGwID(),
                            CmdUtil.MODE_SWITCH, sceneID, null, null, "1",
                            false);
                } else {
                    SendMessage.sendSetSceneMsg(
                            WLCameraOperationManager.this.mApplication,
                            mAccountManger.getmCurrentInfo().getGwID(),
                            CmdUtil.MODE_SWITCH, sceneID, null, null, "2",
                            false);
                }
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    };

    private WLCameraOperationManager() {
        this.mApplication = MainApplication.getApplication();
    }

    public static WLCameraOperationManager getInstance() {
        if (instance == null) {
            synchronized (WLCameraOperationManager.class) {
                if (instance == null) {
                    instance = new WLCameraOperationManager();
                }
            }
        }
        if (instance.initialized == false && instance.initializing == false) {
            instance.initData();
        }
        return instance;
    }

//	public static WLCameraOperationManager getInstance() {
//		return instance;
//	}

    public static void destoryInstance() {
        if (instance != null) {
            instance.finalize();
            instance = null;
        }
    }

    public TaskResultListener getTaskResultListener() {
        return iCamAPIHander;
    }

    public void initData() {
        try {
            initializing = true;
            sp = mApplication.getSharedPreferences(APPConfig.SP_CONFIG, 0);// 不可以一开始就初始化，至少在onCreate里
            app = ICamGlobal.getInstance();
            app.initForV5(mApplication);
            app.initSip();
            updateServerAddress();
            ConfigLibrary.setFirstParamerConfig(mApplication, USER_AGENT);
            account = app.registerAccount();

            if (mAccountManger.getmCurrentInfo() == null
                    || mAccountManger.getmCurrentInfo().getGwID() == null
                    || mAccountManger.getmCurrentInfo().getGwPwd() == null
                    || mAccountManger.getmCurrentInfo().getGwPwd().length() != ConstantsUtil.GATEWAY_MD5_PASSWDLENGTH) {
                return;
            }
            WLCameraLoginUid = mAccountManger.getmCurrentInfo().getGwID();
            WLCameraLoginPwd = mAccountManger.getmCurrentInfo().getGwPwd()
                    .substring(0, AIKAN_PASSWORD_ENDPOS);
            this.reLogin();
        } catch (Exception e) {
            Log.e("iCam", "WLCameraOperator init failed", e);
        }
    }

    private void updateServerAddress() {
        RouteLibraryController
                .setWulianAESLibraryPath(URLConstants.DEFAULT_ACS_BASEURL);

        String serverHost = sp.getString(ICAM_SERVER_HOST, "");
//        String serverHost = "test.sh.gg";
        if (StringUtil.isNullOrEmpty(serverHost) == false) {
            RouteLibraryController.setLibraryPath(serverHost);
        }
        sendRequest(RouteApiType.V3_SERVER, null);
    }

    private void updateUIDandPwdAndRelogin(String uid, String pwd) {
        this.uninitialize();
        WLCameraLoginUid = uid;
        WLCameraLoginPwd = pwd;
        app.initSip();
        account = app.registerAccount();
        if (account == null) {
            account = app.registerAccountForce();
        }
        this.reLogin();

    }

    private void onServerHostReturn(String json) {
        try {
            JSONObject jsonObj = new JSONObject(json);
            String serverHost = jsonObj.getString("server");
            if (StringUtil.isNullOrEmpty(serverHost) == false) {
                Editor editor = sp.edit();
                editor.putString(ICAM_SERVER_HOST, serverHost);
                editor.commit();
                RouteLibraryController.setLibraryPath(serverHost);
            }
        } catch (JSONException e) {
            Log.e(TAG, "should not happen", e);
        }
    }

    private TaskResultListener iCamAPIHander = new TaskResultListener() {
        public void OnSuccess(RouteApiType apiType, String json) {
            Log.i("datareturn", json);
            Utils.sysoInfo("base onsuccess " + apiType);
            try {

                JSONObject dataJson = new JSONObject(json);
                if (apiType == RouteApiType.USER_LOGIN_WLAIKAN) {
                    dataJson = dataJson.getJSONObject("retData");
                    json = dataJson.toString();
                    Log.i("sip", json);
                }
                int status = dataJson.optInt("status");
                if (status == 1) {
                    DataReturn(true, apiType, json);// 第3个参数为需要的json数据
                } else {
                    DataReturn(false, apiType, json);
                }
            } catch (JSONException e) {
                try {
                    DataReturn(false, apiType, json);
                } catch (JSONException e1) {
                    Log.e(TAG, "", e1);
                }
            }
        }

        @Override
        public void OnFail(RouteApiType arg0, ErrorCode arg1) {
        }

    };

    protected void finalize() {
        this.uninitialize();
    }

    public void refreshUserInfoIfGatewayChanged() {
        try {
            if (mAccountManger.getmCurrentInfo() == null) {
                return;
            }
            String gwID = mAccountManger.getmCurrentInfo().getGwID();
            String gwPasswd = mAccountManger.getmCurrentInfo().getGwPwd();
            if (StringUtil.isNullOrEmpty(gwID)
                    || StringUtil.isNullOrEmpty(gwPasswd)) {
                return;
            }
            String newPassword = gwPasswd.substring(0, AIKAN_PASSWORD_ENDPOS);
            if (gwID.equals(WLCameraLoginUid) && newPassword.equals(WLCameraLoginPwd)) {
                return;
            }
            updateUIDandPwdAndRelogin(gwID, newPassword);
        } catch (Exception e) {
            Log.e("iCam", "WLCameraOperator init failed", e);
        }

    }

    private void uninitialize() {
        if (initialized) {
            if (app != null) {
                app.hangupAllCall();
                app.unRegisterAccount();
                app.setUserinfo(new UserInfo());
                app.destorySip();
            }
            this.userInfo = null;
            this.initializing = false;
            this.tasksAfterLogin.clear();
            this.wlCameraJson = "";
            this.WLCameraLoginUid = null;
            this.WLCameraLoginPwd = null;
            initialized = false;
        }
    }

    private ConcurrentLinkedQueue<Runnable> tasksAfterLogin = new ConcurrentLinkedQueue<Runnable>();

    /**
     * @param type   类型
     * @param params 请求参数
     * @MethodName sendRequest
     * @Function 发送网络请求
     * @author Puml
     * @date: 2014-9-9
     * @email puml@wuliangroup.cn
     */
    private void sendRequest(RouteApiType type, HashMap<String, String> params) {
        RouteLibraryController.getInstance().doRequest(mApplication, type, params,
                iCamAPIHander);
    }

    private void sendRequestAndAutoLogin(final RouteApiType type,
                                         final HashMap<String, String> params) {
        // 检查是否超时
        runTaskAndAutoLogin(new Runnable() {
            @Override
            public void run() {
                params.put("auth", WLCameraOperationManager.this.getAuth());// 更新为最新的authCode
                sendRequest(type, params);
            }
        });
    }

    private void runTaskAndAutoLogin(final Runnable task) {
        // 检查是否超时
        if (doNotNeedRelogin()) {
            TaskExecutor.getInstance().execute(task);
        } else {// 已经超时,重新登录
            tasksAfterLogin.offer(task);
            login();
        }
    }

    private String getAuth() {
        if (this.userInfo == null) {
            return "";
        } else {
            return this.userInfo.getAuth();
        }
    }

    private boolean doNotNeedRelogin() {
        // 本地未超时，服务器可能已经超时，所以要提前120秒
        return userInfo != null
                && System.currentTimeMillis() < userInfo.getExpires() * 1000L;
    }

    public void getDeviceList() {
        sendRequestAndAutoLogin(RouteApiType.V3_USER_DEVICES,
                RouteLibraryParams.V3UserDevices(getAuth(), "cmic",1,100));
        Log.i("Auth", "v3_user_device=====" + getAuth());
    }

    public void checkBindingForTest(String deviceId) {
        add_device_id = deviceId;
        checkBinding(add_device_id);
    }

    private void checkBinding(String deviceId) {
        isCheckingBindingWLCamera = true;
        sendRequestAndAutoLogin(RouteApiType.V3_BIND_CHECK,
                RouteLibraryParams.V3BindCheck(deviceId, getAuth()));
    }

    public void delateDeviceFormList(MonitorWLCloudEntity mWlCameraEntity) {
        if (mWlCameraEntity.getMonitorIsBindDevice()) {// 解除绑定设备
            sendRequestAndAutoLogin(
                    RouteApiType.V3_BIND_UNBIND,
                    RouteLibraryParams.V3BindUnbind(getAuth(),
                            mWlCameraEntity.getMonitorDeviceId()));
            // } else {// 解除授权设备
            // sendRequestAndAutoLogin(RouteApiType.BINDING_AUTH_DELTE,
            // RouteLibraryParams.BindingAuthDelete(
            // mWlCameraEntity.getMonitorDeviceId(), getAuth(),
            // userInfo.getUsername()));
        }
    }

    /**
     * @Function 重新登录提取到父类公用
     * @author Wangjj
     * @date 2014年11月24日
     */
    public boolean reLogin() {
        if (doNotNeedRelogin()) {
            return false;
        }
        login();

        return true;
    }

    private void login() {
        if (DateUtil.now() - lastLoginRequestTime < SHOULD_NOT_REDO_LOGIN_IN_TIME) {
            if (TargetConfigure.LOG_LEVEL <= Log.INFO) {
                Log.i(TAG, "Should not login to frequently.");
            }
            return;
        }
        lastLoginRequestTime = DateUtil.now();
        if (TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
            Log.d(TAG, "login by " + WLCameraLoginUid + "/" + WLCameraLoginPwd);
        }
        try {
            sendRequest(RouteApiType.V3_SMARTROOM_LOGIN, RouteLibraryParams.V3SmartRoomLogin("sr-" + WLCameraLoginUid, WLCameraLoginPwd, AIKAN_TOKEN));
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    public void changePassword(String newPassword) {
        sendRequestAndAutoLogin(RouteApiType.V3_SMARTROOM_PASSWORD, RouteLibraryParams.V3SmartroomPassword("", newPassword));
    }

//    public void wakeNewEagle() {
//        sendRequest(RouteApiType.V3_APP_BELL, RouteLibraryParams.V3AppBell(getAuth(),
//                "CMIC0801442C05792F46", "awake"));
//        Log.i("Auth", "v3_APP_BELL------>" + getAuth()+"devId ="+WLCameraLoginUid);
//    }

    protected void DataReturn(boolean success, RouteApiType apiType, String json)
            throws JSONException {
        dismissProgressDialog();
        mDialogManager.dimissDialog(LOGIN_WAITING, 0);
        if (success) {
            switch (apiType) {
                case USER_LOGIN_WLAIKAN:
                    onLoginSuccess(json);
                    Log.i("json", json);
                case V3_SMARTROOM_LOGIN: {// 重新登录成功，获取了新的授权码
                    onLoginSuccess(json);
                    Log.i("success", json);
                    break;
                }
                case V3_SERVER: {
                    onServerHostReturn(json);

                    break;
                }
                case V3_APP_FLAG:
                    break;
                case V3_BIND_UNBIND:

                case V3_USER_DEVICES:
                    wlCameraJson = json;
                    if (this.dataBackListener != null) {
                        this.dataBackListener.onDeviceListBack(json);
                        Log.i("USER_DEVICES", json);
                    }
                    break;
                case V3_BIND_CHECK:
                    CheckBind cb = Utils.parseBean(CheckBind.class, json);
                    if (isCheckingBindingWLCamera) {
                        isCheckingBindingWLCamera = false;
                        this.promptBindingInfo(cb);
                    }
                    break;

                case V3_APP_BELL:
                    CustomToast.show(mApplication,"唤醒成功",1000);
                    Log.i("V3_APP_BELL------>", json);
                    break;
                default:
                    break;
            }
        } else {
            Log.e(TAG, "Call iCam service error:" + apiType.name() + json);
            switch (apiType) {
                case USER_LOGIN_WLAIKAN:
                    onLoginError(json);
                    break;
                case V3_SMARTROOM_LOGIN:
                    loginByACS();
                    break;
                default:
                    break;
            }
        }
    }

    private void loginByACS() {
        sendRequest(RouteApiType.USER_LOGIN_WLAIKAN, RouteLibraryParams.UserWulianAiKanHalfMD5(WLCameraLoginUid,
                WLCameraLoginPwd));

        System.out.println("------>pwd+" + WLCameraLoginPwd);
    }

    private void onLoginError(String json) {
        JSONObject retobj = null;
        try {
            retobj = new JSONObject(json);
            String errorcode = retobj.getString("error_code");
            if (errorcode.equals("1125") || (errorcode.equals("401"))) {
                if (isWLCameraGateway && mAccountManger.isConnectedGW() == false) {
                    // 当通过登录页绑定网关时，密码错误给出的提示
                    PwdHasChangedDialoge();
                }
            } else {
                String msg = mApplication.getString(R.string.login_connect_fail_hint) + ":" + errorcode;
                Toast.makeText(mApplication, msg, Toast.LENGTH_LONG)
                        .show();
            }
        } catch (Exception e) {
            Toast.makeText(mApplication, R.string.login_connect_fail_hint, Toast.LENGTH_SHORT)
                    .show();
        } finally {
            mDialogManager.dimissDialog(LOGIN_WAITING, 0);
        }
    }

    private void PwdHasChangedDialoge() {
        WLDialog.Builder builder = new WLDialog.Builder(MainApplication.getApplication().getTopActiviy());
        builder.setMessage(R.string.home_monitor_login_gateway_first_tip)
                .setPositiveButton(R.string.common_ok)
                .setTitle(R.string.device_songname_refresh_title);
        builder.create().show();
    }

    private void onLoginSuccess(String json) {
        Editor editor = sp.edit();
        // 记住账号
        editor.putString(APPConfig.ACCOUNT_NAME, WLCameraLoginUid);
        editor.putString(APPConfig.PASSWORD,
                Utils.encrypt(WLCameraLoginPwd, APPConfig.ENCRYPT_KEY));
        editor.commit();

        Utils.sysoInfo("base DataReturn USER_V5_LOGIN:::" + json);
        mApplication.getSharedPreferences(APPConfig.SP_CONFIG, 0).edit()
                .putString(APPConfig.ACCOUNT_USERINFO, json).commit();// 缓存用户信息
        Utils.saveUserInfo(json);// 保存全局用户信息

        userInfo = app.getUserinfo();

        ICamGlobal.getInstance().initSip();
        ICamGlobal.getInstance().registerAccount();

        this.initialized = true;
        this.initializing = false;

        for (Runnable task = tasksAfterLogin.poll(); task != null; task = tasksAfterLogin
                .poll()) {
            TaskExecutor.getInstance().execute(task);
        }

        if (dataBackListener != null) {
            dataBackListener.onUserLogin();
        }
    }

    private void showAddWlCameraDialog() {
        final WLDialog dialog;
        WLDialog.Builder builder = new Builder(mApplication.getTopActiviy());
        builder.setContentView(createConfirmView(mApplication
                .getString(R.string.home_monitor_bind_camera)));
        builder.setPositiveButton(android.R.string.ok);
        builder.setNegativeButton(android.R.string.cancel);
        builder.setListener(new MessageListener() {

            @Override
            public void onClickPositive(View contentViewLayout) {
                jumpToBindingActivity();
            }

            private void jumpToBindingActivity() {
                ConfigWiFiInfoModel data = new ConfigWiFiInfoModel();
                data.setDeviceId(WLCameraOperationManager.add_device_id);
                data.setAddDevice(true);
                data.setApConnect(false);
                data.setQrConnect(false);
                data.setSmartConnect(false);
                data.setWiredConnect(true);
                data.setConfigWiFiType(iCamConstants.CONFIG_WIRED_SETTING);
                Intent it = new Intent(WLCameraOperationManager.this.mApplication,
                        DeviceGetReadyGuideActivity.class);
                it.putExtra("configInfo", data);
                WLCameraOperationManager.this.mApplication.startActivity(it);
            }

            @Override
            public void onClickNegative(View contentViewLayout) {
            }

        });

        dialog = builder.create();
        dialog.show();
    }

    /**
     * dialog中布局与对应摄像机的选择
     *
     * @return tocDialog
     */
    private View createConfirmView(String remindString) {
        TextView monitorTextView;
        View tocDialog = View.inflate(mApplication,
                R.layout.sigin_fragment_remind_dialog_layout, null);
        monitorTextView = (TextView) tocDialog
                .findViewById(R.id.monitor_textview_for_alarmmessage);
        monitorTextView.setText(remindString);
        return tocDialog;
    }

    protected void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    public static void judgeAndOperateWLCameraGwId(String result,
                                                   final Context context) {
        /**
         * 当扫描获取的二维码大于12位，即二维码为摄像机二维码时 1.判断摄像机类型； 2.对摄像机进行网络配置；
         */
        result = result.toUpperCase(Locale.getDefault());

        if (result.length() == 16) {
            result = "CMIC" + result;
        }
        add_device_id = result;
        if (result.length() == 20) {
            if (result.startsWith("CMIC")||result.startsWith("CMİC")) {//这个地方用来识别土耳其的cmic格式，将上面改成locale.US 好像不行
                System.out.println("摄像机开始配置网络------>");
                showRemindConnectDialog(context);
            }
        }
    }

    /**
     * 弹出是否配置网络对话框
     */
    private static void showRemindConnectDialog(Context context) {
        SettingAndBindConfirmDialog builder = new SettingAndBindConfirmDialog(
                context);
        builder.initView();
        builder.show();
    }

    private static class SettingAndBindConfirmDialog extends WLDialog.Builder {
        private WLDialog dialog;

        private CheckBox bindAtTheSameTime = null;
        private Context parent;
        private View tocDialog;

        public SettingAndBindConfirmDialog(Context context) {
            super(context);
            parent = context;
        }

        public void initView() {
            this.setTitle(parent.getResources().getString(
                    R.string.device_prompt));
            this.setContentView(this.createBindView());
            this.setPositiveButton(android.R.string.ok);
            this.setNegativeButton(android.R.string.cancel);
            this.setListener(remindConnectDialogHandler);

            individualSetByCameraType();
            dialog = this.create();
        }

        private void individualSetByCameraType() {
            DeviceType type = DeviceType
                    .getDevivceTypeByDeviceID(add_device_id);
            switch (type) {
                case DESKTOP_C:
                    bindAtTheSameTime.setVisibility(View.GONE);
                    bindAtTheSameTime.setChecked(false);
                    break;
                default:
            }
        }

        public void show() {
            dialog.show();
        }

        private View createBindView() {
            tocDialog = View.inflate(parent,
                    R.layout.sigin_fragment_bind_dialog_layout, null);
            bindAtTheSameTime = (CheckBox) tocDialog
                    .findViewById(R.id.bind_at_the_sametime);
            return tocDialog;
        }

        MessageListener remindConnectDialogHandler = new MessageListener() {

            @Override
            public void onClickPositive(View contentViewLayout) {
                isWLCameraGateway = true;
                dialog.dismiss();
                boolean addDevice = bindAtTheSameTime.isChecked();
                WLCameraOperationManager ins = WLCameraOperationManager
                        .getInstance();
                Intent mIntent = new Intent();
                mIntent.setClass(parent, DeviceIdQueryActivity.class);
                mIntent.putExtra("msgData", add_device_id);
                mIntent.putExtra("isAddDevice", addDevice);
                // mApplication.startActivity(mIntent);

                if (addDevice) {
                    String gwid = add_device_id.substring(add_device_id
                            .length() - 12);
                    String defaultPasswd = MD5Util.encrypt(gwid.substring(6));
                    String pwd = defaultPasswd.substring(0, AIKAN_PASSWORD_ENDPOS);

                    final Intent finalIntent = mIntent;
                    ins.updateUIDandPwdAndRelogin(gwid, pwd);
                    mDialogManager.showDialog(LOGIN_WAITING, parent, null,
                            null);
                    Toast.makeText(parent,
                            R.string.login_user_account_register_fail, Toast.LENGTH_SHORT)
                            .show();
                    // 必须登录成功后再跳转到新页面，否则下一个页面可能会报错，或者绑定到其它账号上。
                    ins.runTaskAndAutoLogin(new Runnable() {
                        @Override
                        public void run() {
                            parent.startActivity(finalIntent);
                        }
                    });
                } else {
                    parent.startActivity(mIntent);
                }

            }

            @Override
            public void onClickNegative(View contentViewLayout) {
                dialog.dismiss();
            }
        };
    }

    public void checkBindingWLCamera() {
        if (!isWLCameraGateway) {
            return;
        }
        if (add_device_id == null || add_device_id.length() < 16) {
            return;
        }
        this.checkBinding(add_device_id);
    }

    private void promptBindingInfo(CheckBind cb) {
        if (!TextUtils.isEmpty(cb.getUuid())) {
            // 標識 被綁定過
            userInfo = app.getUserinfo();
            if (userInfo != null && cb.getUuid().equals(userInfo.getUuid())) {
                // 標識 被你 綁定了,do nothing
                ;
            } else {
                WLToast.showToast(
                        mApplication,
                        mApplication.getString(R.string.home_monitor_already_bind_in_other_gateway),
                        WLToast.TOAST_SHORT);
            }
        } else {
            showAddWlCameraDialog();
        }
    }

    private void SoftIpConfigWiFi() {
        if (!wifiAdmin.isWiFiEnabled()) {
            wifiAdmin.openWifi();
            WLToast.showToast(
                    mApplication,
                    mApplication.getResources().getString(
                            R.string.home_monitor_opening_wifi_now),
                    WLToast.TOAST_LONG);
            return;
        }
        Device device = new Device();
        device.setDevice_id(add_device_id);

        mApplication.startActivity(new Intent(mApplication, WifiSettingActivity.class)
                .putExtra("device", device));
    }

    public void setChangedSceneToWulianCamera() {
        final Scene scene = Scene.getInstance();// 爱看中场景list
        final List<SData> list = new ArrayList<SData>();
        final SceneInfo sceneSearchCondition = new SceneInfo();// V5中所有网关下的场景
        sceneSearchCondition.setGwID(mAccountManger.getmCurrentInfo().getGwID());
        List<SceneInfo> infos = sceneDao.findListAll(sceneSearchCondition);
        if (!infos.isEmpty()) {
            for (SceneInfo info : infos) {
                list.add(new SData(info.getName(), StringUtil.toInteger(info
                        .getIcon()), info.getSceneID(), info.getStatus()));
            }
        }
        if (scene.getOnSelectionLisenter() == null)
            scene.setOnSelectionLisenter(sceneSelectionListener);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                scene.setDataChanged(list);
            }
        });
    }

    public interface DataBackListener {
        public void onDeviceListBack(String json);

        public void onDeviceDeleted(String json);

        public void onUserLogin();
    }

    private DataBackListener dataBackListener = null;

    public void setDataBackListener(DataBackListener listener) {
        this.dataBackListener = listener;
    }

}
