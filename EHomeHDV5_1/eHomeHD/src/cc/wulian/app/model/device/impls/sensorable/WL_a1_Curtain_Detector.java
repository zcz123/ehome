package cc.wulian.app.model.device.impls.sensorable;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import cc.wulian.app.model.device.R;
import cc.wulian.app.model.device.category.Category;
import cc.wulian.app.model.device.category.DeviceClassify;
import cc.wulian.app.model.device.interfaces.AbstractLinkTaskView;
import cc.wulian.app.model.device.interfaces.CurtainDetectorChooseView;
import cc.wulian.app.model.device.interfaces.DialogOrActivityHolder;
import cc.wulian.app.model.device.interfaces.LinkTaskCurtainView;
import cc.wulian.ihome.wan.entity.AutoConditionInfo;
import cc.wulian.ihome.wan.entity.TaskInfo;
import cc.wulian.ihome.wan.util.ConstUtil;
import cc.wulian.ihome.wan.util.StringUtil;
import cc.wulian.smarthomev5.activity.BaseActivity;
import cc.wulian.smarthomev5.tools.DeviceTool;

/**
 * 00:正常 02:左区域有人 03:右区域有人
 */

@DeviceClassify(devTypes = { ConstUtil.DEV_TYPE_FROM_GW_CURTAIN_DETECTOR }, category = Category.C_OTHER)
public class WL_a1_Curtain_Detector extends SensorableDeviceImpl {

	public static final String DATA_CTRL_STATE_NORMAL = "00";
	public static final String DATA_CTRL_STATE_BEFORE_ENTER = "02";
	public static final String DATA_CTRL_STATE_BEHIND_ENTER = "03";

	private Handler mHandler;

	private ImageView mStateView;
	private ImageView mLeftView;
	private ImageView mRightView;
	private ProgressBar mCurtainScan;
	private TextView mTextView;

	public WL_a1_Curtain_Detector(Context context, String type) {
		super(context, type);
	}

	@Override
	public CharSequence parseDataWithProtocol(String epData) {
		return disassembleCompoundCmd(epData);
	}

	@Override
	public boolean isLinkControl() {
		return true;
	}

	@Override
	public AbstractLinkTaskView onCreateLinkTaskView(BaseActivity context,
			TaskInfo taskInfo) {
		AbstractLinkTaskView taskView = new LinkTaskCurtainView(context,
				taskInfo);
		taskView.onCreateView();
		return taskView;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle saveState) {
		return inflater.inflate(R.layout.device_curtain_detector, container,
				false);
	}

	@Override
	public void onViewCreated(View view, Bundle saveState) {
		super.onViewCreated(view, saveState);
		mStateView = (ImageView) view.findViewById(R.id.curtain_detector_image);
		mCurtainScan = (ProgressBar) view
				.findViewById(R.id.device_curtain_scan);
		mCurtainScan.setVisibility(View.INVISIBLE);
		mLeftView = (ImageView) view
				.findViewById(R.id.device_curtain_default_left);
		mRightView = (ImageView) view
				.findViewById(R.id.device_curtain_default_right);
		mTextView = (TextView) view.
				findViewById(R.id.device_curtain_detector_text);
	}

	@Override
	public void initViewStatus() {
		super.initViewStatus();
		if (isSameAs(DATA_CTRL_STATE_BEFORE_ENTER, epData)) {
			disassembleCompoundCmd(epData);
			mCurtainScan.setVisibility(View.VISIBLE);
			mLeftView.setVisibility(View.VISIBLE);
			mTextView.setText(getString(R.string.device_before_state));
			hideLeft();
		} else if (isSameAs(DATA_CTRL_STATE_BEHIND_ENTER, epData)) {
			disassembleCompoundCmd(epData);
			mCurtainScan.setVisibility(View.VISIBLE);
			mRightView.setVisibility(View.VISIBLE);
			mTextView.setText(getString(R.string.device_behind_satate));
			hideRight();
		} else if (isSameAs(DATA_CTRL_STATE_NORMAL, epData)) {
			HideAll();
			mTextView.setText(getString(R.string.device_both_normal));
		}
	}

	private void hideLeft() {
		mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mLeftView.setVisibility(View.INVISIBLE);
			}
		}, 3000);
	}

	private void hideRight() {
		mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mRightView.setVisibility(View.INVISIBLE);
			}
		}, 3000);
	}

	private void HideAll() {
		mHandler = new Handler();
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				mLeftView.setVisibility(View.INVISIBLE);
				mRightView.setVisibility(View.INVISIBLE);
				mCurtainScan.setVisibility(View.INVISIBLE);
			}
		}, 2000);
	}

	private String disassembleCompoundCmd(String ensureEpData) {
		String result = "";
		if (StringUtil.isNullOrEmpty(ensureEpData) || ensureEpData.length() > 2) {
			return "";
		}
		int state = StringUtil.toInteger(ensureEpData, 16);
		if (state == 2) {
			result = getString(R.string.device_before_state);
		} else if (state == 3) {
			result = getString(R.string.device_behind_satate);
		}else{
			result = getString(R.string.device_both_normal);
		}
		return result;
	}
	
	@Override
	public DialogOrActivityHolder onCreateHouseKeeperSelectSensorDeviceDataView(
			LayoutInflater inflater, AutoConditionInfo autoConditionInfo,boolean isTriggerCondition) {
		DialogOrActivityHolder holder = new DialogOrActivityHolder();
		CurtainDetectorChooseView curtainDetectorChooseView = new CurtainDetectorChooseView(inflater.getContext());
		curtainDetectorChooseView.setmSensorDeviceValues(autoConditionInfo.getExp(),autoConditionInfo.getDes());
		holder.setShowDialog(false);
		holder.setContentView(curtainDetectorChooseView.getView());
		holder.setFragementTitle(DeviceTool.getDeviceShowName(this));
		return holder;
	}
}
