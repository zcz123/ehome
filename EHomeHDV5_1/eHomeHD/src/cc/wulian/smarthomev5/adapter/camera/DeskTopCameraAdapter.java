package cc.wulian.smarthomev5.adapter.camera;

import java.util.List;

import com.wulian.iot.Config;
import com.wulian.iot.bean.IOTCameraBean;
import com.wulian.iot.view.device.setting.SetCameraActivity;

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
import cc.wulian.smarthomev5.entity.camera.DeskTopCameraEntity;
import cc.wulian.smarthomev5.view.swipemenu.MonitorSwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenu;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuCreator;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuLayout;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuView;

public class DeskTopCameraAdapter extends SwipeMenuAdapter<DeskTopCameraEntity> {
    private TextView name;
    private TextView replay;
    private TextView setting;
    private ImageView type;
    private ImageView iv_replay;
    private ImageView iv_setting;
    private RelativeLayout rl_replay;
    private RelativeLayout rl_setting;

    private LinearLayout.LayoutParams lp = null;
    private DeskTopCameraEntity deskTopEntity = null;

    public DeskTopCameraAdapter(Context context, List<DeskTopCameraEntity> mData) {
        super(context, mData);
//        this.setMenuCreator(this.leftDeleteItemCreator);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        String gwName = null;
        SwipeMenuLayout layout = null;
        if (mData != null) {
            deskTopEntity = mData.get(position);
            View view = bind();
            name = (TextView) view.findViewById(R.id.monitor_name);
            replay = (TextView) view.findViewById(R.id.tv_cameralist_replay);
            setting = (TextView) view.findViewById(R.id.tv_cameralist_setting);
            type = (ImageView) view.findViewById(R.id.monitor_type);
            iv_replay = (ImageView) view.findViewById(R.id.iv_cameralist_replay);
            iv_setting = (ImageView) view.findViewById(R.id.iv_cameralist_setting);
            rl_replay = (RelativeLayout) view.findViewById(R.id.rl_replay);
            rl_setting = (RelativeLayout) view.findViewById(R.id.rl_setting);

            if (deskTopEntity.getGwName() == null || deskTopEntity.getGwName().equals("")) {
                gwName = mContext.getString(R.string.setting_detail_device_06);
            } else {
                gwName = deskTopEntity.getGwName();
            }
            replay.setVisibility(View.INVISIBLE);
            iv_replay.setVisibility(View.INVISIBLE);
            //桌面摄像机显示 在线
            String nameString = gwName + "-[" + mContext.getResources().getString(R.string.main_online) + "]";
            name.setText(nameString);
            type.setImageResource(R.drawable.monitor_desk_online);
            rl_setting.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent mIntent = new Intent(DeskTopCameraAdapter.this.mContext, SetCameraActivity.class);
                    mIntent.putExtra(Config.deskBean, DeskTopCameraAdapter.this.clone(deskTopEntity));
                    DeskTopCameraAdapter.this.mContext.startActivity(mIntent);
                    DeskTopCameraAdapter.this.notifyDataSetChanged();
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
        MonitorSwipeMenuItem item = (MonitorSwipeMenuItem) menu.getMenuItem(index);
        item.onClick(view.getPosition());
    }

    /**
     * 创建左划删除item样式
     */
//    private SwipeMenuCreator leftDeleteItemCreator = new SwipeMenuCreator() {
//        @Override
//        public void create(SwipeMenu menu, int position) {
//            if (menu != null) {
//                SwipeMenuItem deleteItem = new DeleteMenuItem(DeskTopCameraAdapter.this.mContext);
//                menu.addMenuItem(deleteItem);
//                SwipeMenuItem alterItem = new EditMenuItem(DeskTopCameraAdapter.this.mContext);
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
            DeskTopCameraAdapter.this.notifyDataSetChanged();
        }
    }

    private class EditMenuItem extends MonitorSwipeMenuItem {
        public EditMenuItem(Context context) {
            super(context);
            setBackground(R.drawable.camera_compile);
        }

        @Override
        public void onClick(int columnPosition) {
            Intent mIntent = new Intent(DeskTopCameraAdapter.this.mContext, SetCameraActivity.class);
            mIntent.putExtra(Config.deskBean, DeskTopCameraAdapter.this.clone(deskTopEntity));
            DeskTopCameraAdapter.this.mContext.startActivity(mIntent);
            DeskTopCameraAdapter.this.notifyDataSetChanged();
        }
    }

    /**
     * 实体转化
     */
    private IOTCameraBean clone(DeskTopCameraEntity obj) {
        IOTCameraBean info = new IOTCameraBean();
        info.setUid(obj.getTutkUID());
        info.setPassword(obj.getTutkPASSWD());
        info.setGwId(obj.getGwID());
        info.setCamName(obj.getGwName());
        return info;
    }
}
