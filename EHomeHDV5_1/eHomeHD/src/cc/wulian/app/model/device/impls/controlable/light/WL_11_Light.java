package cc.wulian.app.model.device.impls.controlable.light;

import android.content.Context;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.AbstractSwitchDevice;
import cc.wulian.ihome.wan.util.ConstUtil;

/**
 *	0:关,100:开
 */
@Deprecated
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_LIGHT}, 
		category = Category.C_LIGHT)
public class WL_11_Light extends AbstractSwitchDevice
{
	private static final String DATA_CTRL_STATE_OPEN_100 	= "100";
	private static final String DATA_CTRL_STATE_CLOSE_0 	= "0";
	
	private static final int 		SMALL_OPEN_D 							= R.drawable.device_light_open;
	private static final int 		SMALL_CLOSE_D 						= R.drawable.device_light_close;

	private static final int 		BIG_OPEN_D 								= R.drawable.device_button_1_open_big;
	private static final int 		BIG_CLOSE_D 							= R.drawable.device_button_1_close_big;
	
	public WL_11_Light( Context context, String type )
	{
		super(context, type);
	}

	@Override
	public int getOpenSmallIcon(){
		return SMALL_OPEN_D;
	}

	@Override
	public int getCloseSmallIcon(){
		return SMALL_CLOSE_D;
	}

	@Override
	public int getOpenBigPic(){
		return BIG_OPEN_D;
	}

	@Override
	public int getCloseBigPic(){
		return BIG_CLOSE_D;
	}

	@Override
	public String getOpenSendCmd(){
		return DATA_CTRL_STATE_OPEN_100;
	}

	@Override
	public String getCloseSendCmd(){
		return DATA_CTRL_STATE_CLOSE_0;
	}
}