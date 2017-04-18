package cc.wulian.app.model.device.impls.controlable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.util.ArrayMap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import cc.wulian.app.model.device.EpDataAnalysis.WifiEpdataAnalysis;
import cc.wulian.app.model.device.EpDataAnalysis.WifiEpdataAnalysis_Oa;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperSelectControlDeviceDataFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.TargetConfigure;

/**
 * Created by yuxiaoxuan on 2017/1/12.
 * 油烟机
 */
@DeviceClassify(devTypes = {"Oa"}, category = Category.C_CONTROL)
public class WL_Oa_Rangehood extends ControlableDeviceImpl implements Alarmable {

    public WL_Oa_Rangehood(Context context, String type) {
        super(context, type);
    }
    private String pluginName="Device_Oa_Rangehood.zip";
    private H5PlusWebView webView;
    private TextView textView;
    private int iconDefualt=cc.wulian.app.model.device.R.drawable.device_thermost_open;
    //主页
    private  String controlPagePath="file:///android_asset/Device_Oa_Rangehood/Lampblack.html";
    //管家
    private  String housekeeperPagePath="file:///android_asset/Device_Oa_Rangehood/LampblackForButler.html";
    //设置
    private  String settingPagePath="file:///android_asset/Device_Oa_Rangehood/LampblackSetting.html";
    //设备信息
    private  String deviceInfoPagePath="file:///android_asset/Device_Oa_Rangehood/LampblackDeviceInfo.html";
    private String tag="WL_Oa_Rangehood";
    private static boolean isUsePlugin=true;

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
            getPlugin("Lampblack.html","Device_Oa_Rangehood",mContext);
        }else{
            webView.loadUrl(controlPagePath);
        }
    }

    /*设置设备名称*/

    @Override
    public String getDefaultDeviceName() {
        String defaultName = super.getDefaultDeviceName();
        if (StringUtil.isNullOrEmpty(defaultName)) {
            defaultName =getString(cc.wulian.app.model.device.R.string.device_Oa_Rangehood);
        }
        return defaultName;
    }

    /*设备列表右侧快速控制区域 控制页面*/

    @Override
    public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item, LayoutInflater inflater) {
        if (item == null) {
            item = new WL_Oa_Rangehood.shortCutItemForDevice(inflater.getContext());
        }
        item.setWulianDevice(this);
        return super.onCreateShortCutView(item, inflater);
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
    public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(DeviceShortCutSelectDataItem item, LayoutInflater inflater, AutoActionInfo autoActionInfo) {
        if(item == null){
            item = new WL_Oa_Rangehood.ShortCutControlableDeviceSelectDataItem(inflater.getContext());
        }
        item.setWulianDeviceAndSelectData(this, autoActionInfo);
        return item;
    }

    /*管家快捷控制区域*/
    private class ShortCutControlableDeviceSelectDataItem extends  DeviceShortCutSelectDataItem
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
    }

    /*管家主功能区，以H5形式实现*/

    @Override
    public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(LayoutInflater inflater, AutoActionInfo autoActionInfo) {
        DialogOrActivityHolder holder=new DialogOrActivityHolder();
        holder.setShowDialog(false);
        if(HouseKeeperSelectControlDeviceDataFragment.isShowHouseKeeperSelectControlDeviceDataView){
            HouseKeeperSelectControlDeviceDataFragment.isShowHouseKeeperSelectControlDeviceDataView=false;
            View contentView=inflater.inflate(R.layout.device_comm_webpage,null);
            webView=(H5PlusWebView) contentView.findViewById(R.id.device_webview);
            textView= (TextView) contentView.findViewById(R.id.search_tv);
            textView.setVisibility(View.GONE);
            if(autoActionInfo.getEpData()!=null&&autoActionInfo.getEpData().equals("")){
                SmarthomeFeatureImpl.setData("kBulterEPData", "");
            }else if(autoActionInfo.getEpData()!=null){
                SmarthomeFeatureImpl.setData("kBulterEPData", autoActionInfo.getEpData());
            }
//            webView.setWebviewId("OZ_CentralAirBulter");
            SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
            SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
            if(isUsePlugin){
                getPlugin("LampblackForButler.html","Device_Oa_Rangehood", DeviceSettingActivity.instance);
            }else{
                Handler handler = new Handler(Looper
                        .getMainLooper());
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        webView.loadUrl(housekeeperPagePath);
                    }
                });
            }
            holder.setContentView(contentView);
            HouseKeeperSelectControlDeviceDataFragment.setActionBarClickRightListener(new HouseKeeperSelectControlDeviceDataFragment.ActionBarClickRightListener() {

                @Override
                public void doSomething(AutoActionInfo autoActionInfo) {
                    String epData=SmarthomeFeatureImpl.getData("kRangehoodBulterEpData", "");
                    autoActionInfo.setEpData(epData);
                    SmarthomeFeatureImpl.setData("kRangehoodBulterEpData", epData);
                }
            });
        }
        return holder;
    }

    /*增加更多菜单项*/

    @Override
    protected List<MoreMenuPopupWindow.MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
        List<MoreMenuPopupWindow.MenuItem> items = super.getDeviceMenuItems(manager);
        MoreMenuPopupWindow.MenuItem deviceSetting = new MoreMenuPopupWindow.MenuItem(mContext) {
            @Override
            public void initSystemState() {
                titleTextView.setText(mContext.getString(R.string.common_setting));
                iconImageView.setImageResource(R.drawable.device_setting_more_setting);
            }

            @Override
            public void doSomething() {
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, WL_Oa_Rangehood.this.devID);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, WL_Oa_Rangehood.this.gwID);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EP, WL_Oa_Rangehood.this.ep);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EPDATA,WL_Oa_Rangehood.this.epData);
                if(isUsePlugin){
                    settingPagePath=Preference.getPreferences().getHxOaSetting();
                }
                IntentUtil.startHtml5PlusActivity(mContext,
                        settingPagePath,
                        getDefaultDeviceName(),
                        mContext.getString(R.string.common_setting));
                manager.dismiss();
            }
        };
        MoreMenuPopupWindow.MenuItem deviceInfo = new MoreMenuPopupWindow.MenuItem(mContext) {
            @Override
            public void initSystemState() {
                titleTextView.setText(mContext.getString(R.string.setting_device_desc));
                iconImageView.setImageResource(R.drawable.device_setting_more_setting);
            }

            @Override
            public void doSomething() {
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, WL_Oa_Rangehood.this.devID);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, WL_Oa_Rangehood.this.gwID);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EP, WL_Oa_Rangehood.this.ep);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EPDATA,WL_Oa_Rangehood.this.epData);
                if(isUsePlugin){
                    deviceInfoPagePath=Preference.getPreferences().getHxOaDeviceInfo();
                }
                IntentUtil.startHtml5PlusActivity(mContext,
                        deviceInfoPagePath,
                        getDefaultDeviceName(),
                        mContext.getString(R.string.setting_device_desc));
                manager.dismiss();
            }
        };

        if (isDeviceOnLine()) {
            items.add(deviceSetting);
            items.add(deviceInfo);
        }
        return items;
    }

    @Override
    public void registerEPDataToHTML(H5PlusWebView pWebview, String callBackId,String cmd) {
        this.pWebview = pWebview;
        if(mapCallbackID==null){
            mapCallbackID=new ArrayMap<>();
        }
        mapCallbackID.put(cmd,callBackId);
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
                                String settingPage="file:///"+model.getFolder()+"/LampblackSetting.html";
                                String deviceInfoPage="file:///"+model.getFolder()+"/LampblackDeviceInfo.html";
                                Log.d(WL_Oa_Rangehood.class.getName(), "onGetPluginSuccess: settingPage="+settingPage+" deviceInfoPage="+deviceInfoPage);
                                Preference.getPreferences().saveHxOaSetting(settingPage);
                                Preference.getPreferences().saveHxOaDeviceInfo(deviceInfoPage);
                                Handler handler = new Handler(Looper.getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        webView.setWebviewId(htmlID);
                                        webView.loadUrl(uriString);
                                    }
                                });
                            }

                            @Override
                            public void onGetPluginFailed(final String hint) {
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


    /*下面是处理报警信息的*/
    WifiEpdataAnalysis epdataAnalysis=null;
    private void initEpdataAnalysis(){
        if(epdataAnalysis==null){
            epdataAnalysis=new WifiEpdataAnalysis_Oa(this.getContext());
            epdataAnalysis.setMsgPrefix(this.getDeviceRoomID(),this.getDeviceName());
        }
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
        if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
            Log.d(tag, "isAlarming() epData="+epData);
        }
        initEpdataAnalysis();
        getAlarmInfo(this.epData,true);
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
        return null;
    }

    @Override
    public String getNormalProtocol() {
        return null;
    }

    @Override
    public String getAlarmString() {
        initEpdataAnalysis();
        String msg=getAlarmInfo(this.epData,true);
        return msg;
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
    public CharSequence  parseAlarmProtocol(String epData){
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

    @Override
    public boolean isHouseKeeperSelectControlDeviceActionBarUseable() {
        return true;
    }


}
