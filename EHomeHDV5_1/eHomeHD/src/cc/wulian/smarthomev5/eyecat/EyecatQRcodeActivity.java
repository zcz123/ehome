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

import com.eques.icvss.core.module.user.BuddyType;
import com.eques.icvss.utils.Method;

import org.json.JSONObject;

import cc.wulian.smarthomev5.R;


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

    private String reqId;
    String baseUrl = "http://testv2.wulian.cc:52181";
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
        eyecat_next.setVisibility(View.GONE);
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
        password = "taotaotao";
        wifiSsid = "Mi-4c";
        Bitmap bitmap = EyecatManager.getInstance().getICVSSUserInstance().equesCreateQrcode(wifiSsid, password, EyecatManager.KEYID, EyecatManager.username,
                BuddyType.TYPE_WIFI_DOOR_R22, 230);

        eyecat_qrcode.setImageBitmap(bitmap);


    }

    @Override
    protected void onResume() {
        super.onResume();
        EyecatManager.getInstance().addPacketListener(scanResultListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        EyecatManager.getInstance().removePacketListener(scanResultListener);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.eyecat_return:
                finish();
                break;
            case R.id.eyecat_next:
                Intent i = new Intent(EyecatQRcodeActivity.this, EyecatWaitingActivity.class);
                i.putExtra("reqId", reqId);
                startActivity(i);
                finish();
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
    private EyecatManager.PacketListener scanResultListener = new EyecatManager.PacketListener() {
        @Override
        public String getMenthod() {
            return Method.METHOD_ONADDBDY_REQ;
        }

        @Override
        public void processPacket(JSONObject object) {
            reqId = object.optString(Method.ATTR_REQID);
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    eyecat_next.setVisibility(View.VISIBLE);
                }
            });
        }
    };

}
