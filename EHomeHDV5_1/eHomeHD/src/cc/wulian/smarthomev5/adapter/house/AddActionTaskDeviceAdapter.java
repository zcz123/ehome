package cc.wulian.smarthomev5.adapter.house;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.yuantuo.customview.ui.WLDialog;
import com.yuantuo.customview.ui.WLToast;
import com.yuantuo.customview.ui.WLDialog.MessageListener;

import android.app.Activity;
import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.entity.DeviceAreaEntity;
import cc.wulian.smarthomev5.fragment.device.AreaGroupManager;
import cc.wulian.smarthomev5.tools.DeviceTool;

public class AddActionTaskDeviceAdapter extends WLBaseAdapter<WulianDevice>{

	private static final String UNIT_MORE = "[";
	private static final String UNIT_LESS = "]";
	private static final String CONSTANT_COLOR_START = "<font color=#f31961>";
	private static final String CONSTANT_COLOR_END = "</font>";
	private WLDialog dialog = null;
	private Map<Integer,List<WulianDevice>> selectDeviceMap = new HashMap<Integer,List<WulianDevice>>();
	public static int deviceCount = 0;
	public AddActionTaskDeviceAdapter(Context context, List<WulianDevice> data,int count) {
		super(context, data);
		deviceCount = count;
	}

//	@Override
//	public int getCount() {
//		return data.size();
//	}
//	
//	@Override
//	public WulianDevice getItem(int position) {
//		return data.get(position);
//	}
	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.task_manager_add_link_device_item, parent, false);
	}

	@Override
	protected void bindView(Context context, View view, final int pos,
			final WulianDevice device) {
		super.bindView(context, view, pos, device);
		ImageView deviceIcon = (ImageView) view.findViewById(R.id.house_link_add_task_device_icon_iv);
		TextView deviceName = (TextView) view.findViewById(R.id.house_link_add_task_device_name_tv);
		TextView deviceArea = (TextView) view.findViewById(R.id.house_link_add_task_device_area_tv);
		ImageView addNumber = (ImageView) view.findViewById(R.id.house_link_add_task_device_add_img);
		final ImageView subNumber = (ImageView) view.findViewById(R.id.house_link_add_task_device_sub_img);
		final TextView addDeviceNumber = (TextView) view.findViewById(R.id.house_link_add_task_device_number_tv);
		
		List<WulianDevice> autoActionInfoList = selectDeviceMap.get(pos);
		if(autoActionInfoList != null && autoActionInfoList.size() > 0){
			subNumber.setVisibility(View.VISIBLE);
			addDeviceNumber.setVisibility(View.VISIBLE);
			addDeviceNumber.setText(autoActionInfoList.size() + "");
		}else{
			subNumber.setVisibility(View.INVISIBLE);
			addDeviceNumber.setVisibility(View.INVISIBLE);
		}

		//添加设备数量的按钮
		addNumber.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				List<WulianDevice> deviceList = selectDeviceMap.get(pos);
				if (deviceCount < 60) {
					if (deviceList == null) {
						deviceList = new ArrayList<WulianDevice>();
						selectDeviceMap.put(pos, deviceList);
					}
					//显示添加和减少按钮
					subNumber.setVisibility(View.VISIBLE);
					addDeviceNumber.setVisibility(View.VISIBLE);
					//添加设备
					deviceList.add(device);
					deviceCount++;
					addDeviceNumber.setText(deviceList.size() + "");
				} else {
					//设备数量超过60弹出提示对话框
					showTaskDeviceNumberDialog();
				}
			}
		});

		//减少设备数量的按钮
		subNumber.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				List<WulianDevice> deviceList = selectDeviceMap.get(pos);
				if(deviceList == null){
					deviceList = new ArrayList<WulianDevice>();
					selectDeviceMap.put(pos, deviceList);
				}
				subNumber.setVisibility(View.VISIBLE);
				addDeviceNumber.setVisibility(View.VISIBLE);
				if(deviceList.size() > 1){
					deviceList.remove(0);
					addDeviceNumber.setText(deviceList.size() + "");
					deviceCount--;
				}else{
					subNumber.setVisibility(View.INVISIBLE);
					addDeviceNumber.setVisibility(View.INVISIBLE);
					selectDeviceMap.remove(pos);
					deviceCount--;
				}
			}
		});
		deviceIcon.setImageDrawable(device.getDefaultStateSmallIcon());
		StringBuilder sb = new StringBuilder();
		if (!device.isDeviceOnLine()) {
			sb.append(UNIT_MORE);
			sb.append(CONSTANT_COLOR_START);
			sb.append(mResources.getString(R.string.device_offline));
			sb.append(CONSTANT_COLOR_END);
			sb.append(UNIT_LESS);
		}
		sb.append(DeviceTool.getDeviceShowName(device));
//		sb.append("-");
//		sb.append(DeviceUtil.ep2IndexString(device.getDeviceInfo().getDevEPInfo().getEp()));
		deviceName.setText(device.isDeviceOnLine() ? sb.toString() : Html
				.fromHtml(sb.toString()));
		setAreaName(device,deviceArea);
	}

	/**
	 * 设备数量超过60个时弹出此对话框
	 */
	private  void showTaskDeviceNumberDialog(){
		WLDialog.Builder builder = new WLDialog.Builder(mContext);
		LayoutInflater inflater = ((Activity) mContext).getLayoutInflater();
		View taskNumberView = inflater.inflate(
				R.layout.task_maneger_fragment_upgrade_dialog, null);
		TextView tvToast = (TextView) taskNumberView
				.findViewById(R.id.house_keeper_upgrade_promt);
		tvToast.setText(mContext.getResources().getString(R.string.house_rule_tasklist_count_hint));
		builder.setTitle(mContext.getResources().getString(R.string.gateway_router_setting_dialog_toast))
		.setContentView(taskNumberView)
		.setPositiveButton(android.R.string.ok)
		.setCancelOnTouchOutSide(false);
		dialog = builder.create();
		dialog.show();
	}
	private void setAreaName(WulianDevice device,TextView deviceArea){
		StringBuilder sb = new StringBuilder();
		DeviceAreaEntity entity = AreaGroupManager.getInstance().getDeviceAreaEntity(device.getDeviceGwID(),device.getDeviceRoomID());
		if (entity != null){
			sb.append(UNIT_MORE);
			sb.append(entity.getName());
			sb.append(UNIT_LESS);
			deviceArea.setText(sb.toString());
		}
		else {
			sb.append(UNIT_MORE);
			sb.append(mResources.getString(
					R.string.device_config_edit_dev_area_type_other_default));
			sb.append(UNIT_LESS);
			deviceArea.setText(sb.toString());
		}
	}


	public List<WulianDevice> getAllDevice(){
		Collection<List<WulianDevice>> collects = selectDeviceMap.values();
		List<WulianDevice> devices = new ArrayList<WulianDevice>();
		for(List<WulianDevice> collect : collects){
			if(collect != null){
				devices.addAll(collect);
			}
		}
		return devices;
	}
}
