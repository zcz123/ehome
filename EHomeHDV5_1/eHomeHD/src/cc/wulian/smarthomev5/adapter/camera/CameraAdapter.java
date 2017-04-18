package cc.wulian.smarthomev5.adapter.camera;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wulian.icam.model.Device;
import com.wulian.icam.utils.DeviceType;
import com.wulian.icam.view.device.play.ReplayVideoActivity;
import com.wulian.icam.view.device.setting.DeviceSettingActivity;
import com.wulian.icam.view.device.setting.NewEagleSettingActivity;
import com.wulian.iot.Config;
import com.wulian.iot.bean.IOTCameraBean;
import com.wulian.iot.view.device.setting.SetCameraActivity;
import com.wulian.iot.view.device.setting.SetEagleCameraActivity;

import java.util.ArrayList;
import java.util.List;

import cc.wulian.ihome.wan.sdk.user.entity.AMSDeviceInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.iotc.share.EagleShareActivity;
import cc.wulian.smarthomev5.activity.monitor.OtherCameraSettingActivity;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.entity.camera.DeskTopCameraEntity;
import cc.wulian.smarthomev5.entity.camera.MonitorWLCloudEntity;
import cc.wulian.smarthomev5.view.swipemenu.MonitorSwipeMenuItem;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenu;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuAdapter;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuLayout;
import cc.wulian.smarthomev5.view.swipemenu.SwipeMenuView;


/**
 * Created by mabo on 2017/1/4 0004.
 */

public class CameraAdapter<T> extends SwipeMenuAdapter {
    private TextView name;
    private TextView replay;
    private TextView setting;
    private ImageView type;
    private ImageView iv_replay;
    private ImageView iv_setting;
    private RelativeLayout rl_replay;
    private RelativeLayout rl_setting;

    private LinearLayout.LayoutParams lp = null;
    public static String SHARE_MODEL = "SHARE_MODEL";

    private DeskTopCameraEntity deskTopEntity = null;
    private MonitorWLCloudEntity wlIcamera = null;
    private AMSDeviceInfo amsDeviceInfo=null;
    private CameraInfo info=null;


    private List<T> mdata=new ArrayList();
    private List<MonitorWLCloudEntity> wlCloudEntityList=new ArrayList<>();
    private List<DeskTopCameraEntity>  deskTopCameraEntityList=new ArrayList<>();
    private List<CameraInfo> cameraInfoList =new ArrayList<>();
    private List<AMSDeviceInfo> amsDeviceInfoList =new ArrayList<>();
    /*list应该统一传空*/
    public CameraAdapter(Context context, List mData) {
        super(context, mData);
    }

    @Override
    public int getItemViewType(int position) {
        int result =0;
        if (mdata.get(position) instanceof DeskTopCameraEntity){
            result= 0;
        }
        if (mdata.get(position) instanceof  MonitorWLCloudEntity){
            result= 1;
        }
        if (mdata.get(position) instanceof CameraInfo){
            result= 2;
        }
        if (mdata.get(position) instanceof AMSDeviceInfo){
            result= 3;
        }
        return result;
    }

    @Override
    public int getViewTypeCount() {
        return 4;
    }

    public View getView(int position, View convertView, ViewGroup parent){
        SwipeMenuLayout layout = null;
        if (mData != null) {
            View view = bind();
            name = (TextView) view.findViewById(R.id.monitor_name);
            replay = (TextView) view.findViewById(R.id.tv_cameralist_replay);
            setting = (TextView) view.findViewById(R.id.tv_cameralist_setting);
            type = (ImageView) view.findViewById(R.id.monitor_type);
            iv_replay = (ImageView) view.findViewById(R.id.iv_cameralist_replay);
            iv_setting = (ImageView) view.findViewById(R.id.iv_cameralist_setting);
            rl_replay = (RelativeLayout) view.findViewById(R.id.rl_replay);
            rl_setting = (RelativeLayout) view.findViewById(R.id.rl_setting);
            int ty=getItemViewType(position);
            if (ty==0){
                deskTopEntity = (DeskTopCameraEntity) mdata.get(position);
                setDeskCamera();//桌面摄像机
            }
            if (ty==1){
                wlIcamera= (MonitorWLCloudEntity) mdata.get(position);
                setWlCloudCamera();//爱看
            }
            if (ty==2){  //其他类型摄像机
                info= (CameraInfo) mdata.get(position);
                setOtherCamera();

            }
            if(ty==3){  // 猫眼摄像机
                amsDeviceInfo= (AMSDeviceInfo) mdata.get(position);
                setEageleCamera();
            }
            view.setLayoutParams(lp);
            convertView = view;
            if (convertView != null) {
                layout = createMenuView(position, parent, convertView);
            }
        }
        return layout;
    }
    public T getItem(int position){
        return mdata.get(position);
    }

    private View bind() {
        lp = new LinearLayout.LayoutParams(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT);
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.fragment_monitor_list_item, null);
        return view;
    }

    @Override
    public void onItemClick(SwipeMenuView view, SwipeMenu menu, int index) {
        MonitorSwipeMenuItem item = (MonitorSwipeMenuItem) menu.getMenuItem(index);
        item.onClick(view.getPosition());
    }

    public void setDate(List<T> data,String type){
        if (data==null){
            return;
        }
        data=upMonitorWLCloud(data,type);//根据不同类型的摄像机 去重更新
        mdata.addAll(data);
        swapData(mdata);
    }
    /*处理不同类型的数据进行更新去重*/
    private List<T> upMonitorWLCloud(List<T> data,String type){
        List<MonitorWLCloudEntity> removeWLCloud =new ArrayList<>();
        List<MonitorWLCloudEntity> removeOld=new ArrayList<>();
        if (Config.WLCLOUD_CAMERA.equals(type)) {
            for (T t : data) {
                MonitorWLCloudEntity mwc = (MonitorWLCloudEntity) t;
                for (MonitorWLCloudEntity info:wlCloudEntityList){
                    if (mwc.deviceId.equals(info.deviceId)){
                        removeOld.add(info);
                    }
                }
                removeWLCloud.add(mwc);
            }
            wlCloudEntityList.removeAll(removeOld);
            mdata.removeAll(removeOld);
            wlCloudEntityList.addAll(removeWLCloud);
            return (List<T>) wlCloudEntityList;
        }
        if (Config.DESK_CAMERA.equals(type)){
            deskTopCameraEntityList.clear();
            List<DeskTopCameraEntity> removeDesk =new ArrayList<>();
            for (T t : data) {
                DeskTopCameraEntity dtc = (DeskTopCameraEntity) t;
                removeDesk.add(dtc);
            }
            deskTopCameraEntityList.addAll(removeDesk);
            return (List<T>) deskTopCameraEntityList;
        }
        if (Config.OTHER_CAMERA.equals(type)){
            cameraInfoList.clear();
            List<CameraInfo> removeInfo=new ArrayList<>();
            for (T t : data) {   //data 是最新的数据
                CameraInfo cameraInfo = (CameraInfo) t;
                removeInfo.add(cameraInfo);
            }
            cameraInfoList.addAll(removeInfo);
            return (List<T>) cameraInfoList;
        }
        if (Config.EAGLE_CAMERA.equals(type)){
            amsDeviceInfoList.clear();
            List<AMSDeviceInfo> removeAms=new ArrayList<>();
            for (T t : data) {
                AMSDeviceInfo amsDeviceInfo = (AMSDeviceInfo) t;
                removeAms.add(amsDeviceInfo);
            }
            amsDeviceInfoList.addAll(removeAms);
            return (List<T>) amsDeviceInfoList;
        }
        return new ArrayList<>();
    }
    public void clearData(){
        if (mdata!=null){
            mdata.clear();
        }
        if(amsDeviceInfoList!=null){
            amsDeviceInfoList.clear();
        }
        if (cameraInfoList!=null){
            cameraInfoList.clear();
        }
        if (deskTopCameraEntityList!=null){
            deskTopCameraEntityList.clear();
        }
        if (wlCloudEntityList!=null){
            wlCloudEntityList.clear();
        }
    }
    /*************************************************************桌面摄像机区域********************************************************/
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
    private void setDeskCamera(){
        final DeskTopCameraEntity deskTop=deskTopEntity;
        String gwName = null;
        if (deskTop==null){
            return;
        }
        if (deskTop.getGwName() == null || deskTop.getGwName().equals("")) {
            gwName = mContext.getString(R.string.setting_detail_device_06);
        } else {
            gwName = deskTop.getGwName();
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
                Intent mIntent = new Intent(CameraAdapter.this.mContext, SetCameraActivity.class);
                mIntent.putExtra(Config.deskBean, CameraAdapter.this.clone(deskTop));
                CameraAdapter.this.mContext.startActivity(mIntent);
                CameraAdapter.this.notifyDataSetChanged();
            }
        });
    }
    /*******************************************************爱看摄像机区域 :随便看，企鹅，新猫眼**************************************************************************/
    private void setWlCloudCamera(){
        final Device device = wlIcamera.getDevice();
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

    /*********************************************************其他摄像机区域**********************************************************************************************/
   private void setOtherCamera(){
       final CameraInfo tmpInfo=info;
       name.setText(info.getUsername());
       if (info.camType == 12) {
           type.setImageResource(R.drawable.monitor_cloud2_online);
       } else if (info.camType == 13) {
           type.setImageResource(R.drawable.monitor_cloud3_online);
       }
       if (info.camType == 4 || info.camType == 8) {
           type.setImageResource(R.drawable.monitor_hard_disk_online);
       }
       rl_replay.setVisibility(View.INVISIBLE);
       rl_setting.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view) {
               Intent it = new Intent();
               tmpInfo.setIsForSetting(true);
               it.putExtra("camerainfo", tmpInfo);
               it.setClass(mContext, OtherCameraSettingActivity.class);
               mContext.startActivity(it);
           }
       });
   }


    /*********************************************************猫眼摄像机区域**********************************************************************************************/
    private void setEageleCamera(){
        final AMSDeviceInfo amsifo=amsDeviceInfo;
        if (!amsifo.getIsAdmin()){
            replay.setVisibility(View.INVISIBLE);
            iv_replay.setVisibility(View.INVISIBLE);
        }
        if(amsifo.getDeviceName().isEmpty()){
            name.setText(mContext.getResources().getString(R.string.monitor_video_eagle));//WuLian猫眼
        }else {
            name.setText(amsifo.getDeviceName());
        }
        type.setBackground(mContext.getResources().getDrawable(R.drawable.monitor_cat_eye_online));
        replay.setText(mContext.getResources().getString(R.string.home_monitor_share));//分享
        iv_replay.setImageResource(R.drawable.cameralist_cateye_share);
        rl_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {//设置

                Intent mIntent = new Intent(mContext, SetEagleCameraActivity.class);
                mIntent.putExtra(Config.eagleSettingEnter, SetEagleCameraActivity.WITHOUT_CAMERA_SETTING);
                mIntent.putExtra(Config.tutkUid, amsifo.getDeviceId());
                mIntent.putExtra(Config.tutkPwd, amsifo.getPassword());
                mIntent.putExtra(Config.eagleName, amsifo.getDeviceName());
                mIntent.putExtra(Config.isAdmin,amsifo.getIsAdmin());
                mContext.startActivity(mIntent);

            }
        });
        rl_replay.setOnClickListener(new View.OnClickListener() {//分享
            @Override
            public void onClick(View view) {
                Intent mIntent = new Intent(mContext, EagleShareActivity.class);
                mIntent.putExtra(SHARE_MODEL, amsDeviceInfo.getDeviceId());
                mContext.startActivity(mIntent);
            }
        });
    }

}
