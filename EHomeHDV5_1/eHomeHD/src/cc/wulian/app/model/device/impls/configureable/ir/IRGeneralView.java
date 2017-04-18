package cc.wulian.app.model.device.impls.configureable.ir;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.dao.IRDao;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class IRGeneralView extends AbstractIRView{

	private GridView gridView;
	private IRKeyAdapter irKeyAdapter;
	private IRDao irDao = IRDao.getInstance();
	
	public IRGeneralView(Context context, DeviceInfo info) {
		super(context, info);
		irKeyAdapter = new IRKeyAdapter(mContext, null);
	}

	@Override
	public View onCreateView() {
		return inflater.inflate(R.layout.device_ir_genenral_content, null);
	}

	@Override
	public void onViewCreated(View view) {
		gridView = (GridView)view.findViewById(R.id.device_ir_general_gv);
		gridView.setAdapter(irKeyAdapter);
		gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				final DeviceIRInfo deviceIRInfo = irKeyAdapter.getItem(position);
				StringBuilder sb = new StringBuilder();
				sb.append(CmdUtil.IR_MODE_CTRL);
				if(TYPE_GENERAL.equals(getType())){
					sb.append(getType() +"000"+deviceIRInfo.getCode());
				}
				else{
					sb.append(deviceIRInfo.getCode());
				}
				String sendData = sb.toString();
				SendMessage.sendControlDevMsg(
						deviceIRInfo.getGwID(), 
						deviceIRInfo.getDeviceID(),
						deviceIRInfo.getEp(),
						deviceInfo.getType(),
						sendData);
			}
		});
		irKeyAdapter.swapData(getIREntites());
	}
	private List<DeviceIRInfo> getIREntites(){
		DeviceIRInfo entity = new DeviceIRInfo();
		entity.setGwID(deviceInfo.getGwID());
		entity.setDeviceID(deviceInfo.getDevID());
		if(deviceInfo.getDevEPInfo() != null){
			entity.setEp(deviceInfo.getDevEPInfo().getEp());
		}else{
			entity.setEp(WulianDevice.EP_14);
		}
		entity.setIRType(getType());
		entity.setStatus(DeviceIRInfo.STATUS_STUDYED);
		return irDao.findListAll(entity);
	}
	@Override
	public void reloadData() {
		irKeyAdapter.swapData(getIREntites());
	}

	@Override
	public Intent getSettingIntent() {
		Intent intent = new Intent(mContext,DeviceSettingActivity.class);
		DeviceIRInfo info =  new DeviceIRInfo();
		info.setGwID(deviceInfo.getGwID());
		info.setDeviceID(deviceInfo.getDevID());
		if(deviceInfo.getDevEPInfo() != null){
			info.setEp(deviceInfo.getDevEPInfo().getEp());
		}else{
			info.setEp(WulianDevice.EP_14);
		}
		info.setIRType(getType());
		intent.putExtra(EditIRGeneralFragment.EXTRA_DEVICE_IR_GENERAL, info);
		intent.putExtra(AbstractDevice.SETTING_LINK_TYPE, AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
		intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME, EditIRGeneralFragment.class.getName());
		return intent ;
	}
	
	@Override
	public String getType() {
		String type = TYPE_GENERAL;
		if(ConstUtil.DEV_TYPE_FROM_GW_IR_CONTROL.equals(deviceInfo.getType()))
			type = ""; 
		return type;
	}

	public class IRKeyAdapter extends WLBaseAdapter<DeviceIRInfo>{

		public IRKeyAdapter(Context context, List<DeviceIRInfo> data) {
			super(context, data);
		}

		@Override
		protected View newView(Context context, LayoutInflater inflater,
				ViewGroup parent, int pos) {
			return inflater.inflate(R.layout.device_ir_genenral_key_item,null);
		}

		@Override
		protected void bindView(Context context, View view, int pos,DeviceIRInfo item) {
			TextView keyNameTextView = (TextView)view.findViewById(R.id.device_ir_key_name);
			keyNameTextView.setText(item.getName());
		}
	}
}
