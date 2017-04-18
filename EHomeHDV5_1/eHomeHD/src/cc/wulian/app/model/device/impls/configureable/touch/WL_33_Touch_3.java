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
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_TOUCH_3}, 
		category = Category.C_OTHER)
public class WL_33_Touch_3 extends AbstractTouchDevice
{
	private static final String[] EP_SEQUENCE = {EP_14, EP_15, EP_16};

	private static final int BIG_NORMAL_D  = R.drawable.device_bind_scene_normal_3_big;
	private ImageView mBottomView;
	public WL_33_Touch_3( Context context, String type )
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
		return new String[]{ep14Name,ep15Name,ep16Name};
	}

	@Override
	public Drawable[] getStateBigPictureArray(){
		Drawable[] drawables = new Drawable[]{getResources().getDrawable(BIG_NORMAL_D)};
		return drawables;
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData){
		StringBuilder sb = new StringBuilder();
		sb.append(getResources().getString(R.string.device_type_33));
		return sb.toString();
	}
}
