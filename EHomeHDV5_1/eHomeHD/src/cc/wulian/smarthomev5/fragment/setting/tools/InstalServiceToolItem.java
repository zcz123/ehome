package cc.wulian.smarthomev5.fragment.setting.tools;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.InstalServiceToolActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

/**
 * 安装服务工具
 */

public class InstalServiceToolItem extends AbstractSettingItem {
    public InstalServiceToolItem(Context context) {
        super(context, R.drawable.setting_instal_tool_icon, context.getResources()
                .getString(R.string.set_installation_services));
    }

    @Override
    public void initSystemState() {
        super.initSystemState();
        infoImageView.setVisibility(View.VISIBLE);
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
        intent.setClass(mContext, InstalServiceToolActivity.class);
        mContext.startActivity(intent);
    }

}
