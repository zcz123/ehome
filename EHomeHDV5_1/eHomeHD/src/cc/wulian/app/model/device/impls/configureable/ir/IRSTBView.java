package cc.wulian.app.model.device.impls.configureable.ir;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.view.View;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.impls.AbstractDevice;
import cc.wulian.app.model.device.utils.DeviceCache;
import cc.wulian.ihome.wan.entity.DeviceIRInfo;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.activity.devicesetting.DeviceSettingActivity;
import cc.wulian.smarthomev5.dao.IRDao;
import cc.wulian.smarthomev5.tools.SendMessage;
import cc.wulian.smarthomev5.utils.CmdUtil;

import com.yuantuo.customview.ui.WLToast;

public class IRSTBView extends AbstractIRView{
	private WulianDevice device;
	private IRDao irDao = IRDao.getInstance();
	private DeviceIRInfo deviceIRInfo;
	private final View.OnClickListener mOnClickListener = new View.OnClickListener()
	{
		@Override
		public void onClick( View v ) {
			if (device == null || !device.isDeviceOnLine()) {
				WLToast.showToast(mContext, resources.getString(R.string.device_offline), WLToast.TOAST_SHORT);
				return;
			}
			if(deviceIRInfo == null){
				WLToast.showToast(mContext, resources.getString(R.string.device_no_config), WLToast.TOAST_SHORT);
				return;
			}
			String tag = String.valueOf(v.getTag());
			if (StringUtil.isNullOrEmpty(tag)) return;
			StringBuilder sb = new StringBuilder();
			sb.append(CmdUtil.IR_MODE_CTRL);
			sb.append(deviceIRInfo.getKeyset());
			sb.append(tag);
			String sendData = sb.toString();
			SendMessage.sendControlDevMsg(
					deviceIRInfo.getGwID(), 
					deviceIRInfo.getDeviceID(),
					deviceIRInfo.getEp(),
					deviceInfo.getType(),
					sendData);
		}
	};
	public IRSTBView(Context context, DeviceInfo info) {
		super(context, info);
		device = DeviceCache.getInstance(mContext).getDeviceByID(mContext, deviceInfo.getGwID(), deviceInfo.getDevID());
	}

	@Override
	public View onCreateView() {
		return inflater.inflate(R.layout.common_ir_frame_stb, null);
	}

	@Override
	public void onViewCreated(View view) {
		TypedArray array = resources.obtainTypedArray(R.array.ir_stb_id_array);

		int length = array.length();
		for (int i = 0; i < length; i++) {
			initView(view, array.getResourceId(i, 0));
		}
		array.recycle();
	}
	private void initView( View view, int resId ) {
		View keyView = view.findViewById(resId);
		if (keyView == null) return;
		keyView.setOnClickListener(mOnClickListener);
	}
	@Override
	public void reloadData() {
		DeviceIRInfo info = new DeviceIRInfo();
		info.setGwID(deviceInfo.getGwID());
		info.setDeviceID(deviceInfo.getDevID());
		if(deviceInfo.getDevEPInfo() != null){
			info.setEp(deviceInfo.getDevEPInfo().getEp());
		}else{
			info.setEp(WulianDevice.EP_14);
		}
		info.setIRType(getType());
		DeviceIRInfo resultInfo = irDao.getById(info);
		if(resultInfo == null)
			return ;
		if(deviceIRInfo == null)
			deviceIRInfo = resultInfo;
		else{
			deviceIRInfo.setCode(resultInfo.getCode());
			deviceIRInfo.setKeyset(resultInfo.getKeyset());
			deviceIRInfo.setEp(resultInfo.getEp());
			deviceIRInfo.setIRType(resultInfo.getIRType());
		}
	}

	@Override
	public String getType() {
		return TYPE_STB;
	}

	@Override
	public Intent getSettingIntent() {
		DeviceIRInfo info = this.deviceIRInfo;
		if(info == null){
			info = new DeviceIRInfo();
			info.setDeviceID(device.getDeviceID());
			info.setGwID(device.getDeviceGwID());
			info.setIRType(getType());
			if(deviceInfo.getDevEPInfo() != null){
				info.setEp(deviceInfo.getDevEPInfo().getEp());
			}else{
				info.setEp(WulianDevice.EP_14);
			}
		}
		Intent intent = new Intent(mContext,DeviceSettingActivity.class);
		intent.putExtra(EditIRSTBFragment.EXTRA_DEVICE_IR_STB, info);
		intent.putExtra(AbstractDevice.SETTING_LINK_TYPE, AbstractDevice.SETTING_LINK_TYPE_HEAD_DETAIL);
		intent.putExtra(DeviceSettingActivity.SETTING_FRAGMENT_CLASSNAME,EditIRSTBFragment.class.getName());
		return intent ;
	}


}
