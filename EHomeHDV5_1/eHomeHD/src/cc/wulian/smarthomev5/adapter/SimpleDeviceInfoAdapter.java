package cc.wulian.smarthomev5.adapter;

import java.util.List;

import com.google.zxing.common.StringUtils;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;

public class SimpleDeviceInfoAdapter extends WLBaseAdapter<DeviceInfo> {
	private DeviceCache mDeviceCache;

	public SimpleDeviceInfoAdapter(Context context, List<DeviceInfo> data) {
		super(context, data);
		mDeviceCache = DeviceCache.getInstance(context);
	}

	@Override
	protected void bindView(Context context, View view, int pos, DeviceInfo item) {

		ImageView iconImageView = (ImageView) view.findViewById(R.id.scene_icon_iv);
		TextView sceneName = (TextView) view.findViewById(R.id.scene_name_tv);
		if (item.getName().equals(
				mContext.getResources().getString(R.string.scene_unbind))) {
			iconImageView.setVisibility(View.INVISIBLE);
			sceneName.setText(item.getName());
		} else {
			WulianDevice device = mDeviceCache.getDeviceByIDEp(context, item.getGwID(), item.getDevID(), item.getDevEPInfo().getEp());
			if (device != null) {
				iconImageView.setVisibility(View.VISIBLE);
				Drawable normalIcon = device.getDefaultStateSmallIcon();
				iconImageView.setImageDrawable(normalIcon);
				if (StringUtil.isNullOrEmpty(device.getDeviceName())) {
					sceneName.setText(device.getDefaultDeviceName());
				} else {
					sceneName.setText(device.getDeviceName());
				}
			}
		}
	}

	@Override
	protected View newView(Context context, LayoutInflater inflater,
			ViewGroup parent, int pos) {
		return inflater.inflate(R.layout.scene_popup_scene_item, null);
	}

}
