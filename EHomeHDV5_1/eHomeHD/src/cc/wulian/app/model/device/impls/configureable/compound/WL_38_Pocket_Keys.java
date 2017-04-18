package cc.wulian.app.model.device.impls.configureable.compound;

import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.entity.DeviceInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.smarthomev5.dao.DeviceDao;
import cc.wulian.smarthomev5.utils.SqlUtil;

@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_POCKET_KEYS}, category = Category.C_OTHER)
public class WL_38_Pocket_Keys extends AbstractCompoundDevice
{

	private static final String[] EP_TOUCH = {EP_14,EP_16};
	private static final String[] EP_DEVICE = {EP_15};
	private static final int BIG_NORMAL_D = R.drawable.device_pocket_keys_big;

	private DeviceDao deviceDao = DeviceDao.getInstance();
	private ImageView mBottomView;
	public WL_38_Pocket_Keys( Context context, String type )
	{
		super(context, type);
	}

	@Override
	public String[] getDeviceEpResources() {
		return EP_DEVICE;
	}
	@Override
	public String[] getDeviceEpNames() {
		String ep15Name = getResources().getString(R.string.device_key_dev_bind_lock);
		return new String[]{ep15Name};
	}
	
	@Override
	public String[] getTouchEPResources() {
		return EP_TOUCH;
	}
	@Override
	public String[] getTouchEPNames() {
		String ep14Name = getResources().getString(R.string.device_key_dev_bind_left);
		String ep16Name = getResources().getString(R.string.device_key_dev_bind_right);
		return new String[]{ep14Name,ep16Name};
	}
	@Override
	public Drawable[] getStateBigPictureArray(){
		Drawable[] drawables = new Drawable[]{getResources().getDrawable(BIG_NORMAL_D)};
		return drawables;
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData){
		StringBuilder sb = new StringBuilder();
		sb.append(getResources().getString(R.string.device_type_38));
		return sb.toString();
	}
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle saveState ){
		return inflater.inflate(R.layout.device_two_state, container, false);
	}

	@Override
	public void onViewCreated( View view, Bundle saveState ){
		mBottomView = (ImageView) view.findViewById(R.id.dev_state_imageview_0);
	}

	@Override
	public void initViewStatus(){
		mBottomView.setImageDrawable(getStateBigPictureArray()[0]);
		mViewCreated = true;
	}

	@Override
	public List<DeviceInfo> getSelectDevices() {
		String[] devTypes = new String[]{ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK,ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_2,ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_3,ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_4};
		String deviceTypes = SqlUtil.convertArr2SqlArr(devTypes);
		DeviceInfo deviceInfo = new DeviceInfo();
		deviceInfo.setGwID(gwID);
		deviceInfo.setType(deviceTypes);
		List<DeviceInfo> devices = deviceDao.findListAll(deviceInfo);
		for(DeviceInfo info : devices){
			if(ConstUtil.DEV_TYPE_FROM_GW_DOORLOCK_4.equals(info.getType())){
				info.getDevEPInfo().setEpData("11");
			}else{      
				info.getDevEPInfo().setEpData("1");
			}
		}
		return devices;
	}


}
