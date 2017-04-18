package cc.wulian.smarthomev5.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.wulian.icam.model.Device;
import com.wulian.icam.view.widget.CustomToast;
import com.yuantuo.customview.ui.WLDialog;

import java.util.List;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.tools.BindDoorLockManager;

import static android.content.Context.MODE_PRIVATE;
import static android.content.Context.MODE_WORLD_READABLE;

/**
 * @function 网关中所有70门锁adapter
 * Created by hxc on 2016/11/15.
 */

public class BindDoorLockAdapter extends WLBaseAdapter<WulianDevice> {
    private ListView.LayoutParams lp = null;
    private List<WulianDevice> mData;
    private ImageView iv_doorlock_type;
    private TextView tv_doorlock_name;
    private TextView tv_doorlock_area;
    private ImageView iv_doorlock_binded_status;
    private TextView tv_doorlock_unbunding;
    private String cameraId;
    private String devId;
    private SharedPreferences sp;
    private WLDialog dialog;
    private Device icamDevice;
    private String gwId;
    private String loginGwId;
    private BindDoorLockManager bindDoorLockManager  = BindDoorLockManager.getInstance(mContext);

    public BindDoorLockAdapter(Context context, List<WulianDevice> data, Device device,String gwId) {
        super(context, data);
        this.mData = data;
        this.icamDevice = device;
        this.gwId = gwId;
    }

    private View bind() {
        lp = new ListView.LayoutParams(ListView.LayoutParams.MATCH_PARENT,
                ListView.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.item_doorlock_bind, null);
        return view;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (mData != null) {
            View view = bind();
            final WulianDevice device = mData.get(position);
            iv_doorlock_type = (ImageView) view.findViewById(R.id.iv_doorlock_type);
            tv_doorlock_name = (TextView) view.findViewById(R.id.tv_doorlock_nick);
            tv_doorlock_area = (TextView) view.findViewById(R.id.tv_doorlock_area);
            iv_doorlock_binded_status = (ImageView) view.findViewById(R.id.iv_doorlock_status);
            tv_doorlock_unbunding = (TextView) view.findViewById(R.id.tv_doorlock_unbunding);
            SharedPreferences gwIdSp = mContext.getSharedPreferences("doorLockGwId", MODE_PRIVATE);
            loginGwId = gwIdSp.getString("gwId", "-1");
            SharedPreferences bindSp = mContext.getSharedPreferences(loginGwId + icamDevice.getDevice_id().substring(8), MODE_PRIVATE);
            String a = icamDevice.getDevice_id().substring(8);
            Log.i("------2",loginGwId+"--"+icamDevice.getDevice_id().substring(8));
            cameraId = bindSp.getString("cameraId", "-1");
            devId = bindSp.getString("devId","-1");
            if (!StringUtil.isNullOrEmpty(device.getDeviceName())) {
                tv_doorlock_name.setText(device.getDeviceName());
            } else {
                tv_doorlock_name.setText(bindDoorLockManager.getDefaultDevName(device.getDeviceInfo().getDevEPInfo().getEpType()));
//                bindSp.edit().putString("devName", mContext.getResources().getString(R.string.device_type_70)).commit();
            }

            if (mData.get(position).isDeviceOnLine()) {

            } else {
                iv_doorlock_type.setImageResource(R.drawable.door_lock_offline);
            }
//            if (icamDevice.getDevice_id().equals(cameraId)) {//门锁解绑显示
            if(mData.get(position).getDeviceID().equals(devId)){
                iv_doorlock_binded_status.setVisibility(View.VISIBLE);
                tv_doorlock_unbunding.setVisibility(View.VISIBLE);
                tv_doorlock_unbunding.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        removeBindDialog();
                    }
                });
            } else {
                tv_doorlock_unbunding.setVisibility(View.GONE);
            }

            //设置分区信息
            DeviceAreaEntity entity = AreaGroupManager.getInstance().getDeviceAreaEntity(device.getDeviceGwID(), device.getDeviceRoomID());
            String roomName = String.format("[%s]", entity.getName());
            tv_doorlock_area.setText(roomName);
            view.setLayoutParams(lp);
            convertView = view;
        }
        return convertView;
    }

    private void removeBindDialog() {
        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder
                .setTitle(mContext.getResources().getString(R.string.device_mini_geteway_prompt)).setMessage(mContext.getResources().getString(R.string.account_system_unbundling_gateway))
                .setPositiveButton(cc.wulian.smarthomev5.R.string.common_ok)
                .setNegativeButton(cc.wulian.smarthomev5.R.string.cancel)
                .setListener(new WLDialog.MessageListener() {
                    @Override
                    public void onClickPositive(View contentViewLayout) {
                        sp = mContext.getSharedPreferences(loginGwId + icamDevice.getDevice_id().substring(8), MODE_PRIVATE);
                        sp.edit().clear().commit();
                        tv_doorlock_unbunding.setVisibility(View.GONE);
                        iv_doorlock_binded_status.setVisibility(View.GONE);
                        CustomToast.show(mContext,mContext.getResources().getString(R.string.camera_settings_unbundling_success),1000);
                        ((Activity)mContext).finish();
                    }

                    @Override
                    public void onClickNegative(View contentViewLayout) {
                        dialog.dismiss();
                    }

                });
        dialog = builder.create();
        dialog.show();
    }


}
