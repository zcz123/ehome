package cc.wulian.smarthomev5.adapter.camera;

import java.util.List;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.Builder;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

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

import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.Cloud2DeviceSettingActivity;
import cc.wulian.smarthomev5.dao.CameraDao;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.view.swipemenu.MonitorSwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenu;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuCreator;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuLayout;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuView;

public class OtherCameraAdpater extends SwipeMenuAdapter<CameraInfo> {
    private TextView name;
    private TextView replay;
    private TextView setting;
    private ImageView type;
    private ImageView iv_replay;
    private ImageView iv_setting;
    private RelativeLayout rl_replay;
    private RelativeLayout rl_setting;
    private LinearLayout.LayoutParams lp = null;
    private CameraInfo cameraInfo = null;

    public OtherCameraAdpater(Context context, List<CameraInfo> mData) {
        super(context, mData);
//		this.setMenuCreator(this.leftDeleteItemCreator);
    }

    // TODO 添加数据
    public View getView(int position, View convertView, ViewGroup parent) {
        String monitorIsString = null;
        SwipeMenuLayout layout = null;
        if (mData != null) {
            cameraInfo = mData.get(position);
            View view = bind();
            name = (TextView) view.findViewById(R.id.monitor_name);
            replay = (TextView) view.findViewById(R.id.tv_cameralist_replay);
            setting = (TextView) view.findViewById(R.id.tv_cameralist_setting);
            type = (ImageView) view.findViewById(R.id.monitor_type);
            iv_replay = (ImageView) view.findViewById(R.id.iv_cameralist_replay);
            iv_setting = (ImageView) view.findViewById(R.id.iv_cameralist_setting);
            rl_replay = (RelativeLayout) view.findViewById(R.id.rl_replay);
            rl_setting = (RelativeLayout) view.findViewById(R.id.rl_setting);
            name.setText(cameraInfo.getUsername());
            if (cameraInfo.camType == 12) {
                type.setImageResource(R.drawable.monitor_cloud2_online);
            } else if (cameraInfo.camType == 13) {
                type.setImageResource(R.drawable.monitor_cloud3_online);
            }
            if (cameraInfo.camType == 4 || cameraInfo.camType == 8) {
                type.setImageResource(R.drawable.monitor_hard_disk_online);
            }
            rl_replay.setVisibility(View.INVISIBLE);
            rl_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent it = new Intent();
                    it.putExtra("camerainfo", cameraInfo);
                    it.setClass(mContext, Cloud2DeviceSettingActivity.class);
                    mContext.startActivity(it);
                }
            });
            view.setLayoutParams(lp);
            convertView = view;
            if (convertView != null) {
                layout = createMenuView(position, parent, convertView);
            }
        }
        return layout;
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
        MonitorSwipeMenuItem item = (MonitorSwipeMenuItem) menu.getMenuItem(index);
        item.onClick(view.getPosition());
    }

    /**
     * 创建左划删除item样式
     */
//	private SwipeMenuCreator leftDeleteItemCreator = new SwipeMenuCreator() {
//		@Override
//		public void create(SwipeMenu menu,int position) {
//			if (menu != null) {
//				SwipeMenuItem deleteItem = new DeleteMenuItem(OtherCameraAdpater.this.mContext);
//				menu.addMenuItem(deleteItem);
//
////				SwipeMenuItem alterItem = new EditMenuItem(WLCameraAdapter.this.mContext);
////				menu.addMenuItem(alterItem);
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
            showAddWlCameraDialog(columnPosition);
        }

        private View createCustomView() {
            View tocDialog;
            TextView monitorTextView;
            tocDialog = View.inflate(mContext, R.layout.sigin_fragment_remind_dialog_layout, null);
            monitorTextView = (TextView) tocDialog.findViewById(R.id.monitor_textview_for_alarmmessage);
            monitorTextView.setText(mContext.getString(R.string.home_monitor_delete_camera_sure));
            return tocDialog;
        }

        private void showAddWlCameraDialog(final int columnPosition) {
            final WLDialog dialog;
            WLDialog.Builder builder = new Builder(mContext);
            builder.setContentView(createCustomView());
            builder.setPositiveButton(android.R.string.ok);
            builder.setNegativeButton(android.R.string.cancel);
            builder.setListener(new MessageListener() {

                @Override
                public void onClickPositive(View contentViewLayout) {
                    new CameraDao().delete(cameraInfo);//删除摄像机
                    OtherCameraAdpater.this.removeItem(columnPosition);
                }

                @Override
                public void onClickNegative(View contentViewLayout) {
                }

            });

            dialog = builder.create();
            dialog.show();
        }
    }
}
