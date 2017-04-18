package cc.wulian.smarthomev5.adapter;

import java.util.BitSet;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.alarmable.Defenseable;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.DeviceTool;

public class AddDeviceInfoAdapter extends WLBaseAdapter<DeviceInfo>
{
	private final DeviceCache mDeviceCache;
	private BitSet mBitSet;

	public AddDeviceInfoAdapter( Context context, List<DeviceInfo> data )
	{
		super(context, data);
		mDeviceCache = DeviceCache.getInstance(context);
		mBitSet = new BitSet();
	}

	public BitSet getBitSet() {
		return mBitSet;
	}

	@Override
	protected View newView( Context context, LayoutInflater inflater, ViewGroup parent, int pos ) {
		return inflater.inflate(R.layout.item_add_device_infov5, parent, false);
	}

	@Override
	protected void bindView( Context context, View view, int pos, DeviceInfo item ) {
		final ImageView icon = (ImageView) view.findViewById(R.id.imageView_icon);
		final TextView name = (TextView) view.findViewById(R.id.textView_name);
		final CheckBox selectedCheckBox = (CheckBox) view.findViewById(R.id.checkBox_select);

		selectedCheckBox.setChecked(mBitSet.get(pos));

		WulianDevice device = mDeviceCache.getDeviceByID(mContext, item.getGwID(), item.getDevID());
		if (device != null) {
			view.setVisibility(View.VISIBLE);
			icon.setImageDrawable(device.getDefaultStateSmallIcon());
			StringBuilder sb = new StringBuilder();
			sb.append(DeviceTool.getDeviceShowName(device));
			if(item.getDevEPInfo() != null){
				sb.append(" - ");
				sb.append(DeviceUtil.ep2IndexString(item.getDevEPInfo().getEp()));
			}
			name.setText(sb);
			if(device instanceof Defenseable){
				Defenseable defenseable = (Defenseable)device;
				item.getDevEPInfo().setEpData(defenseable.getDefenseSetupProtocol());
			}
			else if(device instanceof Controlable){
				Controlable controlable = (Controlable)device;
				item.getDevEPInfo().setEpData(controlable.getOpenProtocol());
			}
		}
		else {
			view.setVisibility(View.GONE);
		}

	}

	public void onSelection( int pos ) {
		mBitSet.set(pos, !mBitSet.get(pos));
		this.notifyDataSetChanged();
	}

}
