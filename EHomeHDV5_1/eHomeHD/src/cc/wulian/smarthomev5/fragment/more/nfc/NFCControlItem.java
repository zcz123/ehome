package cc.wulian.smarthomev5.fragment.more.nfc;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import cc.wulian.smarthomev5.entity.NFCEntity;
import cc.wulian.smarthomev5.event.NFCEvent;
import cc.wulian.smarthomev5.tools.AccountManager;
import cc.wulian.smarthomev5.utils.CmdUtil;
import cc.wulian.smarthomev5.view.AbstractDeviceControlItem;
import de.greenrobot.event.EventBus;

public class NFCControlItem extends AbstractDeviceControlItem{
	private NFCEntity mifareSectorInfo;
	private String gwID;
	private AccountManager accountManager = AccountManager.getAccountManger();
	public NFCControlItem(Context context,NFCEntity mifareSectorInfo) {
		super(context);
		this.mifareSectorInfo = mifareSectorInfo;
		gwID = accountManager.getmCurrentInfo().getGwID();
		mDevice = mDeviceCache.getDeviceByID(mContext, gwID, this.mifareSectorInfo.getID());
		setWulianDevice(mDevice);
		mDeleteView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				EventBus.getDefault().post(new NFCEvent(CmdUtil.MODE_DEL,false,null,NFCControlItem.this.mifareSectorInfo));
			}
		});
	}
	@Override
	public void setEpData(String epData) {
		mifareSectorInfo.setEpData(epData);
	}

	public String getEPData() {
		return mifareSectorInfo.getEpData();
	}
	public NFCEntity getMifareSectorInfo() {
		return mifareSectorInfo;
	}
	public void setMifareSectorInfo(NFCEntity mifareSectorInfo) {
		this.mifareSectorInfo = mifareSectorInfo;
	}
	@Override
	public String getEP() {
		return mifareSectorInfo.getEp();
	}

}
