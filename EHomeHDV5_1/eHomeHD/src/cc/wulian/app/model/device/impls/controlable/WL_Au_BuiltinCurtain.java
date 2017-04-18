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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.List;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.MoreMenuPopupWindow;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.IntentUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.TargetConfigure;

/**
 * Created by yuxiaoxuan on 2016/11/29.
 * Au 内嵌式窗帘控制器
 */
@DeviceClassify(devTypes = {"Au"}, category = Category.C_CONTROL)
public class WL_Au_BuiltinCurtain extends ControlableDeviceImpl {
    protected String ep;
    protected String epType;
    protected String epData;
    protected String epStatus;
    private H5PlusWebView webView;
    private TextView textView;
    private String pluginName = "Curtain_Au.zip";
    private String TAG = "WL_Au_BuiltinCurtain";
    private final String cmdBack_Open = "02";
    private final String cmdBack_Stop = "01";
    private final String cmdBack_Close = "03";
    private String curCmdBack = "";
    private final String cmdSend_Open = "2";
    private final String cmdSend_Stop = "1";
    private final String cmdSend_Close = "3";
    private String cmdSend_Binding = "4";//绑定/解绑命令，该命令原生没有用到，H5用到了。

    public WL_Au_BuiltinCurtain(Context context, String type) {
        super(context, type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
        View view = inflater.inflate(R.layout.device_au_builtin_curtain, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveState) {
        super.onViewCreated(view, saveState);
        webView = (H5PlusWebView) view.findViewById(R.id.device_webview);
        textView = (TextView) view.findViewById(R.id.search_tv);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
        Engine.bindWebviewToContainer((H5PlusWebViewContainer) this.getCurrentFragment(), webView);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EP, ep);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EPDATA,epData);
        getPlugin("device.html","deviceIndex");
//        textView.setVisibility(View.GONE);
//        webView.loadUrl("file:///android_asset/Curtain_Au/device.html");
    }

    /*获取插件*/
    private void getPlugin(final String urlName, final String htmlID) {
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
                                Preference.getPreferences().saveAuCurtainUri(uri);
                                Preference.getPreferences().saveAuCurtainSettingUri("file:///" + model.getFolder() + "/setting.html");
                                Handler handler = new Handler(Looper
                                        .getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
//                                        webView.loadUrl(uriString);*/
                                        textView.setVisibility(View.GONE);
                                        H5PlusWebView wwebView = Engine.createWebView(webView, uriString, htmlID);
                                        wwebView.getContainer().getContainerRootView().removeAllViews();
                                        ViewGroup viewGroup = wwebView.getContainer().getContainerRootView();
                                        wwebView.onRootViewGlobalLayout(viewGroup, "", "");
                                    }
                                });
                            }

                            @Override
                            public void onGetPluginFailed(final String hint) {
                                if ((!Preference.getPreferences().get30ASwichUri().equals("noUri"))) {
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

    /*设置设备图标*/
    //这个地方有个问题，当我设置完图标之后从什么地方获取呢？
    @Override
    public Drawable getDefaultStateSmallIcon() {
        Drawable icon = null;
        if(isDeviceOnLine()){
            icon = getResources().getDrawable(R.drawable.curtain_au01);
        }else {
            icon = getResources().getDrawable(R.drawable.curtain_au03);
        }
        return icon;
    }

    /*设置设备名称*/

    @Override
    public String getDefaultDeviceName() {
        String defaultName = super.getDefaultDeviceName();
        if (StringUtil.isNullOrEmpty(defaultName)) {
            defaultName = getString(R.string.Au);
        }
        return defaultName;
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
//                String setttingFilePath = Preference.getPreferences().getAuCurtainSettingUri();
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, WL_Au_BuiltinCurtain.this.devID);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, WL_Au_BuiltinCurtain.this.gwID);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EP, WL_Au_BuiltinCurtain.this.ep);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.EPDATA,WL_Au_BuiltinCurtain.this.epData);
                String setttingFilePath =Preference.getPreferences().getAuCurtainSettingUri();
//                String setttingFilePath ="file:///android_asset/Curtain_Au/setting.html";
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, WL_Au_BuiltinCurtain.this.devID);
                SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, WL_Au_BuiltinCurtain.this.gwID);
                IntentUtil.startHtml5PlusActivity(mContext,
                        setttingFilePath,
                        getDefaultDeviceName(),
                        mContext.getString(R.string.common_setting));
                manager.dismiss();
            }
        };
        if (isDeviceOnLine()) {
            items.add(deviceSetting);
        }
        return items;
    }

    /*刷新设备*/
    @Override
    public void refreshDevice() {
        DeviceEPInfo epInfo = getCurrentEpInfo();
        if(epInfo!=null){
            ep = epInfo.getEp();
            epType = epInfo.getEpType();
            epData = epInfo.getEpData();
            epStatus = epInfo.getEpStatus();
            curCmdBack=epData;
        }
    }

    /*设备列表右侧快速控制区域 控制页面*/
    @Override
    public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item, LayoutInflater inflater) {
        if (item == null) {
            item = new shortCutItemForAu(inflater.getContext());
        }
        item.setWulianDevice(this);
        return super.onCreateShortCutView(item, inflater);
    }




    /**
     * 设备控制区域的快捷键
     */
    private class shortCutItemForAu extends DeviceShortCutControlItem {

        private LinearLayout controlableLineLayout = null;
        private ImageView openImageView = null;
        private ImageView stopImageView = null;
        private ImageView closeImageView = null;

        public shortCutItemForAu(Context context) {
            super(context);
            controlableLineLayout = (LinearLayout) inflater.inflate(R.layout.device_short_cut_control_controlable, null);
            openImageView = (ImageView) controlableLineLayout.findViewById(R.id.device_short_cut_control_open_iv);
            stopImageView = (ImageView) controlableLineLayout.findViewById(R.id.device_short_cut_control_stop_iv);
            closeImageView = (ImageView) controlableLineLayout.findViewById(R.id.device_short_cut_control_close_iv);
            openImageView.setOnClickListener(imageView_OnClick);
            stopImageView.setOnClickListener(imageView_OnClick);
            closeImageView.setOnClickListener(imageView_OnClick);
            stopImageView.setSelected(true);
            controlLineLayout.addView(controlableLineLayout);
        }

        @Override
        public void setWulianDevice(WulianDevice device) {
            super.setWulianDevice(device);
            if (isDeviceOnLine()) {
                if (device instanceof Controlable) {
                    String epData = WL_Au_BuiltinCurtain.this.epData;
                    curCmdBack = epData;
                    setImageSelected(epData);
                }
            } else {
                curCmdBack = "";
                openImageView.setSelected(false);
                stopImageView.setSelected(false);
                closeImageView.setSelected(false);
            }
        }

        private void setImageSelected(String backCmd) {
            if(!StringUtil.isNullOrEmpty(backCmd)){
                if (backCmd.equals(cmdBack_Open)) {
                    openImageView.setSelected(true);
                    stopImageView.setSelected(false);
                    closeImageView.setSelected(false);
                } else if (backCmd.equals(cmdBack_Stop)) {
                    openImageView.setSelected(false);
                    stopImageView.setSelected(true);
                    closeImageView.setSelected(false);
                } else if (backCmd.equals(cmdBack_Close)) {
                    openImageView.setSelected(false);
                    stopImageView.setSelected(false);
                    closeImageView.setSelected(true);
                }
            }else{
                openImageView.setSelected(false);
                stopImageView.setSelected(true);
                closeImageView.setSelected(false);
                Log.d(TAG, "WLAubackCmd=" + "null");
            }
        }

        private View.OnClickListener imageView_OnClick = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //只有设备在线或者是当前的状态不为空的时候才可以控制
                if(!isDeviceOnLine()||StringUtil.isNullOrEmpty(curCmdBack)){
                    return;
                }
                switch (view.getId()) {
                    case R.id.device_short_cut_control_open_iv: {
                        if (!curCmdBack.equals(cmdBack_Open)) {
                            controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(), getOpenProtocol());
                        }
                    }
                    break;
                    case R.id.device_short_cut_control_stop_iv: {
                        if (!curCmdBack.equals(cmdBack_Stop)) {
                            controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(), getStopProtocol());
                        }
                    }
                    break;
                    case R.id.device_short_cut_control_close_iv: {
                        if (!curCmdBack.equals(cmdBack_Close)) {
                            controlDevice(getCurrentEpInfo().getEp(), getCurrentEpInfo().getEpType(), getCloseProtocol());
                        }
                    }
                    break;
                }
            }
        };
    }

    /*管家功能设置区域*/
    @Override
    public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(
            DeviceShortCutSelectDataItem item, LayoutInflater inflater,
            AutoActionInfo autoActionInfo) {
        if(item == null){
            item = new WL_Au_BuiltinCurtain.ShortCutControlableDeviceSelectDataItem(inflater.getContext());
        }
        item.setWulianDeviceAndSelectData(this, autoActionInfo);
        return item;
    }
    /**
     * 管家快捷控制区域
     */
    private class ShortCutControlableDeviceSelectDataItem extends DeviceShortCutSelectDataItem{
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
            openImageView.setOnClickListener(clickListener);
            stopImageView.setOnClickListener(clickListener);
            closeImageView.setOnClickListener(clickListener);
            openImageView.setVisibility(View.VISIBLE);
            stopImageView.setVisibility(View.VISIBLE);
            closeImageView.setVisibility(View.VISIBLE);
            controlLineLayout.addView(controlableLineLayout);
        }
        private View.OnClickListener clickListener=new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (view.getId()){
                    case R.id.device_short_cut_control_open_iv:{
                        autoActionInfo.setEpData(getOpenProtocol());

                    }break;
                    case R.id.device_short_cut_control_stop_iv:{
                        autoActionInfo.setEpData(getStopProtocol());
                    }break;
                    case R.id.device_short_cut_control_close_iv:{
                        autoActionInfo.setEpData(getCloseProtocol());
                    }break;
                }
                autoActionInfo.setObject(getDeviceID()+">"+getDeviceType()+">"+WulianDevice.EP_14+">"+getDeviceType());
                fireShortCutSelectDataListener();
            }
        };
        @Override
        public void setWulianDeviceAndSelectData(WulianDevice device, AutoActionInfo autoActionInfo) {
            super.setWulianDeviceAndSelectData(device, autoActionInfo);
            final String SPLIT_SYMBOL = ">";
            final String actionEpData=autoActionInfo.getEpData();
            final String[] type = autoActionInfo.getObject().split(SPLIT_SYMBOL);
            if(device instanceof Controlable) {
                if(!StringUtil.isNullOrEmpty(actionEpData)){
                    if(actionEpData.equals(cmdSend_Open)){
                        openImageView.setSelected(true);
                        stopImageView.setSelected(false);
                        closeImageView.setSelected(false);
                    }else if(actionEpData.equals(cmdSend_Stop)){
                        openImageView.setSelected(false);
                        stopImageView.setSelected(true);
                        closeImageView.setSelected(false);
                    }else if(actionEpData.equals(cmdSend_Close)){
                        openImageView.setSelected(false);
                        stopImageView.setSelected(false);
                        closeImageView.setSelected(true);
                    }
                }

            }

        }
    }
    /*重写控制命令*/

    @Override
    public String getOpenProtocol() {
        return cmdSend_Open;
    }

    @Override
    public String getStopProtocol() {
        return cmdSend_Stop;
    }

    @Override
    public String getCloseProtocol() {
        return cmdSend_Close;
    }

   /* @Override
    public boolean isDeviceOnLine() {
        return true;
    }*/

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


}
