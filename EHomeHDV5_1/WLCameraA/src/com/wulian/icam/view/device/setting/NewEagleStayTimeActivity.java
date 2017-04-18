package com.wulian.icam.view.device.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.model.NewEagleInfo;
import com.wulian.icam.view.base.BaseFragmentActivity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/12/26.
 */

public class NewEagleStayTimeActivity extends BaseFragmentActivity implements View.OnClickListener {

    private ImageView titlebarBack;
    private ListView lvStayTime;
    private List<String> data = new ArrayList<String>();

    private String stayTime;
    private NewEagleInfo newEagleInfo;
    private StayTimeAdapter stayTimeadapter;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_neweagle_stay_time);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        titlebarBack = (ImageView) findViewById(R.id.titlebar_back);
        lvStayTime = (ListView) findViewById(R.id.lv_stay_time);
    }

    private void initListener() {
        titlebarBack.setOnClickListener(this);
        lvStayTime.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView iv = (ImageView) view.findViewById(R.id.iv_stay);
                TextView tv = (TextView) view.findViewById(R.id.tv_stay_time);
                newEagleInfo.setHoverDetectTime(tv.getText().toString().replace("s", "").trim());
                iv.setVisibility(View.VISIBLE);
                setResult(RESULT_OK, new Intent().putExtra("newEagleInfo", newEagleInfo));
                NewEagleStayTimeActivity.this.finish();
            }
        });

    }

    private void initData() {
        newEagleInfo = (NewEagleInfo) getIntent().getSerializableExtra("newEagleInfo");
        stayTime = newEagleInfo.getHoverDetectTime();
        stayTimeadapter = new StayTimeAdapter(this, getData(), stayTime);
        lvStayTime.setAdapter(stayTimeadapter);
    }

    private List<String> getData() {
        data.add("5s");
        data.add("10s");
        data.add("15s");
        data.add("20s");
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
