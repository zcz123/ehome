package cc.wulian.app.model.device.impls.controlable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;
 @DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_MECHANICAL_ARM}, category = Category.C_CONTROL)
public class WL_25_MechanicalArm extends ControlableDeviceImpl{

	private String callback;
	 
	private  ImageView stateBigView;
	private  LinearLayout error_notes_linearlayout;
	private  ImageView error_notes_imageview;
	private  TextView error_notes_textview;
	
	private boolean isOpen = false;
	private static final String  SEND_STOP= "1";
	private static final String  SEND_OPEN= "2";
	private static final String  SEND_CLOSE= "3";
	 
	private static final String  INSIGNIFICANCE= "00";//无意义
	private static final String  DATA_CTRL_STATE_STOP = "01";//停
	private static final String  DATA_CTRL_STATE_OPEN = "02";//开
	private static final String  DATA_CTRL_STATE_CLOSE = "03";//关
	private static final String  DATA_CTRL_STATE_OPEN_TIMEOUT = "04";//未达到开位置
	private static final String  DATA_CTRL_STATE_CLOSE_TIMEOUT = "05";//未达到关位置
	
	private static final int UNKNOW = R.drawable.device_mechanicalarm_small_isf;
	private static final int SMALL_OPEN_D = R.drawable.device_mechanicalarm_small_open;
	private static final int SMALL_CLOSE_D = R.drawable.device_mechanicalarm_small_close;
	private static final int SMALL_STOP_D = R.drawable.device_mechanicalarm_small_nclose;
	private static final int SMALL_NOTCOPEN_D = R.drawable.device_mechanicalarm_small_nopen;
	private static final int SMALL_NOTCCLOSE_D = R.drawable.device_mechanicalarm_small_nclose;
	
	public WL_25_MechanicalArm(Context context, String type) {
		super(context, type);
	}

	
	@Override
	public String getOpenSendCmd() {
		return SEND_OPEN;
	}

	@Override
	public String getCloseSendCmd() {
		return SEND_CLOSE;
	}

	@Override
	public String getOpenProtocol() {
		return getOpenSendCmd();
	}


	@Override
	public String getCloseProtocol() {
		return getCloseSendCmd();
	}


	@Override
	public boolean isOpened() {
		if (isNull(epData)) return false;
		String ctrlState ="-1" ;
		if (epData.length() >= 2) {
			 ctrlState = epData;
		}
		return DATA_CTRL_STATE_OPEN.equals(ctrlState);
	}

	@Override
	public boolean isClosed() {
		if (isNull(epData)) return false;
		String ctrlState ="-1" ;
		if (epData.length() >= 2) {
			 ctrlState = epData;
		}
		return DATA_CTRL_STATE_CLOSE.equals(ctrlState);
	}
	
	@Override
	public Drawable getStateSmallIcon() {
		callback = epData;
		
		return 
				(callback.equals("02")) ? getDrawable(SMALL_OPEN_D)
			     :(callback.equals("03")) ? getDrawable(SMALL_CLOSE_D)
				: (callback.equals("04"))? getDrawable(SMALL_NOTCOPEN_D)
				: (callback.equals("05"))? getDrawable(SMALL_NOTCCLOSE_D)
						:getDrawable(UNKNOW);
	}
		
		@Override
		public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle saveState ) {
			return inflater.inflate(R.layout.mechanical_arm, container, false);
		}
		@Override
		public void onViewCreated( View view, Bundle saveState ) {
			super.onViewCreated(view, saveState);
			
		stateBigView = (ImageView) view.findViewById(R.id.statebigicon);
		error_notes_linearlayout = (LinearLayout) view.findViewById(R.id.error_notes_linearlayout);
		error_notes_imageview = (ImageView) view.findViewById(R.id.error_notes_imageview);
		error_notes_textview = (TextView) view.findViewById(R.id.error_notes_textview);
		
		
		
		stateBigView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String sendData = null;
	            if(isOpen){
	            	sendData = 3+"";
	            }
	            else{
	            	sendData = 2+"";
	            }
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, sendData, true);
			}
		});
		}
		@Override
		public void initViewStatus() {
			super.initViewStatus();
			callback = epData;
			
			if(callback.equals(INSIGNIFICANCE)) {
				isOpen = false;
				stateBigView.setImageResource(R.drawable.device_mechanicalarm_big_nopen);
				showErrorNotes(getResources().getString(R.string.device_mechanicalarm_unknown_state));
			} 
			else if(callback.equals(DATA_CTRL_STATE_STOP)) {
				isOpen = false;
				stateBigView.setImageResource(R.drawable.device_mechanicalarm_big_nopen);
				showErrorNotes(getResources().getString(R.string.device_mechanicalarm_unknown_state));
			} 
			else if(callback.equals(DATA_CTRL_STATE_OPEN)) {
				isOpen = true;
				stateBigView.setImageResource(R.drawable.device_mechanicalarm_big_open);
				error_notes_linearlayout.setVisibility(View.INVISIBLE);
			}
			else if(callback.equals(DATA_CTRL_STATE_CLOSE)){
				isOpen = false;
				stateBigView.setImageResource(R.drawable.device_mechanicalarm_big_close);
				error_notes_linearlayout.setVisibility(View.INVISIBLE);
			}
			else if(callback.equals(DATA_CTRL_STATE_OPEN_TIMEOUT)){
				isOpen = false;
				stateBigView.setImageResource(R.drawable.device_mechanicalarm_big_nclose);
				showErrorNotes(getResources().getString(R.string.device_mechanicalarm_not_reach_the_open_position));
			}
			else if(callback.equals(DATA_CTRL_STATE_CLOSE_TIMEOUT)){
				isOpen = false;
				stateBigView.setImageResource(R.drawable.device_mechanicalarm_big_nopen);
				showErrorNotes(getResources().getString(R.string.device_mechanicalarm_not_reach_the_close_position));
			}
			else{
				isOpen = false;
				stateBigView.setImageResource(R.drawable.device_mechanicalarm_big_nopen);
				showErrorNotes(getResources().getString(R.string.device_mechanicalarm_unknown_state));
			}
		}
		
		

	public void showErrorNotes(String str) {
		error_notes_linearlayout.setVisibility(View.VISIBLE);
		error_notes_textview.setText(str);
	}
}
