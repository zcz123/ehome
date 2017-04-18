package cc.wulian.app.model.device.impls.controlable.curtain;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl;
import cc.wulian.app.model.device.impls.controlable.ControlableDeviceImpl.ShortCutControlableDeviceSelectDataItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.utils.SpannableUtil;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.R;
import cc.wulian.smarthomev5.tools.DeviceTool;

/**
 * 发送: <br/>
 * 位1:是否可调(0:无效,1:有效) 位2～4:打开幅度(000～100) 000表示关闭,100表示打开,255表示停止
 * 
 * <br/>
 * 
 * 返回: <br/>
 * 位1～2:是否可调(00:无效,01:有效) 位3～4:打开幅度(00～0x64表示000～100) 00表示关闭,0x64表示打开,0xFF表示停止 注:0x32表示打开幅度为50%
 * 
 * <br/>
 */
@DeviceClassify(devTypes = {ConstUtil.DEV_TYPE_FROM_GW_CURTAIN_1}, category = Category.C_CONTROL)
public class WL_80_Curtain_1 extends ControlableDeviceImpl
{
	private int SMALL_OPEN_D = R.drawable.device_shade_open;
	private int SMALL_CLOSE_D = R.drawable.device_shade_close;
	private int SMALL_STOP_D = R.drawable.device_shade_mid;
	private static final String DATA_CTRL_STATE_PREFIX_0 = "0";
	private static final String DATA_CTRL_STATE_PREFIX_1 = "1";
	private static final String DATA_CTRL_STATE_STOP_255 = "255";
	private static final String DATA_CTRL_STATE_OPEN_100 = "100";
	private static final String DATA_CTRL_STATE_CLOSE_000 = "000";

	private static final int DEVICE_STATE_STOP_FF = 0xFF;
	private static final int DEVICE_STATE_OPEN_64 = 0x64;
	private static final int DEVICE_STATE_CLOSE_00 = 0x00;

	private View mOpenView;
	private View mStopView;
	private View mCloseView;
	private ImageView curtainIamgeView;
	private View mControlLayout;
	private SeekBar mSeekBar;
	private OnClickListener  clickListener = new OnClickListener(){
		@Override
		public void onClick( View v ) {
			String sendData = null;
			if (v == mOpenView) {
				sendData = getOpenSendCmd();
			}
			else if (v == mStopView) {
				sendData = getStopSendCmd();
			}
			else if (v == mCloseView) {
				sendData = getCloseSendCmd();
			}
			createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, sendData, true);
		}
	};
	public WL_80_Curtain_1( Context context, String type )
	{
		super(context, type);
	}

	@Override
	public String getOpenSendCmd() {
		if (isNull(epData) || epData.length() <= 1) return DATA_CTRL_STATE_PREFIX_0 + DATA_CTRL_STATE_OPEN_100;
		String interData = judgeEpdata(epData.substring(1, 2));
		return interData + DATA_CTRL_STATE_OPEN_100;
	}

	@Override
	public String getCloseSendCmd() {
		if (isNull(epData) || epData.length() <= 1) return DATA_CTRL_STATE_PREFIX_0 + DATA_CTRL_STATE_CLOSE_000;
		String interData = judgeEpdata(epData.substring(1, 2));
		return  interData + DATA_CTRL_STATE_CLOSE_000;
	}
	//解决epdata异常，有时候会回4
	public String judgeEpdata(String str){
		if(!"0".equals(str)){
			str = "1";
		}
		return str;
	}
	@Override
	public boolean isOpened() {
		return !isClosed() & !isStoped();
	}

	@Override
	public boolean isClosed() {
		if (isNull(epData)) return false;
		int ctrlState = -1;
		if (epData.length() >= 2) {
			ctrlState = StringUtil.toInteger(epData.substring(2), 16);
		}
		return DEVICE_STATE_CLOSE_00 == ctrlState;
	}

	@Override
	public boolean isStoped() {
		if (isNull(epData)) return true;
		int ctrlState = -1;
		if (epData.length() >= 2) {
			ctrlState = StringUtil.toInteger(epData.substring(2), 16);
		}
		return DEVICE_STATE_STOP_FF == ctrlState;
	}

	@Override
	public String getStopSendCmd() {
		if (isNull(epData) || epData.length() <= 1) return DATA_CTRL_STATE_PREFIX_0 + DATA_CTRL_STATE_STOP_255;

		return epData.substring(1, 2) + DATA_CTRL_STATE_STOP_255;
	}

	@Override
	public String getStopProtocol() {
		return getStopSendCmd();
	}
	@Override
	public Drawable getStateSmallIcon() {
		return isStoped() ? getDrawable(SMALL_STOP_D)
				: isOpened() ? getDrawable(SMALL_OPEN_D)
						: isClosed() ? getDrawable(SMALL_CLOSE_D)
								: getDrawable(SMALL_CLOSE_D);
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
			ShortCutControlableDeviceSelectDataItem shortCutItem = new WL80ShortCutControlableDeviceSelectDataItem(inflater.getContext());
			shortCutItem.setStopVisiable(true);
			item = shortCutItem;
		}
		item.setWulianDeviceAndSelectData(this, autoActionInfo);
		return item;
	}


	public static class WL80ShortCutControlableDeviceSelectDataItem extends ControlableDeviceImpl.ShortCutControlableDeviceSelectDataItem {
		public WL80ShortCutControlableDeviceSelectDataItem(Context context) {
			super(context);
		}

		@Override
		protected boolean isOpened() {
			return this.autoActionInfo.getEpData().endsWith(DATA_CTRL_STATE_OPEN_100);
		}

		@Override
		protected boolean isClosed() {
			return this.autoActionInfo.getEpData().endsWith(DATA_CTRL_STATE_CLOSE_000);
		}
	}

	public int getCurrentPercent( String specialData ) {
		boolean nullData = isNull(specialData);
		String data = nullData ? epData : specialData;
		int percent = 0;

		if (isNull(data)) percent = 0;

		if (!nullData && isStoped()) percent = 50;

		if (data.length() <= 1) {
			percent = 0;
		}
		else {
			percent = StringUtil.toInteger(data.substring(nullData ? 2 : 1), nullData ? 16 : 10);
		}
		return percent;
	}

	public int getMaxLimit() {
		return StringUtil.toInteger(DATA_CTRL_STATE_OPEN_100);
	}

	public int getMinLimit() {
		return StringUtil.toInteger(DATA_CTRL_STATE_CLOSE_000);
	}

	public boolean canAdjustPercent() {
		return !isNull(epData) && epData.startsWith(DATA_CTRL_STATE_PREFIX_0 + DATA_CTRL_STATE_PREFIX_1);
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		String state = "";
		int color = COLOR_NORMAL_ORANGE;

		if (isStoped()) {
			state = getString(R.string.device_state_stop);
			color = COLOR_NORMAL_ORANGE;
		}
		else if (isOpened()) {
			int epDataInt = -1;
			if (epData.length() >= 2) {
				epDataInt = StringUtil.toInteger(epData.substring(2), 16);
			}
			if (100 == epDataInt) {
				state = epDataInt + " %";
			}
			else {
				state = getString(R.string.device_state_open);
			}
			color = COLOR_CONTROL_GREEN;
		}
		else if (isClosed()) {
			state = getString(R.string.device_state_close);
			color = COLOR_NORMAL_ORANGE;
		}
		else {
		}
		return SpannableUtil.makeSpannable(state, new ForegroundColorSpan(getColor(color)));
	}

	@Override
	public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle saveState ) {
		return inflater.inflate(R.layout.device_curtain_content, container, false);
	}
	//从此处开始更改！！
	@Override
	public void onViewCreated( View view, Bundle saveState ) {
		mControlLayout = view.findViewById(R.id.control_linearLayout);
		mSeekBar = (SeekBar) view.findViewById(R.id.curtain_adjust_sb);
		curtainIamgeView = (ImageView)view.findViewById(R.id.curtain_bg_iv);
		mOpenView = view.findViewById(R.id.curtain_open_ib);
		mStopView = view.findViewById(R.id.curtain_stop_ib);
		mCloseView = view.findViewById(R.id.curtain_close_ib);

		mOpenView.setOnClickListener(clickListener);
		mStopView.setOnClickListener(clickListener);
		mCloseView.setOnClickListener(clickListener);
		mSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch( SeekBar seekBar ) {
				int percent = seekBar.getProgress();
				fireWulianDeviceRequestControlSelf();
				controlDevice(ep,type , DATA_CTRL_STATE_PREFIX_1 + StringUtil.appendLeft(percent+"", 3, '0'));
			}

			@Override
			public void onStartTrackingTouch( SeekBar seekBar ) {

			}

			@Override
			public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
			}
		});
		mViewCreated = true;
	}

	@Override
	public void initViewStatus() {
		if(StringUtil.isNullOrEmpty(epData) || epData.length() <4)
			return ;
		int canCtrl = StringUtil.toInteger(epData.substring(0,2),16);
		if(canCtrl == 0){
			mControlLayout.setVisibility(View.INVISIBLE);
		}else{
			mControlLayout.setVisibility(View.VISIBLE);
		}
		int stateData = StringUtil.toInteger(epData.substring(2,4),16);
		if(stateData !=DEVICE_STATE_STOP_FF )
			mSeekBar.setProgress(stateData);
		if(DEVICE_STATE_OPEN_64 == stateData){
			curtainIamgeView.setImageResource(R.drawable.curtain_bg_open);
		}else if(DEVICE_STATE_CLOSE_00 == stateData){
			curtainIamgeView.setImageResource(R.drawable.curtain_bg_close);
		}else{
			curtainIamgeView.setImageResource(R.drawable.curtain_bg_half_open);
		}
	}

	@Override
	public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater,
			String ep, String epData) {
		if(epData == null){
			epData = "";
		}
		View view = inflater.inflate(R.layout.device_curtain_adjust_control, null);
		linkTaskControlEPData = new StringBuffer(epData);
		SeekBar seekBarAdjust = (SeekBar) view.findViewById(R.id.device_curtain_adjust_seekbar);
		seekBarAdjust.setProgress(0);
		final Button mButtonOn = (Button) view.findViewById(R.id.device_curtain_adjust_open);
		final Button mButtonStop = (Button) view.findViewById(R.id.device_curtain_adjust_stop);
		final Button mButtonOff = (Button) view.findViewById(R.id.device_curtain_adjust_close);
		
		if(epData.startsWith(DATA_CTRL_STATE_PREFIX_1)){
			String lightText = linkTaskControlEPData.substring(1);
			int lightProcess = StringUtil.toInteger(lightText);
			if(StringUtil.equals(lightText, DATA_CTRL_STATE_CLOSE_000)){
				seekBarAdjust.setProgress(lightProcess);
				mButtonOn.setSelected(false);
				mButtonStop.setSelected(false);
				mButtonOff.setSelected(true);
			}else if(StringUtil.equals(lightText, DATA_CTRL_STATE_OPEN_100)){
				mButtonOn.setSelected(true);
				mButtonStop.setSelected(false);
				mButtonOff.setSelected(false);
				seekBarAdjust.setProgress(lightProcess);
			}else if(StringUtil.equals(lightText, DATA_CTRL_STATE_STOP_255)){
				mButtonOn.setSelected(false);
				mButtonStop.setSelected(true);
				mButtonOff.setSelected(false);
			}else{
				seekBarAdjust.setProgress(lightProcess);
			}
			
		}
		
		mButtonOn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mButtonOn.setSelected(true);
				mButtonStop.setSelected(false);
				mButtonOff.setSelected(false);
				linkTaskControlEPData = new StringBuffer(DATA_CTRL_STATE_PREFIX_1 + DATA_CTRL_STATE_OPEN_100);
			}
		});
		mButtonStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mButtonOn.setSelected(false);
				mButtonStop.setSelected(true);
				mButtonOff.setSelected(false);
				linkTaskControlEPData = new StringBuffer(DATA_CTRL_STATE_PREFIX_1 + DATA_CTRL_STATE_STOP_255);
			}
		});
		mButtonOff.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mButtonOn.setSelected(false);
				mButtonStop.setSelected(false);
				mButtonOff.setSelected(true);
				linkTaskControlEPData = new StringBuffer(DATA_CTRL_STATE_PREFIX_1 + DATA_CTRL_STATE_CLOSE_000);
			}
		});
		seekBarAdjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch( SeekBar seekBar ) {
				int mSeekProgress = seekBar.getProgress();
				linkTaskControlEPData =  new StringBuffer(DATA_CTRL_STATE_PREFIX_1 + StringUtil.appendLeft(mSeekProgress+"", 3, '0'));
			}

			@Override
			public void onStartTrackingTouch( SeekBar seekBar ) {

			}

			@Override
			public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
			}
		});
		
		return createControlDataDialog(inflater.getContext(), view);
	}
	
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectControlDeviceDataView(
			LayoutInflater inflater, final AutoActionInfo autoActionInfo) {
		
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		View contentview =  inflater.inflate(cc.wulian.smarthomev5.R.layout.device_curtain_adjust_control, null);		
		String epdata = autoActionInfo.getEpData();
		final StringBuffer buffer = new StringBuffer(epdata);
		SeekBar seekBarAdjust = (SeekBar) contentview.findViewById(R.id.device_curtain_adjust_seekbar);
		seekBarAdjust.setProgress(0);
		final Button mButtonOn = (Button) contentview.findViewById(R.id.device_curtain_adjust_open);
		final Button mButtonStop = (Button) contentview.findViewById(R.id.device_curtain_adjust_stop);
		final Button mButtonOff = (Button) contentview.findViewById(R.id.device_curtain_adjust_close);
		
		if(epdata.startsWith(DATA_CTRL_STATE_PREFIX_1)){
			String lightText =buffer.substring(1);
			int lightProcess =StringUtil.toInteger(lightText);
			if(StringUtil.equals(lightText, DATA_CTRL_STATE_CLOSE_000)){
				seekBarAdjust.setProgress(lightProcess);
				mButtonOn.setSelected(false);
				mButtonStop.setSelected(false);
				mButtonOff.setSelected(true);
			}else if(StringUtil.equals(lightText, DATA_CTRL_STATE_OPEN_100)){
				mButtonOn.setSelected(true);
				mButtonStop.setSelected(false);
				mButtonOff.setSelected(false);
				seekBarAdjust.setProgress(lightProcess);
			}else if(StringUtil.equals(lightText, DATA_CTRL_STATE_STOP_255)){
				mButtonOn.setSelected(false);
				mButtonStop.setSelected(true);
				mButtonOff.setSelected(false);
			}else{
				seekBarAdjust.setProgress(lightProcess);
			}
			
		}
		
		mButtonOn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mButtonOn.setSelected(true);
				mButtonStop.setSelected(false);
				mButtonOff.setSelected(false);
				autoActionInfo.setEpData(DATA_CTRL_STATE_PREFIX_1 + DATA_CTRL_STATE_OPEN_100);
			}
		});
		mButtonStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mButtonOn.setSelected(false);
				mButtonStop.setSelected(true);
				mButtonOff.setSelected(false);
				autoActionInfo.setEpData(DATA_CTRL_STATE_PREFIX_1 + DATA_CTRL_STATE_STOP_255);
			}
		});
		mButtonOff.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mButtonOn.setSelected(false);
				mButtonStop.setSelected(false);
				mButtonOff.setSelected(true);
				autoActionInfo.setEpData(DATA_CTRL_STATE_PREFIX_1 + DATA_CTRL_STATE_CLOSE_000);
			}
		});
		seekBarAdjust.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
		{

			@Override
			public void onStopTrackingTouch( SeekBar seekBar ) {
				int mSeekProgress = seekBar.getProgress();
				autoActionInfo.setEpData(DATA_CTRL_STATE_PREFIX_1 + StringUtil.appendLeft(mSeekProgress+"", 3, '0'));
			}

			@Override
			public void onStartTrackingTouch( SeekBar seekBar ) {
				
			}

			@Override
			public void onProgressChanged( SeekBar seekBar, int progress, boolean fromUser ) {
			}
		});
		holder.setShowDialog(true);
		holder.setContentView(contentview);
		holder.setDialogTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}
	
}