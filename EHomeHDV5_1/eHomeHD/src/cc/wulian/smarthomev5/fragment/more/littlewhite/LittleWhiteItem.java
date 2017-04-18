package cc.wulian.smarthomev5.fragment.more.littlewhite;

import android.content.Context;
import android.content.Intent;
import android.view.View;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.LittleWhiteActivity;
import cc.wulian.smarthomev5.activity.ScanQRCodeActivity;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

/**
 * Created by Administrator on 2016/11/15.
 */

public class LittleWhiteItem extends AbstractSettingItem {


    public LittleWhiteItem(Context context) {
        super(context, R.drawable.third_party_login_item, context.getResources().getString(R.string.gateway_explore_third_party_login));
    }

    @Override
    public void initSystemState() {
        super.initSystemState();
        infoImageView.setVisibility(View.VISIBLE);
        infoImageView.setImageResource(R.drawable.arrow_cutover_gateway);
    }

    @Override
    public void doSomethingAboutSystem() {
        Intent intent = new Intent();
        intent.setClass(mContext, LittleWhiteActivity.class);
        mContext.startActivity(intent);
    }
}
