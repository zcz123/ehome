package cc.wulian.smarthomev5.activity;


import java.util.Locale;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;
import com.yuantuo.customview.ui.WLToast;
import com.yuantuo.netsdk.TKCamHelper;

import java.util.Locale;

import android.widget.TextView;

import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.minigateway.MiniGatewayChannelConflictHelpIdeaActivity;

import cc.wulian.smarthomev5.callback.router.CmdindexTools;
import cc.wulian.smarthomev5.event.GatewaInfoEvent;
import cc.wulian.smarthomev5.event.RouterWifiSettingEvent;
import cc.wulian.smarthomev5.fragment.home.HomeFragment.FragmentCallBack;
import cc.wulian.smarthomev5.fragment.home.HomeManager;
import cc.wulian.smarthomev5.fragment.navigation.NavigationFragment;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat.OnLeftIconClickListener;
import cc.wulian.smarthomev5.tools.FrontBackgroundManager;
import cc.wulian.smarthomev5.tools.IPreferenceKey;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.tools.UpdateManger;
import cc.wulian.smarthomev5.tools.UpdateManger.NewVersionDownloadListener;
import cc.wulian.smarthomev5.view.UpdateProcessDialog;

public class MainHomeActivity extends EventBusActivity implements
        FragmentCallBack {
    private UpdateManger updateManager;
    // 侧边栏全局变量
    private SlidingMenu mSlidingMenu;
    private UpdateProcessDialog progessDialog = null;
    private NavigationFragment navigationFragement;
    private HomeManager homeManager = HomeManager.getInstance();
    private WLDialog dialog;
    private AccountManager accountManager = AccountManager.getAccountManger();
    private Boolean is_minigateway = false;
    private Boolean is_sxgateway = false;
    private DeviceCache deviceCache = DeviceCache.getInstance(this);
    private int channel;
    private int zegbeeChannel;
    private int numDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initConfig();
        initMenu();
        initBar();
        checkForNewVersion();
        // checkForPassword();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mApplication.isTaskRunBack) {
            mApplication.isTaskRunBack = false;
            FrontBackgroundManager.getInstance().fireFrongBackgroundListener(
                    false);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        mAccountManager.signinDefaultAccount();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mApplication.isBackground()) {
            mApplication.isTaskRunBack = true;
            Preference.getPreferences().putLong(
                    IPreferenceKey.P_KEY_GO_BACKGROPUND_TIME,
                    System.currentTimeMillis());
            FrontBackgroundManager.getInstance().fireFrongBackgroundListener(
                    true);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mAccountManager
                .clearConnectGatewayCallbackAndActivity(gatewayConnectCallback);
    }

    private void miniGatewayshowChannelConflictDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setTitle(getResources().getString(
                R.string.gateway_router_setting_dialog_toast));
        View conflictDialog = View.inflate(this,
                R.layout.device_mini_geteway_if_channel_conflict, null);
        builder.setContentView(conflictDialog);
        builder.setPositiveButton(getResources().getString(R.string.minigw_to_solve_hint));
        builder.setNegativeButton(getResources().getString(R.string.minigw_ignore_hint));
        builder.setListener(new MessageListener() {
            @Override
            public void onClickPositive(View contentViewLayout) {
                Intent intent = new Intent();
                intent.setClass(MainHomeActivity.this,
                        MiniGatewayChannelConflictHelpIdeaActivity.class);
                startActivity(intent);
            }

            @Override
            public void onClickNegative(View contentViewLayout) {

            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void sxGatewayshowChannelConflictDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setTitle(getResources().getString(
                R.string.gateway_router_setting_dialog_toast));
        View conflictDialog = View.inflate(this,
                R.layout.device_mini_geteway_if_channel_conflict, null);
        TextView conflict_tv = (TextView) conflictDialog
                .findViewById(R.id.judge_wifi_zegbee_isconflict);
        if ((channel > 5 && (zegbeeChannel == 24 || zegbeeChannel == 25))) {
            conflict_tv
                    .setText(getResources()
                            .getString(
                                    R.string.gateway_router_login_high_channel_interference_error));
        } else if ((!(channel >= 9 && channel <= 13) && zegbeeChannel == 11)) {
            conflict_tv
                    .setText(getResources()
                            .getString(
                                    R.string.gateway_router_login_low_channel_interference_error));
        }
        builder.setContentView(conflictDialog);
        builder.setPositiveButton(
                getResources().getString(R.string.HTML_forgetwgpassword_iknow))
                .setNegativeButton(getResources().getString(R.string.cancel))
                .setListener(new MessageListener() {

                    @Override
                    public void onClickPositive(View contentViewLayout) {

                    }

                    @Override
                    public void onClickNegative(View contentViewLayout) {

                    }
                });
        dialog = builder.create();
        dialog.show();
    }

    /**
     * 初始化配置
     */
    private void initConfig() {
        mApplication.initNotification(getIntent());
        mApplication.startService();
        mAccountManager.setConnectGatewayCallbackAndActivity(
                gatewayConnectCallback, this);
        Preference.getPreferences().putLong(
                IPreferenceKey.P_KEY_GO_BACKGROPUND_TIME, 0);
        Preference.getPreferences().putLong(
                IPreferenceKey.P_KEY_USE_LOGINED_LOG, 0);
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                homeManager.loadBaseData();
                homeManager.checkAdvVersion();
                checkGatewayLegal();
                accountManager.checkGatewayType(MainHomeActivity.this);
            }

            private void checkGatewayLegal() {
                String isLegal = mAccountManager.getmCurrentInfo().isLegal();
                switch (isLegal) {
                    case "true"://正常登陆
                        break;
                    case "false"://强制刷新当地的语言为英文
                        homeManager.initLanguage(Locale.ENGLISH);
                        break;
                    case "01":
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                // 强制用户退出登录
                                forceQuitDialog();
                            }
                        });
                        break;
                }
            }
        });
    }


    private AccountManager.ConnectGatewayCallback gatewayConnectCallback = new AccountManager.ConnectGatewayCallback() {

        @Override
        public void connectSucceed() {

        }

        @Override
        public void connectFailed(int reason) {
            Log.e("MainHomeActivity", "Connect gateway failed:" + reason);
        }
    };

    protected void forceQuitDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setTitle(getResources().getString(
                R.string.gateway_router_setting_dialog_toast));
        builder.setMessage(getResources().getString(
                R.string.isgwlegal_force_quit_dialog));
        builder.setCancelOnTouchOutSide(false);
        builder.setPositiveButton(android.R.string.ok);
        builder.setNegativeButton(null);
        builder.setListener(new MessageListener() {

            @Override
            public void onClickPositive(View contentViewLayout) {
                finish();
            }

            @Override
            public void onClickNegative(View contentViewLayout) {

            }
        });
        WLDialog modifyGwIDAndPwdDialog = builder.create();
        modifyGwIDAndPwdDialog.show();
    }

    /**
     * 检测是否修改密码
     */
    private void checkForPassword() {
        String gwID = mAccountManager.getmCurrentInfo().getGwID();
        if (mAccountManager.isConnectedGW() && gwID != null && gwID.length() >= 6) {
            String oldPwd = gwID.substring(gwID.length() - 6, gwID.length());
            String oldEncryptPwd = MD5Util.encrypt(oldPwd);
            String localPwd = mAccountManager.getmCurrentInfo().getGwPwd();

            if (mAccountManager.getGatewayName(gwID).equals(gwID)
                    && oldEncryptPwd.equals(localPwd)) {
                showModifyGwIDAndPwdDialog(oldPwd);
            }
        }
    }

    private void showModifyGwIDAndPwdDialog(final String oldPwd) {
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.home_gateway_modify));
        builder.setContentView(LayoutInflater.from(this).inflate(
                R.layout.modify_gw_id_pwd_dialog, null));
        builder.setPositiveButton(android.R.string.ok);
        builder.setNegativeButton(getResources().getString(R.string.guide_skip));
        builder.setListener(new MessageListener() {

            @Override
            public void onClickPositive(View contentViewLayout) {
                EditText gwID = (EditText) contentViewLayout
                        .findViewById(R.id.modify_gw_dialog_id);
                EditText pwd = (EditText) contentViewLayout
                        .findViewById(R.id.modify_gw_dialog_pwd);
                String modifyGwName = gwID.getText().toString().trim();
                String modifyPwd = pwd.getText().toString().trim();
                sendModifyMessage(modifyGwName, oldPwd, modifyPwd);
            }

            @Override
            public void onClickNegative(View contentViewLayout) {

            }
        });
        WLDialog modifyGwIDAndPwdDialog = builder.create();
        modifyGwIDAndPwdDialog.show();

    }

    private void sendModifyMessage(String gwName, String oldPwd, String newPwd) {
        if (StringUtil.isNullOrEmpty(gwName)) {
            WLToast.showToast(
                    this,
                    getResources()
                            .getString(
                                    R.string.set_account_manager_gw_name_modify_not_empty),
                    WLToast.TOAST_SHORT);
            return;

        } else if (StringUtil.isNullOrEmpty(newPwd)) {
            WLToast.showToast(
                    this,
                    getResources().getString(
                            R.string.set_password_not_null_hint),
                    WLToast.TOAST_SHORT);
            return;
        }
        if (newPwd.length() < 6 || newPwd.length() > 16) {
            WLToast.showToast(
                    this,
                    getResources()
                            .getString(
                                    R.string.set_account_manager_modify_gw_password_length_not_enough),
                    WLToast.TOAST_SHORT);
            return;
        } else if (oldPwd.equals(newPwd)) {
            WLToast.showToast(
                    this,
                    getResources()
                            .getString(
                                    R.string.set_account_manager_modify_gw_old_new_not_same),
                    WLToast.TOAST_SHORT);
            return;
        }
        newPwd = MD5Util.encrypt(newPwd);
        mAccountManager.setNewPassword(newPwd);

        /**
         * CMD_REQUEST_SET_GATEWAY_INFO = 41
         *
         * 模式(0:获取,2:修改,4:重启网关,5:恢复出厂设置)
         */
        NetSDK.setGatewayInfo(mAccountManager.getmCurrentInfo().getGwID(), "2",
                null, gwName, null, null, null, null, null,null);
        SendMessage.sendChangeGwPwdMsg(this,
                mAccountManager.getmCurrentInfo().getGwID(),
                MD5Util.encrypt(oldPwd), newPwd);
    }

    /**
     * 检查更新
     */
    private void checkForNewVersion() {
        if (!this.getResources().getBoolean(R.bool.use_update)) {
            return;
        }
        updateManager = UpdateManger.getInstance(this);
        updateManager
                .setNewVersionDownloadListener(new NewVersionDownloadListener() {

                    @Override
                    public void processing(int present) {
                        if (progessDialog == null) {
                            progessDialog = new UpdateProcessDialog(
                                    MainHomeActivity.this);
                            progessDialog.show();
                        }
                        progessDialog.setProgess(present);
                        if (present >= 100) {
                            progessDialog.dismiss();
                            progessDialog = null;
                            updateManager.startInstall();
                        }
                    }

                    @Override
                    public void processError(Exception e) {
                        WLToast.showToast(MainHomeActivity.this,
                                getString(R.string.set_version_update_erro),
                                WLToast.TOAST_SHORT);
                        if (progessDialog != null) {
                            progessDialog.dismiss();
                            progessDialog = null;
                        }
                    }
                });
        updateManager.checkUpdate(true);
    }

    /**
     * 初始化ActionBar
     */
    private void initBar() {
        getCompatActionBar().setLeftIconClickListener(
                new OnLeftIconClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (navigationFragement.isShowMenu())
                            navigationFragement.showContent();
                        else {
                            navigationFragement.showMenu();
                        }
                    }
                });
    }

    /**
     * 初始化菜单左侧
     */
    private void initMenu() {
        navigationFragement = new NavigationFragment();
        getmSlidingMenu();
        mSlidingMenu.setMode(SlidingMenu.LEFT);
        mSlidingMenu.setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
        mSlidingMenu.setTouchmodeMarginThreshold(getResources()
                .getDimensionPixelSize(R.dimen.slidingmenu_margin_threshold));
        mSlidingMenu.setShadowWidthRes(R.dimen.slidingmenu_shadow_width);
        mSlidingMenu.setShadowDrawable(R.drawable.slidingmenu_shadow);
        mSlidingMenu.setBehindOffsetRes(R.dimen.slidingmenu_offset);
        mSlidingMenu.setFadeDegree(0.35f);
        mSlidingMenu.attachToActivity(this, SlidingMenu.SLIDING_WINDOW);
        mSlidingMenu.setMenu(R.layout.fragment_nav);
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_nav, navigationFragement,
                        NavigationFragment.class.getName()).commit();

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_MENU) {
            navigationFragement.showMenu();
        } else if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
                && event.getAction() != KeyEvent.ACTION_UP) {
            if (navigationFragement.isShowMenu()) {
                navigationFragement.showContent();
            } else {
                if (navigationFragement.isCurrentHome()) {
                    this.moveTaskToBack(true);
                    homeManager.notifyExit();
                } else {
                    navigationFragement.showMenu();
                }

            }
        }else if (event.getKeyCode() == KeyEvent.KEYCODE_DEL){
            return super.dispatchKeyEvent(event);
        }
            return false;
    }

    public SlidingMenu getmSlidingMenu() {
        if (mSlidingMenu == null) {
            mSlidingMenu = new SlidingMenu(this);
        }
        return mSlidingMenu;
    }

    @Override
    public void resetActionMenu() {
        super.resetActionMenu();
        if (navigationFragement != null) {
            navigationFragement.refreshLeftMenuRedDot();
        }
    }

    @Override
    protected boolean finshSelf() {
        return false;
    }

    @Override
    public boolean fingerRightFromLeft() {
        return false;
    }

    @Override
    public boolean fingerLeft() {
        return false;
    }

    @Override
    public void callbackOpean(boolean isOpean) {
        if (isOpean) {
            mSlidingMenu.showMenu();
        }
    }

    public void onEventMainThread(GatewaInfoEvent gatewayinfoevent) {
        GatewayInfo gatewayInfo = accountManager.getmCurrentInfo();
        if (gatewayInfo != null && gatewayInfo.getGwVer() != null) {
            String gwver = gatewayInfo.getGwVer();
            if (gwver.length() >= 3) {
                is_minigateway = (gwver.charAt(2) + "").equals("8");
                is_sxgateway = (gwver.substring(2, 4) + "").equals("10");
            }
        }

        numDialog++;
        String zgbChannel = gatewayinfoevent.getGwChannel();
        zegbeeChannel = Integer.parseInt(zgbChannel, 16);
        Boolean istrue = (channel <= 5 && (zegbeeChannel == 24 || zegbeeChannel == 25))
                || ((channel >= 9 && channel <= 13) && zegbeeChannel == 11) || zegbeeChannel == 0;
        int deviceSize = deviceCache.size();
        if (numDialog == 1) {
            if (deviceSize != 0) {
                if (is_sxgateway) {
                    if (!istrue) {
                        sxGatewayshowChannelConflictDialog();
                    } else {
                    }
                } else if (is_minigateway) {
                    if (!istrue) {
//                        miniGatewayshowChannelConflictDialog();  //现在不要这个提醒了
                    } else {
                    }
                } else {
                }
            } else {
            }
        }
    }

    public void onEventMainThread(RouterWifiSettingEvent routerWifiSettingEvent) {
        channel = Integer.parseInt(routerWifiSettingEvent.getRadioList().get(0)
                .getChannel());
    }

}
