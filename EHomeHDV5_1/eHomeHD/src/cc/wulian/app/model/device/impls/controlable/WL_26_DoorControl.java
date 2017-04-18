package cc.wulian.app.model.device.impls.controlable;

import android.content.Context;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;

/**
 * 1:RESET,2:常开,3:常关,4:开
 */
@Deprecated
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_DOOR_CONTROL}, 
		category = Category.C_OTHER)
public class WL_26_DoorControl extends AbstractSwitchDevice
{
	private static final String DATA_CTRL_STATE_OPEN_2 		= "2";
	private static final String DATA_CTRL_STATE_CLOSE_3 	= "3";
	
	private static final int 		SMALL_OPEN_D 							= R.drawable.device_door_ctrl_open;
	private static final int 		SMALL_CLOSE_D 						= R.drawable.device_door_ctrl_close;

	private static final int 		BIG_OPEN_D 								= R.drawable.device_door_ctrl_open_big;
	private static final int 		BIG_CLOSE_D 							= R.drawable.device_door_ctrl_close_big;
	
	public WL_26_DoorControl( Context context, String type )
	{
		super(context, type);
	}

	@Override
	public String getOpenSendCmd(){
		return DATA_CTRL_STATE_OPEN_2;
	}

	@Override
	public String getCloseSendCmd(){
		return DATA_CTRL_STATE_CLOSE_3;
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
}
