package cc.wulian.smarthomev5.utils;

import android.content.Context;
import android.view.View;

import com.yuantuo.customview.ui.WLDialog;

import cc.wulian.smarthomev5.R;

/**
 * Created by Administrator on 2017/1/19.
 */

public class WlDialogUtil {
   static WLDialog wlDialog;

    public static WLDialog owSharePwdDiaolg(final Context context, final String message) {
        WLDialog.Builder builder = new WLDialog.Builder(context);
        builder.setMessage(message)
                .setTitle("密码短信已生成")
                .setPositiveButton("分享")
                .setNegativeButton("取消")
                .setListener(new WLDialog.MessageListener() {

                    @Override
                    public void onClickPositive(View contentViewLayout) {
                        IntentUtil.sendMessage(context,message);
                        wlDialog.dismiss();
                    }

                    @Override
                    public void onClickNegative(View contentViewLayout) {
                        wlDialog.dismiss();
                    }

                });
        wlDialog = builder.create();
        wlDialog.show();

        return wlDialog;
    }
}
