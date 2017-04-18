package cc.wulian.app.model.device.impls.controlable;

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

import java.util.ArrayList;
import java.util.Map;

import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.interfaces.DeviceShortCutSelectDataItem;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView;
import cc.wulian.app.model.device.interfaces.EditDeviceInfoView.DeviceCategoryEntity;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.app.model.device.utils.SpannableUtil;
import cc.wulian.ihome.wan.entity.AutoActionInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;

/**
 * 1:停,2:开,3:关
 */
@DeviceClassify(devTypes = { "Ar" }, category = Category.C_CONTROL)
public class WL_Ar_Shade extends ControlableDeviceImpl {
	private static final String DATA_CTRL_STATE_STOP_1 = "1";
	private static final String CMD_CTRL_STATE_STOP_1 = "1";
	private static final String DATA_CTRL_STATE_OPEN_2 = "2";
	private static final String CMD_CTRL_STATE_OPEN_2 = "2";
	private static final String DATA_CTRL_STATE_CLOSE_3 = "3";
	private static final String CMD_CTRL_STATE_CLOSE_3 = "3";
	private int SMALL_OPEN_D = R.drawable.device_shade_open;
	private int SMALL_CLOSE_D = R.drawable.device_shade_close;
	private int SMALL_STOP_D = R.drawable.device_shade_mid;

	private int BIG_OPEN_D = R.drawable.curtain_bg_open;
	private int BIG_CLOSE_D = R.drawable.curtain_bg_close;
	private int BIG_STOP_D = R.drawable.curtain_bg_half_open;

	private Map<String, Map<Integer, Integer>> categoryIcons = DeviceUtil
			.getCurtainCategoryDrawable();
	private ImageView mStateView;
	private View mOpenView;
	private View mStopView;
	private View mCloseView;
	private OnClickListener clickListener =  new OnClickListener(){

		@Override
		public void onClick(View v) {
				String sendData = null;
				if (v == mOpenView) {
					sendData = getOpenSendCmd();
				} else if (v == mStopView) {
					sendData = getStopSendCmd();
				} else if (v == mCloseView) {
					sendData = getCloseSendCmd();
				}
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, sendData,
						true);
		}
	};
	public WL_Ar_Shade(Context context, String type) {
		super(context, type);
	}

	@Override
	public String getOpenSendCmd() {
		return CMD_CTRL_STATE_OPEN_2;
	}

	@Override
	public String getCloseSendCmd() {
		return CMD_CTRL_STATE_CLOSE_3;
	}

	@Override
	public boolean isOpened() {
		return isSameAs(DATA_CTRL_STATE_OPEN_2, epData);
	}

	@Override
	public boolean isClosed() {
		return isSameAs(DATA_CTRL_STATE_CLOSE_3, epData);
	}

	@Override
	public boolean isStoped() {
		return isSameAs(DATA_CTRL_STATE_STOP_1, epData);
	}

	@Override
	public String getStopSendCmd() {
		return CMD_CTRL_STATE_STOP_1;
	}

	@Override
	public String getStopProtocol() {
		return getStopSendCmd();
	}

	@Override
	public DeviceShortCutControlItem onCreateShortCutView(DeviceShortCutControlItem item,LayoutInflater inflater){
		if (item == null) {
			item = new ControlableDeviceShortCutControlItem(inflater.getContext());
		}
		((ControlableDeviceShortCutControlItem) item).setStopVisiable(true);
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
	public Drawable getStateSmallIcon() {
		return isStoped() ? getDrawable(SMALL_STOP_D)
				: isOpened() ? getDrawable(SMALL_OPEN_D)
						: isClosed() ? getDrawable(SMALL_CLOSE_D)
								: getDrawable(SMALL_CLOSE_D);
	}

	@Override
	public Drawable[] getStateBigPictureArray() {
		Drawable[] drawables = new Drawable[1];
		drawables[0] = isStoped() ? getDrawable(BIG_STOP_D)
				: isOpened() ? getDrawable(BIG_OPEN_D)
						: isClosed() ? getDrawable(BIG_CLOSE_D)
								: getDrawable(BIG_CLOSE_D);
		return drawables;
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		String state = "";
		int color = COLOR_NORMAL_ORANGE;

		if (isStoped()) {
			state = getString(R.string.device_state_stop);
			color = COLOR_NORMAL_ORANGE;
		} else if (isOpened()) {
			state = getString(R.string.device_state_open);
			color = COLOR_CONTROL_GREEN;
		} else if (isClosed()) {
			state = getString(R.string.device_state_close);
			color = COLOR_NORMAL_ORANGE;
		}
		return SpannableUtil.makeSpannable(state, new ForegroundColorSpan(
				getResources().getColor(color)));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_shade_content,
				container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		mStateView = (ImageView) view
				.findViewById(R.id.dev_three_state_imageview_1);

		mOpenView = (ImageView)view.findViewById(R.id.curtain_open_ib);
		mStopView = (ImageView)view.findViewById(R.id.curtain_stop_ib);
		mCloseView = (ImageView)view.findViewById(R.id.curtain_close_ib);

		mOpenView.setOnClickListener(clickListener);
		mStopView.setOnClickListener(clickListener);
		mCloseView.setOnClickListener(clickListener);
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		mStateView.setImageDrawable(getStateBigPictureArray()[0]);
	}

	@Override
	public void setResourceByCategory() {
		Map<Integer, Integer> shadekMap = categoryIcons.get(getDeviceCategory());
		if(shadekMap != null && shadekMap.size() >=6){
			SMALL_OPEN_D = shadekMap.get(0);
			SMALL_CLOSE_D = shadekMap.get(1);
			SMALL_STOP_D = shadekMap.get(2);

			BIG_OPEN_D = shadekMap.get(3);
			BIG_CLOSE_D = shadekMap.get(4);
			BIG_STOP_D = shadekMap.get(5);
		}
	}

	@Override
	public EditDeviceInfoView onCreateEditDeviceInfoView(LayoutInflater inflater) {
		EditDeviceInfoView view = super.onCreateEditDeviceInfoView(inflater);
		ArrayList<DeviceCategoryEntity> entities = new ArrayList<DeviceCategoryEntity>();
		for (String key : categoryIcons.keySet()) {
			DeviceCategoryEntity entity = new DeviceCategoryEntity();
			entity.setCategory(key);
			entity.setResources(categoryIcons.get(key));
			entities.add(entity);
		}
		view.setDeviceIcons(entities);
		return view;
	}
	@Override
	public Dialog onCreateChooseContolEpDataView(LayoutInflater inflater,
			String ep, String epData) {
		if(epData == null){
			epData = "";
		}
		View view = inflater.inflate(R.layout.device_curtain_adjust_control, null);
		linkTaskControlEPData = new StringBuffer(epData);
		LinearLayout mSeekBarLayout = (LinearLayout) view.findViewById(R.id.device_curtain_Layout);
		mSeekBarLayout.setVisibility(View.GONE);
		final Button mButtonOn = (Button) view.findViewById(R.id.device_curtain_adjust_open);
		final Button mButtonStop = (Button) view.findViewById(R.id.device_curtain_adjust_stop);
		final Button mButtonOff = (Button) view.findViewById(R.id.device_curtain_adjust_close);
		
		
		if(StringUtil.equals(linkTaskControlEPData, DATA_CTRL_STATE_CLOSE_3)){
			mButtonOn.setSelected(false);
			mButtonStop.setSelected(false);
			mButtonOff.setSelected(true);
		}else if(StringUtil.equals(linkTaskControlEPData, DATA_CTRL_STATE_OPEN_2)){
			mButtonOn.setSelected(true);
			mButtonStop.setSelected(false);
			mButtonOff.setSelected(false);
		}else if(StringUtil.equals(linkTaskControlEPData, DATA_CTRL_STATE_STOP_1)){
			mButtonOn.setSelected(false);
			mButtonStop.setSelected(true);
			mButtonOff.setSelected(false);
		}
			
		
		mButtonOn.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mButtonOn.setSelected(true);
				mButtonStop.setSelected(false);
				mButtonOff.setSelected(false);
				linkTaskControlEPData = new StringBuffer(DATA_CTRL_STATE_OPEN_2);
			}
		});
		mButtonStop.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mButtonOn.setSelected(false);
				mButtonStop.setSelected(true);
				mButtonOff.setSelected(false);
				linkTaskControlEPData = new StringBuffer(DATA_CTRL_STATE_STOP_1);
			}
		});
		mButtonOff.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				mButtonOn.setSelected(false);
				mButtonStop.setSelected(false);
				mButtonOff.setSelected(true);
				linkTaskControlEPData = new StringBuffer(DATA_CTRL_STATE_CLOSE_3);
			}
		});
		
		return createControlDataDialog(inflater.getContext(), view);
	}
}
