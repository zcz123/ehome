package cc.wulian.app.model.device.impls.controlable.dimmerlight;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
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

import com.yuantuo.customview.seekcircle.SeekCircle;
import com.yuantuo.customview.seekcircle.SeekCircle.OnSeekCircleChangeListener;

/**
 * 0:关,100:开,1～99:亮度
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_D_LIGHT }, category = Category.C_LIGHT)
public class WL_12_D_Light extends ControlableDeviceImpl{
	private static final String DATA_CTRL_STATE_OPEN_100 = "100";
	private static final String DATA_CTRL_STATE_CLOSE_0 = "0";

	private static final int SMALL_OPEN_D = R.drawable.device_d_light_open;
	private static final int SMALL_CLOSE_D = R.drawable.device_d_light_close;

	private SeekCircle mSeekBar;
	private TextView mChildText;
	private TextView mDevStateView0;
	private TextView mDevStateView1;
	private TextView mDevStateView2;
	public WL_12_D_Light(Context context, String type) {
		super(context, type);
	}

	@Override
	public String getOpenProtocol() {
		// no used for d light
		return DATA_CTRL_STATE_OPEN_100;
	}

	@Override
	public String getCloseProtocol() {
		return DATA_CTRL_STATE_CLOSE_0;
	}

	public int getOpenSmallIcon() {
		return SMALL_OPEN_D;
	}

	public int getCloseSmallIcon() {
		return SMALL_CLOSE_D;
	}

	/*
	 * how d-light express it's open state? data in 1~100 always means opened,
	 * when data [not null] and [not close state] always can be express opened
	 * state
	 */
	@Override
	public boolean isOpened() {
		// d light has 1~99 means open
		return !isNull(epData) && !isClosed();
	}

	// just for watch code easy
	// 12 0 closed
	// 13 000 closed
	@Override
	public boolean isClosed() {
		int data = StringUtil.toInteger(epData);
		return StringUtil.toInteger(DATA_CTRL_STATE_CLOSE_0) == data;
	}

	@Override
	public String getOpenSendCmd() {
		return DATA_CTRL_STATE_OPEN_100;
	}

	@Override
	public String getCloseSendCmd() {
		return DATA_CTRL_STATE_CLOSE_0;
	}
	/**
	 * convert data(0~100) to alpha(0~255)
	 */
	public int convert2Alpha(int in) {
		float alpha = (in / 100F) * 0xFF;
		return (int) Math.min(alpha, 0xFF);
	}

	public Drawable getStateSmallIcon() {
		Drawable icon = null;
		if (isOpened()) {
			/*
			 * Note: sometimes icon can not see, not use alpha··· int epDataInt
			 * = StringUtil.toInteger(epData); icon =
			 * getDrawable(SMALL_OPEN_D).mutate();
			 * icon.setAlpha(convert2Alpha(epDataInt));
			 */
			icon = getDrawable(SMALL_OPEN_D);
		} else if (isClosed()) {
			icon = getDrawable(SMALL_CLOSE_D);
		} else {
			icon = this.getDefaultStateSmallIcon();
		}
		return icon;
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {

		String state = null;
		int color = COLOR_NORMAL_ORANGE;

		if (isClosed()) {
			state = getString(R.string.device_state_close);
			color = COLOR_NORMAL_ORANGE;
		} else {
			int epDataInt = StringUtil.toInteger(epData);
			state = epDataInt == 100 ? getString(R.string.device_state_open)
					: (epData + " %");
			color = COLOR_CONTROL_GREEN;
		}
		return SpannableUtil.makeSpannable(state, new ForegroundColorSpan(
				getColor(color)));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_light_one_two, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);

		mSeekBar = (SeekCircle) view.findViewById(R.id.dev_light_one_seek);
		mChildText = (TextView) view.findViewById(R.id.dev_child_text);
		mDevStateView0 = (TextView) view.findViewById(R.id.dev_light_one_text0);
		mDevStateView1 = (TextView) view.findViewById(R.id.dev_light_one_text1);
		mDevStateView2 = (TextView) view.findViewById(R.id.dev_light_one_text2);

	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();

		if(StringUtil.toInteger(epData).equals(100)){
			mDevStateView0.setVisibility(View.GONE);
			mDevStateView1.setVisibility(View.GONE);
			mDevStateView2.setVisibility(View.VISIBLE);
			mSeekBar.setProgress(StringUtil.toInteger(epData));	
		}else if(StringUtil.toInteger(epData).equals(0)){
			mDevStateView0.setVisibility(View.VISIBLE);
			mDevStateView1.setVisibility(View.GONE);
			mDevStateView2.setVisibility(View.GONE);
			mSeekBar.setProgress(StringUtil.toInteger(epData));
		}else{
			mDevStateView0.setVisibility(View.GONE);
			mDevStateView1.setVisibility(View.GONE);
			mDevStateView2.setVisibility(View.VISIBLE);
			mSeekBar.setProgress(StringUtil.toInteger(epData));	
//			mDevStateView1.setText(StringUtil.toInteger(epData) + "%");
		}

		mDevStateView0.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fireWulianDeviceRequestControlSelf();
				controlDevice(ep, epType, DATA_CTRL_STATE_OPEN_100);
			}
		});
		mDevStateView2.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				fireWulianDeviceRequestControlSelf();
				controlDevice(ep, epType, DATA_CTRL_STATE_CLOSE_0);
			}
		});
		mSeekBar.setOnSeekCircleChangeListener(new OnSeekCircleChangeListener() {
			
			@Override
			public void onStopTrackingTouch(SeekCircle seekCircle) {
				fireWulianDeviceRequestControlSelf();
				controlDevice(ep, epType, String.valueOf(seekCircle.getProgress()));
			}
			
			@Override
			public void onStartTrackingTouch(SeekCircle seekCircle) {
				
			}
			
			@Override
			public void onProgressChanged(SeekCircle seekCircle, int progress,
					boolean fromUser) {
				if (fromUser) {
					mDevStateView1.setVisibility(View.VISIBLE);
					mDevStateView0.setVisibility(View.GONE);
					mDevStateView2.setVisibility(View.GONE);
					mDevStateView1.setText(progress + "%");
				}
			}
		});
		String epName = getDeviceInfo().getDevEPInfo().getEpName();
		String ep = getDeviceInfo().getDevEPInfo().getEp();
		if(!StringUtil.isNullOrEmpty(epName)){
			mChildText.setText(epName);
		}else{
			if(getDeviceParent() != null){
				mChildText.setText(DeviceUtil.ep2IndexString(ep)+"."+DeviceTool.getDeviceShowName(this));
			}else{
				mChildText.setText("");
			}
		}
	}
	
	@Override
	public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater,String ep,
			String epData) {
		if(epData == null)
			epData = "";
		View view =  inflater.inflate(cc.wulian.smarthomev5.R.layout.scene_task_control_dimmer_switch, null);
		
		linkTaskControlEPData = new StringBuffer(epData);
		final TextView lightTextView = (TextView)view.findViewById(R.id.dev_state_textview_0);
		SeekBar seekBarLight = (SeekBar)view.findViewById(R.id.dev_state_seekbar_0);
		seekBarLight.setProgress(0);
		
		if(!StringUtil.isNullOrEmpty(epData) && StringUtil.toInteger(epData) >= 0){
//			String lightText = linkTaskControlEPData;
			int lightProcess = StringUtil.toInteger(linkTaskControlEPData);
			seekBarLight.setProgress(lightProcess);
			lightTextView.setText(lightProcess+"%");
		}
		
		seekBarLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				lightTextView.setText((int)seekBar.getProgress()+"%");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mSeekProgress = seekBar.getProgress();
				linkTaskControlEPData =  new StringBuffer(mSeekProgress + "");
				lightTextView.setText(mSeekProgress+"%");
			}
			
		});
		return createControlDataDialog(inflater.getContext(), view);
	}
	
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater, final AutoActionInfo autoActionInfo) {
		
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		View contentview =  inflater.inflate(cc.wulian.smarthomev5.R.layout.scene_task_control_dimmer_switch, null);
		
		String epdata = autoActionInfo.getEpData();
		final TextView lightTextView = (TextView)contentview.findViewById(R.id.dev_state_textview_0);
		SeekBar seekBarLight = (SeekBar)contentview.findViewById(R.id.dev_state_seekbar_0);
		seekBarLight.setProgress(0);
		
		if(!StringUtil.isNullOrEmpty(epdata) && StringUtil.toInteger(epdata) >= 0){
//			String lightText = linkTaskControlEPData;
			int lightProcess = StringUtil.toInteger(epdata);
			seekBarLight.setProgress(lightProcess);
			lightTextView.setText(lightProcess+"%");
		}
		
		seekBarLight.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				lightTextView.setText((int)seekBar.getProgress()+"%");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
				
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
				int mSeekProgress = seekBar.getProgress();
				autoActionInfo.setEpData(mSeekProgress + "");
				lightTextView.setText(mSeekProgress+"%");
			}
			
		});
		holder.setShowDialog(true);
		holder.setContentView(contentview);
		holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}
}
