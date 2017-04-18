package cc.wulian.app.model.device.impls.configureable.ir;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.adapter.WLBaseAdapter;
import cc.wulian.smarthomev5.dao.IRDao;
import cc.wulian.smarthomev5.utils.CmdUtil;

public class EpDataGeneralView extends AbstractEpDataView{

	private GridView gridView;
	private IRKeyAdapter irKeyAdapter;
	private IRDao irDao = IRDao.getInstance();

	public EpDataGeneralView(Context context, DeviceInfo info,String epData) {
		super(context, info,epData);
		irKeyAdapter = new IRKeyAdapter(mContext, getIREntites());
		
	}

	@Override
	public View onCreateView() {
		rootView = inflater.inflate(R.layout.device_ir_genenral_content, null);
		return rootView;
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
				setEpData(sendData,true);
			}
		});
		gridView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return mGestureDetector.onTouchEvent(event);
			}
		});
		setEpData(epData,false);
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
	public String getType() {
		String type = TYPE_GENERAL;
		if(ConstUtil.DEV_TYPE_FROM_GW_IR_CONTROL.equals(deviceInfo.getType()))
			type = "-1"; 
		return type;
	}
	private void setEpData(String epData,boolean isFire) {
		if(epData!= null && epData.length()>1){
			if(TYPE_GENERAL.equals(getType())){
				if(epData.length() >1){
					String keySet = epData.substring(1);
					irKeyAdapter.setKeySelectedKeySet(keySet);
					irKeyAdapter.notifyDataSetChanged();
	 				if(isFire){
	 					fireSelectEpDataListener(epData);
	 				}
				}
			}else{
				String keySet = epData.substring(1);
				irKeyAdapter.setKeySelectedKeySet(keySet);
				irKeyAdapter.notifyDataSetChanged();
				if(isFire){
 					fireSelectEpDataListener(epData);
 				}
			}
		}
	}
	public static class IRKeyAdapter extends WLBaseAdapter<DeviceIRInfo>{

		private String keySelectedKeySet = "";
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
			LinearLayout keyBgLineLayout = (LinearLayout)view.findViewById(R.id.device_ir_key_bg);
			TextView keyNameTextView = (TextView)view.findViewById(R.id.device_ir_key_name);
			keyNameTextView.setText(item.getName());
			if(item.getKeyset().equals(keySelectedKeySet)){
				keyBgLineLayout.setSelected(true);
				keyNameTextView.setTextColor(mResources.getColor(R.color.black));
			}else{
				keyBgLineLayout.setSelected(false);
				keyNameTextView.setTextColor
				(mResources.getColor(R.color.white));
			}
		}

		public String getKeySelectedKeySet() {
			return keySelectedKeySet;
		}

		public void setKeySelectedKeySet(String keySelectedKeySet) {
			this.keySelectedKeySet = keySelectedKeySet;
		}
		
	}
}
