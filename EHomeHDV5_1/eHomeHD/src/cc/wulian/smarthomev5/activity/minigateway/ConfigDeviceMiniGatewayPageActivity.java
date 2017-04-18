package cc.wulian.smarthomev5.activity.minigateway;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import cc.wulian.smarthomev5.R;

public class ConfigDeviceMiniGatewayPageActivity extends Activity
        implements OnClickListener {
    private Button btn_next;
    private String add_device_id;
    String DeviceId;
    private ImageView bt_title_back;// 返回

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setViewContent();
        initView();
        initData();
        setListener();

    }

    private void initView() {
        btn_next = (Button) findViewById(R.id.device_mini_connect_net);
        bt_title_back = (ImageView) findViewById(R.id.titlebar_back);
    }

    private void initData() {
        DeviceId = getIntent().getStringExtra("deviceId");
        if (bt_title_back != null) {
            bt_title_back.setOnClickListener(getLeftClick());
        }
    }

    private void setListener() {
        btn_next.setOnClickListener(this);
    }

    protected void setViewContent() {
        setContentView(R.layout.device_mini_connect_net);
    }

    protected String getActivityTitle() {
        return getResources().getString(R.string.setting_wifi_setting_connect);
    }

    protected OnClickListener getLeftClick() {
        return new OnClickListener() {
            public void onClick(View v) {
                ConfigDeviceMiniGatewayPageActivity.this.finish();
            }
        };
    }

    private void judgeDeviceWiFiConfigTyle3(String add_device_id) {
            Intent it = new Intent(ConfigDeviceMiniGatewayPageActivity.this,
                    ConfigDeviceMiniGatewayConnectNetLastActivity.class);
            it.putExtra("Deviceid", DeviceId);
            startActivity(it);
            finish();
        }

        @Override
        public void onClick (View v){
            int id = v.getId();
            if (id == R.id.device_mini_connect_net) {
                judgeDeviceWiFiConfigTyle3(add_device_id);
            } else if (id == R.id.titlebar_back) {
                finish();
            }

        }

    }
