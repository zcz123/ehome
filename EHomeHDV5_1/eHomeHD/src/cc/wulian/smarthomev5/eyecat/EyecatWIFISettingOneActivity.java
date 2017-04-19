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

import cc.wulian.ihome.wan.core.http.Result;
import cc.wulian.ihome.wan.util.TaskExecutor;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.account.WLUserManager;


/**
 * Created by Administrator on 2017/3/8.
 */

public class EyecatWIFISettingOneActivity extends Activity implements View.OnClickListener{
    private Button btn_next;
    private LinearLayout btn_return;
    String bid = "934f4227e83a4d618f27e2dca860625e";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eyecat_activity_wifisetting_one);
        initView();
        TaskExecutor.getInstance().execute(new Runnable() {
            @Override
            public void run() {
                final Result result = WLUserManager.getInstance().getStub().bindDevice(bid,bid,"CAMERA","CMMY02");
                if(0==result.status){
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EyecatWIFISettingOneActivity.this, "初次绑定成功", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(EyecatWIFISettingOneActivity.this, EyecatBindActivity.class);
                            i.putExtra("flag", true);
                            startActivity(i);
                            finish();
                        }
                    });
                }else{
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(EyecatWIFISettingOneActivity.this, "初次绑定失败:"+result.status, Toast.LENGTH_LONG).show();
                            Intent i = new Intent(EyecatWIFISettingOneActivity.this, EyecatBindActivity.class);
                            i.putExtra("flag", false);
                            startActivity(i);
                            finish();
                        }
                    });
                }
            }
        });
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
                    Intent intent = new Intent(EyecatWIFISettingOneActivity.this,EyecatWIFISettingTwoActivity.class);
                    startActivity(intent);
                    finish();
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
