package cc.wulian.app.model.device.impls.controlable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.utils.SpannableUtil;
import cc.wulian.app.model.device.view.CurtainBlind;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.ConstUtil;

/**
 * 1:停,2:开,3:关,4:单开,5:单关 <br/>
 * 
 * <b>Chang Log</b> <br/>
 * 1.大图显示默认位置改为关
 */
@DeviceClassify(
		devTypes = {ConstUtil.DEV_TYPE_FROM_GW_BLIND}, 
		category = Category.C_CONTROL)
public class WL_66_Blind extends ControlableDeviceImpl
{	
	private static final String DATA_CTRL_STATE_STOP_1 				= "1";
	private static final String DATA_CTRL_STATE_OPEN_2 				= "2";
	private static final String DATA_CTRL_STATE_CLOSE_3 			= "3";
	@SuppressWarnings({"unused"})
	private static final String DATA_CTRL_STATE_CLOSE_STEP_4 	= "4";
	@SuppressWarnings({"unused"})
	private static final String DATA_CTRL_STATE_CLOSE_STEP_5 	= "5";
	
	// Mark: 
	// The default resource value 
	// later need to make changes according to the catogrey value
	// wait··· does it can change catogrey?? e,e,e···
	private static final int 		SMALL_OPEN_D 									= R.drawable.device_blind_open;
	private static final int 		SMALL_CLOSE_D 								= R.drawable.device_blind_close;
	private static final int 		SMALL_STOP_D 									= R.drawable.device_blind_stop;
	private CurtainBlind mCurtainBlind;
	private TextView mPercentView;
	
	private View mOpenView;
	private View mStopView;
	private View mCloseView;
	private OnClickListener clickListener = new OnClickListener(){
		@Override
		public void onClick( View v ){
			String sendData = null;
			if (v == mOpenView){
				sendData = getOpenSendCmd();
			}
			else if (v == mStopView){
				sendData = getStopSendCmd();
			}
			else if (v == mCloseView){
				sendData = getCloseSendCmd();
			}
			createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, sendData,true);
		}
	};
	

	private final CurtainBlind.SimpleCurtainScrollListener mCurtainScrollListener = new CurtainBlind.SimpleCurtainScrollListener()
	{
		@Override
		public void onScroll( int percent, boolean scrollUp ){
			mPercentView.setText(String.format("%s%%", String.valueOf(percent)));
		}

		@Override
		public void onScrollOver( int percent, boolean scrollUp, boolean fromUser ){
			if (fromUser){
				String cmd = percent > 50 ? getOpenSendCmd() : 
										 percent == 50 ? getStopSendCmd() : 
										 getCloseSendCmd();
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, cmd,true);
			}
		}
	};
	public WL_66_Blind( Context context, String type )
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
	public boolean isOpened(){
		return isSameAs(DATA_CTRL_STATE_OPEN_2, epData);
	}

	@Override
	public boolean isClosed(){
		return isSameAs(DATA_CTRL_STATE_CLOSE_3, epData);
	}

	@Override
	public boolean isStoped(){
		return isSameAs(DATA_CTRL_STATE_STOP_1, epData);
	}

	@Override
	public String getStopSendCmd(){
		return DATA_CTRL_STATE_STOP_1;
	}

	@Override
	public String getStopProtocol(){
		return getStopSendCmd();
	}
	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item,LayoutInflater inflater){
		if(item == null){
			item = new ControlableDeviceShortCutControlItem(inflater.getContext());
		}
		((ControlableDeviceShortCutControlItem)item).setStopVisiable(true);
		item.setWulianDevice(this);
		return item;
	}
	
	@Override
	public DeviceShortCutSelectDataItem onCreateShortCutSelectDataView(
			DeviceShortCutSelectDataItem item, LayoutInflater inflater,
			AutoActionInfo autoActionInfo) {
		if(item == null){
			item = new ShortCutControlableDeviceSelectDataItem(inflater.getContext());
		}
		((ShortCutControlableDeviceSelectDataItem) item).setStopVisiable(true);
		item.setWulianDeviceAndSelectData(this, autoActionInfo);
		return item;
	}
	@Override
	public Drawable getStateSmallIcon(){
		return isStoped() ? getDrawable(SMALL_STOP_D) : 
					 isOpened() ? getDrawable(SMALL_OPEN_D) : 
					 isClosed() ? getDrawable(SMALL_CLOSE_D) :
					 getDrawable(SMALL_CLOSE_D);
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData){
		String state = "";
		int color = COLOR_NORMAL_ORANGE;
		
		if (isStoped()){
			state = getString(R.string.device_state_stop);
			color = COLOR_NORMAL_ORANGE;
		}
		else if (isOpened()){
			state = getString(R.string.device_state_open);
			color = COLOR_CONTROL_GREEN;
		}
		else if(isClosed()){
			state = getString(R.string.device_state_close);
			color = COLOR_NORMAL_ORANGE;
		}
		return SpannableUtil.makeSpannable(state, new ForegroundColorSpan(getResources().getColor(color)));
	}
		
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle saveState ){
		return inflater.inflate(R.layout.device_blind, container, false);
	}

	@Override
	public void onViewCreated( View view, Bundle saveState ){
		super.onViewCreated(view, saveState);
		
		mCurtainBlind = (CurtainBlind) view.findViewById(R.id.dev_state_blind_0);
		mCurtainBlind.setBlindCanScroll(true);
		mCurtainBlind.setOnCurtainScrollListener(mCurtainScrollListener);

		mPercentView = (TextView) view.findViewById(R.id.dev_state_textview_0);
		
		mOpenView = view.findViewById(R.id.dev_state_button_0);
		mStopView = view.findViewById(R.id.dev_state_button_1);
		mCloseView = view.findViewById(R.id.dev_state_button_2);

		mOpenView.setOnClickListener(clickListener);
		mStopView.setOnClickListener(clickListener);
		mCloseView.setOnClickListener(clickListener);
	}

	@Override
	public void initViewStatus(){
		super.initViewStatus();
		float percent = isStoped() ? 50F : isOpened() ? 100F : isClosed() ? 0F : 0F;
		mCurtainBlind.setCurrentPercent(percent);
	}
}
