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
import cc.wulian.smarthomev5.entity.MessageEventEntity;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.DateUtil;


public class OtherMessageClickedAdapter extends WLBaseAdapter<MessageEventEntity> {
	private boolean isEditable = false;
	private final DeviceCache mCache;
	private Map<Integer, State> stateMap = new HashMap<Integer,State>();
	
    public OtherMessageClickedAdapter(Context context, List<MessageEventEntity> data) {
		super(context, data);
		mCache = DeviceCache.getInstance(context);
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		 return inflater.inflate(R.layout.fragment_other_message_clicked_item, parent, false);
	}
	
	@Override
	protected void bindView(Context context, View view, int pos,
			MessageEventEntity item) {
		final TextView mTimeView = (TextView) view.findViewById(R.id.message_time);
		final TextView mTextView = (TextView) view.findViewById(R.id.detail_message);
		final ImageView delateimage = (ImageView) view.findViewById(R.id.delate_image);
        String mtime = DateUtil.getHourAndMinu(mContext,  Long.parseLong(item.time));		
		mTimeView.setText(mtime);
		
		String contentString = item.epData;
		String showName =item.epData;
		if (item.isMessageScene()) {
			contentString = context.getString(R.string.scene_info_timing_scene);
		}else{
			showName = DeviceTool.getDeviceNameByIdAndType(mContext, item.devID,item.epType);
			WulianDevice device = mCache.getDeviceByID(mContext, item.gwID, item.devID);
			if(device != null){
				showName = DeviceTool.getDeviceShowName(device);
			}
			if (item.isMessageSensor()) {
				contentString = item.epData;
				if(!StringUtil.isNullOrEmpty(item.epName)){
					showName = item.epName;
					contentString = context.getString(R.string.scene_info_timing_scene);
				}
			}else if(item.isMessageLowPower()){
				contentString = mResources.getString(R.string.home_message_low_power_warn);
			}else if(item.isMessageOnline()){
				contentString = mResources.getString(R.string.home_message_online_warning);
			}
		}
		mTextView.setText(showName+"  "+contentString);
		if(isEditable){
			delateimage.setVisibility(View.VISIBLE);
			State state = getState(pos);
			delateimage.setSelected(state.isDeleted());
		}else{
			delateimage.setVisibility(View.GONE);
		}
		
	}
	

	public State getState(int pos){
		State state = stateMap.get(pos);
		if(state == null){
			state = new State();
			stateMap.put(pos, state);
		}
		return state;
	}
	public void setAllSelect(boolean checked){
		for(int i = 0; i < getCount(); i++ ){
			State state = getState(i);
			state.setDeleted(checked);
			notifyDataSetChanged();
		}
	}
	
	
	public String getSelectedIds(){
		String ids = "";
		for(int key: stateMap.keySet()){
			if(stateMap.get(key).isDeleted()){
				ids+=getItem(key).getMsgID()+",";
			}
		}
		return ids;
	}
	public void toggleEditMode(){
		isEditable = !isEditable;
	}
	public void setEditMode(boolean mode){
		this.isEditable = mode;
	}
	public void clearState(){
		stateMap.clear();
	}

	public class State{
		boolean isDeleted = false;

		public boolean isDeleted() {
			return isDeleted;
		}

		public void setDeleted(boolean isDeleted) {
			this.isDeleted = isDeleted;
		}
		
	}
	
}
