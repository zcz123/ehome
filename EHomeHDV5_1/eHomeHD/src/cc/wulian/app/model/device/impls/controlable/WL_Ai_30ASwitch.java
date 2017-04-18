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
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.wulian.icam.utils.StringUtil;
import com.yuantuo.customview.ui.WLDialog;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.TargetConfigure;

/**
 * Created by yuxiaoxuan on 2016/11/17.
 * 30A大功率智能开关
 */
@DeviceClassify(devTypes = {"Ai"},category = Category.C_CONTROL)
public class WL_Ai_30ASwitch  extends ControlableDeviceImpl implements Alarmable{
    private String pluginName="30A_Ai.zip";
    private H5PlusWebView webView;
    private TextView textView;
    protected String ep;
    protected String epType;
    protected String epData;
    protected String epStatus;
    private String tag="WL_Ai_30ASwitch";
    private boolean isUsePlugin=true;//默认是true，调试时可以改成false
    private Map<String, Map<Integer, Integer>> categoryIcons = DeviceUtil.getAiCategoryDrawable();
    protected static int SMALL_OPEN = cc.wulian.app.model.device.R.drawable.device_ai1_02;
    protected static int SMALL_CLOSE = cc.wulian.app.model.device.R.drawable.device_ai1_01;
    protected static int SMALL_ENABLE = cc.wulian.app.model.device.R.drawable.device_ai1_01;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
        View view=inflater.inflate(R.layout.device_ai30aswich,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveState) {
        super.onViewCreated(view, saveState);
        webView = (H5PlusWebView) view.findViewById(R.id.device_webview);
        textView= (TextView) view.findViewById(R.id.search_tv);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
        Engine.bindWebviewToContainer((H5PlusWebViewContainer)this.getCurrentFragment(), webView);
        SmarthomeFeatureImpl.setData("ai30_gwid", gwID);
        SmarthomeFeatureImpl.setData("ai30_devid", devID);
        SmarthomeFeatureImpl.setData("ai30_ep", ep);
        SmarthomeFeatureImpl.setData("ai30_eptype", epType);
        if(isUsePlugin){
            String strUri = Preference.getPreferences().get30ASwichUri();
            File file = new File(strUri);
            Log.d(WL_Ai_30ASwitch.class.getName(), "onViewCreated:strUri="+strUri+" \r\nstrUri.equals(\"noUri\")="+strUri.equals("noUri")+" \r\nfile.exists()="+file.exists());
            if (!strUri.equals("noUri")) {
                textView.setVisibility(View.GONE);
                webView.loadUrl(Preference.getPreferences().get30ASwichUri());
                getPlugin("deviceMenu.html","deviceMenuIndex");
            }else{
                getPlugin("deviceMenu.html","deviceMenuIndex");
            }
        }
        else {
            textView.setVisibility(View.GONE);
            webView.loadUrl("file:///android_asset/30A/deviceMenu.html");
        }


    }
    //设置设备图标
    @Override
    public Drawable getDefaultStateSmallIcon() {
        Drawable icon=null;
        if(this.isDeviceOnLine()){
            if(isOpen){
                icon = getResources().getDrawable(SMALL_OPEN);
            }else {
                icon = getResources().getDrawable(SMALL_CLOSE);
            }
        }
        else{
            icon=getResources().getDrawable(SMALL_ENABLE);
        }
        return icon;
    }

    @Override
    public String getDefaultDeviceName() {
        String defaultName=super.getDefaultDeviceName();
        if(StringUtil.isNullOrEmpty(defaultName)){
            defaultName=getString(R.string.device_type_Ai);
        }
        return defaultName;
    }

    public WL_Ai_30ASwitch(Context context, String type){
        super(context, type);
    }
    @Override
    protected List<MoreMenuPopupWindow.MenuItem> getDeviceMenuItems(
            final MoreMenuPopupWindow manager) {
        List<MoreMenuPopupWindow.MenuItem> items = super.getDeviceMenuItems(manager);
        MoreMenuPopupWindow.MenuItem deviceSettingItem=new MoreMenuPopupWindow.MenuItem(mContext) {
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
                String setttingFilePath=getSettingFilePath();
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, WL_Ai_30ASwitch.this.devID);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, WL_Ai_30ASwitch.this.gwID);
                IntentUtil.startHtml5PlusActivity(mContext,
                        setttingFilePath,
                        getDefaultDeviceName(),
                        mContext.getString(R.string.common_setting));
                manager.dismiss();
            }
        };
        if (isDeviceOnLine()) {
            items.add(deviceSettingItem);
//            items.add(deviceIntroductionItem);
        }
        return items;
    }

    public String getSettingFilePath(){
        String url_setting="";
        if(isUsePlugin){
            url_setting=Preference.getPreferences().get30ASwichSettingUri();
        }else {
            url_setting="file:///android_asset/30A/setting.html";
        }
        return url_setting;
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


                                File file = new File(model.getFolder(),
                                        urlName);
                                String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
                                if (file.exists()) {
                                    uri = "file:///" + file.getAbsolutePath();
                                } else if (LanguageUtil.isChina()) {
                                    uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
                                }
                                final String uriString = uri;
                                Preference.getPreferences().save30ASwichUri(uri);
                                Preference.getPreferences().save30ASwichSettingUri("file:///"+model.getFolder()+"/setting.html");
                                Handler handler = new Handler(Looper
                                        .getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setVisibility(View.GONE);
                                        H5PlusWebView wwebView = Engine.createWebView(webView, uriString, htmlID);
                                        wwebView.getContainer().getContainerRootView().removeAllViews();
                                        ViewGroup viewGroup=wwebView.getContainer().getContainerRootView();
                                        wwebView.onRootViewGlobalLayout(viewGroup, "", "");
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
    public void refreshDevice() {
        DeviceEPInfo epInfo = getCurrentEpInfo();
        if(epInfo!=null){
            ep = epInfo.getEp();
            epType = epInfo.getEpType();
            epData = epInfo.getEpData();
            epStatus = epInfo.getEpStatus();
        }

    }
    /*用于报警时弹窗*/
    private WLDialog dialog_warning;
    @Override
    public void initViewStatus() {
        super.initViewStatus();
        String warningMsg=getAlarmInfo(epData);
        if(!StringUtil.isNullOrEmpty(warningMsg)){
            WLDialog.Builder builder = new WLDialog.Builder(mContext);
            builder.setTitle(mContext.getResources().getString(
                    cc.wulian.app.model.device.R.string.device_metering_switch_html_prompt_hint));
            builder.setMessage(getResources().getString(cc.wulian.app.model.device.R.string.device_72_now_power_disconnect));
            builder.setPositiveButton(android.R.string.ok);
            dialog_warning = builder.create();
            dialog_warning.show();
        }
    }

    protected StringBuilder strAlarm=new StringBuilder();

    @Override
    public boolean isAlarming() {
        if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
            Log.d(tag, "isAlarming() epData="+epData);
        }
        /*xx：00：表示消除报警，01：功率过载， 10：电流过载
        * 实际使用中，只提醒功率过载
        * */
        if(this.epData.equals("0501")){
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
    /*首页报警列表*/
    @Override
    public CharSequence  parseAlarmProtocol(String epData){
        if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
            Log.d(tag, "parseAlarmProtocol() epData="+epData);
        }
        return getAlarmInfo(epData);
    }

    /*报警页面信息*/
    @Override
    public CharSequence parseDataWithProtocol(String epData) {
        if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
            Log.d(tag, "parseDataWithProtocol() epData="+epData);
        }
        return getAlarmInfo(epData);
    }

    private String getAlarmInfo(String epData){
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
                    String msg=getString(R.string.embedded_switch_power_failure_alarm);
                    strAlarm.append(msg);
                }else if(end_data.equals("10")){//电流过载，不进行这个提醒
//                    strAlarm.append("电流过载");
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

    //设备列表页右侧用于简单控制的区域
    @Override
    public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item, LayoutInflater inflater) {
        if(item==null){
            item=new shortCutItemForAi(inflater.getContext());
        }
        item.setWulianDevice(this);
        return super.onCreateShortCutView(item, inflater);
    }
    protected LinearLayout mLinearLayout ;
    protected String[] mSwitchStatus;
    private static final String SPLIT_SYMBOL = ">";
    private static final String[] EP_SEQUENCE = { EP_14 };
    public String[] getLightEPResources() {
        return EP_SEQUENCE;
    }
    @Override
    public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(LayoutInflater inflater, AutoActionInfo autoActionInfo) {
        DialogOrActivityHolder holder = new DialogOrActivityHolder();
        View contentView =  inflater.inflate(cc.wulian.app.model.device.R.layout.task_manager_common_light_setting_view_layout, null);
        mLinearLayout =  (LinearLayout) contentView.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_setting_view_layout);
        mSwitchStatus = new String[getLightEPResources().length]; //动态初始化
        for (int i = 0;i<getLightEPResources().length;i++) {
            mLinearLayout.addView(addChildView(i,getLightEPResources()[i],autoActionInfo));
        }
        holder.setShowDialog(true);
        holder.setContentView(contentView);
        holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
        return holder;
    }
    private View addChildView(final int i,String str,final AutoActionInfo autoActionInfo) {
        // 该设备不需要进行动态布局
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(cc.wulian.app.model.device.R.layout.task_manager_common_light_setting_view, null);
        final WulianDevice device = getChildDevice(str);

        final TextView dev_name = (TextView) view.findViewById(cc.wulian.app.model.device.R.id.device_common_light_setting_dev_name);
        final ImageView switch_status_button_on = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_on);
        final ImageView switch_status_button_off = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_off);
        final ImageView switch_status_button_convert = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_convert);
        final ImageView switch_status_button_unchange = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_unchange);
        final String[] type = autoActionInfo.getObject().split(SPLIT_SYMBOL);
        switch_status_button_unchange.setVisibility(View.GONE);
        view.findViewById(cc.wulian.app.model.device.R.id.layout_button_unchange).setVisibility(View.GONE);
        mSwitchStatus[i] = "2";
        if (!cc.wulian.ihome.wan.util.StringUtil.isNullOrEmpty(autoActionInfo.getEpData())) {
            String epdata = autoActionInfo.getEpData();

//            if (epdata.length() > 1) {
//                mSwitchStatus[i] = epdata.substring(i, i + 1);
//            }else{
//                mSwitchStatus[i] = "2";
//            }
            mSwitchStatus[i] = epdata;
//            if (cc.wulian.ihome.wan.util.StringUtil.equals(type[2], getLightEPResources()[i])) {
//                mSwitchStatus[i] = epdata;
//            }

            if (!cc.wulian.ihome.wan.util.StringUtil.isNullOrEmpty(mSwitchStatus[i])) {
                if (mSwitchStatus[i].equals("2")) {//不变。。默认为不变
                    switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                    switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                    switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                    switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);

                } else if (mSwitchStatus[i].equals("11")) {
                    switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                    switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                    switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                    switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                } else if (mSwitchStatus[i].equals("10")) {
                    switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                    switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                    switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                    switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                }else if (mSwitchStatus[i].equals("13")) {
                    switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                    switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                    switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                    switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                }
            } else {
                // mLinearLayout.getChildAt(i).findViewById(R.id.device_common_light_setting_switch_open).setVisibility(View.GONE);
                // mLinearLayout.getChildAt(i).findViewById(R.id.device_common_light_setting_switch_close).setVisibility(View.VISIBLE);
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                mSwitchStatus[i] = "2";
            }
        }else{
            String mSetSwitchStatus = "" ;
            for(int j = 0; j < getLightEPResources().length; j++){
                mSetSwitchStatus += "2";
            }
            autoActionInfo.setEpData(mSetSwitchStatus);
            autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL
                    + getDeviceType() + SPLIT_SYMBOL + EP_0 + SPLIT_SYMBOL
                    + getDeviceType());
        }
        boolean isNull=device==null
                      ||device.getDeviceInfo()==null
                      ||device.getDeviceInfo().getDevEPInfo()==null
                      ||StringUtil.isNullOrEmpty(device.getDeviceInfo().getDevEPInfo().getEpName());

        if (!isNull) {
            dev_name.setText((i + 1) + "." + device.getDeviceInfo().getDevEPInfo().getEpName());
        } else {
            dev_name
                    .setText((i + 1)
                            + "."
                            + getResources().getString(
                            cc.wulian.app.model.device.R.string.device_type_Ai));
        }
        switch_status_button_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                mSwitchStatus[i] = "11";
                setautoActionInfo(autoActionInfo);
            }
        });
        switch_status_button_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Logger.debug("i="+i);
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                mSwitchStatus[i] = "10";
                setautoActionInfo(autoActionInfo);
            }
        });
        switch_status_button_unchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Logger.debug("i="+i);
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                mSwitchStatus[i] = "2";
                setautoActionInfo(autoActionInfo);
            }
        });
        switch_status_button_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                Logger.debug("i="+i);
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                mSwitchStatus[i] = "13";
                setautoActionInfo(autoActionInfo);
            }
        });
        view.setLayoutParams(lp);
        return view;
    }
    /**
     * 判断不需要设定的数目时候为总数-1
     * 若是，则发送ep端口加对应位置数据
     * 若不是则发送拼接数据；
     * @param autoActionInfo
     */
    private void setautoActionInfo(AutoActionInfo autoActionInfo) {
        String mSetSwitchStatus = "" ;
        for(int i = 0;i<getLightEPResources().length;i++){
            mSetSwitchStatus = mSetSwitchStatus + mSwitchStatus[i];
        }
        autoActionInfo.setEpData(mSetSwitchStatus);
        autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL
                + getDeviceType() + SPLIT_SYMBOL + EP_0 + SPLIT_SYMBOL
                + getDeviceType());
    }
    private boolean isOpen=false;

    @Override
    public String getOpenProtocol() {
        return "11";
    }

    @Override
    public String getCloseProtocol() {
        return "10";
    }

    @Override
    public boolean isOpened() {
        return isOpen;
    }
    private class shortCutItemForAi extends DeviceShortCutControlItem{

        private LinearLayout controlableLineLayout;
        private ImageView openImageView;
        private ImageView closeImageView;


        private View.OnClickListener cliclListener = new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //只有设备在线时才可以有单击事件
                if(isDeviceOnLine()){
                    switch (v.getId()){
                        case R.id.device_short_cut_control_open_iv:{
                            if(!isOpen){
                                clickOpenAi();
                            }

                        }break;
                        case R.id.device_short_cut_control_close_iv:{
                            if(isOpen){
                                clickCloseAi();
                            }
                        }break;
                    }
                }
            }
        };
        public shortCutItemForAi(Context context) {
            super(context);
            controlableLineLayout = (LinearLayout)inflater.inflate(R.layout.device_short_cut_control_controlable, null);
            openImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_open_iv);
            openImageView.setOnClickListener(cliclListener);
            controlableLineLayout.findViewById(R.id.device_short_cut_control_stop_iv).setVisibility(View.INVISIBLE);
            closeImageView = (ImageView)controlableLineLayout.findViewById(R.id.device_short_cut_control_close_iv);
            closeImageView.setOnClickListener(cliclListener);
            controlLineLayout.addView(controlableLineLayout);
        }

        @Override
        public void setWulianDevice(WulianDevice device) {
            super.setWulianDevice(device);
            if(isDeviceOnLine()){
                if(device instanceof Controlable){
                    String deviceData=WL_Ai_30ASwitch.this.epData;
                    if(TargetConfigure.LOG_LEVEL <= Log.DEBUG) {
                        Log.d("shortCutItemForAi", "epData="+WL_Ai_30ASwitch.this.epData);
                    }
                    if(!StringUtil.isNullOrEmpty(deviceData)&&deviceData.length()==22){
                        String strOC=deviceData.substring(2,4);
                        isOpen=strOC.equals("01");
                        if(isOpen){
                            openImageView.setSelected(true);
                            closeImageView.setSelected(false);
                        }else {
                            openImageView.setSelected(false);
                            closeImageView.setSelected(true);
                        }
                    }
                }
            }
            else {
                openImageView.setSelected(false);
                closeImageView.setSelected(false);
            }
        }

        private void clickOpenAi() {
            controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),getOpenProtocol());
            openImageView.setSelected(true);
            closeImageView.setSelected(false);
            isOpen=true;
        }
        private void clickCloseAi() {
            controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(),getCloseProtocol());
            openImageView.setSelected(false);
            closeImageView.setSelected(true);
            isOpen=false;
        }


    }

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
}
