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
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.DisplayUtil;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;


/**
 * Created by hanx .
 * SW16
 */
@DeviceClassify(devTypes = {"At"},category = Category.C_CONTROL)
public class WL_At_Switch extends ControlableDeviceImpl{
    private static final String TAG = WL_At_Switch.class.getSimpleName();
    private String pluginName="Device_At.zip";
    private TextView textView;
    private boolean isUsePlugin=true;
    private H5PlusWebView webView;
    private String mState;
    private String mState14;
    private String mState15;
    private final String HTML_BASEURI = "file:///android_asset/Device_At/";
    private final String OPEN_PROTOCOL= "01";
    private final String CLOSE_PROTOCOL = "00";
    private static final String[] EP_SEQUENCE = { EP_14, EP_15 };
    protected String[] mSwitchStatus;
    private static final String SPLIT_SYMBOL = ">";

    private static final int SMALL_OPEN_D = cc.wulian.app.model.device.R.drawable.device_button_2_open;
    private static final int SMALL_CLOSE_D = cc.wulian.app.model.device.R.drawable.device_button_2_close;
    private static final int SMALL_STATE_BACKGROUND = cc.wulian.app.model.device.R.drawable.device_button_state_background;

    public WL_At_Switch(Context context, String type) {
        super(context, type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
        View view=inflater.inflate(R.layout.device_at_layout,container,false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveState) {
        super.onViewCreated(view, saveState);
        webView = (H5PlusWebView) view.findViewById(R.id.device_at_webview);
        textView= (TextView) view.findViewById(R.id.device_at_search_tv);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
        Engine.bindWebviewToContainer((H5PlusWebViewContainer)this.getCurrentFragment(), webView);

        if(isUsePlugin){
            getPlugin("deviceAt.html","deviceAt");
        }
        else {
            textView.setVisibility(View.GONE);
            webView.setWebviewId("deviceAt");
            webView.loadUrl(HTML_BASEURI+"deviceAt.html");
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
                                Preference.getPreferences().saveAtSwichUri(uri);
                                Preference.getPreferences().saveAtSwichSettingUri("file:///"+model.getFolder()+"/settingAt.html");
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
                                if((!Preference.getPreferences().getAtSwichUri().equals("noUri"))){
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
            url_setting=Preference.getPreferences().getAtSwichSettingUri();
        }else {
            url_setting=HTML_BASEURI+"settingAt.html";
        }
        return url_setting;
    }

    public String[] getSwitchEPResources() {
        return EP_SEQUENCE;
    }

    @Override
    protected boolean isMultiepDevice() {
        return true;
    }

    @Override
    public void onDeviceUp(DeviceInfo devInfo) {
        if(devInfo.getDevEPInfo()!=null){
            handleEpData(devInfo.getDevEPInfo().getEpData(), devInfo.getDevEPInfo().getEp());
        }
        super.onDeviceUp(devInfo);
    }

    @Override
    public String getOpenProtocol() {
        return OPEN_PROTOCOL;
    }

    @Override
    public String getCloseProtocol() {
        return CLOSE_PROTOCOL;
    }

    private int getOpenSmallIcon() {
        return SMALL_OPEN_D;
    }

    private int getCloseSmallIcon() {
        return SMALL_CLOSE_D;
    }


    @Override
    public Drawable getStateSmallIcon() {
        List<Drawable> drawers = new ArrayList<Drawable>();
            if (isSameAs(mState14 , "01")) {
                drawers.add(mResources.getDrawable(getOpenSmallIcon()));
            } else {
                drawers.add(mResources.getDrawable(getCloseSmallIcon()));
            }
        if (isSameAs(mState15 , "01")) {
                drawers.add(mResources.getDrawable(getOpenSmallIcon()));
            } else {
                drawers.add(mResources.getDrawable(getCloseSmallIcon()));
            }

        return DisplayUtil.getDrawablesMerge(
                drawers.toArray(new Drawable[] {}));
    }

    @Override
    public void refreshDevice() {
        super.refreshDevice();
        handleEpData(epData,ep);

    }

    private void  handleEpData(String mEpData, String mEp){
        if(!isNull(mEpData) && !isNull(mEp)){
            Log.i(TAG,"epData:"+mEpData+"--"+mEpData.length()+",ep:"+mEp);
            if(isSameAs(mEp ,EP_14)){
                if(mEpData.length() == 6){
                    mState14 = mEpData.substring(2, 4);
                }else if(mEpData.length() == 4){
                    mState14 = mEpData.substring(2, 4);
                }
            }else if(isSameAs(mEp ,EP_15)){
                if(mEpData.length() == 6){
                    mState15 = mEpData.substring(2, 4);
                }else if(mEpData.length() == 4){
                    mState15 = mEpData.substring(2, 4);
                }
            }
        }
    }

    //设备列表控制按钮
    @Override
    public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item, LayoutInflater inflater) {
        if (item == null) {
            item = new ControlableAtShortCutControlItem(
                    inflater.getContext());
        }
        item.setWulianDevice(this);
        return item;
    }

    //不显示设备列表中控制按钮
    protected class ControlableAtShortCutControlItem extends
            DeviceShortCutControlItem {

        protected ControlableAtShortCutControlItem(Context context) {
            super(context);
           View view = new View(mContext);
            controlLineLayout.addView(view);
        }

    }

    //管家、场景列表控制按钮
    @Override
    public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(DeviceShortCutSelectDataItem item, LayoutInflater inflater, AutoActionInfo autoActionInfo) {
        if(item == null){
            item = new ShortCutSelectDataItem(inflater.getContext());
        }
        item.setWulianDeviceAndSelectData(this, autoActionInfo);
        return item;
    }
    //不显示管家、场景列表中控制按钮
    private class ShortCutSelectDataItem extends DeviceShortCutSelectDataItem{

        public ShortCutSelectDataItem(Context context) {
            super(context);
            View view = new View(mContext);
            controlLineLayout.addView(view);
        }
    }

    //管家dialog
    @Override
    public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
            LayoutInflater inflater,   AutoActionInfo autoActionInfo) {
        DialogOrActivityHolder holder = new DialogOrActivityHolder();
        View contentView =  inflater.inflate(cc.wulian.app.model.device.R.layout.task_manager_common_light_setting_view_layout, null);
        LinearLayout mLinearLayout =  (LinearLayout) contentView.findViewById(cc.wulian.app.model.device.R.id.task_manager_common_light_setting_view_layout);
        mSwitchStatus = new String[getSwitchEPResources().length]; //动态初始化
        for (int i = 0;i<getSwitchEPResources().length;i++) {
            mLinearLayout.addView(addChildView(i,getSwitchEPResources()[i],autoActionInfo));
        }
        holder.setShowDialog(true);
        holder.setContentView(contentView);
        holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
        return holder;
    }
    private View addChildView(final int i,String str,final AutoActionInfo autoActionInfo) {
        // 动态添加布局(xml方式)
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
        mSwitchStatus[i] = "12";
        if (!StringUtil.isNullOrEmpty(autoActionInfo.getEpData())) {
            String epdata = autoActionInfo.getEpData();
            if (epdata.length() > 1) {
                mSwitchStatus[i] = epdata.substring(i+i, i+i+ 2);
            }else{
                mSwitchStatus[i] = "12";
            }

            if (StringUtil.equals(type[2], getSwitchEPResources()[i])) {
                mSwitchStatus[i] = epdata;
            }

            if (!StringUtil.isNullOrEmpty(mSwitchStatus[i])) {
                if (mSwitchStatus[i].equals("12")) {//不变。。默认为不变
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
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                mSwitchStatus[i] = "12";
            }
        }else{
            String mSetSwitchStatus = "" ;
            for(int j = 0; j < getSwitchEPResources().length; j++){
                mSetSwitchStatus += "12";
            }
            autoActionInfo.setEpData(mSetSwitchStatus);
            autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL
                    + getDeviceType() + SPLIT_SYMBOL + EP_0 + SPLIT_SYMBOL
                    + getDeviceType());
        }
//        if (!StringUtil.isNullOrEmpty(device.getDeviceInfo().getDevEPInfo().getEpName())) {
//            dev_name.setText((i + 1) + "." + device.getDeviceInfo().getDevEPInfo().getEpName());
//        } else {
//             dev_name.setText( mContext.getResources().getString(R.string.device_name_embedded_two_way_switch)+ "-"+ (i + 1) );
//        }
        dev_name.setText( mContext.getResources().getString(R.string.At)+ "-"+ (i + 1) );
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
                switch_status_button_on.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_off.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_convert.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_unselected);
                switch_status_button_unchange.setImageResource(cc.wulian.app.model.device.R.drawable.task_manager_common_light_button_selected);
                mSwitchStatus[i] = "12";
                setautoActionInfo(autoActionInfo);
            }
        });
        switch_status_button_convert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
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
        for(int i = 0;i<getSwitchEPResources().length;i++){

            mSetSwitchStatus = mSetSwitchStatus + mSwitchStatus[i];
        }
        autoActionInfo.setEpData(mSetSwitchStatus);
        autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL
                + getDeviceType() + SPLIT_SYMBOL + EP_0 + SPLIT_SYMBOL
                + getDeviceType());

    }

    @Override
    public String getDevWebViewCallBackId(String cmd,String ep,String mode,String devID){
        String callbackID="";
        if(StringUtil.isNullOrEmpty(mode)){
            mode = CmdUtil.MODE_SEARCH_TIME;
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
            if(StringUtil.equals(mode,CmdUtil.MODE_SEARCH_TIME) ||
                    StringUtil.equals(mode,CmdUtil.MODE_ADD_TIME) ||
                    StringUtil.equals(mode,CmdUtil.MODE_DEL_TIME)){
                return false;
            }else{
                return true;
            }
        }else{
            return true;
        }
    }

    @Override
    public boolean isCallBackWithEp(String cmd ,String mode) {
        if(StringUtil.equals(cmd,ConstUtil.CMD_RETN_DATA)){
            return true;
        }
        if(StringUtil.equals(cmd,ConstUtil.CMD_SET_DEV) && StringUtil.equals(mode,CmdUtil.MODE_SEARCH_TIME)){
            return true;
        }
        return false;
    }

    @Override
    public boolean isReturnEpNameToHtml() {
        return true;
    }

    @Override
    public synchronized void onDeviceData(String gwID, String devID, DeviceEPInfo devEPInfo, String cmd, String mode) {
        String ep = devEPInfo.getEp();
        WulianDevice device = getChildDevice(ep);
        if (device != null) {
            device.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
            super.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
            removeCallbacks(mRefreshStateRunnable);
            post(mRefreshStateRunnable);
            fireDeviceRequestControlData();
        } else {
            super.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
        }
    }
}
