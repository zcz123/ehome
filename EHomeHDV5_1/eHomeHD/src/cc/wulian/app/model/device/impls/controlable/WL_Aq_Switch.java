package cc.wulian.app.model.device.impls.controlable;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.impls.alarmable.AlarmableDeviceImpl;
import cc.wulian.app.model.device.impls.alarmable.DefaultAlarmableDeviceImpl;
import cc.wulian.app.model.device.impls.alarmable.Defenseable;
import cc.wulian.app.model.device.interfaces.AbstractLinkTaskView;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;


/**
 * Created by hanx .
 * 人体感应开关
 */
@DeviceClassify(devTypes = {"Aq"},category = Category.C_OTHER)
public class WL_Aq_Switch extends AlarmableDeviceImpl {
    private static final String TAG = WL_Aq_Switch.class.getSimpleName();
    private String pluginName="HBSS.zip";
    private TextView textView;
    private boolean isUsePlugin=true;
    private H5PlusWebView webView;
    // 设防状态   01：设防状态     00：撤防状态
    private String mDefenseState;
    // 红外报警状态  00：消警  01：报警
    private String mAlarmableState;
    private final String HTML_BASEURI = "file:///android_asset/device_Aq/";
    private final String DEFENSE_SETUP_SEND_CMD = "31";
    private final String DEFENSE_UNSETUP_SEND_CMD = "30";
    private final String STATE_SETUP = "01";
    private final String STATE_UNSETUP = "00";
    private int DRAWABLE_ICON = R.drawable.device_aq_icon;

    public WL_Aq_Switch(Context context, String type) {
        super(context, type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
        View view=inflater.inflate(R.layout.device_aq_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveState) {
        super.onViewCreated(view, saveState);
        webView = (H5PlusWebView) view.findViewById(R.id.device_aq_webview);
        textView= (TextView) view.findViewById(R.id.device_aq_search_tv);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
        Engine.bindWebviewToContainer((H5PlusWebViewContainer)this.getCurrentFragment(), webView);

        if(isUsePlugin){
            getPlugin("deviceMenu.html","deviceAq");
        }
        else {
            textView.setVisibility(View.GONE);
            webView.setWebviewId("deviceAq");
            webView.loadUrl(HTML_BASEURI+"deviceMenu.html");
        }
    }

    private void getPlugin(final String urlName,final String htmlID) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PluginsManager pm = PluginsManager.getInstance();
                pm.getHtmlPlugin(mContext, pluginName,
                        new PluginsManager.PluginsManagerCallback() {

                            @Override
                            public void onGetPluginSuccess(PluginModel model) {

                                textView.setVisibility(View.GONE);
                                File file = new File(model.getFolder(),
                                        urlName);
                                String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
                                if (file.exists()) {
                                    uri = "file:///" + file.getAbsolutePath();
                                } else if (LanguageUtil.isChina()) {
                                    uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
                                }
                                final String uriString = uri;
                                Preference.getPreferences().saveAqSwichUri(uri);
                                Preference.getPreferences().saveAqSwichSettingUri("file:///"+model.getFolder()+"/setting.html");
                                Handler handler = new Handler(Looper
                                        .getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        webView.loadUrl(uriString);
//                                        wwebView = Engine.createWebView(webView, uriString, htmlID);
//                                        wwebView.getContainer().getContainerRootView().removeAllViews();
//                                        ViewGroup viewGroup=wwebView.getContainer().getContainerRootView();
//                                        wwebView.onRootViewGlobalLayout(viewGroup, "", "");
                                    }
                                });
                            }

                            @Override
                            public void onGetPluginFailed(final String hint) {
                                if((!Preference.getPreferences().getAqSwichUri().equals("noUri"))){
                                    return;
                                }
                                if (hint != null && hint.length() > 0) {
                                    Handler handler = new Handler(Looper
                                            .getMainLooper());
                                    handler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Toast.makeText(mContext, hint,
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });
            }
        }).start();
    }

    @Override
    protected List<MoreMenuPopupWindow.MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
        List<MoreMenuPopupWindow.MenuItem> mDeviceDetailsMenuItems  = super.getDeviceMenuItems(manager);

        MoreMenuPopupWindow.MenuItem deviceDetialsSettingItem = new MoreMenuPopupWindow.MenuItem(mContext) {

            @Override
            public void initSystemState() {
                titleTextView
                        .setText(mContext.getResources().getString(R.string.set_titel));
                iconImageView
                        .setImageResource(R.drawable.device_setting_more_setting);
            }

            @Override
            public void doSomething() {
                String setttingFilePath=getSettingFilePath();
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, devID);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, gwID);
                IntentUtil.startHtml5PlusActivity(mContext,setttingFilePath,
                        mContext.getResources().getString(R.string.set_titel),
                        getDefaultDeviceName());
                manager.dismiss();
            }
        };

        if(isDeviceOnLine()){
            mDeviceDetailsMenuItems.add(deviceDetialsSettingItem);
        }
        return mDeviceDetailsMenuItems;
    }

    private String getSettingFilePath(){
        String url_setting="";
        if(isUsePlugin){
            url_setting=Preference.getPreferences().getAqSwichSettingUri();
        }else {
            url_setting=HTML_BASEURI+"setting.html";
        }
        return url_setting;
    }

    /**
     * 设备的名字
     */
    @Override
    public String getDefaultDeviceName() {
     //   String defaultName = super.getDefaultDeviceName();
        String defaultName = getDeviceInfo().getDevEPInfo().getEpName();
        if (isNull(defaultName)) {
            defaultName = getString(R.string.device_name_sensory_switch);
        }
        return defaultName;
    }

    @Override
    public void initViewStatus() {
        super.initViewStatus();
    }

    @Override
    public void refreshDevice() {
        super.refreshDevice();
        handleEpData();
    }

    private void  handleEpData(){
        if(!isNull(epData)){
            Log.i(TAG,"epData:"+epData+"--"+epData.length());
            if(epData.length() == 18){
                mDefenseState = epData.substring(6 , 8);
                mAlarmableState = epData.substring(12 , 14);
            }
            if(epData.length() == 6){
                if(isSameAs(epData.substring(2 , 4), "03")){ //设防模式数据
                    mDefenseState = epData.substring(4 , 6);
                }else if(isSameAs(epData.substring(2 , 4), "05")){ //红外报警数据
                    mAlarmableState = epData.substring(4 , 6);
                }
            }


        }
    }

    //设置不作为执行任务
    @Override
    public boolean isAutoControl(boolean isSimple) {
        return false;
    }


    @Override
    public boolean isLinkControlCondition() {
        return false;
    }

    //设防 撤防
    @Override
    public boolean isDefenseSetup() {
        return isSameAs(STATE_SETUP, mDefenseState);
    }

    @Override
    public boolean isDefenseUnSetup() {
        return !isDefenseSetup();
    }

    @Override
    public String getDefenseSetupCmd() {
        return DEFENSE_SETUP_SEND_CMD;
    }

    @Override
    public boolean isLongDefenSetup() {
        return false;
    }

    @Override
    public String getDefenseUnSetupCmd() {
        return DEFENSE_UNSETUP_SEND_CMD;
    }

    @Override
    public String getDefenseSetupProtocol() {
        return getDefenseSetupCmd();
    }

    @Override
    public String getDefenseUnSetupProtocol() {
        return getDefenseUnSetupCmd();
    }

    @Override
    public Drawable getStateSmallIcon() {
        return getDrawable(DRAWABLE_ICON);
    }

    @Override
    public void controlDevice(String ep, String epType, String epData) {
        controlDeviceWidthEpData(ep, epType, epData);
    }

    /**
     * 是否报警中
     */
    @Override
    public boolean isAlarming() {
        String epMsg = getDeviceInfo().getDevEPInfo().getEpMsg();
        if(!isNull(epMsg) && isSameAs(epMsg , "N")){
            return false;
        }
        return isDefenseSetup() && isSameAs(mAlarmableState ,STATE_SETUP);
    }
    /**
     * 是否正常状态
     */
    @Override
    public boolean isNormal() {
        return !isAlarming();
    }

    @Override
    public CharSequence parseAlarmProtocol(String epData) {
        StringBuilder sb = new StringBuilder();
        sb.append(DeviceTool.getDeviceAlarmAreaName(this));
        sb.append(DeviceTool.getDeviceShowName(this));
        if(LanguageUtil.isChina() || LanguageUtil.isTaiWan()){
            sb.append(mContext.getString(R.string.home_device_alarm_default_voice_detect));
        }else{
            sb.append(" "+ mContext.getString(R.string.home_device_alarm_default_voice_detect) + " ");
        }
        sb.append(mContext.getString(R.string.home_device_alarm_type_02_voice));
        return sb.toString();
    }

    @Override
    public CharSequence parseDataWithProtocol(String epData) {
        SpannableStringBuilder ssb = new SpannableStringBuilder();
        ssb.append(mContext.getString(R.string.device_state_alarm));
        return ssb;
    }

    @Override
    public String getDevWebViewCallBackId(String cmd,String ep,String mode,String devID){
        String callbackID="";
        if(StringUtil.isNullOrEmpty(mode)){
            mode=CmdUtil.MODE_SEARCH_TIME;
        }
        //isMustEp 是否包含ep字段，当前只有21命令及At设备需要
        if(StringUtil.equals(cmd,ConstUtil.CMD_RETN_DATA)){
            callbackID="12-0-"+devID;
        }else {
            boolean isMustEp=StringUtil.equals(cmd, ConstUtil.CMD_SET_DEV)&&
                    StringUtil.equals(mode,CmdUtil.MODE_SEARCH_TIME);
            if(!isMustEp){
                callbackID=cmd+"-"+mode+"-"+devID;
            }else {
                callbackID=cmd+"-"+ep+"-"+mode+"-"+devID;
            }
        }
        return callbackID;
    }

    //设备列表图标显示
    @Override
    public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item, LayoutInflater inflater) {
        return getDeviceAqShortCutView(item, inflater);
    }

    protected DeviceShortCutControlItem getDeviceAqShortCutView(
            DeviceShortCutControlItem item, LayoutInflater inflater) {
        if (item == null) {
            item = new DeviceAqShortCutControlItem(inflater.getContext());
        }
        item.setWulianDevice(this);
        return item;
    }
    //设备列表图标
    private class DeviceAqShortCutControlItem extends DeviceShortCutControlItem{

        private LinearLayout controlableLineLayout;
        private ImageView setupImageView;
        private ImageView unSetupImageView;
        private View.OnClickListener cliclListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if(v == setupImageView){
                    clickSetup();
                }else if(v == unSetupImageView){
                    clickUnsetup();
                }
            }

        };

        public DeviceAqShortCutControlItem(Context context) {
            super(context);
            controlableLineLayout = (LinearLayout)inflater.inflate(R.layout.device_short_cut_control_defenseable, null);
            setupImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_defense_setup_iv);
            unSetupImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_defense_unsetup_iv);
            controlLineLayout.addView(controlableLineLayout);
        }

        protected void clickUnsetup() {
            DeviceEPInfo info = mDevice.getDeviceInfo().getDevEPInfo();
            if(info != null){
                controlDevice(info.getEp() , info.getEpType(), getDefenseUnSetupProtocol());
            }
        }

        protected void clickSetup() {
            DeviceEPInfo info = mDevice.getDeviceInfo().getDevEPInfo();
            if(info != null){
                 controlDevice(info.getEp() , info.getEpType(), getDefenseSetupProtocol());
            }
        }

        @Override
        public void setWulianDevice(WulianDevice device) {
            super.setWulianDevice(device);
            if(device instanceof Defenseable){
                Map<String,DeviceEPInfo> infoMap = device.getDeviceInfo().getDeviceEPInfoMap();
                if(infoMap == null)
                    return ;

                if(isDefenseUnSetup()){
                    setupImageView.setSelected(false);
                    unSetupImageView.setSelected(true);
                    setupImageView.setOnClickListener(cliclListener);
                }
                if(isDefenseSetup()){
                    setupImageView.setSelected(true);
                    unSetupImageView.setSelected(false);
                    unSetupImageView.setOnClickListener(cliclListener);
                }
            }
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        if(webView != null){
            webView.reload();
        }
    }

}
