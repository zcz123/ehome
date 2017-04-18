package com.wulian.icam.view.device.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.model.NewEagleInfo;
import com.wulian.icam.view.base.BaseFragmentActivity;

/**
 * Created by Administrator on 2016/12/26.
 */

public class NewEagleLinkageActivity extends BaseFragmentActivity implements View.OnClickListener {
    private ImageView titlebarBack;
    private ImageView ivLinkageSnapshot;
    private RelativeLayout rlLinkageSnapshot;
    private ImageView ivLinkageVideo;
    private RelativeLayout rlLinkageVideo;

    private NewEagleInfo newEagleInfo;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_neweagle_linkage);
        initView();
        initListener();
        initData();
    }

    private void initView() {
        ((TextView) findViewById(R.id.titlebar_title))
                .setText("联动类型");
        titlebarBack = (ImageView) findViewById(R.id.titlebar_back);
        ivLinkageSnapshot = (ImageView) findViewById(R.id.iv_linkage_snapshot);
        rlLinkageSnapshot = (RelativeLayout) findViewById(R.id.rl_linkage_snapshot);
        ivLinkageVideo = (ImageView) findViewById(R.id.iv_linkage_video);
        rlLinkageVideo = (RelativeLayout) findViewById(R.id.rl_linkage_video);
    }

    private void initListener() {
        titlebarBack.setOnClickListener(this);
        rlLinkageSnapshot.setOnClickListener(this);
        rlLinkageVideo.setOnClickListener(this);
    }

    private void initData() {
        newEagleInfo = (NewEagleInfo) getIntent().getSerializableExtra("newEagleInfo");
        if (newEagleInfo.getHoverProcMode() .equals("0")) {
            ivLinkageSnapshot.setVisibility(View.VISIBLE);
            ivLinkageVideo.setVisibility(View.GONE);
        } else if (newEagleInfo.getHoverProcMode().equals("1")) {
            ivLinkageSnapshot.setVisibility(View.GONE);
            ivLinkageVideo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            //finish方法下面执行
        } else if (id == R.id.rl_linkage_snapshot) {
            ivLinkageVideo.setVisibility(View.GONE);
            ivLinkageSnapshot.setVisibility(View.VISIBLE);
            newEagleInfo.setHoverProcMode("0");
            setResult(RESULT_OK, new Intent().putExtra("newEagleInfo", newEagleInfo));
        } else if (id == R.id.rl_linkage_video) {
            ivLinkageVideo.setVisibility(View.VISIBLE);
            ivLinkageSnapshot.setVisibility(View.GONE);
            newEagleInfo.setHoverProcMode("1");
            setResult(RESULT_OK, new Intent().putExtra("newEagleInfo", newEagleInfo));

        }
        this.finish();
    }
}
