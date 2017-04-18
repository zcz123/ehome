package cc.wulian.smarthomev5.adapter.camera;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import com.wulian.iot.Config;
import com.wulian.iot.HandlerConstant;
import com.wulian.iot.view.device.setting.SetEagleCameraActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.os.Handler.Callback;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cc.wulian.ihome.wan.sdk.user.entity.AMSDeviceInfo;
import cc.wulian.ihome.wan.sdk.user.entity.BindUser;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.iotc.share.EagleShareActivity;
import cc.wulian.smarthomev5.tools.BaiduPushManager;
import cc.wulian.smarthomev5.tools.DevicesUserManage;
import cc.wulian.smarthomev5.view.swipemenu.MonitorSwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenu;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuCreator;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuItem;

public class EagleCameraAdapter extends SwipeMenuAdapter<AMSDeviceInfo> {
    private TextView name;
    private TextView replay;
    private TextView setting;
    private ImageView type;
    private ImageView iv_replay;
    private ImageView iv_setting;
    private RelativeLayout rl_replay;
    private RelativeLayout rl_setting;
    public static String SHARE_MODEL = "SHARE_MODEL";
    private List<String> deviceIds = null;
    private static final String TAG = "EagleCameraAdapter";

    public EagleCameraAdapter(Context context, List<AMSDeviceInfo> mData) {
        super(context, mData);
//        this.setMenuCreator(this.leftDeleteItemCreator);
        this.setOnMenuItemClickListener(onMenuItemClickListener);
    }

    @Override
    protected void bindView(Context mContext2, View convertView, final int position,
                            final AMSDeviceInfo item) {
        name = (TextView) convertView.findViewById(R.id.monitor_name);
        replay = (TextView) convertView.findViewById(R.id.tv_cameralist_replay);
        setting = (TextView) convertView.findViewById(R.id.tv_cameralist_setting);
        type = (ImageView) convertView.findViewById(R.id.monitor_type);
        iv_replay = (ImageView) convertView.findViewById(R.id.iv_cameralist_replay);
        iv_setting = (ImageView) convertView.findViewById(R.id.iv_cameralist_setting);
        rl_replay = (RelativeLayout) convertView.findViewById(R.id.rl_replay);
        rl_setting = (RelativeLayout) convertView.findViewById(R.id.rl_setting);

        if (!item.getIsAdmin()){
            replay.setVisibility(View.INVISIBLE);
            iv_replay.setVisibility(View.INVISIBLE);
        }
        if(item.getDeviceName().isEmpty()){
            name.setText(mContext.getResources().getString(R.string.monitor_video_eagle));//WuLian猫眼
        }else {
            name.setText(item.getDeviceName());
        }
        type.setImageResource(R.drawable.monitor_cat_eye_online);
        replay.setText(mContext.getResources().getString(R.string.home_monitor_share));//分享
        iv_replay.setImageResource(R.drawable.cameralist_cateye_share);
        rl_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//设置

                Intent mIntent = new Intent(mContext, SetEagleCameraActivity.class);
                mIntent.putExtra(Config.eagleSettingEnter, SetEagleCameraActivity.WITHOUT_CAMERA_SETTING);
                mIntent.putExtra(Config.tutkUid, item.getDeviceId());
                mIntent.putExtra(Config.tutkPwd, item.getPassword());
                mIntent.putExtra(Config.eagleName, item.getDeviceName());
                mIntent.putExtra(Config.isAdmin,item.getIsAdmin());
                mContext.startActivity(mIntent);

            }
        });
        rl_replay.setOnClickListener(new View.OnClickListener() {//分享
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, EagleShareActivity.class);
                mIntent.putExtra(SHARE_MODEL, item.getDeviceId());
                mContext.startActivity(mIntent);
            }
        });
    }

    @Override
    protected View newView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        return inflater.inflate(R.layout.fragment_monitor_list_item, null);
    }

    private OnMenuItemClickListener onMenuItemClickListener = new OnMenuItemClickListener() {
        @Override
        public void onMenuItemClick(int position, SwipeMenu menu, int index) {
            Log.i(TAG, "index(" + index + ")");
            MonitorSwipeMenuItem item = (MonitorSwipeMenuItem) menu
                    .getMenuItem(index);
            item.onClick(position);
        }
    };
//    private SwipeMenuCreator leftDeleteItemCreator = new SwipeMenuCreator() {
//        @Override
//        public void create(SwipeMenu menu, int position) {
//            if (menu != null) {
//                if (EagleCameraAdapter.this.getItem(position).getIsAdmin()) {
//                    ShareMenuItem shareMenuItem = new ShareMenuItem(mContext);
//                    menu.addMenuItem(shareMenuItem);
//                }
//                SwipeMenuItem deleteItem = new DeleteMenuItem(mContext);
//                menu.addMenuItem(deleteItem);
//                SwipeMenuItem alterItem = new EditMenuItem(mContext);
//                menu.addMenuItem(alterItem);
//            }
//        }
//    };

    private class DeleteMenuItem extends MonitorSwipeMenuItem {
        public DeleteMenuItem(Context context) {
            super(context);
            super.setBackground(R.drawable.camera_delete);
        }

        @Override
        public void onClick(int columnPosition) {
            final AMSDeviceInfo amsDeviceInfo = EagleCameraAdapter.this.getItem(columnPosition);
            unBindUser(amsDeviceInfo);
            Log.d(TAG, "unBindUser3");
            EagleCameraAdapter.this.removeItem(columnPosition);
            Log.d(TAG, "unBindUser4");
        }
    }

    private void unBindUser(AMSDeviceInfo amsDeviceInfo) {
        if (amsDeviceInfo.getIsAdmin()) {
            deviceIds = new ArrayList<String>();
            deviceIds.add(amsDeviceInfo.getDeviceId());
            DevicesUserManage.queryUserByDevice(amsDeviceInfo.getDeviceId(), onRunUIThread, HandlerConstant.SUCCESS);
            Log.d(TAG, "unBindUser1");
        } else {
            DevicesUserManage.unBindDevice(amsDeviceInfo.getDeviceId());
        }
        Log.d(TAG, "unBindUser2");
        unreTutkMapping(amsDeviceInfo.getDeviceId());
    }

    private void unreTutkMapping(final String deviceId) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                BaiduPushManager.getInstance().registerTutkServer(null, deviceId, BaiduPushManager.unreg_mapping, onRunUIThread);
            }
        }).start();
    }

    // add syf
    private class EditMenuItem extends MonitorSwipeMenuItem {
        public EditMenuItem(Context context) {
            super(context);
            setBackground(R.drawable.camera_compile);
        }

        @Override
        public void onClick(int columnPosition) {
            AMSDeviceInfo amsDeviceInfo = EagleCameraAdapter.this.getItem(columnPosition);
            Intent mIntent = new Intent(mContext, SetEagleCameraActivity.class);
            mIntent.putExtra(Config.eagleSettingEnter, SetEagleCameraActivity.WITHOUT_CAMERA_SETTING);
            mIntent.putExtra(Config.tutkUid, amsDeviceInfo.getDeviceId());
            mIntent.putExtra(Config.tutkPwd, amsDeviceInfo.getPassword());
            mIntent.putExtra(Config.eagleName, amsDeviceInfo.getDeviceName());

            mContext.startActivity(mIntent);
        }
    }

    private class ShareMenuItem extends MonitorSwipeMenuItem {
        public ShareMenuItem(Context context) {
            super(context);
            setBackground(R.drawable.eagle_share);
        }

        @Override
        public void onClick(int columnPosition) {
            AMSDeviceInfo amsDeviceInfo = EagleCameraAdapter.this.getItem(columnPosition);
            Intent mIntent = new Intent(mContext, EagleShareActivity.class);
            mIntent.putExtra(SHARE_MODEL, amsDeviceInfo.getDeviceId());
            mContext.startActivity(mIntent);
        }
    }

    private Callback mCallback = new Callback() {
        @SuppressWarnings("unchecked")
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what) {
                case HandlerConstant.SUCCESS:
                    Log.e(TAG, "queryUserByDevice");
                    @SuppressWarnings("unused")
                    List<BindUser> bindUsers = null;
                    if (msg.obj != null) {
                        unBindAuth((bindUsers = (List<BindUser>) msg.obj));
                    }
                    break;
                case HandlerConstant.BAIDU_UNRE_MAPPING:
                    Config.isResBaiduPush = true;
                    Log.i(TAG, "BAIDU_UNRE_MAPPING");
                    break;
                case HandlerConstant.ERROR:
                    if (msg.obj != null) {
                        Log.e(TAG, msg.obj.toString());
                    }
                    break;
            }
            return false;
        }
    };

    private void unBindAuth(List<BindUser> bindUsers) {
        for (BindUser obj : bindUsers) {
            DevicesUserManage.authUser(getAuthUser(obj), deviceIds, false, null);
        }
    }

    private Handler onRunUIThread = new Handler(Looper.getMainLooper(), mCallback);

    public String getAuthUser(BindUser bindUser) {
        String user = null;
        if (bindUser.getUserName() != null) {
            user = bindUser.getUserName();
        } else if (bindUser.getUserId() != -1) {
            user = String.valueOf(bindUser.getUserId());
        }
        return user;
    }
}
