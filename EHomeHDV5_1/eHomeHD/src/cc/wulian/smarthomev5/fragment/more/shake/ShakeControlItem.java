package cc.wulian.smarthomev5.fragment.more.shake;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import cc.wulian.smarthomev5.entity.ShakeEntity;
import cc.wulian.smarthomev5.event.ShakeEvent;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.view.AbstractDeviceControlItem;
import de.greenrobot.event.EventBus;

public class ShakeControlItem extends AbstractDeviceControlItem{
	private ShakeEntity shakeEntity;
	public ShakeControlItem(Context context, ShakeEntity shakeEntity) {
		super(context);
		this.shakeEntity = shakeEntity;
		mDevice = mDeviceCache.getDeviceByID(mContext, shakeEntity.getGwID(), this.shakeEntity.getOperateID());
		mDeleteView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new ShakeEvent(CmdUtil.MODE_DEL, null, ShakeControlItem.this.shakeEntity, true));
			}
		});
		setWulianDevice(mDevice);
	}
	@Override
	public void setEpData(String epData) {
		shakeEntity.setEpData(epData);
	}

	@Override
	public String getEPData() {
		return shakeEntity.getEpData();
	}
	public ShakeEntity getShakeEntity() {
		return shakeEntity;
	}
	public void setShakeEntity(ShakeEntity shakeEntity) {
		this.shakeEntity = shakeEntity;
	}
	@Override
	public String getEP() {
		return shakeEntity.getEp();
	}

}
