package cc.wulian.smarthomev5.fragment.house;

import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.databases.entitys.Area;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.view.SwipeTouchViewListener;

public class HouseKeeperCustomMessageItem {

	protected BaseActivity mActivity;
	private AutoActionInfo mInfo;
	private LayoutInflater inflater;
	
	private LinearLayout linearLayout;
	private FrameLayout frameLayout;
	private Button deleteButton;
	private LinearLayout itemLayout;
	private TextView tvMessage;
	private LinearLayout detailLayout;
	
	private OnMessageItemClickListener mListener;
	
	public HouseKeeperCustomMessageItem(BaseActivity mActivity,AutoActionInfo mInfo){
		this.mActivity = mActivity;
		this.mInfo = mInfo;
		
		inflater = LayoutInflater.from(mActivity);
		linearLayout = (LinearLayout) inflater.inflate(R.layout.layout_custom_message_swipe_menu, null);
		
		frameLayout = (FrameLayout) linearLayout.findViewById(R.id.task_manager_message_item_framelayout);
		deleteButton = (Button) linearLayout.findViewById(R.id.task_manager_message_item_delete);
		
		itemLayout = (LinearLayout) linearLayout.findViewById(R.id.task_manager_message_item_layout);
		tvMessage = (TextView) linearLayout
		.findViewById(R.id.task_manager_message_item_name);
		detailLayout = (LinearLayout) linearLayout
				.findViewById(R.id.task_manager_message_item_imv);
		
		//主要是设置这个监听
		itemLayout.setOnTouchListener(new SwipeTouchViewListener(itemLayout, deleteButton));
		initControlableView();
		initMessageItemView(mInfo);

	}
	

	public Button getDeleteButton() {
		return deleteButton;
	}



	public void setDeleteButton(Button deleteButton) {
		this.deleteButton = deleteButton;
	}



	/**
	 * 自定义消息view的点击监听
	 * @param mInfo2
	 */
	public void initControlableView() {
		detailLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				
				if(mListener!=null){
					mListener.onMessageItemClick();
				}
			}
		});
	}
	
	/**
	 * 设置自定义消息的文字信息
	 * @param mInfo
	 */
	public void initMessageItemView(AutoActionInfo mInfo) {
		String devId = mInfo.getObject().split(">")[0];
		WulianDevice device = DeviceCache.getInstance(mActivity).getDeviceByID(
				mActivity, AccountManager.getAccountManger().getmCurrentInfo().getGwID(), devId);
		if(device==null){
			tvMessage.setText(R.string.house_rule_add_new_no_find_device);
			detailLayout.setOnClickListener(null);
			return;
		}
		String deviceName = device.getDeviceName();
		if (deviceName == null || deviceName.equals("")) {
			deviceName = device.getDefaultDeviceName();
		}
		String area="";
		if(!device.getDeviceRoomID().equals(Area.AREA_DEFAULT)){
			DeviceAreaEntity areaEntity = AreaGroupManager.getInstance()
					.getDeviceAreaEntity(device.getDeviceGwID(),
							device.getDeviceRoomID());
			area= areaEntity.getName();
		}
		String parseData =area+deviceName+mActivity.getResources().getString(
				R.string.house_rule_detect)+mInfo.getEpData().substring(1);
		tvMessage.setText(parseData);
	}
	
	public interface OnMessageItemClickListener{
		void onMessageItemClick();
	}

	public void setOnMessageItemClickListener(OnMessageItemClickListener listener){
		this.mListener = listener;
	}

	public AutoActionInfo getmInfo() {
		return mInfo;
	}



	public void setmInfo(AutoActionInfo mInfo) {
		this.mInfo = mInfo;
	}



	public LinearLayout getView() {
		return linearLayout;
	}

}
