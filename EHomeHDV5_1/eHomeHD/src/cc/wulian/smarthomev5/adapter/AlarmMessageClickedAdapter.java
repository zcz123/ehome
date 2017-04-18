package cc.wulian.smarthomev5.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.dao.CameraDao;
import cc.wulian.smarthomev5.databases.entitys.Area;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.tools.MotionDetectionManger;
import cc.wulian.smarthomev5.utils.DateUtil;
import cc.wulian.smarthomev5.utils.LanguageUtil;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

public class AlarmMessageClickedAdapter extends WLBaseAdapter<MessageEventEntity> {

    private View tocDialog;
    private WLDialog dialog;
    //	private ImageView monitorimage;
    private List<CameraInfo> data;
    private CameraDao cameraDao = CameraDao.getInstance();
    private CameraInfo mCurrentInfo;

    private AreaGroupManager areaGroupManager = AreaGroupManager.getInstance();
    private final DeviceCache mCache;
    private boolean mIsEditingMode = false;
    private Map<Integer, State> stateMap = new HashMap<Integer, State>();
    MotionDetectionManger motionDetectionManger = MotionDetectionManger.getInstance(mContext);

    public AlarmMessageClickedAdapter(Context context, List<MessageEventEntity> data) {
        super(context, data);
        mCache = DeviceCache.getInstance(context);
    }

    @Override
    protected View newView(Context context, LayoutInflater inflater,
                           ViewGroup parent, int pos) {
        return inflater.inflate(R.layout.fragment_message_alarm_clicked_item, null);
    }


    @Override
    protected void bindView(Context context, View view, int pos,
                            final MessageEventEntity item) {
        if (StringUtil.isNullOrEmpty(item.epData) && item.getEpMsg() == null) {
            return;
        }
        final TextView mTextView = (TextView) view.findViewById(R.id.detail_message);
        final TextView mTimeView = (TextView) view.findViewById(R.id.message_time);
//		TextView statusTextView = (TextView)view.findViewById(R.id.alarm_status);
        final ImageView delateimage = (ImageView) view.findViewById(R.id.delate_image);
//		monitorimage = (ImageView) view.findViewById(R.id.monitor_image);
        String mtime = DateUtil.getHourAndMinu(mContext, Long.parseLong(item.getTime()));
        mTimeView.setText(mtime);
        WulianDevice device = mCache.getDeviceByID(mContext, item.gwID, item.devID);
        String formatString = "";
        StringBuilder sb = new StringBuilder();
        if (item.getEpMsg() != null && item.getEpMsg().length() > 1) {
            String parseData = "";
            if (device != null) {
                String deviceName = device.getDeviceName();
                if (deviceName == null || deviceName.equals("")) {
                    deviceName = device.getDefaultDeviceName();
                }
                String area = "";
                if (!device.getDeviceRoomID().equals(Area.AREA_DEFAULT)) {
                    DeviceAreaEntity areaEntity = AreaGroupManager.getInstance()
                            .getDeviceAreaEntity(device.getDeviceGwID(),
                                    device.getDeviceRoomID());
                    area = areaEntity.getName();
                }
                parseData = area + deviceName + mContext.getResources().getString(
                        R.string.house_rule_detect) + item.getEpMsg().substring(1);
            } else {
                parseData = item.getEpMsg().substring(1);
            }
            sb.append(parseData);
        } else {
            sb.append(DeviceTool.getDeviceAlarmAreaName(device));
            String deviceName = "";
            if (device != null) {
                WulianDevice childDevcie = mCache.getDeviceByIDEp(mContext, item.gwID, item.devID, item.ep);
                if (childDevcie != null) {
                    String epName = childDevcie.getDeviceInfo().getDevEPInfo().getEpName();
                    if (!StringUtil.isNullOrEmpty(epName)) {
                        deviceName = epName;
                        if (StringUtil.equals(epName, "-1")) {
                            deviceName = device.getDefaultDeviceName();
                        }
                    } else {
                        deviceName = DeviceTool.getDeviceShowName(device);
                    }
                } else {
                    deviceName = DeviceTool.getDeviceShowName(device);
                }
            } else {
                if (!StringUtil.isNullOrEmpty(item.epType) && item.epType.equals("DC")) {
                    deviceName = motionDetectionManger.getDeviceByEpdata(item.getEpData());
                } else {
                    deviceName = DeviceTool.getDeviceNameByIdAndType(mContext, item.devID, item.epType);
                }
            }
            sb.append(deviceName);
            if (LanguageUtil.isChina() || LanguageUtil.isTaiWan()) {
                sb.append(mContext.getString(R.string.home_device_alarm_default_voice_detect));
            } else {
                sb.append(" " + mContext.getString(R.string.home_device_alarm_default_voice_detect) + " ");
            }

            String parseData = context.getString(R.string.home_device_alarm_default_voice_notification);
            if (device != null) {
                CharSequence csEpdata=device.parseDataWithProtocol(item.epData);
                if(csEpdata!=null){
                    parseData =csEpdata.toString();
                }
                if(!StringUtil.isNullOrEmpty(item.getExtData())){
                    parseData=device.parseDataWithExtData(item.getExtData()).toString();
                }
            }
            sb.append(parseData);
        }
        formatString = sb.toString();
        mTextView.setText(formatString);

        if (mIsEditingMode) {
//			monitorimage.setVisibility(View.GONE);
            delateimage.setVisibility(View.VISIBLE);
            State state = getState(pos);
            delateimage.setSelected(state.isDeleted());
        } else {
//			monitorimage.setVisibility(View.VISIBLE);
            delateimage.setVisibility(View.GONE);
        }
        /**
         * 单击开启对应区域视频！
         */
//		monitorimage.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				
//				String devId = item.devID;
//				String gwId = item.gwID;
//				
//				WulianDevice device = mCache.getDeviceByID(mContext, gwId, devId);
//				String areaId = device.getDeviceRoomID();
//				
////				WLToast.showToast(mContext, "areaId"+":"+areaId, WLToast.TOAST_LONG);
//				
//				showSlectedAreaCameraDialog(gwId,areaId);
//				
//			}
//		});
    }

    /**
     * 选择开启摄像机的dialog
     *
     * @param gwId
     * @param areaId
     */
    private void showSlectedAreaCameraDialog(String gwId, String areaId) {

        data = getCameraInfos(gwId, areaId);

        WLDialog.Builder builder = new WLDialog.Builder(mContext);
        builder.setTitle(mContext.getResources().getString(R.string.device_alarm_monitor));
//		builder.setContentView(createCustomView());
        builder.setNegativeButton(android.R.string.cancel);
        builder.setListener(new MessageListener() {
            @Override
            public void onClickPositive(View contentViewLayout) {
            }

            @Override
            public void onClickNegative(View contentViewLayout) {
                dialog.dismiss();
            }
        });

        dialog = builder.create();
        dialog.show();
    }

    /**
     * dialog中布局与对应摄像机的选择
     *
     * @return tocDialog
     */
//	private View createCustomView() {
//		
//	    ListView monitorListView;
//	    TextView monitorTextView;
//	    int mMonitorInfoAdapterCount;
//	    MonitorInfoAdapterForAlarmMessage mMonitorInfoAdapter;
//	    
//		tocDialog = View.inflate(mContext,R.layout.monitor_content_for_alarmmessage, null);
//		monitorListView = (ListView) tocDialog.findViewById(R.id.monitor_listview_for_alarmmessage);
//		monitorTextView = (TextView) tocDialog.findViewById(R.id.monitor_textview_for_alarmmessage);
//	
//		mMonitorInfoAdapter = new MonitorInfoAdapterForAlarmMessage(mContext,
//				data, mCurrentInfo);
//		mMonitorInfoAdapterCount =mMonitorInfoAdapter.getCount();
//		/**
//		 * 判断列表数据数目；
//		 * 若为0 ，则隐藏列表显示 “该区域无摄像机”
//		 * 若不为0 ，则显示列表
//		 */
//		if(mMonitorInfoAdapterCount ==0){
//			monitorListView.setVisibility(View.GONE);
//			monitorTextView.setVisibility(View.VISIBLE);
//			monitorTextView.setText(mContext.getResources().getString(R.string.device_alarm_monitor_no_monitor));
//		}
//		else{
//			monitorListView.setVisibility(View.VISIBLE);
//			monitorTextView.setVisibility(View.GONE);
//			monitorListView.setAdapter(mMonitorInfoAdapter);
//		}
//		
//		
//		return tocDialog;
//	}
    private List<CameraInfo> getCameraInfos(String gwId, String areaId) {
        DeviceAreaEntity selectedAreaEntity;
        String selectedAreaId;
        CameraInfo camerainfo = new CameraInfo();
        camerainfo.setGwId(gwId);

        selectedAreaEntity = AreaGroupManager.getInstance().getDeviceAreaEntity(gwId, areaId);
        if (selectedAreaEntity == null) {
            selectedAreaId = Area.AREA_DEFAULT;
        } else {
            selectedAreaId = selectedAreaEntity.getRoomID();
        }
        camerainfo.setAreaID(selectedAreaId);
        return cameraDao.findListAll(camerainfo);
    }

    /**
     * 选择删除
     *
     * @param pos
     * @return
     */
    public State getState(int pos) {
        State state = stateMap.get(pos);
        if (state == null) {
            state = new State();
            stateMap.put(pos, state);
        }
        return state;
    }

    public void setAllSelect(boolean checked) {
        for (int i = 0; i < getCount(); i++) {
            State state = getState(i);
            state.setDeleted(checked);
            notifyDataSetChanged();
        }
    }

    public String getSelectedIds() {
        String ids = "";
        for (int key : stateMap.keySet()) {
            if (stateMap.get(key).isDeleted()) {
                ids += getItem(key).getMsgID() + ",";
            }
        }
        return ids;
    }

    public void setEditMode(boolean mode) {
        this.mIsEditingMode = mode;
        notifyDataSetChanged();
    }

    public void clearState() {
        stateMap.clear();
    }

    public class State {
        boolean isDeleted = false;

        public boolean isDeleted() {
            return isDeleted;
        }

        public void setDeleted(boolean isDeleted) {
            this.isDeleted = isDeleted;
        }
    }
}
