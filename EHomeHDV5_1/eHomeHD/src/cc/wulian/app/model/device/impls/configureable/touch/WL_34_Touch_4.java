package cc.wulian.app.model.device.impls.configureable.touch;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.util.ConstUtil;

@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_TOUCH_4}, 
		category = Category.C_OTHER)
public class WL_34_Touch_4 extends AbstractTouchDevice
{
	private static final String[] EP_SEQUENCE = {EP_14, EP_15, EP_16, EP_17};
	
	private static final int BIG_NORMAL_D = R.drawable.device_bind_scene_normal_4_big;
	private ImageView mBottomView;
	public WL_34_Touch_4( Context context, String type )
	{
		super(context, type);
	}
	@Override
	public String[] getTouchEPResources() {
		return EP_SEQUENCE;
	}
	@Override
	public String[] getTouchEPNames() {
		String ep14Name = DeviceUtil.ep2IndexString(EP_14)+getResources().getString(R.string.device_key_scene_bind);
		String ep15Name = DeviceUtil.ep2IndexString(EP_15)+getResources().getString(R.string.device_key_scene_bind);
		String ep16Name = DeviceUtil.ep2IndexString(EP_16)+getResources().getString(R.string.device_key_scene_bind);
		String ep17Name = DeviceUtil.ep2IndexString(EP_17)+getResources().getString(R.string.device_key_scene_bind);
		return new String[]{ep14Name,ep15Name,ep16Name,ep17Name};
	}
	@Override
	public Drawable[] getStateBigPictureArray(){
		Drawable[] drawables = new Drawable[]{getResources().getDrawable(BIG_NORMAL_D)};
		return drawables;
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData){
		StringBuilder sb = new StringBuilder();
		sb.append(getResources().getString(R.string.device_type_34));
		return sb.toString();
	}

}
