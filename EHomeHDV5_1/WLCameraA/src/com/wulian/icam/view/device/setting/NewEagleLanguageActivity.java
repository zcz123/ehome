package com.wulian.icam.view.device.setting;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.icam.R;
import com.wulian.icam.common.EagleConfig;
import com.wulian.icam.utils.StringUtil;
import com.wulian.icam.view.base.BaseFragmentActivity;

/**
 * Created by hxc on 2016/12/23.
 * func: 播报语言设置界面
 */

public class NewEagleLanguageActivity extends BaseFragmentActivity implements View.OnClickListener {

    private ImageView titlebarBack;
    private TextView titlebarTitle;
    private RelativeLayout mainTitlebar;
    private RelativeLayout rlLanguageEn;
    private RelativeLayout rlLanguageCh;
    private ImageView ivLanguageCh;
    private ImageView ivLanguageEn;


    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_neweagle_language);
        initView();
        initData();
    }

    private void initView() {
        ((TextView) findViewById(R.id.titlebar_title))
                .setText(R.string.desk_broadcast_language);
        titlebarBack = (ImageView) findViewById(R.id.titlebar_back);
        titlebarTitle = (TextView) findViewById(R.id.titlebar_title);
        mainTitlebar = (RelativeLayout) findViewById(R.id.main_titlebar);
        rlLanguageEn = (RelativeLayout) findViewById(R.id.rl_language_en);
        rlLanguageCh = (RelativeLayout) findViewById(R.id.rl_language_ch);
        ivLanguageCh = (ImageView) findViewById(R.id.iv_language_ch);
        ivLanguageEn = (ImageView) findViewById(R.id.iv_language_en);
        titlebarBack.setOnClickListener(this);
        rlLanguageEn.setOnClickListener(this);
        rlLanguageCh.setOnClickListener(this);
    }

    private void initData() {
        String language = getIntent().getStringExtra("language");
        if (!StringUtil.isNullOrEmpty(language)) {
            if (language.equals("1")) {
                ivLanguageCh.setVisibility(View.VISIBLE);
            } else if (language.equals("2")) {
                ivLanguageEn.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.titlebar_back) {
            NewEagleLanguageActivity.this.finish();
        } else if (id == R.id.rl_language_ch) {
            ivLanguageCh.setVisibility(View.VISIBLE);
            ivLanguageEn.setVisibility(View.INVISIBLE);
            setResult(RESULT_OK, new Intent().putExtra("language", EagleConfig.LANGUAGE_CH));
            this.finish();
        } else if (id == R.id.rl_language_en) {
            ivLanguageCh.setVisibility(View.INVISIBLE);
            ivLanguageEn.setVisibility(View.VISIBLE);
            setResult(RESULT_OK, new Intent().putExtra("language", EagleConfig.LANGUAGE_EN));
            this.finish();
        }
    }
}
