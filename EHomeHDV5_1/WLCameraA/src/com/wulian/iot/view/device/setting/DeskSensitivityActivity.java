package com.wulian.iot.view.device.setting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.Preference;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.utils.StringUtil;
import com.wulian.iot.Config;
import com.wulian.iot.view.adapter.DeskSensitivityAdapter;
import com.wulian.iot.view.base.SimpleFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/11/24.
 */

public class DeskSensitivityActivity extends SimpleFragmentActivity implements View.OnClickListener {
    private ListView lv_desk_sensitivity;
    private DeskSensitivityAdapter DSadapter;
    private List<String> data = new ArrayList<String>();
    private ImageView titlebar_back;
    private TextView titlebar_title;
    private String type;


    @Override
    public void root() {
        super.root();
        setContentView(R.layout.activity_desk_sensitivity);
        initData();
        initView();
        setListener();
        jumpToSensitivity();
    }

    public void initView() {
        ((TextView) findViewById(R.id.titlebar_title)).setText(getResources().getString(R.string.protect_sensitivity));
        lv_desk_sensitivity = (ListView) findViewById(R.id.lv_desk_sensitivity);
        titlebar_back = (ImageView) findViewById(R.id.titlebar_back);
    }

    @Override
    public void initData() {
        super.initData();
        type = getIntent().getStringExtra("type");

    }

    private void setListener() {
        titlebar_back.setOnClickListener(this);
    }

    private void jumpToSensitivity() {
        lv_desk_sensitivity.setAdapter(new DeskSensitivityAdapter(this, getData(), type));
        lv_desk_sensitivity.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView iv = (ImageView) view.findViewById(R.id.desk_sensitivity_pic);
                iv.setVisibility(View.VISIBLE);
                TextView tv = (TextView) view.findViewById(R.id.desk_sensitivity_type);
                String value = tv.getText().toString();
                editor.putString(Config.DESK_CAMERA_SENSITIVITY_SP, value);
                editor.commit();
                DeskSensitivityActivity.this.finish();
            }
        });
    }

    private List<String> getData() {
        data.add(getResources().getString(R.string.dt_super_lower));
        data.add(getResources().getString(R.string.cateye_sensitivity_setting_low));
        data.add(getResources().getString(R.string.cateye_sensitivity_setting_mid));
        data.add(getResources().getString(R.string.cateye_sensitivity_setting_high));
        data.add(getResources().getString(R.string.dt_super_higher));
        return data;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            this.finish();
        }
    }
}
