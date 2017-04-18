package cc.wulian.app.model.device.impls.controlable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.icam.utils.StringUtil;
import com.yuantuo.customview.ui.WLDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.TargetConfigure;

/**
 * Created by caoww on 2016/12/21.
 * 10A计量插座
 */
@DeviceClassify(devTypes = {"Ak"}, category = Category.C_CONTROL)
public class WL_Ak_10Switch extends ControlableDeviceImpl implements Alarmable {
    private String TAG = WL_Ak_10Switch.class.getName();

    private String pluginName = "10A_Ak.zip";
    private H5PlusWebView webView;
    private TextView textView;
    protected String ep;
    protected String epType;
    protected String epData;
    protected String epStatus;
    private boolean isOpen = false;
    private boolean isUsePlugin = true;//默认是true，调试时可以改成false
    protected static int SMALL_OPEN = cc.wulian.app.model.device.R.drawable.little_ak_0100_off;
    protected static int SMALL_CLOSE = cc.wulian.app.model.device.R.drawable.little_ak_0100_on;
    protected static int SMALL_ENABLE = cc.wulian.app.model.device.R.drawable.little_ak_0100_on;//设备开关的图标
    private AutoActionInfo autoActionInfo;
    private final String cmdSend_Open = "11";
    private final String cmdSend_Close = "10";
    private String mOnOff;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
        View view = inflater.inflate(R.layout.device_ak10_switch, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveState) {
        super.onViewCreated(view, saveState);
        webView = (H5PlusWebView) view.findViewById(R.id.device_10ak_webview);
        textView = (TextView) view.findViewById(R.id.search_ak10_tv);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
        Engine.bindWebviewToContainer((H5PlusWebViewContainer) this.getCurrentFragment(), webView);
        if (isUsePlugin) {
            String strUri = Preference.getPreferences().get10ASwichUri();
            if (!"noUri".equals(strUri)) {
                textView.setVisibility(View.GONE);
                webView.loadUrl(Preference.getPreferences().get10ASwichUri());
                getPlugin("deviceMenu.html","deviceMenuIndex");
            }else{
                getPlugin("deviceMenu.html","deviceMenuIndex");
            }
        } else {
            textView.setVisibility(View.GONE);
            webView.loadUrl("file:///android_asset/10A_Ak/deviceMenu.html");
        }
    }

    /**
     * 设置设备的图标
     *
     * @return
     */
    @Override
    public Drawable getDefaultStateSmallIcon() {
        Drawable icon = null;
        if (this.isDeviceOnLine()) {
            if (isOpened()) {
                icon = getResources().getDrawable(SMALL_OPEN);
            } else {
                icon = getResources().getDrawable(SMALL_CLOSE);
            }
        } else {
            icon = getResources().getDrawable(SMALL_ENABLE);
        }

        return icon;
    }

    /**
     * 设备的名字
     *
     * @return
     */
    @Override
    public String getDefaultDeviceName() {
        String defaultName = super.getDefaultDeviceName();
        if (StringUtil.isNullOrEmpty(defaultName)) {
            defaultName = getString(R.string.Ak);
        }
        return defaultName;
    }

    public WL_Ak_10Switch(Context context, String type) {
        super(context, type);
    }

    @Override
    protected List<MoreMenuPopupWindow.MenuItem> getDeviceMenuItems(final MoreMenuPopupWindow manager) {
        List<MoreMenuPopupWindow.MenuItem> items = super.getDeviceMenuItems(manager);
        MoreMenuPopupWindow.MenuItem menuItem = new MoreMenuPopupWindow.MenuItem(mContext) {
            @Override
            public void initSystemState() {
                titleTextView
                        .setText(mContext
                                .getString(R.string.common_setting));
                iconImageView
                        .setImageResource(R.drawable.device_setting_more_setting);
            }

            @Override
            public void doSomething() {
                String setttingFilePath = getSettingFilePath();
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, WL_Ak_10Switch.this.devID);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, WL_Ak_10Switch.this.gwID);
                IntentUtil.startHtml5PlusActivity(mContext,
                        setttingFilePath,
                        getDefaultDeviceName(),
                        mContext.getString(R.string.common_setting));
                manager.dismiss();
            }

        };
        if (isDeviceOnLine()) {
            items.add(menuItem);
        }
        return items;
    }

    /**
     * 获取设置的html
     * @return
     */
    private String getSettingFilePath() {
        String url_setting = "";
        if (isUsePlugin) {
            url_setting = Preference.getPreferences().get10ASwichSettingUri();
        } else {
            url_setting = "file:///android_asset/10A_Ak/setting.html";
        }
        return url_setting;
    }


    /**
     * 下载插件，并保存到本地，下次进设备，直接加载本地的（未清空数据）
     */
    private void getPlugin(final String urlName, final String HtmlId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                PluginsManager pm = PluginsManager.getInstance();
                pm.getHtmlPlugin(mContext, pluginName,
                        new PluginsManager.PluginsManagerCallback() {

                            @Override
                            public void onGetPluginSuccess(PluginModel model) {
                                if (!("noUri".equals(Preference.getPreferences().get10ASwichUri()))) {
                                    Log.e(TAG,"onGetPluginSuccess = "+Preference.getPreferences().get10ASwichUri());
                                    return;

                                }

                                File file = new File(model.getFolder(),
                                        urlName);
                                String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
                                if (file.exists()) {
                                    uri = "file:///" + file.getAbsolutePath();
                                } else if (LanguageUtil.isChina()) {
                                    uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
                                }
                                final String uriString = uri;
                                Preference.getPreferences().save10ASwichUri(uri);
                                Preference.getPreferences().save10ASwichSettingUri("file:///" + model.getFolder() + "/setting.html");
                                Handler handler = new Handler(Looper
                                        .getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setVisibility(View.GONE);
//                                        webView.loadUrl(uriString);*/
                                        H5PlusWebView wwebView = Engine.createWebView(webView, uriString, HtmlId);
                                        wwebView.getContainer().getContainerRootView().removeAllViews();
                                        ViewGroup viewGroup = wwebView.getContainer().getContainerRootView();
                                        wwebView.onRootViewGlobalLayout(viewGroup, "", "");
                                    }
                                });
                            }

                            @Override
                            public void onGetPluginFailed(final String hint) {
                                if ((!Preference.getPreferences().get10ASwichUri().equals("noUri"))) {
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

    /**
     * 更新设备
     */
    @Override
    public void refreshDevice() {
        super.refreshDevice();
        DeviceEPInfo epInfo = getCurrentEpInfo();
        ep = epInfo.getEp();
        epType = epInfo.getEpType();
        epData = epInfo.getEpData();
        epStatus = epInfo.getEpStatus();
        handleEpData();
    }

    /**
     * 截取开关的字段
     */
    private void handleEpData() {
        if(!isNull(epData)){
            if(epData.length() == 28){
                mOnOff = epData.substring(2 , 4);
            }
        }
    }


    @Override
    public boolean isAlarming() {
        if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
            Log.d(TAG, "isAlarming() epData="+epData);
        }
        /**
         * 05：代表过流报警识别
         * xx：00：表示消除报警，01：功率过载， 10：电流过载
         * 实际使用中，只提醒功率过载
         */
        if ("0501".equals(this.epData)){
            return true;
        }
        return false;
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
        return null;
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


    @Override
    public CharSequence parseDataWithProtocol(String epData) {
        if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
            Log.d(TAG, "parseDataWithProtocol() epData="+epData);
        }
        return getAlarmInfo(epData);
    }

    @Override
    public CharSequence parseAlarmProtocol(String epData) {
        if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
            Log.d(TAG, "parseAlarmProtocol() epData="+epData);
        }
        return getAlarmInfo(epData);
    }

    protected StringBuilder strAlarm=new StringBuilder();

    /**
     * 设置报警，解析报警的协议
     * @param epData
     * @return
     */
    private String getAlarmInfo(String epData) {
        String temporaryEpData=this.epData;
        this.epData = epData;
        strAlarm.replace(0,strAlarm.length(),"");
        //解析报警
        if(!StringUtil.isNullOrEmpty(epData)&&epData.length()==4){
            String header_data=epData.substring(0,2);
            String end_data=epData.substring(2,4);
            if(header_data.equals("05")){
                strAlarm.append(DeviceTool.getDeviceAlarmAreaName(this));
                strAlarm.append(DeviceTool.getDeviceShowName(this));
                if(end_data.equals("00")){//0501 报警消除

                }else if(end_data.equals("01")){//功率过载
                    strAlarm.append(getString(R.string.embedded_switch_power_failure_alarm));
                }
            }
        }
        this.epData = temporaryEpData;
        if(strAlarm.length()==0){
            return null;
        }else {
            return strAlarm.toString();
        }
    }

    @Override
    public String getCloseSendCmd() {
        return cmdSend_Close;
    }

    @Override
    public String getOpenSendCmd() {
        return cmdSend_Open;
    }

    @Override
    public boolean isOpened() {
        return isSameAs(mOnOff,"01")? true : false;
    }

    @Override
    public boolean isClosed() {
        return !isOpened();
    }

    private Map<String, Map<Integer, Integer>> categoryIcons = DeviceUtil.getAkCategoryDrawable();
    @Override
    public EditDeviceInfoView onCreateEditDeviceInfoView(LayoutInflater inflater) {
        EditDeviceInfoView view = super.onCreateEditDeviceInfoView(inflater);
        ArrayList<EditDeviceInfoView.DeviceCategoryEntity> entities = new ArrayList<EditDeviceInfoView.DeviceCategoryEntity>();
        for (String key : categoryIcons.keySet()) {
            EditDeviceInfoView.DeviceCategoryEntity entity = new EditDeviceInfoView.DeviceCategoryEntity();
            entity.setCategory(key);
            Map<Integer, Integer> map=categoryIcons.get(key);
            Map<Integer,Integer> mapDisplay=new HashMap<>();
            //这边之所以取第一个，是为了让重命名框中显示的图片都是电源通电的状态
            mapDisplay.put(0,map.get(0));
            mapDisplay.put(1,map.get(0));
            entity.setResources(mapDisplay);
            entities.add(entity);
        }
        view.setDeviceIcons(entities);
        return view;
    }
    @Override
    public void setResourceByCategory() {
        Map<Integer, Integer> dockMap = categoryIcons.get(getDeviceCategory());
        if (dockMap != null && dockMap.size() >= 2) {
            SMALL_OPEN = dockMap.get(0);
            SMALL_CLOSE = dockMap.get(1);
            SMALL_ENABLE = dockMap.get(1);
        }
    }
    @Override
    public String getDevWebViewCallBackId(String cmd, String ep, String mode, String devID) {
        String callbackID = "";
        if (StringUtil.isNullOrEmpty(mode)) {
            mode = "6";
        }
        //isMustEp 是否包含ep字段，当前只有21命令及At设备需要
        if (cmd.equals("13")) {
            callbackID = "12-0-" + devID;
        } else {
            boolean isMustEp = (cmd.equals("21") && mode.equals(CmdUtil.MODE_SEARCH_TIME));
            if (!isMustEp) {
                callbackID = cmd + "-" + mode + "-" + devID;
            } else {
                callbackID = cmd + "-" + ep + "-" + mode + "-" + devID;
            }
        }
        return callbackID;
    }


    //判断是否是21命令，如果是21就返回false，否则为true
    @Override
    public boolean run21IsSendEpdata(String cmd,String ep,String mode,String devID){
        if(cc.wulian.ihome.wan.util.StringUtil.equals(cmd, ConstUtil.CMD_SET_DEV) ){
            return false;
        }else{
            return true;
        }
    }
}
