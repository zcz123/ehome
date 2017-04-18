package cc.wulian.smarthomev5.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.wulian.iot.Config;
import com.yuantuo.customview.ui.WLDialog;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.event.DialogEvent;
import cc.wulian.smarthomev5.event.JoinDeviceEvent;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.fragment.device.DeviceDetailsFragment;
import cc.wulian.smarthomev5.fragment.device.joingw.DeviceJoinGWManager;
import cc.wulian.smarthomev5.tools.ActionBarCompat;
import cc.wulian.smarthomev5.tools.AreaList;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.ProgressDialogManager;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

/**
 * Created by Administrator on 2017/1/24.
 */

public class QuickEditSuccessActivity extends EventBusActivity {

    private BaseActivity mActivity;
    private LayoutInflater inflater;
    private View contentView;
    private TextView areaGroup;
    private EditText rename;
    private TextView commit;
    private LinearLayout areaLayout;
    private TextView lookDevice;
    private DeviceJoinGWManager joinGWManager = DeviceJoinGWManager
            .getInstance();
    private DeviceCache cache;
    private WulianDevice curDevice;
    protected ProgressDialogManager mDialogManager = ProgressDialogManager
            .getDialogManager();
    protected WLDialog returnNoticeDialog;
    private String gwID ;
    private String devID ;
    private String roomID;
    private boolean isSaved = false;
    public static final String QUICK_EDIT_GW_ID = "qiuck_edit_gwID";
    public static final String QUICK_EDIT_DEV_ID = "qiuck_edit_devID";
    public static final String QUICK_EDIT_ROOM_ID = "qiuck_edit_roomID";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getBundleExtra("QuickEditDeviceInfo");
        gwID = bundle.getString(QUICK_EDIT_GW_ID);
        devID = bundle.getString(QUICK_EDIT_DEV_ID);
        roomID = bundle.getString(QUICK_EDIT_ROOM_ID);
        cache=DeviceCache.getInstance(mActivity);
        curDevice = cache.getDeviceByID(mActivity , gwID ,devID);
        initBar();
        inflater = LayoutInflater.from(this);
        contentView = inflater.inflate(R.layout.device_activity_quick_edit_success, null);
        setContentView(contentView);
        initContentView();
    }

    public void initBar() {
        resetActionMenu();
        getCompatActionBar().setDisplayHomeAsUpEnabled(true);
        getCompatActionBar().setIconText(
                getResources().getString(R.string.device_ir_back));
        getCompatActionBar().setTitle(curDevice.getDefaultDeviceName());
        getCompatActionBar().setLeftIconClickListener(new ActionBarCompat.OnLeftIconClickListener() {
            @Override
            public void onClick(View v) {
                if(!isSaved){
                  showNoticeDialog();
                }
            }
        });
    }

    private void showNoticeDialog(){
        WLDialog.Builder builder = new WLDialog.Builder(this);
        builder.setTitle(getResources().getString(R.string.embedded_switch_prompt_hint))
                .setMessage(getResources().getString(R.string.explore_edit_not_saved))
                .setPositiveButton(getResources().getString(R.string.explore_edit_go_on))
                .setNegativeButton(getResources().getString(R.string.explore_edit_give_up))
                .setDismissAfterDone(true).setListener(new WLDialog.MessageListener() {

            @Override
            public void onClickPositive(View contentViewLayout) {

            }

            public void onClickNegative(View contentViewLayout) {
                QuickEditSuccessActivity.this.finish();
                QuickEditActivity.quickEditActivity.finish();
            }

        });
        returnNoticeDialog = builder.create();
        returnNoticeDialog.show();
    }

    private void initContentView() {
        areaLayout = (LinearLayout) contentView
                .findViewById(R.id.config_quick_edit_area_linear);
        areaGroup = (TextView) contentView
                .findViewById(R.id.config_quick_edit_area);
        rename = (EditText) contentView.findViewById(R.id.config_quick_edit);
        commit = (TextView) contentView
                .findViewById(R.id.config_quick_edit_commit);
        lookDevice = (TextView) contentView.findViewById(R.id.tv_quick_edit_look_device);
        lookDevice.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG);
        rename.setHint(curDevice.getDefaultDeviceName());
        if(!StringUtil.isNullOrEmpty(curDevice.getDeviceName())){
            rename.setText(curDevice.getDeviceName());
            rename.setSelection(curDevice.getDeviceName().length());
            rename.requestFocus();
        }else{
            rename.setText(curDevice.getDefaultDeviceName());
            rename.setSelection(curDevice.getDeviceName().length());
            rename.requestFocus();
        }
        commit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                    String settingName = rename.getText().toString();
                    if (settingName != null) {
                        SendMessage.sendSetDevMsg(
                                QuickEditSuccessActivity.this,
                                curDevice.getDeviceGwID(), CmdUtil.MODE_UPD,
                                curDevice.getDeviceID(), "",
                                curDevice.getDeviceType(), settingName,
                                curDevice.getDeviceCategory(), tempRoomID, "",
                                "", null, true, false);
                    }
                isSaved = true;
                 QuickEditSuccessActivity.this.finish();
            }
        });

        //点击查看设备详情
        lookDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCommonDeviceDetails(curDevice);
            }
        });
        // 初次进入初始化commit按钮
        changeCommitBtnByQueueItem();
        // 初次进去初始化设备相关内容
        initCurrentDeviceView(curDevice);
    }

    public void changeCommitBtnByQueueItem() {
       commit.setText(getResources()
                    .getString(
                            R.string.set_sound_notification_bell_prompt_choose_complete));
    }

    private String tempRoomID;

    public void initCurrentDeviceView(final WulianDevice device) {
        DeviceAreaEntity areaEntity = AreaGroupManager
                .getInstance()
                .getDeviceAreaEntity(gwID, roomID);
        if (areaEntity != null) {
            areaGroup.setText(areaEntity.getName());
        }
        areaLayout.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                final AreaList areaList = new AreaList(
                        QuickEditSuccessActivity.this, true);
                areaList.setOnAreaListItemClickListener(new AreaList.OnAreaListItemClickListener() {

                    @Override
                    public void onAreaListItemClicked(AreaList list, int pos,
                                                      RoomInfo info) {
                        tempRoomID = list.getAdapter().getItem(pos).getRoomID();
                        areaGroup.setText(list.getAdapter().getItem(pos)
                                .getName());
                        SendMessage.sendSetDevMsg(
                                QuickEditSuccessActivity.this,
                                device.getDeviceGwID(), CmdUtil.MODE_UPD,
                                device.getDeviceID(), "",
                                device.getDeviceType(), device.getDeviceName(),
                                device.getDeviceCategory(), tempRoomID, "", "",
                                null, true, false);
                        areaList.dismiss();
                    }
                });
                areaList.show(arg0);
            }
        });

    }

    // 进入常用设备详情
    private void showCommonDeviceDetails(WulianDevice device) {
        String isvalidate=device.getDeviceInfo().getIsvalidate();
        if(isvalidate!=null&&isvalidate.equals("2")){
            Intent it =new Intent(mActivity, BackMusicActivationActivity.class);
            it.putExtra(Config.DEVICE_ID,device.getDeviceID());
            it.putExtra(Config.DEVICE_TYPE,device.getDeviceType());
            it.putExtra(Config.GW_ID,device.getDeviceGwID());
            startActivity(it);
            return;
        }
        Bundle args = new Bundle();
        args.putString(DeviceDetailsFragment.EXTRA_DEV_GW_ID,device.getDeviceGwID());
        args.putString(DeviceDetailsFragment.EXTRA_DEV_ID, device.getDeviceID());
        Intent intent = new Intent();
        intent.setClass(this, DeviceDetailsActivity.class);
        if (args != null)
            intent.putExtras(args);
        this.startActivity(intent);
    }

    public void onEventMainThread(JoinDeviceEvent event) {
        changeCommitBtnByQueueItem();
    }

    public ProgressDialogManager getDialogManager() {
        return mDialogManager;
    }

    public void onEventMainThread(DialogEvent event) {
        // 自动刷新commit按钮
        ProgressDialogManager dialogManager = getDialogManager();
        if (dialogManager.containsDialog(SendMessage.ACTION_SET_DEVICE
                + event.actionKey)) {
            dialogManager.dimissDialog(SendMessage.ACTION_SET_DEVICE
                    + event.actionKey, event.resultCode);
        } else if (dialogManager.containsDialog(event.actionKey)) {
            dialogManager.dimissDialog(event.actionKey, event.resultCode);
        }

    }
}
