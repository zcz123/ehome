package cc.wulian.smarthomev5.activity;

import android.content.Intent;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.entity.RoomInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.event.DeviceEvent;
import cc.wulian.smarthomev5.event.DialogEvent;
import cc.wulian.smarthomev5.event.JoinDeviceEvent;
import cc.wulian.smarthomev5.fragment.device.joingw.DeviceConfigJoinGWActivity;
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

public class QuickEditActivity extends EventBusActivity {

    private LayoutInflater inflater;
    private View contentView;
    protected ProgressDialogManager mDialogManager = ProgressDialogManager
            .getDialogManager();
    public static QuickEditActivity quickEditActivity = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        quickEditActivity = this;
        initBar();
        inflater = LayoutInflater.from(this);
        contentView = inflater.inflate(R.layout.device_activity_quick_edit, null);
        setContentView(contentView);
    }

    public void initBar() {
        resetActionMenu();
        getCompatActionBar().setDisplayHomeAsUpEnabled(true);
        getCompatActionBar().setIconText(
                getResources().getString(R.string.device_ir_back));
        getCompatActionBar().setTitle(getResources().getString(R.string.explore_edit_shortcut_editing));
    }

    public void onEventMainThread(DeviceEvent event) {
        if(StringUtil.equals(event.action , DeviceEvent.QUICK_EDIT)){
            if(event.deviceInfo != null){
//            Log.d("---ccc---","gwid:"+event.deviceInfo.getGwID()+",devId:"+event.deviceInfo.getDevID());
//                DeviceCache cache = DeviceCache.getInstance(this);
//                WulianDevice curDevice = cache.getDeviceByID(this , event.deviceInfo.getGwID() ,event.deviceInfo.getDevID());
//                Log.d("---ccc---","isDeviceOnLine:"+curDevice.isDeviceOnLine());
                jumpToQuickEditScuessActivity(event.deviceInfo);

            }
        }
    }

    private void jumpToQuickEditScuessActivity(DeviceInfo devInfo){
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString(QuickEditSuccessActivity.QUICK_EDIT_GW_ID,devInfo.getGwID());
        bundle.putString(QuickEditSuccessActivity.QUICK_EDIT_DEV_ID,devInfo.getDevID());
        bundle.putString(QuickEditSuccessActivity.QUICK_EDIT_ROOM_ID,devInfo.getRoomID());
        intent.putExtra("QuickEditDeviceInfo",bundle);
        intent.setClass(QuickEditActivity.this,QuickEditSuccessActivity.class);
        startActivity(intent);
    //   finish();
    }

}
