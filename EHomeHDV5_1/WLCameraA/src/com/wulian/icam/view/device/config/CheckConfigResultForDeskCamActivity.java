package com.wulian.icam.view.device.config;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.wulian.icam.R;
import com.wulian.icam.utils.DialogUtils;
import com.wulian.icam.view.base.BaseFragmentActivity;

import static android.R.attr.id;

public class CheckConfigResultForDeskCamActivity extends BaseFragmentActivity implements View.OnClickListener {

    public static final int SETUP_WIFI_FAILED = 1;
    private Button btn_finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        handler.sendEmptyMessageDelayed(SETUP_WIFI_FAILED, 60000);
    }

    private void initView() {
        btn_finish = (Button) findViewById(R.id.btn_finish);

        btn_finish.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_finish) {
            finishMe();
        } else if (id == R.id.titlebar_back) {
            finishMe();
        }
    }

    private void finishMe() {
        handler.removeMessages(SETUP_WIFI_FAILED);
        finish();
    }

    @Override
    protected void setViewContent() {
        setContentView(R.layout.activity_check_config_result_for_desk_cam);
    }

    @Override
    protected String getActivityTitle() {
        return getResources().getString(R.string.desktop_wifi_setup_connecting_title);
    }

    private  Handler handler = new Handler() {
        private Dialog mTipDialog;

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case SETUP_WIFI_FAILED:
                    showNetworkFailedDialog();
                    break;
                default:
                    break;
            }
        }

        private void showNetworkFailedDialog() {
            mTipDialog = DialogUtils.showCommonDialog(CheckConfigResultForDeskCamActivity.this, true,
                    getResources().getString(R.string.common_kind_tip),
                    getResources().getString(R.string.desktop_wifi_setup_failed_tip),
                    getResources().getString(R.string.common_retry),
                    getResources().getString(R.string.common_cancel),
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTipDialog.dismiss();
                            Intent it = new Intent(CheckConfigResultForDeskCamActivity.this, WifiInputActivity.class);
                            it.putExtra("configInfo", CheckConfigResultForDeskCamActivity.this.getIntent().getParcelableExtra("configInfo"));
                            startActivity(it);
                            CheckConfigResultForDeskCamActivity.this.finish();
                        }
                    }, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            mTipDialog.dismiss();
                            CheckConfigResultForDeskCamActivity.this.finish();
                        }
                    });
        }
    };

}
