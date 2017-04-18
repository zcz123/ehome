package cc.wulian.smarthomev5.service.html5plus.plugins;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.hisense.hitv.hicloud.bean.global.BaseInfo;
import com.hisense.hitv.hicloud.bean.global.HiSDKInfo;
import com.hisense.hitv.hicloud.bean.wgapi.BindedHomeDeviceListReply;
import com.hisense.hitv.hicloud.bean.wgapi.Device;
import com.hisense.hitv.hicloud.bean.wgapi.DeviceStatus;
import com.hisense.hitv.hicloud.bean.wgapi.DeviceStatusReply;
import com.hisense.hitv.hicloud.bean.wgapi.HomeListReply;
import com.hisense.hitv.hicloud.bean.wgapi.MsgAndChannelsReplay;
import com.hisense.hitv.hicloud.bean.wgapi.TaskTimeReplay;
import com.hisense.hitv.hicloud.factory.HiCloudServiceFactory;
import com.hisense.hitv.hicloud.service.WgApiService;
import com.hismart.easylink.localjni.WiFiInfo;
import com.wulian.icam.wifidirect.utils.WiFiLinker;
import com.wulian.iot.Config;
import com.wulian.iot.view.device.play.PlayEagleActivity;
import com.wulian.iot.view.device.setting.SetEagleCameraActivity;
import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.UserRightUtil;
import cc.wulian.h5plus.common.Engine;
import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.h5plus.view.H5PlusWebView;
import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.core.http.HttpManager;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.MD5Util;
import cc.wulian.ihome.wan.util.ResultUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.account.BindGateWayActivity;
import cc.wulian.smarthomev5.account.WLUserManager;
import cc.wulian.smarthomev5.activity.AccountInformationSettingManagerActivity;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.activity.IActivityCallerWithResult;
import cc.wulian.smarthomev5.activity.MainApplication;
import cc.wulian.smarthomev5.activity.MainHomeActivity;
import cc.wulian.smarthomev5.activity.NFCActivity;
import cc.wulian.smarthomev5.activity.QuickEditActivity;
import cc.wulian.smarthomev5.activity.QRScanActivity;
import cc.wulian.smarthomev5.activity.RouteRemindActivity;
import cc.wulian.smarthomev5.activity.SigninActivityV5;
import cc.wulian.smarthomev5.activity.iotc.config.IOTCDevConfigActivity;
import cc.wulian.smarthomev5.activity.uei.CustomRemooteControlActivity;
import cc.wulian.smarthomev5.activity.uei.MatchControlActivity;
import cc.wulian.smarthomev5.activity.uei.UeiMatchActivity;
import cc.wulian.smarthomev5.dao.Command406_Dao;
import cc.wulian.smarthomev5.dao.Command406_DeviceConfigMsg;
import cc.wulian.smarthomev5.entity.Command406Result;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.entity.haixin.CloudRequest;
import cc.wulian.smarthomev5.entity.haixin.DeviceWifiSet;
import cc.wulian.smarthomev5.entity.uei.UEIEntity;
import cc.wulian.smarthomev5.entity.uei.UEIEntityManager;
import cc.wulian.smarthomev5.eyecat.EyecatWIFISettingOneActivity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.fragment.setting.gateway.AccountInformationSettingManagerFragment;
import cc.wulian.smarthomev5.fragment.uei.UeiAirDataUtil;
import cc.wulian.smarthomev5.fragment.uei.UeiMatchFragment;
import cc.wulian.smarthomev5.fragment.uei.UeiOnlineUtil;
import cc.wulian.smarthomev5.service.MainService;
import cc.wulian.smarthomev5.service.html5plus.core.Html5PlusWebViewActvity;
import cc.wulian.smarthomev5.service.html5plus.plugins.FileDownload.FileDownloadCallBack;
import cc.wulian.smarthomev5.service.html5plus.plugins.PhotoSelector.PhotoSelectCallback;
import cc.wulian.smarthomev5.thirdparty.uei_yaokan.YkanIRInterfaceImpl;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.tools.UpdateCameraInfo;
import cc.wulian.smarthomev5.tools.WulianCloudURLManager;
import cc.wulian.smarthomev5.tools.configure.UserFileConfig;
import cc.wulian.smarthomev5.utils.DrawableUtil;
import cc.wulian.smarthomev5.utils.FileUtil;
import cc.wulian.smarthomev5.utils.HttpUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;
import cc.wulian.smarthomev5.utils.URLConstants;
import cc.wulian.smarthomev5.utils.WifiUtil;

import static cc.wulian.app.model.device.impls.configureable.ir.WL_23_IR_Control.pluginName;

public class SmarthomeFeatureImpl {
    public static final class Constants {
        public static final String IS_LOGIN = "is_login";
        public static final String ACCOUNT = "account";
        public static final String USERID = "userID";
        public static final String USERNAME = "userName";
        public static final String TOKEN = "token";
        public static final String DISTRICT = "mxzh_district";
        public static final String TEXT_WILL_SHOW = "text_will_show";
        public static final String GATEWAYID = "gwID";
        public static final String DEVICEID = "devID";
        public static final String EP = "ep";
        public static final String DEVICEPARAM = "deviceparam";
        public static final String EPTYPE = "epType";
        public static final String EPDATA = "epData";
        public static final String NICKNAME = "nickName";
        public static final String COUNT_VALUE = "count";
        public static final String COUNT_TEMPUTURE = "temputure";
        public static final String COUNT_HUMIDITY = "humidity";
        public static final String UNIT_DOU = ",";
        public static final String MD5PWD = "md5pwd";

        public static final String AMS_URLBASE = "_AMS_urlbase";
        public static final String PARAM_DIGEST_URLBASE = "_AMS_digest_urlbase";
        public static final String FILE_ADDRESS = "_FILE_ADDRESS";
        public static final String DEMO_CLOUD_ADDRESS = "testdemo.wulian.cc";

        // add_by_yanzy_at_2016-5-20:被授权的网关
        public static final String BIND_TYPE_AUTHORIZED = "0";
        // add_by_yanzy_at_2016-5-20:绑定的网关
        public static final String BIND_TYPE_ADMIN = "1";
        // add_by_yanzy_at_2016-5-20:DEMO模式的网关
        public static final String BIND_TYPE_DEMO = "2";
    }

    private static GatewayInfo gatewayInfo;
    private static DeviceCache deviceCache;
    public static String callbackid = null;
    public static H5PlusWebView pWebview = null;
    private WLDialog mMessageDialog = null;

    public static final String PREFERENCE_HTML5_PLUS = "preference_html5_plus";
    private static Map<String, String> params = new HashMap<String, String>();
    private static Map<String, String> cookies = new HashMap<String, String>();
    private static MainApplication application = MainApplication.getApplication();
    private static SharedPreferences sharedPref = null;
    private static SharedPreferences.Editor sharedEditor = null;
    private Class<?> clzz = R.string.class;
    private static Map<String, ActivityInfo> activitys = new HashMap<String, ActivityInfo>();
    private Handler mainHandler = new Handler(Looper.getMainLooper());

    public static String mCallBackId;
    public static H5PlusWebView mWebview;
    public static boolean isAddAreaHere = false;
    public static boolean isGwLocationSet = false;

    private static class ActivityInfo {
        public Class<? extends Activity> activityClass;
        public boolean finishHtml = false;
    }

    private static void registerActivity(String name, Class<? extends Activity> activiyClass, boolean finishHtml) {
        ActivityInfo info = new ActivityInfo();
        info.activityClass = activiyClass;
        info.finishHtml = finishHtml;
        activitys.put(name, info);
    }

    static {
        sharedPref = application.getSharedPreferences(PREFERENCE_HTML5_PLUS, Context.MODE_PRIVATE);
        sharedEditor = sharedPref.edit();
        params.put("_digest_username", application.getResources().getString(cc.wulian.smarthomev5.R.string.auth_ams_digest_username));
        params.put("_digest_password", application.getResources().getString(cc.wulian.smarthomev5.R.string.auth_ams_digest_password));
        params.put(Constants.AMS_URLBASE, URLConstants.AMS_URLBASE_VALUE);
        params.put(Constants.FILE_ADDRESS, URLConstants.FILE_ADDRESS_VALUE);
        params.put(Constants.PARAM_DIGEST_URLBASE, URLConstants.AMS_DIGEST_URLBASE_VALUE);

        registerActivity("login", SigninActivityV5.class, true);
        registerActivity("home", MainHomeActivity.class, true);
        registerActivity("controlCenter", AccountInformationSettingManagerActivity.class, false);
        registerActivity("bindGateway", BindGateWayActivity.class, false);
        registerActivity("yikangWifiSetting", EyecatWIFISettingOneActivity.class, false);
        registerActivity("matchControl", MatchControlActivity.class, false);
        registerActivity("customRemoteControlActivity", CustomRemooteControlActivity.class, false);
//		registerActivity("TVRemoteControlActivity", TVRemooteControlActivity.class, false);
    }

    @JavascriptInterface
    public String getLanguage(H5PlusWebView pWebview, final String data) {
        JSONObject result = new JSONObject();
        try {
            JSONArray array = new JSONArray(data);

            for (int i = 0; i < array.length(); i++) {
                String key = array.getString(i);
                result.put(key, application.getString(clzz.getField(key).getInt(null)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result.toString();
    }

    @JavascriptInterface
    public void closeWebview(H5PlusWebView pWebview, final String data) {
        //TODO::可能需要关闭activity
        pWebview.close();
        ((Activity)pWebview.getContext()).finish();
    }

    @JavascriptInterface
    public String getCurrentLanguag(H5PlusWebView pWebview, final String data) {
        String v = null;
        try {
            v = LanguageUtil.getWulianCloudLanguage();
        } catch (Exception e) {
            Log.e("", "", e);
        }
        return v;
    }


    @JavascriptInterface
    public String getLang(H5PlusWebView pWebview, final String data) {
        String v = null;
        try {
            JSONArray array = new JSONArray(data);
            if (array.length() == 0) {
                v = LanguageUtil.getWulianCloudLanguage();
            } else {
                String name = array.optString(0);
                int i = (Integer) cc.wulian.smarthomev5.R.string.class.getField(name).get(null);
                v = application.getResources().getString(i);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (StringUtil.isNullOrEmpty(v)) {
            try {
                JSONArray array = new JSONArray(data);
                String name = array.optString(0);
                return name;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return v;
    }

    @JavascriptInterface
    public String getData(H5PlusWebView pWebview, final String data) {
        try {
            JSONArray array = new JSONArray(data);
            String name = array.optString(0);
            // String v = getData(name);
            // return JSUtil.wrapJsVar(v);
            return getData(name);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getData(String name) {
        String v = "";
        v = getData(name, v);
        return v;
    }

    public static String getData(String name, String v) {
        if (SmarthomeFeatureImpl.cookies.containsKey(name)) {
            v = SmarthomeFeatureImpl.cookies.get(name);
        } else if (sharedPref.contains(name)) {
            v = sharedPref.getString(name, "");
            SmarthomeFeatureImpl.cookies.put(name, v);
        } else if (params.containsKey(name)) {
            v = params.get(name);
        }
        return v;
    }

    @JavascriptInterface
    public String setData(H5PlusWebView pWebview, final String data) {
        try {
            JSONArray array = new JSONArray(data);
            String name = array.optString(0);
            String value = "";
            if (!array.isNull(1)) {
                value = array.optString(1);
            }
            SmarthomeFeatureImpl.setData(name, value);
            return "true";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "false";
    }

    public static void setData(String name, String value) {
        SmarthomeFeatureImpl.cookies.put(name, value);
        sharedEditor.putString(name, value);
        sharedEditor.commit();
    }

    @JavascriptInterface
    public void selectPhoto(final H5PlusWebView pWebview, final String data) {
        try {
            final JSONArray array = new JSONArray(data);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    PhotoSelector selector = new PhotoSelector((IActivityCallerWithResult) pWebview.getContainer(),
                            UserFileConfig.getInstance().getUserPath() + '/' + UserFileConfig.HEAD_ICON);
                    final String CallBackID = array.optString(0);
                    selector.setPhotoSelectCallback(new PhotoSelectCallback() {
                        @Override
                        public void doWhatOnSuccess(String path) {
                            JsUtil.getInstance().execCallback(pWebview, CallBackID, path, JsUtil.OK, false);
                        }

                        @Override
                        public void doWhatOnFailed(Exception e) {
                            JsUtil.getInstance()
                                    .execCallback(pWebview, CallBackID, e.getMessage(), JsUtil.ERROR, false);
                        }
                    });
                    selector.iniPopupWidow(pWebview);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void uploadFile(final H5PlusWebView pWebview, final String data) {

        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String CallBackID = "";
                try {
                    final JSONArray array = new JSONArray(data);
                    CallBackID = array.optString(0);
                    String filepath = array.optString(1);
                    UserFileConfig userFileConfig = UserFileConfig.getInstance();
                    String ret = userFileConfig.uploadUserFile(filepath);
                    JsUtil.getInstance().execCallback(pWebview, CallBackID, ret, JsUtil.OK, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JsUtil.getInstance().execCallback(pWebview, CallBackID, e.toString(), JsUtil.ERROR, true);
                }
            }
        });
    }

    @JavascriptInterface
    public void downloadFile(final H5PlusWebView pWebview, final String data) {
        String callBackID = "";
        try {
            JSONArray array = new JSONArray(data);
            final String filename = array.optString(1);
            callBackID = array.optString(0);
            final String mCallBackID = callBackID;
            TaskExecutor.getInstance().execute(new Runnable() {

                @Override
                public void run() {
                    UserFileConfig config = UserFileConfig.getInstance();
                    config.downloadFileToFolder("temp_head.png", new FileDownloadCallBack() {
                        @Override
                        public void doWhatOnSuccess(String path) {
                            Bitmap bitmap = BitmapFactory.decodeFile(path);
                            int bitmapSize = bitmap.getRowBytes() * bitmap.getHeight();
                            if (!(bitmapSize > 10)) {// 对从服务器获取的图片大小判断
                                // ，若为空改变path的值
                                path = "";
                            }
                            JsUtil.getInstance().execCallback(pWebview, mCallBackID, path, JsUtil.OK, true);
                        }

                        @Override
                        public void doWhatOnFailed(Exception e) {
                            JsUtil.getInstance().execCallback(pWebview, mCallBackID, e.getMessage(), JsUtil.ERROR,
                                    false);
                        }
                    });
                }
            });
        } catch (JSONException e1) {
            JsUtil.getInstance().execCallback(pWebview, callBackID, e1.getMessage(), JsUtil.ERROR, false);
        }
    }

    @JavascriptInterface
    public void interfaceBetweenHttpAndCloud(final H5PlusWebView pWebview, final String data) {
        try {
            TaskExecutor.getInstance().execute(new Runnable() {
                final JSONArray array = new JSONArray(data);

                @Override
                public void run() {
                    String CallBackID = array.optString(0);
                    try {
                        JSONObject params = new JSONObject(array.optString(1));
                        String url = params.optString("uri", "");
                        if (params.optBoolean("dynamic", false)) {
                            url = "https://" + WulianCloudURLManager.getBaseUrl() + "/" + url;
                        }
                        com.alibaba.fastjson.JSONObject requestBody = com.alibaba.fastjson.JSONObject
                                .parseObject(params.optString("body", ""));
                        com.alibaba.fastjson.JSONObject response = HttpUtil.postWulianCloudOrigin(url, requestBody);
                        String responseBody = response.getString("body");
                        if (responseBody == null) {
                            JsUtil.getInstance().execCallback(
                                    pWebview,
                                    CallBackID,
                                    MainApplication.getApplication().getString(
                                            cc.wulian.smarthomev5.R.string.html_user_operation_failed), JsUtil.ERROR,
                                    false);
                        } else {
                            JsUtil.getInstance().execCallback(pWebview, CallBackID, responseBody, JsUtil.OK, false);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JsUtil.getInstance()
                                .execCallback(
                                        pWebview,
                                        CallBackID,
                                        MainApplication.getApplication().getString(
                                                cc.wulian.smarthomev5.R.string.html_user_operation_failed),
                                        JsUtil.ERROR, false);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 显示等待
    @JavascriptInterface
    public void showWaiting(final H5PlusWebView pWebview, final String data) {
        try {
            final JSONArray array = new JSONArray(data);
            final String key = array.optString(0);
//            Log.d("Waiting", "显示等待=" + key);
            pWebview.post(new Runnable() {

                @Override
                public void run() {
                    ProgressDialogManager.getDialogManager().showDialog(key, pWebview.getContext(), null, null);
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 关闭等待
    @JavascriptInterface
    public void closeWaiting(final H5PlusWebView pWebview, final String data) {
        try {
            final JSONArray array = new JSONArray(data);
            final ProgressDialogManager dialogManager = ProgressDialogManager.getDialogManager();
            final String key = array.optString(0);
            Log.d("Waiting", "关闭等待=" + key);
            pWebview.post(new Runnable() {

                @Override
                public void run() {
                    if (dialogManager.containsDialog(key))
                        dialogManager.dimissDialog(key, 0);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 显示等待
    @JavascriptInterface
    public void sendControlDevice(final H5PlusWebView pWebview, final String data) {
        try {
            final JSONArray array = new JSONArray(data);
            TaskExecutor.getInstance().execute(new Runnable() {

                @Override
                public void run() {
                    Log.d("loadData", "SmarthomeFeatureImpl.sendControlDevice 1 data="+data);
                    final String callBackID = array.optString(0);
                    try {
                        Log.d("loadData", "SmarthomeFeatureImpl.sendControlDevice 2");
                        com.alibaba.fastjson.JSONObject msgBody = com.alibaba.fastjson.JSONObject.parseObject(array
                                .optString(1));
                        String gwID = msgBody.getString(Constants.GATEWAYID);
                        JsUtil.getInstance().putCallback(callBackID, pWebview);
                        FeatureImplSendCommand sendDevMsg = new FeatureImplSendCommand(pWebview, callBackID);
                        String epType = msgBody.getString(Constants.EPTYPE);
                        sendDevMsg.SetCurrType(epType);
                        sendDevMsg.SendDevMsg(gwID, msgBody);
                        Log.d("loadData", "SmarthomeFeatureImpl.sendControlDevice 3");
//						JsUtil.getInstance().execCallback(pWebview, callBackID, "", JsUtil.OK, true);
                    } catch (Exception e) {
                        Log.e("loadData", "SmarthomeFeatureImpl.sendControlDevice 4 error="+e.toString());
                        e.printStackTrace();
                        String result = MainApplication.getApplication().getString(
                                cc.wulian.smarthomev5.R.string.html_user_operation_failed);
                        JsUtil.getInstance().execCallback(pWebview, callBackID, result, JsUtil.ERROR, true);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 获取图片的base64 编码
    @JavascriptInterface
    public void getPicData(final H5PlusWebView pWebview, final String data) {
        try {
            final JSONArray array = new JSONArray(data);
            TaskExecutor.getInstance().execute(new Runnable() {

                @Override
                public void run() {
                    final String callBackID = array.optString(0);
                    String param = array.optString(1);
                    try {
                        // Drawable drawable =
                        // application.getResources().getDrawable(R.drawable.class.getField(param).getInt(null));
                        Drawable drawable = application.getResources().getDrawable(
                                cc.wulian.smarthomev5.R.drawable.class.getField(param).getInt(null));
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        BitmapDrawable bd = (BitmapDrawable) drawable;
                        Bitmap bitmap = bd.getBitmap();
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                        String data = android.util.Base64.encodeToString(os.toByteArray(), android.util.Base64.DEFAULT);
                        JsUtil.getInstance().execCallback(pWebview, callBackID, data, JsUtil.OK, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JsUtil.getInstance().execCallback(pWebview, callBackID, "", JsUtil.OK, false);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void getDevicePictureData(final H5PlusWebView pWebview, final String param) {
        try {
            final JSONArray array = new JSONArray(param);
            TaskExecutor.getInstance().execute(new Runnable() {

                @Override
                public void run() {
                    final String callBackID = array.optString(0);
                    String gwID = array.optString(1);
                    String devID = array.optString(2);
                    try {
                        WulianDevice device = DeviceCache.getInstance(application).getDeviceByID(application, gwID,
                                devID);
                        Drawable drawable = device.getDefaultStateSmallIcon();
                        ByteArrayOutputStream os = new ByteArrayOutputStream();
                        Bitmap bitmap = DrawableUtil.drawableToBitmap(drawable);
                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                        String data = android.util.Base64.encodeToString(os.toByteArray(), android.util.Base64.DEFAULT);
                        JsUtil.getInstance().execCallback(pWebview, callBackID, data, JsUtil.OK, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JsUtil.getInstance().execCallback(pWebview, callBackID, "", JsUtil.ERROR, false);
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // 显示等待
    @JavascriptInterface
    public void startActivity(final H5PlusWebView pWebview, final String result) {
        try {
            final JSONArray array = new JSONArray(result);
            JSONObject data = new JSONObject(array.optString(0));
            String action = data.getString("action");
            ActivityInfo aInfo = activitys.get(action);
            if (aInfo != null) {
                this.mWebview = pWebview;
                Class<? extends Activity> activity = aInfo.activityClass;
                Intent intent = new Intent(pWebview.getContext(), activity);
                JSONArray params = data.getJSONArray("params");
                for (int i = 0; i < params.length(); i++) {
                    JSONObject param = params.getJSONObject(i);
                    intent.putExtra(param.getString("param"), param.getString("value"));
                }
                Activity thisActivity = (Activity) pWebview.getContainer();
                thisActivity.startActivity(intent);
                if (aInfo.finishHtml) {
                    thisActivity.finish();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void popWindow(final H5PlusWebView pWebview, final String result) {
        pWebview.post(new Runnable() {

            @Override
            public void run() {

                CustomKeyPopupWindow customKeyPopupWindow = new CustomKeyPopupWindow(pWebview, result);
                customKeyPopupWindow.showAtLocation(pWebview, Gravity.BOTTOM
                        | Gravity.CENTER_HORIZONTAL, 0, 0);

            }
        });
    }

    @JavascriptInterface
    public void myDialog(final H5PlusWebView pWebview, final String result) {
        pWebview.post(new Runnable() {

            @Override
            public void run() {
                JSONArray array;
                try {
                    array = new JSONArray(result);
                    final String callBackId = array.getString(0);
                    final JSONObject obj = array.getJSONObject(1);
                    Activity thisActivity = (Activity) pWebview.getContainer();
                    final WLDialog.Builder builder = new WLDialog.Builder(thisActivity);
                    View rootView = View.inflate(thisActivity, cc.wulian.smarthomev5.R.layout.setname_dialog,
                            null);
                    final EditText edit = (EditText) rootView.findViewById(cc.wulian.smarthomev5.R.id.et_newname);
                    edit.setHint(obj.getString("hint"));
                    if (!obj.optString("name").equals("")) {
                        edit.setText(obj.getString("name"));
                    }
                    if (obj.getString("title").endsWith(application.getResources().getString(cc.wulian.smarthomev5.R.string.login_gateway_password_hint))) {
                        edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    builder.setContentView(rootView)

                            .setPositiveButton(obj.getString("ok")).setNegativeButton(obj.getString("cancel"))
                            .setTitle(obj.getString("title")).setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View contentViewLayout) {
                            if (edit.getText().toString().length() != 0) {
                                JsUtil.getInstance().execCallback(pWebview, callBackId,
                                        edit.getText().toString().trim(), JsUtil.OK, true);
                            } else {
                                try {
                                    JsUtil.getInstance().execCallback(pWebview, callBackId,
                                            obj.getString("hint"), JsUtil.OK, true);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }

                        @Override
                        public void onClickNegative(View contentViewLayout) {
                            JsUtil.getInstance().execCallback(pWebview, callBackId, "", JsUtil.ERROR, true);
                        }
                    });

                    WLDialog mMessageDialog = builder.create();
                    mMessageDialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    @JavascriptInterface
    public void alertDialog(final H5PlusWebView pWebview, final String result) {
        pWebview.post(new Runnable() {

            @Override
            public void run() {
                JSONArray array;
                try {
                    array = new JSONArray(result);
                    final String callBackId=array.getString(0);
                    final JSONObject obj = array.getJSONObject(1);
                    final WLDialog.Builder builder = new WLDialog.Builder(pWebview.getContext());
                    String title=pWebview.getContext().getString(R.string.device_songname_refresh_title);
                    builder.setTitle(title);
                    WLDialog.MessageListener positiveListener=new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View view) {
                            JsUtil.getInstance().execCallback(pWebview, callBackId,"0", JsUtil.OK, true);
                        }

                        @Override
                        public void onClickNegative(View view) {
                            JsUtil.getInstance().execCallback(pWebview, callBackId,"1", JsUtil.ERROR, true);
                        }
                    };
                    builder.setPositiveButton(obj.getString("ok")).setMessage(obj.getString("message")).setListener(positiveListener);
                    WLDialog mMessageDialog = builder.create();
                    mMessageDialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    // 显示等待
    @JavascriptInterface
    public void changeGateWay(final H5PlusWebView pWebview, final String result) {
        try {
            JSONArray array = new JSONArray(result);
            String callbackId = array.optString(0);
            JSONObject data = new JSONObject(array.optString(1));
            final String gwID = data.getString("gwID");
            this.pWebview = pWebview;
            this.callbackid = callbackId;
            String gwPwd1 = data.getString("gwPwd");
            String isAdmin1 = data.getString("isAdmin");
            ArrayList<String> ips1 = null;
            if (isAdmin1.equals(Constants.BIND_TYPE_DEMO)) {
                // add_by_yanzy_at_2016-5-19:表明是demo模式
                ips1 = new ArrayList<String>();
                ips1.add(Constants.DEMO_CLOUD_ADDRESS);
                gwPwd1 = MD5Util.encrypt(gwPwd1);
                isAdmin1 = Constants.BIND_TYPE_ADMIN;
            }
            boolean isAdmin = Constants.BIND_TYPE_ADMIN.equals(isAdmin1);

            final Activity thisActivity = (Activity) pWebview.getContainer();

            if (AccountManager.getAccountManger().isSigning(gwID)) {
                thisActivity.startActivity(new Intent(thisActivity, MainHomeActivity.class));
                JsUtil.getInstance().execCallback(pWebview, callbackId, "", JsUtil.OK, true);
            } else {
                JsUtil.getInstance().putCallback(callbackId, pWebview);
            }
            AccountManager.ConnectGatewayCallback callback = new WebviewConnectGatewayCallback(thisActivity, gwID);
            AccountManager.getAccountManger().setConnectGatewayCallbackAndActivity(callback, thisActivity);
            AccountManager.getAccountManger().connectToGateway(gwID, gwPwd1, isAdmin, ips1, Preference.ENTER_TYPE_ACCOUNT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private class WebviewConnectGatewayCallback implements AccountManager.ConnectGatewayCallback {
        Activity activity = null;
        String gwid = null;

        public WebviewConnectGatewayCallback(Activity activity, String gwid) {
            this.activity = activity;
            this.gwid = gwid;
        }

        @Override
        public void connectSucceed() {
            JsUtil.getInstance().execSavedCallback("changeGateWay", "", JsUtil.OK, true);
            setData("Account" + gwid, AccountManager.getAccountManger().getmCurrentInfo().getGwPwd());
            AccountManager.getAccountManger().clearConnectGatewayCallbackAndActivity(this);
            activity.startActivity(new Intent(activity, MainHomeActivity.class));
            activity.finish();
            activity = null;
        }

        @Override
        public void connectFailed(int reason) {
            if (reason == ResultUtil.EXC_GW_PASSWORD_WRONG) {
//				AccountManager.showGatewayPasswordErrorFromAccountDialog(activity, gwid);
                JsUtil.getInstance().execSavedCallback("changeGateWay", "passworderror", JsUtil.ERROR, true);
            } else {
                JsUtil.getInstance().execSavedCallback("changeGateWay", "fail", JsUtil.ERROR, true);
            }
        }
    }

    ;


    @JavascriptInterface
    public void sendUeiCommand(final H5PlusWebView pWebview, final String param) {
        JSONArray array;
        String callBackId = "";
        try {
            array = new JSONArray(param);
            callBackId = array.getString(0);
            JSONObject data = array.getJSONObject(1);
            String command = data.getString("command");
            if ("sendToUeiHelper".equals(command)) {
                // TODO 调用UeiHelper 获取 将要发送的数据
                String result = new UeiOnlineUtil().sendToUeiHelper(data.getString("testCode"), data.getInt("testKeyId"),
                        data.getString("testCodeData"));
                JsUtil.getInstance().execCallback(pWebview, callBackId, result, JsUtil.OK, true);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            JsUtil.getInstance().execCallback(pWebview, callBackId, "", JsUtil.ERROR, true);
        }
    }

    @JavascriptInterface
    public void userLogout(final H5PlusWebView pWebview, final String param) {
        final Activity thisActivity = (Activity) pWebview.getContainer();
        Preference.getPreferences().saveAutoLoginChecked(false ,AccountManager.getAccountManger().getmCurrentInfo().getGwID());
        Intent intent = new Intent(thisActivity, Html5PlusWebViewActvity.class);
        String uri = URLConstants.LOCAL_BASEURL + "login.html";
        intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        thisActivity.startActivity(intent);
        MainService.getMainService().unregisterCurrentTopic();
        MainApplication.getApplication().stopApplication();
    }

    /**
     * 用户登录成功，更新相关信息，并且设置UserManager。 2016-6-8
     *
     * @param pWebview
     * @param param
     * @author Administrator
     */
    @JavascriptInterface
    public void userLogin(final H5PlusWebView pWebview, final String param) {
        try {
            JSONArray array = new JSONArray(param);
            String account = array.optString(0);
            String md5passwd = array.optString(1);
            String token = array.optString(2);
            userLogin(account, md5passwd, token);
            WLUserManager.getInstance().init(account, md5passwd, token);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void userLogin(String account, String md5passwd, String token) {
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.IS_LOGIN, "true");
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.ACCOUNT, account);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.TOKEN, token);
        SmarthomeFeatureImpl.setData(SmarthomeFeatureImpl.Constants.MD5PWD, md5passwd);
    }

    @JavascriptInterface
    public void getGateWayPic(final H5PlusWebView pWebview, final String param) {
        JSONArray array;
        String callBackId = "";
        InputStream is = null;
        ByteArrayOutputStream os = null;
        try {
            array = new JSONArray(param);
            callBackId = array.getString(0);
            String gwId = array.getString(1);
            String picPath = FileUtil.getGatewayDirectoryPath(gwId) + "/"
                    + AccountInformationSettingManagerFragment.PICTURE_GATEWAY_HEAD;
            File picFile = new File(picPath);
            if (picFile.exists()) {
                os = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                is = new FileInputStream(picPath);
                int readSize = 0;
                while ((readSize = is.read(buffer)) != -1) {
                    os.write(buffer, 0, readSize);
                }
                String data = android.util.Base64.encodeToString(os.toByteArray(), android.util.Base64.DEFAULT);
                JsUtil.getInstance().execCallback(pWebview, callBackId, data, JsUtil.OK, true);
            } else {
                JsUtil.getInstance().execCallback(pWebview, callBackId, "", JsUtil.ERROR, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsUtil.getInstance().execCallback(pWebview, callBackId, "", JsUtil.ERROR, true);
        }
    }

    // html 调用 弹出提示 Toast
    @JavascriptInterface
    public void toast(final H5PlusWebView pWebview, final String param) {
        pWebview.post(new Runnable() {

            @Override
            public void run() {
                Toast.makeText(application, param, Toast.LENGTH_SHORT).show();
            }
        });
    }

    // add_by_sws
    @JavascriptInterface
    public void getCurrentDeviceData(final H5PlusWebView pWebview, final String devID) {
        String callBackId = "";
        try {
            com.alibaba.fastjson.JSONArray array = JSON.parseArray(devID);
            callBackId = array.getString(0);
            String devIDString = array.getString(1);
            AccountManager accountManager = AccountManager.getAccountManger();
            gatewayInfo = accountManager.getmCurrentInfo();
            deviceCache = DeviceCache.getInstance(application);
            String gwID = gatewayInfo.getGwID();
            WulianDevice wulianDevice = deviceCache.getDeviceByID(application, gwID, devIDString);
            wulianDevice.registerEPDataToHTML(pWebview, callBackId);
        } catch (Exception e) {
            JsUtil.getInstance().execCallback(pWebview, callBackId, "0", JsUtil.OK, false);
        }
    }

    /**
     * 下载UEI-空调码库
     *
     * @param pWebview
     * @param webparam
     */
    @JavascriptInterface
    public void saveUeiAirData(final H5PlusWebView pWebview, final String webparam) {
        JSONArray array;
        String callBackId = "";
        UeiMatchFragment.pWebview = null;
        try {
            array = new JSONArray(webparam);
            callBackId = array.getString(0);
            JSONArray dataarray = new JSONArray(array.getString(1));
            String data_brandCode = dataarray.getString(0);//品牌编码
            String data_brandName = dataarray.getString(1);//品牌名称
            String data_brandType = dataarray.getString(2);//品牌类型
            JSONArray data_libCode = dataarray.getJSONArray(3);//码库编码
            String gwID = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.GATEWAYID);
            String devID = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.DEVICEID);
            //若码库编码为空，则根据data_brandCode直接下载码库并发送406保存
            if (data_libCode == null || data_libCode.length() == 0) {
                UeiAirDataUtil ueiAirData = new UeiAirDataUtil();
                ueiAirData.setIsFirstReadLocal(false);
                ueiAirData.setdevID(devID);
                String airData = ueiAirData.getAirData(data_brandCode);
                if (!StringUtil.isNullOrEmpty(airData)) {
                    Command406_DeviceConfigMsg command406 = new Command406_DeviceConfigMsg();
                    command406.setGwID(gwID);
                    command406.setDevID(devID);
                    JSONObject json_addData = new JSONObject();
                    json_addData.put("m", data_brandType);
                    json_addData.put("b", data_brandName);
                    json_addData.put("nm", "");
                    json_addData.put("v", "");
                    command406.SendCommand_Add("3_" + data_brandCode, json_addData.toString());
                    JsUtil.getInstance().execCallback(pWebview, callBackId, "", JsUtil.OK, true);
                } else {
                    JsUtil.getInstance().execCallback(pWebview, callBackId, "", JsUtil.ERROR, true);
                }
            } else {
                //将弹出Activity
                StringBuilder strLibCode = new StringBuilder();
                for (int i = 0; i < data_libCode.length(); i++) {
                    if (i != 0) {
                        strLibCode.append("," + data_libCode.get(i));
                    } else {
                        strLibCode.append(data_libCode.get(i));
                    }
                }
                if (strLibCode.length() > 0) {
                    UeiMatchFragment.pWebview = pWebview;
                    Intent intent = new Intent(pWebview.getContext(), UeiMatchActivity.class);
                    Bundle args = new Bundle();
                    args.putString("libCodes", strLibCode.toString());
                    args.putString("callBackId", callBackId);
                    args.putString("gwID", gwID);
                    args.putString("devID", devID);
                    intent.putExtra("args", args);
                    Activity thisActivity = (Activity) pWebview.getContainer();
                    thisActivity.startActivity(intent);
                } else {
                    JsUtil.getInstance().execCallback(pWebview, callBackId, "", JsUtil.ERROR, true);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsUtil.getInstance().execCallback(pWebview, callBackId, "", JsUtil.ERROR, true);
        }
    }

    @JavascriptInterface
    public void saveUeiData(final H5PlusWebView pWebview, final String param) {

        try {
            final JSONArray array = new JSONArray(param);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    final String callBackID = array.optString(0);
                    try {
                        com.alibaba.fastjson.JSONObject msgBody = com.alibaba.fastjson.JSONObject.parseObject(array.optString(1));
                        String gwID = msgBody.getString("gwID");
                        String devID = msgBody.getString("d");
                        String time = AccountManager.getAccountManger().getmCurrentInfo().getTime();
                        String configurationItem = msgBody.getString("k");
                        String value = msgBody.getString("v");
                        System.out.println("value=" + value);
                        JsUtil.getInstance().putCallback(callBackID, pWebview);
                        NetSDK.sendCommonDeviceConfigMsg(gwID, devID, "1", time, configurationItem, value);
                        JsUtil.getInstance().execCallback(pWebview, callBackID, "", JsUtil.OK, true);
                    } catch (Exception e) {
                        e.printStackTrace();
                        JsUtil.getInstance().execCallback(pWebview, callBackID, "", JsUtil.ERROR, true);
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 查询同一码库是否已存在
     *
     * @param pWebview 当前的webView
     * @param param    传入的参数
     */
    @JavascriptInterface
    public void checkedUeiIsExists(final H5PlusWebView pWebview, final String param) {
        try {
            final JSONArray array = new JSONArray(param);
            new Thread(new Runnable() {
                @Override
                public void run() {
                    final String callBackID = array.optString(0);
                    try {
                        com.alibaba.fastjson.JSONObject msgBody = com.alibaba.fastjson.JSONObject.parseObject(array.optString(1));
                        String deviceCode = msgBody.getString("deviceCode");
                        String gwID = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.GATEWAYID);
                        String devID = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.DEVICEID);
                        String msg = "";
                        Command406_Dao command406Dao = new Command406_Dao();
                        List<Command406Result> resultList = command406Dao.GetItemsByKey(gwID, devID, deviceCode);
                        if (resultList != null && resultList.size() > 0) {
                            Command406Result result = resultList.get(0);
                            UEIEntity ueientity = UEIEntityManager.CreateUEIEnitity(result.getKey());
                            ueientity.setValue(result.getData());
                            if (ueientity != null) {
                                msg = "已存在同型号遥控器 " + ueientity.getBrandName() + " " + ueientity.getBrandType();
                            }
                        }
                        JsUtil.getInstance().putCallback(callBackID, pWebview);
                        if (!StringUtil.isNullOrEmpty(msg)) {
                            toast(pWebview, msg);
                            JsUtil.getInstance().execCallback(pWebview, callBackID, "true", JsUtil.OK, true);
                        } else {
                            JsUtil.getInstance().execCallback(pWebview, callBackID, "false", JsUtil.OK, true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        JsUtil.getInstance().execCallback(pWebview, callBackID, "", JsUtil.ERROR, true);
                    }
                }
            }).start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void jump(final H5PlusWebView pWebview, final String data) {
        Intent intent = new Intent(pWebview.getContext(), RouteRemindActivity.class);
        intent.putExtra(NFCActivity.IS_EXECUTE, false);
        Activity thisActivity = (Activity) pWebview.getContainer();
        thisActivity.startActivity(intent);
    }

    @JavascriptInterface
    public void getWifiInfo(final H5PlusWebView pWebview, final String data) {
        com.alibaba.fastjson.JSONArray array = JSON.parseArray(data);
        String callBackID = array.getString(0);
        try {
            FeatureImplSendCommand sendDevMsg = new FeatureImplSendCommand(pWebview, callBackID);
            sendDevMsg.getWifiInfoFromGateway();
        } catch (Exception e) {
            JsUtil.getInstance().execCallback(pWebview, callBackID, "", JsUtil.ERROR, true);
        }
    }

    @JavascriptInterface
    public void getZigbeeInfo(final H5PlusWebView pWebview, final String data) {
        com.alibaba.fastjson.JSONArray array = JSON.parseArray(data);
        String callBackID = array.getString(0);
        try {
            FeatureImplSendCommand sendDevMsg = new FeatureImplSendCommand(pWebview, callBackID);
            sendDevMsg.getZigbeeInfo();
        } catch (Exception e) {
            JsUtil.getInstance().execCallback(pWebview, callBackID, "", JsUtil.ERROR, true);
        }
    }

    /**
     * 学习键
     *
     * @param pWebview webvie控件
     * @param webparam 参数
     */
    @JavascriptInterface
    public void achieveUeiLearnIndex(final H5PlusWebView pWebview, final String webparam) {
        JSONArray array;
        String callBackId = "";
        try {
            array = new JSONArray(webparam);
            callBackId = array.getString(0);
            String gwID = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.GATEWAYID);
            String devID = SmarthomeFeatureImpl.getData(SmarthomeFeatureImpl.Constants.DEVICEID);
            if (!StringUtil.isNullOrEmpty(callBackId)) {
                FeatureImplSendCommand sendCommand = new FeatureImplSendCommand(pWebview, callBackId);
                sendCommand.SetCurrType("23");
                sendCommand.getUeiLearnIndex(gwID, devID);
//				NetSDK.sendCommonDeviceConfigMsg(gwID, devID, "3", System.currentTimeMillis()+"", "LearnIndex", null);
            } else {
                JsUtil.getInstance().execCallback(pWebview, callBackId, "", JsUtil.ERROR, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
            JsUtil.getInstance().execCallback(pWebview, callBackId, "", JsUtil.ERROR, true);
        }
    }

    @JavascriptInterface
    public void controlDevice(final H5PlusWebView pWebview, final String webparam) {
        com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
        String callBackId = array.getString(0);
        String devID = array.getString(1);
        String param = array.getString(2);
        pWebview.setCallbackID(callBackId);
        gatewayInfo = AccountManager.getAccountManger().getmCurrentInfo();
        deviceCache = DeviceCache.getInstance(application);
        String gwID = gatewayInfo.getGwID();
        WulianDevice wulianDevice = deviceCache.getDeviceByID(application, gwID, devID);
        wulianDevice.registerEPDataToHTML(pWebview, callBackId,"13");
        SendMessage.sendControlDevMsg(gwID, devID, "14", "DD", param);
    }

    //隐藏当前视图的ActionBar
    @JavascriptInterface
    public void hideNavigationBar(final H5PlusWebView pWebview, final String webparam) {
        doSomeThingInUIThread(new HandlerListener() {

            @Override
            public void doSomeThingInUIThread() {
                MainApplication application = MainApplication.getApplication();
                List<BaseActivity> activities = application.getActivities();
                BaseActivity activity = activities.get(activities.size() - 1);
                activity.getCompatActionBar().hide();
            }
        });
    }

    //展示当前视图的ActionBar
    @JavascriptInterface
    public void showNavigationBar(final H5PlusWebView pWebview, final String webparam) {
        doSomeThingInUIThread(new HandlerListener() {

            @Override
            public void doSomeThingInUIThread() {
                MainApplication application = MainApplication.getApplication();
                List<BaseActivity> activities = application.getActivities();
                BaseActivity activity = activities.get(activities.size() - 1);
                activity.getCompatActionBar().show();
            }
        });
    }

    public interface HandlerListener {
        void doSomeThingInUIThread();
    }

    private void doSomeThingInUIThread(final HandlerListener handleListener) {
        mainHandler.post(new Runnable() {

            @Override
            public void run() {
                handleListener.doSomeThingInUIThread();
            }
        });
    }

    @JavascriptInterface
    public void EPGHttpRequset(final H5PlusWebView pWebview, final String str) {
        String callBackId = "";
        try {
            com.alibaba.fastjson.JSONArray array = JSON.parseArray(str);
            callBackId = array.getString(0);
            String jsonDic = array.getString(1);
            Log.d("EPGHttpRequset", "EPGHttpRequset: " + str);
            String result = "";
            if (!StringUtil.isNullOrEmpty(jsonDic)) {
                com.alibaba.fastjson.JSONObject jsonData = JSON.parseObject(jsonDic);
                String api = jsonData.getString("api");
                com.alibaba.fastjson.JSONArray jsonParams = jsonData.getJSONArray("param");
                List<String> params = new ArrayList<>();
                for (int i = 0; i < jsonParams.size(); i++) {
                    params.add(jsonParams.getString(i));
                }
                YkanIRInterfaceImpl ykanImpl = new YkanIRInterfaceImpl();
                result = ykanImpl.HttpUtil_postMethod(api, params);
            }
            JsUtil.getInstance().execCallback(pWebview, callBackId, result, JsUtil.OK, false);

        } catch (Exception e) {
            JsUtil.getInstance().execCallback(pWebview, callBackId, "0",
                    JsUtil.ERROR, false);
        }
    }

    //解绑子网关
    @JavascriptInterface
    public void unbindSubGW(final H5PlusWebView pWebview, final String webparam) {
        try {
            com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
            String callBackId = array.getString(0);
            String gwID = array.getString(1);
            this.mCallBackId = callBackId;
            this.mWebview = pWebview;
            NetSDK.managerChildGateway(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), gwID, "", 2 + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //绑定子网关
    @JavascriptInterface
    public void bindSubGW(final H5PlusWebView pWebview, final String webparam) {
        try {
            com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
            String callBackId = array.getString(0);
            String gwID = array.getString(1);
            String psd = array.getString(2);
            psd = MD5Util.encrypt(psd);
            this.mCallBackId = callBackId;
            this.mWebview = pWebview;
            NetSDK.managerChildGateway(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), gwID, psd, 1 + "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //获取管理的子网关
    @JavascriptInterface
    public void getManagerSubGW(final H5PlusWebView pWebview, final String webparam) {
        try {
            com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
            String callBackId = array.getString(0);
            this.mCallBackId = callBackId;
            this.mWebview = pWebview;
        } catch (Exception e) {
            e.printStackTrace();
        }
        NetSDK.queryChildGatewayList(AccountManager.getAccountManger().getmCurrentInfo().getGwID());
    }

    //获取所有的区域
    @JavascriptInterface
    public void getAllArea(final H5PlusWebView pWebview, final String webparam) {
        try {
            com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
            String callBackId = array.getString(0);
            com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
            List<DeviceAreaEntity> areaEntityList = AreaGroupManager.getInstance().getDeviceAreaEnties();
            int areaEntityListSize = areaEntityList.size();
            for (int j = 0; j < areaEntityListSize; j++) {
                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                jsonObject.put("areaID", areaEntityList.get(j).getRoomID());
                jsonObject.put("areaName", areaEntityList.get(j).getName());
                jsonArray.add(jsonObject);
            }
            com.alibaba.fastjson.JSONObject dataJsonObject = new com.alibaba.fastjson.JSONObject();
            dataJsonObject.put("area", jsonArray);
            JsUtil.getInstance().execCallback(pWebview, callBackId, dataJsonObject.toString(), JsUtil.OK, true);
            isAddAreaHere = true;
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    //改变子网关信息
    @JavascriptInterface
    public void changeSubGWMsg(final H5PlusWebView pWebview, final String webparam) {
        try {
            com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
            String callBackId = array.getString(0);
            String gwID = array.getString(1);
            String gwName = array.getString(2);
            String gwArea = array.getString(3);
            this.mCallBackId = callBackId;
            this.mWebview = pWebview;
            NetSDK.setChildGatewayInfo(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), gwID, 2 + "", "", gwName, gwArea);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //添加区域
    @JavascriptInterface
    public void addArea(final H5PlusWebView pWebview, final String webparam) {
        try {
            com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
            String callBackId = array.getString(0);
            String areaName = array.getString(1);
            String areaIcon = array.getString(2);
            this.mCallBackId = callBackId;
            this.mWebview = pWebview;
            NetSDK.sendSetRoomMsg(AccountManager.getAccountManger().getmCurrentInfo().getGwID(), 1 + "", null, areaName, areaIcon, null);
        } catch (Exception e) {
            e.printStackTrace();
        }

//        NSString* areaName =[pars objectAtIndex:1];
//        NSString* areaIcon =[pars objectAtIndex:2];
    }

    //获取子网关信息
    @JavascriptInterface
    public void getSubGWDevice(final H5PlusWebView pWebview, final String webparam) {
        //TODO ===============================
        String callBackId = "";
        try {
            com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
            callBackId = array.getString(0);
            String subGWID = array.getString(1);
            this.mCallBackId = callBackId;
            this.mWebview = pWebview;
            com.alibaba.fastjson.JSONArray jsonArray = new com.alibaba.fastjson.JSONArray();
            deviceCache = DeviceCache.getInstance(application);
            for (WulianDevice wulianDevice : deviceCache.getAllDevice()) {
                if ((!StringUtil.isNullOrEmpty(wulianDevice.getDeviceInfo().getSubGWID())) && wulianDevice.getDeviceInfo().getSubGWID().equals(subGWID)) {
                    String devID = wulianDevice.getDeviceID();
                    String devName = wulianDevice.getDeviceName();
                    if (StringUtil.isNullOrEmpty(devName)) {
                        devName = wulianDevice.getDefaultDeviceName();
                    }
                    String devRoomName = wulianDevice.getDeviceRoomID();
                    List<DeviceAreaEntity> areaEntityList = AreaGroupManager.getInstance().getDeviceAreaEnties();
                    int areaEntityListSize = areaEntityList.size();
                    for (int j = 0; j < areaEntityListSize; j++) {
                        if (areaEntityList.get(j).getRoomID().equals(devRoomName)) {
                            devRoomName = areaEntityList.get(j).getName();
                        }
                    }
                    if (StringUtil.isNullOrEmpty(devRoomName)) {
                        devRoomName = "[" + application.getResources().getString(cc.wulian.smarthomev5.R.string.device_config_edit_dev_area_type_other_default) + "]";
                    }
                    com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                    jsonObject.put("devID", devID);
                    jsonObject.put("devName", devName);
                    jsonObject.put("devRoomName", devRoomName);
                    jsonArray.add(jsonObject);
                }
            }
            com.alibaba.fastjson.JSONObject dataJsonObject = new com.alibaba.fastjson.JSONObject();
            dataJsonObject.put("dev", jsonArray);
            JsUtil.getInstance().execCallback(pWebview, callBackId, dataJsonObject.toString(), JsUtil.OK, true);
        } catch (Exception e) {
            e.printStackTrace();
            JsUtil.getInstance().execCallback(pWebview, callBackId, "", JsUtil.ERROR, true);
        }
    }

    //改命令专门用于30A大功率开关的控制、状态查询
    @JavascriptInterface
    public void controlDeviceAi(final H5PlusWebView pWebview, final String webparam) {
        com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
        final String callBackID = array.getString(0);
        final String epData = array.getString(1);
        try {
            String gwID = AccountManager.getAccountManger().getmCurrentInfo().getGwID();
            String devID = SmarthomeFeatureImpl.getData("ai30_devid");
            String eptype = SmarthomeFeatureImpl.getData("ai30_eptype");
            String ep = SmarthomeFeatureImpl.getData("ai30_ep");
            JsUtil.getInstance().putCallback(callBackID, pWebview);
            //为设备注册H5页面
            AccountManager accountManager = AccountManager.getAccountManger();
            gatewayInfo = accountManager.getmCurrentInfo();
            deviceCache = DeviceCache.getInstance(application);
            WulianDevice wulianDevice = deviceCache.getDeviceByID(application, gwID, devID);
            wulianDevice.registerEPDataToHTML(pWebview, callBackID);

            com.alibaba.fastjson.JSONObject jsonObj = new com.alibaba.fastjson.JSONObject();
            jsonObj.put("cmd", "12");
            jsonObj.put("gwID", gwID);
            jsonObj.put("devID", devID);
            jsonObj.put("ep", ep);
            jsonObj.put("epType", eptype);
            jsonObj.put("epData", epData);
            String jsonData = jsonObj.toString();
            com.alibaba.fastjson.JSONObject msgBody = com.alibaba.fastjson.JSONObject.parseObject(jsonData);
            if (UserRightUtil.getInstance().canControlDevice(devID)) {
                NetSDK.sendDevMsg(gwID, msgBody);
            } else {
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        WLToast.showToast(
                                pWebview.getContext(),
                                pWebview.getContext().getResources().getString(
                                        cc.wulian.smarthomev5.R.string.common_no_right),
                                Toast.LENGTH_SHORT);
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            String result = MainApplication.getApplication().getString(
                    cc.wulian.smarthomev5.R.string.html_user_operation_failed);
            JsUtil.getInstance().execCallback(pWebview, callBackID, result, JsUtil.ERROR, true);
        }
    }

    //网关位置设置
    @JavascriptInterface
    public void setGWLocation(final H5PlusWebView pWebview, final String param) {
        try {
            JSONArray array = new JSONArray(param);
            String callBackId = array.getString(0);
            String cityID = array.getString(1);
            String gwID = AccountManager.getAccountManger().getmCurrentInfo().getGwID();
            NetSDK.setGatewayInfo(gwID, "2", null, null, null, null, null, null, cityID,null);
            isGwLocationSet = true;

            String url = URLConstants.AMS_URLBASE_VALUE + URLConstants.AMS_PATH + "/user/device";
            Map<String, String> headerMap = new HashMap<String, String>();
            headerMap.put("cmd", "deviceUpdate");
            headerMap.put("token", SmarthomeFeatureImpl.getData(Constants.TOKEN));
            com.alibaba.fastjson.JSONObject bodyObject = new com.alibaba.fastjson.JSONObject();
            bodyObject.put("deviceId", gwID);
            bodyObject.put("wCityId", cityID);
            byte[] body = bodyObject.toJSONString().getBytes();
            com.alibaba.fastjson.JSONObject result = HttpManager.getWulianCloudProvider().post(url, headerMap, body);

            String url2 = URLConstants.AMS_URLBASE_VALUE + URLConstants.AMS_PATH + "/user/access";
            Map<String, String> headerMap2 = new HashMap<String, String>();
            headerMap.put("cmd", "getAreaInfo");
            com.alibaba.fastjson.JSONObject bodyObject2 = new com.alibaba.fastjson.JSONObject();
            bodyObject2.put("level", "city");
            bodyObject2.put("cityId", cityID);
            byte[] body2 = bodyObject2.toJSONString().getBytes();
            com.alibaba.fastjson.JSONObject result2 = HttpManager.getWulianCloudProvider().post(url2, headerMap2, body2);
            String responseBody = result2.getString("body");

            if (!StringUtil.isNullOrEmpty(responseBody)) {
                com.alibaba.fastjson.JSONObject jsonObject = new com.alibaba.fastjson.JSONObject();
                com.alibaba.fastjson.JSONArray jsonArray = JSON.parseObject(responseBody).getJSONArray("records");
                String cityInfo = jsonArray.getString(0);
                String province = JSON.parseObject(cityInfo).getString("province");
                String city = JSON.parseObject(cityInfo).getString("cityName");
                jsonObject.put("province", province);
                jsonObject.put("city", city);
                jsonObject.put("cityID", cityID);
//                System.out.println("---ccc---"+jsonObject.toJSONString());
                setData("gw_location_city_info", jsonObject.toJSONString());
            }
            this.mCallBackId = callBackId;
            this.mWebview = pWebview;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void passVauleToWebView(final H5PlusWebView pWebview, final String param) {
        try {
            JSONArray array = new JSONArray(param);
            String webID = array.getString(0);
            String functionName = array.getString(1);
            String arg = array.getString(2);
            H5PlusWebView webview = Engine.getWebview(pWebview.getContainer(), webID);
            if (webview != null) {
                //直接调用htnl5中方法
                JsUtil.getInstance().execJSFunction(webview, "javascript:" + functionName + "(" + "\"" + arg + "\"" + ")");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void interfaceBetweenAMSHttpAndCloud(final H5PlusWebView pWebview, final String param) {
        try {
            TaskExecutor.getInstance().execute(new Runnable() {
                com.alibaba.fastjson.JSONArray array = JSON.parseArray(param);

                @Override
                public void run() {
                    try {
                        String callBackId = array.getString(0);
                        String data = array.getString(1);
                        com.alibaba.fastjson.JSONObject jsonObject = JSON.parseObject(data);
                        String body = jsonObject.getString("body");
                        String head = jsonObject.getString("head");
                        String uri = jsonObject.getString("uri");
                        // URLConstants.AMS_URLBASE_VALUE_D   "https://v2.wuliancloud.com:52182"
//                        String testUri = "https://testv2.wulian.cc:52182";
                        String url = URLConstants.AMS_URLBASE_VALUE + URLConstants.AMS_PATH + "/" + uri;

                        com.alibaba.fastjson.JSONObject headJsonObject = JSON.parseObject(JSON.parseArray(head).getString(0));
                        Map<String, String> headerMap = new HashMap<String, String>();
                        headerMap.put(headJsonObject.getString("head"), headJsonObject.getString("value"));

                        byte[] bodyByte = body.getBytes();
                        com.alibaba.fastjson.JSONObject result = HttpManager.getWulianCloudProvider().post(url, headerMap, bodyByte);
                        String responseBody = result.getString("body");
                        if (responseBody == null) {
                            JsUtil.getInstance().execCallback(pWebview, callBackId,
                                    MainApplication.getApplication().getString(
                                            cc.wulian.smarthomev5.R.string.html_user_operation_failed), JsUtil.ERROR,
                                    false);
                        } else {
                            JsUtil.getInstance().execCallback(pWebview, callBackId, responseBody, JsUtil.OK, true);
                        }
                        mCallBackId = callBackId;
                        mWebview = pWebview;
                    } catch (Exception e) {
                        e.printStackTrace();
                        JsUtil.getInstance().execCallback(pWebview, mCallBackId,
                                MainApplication.getApplication().getString(cc.wulian.smarthomev5.R.string.html_user_operation_failed),
                                JsUtil.ERROR, false);
                    }

                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void goToEyeCamera(final H5PlusWebView pWebview, final String webparam) {
        com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
        String devIDString = array.getString(1);
        Intent intent = new Intent(pWebview.getContext(), PlayEagleActivity.class);
        intent.putExtra(Config.tutkUid, devIDString);
        intent.putExtra(Config.tutkPwd, "admin");
        intent.putExtra("without", 1);
        Activity thisActivity = (Activity) pWebview.getContainer();
        thisActivity.startActivity(intent);
    }

    @JavascriptInterface
    public void addEyeCamera(final H5PlusWebView pWebview, final String webparam) {
        String callBackId = "";
        try {
            com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
            callBackId = array.getString(0);
            this.callbackid = callBackId;
            this.pWebview = pWebview;
            Intent intent = new Intent(pWebview.getContext(), IOTCDevConfigActivity.class);
            intent.putExtra(IOTCDevConfigActivity.WIFI_CONFIG_TYPE, 0);
            Activity thisActivity = (Activity) pWebview.getContainer();
            thisActivity.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void goToSetEyeCamera(final H5PlusWebView pWebview, final String webparam) {
        com.alibaba.fastjson.JSONArray array = JSON.parseArray(webparam);
        String callBackId = array.getString(0);
        String devIDString = array.getString(1);
        this.callbackid = callBackId;
        this.pWebview = pWebview;
        Intent intent = new Intent(pWebview.getContext(), SetEagleCameraActivity.class);
        SetEagleCameraActivity.setUpdateCameraName(new UpdateCameraInfo());//为了修改猫眼设备名
        intent.putExtra(Config.eagleSettingEnter, SetEagleCameraActivity.WITHOUT_CAMERA_SETTING);
        intent.putExtra(Config.tutkUid, devIDString);
        intent.putExtra(Config.tutkPwd,"admin");
        intent.putExtra(Config.isAdmin,Preference.getPreferences().getGatewayIsAdmin());
        Activity thisActivity = (Activity) pWebview.getContainer();
        thisActivity.startActivity(intent);
    }

    @JavascriptInterface
    public void myDialogWithLink(final H5PlusWebView pWebview, final String result) {
        pWebview.post(new Runnable() {

            @Override
            public void run() {
                JSONArray array;
                try {
                    array = new JSONArray(result);
                    final String callBackId = array.getString(0);
                    final JSONObject obj = array.getJSONObject(1);
                    Activity thisActivity = (Activity) pWebview.getContainer();
                    final WLDialog.Builder builder = new WLDialog.Builder(thisActivity);
                    View rootView = View.inflate(thisActivity, cc.wulian.smarthomev5.R.layout.fragment_gateway_password_error_from_account,
                            null);
                    final EditText edit = (EditText) rootView.findViewById(cc.wulian.smarthomev5.R.id.et_password);
                    final TextView textView = (TextView) rootView.findViewById(cc.wulian.smarthomev5.R.id.lb_forget_gateway_password);
                    final TextView errorHint = (TextView) rootView.findViewById(cc.wulian.smarthomev5.R.id.password_error_hint);
                    final View breakline = (View) rootView.findViewById(cc.wulian.smarthomev5.R.id.break_line);
                    breakline.setVisibility(View.VISIBLE);
                    errorHint.setVisibility(View.GONE);
                    edit.setHint(obj.getString("hint"));
                    if (!obj.optString("name").equals("")) {
                        edit.setText(obj.getString("name"));
                    }
                    if (obj.getString("title").endsWith(application.getResources().getString(cc.wulian.smarthomev5.R.string.login_gateway_password_hint))) {
                        edit.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    }
                    textView.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            JsUtil.getInstance().execCallback(pWebview, callBackId,
                                    "forgetPWD", JsUtil.ERROR, true);
                            mMessageDialog.dismiss();
                        }
                    });
                    builder.setContentView(rootView)

                            .setPositiveButton(obj.getString("ok")).setNegativeButton(obj.getString("cancel"))
                            .setTitle(obj.getString("title")).setListener(new WLDialog.MessageListener() {
                        @Override
                        public void onClickPositive(View contentViewLayout) {
                            if (edit.getText().toString().length() != 0) {
                                JsUtil.getInstance().execCallback(pWebview, callBackId,
                                        edit.getText().toString().trim(), JsUtil.OK, true);
                            } else {
                                JsUtil.getInstance().execCallback(pWebview, callBackId,
                                        "", JsUtil.OK, true);
//                                WLToast.showToast(pWebview.getContext(), application.getResources().getString(cc.wulian.smarthomev5.R.string.set_password_not_null_hint), WLToast.TOAST_SHORT);
//                                try {
//                                    JsUtil.getInstance().execCallback(pWebview, callBackId,
//                                            obj.getString("hint"), JsUtil.OK, true);
//                                } catch (JSONException e) {
//                                    e.printStackTrace();
//                                }
                            }
                        }

                        @Override
                        public void onClickNegative(View contentViewLayout) {
                            JsUtil.getInstance().execCallback(pWebview, callBackId, "", JsUtil.ERROR, true);
                        }
                    });

                    mMessageDialog = builder.create();
                    mMessageDialog.show();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }


    //获取网关类型
    @JavascriptInterface
    public String getGateWayType(final H5PlusWebView pWebview, final String webparam) {
        String gatewayType=AccountManager.getAccountManger().getmCurrentInfo().getGwType();
        if(StringUtil.isNullOrEmpty(gatewayType)){
            return "";
        }else{
            return gatewayType;
        }
    }

    @JavascriptInterface
    public void controllPush(final H5PlusWebView pWebview, final String webparam) {
        JSONArray array;
        try {
            Context thisActivity = pWebview.getContext();
            array = new JSONArray(webparam);
            String dataStr=array.getString(0);
            com.alibaba.fastjson.JSONObject jsonObject= com.alibaba.fastjson.JSONObject.parseObject(dataStr);
            String dataType=jsonObject.getString("action");
            Intent intent;
            if(dataType.equals("InstallToolQuickEdit")){
                intent = new Intent(pWebview.getContext(), QuickEditActivity.class);
                thisActivity.startActivity(intent);
            }else if(dataType.equals("InstallToolChannel")){
                getPlugin(thisActivity);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getPlugin(final Context context){
        new Thread(new Runnable() {
            @Override
            public void run() {
                PluginsManager pm=PluginsManager.getInstance();
                pm.getHtmlPlugin(context,"channel.zip",false,new PluginsManager.PluginsManagerCallback() {

                    @Override
                    public void onGetPluginSuccess(PluginModel model) {
                        File file=new File(model.getFolder(),model.getEntry());
                        String uri="file:///android_asset/disclaimer/error_page_404_en.html";
                        if(file.exists()){
                            uri="file:///"+file.getAbsolutePath();
                            Preference.getPreferences().saveChannelUri(uri);
                        }else if(LanguageUtil.isChina()){
                            uri = "file:///android_asset/disclaimer/error_page_404_zh.html";
                        }

                        Intent intent= new Intent();
                        intent.setClass(context, Html5PlusWebViewActvity.class);
                        intent.putExtra(Html5PlusWebViewActvity.KEY_URL, uri);
                        context.startActivity(intent);
                    }

                    @Override
                    public void onGetPluginFailed(final String hint) {
                        if((!Preference.getPreferences().getChannelUri().equals("noUri"))){
                            return;
                        }
                        if(hint!=null&&hint.length()>0){
                            Handler handler=new Handler(Looper.getMainLooper());
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(context, hint, Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    }
                });
            }
        }).start();
    }

    //安装服务工具 重置功能提供网关名称
    @JavascriptInterface
    public String getGateWayName(H5PlusWebView pWebview, final String data) {
        String gateWayName  = AccountManager.getAccountManger().getmCurrentInfo().getGwName();
        if(StringUtil.isNullOrEmpty(gateWayName)){
           return "";
        }else{
            return gateWayName;
        }
    }

    /**
     * 海信空调配网流程
     */
    @JavascriptInterface
    public void setHaiXinWifi(final H5PlusWebView pWebview,final String webParam){
        Log.d("HXKT", "setHaiXinWifi: "+webParam);
        if(!StringUtil.isNullOrEmpty(webParam)){
            com.alibaba.fastjson.JSONArray array=JSON.parseArray(webParam);
            String callBackId=array.getString(0);
            String funcName=array.getString(1);
            DeviceWifiSet devSet=DeviceWifiSet.sharedDeviceWifiSet();
            if(!StringUtil.isNullOrEmpty(funcName)) {
                if (funcName.equals("start")) {/*初始化*/
                    devSet.initDeviceWifISetInit();
                    JsUtil.getInstance().execCallback(pWebview, callBackId, "0", JsUtil.OK, true);
                } else if (funcName.equals("getwifi")) {/*获取wifi列表*/
                    com.alibaba.fastjson.JSONObject jsonNP=array.getJSONObject(2);
                    String wifiname=jsonNP.getString("wifiname");
                    String wifipass=jsonNP.getString("wifipass");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    List<WiFiInfo> devList=null;
                    for(int i=0;i<=5;i++){
                        devList=devSet.setDeviceWifiSetSsid(wifiname,wifipass);//获取设备列表
                        if(devList==null||devList.size()==0){
                            try {
                                Thread.sleep(1000L);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }else{
                            break;
                        }
                    }
                    com.alibaba.fastjson.JSONArray jsonObject= DeviceWifiSet.GetJsonForGetwifi(devList);
                    //下面两句是模拟数据
//                    String strJson="[{\"HType\":1,\"did\":\"86100c00900200100000010195958047\",\"deviceNickName\":\"\"}]";
//                    com.alibaba.fastjson.JSONArray jsonObject= com.alibaba.fastjson.JSONArray.parseArray(strJson);
                    JsUtil.getInstance().execCallback(pWebview, callBackId, jsonObject.toJSONString(), JsUtil.OK, true);
                } else if (funcName.equals("deviceselected")) {/*把选择的wifi存储到本地*/
                    String selectedWifi=array.getString(2);
                    CloudRequest.SaveHxWifiAndDeviceId(selectedWifi,"");
                    JsUtil.getInstance().execCallback(pWebview, callBackId, "0", JsUtil.OK, true);

                } else if (funcName.equals("bindingDevice")) {
                    //绑定设备的代码全部由网关实现，此处无代码；
                }

            }else {
                JsUtil.getInstance().execCallback(pWebview, callBackId, "", JsUtil.ERROR, true);
            }
        }


    }

    @JavascriptInterface
    public void scanDevice(final H5PlusWebView pWebview,final String data) {

        String callBackId = "";
        com.alibaba.fastjson.JSONArray array = JSON.parseArray(data);
        callBackId = array.getString(0);
        this.mCallBackId = callBackId;
        this.mWebview = pWebview;
        Intent intent = new Intent(pWebview.getContext(), QRScanActivity.class);
        intent.putExtra("HitvScanJudge", true);
        Activity thisActivity = (Activity) pWebview.getContainer();

        if (thisActivity != null) {
            thisActivity.startActivity(intent);
        } else {
            Log.e("HXKT", "scanDevice: pWebview.getContainer()获取到的Activity是Null");
            MainApplication application = MainApplication.getApplication();
            List<BaseActivity> activities = application.getActivities();
            BaseActivity activity = activities.get(activities.size() - 1);
            activity.startActivity(intent);
        }

    }

    @JavascriptInterface
    public void takePhotoForCus(final H5PlusWebView pWebview,final String data) {
        try {
            final JSONArray array = new JSONArray(data);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override
                public void run() {
                    PhotoSelector selector = new PhotoSelector((IActivityCallerWithResult) pWebview.getContainer(),
                            UserFileConfig.getInstance().getUserPath() + '/' + "customer_service.png",PhotoSelector.PIC_FROM_CAMERA);
                    final String CallBackID = array.optString(0);
                    selector.setPhotoSelectCallback(new PhotoSelectCallback() {
                        @Override
                        public void doWhatOnSuccess(String path) {
                            JsUtil.getInstance().execCallback(pWebview, CallBackID, path, JsUtil.OK, false);
                        }

                        @Override
                        public void doWhatOnFailed(Exception e) {
                            JsUtil.getInstance()
                                    .execCallback(pWebview, CallBackID, e.getMessage(), JsUtil.ERROR, false);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void selectPhotoForCus(final H5PlusWebView pWebview,final String data) {
        try {
            final JSONArray array = new JSONArray(data);
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(new Runnable() {

                @Override


                public void run() {
                    PhotoSelector selector = new PhotoSelector((IActivityCallerWithResult) pWebview.getContainer(),
                            UserFileConfig.getInstance().getUserPath() + '/' + "customer_service.png",PhotoSelector.PIC_FROM＿LOCALPHOTO);
                    final String CallBackID = array.optString(0);
                    selector.setPhotoSelectCallback(new PhotoSelectCallback() {
                        @Override
                        public void doWhatOnSuccess(String path) {
                            JsUtil.getInstance().execCallback(pWebview, CallBackID, path, JsUtil.OK, false);
                        }

                        @Override
                        public void doWhatOnFailed(Exception e) {
                            JsUtil.getInstance()
                                    .execCallback(pWebview, CallBackID, e.getMessage(), JsUtil.ERROR, false);
                        }
                    });
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @JavascriptInterface
    public void uploadChatPic(final H5PlusWebView pWebview,final String data) {
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String CallBackID = "";
                try {
                    final JSONArray array = new JSONArray(data);
                    CallBackID = array.optString(0);
                    String filepath = array.optString(1);
                    UserFileConfig userFileConfig = UserFileConfig.getInstance();
                    String ret = userFileConfig.uploadUserFile(filepath);
                    JsUtil.getInstance().execCallback(pWebview, CallBackID, ret, JsUtil.OK, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JsUtil.getInstance().execCallback(pWebview, CallBackID, e.toString(), JsUtil.ERROR, true);
                }
            }
        });
    }

    @JavascriptInterface
    public void getLastData(final H5PlusWebView pWebview,final String data) {
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
                public void run() {
                String CallBackID = "";
                try {
                    final JSONArray array = new JSONArray(data);
                    CallBackID = array.optString(0);
                    String gwID = array.optString(1);
                    String gwPsd = array.optString(2);
                    if(StringUtil.isNullOrEmpty(gwID)){
                        gwID=AccountManager.getAccountManger().getmCurrentInfo().getGwID();
                    }
                    if(StringUtil.isNullOrEmpty(gwPsd)){
                        gwPsd=AccountManager.getAccountManger().getmCurrentInfo().getGwPwd();
                    }else{
                        gwPsd=MD5Util.encrypt(gwPsd);
                    }
                    com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
                    jsonObject.put("gwID",gwID);
                    jsonObject.put("password",gwPsd);
                    jsonObject.put("appId",AccountManager.getAccountManger().getRegisterInfo().getAppID());
                    com.alibaba.fastjson.JSONObject result = HttpUtil.postWulianCloudOrigin(WulianCloudURLManager.getGatewayCloneURL(), jsonObject);
                    Logger.debug("basic data:"+result.toJSONString());
                    com.alibaba.fastjson.JSONObject bodyObject = result.getJSONObject("body");
                    com.alibaba.fastjson.JSONObject retDataObject = bodyObject.getJSONObject("retData");
                    String token=retDataObject.getString("token");
                    String createDate=retDataObject.getString("createDate");
                    String bn=AccountManager.getAccountManger().getmCurrentInfo().getBn();
                    com.alibaba.fastjson.JSONObject backData=new com.alibaba.fastjson.JSONObject();
                    backData.put("gateWayID",AccountManager.getAccountManger().getmCurrentInfo().getGwID());
                    if(StringUtil.isNullOrEmpty(createDate)){
                        createDate="暂未备份";
                    }
                    backData.put("cloneData",createDate);
                    JsUtil.getInstance().execCallback(pWebview, CallBackID, backData.toJSONString(), JsUtil.OK, true);
                } catch (Exception e) {
                    e.printStackTrace();
                    JsUtil.getInstance().execCallback(pWebview, CallBackID, e.toString(), JsUtil.ERROR, true);
                }
            }
        });
    }

    @JavascriptInterface
    public void cloneFunction(final H5PlusWebView pWebview,final String data) {
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String callBackId = "";
                try {
                    final JSONArray array = new JSONArray(data);
                    callBackId = array.optString(0);
                    String operationType = array.optString(1);
                    String oldGWID = array.optString(2);
                    String oldGWPsw = array.optString(3);
                    if(StringUtil.isNullOrEmpty(oldGWID)){
                        oldGWID=AccountManager.getAccountManger().getmCurrentInfo().getGwID();
                    }
                    if(StringUtil.isNullOrEmpty(oldGWPsw)){
                        oldGWPsw=AccountManager.getAccountManger().getmCurrentInfo().getGwPwd();
                    }else{
                        oldGWPsw=MD5Util.encrypt(oldGWPsw);
                    }
                    com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
                    jsonObject.put("gwID",oldGWID);
                    jsonObject.put("password",oldGWPsw);
                    jsonObject.put("appId",AccountManager.getAccountManger().getRegisterInfo().getAppID());
                    com.alibaba.fastjson.JSONObject result = HttpUtil.postWulianCloudOrigin(WulianCloudURLManager.getGatewayCloneURL(), jsonObject);
                    Logger.debug("basic data:"+result.toJSONString());
                    com.alibaba.fastjson.JSONObject bodyObject = result.getJSONObject("body");
                    com.alibaba.fastjson.JSONObject retDataObject = bodyObject.getJSONObject("retData");
                    String token=retDataObject.getString("token");
                    if(operationType.equals("backup")){
                        NetSDK.gatewayCloneAndBackup(AccountManager.getAccountManger().getmCurrentInfo().getGwID(),"0",null,null,token);
                    }else if(operationType.equals("clone")){
                        String gwModel=retDataObject.getString("gwModel");
                        NetSDK.gatewayCloneAndBackup(AccountManager.getAccountManger().getmCurrentInfo().getGwID(),"2",gwModel,oldGWID,token);
                    }
                    SmarthomeFeatureImpl.mCallBackId = callBackId;
                    SmarthomeFeatureImpl.mWebview = pWebview;
                } catch (Exception e) {
                    e.printStackTrace();
                    JsUtil.getInstance().execCallback(pWebview, callBackId, e.toString(), JsUtil.ERROR, true);
                }
            }
        });
    }

    @JavascriptInterface
    public void judgeGatewayType(final H5PlusWebView pWebview,final String data) {
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                String callBackId = "";
                try {
                    final JSONArray array = new JSONArray(data);
                    callBackId = array.optString(0);
                    String gwID = array.optString(1);
                    String gwPsw = MD5Util.encrypt(array.optString(2));
                    com.alibaba.fastjson.JSONObject jsonObject=new com.alibaba.fastjson.JSONObject();
                    jsonObject.put("gwID",gwID);
                    jsonObject.put("password",gwPsw);
                    jsonObject.put("appId",AccountManager.getAccountManger().getRegisterInfo().getAppID());
                    com.alibaba.fastjson.JSONObject result = HttpUtil.postWulianCloudOrigin(WulianCloudURLManager.getGatewayCloneURL(), jsonObject);
                    Logger.debug("basic data:"+result.toJSONString());
                    com.alibaba.fastjson.JSONObject bodyObject = result.getJSONObject("body");
                    com.alibaba.fastjson.JSONObject retDataObject = bodyObject.getJSONObject("retData");
                    String gwModel=retDataObject.getString("gwModel");
                    String currentGwModel=AccountManager.getAccountManger().getmCurrentInfo().getBn();
                    if((!StringUtil.isNullOrEmpty(currentGwModel))&&currentGwModel.equals(gwModel)){
                        JsUtil.getInstance().execCallback(pWebview, callBackId, "0", JsUtil.OK, true);
                    }else{
                        JsUtil.getInstance().execCallback(pWebview, callBackId, "1", JsUtil.OK, true);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    JsUtil.getInstance().execCallback(pWebview, callBackId, e.toString(), JsUtil.ERROR, true);
                }
            }
        });
    }

    @JavascriptInterface
    public void getWifiSSID(final H5PlusWebView pWebview, final String data) {
        com.alibaba.fastjson.JSONArray array = JSON.parseArray(data);
        String callBackID = array.getString(0);
        try {
            boolean iswifi= WifiUtil.getIsWifi(pWebview.getContext());
            String wifiSSID="";
            if(iswifi){
                WiFiLinker wiFiLinker=new WiFiLinker();
                wiFiLinker.WifiInit(pWebview.getContext());
                wifiSSID=wiFiLinker.getConnectedWifiSSID();
                wifiSSID=wifiSSID.replace("\"","");
            }
            JsUtil.getInstance().execCallback(pWebview, callBackID, wifiSSID, JsUtil.OK, true);
        } catch (Exception e) {
            JsUtil.getInstance().execCallback(pWebview, callBackID, "", JsUtil.ERROR, true);
        }
    }
}
