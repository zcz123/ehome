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

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.alarmable.Alarmable;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
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


/**
 * Created by hanx .
 * SW16
 */
@DeviceClassify(devTypes = {"Aj"},category = Category.C_CONTROL)
public class WL_Aj_Switch extends ControlableDeviceImpl implements Alarmable{
    private static final String TAG = WL_Aj_Switch.class.getSimpleName();
    private String pluginName="SW16.zip";
    private TextView textView;
    private boolean isUsePlugin=true;
    private H5PlusWebView webView;
    private String mOnOff;
    private final String HTML_BASEURI = "file:///android_asset/SW16/";
    private final String OPEN_SEND_CMD = "11";
    private final String CLOSE_SEND_CMD = "10";
    private static final String SPLIT_SYMBOL = ">";
    private LinearLayout mLinearLayout ;
    private String mSwitchStatus;
    private Map<String, Map<Integer, Integer>> categoryIcons = DeviceUtil.getAiCategoryDrawable();

    private int SMALL_OPEN_D = cc.wulian.app.model.device.R.drawable.device_button_1_open;
    private int SMALL_CLOSE_D = cc.wulian.app.model.device.R.drawable.device_button_1_close;

    public WL_Aj_Switch(Context context, String type) {
        super(context, type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
        View view=inflater.inflate(R.layout.device_aj_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveState) {
        super.onViewCreated(view, saveState);
        webView = (H5PlusWebView) view.findViewById(R.id.device_aj_webview);
        textView= (TextView) view.findViewById(R.id.device_aj_search_tv);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
        Engine.bindWebviewToContainer((H5PlusWebViewContainer)this.getCurrentFragment(), webView);
        if(isUsePlugin){
            getPlugin("deviceSW16.html","deviceSW16");
        }
        else {
            textView.setVisibility(View.GONE);
            webView.setWebviewId("deviceSW16");
            webView.loadUrl(HTML_BASEURI+"deviceSW16.html");
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

                                File file = new File(model.getFolder(),
                                        urlName);
                                String uri = "file:///android_asset/disclaimer/error_page_404_en.html";
                                if (file.exists()) {
                                    uri = "file:///" + file.getAbsolutePath();
                                } else if (LanguageUtil.isChina()) {
                                    uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
                                }
                                final String uriString = uri;
                                Preference.getPreferences().saveAjSwichUri(uri);
                                Preference.getPreferences().saveAjSwichSettingUri("file:///"+model.getFolder()+"/setting.html");
                                Handler handler = new Handler(Looper
                                        .getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        textView.setVisibility(View.GONE);
                                        webView.loadUrl(uriString);
                                    }
                                });
                            }

                            @Override
                            public void onGetPluginFailed(final String hint) {
                                if((!Preference.getPreferences().getAjSwichUri().equals("noUri"))){
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
                        mContext.getResources().getString(R.string.nav_device_title));
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
            url_setting=Preference.getPreferences().getAjSwichSettingUri();
        }else {
            url_setting=HTML_BASEURI+"setting.html";
        }
        return url_setting;
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
            if(epData.length() == 24){
                mOnOff = epData.substring(2 , 4);
            }
        }
    }

    @Override
    public String getCloseSendCmd() {
        return CLOSE_SEND_CMD;
    }

    @Override
    public String getOpenSendCmd() {
        return OPEN_SEND_CMD;
    }

    @Override
    public boolean isClosed() {
        return !isOpened();
    }

    @Override
    public boolean isOpened() {
        return isSameAs(mOnOff,"01")? true : false;
    }

    @Override
    public String getOpenProtocol() {
        return getOpenSendCmd();
    }

    @Override
    public String getCloseProtocol() {
        return getCloseSendCmd();
    }

    @Override
    public Drawable getStateSmallIcon() {
        return isOpened() ? getDrawable(getOpenSmallIcon()) : isClosed() ? getDrawable(getCloseSmallIcon()) : this
                .getDefaultStateSmallIcon();
    }

    //开启时小图标
    private int getOpenSmallIcon(){
        return SMALL_OPEN_D;
    }

    //关闭时小图标
    private  int getCloseSmallIcon(){
        return SMALL_CLOSE_D;
    }

   //列表图标切换
    @Override
    public void setResourceByCategory() {
        Map<Integer, Integer> dockMap = categoryIcons.get(getDeviceCategory());
        if (dockMap != null && dockMap.size() >= 2) {
            SMALL_OPEN_D = dockMap.get(0);
            SMALL_CLOSE_D = dockMap.get(1);
        }
    }

    //重命名选择图标
    @Override
    public EditDeviceInfoView onCreateEditDeviceInfoView(LayoutInflater inflater) {
        EditDeviceInfoView view = super.onCreateEditDeviceInfoView(inflater);
        ArrayList<EditDeviceInfoView.DeviceCategoryEntity> entities = new ArrayList<EditDeviceInfoView.DeviceCategoryEntity>();
        for (String key : categoryIcons.keySet()) {
            EditDeviceInfoView.DeviceCategoryEntity entity = new EditDeviceInfoView.DeviceCategoryEntity();
            entity.setCategory(key);
            entity.setResources(categoryIcons.get(key));
            entities.add(entity);
        }
        view.setDeviceIcons(entities);
        return view;
    }

    //报警
    @Override
    public boolean isAlarming() {
        //功率过载0501、电流过载0510 报警
        if(isSameAs(epData, "0501")){
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
    public CharSequence parseAlarmProtocol(String epData) {
        StringBuilder sb = new StringBuilder();
        sb.append(DeviceTool.getDeviceAlarmAreaName(this));
        sb.append(DeviceTool.getDeviceShowName(this));
        if (LanguageUtil.isChina() || LanguageUtil.isTaiWan()) {
            sb.append(mContext.getString(R.string.home_device_alarm_default_voice_detect));
        } else {
            sb.append(" "+ mContext.getString(R.string.home_device_alarm_default_voice_detect)+ " ");
        }
        if (isSameAs(epData, "0501")) {   //功率过载
            sb.append(mContext.getResources().getString(R.string.embedded_switch_power_failure_alarm));
        }
//        else if (isSameAs(epData, "0510")) {  //电流过载
//            sb.append("电流过载");
//        }
        return sb.toString();
    }

    //管家设置
    @Override
    public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
            LayoutInflater inflater,   AutoActionInfo autoActionInfo) {
        DialogOrActivityHolder holder = new DialogOrActivityHolder();
        View contentView =  inflater.inflate(cc.wulian.app.model.device.R.layout.task_manager_common_light_setting_view_layout, null);
        mLinearLayout =  (LinearLayout) contentView.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_setting_view_layout);
        mLinearLayout.addView(addChildView(autoActionInfo));
        holder.setShowDialog(true);
        holder.setContentView(contentView);
        holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
        return holder;
    }

    private View addChildView(final AutoActionInfo autoActionInfo) {
        // 动态添加布局(xml方式)
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(cc.wulian.app.model.device.R.layout.task_manager_common_light_setting_view, null);

        final TextView dev_name = (TextView) view.findViewById(cc.wulian.app.model.device.R.id.device_common_light_setting_dev_name);
        final ImageView switch_status_button_on = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_on);
        final ImageView switch_status_button_off = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_off);
        final ImageView switch_status_button_convert = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_convert);
        final ImageView switch_status_button_unchange = (ImageView) view.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_button_unchange);
//        switch_status_button_unchange.setVisibility(View.INVISIBLE);
        dev_name.setVisibility(View.INVISIBLE);
        final String[] type = autoActionInfo.getObject().split(SPLIT_SYMBOL);
        mSwitchStatus = "";
        if (!StringUtil.isNullOrEmpty(autoActionInfo.getEpData())) {
            mSwitchStatus = autoActionInfo.getEpData();
            if (mSwitchStatus.equals("12")) {//不变。。默认为不变
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);

            } else if (mSwitchStatus.equals("11")) {
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
            } else if (mSwitchStatus.equals("10")) {
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
            }else if (mSwitchStatus.equals("13")) {
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
            }
        } else {
            switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
            switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
            switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
            switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
            mSwitchStatus = "12";
            autoActionInfo.setEpData(mSwitchStatus);
            autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL
                    + getDeviceType() + SPLIT_SYMBOL + EP_14 + SPLIT_SYMBOL
                    + getDeviceType());
        }

        switch_status_button_on.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                mSwitchStatus = "11";
                setautoActionInfo(autoActionInfo,mSwitchStatus);
            }
        });
        switch_status_button_off.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                mSwitchStatus = "10";
                setautoActionInfo(autoActionInfo,mSwitchStatus);
            }
        });
        switch_status_button_unchange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                mSwitchStatus = "12";
                setautoActionInfo(autoActionInfo,mSwitchStatus);
            }
        });
        switch_status_button_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                mSwitchStatus = "13";
                setautoActionInfo(autoActionInfo,mSwitchStatus);
            }
        });
        view.setLayoutParams(lp);
        return view;
    }

    private void setautoActionInfo(AutoActionInfo autoActionInfo,String selectData) {
        autoActionInfo.setEpData(selectData);
        autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL
                + getDeviceType() + SPLIT_SYMBOL + EP_14 + SPLIT_SYMBOL
                + getDeviceType());
//		}
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

    @Override
    public boolean run21IsSendEpdata(String cmd,String ep,String mode,String devID){
        if(StringUtil.equals(cmd, ConstUtil.CMD_SET_DEV)){
            return false;
        }else{
            return true;
        }
    }


}
