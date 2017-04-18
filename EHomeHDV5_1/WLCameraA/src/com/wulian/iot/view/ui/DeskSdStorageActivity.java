package com.wulian.iot.view.ui;

import com.tutk.IOTC.AVIOCTRLDEFs;
import com.tutk.IOTC.Packet;
import com.wulian.icam.R;
import com.wulian.iot.server.IotSendOrder;
import com.wulian.iot.server.helper.CameraHelper;
import com.wulian.iot.utils.RemindDialog;
import com.wulian.iot.view.base.SimpleFragmentActivity;
import com.wulian.iot.widght.DialogManager;
import com.yuantuo.customview.ui.WLToast;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class DeskSdStorageActivity extends SimpleFragmentActivity {
    @SuppressWarnings("unused")
    private ImageView ivback;
    private TextView tvTitle;
    private TextView tvtotal;
    private TextView tvfree;
    private Button btnformat;
    private float total, free;
    private CameraHelper.Observer observer = new CameraHelper.Observer() {
        @Override
        public void avIOCtrlOnLine() {
        }

        @Override
        public void avIOCtrlDataSource(final byte[] data, final int avIOCtrlMsgType) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    switch (avIOCtrlMsgType) {
                        case AVIOCTRLDEFs.IOTYPE_USER_IPCAM_SDCARD_FORMAT_RESP:
                            dismissDialog();// 等待 格式化对话框消失
                            if (Packet.byteArrayToInt_Little(data, 0) == 0) {
                                tvfree.setText(total + "G");
                                return;
                            }
                            WLToast.showToast(DeskSdStorageActivity.this, getResources().getString(R.string.desk_format_fail), Toast.LENGTH_SHORT);
                            break;
                    }
                }
            });
        }

        @Override
        public void avIOCtrlMsg(int resCode,String method) {

        }
    };
    @Override
    public void root() {
        setContentView(R.layout.activity_sdcard_storage);
    }

    @Override
    public void initView() {
        ivback = (ImageView) findViewById(R.id.titlebar_back);
        tvTitle = (TextView) findViewById(R.id.titlebar_title);
        tvtotal = (TextView) findViewById(R.id.tv_sd_storage_total);
        tvfree = (TextView) findViewById(R.id.tv_sd_storage_free);
        btnformat = (Button) findViewById(R.id.btn_desk_sd_format);
    }

    @Override
    public void initData() {
        total = getIntent().getFloatExtra("total", 0);
        free = getIntent().getFloatExtra("free", 0);
        if (cameaHelper != null) {
            cameaHelper.attach(observer);
        }
    }

    @Override
    public void initEvents() {
        tvTitle.setText(R.string.set_desk_format_sdcard);
        tvtotal.setText(total + "G");
        tvfree.setText(free + "G");
        btnformat.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showDialogNote();
            }
        });
        ivback.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                animationExit();
            }
        });
    }

    public void showDialogNote() {
        final RemindDialog rd = new RemindDialog(this);
        rd.setDialogWidth(getWindowManager());
        rd.setTitle(this.getResources().getString(
                R.string.set_desk_format_dialog_title));
        // 将删除卡内所有数据
        rd.setMessage(this.getResources().getString(
                R.string.set_desk_format_dialog_message0));
        rd.getTvSuer().setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                IotSendOrder.sendSdFormat(cameaHelper.getmCamera());// 发送格式化命令
                rd.getDialog().dismiss();
                showWaitDialogFormat();
            }
        });
        rd.showDialog();
    }

    /**
     * 格式化 等待的dialog
     */
    public void showWaitDialogFormat() {
        DialogManager manager = new DialogManager(this);
        // 自定义实现部分
        View view = manager.getView(DialogManager.iot_camera);
        LinearLayout layout = (LinearLayout) view
                .findViewById(R.id.dialog_view);// 加载布局
        ImageView spaceshipImage = (ImageView) view.findViewById(R.id.img);
        TextView tipTextView = (TextView) view.findViewById(R.id.tipTextView);// 提示文字
        spaceshipImage.setAnimation(manager
                .getAnimation(DialogManager.animation));
        tipTextView.setText(R.string.desk_format_wait_dialog);
        // 自定义实现部分
        if (layout != null) {
            manager.setCancelable(true);
            mDiglog = manager.getDialog(DialogManager.iot_dialog_style, layout);
            showDialog();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(cameaHelper!=null){
            cameaHelper.detach(observer);
        }
    }
}
