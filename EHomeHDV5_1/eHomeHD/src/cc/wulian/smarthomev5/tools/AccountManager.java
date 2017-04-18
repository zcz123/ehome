package cc.wulian.smarthomev5.tools;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings.Secure;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSONObject;
import com.wulian.iot.Config;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.app.model.device.view.ColorPickerView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.entity.RegisterInfo;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.ihome.wan.entity.SceneInfo;
import cc.wulian.ihome.wan.sdk.user.AMSConstants;
import cc.wulian.ihome.wan.sdk.user.entity.AMSDeviceInfo;
import cc.wulian.ihome.wan.util.CollectionsUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.ResultUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.account.WLUserManager;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.activity.SigninActivityV5;
import cc.wulian.smarthomev5.activity.minigateway.ConfigDeviceMiniGateWayKnowWifiRelayActivity;
import cc.wulian.smarthomev5.activity.minigateway.ConfigDeviceMiniGatewayPageActivity;
import cc.wulian.smarthomev5.callback.SDKLogCallback;
import cc.wulian.smarthomev5.callback.ServiceCallback;
import cc.wulian.smarthomev5.dao.AreaDao;
import cc.wulian.smarthomev5.dao.SceneDao;
import cc.wulian.smarthomev5.dao.SigninDao;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.event.GatewayEvent;
import cc.wulian.smarthomev5.event.SigninEvent;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.fragment.house.AutoProgramTaskManager;
import cc.wulian.smarthomev5.fragment.welcome.WelcomeActivityV5;
import cc.wulian.smarthomev5.service.MainService;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.utils.InputMethodUtils;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.NetworkUtil;
import cc.wulian.smarthomev5.utils.URLConstants;
import cc.wulian.smarthomev5.utils.VersionUtil;
import de.greenrobot.event.EventBus;

import static android.content.Context.MODE_PRIVATE;


public class AccountManager {
    private static final String TAG = "AccountManager";
    private volatile static AccountManager instance = null;
    private MainApplication application = MainApplication.getApplication();

    private boolean isConnectedGW = false;
    private GatewayInfo mCurrentInfo;
    private Preference preference = Preference.getPreferences();
    private MainApplication mApp = MainApplication.getApplication();
    private DeviceCache mCache = DeviceCache.getInstance(mApp);
    private Map<String, GatewayInfo> historyGatewayMap = new HashMap<String, GatewayInfo>();
    private Map<String, GatewayInfo> searchGatewayMap = new HashMap<String, GatewayInfo>();
    private SigninDao signinDao = SigninDao.getInstance();
    private AreaDao areaDao = AreaDao.getInstance();
    private SceneDao sceneDao = SceneDao.getInstance();
    private AutoProgramTaskManager autoProgramTaskManager = null;
    private RegisterInfo registerInfo;
    private WLUserManager userManager = null;
    private WLDialog dialog;
    private View tocDialog;
    private SharedPreferences sp;

    //当用户修改密码时，此密码为修改后的密码，当收到密码更改成功时，把该密码置空
    private String newPassword = null;


    public GatewayInfo getmCurrentInfo() {
        return mCurrentInfo;
    }

    public void setmCurrentInfo(GatewayInfo mCurrentInfo) {
        this.mCurrentInfo = mCurrentInfo;
    }

    public boolean isConnectedGW() {
        return isConnectedGW;
    }

    public void setConnectedGW(boolean isConnectedGW) {
        this.isConnectedGW = isConnectedGW;
    }

    public static AccountManager getAccountManger() {

        if (instance == null) {
            synchronized (AccountManager.class) {
                if (instance == null) {
                    AccountManager newInstance = new AccountManager();
                    newInstance.initData();
                    instance = newInstance;
                }
            }
        }
        return instance;
    }

    private AccountManager() {
    }

    private void initData() {
        cacheAllGateWayInfo();
        autoProgramTaskManager = AutoProgramTaskManager.getInstance();

        mCurrentInfo = new GatewayInfo();
        mCurrentInfo.setGwID(preference.getLastSigninID());
        GatewayInfo historyInfo = findExistGatewayInfo(mCurrentInfo.getGwID());
        if (historyInfo != null) {
            mCurrentInfo = historyInfo;
        }

        userManager = WLUserManager.getInstance();
        if (application.getResources().getBoolean(R.bool.use_account)) {
            String account = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.ACCOUNT);
            String passwd = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.MD5PWD);
            String token = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.TOKEN);
            userManager.init(account, passwd, token);
        }

        EventBus.getDefault().register(this);

        Logger.debug("history gateway info:" + mCurrentInfo.getGwID() + " password:" + mCurrentInfo.getGwPwd());
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    searchGateway();
                } catch (Exception e) {

                }

            }
        });

    }

    public boolean loginLastGatewayByAccount() {

        if (preference.isAutoLoginChecked(mCurrentInfo.getGwID())) {
            connectToGateway(mCurrentInfo.getGwID(), mCurrentInfo.getGwPwd(), UserRightUtil.getInstance().isAdmin(), null,
                    Preference.ENTER_TYPE_ACCOUNT);
            getRightsAsync();
            return true;
        }

        int getUserInfoStatus = userManager.getStub().getUserInfo();
        if (getUserInfoStatus != AMSConstants.RESULT_AMS_COMMON_SUCCESSFUL) {
            tellConnectFailed(getUserInfoStatus);
            return false;
        }

        AMSDeviceInfo dInfo = WLUserManager.getInstance().getStub().getDeviceInfo(mCurrentInfo.getGwID());
        if (dInfo.status != AMSConstants.RESULT_AMS_COMMON_SUCCESSFUL) {
            // 没有查到授权信息
            Logger.info("Can't get device info info from AMS.");
            tellConnectFailed(dInfo.status);
            return false;
        }
        boolean isAdmin = dInfo.getIsAdmin();
        connectToGateway(mCurrentInfo.getGwID(), dInfo.getPassword(), isAdmin, null, Preference.ENTER_TYPE_ACCOUNT);
        return true;
    }

    private void getRightsAsync() {
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                int getUserInfoStatus = userManager.getStub().getUserInfo();
                if (getUserInfoStatus != AMSConstants.RESULT_AMS_COMMON_SUCCESSFUL) {
                    return;
                }

                AMSDeviceInfo dInfo = WLUserManager.getInstance().getStub().getDeviceInfo(mCurrentInfo.getGwID());
                if (dInfo.status != AMSConstants.RESULT_AMS_COMMON_SUCCESSFUL) {
                    // 没有查到授权信息
                    Logger.info("Can't get device info info from AMS.");
                    return;
                }
                if (dInfo.getIsAdmin()) {
                    UserRightUtil.getInstance().setAdmin(true);
                } else {
                    UserRightUtil.getInstance().setAdmin(false);
                    UserRightUtil.getInstance().loadUserRight(mCurrentInfo.getGwID());
                }
            }
        });
    }

    private void tellConnectFailed(int reason) {
        if (connectGatewayCallback != null) {
            connectGatewayCallback.connectFailed(reason);
        }
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public interface ConnectGatewayCallback {
        public static final int FAILED_BY_IS_LOGING = 101;
        public static final int FAILED_BY_CANT_GET_USER_RIGHT = 102;
        public static final int FAILED_BY_TIMEOUT = 103;
        public static final int FAILED_BY_CANT_GET_DEVICE_INFO = 104;

        public void connectSucceed();

        public void connectFailed(int reason);
    }

    private SoftReference<Activity> softActivity;// 调用登录操作的上下文
    private ConnectGatewayCallback connectGatewayCallback = null;

    public void setConnectGatewayCallbackAndActivity(ConnectGatewayCallback callback, final Activity activity) {
        this.connectGatewayCallback = callback;
        if (softActivity != null) {
            this.softActivity.clear();
        }
        this.softActivity = new SoftReference<Activity>(activity);
    }

    public void clearConnectGatewayCallbackAndActivity(ConnectGatewayCallback callback) {
        if (this.connectGatewayCallback == callback) {
            this.softActivity = null;
            this.connectGatewayCallback = null;
        }
    }

    public void connectToGateway(final String gwID, final String gwPwd, final boolean isAdmin,
                                 final ArrayList<String> ips, final String enterType) {

        if (this.isSigning(gwID)) {
            tellConnectFailed(ConnectGatewayCallback.FAILED_BY_IS_LOGING);
            return;
        }

        Preference.getPreferences().saveGatewayIsAdmin(isAdmin);
        Preference.getPreferences().saveUserEnterType(enterType);

        // 获取用户权限
        if (isAdmin) {
            UserRightUtil.getInstance().setAdmin(true);
        } else {
            UserRightUtil.getInstance().setAdmin(false);
            if (UserRightUtil.getInstance().loadUserRight(gwID) != AMSConstants.RESULT_AMS_COMMON_SUCCESSFUL) {
                tellConnectFailed(ConnectGatewayCallback.FAILED_BY_CANT_GET_USER_RIGHT);
                return;
            }
        }

        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                AccountManager.this.signinAccount(gwID.substring(gwID.length() - 12), gwPwd, ips);
            }
        });
    }

    private void handlerSigninResult(SigninEvent event) {
        if (softActivity == null || softActivity.get() == null)
            return;
        if (event.isSigninSuccess) {
            preference.saveLastSigninID(event.gwID);
            //这个地方存一下gwId在摄像机设置页绑定门锁界面判断  add by hxc
            sp = mApp.getSharedPreferences("doorLockGwId",MODE_PRIVATE);
            sp.edit().putString("gwId",event.gwID).commit();
            if (Preference.getPreferences().isUseAccount() && (UserRightUtil.getInstance().isAdmin() == false)) {
                // 授权用户不保存密码。
                preference.saveRememberChecked(false, event.gwID);
            }
            saveHistoryAndPassword();

//			updateMemHistory(event);
            registerAlertPush();

            ColorPickerView.isLan = (!StringUtil.isNullOrEmpty(mCurrentInfo.getGwSerIP()));
            if (connectGatewayCallback != null) {
                connectGatewayCallback.connectSucceed();
            }
        } else {
            switch (event.result) {
                case ResultUtil.EXC_GW_PASSWORD_WRONG:
                    //密码错误由回调部分处理。不统一提示。
                    break;
                case ResultUtil.RESULT_CHANGE_HOST:
                case ResultUtil.EXC_GW_REMOTE_SERIP:
                case ResultUtil.RESULT_CONNECTING:
                case ResultUtil.RESULT_DISCONNECT:
                    // do nothing, not actually failed.
                    return;
                default:
                    //增加对信息捕获的时间识别，当软件在后台运行时不进行弹框提醒
                    if (!mApp.isBackground()) {
                        String errMsg = getResultMessage(softActivity.get(), event.result);
                        WLToast.showToastWithAnimation(softActivity.get(), errMsg, Toast.LENGTH_SHORT);
                    }
            }
            tellConnectFailed(event.result);
        }
    }

    private void saveHistoryAndPassword() {
        String gwID = mCurrentInfo.getGwID();
        //update inmemory history
        historyGatewayMap.put(gwID, mCurrentInfo);

        boolean rememberPasswd = preference.isRememberChecked(gwID);
        String passwd = null;
        if (rememberPasswd) {
            // 保存密码
            passwd = mCurrentInfo.getGwPwd();
        }
        application.mDataBaseHelper.insertOrUpdateGwHistory(gwID, passwd,
                String.valueOf(System.currentTimeMillis()), mCurrentInfo.getGwSerIP(), true);
    }

    public void checkGatewayType(final Activity activity) {
        String gwType = AccountManager.getAccountManger().getmCurrentInfo().getGwType();
        if (gwType != null && gwType.equals("2")) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    CheckChildGatewayDialogBuilder builder = new CheckChildGatewayDialogBuilder(activity);
                    builder.initView();
                    builder.show();
                }
            });
        }
    }

    private void registerAlertPush() {
        String enterType = preference.getUserEnterType();
        String userID = null;
        if (Preference.getPreferences().isUseAccount()) {
            userID = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.USERID);
        }
        if (MainService.getMainService() != null) {
            try {
                String pushType=SmarthomeFeatureImpl.getData(WelcomeActivityV5.ANDROID_LOGIN_PUSH_TYPE);
                if(pushType.equals(WelcomeActivityV5.ANDROID_LOGIN_PUSH_MQTT)){
                    MainService.getMainService().registerPushTopic(userID, enterType);
                }
            } catch (Exception e) {
            }
        }
    }

    /**
     * @Title: showResetGwPasswordDialog
     * @Description:
     */
    public static void showResetGwPasswordDialog(final Activity activity) {
        ResetGwPasswordDialogBuilder builder = new ResetGwPasswordDialogBuilder(activity);
        builder.initView();
        builder.show();
    }

    private static class ResetGwPasswordDialogBuilder extends WLDialog.Builder {
        private static WLDialog dialog;
        private Context mContext;

        public ResetGwPasswordDialogBuilder(Context context) {
            super(context);
            mContext = context;
        }

        public void initView() {
            setContentView(R.layout.fragment_signfragment_reset_password_dialog);
            setPositiveButton(mContext.getString(R.string.login_forget_password_hint));
            setNegativeButton(android.R.string.cancel);
            setListener(new MessageListener() {
                @Override
                public void onClickPositive(View contentViewLayout) {
                    IntentUtil.startHtml5PlusActivity(mContext, URLConstants.LOCAL_BASEURL + "forgetwgpassword.html");
                    dialog.dismiss();
                }

                @Override
                public void onClickNegative(View contentViewLayout) {
                    dialog.dismiss();
                }
            });
            dialog = this.create();
        }

        public void show() {
            dialog.show();
        }

    }

    private static class CheckChildGatewayDialogBuilder extends WLDialog.Builder {
        private static WLDialog dialog;
        private Context mContext;

        public CheckChildGatewayDialogBuilder(Context context) {
            super(context);
            mContext = context;
        }

        public void initView() {
            setContentView(R.layout.check_child_manager_dialog);
            setPositiveButton(mContext.getString(R.string.set_account_manager_permission_unbinding_status));
            setNegativeButton(android.R.string.cancel);
            setListener(new MessageListener() {
                @Override
                public void onClickPositive(View contentViewLayout) {
                    GatewayInfo gatewayInfo = AccountManager.getAccountManger().getmCurrentInfo();
                    NetSDK.managerChildGateway( gatewayInfo.getGwID(),gatewayInfo.getManagerGWID(), "123456", 2 + "");
                    dialog.dismiss();
                }

                @Override
                public void onClickNegative(View contentViewLayout) {
                    dialog.dismiss();
                }
            });
            setCancelOnTouchOutSide(false);
            dialog = this.create();
        }

        public void show() {
            dialog.show();
        }

    }

    public static void showGatewayPasswordErrorFromAccountDialog(final Activity activity, String gwid) {
        GatewayPasswordErrorFromAccountDialogBuilder db = new GatewayPasswordErrorFromAccountDialogBuilder(activity,
                gwid);
        db.initView();
        db.show();
    }

    private static class GatewayPasswordErrorFromAccountDialogBuilder extends WLDialog.Builder {
        private WLDialog dialog;

        private EditText et_password = null;
        private TextView lb_forget_gateway_password = null;
        private String gwID = null;
        private Context mContext;
        private View contentView;

        public GatewayPasswordErrorFromAccountDialogBuilder(Context context, String gwid) {
            super(context);
            mContext = context;
            gwID = gwid;
        }

        public void initView() {
            this.setTitle(mContext.getResources().getString(R.string.home_password_error));
            this.setContentView(this.createBindView());
            this.setListener();
            this.setPositiveButton(android.R.string.ok);
            this.setNegativeButton(android.R.string.cancel);
            this.setListener(dialogHandler);
            dialog = this.create();
        }

        public void show() {
            dialog.show();
        }

        private void setListener() {
            lb_forget_gateway_password.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    IntentUtil.startHtml5PlusActivity(mContext, URLConstants.LOCAL_BASEURL + "forgetwgpassword.html");
                    dialog.dismiss();

                }
            });
        }

        private View createBindView() {
            contentView = View.inflate(mContext, R.layout.fragment_gateway_password_error_from_account, null);
            et_password = (EditText) contentView.findViewById(R.id.et_password);
            lb_forget_gateway_password = (TextView) contentView.findViewById(R.id.lb_forget_gateway_password);
            return contentView;
        }

        MessageListener dialogHandler = new MessageListener() {

            @Override
            public void onClickPositive(View contentViewLayout) {
                dialog.dismiss();
                if (InputMethodUtils.isShow(mContext)) {
                    InputMethodUtils.hide(mContext);
                }

                ProgressDialogManager.getDialogManager().showDialog(TAG, mContext, null, null);

                String password = et_password.getText().toString().trim();
                final String passwordMD5 = MD5Util.encrypt(password);
                TaskExecutor.getInstance().execute(new Runnable() {

                    @Override
                    public void run() {
                        AccountManager.getAccountManger().signinAccount(
                                GatewayPasswordErrorFromAccountDialogBuilder.this.gwID, passwordMD5);
                    }
                });
            }

            @Override
            public void onClickNegative(View contentViewLayout) {
                dialog.dismiss();
            }
        };
    }

    public void onEventMainThread(SigninEvent event) {
        if (SigninEvent.ACTION_SIGNIN_RESULT.equals(event.action)) {
            Log.i("requestSigninDialog", "status" + Config.isEagleNetWork);
            ProgressDialogManager.getDialogManager().dimissDialog(TAG, 0);
            // add syf 默认为 true 猫眼配网时 false
            if (Config.isEagleNetWork) {
                handlerSigninResult(event);
            }
        }
    }

    public void onEventMainThread(GatewayEvent event) {
        if (GatewayEvent.ACTION_CHANGE_PWD.equals(event.action) && (event.result == 0)) {
            WLToast.showToast(
                    mApp,
                    mApp.getString(R.string.PWSETOK),
                    WLToast.TOAST_SHORT);
            if (StringUtil.isNullOrEmpty(newPassword) == false) {
                //newPassword is not null, don't exit and save new password
                String newAikanPassword = newPassword.substring(0, WLCameraOperationManager.AIKAN_PASSWORD_ENDPOS);
                if (softActivity != null && softActivity.get() != null) {
                    WLCameraOperationManager.getInstance().changePassword(newAikanPassword);
                }
                mCurrentInfo.setGwPwd(newPassword);
                newPassword = null;
                saveHistoryAndPassword();
                WLCameraOperationManager.getInstance().refreshUserInfoIfGatewayChanged();
            } else {
                //newPassword is null, others modified password, should logout
                if (softActivity != null && softActivity.get() != null) {
                    getAccountManger().exitCurrentGateway(softActivity.get());
                }
            }

        }
    }

    public void updateCurrentAccount(GatewayInfo info) {
        mCurrentInfo = info;
        preference.saveLastSigninID(info.getGwID());
    }

    public void updateAutoLogin(String gwID) {
        preference.saveAutoLoginChecked(true, gwID);
    }

    public void removeAllHouseRule() {
        autoProgramTaskManager.clear();
    }

    public void switchAccount(GatewayInfo info) {
        if (info == null)
            return;
        logoutAccount();
        updateCurrentAccount(info);
    }

    public void exitCurrentGateway(Context context) {
        logoutAccount();
        preference.saveAutoLoginChecked(false, mCurrentInfo.getGwID());
        if (preference.isUseAccount()) {
            SmarthomeFeatureImpl.setData(
                    SmarthomeFeatureImpl.Constants.GATEWAYID, "");
            exitGatewayToAccountLoginHTML(context);
        } else {
            exitGatewayToSigninActivity(context);
        }
        if (context instanceof Activity) {
            ((Activity) context).finish();
        }
    }

    private void exitGateWayToSignActivityByDefaultContext() {
        if (softActivity != null && softActivity.get() != null) {
            AccountManager.this.exitGatewayToSigninActivity(softActivity.get());
        } else {
            Log.i(TAG,
                    "Can't login default account, because infomation is not enough. should call setConnectGatewayCallbackAndActivity to handle this situation.");
        }
    }

    private void exitGatewayToSigninActivity(Context activity) {
        MainService.getMainService().unregisterCurrentTopic();
        activity.startActivity(new Intent(activity, SigninActivityV5.class));
        MainApplication.getApplication().stopApplication();
    }

    private void exitGatewayToAccountLoginHTML(Context context) {
        Intent intent = new Intent();
        intent.setClass(context, Html5PlusWebViewActvity.class);
        String uri = URLConstants.LOCAL_BASEURL + "gatewayList.html?autoLoginFlag=0";
        intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
        context.startActivity(intent);
        if(context instanceof Activity) {
            Activity activity = (Activity)context;
            activity.finish();
        }
    }

    public boolean signinDefaultAccount() {
        if (isConnectedGW) {
            return true;
        }

        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    EventBus.getDefault().post(
                            new GatewayEvent(GatewayEvent.ACTION_CONNECTING, mCurrentInfo.getGwID(),
                                    ResultUtil.RESULT_CONNECTING));
                    if (preference.isUseAccount()) {
                        boolean logining = AccountManager.this.loginLastGatewayByAccount();
                        if (logining == false) {
                            EventBus.getDefault().post(
                                    new GatewayEvent(GatewayEvent.ACTION_DISCONNECTED, mCurrentInfo.getGwID(),
                                            ResultUtil.RESULT_FAILED));
                        }
                    } else {
                        if ((StringUtil.isNullOrEmpty(mCurrentInfo.getGwID())
                                || StringUtil.isNullOrEmpty(mCurrentInfo.getGwPwd())
                                || false == preference.isAutoLoginChecked(mCurrentInfo.getGwID()))
                                && application.isBackground() == false) {
                            exitGateWayToSignActivityByDefaultContext();
                        } else {
                            SendMessage.customIp = findExistGatewayServerIP(mCurrentInfo.getGwID());
                            signinAccount(mCurrentInfo.getGwID(), mCurrentInfo.getGwPwd());
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "", e);
                    EventBus.getDefault().post(
                            new GatewayEvent(GatewayEvent.ACTION_DISCONNECTED, mCurrentInfo.getGwID(),
                                    ResultUtil.RESULT_FAILED));
                }
            }
        });
        return false;
    }

    public void signinAccount(String gwID, String gwPwd) {
        signinAccount(gwID, gwPwd, null);
    }

    private synchronized void signinAccount(String gwID, String gwPwd, List<String> ips) {
        if (isSigning(gwID) && NetSDK.isConnected(gwID))
            return;
        logoutAccount();
        if (mCurrentInfo == null) {
            mCurrentInfo = new GatewayInfo();
        }
        mCurrentInfo.setGwID(gwID);
        mCurrentInfo.setGwPwd(gwPwd);

        if (!NetSDK.isValid()) {
            NetSDK.init(new ServiceCallback(mApp));
            NetSDK.setLogCallback(new SDKLogCallback());
        }
        cacheData(gwID);
        if (ips == null) {
            SendMessage.customIp = findExistGatewayServerIP(gwID);
            SendMessage.connect(gwID, gwPwd, getRegisterInfo());
        } else {
            NetSDK.connectSpecial(gwID, ips, gwPwd, getRegisterInfo());
        }
    }

    public List<GatewayInfo> searchGateway() {
        searchGatewayMap.clear();
        // 获得局域网网关，返回值List<JSONobject>,每个JSONobject包含网关ID(gwID)和网关IP字段(gwSerIP)
        List<JSONObject> result = NetSDK.search();
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                JSONObject wgJsonObject = result.get(i);
                String gWID = wgJsonObject.getString("gwID");
                String gWIP = wgJsonObject.getString("gwSerIP");

            }
        }
        if (result != null) {
            for (JSONObject object : result) {
                GatewayInfo info = new GatewayInfo(object);
                searchGatewayMap.put(info.getGwID(), info);
            }
        }
        return CollectionsUtil.mapConvertToList(searchGatewayMap);
    }

    // 得到Mini网关ID IP
    public Map<String, String> searchGateWayGetIp() {
        List<JSONObject> result = NetSDK.search();
        Map<String, String> gwMap = new HashMap<>();
        if (result != null) {
            for (int i = 0; i < result.size(); i++) {
                JSONObject wgJsonObject = result.get(i);
                String gWIP = wgJsonObject.getString("gwSerIP");
                String gWID = wgJsonObject.getString("gwID");
                gwMap.put(gWID, gWIP);
            }

        }
        return gwMap;
    }

    public boolean isSigning(String gwID) {
        return NetSDK.isConnecting(gwID);
    }

    public void logoutAccount() {
        isConnectedGW = false;
        if (mCurrentInfo == null) {
            return;
        }
        try {
            NetSDK.disconnect(mCurrentInfo.getGwID());
            NetSDK.disconnectAll();
        } catch (Exception e) {
            Log.e(TAG, "", e);
        }
    }

    public String getGatewayName(String gwID) {
        String gwName = preference.getGateWayName(gwID);
        if (StringUtil.isNullOrEmpty(gwName)) {
            gwName = gwID;
        }
        return gwName;
    }

    public String getResultMessage(Context context, int result) {
        Resources res = context.getResources();
        String msg = null;
        switch (result) {
            case ResultUtil.RESULT_SUCCESS:
                msg = res.getString(R.string.login_login_success_hint);
                break;
            case ResultUtil.EXC_GW_USER_WRONG:
                msg = res.getString(R.string.login_name_error);
                break;
            case ResultUtil.EXC_GW_PASSWORD_WRONG:
                msg = res.getString(R.string.login_password_error);
                break;
            case ResultUtil.LOGIN_RUNING:
                msg = res.getString(R.string.home_monitor_result_connecting);
                break;
            case ResultUtil.RESULT_FAILED:
                if (NetworkUtil.isNetworkAvailable(context))
                    msg = res.getString(R.string.login_gateway_login_failed_hint);
                else
                    msg = res.getString(R.string.login_no_network_hint);
                break;
            default:
                msg = "Unknown error:" + result;
        }
        return msg;
    }

    public void removeAccount(String gwID) {
        preference.clearCustomKeyData(gwID);
        historyGatewayMap.remove(gwID);
        searchGatewayMap.remove(gwID);
        GatewayInfo gateway = new GatewayInfo();
        gateway.setGwID(gwID);
        signinDao.delete(gateway);
        mCache.removeDeviceInGateway(gwID);
    }

    public List<GatewayInfo> getHistoryGatewayInfos() {
        return CollectionsUtil.mapConvertToList(historyGatewayMap);
    }

    public List<GatewayInfo> getSearchGatewayInfos() {
        return CollectionsUtil.mapConvertToList(searchGatewayMap);
    }

    public GatewayInfo findExistGatewayInfo(String gwID) {
        if (StringUtil.isNullOrEmpty(gwID))
            return null;
        return historyGatewayMap.get(gwID);
    }

    public String findExistGatewayServerIP(String gwID) {
        if (StringUtil.isNullOrEmpty(gwID))
            return null;
        String resultIp = null;
        for (GatewayInfo info : getSearchGatewayInfos()) {
            if (StringUtil.equals(info.getGwID(), gwID)) {
                return info.getGwSerIP();
            }
        }
        GatewayInfo gwInfo = historyGatewayMap.get(gwID);
        if (gwInfo != null) {
            return gwInfo.getGwSerIP();
        }
        return resultIp;
    }

    private void cacheData(String gwID) {
        // cacheDeviceInfo(gwID);
        cacheRoomInfo(gwID);
        cacheSceneInfo(gwID);
    }

    public GatewayInfo getLastGateayInfo() {
        String gwID = preference.getLastSigninID();
        GatewayInfo info = new GatewayInfo();
        if (!TextUtils.isEmpty(gwID)) {
            info.setGwID(gwID);
            GatewayInfo historyGateWay = signinDao.getById(info);
            if (historyGateWay != null) {
                info = historyGateWay;
            }
        }
        return info;
    }

    public void cacheAllGateWayInfo() {
        List<GatewayInfo> infos = signinDao.findListAll(new GatewayInfo());
        for (GatewayInfo info : infos) {
            historyGatewayMap.put(info.getGwID(), info);
        }
    }

    private void cacheRoomInfo(String gwID) {
        AreaGroupManager.getInstance().clear();
        RoomInfo arg = new RoomInfo();
        arg.setGwID(gwID);
        List<RoomInfo> roomInfos = areaDao.findListAll(arg);
        for (RoomInfo info : roomInfos) {
            DeviceAreaEntity entity = new DeviceAreaEntity(info);
            AreaGroupManager.getInstance().addDeviceAreaEntity(entity);
        }
    }

    private void cacheSceneInfo(String gwID) {
        SceneInfo arg = new SceneInfo();
        arg.setGwID(gwID);
        List<SceneInfo> sceneInfos = sceneDao.findListAll(arg);
        final Map<String, SceneInfo> sceneMap = mApp.sceneInfoMap;
        sceneMap.clear();
        Iterator<SceneInfo> iterator = sceneInfos.iterator();
        while (iterator.hasNext()) {
            SceneInfo sceneInfo = iterator.next();
            sceneMap.put(gwID + sceneInfo.getSceneID(), sceneInfo);
        }
    }

    private String getCurrentNetType() {
        String type = "wifi";
        ConnectivityManager cm = (ConnectivityManager) mApp.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        if (info != null && info.getType() == ConnectivityManager.TYPE_MOBILE) {
            int subType = info.getSubtype();
            if (subType == TelephonyManager.NETWORK_TYPE_CDMA || subType == TelephonyManager.NETWORK_TYPE_GPRS
                    || subType == TelephonyManager.NETWORK_TYPE_EDGE) {
                type = "2G";
            } else if (subType == TelephonyManager.NETWORK_TYPE_UMTS || subType == TelephonyManager.NETWORK_TYPE_HSDPA
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_A
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_0
                    || subType == TelephonyManager.NETWORK_TYPE_EVDO_B) {
                type = "3G";
            } else if (subType == TelephonyManager.NETWORK_TYPE_LTE) {// LTE是3g到4g的过渡，是3.9G的全球标准
                type = "4G";
            }
        }
        return type;
    }

    public RegisterInfo getRegisterInfo() {
        if (registerInfo == null) {
            TelephonyManager tm = (TelephonyManager) mApp.getSystemService(Context.TELEPHONY_SERVICE);
            // 设备的唯一标识
            String deviceId = tm.getDeviceId();
            // SIM卡的唯一标识
            String simId = tm.getSubscriberId();
            // SIM卡序列号
            String simSerialNo = tm.getSimSerialNumber();
            // SIM卡运营商的国家代码
            String simCountryIso = tm.getSimCountryIso();
            // SIM卡运营商名称
            String simOperatorName = tm.getSimOperatorName();

            // 设置终端信息
            if (StringUtil.isNullOrEmpty(deviceId)) {
                deviceId = Secure.getString(mApp.getContentResolver(), Secure.ANDROID_ID);
            }
            if (StringUtil.isNullOrEmpty(simOperatorName)) {
                String operator = tm.getSimOperator();
                if (operator.equals("46001")) { // 中国联通
                    simOperatorName = "ChinaUnicom";
                }
            }
            registerInfo = new RegisterInfo(deviceId);
            String appType = mApp.getResources().getString(R.string.app_type);
            registerInfo.setSdkToken("");
            registerInfo.setAppType(appType);
            registerInfo.setAppVersion("V5_" + VersionUtil.getVersionName(mApp));
            registerInfo.setNetType(getCurrentNetType());
            registerInfo.setSimId(simId);
            registerInfo.setSimSerialNo(simSerialNo);
            registerInfo.setSimCountryIso(simCountryIso);
            registerInfo.setSimOperatorName(simOperatorName);
            registerInfo.setPhoneType(android.os.Build.MODEL);
            registerInfo.setPhoneOS(android.os.Build.VERSION.RELEASE);

        }
        registerInfo.setLang(LanguageUtil.getWulianCloudLanguage());
        return registerInfo;
    }

    /**
     * 判断当前应用程序处于前台还是后台
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;

    }

    // mini网关的中继配置之dialog
    public void showRemindallocationrelayDialog(boolean showPositiveButton, boolean showNegativeButton, final String add_device_id, final Context context) {
        // 先获得布局view，再获得其中的控件button
        tocDialog = View.inflate(context,
                R.layout.device_mini_geteway_allocation_relay, null);
        Button btn_konw_relay = (Button) tocDialog
                .findViewById(R.id.device_mini_gateway_know_relay);
        WLDialog.Builder builder = new WLDialog.Builder(context);
        builder.setTitle(context.getResources().getString(
                R.string.device_songname_refresh_title));

        builder.setContentView(tocDialog);
        if (showPositiveButton) {
            builder.setPositiveButton(android.R.string.ok);
        }
        if (showNegativeButton) {
            builder.setNegativeButton(android.R.string.cancel);
        }

        builder.setListener(new MessageListener() {
            @Override
            public void onClickPositive(View contentViewLayout) {
                Intent it = new Intent(context,
                        ConfigDeviceMiniGatewayPageActivity.class);
                it.putExtra("deviceId", add_device_id);
                context.startActivity(it);
            }

            @Override
            public void onClickNegative(View contentViewLayout) {
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        btn_konw_relay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,
                        ConfigDeviceMiniGateWayKnowWifiRelayActivity.class);
                context.startActivity(intent);
            }
        });
        dialog.show();
    }

}
