package com.wulian.icam.view.device.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.model.NewEagleInfo;
import com.wulian.icam.utils.StringUtil;
import com.wulian.icam.view.base.BaseFragmentActivity;

/**
 * Created by Administrator on 2016/12/26.
 * func:新猫眼灵敏度设置界面
 */

public class NewEagleSensitiveActivity extends BaseFragmentActivity implements View.OnClickListener {
    private ImageView titlebarBack;
    private ImageView ivSensitiveLow;
    private RelativeLayout rlSensitiveLow;
    private ImageView ivSensitiveHigh;
    private RelativeLayout rlSensitiveHigh;

    private NewEagleInfo newEagleInfo;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_neweagle_sensitive);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        ((TextView) findViewById(R.id.titlebar_title))
                .setText(R.string.cateye_sensitivity_setting);
        titlebarBack = (ImageView) findViewById(R.id.titlebar_back);
        ivSensitiveLow = (ImageView) findViewById(R.id.iv_sensitive_low);
        rlSensitiveLow = (RelativeLayout) findViewById(R.id.rl_sensitive_low);
        ivSensitiveHigh = (ImageView) findViewById(R.id.iv_sensitive_high);
        rlSensitiveHigh = (RelativeLayout) findViewById(R.id.rl_sensitive_high);
    }

    private void initListener() {
        titlebarBack.setOnClickListener(this);
        rlSensitiveLow.setOnClickListener(this);
        rlSensitiveHigh.setOnClickListener(this);
    }

    private void initData() {
        newEagleInfo = (NewEagleInfo) getIntent().getSerializableExtra("newEagleInfo");
        if (newEagleInfo.getPIRDetectLevel() .equals("1")) {
            ivSensitiveLow.setVisibility(View.VISIBLE);
            ivSensitiveHigh.setVisibility(View.GONE);
        } else if (newEagleInfo.getPIRDetectLevel().equals("0")) {
            ivSensitiveLow.setVisibility(View.GONE);
            ivSensitiveHigh.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            //在下面finish；
        } else if (id == R.id.rl_sensitive_low) {
            ivSensitiveLow.setVisibility(View.VISIBLE);
            ivSensitiveHigh.setVisibility(View.GONE);
            newEagleInfo.setPIRDetectLevel("1");
            setResult(RESULT_OK, new Intent().putExtra("newEagleInfo", newEagleInfo));
        } else if (id == R.id.rl_sensitive_high) {
            ivSensitiveLow.setVisibility(View.GONE);
            ivSensitiveHigh.setVisibility(View.VISIBLE);
            newEagleInfo.setPIRDetectLevel("0");
            setResult(RESULT_OK, new Intent().putExtra("newEagleInfo", newEagleInfo));
        }
        this.finish();
    }
}
