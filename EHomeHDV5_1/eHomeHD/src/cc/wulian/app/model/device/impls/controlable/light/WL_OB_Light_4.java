package cc.wulian.app.model.device.impls.controlable.light;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.app.model.device.utils.SpannableUtil;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.tools.DeviceTool;
import cc.wulian.smarthomev5.utils.DisplayUtil;

/**
 *	0:关,1:开,255:异常
 */
@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_LIGHT_OB_4}, category = Category.C_LIGHT)
public class WL_OB_Light_4 extends ControlableDeviceImpl
{
	private boolean[] lightStates = new boolean[4];
	private static final int SMALL_STATE_BACKGROUND = R.drawable.device_button_state_background;
	private static final String DATA_CTRL_STATE_QUERY = "1p#";
	private static final String DATA_CTRL_STATE_OPEN_1 = "1111#";
	private static final String DATA_CTRL_STATE_CLOSE_0 = "0000#";
	
	public static final String DATA__ONE_OPEN = "01";
	public static final String DATA__ONE_CLOSE = "00";
	
	private static final int SMALL_OPEN_D = R.drawable.device_button_4_open;
	private static final int SMALL_CLOSE_D 	= R.drawable.device_button_4_close;
	
		
	protected LinearLayout mLightLayout;
	
	public WL_OB_Light_4( Context context, String type ){
		super(context, type);
	}

	@Override
	public String getOpenProtocol() {
		return DATA_CTRL_STATE_OPEN_1;
	}

	@Override
	public String getCloseProtocol() {
		return DATA_CTRL_STATE_CLOSE_0;
	}

	@Override
	public String getOpenSendCmd() {
		return getOpenProtocol();
	}

	@Override
	public String getCloseSendCmd() {
		return getCloseProtocol();
	}
	
	public int getOpenSmallIcon(){
		return SMALL_OPEN_D;
	}

	public int getCloseSmallIcon(){
		return SMALL_CLOSE_D;
	}

	public Drawable getStateSmallIcon() {
		List<Drawable> drawers = new ArrayList<Drawable>();
		for (boolean b : lightStates) {
			if (b) {
				drawers.add(mResources.getDrawable(getOpenSmallIcon()));
			} else {
				drawers.add(mResources.getDrawable(getCloseSmallIcon()));
			}
		}
		return DisplayUtil.getDrawablesMerge(
				drawers.toArray(new Drawable[] {}),
				mResources.getDrawable(SMALL_STATE_BACKGROUND));
	}
	@Override
	public boolean isOpened() {
		boolean isOpen = false;
		for(boolean b : lightStates){
			isOpen = isOpen || b;
		}
		return isOpen;
	}

	@Override
	public boolean isClosed() {
		return !isOpened();
	}
	
	public String[] getLightEPNames() {
		String ep14Name = DeviceUtil.ep2IndexString(EP_14)+getResources().getString(R.string.device_type_11);
		String ep15Name = DeviceUtil.ep2IndexString(EP_15)+getResources().getString(R.string.device_type_11);
		String ep16Name = DeviceUtil.ep2IndexString(EP_16)+getResources().getString(R.string.device_type_11);
		String ep17Name = DeviceUtil.ep2IndexString(EP_17)+getResources().getString(R.string.device_type_11);
		return new String[]{ep14Name,ep15Name,ep16Name,ep17Name};
	}
	
	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		String state = "";
		int color = COLOR_NORMAL_ORANGE;

		if (isOpened()) {
			state = getString(R.string.device_state_open);
			color = COLOR_CONTROL_GREEN;
		} else if (isClosed()) {
			state = getString(R.string.device_state_close);
			color = COLOR_NORMAL_ORANGE;
		}
		return SpannableUtil.makeSpannable(state, new ForegroundColorSpan(
				getColor(color)));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle saveState) {
		mLightLayout = (LinearLayout) inflater.inflate(R.layout.device_other_touch_scene, container, false);
		return mLightLayout;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		controlDevice(ep,epType,DATA_CTRL_STATE_QUERY);
	}

	@Override
	public void refreshDevice() {
		super.refreshDevice();
		if(isNull(epData)||epData.length()<	8){
			return;
		}
		int index=epData.startsWith("AA") ? 2 : 0;
        String dataTemp=epData.substring(index,epData.length());
        for(int i=0;i<lightStates.length;i++){
        	String str = dataTemp.substring(2*i, 2*(i+1));
        	if(DATA__ONE_OPEN.equals(str)){
        		lightStates[i] = true;
        	}else{
        		lightStates[i] = false;
        	}
        }	        
	}
	@Override
	public void initViewStatus() {
		super.initViewStatus();
		showView();
	}	
	
	public void showView() {
		LayoutInflater inflater = LayoutInflater.from(mContext);
		int lightSwitchLength = lightStates.length;
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
			final int index = j;
			int rowIndex = (j+rowMode) / 2;
			LinearLayout rowLineLayout = (LinearLayout) mLightLayout.getChildAt(rowIndex);
			LinearLayout itemView = (LinearLayout) inflater.inflate(R.layout.device_light_switch_chilid, null);
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT);
			lp.weight = 1;
			itemView.setGravity(Gravity.CENTER_HORIZONTAL);
			itemView.setLayoutParams(lp);

			ImageView mSwitchLight = (ImageView) itemView.findViewById(R.id.dev_light_switch_imageview);
			TextView mLightText = (TextView) itemView.findViewById(R.id.dev_light_switch_textview);
			if (lightStates[j]) {
				mSwitchLight.setImageDrawable(getResources().getDrawable(R.drawable.device_light_module_open));
			} else {
				mSwitchLight.setImageDrawable(getResources().getDrawable(R.drawable.device_light_module_close));
			}
			mLightText.setText((j + 1)+ "."+ getResources().getString(R.string.device_type_11));
			mSwitchLight.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					fireWulianDeviceRequestControlSelf();
					controlDevice(ep, epType, lightControlDataByIndex(index)+"#");
				}
			});
			rowLineLayout.addView(itemView);
		}
	}
	
	//将String数组转换成String
	private String lightControlDataByIndex(int index){
        StringBuilder result=new StringBuilder();
        for(int i=0 ;i < lightStates.length; i++){
            if(i == index){
            	if(lightStates[i]){
            		result.append(0);
            	}else{
            		result.append(1);
            	}
            }else{
            	if(lightStates[i]){
            		result.append(1);
            	}else{
            		result.append(0);
            	}
            }
        }
        return result.toString();
    }
	
	  /**
	    * 设置dialog
	    */
		@Override
		public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(LayoutInflater inflater,   AutoActionInfo autoActionInfo) {
			DialogOrActivityHolder holder = new DialogOrActivityHolder();
			LinearLayout contentView =  (LinearLayout)inflater.inflate(R.layout.task_manager_common_light_setting_view_layout, null);
			boolean states[] = new boolean[lightStates.length];
			String epData = autoActionInfo.getEpData();
			if(!isNull(epData) && epData.length() >= 5){
				String dataTemp=epData.substring(0,4);
				for(int i=0;i<states.length;i++){
					String str = dataTemp.substring(i, i+1);
					if("1".equals(str)){
						states[i] = true;
					}else{
						states[i] = false;
					}
				}	        
			}else{
				for(int i=0;i<states.length;i++){
					states[i] = false;
				}
			}
			for (int i = 0;i<states.length ;i++) {
				contentView.addView(addChildView(i,states,autoActionInfo));
			}
			holder.setShowDialog(true);
			holder.setContentView(contentView);
			holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
			return holder;
		}
		
		private View addChildView(final int index,final boolean states[] , final AutoActionInfo autoActionInfo) {
			// TODO 动态添加布局(xml方式)
			LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
			LayoutInflater inflater = LayoutInflater.from(mContext);
			View view = inflater.inflate(R.layout.task_manager_ob_light_setting_view, null);
			
			final TextView deviceNameTextView = (TextView) view.findViewById(R.id.device_common_light_setting_dev_name);
			final FrameLayout switchOpenView = (FrameLayout) view.findViewById(R.id.device_common_light_setting_switch_open);
			final FrameLayout switchCloseView = (FrameLayout) view.findViewById(R.id.device_common_light_setting_switch_close);
//			final ImageView switchStatusAvailableView = (ImageView) view.findViewById(R.id.device_common_light_setting_switch_status_available);
//			switchStatusAvailableView.setVisibility(View.GONE);
			if(states[index]){
				switchOpenView.setVisibility(View.VISIBLE);
				switchCloseView.setVisibility(View.GONE);
			}else{
				switchOpenView.setVisibility(View.GONE);
				switchCloseView.setVisibility(View.VISIBLE);
			}
			setautoActionInfo(autoActionInfo,states);
			deviceNameTextView.setText((index + 1)+ "."+ getResources().getString(R.string.device_type_11));
			switchOpenView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					switchOpenView.setVisibility(View.GONE);
					switchCloseView.setVisibility(View.VISIBLE);
					states[index] = false;
					setautoActionInfo(autoActionInfo,states);
				}
			});
			switchCloseView.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					switchOpenView.setVisibility(View.VISIBLE);
					switchCloseView.setVisibility(View.GONE);
					states[index] = true;
					setautoActionInfo(autoActionInfo,states);
				}
				
			});
			view.setLayoutParams(lp);
			return view;
		}

		private void setautoActionInfo(AutoActionInfo autoActionInfo,boolean[] states) {
			String result = "";
			for(boolean b : states){
				if(b)
					result += "1";
				else
					result += "0";
			}
			autoActionInfo.setEpData(result+"#");
			autoActionInfo.setObject(getDeviceID() + ">" + getDeviceType() + ">" + EP_14 + ">" + getDeviceType());
		}
}
