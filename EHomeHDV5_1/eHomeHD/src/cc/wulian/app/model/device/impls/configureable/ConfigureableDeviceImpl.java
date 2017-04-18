package cc.wulian.app.model.device.impls.configureable;

import android.content.Context;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;

public abstract class ConfigureableDeviceImpl extends AbstractDevice implements Configureable
{
	protected String ep;
	protected String epType;
	protected String epData;
	protected String epStatus;
	
	public ConfigureableDeviceImpl( Context context, String type )
	{
		super(context, type);
	}
	
	@Override
	public void refreshDevice(){
		DeviceEPInfo epInfo = getCurrentEpInfo();
		if (epInfo == null){
			return;
		}

		ep = epInfo.getEp();
		epType = epInfo.getEpType();
		epData = epInfo.getEpData();
		epStatus = epInfo.getEpStatus();
	}
}
