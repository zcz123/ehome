package cc.wulian.app.model.device.impls.controlable.light;

import java.util.Map;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.WulianDevice;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.AbstractSwitchDevice;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.Logger;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.tools.DeviceTool;

/**
 * 0:关,1:开,255:异常
 */
@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_LIGHT_1}, category = Category.C_LIGHT)
public class WL_61_Light_1 extends AbstractSwitchDevice
{
	private static final String DATA_CTRL_STATE_OPEN_1 = "1";
	private static final String DATA_CTRL_STATE_CLOSE_0 = "0";
	private static final String SPLIT_SYMBOL = ">";

	private int SMALL_OPEN_D = R.drawable.device_button_1_open;
	private int SMALL_CLOSE_D = R.drawable.device_button_1_close;
	private int BIG_OPEN_D = R.drawable.device_light_module_open;
	private int BIG_CLOSE_D = R.drawable.device_light_module_close;
	private LinearLayout mLinearLayout ;
	private LinearLayout mLightLayout;
	private String mSwitchStatus;
	private Map<String,Map<Integer,Integer>> categoryIcons = DeviceUtil.getLightCategoryDrawable();
	public WL_61_Light_1( Context context, String type )
	{
		super(context, type);
	}

	@Override
	public String getOpenSendCmd() {
		return DATA_CTRL_STATE_OPEN_1;
	}

	@Override
	public String getCloseSendCmd() {
		return DATA_CTRL_STATE_CLOSE_0;
	}

	@Override
	public int getOpenSmallIcon() {
		return SMALL_OPEN_D;
	}

	@Override
	public int getCloseSmallIcon() {
		return SMALL_CLOSE_D;
	}

	@Override
	public int getOpenBigPic() {
		return BIG_OPEN_D;
	}

	@Override
	public int getCloseBigPic() {
		return BIG_CLOSE_D;
	}

	@Override
	public void setResourceByCategory( ) {
		Map<Integer, Integer> shadekMap = categoryIcons.get(getDeviceCategory());
		if(shadekMap != null && shadekMap.size() >=4){
			SMALL_OPEN_D = shadekMap.get(0);
			SMALL_CLOSE_D = shadekMap.get(1);
		}
	}
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater,   AutoActionInfo autoActionInfo) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		View contentView =  inflater.inflate(R.layout.task_manager_common_light_setting_view_layout, null);
		mLinearLayout =  (LinearLayout) contentView.findViewById(R.id.task_manager_common_light_setting_view_layout);
		mLinearLayout.addView(addChildView(autoActionInfo));
		holder.setShowDialog(true);
		holder.setContentView(contentView);
		holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}
	
	private View addChildView(final AutoActionInfo autoActionInfo) {
		// TODO 动态添加布局(xml方式)
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		LayoutInflater inflater = LayoutInflater.from(mContext);
		View view = inflater.inflate(R.layout.task_manager_common_light_setting_view, null);
		
		final TextView dev_name = (TextView) view.findViewById(R.id.device_common_light_setting_dev_name);
		final ImageView switch_status_button_on = (ImageView) view.findViewById(R.id.task_manager_common_light_button_on);
		final ImageView switch_status_button_off = (ImageView) view.findViewById(R.id.task_manager_common_light_button_off);
		final ImageView switch_status_button_convert = (ImageView) view.findViewById(R.id.task_manager_common_light_button_convert);
		final ImageView switch_status_button_unchange = (ImageView) view.findViewById(R.id.task_manager_common_light_button_unchange);
		
		dev_name.setVisibility(View.INVISIBLE);
		final String[] type = autoActionInfo.getObject().split(SPLIT_SYMBOL);
		mSwitchStatus = "";
		if (!StringUtil.isNullOrEmpty(autoActionInfo.getEpData())) {
			mSwitchStatus = autoActionInfo.getEpData();
			if (mSwitchStatus.equals("2")) {//不变。。默认为不变
				switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_selected);
				
			} else if (mSwitchStatus.equals("1")) {
				switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_selected);
				switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_unselected);
			} else if (mSwitchStatus.equals("0")) {
				switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_selected);
				switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_unselected);
			}else if (mSwitchStatus.equals("3")) {
				switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_selected);
				switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_unselected);
			}
		} else {
			// mLinearLayout.getChildAt(i).findViewById(R.id.device_common_light_setting_switch_open).setVisibility(View.GONE);
			// mLinearLayout.getChildAt(i).findViewById(R.id.device_common_light_setting_switch_close).setVisibility(View.VISIBLE);
			switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_unselected);
			switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_unselected);
			switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_unselected);
			switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_selected);
			mSwitchStatus = "2";
			autoActionInfo.setEpData(mSwitchStatus);
			autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL
					+ getDeviceType() + SPLIT_SYMBOL + EP_14 + SPLIT_SYMBOL
					+ getDeviceType());
		}
	
		switch_status_button_on.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_selected);
					switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_unselected);
					mSwitchStatus = "1";
					setautoActionInfo(autoActionInfo,mSwitchStatus);
				}
			});
		switch_status_button_off.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_selected);
				switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				mSwitchStatus = "0";
				setautoActionInfo(autoActionInfo,mSwitchStatus);
			}
		});
		switch_status_button_unchange.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_selected);
				mSwitchStatus = "2";
				setautoActionInfo(autoActionInfo,mSwitchStatus);
			}
		});
		switch_status_button_convert.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				switch_status_button_on.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_off.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				switch_status_button_convert.setImageResource(R.drawable.task_manager_common_light_button_selected);
				switch_status_button_unchange.setImageResource(R.drawable.task_manager_common_light_button_unselected);
				mSwitchStatus = "3";
				setautoActionInfo(autoActionInfo,mSwitchStatus);
			}
		});
		view.setLayoutParams(lp);
		return view;
	   }
	
	private void setautoActionInfo(AutoActionInfo autoActionInfo,String selectData) {
			autoActionInfo.setEpData(selectData);
			autoActionInfo.setObject(getDeviceID() + SPLIT_SYMBOL
					+ getDeviceType() + SPLIT_SYMBOL + EP_14 + SPLIT_SYMBOL
					+ getDeviceType());
//		}
	}
}