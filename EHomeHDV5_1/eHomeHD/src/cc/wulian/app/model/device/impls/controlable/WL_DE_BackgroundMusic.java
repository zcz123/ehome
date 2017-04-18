package cc.wulian.app.model.device.impls.controlable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;

import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.interfaces.H5PlusWebViewContainer;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.house.HouseKeeperSelectControlDeviceDataFragment;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginModel;
import cc.wulian.smarthomev5.service.html5plus.plugins.PluginsManager;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.LanguageUtil;

/**
 * Created by yuxiaoxuan on 2017/1/13.
 * 海尔思背景音乐
 */
@DeviceClassify(devTypes = {"DE"}, category = Category.C_CONTROL)
public class WL_DE_BackgroundMusic extends ControlableDeviceImpl {
    private String pluginName = "WiseBGMusic.zip";
    private H5PlusWebView webView;
    private TextView textView;
    private int iconDefualt = cc.wulian.app.model.device.R.drawable.device_thermost_open;
    //主页
    private static final String controlPagePath = "musicPlay.html";
    //管家
    private static final String housekeeperPagePath = "musicPlayList.html";

    public WL_DE_BackgroundMusic(Context context, String type) {
        super(context, type);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveState) {
        View view = inflater.inflate(R.layout.device_comm_webpage, container, false);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle saveState) {
        super.onViewCreated(view, saveState);
        webView = (H5PlusWebView) view.findViewById(R.id.device_webview);
        textView = (TextView) view.findViewById(R.id.search_tv);
        textView.setVisibility(View.GONE);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
        getPlugin(controlPagePath, "WiseBGMusic", mContext);
    }

    /*设置设备图标*/
    @Override
    public Drawable getDefaultStateSmallIcon() {
        Drawable icon = getResources().getDrawable(R.drawable.device_thermost_open);
        return icon;
    }

    /*设置设备名称*/
    @Override
    public String getDefaultDeviceName() {
        String defaultName = super.getDefaultDeviceName();
        if (StringUtil.isNullOrEmpty(defaultName)) {
            defaultName = mContext.getString(cc.wulian.app.model.device.R.string.device_type_DE);
        }
        return defaultName;
    }

    /*设备列表右侧快速控制区域 控制页面*/

    @Override
    public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item, LayoutInflater inflater) {
        if (item == null) {
            item = new WL_DE_BackgroundMusic.shortCutItemForDevice(inflater.getContext());
        }
        item.setWulianDevice(this);
        return super.onCreateShortCutView(item, inflater);
    }

    /*设备控制区域的快捷键*/
    private class shortCutItemForDevice extends DeviceShortCutControlItem {

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
        if (item == null) {
            item = new WL_DE_BackgroundMusic.ShortCutControlableDeviceSelectDataItem(inflater.getContext());
        }
        item.setWulianDeviceAndSelectData(this, autoActionInfo);
        return item;
    }

    /*管家快捷控制区域*/
    private class ShortCutControlableDeviceSelectDataItem extends DeviceShortCutSelectDataItem {
        private LinearLayout controlableLineLayout = null;
        private ImageView openImageView;
        private ImageView openImageViewColor;
        private ImageView closeImageView;
        private ImageView stopImageView;

        public ShortCutControlableDeviceSelectDataItem(Context context) {
            super(context);
            controlableLineLayout = (LinearLayout) inflater.inflate(R.layout.device_short_cut_control_controlable, null);
            controlableLineLayout.findViewById(R.id.open_imageview_color_layout).setVisibility(View.GONE);
            openImageView = (ImageView) controlableLineLayout.findViewById(R.id.device_short_cut_control_open_iv);
            stopImageView = (ImageView) controlableLineLayout.findViewById(R.id.device_short_cut_control_stop_iv);
            closeImageView = (ImageView) controlableLineLayout.findViewById(R.id.device_short_cut_control_close_iv);
            openImageView.setVisibility(View.GONE);
            stopImageView.setVisibility(View.GONE);
            closeImageView.setVisibility(View.GONE);
            controlLineLayout.addView(controlableLineLayout);
        }
    }

    /*管家主功能区，以H5形式实现*/

    @Override
    public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(LayoutInflater inflater, AutoActionInfo autoActionInfo) {
        DialogOrActivityHolder holder = new DialogOrActivityHolder();
        holder.setShowDialog(false);
        if (HouseKeeperSelectControlDeviceDataFragment.isShowHouseKeeperSelectControlDeviceDataView) {
            HouseKeeperSelectControlDeviceDataFragment.isShowHouseKeeperSelectControlDeviceDataView = false;
            View contentView = inflater.inflate(R.layout.device_comm_webpage, null);
            webView = (H5PlusWebView) contentView.findViewById(R.id.device_webview);
            textView = (TextView) contentView.findViewById(R.id.search_tv);
            textView.setVisibility(View.GONE);
            if (autoActionInfo.getEpData() != null && autoActionInfo.getEpData().equals("")) {
                SmarthomeFeatureImpl.setData("kMideaBulterEpData", "");
            } else if (autoActionInfo.getEpData() != null) {
                SmarthomeFeatureImpl.setData("kMideaBulterEpData", autoActionInfo.getEpData());
            }
            SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.DEVICEID, this.devID);
            SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.GATEWAYID, this.gwID);
            Engine.bindWebviewToContainer((H5PlusWebViewContainer) this.getCurrentFragment(), webView);

            Handler handler = new Handler(Looper
                    .getMainLooper());
            handler.post(new Runnable() {
                @Override
                public void run() {
                    webView.loadUrl(housekeeperPagePath);
                }
            });
            holder.setContentView(contentView);
            HouseKeeperSelectControlDeviceDataFragment.setActionBarClickRightListener(new HouseKeeperSelectControlDeviceDataFragment.ActionBarClickRightListener() {

                @Override
                public void doSomething(AutoActionInfo autoActionInfo) {
                    String epData = SmarthomeFeatureImpl.getData("kMideaBulterEpData", "");
                    autoActionInfo.setEpData(epData);
                    SmarthomeFeatureImpl.setData("kMideaBulterEpData", epData);
                }
            });
        }
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
                                Preference.getPreferences().save30ASwichUri(uri);
                                Preference.getPreferences().save30ASwichSettingUri("file:///" + model.getFolder() + "/setting.html");
                                Handler handler = new Handler(Looper
                                        .getMainLooper());
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        webView.loadUrl(uriString);
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
}
