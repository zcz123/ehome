package cc.wulian.smarthomev5.eyecat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cc.wulian.smarthomev5.R;

/**
 * Created by Administrator on 2017/3/8.
 */

public class EyecatWIFISettingTwoActivity extends Activity implements View.OnClickListener {
    private TextView wifiname;
    private TextView checkout_wifi;
    private EditText eyecat_password;
    private Button eyecat_next;
    private CheckBox eyecat_display_password;
    private LinearLayout eyecat_return;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eyecat_activity_wifisetting_two);
        initView();
    }
    private void initView(){
        wifiname = (TextView) findViewById(R.id.eyecat_wifiname);
        checkout_wifi = (TextView) findViewById(R.id.eyecat_checkout_wifi);
        checkout_wifi.setOnClickListener(this);
        eyecat_next = (Button) findViewById(R.id.eyecat_next);
        eyecat_next.setOnClickListener(this);
        eyecat_password = (EditText) findViewById(R.id.eyecat_password);
        eyecat_display_password = (CheckBox) findViewById(R.id.eyecat_display_password);
        eyecat_return = (LinearLayout) findViewById(R.id.eyecat_return);
    }
    private String getWifiname(){
        WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiMgr.getConnectionInfo();
        String wifiName = info.getSSID();
        return  wifiName;
    }
    private void openWifimenu(){
        Intent intent = new Intent(android.provider.Settings.ACTION_WIFI_SETTINGS);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        wifiname.setText(getWifiname().substring(1,getWifiname().length()-1));
        if(checkIsCurrentWifiHasPassword(EyecatWIFISettingTwoActivity.this)){
            eyecat_password.setVisibility(View.VISIBLE);
            eyecat_display_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        eyecat_password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    }else{
                        eyecat_password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                }
            });
        }else{
            eyecat_password.setVisibility(View.INVISIBLE);
        }
        eyecat_password.addTextChangedListener(new TextWatcher() {
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // 判断输入不为空，按钮可点击
                if (eyecat_password.length()>7) {
                    eyecat_next.setEnabled(true);
                    eyecat_next.setBackgroundResource(R.drawable.btn_green);
                } else {
                    eyecat_next.setEnabled(false);
                    eyecat_next.setBackgroundResource(R.drawable.btn_gray);
                }
            }

            public void afterTextChanged(Editable s) {

            }
        });
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.eyecat_checkout_wifi:
                openWifimenu();
                break;
            case R.id.eyecat_next:
                if(isWifiConnect(EyecatWIFISettingTwoActivity.this)){
                    Intent intent = new Intent(EyecatWIFISettingTwoActivity.this,EyecatScanQRcodeActivity.class);
                    intent.putExtra("pwd",eyecat_password.getText().toString());
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(this, "wifi连接失败，请重新检查wifi密码", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(EyecatWIFISettingTwoActivity.this,EyecatScanQRcodeActivity.class);
                    intent.putExtra("pwd",eyecat_password.getText().toString());
                    startActivity(intent);
                    finish();
                }
                break;
            case R.id.eyecat_return:
                finish();
                break;
        }

    }
    public static boolean isWifiConnect(Context context){

        ConnectivityManager connManager =
                (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return mWifi.isConnected();
    }

    public static boolean checkIsCurrentWifiHasPassword(Context context ) {
        try {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

            // 得到当前连接的wifi热点的信息
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();

            // 得到当前WifiConfiguration列表，此列表包含所有已经连过的wifi的热点信息，未连过的热点不包含在此表中
            List<WifiConfiguration> wifiConfiguration = wifiManager.getConfiguredNetworks();

            String currentSSID = wifiInfo.getSSID();
            if (currentSSID != null && currentSSID.length() > 2) {
                if (currentSSID.startsWith("\"") && currentSSID.endsWith("\"")) {
                    currentSSID = currentSSID.substring(1, currentSSID.length() - 1);
                }

                if (wifiConfiguration != null && wifiConfiguration.size() > 0) {
                    for (WifiConfiguration configuration : wifiConfiguration) {
                        if (configuration != null && configuration.status == WifiConfiguration.Status.CURRENT) {
                            String ssid = null;
                            if (!TextUtils.isEmpty(configuration.SSID)) {
                                ssid = configuration.SSID;
                                if (configuration.SSID.startsWith("\"") && configuration.SSID.endsWith("\"")) {
                                    ssid = configuration.SSID.substring(1, configuration.SSID.length() - 1);
                                }
                            }
                            if (TextUtils.isEmpty(currentSSID) || currentSSID.equalsIgnoreCase(ssid)) {
                                //KeyMgmt.NONE表示无需密码
                                return (!configuration.allowedKeyManagement.get(WifiConfiguration.KeyMgmt.NONE));
                            }
                        }
                    }
                }
            }
        }catch (Exception e) {
                //do nothing
        }
        //默认为需要连接密码
        return true;
        }
    }
