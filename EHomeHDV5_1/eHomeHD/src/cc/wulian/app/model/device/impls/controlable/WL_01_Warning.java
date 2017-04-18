package cc.wulian.app.model.device.impls.controlable;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.utils.SpannableUtil;
import cc.wulian.ihome.wan.util.ConstUtil;

/**
 * 0:消警,1:报警,2:火警
 */
@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_WARNING }, category = Category.C_SECURITY)
public class WL_01_Warning extends ControlableDeviceImpl implements
		OnClickListener {
	private static final String DATA_STATE_0 = "0";
	private static final String DATA_STATE_1 = "16001";
	private static final String DATA_STATE_2 = "26001";

	private static final String _00 = "00";
	private static final String _01 = "01";
	private static final String _02 = "02";

	private static final String _0 = "0";
	private static final String _1 = "1";
	private static final String _2 = "2";
	
	private static final int STATE_NORMAL = 0;
	private static final int STATE_ALARM = 1;
	private static final int STATE_ALARM_FIRE = 2;

	/**
	 * 设备在线小图标
	 */
	private static final int SMALL_NORMAL_D = R.drawable.device_warning_normal;
	/**
	 * 报警图标
	 */
	private static final int SMALL_ALARM_D = R.drawable.device_warning_alarm;
	/**
	 * 火警小图标
	 */
	private static final int SMALL_ALARM_FIRE_D = R.drawable.device_warning_alarm_fire;
	/**
	 * 主显示
	 */
	private static final int BIG_NORMAL_D = R.drawable.device_securit_warning_normal;
	/**
	 * 火警显示
	 */
	private static final int BIG_ALARM_FIRE_D_1 = R.drawable.device_securit_warning_alarm_fire;

	private ProgressBar mSecurityAlarms;// 火警报警条
	private ImageView mSecurityNormal;// 显示图片

	private FrameLayout mSecuritySetUp;
	private FrameLayout mSecurityRemove;
	private FrameLayout mSecurityAlarm;
	private FrameLayout mSecurityFire;
	/**
	 * 关闭所有报警
	 */
	private ImageView mRemoveImageView;
	/**
	 * 报警
	 */
	private ImageView mAlarmImageView;
	/**
	 * 火警
	 */
	private ImageView mAalarmFireImageView;

	public WL_01_Warning(Context context, String type) {
		super(context, type);
	}

	@Override
	public String getOpenSendCmd() {
		return DATA_STATE_1;
	}

	@Override
	public String getCloseSendCmd() {
		return DATA_STATE_0;
	}

	@Override
	public boolean isOpened() {
		return isAlarming() || isFireAlarming();
	}

	public boolean isAlarming() {
		return this.deviceState() == STATE_ALARM;
	}

	public boolean isFireAlarming() {
		return this.deviceState() == STATE_ALARM_FIRE;
	}

	@Override
	public boolean isClosed() {
		return !isOpened();
	}

	public int[] getAlarmBigDrawableArray() {
		int[] arr = new int[1];
		arr[0] = BIG_NORMAL_D;
		if (isAlarming()) {
			arr[0] = BIG_NORMAL_D;
		} else if (isFireAlarming()) {
			arr[0] = BIG_ALARM_FIRE_D_1;
		}
		return arr;
	}

	/**
	 * @text 判断开关返回状态 新老版本的硬件兼容
	 * @author Sunyf
	 * @return int
	 */
	private final int deviceState() {
		return deviceState(this.epData);
	}

	private final int deviceState(String epData) {
		if (epData != null && !epData.equals("")) {
			String finalData = null;
			switch (epData.length()) {
			case 1:
				finalData = epData.substring(0, 1);
				break;
			case 2:
			case 6:
				finalData = epData.substring(0, 2);
				break;
			default:
				return -1;
			}
			if (finalData.equals(_00) || finalData.equals(_0)) {
				return 0;
			} else if (finalData.equals(_01) || finalData.equals(_1)) {
				return 1;
			} else if (finalData.equals(_02) || finalData.equals(_2)) {
				return 2;
			}
		}
		return -1;

	}

	/**
	 * 2016 3.9
	 * 
	 * @text 更新声控报警ui
	 * @author Sunyf
	 */
	private final void updateDeviceUI() {
		switch (this.deviceState()) {
		case STATE_NORMAL:
			mSecurityNormal.setImageDrawable(this.getDrawable(BIG_NORMAL_D));
			mSecurityAlarms.setVisibility(View.INVISIBLE);
			break;
		case STATE_ALARM:
			mSecurityNormal.setImageDrawable(this.getDrawable(BIG_NORMAL_D));
			mSecurityAlarms.setVisibility(View.VISIBLE);
			break;
		case STATE_ALARM_FIRE:
			mSecurityNormal.setImageDrawable(this
					.getDrawable(BIG_ALARM_FIRE_D_1));
			mSecurityAlarms.setVisibility(View.VISIBLE);
			break;
		default:
			break;
		}
	}

	public Drawable getStateSmallIcon() {
		return isClosed() ? getDrawable(SMALL_NORMAL_D)
				: isAlarming() ? getDrawable(SMALL_ALARM_D)
						: isFireAlarming() ? getDrawable(SMALL_ALARM_FIRE_D)
								: WL_01_Warning.this.getDefaultStateSmallIcon();
	}

	@Override
	public Drawable[] getStateBigPictureArray() {
		Drawable[] drawables = new Drawable[1];
		Drawable normalStateDrawable = this
				.getDrawable(getAlarmBigDrawableArray()[0]);
		drawables[0] = normalStateDrawable;
		return drawables;
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		String state = getString(R.string.device_exception);
		int color = COLOR_CONTROL_GREEN;
		switch (this.deviceState(epData)) {
		case STATE_NORMAL:
			state = getString(R.string.device_state_alarm_removed);
			color = COLOR_NORMAL_ORANGE;
			break;
		case STATE_ALARM:
			state = getString(R.string.device_state_alarm);
			color = COLOR_ALARM_RED;
			break;
		case STATE_ALARM_FIRE:
			state = getString(R.string.device_state_alarm_fire);
			color = COLOR_ALARM_RED;
			break;
		default:
			break;
		}
		return SpannableUtil.makeSpannable(state, new ForegroundColorSpan(
				getColor(color)));
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_security_alarm_common_layout,
				container, false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);

		mSecurityAlarms = (ProgressBar) view
				.findViewById(R.id.device_security_alarm);
		mSecurityNormal = (ImageView) view
				.findViewById(R.id.device_security_normal);
		mSecuritySetUp = (FrameLayout) view
				.findViewById(R.id.device_alarm_security);
		mSecuritySetUp.setVisibility(View.GONE);

		mSecurityAlarm = (FrameLayout) view
				.findViewById(R.id.device_security_alarm_layout);
		mSecurityRemove = (FrameLayout) view
				.findViewById(R.id.device_security_remove);
		mSecurityFire = (FrameLayout) view.findViewById(R.id.device_alarm_fire);
		mSecurityAlarm.setVisibility(View.VISIBLE);
		mSecurityRemove.setVisibility(View.VISIBLE);
		mSecurityFire.setVisibility(View.VISIBLE);

		mAlarmImageView = (ImageView) view
				.findViewById(R.id.device_security_alarm_image);
		mRemoveImageView = (ImageView) view
				.findViewById(R.id.device_security_remove_image);
		mAalarmFireImageView = (ImageView) view
				.findViewById(R.id.device_security_fire_image);

		mAlarmImageView.setOnClickListener(this);
		mRemoveImageView.setOnClickListener(this);
		mAalarmFireImageView.setOnClickListener(this);
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		this.updateDeviceUI();
	}

	@Override
	public void onClick(View v) {
		String sendData = null;
		if (v == mRemoveImageView) {
			sendData = getCloseSendCmd();// 关闭报警
		} else if (v == mAlarmImageView) {
			sendData = getOpenSendCmd();
			System.out.println("报警---->" + sendData);
		} else if (v == mAalarmFireImageView) {
			sendData = DATA_STATE_2;
			System.out.println("火警---->" + sendData);
		}
		// 提交数据
		this.createControlOrSetDeviceSendData(DEVICE_OPERATION_CTRL, sendData,
				true);
	}
}