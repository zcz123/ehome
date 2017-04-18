package cc.wulian.smarthomev5.activity.minigateway;

import android.content.Context;
import android.view.View;

import com.yuantuo.customview.ui.WLDialog;

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;

public class RemindUserGatewayDisconnectDialog {

    private BaseActivity mactivity;

    public RemindUserGatewayDisconnectDialog(BaseActivity activity){
        mactivity = activity;
    }

    public void remindUserGatewayRestart(){
        WLDialog.Builder builder = new WLDialog.Builder(mactivity);
        builder.setTitle(mactivity.getResources().getString(R.string.gateway_router_setting_dialog_toast));
        builder.setMessage(mactivity.getResources().getString(R.string.minigw_modify_wifi_settings_name_password));
        builder.setPositiveButton(mactivity.getResources().getString(R.string.common_ok));
        builder.setNegativeButton(mactivity.getResources().getString(R.string.cancel));
        builder.setListener(new WLDialog.MessageListener() {
            @Override
            public void onClickPositive(View view) {
            }
            @Override
            public void onClickNegative(View view) {

            }
        });
        WLDialog dlg = builder.create();
        dlg.show();
    }
}
