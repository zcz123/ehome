package cc.wulian.app.model.device.impls.controlable.light;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.Controlable;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;

/**
 *	0:关,1:开,255:异常
 */
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_LIGHT_3}, 
		category = Category.C_LIGHT)
public class WL_63_Light_3 extends WL_62_Light_2
{
	
	private static final int SMALL_OPEN_D 								= R.drawable.device_button_3_open;
	private static final int SMALL_CLOSE_D 								= R.drawable.device_button_3_close;

	private static final String[] EP_SEQUENCE = {EP_14, EP_15, EP_16};
	
	public WL_63_Light_3( Context context, String type )
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
	public String[] getLightEPResources() {
		return EP_SEQUENCE;
	}

	@Override
	public String[] getLightEPNames() {
		String ep14Name = DeviceUtil.ep2IndexString(EP_14)+getResources().getString(R.string.device_type_11);
		String ep15Name = DeviceUtil.ep2IndexString(EP_15)+getResources().getString(R.string.device_type_11);
		String ep16Name = DeviceUtil.ep2IndexString(EP_16)+getResources().getString(R.string.device_type_11);
		return new String[]{ep14Name,ep15Name,ep16Name};
	}

	public void showView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		int lightSwitchLength = getLightEPResources().length;
		int accountRow = (lightSwitchLength + 1) / 2;
		mLightLayout.removeAllViews();

		for (int i = 0; i < accountRow; i++) {
			LinearLayout rowLinearLayout = new LinearLayout(mContext);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT, 0);
			lp.weight = 1;
			rowLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
			rowLinearLayout.setGravity(Gravity.CENTER);
			rowLinearLayout.setLayoutParams(lp);
			mLightLayout.addView(rowLinearLayout);
		}
		int rowMode = lightSwitchLength%2;
		for (int j = 0; j < lightSwitchLength; j++) {
			final String ep = getLightEPResources()[j];
			int rowIndex = (j+rowMode) / 2;
			LinearLayout rowLineLayout = (LinearLayout) mLightLayout
					.getChildAt(rowIndex);
			LinearLayout itemView = (LinearLayout) inflater.inflate(
					R.layout.device_light_switch_chilid, null);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
					LinearLayout.LayoutParams.MATCH_PARENT,
					LinearLayout.LayoutParams.MATCH_PARENT);
			lp.weight = 1;
			itemView.setGravity(Gravity.CENTER_HORIZONTAL);
			itemView.setLayoutParams(lp);

			ImageView mSwitchLight = (ImageView) itemView
					.findViewById(R.id.dev_light_switch_imageview);
			TextView mLightText = (TextView) itemView
					.findViewById(R.id.dev_light_switch_textview);
			if(getChildDevice(ep) != null){
				final WulianDevice device = getChildDevice(ep);
				if (device instanceof Controlable) {
					Controlable controlable = (Controlable) device;
					String epName = device.getDeviceInfo().getDevEPInfo()
							.getEpName();
					if (controlable.isOpened()) {
						mSwitchLight.setImageDrawable(getResources().getDrawable(
								R.drawable.device_light_module_open));
					} else {
						mSwitchLight.setImageDrawable(getResources().getDrawable(
								R.drawable.device_light_module_close));
					}
					if (!StringUtil.isNullOrEmpty(epName)) {
						mLightText.setText((j + 1) + "." + epName);
					} else {
						mLightText
								.setText((j + 1)
										+ "."
										+ getResources().getString(
												R.string.device_type_11));
					}
					mSwitchLight.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							fireWulianDeviceRequestControlSelf();
							controlDevice(ep, device.getDeviceInfo().getDevEPInfo().getEpType(), null);
						}
					});
				}
			}else{
				mSwitchLight.setImageDrawable(getResources().getDrawable(
						R.drawable.device_light_module_close));
				mLightText
				.setText((j + 1)
						+ "."
						+ getResources().getString(
								R.string.device_type_11));
			}
			
			rowLineLayout.addView(itemView);
		}
	}
}
