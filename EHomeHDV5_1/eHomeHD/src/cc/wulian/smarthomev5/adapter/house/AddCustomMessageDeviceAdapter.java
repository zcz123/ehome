package cc.wulian.smarthomev5.adapter.house;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;

/**
 * 自定义消息选择添加设备Adapter
 * 均为此管家规则中触发事件，限制条件和执行任务中出现的设备
 * @author Administrator
 *
 */
public class AddCustomMessageDeviceAdapter extends WLBaseAdapter<WulianDevice> {

	
	
	public AddCustomMessageDeviceAdapter(Context context,
			List<WulianDevice> data) {
		super(context, data);
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.task_manager_add_custom_message_device_item, parent,false);
	}
	
	@Override
	protected void bindView(Context context, View view, int pos,
			WulianDevice item) {
		ImageView ivIcon = (ImageView) view.findViewById(R.id.house_link_add_task_device_icon_iv);
		TextView tvDevName = (TextView) view.findViewById(R.id.house_custom_message_add_device_name_tv);
		TextView tvDevArea = (TextView) view.findViewById(R.id.house_custom_message_add_device_area_tv);
		
		if(ivIcon == null){
			System.out.println("ivIcon = null");
		}
		if(item == null){
			System.out.println("item = null");
		}
		
		ivIcon.setImageDrawable(item.getStateSmallIcon());
		if(item.getDeviceName()==null||item.getDeviceName().equals("")){
			tvDevName.setText(item.getDefaultDeviceName());
		}else{
			tvDevName.setText(item.getDeviceName());
		}
		
		//获取设备区域信息
		DeviceAreaEntity deviceAreaEntity = AreaGroupManager.getInstance().getDeviceAreaEntity(item.getDeviceGwID(), item.getDeviceRoomID());
		tvDevArea.setText("["+deviceAreaEntity.getName()+"]");
		
	}

}
