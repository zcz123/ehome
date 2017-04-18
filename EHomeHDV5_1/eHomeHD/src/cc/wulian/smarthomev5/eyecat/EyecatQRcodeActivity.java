package cc.wulian.smarthomev5.eyecat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.eques.icvss.core.module.user.BuddyType;
import com.eques.icvss.utils.Method;

import org.json.JSONArray;
import org.json.JSONObject;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.bean.Session;


/**
 * Created by Administrator on 2017/3/9.
 */

public class EyecatQRcodeActivity extends Activity implements View.OnClickListener
{
    private String password,wifiSsid;
    private Button eyecat_next;
    private ImageView eyecat_qrcode;
    private LinearLayout eyecat_return;
    public static final String TAG = "EyecatQRcodeActivity";

    private String bid;
    private String reqId;
    String baseUrl = "http://testv2.wulian.cc:52181";

    private EyecatManager.PacketListener scanResultListener = new EyecatManager.PacketListener() {
        @Override
        public String getMenthod() {
            return Method.METHOD_ONADDBDY_REQ;
        }

        @Override
        public void processPacket(JSONObject object) {
            reqId = object.optString(Method.ATTR_REQID);
            EyecatManager.getInstance().getICVSSUserInstance().equesAckAddResponse(reqId, 1);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EyecatQRcodeActivity.this,"同意发起绑定",Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
    private EyecatManager.PacketListener addDeviceResultListener = new EyecatManager.PacketListener() {
        @Override
        public String getMenthod() {
            return Method.METHOD_ONADDBDY_RESULT;
        }

        @Override
        public void processPacket(JSONObject object) {
            String code = object.optString(Method.ATTR_ERROR_CODE);
            if("4407".equals(code)){
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EyecatQRcodeActivity.this,"已绑定设备:",Toast.LENGTH_SHORT).show();
                    }
                });
                EyecatManager.getInstance().getICVSSUserInstance().equesGetDeviceList();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EyecatQRcodeActivity.this,"开始获取设备列表",Toast.LENGTH_SHORT).show();
                    }
                });
            }
            else if("4000".equals(code)) {
                JSONObject added_bdy = object.optJSONObject(Method.ATTR_ADDED_BDY);
                if (added_bdy != null) {
                    bid = added_bdy.optString(Method.ATTR_BUDDY_BID);
                }
                JSONArray array = object.optJSONArray(Method.ATTR_ONLINES);
                for (int i = 0; i < array.length(); i++) {
                    JSONObject obj = array.optJSONObject(i);
                    EyecatManager.EyecatDevice device = new EyecatManager.EyecatDevice();
                    device.setBid(obj.optString(Method.ATTR_BUDDY_BID));
                    device.setUid(obj.optString(Method.ATTR_BUDDY_UID));
                    device.setStatus(obj.optInt(Method.ATTR_BUDDY_STATUS));
                    EyecatManager.getInstance().putDevice(device);
                    if (StringUtil.equals(bid, obj.optString(Method.ATTR_BUDDY_BID))) {
                        Session session = new Session();
                        session.setBid(bid);
                        session.setUid(EyecatManager.getInstance().getDevice(bid).getUid());
                        Cookies.saveSession(getApplicationContext(), session);
                    }
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(EyecatQRcodeActivity.this, "初次绑定成功", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(EyecatQRcodeActivity.this, EyecatBindActivity.class);
                        i.putExtra("flag", true);
                        startActivity(i);
                        finish();
                    }
                });
            }
        }
    };
    private EyecatManager.PacketListener devstResultListener = new EyecatManager.PacketListener() {
        @Override
        public String getMenthod() {
            return Method.METHOD_DEVST;
        }

        @Override
        public void processPacket(JSONObject object) {
            bid = object.optString(Method.ATTR_BUDDY_BID);
            EyecatManager.EyecatDevice device = new EyecatManager.EyecatDevice();
            device.setBid(object.optString(Method.ATTR_BUDDY_BID));
            device.setUid(object.optString(Method.ATTR_BUDDY_UID));
            device.setStatus(object.optInt(Method.ATTR_BUDDY_STATUS));
            EyecatManager.getInstance().putDevice(device);
            Session session = new Session();
            session.setBid(object.optString(Method.ATTR_BUDDY_BID));
            session.setUid(object.optString(Method.ATTR_BUDDY_UID));
            Cookies.saveSession(getApplicationContext(),session);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EyecatQRcodeActivity.this,"设备已绑定过",Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
    private EyecatManager.PacketListener deviceListResultListener = new EyecatManager.PacketListener() {
        @Override
        public String getMenthod() {
            return Method.METHOD_BDYLIST;
        }

        @Override
        public void processPacket(JSONObject object) {
            JSONArray array = object.optJSONArray(Method.ATTR_ONLINES);
            for(int i=0 ;i < array.length() ;i++){
                JSONObject obj = array.optJSONObject(i);
                EyecatManager.EyecatDevice device = new EyecatManager.EyecatDevice();
                device.setBid(obj.optString(Method.ATTR_BUDDY_BID));
                device.setUid(obj.optString(Method.ATTR_BUDDY_UID));
                device.setStatus(obj.optInt(Method.ATTR_BUDDY_STATUS));
                EyecatManager.getInstance().putDevice(device);
                if(i==0){
                    Session session = new Session();
                    session.setBid(obj.optString(Method.ATTR_BUDDY_BID));
                    session.setUid(obj.optString(Method.ATTR_BUDDY_UID));
                    Cookies.saveSession(getApplicationContext(),session);
                }
            }
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(EyecatQRcodeActivity.this,"获取设备列表成功",Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eyecat_activity_qrcode);
        initView();
        initData();
    }
    private void initView(){
        eyecat_return = (LinearLayout) findViewById(R.id.eyecat_return);
        eyecat_return.setOnClickListener(this);
        eyecat_qrcode = (ImageView) findViewById(R.id.eyecat_qrcode);
        eyecat_next = (Button) findViewById(R.id.eyecat_next);
        eyecat_next.setOnClickListener(this);
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.show();
        Window window=alertDialog.getWindow();
        window.setContentView(R.layout.eyecat_my_dialog);
        Button iknow = (Button) window.findViewById(R.id.eyecat_iknow);
        iknow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });

    }
    private void initData(){
        EyecatManager.getInstance().login();
        Intent intent = getIntent();
        password = intent.getStringExtra("pwd");
        WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiSsid = getWifiInfoSSid(wifiManager);
        Bitmap bitmap = EyecatManager.getInstance().getICVSSUserInstance().equesCreateQrcode(wifiSsid, password, EyecatManager.KEYID, EyecatManager.username,
                BuddyType.TYPE_WIFI_DOOR_R22, 230);

        eyecat_qrcode.setImageBitmap(bitmap);


    }

    @Override
    protected void onResume() {
        super.onResume();
        EyecatManager.getInstance().addPacketListener(scanResultListener);
        EyecatManager.getInstance().addPacketListener(addDeviceResultListener);
        EyecatManager.getInstance().addPacketListener(devstResultListener);
        EyecatManager.getInstance().addPacketListener(deviceListResultListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EyecatManager.getInstance().removePacketListener(scanResultListener);
        EyecatManager.getInstance().removePacketListener(addDeviceResultListener);
        EyecatManager.getInstance().removePacketListener(devstResultListener);
        EyecatManager.getInstance().removePacketListener(deviceListResultListener);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.eyecat_return:
                finish();
                break;
            case R.id.eyecat_next:
                if(StringUtil.isNullOrEmpty(bid)){
                    Intent i = new Intent(EyecatQRcodeActivity.this, EyecatBindActivity.class);
                    i.putExtra("flag", false);
                    startActivity(i);
                    finish();
                }else {
                    Intent intent = new Intent(EyecatQRcodeActivity.this,EyecatMonitoringActivity.class);
                    startActivity(intent);
                    finish();
                }
                break;
        }
    }

    public String getWifiInfoSSid(WifiManager wifiManager) {
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        if (wifiInfo != null) {
            String ssid = wifiInfo.getSSID();
            if (ssid != null) {
                int i = ssid.indexOf("\"");
                if (i != -1) {
                    ssid = ssid.replaceAll("\"", "");
                }
            }
            return ssid;
        }
        return null;
    }

}
