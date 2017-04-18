package cc.wulian.app.model.device.impls.controlable.newthermostat.setting.equipment;

import android.content.Context;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.fragment.setting.AbstractSettingItem;

public class SendEquipmentItem extends AbstractSettingItem{
	
	private Button sendBtn;
	private SendClickListener sendClickListener;

	public SendEquipmentItem(Context context) {
	//	super(context, R.drawable.icon_gateway_id, context.getResources().getString(R.string.set_account_manager_gw_ID));
		super(context, R.drawable.icon_gateway_id, "sendEquipment");
	}

	@Override
	public void initSystemState() {
		super.initSystemState();
		setSendEquipment();
	}

	public void setSendClickListener(SendClickListener sendClickListener) {
		this.sendClickListener = sendClickListener;
	}

	public void setSendEquipment() {
		view.setBackgroundColor(mContext.getResources().getColor(R.color.trant));
		upLinearLayout.removeAllViews();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
		params.setMargins(0, 0,0, 0);
		upLinearLayout.setLayoutParams(params);
		LinearLayout.LayoutParams sendParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		sendParams.setMargins(0, 0,0, 0);
		sendBtn = new Button(mContext);
		sendBtn.setLayoutParams(sendParams);
		sendBtn.setGravity(Gravity.CENTER);
		sendBtn.setText("Send all equipment settings to the thermostat?");
		sendBtn.setTextSize(14);
		sendBtn.setBackgroundResource(R.drawable.thermost_setting_send_btn_selector);
		upLinearLayout.addView(sendBtn);
		
		sendBtn.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				switch (arg1.getAction()) {
				case MotionEvent.ACTION_DOWN:
					sendBtn.setBackgroundResource(R.drawable.thermost_setting_send_btn_selector2);
					return true;
				case MotionEvent.ACTION_MOVE:
					return false;
				case MotionEvent.ACTION_UP:
					sendBtn.setBackgroundResource(R.drawable.thermost_setting_send_btn_selector);
					sendClickListener.onSendClick();
					return true;
				}
				return false;
			}
		});
		
		
//		sendBtn.setOnClickListener(new OnClickListener() {
//			
//			@Override
//			public void onClick(View arg0) {
//				
//				sendClickListener.onSendClick();
//			}
//		});
	}
	
	public interface SendClickListener{
		public void onSendClick();
	}

	@Override
	public void doSomethingAboutSystem() {
		
	}
}
