package cc.wulian.smarthomev5.adapter.camera;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.icam.model.Device;
import com.wulian.icam.utils.DeviceType;
import com.wulian.icam.view.device.play.ReplayVideoActivity;
import com.wulian.icam.view.device.setting.DeviceSettingActivity;
import com.wulian.icam.view.device.setting.NewEagleSettingActivity;
import com.yuantuo.customview.ui.WLDialog;

import java.util.List;

import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.camera.MonitorWLCloudEntity;
import cc.wulian.smarthomev5.view.swipemenu.MonitorSwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenu;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuLayout;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuView;

/**
 * 整合 所有摄像机
 *
 * @author syf
 */
public class WLCameraAdapter extends SwipeMenuAdapter<MonitorWLCloudEntity> {
    private TextView name;
    private TextView replay;
    private TextView setting;
    private ImageView type;
    private ImageView iv_replay;
    private ImageView iv_setting;
    private RelativeLayout rl_replay;
    private RelativeLayout rl_setting;



    LinearLayout.LayoutParams lp = null;

    public WLCameraAdapter(Context context, List<MonitorWLCloudEntity> mData) {
        super(context, mData);
//		this.setMenuCreator(this.leftDeleteItemCreator);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        MonitorWLCloudEntity wlIcamera = null;
        SwipeMenuLayout layout = null;
        if (mData != null) {
            wlIcamera = mData.get(position);
            final Device device = wlIcamera.getDevice();
            View view = bind();
            name = (TextView) view.findViewById(R.id.monitor_name);
            replay = (TextView) view.findViewById(R.id.tv_cameralist_replay);
            setting = (TextView) view.findViewById(R.id.tv_cameralist_setting);
            type = (ImageView) view.findViewById(R.id.monitor_type);
            iv_replay = (ImageView) view.findViewById(R.id.iv_cameralist_replay);
            iv_setting = (ImageView) view.findViewById(R.id.iv_cameralist_setting);
            rl_replay = (RelativeLayout) view.findViewById(R.id.rl_replay);
            rl_setting = (RelativeLayout) view.findViewById(R.id.rl_setting);

            rl_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent();
                    it.putExtra("device", device);
                    if(device.getDevice_id().startsWith("cmic08")){
                        it.setClass(mContext, NewEagleSettingActivity.class);
                    }else{
                        it.setClass(mContext, DeviceSettingActivity.class);
                    }
                    mContext.startActivity(it);
                }
            });
            if (wlIcamera.getMonitorIsOnline().equals("1")) {
                rl_replay.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent it = new Intent();
                        it.putExtra("device", device);
                        it.setClass(mContext, ReplayVideoActivity.class);
                        mContext.startActivity(it);
                    }
                });
            }
            String typename = mContext.getResources().getString(
                    R.string.monitor_cloud_video_camera_wlcl);
            DeviceType typeEnum = DeviceType
                    .getDevivceTypeByDeviceID(wlIcamera.deviceId);
            switch (typeEnum) {
                case INDOOR:
                case INDOOR2:
                    typename = mContext.getResources().getString(
                            R.string.monitor_cloud_video_camera_wlpg);
                    isPenguinCamera(wlIcamera);
                    break;
                case OUTDOOR:
                    // TODO::户外摄像机
                    break;
                case SIMPLE:
                    isIcamCamera(wlIcamera);
                case SIMPLE_N:
                    isIcamCamera(wlIcamera);
                    break;
                case NewEagle:
                    typename ="新猫眼";
                    isNewEagleCamera(wlIcamera);
                    break;
                default:
                    break;
            }
            setCameraName(wlIcamera, typename);
            view.setLayoutParams(lp);
            convertView = view;
            if (convertView != null) {
                layout = createMenuView(position, parent, convertView);
            }
        }
        return layout;
    }

    private void setCameraName(MonitorWLCloudEntity wlIcamera, String typename) {
        String tempName = "";
        if (!StringUtil.isNullOrEmpty(wlIcamera.getMonitorDeviceNick())) {
            tempName = wlIcamera.getMonitorDeviceNick();
        } else {
            tempName = typename.substring(0, 3)
                    + wlIcamera
                    .getDevice()
                    .getDevice_id()
                    .substring(
                            wlIcamera.getDevice().getDevice_id()
                                    .length() - 4,
                            wlIcamera.getDevice().getDevice_id()
                                    .length());
        }
        name.setText(tempName);
        wlIcamera.setMonitorDeviceNick(tempName);
    }

    //企鹅摄像机视图初始化
    private void isPenguinCamera(MonitorWLCloudEntity wlIcamera) {
        if (wlIcamera.getMonitorIsOnline().equals("1")) {
            type.setImageResource(R.drawable.monitor_pleguin_online);
            iv_replay.setImageResource(R.drawable.cameralist_replay_online);
            iv_setting.setImageResource(R.drawable.cameralist_set_online);
        } else {
            iv_replay.setImageResource(R.drawable.cameralist_replay_offline);
            type.setImageResource(R.drawable.monitor_pleguin_offline);
            iv_setting.setImageResource(R.drawable.cameralist_set_offline);
        }
    }

    //随便看摄像机视图初始化
    private void isIcamCamera(MonitorWLCloudEntity wlIcamera) {
        if (wlIcamera.getMonitorIsOnline().equals("1")) {
            type.setImageResource(R.drawable.monitor_icam_pic_online);
            iv_replay.setImageResource(R.drawable.cameralist_replay_online);
            iv_setting.setImageResource(R.drawable.cameralist_set_online);

        } else {
            type.setImageResource(R.drawable.monitor_icam_pic_offline);
            iv_setting.setImageResource(R.drawable.cameralist_set_offline);
            iv_replay.setImageResource(R.drawable.cameralist_replay_offline);
        }
    }
    private void isNewEagleCamera(MonitorWLCloudEntity wlIcamera) {
        if (wlIcamera.getMonitorIsOnline().equals("1")) {
            type.setImageResource(R.drawable.monitor_cat_eye_online);
            iv_replay.setImageResource(R.drawable.cameralist_replay_online);
            iv_setting.setImageResource(R.drawable.cameralist_set_online);

        } else {
            type.setImageResource(R.drawable.monitor_cat_eye_offline);
            iv_setting.setImageResource(R.drawable.cameralist_set_offline);
            iv_replay.setImageResource(R.drawable.cameralist_replay_offline);
        }
    }

    private View bind() {
        lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.fragment_monitor_list_item, null);
        return view;
    }

    @Override
    public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
        view.closeMenu();
        MonitorSwipeMenuItem item = (MonitorSwipeMenuItem) menu
                .getMenuItem(index);
        item.onClick(view.getPosition());
    }

    /**
     * 创建左划删除item样式
     */
//	private SwipeMenuCreator leftDeleteItemCreator = new SwipeMenuCreator() {
//		@Override
//		public void create(SwipeMenu menu,int position) {
//			if (menu != null) {
//				SwipeMenuItem deleteItem = new DeleteMenuItem(
//						WLCameraAdapter.this.mContext);
//				menu.addMenuItem(deleteItem);
//
//				SwipeMenuItem alterItem = new EditMenuItem(
//						WLCameraAdapter.this.mContext);
//				menu.addMenuItem(alterItem);
//			}
//		}
//	};

    private class DeleteMenuItem extends MonitorSwipeMenuItem {

        public DeleteMenuItem(Context context) {
            super(context);
            super.setBackground(R.drawable.camera_delete);
        }

        @Override
        public void onClick(int columnPosition) {
//            showAddWlCameraDialog(columnPosition);

        }

        private View createCustomView() {
            View tocDialog;
            TextView monitorTextView;
            tocDialog = View.inflate(mContext,
                    R.layout.sigin_fragment_remind_dialog_layout, null);
            monitorTextView = (TextView) tocDialog
                    .findViewById(R.id.monitor_textview_for_alarmmessage);
            monitorTextView.setText(mContext
                    .getString(R.string.home_monitor_delete_camera_sure));
            return tocDialog;
        }

        private void showAddWlCameraDialog(final int columnPosition) {
            final WLDialog dialog;
            WLDialog.Builder builder = new WLDialog.Builder(mContext);
            builder.setContentView(createCustomView());
            builder.setPositiveButton(android.R.string.ok);
            builder.setNegativeButton(android.R.string.cancel);
            builder.setListener(new WLDialog.MessageListener() {

                @Override
                public void onClickPositive(View contentViewLayout) {
                    final MonitorWLCloudEntity wlIcamera = (MonitorWLCloudEntity) mData
                            .get(columnPosition);
                    WLCameraAdapter.this.removeItem(columnPosition);
//                    WLCameraOperationManager.getInstance(
//                            WLCameraAdapter.this.mContext)
//                            .delateDeviceFormList(wlIcamera);
                }

                @Override
                public void onClickNegative(View contentViewLayout) {

                }

            });

            dialog = builder.create();
            dialog.show();
        }

    }

    private class EditMenuItem extends MonitorSwipeMenuItem {

        public EditMenuItem(Context context) {
            super(context);
            setBackground(R.drawable.camera_compile);
        }

        @Override
        public void onClick(int columnPosition) {
            MonitorWLCloudEntity wlIcamera = (MonitorWLCloudEntity) mData
                    .get(columnPosition);
            WLCameraAdapter.this.mContext.startActivity(new Intent(
                    WLCameraAdapter.this.mContext, DeviceSettingActivity.class)
                    .putExtra("device", wlIcamera.getDevice()));
            deleteItem(columnPosition);
        }
    }

    public void deleteItem(int columnPosition) {
    }

}
