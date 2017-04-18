package cc.wulian.smarthomev5.activity.iotc.res;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.wulian.iot.view.base.SimpleFragmentActivity;

import cc.wulian.h5plus.common.JsUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.service.html5plus.plugins.SmarthomeFeatureImpl;

/**
 * Created by syf on 2016/10/21.
 */

public class IOTCDevConfigFailActivity extends SimpleFragmentActivity {
    private TextView configTxt,configHint;
    private ImageView resImg;
    private Button btn;
    @Override
    public void root() {
        setContentView(com.wulian.icam.R.layout.activity_eagle_setting_wifi_fail);
    }
    @Override
    public void initView() {
        resImg =  (ImageView) findViewById(com.wulian.icam.R.id.iotc_dev_res_img);
        btn = (Button) findViewById(com.wulian.icam.R.id.eye_retry_button);
        configTxt = (TextView) findViewById(R.id.iot_dev_config_txt);
        configHint = (TextView) findViewById(R.id.iot_dev_config_hint);
        upDateUi();
    }
    private void upDateUi(){
        configTxt.setText(getResources().getString(R.string.gateway_router_setting_wifi_relay_failure));
        resImg.setBackground(getResources().getDrawable(R.drawable.eagle_wifi_conn_fail));
        configHint.setVisibility(View.VISIBLE);
    }
    @Override
    public void initEvents() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JsUtil.getInstance().execCallback(SmarthomeFeatureImpl.pWebview, SmarthomeFeatureImpl.callbackid,
                        "-1", JsUtil.OK,
                        false);
                finish();
            }
        });
    }
}
