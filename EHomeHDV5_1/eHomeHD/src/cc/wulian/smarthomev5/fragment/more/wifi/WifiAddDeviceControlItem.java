package cc.wulian.smarthomev5.fragment.more.wifi;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import cc.wulian.smarthomev5.entity.WifiEntity;
import cc.wulian.smarthomev5.event.WifiEvent;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.view.AbstractDeviceControlItem;
import de.greenrobot.event.EventBus;

public class WifiAddDeviceControlItem extends AbstractDeviceControlItem {
	private WifiEntity wifiEntity;

	public WifiAddDeviceControlItem(Context context, WifiEntity entity) {
		super(context);
		wifiEntity = entity;
		mDevice = mDeviceCache.getDeviceByID(mContext, wifiEntity.getGwID(), this.wifiEntity.getOperateID());
		mDeleteView.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				EventBus.getDefault()
						.post(new WifiEvent(CmdUtil.MODE_DEL, null,
								WifiAddDeviceControlItem.this.wifiEntity, true));
			}
		});
		setWulianDevice(mDevice);
	}

	@Override
	public void setEpData(String epData) {
		wifiEntity.setEpData(epData);
	}

	@Override
	public String getEPData() {
		return wifiEntity.getEpData();
	}

	@Override
	public String getEP() {
		return wifiEntity.getEp();
	}

	public WifiEntity getWifiEntity() {
		return wifiEntity;
	}

	public void setWifiEntity(WifiEntity wifiEntity) {
		this.wifiEntity = wifiEntity;
	}

}
