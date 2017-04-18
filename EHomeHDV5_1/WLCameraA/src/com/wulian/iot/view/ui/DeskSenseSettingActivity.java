package com.wulian.iot.view.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.view.base.SimpleFragmentActivity;

import static com.wulian.icam.R.id.ll_protect_area_move;
import static com.wulian.icam.R.id.ll_protect_time_move;

/**
 * Created by Administrator on 2016/11/18.
 */

public class DeskSenseSettingActivity extends SimpleFragmentActivity implements View.OnClickListener {

    private RelativeLayout rl_sense_verylow;
    private RelativeLayout rl_sense_low;
    private RelativeLayout rl_sense_mid;
    private RelativeLayout rl_sense_high;
    private RelativeLayout rl_sense_veryhigh;
    private ImageView iv_choose_0;
    private ImageView iv_choose_1;
    private ImageView iv_choose_2;
    private ImageView iv_choose_3;
    private ImageView iv_choose_4;
    private ImageView titlebar_back;
    private TextView tv_verylow;
    private TextView tv_low;
    private TextView tv_mid;
    private TextView tv_high;
    private TextView tv_veryhigh;

    @Override
    public View onCreateView(View parent, String name, Context context, AttributeSet attrs) {
        return super.onCreateView(parent, name, context, attrs);
    }

    @Override
    public void root() {
        super.root();
        setContentView(R.layout.activity_sensitivity_setting);
        initViews();
        setListener();
    }

    private void initViews() {
        ((TextView) findViewById(R.id.titlebar_title)).setText(getString(R.string.protect_sensitivity));
        rl_sense_verylow = (RelativeLayout) findViewById(R.id.cateye_sensitivity_setting_verylow);
        rl_sense_low = (RelativeLayout) findViewById(R.id.cateye_sensitivity_setting_low);
        rl_sense_mid = (RelativeLayout) findViewById(R.id.cateye_sensitivity_setting_mid);
        rl_sense_high = (RelativeLayout) findViewById(R.id.cateye_sensitivity_setting_high);
        rl_sense_veryhigh = (RelativeLayout) findViewById(R.id.cateye_sensitivity_setting_veryhigh);
        iv_choose_0 = (ImageView) findViewById(R.id.iv_choose_0);
        iv_choose_1 = (ImageView) findViewById(R.id.iv_choose_1);
        iv_choose_2 = (ImageView) findViewById(R.id.iv_choose_2);
        iv_choose_3 = (ImageView) findViewById(R.id.iv_choose_3);
        iv_choose_4 = (ImageView) findViewById(R.id.iv_choose_4);
        titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
        tv_verylow = (TextView) findViewById(R.id.tv_verylow);
        tv_low = (TextView) findViewById(R.id.tv_low);
        tv_mid = (TextView) findViewById(R.id.tv_mid);
        tv_high = (TextView) findViewById(R.id.tv_high);
        tv_veryhigh = (TextView) findViewById(R.id.tv_veryhigh);
    }

    private void setListener() {
        rl_sense_verylow.setOnClickListener(this);
        rl_sense_low.setOnClickListener(this);
        rl_sense_mid.setOnClickListener(this);
        rl_sense_high.setOnClickListener(this);
        rl_sense_veryhigh.setOnClickListener(this);
        titlebar_back.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            this.finish();
        } else if (id == R.id.cateye_sensitivity_setting_low) {
            setChooseSensitivity(R.id.cateye_sensitivity_setting_low);
        } else if (id == R.id.cateye_sensitivity_setting_mid) {
            setChooseSensitivity(R.id.cateye_sensitivity_setting_mid);
        } else if (id == R.id.cateye_sensitivity_setting_high) {
            setChooseSensitivity(R.id.cateye_sensitivity_setting_high);

        }
    }

    private CameraHelper.Observer observer = new CameraHelper.Observer() {
        @Override
        public void avIOCtrlOnLine() {

        }

        @Override
        public void avIOCtrlDataSource(byte[] data, final int avIOCtrlMsgType) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (avIOCtrlMsgType) {

                    }
                }
            });

        }

        @Override
        public void avIOCtrlMsg(int resCode, String method) {

        }
    };

    //显示选择灵敏度的图标
    private void setChooseSensitivity(int resID) {
        if (resID == R.id.cateye_sensitivity_setting_low) {
            iv_choose_1.setVisibility(View.VISIBLE);
            iv_choose_2.setVisibility(View.INVISIBLE);
            iv_choose_3.setVisibility(View.INVISIBLE);
        } else if (resID == R.id.cateye_sensitivity_setting_mid) {
            iv_choose_1.setVisibility(View.INVISIBLE);
            iv_choose_2.setVisibility(View.VISIBLE);
            iv_choose_3.setVisibility(View.INVISIBLE);
        } else if (resID == R.id.cateye_sensitivity_setting_high) {
            iv_choose_1.setVisibility(View.INVISIBLE);
            iv_choose_2.setVisibility(View.INVISIBLE);
            iv_choose_3.setVisibility(View.VISIBLE);
        }
    }

}
