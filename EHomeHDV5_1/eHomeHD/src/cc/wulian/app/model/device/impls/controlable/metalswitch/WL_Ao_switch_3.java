package cc.wulian.app.model.device.impls.controlable.metalswitch;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.ihome.wan.entity.DeviceEPInfo;
import cc.wulian.smarthomev5.R;

/**
 * 三键金属开关
 */
@DeviceClassify(devTypes = {"Am"}, category = Category.C_CONTROL)
public class WL_Ao_switch_3 extends AbstractMetalSwitch
{

	protected String mSwitchStatus14;
	protected String mSwitchStatus15;
	protected String mSwitchStatus16;
	protected String mSwitchMode14;
	protected String mSwitchMode15;
	protected String mSwitchMode16;
	private static final String[] EP_SEQUENCE = {EP_14, EP_15, EP_16};

	public WL_Ao_switch_3(Context context, String type )
	{
		super(context, type);
	}

	@Override
	public void onResume() {
		super.onResume();
		controlDevice(ep, epType, "102");
	}

	@Override
	protected boolean isMultiepDevice() {
		return true;
	}

	@Override
	public synchronized void onDeviceData(String gwID, String devID, DeviceEPInfo devEPInfo, String cmd, String mode) {
		String ep = devEPInfo.getEp();
		WulianDevice device = getChildDevice(ep);
		if (device != null) {
			device.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
			super.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
			removeCallbacks(mRefreshStateRunnable);
			post(mRefreshStateRunnable);
			fireDeviceRequestControlData();
		} else {
			super.onDeviceData(gwID, devID, devEPInfo,cmd,mode);
		}
	}



	@Override
	public String getDefaultDeviceName() {
		String defaultName = getDeviceInfo().getDevEPInfo().getEpName();
		if (isNull(defaultName)) {
			defaultName = getString(R.string.add_device_name_switch_3_key);
		}
		return defaultName;
	}

	@Override
	public String[] getTouchEPResources() {
		return EP_SEQUENCE;
	}

	@Override
	public String[] getTouchEPNames() {
		String ep14Name = "key1";
		String ep15Name = "key2";
		String ep16Name = "key3";
		return new String[]{ep14Name,ep15Name, ep16Name};
	}

	@Override
	public String[] getSwitchModes() {
		return new String[]{mSwitchMode14 , mSwitchMode15, mSwitchMode16};
	}

	@Override
	public String[] getSwitchStatus() {
		return new String[]{mSwitchStatus14 ,mSwitchStatus15, mSwitchStatus16};
	}

	@Override
	public Intent getSettingIntent() {
		Intent intent = new Intent(mContext, SettingActivity.class);
		Bundle bundle = new Bundle();
		bundle.putString("gwID", gwID);
		bundle.putString("devID", devID);
		bundle.putString("ep", ep);
		bundle.putString("epType", epType);
		intent.putExtra("SettingFragmentInfo", bundle);
		return intent ;
	}

	@Override
	public void handleEpData(String mEpData){
		Log.d("---ccc---", "mEpData3:::"+mEpData);
		if(mEpData.length() == 6){
			if(isSameAs(mEpData.substring(2 ,4) ,"01")){
				if(isSameAs(mEpData.substring(0 ,2) ,"00")){
					if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_MODE_TURN)){
						mSwitchMode14 = SWTICH_MODE_TURN;
					}else if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_MODE_SCENCE)){
						mSwitchMode14 = SWTICH_MODE_SCENCE;
					}else if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_MODE_BIND)){
						mSwitchMode14 = SWTICH_MODE_BIND;
					}
				}
				if(isSameAs(mEpData.substring(0 ,2) ,"01")){
					mSwitchMode14 = SWTICH_MODE_TURN;
					if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_STATUS_ON)){
						mSwitchStatus14 = SWTICH_STATUS_ON;
					}else if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_STATUS_OFF)){
						mSwitchStatus14 = SWTICH_STATUS_OFF;
					}
				}
			}

			if(isSameAs(mEpData.substring(2 ,4) ,"02")){
				if(isSameAs(mEpData.substring(0 ,2) ,"00")){
					if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_MODE_TURN)){
						mSwitchMode15 = SWTICH_MODE_TURN;
					}else if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_MODE_SCENCE)){
						mSwitchMode15 = SWTICH_MODE_SCENCE;
					}else if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_MODE_BIND)){
						mSwitchMode15 = SWTICH_MODE_BIND;
					}
				}
				if(isSameAs(mEpData.substring(0 ,2) ,"01")){
					mSwitchMode15 = SWTICH_MODE_TURN;
					if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_STATUS_ON)){
						mSwitchStatus15 = SWTICH_STATUS_ON;
					}else if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_STATUS_OFF)){
						mSwitchStatus15= SWTICH_STATUS_OFF;
					}
				}else if(isSameAs(mEpData.substring(0 ,2) ,"03")){

				}
			}

			if(isSameAs(mEpData.substring(2 ,4) ,"03")){
				if(isSameAs(mEpData.substring(0 ,2) ,"00")){
					if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_MODE_TURN)){
						mSwitchMode16 = SWTICH_MODE_TURN;
					}else if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_MODE_SCENCE)){
						mSwitchMode16 = SWTICH_MODE_SCENCE;
					}else if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_MODE_BIND)){
						mSwitchMode16 = SWTICH_MODE_BIND;
					}
				}
				if(isSameAs(mEpData.substring(0 ,2) ,"01")){
					mSwitchMode16 = SWTICH_MODE_TURN;
					if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_STATUS_ON)){
						mSwitchStatus16 = SWTICH_STATUS_ON;
					}else if(isSameAs(mEpData.substring(4 ,6) ,SWTICH_STATUS_OFF)){
						mSwitchStatus16= SWTICH_STATUS_OFF;
					}
				}else if(isSameAs(mEpData.substring(0 ,2) ,"03")){

				}
			}
		}
	}

}