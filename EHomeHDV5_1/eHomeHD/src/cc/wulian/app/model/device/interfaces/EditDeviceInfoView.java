package cc.wulian.app.model.device.interfaces;

import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.CollectionsUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class EditDeviceInfoView {

	private Context context;
	private LinearLayout customViewBackground;
	private Resources resource;
	private LayoutInflater inflater;
	private List<DeviceCategoryEntity> icons ;
	private WulianDevice device;
	private CategoryIconAdapter adapter ;
	public EditDeviceInfoView(Context context) {
		this.context = context;
		inflater = LayoutInflater.from(context);
		resource = context.getResources();
		customViewBackground = (LinearLayout)inflater.inflate(R.layout.common_edit_device_content, null);
		
	}
	public void setDevice(WulianDevice device){
		this.device = device;
	}

	public void setDeviceIcons(List<DeviceCategoryEntity> icons) {
		this.icons = icons;
	}
	private void buildView(){
		EditText mainDeviceName = (EditText) customViewBackground.findViewById(R.id.mainDeviceEditText);
		if(!StringUtil.equals(device.getDeviceName(), "-1")){
			mainDeviceName.setText(device.getDeviceName());
		}
		if(!StringUtil.isNullOrEmpty(device.getDeviceName()))
			mainDeviceName.setSelection(device.getDeviceName().length());
		mainDeviceName.requestFocus();
		mainDeviceName.setHint(device.getDefaultDeviceName());
		Map<String,WulianDevice> childMap = device.getChildDevices();
		if (childMap != null && !childMap.isEmpty()) {
			TextView mainDeviceTextView = (TextView) customViewBackground.findViewById(R.id.mainDeviceTextView);
			mainDeviceTextView.setVisibility(View.VISIBLE);

			LinearLayout subDeviceLinearLayout = (LinearLayout) customViewBackground.findViewById(R.id.subDeviceLinearLayout);
			subDeviceLinearLayout.setVisibility(View.VISIBLE);

			LinearLayout subDeviceInfoLinearLayout = (LinearLayout) customViewBackground.findViewById(R.id.subDeviceInfoLinearLayout);

			for (String ep : childMap.keySet()) {
				WulianDevice childDevice = childMap.get(ep);
				EditText subDeviceEditText = new EditText(inflater.getContext());
				subDeviceEditText.setHint(childDevice.getDefaultDeviceName());
				if(!StringUtil.equals(childDevice.getDeviceInfo().getDevEPInfo().getEpName(), "-1")){
					subDeviceEditText.setText(childDevice.getDeviceInfo().getDevEPInfo().getEpName());
				}
				subDeviceEditText.setTextColor(inflater.getContext().getResources().getColor(R.color.black));
				subDeviceInfoLinearLayout.addView(subDeviceEditText);
			}
		}
		if(icons != null && icons.size() >0){
			adapter = new CategoryIconAdapter(context, icons);
			GridView iconGridView = (GridView)customViewBackground.findViewById(R.id.chooseDeviceCategoryGridView);
			iconGridView.setVisibility(View.VISIBLE);
			adapter.setSelectCategory(device.getDeviceInfo().getCategory());
			iconGridView.setAdapter(adapter);
		}
	}
	public View getView(){
		buildView();
		return customViewBackground;
	}
	public void updateDeviceInfo(){
		EditText mainDeviceTextView = (EditText) customViewBackground.findViewById(R.id.mainDeviceEditText);
		LinearLayout subDeviceInfoLinearLayout = (LinearLayout) customViewBackground.findViewById(R.id.subDeviceInfoLinearLayout);
		DeviceInfo info = new DeviceInfo();
		String devType = device.getDeviceType();
		info.setGwID(device.getDeviceGwID());
		info.setDevID(device.getDeviceID());
		info.setType(devType);
		if(adapter != null && !StringUtil.isNullOrEmpty(adapter.getSelectCategory()) && !StringUtil.equals(adapter.getSelectCategory(), "-1") ){
			info.setCategory(adapter.getSelectCategory());
		}else{
			info.setCategory(device.getDeviceInfo().getCategory());
		}
		info.setName(mainDeviceTextView.getText().toString());
		Map<String,WulianDevice> childMap= device.getChildDevices();
		if (childMap != null && !childMap.isEmpty()) {
			List<WulianDevice> childDevices = CollectionsUtil.mapConvertToList(childMap);
			for (int i = 0; i < childDevices.size(); i++) {
				WulianDevice childDevice = childDevices.get(i);
				DeviceInfo childDeviceInfo = childDevice.getDeviceInfo();
				DeviceEPInfo epInfo = childDeviceInfo.getDevEPInfo();
				String ep = epInfo.getEp();
				String epType = epInfo.getEpType();
				String status = epInfo.getEpStatus();
				String epName = ((EditText) subDeviceInfoLinearLayout.getChildAt(i)).getText().toString();
				SendMessage.sendSetDevMsg( info.getGwID(), CmdUtil.MODE_UPD,
						info.getDevID(),  info.getType(), info.getName(),
						info.getCategory(), null,ep, epType, epName, status);
			}
		}
		else {
			DeviceEPInfo epInfo = device.getDeviceInfo().getDevEPInfo();
			SendMessage.sendSetDevMsg( info.getGwID(), CmdUtil.MODE_UPD,
					info.getDevID(),  info.getType(), info.getName(),
					info.getCategory(), info.getRoomID(),device.getDefaultEndPoint(),epInfo.getEpType(), epInfo.getEpName(), epInfo.getEpStatus());
		}
	}
	public class CategoryIconAdapter extends WLBaseAdapter<DeviceCategoryEntity>
	{

		private String selectCategory;
		public CategoryIconAdapter( Context context, List<DeviceCategoryEntity> data )
		{
			super(context, data);
		}


		@Override
		protected View newView( Context context, LayoutInflater inflater, ViewGroup parent, int pos ) {
			return inflater.inflate(R.layout.common_edit_device_category_item, parent, false);
		}

		@Override
		protected void bindView( Context context, View view, final int pos, final DeviceCategoryEntity item ) {

			LinearLayout iconBackLayout = (LinearLayout) view.findViewById(R.id.categoryIconBackLayout);
			ImageView categoryIcon = (ImageView) view.findViewById(R.id.categoryIcon);
			Drawable iconDrawable = mResources.getDrawable(item.getResources().get(1));
			if(device instanceof Controlable){
				Controlable controlable  = (Controlable)device;
				if(controlable.isOpened())
					iconDrawable = mResources.getDrawable(item.getResources().get(0));
			}
			if(iconDrawable != null)
				categoryIcon.setImageDrawable(iconDrawable);
			if (item.getCategory().equals(selectCategory)) {
				iconBackLayout.setBackgroundColor(context.getResources().getColor(R.color.holo_blue_dark));
			}
			else {
				iconBackLayout.setBackgroundColor(context.getResources().getColor(R.color.trant));
			}
			iconBackLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					selectCategory = item.getCategory();
					notifyDataSetChanged();
				}
			});
		}


		public String getSelectCategory() {
			return selectCategory;
		}


		public void setSelectCategory(String selectCategory) {
			this.selectCategory = selectCategory;
		}
		
	}
	
	public static class DeviceCategoryEntity{
		private String category;
		private Map<Integer,Integer> resources;
		public String getCategory() {
			return category;
		}
		public void setCategory(String category) {
			this.category = category;
		}
		public Map<Integer, Integer> getResources() {
			return resources;
		}
		public void setResources(Map<Integer, Integer> resources) {
			this.resources = resources;
		}
		
	}
}
