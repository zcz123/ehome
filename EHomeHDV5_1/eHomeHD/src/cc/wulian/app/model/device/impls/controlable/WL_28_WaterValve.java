package cc.wulian.app.model.device.impls.controlable;

import java.util.Calendar;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;

import com.yuantuo.customview.wheel.WL_28_WaterValue_TimePickerDialog;
import com.yuantuo.customview.wheel.WL_28_WaterValue_TimePickerDialog.OnDateTimeSetListener;

/**
 * 11:开, 10:关 , 2TTTT:开TTTT秒后关闭(0000-7200)
 */
@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_WATER_VALVE}, category = Category.C_OTHER)
public class WL_28_WaterValve extends AbstractSwitchDevice
{
	private static final String DEVICE_STATE_2 = "2";
	private static final String DATA_CTRL_STATE_OPEN_11 = "11";
	private static final String DATA_CTRL_STATE_CLOSE_10 = "10";

	private static final int SMALL_OPEN_D = R.drawable.device_water_valve_open;
	private static final int SMALL_CLOSE_D = R.drawable.device_water_valve_close;

	private static final int BIG_OPEN_D = R.drawable.device_water_valve_open_big;
	private static final int BIG_CLOSE_D = R.drawable.device_water_valve_close_big;
	private ImageView mBottomView;
	private TextView mDevStateView;
    private ImageView mClock;
    private Calendar calendarSet;
	public WL_28_WaterValve( Context context, String type )
	{
		super(context, type);
	}

	@Override
	public String getOpenSendCmd() {
		return DATA_CTRL_STATE_OPEN_11;
	}

	@Override
	public String getCloseSendCmd() {
		return DATA_CTRL_STATE_CLOSE_10;
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
	public boolean isOpened() {
		return isSameAs(getOpenProtocol(), epData) || isOpenSomeTime();
	}

	private boolean isOpenSomeTime() {
		if (isNull(epData)) return false;

		return epData.startsWith(DEVICE_STATE_2);
	}

	// temp not support control with special data
	// @Override
	// public String controlDevice( String sendData ){
	// // if not null or not same as open(close) cmd, add prefix
	// if(!isNull(sendData)
	// && !isSameAs(getOpenSendCmd(), sendData)
	// && !isSameAs(getCloseSendCmd(), sendData)){
	// sendData = DEVICE_STATE_2 + sendData;
	// }
	// return super.controlDevice(sendData);
	// }

	
	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle saveState ) {
		return inflater.inflate(R.layout.device_clock_to_choose_time, container, false);
	}
	@Override
	public void onViewCreated( View view, Bundle saveState ) {
		mBottomView = (ImageView) view.findViewById(R.id.dev_state_imageview_0);
		mDevStateView = (TextView) view.findViewById(R.id.dev_state_textview_0);
		mClock= (ImageView) view.findViewById(R.id.mybut_watervalue);
		mClock.setOnClickListener(new OnClickListenerIMPL());
		calendarSet = Calendar.getInstance();
		calendarSet.set(2014, 0, 1, 0, 00, 00);
        mBottomView.setOnClickListener(new OnClickListener() {
		
			@Override
			public void onClick(View v) {
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, null,true);
			}
		});
        mViewCreated = true;
	}

	/* 添加BUTTTON按钮显示时间dialog**/
	 
	@Override
	public void initViewStatus() {
		Drawable[] drawables = getStateBigPictureArray();
		mBottomView.setImageDrawable(drawables[0]);

		if (isOpenSomeTime() && epData!=null && epData.length()>1) {
			mDevStateView.setText(StringUtil.toInteger(epData.substring(1)) + "s");
		}
		else if (isClosed()) {
			mDevStateView.setText(0+ "s");
		}
	}

	 private class OnClickListenerIMPL implements OnClickListener{
		  @Override
		  public void onClick(View v){
					String time = mDevStateView.getText().toString();
					if (!TextUtils.isEmpty(time)) {
						calendarSet.set(2014,
								00,
								00,
								0,
								0,
								00);
					}
					 new WL_28_WaterValue_TimePickerDialog(mContext,
							calendarSet, new OnDateTimeSetListener() {

								@Override
								public void onDateTimeSet( int hour,
										int minute,int second) {										
//									mDevStateView.setText( hour + "时"
//											+ minute + "分"+second+"秒");
									mDevStateView.setText( hour + mContext.getString(R.string.device_adjust_hour_common)
											+ minute + mContext.getString(R.string.device_adjust_minutes_common) + second+ mContext.getString(R.string.device_adjust_second_common));
									int data = hour*3600+minute*60+second;
									String s = String.format("%04d", data);
				    createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, DEVICE_STATE_2 + s,true);
								}
							}).show();
				}
	  }
}