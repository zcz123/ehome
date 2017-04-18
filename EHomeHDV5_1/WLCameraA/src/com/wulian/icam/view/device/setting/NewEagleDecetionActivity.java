package com.wulian.icam.view.device.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.wulian.icam.R;
import com.wulian.icam.common.EagleConfig;
import com.wulian.icam.model.NewEagleInfo;
import com.wulian.icam.view.base.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Administrator on 2016/12/23.
 * func：新猫眼移动侦测界面
 */

public class NewEagleDecetionActivity extends BaseFragmentActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {
    private ImageView titlebarBack;
    private RelativeLayout rlPirSwitch;
    private TextView tvSensitivityShow;
    private ImageView ivPir;
    private RelativeLayout rlPirSensitivity;
    private TextView tvStayShow;
    private ImageView ivStay;
    private RelativeLayout rlStayTime;
    private TextView tvLinkageShow;
    private ImageView ivLinkage;
    private RelativeLayout rlLinkageType;
    private Button btnStartProtect;
    private ToggleButton tbPirSwitch;

    private NewEagleInfo newEagleInfo;

    private HashMap<String, String> sensitivityMap;
    private HashMap<String, String> hoverTimeMap;
    private HashMap<String, String> HoverTypeMap;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_neweagle_detection);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        ((TextView) findViewById(R.id.titlebar_title))
                .setText(R.string.device_ir_setting);
        titlebarBack = (ImageView) findViewById(R.id.titlebar_back);
        rlPirSwitch = (RelativeLayout) findViewById(R.id.rl_pir_switch);
        tvSensitivityShow = (TextView) findViewById(R.id.tv_sensitivity_show);
        ivPir = (ImageView) findViewById(R.id.iv_pir);
        rlPirSensitivity = (RelativeLayout) findViewById(R.id.rl_pir_sensitivity);
        tvStayShow = (TextView) findViewById(R.id.tv_stay_show);
        ivStay = (ImageView) findViewById(R.id.iv_stay);
        rlStayTime = (RelativeLayout) findViewById(R.id.rl_stay_time);
        tvLinkageShow = (TextView) findViewById(R.id.tv_linkage_show);
        ivLinkage = (ImageView) findViewById(R.id.iv_linkage);
        rlLinkageType = (RelativeLayout) findViewById(R.id.rl_linkage_type);
        btnStartProtect = (Button) findViewById(R.id.btn_start_protect);
        tbPirSwitch = (ToggleButton) findViewById(R.id.tb_pir_switch);
    }

    private void initListener() {
        titlebarBack.setOnClickListener(this);
        rlStayTime.setOnClickListener(this);
        rlLinkageType.setOnClickListener(this);
        btnStartProtect.setOnClickListener(this);
        rlPirSensitivity.setOnClickListener(this);
        tbPirSwitch.setOnCheckedChangeListener(this);
    }

    private void initData() {
        newEagleInfo = (NewEagleInfo) getIntent().getSerializableExtra("newEagleInfo");
        showPIRInfo();
    }

    private void showPIRInfo() {
        if (newEagleInfo.getPIRSwitch().equals("1")) {
            tbPirSwitch.setChecked(true);
        } else {
            tbPirSwitch.setChecked(false);
        }
        switch (newEagleInfo.getPIRDetectLevel()) {
            case "0":
                tvSensitivityShow.setText(getResources().getString(R.string.cateye_sensitivity_setting_high));
                break;
            case "1":
                tvSensitivityShow.setText(getResources().getString(R.string.cateye_sensitivity_setting_low));
                break;
            default:
                break;
        }

        switch (newEagleInfo.getHoverDetectTime()) {
            case "5":
                tvStayShow.setText("5s");
                break;
            case "10":
                tvStayShow.setText("10s");
                break;
            case "15":
                tvStayShow.setText("15s");
                break;
            case "20":
                tvStayShow.setText("20s");
                break;
            default:
                break;
        }
        switch (newEagleInfo.getHoverProcMode()) {
            case "0":
                tvLinkageShow.setText("抓拍");
                break;
            case "1":
                tvLinkageShow.setText("录像");
                break;
            default:
                break;
        }
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            this.finish();
        } else if (id == R.id.rl_pir_sensitivity) {
            startActivityForResult(new Intent(NewEagleDecetionActivity.this, NewEagleSensitiveActivity.class).putExtra("newEagleInfo", newEagleInfo), EagleConfig.REQUEST_FOR_SENSITIVE);
        } else if (id == R.id.rl_linkage_type) {
            startActivityForResult(new Intent(NewEagleDecetionActivity.this, NewEagleLinkageActivity.class).putExtra("newEagleInfo", newEagleInfo), EagleConfig.REQUEST_FOR_LINKAGE);
        } else if (id == R.id.rl_stay_time) {
            startActivityForResult(new Intent(NewEagleDecetionActivity.this, NewEagleStayTimeActivity.class).putExtra("newEagleInfo", newEagleInfo), EagleConfig.REQUEST_FOR_STAY_TIME);
        } else if (id == R.id.btn_start_protect) {
            setResult(RESULT_OK, new Intent().putExtra("newEagleInfo", newEagleInfo));
            this.finish();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        if (id == R.id.tb_pir_switch) {
            if (isChecked) {
                newEagleInfo.setPIRSwitch("1");
                rlLinkageType.setVisibility(View.VISIBLE);
                rlPirSensitivity.setVisibility(View.VISIBLE);
                rlStayTime.setVisibility(View.VISIBLE);
            } else {
                newEagleInfo.setPIRSwitch("0");
                rlLinkageType.setVisibility(View.GONE);
                rlPirSensitivity.setVisibility(View.GONE);
                rlStayTime.setVisibility(View.GONE);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case EagleConfig.REQUEST_FOR_SENSITIVE:
                if (resultCode == RESULT_OK) {
                    newEagleInfo = (NewEagleInfo) data.getSerializableExtra("newEagleInfo");
                    showPIRInfo();
                }
                break;
            case EagleConfig.REQUEST_FOR_LINKAGE:
                if (resultCode == RESULT_OK) {
                    newEagleInfo = (NewEagleInfo) data.getSerializableExtra("newEagleInfo");
                    showPIRInfo();
                }
                break;

            case EagleConfig.REQUEST_FOR_STAY_TIME:
                if(resultCode ==RESULT_OK){
                    newEagleInfo = (NewEagleInfo) data.getSerializableExtra("newEagleInfo");
                    showPIRInfo();
                }
                break;
            default:
                break;
        }
    }
}
