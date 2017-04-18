package cc.wulian.smarthomev5.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
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

/**
 * Created by WIN7 on 2014/7/11.
 */
public class OtherMessageAdapter extends WLBaseAdapter<MessageEventEntity> {
	
	private final DeviceCache mCache;
	
    public OtherMessageAdapter(Context context, List<MessageEventEntity> data) {
		super(context, data);
		mCache = DeviceCache.getInstance(context);
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.message_item, parent, false);
	}

	@Override
	protected void bindView(Context context, View view, int pos,
			MessageEventEntity item) {
		TextView mDeviceView = (TextView) view.findViewById(R.id.device_or_scene_message);
		TextView mTimeView = (TextView) view.findViewById(R.id.message_time);
		TextView mTextView = (TextView) view.findViewById(R.id.detail_message);
		ImageView mImageView = (ImageView) view.findViewById(R.id.home_number_imageview);
		String mtime = DateUtil.getHourAndMinu(mContext,  Long.parseLong(item.time));		
		mTimeView.setText(mtime);
		WulianDevice device = mCache.getDeviceByID(mContext, item.gwID, item.devID);
		String showName = DeviceTool.getDeviceNameByIdAndType(mContext, item.devID,item.epType);
		if(device != null){
			showName = DeviceTool.getDeviceShowName(device);
		}
		String contentStr= item.getEpData();
		if(item.isMessageLowPower()){
			contentStr = mResources.getString(R.string.home_message_low_power_warn);
		}else if(item.isMessageOnline()){
			contentStr = mResources.getString(R.string.home_message_online_warning);
		}else if(item.isMessageSensor()){
			if(!StringUtil.isNullOrEmpty(item.epName)){
				showName = item.epName;
				contentStr = context.getString(R.string.scene_info_timing_scene);
			}
		}
		mDeviceView.setText(showName);
		mTextView.setText(contentStr);
		Drawable smileDrawable = DeviceTool.getSmileDrawable(mContext, item.getSmile());
		if(smileDrawable != null){
			mImageView.setVisibility(View.VISIBLE);
			mImageView.setImageDrawable(smileDrawable);
		}else{
			mImageView.setVisibility(View.GONE);
		}
		
	}
}