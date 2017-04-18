package cc.wulian.app.model.device.impls.sensorable;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.view.AnimationUtil;

public abstract class AbstractScanAnimSensorDevice extends SensorableDeviceImpl
		implements Scanable {
	private static final int BIG_SCAN_COVER = R.drawable.device_env_detec_cover;

	protected static final float SCAN_SPEED_SLOW = 1600F;
	protected static final float SCAN_SPEED_NORMAL = 2000F;
	protected static final float SCAN_SPEED_FAST = 2500F;

	private ImageView mBottomView;
	private ImageView mCoverView;

	private TextView mTopTextView;
	private TextView mMidTextView;
	private TextView mBottomTextView;

	public AbstractScanAnimSensorDevice(Context context, String type) {
		super(context, type);
	}

	@Override
	public ScanAnimationInfo getScanAnimationInfo() {
		ScanAnimationInfo sai = new ScanAnimationInfo();

		String flag = checkDataRatioFlag();
		int res = getScanStateNormalRes();
		float speed = SCAN_SPEED_SLOW;
		if (FLAG_RATIO_NORMAL.equals(flag)) {
			res = getScanStateNormalRes();
			speed = SCAN_SPEED_SLOW;
		} else if (FLAG_RATIO_MID.equals(flag)) {
			res = getScanStateMediumRes();
			speed = SCAN_SPEED_NORMAL;
		} else if (FLAG_RATIO_ALARM.equals(flag)) {
			res = getScanStateAlarmRes();
			speed = SCAN_SPEED_FAST;
		}

		sai.sensorStateDrawable = getDrawable(res);
		sai.scanSpeed = speed;
		return sai;
	}

	// must set the normal state big res
	public abstract int getScanStateNormalRes();

	public int getScanStateMediumRes() {
		return 0;
	}

	public int getScanStateAlarmRes() {
		return 0;
	}

	public abstract void onInitViewState(TextView topView, TextView midView,
			TextView bottomView);

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		View view = super.onCreateView(inflater, container, saveState);
		view = inflater.inflate(R.layout.device_ems, container, false);
		return view;
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);

		ScanAnimationInfo sai = getScanAnimationInfo();

		mBottomView = (ImageView) view.findViewById(R.id.dev_state_imageview_0);
		mBottomView.setImageDrawable(sai.sensorStateDrawable);

		mCoverView = (ImageView) view.findViewById(R.id.dev_state_imageview_1);
		mCoverView.setImageDrawable(getDrawable(BIG_SCAN_COVER));

		mTopTextView = (TextView) view.findViewById(R.id.dev_state_textview_0);
		mMidTextView = (TextView) view.findViewById(R.id.dev_state_textview_1);
		mBottomTextView = (TextView) view
				.findViewById(R.id.dev_state_textview_2);
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();

		ScanAnimationInfo sai = getScanAnimationInfo();
		Animation animation = AnimationUtil.getRotateAnimation(sai.scanSpeed);
		mCoverView.setAnimation(animation);

		onInitViewState(mTopTextView, mMidTextView, mBottomTextView);
	}
}
