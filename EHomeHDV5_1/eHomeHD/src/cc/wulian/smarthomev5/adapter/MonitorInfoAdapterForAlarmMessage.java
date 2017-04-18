package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import cc.wulian.smarthomev5.entity.camera.CameraInfo;

public class MonitorInfoAdapterForAlarmMessage extends WLBaseAdapter<CameraInfo>
{

	public MonitorInfoAdapterForAlarmMessage(Context context,
			List<CameraInfo> data) {
		super(context, data);
		// TODO Auto-generated constructor stub
	}	 
//	  private int cameraId = -1;
//	  private LinearLayout editLayout ;
//	  private LinearLayout monitorNameLayout ;
//	  private LinearLayout monitorTypeLayout;
//	  
//      private TextView monintorName ;
//      private TextView monintorType ;
//	  private AreaGroupManager areaGroupManager = AreaGroupManager.getInstance();
//	public MonitorInfoAdapterForAlarmMessage( Context context, List<CameraInfo> data,CameraInfo mCurrentInfo )
//	{
//		super(context, data);
//	}
//
//	@Override
//	protected View newView(Context context, LayoutInflater inflater,
//			ViewGroup parent, int pos) {
//		return inflater.inflate(R.layout.fragment_alarm_message_monitor_list_item, parent, false);
//	}
//	@Override
//	protected void bindView( Context context, View view, int pos, CameraInfo item ) {
//		monitorNameLayout = (LinearLayout) view.findViewById(R.id.monitor_name_layout);
//		monitorTypeLayout = (LinearLayout) view.findViewById(R.id.monitor_type_layout);
//		monintorName = (TextView) view.findViewById(R.id.monitor_name);
//		monintorType = (TextView) view.findViewById(R.id.monitor_type);
//		
//		DeviceAreaEntity mDeviceAreaEntity =areaGroupManager.getDeviceAreaEntity(item.getGwId(), item.getAreaID());
//		String areaName = "";
//		if(mDeviceAreaEntity != null){
//			areaName = mDeviceAreaEntity.getName();
//		}else{
//			areaName = mContext.getString(R.string.config_edit_dev_area_type_other_default);
//		}
//		monintorName.setText(item.getCamName()+"-"+ areaName);
//		switch(item.getCamType()){
//        case 1 :
//        	monintorType.setText(mContext.getString(R.string.monitor_ip_video_camera));
//        	break;
//        case 4 :
//        	monintorType.setText(mContext.getString(R.string.monitor_hard_disk_video_camera_4));
//        	break;
//        case 8 :
//        	monintorType.setText(mContext.getString(R.string.monitor_hard_disk_video_camera_8));
//        	break;
//        case 11 :
//        	monintorType.setText(mContext.getString(R.string.monitor_cloud_one_video_camera));
//        	break;
//        case 12 :
//        	monintorType.setText(mContext.getString(R.string.monitor_cloud_two_video_camera));
//        	break;
//        case 13 :
//        	monintorType.setText(mContext.getString(R.string.monitor_cloud_three_video_camera));
//        	break;
//        case 21 :
//        	monintorType.setText(mContext.getString(R.string.monitor_cloud_video_camera_wla));
//        	break;
//		default :
//			break;
//        }		
//        monitorNameLayout.setOnClickListener(new OnClick(item));
//        monitorTypeLayout.setOnClickListener(new OnClick(item));
//	}
//
//	private final class OnClick implements View.OnClickListener
//	{
//		private final CameraInfo item;
//
//		public OnClick( CameraInfo item )
//		{
//			this.item = item;
//		}
//
//		@Override
//		public void onClick( View v ) {
//			switch (v.getId()) {
//				case R.id.monitor_name_layout :
//					jumptoMonitorView(item);
//					break;
//				case R.id.monitor_type_layout :
//					jumptoMonitorView(item);
//					break;
//				default :
//					break;
//			}
//		}
//	}
//	public void jumptoMonitorView (CameraInfo info){
//		Intent it = new Intent();
//		 switch(info.getCamType()){
//	        case 1 :
//	        	it.setClass(mContext, MonitorTTActivity.class);
//	        	break;
//	        case 4 :
//	        	it.setClass(mContext, MonitorHCActivity.class);
//	        	break;
//	        case 8 :
//	        	it.setClass(mContext, MonitorHCActivity.class);
//	        	break;
//	        case 11 :
//	        	it.setClass(mContext, MonitorTKActivity.class);
//	        	break;
//	        case 12 :
//	        	it.setClass(mContext, MonitorVSActivity.class);
//	        	break;
//	        case 13 :
//	        	it.setClass(mContext, MonitorSTActivity.class);
//	        	break;
//	        case 21 :
////	        	it.setClass(mContext, MonitorWLActivity.class);
//	        	WLToast.showToast(mContext, "物联摄像机", WLToast.TOAST_SHORT);
//	        	break;
//			default :
//				break;
//	        }		
//		it.putExtra(CameraInfo.EXTRA_CAMERA_INFO, info);
//		mContext.startActivity(it);
//	}
}
