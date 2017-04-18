package cc.wulian.smarthomev5.activity.minigateway;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.wulian.icam.wifidirect.utils.WiFiLinker;

import org.json.JSONException;

import java.io.IOException;

import cc.wulian.ihome.wan.NetSDK;
import cc.wulian.ihome.wan.entity.GatewayInfo;
import cc.wulian.lan.LanSocketConnection;
import cc.wulian.lan.LanSocketConnectionHandler;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.AccountInformationSettingManagerActivity;
import cc.wulian.smarthomev5.activity.EventBusActivity;
import cc.wulian.smarthomev5.activity.SigninActivityV5;
import cc.wulian.smarthomev5.activity.sxgateway.ConfigDeviceSxGatewayConnectNetAgainActivity;
import cc.wulian.smarthomev5.activity.sxgateway.SxGatewayRelayFailActivity;
import cc.wulian.smarthomev5.activity.sxgateway.SxGatewayRelaySucceedActivity;
import cc.wulian.smarthomev5.event.MiniGatewayEvent;
import cc.wulian.smarthomev5.fragment.setting.minigateway.MiniRouterSettingActivity;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.tools.Preference;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.utils.WifiUtil;

public class MiniGatewayRelayWifiConnectActivity extends EventBusActivity implements
        OnClickListener, LanSocketConnectionHandler {
    private String gwID = AccountManager.getAccountManger().getmCurrentInfo()
            .getGwID();
    private Button btn_fail;
    private LinearLayout linearLayout;
    public String extraString;
    private AccountManager accountManager = AccountManager.getAccountManger();
    private GatewayInfo info = accountManager.getmCurrentInfo();
    private Boolean is_sxgateway = false;
    private String wifiName_last;
    private WiFiLinker wifiLinker = new WiFiLinker();
    LanSocketConnection connection = new LanSocketConnection(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        wifiLinker.WifiInit(this);
        if (info != null && info.getGwVer() != null) {
            String gwver = info.getGwVer();
            if (gwver.length() >= 3) {
                is_sxgateway = (gwver.substring(2, 4) + "").equals("10");
            }
        }
        if (is_sxgateway) {
            setContentView(R.layout.device_sxgateway_alloction_connect_progress);
            initBar();
            //发送配置中继的命令，第四个参数要有值。
            LogonAndScanConnectRelayWifi();
            setNowWifiNameDelay();
        } else {
            setContentView(R.layout.mini_gateway_scan_relay_wifi_connect);
            initView();
            setListener();
            initBar();
            LogonAndScanConnectRelayWifi();
        }

    }

    private void initView() {
        linearLayout = (LinearLayout) findViewById(R.id.mini_gateway_wifi_scan_connect_succeed);
        btn_fail = (Button) findViewById(R.id.mini_gateway_wifi_scan_connect_fail);
        // 增加下划线
        btn_fail.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
    }

    private void setListener() {
        btn_fail.setOnClickListener(this);
        linearLayout.setOnClickListener(this);
    }

    private void initBar() {
        resetActionMenu();
        getCompatActionBar().setDisplayHomeAsUpEnabled(true);
        getCompatActionBar().setIconText("");
        getCompatActionBar().setTitle(
                getResources().getString(
                        R.string.gateway_router_setting_wifi_relay_setting));
    }

    private void setNowWifiNameDelay() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(15000);
                    wifiLinker.getSxConnectedWifiSSID();
                    judgeConnectWifi();

                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void judgeConnectWifi() {
        String wifiName = wifiLinker.getSxConnectedWifiSSID();
        wifiName_last = Preference.getPreferences().getNowtimeWifiName();
        if (wifiName.equals(wifiName_last)) {
            // 相同
            if (NetSDK.isConnected(gwID)) {
                NetSDK.sendMiniGatewayWifiSettingMsg(gwID, "1", "get", null);
            } else {
                Intent intent = new Intent();
                intent.putExtra("sx_wifiName", wifiName_last);
                intent.setClass(getApplication(), ConfigDeviceSxGatewayConnectNetAgainActivity.class);
                startActivity(intent);
                finish();
            }
        } else {
            Intent intent = new Intent();
            intent.putExtra("sx_wifiName", wifiName_last);
            intent.setClass(getApplication(), ConfigDeviceSxGatewayConnectNetAgainActivity.class);
            startActivity(intent);
            finish();
        }
    }


    private void LogonAndScanConnectRelayWifi() {
        Intent intent = getIntent();
        extraString = intent.getStringExtra("FLAG_2");
        String ssid = intent.getStringExtra("ssid");
        String pwd = intent.getStringExtra("pwd");
        String address = intent.getStringExtra("address");
        String channel = intent.getStringExtra("channel");
        String encryptionString = intent.getStringExtra("encryption");
        int encryp=0;
        if (!is_sxgateway){
            if (encryptionString.startsWith("WPA P")){
                encryp=1;
            }else if (encryptionString.startsWith("WPA2 P")){
                encryp=2;
            }else if (encryptionString.startsWith("mixed")){
                encryp=3;
            }else if (encryptionString.startsWith("WEP")){
                encryp=4;
            }
        }

        JSONArray wifiInfoJA = new JSONArray();
        JSONObject wifiInfo = new JSONObject();

        String miniName="";
        String miniPassword="";
        int customflag=-1;
        JSONObject data = new JSONObject();
        JSONObject miniInfo=new JSONObject();
        if (!is_sxgateway){
             miniName=intent.getStringExtra("mininame");
             miniPassword=intent.getStringExtra("minipassword");
             customflag=intent.getIntExtra("customflag",-1);
        }

        if ("EXTRA_0".equals(extraString)) {
            // 账号登陆
            if (encryptionString.equals("WEP")) {
                if (intent.getStringExtra("apcli_encryption").equals("共享型")) {
                    wifiInfo.put("apcli_encryption", "SHARE");
                } else if (intent.getStringExtra("apcli_encryption").equals(
                        "开放型")) {
                    wifiInfo.put("apcli_encryption", "NONE");
                }
                wifiInfo.put("apcli_index", "1234");
            } else {
                wifiInfo.put("apcli_encryption", "NONE");
                wifiInfo.put("ap_index", null);
            }
            wifiInfo.put("ssid", ssid);
            wifiInfo.put("channel", channel);
            wifiInfo.put("mac", address);
            if (is_sxgateway){
                wifiInfo.put("encryption", encryptionString);
            }else {
                wifiInfo.put("encryption", encryp);
            }
            wifiInfo.put("key", pwd);
            wifiInfo.put("roaming", null);
            wifiInfo.put("ap_ssid", "");
            wifiInfo.put("ap_key", null);

            if (!is_sxgateway){
                miniInfo.put("ssid",miniName);
                miniInfo.put("key",miniPassword);
                miniInfo.put("encryption",3);
                miniInfo.put("channel", channel);

                data.put("config0",wifiInfo);
                data.put("config1",miniInfo);
                data.put("customflag",customflag);
                wifiInfoJA.add(data);
                NetSDK.sendMiniGatewayWifiSettingMsg(gwID, "7", "set", wifiInfoJA); // 修改以前的3  改为7
            }else {
                wifiInfoJA.add(wifiInfo);
                NetSDK.sendMiniGatewayWifiSettingMsg(gwID, "3", "set", wifiInfoJA);
            }

        } else {
            connectMiniGateSocketNext();
        }
    }

    // 扫码登陆
    private void connectMiniGateSocketNext() {
        new Thread() {
            @Override
            public void run() {
                try {
                    if (connection.isConnected() == false) {
                        connection.connectGateway(new WifiUtil().getGatewayIP(), 11328);
                    }
                } catch (IOException e) {
                    Log.e("cfgmini", "", e);
                }

                Intent intent = getIntent();
                String extraString = intent.getStringExtra("FLAG_2");
                String ssid = intent.getStringExtra("ssid");
                String pwd = intent.getStringExtra("pwd");
                String address = intent.getStringExtra("address");
                String channel = intent.getStringExtra("channel");
                String encryptionStr = intent.getStringExtra("encryption");

                String miniName="";
                String miniPassword="";
                int customflag=-1;
                if (!is_sxgateway){
                    miniName   =intent.getStringExtra("mininame");
                    miniPassword    =intent.getStringExtra("minipassword");
                    customflag   =intent.getIntExtra("customflag",-1);
                }
                int encryption = 0;
                if (encryptionStr.startsWith("WPA P")) {
                    encryption = 1;
                } else if (encryptionStr.startsWith("WPA2 P")) {
                    encryption = 2;
                } else if (encryptionStr.startsWith("mixed")) {
                    encryption = 3;
                } else if (encryptionStr.contains("WEP")) {
                    encryption = 4;
                }
                JSONObject wifiInfoJO = new JSONObject();


                if (encryptionStr.equals("WEP")) {
                    if (intent.getStringExtra("apcli_encryption").equals("共享型")) {
                        wifiInfoJO.put("apcli_encryption", "SHARE");
                    } else if (intent.getStringExtra("apcli_encryption")
                            .equals("开放型")) {
                        wifiInfoJO.put("apcli_encryption", "NONE");
                    }

                    wifiInfoJO.put("apcli_index", "1234");
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("WEP_INFO", wifiInfoJO);
                    jsonObject.put("sercurity_type", "aes");
                    jsonObject.put("encryption", encryption);
                    jsonObject.put("key", pwd);
                    jsonObject.put("ssid", ssid);
                    JSONObject WEPJsonObject = new JSONObject();
                    WEPJsonObject.put("body", jsonObject);
                    WEPJsonObject.put("adress", address);
                    WEPJsonObject.put("channel", channel);
                    WEPJsonObject.put("msgid", 2);
                    WEPJsonObject.put("cmd", "setUplinkInfo");


                    try {
                        connection.sendMessage(WEPJsonObject.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    wifiInfoJO.put("apcli_encryption", "NONE");
                    wifiInfoJO.put("ap_index", null);

                    JSONObject jsonObject = new JSONObject();
                    if (is_sxgateway){
                        jsonObject.put("sercurity_type", "aes");
                        jsonObject.put("WEP_INFO", "");
                    }
                    jsonObject.put("encryption", encryption);
                    jsonObject.put("key", pwd);
                    jsonObject.put("ssid", ssid);
                    jsonObject.put("channel", channel);
                    jsonObject.put("address", address);

                    JSONObject miniInfo=new JSONObject();  //设置mini网关的wifi
                    JSONObject body =new JSONObject();
                    if (!is_sxgateway){
                        miniInfo.put("ssid",miniName);
                        miniInfo.put("key",miniPassword);
                        miniInfo.put("encryption",3);
                        miniInfo.put("channel", channel);

                        body.put("config0",jsonObject);
                        body.put("config1",miniInfo);
                        body.put("customflag",customflag);
                    }

                    JSONObject bodyJsonObject = new JSONObject();
                    if (!is_sxgateway){
                        bodyJsonObject.put("body", body);
                        bodyJsonObject.put("cmd", "setAlllinkInfo");
                    }else {
                        bodyJsonObject.put("body", jsonObject);
                        bodyJsonObject.put("cmd", "setUplinkInfo");
                    }
//                    bodyJsonObject.put("channel", channel);
//                    bodyJsonObject.put("address", address);
                    bodyJsonObject.put("msgid", "2");
                    try {
                        connection.sendMessage(bodyJsonObject.toString());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }.start();
    }

    @Override
    protected boolean finshSelf() {
        return false;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent = new Intent();
        if (id == R.id.mini_gateway_wifi_scan_connect_succeed) {
//            if ("EXTRA_0".equals(extraString)) {
//                intent.setClass(this,
//                        AccountInformationSettingManagerActivity.class);
//                startActivity(intent);
//            } else {
//                intent.setClass(this, SigninActivityV5.class);
//				intent.putExtra("shock", "shock_value");
//                startActivity(intent);
                finish();
//            }

        } else if (id == R.id.mini_gateway_wifi_scan_connect_fail) {
            if ("EXTRA_0".equals(extraString)) {
//                intent.setClass(this, MiniRouterSettingActivity.class);
//                startActivity(intent);

            }
            else {
                intent.setClass(this, MiniGatewayRelaySettingActivity.class);
                intent.putExtra("extra", 100);
                Bundle bundlekey=new Bundle();
                bundlekey.putString("Wifiname_key", "");
                intent.putExtra("Wifiname_key", bundlekey);
                startActivity(intent);
            }
            finish();
        }
    }

    //判断是否成功或失败
    public void onEventMainThread(MiniGatewayEvent gatewayevent) {
        if (!CmdUtil.MINIGATEWAY_GET_JUDGE_RELAY_SIGN.equals(gatewayevent
                .getCmdindex())) {
            return;
        }
        org.json.JSONObject jsonObject;
        org.json.JSONArray jsonArray;
        try {
            jsonArray = new org.json.JSONArray(gatewayevent.getData());
            jsonObject = jsonArray.getJSONObject(0);
            String wifiFlag = jsonObject.getString("setRepeaterFlag");

            if (wifiFlag.equals("0")) {
                Intent intent = new Intent();
                intent.putExtra("GateWayName", wifiName_last);
                intent.setClass(getApplication(), SxGatewayRelayFailActivity.class);
                startActivity(intent);
                finish();
            } else if (wifiFlag.equals("1")) {
                Intent intent = new Intent();
                intent.setClass(getApplication(), SxGatewayRelaySucceedActivity.class);
                startActivity(intent);
                finish();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionBroken(int reason) {
    }

    @Override
    public void receviedMessage(String msg) {
    }

}
