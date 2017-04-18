package cc.wulian.app.model.device.impls.controlable;

import java.util.Map;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.DeviceShortCutControlItem;
import cc.wulian.app.model.device.utils.DeviceUtil;
import cc.wulian.app.model.device.utils.SpannableUtil;
import cc.wulian.ihome.wan.util.ConstUtil;

/**
 * 1:停,2:开,3:关
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_ZHIHUANG_MOTOR }, category = Category.C_CONTROL)
public class WL_OK_ZHIHUANG_Motor extends ControlableDeviceImpl {
	
	private static final String DATA_CTRL_STATE_CLOSE_1 = "11#";
	private static final String DATA_CTRL_STATE_OPEN_2 = "12#";
	private static final String DATA_CTRL_STATE_STOP_3 = "13#";
	
	private static final String DATA_CTRL_STATE_QUERY = "1A#";
	
	private static final String DATA_CTRL_STATE_NORMAL = "21#";
	private static final String DATA_CTRL_STATE_ANORMAL= "22#";
	
	private static final String DATA_STATE_NORMAL_CLOSE_1 = "030101";
	private static final String DATA_STATE_NORMAL_OPEN_2 = "030102";
	private static final String DATA_STATE_NORMAL_STOP_3 = "030103";
	
	private static final String DATA_STATE_ABNORMAL_CLOSE_1 = "030202";
	private static final String DATA_STATE_ABNORMAL_OPEN_2 = "030201";
	private static final String DATA_STATE_ABNORMAL_STOP_3 = "030203";
	
	private static final String DATA_QUERY_STATE_NORMAL_CLOSE_1 = "AA0101";
	private static final String DATA_QUERY_STATE_NORMAL_OPEN_2 = "AA0102";
	private static final String DATA_QUERY_STATE_NORMAL_STOP_3 = "AA0103";
	
	private static final String DATA_QUERY_STATE_ABNORMAL_CLOSE_1 = "AA0202";
	private static final String DATA_QUERY_STATE_ABNORMAL_OPEN_2 = "AA0201";
	private static final String DATA_QUERY_STATE_ABNORMAL_STOP_3 = "AA0203";
	
	private static final String DATA_STATE_NORMAL = "01";
	private static final String DATA_STATE_ABNORMAL= "02";
	
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
	
	private FrameLayout normalLayout;
	private FrameLayout abnormalLayout;
	
	private ImageView normalImageView;
	private ImageView abnormalImageView;
	
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
				}else if (v == normalLayout) {
					sendData = getNormalCmd();
				}else if (v == abnormalLayout) {
					sendData = getAbnormalCmd();
				}
				createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, sendData,true);
		}
	};
	public WL_OK_ZHIHUANG_Motor(Context context, String type) {
		super(context, type);
	}

	@Override
	public String getOpenSendCmd() {
		return DATA_CTRL_STATE_OPEN_2;
	}

	@Override
	public String getCloseSendCmd() {
		return DATA_CTRL_STATE_CLOSE_1;
	}

	@Override
	public String getStopSendCmd() {
		return DATA_CTRL_STATE_STOP_3;
	}
	
	public String getNormalCmd() {
		return DATA_CTRL_STATE_NORMAL;
	}
	
	public String getAbnormalCmd() {
		return DATA_CTRL_STATE_ANORMAL;
	}
	
	public String getQueryCmd() {
		return DATA_CTRL_STATE_QUERY;
	}
	
	@Override
	public boolean isOpened() {
		return isSameAs(DATA_STATE_NORMAL_OPEN_2, epData)||isSameAs(DATA_STATE_ABNORMAL_CLOSE_1, epData)||isSameAs(DATA_QUERY_STATE_NORMAL_CLOSE_1, epData)||isSameAs(DATA_QUERY_STATE_ABNORMAL_CLOSE_1, epData);
	}

	@Override
	public boolean isClosed() {
		return isSameAs(DATA_STATE_NORMAL_CLOSE_1, epData)||isSameAs(DATA_STATE_ABNORMAL_OPEN_2, epData)||isSameAs(DATA_QUERY_STATE_NORMAL_OPEN_2, epData)||isSameAs(DATA_QUERY_STATE_ABNORMAL_OPEN_2, epData);
	}

	@Override
	public boolean isStoped() {
		return isSameAs(DATA_STATE_NORMAL_STOP_3, epData)||isSameAs(DATA_STATE_ABNORMAL_STOP_3, epData)||isSameAs(DATA_QUERY_STATE_NORMAL_STOP_3, epData)||isSameAs(DATA_QUERY_STATE_ABNORMAL_STOP_3, epData);
	}
	
	public boolean isNormal() {
		return isSameAs(DATA_STATE_NORMAL, epData.substring(2, 4));
	}
	
	public boolean isAbnormal() {
		return isSameAs(DATA_STATE_ABNORMAL, epData.substring(2, 4));
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
		createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, DATA_CTRL_STATE_QUERY,true);
		return inflater.inflate(R.layout.device_motor_content,
				container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		mStateView = (ImageView) view
				.findViewById(R.id.dev_three_state_imageview_1);

		mOpenView = (ImageView)view.findViewById(R.id.motor_open_ib);
		mStopView = (ImageView)view.findViewById(R.id.motor_stop_ib);
		mCloseView = (ImageView)view.findViewById(R.id.motor_close_ib);
		
		normalLayout = (FrameLayout)view.findViewById(R.id.device_motor_direct_normal_layout);
		abnormalLayout = (FrameLayout)view.findViewById(R.id.device_motor_direct_abnormal_layout);
		
		normalImageView= (ImageView)view.findViewById(R.id.device_motor_direct_normal_imageView);
		abnormalImageView= (ImageView)view.findViewById(R.id.device_motor_direct_abnormal_imageView);
		showDeviceMotionDirection();
		mOpenView.setOnClickListener(clickListener);
		mStopView.setOnClickListener(clickListener);
		mCloseView.setOnClickListener(clickListener);
		normalLayout.setOnClickListener(clickListener);
		abnormalLayout.setOnClickListener(clickListener);
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		mStateView.setImageDrawable(getStateBigPictureArray()[0]);
		showDeviceMotionDirection();
	}

	private void showDeviceMotionDirection() {
		if(isAbnormal()){
			normalImageView.setImageResource(R.drawable.device_motor_direct_imageview_background_gray);
			abnormalImageView.setImageResource(R.drawable.device_motor_direct_imageview_background);
			
		}else{
			normalImageView.setImageResource(R.drawable.device_motor_direct_imageview_background);
			abnormalImageView.setImageResource(R.drawable.device_motor_direct_imageview_background_gray);
		}
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
}
