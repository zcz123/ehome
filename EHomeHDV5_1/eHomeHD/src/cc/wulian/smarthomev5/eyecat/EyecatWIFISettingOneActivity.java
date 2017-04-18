package cc.wulian.smarthomev5.eyecat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import cc.wulian.smarthomev5.R;


/**
 * Created by Administrator on 2017/3/8.
 */

public class EyecatWIFISettingOneActivity extends Activity implements View.OnClickListener{
    private Button btn_next;
    private LinearLayout btn_return;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eyecat_activity_wifisetting_one);
        initView();
    }
    private void initView(){
        btn_return = (LinearLayout) findViewById(R.id.eyecat_return);
        btn_next = (Button) findViewById(R.id.eyecat_next);
        btn_return.setOnClickListener(this);
        btn_next.setOnClickListener(this);
    }

    public void onClick(View v) {
        switch (v.getId()){
            case R.id.eyecat_return:
                finish();
                break;
            case R.id.eyecat_next:
                if(isWiFiActive()){
                    Intent intent = new Intent(EyecatWIFISettingOneActivity.this,EyecatWIFISettingTwoActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Toast.makeText(this, "添加设备只支持在WiFi网络下进行", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    public boolean isWiFiActive() {
        ConnectivityManager connectivity = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] infos = connectivity.getAllNetworkInfo();
            if (infos != null) {
                for(NetworkInfo ni : infos){
                    if(ni.getTypeName().equals("WIFI") && ni.isConnected()){
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
