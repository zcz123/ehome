package com.yuantuo.bean;

import android.content.Context;
import android.content.DialogInterface;

import com.yuantuo.customview.ui.WLDialog;

/**
 * Created by Administrator on 2016/12/13 0013.
 */

public class SingleErrorDialog {
    public static SingleErrorDialog instant=null;
    private static boolean isShowning=false;
    private WLDialog dialog=null;
    private SingleErrorDialog(){}
    public static SingleErrorDialog getInstant(){
        if (instant==null){
            synchronized (SingleErrorDialog.class){
                if (instant==null){
                    instant=new SingleErrorDialog();
                }
            }
        }
        return  instant;
    }
    public void showErrorDialog(Context mContext) {
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setNegativeButton((String)null);
        builder.setPositiveButton(com.yuantuo.customview.R.string.switch_off);
        builder.setContentView(com.yuantuo.customview.R.layout.dialog_error_content);

        if (dialog==null){
            dialog= builder.create();
        }
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog1) {
                isShowning=false;
                dialog=null;
            }
        });
        if(!isShowning) {
            isShowning=true;
            dialog.show();
        }
    }
}
