package cc.wulian.app.model.device.impls.controlable;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;
import android.support.v4.util.ArrayMap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.yuantuo.customview.ui.WLToast;

import java.io.File;
import java.util.List;

import cc.wulian.app.model.device.EpDataAnalysis.WifiEpdataAnalysis;
import cc.wulian.app.model.device.EpDataAnalysis.WifiEpdataAnalysis_Oc;
import cc.wulian.app.model.device.EpDataAnalysis.WifiEpdataAnalysis_Od;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.impls.alarmable.AlarmableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperSelectControlDeviceDataFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

/**
 * Created by yuxiaoxuan on 2017/1/12.
 * 燃气灶
 */
@DeviceClassify(devTypes = {"Od"}, category = Category.C_CONTROL)
public class WL_Od_GasStoves extends AlarmableDeviceImpl {
    public WL_Od_GasStoves(Context context, String type) {
        super(context, type);
    }
    private String pluginName="Device_Od_GasStove.zip";
    private H5PlusWebView webView;
    private TextView textView;
    private int iconDefualt=cc.wulian.app.model.device.R.drawable.device_thermost_open;
    private static boolean isUsePlugin=true;
    //主页
    private static String controlPagePath="file:///android_asset/Device_Od_GasStove/gasStove.html";
    //管家
//    private static String housekeeperPagePath="file:///android_asset/Device_Od_GasStoves/bulterIndex.html";
    //设备信息
    private String deviceInfoPagePath="file:///android_asset/Device_Od_GasStove/gasStoveDeviceInfo.html";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
        View view=inflater.inflate(R.layout.device_comm_webpage,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveState) {
        super.onViewCreated(view, saveState);
        webView = (H5PlusWebView) view.findViewById(R.id.device_webview);
        textView= (TextView) view.findViewById(R.id.search_tv);
        textView.setVisibility(View.GONE);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
        if(isUsePlugin){
            getPlugin("gasStove.html","Device_Od_GasStove",mContext);
        }else {
            webView.loadUrl(controlPagePath);
        }


    }
    /*设置设备图标*/
   /* @Override
    public Drawable getDefaultStateSmallIcon() {
        Drawable icon = getResources().getDrawable(R.drawable.device_od_gasstoves);
        return icon;
    }*/

    /*设置设备名称*/

    @Override
    public String getDefaultDeviceName() {
        String defaultName = super.getDefaultDeviceName();
        if (StringUtil.isNullOrEmpty(defaultName)) {
            defaultName =getString(cc.wulian.app.model.device.R.string.device_Od_GasStoves);
        }
        return defaultName;
    }

    /*设备列表右侧快速控制区域 控制页面*/

    @Override
    public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item, LayoutInflater inflater) {
        if (item == null) {
            item = new WL_Od_GasStoves.shortCutItemForDevice(inflater.getContext());
        }
        item.setWulianDevice(this);
        return super.onCreateShortCutView(item, inflater);
    }
    /*下面是处理报警信息的*/
    WifiEpdataAnalysis epdataAnalysis=null;
    private void initEpdataAnalysis() {
        if (epdataAnalysis == null) {
            epdataAnalysis = new WifiEpdataAnalysis_Od(this.getContext());
            epdataAnalysis.setMsgPrefix(this.getDeviceRoomID(), this.getDeviceName());
        }
//        if (webView != null) {
//            Handler mainHandler = new Handler(Looper.getMainLooper());
//            mainHandler.post(new Runnable() {
//                @TargetApi(Build.VERSION_CODES.KITKAT)
//                @Override
//                public void run() {
//                    String log = epdataAnalysis.printLog();
//                    String function = String.format("javascript:showLog('%s')", WL_Od_GasStoves.this.epData+" "+log);
//                    webView.evaluateJavascript(function, new ValueCallback<String>() {
//                        @Override
//                        public void onReceiveValue(String value) {
//
//                        }
//                    });
//                }
//
//            });
//        }

    }
    private String getAlarmInfo(String epData,boolean isFull){
        epdataAnalysis.setEpdata(epData);
        epdataAnalysis.AnalysisEpdata();
        if(epdataAnalysis.isHaveAlarming()){
            String msg="";
            if(isFull){
                msg=epdataAnalysis.getAlarmingFullMsg();
            }else{
                msg=epdataAnalysis.getAlramingBriefMsg();
            }
            return msg;
        }else{
            return null;
        }
    }
    @Override
    public boolean isAlarming() {
        initEpdataAnalysis();
        boolean isalarming=epdataAnalysis.isHaveAlarming();
        return isalarming;
    }

    @Override
    public boolean isNormal() {
        return false;
    }

    @Override
    public String getCancleAlarmProtocol() {
        return null;
    }

    @Override
    public String getAlarmProtocol() {
        return "AAAA000982000202010401";
    }

    @Override
    public String getNormalProtocol() {
        return null;
    }

    @Override
    public String getAlarmString() {
//        initEpdataAnalysis();
//        String msg=getAlarmInfo(this.epData,true);
//        return msg;
        return "关火";
    }

    @Override
    public String getNormalString() {
        return null;
    }

    @Override
    public boolean isDestory() {
        return false;
    }

    @Override
    public boolean isLowPower() {
        return false;
    }
    /*首页报警列表*/
    @Override
    public CharSequence parseAlarmProtocol(String epData) {
        initEpdataAnalysis();
        String msg=getAlarmInfo(epData,false);
        return msg;
    }
    /*报警页面信息*/
    @Override
    public CharSequence parseDataWithProtocol(String epData) {
        initEpdataAnalysis();
        String msg=getAlarmInfo(epData,false);
        return msg;
    }
    /*设备控制区域的快捷键*/
    private class shortCutItemForDevice extends DeviceShortCutControlItem{

        private LinearLayout controlableLineLayout = null;
        private ImageView openImageView = null;
        private ImageView stopImageView = null;
        private ImageView closeImageView = null;
        public shortCutItemForDevice(Context context) {
            super(context);
            controlableLineLayout = (LinearLayout) inflater.inflate(R.layout.device_short_cut_control_controlable, null);
            openImageView = (ImageView) controlableLineLayout.findViewById(R.id.device_short_cut_control_open_iv);
            stopImageView = (ImageView) controlableLineLayout.findViewById(R.id.device_short_cut_control_stop_iv);
            closeImageView = (ImageView) controlableLineLayout.findViewById(R.id.device_short_cut_control_close_iv);
            //三个按键 开、关、停都隐藏
            openImageView.setVisibility(View.GONE);
            stopImageView.setVisibility(View.GONE);
            closeImageView.setVisibility(View.GONE);
        }
    }

    /*管家功能设置区域*/
    @Override
    public boolean isAutoControl(boolean isSimple) {
        return false;
    }
   /* @Override
    public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(DeviceShortCutSelectDataItem item, LayoutInflater inflater, AutoActionInfo autoActionInfo) {
        if(item == null){
            item = new WL_Od_GasStoves.ShortCutControlableDeviceSelectDataItem(inflater.getContext());
        }
        item.setWulianDeviceAndSelectData(this, autoActionInfo);
        return item;
    }*/

    /*管家快捷控制区域*/
    /*private class ShortCutControlableDeviceSelectDataItem extends  DeviceShortCutSelectDataItem
    {
        private LinearLayout controlableLineLayout= null;
        private ImageView openImageView;
        private ImageView openImageViewColor;
        private ImageView closeImageView;
        private ImageView stopImageView;
        public ShortCutControlableDeviceSelectDataItem(Context context) {
            super(context);
            controlableLineLayout = (LinearLayout)inflater.inflate(R.layout.device_short_cut_control_controlable, null);
            controlableLineLayout.findViewById(R.id.open_imageview_color_layout).setVisibility(View.GONE);
            openImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_open_iv);
            stopImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_stop_iv);
            closeImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_close_iv);
            openImageView.setVisibility(View.GONE);
            stopImageView.setVisibility(View.GONE);
            closeImageView.setVisibility(View.GONE);
            controlLineLayout.addView(controlableLineLayout);
        }
    }*/

    /*管家主功能区，以H5形式实现*/

//    @Override
//    public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(LayoutInflater inflater, AutoActionInfo autoActionInfo) {
//        DialogOrActivityHolder holder=new DialogOrActivityHolder();
//        holder.setShowDialog(false);
//        if(HouseKeeperSelectControlDeviceDataFragment.isShowHouseKeeperSelectControlDeviceDataView){
//            HouseKeeperSelectControlDeviceDataFragment.isShowHouseKeeperSelectControlDeviceDataView=false;
//            View contentView=inflater.inflate(R.layout.device_comm_webpage,null);
//            webView=(H5PlusWebView) contentView.findViewById(R.id.device_webview);
//            textView= (TextView) contentView.findViewById(R.id.search_tv);
//            textView.setVisibility(View.GONE);
//            if(autoActionInfo.getEpData()!=null&&autoActionInfo.getEpData().equals("")){
//                SmarthomeFeatureImpl.setData("kMideaBulterEpData", "");
//            }else if(autoActionInfo.getEpData()!=null){
//                SmarthomeFeatureImpl.setData("kMideaBulterEpData", autoActionInfo.getEpData());
//            }
////            webView.setWebviewId("OZ_CentralAirBulter");
//            SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
//            SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
//            Engine.bindWebviewToContainer((H5PlusWebViewContainer)this.getCurrentFragment(), webView);
//            /*if (!Preference.getPreferences().getBackgroundMusicHtmlUri().equals("noUri")) {
//                textView.setVisibility(View.GONE);
//                webView.loadUrl(Preference.getPreferences().getBackgroundMusicHtmlUri()+"/bulterIndex.html");
//                getPlugin("bulterIndex.html","OZ_CentralAirBulter", DeviceSettingActivity.instance);
//            }else{
//                getPlugin("bulterIndex.html","OZ_CentralAirBulter",DeviceSettingActivity.instance);
//            }*/
//
//            Handler handler = new Handler(Looper
//                    .getMainLooper());
//            handler.post(new Runnable() {
//                @Override
//                public void run() {
//                    webView.loadUrl(housekeeperPagePath);
//                }
//            });
//            holder.setContentView(contentView);
//            HouseKeeperSelectControlDeviceDataFragment.setActionBarClickRightListener(new HouseKeeperSelectControlDeviceDataFragment.ActionBarClickRightListener() {
//
//                @Override
//                public void doSomething(AutoActionInfo autoActionInfo) {
//                    String epData=SmarthomeFeatureImpl.getData("kMideaBulterEpData", "");
//                    autoActionInfo.setEpData(epData);
//                    SmarthomeFeatureImpl.setData("kMideaBulterEpData", epData);
//                }
//            });
//        }
//        return holder;
//    }
    //=AAAA000982000202010401 打钩的；=AAAA000982000202010401$没有打钩的
    @Override
    public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(
            LayoutInflater inflater, final AutoConditionInfo autoConditionInfo,final boolean isTriggerCondition) {
        DialogOrActivityHolder holder = new DialogOrActivityHolder();
        View contentView =  inflater.inflate(R.layout.task_manager_trigger_setup_device_select, null);
        RadioGroup radiogroup = (RadioGroup) contentView.findViewById(R.id.task_manager_select_radiogroup);
        TextView noticeRemind = (TextView) contentView.findViewById(R.id.task_manager_device_notice_remind_textview);
        final RadioButton alarmbutton = (RadioButton) contentView.findViewById(R.id.task_manager_select_alarm_status);
        final RadioButton normalbutton = (RadioButton) contentView.findViewById(R.id.task_manager_select_normal_status);
        final LinearLayout noticeLayout = (LinearLayout) contentView.findViewById(R.id.task_manager_device_select_notice_layout);
        final ImageView noticeImageView = (ImageView) contentView.findViewById(R.id.task_manager_device_select_img);
        final TextView task_manager_msg=(TextView)contentView.findViewById(R.id.task_manager_msg);
        noticeRemind.setText(mContext.getResources().getString(R.string.house_rule_add_new_condition_device_when)+ " " + DeviceTool.getDeviceShowName(this)
                +  " " +mContext.getResources().getString(R.string.home_device_alarm_default_voice_detect));
//        alarmbutton.setText(getAlarmString());
//        normalbutton.setText(getNormalString());
        alarmbutton.setChecked(false);
        alarmbutton.setVisibility(View.GONE);
        normalbutton.setVisibility(View.GONE);
        task_manager_msg.setVisibility(View.VISIBLE);
        task_manager_msg.setText("关火");
        if(isTriggerCondition){
            noticeLayout.setVisibility(View.VISIBLE);
            noticeImageView.setSelected(true);
        }else{
            noticeLayout.setVisibility(View.GONE);
        }
        autoConditionInfo.setExp("=" + getAlarmProtocol());
//        CompoundButton.OnCheckedChangeListener checkListener = new CompoundButton.OnCheckedChangeListener() {
//
//            @Override
//            public void onCheckedChanged(CompoundButton arg0, boolean isChecked) {
//                if(isChecked){
//                    String selectstatus = "";
//                    if(arg0 == alarmbutton){
//                        if(isTriggerCondition){
//                            noticeLayout.setVisibility(View.VISIBLE);
//                        }
//                        selectstatus = "=" + getAlarmProtocol();
//                    }else{
//                        if(isTriggerCondition){
//                            noticeLayout.setVisibility(View.GONE);
//                        }
//                        selectstatus = "=" + getNormalProtocol();
//                    }
//                    autoConditionInfo.setExp(selectstatus);
//                }
//            }
//        };
//        alarmbutton.setOnCheckedChangeListener(checkListener);
//        normalbutton.setOnCheckedChangeListener(checkListener);
        noticeImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String selectstatus = "";
                if(noticeImageView.isSelected()){
                    selectstatus = "=" + getAlarmProtocol() + "$";
                    noticeImageView.setSelected(false);
                }else{
                    selectstatus = "=" + getAlarmProtocol();
                    noticeImageView.setSelected(true);
                }
                autoConditionInfo.setExp(selectstatus);
            }
        });
        holder.setShowDialog(true);
        holder.setContentView(contentView);
        holder.setDialogTitle(mContext.getResources().getString(R.string.house_rule_add_new_condition_select_alarm));
        return holder;
    }
    private void getPlugin(final String urlName, final String htmlID, final Context context) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PluginsManager pm = PluginsManager.getInstance();
                pm.getHtmlPlugin(context, pluginName,
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

                                Preference.getPreferences().saveHxOdDeviceInfo("file:///"+model.getFolder()+"/gasStoveDeviceInfo.html");
                                Handler handler = new Handler(Looper
                                        .getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
//                                        webView.loadUrl(uriString);*/
                                        webView.setWebviewId(htmlID);
                                        webView.loadUrl(uriString);
                                    }
                                });
                            }

                            @Override
                            public void onGetPluginFailed(final String hint) {
                                if((!Preference.getPreferences().get30ASwichUri().equals("noUri"))){
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
    public void registerEPDataToHTML(H5PlusWebView pWebview, String callBackId,String cmd) {
        this.pWebview = pWebview;
        if(mapCallbackID==null){
            mapCallbackID=new ArrayMap<>();
        }
        mapCallbackID.put(cmd,callBackId);
    }
    @Override
    public String getDevWebViewCallBackId(String cmd, String ep, String mode, String devID) {
        String callbackID = "";
        if (StringUtil.isNullOrEmpty(mode)) {
            mode = "6";
        }
        if (cmd.equals("13")) {
            callbackID = "12-0-" + devID;
        } else {
            callbackID = cmd + "-" + mode + "-" + devID;
        }
        return callbackID;
    }
    /*增加更多菜单项*/

    @Override
    protected List<MoreMenuPopupWindow.MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
        List<MoreMenuPopupWindow.MenuItem> items = super.getDeviceMenuItems(manager);
        MoreMenuPopupWindow.MenuItem deviceInfo = new MoreMenuPopupWindow.MenuItem(mContext) {
            @Override
            public void initSystemState() {
                titleTextView.setText(mContext.getString(R.string.setting_device_desc));
                iconImageView.setImageResource(R.drawable.device_setting_more_setting);
            }

            @Override
            public void doSomething() {
//                String setttingFilePath = Preference.getPreferences().getAuCurtainSettingUri();
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, WL_Od_GasStoves.this.devID);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, WL_Od_GasStoves.this.gwID);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EP, "14");
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EPDATA,WL_Od_GasStoves.this.epData);
                if(isUsePlugin){
                    deviceInfoPagePath=Preference.getPreferences().getHxOdDeivceInfo();
                }
                IntentUtil.startHtml5PlusActivity(mContext,
                        deviceInfoPagePath,
                        getDefaultDeviceName(),
                        mContext.getString(R.string.setting_device_desc));
                manager.dismiss();
            }
        };

        if (isDeviceOnLine()) {
            items.add(deviceInfo);
        }
        return items;
    }
}
