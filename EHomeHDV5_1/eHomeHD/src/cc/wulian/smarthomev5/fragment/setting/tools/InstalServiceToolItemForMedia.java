package cc.wulian.smarthomev5.fragment.setting.tools;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.InstalServiceToolActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

/**
 * Created by yuxiaoxuan on 2017/1/3.
 * 安装服务工具
 */

public class InstalServiceToolItemForMedia extends AbstractSettingItem {
    public InstalServiceToolItemForMedia(Context context) {
        super(context, R.drawable.thermostat_mode_heat_select, "中央空调");
    }

    @Override
    public void initSystemState() {
        super.initSystemState();
        infoImageView.setVisibility(View.GONE);
        infoImageView.setImageResource(R.drawable.voice_remind_right);
        infoImageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                startToolActivity();
            }
        });
    }

    @Override
    public void doSomethingAboutSystem() {
        startToolActivity();
    }

    private void startToolActivity(){
        Intent intent = new Intent();
        intent.putExtra("tooltype" , "0001");
        intent.setClass(mContext, InstalServiceToolActivity.class);
        mContext.startActivity(intent);
    }



}
